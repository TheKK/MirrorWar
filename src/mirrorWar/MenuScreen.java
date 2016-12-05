package mirrorWar;

import gameEngine.Game;
import gameEngine.GameScene;
import gameEngine.SpriteGameNode;
import javafx.application.Platform;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class MenuScreen extends GameScene {
	public MenuScreen() {
		Game.clearColor = Color.BLUEVIOLET;
		
		SpriteGameNode shadow = new SpriteGameNode(Game.loadImage("./src/mirrorWar/pic/Quit.png")){
			@Override
			public boolean onMouseReleased(MouseEvent event) {
				Platform.exit();
				return true;
			}
		};
		shadow.mouseBound = shadow.geometry;
		rootNode.addChild(shadow);
	}
}
