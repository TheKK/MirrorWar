package mirrorWar;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import gameEngine.AnimationPlayer;
import gameEngine.FunctionTriggerAnimation;
import gameEngine.Game;
import gameEngine.GameScene;
import javafx.scene.paint.Color;
import mirrorWar.gameStatusUpdate.GameStatusUpdate.GameResult;
import mirrorWar.gameStatusUpdate.GameStatusUpdate.ServerMessage;

enum GameState {
	SENDING_READY_MESSAGE, WAIT_FOR_OTHER_PLAYER, START_PLAYING, GAME_OVER
}

public class MirrorWarScene extends GameScene {
	private BoardGameNode messageBoard = new BoardGameNode(5000);
	private AnimationPlayer aniPlayer = new AnimationPlayer(5000);
	private GameState gameState = GameState.SENDING_READY_MESSAGE;
	private final Socket serverConnSocket;


	public MirrorWarScene(Socket s) {
		messageBoard.geometry.x = Game.canvasWidth() / 2;
		messageBoard.geometry.y = Game.canvasHeight() / 2;
		Game.clearColor = Color.PINK;
		serverConnSocket = s;
		setupAnimationPlayer();
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

		Runnable routine = () -> {
			gameState = GameState.WAIT_FOR_OTHER_PLAYER;

			waitForMessageAndDo(in, ServerMessage.Message.ALL_PLAYER_READY,
					() -> { System.out.println("[Client] All players are ready");},
					() -> {});

			// Handshaking inside ClientMatrixGameNode's initialize method
			// TODO This is too implicit, make it more clear and straightforward
			ClientMatrixGameNode clientGameNode = new ClientMatrixGameNode(serverConnSocket);

			waitForMessageAndDo(in, ServerMessage.Message.GAME_START,
					() -> {},
					() -> {});

			rootNode.addChild(clientGameNode);

			GameResult result;
			try {
				result = GameResult.parseDelimitedFrom(in);
			} catch (IOException e) {
				DangerousGlobalVariables.logger.severe(e.getMessage());
				return;
			}

			if (result.getWinnerId() == clientGameNode.getControllingId()) {
				playerWin();
			} else {
				playerLose();
			}
		};

		Thread thread = new  Thread(routine);
		thread.setDaemon(true);
		thread.run();
	}
	
	private void playerWin() {
		messageBoard.showMessage("You win!");
		rootNode.addChild(messageBoard);
		aniPlayer.play(1);
		DangerousGlobalVariables.logger.info("win");
	}
	
	private void playerLose() {
		messageBoard.showMessage("You lose!");
		rootNode.addChild(messageBoard);
		aniPlayer.play(1);
		DangerousGlobalVariables.logger.info("lose");
	}

	private void waitForMessageAndDo(InputStream in, ServerMessage.Message whatMessage,
									  Runnable somethingToDo, Runnable otherwise) {
			ServerMessage serverMessage;

			try {
				serverMessage = ServerMessage.parseDelimitedFrom(in);
			} catch (IOException e) {
				DangerousGlobalVariables.logger.severe(e.getMessage());
				return;
			}

			if (serverMessage.getMsg() == whatMessage) {
				somethingToDo.run();
			} else {
				otherwise.run();
			}
	}
	
	private void setupAnimationPlayer() {
		FunctionTriggerAnimation funcAni = new FunctionTriggerAnimation();
		funcAni.addAnchor(5000, () -> {
			try {
				serverConnSocket.close();
			} catch (IOException e) {
				DangerousGlobalVariables.logger.warning("[CLIENT] " + e.getMessage());
			}

			DangerousGlobalVariables.logger.info("game is over");

			Game.swapScene(new JoinGameScene());
		});
		
		aniPlayer.addAnimation("gameOver", funcAni);
		rootNode.addChild(aniPlayer);
	}
}
