package application;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class TextGameNode extends GameNode {
	public Color strokeColor = Color.BLACK;
	public String text = "";
	
	public TextGameNode(String text) {
		this.text = text;
	}

	@Override
	public void render(GraphicsContext gc) {
		gc.setStroke(strokeColor);
		gc.strokeText(text, geometry.getX(), geometry.getY());
	}
}
