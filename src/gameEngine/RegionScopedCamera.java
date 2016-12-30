package gameEngine;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.OptionalDouble;

public class RegionScopedCamera extends GameSceneCamera {
	private final double SCALE_SPEED = 0.0002;

	public ArrayList<GameNode> targets = new ArrayList<>();

	private double top, left, bottom, right;
	private Rectangle2D.Double mapSize;

	public RegionScopedCamera(double top, double right, double bottom, double left, Rectangle2D.Double mapSize) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;

		this.mapSize = mapSize;;
	}

	@Override
	public void update(long elapse) {
		OptionalDouble leftGameNodeX = targets.stream()
				.mapToDouble(node -> node.geometry.getMinX())
				.min();

		OptionalDouble rightGameNodeX = targets.stream()
				.mapToDouble(node -> node.geometry.getMaxX())
				.max();

		OptionalDouble topGameNodeY = targets.stream()
				.mapToDouble(node -> node.geometry.getMinY())
				.min();

		OptionalDouble bottomGameNodeY = targets.stream()
				.mapToDouble(node -> node.geometry.getMaxY())
				.max();

		double leftEdge = leftGameNodeX.orElse(mapSize.x) - left;
		double rightEdge = rightGameNodeX.orElse(mapSize.getMaxX()) + right;
		double topEdge = topGameNodeY.orElse(mapSize.y) - top;
		double bottomEdge = bottomGameNodeY.orElse(mapSize.getMaxY()) + bottom;

		geometry.x = Math.max(leftEdge, mapSize.getMinX());
		geometry.y = Math.max(topEdge, mapSize.getMinY());

		// TODO This doesn't do anything... why?
//		anchorX = (rightEdge - leftEdge) / 2;
//		anchorY = (bottomEdge - topEdge) / 2;

		double targetAreaWidth = rightEdge - leftEdge;
		double targetAreaHeight = bottomEdge - topEdge;

		double cameraLength = Math.pow(geometry.width, 2) + Math.pow(geometry.height, 2);
		double targetAreaLength = Math.pow(targetAreaWidth, 2) + Math.pow(targetAreaHeight, 2);

		double targetZoom = Math.sqrt(cameraLength / targetAreaLength);
		if ((geometry.x + geometry.width / targetZoom) > mapSize.getMaxX()) {
			double maxWidth = mapSize.getMaxX() - geometry.x;
			targetZoom = maxWidth / mapSize.width;
		}

		System.out.println(targetZoom);

		if (zoom > targetZoom) {
			zoom = Math.max(targetZoom, zoom - SCALE_SPEED * elapse);
		} else {
			zoom = Math.min(targetZoom, zoom + SCALE_SPEED * elapse);
		}
	}

	public void addTarget(GameNode newTarget) {
		targets.add(newTarget);
	}

	private double minmax(double min, double max, double val) {
		return Math.min(Math.max(min, val), max);
	}
}
