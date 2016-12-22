package mirrorWar;

import gameEngine.AnimationPlayer;
import gameEngine.FunctionTriggerAnimation;
import gameEngine.GameNode;
import gameEngine.RectangleGameNode;
import gameEngine.TextGameNode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class BoardGameNode extends GameNode {
	private RectangleGameNode background = new RectangleGameNode(0, 0, 600, 50, Color.WHITE);
	private TextGameNode text = new TextGameNode("");
	private AnimationPlayer fadeoutAnimation;

	public BoardGameNode(long duration) {
		fadeoutAnimation = new AnimationPlayer(duration);
		visible = false;
		
		background.geometry.x = (background.geometry.width / 2 )* -1;
		background.geometry.y = (background.geometry.height / 2 )* -1;
		
		addChild(background);
		addChild(text);
		text.geometry.y += 10;
		text.font = Font.font(30);
		text.align = TextAlignment.CENTER;
		text.strokeColor = Color.RED;
		text.fillColor = Color.RED;
		addChild(fadeoutAnimation);
		
		FunctionTriggerAnimation funcAni = new FunctionTriggerAnimation();
		funcAni.addAnchor(duration, () -> {
			visible = false;
		});
		fadeoutAnimation.addAnimation("rand", funcAni);
	}
	
	public void showMessage(String msg) {
		text.text = msg;
		visible = true;
		fadeoutAnimation.playFromStart(1);
	}
}
