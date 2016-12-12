package netGameNodeSDK;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import gameEngine.GameNode;
import gameEngine.GameScene;
import javafx.application.Platform;

public class DevScene extends GameScene {
	public DevScene() {
	}

	@Override
	protected void initialize() {
		try {
			hostServer(Main.serverTCPDefaultPort);
			System.out.println("Server created at localhost:" + Main.serverTCPDefaultPort + "!");

			connectToServer(Main.serverAddr);
			System.out.println("Connected to server!");

		} catch (IOException e) {
			System.out.println("error while connecting to server");
			System.out.println(e.getClass() + ": " + e.getMessage());

			Platform.exit();
			return;
		}
	}

	private void hostServer(int tcpServerPort) throws IOException {
		GameNode serverMatrixGameNode = new ServerMatrixGameNode(tcpServerPort);
		serverMatrixGameNode.visible = false;
		rootNode.addChild(serverMatrixGameNode);
	}

	private void connectToServer(InetSocketAddress serverAddr) throws IOException {
		Socket tcpSocket = new Socket();
		tcpSocket.connect(serverAddr);

		GameNode clientMatrixGameNode = new ClientMatrixGameNode(tcpSocket);
		rootNode.addChild(clientMatrixGameNode);
	}
}