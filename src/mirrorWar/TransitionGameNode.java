package mirrorWar;

import gameEngine.Game;
import gameEngine.GameNode;
import gameEngine.SpriteGameNode;

public class TransitionGameNode extends GameNode {
	public static enum Type {
		IN, OUT,
	}

	private static final double SPEED = 25;
	private final SpriteGameNode background = new SpriteGameNode(Game.loadImage("./src/mirrorWar/pic/mirrorSceneTransfer.png"));

	private Type type;
	private Runnable runLater;

	public TransitionGameNode(Type type, Runnable runLater) {
		this.type = type;
		this.runLater = runLater;

		enable = false;

		addChild(background);

		switch (type) {
		case OUT:
			background.geometry.x = Game.canvasWidth() * -3;
			break;

		case IN:
			background.geometry.x = Game.canvasWidth() * -1;
			break;
		}
	}

	@Override
	public void update(long elapse) {
		background.geometry.x += SPEED;

		double targetX = 0;

		switch (type) {
		case OUT:
			targetX = -1 * Game.canvasWidth();
			break;

		case IN:
			targetX = 1 * Game.canvasWidth();
			break;
		}

		if (background.geometry.x > targetX) {
			background.geometry.x = targetX;

			runLater.run();
			detachFromParent();
		}
	}
}
