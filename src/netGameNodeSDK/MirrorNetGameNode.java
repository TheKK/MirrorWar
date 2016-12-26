package netGameNodeSDK;

import java.io.FileNotFoundException;

import gameEngine.AnimatedSpriteGameNode;
import gameEngine.Game;
import gameEngine.GameScene;
import mirrorWar.mirror.Mirror.MirrorState;
import mirrorWar.mirror.Mirror.MirrorState.Direction;

public class MirrorNetGameNode extends NetGameNode<MirrorState, Void> {
	private int id;
	private MirrorState.Direction direction = MirrorState.Direction.SLASH;

	// Client stuff
	private AnimatedSpriteGameNode clientSlashMirrorSprite;
	private AnimatedSpriteGameNode clientBackSlashMirrorSprite;

	// Server stuff
	private boolean serverIsPicked = false;

	public MirrorNetGameNode(int id) {
		this.id = id;
	}

	public void spin() {
		direction = (direction == Direction.SLASH) ? Direction.BACK_SLACK : Direction.SLASH;
	}

	public void picked() {
		Game.currentScene().physicEngine.removeStaticNode(this);
		visible = false;

		serverIsPicked = true;

		geometry.x = 99999;
		geometry.y = 99999;
	}

	public void drop(double x, double y) {
		Game.currentScene().physicEngine.addStaticNode(this);
		visible = true;

		serverIsPicked = false;

		geometry.x = x;
		geometry.y = y;
	}
	public MirrorState.Direction getDirection() {
		return direction;
	}
	@Override
	public void clientInitialize(GameScene scene) {
		try {
			clientSlashMirrorSprite = new AnimatedSpriteGameNode(
					Game.loadImage("./src/mirrorWar/pic/mirrorSlash.png"),
					"./src/mirrorWar/pic/mirrorSlash.json");
			clientBackSlashMirrorSprite = new AnimatedSpriteGameNode(
					Game.loadImage("./src/mirrorWar/pic/mirrorBackSlash.png"),
					"./src/mirrorWar/pic/mirrorBackSlash.json");

			clientSlashMirrorSprite.autoPlayed = false;
			clientSlashMirrorSprite.visible = false;

			clientBackSlashMirrorSprite.autoPlayed = false;
			clientBackSlashMirrorSprite.visible = false;

			addChild(clientSlashMirrorSprite);
			addChild(clientBackSlashMirrorSprite);

		} catch (FileNotFoundException e) {
		}

		updateFunc = this::clientUpdate;
	}

	@Override
	public void serverInitialize(GameScene scene, boolean debugMode) {
		geometry.width = 50;
		geometry.height = 50;

		addColissionGroup(Main.MIRROR_COLLISION_ID);

		Game.currentScene().physicEngine.addStaticNode(this);

		updateFunc = this::serverUpdate;

//		if (debugMode) {
//			RectangleGameNode rect = new RectangleGameNode(0, 0, 50, 50, Color.BROWN);
//			addChild(rect);
//		}
	}

	@Override
	protected void clientUpdate(long elapse) {
		switch (direction) {
		case BACK_SLACK:
			clientSlashMirrorSprite.visible = false;
			clientSlashMirrorSprite.autoPlayed = false;

			clientBackSlashMirrorSprite.visible = true;
			clientBackSlashMirrorSprite.play(-1);
			break;

		case SLASH:
			clientBackSlashMirrorSprite.visible = false;
			clientBackSlashMirrorSprite.autoPlayed = false;

			clientSlashMirrorSprite.visible = true;
			clientSlashMirrorSprite.play(-1);
			break;
		}
	}

	@Override
	protected void serverUpdate(long elapse) {
	}

	@Override
	public void clientHandleServerUpdate(MirrorState update) {
		geometry.x = update.getX();
		geometry.y = update.getY();

		direction = update.getDirection();

		visible = !update.getPicked();
	}

	@Override
	protected void serverHandleClientInput(Void input) {
	}

	@Override
	public MirrorState getStates() {
		MirrorState mirrorState = MirrorState.newBuilder()
				.setId(id)
				.setX(geometry.x)
				.setY(geometry.y)
				.setPicked(serverIsPicked)
				.setDirection(direction)
				.build();

		return mirrorState;
	}
}
