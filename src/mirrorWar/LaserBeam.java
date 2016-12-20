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

	private GameNode laserHeadNode, laserBodyNode, laserTailNode;
	private List<LaserBeamInfo> laserBeams;

	public LaserBeam(GameNode laserStartNode, GameNode laserBodyNode, GameNode laserTailNode) {
		this.laserHeadNode = laserStartNode;
		this.laserBodyNode = laserBodyNode;
		this.laserTailNode = laserTailNode;
	}

	public void setLaserBeamPositions(List<LaserBeamInfo> laserBeams) {
		this.laserBeams = laserBeams;
	}

	@Override
	public void update(long elapse) {
		laserHeadNode.update(elapse);
		laserBodyNode.update(elapse);
		laserTailNode.update(elapse);
	}

	@Override
	public void render(GraphicsContext gc) {
		laserBeams.forEach(beam -> {
			// Use rotate to make this happens
			switch (beam.direction) {
			case LEFT: {
				double laserWidth = beam.beamBody.height;

				this.setLaserTileGeometry(
						beam.beamBody.getMaxX() - beam.beamBody.height,
						beam.beamBody.y,
						laserWidth,
						laserWidth);

				double curX = laserHeadNode.geometry.x;
				double targetX = beam.beamBody.getMinX() + laserWidth * 2;

				// Head
				laserHeadNode.geometry.x = curX;
				laserHeadNode.render(gc);

				// Body
				while (curX > targetX) {
					curX -= laserWidth;

					laserBodyNode.geometry.x = curX;
					laserBodyNode.render(gc);
				}

				// Tail
				laserTailNode.geometry.x = curX;
				laserTailNode.render(gc);
			}
				break;

			case RIGHT: {
				double laserWidth = beam.beamBody.height;

				this.setLaserTileGeometry(
						beam.beamBody.x,
						beam.beamBody.y,
						laserWidth,
						laserWidth);

				double curX = laserHeadNode.geometry.x;
				double targetX = beam.beamBody.getMaxX() - laserWidth * 2;

				// Head
				laserHeadNode.geometry.x = curX;
				laserHeadNode.render(gc);

				// Body
				while (curX < targetX) {
					curX += laserWidth;

					laserBodyNode.geometry.x = curX;
					laserBodyNode.render(gc);
				}

				// Tail
				laserTailNode.geometry.x = curX;
				laserTailNode.render(gc);
			}
				break;

			case DOWN: {
				double laserHeight = beam.beamBody.width;

				this.setLaserTileGeometry(
						beam.beamBody.x,
						beam.beamBody.y,
						laserHeight,
						laserHeight);

				double curY = laserHeadNode.geometry.y;
				double targetY = beam.beamBody.getMaxY() - laserHeight * 2;

				// Head
				laserHeadNode.geometry.y = curY;
				laserHeadNode.render(gc);

				// Body
				while (curY < targetY) {
					curY += laserHeight;

					laserBodyNode.geometry.y = curY;
					laserBodyNode.render(gc);
				}

				// Tail
				laserTailNode.geometry.y = curY;
				laserTailNode.render(gc);
			}
				break;

			case UP: {
				double laserHeight = beam.beamBody.width;

				this.setLaserTileGeometry(
						beam.beamBody.x,
						beam.beamBody.getMaxY() - laserHeight,
						laserHeight,
						laserHeight);

				double curY = laserHeadNode.geometry.y;
				double targetY = beam.beamBody.getMinY() + laserHeight * 2;

				// Head
				laserHeadNode.geometry.y = curY;
				laserHeadNode.render(gc);

				// Body
				while (curY > targetY) {
					curY -= laserHeight;

					laserBodyNode.geometry.y = curY;
					laserBodyNode.render(gc);
				}

				// Tail
				laserTailNode.geometry.y = curY;
				laserTailNode.render(gc);
			}
				break;
			}
		});
	}

	private void setLaserTileGeometry(double x, double y, double width, double height) {
		laserHeadNode.geometry.x = laserBodyNode.geometry.x = laserTailNode.geometry.x = x;
		laserHeadNode.geometry.y = laserBodyNode.geometry.y = laserTailNode.geometry.y = y;
		laserHeadNode.geometry.width = laserBodyNode.geometry.width = laserTailNode.geometry.width = width;
		laserHeadNode.geometry.height = laserBodyNode.geometry.height = laserTailNode.geometry.height = height;
	}
}
