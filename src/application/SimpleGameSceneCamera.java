package application;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;

public class SimpleGameSceneCamera implements GameSceneCamera {
	public Rectangle2D.Double geometry = new Rectangle2D.Double();

	public SimpleGameSceneCamera(double x, double y, double w, double h) {
		geometry.setRect(x, y, w, h);
	}

	@Override
	public Rectangle2D.Double geometry() {
		return geometry;
	}

	@Override
	public void update(long elapse) {
	}
}