package netGameNodeSDK;

import gameEngine.GameScene;
import mirrorWar.Constants;
import mirrorWar.gameReport.GameReport;

public abstract class GameReportNetGameNode extends NetGameNode<GameReport.Status, Void> {
	private int[] playersLife = {Constants.LIFE, Constants.LIFE};
	private int clientPlayerId = -1;

	public GameReportNetGameNode() {
	}
	
	public GameReportNetGameNode(int playerId) {
		clientPlayerId = playerId;
	}
	
	@Override
	public void clientInitialize(GameScene scene) {
		
	}

	@Override
	public void serverInitialize(GameScene scene, boolean debugMode) {

	}

	@Override
	protected void clientUpdate(long elapse) {

	}

	@Override
	protected void serverUpdate(long elapse) {
		
	}

	@Override
	protected void clientHandleServerUpdate(GameReport.Status update) {
		if (playersLife[clientPlayerId] != update.getPlayersLife(clientPlayerId)) {
			playersLife[clientPlayerId] = update.getPlayersLife(clientPlayerId);
			beAttacked();
		}
	}

	@Override
	protected void serverHandleClientInput(Void v) {
	}

	@Override
	public GameReport.Status getStates() {
		GameReport.Status.Builder result = GameReport.Status.newBuilder();
		
		for(int life: playersLife) {
			result.addPlayersLife(life);
		}
		
		return result.build();
	}

	public void hurtPlayer(int playerId) {
		playersLife[playerId] -= 1;
	}
	
	public boolean isPlayerDead(int playerId) {
		return playersLife[playerId] <= 0;
	}
	
	protected abstract void beAttacked();
}
