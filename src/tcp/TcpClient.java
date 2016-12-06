package tcp;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TcpClient {
	private DataInputStream input = null;
	private Socket socket = new Socket();
	
	public boolean coonectServer(InetSocketAddress serverIp) {
		assert(serverIp != null);
		
		try {
			socket.connect(serverIp);
			input = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			System.err.println("Failed to establish connection");
			return false;
		}
		return true;
	}
	
	public GameMessage waitForGameMessage() throws IOException, WrongMessageException {
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
			socket.close();
		} catch (IOException e) {
			System.err.println("Close socket failed");
		}
	}
}
