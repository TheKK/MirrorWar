package application;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class YetAnohterblankScene extends GameScene {
	ArrayList<SmallBox> smallBoxes = new ArrayList<SmallBox>();

	class SmallBox extends RectangleGameNode {
		SmallBox(double x, double y,
				double width, double height,
				double vx, double vy,
				Color color) {
			super(x, y, width, height, color);

			this.vx = vx;
			this.vy = vy;
		}
		
		public void update(long elapse) {
			geometry.x += vx * elapse;
			geometry.y += vy * elapse;

			if (geometry.x + geometry.width > Game.canvasWidth()) {
				geometry.x = (int) Game.canvasWidth() - geometry.width;
				vx *= -1;
			}
			if (geometry.x < 0) {
				geometry.x = 0;
				vx *= -1;
			}
			if (geometry.y + geometry.height > Game.canvasHeight()) {
				geometry.y = (int) Game.canvasHeight() - geometry.height;
				vy *= -1;
			}
			if (geometry.y < 0) {
				geometry.y = 0;
				vy *= -1;
			}
		}
	}
	
	public YetAnohterblankScene() {
		ThreadLocalRandom rng = ThreadLocalRandom.current();

		for (int i = 0; i < 100; ++i) {
			double width = rng.nextDouble(10, 60);
			double height = width;
			double vx = rng.nextDouble(-0.2, 0.2);
			double vy = rng.nextDouble(-0.2, 0.2);
			double x = rng.nextDouble(0, Game.canvasWidth() - width);
			double y = rng.nextDouble(0, Game.canvasHeight() - height);
			Color color = Color.rgb(
					rng.nextInt(0, 255),
					rng.nextInt(0, 255),
					rng.nextInt(0, 255),
					rng.nextDouble(0.3, 1.0));

			rootNode.addChild(new SmallBox(x, y, width, height, vx, vy, color));
		}
		
		GameNode returnButton = new RectangleGameNode(0, 0, 50, 50, Color.RED) {
			public boolean onMousePressed(MouseEvent event) {
				Game.popScene();
				return false;
			}
		};
		rootNode.addChild(returnButton);
	}
}
