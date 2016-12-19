package netGameNodeSDK;

import gameEngine.GameNode;
import gameEngine.GameScene;
import gameEngine.RectangleGameNode;
import javafx.scene.paint.Color;
import mirrorWar.Constants;
import netGameNodeSDK.charger.Charger.ChargerState;
import netGameNodeSDK.charger.Charger.ChargerState.Animation;

public class ChargerNetGameNode extends NetGameNode<ChargerState, Void> {
	private int id;
	private RectangleGameNode serverChargeringArea;
	private ChargerState.Animation clientCurrentAnimation = Animation.NORMAL, serverCurrentAnimation = Animation.NORMAL;

	public ChargerNetGameNode(int id) {
		this.id = id;
	}

	public int id() {
		return id;
	}

	@Override
	protected void clientInitialize(GameScene scene) {
		RectangleGameNode rec = new RectangleGameNode(0, 0, 50, 50, Color.BLUE);

		addChild(rec);

		updateFunc = (elapse) -> {
			clientUpdate(elapse);
		};
	}

	@Override
	protected void serverInitialize(GameScene scene, boolean debugMode) {
		geometry.x = 0;
		geometry.y = 0;
		geometry.width = 50;
		geometry.height = 50;

		scene.physicEngine.addStaticNode(this);

		serverChargeringArea = new RectangleGameNode(0, 0, 50, 50, Color.TRANSPARENT);
		addChild(serverChargeringArea);
		scene.physicEngine.addStaticNode(serverChargeringArea);

		updateFunc = (elapse) -> {
			serverUpdate(elapse);
		};
	}

	@Override
	protected void clientUpdate(long elapse) {
	}

	@Override
	protected void serverUpdate(long elapse) {
		if (serverChargeringArea.isAreaEntred()) {
			boolean player1LaserHit = false, player2LaserHit = false;

			for (GameNode node : serverChargeringArea.enteredAreaSet()) {
				if (node.colissionGroup().contains(Constants.PLAYER1_LASER_COLLISION_GROUP)) {
					player1LaserHit = true;
				} else if (node.colissionGroup().contains(Constants.PLAYER2_LASER_COLLISION_GROUP)) {
					player2LaserHit = true;
				}
			}

			if (player1LaserHit) {
				chargePlayer1();
			}
			if (player2LaserHit) {
				chargePlayer2();
			}
			if (player1LaserHit || player2LaserHit) {
				serverCurrentAnimation = Animation.IS_CHARGED;
			} else {
				serverCurrentAnimation = Animation.NORMAL;
			}
		}

	}

	protected void chargePlayer2() {

	}

	protected void chargePlayer1() {

	}

	@Override
	protected void clientHandleServerUpdate(ChargerState update) {
		if (clientCurrentAnimation != update.getAnimation()) {
			// TODO: play animation
		}
		clientCurrentAnimation = update.getAnimation();
		geometry.x = update.getX();
		geometry.y = update.getY();
	}

	@Override
	protected void serverHandleClientInput(Void input) {
	}

	@Override
	public ChargerState getStates() {
		return ChargerState.newBuilder()
				.setId(id)
				.setX(geometry.x)
				.setY(geometry.y)
				.setAnimation(serverCurrentAnimation)
				.build();
	}

}
