package application;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

public class CoinGameScene extends GameScene {
	public CoinGameScene() {
		final int COIN_GROUP_ID = 0;

		Game.clearColor = Color.RED;
		
		for (int i = 0; i < 10; ++i) {
			Random rng = new Random();
			int x = rng.nextInt(400);
			int y = rng.nextInt(400);

			GameNode coin = new RectangleGameNode(x, y, 25, 25, Color.YELLOW);
			coin.addColissionGroup(COIN_GROUP_ID);
			rootNode.addChild(coin);
			physicEngine.addDynamicNode(coin);
		}

		GameNode player = new RectangleGameNode(100, 100, 50, 50, Color.BLUE) {
			int coins = 0;

			boolean upIsPressed = false;
			boolean downIsPressed = false;
			boolean leftIsPressed = false;
			boolean rightIsPressed = false;
			
			TextGameNode text;
			{
				text = new TextGameNode("0");
				text.geometry.x = 0;
				text.geometry.y = -10;
				addChild(text);
			}

			@Override
			public void update(long elapse) {
				if (upIsPressed) this.pulseY -= 0.005/elapse;
				if (downIsPressed) this.pulseY += 0.005/elapse;
				if (leftIsPressed) this.pulseX -= 0.005/elapse;
				if (rightIsPressed) this.pulseX += 0.005/elapse;
			}
			
			@Override
			public void onCollided(GameNode node, long elapse) {
				if (!node.colissionGroup().contains(COIN_GROUP_ID)) {
					return;
				}

				if (node.parent().isPresent()) {
					GameNode parent = node.parent().get();
					parent.removeChild(node);
					
					coins += 1;
					text.text = coins + " !";
				}
			}
			
			@Override
			public boolean onKeyPressed(KeyEvent event) {
				switch (event.getCode()) {
				case UP:
					upIsPressed = true;
					break;
				case DOWN:
					downIsPressed = true;
					break;
				case LEFT:
					leftIsPressed = true;
					break;
				case RIGHT:
					rightIsPressed = true;
					break;
				default:
					break;
				}

				return true;
			}

			@Override
			public boolean onKeyReleased(KeyEvent event) {
				switch (event.getCode()) {
				case UP:
					upIsPressed = false;
					break;
				case DOWN:
					downIsPressed = false;
					break;
				case LEFT:
					leftIsPressed = false;
					break;
				case RIGHT:
					rightIsPressed = false;
					break;
				default:
					break;
				}

				return true;
			}
		};
		player.dampX = 0.002;
		player.dampY = 0.002;

		rootNode.addChild(player);
		physicEngine.addDynamicNode(player);
		
		SimpleGameSceneCamera camera = new SimpleGameSceneCamera(0, 0, Game.canvasWidth(), Game.canvasHeight());
		camera.cameraTarget = Optional.of(player);
		setCamera(camera);
	}
}