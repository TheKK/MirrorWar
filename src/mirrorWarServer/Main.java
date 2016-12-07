package mirrorWarServer;

import java.io.IOException;

import gameEngine.Game;
import mirrorWar.Constants;
import mirrorWar.DangerousGlobalVariables;
import tcp.GameMessage;
import tcp.TcpServer;
import tcp.WrongMessageException;

public class Main {
	static public void main(String[] args){	
		TcpServer tcpServer = new TcpServer(Constants.SERVER_HOST_PORT);
		
		while (true) {
			DangerousGlobalVariables.logger.info("[SERVER] wait for players to join...");
			
			try {
				tcpServer.waitForPlayerToJoin();
				tcpServer.sendMessageForEachPlayer(GameMessage.TEAM_MATCHED);
				if(GameMessage.PLAYER_IS_READY != tcpServer.recvMessageFromPlayer(0)) {
					throw new WrongMessageException("Message must be " + GameMessage.PLAYER_IS_READY.toString());
				}
				if(GameMessage.PLAYER_IS_READY != tcpServer.recvMessageFromPlayer(1)) {
					throw new WrongMessageException("Message must be " + GameMessage.PLAYER_IS_READY.toString());
				}
				DangerousGlobalVariables.logger.info("[SERVER] All client already join game");
				tcpServer.sendMessageForEachPlayer(GameMessage.GAME_START);
			} catch (WrongMessageException e) {
				DangerousGlobalVariables.logger.severe(e.getMessage());
			} catch (IOException e) {
				DangerousGlobalVariables.logger.severe(e.getMessage());
			}	
			
			try {
				Thread.sleep(3000);
				tcpServer.sendMessageForEachPlayer(GameMessage.GAME_OVER);
				DangerousGlobalVariables.logger.info("[SERVER] Game is over");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				tcpServer.clearAllSocket();
			}
			
			
		}
	}
}
