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
import javafx.scene.input.KeyEvent;
import javafx.scene.media.MediaPlayer;

public class SplashScreen extends GameScene {
	private final long SPLASH_DISPLAY_INTERVAL = 5000;
	private final long LOGO_FADEIN_TIME = 1500;
	private final long LOGO_FADEOUT_TIME = 3000;
	private final long SEGA_PALY_TIME = 1000;
	
	private final String SPLASH_IMAGE_PATH = "./src/application/assets/splash.png";
	private final String SEGA_SOUND_PATH = "./src/application/assets/sega.wav";
	private final MediaPlayer sega;
	
	public SplashScreen() {
		GameNode splashImage = new SpriteGameNode(Game.loadImage(SPLASH_IMAGE_PATH));
		splashImage.geometry.setFrame(0, 0, Game.canvasWidth(), Game.canvasHeight());
		rootNode.addChild(splashImage);

		sega = new MediaPlayer(Game.loadMedia(SEGA_SOUND_PATH));

		AnimationPlayer fadeinAniPlayer = new AnimationPlayer(SPLASH_DISPLAY_INTERVAL);
		rootNode.addChild(fadeinAniPlayer);
		
		ContinuousFuncAnimation<Double> logoFadeInAni = new ContinuousFuncAnimation<>((val) -> {
			splashImage.alpha = val;
		});
		logoFadeInAni.addAnchor(0, 0., TransitionType.SIN, EaseType.IN_OUT);
		logoFadeInAni.addAnchor(LOGO_FADEIN_TIME, 1., TransitionType.SIN, EaseType.IN_OUT);
		logoFadeInAni.addAnchor(LOGO_FADEOUT_TIME, 1., TransitionType.SIN, EaseType.IN_OUT);
		logoFadeInAni.addAnchor(SPLASH_DISPLAY_INTERVAL, 0., TransitionType.SIN, EaseType.IN_OUT);
		
		FunctionTriggerAnimation functionTriggerAni = new FunctionTriggerAnimation();
		functionTriggerAni.addAnchor(SEGA_PALY_TIME, () -> {
			sega.play();
		});
		functionTriggerAni.addAnchor(SPLASH_DISPLAY_INTERVAL, () -> {
			Game.swapScene(new MenuScreen());
		});

		fadeinAniPlayer.addAnimation("logo", logoFadeInAni);
		fadeinAniPlayer.addAnimation("sega", functionTriggerAni);
		fadeinAniPlayer.play(1);
	}
	
	@Override
	protected boolean onKeyPressed(KeyEvent event) {
		sega.stop();
		Game.swapScene(new MenuScreen());
		return false;
	}
}