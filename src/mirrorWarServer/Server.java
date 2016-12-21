package mirrorWarServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import mirrorWar.Constants;
import mirrorWar.DangerousGlobalVariables;
import mirrorWar.gameStatusUpdate.GameStatusUpdate;
import mirrorWar.gameStatusUpdate.GameStatusUpdate.ServerMessage;
import mirrorWar.gameStatusUpdate.GameStatusUpdate.ServerMessage.Message;
import netGameNodeSDK.handshake.Handshake.ClientHandshake;
import netGameNodeSDK.handshake.Handshake.ServerHandshake;

public class Server {
	private DatagramSocket commandInputSocket;
	private DatagramPacket commandPacket;

	private DatagramSocket updateOutputSocket;
	private DatagramPacket updatePacket;
	
	private Map<Integer, Socket> playerTCPMap = Collections.synchronizedMap(new HashMap<>());
	private Map<Integer, InetSocketAddress> playerUDPMap = Collections.synchronizedMap(new HashMap<>());

	public void run() {
		ServerSocket tcpServer;
		try {
			tcpServer = new ServerSocket(Constants.SERVER_HOST_PORT);
			setupUDPServer();
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		
		
		while (true) {
			DangerousGlobalVariables.logger.info("[SERVER] wait for players to join...");
			
			try {
				final int PLAYER_NUM = 1;
				
				for (int clientId = 0; clientId < PLAYER_NUM; ++clientId) {
					Socket clientSocket = tcpServer.accept();
					ClientHandshake clientHandshake = handshakeWithClient(clientSocket, clientId);
					
					InetSocketAddress newClientUpdateAddr = new InetSocketAddress(clientSocket.getInetAddress(), clientHandshake.getUpdatePort());
					
					playerTCPMap.put(clientId, clientSocket);
					playerUDPMap.put(clientId, newClientUpdateAddr);
				}
				
				sendServerMessageToAllClient(ServerMessage.Message.ALL_PLAYER_READY);
				sendServerMessageToAllClient(ServerMessage.Message.GAME_START);


				DangerousGlobalVariables.logger.info("[SERVER] All client already join game");
			} catch (IOException e) {
				DangerousGlobalVariables.logger.severe(e.getMessage());
			}	
			
			try {
				Thread.sleep(3000);
				
				DangerousGlobalVariables.logger.info("[SERVER] Game is over");

				sendServerMessageToAllClient(ServerMessage.Message.GAME_OVER);
				playerTCPMap.clear();
				playerUDPMap.clear();
				
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
	}
	
	private void sendServerMessageToAllClient(ServerMessage.Message message) {
		playerTCPMap.forEach((clientId, clientSocket) -> {
			try {
				OutputStream out = clientSocket.getOutputStream();
				GameStatusUpdate.ServerMessage.newBuilder()
					.setMsg(message)
					.build()
					.writeDelimitedTo(out);
			} catch (IOException e) {
				DangerousGlobalVariables.logger.severe("Connection error: Unable to sent message to client");
			}
		});
	}
	
	private ClientHandshake handshakeWithClient(Socket socket, int clientId) throws IOException {
		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();

		// I. Wait for player sending ClientHandshake
		ClientHandshake clientHandshake = ClientHandshake.parseDelimitedFrom(in);

		// II. Send server's port which accepting client's commands
		ServerHandshake.newBuilder()
				.setCommandPort(commandInputSocket.getLocalPort())
				.setClientId(clientId)
				.build()
				.writeDelimitedTo(out);

		return clientHandshake;
	}
	
	private void setupUDPServer() throws SocketException {
		commandInputSocket = new DatagramSocket();
		updateOutputSocket = new DatagramSocket();

		byte[] commandData = new byte[2048];
		commandPacket = new DatagramPacket(commandData, commandData.length);

		byte[] updateData = new byte[2048];
		updatePacket = new DatagramPacket(updateData, updateData.length);
	}
}
