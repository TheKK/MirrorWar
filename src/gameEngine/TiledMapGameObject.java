package gameEngine;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class TiledMapGameObject extends GameNode {
	class Pos {
		int x, y;
	}

	private int mapWidth, mapHeight;
	private int tileWidth, tileHeight;

	private Image tileSetImage;
	private ArrayList<Pos> tileSetClips = new ArrayList<>();
	private int[] mapData;

	public TiledMapGameObject(String mapFilePath, Image tileSetImage) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		this.tileSetImage = tileSetImage;

		Gson gson = new Gson();
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(new FileReader(mapFilePath));

		JsonObject mapObject = element.getAsJsonObject();
		JsonArray layerArray = mapObject.get("layers").getAsJsonArray();

		Map<String, int[]> maps = new HashMap<>(); {

			for (JsonElement layerElement: layerArray) {
				JsonObject layerObject = layerElement.getAsJsonObject();
				String layerType = layerObject.get("type").getAsString();

				if (!layerType.equals("tilelayer")) continue;

				mapWidth = layerObject.get("width").getAsInt();
				mapHeight = layerObject.get("height").getAsInt();

				String mapName = layerObject.get("name").getAsString();
				mapData = gson.fromJson(layerObject.get("data").getAsJsonArray(), int[].class);

				maps.put(mapName, mapData);
			}
		}

		JsonArray tilesetsArray = mapObject.get("tilesets").getAsJsonArray();

		for (JsonElement tilesetElement : tilesetsArray) {
			JsonObject tilesetObject = tilesetElement.getAsJsonObject();

			tileWidth = tilesetObject.get("tilewidth").getAsInt();
			tileHeight = tilesetObject.get("tileheight").getAsInt();

			int imageWidth = tilesetObject.get("imagewidth").getAsInt();
			int imageHeight = tilesetObject.get("imageheight").getAsInt();

			int columns = imageWidth / tileWidth;
			int rows = imageHeight / tileHeight;

			tileSetClips.ensureCapacity(columns * rows);

			for (int y = 0; y < rows; ++y) {
				for (int x = 0; x < columns; ++x) {
					Pos pos = new Pos();

					pos.x = x * tileWidth;
					pos.y = y * tileHeight;

					tileSetClips.add(pos);
				}
			}

			// TODO Currently we only support "ONE" tile set, the reset will be ignored.
			break;
		}
	}

	@Override
	public void render(GraphicsContext gc) {
		for (int y = 0; y < mapHeight; ++y) {
			for (int x = 0; x < mapWidth; ++x) {
				int tileId = mapData[x + y * mapWidth];

				if (tileId == 0) continue;

				Pos clip = tileSetClips.get(tileId - 1);

				// XXX The visual doesn't work well, there's always some "grid" shown on screen.
				gc.drawImage(
						tileSetImage,
						clip.x, clip.y, tileWidth, tileHeight,
						x * tileWidth, y * tileHeight, tileWidth + 1, tileHeight + 1);
			}
		}
	}
}
