package MapLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.sun.media.jfxmedia.events.NewFrameEvent;

public class MapLoader {
	
	String mapPath;
	Gson parser;
	JsonReader reader;
	ArrayList<Mirror> mirrors;
	ArrayList<Charger> chargers;
	ArrayList<LaserShooter> laserShooters;
	long tileHeight;
	long tileWidth;
	long height;
	long width;
	String curDataHolder;
	
	
	public MapLoader() throws IOException {
		// TODO Auto-generated constructor stub
		String filePath = new File("").getAbsolutePath();
		mapPath=filePath.concat("//src//MapLoader//map.json");
		parser= new Gson();
		reader=new JsonReader(new FileReader(mapPath));
		mirrors=new ArrayList<>();
		chargers=new ArrayList<>();
		laserShooters=new ArrayList<>();
		tileHeight=0;
		tileWidth=0;
		height=0;
		width=0;
		curDataHolder="";
		readMap();
		
	}
	
	public MapLoader(String mapPath) throws IOException {
		// TODO Auto-generated constructor stub
		this.mapPath=mapPath;
		parser= new Gson();
		reader=new JsonReader(new FileReader(mapPath));
		mirrors=new ArrayList<>();
		chargers=new ArrayList<>();
		laserShooters=new ArrayList<>();
		tileHeight=0;
		tileWidth=0;
		height=0;
		width=0;
		curDataHolder="";
		readMap();
	}
	
	private void readMap() throws IOException{
		
		reader.beginObject();  
        while (reader.hasNext()) {  
            String tagName = reader.nextName(); 
            
            switch (tagName) {
			case "layers":
				getLayers();
				break;
				
			case "tileheight":
				tileHeight=reader.nextLong();
				break;
				
			case "tilewidth":
				tileWidth=reader.nextLong();
				break;
				
			case "height":
				height=reader.nextLong();
				break;
				
			case "width":
				width=reader.nextLong();
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
				String layerName=reader.nextString();
            	curDataHolder=layerName;
            	
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
		while(reader.hasNext()){
			readObject();
		}
		reader.endArray();
	}

	private void readObject() throws IOException {
		
		
		int height=0;
		int width=0;
		int x =0;
		int y =0;
		HashMap<String,String> properties=new HashMap<>();

		
		reader.beginObject();
		while (reader.hasNext()) {
			String tagName = reader.nextName();
			
			switch (tagName) {
			case "properties":
				properties=readProperties();
				break;
			case "x":
				x=reader.nextInt();
				break;
				
			case "y":
				y=reader.nextInt();
				break;
				
			case "height":
				height=reader.nextInt();
				break;
				
			case "width":
				width=reader.nextInt();
				break;

			default:
				reader.skipValue();
				break;
				
			}
			
		}
		reader.endObject();
		
		switch (curDataHolder) {
		case "chargers":
			chargers.add(new Charger(x,y,height,width));
			break;
		case "mirrors":
			mirrors.add(new Mirror(x,y,height,width));
			break;
		case "laserShooters":
			laserShooters.add(new LaserShooter(x,y,height,width,properties));
			break;

		default:
			break;
		}
		
	}

	private HashMap<String, String> readProperties() throws IOException {
		
		HashMap<String, String> properties=new HashMap<>();
		reader.beginObject();
		while(reader.hasNext()){
			String tagName=reader.nextName();
			String value=reader.nextString();
			properties.put(tagName, value);
		}
		reader.endObject();
		return properties;
		
	}
	
	public ArrayList<Charger> getChargers() {
		return chargers;
	}
	
	public ArrayList<Mirror> getMirrors() {
		return mirrors;
	}
	
	public ArrayList<LaserShooter> getLaserShooters() {
		return laserShooters;
	}
	
	

}
