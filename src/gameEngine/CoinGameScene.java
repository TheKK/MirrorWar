package gameEngine;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.Optional;
import java.util.Random;

import gameEngine.TransitionFuncs.EaseType;
import gameEngine.TransitionFuncs.TransitionType;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

public class CoinGameScene extends GameScene {
	final int COIN_GROUP_ID = 0;
	
	final String COIN_SHADOW_IMAGE_PATH = "./src/application/assets/coinShadow.png";
	final String COIN_ANIMATION_PATH = "./src/application/assets/animatedCoin.png";

	class Coin extends GameNode {
		AnimatedSpriteGameNode animatedCoinSprite;
		SpriteGameNode coinShadowSprite;
		GameNode hitbox = new RootGameNode();

		public Coin(double x, double y) {
			geometry.x = x;
			geometry.y = y;

			coinShadowSprite = new SpriteGameNode(Game.loadImage(COIN_SHADOW_IMAGE_PATH));
			coinShadowSprite.geometry.y = 15;
			addChild(coinShadowSprite);
			
			animatedCoinSprite = new AnimatedSpriteGameNode(Game.loadImage(COIN_ANIMATION_PATH), 16, 16);
			animatedCoinSprite.autoPlayed = true;
			addChild(animatedCoinSprite);
			
			// TODO Make this easier
			final long PERIOD = 200;
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
		}
	}

	public CoinGameScene() {
		Game.clearColor = Color.RED;

		LayerGameNode rootLayer = new LayerGameNode();
		rootNode.addChild(rootLayer);

		AnimationPlayer cameraShakeAniPlayer = new AnimationPlayer(1000);

		GameNode coinGroup = new RootGameNode();
		rootLayer.addChild(coinGroup);

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
		rootLayer.addChild(aniPlayer);

		GameNode player = new RectangleGameNode(100, 100, 50, 50, Color.BLUE) {
			AnimationPlayer countDownAniPlayer = new AnimationPlayer(6000);
			int coins = 0;
			TextGameNode text;

			{
				text = new TextGameNode("0");
				text.geometry.x = 0;
				text.geometry.y = -10;
				addChild(text);
				
				DiscreteFuncAnimation<Integer> countDownAni = new DiscreteFuncAnimation<>(val -> {
					text.text = "EXPLOSION COUNT DOWN: " + val;
				});
				countDownAni.addAnchor(0, 5);
				countDownAni.addAnchor(1000, 4);
				countDownAni.addAnchor(2000, 3);
				countDownAni.addAnchor(3000, 2);
				countDownAni.addAnchor(4000, 1);
				countDownAni.addAnchor(5000, 0);
				
				FunctionTriggerAnimation exitAni = new FunctionTriggerAnimation();
				exitAni.addAnchor(0, () -> {
					text.strokeColor = Color.CHARTREUSE;
				});
				exitAni.addAnchor(countDownAniPlayer.totalLength, () -> { Platform.exit(); });

				countDownAniPlayer.addAnimation("countDown", countDownAni);
				countDownAniPlayer.addAnimation("exit", exitAni);
				addChild(countDownAniPlayer);
			}

			@Override
			public void update(long elapse) {
				final double MOVE_SPEED = 0.060;

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
				}

				physicEngine.removeDynamicNode(node);
				cameraShakeAniPlayer.playFromStart(1);
					
				coins += 1;
				text.text = coins + " !";
				
				if (coins >= 10 && !countDownAniPlayer.isPlaying) {
					countDownAniPlayer.play(1);
					cameraShakeAniPlayer.play(-1);
				}
			}
		};
		player.dampX = 0.1;
		player.dampY = 0.1;

		rootLayer.addChild(player);
		physicEngine.addDynamicNode(player);
		
		SimpleGameSceneCamera camera = new SimpleGameSceneCamera(0, 0, Game.canvasWidth(), Game.canvasHeight());
		camera.cameraTarget = Optional.of(player);
		rootLayer.camera = camera;
		
		Random rng = new Random();
		ContinuousFuncAnimation<Double> cameraPosShakeAni = new ContinuousFuncAnimation<>(
				val -> {
					final double SHAKE_PULSE = 10;
					camera.offsetX = rng.nextDouble() * SHAKE_PULSE;
					camera.offsetY = rng.nextDouble() * SHAKE_PULSE;
				});
		for (int i = 0; i <= 15; ++i) {
			long step = cameraShakeAniPlayer.totalLength() / 15 * i;
			cameraPosShakeAni.addAnchor(step, 0., TransitionType.SIN, EaseType.IN_OUT);
		}
		cameraShakeAniPlayer.addAnimation("cameraPosShake", cameraPosShakeAni);
		rootLayer.addChild(cameraShakeAniPlayer);
	}
}