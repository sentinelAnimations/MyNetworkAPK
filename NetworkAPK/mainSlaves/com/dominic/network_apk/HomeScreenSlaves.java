package com.dominic.network_apk;

import java.io.PrintWriter;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import processing.core.PApplet;
import processing.core.PFont;

public class HomeScreenSlaves {
	private int stdTs, edgeRad, margin, btnSize, btnSizeSmall, dark, light, lighter, lightest, textCol, textDark, border, red, green;
	private int renderMode; // rendermode --> 0=render files, 1=render on
							// sheepit,2=sleeping
	private float textYShift;
	private Boolean allWorking=true;
	private String pathToCloud, pcAlias, pcFolderName;
	private String[] pictoPaths;
	private long curTime, lastLogTime;
	private PFont stdFont;
	private PApplet p;
	private ImageButton[] mainButtons;
	private ImageButton cancelRendering_ImageButton;
	private MainActivity mainActivity;
	private PictogramImage fileRendering_PictogramImage, sheepitRendering_PictogramImage, sleeping_PictogramImage;
	private TimeField timeField;
	private JsonHelper jsonHelper;
	private FileInteractionHelper fileInteractionHelper;
	private ArrayList<MakeToast> makeToasts = new ArrayList<MakeToast>();

	public HomeScreenSlaves(PApplet p, int stdTs, int edgeRad, int margin, int btnSizeLarge, int btnSize, int btnSizeSmall, int dark, int light, int lighter, int textCol, int textDark, int border, int red, int green, float textYShift, String[] pictoPaths, PFont stdFont) {
		this.p = p;
		this.stdTs = stdTs;
		this.edgeRad = edgeRad;
		this.margin = margin;
		this.btnSize = btnSize;
		this.btnSizeSmall = btnSizeSmall;
		this.dark = dark;
		this.light = light;
		this.lighter = lighter;
		this.lightest = lightest;
		this.textCol = textCol;
		this.textDark = textDark;
		this.border = border;
		this.red = red;
		this.green = green;
		this.textYShift = textYShift;
		this.pictoPaths = pictoPaths;
		this.stdFont = stdFont;
		mainActivity = (MainActivity) p;

		fileInteractionHelper = new FileInteractionHelper(p);

		if (mainActivity.getIsMaster()) {
			mainButtons = mainActivity.getMainButtonsMaster();
		} else {
			mainButtons = mainActivity.getMainButtonsSlave();
		}

		sheepitRendering_PictogramImage = new PictogramImage(p, p.width / 2, p.height / 2, btnSizeLarge, btnSizeLarge, margin, stdTs, edgeRad, textCol, textYShift, false, false, pictoPaths[0], "Rendering on Sheepit", null);
		sleeping_PictogramImage = new PictogramImage(p, p.width / 2, p.height / 2, btnSizeLarge, btnSizeLarge, margin, stdTs, edgeRad, textCol, textYShift, false, false, pictoPaths[1], "sleeping", null);
		fileRendering_PictogramImage = new PictogramImage(p, p.width / 2, p.height / 2, btnSizeLarge, btnSizeLarge, margin, stdTs, edgeRad, textCol, textYShift, false, false, pictoPaths[2], "Rendering file", null);

		timeField = new TimeField(p, margin, p.height - btnSizeSmall / 2 - margin,btnSizeLarge,stdTs+margin*2, stdTs, margin, edgeRad, textCol, light, false, false,true, "Timestamp: ", "", stdFont, null);
		timeField.setPos(timeField.getW() / 2 + margin, timeField.getY());
		p.println("now setting paths");
		pathToCloud = mainActivity.getPathToCloud();
		pcAlias = mainActivity.getPCName();
		pcFolderName = mainActivity.getPCFolderName();
		while (p.str(pathToCloud.charAt(pathToCloud.length() - 1)) == "\\") {
			pathToCloud = pathToCloud.substring(0, pathToCloud.length() - 1);
		}
		if (pathToCloud == null) {
			pathToCloud = "";
		}
		if (pcAlias == null) {
			pcAlias = "";
		}

		jsonHelper = new JsonHelper(p);
	}

	public void render() {
		if (mainActivity.getIsMaster()) {
			mainActivity.renderMainButtonsMaster();
		} else {
			mainActivity.renderMainButtonsSlave();
		}

		if (renderMode == 0) {
			fileRendering_PictogramImage.render();
		}
		if (renderMode == 1) {
			sheepitRendering_PictogramImage.render();
		}
		if (renderMode == 2) {
			sleeping_PictogramImage.render();
		}
		timeField.render();
	
		for (int i = 0; i < makeToasts.size(); i++) {
			MakeToast m = makeToasts.get(i);
			if (m.remove) {
				makeToasts.remove(i);
			} else {
				m.render();
			}
		}

		curTime = System.nanoTime() / 1000000000;
		if (curTime - lastLogTime > mainActivity.getStdTimeIntervall()) {
			p.println(curTime, lastLogTime, curTime - lastLogTime);
			logData();
			lastLogTime = curTime;
		}
	}

	private void logData() {

		jsonHelper.clearArray();
		JSONObject settingsDetails = new JSONObject();
		JSONObject settingsObject = new JSONObject();

		settingsDetails.put("logTime", curTime);
		settingsDetails.put("readableTime", timeField.getTimeString());
		settingsDetails.put("renderMode", renderMode);
		settingsObject.put("SystemLog", settingsDetails);

		String jsonPath = pathToCloud + "\\" + pcFolderName + "\\" + pcAlias + "\\" + mainActivity.getLogFileName();
		if (fileInteractionHelper.createParentFolders(jsonPath)) {
			jsonHelper.appendObjectToArray(settingsObject);
			allWorking = jsonHelper.writeData(jsonPath);
			if (allWorking) {
				makeToasts.add(new MakeToast(p, p.width / 2, timeField.getY(), stdTs, margin, edgeRad, 100, light, textCol, textYShift, false, "Saved LogFile", stdFont, null));
				timeField.setCol(green);
				timeField.setPostfix(" -Everything working correctly");
			} else {
				String errorMessage = "Error: can't write logFile.Path to cloud: '" + mainActivity.getPathToCloud() + "' might be incorrect";
				makeToasts.add(new MakeToast(p, p.width / 2, timeField.getY(), stdTs, margin, edgeRad, errorMessage.length() * 3, light, textCol, textYShift, false, errorMessage, stdFont, null));
				timeField.setCol(red);
				timeField.setPostfix(" -Some errors occured");
			}
		}
	}

	public void onMousePressed(int mouseButton) {
		for (int i = 0; i < mainButtons.length; i++) {
			mainButtons[i].onMousePressed();
		}
	}

	public void onMouseReleased(int mouseButton) {
		for (int i = 0; i < mainButtons.length; i++) {
			mainButtons[i].onMouseReleased();
		}
	}

	public void onKeyPressed(char key) {

	}

	public void onKeyReleased(char key) {
		for (int i = 0; i < mainButtons.length; i++) {
			mainButtons[i].onKeyReleased(key);
		}
	}

	public void onScroll(float e) {

	}
}
