package gameEngine;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class RectangleGameNode extends GameNode {
	Color color;

	public RectangleGameNode(double x, double y, double w, double h, Color color) {
		geometry.x = x;
		geometry.y = y;
		geometry.width = w;
		geometry.height = h;
		
		mouseBound = geometry;
		
		this.color = color;
	}
	
	public void render(GraphicsContext gc) {
		gc.setFill(color);
		gc.fillRect(geometry.x, geometry.y, geometry.width, geometry.height);
	}
}
