package netGameNodeSDK;

import java.net.InetSocketAddress;

import gameEngine.Game;

public class Main {
	public static int serverTCPDefaultPort = 3399;
	public static int clientUDPDefaultPort = 3388;

	public static InetSocketAddress serverAddr = new InetSocketAddress("127.0.0.1", serverTCPDefaultPort);
	public static InetSocketAddress clientAddr = new InetSocketAddress("127.0.0.1", clientUDPDefaultPort);

	public static int PLAYER_COLLISION_ID = 0;
	public static int MIRROR_COLLISION_ID = 666;

	public static void main(String[] args) {
		Game.isClickBoundDebugMode = true;
		Game.isPhysicEngineDebugMode = true;

		if (args.length < 1) {
			Game.run(DevScene.class);

		} else if (args[0].equals("server")) {
			Game.run(ServerScene.class);

		} else if (args[0].equals("client")) {
			Game.run(ClientScene.class);

		} else {
			System.out.println("What is '" + args[0] + "' ?");
			return;
		}
	}
}
