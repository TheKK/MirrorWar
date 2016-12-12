package gameEngine;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public final class PhysicEngine {
	public double worldGravityX = 0, worldGravityY = 0;

	private ArrayList<GameNode> dynamicNodes = new ArrayList<GameNode>();
	private ArrayList<GameNode> staticNodes = new ArrayList<GameNode>();
	private ArrayList<GameNode> areaNodes = new ArrayList<GameNode>();

	public final void addDynamicNode(GameNode node) {
		dynamicNodes.add(node);
	}

	public final void removeDynamicNode(GameNode node) {
		dynamicNodes.remove(node);
	}

	// TODO Make dynamic object an separated interface
	public final void addStaticNode(GameNode node) {
		staticNodes.add(node);
	}

	public final void removeStaticNode(GameNode node) {
		staticNodes.remove(node);
	}

	public final void addAreaNode(GameNode node) {
		areaNodes.add(node);
	}

	public final void removeAreaNode(GameNode node) {
		areaNodes.remove(node);
	}

	public final void updateVelocity(long elapse) {
		dynamicNodes.forEach(node -> {
			node.ax = (node.fx / node.mass + worldGravityX);
			node.ay = (node.fy / node.mass + worldGravityY);

			node.ax += node.pulseX / node.mass;
			node.ay += node.pulseY / node.mass;

			node.pulseX = 0;
			node.pulseY = 0;

			node.vx = (node.vx * node.dampX) + (node.ax * elapse);
			node.vy = (node.vy * node.dampY) + (node.ay * elapse);
		});
	}
	public final void updatePosition(long elapse) {
		dynamicNodes.forEach(node -> {
			node.geometry.x += node.vx * elapse;
			node.geometry.y += node.vy * elapse;
		});
	}

	public final void handleCollisions(long elapse) {
		final Point2D.Double dynamicNodeTranslate = new Point2D.Double();
		final Point2D.Double staticNodeTranslate = new Point2D.Double();
		final Point2D.Double sensorNodeTranslate = new Point2D.Double();

		// Handle static and dynamic node
		dynamicNodes.forEach(dynamicNode -> {
			dynamicNodeTranslate.setLocation(dynamicNode.getTranslationInScreen());
			dynamicNode.geometry.x += dynamicNodeTranslate.x;
			dynamicNode.geometry.y += dynamicNodeTranslate.y;

			staticNodes.forEach(staticNode -> {
				staticNodeTranslate.setLocation(staticNode.getTranslationInScreen());
				staticNode.geometry.x += staticNodeTranslate.x;
				staticNode.geometry.y += staticNodeTranslate.y;
				{
					handleDynamicAndStaticCollision(dynamicNode, staticNode, elapse);
				}
				staticNode.geometry.x -= staticNodeTranslate.x;
				staticNode.geometry.y -= staticNodeTranslate.y;
			});

			dynamicNode.geometry.x -= dynamicNodeTranslate.x;
			dynamicNode.geometry.y -= dynamicNodeTranslate.y;
		});

		// FIXME This would drive me crazy... Make things clear and clean.
		// Handle dynamic and dynamic node
		for (int i = 0; i < dynamicNodes.size(); ++i) {
			GameNode nodeA = dynamicNodes.get(i);
			dynamicNodeTranslate.setLocation(nodeA.getTranslationInScreen());
			nodeA.geometry.x += dynamicNodeTranslate.x;
			nodeA.geometry.y += dynamicNodeTranslate.y;
			{
				for (int j = i + 1; j < dynamicNodes.size(); ++j) {
					GameNode nodeB = dynamicNodes.get(j);
					staticNodeTranslate.setLocation(nodeB.getTranslationInScreen());
					nodeB.geometry.x += staticNodeTranslate.x;
					nodeB.geometry.y += staticNodeTranslate.y;
					{
						handleDynamicAndDynamicCollision(nodeA, nodeB, elapse);
					}
					nodeB.geometry.x -= staticNodeTranslate.x;
					nodeB.geometry.y -= staticNodeTranslate.y;
				}
			}
			nodeA.geometry.x -= dynamicNodeTranslate.x;
			nodeA.geometry.y -= dynamicNodeTranslate.y;
		}

		// Handle sensor node
		areaNodes.forEach(sensorNode -> {
			sensorNodeTranslate.setLocation(sensorNode.getTranslationInScreen());
			sensorNode.geometry.x += sensorNodeTranslate.x;
			sensorNode.geometry.y += sensorNodeTranslate.y;
			{
				staticNodes.forEach(staticNode -> {
					staticNodeTranslate.setLocation(staticNode.getTranslationInScreen());
					staticNode.geometry.x += staticNodeTranslate.x;
					staticNode.geometry.y += staticNodeTranslate.y;
					{
						doCollisionCheck(sensorNode, staticNode, elapse);
					}
					staticNode.geometry.x -= staticNodeTranslate.x;
					staticNode.geometry.y -= staticNodeTranslate.y;
				});

				dynamicNodes.forEach(dynamicNode -> {
					dynamicNodeTranslate.setLocation(dynamicNode.getTranslationInScreen());
					dynamicNode.geometry.x += dynamicNodeTranslate.x;
					dynamicNode.geometry.y += dynamicNodeTranslate.y;
					{
						doCollisionCheck(sensorNode, dynamicNode, elapse);
					}
					dynamicNode.geometry.x -= dynamicNodeTranslate.x;
					dynamicNode.geometry.y -= dynamicNodeTranslate.y;
				});
			}
			sensorNode.geometry.x -= sensorNodeTranslate.x;
			sensorNode.geometry.y -= sensorNodeTranslate.y;
		});
	}

	public final void renderDebug(GraphicsContext gc) {
		gc.setFill(Color.web("0x80008044"));
		dynamicNodes.forEach(node -> {
			Rectangle2D.Double r = node.geometry;
			Point2D.Double translate = node.getTranslationInScreen();

			gc.fillRect(r.x + translate.x, r.y + translate.y, r.width, r.height);
		});

		gc.setFill(Color.web("0xdaa52044"));
		staticNodes.forEach(node -> {
			Rectangle2D.Double r = node.geometry;
			Point2D.Double translate = node.getTranslationInScreen();

			gc.fillRect(r.x + translate.x, r.y + translate.y, r.width, r.height);
		});

		gc.setFill(Color.web("0xdd22dd44"));
		areaNodes.forEach(node -> {
			Rectangle2D.Double r = node.geometry;
			Point2D.Double translate = node.getTranslationInScreen();

			gc.fillRect(r.x + translate.x, r.y + translate.y, r.width, r.height);
		});
	}

	private void doCollisionCheck(GameNode nodeA, GameNode nodeB, long elapse) {
		if (nodeA.geometry.intersects(nodeB.geometry)) {
			nodeA.onCollided(nodeB, elapse);
			nodeB.onCollided(nodeA, elapse);

			nodeA._onAreaEntered(nodeB, elapse);
			nodeB._onAreaEntered(nodeA, elapse);
		} else {
			nodeA._onAreaExited(nodeB, elapse);
			nodeB._onAreaExited(nodeA, elapse);
		}
	}

	private void handleDynamicAndStaticCollision(GameNode dynamicNode, GameNode staticNode, long elapse) {
		Runnable handleXAxis = () -> {
			Rectangle2D intersection = dynamicNode.geometry.createIntersection(staticNode.geometry);
			if (intersection.isEmpty()) {
				return;
			}

			Point2D.Double forceVec = new Point2D.Double(dynamicNode.geometry.getCenterX() - intersection.getCenterX(),
					dynamicNode.geometry.getCenterY() - intersection.getCenterY());

			if (forceVec.x > 0) {
				dynamicNode.geometry.x += intersection.getWidth();
			} else {
				dynamicNode.geometry.x -= intersection.getWidth();
			}

			dynamicNode.vx = 0;
		};
		Runnable handleYAxis = () -> {
			Rectangle2D intersection = dynamicNode.geometry.createIntersection(staticNode.geometry);
			if (intersection.isEmpty()) {
				return;
			}

			Point2D.Double forceVec = new Point2D.Double(dynamicNode.geometry.getCenterX() - intersection.getCenterX(),
					dynamicNode.geometry.getCenterY() - intersection.getCenterY());

			if (forceVec.y > 0) {
				dynamicNode.geometry.y += intersection.getHeight();
			} else {
				dynamicNode.geometry.y -= intersection.getHeight();
			}

			dynamicNode.vy = 0;
		};

		Rectangle2D intersection = staticNode.geometry.createIntersection(dynamicNode.geometry);
		if (intersection.isEmpty()) {
			return;
		}

		Point2D.Double forceVec = new Point2D.Double(dynamicNode.geometry.getCenterX() - intersection.getCenterX(),
				dynamicNode.geometry.getCenterY() - intersection.getCenterY());

		if (Math.abs(forceVec.y) > Math.abs(forceVec.x)) {
			handleYAxis.run();
			handleXAxis.run();
		} else {
			handleXAxis.run();
			handleYAxis.run();
		}

		// Calling user defined or default collision handler.
		staticNode.onCollided(dynamicNode, elapse);
		dynamicNode.onCollided(staticNode, elapse);
	}

	private void handleDynamicAndDynamicCollision(GameNode nodeA, GameNode nodeB, long elapse) {
		Rectangle2D intersection = nodeA.geometry.createIntersection(nodeB.geometry);
		if (intersection.isEmpty()) {
			return;
		}

		// TODO Handle collision properly.

		// Calling user defined or default collision handler.
		nodeA.onCollided(nodeB, elapse);
		nodeB.onCollided(nodeA, elapse);
	}
}
