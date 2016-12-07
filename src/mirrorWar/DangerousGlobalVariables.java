package mirrorWar;

import java.util.Optional;
import java.util.logging.Logger;

import tcp.TcpClient;

public class DangerousGlobalVariables {
	public static Optional<TcpClient> tcpClient;
	public static Logger logger = Logger.getLogger("mirrorWar");
}
