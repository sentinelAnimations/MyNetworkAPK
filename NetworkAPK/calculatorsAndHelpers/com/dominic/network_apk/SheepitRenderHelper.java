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
		String path = "";
		try {
			path = mainActivity.getLocalProgrammPath()+"\\"+fileInteractionHelper.getFoldersAndFiles(mainActivity.getLocalProgrammPath(), false)[0];

		} catch (Exception e) {
			e.printStackTrace();
		}

		return path;
	}

	public void startRenderingOnSheepit(String sheepitExePath) {
		
		JSONArray loadedData = jsonHelper.getData(mainActivity.getSheepitSettingsPath());
		if (loadedData.isEmpty()) {
		} else {
			try {
				JSONObject settingsObject = (JSONObject) loadedData.get(0);
				String password = settingsObject.get("password").toString();
				String username = settingsObject.get("username").toString();
				
				Boolean[] hwToUse = mainActivity.getHardwareToRenderWith(mainActivity.getPCName(), true);
				p.println(hwToUse);
				for(int i=0;i<hwToUse.length;i++) {
				p.println(sheepitExePath);
				if( hwToUse[i]==true) {
				String commandStr="start "+sheepitExePath+" -compute-method ";
				if(i==0) {
					commandStr+="CPU";
				}
				if(i==1) {
					commandStr+="GPU";
				}
				commandStr+=" -login "+username+" -password "+password;
				if(i==1 ) {
					commandStr+=" -gpu CUDA_0";
				}
				
				commandExecutionHelper.executeCommand(commandStr);
				isRendering = true;
				p.println("started sheepit"+commandStr);
				}
			}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

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
	public Boolean getWindowIsOpen() {
		Boolean isOpen=false;
		isOpen=commandExecutionHelper.isWindowOpenSimple("Sheep*");
		return isOpen;
	}
}
