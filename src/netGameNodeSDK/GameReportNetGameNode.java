package netGameNodeSDK;

import gameEngine.GameScene;
import mirrorWar.Constants;
import mirrorWar.gameReport.GameReport;

public abstract class GameReportNetGameNode extends NetGameNode<GameReport.Status, Void> {
	private int[] playersLife = {Constants.LIFE, Constants.LIFE};
	private double[] chargersEnergy = {0, 0};
	
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
	public void clientHandleServerUpdate(GameReport.Status update) {
		if (playersLife[clientPlayerId] != update.getPlayersLife(clientPlayerId)) {
			playersLife[clientPlayerId] = update.getPlayersLife(clientPlayerId);
			beAttacked();
		}

		for(int i = 0; i < update.getPlayersChargerEnergyCount(); ++i) {
			chargersEnergy[i] = update.getPlayersChargerEnergy(i);
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
		
		for (double energy: chargersEnergy) {
			result.addPlayersChargerEnergy(energy);
		}
		
		return result.build();
	}
	
	public void chargeEnergy(int chargerId, double energyToCharge) {
		chargersEnergy[chargerId] += energyToCharge;
		
		while (chargersEnergy[chargerId] >= 100) {
			chargersEnergy[chargerId] -= 100;

			hurtPlayer((chargerId == 0) ? 1 : 0);
		}
	}

	public void hurtPlayer(int playerId) {
		playersLife[playerId] -= 1;
	}
	
	public boolean isPlayerDead(int playerId) {
		return playersLife[playerId] <= 0;
	}
	
	public int getPlayerLife(int playerId) {
		return playersLife[playerId];
	}
	
	public double getChargerEnergy(int chargerId) {
		return chargersEnergy[chargerId];
	}
	
	protected abstract void beAttacked();
}
