package application;

import java.io.File;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;

public class SplashGameScene extends GameScene {
	public SplashGameScene() {
		File file = new File("./src/application/assets/splash.png");
		Image image = new Image(file.toURI().toString());
		GameNode splashImage = new SpriteGameNode(image);
		splashImage.geometry.setFrame(0, 0, Game.canvasWidth(), Game.canvasHeight());
		rootNode.addChild(splashImage);

		GameNode timer = new GameNode() {
			int life = 1000;
			int currentLife = 0;

			File file = new File("./src/application/assets/sega.wav");
			Media media = new Media(file.toURI().toString());
			MediaPlayer sega = new MediaPlayer(media);

			@Override
			public void update(long elapse) {
				currentLife += elapse;
				if (currentLife >= life) {
					Game.swapScene(new BlankGameScene());
				}
				
				if (currentLife >= 2000 && sega.getStatus() == Status.READY) {
					sega.play();
				}
				
				float x = (float) currentLife / 1000.f;
				float d = (float) life / 1000.f;
				float v = 2.5f;
				splashImage.alpha = (2.0 * x * v/ d) * ((-1 * x / d) + 1);
			}
		};
		rootNode.addChild(timer);
	}
}
