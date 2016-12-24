package demo;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import gameEngine.AnimatedSpriteGameNode;
import gameEngine.AnimationPlayer;
import gameEngine.ContinuousFuncAnimation;
import gameEngine.DiscreteFuncAnimation;
import gameEngine.FunctionTriggerAnimation;
import gameEngine.Game;
import gameEngine.GameNode;
import gameEngine.GameScene;
import gameEngine.LayerGameNode;
import gameEngine.RectangleGameNode;
import gameEngine.RootGameNode;
import gameEngine.SimpleGameSceneCamera;
import gameEngine.SpriteGameNode;
import gameEngine.TextGameNode;
import gameEngine.TransitionFuncs.EaseType;
import gameEngine.TransitionFuncs.TransitionType;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import mirrorWar.LaserBeam;

public class CoinGameScene extends GameScene {
	final int COIN_GROUP_ID = 0;

	final String COIN_SHADOW_IMAGE_PATH = "./src/application/assets/coinShadow.png";
	final String COIN_ANIMATION_PATH = "./src/application/assets/animatedCoin.png";

	final String LASER_HEAD_ANIMATION_PATH = "./src/mirrorWar/pic/laserHead.png";
	final String LASER_BODY_ANIMATION_PATH = "./src/mirrorWar/pic/laserBody.png";
	final String LASER_TAIL_ANIMATION_PATH = "./src/mirrorWar/pic/laserTail.png";

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
					for (GameNode coin: coinGroup.children()) {
						((Coin) coin).animatedCoinSprite.offsetY = val;
					}
				});
		floatAnimation.addAnchor(0, -3., TransitionType.SIN, EaseType.IN_OUT);
		floatAnimation.addAnchor(1500, 3., TransitionType.SIN, EaseType.IN_OUT);
		floatAnimation.addAnchor(3000, -3., TransitionType.SIN, EaseType.IN_OUT);
		aniPlayer.addAnimation("floatPos", floatAnimation);

		ContinuousFuncAnimation<Double> scaleAni = new ContinuousFuncAnimation<>(
				val -> {
					for (GameNode coin: coinGroup.children()) {
						((Coin) coin).animatedCoinSprite.anchorX = 0.5;
						((Coin) coin).animatedCoinSprite.anchorY = 0.5;

						((Coin) coin).animatedCoinSprite.scaleX = val;
						((Coin) coin).animatedCoinSprite.scaleY = val;

						((Coin) coin).animatedCoinSprite.rotate = val * 360;
					}
				});
		scaleAni.addAnchor(0, 1., TransitionType.SIN, EaseType.IN_OUT);
		scaleAni.addAnchor(1500, 3., TransitionType.SIN, EaseType.IN_OUT);
		scaleAni.addAnchor(3000, 1., TransitionType.SIN, EaseType.IN_OUT);
		aniPlayer.addAnimation("scaleAni", scaleAni);

		aniPlayer.play(-1);
		rootLayer.addChild(aniPlayer);

		int laserWidth = 50, laserHeight = 50;
		AnimatedSpriteGameNode laserHead =
				new AnimatedSpriteGameNode(Game.loadImage(LASER_HEAD_ANIMATION_PATH), laserWidth, laserHeight);
		AnimatedSpriteGameNode laserBody =
				new AnimatedSpriteGameNode(Game.loadImage(LASER_BODY_ANIMATION_PATH), laserWidth, laserHeight);
		AnimatedSpriteGameNode laserTail =
				new AnimatedSpriteGameNode(Game.loadImage(LASER_TAIL_ANIMATION_PATH), laserWidth, laserHeight);

		List<Rectangle2D.Double> frames = new ArrayList<Rectangle2D.Double>();
		frames.add(new Rectangle2D.Double(50 * 0, 0, 50, 50));
		frames.add(new Rectangle2D.Double(50 * 1, 0, 50, 50));
		frames.add(new Rectangle2D.Double(50 * 2, 0, 50, 50));

		frames.forEach(frame -> {
			laserHead.addFrame(frame, 60);
			laserBody.addFrame(frame, 60);
			laserTail.addFrame(frame, 60);
		});

		laserHead.autoPlayed = true;
		laserBody.autoPlayed = true;
		laserTail.autoPlayed = true;

		rootLayer.addChild(new RectangleGameNode(0, 30, 500, 50, Color.WHITE));
		rootLayer.addChild(new RectangleGameNode(-130, 0, 50, 500, Color.WHITE));

		LaserBeam laserBeam = new LaserBeam(laserHead, laserBody, laserTail);
		rootLayer.addChild(laserBeam);

		List<LaserBeam.LaserBeamInfo> beams = new ArrayList<>();
		beams.add(new LaserBeam.LaserBeamInfo(new Rectangle2D.Double(0, 0, 500, 50), LaserBeam.Direction.LEFT));
		beams.add(new LaserBeam.LaserBeamInfo(new Rectangle2D.Double(0, 60, 500, 50), LaserBeam.Direction.RIGHT));
		beams.add(new LaserBeam.LaserBeamInfo(new Rectangle2D.Double(-100, 0, 50, 500), LaserBeam.Direction.DOWN));
		beams.add(new LaserBeam.LaserBeamInfo(new Rectangle2D.Double(-160, 0, 50, 500), LaserBeam.Direction.UP));

		laserBeam.setLaserBeamPositions(beams);

		ContinuousFuncAnimation<Double> beamAni = new ContinuousFuncAnimation<>(
				val -> {
					for (LaserBeam.LaserBeamInfo beam: beams) {
						switch (beam.direction) {
						case DOWN:
						case UP:
							beam.beamBody.height = 150 * val;
							break;

						case LEFT:
						case RIGHT:
							beam.beamBody.width = 150 * val;
							break;
						}
					}
				});
		beamAni.addAnchor(0, 1., TransitionType.SIN, EaseType.IN_OUT);
		beamAni.addAnchor(1500, 2., TransitionType.SIN, EaseType.IN_OUT);
		beamAni.addAnchor(3000, 1., TransitionType.SIN, EaseType.IN_OUT);
		aniPlayer.addAnimation("beamAni", beamAni);

		GameNode player = new RectangleGameNode(0, 0, 50, 50, Color.BLUE) {
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
				exitAni.addAnchor(countDownAniPlayer.totalLength(), () -> { Platform.exit(); });

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
				if (!node.collissionGroup().contains(COIN_GROUP_ID)) {
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

				if (coins >= 10 && !countDownAniPlayer.isPlaying()) {
					countDownAniPlayer.play(1);
					cameraShakeAniPlayer.play(-1);
				}
			}
		};
		player.dampX = 0.9;
		player.dampY = 0.9;

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