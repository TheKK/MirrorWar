package application;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.Optional;

public class SimpleGameSceneCamera implements GameSceneCamera {
	public Optional<GameNode> cameraTarget = Optional.empty();
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
		if (!cameraTarget.isPresent()) {
			return;
		}
		
		GameNode target = cameraTarget.get();

		if (target.geometry.getCenterX() > geometry.getCenterX() + 100) {
			geometry.x += target.geometry.getCenterX() - (geometry.getCenterX() + 100);
		}
		if (target.geometry.getCenterX() < geometry.getCenterX() - 100) {
			geometry.x -= (geometry.getCenterX() - 100) - target.geometry.getCenterX();
		}
		if (target.geometry.getCenterY() > geometry.getCenterY() + 80) {
			geometry.y += target.geometry.getCenterY() - (geometry.getCenterY() + 80);
		}
		if (target.geometry.getCenterY() < geometry.getCenterY() - 80) {
			geometry.y -= (geometry.getCenterY() - 80) - target.geometry.getCenterY();
		}
	}
}