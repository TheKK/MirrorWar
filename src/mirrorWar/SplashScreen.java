package mirrorWar;

import java.io.FileNotFoundException;

import gameEngine.AnimatedSpriteGameNode;
import gameEngine.AnimationPlayer;
import gameEngine.FunctionTriggerAnimation;
import gameEngine.Game;
import gameEngine.GameScene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;

public class SplashScreen extends GameScene {
	private final long SPLASH_DISPLAY_INTERVAL = 5000;
	private final long LOGO_FADEIN_TIME = 1500;
	private final long LOGO_FADEOUT_TIME = 3000;
	private final long SEGA_PALY_TIME = 1000;

	private final String SPLASH_IMAGE_PATH = "./src/application/assets/splash.png";
	private final String SEGA_SOUND_PATH = "./src/application/assets/sega.wav";
	private MediaPlayer sega;

	public SplashScreen() {
	}

	@Override
	protected void initialize() {
		AnimatedSpriteGameNode slpashSprite;
		try {
			slpashSprite = new AnimatedSpriteGameNode(
					Game.loadImage("./src/mirrorWar/pic/mirrorSplash.png"),
					"./src/mirrorWar/pic/mirrorSplash.json");
		} catch (FileNotFoundException e) {
			return;
		}

		slpashSprite.geometry.setFrame(0, 0, Game.canvasWidth(), Game.canvasHeight());
		slpashSprite.playFromStart(1);
		rootNode.addChild(slpashSprite);

		sega = new MediaPlayer(Game.loadMedia(SEGA_SOUND_PATH));

		// FIXME Neither animation player or animated sprite has accurate problem
		long slpashLength = slpashSprite.animationLength() - 200;

		AnimationPlayer fadeinAniPlayer = new AnimationPlayer(slpashLength);
		fadeinAniPlayer.play(1);
		rootNode.addChild(fadeinAniPlayer);

		FunctionTriggerAnimation functionTriggerAni = new FunctionTriggerAnimation();
		functionTriggerAni.addAnchor(slpashLength, () -> {
			Game.swapScene(new MenuScene());
		});

		fadeinAniPlayer.addAnimation("sega", functionTriggerAni);
	}

	@Override
	protected boolean onKeyPressed(KeyEvent event) {
		sega.stop();
		Game.swapScene(new MenuScene());
		return false;
	}

	@Override
	protected boolean onMousePressed(MouseEvent event) {
		sega.stop();
		Game.swapScene(new MenuScene());
		return false;
	}
}