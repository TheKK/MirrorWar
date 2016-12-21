package mirrorWarServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.protobuf.InvalidProtocolBufferException;

import gameEngine.AnimationPlayer;
import gameEngine.FunctionTriggerAnimation;
import gameEngine.Game;
import gameEngine.GameNode;
import gameEngine.RectangleGameNode;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import netGameNodeSDK.ChargerNetGameNode;
import netGameNodeSDK.MirrorNetGameNode;
import netGameNodeSDK.PlayerNetGameNode;
import netGameNodeSDK.charger.Charger.ChargerState;
import netGameNodeSDK.input.InputOuterClass.Inputs;
import netGameNodeSDK.mirror.Mirror.MirrorState;
import netGameNodeSDK.player.Player.PlayerState;
import netGameNodeSDK.update.UpdateOuterClass.Update;
import netGameNodeSDK.update.UpdateOuterClass.Updates;


public class ServerMatrixGameNode extends GameNode {
	private int objectId = 0;

	private Map<PlayerNetGameNode, ClientInfo> playerInfoMap = Collections.synchronizedMap(new HashMap<>());
	private Map<Integer, PlayerNetGameNode> players = Collections.synchronizedMap(new HashMap<>());
	private Map<Integer, MirrorNetGameNode> mirrors = Collections.synchronizedMap(new HashMap<>());
	private Map<Integer, ChargerNetGameNode> chargers = Collections.synchronizedMap(new HashMap<>());

	private DatagramSocket commandInputSocket;
	private DatagramPacket commandPacket;

	private DatagramSocket updateOutputSocket;
	private DatagramPacket updatePacket;
	
	static public class ClientInfo {
		public Socket tcpSocket;
		public InetSocketAddress updateAddr;
		public int id;
		
		public ClientInfo(Socket tcpSocket, InetSocketAddress updateAddr, int clientId) {
			this.tcpSocket = tcpSocket;
			this.updateAddr = updateAddr;
			this.id = clientId;
		}
	}

	public ServerMatrixGameNode(List<ClientInfo> clients) throws IOException {
		setupUDPServer();

		setupUpdateBoardcastingService();
		setupWaitsForCommandsService();

		randomlyAddMirrorToGame();
		randomlyAddChargerToGame();
		
		clients.forEach(this::addNewClient);

		RectangleGameNode wall = new RectangleGameNode(100, 100, 9000, 30, Color.PURPLE);
		Game.currentScene().physicEngine.addStaticNode(wall);
	}

	private void randomlyAddMirrorToGame() {
		for (int i = 0; i < 20; ++i) {
			double x = Math.random() * 1000;
			double y = Math.random() * 1000;
			int id = getUniqueObjectId();

			MirrorNetGameNode newMirror = new MirrorNetGameNode(id);
			newMirror.serverInitialize(Game.currentScene(), false);

			newMirror.geometry.x = x;
			newMirror.geometry.y = y;

			if (Math.random() * 10 >= 5) {
				newMirror.spin();
			}

			addChild(newMirror);

			mirrors.put(id, newMirror);
		}
	}

	private void randomlyAddChargerToGame() {
		for (int i = 0; i < 2; ++i) {
			double x = Math.random() * 300;
			double y = Math.random() * 300;
			int id = getUniqueObjectId();

			ChargerNetGameNode newCharger = new ChargerNetGameNode(id) {
				@Override
				protected void chargePlayer2() {
					gameChargePlayer2();
				}

				@Override
				protected void chargePlayer1() {
					gameChargePlayer1();
				}
			};
			newCharger.serverInitialize(Game.currentScene(), false);

			newCharger.geometry.x = x;
			newCharger.geometry.y = y;

			addChild(newCharger);

			chargers.put(id, newCharger);
		}
	}

	private void gameChargePlayer1() {
		// FIXME
		System.out.println("Player 1 is charged");
	}

	private void gameChargePlayer2() {
		//FIXME
		System.out.println("Player 2 is charged");
	}

	private void setupUpdateBoardcastingService() {
		long fps30 = 16 * 3;

		AnimationPlayer aniPlayer = new AnimationPlayer(fps30);
		FunctionTriggerAnimation boardcastUpdateAni = new FunctionTriggerAnimation();
		boardcastUpdateAni.addAnchor(0, this::boardcastGameWorldStateRoutine);

		aniPlayer.addAnimation("boardcastGameWorldStateRoutine", boardcastUpdateAni);
		aniPlayer.playFromStart(-1);

		addChild(aniPlayer);
	}

	private void setupWaitsForCommandsService() {
		Thread waitsForCommandsThread =
				new Thread(this::waitForCommandsRoutin, "waitForCommandsRoutin");
		waitsForCommandsThread.setDaemon(true);
		waitsForCommandsThread.start();
	}

	@Override
	public void update(long elapse) {
	}

	@Override
	public void render(GraphicsContext gc) {
	}

	private void setupUDPServer() throws SocketException {
		commandInputSocket = new DatagramSocket();
		updateOutputSocket = new DatagramSocket();

		byte[] commandData = new byte[2048];
		commandPacket = new DatagramPacket(commandData, commandData.length);

		byte[] updateData = new byte[2048];
		updatePacket = new DatagramPacket(updateData, updateData.length);
	}

	private void addNewClient(ClientInfo clientInfo) {
		PlayerNetGameNode playerNode = new PlayerNetGameNode(clientInfo.id);
		playerNode.serverInitialize(Game.currentScene(), true);

		addChild(playerNode);

		players.put(clientInfo.id, playerNode);
		playerInfoMap.put(playerNode, clientInfo);
	};

	private synchronized int getUniqueObjectId() {
		return objectId++;
	}

	private void waitForCommandsRoutin() {
		while (true) {
			try {
				commandInputSocket.receive(commandPacket);
			} catch (IOException e) {
				System.out.println("error while reading commandPacket");
				System.out.println(e.getClass() + ": " + e.getMessage());

				Platform.exit();
			}

			byte[] data =
					Arrays.copyOf(commandPacket.getData(), commandPacket.getLength());

			Inputs inputs;

			try {
				inputs = Inputs.parseFrom(data);
			} catch (InvalidProtocolBufferException e) {
				System.out.println("error while parsing protobuff data");
				System.out.println(e.getClass() + ": " + e.getMessage());

				continue;
			}

			PlayerNetGameNode player = players.get(inputs.getClientId());
			if (player == null) {
				System.out.println("Get commands from unknow client, are we hacked!?");
				System.out.println("clientId: " + inputs.getClientId());
				System.out.println("ip address: " + commandPacket.getSocketAddress());

				continue;
			}

			player.inputQueue.addAll(inputs.getInputsList());
		}
	};

	private void boardcastGameWorldStateRoutine() {
		Updates.Builder updatesBuilder = Updates.newBuilder();

		players.forEach((playerId, player) -> {
			PlayerState playerState = player.getStates();
			Update.Builder update = Update.newBuilder()
					.setPlayerState(playerState);

			updatesBuilder.addUpdates(update);
		});

		mirrors.forEach((mirrorId, mirror) -> {
			MirrorState mirrorState = mirror.getStates();
			Update.Builder update = Update.newBuilder()
					.setMirrorState(mirrorState);

			updatesBuilder.addUpdates(update);
		});

		chargers.forEach((chargerId, charger) -> {
			ChargerState chargerState = charger.getStates();
			Update.Builder update = Update.newBuilder()
					.setChargerState(chargerState);

			updatesBuilder.addUpdates(update);
		});

		playerInfoMap.forEach((playerNode, playerInfo) -> {
			byte[] data = updatesBuilder.build().toByteArray();
			updatePacket.setSocketAddress(playerInfo.updateAddr);
			updatePacket.setData(data);

			try {
				updateOutputSocket.send(updatePacket);

			} catch (IOException e) {
				System.out.println("error while sending updates to clilent");
				System.out.println(updatePacket.getSocketAddress());

				return;
			}
		});
	}
}