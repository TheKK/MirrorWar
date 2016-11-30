package mirrorWar;

import gameEngine.Game;
import gameEngine.GameScene;
import gameEngine.RectangleGameNode;
import gameEngine.SpriteGameNode;
import javafx.application.Platform;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class MenuScreen extends GameScene {
	public MenuScreen() {
		Game.clearColor = Color.BLUEVIOLET;
		
		RectangleGameNode button = new RectangleGameNode(50, 50, 100, 20, Color.AQUA) {
			@Override
			public boolean onMouseReleased(MouseEvent event) {
				Platform.exit();
				return true;
			}
		};
		rootNode.addChild(button);
		
		SpriteGameNode shadow = new SpriteGameNode(Game.loadImage("./src/application/assets/animatedCoin.png"));
		rootNode.addChild(shadow);
	}
}
