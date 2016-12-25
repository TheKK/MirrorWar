package netGameNodeSDK;

import gameEngine.GameScene;
import gameEngine.RectangleGameNode;
import javafx.scene.paint.Color;
import mirrorWar.charger.Charger.ChargerState;
import mirrorWar.charger.Charger.ChargerState.Animation;

public class ChargerNetGameNode extends NetGameNode<ChargerState, Void> {
	private int id;
	protected boolean isCharging = false;
	private ChargerState.Animation clientCurrentAnimation = Animation.NORMAL;
	private ChargerState.Animation serverCurrentAnimation = Animation.NORMAL;

	public ChargerNetGameNode(int id) {
		this.id = id;
	}

	public int id() {
		return id;
	}

	@Override
	public void clientInitialize(GameScene scene) {
		RectangleGameNode rec = new RectangleGameNode(0, 0, 50, 50, Color.BLUE);

		addChild(rec);

		updateFunc = (elapse) -> {
			clientUpdate(elapse);
		};
	}

	@Override
	public void serverInitialize(GameScene scene, boolean debugMode) {
		geometry.x = 0;
		geometry.y = 0;
		geometry.width = 50;
		geometry.height = 50;

		scene.physicEngine.addStaticNode(this);

		updateFunc = (elapse) -> {
			serverUpdate(elapse);
		};
	}

	@Override
	protected void clientUpdate(long elapse) {
	}

	@Override
	protected void serverUpdate(long elapse) {
		if (isCharging) {
			serverCurrentAnimation = Animation.IS_CHARGED;
		} else {
			serverCurrentAnimation = Animation.NORMAL;
		}
		
		isCharging = false;
	}

	public void chargePlayer1() {

	}

	public void chargePlayer0() {

	}

	@Override
	public void clientHandleServerUpdate(ChargerState update) {
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
