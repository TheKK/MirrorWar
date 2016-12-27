package mirrorWar;

import gameEngine.AnimationPlayer;
import gameEngine.ContinuousFuncAnimation;
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

	private static final String BACKGROUND_IMAGE = "./src/mirrorWar/pic/miku.jpg";

	private static final String JOIN_GAME_TEXT = "./src/mirrorWar/pic/mirrorJoinGameButton.png";
	private static final String CREDITS_TEXT = "./src/mirrorWar/pic/mirrorCreditsButton.png";
	private static final String QUIT_TEXT = "./src/mirrorWar/pic/mirrorQuitButton.png";
	private static final String BUTTON_BACKGROUND_IMAGE = "./src/mirrorWar/pic/mirrorButtonBackground.png";

	private static final String TITLE_IMAGE = "./src/mirrorWar/pic/mirrorTitle.png";

	MediaPlayer menuHoveredSe;

	@Override
	protected void initialize() {
		final double BUTTON_MARGIN = 40;

		Game.clearColor = Color.BLUEVIOLET;

		SpriteGameNode background = new SpriteGameNode(Game.loadImage(BACKGROUND_IMAGE));
		rootNode.addChild(background);
		GameNode joinGameButton = this.createButton(
				JOIN_GAME_TEXT,
				() -> { Game.swapScene(new JoinGameScene()); });

		joinGameButton.geometry.x = BUTTON_MARGIN;
		joinGameButton.geometry.y = 330;
		rootNode.addChild(joinGameButton);

		GameNode creditsButton = this.createButton(
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

		GameNode quitButton = this.createButton(
				QUIT_TEXT,
				() -> { Platform.exit(); });

		quitButton.geometry.x = Game.canvasWidth() - quitButton.geometry.width - BUTTON_MARGIN;
		quitButton.geometry.y = 390;
		rootNode.addChild(quitButton);

		DangerousGlobalVariables.logger.config("In menu scene");

		AnimationPlayer aniPlayer = new AnimationPlayer(7000);
		rootNode.addChild(aniPlayer);

		SpriteGameNode title = new SpriteGameNode(Game.loadImage(TITLE_IMAGE));
		title.geometry.x = (Game.canvasWidth() - title.geometry.width) / 2;
		title.geometry.y = 20;
		title.anchorX = 0.5;
		title.anchorY = 0.2;
		rootNode.addChild(title);

		ContinuousFuncAnimation<Double> contiAni = new ContinuousFuncAnimation<>(val -> {
			title.scaleX = title.scaleY = 1 + (0.04 * Math.sin(val * 2));

			title.offsetX = 10 * Math.cos(val);
			title.offsetY = 5 * Math.sin(val);
		});
		contiAni.addAnchor(0, 0., TransitionType.LINEAR, EaseType.IN_OUT);
		contiAni.addAnchor(aniPlayer.totalLength(), Math.PI * 2, TransitionType.LINEAR, EaseType.IN_OUT);

		aniPlayer.addAnimation("contiAni", contiAni);
		aniPlayer.play(-1);

		menuHoveredSe = new MediaPlayer(Game.loadMedia(MENU_HOVERED_SE));
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
}
