package netGameNodeSDK;

import java.io.IOException;

import gameEngine.GameNode;
import gameEngine.GameScene;
import javafx.application.Platform;

public class ServerScene extends GameScene {
	@Override
	protected void initialize() {
		GameNode serverMatrixGameNode;
		try {
			serverMatrixGameNode = new ServerMatrixGameNode(Main.serverTCPDefaultPort);

		} catch (IOException e) {
			System.out.println("error while creating ServerMatrixGameNode");
			System.out.println(e.getClass() + ": " + e.getMessage());

			Platform.exit();
			return;
		}

		rootNode.addChild(serverMatrixGameNode);
	}
}
