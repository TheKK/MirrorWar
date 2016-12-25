package netGameNodeSDK;

import java.awt.geom.Rectangle2D;
import java.util.Optional;
import java.util.function.Function;

import gameEngine.Game;
import gameEngine.GameNode;
import gameEngine.GameScene;
import gameEngine.RectangleGameNode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import mirrorWar.Constants;
import mirrorWar.input.InputOuterClass.Input;
import mirrorWar.key.KeyOuterClass.Key;
import mirrorWar.key.KeyOuterClass.KeyDown;
import mirrorWar.key.KeyOuterClass.KeyType;
import mirrorWar.key.KeyOuterClass.KeyUp;
import mirrorWar.player.Player.PlayerState;
import mirrorWar.player.Player.PlayerState.Animation;
import mirrorWar.player.Player.PlayerState.Facing;

public final class PlayerNetGameNode extends NetGameNode<PlayerState, Input> {
	final double WALKING_SPEED = 0.12;
	
	final double mirrorSpinSensorWidth = 35, mirrorSpinSensorHeight = 20;
	final double mirrorPlaceSensorWidth = 50, mirrorPlaceSensorHeight = 50;

	private int id;

	private PlayerState.Color color = PlayerState.Color.ORANGE;

	private boolean upIsPressed = false;
	private boolean downIsPressed = false;
	private boolean leftIsPressed = false;
	private boolean rightIsPressed = false;

	private boolean spinMirrorIsPressed = false;
	private boolean pickMirrorIsPressed = false;

	private Animation currentAnimation = Animation.STANDING;
	private Facing currentFacing = Facing.RIGHT;

	// Client stuff
	private RectangleGameNode clientRect;

	// Server stuff
	private RectangleGameNode serverMirrorSpinSensor;
	private RectangleGameNode serverMirrorPlaceSensor;
	private Optional<MirrorNetGameNode> serverPickedMirror = Optional.empty();
	private Rectangle2D.Double serverRespawnRegion = null;

	public PlayerNetGameNode(int id) {
		this.id = id;
	}
	
	public PlayerNetGameNode(int id, Rectangle2D.Double respawnRegion) {
		this.id = id;
		serverRespawnRegion = respawnRegion;
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
	public void clientInitialize(GameScene scene) {

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
	public void serverInitialize(GameScene scene, boolean debugMode) {
		respawn();

		geometry.width = 50;
		geometry.height = 50;

		dampX = 0.75;
		dampY = 0.75;

		updateFunc = elapse -> {
			synchronized (inputQueue) {
				inputQueue.forEach(this::serverHandleClientInput);
				inputQueue.clear();
			}

			serverUpdate(elapse);
		};

		scene.physicEngine.addDynamicNode(this);

		serverMirrorSpinSensor = new RectangleGameNode(50, 10, mirrorSpinSensorWidth, mirrorSpinSensorHeight, Color.TRANSPARENT);
		addChild(serverMirrorSpinSensor);
		scene.physicEngine.addAreaNode(serverMirrorSpinSensor);

		serverMirrorPlaceSensor = new RectangleGameNode(0, 0, mirrorPlaceSensorWidth, mirrorPlaceSensorHeight, Color.TRANSPARENT);
		addChild(serverMirrorPlaceSensor);

		// TODO I'm not 100% sure this would work perfectly for some cases,
		//      find some ways to solve this.
		if (debugMode) {
			GameNode rect = new RectangleGameNode(0, 0, 50, 50, Color.RED);
			addChild(rect);
		}
		
		faceRight();
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

		// Handle SPIN_MIRRIR event
		if (spinMirrorIsPressed) {
			spinMirrorIsPressed = false;

			if (serverMirrorSpinSensor.isAreaEntred()) {
				GameNode node = serverMirrorSpinSensor.enteredAreaSet().iterator().next();
				if (node.collissionGroup().contains(Main.MIRROR_COLLISION_ID)) {
					((MirrorNetGameNode) node).spin();
				}
			}
		}

		// Handle PICK_MIRROR event
		if (pickMirrorIsPressed) {
			pickMirrorIsPressed = false;

			// Drop the mirror
			if (serverPickedMirror.isPresent()) {
				if (!serverMirrorPlaceSensor.isAreaEntred()) {
					dropMirror();
				}

			// Pick the mirror
			} else {
				if (serverMirrorSpinSensor.isAreaEntred()) {
					GameNode node = serverMirrorSpinSensor.enteredAreaSet().iterator().next();
					if (node.collissionGroup().contains(Main.MIRROR_COLLISION_ID)) {
						MirrorNetGameNode mirror = (MirrorNetGameNode) node;

						pickMirror(mirror);
					}
				}
			}
		}

		if (this.isAreaEntred()) {
			for (GameNode node : this.enteredAreaSet()) {
				if (node.collissionGroup().contains(Constants.PLAYER0_LASER_COLLISION_GROUP) || node.collissionGroup().contains(Constants.PLAYER1_LASER_COLLISION_GROUP)) {
					beKilled();
				}
			}
		}
		
		if (Math.abs(vx) + Math.abs(vy) >= 0.001) {
			currentAnimation = Animation.WALKING;
		} else {
			currentAnimation = Animation.STANDING;
		}
	}

	private void pickMirror(MirrorNetGameNode mirror) {
		mirror.picked();
		serverPickedMirror = Optional.of(mirror);
		Game.currentScene().physicEngine.removeAreaNode(serverMirrorSpinSensor);
		Game.currentScene().physicEngine.addAreaNode(serverMirrorPlaceSensor);
	}

	private void dropMirror() {
		Rectangle2D.Double dropPos = serverMirrorPlaceSensor.geometryInGameWorld();
		serverPickedMirror.get().drop(dropPos.x, dropPos.y);
		serverPickedMirror = Optional.empty();

		Game.currentScene().physicEngine.addAreaNode(serverMirrorSpinSensor);
		Game.currentScene().physicEngine.removeAreaNode(serverMirrorPlaceSensor);
	}

	private void faceRight() {
		currentFacing = Facing.RIGHT;
		
		serverMirrorSpinSensor.geometry.width = mirrorSpinSensorWidth;
		serverMirrorSpinSensor.geometry.height = mirrorSpinSensorHeight;
		serverMirrorSpinSensor.geometry.x = 50 + 0.1;
		serverMirrorSpinSensor.geometry.y = (50 - serverMirrorSpinSensor.geometry.height) / 2;
		
		serverMirrorPlaceSensor.geometry.x = 50 + 0.1;
		serverMirrorPlaceSensor.geometry.y = 0;
	}
	
	private void faceUp() {
		serverMirrorSpinSensor.geometry.width = mirrorSpinSensorHeight;
		serverMirrorSpinSensor.geometry.height = mirrorSpinSensorWidth;
		serverMirrorSpinSensor.geometry.x = (50 - serverMirrorSpinSensor.geometry.width) / 2;	
		serverMirrorSpinSensor.geometry.y = -serverMirrorSpinSensor.geometry.height - 0.1;
		
		serverMirrorPlaceSensor.geometry.x = 0;
		serverMirrorPlaceSensor.geometry.y = -serverMirrorPlaceSensor.geometry.height - 0.1;
	}
	
	private void faceDown() {
		serverMirrorSpinSensor.geometry.width = mirrorSpinSensorHeight;
		serverMirrorSpinSensor.geometry.height = mirrorSpinSensorWidth;
		serverMirrorSpinSensor.geometry.x = (50 - serverMirrorSpinSensor.geometry.width) / 2;
		serverMirrorSpinSensor.geometry.y = 50 + 0.1;
		
		serverMirrorPlaceSensor.geometry.x = 0;
		serverMirrorPlaceSensor.geometry.y = serverMirrorPlaceSensor.geometry.height + 0.1;
	}

	private void faceLeft() {
		currentFacing = Facing.LEFT;
		
		serverMirrorSpinSensor.geometry.width = mirrorSpinSensorWidth;
		serverMirrorSpinSensor.geometry.height = mirrorSpinSensorHeight;
		serverMirrorSpinSensor.geometry.x = -serverMirrorSpinSensor.geometry.width - 0.1;
		serverMirrorSpinSensor.geometry.y = (50 - serverMirrorSpinSensor.geometry.height) / 2;
		
		serverMirrorPlaceSensor.geometry.x = -serverMirrorPlaceSensor.geometry.width - 0.1;
		serverMirrorPlaceSensor.geometry.y = 0;
	}

	@Override
	public void clientHandleServerUpdate(PlayerState update) {
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

		case X:
			inputQueue.add(createInputKeyDown.apply(KeyType.PICK_MIRROR));
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

		case X:
			inputQueue.add(createInputKeyUp.apply(KeyType.PICK_MIRROR));
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
			faceUp();
			upIsPressed = true;
			break;

		case MOVE_DOWN:
			faceDown();
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

		case PICK_MIRROR:
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

		case PICK_MIRROR:
			pickMirrorIsPressed = true;
			break;

		case PICK_ITEM:
			break;

		case SECRET:
			break;
		}
	}
	
	public void beKilled() {
		// TODO make this and animation
		respawn();
	}
	
	private void respawn() {
		geometry.x = serverRespawnRegion.x + Math.random() * (serverRespawnRegion.width) - geometry.width;
		geometry.y = serverRespawnRegion.y + Math.random() * (serverRespawnRegion.height) - geometry.height;
		vx = 0;
		vy = 0;
		ay = 0;
		ax = 0;
		pulseX = 0;
		pulseY = 0;
	}
}