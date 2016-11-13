package application;

import java.awt.geom.Rectangle2D;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class ImageGameNode extends GameNode {
	Image image;
	Rectangle2D.Double srcRect;

	public ImageGameNode(Image image) {
		this.image = image;
		this.srcRect = new Rectangle2D.Double(0, 0, image.getWidth(), image.getHeight());
	}

	@Override
	public void render(GraphicsContext gc) {
		gc.drawImage(
				image,
				srcRect.x, srcRect.y, srcRect.width, srcRect.height,
				geometry.x, geometry.y, geometry.width, geometry.height);
	}
}
