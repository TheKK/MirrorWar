package netGameNodeSDK;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Set;

import com.sun.org.apache.bcel.internal.generic.NEW;

import gameEngine.Game;
import gameEngine.GameNode;
import gameEngine.GameScene;
import gameEngine.PhysicEngine;
import gameEngine.RectangleGameNode;
import javafx.scene.paint.Color;
import netGameNodeSDK.laser.Laser.LaserState;
import netGameNodeSDK.laser.Laser.LaserState.Direction;
import netGameNodeSDK.mirror.Mirror.MirrorState;

public class LaserEmiterNetGameNode extends NetGameNode<LaserState, Void> {
	private RectangleGameNode clientLaserImage;
	private LaserState.Direction currentDir = LaserState.Direction.Right;
	private int id;
	ArrayList<Rectangle2D.Double> laserPath = new ArrayList<>();
	ArrayList<GameNode> laserNodes = new ArrayList<GameNode>();

	public LaserEmiterNetGameNode(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	protected void clientInitialize(GameScene scene) {
		clientLaserImage = new RectangleGameNode(0, 0, 50, 50, Color.ORANGE);
		addChild(clientLaserImage);

	}

	@Override
	protected void serverInitialize(GameScene scene, boolean debugMode) {

	}

	@Override
	protected void clientUpdate(long elapse) {

	}

	@Override
	protected void serverUpdate(long elapse) {
		clearLaser();
		caculateLaser();
		addLaserToPhysicEng();
	}

	@Override
	protected void clientHandleServerUpdate(LaserState update) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void serverHandleClientInput(Void input) {
		// TODO Auto-generated method stub

	}

	@Override
	public LaserState getStates() {

		return LaserState.newBuilder().setId(id).setX(geometry.x).setY(geometry.y).setDirection(currentDir).build();
	}

	private void addLaserToPhysicEng() {
		for (Rectangle2D.Double laser : laserPath) {
			laserNodes.add(
					new RectangleGameNode(laser.x, laser.y, laser.getWidth(), laser.getHeight(), Color.TRANSPARENT));
		}
		PhysicEngine physicEngine = Game.currentScene().physicEngine;
		for (GameNode gameNode : laserNodes) {
			physicEngine.addAreaNode(gameNode);
		}
	}

	private void clearLaser() {
		PhysicEngine physicEngine = Game.currentScene().physicEngine;
		for (GameNode gameNode : laserNodes) {
			physicEngine.removeAreaNode(gameNode);
		}
		laserNodes.clear();
		laserPath.clear();
	}

	private void caculateLaser() {
		// Calculate laser
		Rectangle2D.Double laserRectangle = null;
		PhysicEngine physicEngine = Game.currentScene().physicEngine;
		clearLaser();

		Set<GameNode> in = null;
		LaserState.Direction tmpDir = currentDir;
		Rectangle2D.Double tmpLaserHead = new Rectangle2D.Double();
		tmpLaserHead.setRect(geometry.x, geometry.y, geometry.getWidth(), geometry.getHeight());
		while (true) {
			switch (tmpDir.getNumber()) {
			case LaserState.Direction.Down_VALUE:
				laserRectangle = new Rectangle2D.Double();
				laserRectangle.setRect(tmpLaserHead.x, tmpLaserHead.y + geometry.height, 50, 1000);
				in = physicEngine.getStaticNodesInArea(laserRectangle);
				if (in.isEmpty()) {
					laserPath.add(laserRectangle);
					return;
				} else {
					GameNode nearestNode = null;
					double min = Double.MAX_VALUE;
					for (GameNode gameNode : in) {
						if (gameNode.geometry.y < min) {
							min = gameNode.geometry.y;
							nearestNode = gameNode;
						}
					}

					if (nearestNode instanceof MirrorNetGameNode) {
						MirrorNetGameNode mirrorNetGameNode = (MirrorNetGameNode) nearestNode;
						laserRectangle.setRect(tmpLaserHead.x, tmpLaserHead.y + geometry.height, 50,
								nearestNode.geometry.y - tmpLaserHead.y + tmpLaserHead.height);
						laserPath.add(laserRectangle);

						// prepare for next
						if (mirrorNetGameNode.getDirection() == MirrorState.Direction.SLASH) {
							tmpDir = LaserState.Direction.Left;
						} else {// MirrorState.Direction.BACK_SLACK
							tmpDir = LaserState.Direction.Right;
						}
						tmpLaserHead = new Rectangle2D.Double();
						tmpLaserHead.setRect(nearestNode.geometry.x, nearestNode.geometry.y, geometry.width,
								geometry.height);
					} else {// hit wall
						laserRectangle.setRect(tmpLaserHead.x, tmpLaserHead.y + geometry.height, 50,
								nearestNode.geometry.y - tmpLaserHead.y - tmpLaserHead.height);
						laserPath.add(laserRectangle);
						return;
					}
				}

				break;
			case LaserState.Direction.Left_VALUE:
				laserRectangle = new Rectangle2D.Double();
				laserRectangle.setRect(tmpLaserHead.x - 1000, tmpLaserHead.y, 1000, 50);
				in = physicEngine.getStaticNodesInArea(laserRectangle);
				if (in.isEmpty()) {
					laserPath.add(laserRectangle);
					return;
				} else {
					GameNode nearestNode = null;
					double max = Double.MIN_VALUE;
					for (GameNode gameNode : in) {
						if (gameNode.geometry.x > max) {
							max = gameNode.geometry.x;
							nearestNode = gameNode;
						}
					}

					if (nearestNode instanceof MirrorNetGameNode) {
						MirrorNetGameNode mirrorNetGameNode = (MirrorNetGameNode) nearestNode;
						laserRectangle.setRect(nearestNode.geometry.x + nearestNode.geometry.width,
								nearestNode.geometry.y,
								tmpLaserHead.x - nearestNode.geometry.x - nearestNode.geometry.width, 50);
						laserPath.add(laserRectangle);

						// prepare for next
						if (mirrorNetGameNode.getDirection() == MirrorState.Direction.SLASH) {
							tmpDir = LaserState.Direction.Down;
						} else {// MirrorState.Direction.BACK_SLACK
							tmpDir = LaserState.Direction.Up;
						}
						tmpLaserHead = new Rectangle2D.Double();
						tmpLaserHead.setRect(nearestNode.geometry.x, nearestNode.geometry.y, geometry.width,
								geometry.height);
					} else {
						laserRectangle.setRect(nearestNode.geometry.x + nearestNode.geometry.width, tmpLaserHead.y,
								tmpLaserHead.x - nearestNode.geometry.x - nearestNode.geometry.width, 50);
						laserPath.add(laserRectangle);
						return;
					}
				}
				break;
			case LaserState.Direction.Right_VALUE:
				laserRectangle = new Rectangle2D.Double();
				laserRectangle.setRect(tmpLaserHead.x + geometry.width, tmpLaserHead.y, 50, 1000);
				in = physicEngine.getStaticNodesInArea(laserRectangle);
				if (in.isEmpty()) {
					laserPath.add(laserRectangle);
					return;
				} else {
					GameNode nearestNode = null;
					double min = Double.MAX_VALUE;
					for (GameNode gameNode : in) {
						if (gameNode.geometry.x < min) {
							min = gameNode.geometry.x;
							nearestNode = gameNode;
						}
					}
					if (nearestNode instanceof MirrorNetGameNode) {
						MirrorNetGameNode mirrorNetGameNode = (MirrorNetGameNode) nearestNode;
						laserRectangle.setRect(tmpLaserHead.x + tmpLaserHead.width, tmpLaserHead.y,
								nearestNode.geometry.x - tmpLaserHead.x - tmpLaserHead.width, 50);
						laserPath.add(laserRectangle);
						// prepare for next
						if (mirrorNetGameNode.getDirection() == MirrorState.Direction.SLASH) {
							tmpDir = LaserState.Direction.Up;
						} else {// MirrorState.Direction.BACK_SLACK
							tmpDir = LaserState.Direction.Down;
						}
						tmpLaserHead = new Rectangle2D.Double();
						tmpLaserHead.setRect(nearestNode.geometry.x, nearestNode.geometry.y, geometry.width,
								geometry.height);
					} else {
						laserRectangle.setRect(tmpLaserHead.x + tmpLaserHead.width, tmpLaserHead.y,
								nearestNode.geometry.x - tmpLaserHead.x - tmpLaserHead.width, 50);
						laserPath.add(laserRectangle);
						return;
					}
				}
				break;
			case LaserState.Direction.Up_VALUE:
				laserRectangle = new Rectangle2D.Double();
				laserRectangle.setRect(tmpLaserHead.x, tmpLaserHead.y - 1000, 50, 1000);
				in = physicEngine.getStaticNodesInArea(laserRectangle);
				if (in.isEmpty()) {
					laserPath.add(laserRectangle);
					return;
				} else {
					GameNode nearestNode = null;
					double max = Double.MIN_VALUE;
					for (GameNode gameNode : in) {
						if (gameNode.geometry.y > max) {
							max = gameNode.geometry.y;
							nearestNode = gameNode;
						}
					}
					if (nearestNode instanceof MirrorNetGameNode) {
						MirrorNetGameNode mirrorNetGameNode = (MirrorNetGameNode) nearestNode;
						laserRectangle.setRect(tmpLaserHead.x, tmpLaserHead.y - nearestNode.geometry.y, geometry.width,
								geometry.height);
						laserPath.add(laserRectangle);
						// prepare for next
						if (mirrorNetGameNode.getDirection() == MirrorState.Direction.SLASH) {
							tmpDir = LaserState.Direction.Right;
						} else {// MirrorState.Direction.BACK_SLACK
							tmpDir = LaserState.Direction.Left;
						}
						tmpLaserHead = new Rectangle2D.Double();
						tmpLaserHead.setRect(nearestNode.geometry.x, nearestNode.geometry.y, geometry.width,
								geometry.height);
					} else {
						laserRectangle.setRect(tmpLaserHead.x, tmpLaserHead.y - nearestNode.geometry.y, geometry.width,
								geometry.height);
						laserPath.add(laserRectangle);
						return;
					}
				}
				break;
			default:
				return;
			}
		}
	}

}
