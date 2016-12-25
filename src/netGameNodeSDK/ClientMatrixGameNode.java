package netGameNodeSDK;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.protobuf.InvalidProtocolBufferException;

import gameEngine.AnimationPlayer;
import gameEngine.FunctionTriggerAnimation;
import gameEngine.Game;
import gameEngine.GameNode;
import gameEngine.LayerGameNode;
import gameEngine.SimpleGameSceneCamera;
import gameEngine.TextGameNode;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;

import mirrorWar.charger.Charger.ChargerState;
import mirrorWar.handshake.Handshake.ClientHandshake;
import mirrorWar.handshake.Handshake.ServerHandshake;
import mirrorWar.input.InputOuterClass.Input;
import mirrorWar.input.InputOuterClass.Inputs;
import mirrorWar.laser.Laser.LaserState;
import mirrorWar.mirror.Mirror.MirrorState;
import mirrorWar.player.Player.PlayerState;
import mirrorWar.update.UpdateOuterClass.Update;
import mirrorWar.update.UpdateOuterClass.Updates;
import sun.net.www.http.Hurryable;

public class ClientMatrixGameNode extends GameNode {
	private DatagramSocket commandOutputSocket;
	private DatagramPacket commandPacket;

	private DatagramSocket updateInputSocket;
	private DatagramPacket updatePacket;

	private List<Update> updateQueue = Collections.synchronizedList(new ArrayList<>());

	private Map<Integer, PlayerNetGameNode> players = new HashMap<>();
	private Map<Integer, MirrorNetGameNode> mirrors = new HashMap<>();
	private Map<Integer, ChargerNetGameNode> chargers = new HashMap<>();
	private GameReportNetGameNode gameReport;

	private Map<Integer, LaserEmitterNetGameNode> lasers= new HashMap<>();

	private int controllingPlayerId;

	private LayerGameNode rootLayer;

	public ClientMatrixGameNode(Socket serverSocket) {
		rootLayer = new LayerGameNode();
		addChild(rootLayer);
		
		LayerGameNode hudLayer = new LayerGameNode();
		addChild(hudLayer);
		
		TextGameNode player0Info = new TextGameNode("") {
			@Override
			public void update(long elapse) {
				if (gameReport != null) {
					int life = gameReport.getPlayerLife(0);
					double energy = gameReport.getChargerEnergy(0);
					
					text = String.format("Player1 Life: %d\nPlayer Energy: %2f", life, energy);
				}
			}
		};
		player0Info.geometry.y = 10;
		hudLayer.addChild(player0Info);
		
		TextGameNode player1Info = new TextGameNode("") {
			@Override
			public void update(long elapse) {
				if (gameReport != null) {
					int life = gameReport.getPlayerLife(1);
					double energy = gameReport.getChargerEnergy(1);
					
					text = String.format("Player2 Life: %d\nPlayer Energy: %2f", life, energy);
				}
			}
		};
		player1Info.geometry.y = 10;
		player1Info.geometry.x = 550;
		hudLayer.addChild(player1Info);
		

		try {
			setupUDPSockets(serverSocket);
			ServerHandshake serverHandshake = handshakeWithServer(serverSocket);
			System.out.println("finish handshaking!!");

			controllingPlayerId = serverHandshake.getClientId();

		} catch (IOException e) {

			Platform.exit();
			return;
		}

		gameReport = new GameReportNetGameNode(controllingPlayerId) {
			@Override
			protected void beAttacked() {
				// TODO add actual implementation
				System.out.println("play attack animation");
			}
		};
		addChild(gameReport);

		setupSendingInputsService();
		setupReceivingUpdateService();
	}

	@Override
	public void update(long elapse) {
		synchronized (updateQueue) {
			updateQueue.forEach(update -> {
				switch (update.getUpdateCase()) {
				case PLAYER_STATE:
					addOrUpdatePlayer(update.getPlayerState());
					break;

				case MIRROR_STATE:
					addOrUpdateMirror(update.getMirrorState());
					break;

				case CHARGER_STATE:
					addOrUpdateCharger(update.getChargerState());
					break;

				case GAME_REPORT_STATE:
					gameReport.clientHandleServerUpdate(update.getGameReportState());
					break;

				case LASER_STATE:
					addOrUpdateLaser(update.getLaserState());
					break;

				case UPDATE_NOT_SET:
					break;
				}

			});

			updateQueue.clear();
		}
	}

	private void addOrUpdateCharger(ChargerState chargerState) {
		int chargerId = chargerState.getId();

		ChargerNetGameNode charger = chargers.get(chargerId);
		if (charger == null) {
			charger = new ChargerNetGameNode(chargerId);
			charger.clientInitialize(Game.currentScene());

			chargers.put(chargerId, charger);

			rootLayer.addChild(charger);
		}

		charger.clientHandleServerUpdate(chargerState);
	}

	private void addOrUpdateMirror(MirrorState mirrorState) {
		int mirrorId = mirrorState.getId();

		MirrorNetGameNode mirror = mirrors.get(mirrorId);
		if (mirror == null) {
			mirror = new MirrorNetGameNode(mirrorId);
			mirror.clientInitialize(Game.currentScene());

			mirrors.put(mirrorId, mirror);

			rootLayer.addChild(mirror);
		}

		mirror.clientHandleServerUpdate(mirrorState);
	}

	private void addOrUpdatePlayer(PlayerState playerState) {
		int playerId = playerState.getId();

		PlayerNetGameNode player = players.get(playerId);
		if (player == null) {
			player = new PlayerNetGameNode(playerId);
			player.clientInitialize(Game.currentScene());

			if (playerId == controllingPlayerId) {
				player.isControlling = true;

				SimpleGameSceneCamera camera = new SimpleGameSceneCamera(0, 0, Game.canvasWidth(), Game.canvasHeight());
				camera.cameraTarget = Optional.of(player);
				rootLayer.camera = camera;
			}

			players.put(playerId, player);

			rootLayer.addChild(player);
		}

		player.clientHandleServerUpdate(playerState);
	}

	private void addOrUpdateLaser(LaserState laserState) {
		int laserId = laserState.getId(), ownerId = laserState.getOwnerId();

		LaserEmitterNetGameNode laser = lasers.get(laserId);
		if (laser == null) {
			laser = new LaserEmitterNetGameNode(laserId, ownerId);
			laser.clientInitialize(Game.currentScene());

			lasers.put(laserId, laser);

			rootLayer.addChild(laser);
		}

		laser.clientHandleServerUpdate(laserState);
	}

	@Override
	public void render(GraphicsContext gc) {
	}

	private void setupUDPSockets(Socket serverSocket) throws SocketException {
		commandOutputSocket = new DatagramSocket();
		updateInputSocket = new DatagramSocket();

		System.out.println("hold command udp port at: " + commandOutputSocket.getLocalPort());
		System.out.println("hold update udp port at: " + updateInputSocket.getLocalPort());

		byte[] commandData = new byte[2048];
		commandPacket = new DatagramPacket(commandData, commandData.length);
		commandPacket.setSocketAddress(serverSocket.getRemoteSocketAddress());

		byte[] updateData = new byte[2048];
		updatePacket = new DatagramPacket(updateData, updateData.length);
	}

	private void setupSendingInputsService() {
		long fps20 = 16 * 3;
		AnimationPlayer aniPlayer = new AnimationPlayer(fps20);
		FunctionTriggerAnimation sendInputsToServerAni = new FunctionTriggerAnimation();
		sendInputsToServerAni.addAnchor(0, () -> {
			try {
				consumeInputsAndSendToServer();
			} catch (IOException e) {
				System.out.println("error while sending inputs to server: ");
				System.out.println(e.getClass() + ": " + e.getMessage());

				Platform.exit();
			}
		});

		aniPlayer.addAnimation("sendInputs", sendInputsToServerAni);
		aniPlayer.play(-1);

		addChild(aniPlayer);
	}

	private void setupReceivingUpdateService() {
		Thread thread = new Thread(() -> {
			while (true) {
				try {
					updateInputSocket.receive(updatePacket);

				} catch (IOException e) {
					System.out.println("error while reading updatePacket");
					System.out.println(e.getClass() + ": " + e.getMessage());

					continue;
				}

				byte[] data = Arrays.copyOf(updatePacket.getData(), updatePacket.getLength());

				Updates updates;

				try {
					updates = Updates.parseFrom(data);

				} catch (InvalidProtocolBufferException e) {
					System.out.println("error while parsing protobuff data");
					System.out.println(e.getClass() + ": " + e.getMessage());

					continue;
				}

				updateQueue.addAll(updates.getUpdatesList());
			}
		});

		thread.setDaemon(true);
		thread.start();
	}

	private void consumeInputsAndSendToServer() throws IOException {
		PlayerNetGameNode controlleringPlayer = players.get(controllingPlayerId);
		if (controlleringPlayer == null) {
			return;
		}

		List<Input> inputQueue = controlleringPlayer.inputQueue;
		if (inputQueue.isEmpty()) {
			return;
		}

		byte[] data = Inputs.newBuilder().setClientId(controllingPlayerId).addAllInputs(inputQueue).build()
				.toByteArray();

		commandPacket.setData(data);
		commandOutputSocket.send(commandPacket);

		// Should we clear inputQueue inside PlayerNetGameNode ?
		inputQueue.clear();
	}

	private ServerHandshake handshakeWithServer(Socket serverSocket) throws IOException {
		InputStream in = serverSocket.getInputStream();
		OutputStream out = serverSocket.getOutputStream();

		// I. Sending ClientHandshake to server
		ClientHandshake.newBuilder().setUpdatePort(updateInputSocket.getLocalPort()).build().writeDelimitedTo(out);

		// II. Receive server's port which sending commands to client
		ServerHandshake serverHandshake = ServerHandshake.parseDelimitedFrom(in);

		commandPacket.setAddress(serverSocket.getInetAddress());
		commandPacket.setPort(serverHandshake.getCommandPort());

		return serverHandshake;
	}

	public int getControllingId() {
		return controllingPlayerId;
	}
}