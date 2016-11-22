package gameEngine;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

public class CoinGameScene extends GameScene {
	public CoinGameScene() {
		final int COIN_GROUP_ID = 0;

		Game.clearColor = Color.RED;
		
		for (int i = 0; i < 100; ++i) {
			Random rng = new Random();
			int x = -500 + rng.nextInt(1000);
			int y = -500 + rng.nextInt(1000);

			GameNode coin = new RectangleGameNode(x, y, 25, 25, Color.YELLOW);
			coin.addColissionGroup(COIN_GROUP_ID);
			rootNode.addChild(coin);
			physicEngine.addDynamicNode(coin);
		}

		GameNode player = new RectangleGameNode(100, 100, 50, 50, Color.BLUE) {
			int coins = 0;

			TextGameNode text;
			{
				text = new TextGameNode("0");
				text.geometry.x = 0;
				text.geometry.y = -10;
				addChild(text);
			}

			@Override
			public void update(long elapse) {
				final double MOVE_SPEED = 0.010;

				if (Game.getKeyboardState(KeyCode.UP)) this.pulseY -= MOVE_SPEED/elapse;
				if (Game.getKeyboardState(KeyCode.DOWN)) this.pulseY += MOVE_SPEED/elapse;
				if (Game.getKeyboardState(KeyCode.LEFT)) this.pulseX -= MOVE_SPEED/elapse;
				if (Game.getKeyboardState(KeyCode.RIGHT)) this.pulseX += MOVE_SPEED/elapse;
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
		};
		player.dampX = 0.005;
		player.dampY = 0.005;

		rootNode.addChild(player);
		physicEngine.addDynamicNode(player);
		
		SimpleGameSceneCamera camera = new SimpleGameSceneCamera(0, 0, Game.canvasWidth(), Game.canvasHeight());
		camera.cameraTarget = Optional.of(player);
		setCamera(camera);
	}
}