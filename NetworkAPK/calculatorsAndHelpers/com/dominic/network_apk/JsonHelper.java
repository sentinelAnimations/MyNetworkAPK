package com.dominic.network_apk;

import org.json.simple.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import processing.core.PApplet;

public class JsonHelper {
	private Boolean isFlawlessLoaded = true;
	private PApplet p;
	private JSONArray myArray = new JSONArray();
	private JSONArray loadedData = new JSONArray();

	public JsonHelper(PApplet p) {
		this.p = p;
	}

	public void writeData(String path) {
		// Write JSON file
		String[] splitPath = p.split(path, "/");
		File f = new File(splitPath[0]);
		f.mkdir();
		f = new File(path);
		f.getParentFile().mkdir();

		try (FileWriter file = new FileWriter(path)) {

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String jsonOutput = gson.toJson(myArray);

			file.write(jsonOutput);
			file.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readData(String path) throws Exception {
		JSONParser jsonParser = new JSONParser();
		isFlawlessLoaded = false;
		try (FileReader reader = new FileReader(path)) {
			isFlawlessLoaded = true;
			Object obj = jsonParser.parse(reader); 
			loadedData = (JSONArray) obj;

		} catch (FileNotFoundException e) {
			isFlawlessLoaded = false;
			e.printStackTrace();
		} catch (IOException e) {
			isFlawlessLoaded = false;
			e.printStackTrace();
		} catch (ParseException e) {
			isFlawlessLoaded = false;
			e.printStackTrace();
		}
	}

	public JSONArray getData(String path) {
		try {
            readData(path);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		return loadedData;
	}

	public Boolean getIsFlawlessLoaded() {
		return isFlawlessLoaded;
	}

	public void appendObjectToArray(JSONObject jObj) {
		myArray.add(jObj);
	}
	
	public void setArray(JSONArray jArr) {
	    myArray.clear();
	    myArray=jArr;
	}


	public void clearArray() {
		myArray.clear();
	}

}
