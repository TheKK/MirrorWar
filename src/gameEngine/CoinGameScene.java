package gameEngine;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.Optional;
import java.util.Random;

import gameEngine.TransitionFuncs.EaseType;
import gameEngine.TransitionFuncs.TransitionType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

public class CoinGameScene extends GameScene {
	final int COIN_GROUP_ID = 0;

	class Coin extends GameNode {
		AnimatedSpriteGameNode animatedCoinSprite;
		SpriteGameNode coinShadowSprite;
		GameNode hitbox = new RootGameNode();

		public Coin(double x, double y) {
			geometry.x = x;
			geometry.y = y;

			File coinShadowFile = new File("./src/application/assets/coinShadow.png");
			Image coinShadowImage = new Image(coinShadowFile.toURI().toString());
			coinShadowSprite = new SpriteGameNode(coinShadowImage);
			coinShadowSprite.geometry.y = 15;
			addChild(coinShadowSprite);
			
			File coinFile = new File("./src/application/assets/animatedCoin.png");
			Image coinImage = new Image(coinFile.toURI().toString());
			animatedCoinSprite = new AnimatedSpriteGameNode(coinImage, 16, 16);
			animatedCoinSprite.autoPlayed = true;
			
			// TODO Make this easier
			final long PERIOD = 300;
			animatedCoinSprite.addFrame(
					new Rectangle2D.Double(16 * 0, 16 * 0, 16, 16),
					PERIOD);
			animatedCoinSprite.addFrame(
					new Rectangle2D.Double(16 * 1, 16 * 0, 16, 16),
					PERIOD);
			animatedCoinSprite.addFrame(
					new Rectangle2D.Double(16 * 2, 16 * 0, 16, 16),
					PERIOD);
			animatedCoinSprite.addFrame(
					new Rectangle2D.Double(16 * 3, 16 * 0, 16, 16),
					PERIOD);
			animatedCoinSprite.addFrame(
					new Rectangle2D.Double(16 * 0, 16 * 1, 16, 16),
					PERIOD);
			animatedCoinSprite.addFrame(
					new Rectangle2D.Double(16 * 1, 16 * 1, 16, 16),
					PERIOD);
			addChild(animatedCoinSprite);
		}
	}

	public CoinGameScene() {
		Game.clearColor = Color.RED;

		GameNode coinGroup = new RootGameNode();
		rootNode.addChild(coinGroup);

		for (int i = 0; i < 100; ++i) {
			Random rng = new Random();
			int x = -500 + rng.nextInt(1000);
			int y = -500 + rng.nextInt(1000);

			Coin coin = new Coin(x, y);
			coin.geometry.width = 16;
			coin.geometry.height = 16;
			coin.addColissionGroup(COIN_GROUP_ID);
			physicEngine.addDynamicNode(coin);

			coinGroup.addChild(coin);
		}

		AnimationPlayer aniPlayer = new AnimationPlayer(3000);
		ContinuousFuncAnimation<Double> floatAnimation = new ContinuousFuncAnimation<>(
				val -> {
					for (GameNode coin: coinGroup.children) {
						((Coin) coin).animatedCoinSprite.offsetY = val;
					}
				});
		floatAnimation.addAnchor(0, -3., TransitionType.SIN, EaseType.IN_OUT);
		floatAnimation.addAnchor(1500, 3., TransitionType.SIN, EaseType.IN_OUT);
		floatAnimation.addAnchor(3000, -3., TransitionType.SIN, EaseType.IN_OUT);
		aniPlayer.addAnimation("floatPos", floatAnimation);

		aniPlayer.play(-1);
		rootNode.addChild(aniPlayer);

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

					physicEngine.removeDynamicNode(node);
					
					coins += 1;
					text.text = coins + " !";
				}
			}
		};
		player.dampX = 0.008;
		player.dampY = 0.008;

		rootNode.addChild(player);
		physicEngine.addDynamicNode(player);
		
		SimpleGameSceneCamera camera = new SimpleGameSceneCamera(0, 0, Game.canvasWidth(), Game.canvasHeight());
		camera.cameraTarget = Optional.of(player);
		setCamera(camera);
	}
}