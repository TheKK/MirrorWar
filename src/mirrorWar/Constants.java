package mirrorWar;

import java.awt.geom.Rectangle2D;

public class Constants {
	public static final int SERVER_HOST_PORT = 8521;
	public static final int PLAYER0_LASER_COLLISION_GROUP = 10;
	public static final int PLAYER1_LASER_COLLISION_GROUP = 11;
	public static final int LIFE = 3;
	public static final int PLAYER_NUM = 2;
	public static final Rectangle2D.Double PLAYER0_RESPAWN_REGION = new Rectangle2D.Double(0, 0, 150, 150);
	public static final Rectangle2D.Double PLAYER1_RESPAWN_REGION = new Rectangle2D.Double(300, 300, 150, 150);
}
