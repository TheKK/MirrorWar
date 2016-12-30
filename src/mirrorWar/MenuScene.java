package mirrorWar;

import gameEngine.AnimationPlayer;
import gameEngine.ContinuousFuncAnimation;
import gameEngine.FunctionTriggerAnimation;
import gameEngine.Game;
import gameEngine.GameNode;
import gameEngine.GameScene;
import gameEngine.SpriteGameNode;
import gameEngine.TransitionFuncs.EaseType;
import gameEngine.TransitionFuncs.TransitionType;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import mirrorWar.TransitionGameNode.Type;

public class MenuScene extends GameScene {
	private static final String MENU_HOVERED_SE = "./src/mirrorWar/sounds/menuItemHovered.wav";

	private static final String JOIN_GAME_TEXT = "./src/mirrorWar/pic/mirrorJoinGameButton.png";
	private static final String CREDITS_TEXT = "./src/mirrorWar/pic/mirrorCreditsButton.png";
	private static final String QUIT_TEXT = "./src/mirrorWar/pic/mirrorQuitButton.png";
	private static final String BUTTON_BACKGROUND_IMAGE = "./src/mirrorWar/pic/mirrorButtonBackground.png";

	private static final String TITLE_IMAGE = "./src/mirrorWar/pic/mirrorTitle.png";

	private MediaPlayer menuHoveredSe;
	private MediaPlayer bgm;

	private GameNode titleImage;
	private GameNode joinGameButton, creditsButton, quitButton;

	@Override
	protected void initialize() {
		final double BUTTON_MARGIN = 40;

		Game.clearColor = Color.BLACK;

		rootNode.addChild(new MenuBackground());

		joinGameButton = this.createButton(
				JOIN_GAME_TEXT,
				() -> { navigateToOtherScene(new JoinGameScene()); });

		joinGameButton.geometry.x = BUTTON_MARGIN;
		joinGameButton.geometry.y = 330;
		rootNode.addChild(joinGameButton);

		creditsButton = this.createButton(
				CREDITS_TEXT,
				() -> {
					GameNode transitionNode = new TransitionGameNode(Type.OUT, () -> {
						Game.pushScene(new CreditScene());
					});

					rootNode.addChild(transitionNode);
				});

		creditsButton.geometry.x = (Game.canvasWidth() - creditsButton.geometry.width) / 2;
		creditsButton.geometry.y = 360;
		rootNode.addChild(creditsButton);

		quitButton = this.createButton(
				QUIT_TEXT,
				() -> { Platform.exit(); });

		quitButton.geometry.x = Game.canvasWidth() - quitButton.geometry.width - BUTTON_MARGIN;
		quitButton.geometry.y = 390;
		rootNode.addChild(quitButton);

		DangerousGlobalVariables.logger.config("In menu scene");

		AnimationPlayer aniPlayer = new AnimationPlayer(7000);
		rootNode.addChild(aniPlayer);

		titleImage = new SpriteGameNode(Game.loadImage(TITLE_IMAGE));
		titleImage.geometry.x = (Game.canvasWidth() - titleImage.geometry.width) / 2;
		titleImage.geometry.y = 20;
		titleImage.anchorX = 0.5;
		titleImage.anchorY = 0.2;
		rootNode.addChild(titleImage);

		ContinuousFuncAnimation<Double> contiAni = new ContinuousFuncAnimation<>(val -> {
			titleImage.scaleX = titleImage.scaleY = 1 + (0.04 * Math.sin(val * 2));

			titleImage.offsetX = 10 * Math.cos(val);
			titleImage.offsetY = 5 * Math.sin(val);
		});
		contiAni.addAnchor(0, 0., TransitionType.LINEAR, EaseType.IN_OUT);
		contiAni.addAnchor(aniPlayer.totalLength(), Math.PI * 2, TransitionType.LINEAR, EaseType.IN_OUT);

		aniPlayer.addAnimation("contiAni", contiAni);
		aniPlayer.play(-1);

		loadSeAndPlayBGM();
	}

	@Override
	protected void cleanup() {
		menuHoveredSe.stop();
		bgm.stop();
	}

	private GameNode createButton(String textImagePath, Runnable clickCallback) {
		final Image backgroundImage = Game.loadImage(BUTTON_BACKGROUND_IMAGE);

		SpriteGameNode backgroundNode = new SpriteGameNode(backgroundImage) {
			@Override
			public boolean onMousePressed(MouseEvent event) {
				if (event.getButton() == MouseButton.PRIMARY) {
					clickCallback.run();
				}

				return true;
			}

			@Override
			protected boolean onMouseEntered(MouseEvent event) {
				menuHoveredSe.stop();
				menuHoveredSe.play();
				return true;
			}
		};

		SpriteGameNode textImageNode = new SpriteGameNode(Game.loadImage(textImagePath)){
			@Override
			public void update(long elapse) {
				if (backgroundNode.isMouseEntered()) {
					offsetX = -5;
					offsetY = -3;
				} else {
					offsetX = offsetY = 0;
				}
			}
		};
		textImageNode.geometry.x = (backgroundNode.geometry.width - textImageNode.geometry.width) / 2;
		textImageNode.geometry.y = (backgroundNode.geometry.height - textImageNode.geometry.height) / 2;
		backgroundNode.addChild(textImageNode);

		return backgroundNode;
	}

	private void navigateToOtherScene(GameScene otherScene) {
		final long animationDuration = 2000;

		this.enable = false;

		addFlyoutAnimation(titleImage, animationDuration);
		addFlyoutAnimation(joinGameButton, animationDuration);
		addFlyoutAnimation(creditsButton, animationDuration);
		addFlyoutAnimation(quitButton, animationDuration);

		AnimationPlayer aniPlayer = new AnimationPlayer(animationDuration);
		aniPlayer.play(1);
		rootNode.addChild(aniPlayer);

		FunctionTriggerAnimation funcAni = new FunctionTriggerAnimation();
		funcAni.addAnchor(aniPlayer.totalLength(), () -> {
			Game.swapScene(otherScene);
		});

		ContinuousFuncAnimation<Double> fadeOutAni = new ContinuousFuncAnimation<>(val -> {
			rootNode.alpha = 1 - val;
		});
		fadeOutAni.addAnchor(0, 0., TransitionType.SIN, EaseType.IN);
		fadeOutAni.addAnchor(animationDuration - 200, 1., TransitionType.SIN, EaseType.OUT);

		aniPlayer.addAnimation("funcAni", funcAni);
		aniPlayer.addAnimation("fadeOutAni", fadeOutAni);
	}

	private void addFlyoutAnimation(GameNode node, long duration) {
		AnimationPlayer aniPlayer = new AnimationPlayer(duration);
		aniPlayer.play(1);
		rootNode.addChild(aniPlayer);

		double originOffsetX = node.offsetX;
		double originOffsetY = node.offsetY;

		double nodeMidX = (node.geometry.x + node.geometry.width) / 2;
		double moveDownOffsetX = ((nodeMidX / Game.canvasWidth()) - 0.5) * -200;
		double moveDownOffsetY = 30 + 10 * Math.random();

		double moveUpOffsetX = ((nodeMidX / Game.canvasWidth()) - 0.5) * 1500;
		double moveUpOffsetY =  2000 + 100 * Math.random();

		long moveUpTime = duration / 5;

		ContinuousFuncAnimation<Double> moveDownAni = new ContinuousFuncAnimation<>(val -> {
			node.offsetX = originOffsetX + (val * moveDownOffsetX);
			node.offsetY = originOffsetY + (val * moveDownOffsetY);
		});
		moveDownAni.addAnchor(0, 0., TransitionType.SIN, EaseType.OUT);
		moveDownAni.addAnchor(moveUpTime, 1., TransitionType.SIN, EaseType.OUT);

		ContinuousFuncAnimation<Double> moveUpAni = new ContinuousFuncAnimation<>(val -> {
			node.offsetX = originOffsetX + moveDownOffsetX + (val * moveUpOffsetX);
			node.offsetY = originOffsetY + moveDownOffsetY - (val * moveUpOffsetY);
		});
		moveUpAni.addAnchor(moveUpTime, 0., TransitionType.SIN, EaseType.IN);
		moveUpAni.addAnchor(duration, 1., TransitionType.SIN, EaseType.OUT);

		aniPlayer.addAnimation("moveDown", moveDownAni);
		aniPlayer.addAnimation("moveUp", moveUpAni);
	}

	private void loadSeAndPlayBGM() {
		menuHoveredSe = new MediaPlayer(Game.loadMedia(MENU_HOVERED_SE));
		bgm = new MediaPlayer(Game.loadMedia("./src/mirrorWar/sounds/menuBGM.wav"));

		bgm.setCycleCount(MediaPlayer.INDEFINITE);
		bgm.play();
	}
}
