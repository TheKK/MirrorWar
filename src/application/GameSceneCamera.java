package application;

import java.awt.geom.Rectangle2D;

public interface GameSceneCamera {
	Rectangle2D.Double geometry();
	void update(long elapse);
}
