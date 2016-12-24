package gameEngine;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class TextGameNode extends GameNode {
	public Color strokeColor = Color.BLACK;
	public Color fillColor = Color.BLACK;
	public String text = "";
	public TextAlignment align = TextAlignment.LEFT;
	public Font font = Font.font(12);

	public TextGameNode(String text) {
		this.text = text;
	}

	@Override
	public void render(GraphicsContext gc) {
		gc.setTextAlign(align);
		gc.setFont(font);
		gc.setStroke(strokeColor);
		gc.setFill(fillColor);
		gc.fillText(text, 0, 0);
		gc.strokeText(text, 0, 0);
	}
}
