package demo;

import java.awt.geom.Point2D;

import gameEngine.Game;
import gameEngine.GameScene;
import gameEngine.GameSceneCamera;
import gameEngine.LayerGameNode;
import gameEngine.RectangleGameNode;
import gameEngine.SimpleGameSceneCamera;
import gameEngine.TextGameNode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class BlankGameScene extends GameScene {
	final String DEFAULT_HINT_STRING = "Press the rectangle to go to another scene";

	public BlankGameScene() {
		LayerGameNode rootLayer = new LayerGameNode();
		rootNode.addChild(rootLayer);

		TextGameNode hintText = new TextGameNode(DEFAULT_HINT_STRING) {
			{
				strokeColor = Color.BLACK;
			}

			public void update(long elapse) {
				Point2D.Double mousePos = Game.getMousePos();

				geometry.x = mousePos.x;
				geometry.y = mousePos.y;
			}
		};

		RectangleGameNode buttonA = new RectangleGameNode(
				75, 75, 100, 100, Color.web("0xaabbcc")) {

			public boolean onMousePressed(MouseEvent event) {
				Game.pushScene(new YetAnohterblankScene());
				return false;
			}
			public boolean onMouseEntered(MouseEvent event) {
				color = Color.RED;
				hintText.text = "Boring box bouncing around";
				return false;
			}
			public boolean onMouseExited(MouseEvent event) {
				color = Color.web("0xaabbcc");
				hintText.text = DEFAULT_HINT_STRING;
				return false;
			}
		};
		rootLayer.addChild(buttonA);

		RectangleGameNode buttonB = new RectangleGameNode(
				75, 75 + 2 * 100, 100, 100, Color.web("0xaabbcc")) {

			public boolean onMousePressed(MouseEvent event) {
				Game.pushScene(new GameNodeTestScene());
				return false;
			}
			public boolean onMouseEntered(MouseEvent event) {
				color = Color.RED;
				hintText.text = "Solar system (GameNode system)";
				return false;
			}
			public boolean onMouseExited(MouseEvent event) {
				color = Color.web("0xaabbcc");
				hintText.text = DEFAULT_HINT_STRING;
				return false;
			}
		};
		rootLayer.addChild(buttonB);

		RectangleGameNode buttonC = new RectangleGameNode(
				75 + 1 * 150, 75 + 0 * 100, 100, 100, Color.web("0xaabbcc")) {

			public boolean onMousePressed(MouseEvent event) {
				Game.pushScene(new PhysicsTestBedGameScene());
				return false;
			}
			public boolean onMouseEntered(MouseEvent event) {
				color = Color.RED;
				hintText.text = "Physics test bed";
				return false;
			}
			public boolean onMouseExited(MouseEvent event) {
				color = Color.web("0xaabbcc");
				hintText.text = DEFAULT_HINT_STRING;
				return false;
			}
		};
		rootLayer.addChild(buttonC);

		RectangleGameNode buttonD = new RectangleGameNode(
				75 + 2 * 150, 75 + 1 * 100, 100, 100, Color.web("0xaabbcc")) {

			public boolean onMousePressed(MouseEvent event) {
				Game.pushScene(new PongGameScene());
				return false;
			}
			public boolean onMouseEntered(MouseEvent event) {
				color = Color.RED;
				hintText.text = "The famous PONG!";
				return false;
			}
			public boolean onMouseExited(MouseEvent event) {
				color = Color.web("0xaabbcc");
				hintText.text = DEFAULT_HINT_STRING;
				return false;
			}
		};
		rootLayer.addChild(buttonD);

		RectangleGameNode buttonDebug = new RectangleGameNode(
				0, 0, 50, 50, Color.web("0xaabbcc")) {

			public boolean onMousePressed(MouseEvent event) {
				Game.isClickBoundDebugMode = !Game.isClickBoundDebugMode;
				Game.isPhysicEngineDebugMode = !Game.isPhysicEngineDebugMode;
				return false;
			}
			public boolean onMouseEntered(MouseEvent event) {
				color = Color.RED;
				hintText.text = "Enable/disable debug render";
				return false;
			}
			public boolean onMouseExited(MouseEvent event) {
				color = Color.web("0xaabbcc");
				hintText.text = DEFAULT_HINT_STRING;
				return false;
			}
		};
		rootLayer.addChild(buttonDebug);
		
		TextGameNode textA = new TextGameNode("Stupid running boxes");
		textA.strokeColor = Color.SLATEBLUE;
		textA.geometry.x = buttonA.geometry.x;
		textA.geometry.y = buttonA.geometry.y - 10;
		rootLayer.addChild(textA);

		TextGameNode textB = new TextGameNode("Stupid running boxes");
		textB.strokeColor = Color.SLATEBLUE;
		textB.geometry.x = buttonB.geometry.x;
		textB.geometry.y = buttonB.geometry.y - 10;
		rootLayer.addChild(textB);
		
		GameSceneCamera camera = new SimpleGameSceneCamera(0, 0, Game.canvasWidth(), Game.canvasHeight()) {
			@Override
			public void update(long elapse) {
				double MAGIC_OFFSET = -20;
				Point2D.Double mousePos = Game.getMousePos();
				double offsetX = MAGIC_OFFSET * (0.5 - (mousePos.x / Game.canvasWidth()));
				double offsetY = MAGIC_OFFSET * (0.5 - (mousePos.y / Game.canvasHeight()));
				
				this.geometry.x = offsetX;
				this.geometry.y = offsetY;
			}
		};
		rootLayer.camera = camera;
		rootLayer.addChild(hintText);
	}
}
