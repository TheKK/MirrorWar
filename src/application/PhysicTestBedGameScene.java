package application;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.sound.sampled.LineUnavailableException;

import com.sun.media.jfxmedia.events.NewFrameEvent;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

class SayHelloNode extends RectangleGameNode {
	TextGameNode label = new TextGameNode("Hello");

	public SayHelloNode() {
		super(400, 50, 150, 150, Color.ANTIQUEWHITE);

		label.geometry.y = -30;
		addChild(label);
	}

	@Override
	public void onCollided(GameNode node, long elapse) {
	}
	
	@Override
	public void onAreaEntered(GameNode node, long elapse) {
		label.text = "Ouuuuuuch!!!!!";
	}

	@Override
	public void onAreaExited(GameNode node, long elapse) {
		label.text = "Please touch me again...";
	}
}

public final class PhysicTestBedGameScene extends GameScene {
	public PhysicTestBedGameScene() {
		physicEngine.worldGravityX = 0;
		physicEngine.worldGravityY = 0.003;

		RectangleGameNode button = new RectangleGameNode(0, 0, 50, 50, Color.RED) {
			public boolean onMousePressed(MouseEvent event) {
				Game.popScene();
				return false;
			}
		};
		rootNode.addChild(button);

		RectangleGameNode player = new RectangleGameNode(200, 200, 50, 50, Color.GREEN) {
			boolean upIsPressed = false;
			boolean downIsPressed = false;
			boolean leftIsPressed = false;
			boolean rightIsPressed = false;
			boolean jumpIsPressed = false;
			
			boolean canJump = false;
		
			GameNode floorSensor = new RectangleGameNode(0, geometry.height, geometry.width, 1, Color.TRANSPARENT) {
				@Override
				public void onAreaEntered(GameNode node, long elapse) {
					canJump = true;
				}
			};

			{
				addChild(floorSensor);
				physicEngine.addAreaNode(floorSensor);
			}
			
			@Override
			public void update(long elapse) {
				if (upIsPressed) pulseY -= 0.001;
				if (downIsPressed) pulseY += 0.001;
				if (leftIsPressed) pulseX -= 0.001;
				if (rightIsPressed) pulseX += 0.001;
				
				if (jumpIsPressed && canJump) {
					pulseY -= (1.0 / elapse);
					canJump = false;
				}

				jumpIsPressed = false;
			}

			@Override
			public boolean onKeyPressed(KeyEvent event) {
				switch (event.getCode()) {
				case UP:
					upIsPressed = true;
					break;
				case DOWN:
					downIsPressed = true;
					break;
				case LEFT:
					leftIsPressed = true;
					break;
				case RIGHT:
					rightIsPressed = true;
					break;
					
				case SPACE:
					color = Color.GREEN;
					jumpIsPressed = true;
					break;

				default:
					break;
				}
				return false;
			}

			@Override
			public boolean onKeyReleased(KeyEvent event) {
				switch (event.getCode()) {
				case UP:
					upIsPressed = false;
					break;
				case DOWN:
					downIsPressed = false;
					break;
				case LEFT:
					leftIsPressed = false;
					break;
				case RIGHT:
					rightIsPressed = false;
					break;

				default:
					break;
				}
				return false;
			}
		};
		player.dampX = 0.05;
		player.dampY = 0.05;

		rootNode.addChild(player);
		physicEngine.addDynamicNode(player);
		
		GameNode sayHelloRectangle = new SayHelloNode();
		rootNode.addChild(sayHelloRectangle);
		physicEngine.addAreaNode(sayHelloRectangle);
		
		GameSceneCamera camera = new SimpleGameSceneCamera(0, 0, Game.width, Game.height) {
			@Override
			public void update(long elapse) {
				if (player.geometry.getCenterX() > geometry.getCenterX() + 100) {
					geometry.x += player.geometry.getCenterX() - (geometry.getCenterX() + 100);
				}
				if (player.geometry.getCenterX() < geometry.getCenterX() - 100) {
					geometry.x -= (geometry.getCenterX() - 100) - player.geometry.getCenterX();
				}
				if (player.geometry.getCenterY() > geometry.getCenterY() + 80) {
					geometry.y += player.geometry.getCenterY() - (geometry.getCenterY() + 80);
				}
				if (player.geometry.getCenterY() < geometry.getCenterY() - 80) {
					geometry.y -= (geometry.getCenterY() - 80) - player.geometry.getCenterY();
				}
			}
		};
		setCamera(camera);
		
		GameNode floor = new RectangleGameNode(
				200, Game.canvasHeight() / 2,
				Game.canvasWidth() - 400, 50, Color.web("0x00ffff"));
		rootNode.addChild(floor);
		physicEngine.addStaticNode(floor);
	}
}
