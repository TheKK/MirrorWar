package gameEngine;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.Optional;

import javax.sound.sampled.LineUnavailableException;

import com.sun.media.jfxmedia.events.NewFrameEvent;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
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
		color= Color.PURPLE;
	}

	@Override
	public void onAreaExited(GameNode node, long elapse) {
		label.text = "Please touch me again...";
		color= Color.MAGENTA;
	}
}

public final class PhysicsTestBedGameScene extends GameScene {
	public PhysicsTestBedGameScene() {
		LayerGameNode rootLayer = new LayerGameNode();
		rootNode.addChild(rootLayer);

		physicEngine.worldGravityX = 0;
		physicEngine.worldGravityY = 0.003;

		ParticleGameNode fairyParticle;
		{
			File file = new File("./src/application/assets/particle.png");
			Image image = new Image(file.toURI().toString());
			SpriteGameNode sprite = new SpriteGameNode(image);

			fairyParticle = new ParticleGameNode(sprite, 1000);
		}

		RectangleGameNode button = new RectangleGameNode(0, 0, 50, 50, Color.RED) {
			public boolean onMousePressed(MouseEvent event) {
				Game.popScene();
				return false;
			}
		};
		rootLayer.addChild(button);

		RectangleGameNode player = new RectangleGameNode(290, 100, 50, 50, Color.GREEN) {
			boolean jumpIsPressed = false;
			
			{
				RotateTextGameNode cooool = new RotateTextGameNode("X", 0.0, 45.0, 0.0045) {
					@Override
					public void update(long elapse) {
						super.update(elapse);
						
						GameNode parent = parent().get();
						Point2D.Double pos = new Point2D.Double(parent.geometry.x, parent.geometry.y);
						fairyParticle.emit(pos.x + geometry.x + offsetX, pos.y + geometry.y + offsetY);
					}
				};

				cooool.geometry.x = geometry.width / 2;
				cooool.geometry.y = geometry.height / 2;
				addChild(cooool);
			}
		
			@Override
			public void update(long elapse) {
				final double MOVE_SPEED = 0.005;

				if (Game.getKeyboardState(KeyCode.UP)) pulseY -= MOVE_SPEED;
				if (Game.getKeyboardState(KeyCode.DOWN)) pulseY += MOVE_SPEED;
				if (Game.getKeyboardState(KeyCode.LEFT)) pulseX -= MOVE_SPEED;
				if (Game.getKeyboardState(KeyCode.RIGHT)) pulseX += MOVE_SPEED;
				
				if (jumpIsPressed) {
					jumpIsPressed = false;
					pulseX = -Math.signum(physicEngine.worldGravityX) / elapse;
					pulseY = -Math.signum(physicEngine.worldGravityY) / elapse;
				}
			}

			@Override
			public boolean onKeyPressed(KeyEvent event) {
				switch (event.getCode()) {
				case R:
					double vx = physicEngine.worldGravityX;
					double vy = physicEngine.worldGravityY;
					if (vx > 0.0) {
						physicEngine.worldGravityY = Math.abs(vx);
						physicEngine.worldGravityX = 0;
					} else if (vx < 0.0) {
						physicEngine.worldGravityY = -1 * Math.abs(vx);
						physicEngine.worldGravityX = 0;
					} else if (vx == 0.0) {
						if (vy > 0.0) {
							physicEngine.worldGravityX = -1 * Math.abs(vy);
							physicEngine.worldGravityY = 0;
						} else if (vy < 0.0) {
							physicEngine.worldGravityX = Math.abs(vy);
							physicEngine.worldGravityY = 0;
						}
					}
					break;
					
				case SPACE:
					jumpIsPressed = true;
					break;

				default:
					break;
				}
				return false;
			}
		};
		player.dampX = 0.90;
		player.dampY = 0.98;

		physicEngine.addDynamicNode(player);
		
		SimpleGameSceneCamera camera = new SimpleGameSceneCamera(0, 0, Game.width, Game.height);
		camera.cameraTarget = Optional.of(player);
		rootLayer.camera = camera;
		
		GameNode floor = new RectangleGameNode(
				200, Game.canvasHeight() / 2,
				Game.canvasWidth() - 400, 50, Color.web("0x00ffff"));
		rootLayer.addChild(floor);
		physicEngine.addStaticNode(floor);

		GameNode celling = new RectangleGameNode(
				200, 0,
				Game.canvasWidth() * 3, 50, Color.web("0x00ffff"));
		rootLayer.addChild(celling);
		physicEngine.addStaticNode(celling);

		GameNode leftWall = new RectangleGameNode(
				200, 100,
				50, Game.canvasHeight() / 2, Color.web("0x00ffff"));
		rootLayer.addChild(leftWall);
		physicEngine.addStaticNode(leftWall);

		GameNode rightWall = new RectangleGameNode(
				Game.canvasWidth() - 150, 100,
				50, Game.canvasHeight() / 2, Color.web("0x00ffff"));
		rootLayer.addChild(rightWall);
		physicEngine.addStaticNode(rightWall);
		
		GameNode sayHelloRectangle = new SayHelloNode();
		rootLayer.addChild(sayHelloRectangle);
		physicEngine.addAreaNode(sayHelloRectangle);
		
		File file = new File("./src/application/assets/sprite.png");
		Image image = new Image(file.toURI().toString());
		AnimatedSpriteGameNode animatedSprite = new AnimatedSpriteGameNode(image, 16, 16);
		animatedSprite.addFrame(new Rectangle2D.Double(0, 0, 16, 16), 500);
		animatedSprite.addFrame(new Rectangle2D.Double(16, 0, 16, 16), 500);
		animatedSprite.geometry.x = 300;
		animatedSprite.geometry.y = 10;
		rootLayer.addChild(animatedSprite);

		rootLayer.addChild(player);
		rootLayer.addChild(fairyParticle);
	}
}
