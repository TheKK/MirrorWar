package mirrorWarServer;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.protobuf.InvalidProtocolBufferException;

import MapLoader.MapLoader;
import MapLoader.MapObject;
import gameEngine.AnimationPlayer;
import gameEngine.FunctionTriggerAnimation;
import gameEngine.Game;
import gameEngine.GameNode;
import gameEngine.RectangleGameNode;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import mirrorWar.Constants;
import mirrorWar.DangerousGlobalVariables;
import mirrorWar.charger.Charger.ChargerState;
import mirrorWar.charger.Charger.ChargerState.Animation;
import mirrorWar.gameReport.GameReport;
import mirrorWar.gameStatusUpdate.GameStatusUpdate;
import mirrorWar.gameStatusUpdate.GameStatusUpdate.ServerMessage;
import mirrorWar.handshake.Handshake.ClientHandshake;
import mirrorWar.handshake.Handshake.ServerHandshake;
import mirrorWar.input.InputOuterClass.Inputs;
import mirrorWar.laser.Laser.LaserState;
import mirrorWar.mirror.Mirror.MirrorState;
import mirrorWar.player.Player.PlayerState;
import mirrorWar.update.UpdateOuterClass.Update;
import mirrorWar.update.UpdateOuterClass.Updates;
import netGameNodeSDK.ChargerNetGameNode;
import netGameNodeSDK.GameReportNetGameNode;
import netGameNodeSDK.LaserEmitterNetGameNode;
import netGameNodeSDK.MirrorNetGameNode;
import netGameNodeSDK.PlayerNetGameNode;

public class ServerGameNode extends GameNode {
	private int objectId = 0;

	private List<Socket> playerSockets;
	private Map<PlayerNetGameNode, InetSocketAddress> playerIPMap = Collections.synchronizedMap(new HashMap<>());
	private Map<Integer, PlayerNetGameNode> players = Collections.synchronizedMap(new HashMap<>());
	private Map<Integer, MirrorNetGameNode> mirrors = Collections.synchronizedMap(new HashMap<>());
	private Map<Integer, ChargerNetGameNode> chargers = Collections.synchronizedMap(new HashMap<>());
	private Map<Integer, LaserEmitterNetGameNode> lasers = Collections.synchronizedMap(new HashMap<>());
	private Map<Integer, Rectangle2D.Double> playersRespawnRegion = new HashMap<Integer, Rectangle2D.Double>();

	private DatagramSocket commandInputSocket;
	private DatagramPacket commandPacket;

	private DatagramSocket updateOutputSocket;
	private DatagramPacket updatePacket;
	
	private GameReportNetGameNode gameReport = new GameReportNetGameNode() {
		@Override
		protected void beAttacked() {
		}
	};
	
	public ServerGameNode(ServerSocket serverSocket, List<Socket> playerSockets) throws IOException {
		this.playerSockets = playerSockets;
		
		MapLoader mLoader = new MapLoader("./src/MapLoader/map.json"); 

		readPlayerRespawnRegion(mLoader.getObjectLayers().get("respawnArea"));

		setupUDPServer();

		this.playerSockets.forEach(this::addNewClient);

		// Tell all player to start their games!
		// TODO Extract this to a proper place
		sendMessageToAllClient(ServerMessage.Message.GAME_START);

		setupUpdateBoardcastingService();
		setupWaitsForCommandsService();
		
		addLaserToGame(mLoader.getObjectLayers().get("lasers"));
		addMirrorToGame(mLoader.getObjectLayers().get("mirrors"));
		addChargerToGame(mLoader.getObjectLayers().get("chargers"));
		addWallToGame(mLoader.getObjectLayers().get("walls"));
		
		addChild(gameReport);
		
		Game.canvas.setWidth(1250);
		Game.canvas.setHeight(750);
		Game.stage.setWidth(1250);
		Game.stage.setHeight(750);
	}
	
	private void readPlayerRespawnRegion(List<MapObject> respawnRegions) {
		for (int i = 0; i < respawnRegions.size(); ++i) {
			MapObject region = respawnRegions.get(i);
			playersRespawnRegion.put(i,new Rectangle2D.Double(region.x, region.y, region.width, region.height));
		}
	}

	private void sendMessageToAllClient(ServerMessage.Message msg) {
		playerSockets.forEach(socket -> {
			try {
				OutputStream out = socket.getOutputStream();

				GameStatusUpdate.ServerMessage.newBuilder()
					.setMsg(msg)
					.build()
					.writeDelimitedTo(out);

			} catch (IOException e) {
				DangerousGlobalVariables.logger.severe("Connection error: Unable to sent message to client");
			}
		});
	}
	
	private void addLaserToGame(List<MapObject> laserList) {
		for (MapObject laser : laserList) {
			int id = getUniqueObjectId();
			int ownerId = Integer.valueOf(laser.properties.get("owner"));

			LaserEmitterNetGameNode newLaser = new LaserEmitterNetGameNode(id, ownerId);
			newLaser.serverInitialize(Game.currentScene(), false);

			newLaser.geometry.x = laser.x;
			newLaser.geometry.y = laser.y;
			newLaser.geometry.width = laser.width;
			newLaser.geometry.height = laser.height;
			
			for (LaserState.Direction dir : LaserState.Direction.values()) {
				if (dir.toString().equals(laser.properties.get("direction"))) {
					newLaser.currentDir = dir;
				}
			}

			addChild(newLaser);

			lasers.put(id, newLaser);
		}
	}

	private void addMirrorToGame(List<MapObject> mirrorList) {
		for (MapObject mirror : mirrorList) {
			int id = getUniqueObjectId();

			MirrorNetGameNode newMirror = new MirrorNetGameNode(id);
			newMirror.serverInitialize(Game.currentScene(), false);

			newMirror.geometry.x = mirror.x;
			newMirror.geometry.y = mirror.y;
			newMirror.geometry.width = mirror.width;
			newMirror.geometry.height = mirror.height;

			if (Math.random() * 10 >= 5) {
				newMirror.spin();
			}

			addChild(newMirror);

			mirrors.put(id, newMirror);
		}
	}

	private void addChargerToGame(List<MapObject> chargerList) {
		for (MapObject charger : chargerList) {
			int id = getUniqueObjectId();

			ChargerNetGameNode newCharger = new ChargerNetGameNode(id) {
				@Override
				public void chargePlayer0() {
					isCharging = true;
					gameChargePlayer0();
				}
				
				@Override
				public void chargePlayer1() {
					isCharging = true;
					gameChargePlayer1();
				}

			};
			newCharger.serverInitialize(Game.currentScene(), false);

			newCharger.geometry.x = charger.x;
			newCharger.geometry.y = charger.y;
			newCharger.geometry.width = charger.width;
			newCharger.geometry.height = charger.height;

			addChild(newCharger);

			chargers.put(id, newCharger);
		}
	}
	
	private void addWallToGame(List<MapObject> wallList) {
		for (MapObject wall : wallList) {
			RectangleGameNode newWall = new RectangleGameNode(wall.x, wall.y, wall.width, wall.height, Color.TRANSPARENT);
			
			addChild(newWall);
			Game.currentScene().physicEngine.addStaticNode(newWall);
		}
	}

	private void gameChargePlayer0() {
		gameReport.chargeEnergy(0, Constants.DEFAULT_CHARGE_ENERGY);
	}

	private void gameChargePlayer1() {
		gameReport.chargeEnergy(1, Constants.DEFAULT_CHARGE_ENERGY);
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
		for (int id = 0; id < Constants.PLAYER_NUM; ++id) {
			if (gameReport.isPlayerDead(id)) {
				sendResultToAllClient(id);
				playerSockets.clear();
				Platform.exit();
			}
		}
	}
	
	private void sendResultToAllClient(int loseId) {
		// dirty code
		int winnerId = (loseId == 0) ? 1: 0;
		
		playerSockets.forEach(socket -> {
			try {
				OutputStream out = socket.getOutputStream();

				GameStatusUpdate.GameResult.newBuilder()
					.setWinnerId(winnerId)
					.build()
					.writeDelimitedTo(out);
			} catch (IOException e) {
				DangerousGlobalVariables.logger.severe("Connection error: Unable to sent message to client");
			}
		});
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

	private void addNewClient(Socket newClientSocket) {
		int clientId = getUniqueObjectId();

		ClientHandshake clientHandshake;

		System.out.println("[new player] " + clientId + ", from: " + newClientSocket.getRemoteSocketAddress());

		try {
			clientHandshake = handshakeWithClient(newClientSocket, clientId);

		} catch (IOException e) {
			System.out.println("erorr while welcom new player");
			System.out.println(e.getClass() + ": " + e.getMessage());

			return;
		}

		Rectangle2D.Double rec = playersRespawnRegion.get(clientId);
		PlayerNetGameNode playerNode = new PlayerNetGameNode(clientId, rec);
		playerNode.serverInitialize(Game.currentScene(), true);
		addChild(playerNode);

		InetSocketAddress newClientUpdateAddr = new InetSocketAddress(
				newClientSocket.getInetAddress(),
				clientHandshake.getUpdatePort());

		players.put(clientId, playerNode);
		playerIPMap.put(playerNode, newClientUpdateAddr);
	};

	private ClientHandshake handshakeWithClient(Socket socket, int clientId) throws IOException {
		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();

		// I. Wait for player sending ClientHandshake
		ClientHandshake clientHandshake = ClientHandshake.parseDelimitedFrom(in);

		// II. Send server's port which accepting client's commands
		ServerHandshake.newBuilder()
				.setCommandPort(commandInputSocket.getLocalPort())
				.setClientId(clientId)
				.build()
				.writeDelimitedTo(out);

		return clientHandshake;
	}

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

			byte[] data = Arrays.copyOf(commandPacket.getData(), commandPacket.getLength());
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
		
		lasers.forEach((laserId, laser) -> {
			LaserState laserState = laser.getStates();
			Update.Builder update = Update.newBuilder()
					.setLaserState(laserState);

			updatesBuilder.addUpdates(update);
		});
		
		{
			GameReport.Status gameReportStatus = gameReport.getStates();
			Update.Builder update = Update.newBuilder()
					.setGameReportState(gameReportStatus);

			updatesBuilder.addUpdates(update);
		}

		playerIPMap.forEach((playerNode, playerIP) -> {
			byte[] data = updatesBuilder.build().toByteArray();
			updatePacket.setSocketAddress(playerIP);
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