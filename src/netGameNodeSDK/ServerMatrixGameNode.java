package netGameNodeSDK;

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
import netGameNodeSDK.handshake.Handshake.ClientHandshake;
import netGameNodeSDK.handshake.Handshake.ServerHandshake;
import netGameNodeSDK.input.InputOuterClass.Inputs;
import netGameNodeSDK.mirror.Mirror.MirrorState;
import netGameNodeSDK.player.Player.PlayerState;
import netGameNodeSDK.update.UpdateOuterClass.Update;
import netGameNodeSDK.update.UpdateOuterClass.Updates;

public class ServerMatrixGameNode extends GameNode {
	private class Client {
		public InetSocketAddress updateAddr;
		public PlayerNetGameNode node;
	}

	private int clientId = 0;
	private int mirrorId = 0;

	private ServerSocket serverSocket;
	private Map<Integer, Client> clients = Collections.synchronizedMap(new HashMap<>());
	private Map<Integer, MirrorNetGameNode> mirrors = Collections.synchronizedMap(new HashMap<>());

	private DatagramSocket commandInputSocket;
	private DatagramPacket commandPacket;

	private DatagramSocket updateOutputSocket;
	private DatagramPacket updatePacket;

	public ServerMatrixGameNode(int tcpServerPort) throws IOException {
		setupTCPServer(tcpServerPort);
		setupUDPServer();

		setupUpdateBoardcastingService();
		setupWaitsForPlayerService();
		setupWaitsForCommandsService();

		randomlyAddMirrorToGame();

		RectangleGameNode wall = new RectangleGameNode(100, 100, 9000, 30, Color.PURPLE);
		Game.currentScene().physicEngine.addStaticNode(wall);
	}

	private void randomlyAddMirrorToGame() {
		for (int i = 0; i < 30; ++i) {
			double x = Math.random() * 1000;
			double y = Math.random() * 1000;
			int id = getUniqueMirrorId();

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

	private void setupUpdateBoardcastingService() {
		long fps30 = 16 * 3;

		AnimationPlayer aniPlayer = new AnimationPlayer(fps30);
		FunctionTriggerAnimation boardcastUpdateAni = new FunctionTriggerAnimation();
		boardcastUpdateAni.addAnchor(0, this::boardcastGameWorldStateRoutine);

		aniPlayer.addAnimation("boardcastGameWorldStateRoutine", boardcastUpdateAni);
		aniPlayer.playFromStart(-1);

		addChild(aniPlayer);
	}

	private void setupWaitsForPlayerService() {
		Thread waitsForPlayerThread =
				new Thread(this::waitsForPlayerRoutine, "waitsForPlayerRoutine");
		waitsForPlayerThread.setDaemon(true);
		waitsForPlayerThread.start();
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

	private void setupTCPServer(int tcpServerPort) throws IOException {
		serverSocket = new ServerSocket(tcpServerPort);
	}

	private void setupUDPServer() throws SocketException {
		commandInputSocket = new DatagramSocket();
		updateOutputSocket = new DatagramSocket();

		byte[] commandData = new byte[2048];
		commandPacket = new DatagramPacket(commandData, commandData.length);

		byte[] updateData = new byte[2048];
		updatePacket = new DatagramPacket(updateData, updateData.length);
	}

	private void addNewClient(InetSocketAddress clientUpdateAddr, int clientId) {
		PlayerNetGameNode playerNode = new PlayerNetGameNode(clientId);
		playerNode.serverInitialize(Game.currentScene(), true);

		addChild(playerNode);

		Client newClient = new Client();
		newClient.updateAddr = clientUpdateAddr;
		newClient.node = playerNode;

		clients.put(clientId, newClient);
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

	private synchronized int getUniqueClientId() {
		clientId += 1;
		return clientId;
	}

	private synchronized int getUniqueMirrorId() {
		mirrorId += 1;
		return mirrorId;
	}

	private void waitsForPlayerRoutine() {
		try {
			while (true) {
				Socket newClientSocket = serverSocket.accept();

				// TODO Consider creating worker threads to do this asynchornously
				new Thread(() -> {
					int clientId = getUniqueClientId();

					ClientHandshake clientHandshake;

					System.out.println("[new player] " + clientId);

					try {
						clientHandshake = handshakeWithClient(newClientSocket, clientId);

					} catch (IOException e) {
						System.out.println("erorr while welcom new player");
						System.out.println(e.getClass() + ": " + e.getMessage());

						return;
					}

					InetSocketAddress newClientUpdateAddr = new InetSocketAddress(
							newClientSocket.getInetAddress(),
							clientHandshake.getUpdatePort());

					addNewClient(newClientUpdateAddr, clientId);

				}).start();
			}

		} catch (IOException e) {
			System.out.println("error while accepting new player");
			System.out.println(e.getClass() + ": " + e.getMessage());

			Platform.exit();
		}
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

			Client client = clients.get(inputs.getClientId());
			if (client == null) {
				System.out.println("Get commands from unknow client, are we hacked!?");
				System.out.println("clientId: " + inputs.getClientId());
				System.out.println("ip address: " + commandPacket.getSocketAddress());

				continue;
			}

			client.node.inputQueue.addAll(inputs.getInputsList());
		}
	};

	private void boardcastGameWorldStateRoutine() {
		Updates.Builder updatesBuilder = Updates.newBuilder();

		clients.forEach((clientId, client) -> {
			PlayerState playerState = client.node.getStates();
			Update.Builder update = Update.newBuilder()
					.setPlayerState(playerState);

			updatesBuilder.addUpdates(update);
		});

		mirrors.forEach((mirrorId, mirror) -> {
			MirrorState mirrorState = mirror.getStates();
			Update.Builder update = Update.newBuilder()
					.setMirrorStaet(mirrorState);

			updatesBuilder.addUpdates(update);
		});

		clients.forEach((clientId, client) -> {
			byte[] data = updatesBuilder.build().toByteArray();
			updatePacket.setSocketAddress(client.updateAddr);
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