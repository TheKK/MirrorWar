package netGameNodeSDK;

import java.io.FileNotFoundException;

import gameEngine.AnimatedSpriteGameNode;
import gameEngine.Game;
import gameEngine.GameScene;
import mirrorWar.charger.Charger.ChargerState;
import mirrorWar.charger.Charger.ChargerState.Animation;

public class ChargerNetGameNode extends NetGameNode<ChargerState, Void> {
	private int id;
	protected boolean isCharging = false;

	// Client stuff
	private ChargerState.Animation clientCurrentAnimation = Animation.NORMAL;
	private AnimatedSpriteGameNode clientStandbySprite;
	private AnimatedSpriteGameNode clientChargingSprite;

	// Server stuff
	private ChargerState.Animation serverCurrentAnimation = Animation.NORMAL;

	public ChargerNetGameNode(int id) {
		this.id = id;
	}

	public int id() {
		return id;
	}

	@Override
	public void clientInitialize(GameScene scene) {
		try {
			clientStandbySprite = new AnimatedSpriteGameNode(
					Game.loadImage("./src/mirrorWar/pic/mirrorChargerStandby.png"),
					"./src/mirrorWar/pic/mirrorChargerStandby.json");
			addChild(clientStandbySprite);

			clientChargingSprite = new AnimatedSpriteGameNode(
					Game.loadImage("./src/mirrorWar/pic/mirrorChargerCharging.png"),
					"./src/mirrorWar/pic/mirrorChargerCharging.json");
			addChild(clientChargingSprite);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1290);
		}

		updateFunc = this::clientUpdate;

		setStandby();
	}

	@Override
	public void serverInitialize(GameScene scene, boolean debugMode) {
		geometry.x = 0;
		geometry.y = 0;
		geometry.width = 50;
		geometry.height = 50;

		scene.physicEngine.addStaticNode(this);

		updateFunc = this::serverUpdate;
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
			switch (update.getAnimation()) {
			case IS_CHARGED:
				setCharging();
				break;

			case NORMAL:
				setStandby();
				break;
			}
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

	private void setStandby() {
		clientStandbySprite.playFromStart(-1);
		clientStandbySprite.visible = true;

		clientChargingSprite.autoPlayed = false;
		clientChargingSprite.visible = false;
	}

	private void setCharging() {
		clientChargingSprite.playFromStart(-1);
		clientChargingSprite.visible = true;

		clientStandbySprite.autoPlayed = false;
		clientStandbySprite.visible = false;
	}
}