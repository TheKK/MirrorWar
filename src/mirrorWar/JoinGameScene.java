package mirrorWar;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gameEngine.AnimatedSpriteGameNode;
import gameEngine.Game;
import gameEngine.GameNode;
import gameEngine.GameScene;
import gameEngine.RectangleGameNode;
import gameEngine.SpriteGameNode;
import gameEngine.TextGameNode;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;


enum State {
	USER_INPUT,
	WAITTING_CONNECTION,
	WAITTING_FOR_OTHER_PLAYER,
}

public class JoinGameScene extends GameScene {
	private BoardGameNode messageBoard = new BoardGameNode(3000);

	private State currentState = State.USER_INPUT;
	private String serverIp = "";
	private final int width = 200;
	private final int height = 30;

	private MediaPlayer warnningSe;
	private MediaPlayer bgm;
	private MediaPlayer keyPressedSe;
	private MediaPlayer keyDeleteSe;

	@Override
	protected void initialize() {
		Game.clearColor = Color.BLACK;

		CyclicTiledBackground cyclicTileBg = createCyclicTiledBackground();
		rootNode.addChild(cyclicTileBg);

		GameNode dialogBackgroud = new RectangleGameNode(300, 250, width, height, Color.WHITE);
		rootNode.addChild(dialogBackgroud);

		messageBoard.geometry.x = Game.canvasWidth() / 2;
		messageBoard.geometry.y = Game.canvasHeight() * 0.25;
		rootNode.addChild(messageBoard);

		GameNode text = new TextGameNode(serverIp) {
			@Override
			public boolean onKeyPressed(KeyEvent event) {
				switch (event.getCode()) {
					case BACK_SPACE:
						if (serverIp.length() > 0) {
							keyDeleteSe.stop();
							keyDeleteSe.play();
						}
						break;

					case PERIOD:
					case DECIMAL:
						keyPressedSe.stop();
						keyPressedSe.play();
						break;

					default:
						if (event.getCode().isDigitKey()) {
							keyPressedSe.stop();
							keyPressedSe.play();
						}
				}

				switch (event.getCode()) {
					case BACK_SPACE:
						serverIp = cutOffLastWord(serverIp);
						break;
					case PERIOD:
					case DECIMAL:
						contentAppend(".");
						break;
					default:
						if (event.getCode().isDigitKey()) {
							contentAppend(event.getText());
						}
				}

				text = serverIp;

				return true;
			}
		};
		text.geometry.y = 20;
		dialogBackgroud.addChild(text);

		GameNode okBtn = new SpriteGameNode(Game.loadImage("./src/mirrorWar/pic/button_ok.png")) {
			@Override
			protected boolean onMouseReleased(MouseEvent event) {
				tryConnectAndWaitForOtherPlayer();
				return false;
			}

			@Override
			protected boolean onKeyPressed(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					tryConnectAndWaitForOtherPlayer();
				}
				return true;
			}
		};
		okBtn.geometry.x = width;
		okBtn.geometry.width = 50;
		okBtn.geometry.height = height;
		dialogBackgroud.addChild(okBtn);

		loadSeAndPlayBGM();
	}

	@Override
	protected void cleanup() {
		warnningSe.stop();
		keyPressedSe.stop();
		keyDeleteSe.stop();
		bgm.stop();
	}

	private CyclicTiledBackground createCyclicTiledBackground() {
		Image backgroundTileImage = Game.loadImage("./src/mirrorWar/pic/joinSceneBackgroundTile.png");
		AnimatedSpriteGameNode backgroundTileAniSprite = new AnimatedSpriteGameNode(backgroundTileImage , 64, 64);
		backgroundTileAniSprite.autoPlayed = true;
		backgroundTileAniSprite.addFrame(new Rectangle2D.Double(0, 0, 64, 64), 1000);
		backgroundTileAniSprite.addFrame(new Rectangle2D.Double(64, 0, 64, 64), 1000);
		backgroundTileAniSprite.addFrame(new Rectangle2D.Double(0, 64, 64, 64), 1000);
		backgroundTileAniSprite.addFrame(new Rectangle2D.Double(64, 64, 64, 64), 1000);
		backgroundTileAniSprite.geometry.width *= 2;
		backgroundTileAniSprite.geometry.height *= 2;

		return new CyclicTiledBackground(backgroundTileAniSprite);
	}

	private Socket connectToServer(String ip) {
		try {
			InetSocketAddress ipAddr = getIp(ip);
			assert ipAddr != null;
			Socket socket = new Socket();

			currentState = State.WAITTING_CONNECTION;

			socket.connect(ipAddr);

			currentState = State.WAITTING_FOR_OTHER_PLAYER;

			return socket;

		} catch (UnknownHostException e) {
			currentState = State.USER_INPUT;
			throw new CompletionException("Unkown host", e);
		} catch (IOException e) {
			currentState = State.USER_INPUT;
			throw new CompletionException("Connection failed", e);
		} catch (Exception e) {
			currentState = State.USER_INPUT;
			throw new CompletionException(e.getMessage()  , e);
		}
	}

	private void tryConnectAndWaitForOtherPlayer() {
		CompletableFuture
			.supplyAsync(() -> serverIp)
			.thenApplyAsync(this::connectToServer)
			.whenComplete((serverSocket, e) -> {
				if (serverSocket != null) {
					Game.swapScene(new MirrorWarScene(serverSocket));
				} else {
					warnningSe.stop();
					warnningSe.play();
					messageBoard.showMessage(e.getMessage());
				}
			});
	}


	private InetSocketAddress getIp(String ip) throws Exception {
		final String IPADDRESS_PATTERN = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

		Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
		Matcher matcher = pattern.matcher(ip);
		if (matcher.find()) {
			InetSocketAddress ipAddr = new InetSocketAddress(ip, Constants.SERVER_HOST_PORT);
			return ipAddr;
		}

		throw new Exception("IP format is invalid");
	}

	private String cutOffLastWord(String str) {
		String content = "";
		if (str.length() > 0) {
			content = str.substring(0, str.length()-1);
		}

		return content;
	}

	private void contentAppend(String str) {
		if (serverIp.length() < 15) {
			serverIp += str;
		}
	}

	@Override
	protected boolean onMouseReleased(MouseEvent event) {
		switch (currentState) {
			case USER_INPUT:
				return true;
			case WAITTING_CONNECTION:
			case WAITTING_FOR_OTHER_PLAYER:
			default:
				return false;
		}
	}

	@Override
	protected boolean onKeyPressed(KeyEvent event) {
		switch (currentState) {
			case USER_INPUT:
				switch (event.getCode()) {
				case ESCAPE:
					Game.swapScene(new MenuScene());
					break;
				}
				return true;
			case WAITTING_CONNECTION:
			case WAITTING_FOR_OTHER_PLAYER:
			default:
				return false;
		}
	}

	private void loadSeAndPlayBGM() {
		warnningSe = new MediaPlayer(Game.loadMedia("./src/mirrorWar/sounds/joinGameSceneWarnning.wav"));
		keyPressedSe = new MediaPlayer(Game.loadMedia("./src/mirrorWar/sounds/joinGameSceneKey.wav"));
		keyDeleteSe = new MediaPlayer(Game.loadMedia("./src/mirrorWar/sounds/joinGameSceneDelete.wav"));
		bgm = new MediaPlayer(Game.loadMedia("./src/mirrorWar/sounds/joinGameSceneBGM.wav"));

		bgm.setCycleCount(MediaPlayer.INDEFINITE);
		bgm.play();
	}
}