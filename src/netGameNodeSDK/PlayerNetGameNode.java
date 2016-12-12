package netGameNodeSDK;

import java.util.function.Function;

import gameEngine.GameNode;
import gameEngine.GameScene;
import gameEngine.RectangleGameNode;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import netGameNodeSDK.input.InputOuterClass.Input;
import netGameNodeSDK.key.KeyOuterClass.Key;
import netGameNodeSDK.key.KeyOuterClass.KeyDown;
import netGameNodeSDK.key.KeyOuterClass.KeyType;
import netGameNodeSDK.key.KeyOuterClass.KeyUp;
import netGameNodeSDK.player.Player.PlayerState;
import netGameNodeSDK.player.Player.PlayerState.Animation;
import netGameNodeSDK.player.Player.PlayerState.Facing;

public final class PlayerNetGameNode extends NetGameNode<PlayerState, Input> {
	final double WALKING_SPEED = 0.12;

	private int id;

	private PlayerState.Color color = PlayerState.Color.ORANGE;

	private boolean upIsPressed = false;
	private boolean downIsPressed = false;
	private boolean leftIsPressed = false;
	private boolean rightIsPressed = false;

	private boolean spinMirrorIsPressed = false;

	private Animation currentAnimation = Animation.STANDING;
	private Facing currentFacing = Facing.RIGHT;

	// Client stuff
	private RectangleGameNode clientRect;

	// Server stuff
	private RectangleGameNode serverMirrorSpinSensor;

	public PlayerNetGameNode(int id) {
		this.id = id;
	}

	public int id() {
		return id;
	}

	@Override
	public PlayerState getStates() {
		PlayerState state = PlayerState.newBuilder()
				.setId(id)
				.setColor(color)
				.setX(geometry.x)
				.setY(geometry.y)
				.setAnimation(currentAnimation)
				.setFacing(currentFacing)
				.build();

		return state;
	}

	@Override
	protected void clientInitialize(GameScene scene) {
		geometry.x = 0;
		geometry.y = 0;

		dampX = 0.9999;
		dampY = 0.9999;

		clientRect = new RectangleGameNode(0, 0, 50, 50, Color.RED);

		updateFunc = elapse -> {
			updateQueue.forEach(this::clientHandleServerUpdate);
			updateQueue.clear();

			clientUpdate(elapse);
		};

		onKeyPressedFunc = this::clientOnKeyPressed;
		onKeyReleasedFunc = this::clientOnKeyReleased;

		addChild(clientRect);
	}

	@Override
	protected void serverInitialize(GameScene scene, boolean debugMode) {
		geometry.x = 0;
		geometry.y = 0;
		geometry.width = 50;
		geometry.height = 50;

		dampX = 0.9;
		dampY = 0.9;

		updateFunc = elapse -> {
			synchronized (inputQueue) {
				inputQueue.forEach(this::serverHandleClientInput);
				inputQueue.clear();
			}

			serverUpdate(elapse);
		};

		scene.physicEngine.addDynamicNode(this);

		serverMirrorSpinSensor = new RectangleGameNode(50, 10, 35, 20, Color.TRANSPARENT);
		addChild(serverMirrorSpinSensor);
		scene.physicEngine.addAreaNode(serverMirrorSpinSensor);

		// TODO I'm not 100% sure this would work perfectly for some cases,
		//      find some ways to solve this.
		if (debugMode) {
			GameNode rect = new RectangleGameNode(0, 0, 50, 50, Color.RED);
			addChild(rect);
		}
	}

	@Override
	protected void clientUpdate(long elapse) {
	}

	@Override
	protected void serverUpdate(long elapse) {
		// It seems like this "sometimes" happened
		if (elapse <= 0) {
			return;
		}

		if (upIsPressed) pulseY -= WALKING_SPEED;
		if (downIsPressed) pulseY += WALKING_SPEED;
		if (leftIsPressed) pulseX -= WALKING_SPEED;
		if (rightIsPressed) pulseX += WALKING_SPEED;

		pulseX /= elapse;
		pulseY /= elapse;

		if (spinMirrorIsPressed) {
			spinMirrorIsPressed = false;

			if (serverMirrorSpinSensor.isAreaEntred()) {
				GameNode node = serverMirrorSpinSensor.enteredAreaSet().iterator().next();
				if (node.colissionGroup().contains(Main.MIRROR_COLLISION_ID)) {
					((MirrorNetGameNode) node).spin();
				}
			}
		}

		if (Math.abs(vx) + Math.abs(vy) >= 0.001) {
			currentAnimation = Animation.WALKING;
		} else {
			currentAnimation = Animation.STANDING;
			Image i;
		}
	}

	private void faceRight() {
		currentFacing = Facing.RIGHT;
		serverMirrorSpinSensor.geometry.x = 50;
	}

	private void faceLeft() {
		currentFacing = Facing.LEFT;
		serverMirrorSpinSensor.geometry.x = -serverMirrorSpinSensor.geometry.width;
	}

	@Override
	protected void clientHandleServerUpdate(PlayerState update) {
		geometry.x = update.getX();
		geometry.y = update.getY();

		if (currentAnimation != update.getAnimation()) {
			// Change animation
		}

		currentFacing = update.getFacing();

		switch (update.getAnimation()) {
		case RUNNING:
			clientRect.color = Color.RED;
			break;

		case STANDING:
			clientRect.color = Color.GREY;
			break;

		case WALKING:
			clientRect.color = Color.BISQUE;
			break;
		}
	}

	@Override
	protected void serverHandleClientInput(Input input) {
		switch (input.getInputCase()) {
		case KEY:
			servertHandleKeyInput(input.getKey());
			break;

		case INPUT_NOT_SET:
			break;
		}
	}

	private boolean clientOnKeyPressed(KeyEvent event) {
		final Function<KeyType, Input> createInputKeyDown = keyType -> {
			KeyDown.Builder keyDown = KeyDown.newBuilder()
					.setKeyType(keyType);

			Key.Builder key = Key.newBuilder()
					.setKeyDown(keyDown);

			Input input = Input.newBuilder()
					.setKey(key)
					.build();

			return input;
		};

		switch (event.getCode()) {
		case UP:
			inputQueue.add(createInputKeyDown.apply(KeyType.MOVE_UP));
			break;

		case DOWN:
			inputQueue.add(createInputKeyDown.apply(KeyType.MOVE_DOWN));
			break;

		case LEFT:
			inputQueue.add(createInputKeyDown.apply(KeyType.MOVE_LEFT));
			break;

		case RIGHT:
			inputQueue.add(createInputKeyDown.apply(KeyType.MOVE_RIGHT));
			break;

		case Z:
			inputQueue.add(createInputKeyDown.apply(KeyType.SPIN_MIRROR));
			break;

		default:
			break;
		}

		return true;
	}

	private boolean clientOnKeyReleased(KeyEvent event) {
		final Function<KeyType, Input> createInputKeyUp = keyType -> {
			KeyUp.Builder keyUp = KeyUp.newBuilder()
					.setKeyType(keyType);

			Key.Builder key = Key.newBuilder()
					.setKeyUp(keyUp);

			Input input = Input.newBuilder()
					.setKey(key)
					.build();

			return input;
		};

		switch (event.getCode()) {
		case UP:
			inputQueue.add(createInputKeyUp.apply(KeyType.MOVE_UP));
			break;

		case DOWN:
			inputQueue.add(createInputKeyUp.apply(KeyType.MOVE_DOWN));
			break;

		case LEFT:
			inputQueue.add(createInputKeyUp.apply(KeyType.MOVE_LEFT));
			break;

		case RIGHT:
			inputQueue.add(createInputKeyUp.apply(KeyType.MOVE_RIGHT));
			break;

		case Z:
			inputQueue.add(createInputKeyUp.apply(KeyType.SPIN_MIRROR));
			break;

		default:
			break;
		}

		return true;
	}

	protected void servertHandleKeyInput(Key key) {
		switch (key.getKeyCase()) {
		case KEY_DOWN:
			serverHandleKeydownInput(key.getKeyDown());
			break;

		case KEY_UP:
			serverHandleKeyupInput(key.getKeyUp());
			break;

		case KEY_NOT_SET:
			break;
		}
	}

	public void serverHandleKeydownInput(KeyDown keyDown) {
		switch (keyDown.getKeyType()) {
		case MOVE_UP:
			upIsPressed = true;
			break;

		case MOVE_DOWN:
			downIsPressed = true;
			break;

		case MOVE_LEFT:
			faceLeft();
			leftIsPressed = true;
			break;

		case MOVE_RIGHT:
			faceRight();
			rightIsPressed = true;
			break;

		case SPIN_MIRROR:
			break;

		case PICK_ITEM:
			break;

		case SECRET:
			break;
		}
	}

	public void serverHandleKeyupInput(KeyUp keyUp) {
		switch (keyUp.getKeyType()) {
		case MOVE_UP:
			upIsPressed = false;
			break;

		case MOVE_DOWN:
			downIsPressed = false;
			break;

		case MOVE_LEFT:
			leftIsPressed = false;
			break;

		case MOVE_RIGHT:
			rightIsPressed = false;
			break;

		case SPIN_MIRROR:
			spinMirrorIsPressed = true;
			break;

		case PICK_ITEM:
			break;

		case SECRET:
			break;
		}
	}
}