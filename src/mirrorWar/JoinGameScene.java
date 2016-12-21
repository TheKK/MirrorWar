package mirrorWar;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
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
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import mirrorWar.gameStatusUpdate.GameStatusUpdate.ServerMessage;
import netGameNodeSDK.handshake.Handshake.ClientHandshake;
import netGameNodeSDK.handshake.Handshake.ServerHandshake;


enum State {
	USER_INPUT,
	WAITTING_CONNECTION,
	WAITTING_FOR_OTHER_PLAYER,
}

public class JoinGameScene extends GameScene {
	
	private State currentState = State.USER_INPUT;
	private DatagramSocket updateInputSocket;
	private DatagramPacket updatePacket;
	private DatagramSocket commandOutputSocket;
	private DatagramPacket commandPacket;
	private String serverIp = "";
	private final int width = 200;
	private final int height = 30;
	private int controllingPlayerId = 0;
	
	public JoinGameScene() {
		Game.clearColor = Color.YELLOW;
		
		CyclicTiledBackground cyclicTileBg = createCyclicTiledBackground();
		rootNode.addChild(cyclicTileBg);

		GameNode dialogBackgroud = new RectangleGameNode(300, 250, width, height, Color.WHITE);
		rootNode.addChild(dialogBackgroud);
		
		GameNode text = new TextGameNode(serverIp) {
			@Override
			public boolean onKeyPressed(KeyEvent event) {
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
			throw new CompletionException(e);

		} catch (IOException e) {
			currentState = State.USER_INPUT;
			throw new CompletionException(e);
		}
	}
	
	private void tryConnectAndWaitForOtherPlayer() {
		Runnable routine = () -> {
			Socket serverSocket;
			serverSocket = connectToServer(serverIp);
			
			DangerousGlobalVariables.logger.info("Game start");
			
			
			CompletableFuture
				.supplyAsync(() -> new MirrorWarScene(serverSocket))
				.whenComplete((result, e) -> {
					if (result != null) {
						Game.swapScene(result);	
					} else {
						e.printStackTrace();
					}
				});
		};

		new Thread(routine).run();
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
				return true;
			case WAITTING_CONNECTION:
			case WAITTING_FOR_OTHER_PLAYER:
			default:
				return false;
		}
	}
	
	private ServerHandshake handshakeWithServer(Socket serverSocket) {
		try {
			InputStream in = serverSocket.getInputStream();
			OutputStream out = serverSocket.getOutputStream();
			
			// I. Sending ClientHandshake to server
			ClientHandshake.newBuilder()
					.setUpdatePort(updateInputSocket.getLocalPort())
					.build()
					.writeDelimitedTo(out);
	
			// II. Receive server's port which sending commands to client
			ServerHandshake serverHandshake = ServerHandshake.parseDelimitedFrom(in);
	
			commandPacket.setAddress(serverSocket.getInetAddress());
			commandPacket.setPort(serverHandshake.getCommandPort());
			
			return serverHandshake;
		} catch (IOException e) {
			throw new CompletionException(e);
		}
	}
	
	private void setupUDPSockets(Socket serverSocket) throws SocketException {
		commandOutputSocket = new DatagramSocket();
		updateInputSocket = new DatagramSocket();

		byte[] commandData = new byte[2048];
		commandPacket = new DatagramPacket(commandData, commandData.length);
		commandPacket.setSocketAddress(serverSocket.getRemoteSocketAddress());

		byte[] updateData = new byte[2048];
		updatePacket = new DatagramPacket(updateData, updateData.length);
	}
}