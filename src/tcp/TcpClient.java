package tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TcpClient {
	private DataInputStream input = null;
	private DataOutputStream output = null;
	private Socket socket = new Socket();
	
	public void connectServer(InetSocketAddress serverIp) throws IOException {
		assert(serverIp != null);
		
		socket.connect(serverIp);
		input = new DataInputStream(socket.getInputStream());
		output = new DataOutputStream(socket.getOutputStream());
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
	
	public void sendGameMessage(GameMessage gm) throws IOException {
		output.writeUTF(gm.toString());
	}

	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			System.err.println("Close socket failed");
		}
	}
}
