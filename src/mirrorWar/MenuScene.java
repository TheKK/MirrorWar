package mirrorWar;

import gameEngine.Game;
import gameEngine.GameScene;
import gameEngine.SpriteGameNode;
import javafx.application.Platform;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class MenuScene extends GameScene {
	public MenuScene() {
		Game.clearColor = Color.BLUEVIOLET;
		
		SpriteGameNode background = new SpriteGameNode(Game.loadImage("./src/mirrorWar/pic/miku.jpg"));
		rootNode.addChild(background);
		
		SpriteGameNode title = new SpriteGameNode(Game.loadImage("./src/mirrorWar/pic/title.png"));
		title.geometry.x = 250;
		title.geometry.y = 50;
		rootNode.addChild(title);
		
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
				GameScene cd = new CreditScene();
				Game.pushScene(cd);
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
