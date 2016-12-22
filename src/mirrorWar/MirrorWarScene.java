package mirrorWar;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import gameEngine.Game;
import gameEngine.GameNode;
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

		final Runnable cleanup = () -> {
			try {
				serverConnSocket.close();
			} catch (IOException e) {
				DangerousGlobalVariables.logger.warning("[CLIENT] " + e.getMessage());
			}
		};

		Runnable routine = () -> {
			gameState = GameState.WAIT_FOR_OTHER_PLAYER;

			waitForMessageAndDo(in, ServerMessage.Message.ALL_PLAYER_READY,
					() -> { System.out.println("[Client] All players are ready");},
					() -> {});

			// Handshaking inside ClientMatrixGameNode's initialize method
			// TODO This is too implicit, make it more clear and straightforward
			GameNode clientGameNode = new ClientMatrixGameNode(serverConnSocket);

			waitForMessageAndDo(in, ServerMessage.Message.GAME_START,
					() -> {},
					() -> {});

			rootNode.addChild(clientGameNode);

			waitForMessageAndDo(in, ServerMessage.Message.GAME_OVER,
					() -> {},
					() -> {});

			cleanup.run();

			DangerousGlobalVariables.logger.info("game is over");

			Game.swapScene(new JoinGameScene());
		};

		Thread thread = new  Thread(routine);
		thread.setDaemon(true);
		thread.run();
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
}
