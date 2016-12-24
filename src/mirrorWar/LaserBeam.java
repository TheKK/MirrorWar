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
		public Rectangle2D.Double beamBody;
		public Direction direction;

		public LaserBeamInfo(Rectangle2D.Double beamBody, Direction direction) {
			this.beamBody = beamBody;
			this.direction = direction;
		}
	}

	private GameNode laserHeadNode, laserBodyNode, laserTailNode;
	private List<LaserBeamInfo> laserBeams;

	public LaserBeam(GameNode laserStartNode, GameNode laserBodyNode, GameNode laserTailNode) {
		laserStartNode.anchorX = laserBodyNode.anchorX = laserTailNode.anchorX = 0.5;
		laserStartNode.anchorY = laserBodyNode.anchorY = laserTailNode.anchorY = 0.5;

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
			switch (beam.direction) {
			case LEFT: {
				laserHeadNode.rotate = laserBodyNode.rotate = laserTailNode.rotate = 0;

				Rectangle2D.Double beamBody = beam.beamBody;
				double laserWidth = beamBody.height;

				this.setLaserTileGeometry(
						beamBody.getMaxX() - laserWidth, beamBody.y,
						laserWidth, laserWidth);

				int colNum = (int) (beamBody.width / laserWidth);
				double leftPadding = beamBody.width - (laserWidth * colNum);

				double curX = beamBody.getMaxX() - laserWidth * 2;

				// Head
				laserHeadNode.geometry.x = curX;
				laserHeadNode._render(gc);

				curX -= laserWidth;

				// Body
				for (int i = 0; i < colNum - 2; ++i) {
					laserBodyNode.geometry.x = curX;
					laserBodyNode._render(gc);

					curX -= laserWidth;
				}

				// Tail
				laserTailNode.geometry.x = curX + laserWidth - leftPadding;
				laserTailNode._render(gc);
			}
				break;

			case RIGHT: {
				laserHeadNode.rotate = laserBodyNode.rotate = laserTailNode.rotate = 180;

				Rectangle2D.Double beamBody = beam.beamBody;
				double laserWidth = beamBody.height;

				this.setLaserTileGeometry(
						beamBody.x, beamBody.y,
						laserWidth, laserWidth);

				int colNum = (int) (beamBody.width / laserWidth);
				double leftPadding = beamBody.width - (laserWidth * colNum);

				double curX = beamBody.getMinX();

				// Head
				laserHeadNode.geometry.x = curX;
				laserHeadNode._render(gc);

				curX += laserWidth;

				// Body
				for (int i = 0; i < colNum - 2; ++i) {
					laserBodyNode.geometry.x = curX;
					laserBodyNode._render(gc);

					curX += laserWidth;
				}

				// Tail
				laserTailNode.geometry.x = curX - laserWidth + leftPadding;
				laserTailNode._render(gc);
			}
				break;

			case DOWN: {
				laserHeadNode.rotate = laserBodyNode.rotate = laserTailNode.rotate = -90;

				Rectangle2D.Double beamBody = beam.beamBody;
				double laserHeight = beamBody.width;

				this.setLaserTileGeometry(
						beamBody.x, beamBody.y,
						laserHeight, laserHeight);

				int rowNum = (int) (beamBody.height / laserHeight);
				double leftPadding = beamBody.height - (laserHeight * rowNum);

				double curY = beamBody.y;

				// Head
				laserHeadNode.geometry.y = curY;
				laserHeadNode._render(gc);

				curY += laserHeight;

				// Body
				for (int i = 0; i < rowNum - 2; ++i) {
					laserBodyNode.geometry.y = curY;
					laserBodyNode._render(gc);

					curY += laserHeight;
				}

				// Tail
				laserTailNode.geometry.y = curY - laserHeight + leftPadding;
				laserTailNode._render(gc);
			}
				break;

			case UP: {
				laserHeadNode.rotate = laserBodyNode.rotate = laserTailNode.rotate = 90;

				Rectangle2D.Double beamBody = beam.beamBody;
				double laserHeight = beamBody.width;

				this.setLaserTileGeometry(
						beamBody.x, beamBody.getMaxY() - laserHeight,
						laserHeight, laserHeight);

				int rowNum = (int) (beamBody.height / laserHeight);
				double leftPadding = beamBody.height - (laserHeight * rowNum);

				double curY = beamBody.getMaxY() - laserHeight * 2;

				// Head
				laserHeadNode.geometry.y = curY;
				laserHeadNode._render(gc);

				curY -= laserHeight;

				// Body
				for (int i = 0; i < rowNum - 2; ++i) {
					laserBodyNode.geometry.y = curY;
					laserBodyNode._render(gc);

					curY -= laserHeight;
				}

				// Tail
				laserTailNode.geometry.y = curY + laserHeight - leftPadding;
				laserTailNode._render(gc);
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
