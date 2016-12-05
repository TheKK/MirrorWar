package mirrorWar;

import gameEngine.Game;
import gameEngine.GameScene;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

public class CreditScene extends GameScene {
	public CreditScene() {
		Game.clearColor = Color.GRAY;
	}

	@Override
	protected boolean onKeyPressed(KeyEvent event) {
		Game.popScene();
		return false;
	}
}
