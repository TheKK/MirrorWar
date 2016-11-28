package gameEngine;

import java.awt.geom.Rectangle2D;

public class GameSceneCamera {
	public Rectangle2D.Double geometry = new Rectangle2D.Double();
	public double offsetX = 0, offsetY = 0;

	public void update(long elapse) {}

	final Rectangle2D.Double geometryWithOffset() { 
		Rectangle2D.Double result = new Rectangle2D.Double();
		result.setFrame(geometry);
		result.x += offsetX;
		result.y += offsetY;
		return result;
	}
}