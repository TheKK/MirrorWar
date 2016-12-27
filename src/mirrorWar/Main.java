package mirrorWar;

import java.util.logging.Level;

import gameEngine.Game;

public class Main {
	public static void main(String[] args) {

		DangerousGlobalVariables.logger.setLevel(Level.INFO);

//		Game.isClickBoundDebugMode = true;
//		Game.isPhysicEngineDebugMode = true;
		Game.title = "Mirror War";

		Game.run(SplashScreen.class);
	}
}
