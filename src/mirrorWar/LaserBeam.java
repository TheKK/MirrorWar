package mirrorWar;

import java.awt.geom.Rectangle2D;
import java.util.List;

import gameEngine.GameNode;
import javafx.scene.canvas.GraphicsContext;

public final class LaserBeam extends GameNode {
	static public enum Direction {
		UP, LEFT, RIGHT, DOWN,
	}

	static public class LaserBeamInfo {
		Rectangle2D.Double beamBody;
		Direction direction;

		public LaserBeamInfo(Rectangle2D.Double beamBody, Direction direction) {
			this.beamBody = beamBody;
			this.direction = direction;
		}
	}

	private GameNode laserAnimation;
	private List<LaserBeamInfo> laserBeams;

	public LaserBeam(GameNode laserAnimation) {
		this.laserAnimation = laserAnimation;
	}

	public void setLaserBeamPositions(List<LaserBeamInfo> laserBeams) {
		this.laserBeams = laserBeams;
	}

	@Override
	public void update(long elapse) {
		laserAnimation.update(elapse);
	}

	@Override
	public void render(GraphicsContext gc) {
		laserBeams.forEach(beam -> {
			// Use rotate to make this happens
			switch (beam.direction) {
			case LEFT: {
				laserAnimation.geometry.width = beam.beamBody.height;
				laserAnimation.geometry.height = beam.beamBody.height;
				laserAnimation.geometry.x = beam.beamBody.getMaxX() - laserAnimation.geometry.width;
				laserAnimation.geometry.y = beam.beamBody.y;

				double curX = laserAnimation.geometry.x;
				double targetX = beam.beamBody.getMinX();

				while (curX > targetX) {
					laserAnimation.render(gc);

					curX -= laserAnimation.geometry.width;
					laserAnimation.geometry.x = curX;
				}
			}
				break;

			case RIGHT: {
				laserAnimation.geometry.width = beam.beamBody.height;
				laserAnimation.geometry.height = beam.beamBody.height;
				laserAnimation.geometry.x = beam.beamBody.x;
				laserAnimation.geometry.y = beam.beamBody.y;

				double curX = laserAnimation.geometry.x;
				double targetX = beam.beamBody.getMaxX() - laserAnimation.geometry.width;


				while (curX < targetX) {
					laserAnimation.render(gc);

					curX += laserAnimation.geometry.width;
					laserAnimation.geometry.x = curX;
				}
			}
				break;

			case DOWN: {
				laserAnimation.geometry.width = beam.beamBody.width;
				laserAnimation.geometry.height = beam.beamBody.width;
				laserAnimation.geometry.x = beam.beamBody.x;
				laserAnimation.geometry.y = beam.beamBody.y;

				double curY = laserAnimation.geometry.y;
				double targetY = beam.beamBody.getMaxY() - laserAnimation.geometry.height;

				while (curY < targetY) {
					laserAnimation.render(gc);

					curY += laserAnimation.geometry.height;
					laserAnimation.geometry.y = curY;
				}
			}
				break;

			case UP: {
				laserAnimation.geometry.width = beam.beamBody.width;
				laserAnimation.geometry.height = beam.beamBody.width;
				laserAnimation.geometry.x = beam.beamBody.x;
				laserAnimation.geometry.y = beam.beamBody.getMaxY() - laserAnimation.geometry.height;

				double curY = laserAnimation.geometry.y;
				double targetY = beam.beamBody.getMinY();

				while (curY > targetY) {
					laserAnimation.render(gc);

					curY -= laserAnimation.geometry.height;
					laserAnimation.geometry.y = curY;
				}
			}
				break;
			}
		});
	}
}
