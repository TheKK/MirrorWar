package mirrorWar;

import gameEngine.Game;
import gameEngine.GameNode;
import gameEngine.GameScene;
import gameEngine.RectangleGameNode;
import gameEngine.SpriteGameNode;
import gameEngine.TextGameNode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

public class JoinGameScene extends GameScene {
	private String content = "";
	
	public JoinGameScene() {
		Game.clearColor = Color.YELLOW;
		GameNode dialogBackgroud = new RectangleGameNode(300, 250, 200, 30, Color.WHITE);
		rootNode.addChild(dialogBackgroud);
		
		GameNode text = new TextGameNode(content) {
			@Override
			public boolean onKeyPressed(KeyEvent event) {
				switch (event.getCode()) {
					case BACK_SPACE:
						content = cutOffLastWord(content);
						break;
					case PERIOD:
					case DECIMAL:
						contentAppend(".");
						break;
					default:
						if (event.getCode().isDigitKey()) {
							contentAppend(event.getText());
						}
				}
				
				text = content;
				
				return true;
			}
		};
		text.geometry.y = 20;
		dialogBackgroud.addChild(text);
		
		GameNode okBtn = new SpriteGameNode(Game.loadImage("./src/mirrorWar/pic/button_ok.png"));
		dialogBackgroud.addChild(okBtn);
	}
	
	private String cutOffLastWord(String str) {
		String content = "";
		if (str.length() > 0) {
			content = str.substring(0, str.length()-1);
		}
		
		return content;
	}
	
	private void contentAppend(String str) {
		if (content.length() < 15) {
			content += str;
		}
	}
}
