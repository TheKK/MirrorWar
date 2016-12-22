package mirrorWarServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import gameEngine.Game;
import gameEngine.GameScene;
import javafx.application.Platform;

public class ServerScene extends GameScene {
	private ServerSocket serverSocket;
	private List<Socket> clients;

	public ServerScene(ServerSocket serverSocket, List<Socket> clients) {
		this.serverSocket = serverSocket;
		this.clients = clients;
	}

	@Override
	protected void initialize() {
		Game.title = "Server";

		try {
			rootNode.addChild(new ServerMatrixGameNode(serverSocket, clients));

		} catch (IOException e) {
			System.out.println("error while creating ServerMatrixGameNode");
			System.out.println(e.getClass() + ": " + e.getMessage());

			Platform.exit();
			return;
		}
	}
}
