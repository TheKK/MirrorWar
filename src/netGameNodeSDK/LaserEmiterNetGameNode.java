package netGameNodeSDK;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.swing.internal.plaf.basic.resources.basic;

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
	private LaserState.Direction currentDir = LaserState.Direction.Up;
	private int id;
	ArrayList<Rectangle2D.Double> laserPath = new ArrayList<>();
	ArrayList<GameNode> laserNodes = new ArrayList<GameNode>();

	public LaserEmiterNetGameNode(int id) {
		this.id = id;
		geometry.width = 50;
		geometry.height = 50;
	}

	public int getId() {
		return id;
	}

	@Override
	public void clientInitialize(GameScene scene) {
		clientLaserImage = new RectangleGameNode(geometry.x, geometry.y, geometry.width, geometry.height, Color.ORANGE);
		addChild(clientLaserImage);
	}

	@Override
	public void serverInitialize(GameScene scene, boolean debugMode) {
		updateFunc = this::serverUpdate;
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

	private void clearLaser() {
		PhysicEngine physicEngine = Game.currentScene().physicEngine;
		for (GameNode gameNode : laserNodes) {
			physicEngine.removeAreaNode(gameNode);
		}
		laserNodes.clear();
		laserPath.clear();
	}

	private void addLaserToPhysicEng() {
		for (Rectangle2D.Double laser : laserPath) {
			laserNodes.add(
					new RectangleGameNode(laser.x, laser.y, laser.getWidth(), laser.getHeight(), Color.CHARTREUSE));
		}
		PhysicEngine physicEngine = Game.currentScene().physicEngine;
		for (GameNode gameNode : laserNodes) {
			physicEngine.addAreaNode(gameNode);
			System.out.println(gameNode + "" + gameNode.geometry);
		}
		System.out.println("~");

	}

	private void caculateLaser() {
		Rectangle2D.Double laserlight = null;

		PhysicEngine physicEngine = Game.currentScene().physicEngine;
		Set<GameNode> intersects = null;
		LaserState.Direction tmpDir = currentDir;
		Rectangle2D.Double tmpEmiter = new Rectangle.Double(geometry.x, geometry.y, geometry.getWidth(),
				geometry.getHeight());
		final int maxReflectionTimes =15;
		int cnt=0;
		while (cnt<maxReflectionTimes) {
			cnt++;
			System.out.println("tmpEmiter" + tmpEmiter);
			int dirNum = tmpDir.getNumber();
			if (dirNum == LaserState.Direction.Up_VALUE) {
				System.out.println("Up");
				laserlight = new Rectangle.Double(tmpEmiter.x, tmpEmiter.y - 1000, 50, 1000);
				intersects = physicEngine.getStaticNodesInArea(laserlight);

				if (intersects.isEmpty()) { // nothing
					laserPath.add(laserlight);
					break;
				} else {
					// find closest
					GameNode nearestNode = null;
					double yMax = Double.MIN_VALUE;
					for (GameNode gameNode : intersects) {
						if (gameNode.geometry.y > yMax) {
							yMax = gameNode.geometry.y;
							nearestNode = gameNode;
						}
					}
					// mirror
					if (nearestNode instanceof MirrorNetGameNode) {
						MirrorNetGameNode mirrorNetGameNode = (MirrorNetGameNode) nearestNode;
						laserlight.setRect(tmpEmiter.x, nearestNode.geometry.y, tmpEmiter.width,
								takePositive(tmpEmiter.y - nearestNode.geometry.y));
						laserPath.add(laserlight);
						// prepare for next
						if (mirrorNetGameNode.getDirection() == MirrorState.Direction.SLASH) {
							tmpDir = LaserState.Direction.Right;
						} else if (mirrorNetGameNode.getDirection() == MirrorState.Direction.BACK_SLACK) {
							tmpDir = LaserState.Direction.Left;
						}
						tmpEmiter.setRect(nearestNode.geometry.x, nearestNode.geometry.y, nearestNode.geometry.width,
								nearestNode.geometry.height);
					} else {// wall
						laserlight.setRect(tmpEmiter.x, nearestNode.geometry.y, tmpEmiter.width,
								takePositive(tmpEmiter.y - nearestNode.geometry.y));
						laserPath.add(laserlight);
						break;
					}
				}
			} else if (dirNum == LaserState.Direction.Down_VALUE) {
				System.out.println("Down");
				laserlight = new Rectangle.Double(tmpEmiter.x, tmpEmiter.y + tmpEmiter.height, 50, 1000);
				intersects = physicEngine.getStaticNodesInArea(laserlight);

				if (intersects.isEmpty()) { // nothing
					laserPath.add(laserlight);
					break;
				} else {
					// find closest
					GameNode nearestNode = null;
					double yMin = Double.MAX_VALUE;
					for (GameNode gameNode : intersects) {
						if (gameNode.geometry.y < yMin) {
							yMin = gameNode.geometry.y;
							nearestNode = gameNode;
						}
					}
					// mirror
					if (nearestNode instanceof MirrorNetGameNode) {
						MirrorNetGameNode mirrorNetGameNode = (MirrorNetGameNode) nearestNode;
						laserlight.setRect(tmpEmiter.x, tmpEmiter.y+tmpEmiter.height, tmpEmiter.width,
								takePositive(nearestNode.geometry.y - tmpEmiter.y));
						laserPath.add(laserlight);
						// prepare for next
						if (mirrorNetGameNode.getDirection() == MirrorState.Direction.SLASH) {
							tmpDir = LaserState.Direction.Left;
						} else if (mirrorNetGameNode.getDirection() == MirrorState.Direction.BACK_SLACK) {
							tmpDir = LaserState.Direction.Right;
						}
						tmpEmiter.setRect(nearestNode.geometry.x, nearestNode.geometry.y, nearestNode.geometry.width,
								nearestNode.geometry.height);
					} else {// wall
						laserlight.setRect(tmpEmiter.x, nearestNode.geometry.y, tmpEmiter.width,
								takePositive(nearestNode.geometry.y - tmpEmiter.y));
						laserPath.add(laserlight);
						break;
					}
				}
			} else if (dirNum == LaserState.Direction.Left_VALUE) {
				System.out.println("Left");
				laserlight = new Rectangle.Double(tmpEmiter.x-1000, tmpEmiter.y, 1000, 50);
				intersects = physicEngine.getStaticNodesInArea(laserlight);

				if (intersects.isEmpty()) { // nothing
					laserPath.add(laserlight);
					break;
				} else {
					// find closest
					GameNode nearestNode = null;
					double xMax = Double.MIN_VALUE;
					for (GameNode gameNode : intersects) {
						if (gameNode.geometry.x > xMax) {
							xMax = gameNode.geometry.x;
							nearestNode = gameNode;
						}
					}
					// mirror
					if (nearestNode instanceof MirrorNetGameNode) {
						MirrorNetGameNode mirrorNetGameNode = (MirrorNetGameNode) nearestNode;
						laserlight.setRect(nearestNode.geometry.x+nearestNode.geometry.width, tmpEmiter.y, takePositive(tmpEmiter.x-nearestNode.geometry.x),
								tmpEmiter.height);
						laserPath.add(laserlight);
						// prepare for next
						if (mirrorNetGameNode.getDirection() == MirrorState.Direction.SLASH) {
							tmpDir = LaserState.Direction.Down;
						} else if (mirrorNetGameNode.getDirection() == MirrorState.Direction.BACK_SLACK) {
							tmpDir = LaserState.Direction.Up;
						}
						tmpEmiter.setRect(nearestNode.geometry.x, nearestNode.geometry.y, nearestNode.geometry.width,
								nearestNode.geometry.height);
					} else {// wall
						laserlight.setRect(nearestNode.geometry.x+nearestNode.geometry.width, tmpEmiter.x, takePositive(tmpEmiter.x-nearestNode.geometry.x),
								tmpEmiter.height);
						laserPath.add(laserlight);
						break;
					}
				}
			} else if (dirNum == LaserState.Direction.Right_VALUE) {
				System.out.println("Right");
				laserlight = new Rectangle.Double(tmpEmiter.x+tmpEmiter.width, tmpEmiter.y, 1000, 50);
				intersects = physicEngine.getStaticNodesInArea(laserlight);

				if (intersects.isEmpty()) { // nothing
					laserPath.add(laserlight);
					break;
				} else {
					// find closest
					GameNode nearestNode = null;
					double xMin = Double.MAX_VALUE;
					for (GameNode gameNode : intersects) {
						if (gameNode.geometry.x < xMin) {
							xMin= gameNode.geometry.x;
							nearestNode = gameNode;
						}
					}
					// mirror
					if (nearestNode instanceof MirrorNetGameNode) {
						MirrorNetGameNode mirrorNetGameNode = (MirrorNetGameNode) nearestNode;
						laserlight.setRect(tmpEmiter.x+tmpEmiter.width, tmpEmiter.y, takePositive(nearestNode.geometry.x-tmpEmiter.x),
								tmpEmiter.height);
						laserPath.add(laserlight);
						// prepare for next
						if (mirrorNetGameNode.getDirection() == MirrorState.Direction.SLASH) {
							tmpDir = LaserState.Direction.Up;
						} else if (mirrorNetGameNode.getDirection() == MirrorState.Direction.BACK_SLACK) {
							tmpDir = LaserState.Direction.Down;
						}
						tmpEmiter.setRect(nearestNode.geometry.x, nearestNode.geometry.y, nearestNode.geometry.width,
								nearestNode.geometry.height);
					} else {// wall
						laserlight.setRect(tmpEmiter.x+tmpEmiter.width, tmpEmiter.y, takePositive(nearestNode.geometry.x-tmpEmiter.x),
								tmpEmiter.height);
						laserPath.add(laserlight);
						break;
					}
				}
			}
		}
	}

	private double takePositive(double in) {
		if (in > 0) {
			return in;
		} else {
			return 0;
		}
	}
}
