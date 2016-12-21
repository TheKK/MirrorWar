package mirrorWarServer;

import java.io.IOException;
import java.util.List;

import gameEngine.Game;
import gameEngine.GameNode;
import gameEngine.GameScene;
import javafx.application.Platform;
import mirrorWarServer.ServerMatrixGameNode.ClientInfo;

public class ServerScene extends GameScene {
	List<ClientInfo> clients;
	
	public ServerScene(List<ClientInfo> clients) {
		this.clients = clients;
	}
	
	@Override
	protected void initialize() {
		GameNode serverMatrixGameNode;
		Game.title = "Server";
		try {
			serverMatrixGameNode = new ServerMatrixGameNode(clients);

		} catch (IOException e) {
			System.out.println("error while creating ServerMatrixGameNode");
			System.out.println(e.getClass() + ": " + e.getMessage());

			Platform.exit();
			return;
		}

		rootNode.addChild(serverMatrixGameNode);
	}
}
