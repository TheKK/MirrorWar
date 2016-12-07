package tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import mirrorWar.DangerousGlobalVariables;

public class TcpServer {
	private ServerSocket serverSocket = null;
	private List<Socket> clientList = new  ArrayList<Socket>();
	
	public TcpServer(int port) {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Failed to establish socket");
		}
	}
	
	public void waitForPlayerToJoin() throws IOException {
		while(clientList.size() < 2) {
			Socket socket = serverSocket.accept();
			clientList.add(socket);
		}
	}
	
	public void sendMessageForEachPlayer(GameMessage gm) throws IOException {
		for (Socket s : clientList) {
			DataOutputStream output = new DataOutputStream(s.getOutputStream());
			output.writeUTF(gm.toString());
		}
	}
	
	public void clearAllSocket() {
		for (Socket s : clientList) {
			try {
				s.close();
			} catch (IOException e) {
				DangerousGlobalVariables.logger.warning("[SERVER] Can't close socket");
			}
		}
		
		clientList.clear();
	}
	
	public GameMessage recvMessageFromPlayer(int playerNo) throws IOException, WrongMessageException {
		Socket s = clientList.get(playerNo);
		DataInputStream input = new DataInputStream(s.getInputStream());
		String gmCode = input.readUTF();
		
		for (GameMessage gm : GameMessage.values()) {
			if (gm.toString().equals(gmCode)) {
				return gm;
			}
		}
		
		throw new WrongMessageException("Wrong get message: " + gmCode);
	}
	
	public void close() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			System.err.println("Close socket failed");
		}
	}
}
