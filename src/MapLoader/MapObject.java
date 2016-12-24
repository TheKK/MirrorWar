package MapLoader;

import java.util.Map;

public class MapObject {
	public int x;
	public int y;
	public int height;
	public int width;
	public Map<String, String> properties = null;

	public MapObject(int x, int y, int height, int width, Map<String, String> properties) {
		this.x=x;
		this.y=y;
		this.height=height;
		this.width=width;
		this.properties = properties;
	}
}
