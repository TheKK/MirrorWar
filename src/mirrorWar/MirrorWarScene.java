package mirrorWar;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import gameEngine.Game;
import gameEngine.GameScene;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import tcp.GameMessage;
import tcp.TcpClient;
import tcp.WrongMessageException;

enum GameState {
	SENDING_READY_MESSAGE, WAIT_FOR_OTHER_PLAYER, START_PLAYING
}

public class MirrorWarScene extends GameScene {
	private GameState gameState = GameState.SENDING_READY_MESSAGE;
	private final TcpClient tcpClient;
	
	public MirrorWarScene() {
		Game.clearColor = Color.PINK;
		tcpClient = DangerousGlobalVariables.tcpClient.get();
		
		CompletableFuture.runAsync(() -> {
			gameState = GameState.SENDING_READY_MESSAGE;

			try {
				tcpClient.sendGameMessage(GameMessage.PLAYER_IS_READY);
			} catch (IOException e) {
				throw new CompletionException(e);
			}
		})
		.thenRunAsync(() -> {
			gameState = GameState.WAIT_FOR_OTHER_PLAYER;

			try {
				if (tcpClient.waitForGameMessage() == GameMessage.GAME_START) {
					DangerousGlobalVariables.logger.info("[CLIENT] Game start");
				} else {
					throw new CompletionException(new Exception("Wrong protocol!!"));
				}
			} catch (IOException | WrongMessageException e) {
				throw new CompletionException(e);
			}
		})
		.thenRunAsync(() -> {
			gameState = GameState.START_PLAYING;
			
			try {
				if (tcpClient.waitForGameMessage() != GameMessage.GAME_OVER) {
					throw new CompletionException(new Exception("Wrong protocol!!"));
				}
			} catch (Exception e) {
				throw new CompletionException(e);
			}
		})
		.whenComplete((result, e) -> {
			if (e != null) {
				DangerousGlobalVariables.logger.severe("[CLIENT] " + e.getMessage());
				Platform.exit();
			}
			
			DangerousGlobalVariables.logger.info("game is over");
			Game.popScene();
		});

	}
}
