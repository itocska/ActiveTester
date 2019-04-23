package ecdh;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class Learn {
	
	static JsonObject wrapper = new JsonObject();
	static JsonObject logString = new JsonObject();
	static JsonArray steps = new JsonArray();
	
	public static void main(String[] args) throws Throwable {
		
		logString.addProperty("time", "2018-01-03 22:12:23");
		
		JsonObject testlogs = new JsonObject();
		
		
		Gson g = new Gson();
		String toSend = g.toJson(logString);
		JsonObject newObj = new JsonParser().parse(toSend).getAsJsonObject(); 
		
		JsonPrimitive step = new JsonPrimitive("test");
		steps.add(step);
		
		logString.addProperty("time", "2018-01-03 22:12:23");
		logString.addProperty("steps", g.toJson(steps));
		wrapper.addProperty("test", g.toJson(logString));
		System.out.println(wrapper);
		
		System.out.println(newObj);
		
	}

	private static void log(String string) throws IOException {
		BufferedWriter bw = null;
		FileWriter fw = null;
	
		File file = new File("/www/webdrivers/test.txt");
	
		// if file doesn't exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}
	
		// true = append file
		fw = new FileWriter(file.getAbsoluteFile(), true);
		bw = new BufferedWriter(fw);
	
		bw.write(string);
	
		System.out.println("Done");
		
		bw.close();
		fw.close();
		
	}
	
	
}
