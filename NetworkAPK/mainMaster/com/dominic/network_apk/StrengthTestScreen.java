package com.dominic.network_apk;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class StrengthTestScreen {

	private int mode, btnSize, btnSizeSmall, margin, stdTs, edgeRad, dark, darkest, light, lighter, lightest, border, textCol, textDark, red, green;
	private float textYShift;
	private Boolean fileExplorerIsOpen = false, prevStrengthTestThreadIsAlive = false;
	private String mySavePath;
	private String[] pictoPaths, hoLiPictoPaths, startList = {}, allPCNames;
	private float[] listX, listW;
	private int[] allPCStatus, allPCStrengths;
	private long curTime, prevTime;
	private PFont stdFont;
	private PApplet p;
	private HorizontalList strengthTest_HorizontalList;
	private ImageButton startTest_ImageButton;
	private MainActivity mainActivity;
	private ImageButton[] mainButtons;
	private ArrayList<Node> allConnectedNodes = new ArrayList<>();
	private JsonHelper jsonHelper;
	private Thread strengthTestThread;

	public StrengthTestScreen(PApplet p, int mode, int btnSize, int btnSizeSmall, int margin, int stdTs, int edgeRad, int dark, int darkest, int light, int lighter, int lightest, int border, int textCol, int textDark, int red, int green, float textYShift, String mySavePath, String[] pictoPaths, String[] hoLiPictoPaths, PFont stdFont) {
		this.mode = mode;
		this.btnSize = btnSize;
		this.btnSizeSmall = btnSizeSmall;
		this.margin = margin;
		this.stdTs = stdTs;
		this.edgeRad = edgeRad;
		this.dark = dark;
		this.darkest = darkest;
		this.light = light;
		this.lighter = lighter;
		this.lightest = lightest;
		this.border = border;
		this.textCol = textCol;
		this.textDark = textDark;
		this.red = red;
		this.green = green;
		this.textYShift = textYShift;
		this.mySavePath = mySavePath;
		this.pictoPaths = pictoPaths;
		this.hoLiPictoPaths = hoLiPictoPaths;
		this.stdFont = stdFont;
		this.p = p;

		mainActivity = (MainActivity) p;
		if (mainActivity.getIsMaster()) {
			mainButtons = mainActivity.getMainButtonsMaster();
		} else {
			mainButtons = mainActivity.getMainButtonsSlave();
		}
		jsonHelper = new JsonHelper(p);
		setupAll();
	}

	public void render() {
		if (mainActivity.getIsMaster()) {
			mainActivity.renderMainButtonsMaster();
		} else {
			mainActivity.renderMainButtonsSlave();
		}

		strengthTest_HorizontalList.render();
		startTest_ImageButton.render();

		if (strengthTest_HorizontalList.getList().length > 0) {
			if (strengthTest_HorizontalList.getIsShifted()) {

				listX = new float[strengthTest_HorizontalList.getListX().length];
				listW = new float[strengthTest_HorizontalList.getListW().length];
				listX = strengthTest_HorizontalList.getListX();
				listW = strengthTest_HorizontalList.getListW();

				/*
				 * for (int i = 0; i < allPCPictos.length; i++) { }
				 */
				strengthTest_HorizontalList.setIsShifted(false);
			}
			for (int i = strengthTest_HorizontalList.getFirstDisplayedInd(); i <= strengthTest_HorizontalList.getLastDisplayedInd(); i++) {
				try {
					if (allPCStatus[i] < 2) {
						p.stroke(green);
					} else {
						p.stroke(red);
					}
					p.noFill();
					p.rect(listX[i], strengthTest_HorizontalList.getY(), listW[i], strengthTest_HorizontalList.getH() - margin * 2, edgeRad);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		// update PCList---------------------------------------------
		if (allConnectedNodes.size() > 0) {
			curTime = System.nanoTime() / 1000000000;
			if (curTime - prevTime > mainActivity.getSuperShortTimeIntervall()) {
				updateLists();
				prevTime = curTime;
			}
		}
		// update PCList---------------------------------------------

		// handle buttons---------------------------------------------
		if (startTest_ImageButton.getIsClicked()) {
			strengthTestThread = new Thread(new Runnable() {
				@Override
				public void run() {
					startStrengthTest();
				}
			});
			strengthTestThread.start();
			startTest_ImageButton.setIsClicked(false);
		}
		if (strengthTestThread != null) {
			if (strengthTestThread.isAlive() == false && prevStrengthTestThreadIsAlive == true) {

			}
			prevStrengthTestThreadIsAlive = strengthTestThread.isAlive();
		}
	}

	private void startStrengthTest() {
		JSONArray loadedData = new JSONArray();
		JSONObject settingsDetails = new JSONObject();
		JSONObject settingsObject = new JSONObject();
		// give command to all pcs to do test -----------------------------

		loadedData = jsonHelper.getData(mainActivity.getMasterCommandFilePath());
		if (loadedData.isEmpty()) {
		} else {
			String modeName = mainActivity.getModeNamesMaster()[mode - 1];

			JSONObject loadedObject = (JSONObject) (loadedData.get(mode - 1));
			loadedObject = (JSONObject) loadedObject.get(modeName);
			p.println(loadedObject);
			loadedObject.put("startTesting", p.random(100));
			settingsObject.put(modeName,loadedObject);
			loadedData.set(mode-1, settingsObject);
			jsonHelper.setArray(loadedData);
			jsonHelper.writeData(mainActivity.getMasterCommandFilePath());
		}

		for (int i = 0; i < allConnectedNodes.size(); i++) {
			Node n = allConnectedNodes.get(i);
			if (allPCStatus[i] < 2) {
				settingsDetails.put(n.getPcSelection_DropdownMenu().getSelectedItem(), (int) (p.random(100)));
			}
		}
		// give command to all pcs to do test -----------------------------

		// check if all pcs are done with test ---------------------------

		// check if all pcs are done with test ---------------------------

		jsonHelper.clearArray();

		settingsObject.put("Strength", settingsDetails);
		jsonHelper.appendObjectToArray(settingsObject);
		jsonHelper.writeData(mySavePath);
	}

	private void updateLists() {
		if (allConnectedNodes.size() > 0) {
			for (int i = 0; i < strengthTest_HorizontalList.getList().length; i++) {
				Node n = allConnectedNodes.get(i);
				n.checkForSignsOfLife();

				try {
					allPCStatus[i] = n.getPcStatus();
					allPCNames[i] = n.getPcSelection_DropdownMenu().getSelectedItem();
					allPCStrengths[i] = n.getPCStrength();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void onMousePressed(int mouseButton) {
		strengthTest_HorizontalList.onMousePressed();
		startTest_ImageButton.onMousePressed();
		for (int i = 0; i < mainButtons.length; i++) {
			if (mainButtons[0].getClickCount() % 2 == 0 || i == 0) {
				mainButtons[i].onMousePressed();
			}
		}
	}

	public void onMouseReleased(int mouseButton) {
		strengthTest_HorizontalList.onMouseReleased(mouseButton);
		startTest_ImageButton.onMouseReleased();
		for (int i = 0; i < mainButtons.length; i++) {
			if (mainButtons[0].getClickCount() % 2 == 0 || i == 0) {
				mainButtons[i].onMouseReleased();
			}
		}
	}

	public void onKeyPressed(char key) {

	}

	public void onKeyReleased(char key) {

	}

	public void onScroll(float e) {

	}

	public void setupAll() {
		getConnectedNodes();
		if (allConnectedNodes.size() > 0) {
			startList = new String[allConnectedNodes.size()];
			for (int i = 0; i < startList.length; i++) {
				startList[i] = "Strength of PC: untested!";
			}
		}
		allPCStatus = new int[allConnectedNodes.size()];
		allPCNames = new String[allConnectedNodes.size()];
		allPCStrengths = new int[allConnectedNodes.size()];

		String descriptionText = "Connected computers";
		strengthTest_HorizontalList = new HorizontalList(p, p.width / 2 - btnSize / 2 - margin / 2, p.height / 2, p.width - margin * 3 - btnSize, btnSize, margin, edgeRad, stdTs, (int) p.textWidth(descriptionText) + margin * 3 + btnSizeSmall, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border, textYShift, '\\', false, false, false, descriptionText, hoLiPictoPaths, startList, stdFont, null);
		int btnX = strengthTest_HorizontalList.getW() / 2 + margin + strengthTest_HorizontalList.getH() / 2;
		startTest_ImageButton = new ImageButton(p, btnX, 0, strengthTest_HorizontalList.getH(), strengthTest_HorizontalList.getH(), stdTs, margin, edgeRad, -1, textYShift, true, true, textCol, lighter, pictoPaths[0], "Start Testing computer Strength", strengthTest_HorizontalList);

	}

	private void getConnectedNodes() {
		if (!mainActivity.getNodeEditor().getIsSetup()) {
			mainActivity.getNodeEditor().setupAll();
		}
		allConnectedNodes = mainActivity.getNodeEditor().getAllConnectedNodes();
	}

	public int getMode() {
		return mode;
	}
}
