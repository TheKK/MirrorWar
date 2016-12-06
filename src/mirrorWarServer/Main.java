package mirrorWarServer;

import java.io.IOException;

import mirrorWar.Constants;
import tcp.GameMessage;
import tcp.TcpServer;

public class Main {
	static public void main(String[] args) throws IOException {	
		TcpServer tcpServer = new TcpServer(Constants.SERVER_HOST_PORT);
		
		System.out.println("[server] wait for players to join...");
		tcpServer.waitForPlayerToJoin();
		
		System.out.println("[server] tell players to start game...");
		tcpServer.sendMessageForEachPlayer(GameMessage.GAME_START);
		
		System.out.println("Game started!");
	}
}
