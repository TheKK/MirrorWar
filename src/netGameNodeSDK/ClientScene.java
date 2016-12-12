package netGameNodeSDK;

import java.io.IOException;
import java.net.Socket;

import gameEngine.GameNode;
import gameEngine.GameScene;
import javafx.application.Platform;

public class ClientScene extends GameScene {
	private Socket tcpSocket;

	@Override
	protected void initialize() {
		connectToServer();

		GameNode clientMatrixGameNode = new ClientMatrixGameNode(tcpSocket);
		rootNode.addChild(clientMatrixGameNode);
	}

	private void connectToServer() {
		try {
			tcpSocket = new Socket();
			tcpSocket.connect(Main.serverAddr);
//			tcpSocket.connect(new InetSocketAddress("140.115.59.164", Main.serverTCPDefaultPort));
			System.out.println("Connected to server! " + tcpSocket);

		} catch (IOException e) {
			System.out.println("error while connecting to server");
			System.out.println(e.getClass() + ": " + e.getMessage());

			Platform.exit();
		}
	}
}
