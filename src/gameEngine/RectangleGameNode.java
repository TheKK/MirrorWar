package gameEngine;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class RectangleGameNode extends GameNode {
	public Color color;

	public RectangleGameNode(double x, double y, double w, double h, Color color) {
		geometry.x = x;
		geometry.y = y;
		geometry.width = w;
		geometry.height = h;

		mouseBound = geometry;

		this.color = color;
	}

	@Override
	public void render(GraphicsContext gc) {
		gc.setFill(color);
		gc.fillRect(0, 0, geometry.width, geometry.height);
	}
}
