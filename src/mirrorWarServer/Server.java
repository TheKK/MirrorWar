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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gameEngine.Game;
import mirrorWar.Constants;
import mirrorWar.DangerousGlobalVariables;
import mirrorWar.gameStatusUpdate.GameStatusUpdate;
import mirrorWar.gameStatusUpdate.GameStatusUpdate.ServerMessage;
import mirrorWarServer.ServerMatrixGameNode.ClientInfo;
import netGameNodeSDK.handshake.Handshake.ClientHandshake;
import netGameNodeSDK.handshake.Handshake.ServerHandshake;

public class Server {
	private DatagramSocket commandInputSocket;
	private DatagramPacket commandPacket;

	private DatagramSocket updateOutputSocket;
	private DatagramPacket updatePacket;

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
			
			List<ClientInfo> clientInfoList = new ArrayList<>();
			
			try {
				final int PLAYER_NUM = 1;
				
				for (int clientId = 0; clientId < PLAYER_NUM; ++clientId) {
					Socket clientSocket = tcpServer.accept();
					ClientHandshake clientHandshake = handshakeWithClient(clientSocket, clientId);
					
					InetSocketAddress clientUpdateAddr = new InetSocketAddress(clientSocket.getInetAddress(), clientHandshake.getUpdatePort());
					
					ClientInfo clientInfo = new ClientInfo(clientSocket, clientUpdateAddr, clientId);
					clientInfoList.add(clientInfo);
				}
				
				sendServerMessageToAllClient(clientInfoList, ServerMessage.Message.ALL_PLAYER_READY);
				sendServerMessageToAllClient(clientInfoList, ServerMessage.Message.GAME_START);
				
				DangerousGlobalVariables.logger.info("[SERVER] All client already join game");
				Game.run(new ServerScene(clientInfoList));
			} catch (IOException e) {
				DangerousGlobalVariables.logger.severe(e.getMessage());
			}
		}
	}
	
	private void sendServerMessageToAllClient(List<ClientInfo> clientInfoList, ServerMessage.Message message) {
		clientInfoList.forEach(clientInfo -> {
			try {
				OutputStream out = clientInfo.tcpSocket.getOutputStream();
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
