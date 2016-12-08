package mirrorWar;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Optional;
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
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import tcp.GameMessage;
import tcp.TcpClient;

enum State {
	USER_INPUT,
	WAITTING_CONNECTION,
	WAITTING_FOR_OTHER_PLAYER,
}

public class JoinGameScene extends GameScene {
	
	private State currentState = State.USER_INPUT;
	
	private String content = "";
	private final int width = 200;
	private final int height = 30;
	
	public JoinGameScene() {
		Game.clearColor = Color.YELLOW;
		
		CyclicTiledBackground cyclicTileBg = createCyclicTiledBackground();
		rootNode.addChild(cyclicTileBg);

		GameNode dialogBackgroud = new RectangleGameNode(300, 250, width, height, Color.WHITE);
		rootNode.addChild(dialogBackgroud);
		
		GameNode text = new TextGameNode(content) {
			@Override
			public boolean onKeyPressed(KeyEvent event) {
				switch (event.getCode()) {
					case BACK_SPACE:
						content = cutOffLastWord(content);
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
				
				text = content;
				
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
	
	private TcpClient connectToServer(String ip) {
		try {
			InetSocketAddress ipAddr = getIp(ip);
			TcpClient client = new TcpClient();
			
			currentState = State.WAITTING_CONNECTION;
			
			client.connectServer(ipAddr);
			
			currentState = State.WAITTING_FOR_OTHER_PLAYER;

			return client;

		} catch (UnknownHostException e) {
			currentState = State.USER_INPUT;
			throw new CompletionException(e);

		} catch (IOException e) {
			currentState = State.USER_INPUT;
			throw new CompletionException(e);
		}
	}
	
	private void waitForOtherPlayerToJoin(TcpClient tcpClient) {
		try {
			// This would block current thread
			GameMessage gameMessage = tcpClient.waitForGameMessage();
			switch (gameMessage) {
				case TEAM_MATCHED:
					return;
				default:
					Platform.exit();
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			throw new CompletionException(e);
		}	
	}
	
	private void tryConnectAndWaitForOtherPlayer() {
		CompletableFuture
			.supplyAsync(() -> { return content; })
			.thenApplyAsync(this::connectToServer)
			.thenApplyAsync(tcpClient -> {
					DangerousGlobalVariables.tcpClient = Optional.of(tcpClient);
					return tcpClient;
			})
			.thenAcceptAsync(this::waitForOtherPlayerToJoin)
			.whenComplete((result, e) -> {
				if (e == null) {
					DangerousGlobalVariables.logger.info("Game start");
					MirrorWarScene mws = new MirrorWarScene();
					
					currentState = State.USER_INPUT;
					Game.pushScene(mws);
				} else {
					// TODO Show these error message to player
					DangerousGlobalVariables.logger.warning(e.getMessage());
				}
			});
	}
	
	private InetSocketAddress getIp(String ip) throws UnknownHostException {
		final String IPADDRESS_PATTERN = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

		Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
		Matcher matcher = pattern.matcher(ip);
		if (matcher.find()) {
			InetSocketAddress ipAddr = new InetSocketAddress(ip, Constants.SERVER_HOST_PORT);
			return ipAddr;
		}
		
		throw new UnknownHostException("IP is not true");
	}
	
	private String cutOffLastWord(String str) {
		String content = "";
		if (str.length() > 0) {
			content = str.substring(0, str.length()-1);
		}
		
		return content;
	}
	
	private void contentAppend(String str) {
		if (content.length() < 15) {
			content += str;
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
				return true;
			case WAITTING_CONNECTION:
			case WAITTING_FOR_OTHER_PLAYER:
			default:
				return false;
		}
	}
}