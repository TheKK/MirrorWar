package application;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.util.Duration;

class RotateTextGameNode extends TextGameNode {
	double degree;
	double radiusX, radiusY;
	double speed;
	double x, y;
	
	public RotateTextGameNode(String text, double startDegree, double radius, double speed) {
		super(text);

		this.degree = startDegree;
		this.radiusX = radius;
		this.radiusY = radius;
		this.speed = speed;
		
		this.x = 0;
		this.y = 0;
		
		this.mouseBound.width = 50;
		this.mouseBound.height = 50;
	}

	public void update(long elpase) {
		degree += elpase * speed;
		geometry.x = x + (int) (Math.cos(degree) * radiusX);
		geometry.y = y + (int) (Math.sin(degree) * radiusY);
		mouseBound.x = geometry.x;
		mouseBound.y = geometry.y;
	}
	
	public boolean onMousePressed(MouseEvent event) {
		System.out.println("hit");
		return true;
	}
}
	
public class GameNodeTestScene extends GameScene {
	TextGameNode textA;
	
	public GameNodeTestScene() {
		RectangleGameNode button = new RectangleGameNode(0, 0, 100, 100, Color.RED) {
			File file = new File("./src/application/assets/sega.wav");
			Media media = new Media(file.toURI().toString());

			public boolean onMousePressed(MouseEvent event) {
				System.out.println("red:" + event.getButton());
				MediaPlayer sega = new MediaPlayer(media);
				sega.play();
				return false;
			}
		};
		rootNode.addChild(button);
		
		RectangleGameNode button2 = new RectangleGameNode(50, 50, 100, 100, Color.BLUE) {
			public boolean onMousePressed(MouseEvent event) {
				System.out.println("blue:" + event.getButton());
				Game.popScene();
				return false;
			}
			public boolean onMouseEntered(MouseEvent event) {
				color = Color.BISQUE;
				return true;
			}
			public boolean onMouseExited(MouseEvent event) {
				color = Color.CHOCOLATE;
				return true;
			}
		};
		rootNode.addChild(button2);

		textA = new TextGameNode("sun") {
			public boolean onMouseMoved(MouseEvent event) {
				geometry.x = (int) event.getX();
				geometry.y = (int) event.getY();
				return true;
			}
		};
		rootNode.addChild(textA);
		
		ThreadLocalRandom rng = ThreadLocalRandom.current();
		for (int i = 0; i < 5; ++i) {
			double startDegree = rng.nextDouble(0, Math.PI * 2);
			double radius = rng.nextDouble(100, 300);
			double speed = rng.nextDouble(-0.002,  0.002);
			String text = "O";
			GameNode planet = new RotateTextGameNode(text, startDegree, radius, speed);
			
			textA.addChild(planet);

			if (rng.nextBoolean()) {
				double s_startDegree = rng.nextDouble(0, Math.PI * 2);
				double s_radius = rng.nextDouble(20, 70);
				double s_speed = rng.nextDouble(-0.006, 0.007);
				String s_text = "e";
				GameNode satellite = new RotateTextGameNode(s_text, s_startDegree, s_radius, s_speed);
				
				planet.addChild(satellite);
			}
		}
	}
}