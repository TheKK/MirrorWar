package demo;

import gameEngine.AnimationPlayer;
import gameEngine.ContinuousFuncAnimation;
import gameEngine.FunctionTriggerAnimation;
import gameEngine.Game;
import gameEngine.GameNode;
import gameEngine.GameScene;
import gameEngine.SpriteGameNode;
import gameEngine.TransitionFuncs.EaseType;
import gameEngine.TransitionFuncs.TransitionType;
import javafx.scene.media.MediaPlayer;

public class SplashGameScene extends GameScene {
	final long SPLASH_DISPLAY_INTERVAL = 5000;
	final long LOGO_FADEIN_TIME = 1500;
	final long LOGO_FADEOUT_TIME = 3000;
	final long SEGA_PALY_TIME = 1000;
	
	final String SPLASH_IMAGE_PATH = "./src/application/assets/splash.png";
	final String SEGA_SOUND_PATH = "./src/application/assets/sega.wav";

	public SplashGameScene() {
		GameNode splashImage = new SpriteGameNode(Game.loadImage(SPLASH_IMAGE_PATH));
		splashImage.geometry.setFrame(0, 0, Game.canvasWidth(), Game.canvasHeight());
		rootNode.addChild(splashImage);

		MediaPlayer sega = new MediaPlayer(Game.loadMedia(SEGA_SOUND_PATH));

		AnimationPlayer aniPlayer = new AnimationPlayer(SPLASH_DISPLAY_INTERVAL);
		rootNode.addChild(aniPlayer);
		
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
			Game.swapScene(new BlankGameScene());
		});

		aniPlayer.addAnimation("logo", logoFadeInAni);
		aniPlayer.addAnimation("sega", functionTriggerAni);
		aniPlayer.play(1);
	}
}
