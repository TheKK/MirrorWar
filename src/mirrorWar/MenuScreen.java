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
		
		SpriteGameNode join = new SpriteGameNode(Game.loadImage("./src/mirrorWar/pic/join.png")){
			@Override
			public boolean onMouseReleased(MouseEvent event) {
				GameScene login = new JoinGameScene();
				Game.swapScene(login);
				return true;
			}
		};
		join.geometry.x = 300;
		join.geometry.y = 250;
		join.geometry.height = 50;
		rootNode.addChild(join);
		
		SpriteGameNode credit = new SpriteGameNode(Game.loadImage("./src/mirrorWar/pic/credit.png")){
			@Override
			public boolean onMouseReleased(MouseEvent event) {
				Platform.exit();
				return true;
			}
		};
		credit.geometry.x = 300;
		credit.geometry.y = 300;
		credit.geometry.height = 50;
		rootNode.addChild(credit);
		
		SpriteGameNode quit = new SpriteGameNode(Game.loadImage("./src/mirrorWar/pic/quit.png")){
			@Override
			public boolean onMouseReleased(MouseEvent event) {
				Platform.exit();
				return true;
			}
		};
		quit.geometry.x = 300;
		quit.geometry.y = 350;
		quit.geometry.height = 50;
		rootNode.addChild(quit);
	}
}
