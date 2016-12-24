package MapLoader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class MapLoader {

	private Gson parser;
	private JsonReader reader;
	private Map<String, List<MapObject>> objectLayers = new HashMap<String, List<MapObject>>();
	private long tileHeight;
	private long tileWidth;
	private long height;
	private long width;
	private String curDataHolder;

	public MapLoader(String mapPath) throws IOException {
		parser = new Gson();
		reader = new JsonReader(new FileReader(mapPath));
		tileHeight = 0;
		tileWidth = 0;
		height = 0;
		width = 0;
		curDataHolder = "";
		readMap();
	}

	private void readMap() throws IOException {

		reader.beginObject();
		while (reader.hasNext()) {
			String tagName = reader.nextName();

			switch (tagName) {
			case "layers":
				getLayers();
				break;

			case "tileheight":
				tileHeight = reader.nextLong();
				break;

			case "tilewidth":
				tileWidth = reader.nextLong();
				break;

			case "height":
				height = reader.nextLong();
				break;

			case "width":
				width = reader.nextLong();
				break;

			default:
				reader.skipValue();
				break;
			}
		}
		reader.endObject();

	}

	private void getLayers() throws IOException {

		reader.beginArray();
		while (reader.hasNext()) {
			readLayer();
		}
		reader.endArray();
	}

	private void readLayer() throws IOException {

		reader.beginObject();
		while (reader.hasNext()) {
			String tagName = reader.nextName();

			switch (tagName) {
			case "objects":
				getObjects();
				break;
			case "name":
				String layerName = reader.nextString();
				curDataHolder = layerName;

				break;
			default:
				reader.skipValue();
				break;
			}
		}
		reader.endObject();

	}

	private void getObjects() throws IOException {

		reader.beginArray();
		while (reader.hasNext()) {
			readObject();
		}
		reader.endArray();
	}

	private void readObject() throws IOException {

		int height = 0;
		int width = 0;
		int x = 0;
		int y = 0;
		HashMap<String, String> properties = new HashMap<>();

		reader.beginObject();
		while (reader.hasNext()) {
			String tagName = reader.nextName();

			switch (tagName) {
			case "properties":
				properties = readProperties();
				break;
			case "x":
				x = reader.nextInt();
				break;

			case "y":
				y = reader.nextInt();
				break;

			case "height":
				height = reader.nextInt();
				break;

			case "width":
				width = reader.nextInt();
				break;

			default:
				reader.skipValue();
				break;

			}

		}
		reader.endObject();
		
		if (!objectLayers.containsKey(curDataHolder)) {
			objectLayers.put(curDataHolder, new ArrayList<MapObject>());
		}
		objectLayers.get(curDataHolder).add(new MapObject(x, y, height, width, properties));
	}

	private HashMap<String, String> readProperties() throws IOException {

		HashMap<String, String> properties = new HashMap<>();
		reader.beginObject();
		while (reader.hasNext()) {
			String tagName = reader.nextName();
			String value = reader.nextString();
			properties.put(tagName, value);
		}
		reader.endObject();
		return properties;

	}

	public Map<String, List<MapObject>> getObjectLayers() {
		return objectLayers;
	}

}
