package mirrorWarServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import gameEngine.Game;
import mirrorWar.Constants;
import mirrorWar.DangerousGlobalVariables;
import mirrorWar.gameStatusUpdate.GameStatusUpdate;
import mirrorWar.gameStatusUpdate.GameStatusUpdate.ServerMessage;

public class Server {
	public void run() {
		ServerSocket tcpServer;
		try {
			tcpServer = new ServerSocket(Constants.SERVER_HOST_PORT);

		} catch (IOException e1) {
			DangerousGlobalVariables.logger.info("[SERVER] error while setup TCP server...");
			DangerousGlobalVariables.logger.info(e1.getMessage());

			return;
		}

		while (true) {
			List<Socket> clientSockets = new ArrayList<>();

			DangerousGlobalVariables.logger.info("[SERVER] wait for players to join...");

			try {
				for (int clientId = 0; clientId < Constants.PLAYER_NUM; ++clientId) {
					Socket clientSocket = tcpServer.accept();
					clientSockets.add(clientSocket);
				}

				System.out.println("[server] all player join room: " + clientSockets.size() + " players");

				sendServerMessageToAllClient(clientSockets, ServerMessage.Message.ALL_PLAYER_READY);

				DangerousGlobalVariables.logger.info("[SERVER] All client already join game");
				Game.run(new ServerScene(tcpServer, clientSockets));

			} catch (IOException e) {
				DangerousGlobalVariables.logger.severe(e.getMessage());
			}
		}
	}

	private void sendServerMessageToAllClient(List<Socket> clientSockets, ServerMessage.Message message) {
		clientSockets.forEach(socket -> {
			try {
				OutputStream out = socket.getOutputStream();

				GameStatusUpdate.ServerMessage.newBuilder()
					.setMsg(message)
					.build()
					.writeDelimitedTo(out);

			} catch (IOException e) {
				DangerousGlobalVariables.logger.severe("Connection error: Unable to sent message to client");
			}
		});
	}
}
