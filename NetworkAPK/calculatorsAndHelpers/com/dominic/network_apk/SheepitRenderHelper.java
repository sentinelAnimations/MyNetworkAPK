package com.dominic.network_apk;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import processing.core.PApplet;

public class SheepitRenderHelper {
	private Boolean isRendering = false;
	private PApplet p;
	private MainActivity mainActivity;
	private JsonHelper jsonHelper;
	private CommandExecutionHelper commandExecutionHelper;
	private FileInteractionHelper fileInteractionHelper;

	public SheepitRenderHelper(PApplet p) {
		this.p = p;
		mainActivity = (MainActivity) p;
		jsonHelper = new JsonHelper(p);
		commandExecutionHelper = new CommandExecutionHelper(p);
		fileInteractionHelper = new FileInteractionHelper(p);
	}

	public String getSheepitExePath() {
		String path = fileInteractionHelper.getFoldersAndFiles(mainActivity.getLocalProgrammPath(), false)[0];
		if (path == null) {
			path = "";
		}
		return path;
	}

	public void startRenderingOnSheepit(String sheepitExePath) {
		
		
		Boolean[] hwToUse = mainActivity.getHardwareToRenderWith(mainActivity.getPCName(), true);
		commandExecutionHelper.executeCommand("");
		isRendering = true;
		p.println("started sheepit");
	}

	public void finishRenderingOnSheepit() {
		commandExecutionHelper.executeCommand("TASKKILL /FI \"WINDOWTITLE eq sheep*\"");
		isRendering = false;
	}

	public void setStartRenderingOnSheepit(Boolean state) {
		try {
			int mode = mainActivity.getHomeScreenMaster().getMode() - 1;
			String modeName = mainActivity.getModeNamesMaster()[mode];
			JSONArray loadedArray = jsonHelper.getData(mainActivity.getMasterCommandFilePath());
			JSONObject homeObj = (JSONObject) (loadedArray.get(mode));
			JSONObject innerHomeObj = (JSONObject) homeObj.get(modeName);
			innerHomeObj.put("startRenderingOnSheepit", state);
			homeObj.put(modeName, innerHomeObj);
			loadedArray.set(mode, homeObj);
			jsonHelper.clearArray();
			jsonHelper.setArray(loadedArray);
			jsonHelper.writeData(mainActivity.getMasterCommandFilePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Boolean getStartRenderingOnSheepit(String path) {
		try {
			int mode = 0;
			String modeName = "";
			if (mainActivity.getIsMaster()) {
				mode = mainActivity.getHomeScreenMaster().getMode() - 1;
				modeName = mainActivity.getModeNamesMaster()[mode];
			} else {
				mode = mainActivity.getHomeScreenSlaves().getMode() - 1;
				modeName = mainActivity.getModeNamesSlaves()[mode];
			}
			JSONArray loadedArray = jsonHelper.getData(mainActivity.getMasterCommandFilePath());
			JSONObject homeObj = (JSONObject) (loadedArray.get(mode));
			JSONObject innerHomeObj = (JSONObject) homeObj.get(modeName);
			return Boolean.parseBoolean(innerHomeObj.get("startRenderingOnSheepit").toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Boolean getIsRendering() {
		return isRendering;
	}
}
