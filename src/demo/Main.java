package demo;

import gameEngine.Game;

public class Main {
	public static void main(String[] args) {
		Game.isClickBoundDebugMode = true;
		Game.isPhysicEngineDebugMode = true;
		Game.run(CoinGameScene.class);
	}
}