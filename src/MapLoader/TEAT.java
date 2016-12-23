package MapLoader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.stream.JsonReader;
import com.sun.media.jfxmedia.events.NewFrameEvent;

public class TEAT {
	
	
	static JsonReader reader;
	static ArrayList<Mirror> mirrors;
	static ArrayList<Charger> chargers;
	static ArrayList<LaserShooter> laserShooters;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		String filePath = new File("").getAbsolutePath();
		String mapPath=filePath.concat("//src//MapLoader//test.json");
		
		reader=new JsonReader(new FileReader(mapPath));
		mirrors=new ArrayList<>();
		chargers=new ArrayList<>();
		laserShooters=new ArrayList<>();

		MapLoader mLoader=new MapLoader();
		
		//readObject();
		
		System.out.println(mLoader);
	}
	
	private static void readObject() throws IOException {
		
		System.out.println("readObj");
		int height=0;
		int width=0;
		int x =0;
		int y =0;
		HashMap<String,String> properties=new HashMap<>();

		
		reader.beginObject();
		while (reader.hasNext()) {
			String tagName = reader.nextName();
			System.out.println(tagName);
			
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
				System.out.println(tagName);
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
		
		switch ("laserShooters") {
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

	private static HashMap<String, String> readProperties() throws IOException {
		System.out.println("readProperties");
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

}
