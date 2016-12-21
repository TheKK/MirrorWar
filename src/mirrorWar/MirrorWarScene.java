package mirrorWar;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import gameEngine.Game;
import gameEngine.GameScene;
import javafx.scene.paint.Color;
import mirrorWar.gameStatusUpdate.GameStatusUpdate.ServerMessage;

enum GameState {
	SENDING_READY_MESSAGE, WAIT_FOR_OTHER_PLAYER, START_PLAYING, GAME_OVER
}

public class MirrorWarScene extends GameScene {
	private GameState gameState = GameState.SENDING_READY_MESSAGE;
	private final Socket serverConnSocket;

	
	public MirrorWarScene(Socket s) {
		Game.clearColor = Color.PINK;
		serverConnSocket = s;
	}

	@Override
	protected void initialize() {
		setupTCPGameStateUpdateService();
	}
	
	private void setupTCPGameStateUpdateService() {
		InputStream in;
		
		try {
			in = serverConnSocket.getInputStream();

		} catch (IOException e) {
			DangerousGlobalVariables.logger.severe("Error while retrieving input and output stream.");
			Game.swapScene(new JoinGameScene());
			
			return;
		}
		
		CompletableFuture.runAsync(() -> {
			gameState = GameState.SENDING_READY_MESSAGE;
		})
		.thenRunAsync(() -> {
			rootNode.addChild(new ClientMatrixGameNode(serverConnSocket));
		})
		.thenRunAsync(() -> {
			gameState = GameState.WAIT_FOR_OTHER_PLAYER;

			ServerMessage serverMessage;
			try {
				serverMessage = ServerMessage.parseDelimitedFrom(in);
				
			} catch (IOException e) {
				throw new CompletionException(e);
			}
			
			switch (serverMessage.getMsg()) {
			case GAME_START:
				gameState = GameState.START_PLAYING;
				break;
				
			default:
				DangerousGlobalVariables.logger.severe("Wrong protocol: expecting 'START_PLAYING'");
				break;
			}
		})
		.thenRunAsync(() -> {
			ServerMessage serverMessage;
			try {
				serverMessage = ServerMessage.parseDelimitedFrom(in);
				
			} catch (IOException e) {
				throw new CompletionException(e);
			}
			
			switch (serverMessage.getMsg()) {
			case GAME_OVER:
				gameState = GameState.GAME_OVER;
				break;
				
			default:
				DangerousGlobalVariables.logger.severe("Wrong protocol !!!");
				break;
			}
		})
		.whenComplete((result, e) -> {
			if (e != null) {
				DangerousGlobalVariables.logger.severe("[CLIENT] " + e.getMessage());
			}
			try {
				serverConnSocket.close();
			} catch (IOException ex) {
			}
			
			DangerousGlobalVariables.logger.info("game is over");
			Game.swapScene(new JoinGameScene());
		});
	}
}
