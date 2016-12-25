package netGameNodeSDK;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;

import gameEngine.Game;
import gameEngine.GameNode;
import gameEngine.GameScene;
import gameEngine.PhysicEngine;
import gameEngine.RectangleGameNode;
import javafx.scene.paint.Color;
import mirrorWar.Constants;
import mirrorWar.LaserBeam;
import mirrorWar.LaserBeam.LaserBeamInfo;
import mirrorWar.laser.Laser;
import mirrorWar.laser.Laser.LaserState;
import mirrorWar.laser.Laser.LaserState.Rect;
import mirrorWar.mirror.Mirror.MirrorState;

public class LaserEmitterNetGameNode extends NetGameNode<LaserState, Void> {
	public LaserState.Direction currentDir = null;
	
	private GameNode clientLaserEmitterImage;
	
	private int id, ownerId, serverLaserGroupId;
	
	ArrayList<Rectangle2D.Double> laserPath = new ArrayList<>();
	ArrayList<LaserState.Direction> laserDir = new ArrayList<>();
	ArrayList<GameNode> laserNodes = new ArrayList<GameNode>();
	
	LaserBeam clientLaserBeam = null;

	public LaserEmitterNetGameNode(int id, int ownerId) {
		this.id = id;
		this.ownerId = ownerId;
	}

	public int getId() {
		return id;
	}

	@Override
	public void clientInitialize(GameScene scene) {
		clientLaserEmitterImage = new RectangleGameNode(0, 0, 50, 50, Color.BISQUE);
		
		Color laserColor = (ownerId == 0) ? Color.BLUE : Color.ORANGERED;
		clientLaserBeam = new LaserBeam(
				new RectangleGameNode(0, 0, 50, 50, laserColor),
				new RectangleGameNode(0, 0, 50, 50, laserColor),
				new RectangleGameNode(0, 0, 50, 50, laserColor));
		
		addChild(clientLaserEmitterImage);
		addChild(clientLaserBeam);
	}

	@Override
	public void serverInitialize(GameScene scene, boolean debugMode) {
		serverLaserGroupId = (ownerId == 0) ? Constants.PLAYER0_LASER_COLLISION_GROUP : Constants.PLAYER1_LASER_COLLISION_GROUP;
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
		geometry.x = update.getX();
		geometry.y = update.getY();

		// draw laser with laser Beam
		ArrayList<LaserBeamInfo> laserBeamInfos = new ArrayList<>();
		
		for (Rect re : update.getRectsList()) {
			LaserState.Direction from = re.getDirec();
			LaserBeam.Direction to = null;
			
			switch (from.getNumber()) {
			case LaserState.Direction.Down_VALUE:
				to = LaserBeam.Direction.DOWN;
				break;
				
			case LaserState.Direction.Left_VALUE:
				to = LaserBeam.Direction.LEFT;
				break;
				
			case LaserState.Direction.Right_VALUE:
				to = LaserBeam.Direction.RIGHT;
				break;
				
			case LaserState.Direction.Up_VALUE:
				to = LaserBeam.Direction.UP;
				break;

			}

			Rectangle2D.Double beamBody = new Rectangle2D.Double(
					re.getX() - geometry.x, re.getY() - geometry.y,
					re.getWidth(), re.getHeight());
			laserBeamInfos.add(new LaserBeam.LaserBeamInfo(beamBody, to));
		}
		

		clientLaserBeam.setLaserBeamPositions(laserBeamInfos);
	}

	@Override
	protected void serverHandleClientInput(Void input) {
	}

	@Override
	public LaserState getStates() {
		ArrayList<Laser.LaserState.Rect> rects = new ArrayList<>();

		for (int i = 0; i < laserPath.size(); i++) {
			rects.add(Laser.LaserState.Rect.newBuilder()
					.setX(laserPath.get(i).x)
					.setY(laserPath.get(i).y)
					.setWidth(laserPath.get(i).width)
					.setHeight(laserPath.get(i).height)
					.setDirec(laserDir.get(i))
					.build());
		}

		return LaserState.newBuilder()
				.setId(id)
				.setOwnerId(ownerId)
				.setX(geometry.x)
				.setY(geometry.y)
				.addAllRects(rects)
				.setDir(currentDir)
				.build();
	}

	private void clearLaser() {
		PhysicEngine physicEngine = Game.currentScene().physicEngine;
		for (GameNode gameNode : laserNodes) {
			physicEngine.removeAreaNode(gameNode);
		}
		laserNodes.clear();
		laserPath.clear();
		laserDir.clear();
	}

	private void addLaserToPhysicEng() {
		for (Rectangle2D.Double laser : laserPath) {
			laserNodes.add(new RectangleGameNode(laser.x, laser.y, laser.getWidth(), laser.getHeight(), Color.CHARTREUSE));
		}
		PhysicEngine physicEngine = Game.currentScene().physicEngine;
		for (GameNode gameNode : laserNodes) {
			gameNode.addColissionGroup(serverLaserGroupId);
			Game.currentScene().physicEngine.addAreaNode(gameNode);
		}
	}

	private void caculateLaser() {
		Rectangle2D.Double laserlight = null;

		PhysicEngine physicEngine = Game.currentScene().physicEngine;
		Set<GameNode> intersects = null;
		LaserState.Direction tmpDir = currentDir;
		Rectangle2D.Double tmpEmiter = new Rectangle.Double(geometry.x, geometry.y, geometry.getWidth(),
				geometry.getHeight());
		final int maxReflectionTimes = 15;
		int cnt = 0;
		while (cnt < maxReflectionTimes) {
			cnt++;
			int dirNum = tmpDir.getNumber();
			if (dirNum == LaserState.Direction.Up_VALUE) {
				//System.out.println("Up");
				laserlight = new Rectangle.Double(tmpEmiter.x, tmpEmiter.y - 1000 - 1, 50, 1000);
				intersects = physicEngine.getStaticNodesInArea(laserlight);
				Optional<GameNode> nearestNodeOp = intersects.stream()
						.max(Comparator.comparing(node -> ((GameNode) node).geometry.y));
				
				if (!nearestNodeOp.isPresent()) { // nothing
					laserPath.add(laserlight);
					laserDir.add(LaserState.Direction.Up);
					break;
					
				} else {
					GameNode nearestNode = nearestNodeOp.get();
					
					tryCharging(nearestNode);
					
					// mirror
					if (nearestNode instanceof MirrorNetGameNode) {
						//System.out.println("Mirror!");
						MirrorNetGameNode mirrorNetGameNode = (MirrorNetGameNode) nearestNode;
						laserlight.setRect(tmpEmiter.x, nearestNode.geometry.y, tmpEmiter.width,
								clampToZero(tmpEmiter.y - nearestNode.geometry.y));
						laserPath.add(laserlight);
						laserDir.add(LaserState.Direction.Up);
						// prepare for next
						if (mirrorNetGameNode.getDirection() == MirrorState.Direction.SLASH) {
							//System.out.println("slash");
							tmpDir = LaserState.Direction.Right;
						} else if (mirrorNetGameNode.getDirection() == MirrorState.Direction.BACK_SLACK) {
							//System.out.println("back_slash");
							tmpDir = LaserState.Direction.Left;
						}
						tmpEmiter.setRect(nearestNode.geometry.x, nearestNode.geometry.y, nearestNode.geometry.width,
								nearestNode.geometry.height);
					} else {// wall
						//System.out.println("Wall");
						laserlight.setRect(
								tmpEmiter.x,
								nearestNode.geometry.y - Constants.LASER_BEAM_OFFSET,
								tmpEmiter.width,
								clampToZero(tmpEmiter.y - nearestNode.geometry.y) + Constants.LASER_BEAM_OFFSET);
						
						laserPath.add(laserlight);
						laserDir.add(LaserState.Direction.Up);
						break;
					}
				}
			} else if (dirNum == LaserState.Direction.Down_VALUE) {
				//System.out.println("Down");
				laserlight = new Rectangle.Double(tmpEmiter.x, tmpEmiter.y + tmpEmiter.height + 1, 50, 1000);
				intersects = physicEngine.getStaticNodesInArea(laserlight);
				Optional<GameNode> nearestNodeOp = intersects.stream()
						.min(Comparator.comparing(node -> ((GameNode) node).geometry.y));
				
				if (!nearestNodeOp.isPresent()) { // nothing
					laserPath.add(laserlight);
					laserDir.add(LaserState.Direction.Down);
					break;
					
				} else {
					GameNode nearestNode = nearestNodeOp.get();
					
					tryCharging(nearestNode);
					
					// mirror
					if (nearestNode instanceof MirrorNetGameNode) {
						//System.out.println("Mirror!");
						MirrorNetGameNode mirrorNetGameNode = (MirrorNetGameNode) nearestNode;
						laserlight.setRect(tmpEmiter.x, tmpEmiter.y + tmpEmiter.height, tmpEmiter.width,
								clampToZero(nearestNode.geometry.y - tmpEmiter.y));
						laserPath.add(laserlight);
						laserDir.add(LaserState.Direction.Down);
						// prepare for next
						if (mirrorNetGameNode.getDirection() == MirrorState.Direction.SLASH) {
							//System.out.println("slash");
							tmpDir = LaserState.Direction.Left;
						} else if (mirrorNetGameNode.getDirection() == MirrorState.Direction.BACK_SLACK) {
							//System.out.println("back_slash");
							tmpDir = LaserState.Direction.Right;
						}
						tmpEmiter.setRect(nearestNode.geometry.x, nearestNode.geometry.y, nearestNode.geometry.width,
								nearestNode.geometry.height);
					} else {// wall
						//System.out.println("Wall");
						laserlight.setRect(
								tmpEmiter.x,
								tmpEmiter.y + tmpEmiter.height,
								tmpEmiter.width,
								clampToZero(nearestNode.geometry.y - tmpEmiter.y) + Constants.LASER_BEAM_OFFSET);
						
						laserPath.add(laserlight);
						laserDir.add(LaserState.Direction.Down);
						break;
					}
				}
			} else if (dirNum == LaserState.Direction.Left_VALUE) {
				//System.out.println("Left");
				laserlight = new Rectangle.Double(tmpEmiter.x - 1000 - 1, tmpEmiter.y, 1000, 50);
				intersects = physicEngine.getStaticNodesInArea(laserlight);
				Optional<GameNode> nearestNodeOp = intersects.stream()
						.max(Comparator.comparing(node -> ((GameNode) node).geometry.x));
				
				if (!nearestNodeOp.isPresent()) { // nothing
					laserPath.add(laserlight);
					laserDir.add(LaserState.Direction.Left);
					break;
					
				} else {
					GameNode nearestNode = nearestNodeOp.get();
					
					tryCharging(nearestNode);
					
					// mirror
					if (nearestNode instanceof MirrorNetGameNode) {
						//System.out.println("Mirror!");
						MirrorNetGameNode mirrorNetGameNode = (MirrorNetGameNode) nearestNode;
						laserlight.setRect(nearestNode.geometry.x + nearestNode.geometry.width, tmpEmiter.y,
								clampToZero(tmpEmiter.x - nearestNode.geometry.x), tmpEmiter.height);
						laserPath.add(laserlight);
						laserDir.add(LaserState.Direction.Left);
						// prepare for next
						if (mirrorNetGameNode.getDirection() == MirrorState.Direction.SLASH) {
							//System.out.println("slash");
							tmpDir = LaserState.Direction.Down;
						} else if (mirrorNetGameNode.getDirection() == MirrorState.Direction.BACK_SLACK) {
							//System.out.println("back_slash");
							tmpDir = LaserState.Direction.Up;
						}
						tmpEmiter.setRect(nearestNode.geometry.x, nearestNode.geometry.y, nearestNode.geometry.width,
								nearestNode.geometry.height);
					} else {// wall
						//System.out.println("Wall");
						laserlight.setRect(
								nearestNode.geometry.x + nearestNode.geometry.width - Constants.LASER_BEAM_OFFSET,
								tmpEmiter.y,
								clampToZero(tmpEmiter.x - nearestNode.geometry.x) + Constants.LASER_BEAM_OFFSET,
								tmpEmiter.height);
						
						laserPath.add(laserlight);
						laserDir.add(LaserState.Direction.Left);
						break;
					}
				}
			} else if (dirNum == LaserState.Direction.Right_VALUE) {
				//System.out.println("Right");
				laserlight = new Rectangle.Double(tmpEmiter.x + tmpEmiter.width + 1, tmpEmiter.y, 1000, 50);
				intersects = physicEngine.getStaticNodesInArea(laserlight);
				Optional<GameNode> nearestNodeOp = intersects.stream()
						.min(Comparator.comparing(node -> ((GameNode) node).geometry.x));
				
				if (!nearestNodeOp.isPresent()) { // nothing
					laserPath.add(laserlight);
					laserDir.add(LaserState.Direction.Right);
					break;
					
				} else {
					GameNode nearestNode = nearestNodeOp.get();
					
					tryCharging(nearestNode);
					
					// mirror
					if (nearestNode instanceof MirrorNetGameNode) {
						//System.out.println("Mirror!");
						MirrorNetGameNode mirrorNetGameNode = (MirrorNetGameNode) nearestNode;
						laserlight.setRect(tmpEmiter.x + tmpEmiter.width, tmpEmiter.y,
								clampToZero(nearestNode.geometry.x - tmpEmiter.x), tmpEmiter.height);
						laserPath.add(laserlight);
						laserDir.add(LaserState.Direction.Right);
						// prepare for next
						if (mirrorNetGameNode.getDirection() == MirrorState.Direction.SLASH) {
							//System.out.println("slash");
							tmpDir = LaserState.Direction.Up;
						} else if (mirrorNetGameNode.getDirection() == MirrorState.Direction.BACK_SLACK) {
							//System.out.println("back_slash");
							tmpDir = LaserState.Direction.Down;
						}
						tmpEmiter.setRect(nearestNode.geometry.x, nearestNode.geometry.y, nearestNode.geometry.width,
								nearestNode.geometry.height);
					} else {// wall
						//System.out.println("Wall");
						laserlight.setRect(
								tmpEmiter.x + tmpEmiter.width,
								tmpEmiter.y,
								clampToZero(nearestNode.geometry.x - tmpEmiter.x) + Constants.LASER_BEAM_OFFSET,
								tmpEmiter.height);
						
						
						laserPath.add(laserlight);
						laserDir.add(LaserState.Direction.Right);
						break;
					}
				}
			}
		}
	}

	private double clampToZero(double in) {
		return Math.max(0, in);
	}
	
	private void tryCharging(GameNode node) {
		if (node instanceof ChargerNetGameNode) {
			if (ownerId == 0) {
				((ChargerNetGameNode) node).chargePlayer0();
			} else if (ownerId == 1) {
				((ChargerNetGameNode) node).chargePlayer1();
			}
		}
	}
}
