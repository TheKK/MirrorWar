package mirrorWar;

import gameEngine.Game;
import gameEngine.GameNode;
import gameEngine.RectangleGameNode;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class MenuBackground extends GameNode {
	private RectangleGameNode skyGameNode = new RectangleGameNode(
			0, 0, Game.canvasWidth(), Game.canvasHeight(),
			Color.web("0x81d4fa"));

	private Image cloudImage = Game.loadImage("./src/mirrorWar/pic/mirrorMenuCloud.png");
	private Image groundImage = Game.loadImage("./src/mirrorWar/pic/mirrorMenuGround.png");

	private double cloudOffsetX = 0;
	private double groundOffsetX = 0;

	private int cloudCol, groundCol;

	private final double cloudMoveSpeed = 0.01;
	private final double groundMoveSpeed = 0.005;

	public MenuBackground() {
		cloudOffsetX = Math.random() * cloudImage.getWidth();
		groundOffsetX = Math.random() * groundImage.getWidth();

		cloudCol = (int) (Game.canvasWidth() / cloudImage.getWidth()) + 1;
		groundCol = (int) (Game.canvasWidth() / groundImage.getWidth()) + 1;
	}

	@Override
	public void update(long elapse) {
		cloudOffsetX = (cloudOffsetX + cloudMoveSpeed * elapse) % cloudImage.getWidth();
		groundOffsetX = (groundOffsetX + groundMoveSpeed * elapse) % groundImage.getWidth();
	}

	@Override
	public void render(GraphicsContext gc) {
		skyGameNode.render(gc);

		for (int i = -1; i < cloudCol; ++i) {
			double x = cloudOffsetX + i * cloudImage.getWidth();
			double y = 0;

			gc.drawImage(cloudImage, x, y);
		}

		for (int i = -1; i < groundCol; ++i) {
			double x = groundOffsetX + i * groundImage.getWidth();
			double y = Game.canvasHeight() - groundImage.getHeight();

			gc.drawImage(groundImage, x, y);
		}
	}
}
