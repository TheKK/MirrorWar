package mirrorWar;

import gameEngine.Game;
import gameEngine.GameScene;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import mirrorWar.TransitionGameNode.Type;

public class CreditScene extends GameScene {
	@Override
	protected void initialize() {
		Game.clearColor = Color.GRAY;
		enable = false;

		rootNode.addChild(new TransitionGameNode(Type.IN, () -> { enable = true; }));
	}

	@Override
	protected boolean onKeyPressed(KeyEvent event) {
		Game.popScene();
		return false;
	}
}