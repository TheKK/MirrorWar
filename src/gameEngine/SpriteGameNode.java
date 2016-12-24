package gameEngine;

import java.awt.geom.Rectangle2D;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class SpriteGameNode extends GameNode {
	Image image;
	Rectangle2D.Double srcRect;

	public SpriteGameNode(Image image) {
		this.image = image;

		srcRect = new Rectangle2D.Double(0, 0, image.getWidth(), image.getHeight());
		geometry.setFrame(srcRect);
		mouseBound = geometry;
	}

	@Override
	public void render(GraphicsContext gc) {
		gc.drawImage(
				image,
				srcRect.x, srcRect.y, srcRect.width, srcRect.height,
				0, 0, geometry.width, geometry.height);
	}
}
