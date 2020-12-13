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
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;

import processing.core.PApplet;

public class JsonHelper {
	private Boolean isFlawlessLoaded = true;
	private PApplet p;
	private JSONArray myArray = new JSONArray();
	private JSONArray loadedData = new JSONArray();
	private FileInteractionHelper fileInteractionHelper;

	public JsonHelper(PApplet p) {
		this.p = p;
		fileInteractionHelper = new FileInteractionHelper(p);
	}

	public JSONArray getJSONArrayFromFileChannel(FileChannel channel) {
		JSONParser jsonParser = new JSONParser();
		Object obj;
		JSONArray arr = new JSONArray();
		try {
			ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());
			Charset charset = Charset.forName("UTF-8");
			String str = "";
			while (channel.read(byteBuffer) > 0) {
				byteBuffer.rewind();
				CharBuffer cb = charset.decode(byteBuffer);
				str += cb;
				byteBuffer.flip();
			}
			str = str.trim();
			//System.out.println(str);
			
			try {
				obj = jsonParser.parse(str);
				arr = (JSONArray) obj;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return arr;
	}

	public void writeFileChannel(File file, FileChannel channel, ByteBuffer byteBuffer) throws IOException {
		// delete old contente before writing new ------------------------
		channel.truncate(0);
		// delete old contente before writing new ------------------------

		// write to file-------------------------------------
		Set<StandardOpenOption> options = new HashSet<>();
		options.add(StandardOpenOption.CREATE);
		options.add(StandardOpenOption.APPEND);
		Path path = Paths.get(file.getAbsolutePath());
		channel.write(byteBuffer);
		// write to file-------------------------------------

	}

	public Boolean writeData(String path) {
		// Write JSON file
		// String[] splitPath = p.split(path, "/");
		// File f = new File(splitPath[0]);
		// f.mkdir();
		Boolean isSaved = false;
		File f = new File(path);
		if (!f.exists()) {
			// f.getParentFile().mkdirs();
			fileInteractionHelper.createParentFolders(f.getAbsolutePath());
		}

		try (FileWriter file = new FileWriter(path)) {

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String jsonOutput = gson.toJson(myArray);

			file.write(jsonOutput);
			file.flush();
			isSaved = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return isSaved;
	}

	private void readData(String path) throws Exception {
		JSONParser jsonParser = new JSONParser();
		isFlawlessLoaded = false;
		loadedData = new JSONArray();
		try (FileReader reader = new FileReader(path)) {
			isFlawlessLoaded = true;
			Object obj = jsonParser.parse(reader);
			loadedData = (JSONArray) obj;

		} catch (Exception e) {
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

	public JSONArray getDataFromSourceFolder(String path) {
		isFlawlessLoaded = false;

		try (InputStreamReader inputStreamReader = new InputStreamReader(JsonHelper.class.getResourceAsStream(path))) {
			loadedData = (JSONArray) new JSONParser().parse(inputStreamReader);
			isFlawlessLoaded = true;

		} catch (Exception e) {
			e.printStackTrace();
			isFlawlessLoaded = false;
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
		myArray = jArr;
	}

	public void clearArray() {
		myArray.clear();
	}

}
