package MapLoader;

import java.util.Map;

public class MapObject {
	public double x;
	public double y;
	public double height;
	public double width;
	public Map<String, String> properties = null;

	public MapObject(double x, double y, double height, double width, Map<String, String> properties) {
		this.x=x;
		this.y=y;
		this.height=height;
		this.width=width;
		this.properties = properties;
	}
}
