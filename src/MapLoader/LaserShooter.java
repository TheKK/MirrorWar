package MapLoader;

import java.util.HashMap;



public class LaserShooter {
	
	private int x;
	private int y;
	private int height;
	private int width;
	private HashMap<String, String> properties;

	public LaserShooter(int x, int y, int height, int width, HashMap<String, String> properties) {
		// TODO Auto-generated constructor stub
		this.x=x;
		this.y=y;
		this.height=height;
		this.width=width;
		this.properties=properties;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public String getProperty(String key) {
		return properties.get(key);
	}

}
