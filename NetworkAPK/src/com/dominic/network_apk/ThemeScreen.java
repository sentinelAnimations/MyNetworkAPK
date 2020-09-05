package com.dominic.network_apk;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class ThemeScreen {
	private int btnSize, btnSizeSmall, margin, stdTs, edgeRad, dark, darkest, light, lighter, lightest, border, textCol, textDark;
	private float textYShift;
	private Boolean renderFileExplorer = false;
	private String mySavePath, darkThemePath, brightThemePath;
	private String[] nodePaths1, nodePaths2, pcPaths, colorPickerTitles = { "Dark", "Light", "Lighter", "Lightest", "Borders", "Text", "Text dark" };
	private PFont stdFont;
	private PImage screenshot;
	private PApplet p;
	private MainActivity mainActivity;
	private ImageButton[] mainButtons;
	private ColorPicker[] colorPickers = new ColorPicker[7];
	private ImageButton[] imageButons = new ImageButton[3];
	private JsonHelper jHelper;

	public ThemeScreen(PApplet p, int btnSize, int btnSizeSmall, int margin, int stdTs, int edgeRad, int dark, int darkest, int light, int lighter, int lightest, int border, int textCol, int textDark, float textYShift, String mySavePath, String[] pictoPaths, PFont stdFont) {
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
		this.textYShift = textYShift;
		this.mySavePath = mySavePath;
		this.nodePaths1 = nodePaths1;
		this.nodePaths2 = nodePaths2;
		this.stdFont = stdFont;
		this.p = p;
		mainActivity = (MainActivity) p;
		mainButtons = mainActivity.getMainButtons();
		for (int i = 0; i < colorPickers.length; i++) {
			colorPickers[i] = new ColorPicker(p, p.width / (colorPickers.length + 1) + p.width / (colorPickers.length + 1) * i, p.height / 2, btnSize, btnSizeSmall, (int) (btnSize * 1.2f), dark, stdTs, edgeRad, margin, btnSize, btnSizeSmall, light, lighter, lightest, textCol, textYShift, false, false, true, pictoPaths[0], stdFont, null); // isParented,renderBg,stayOpen
		}

		String[] imageButtonsHoverText = { "Set colors as theme", "Set bright theme", "Set dark theme" };
		for (int i = 0; i < imageButons.length; i++) {
			imageButons[i] = new ImageButton(p, p.width - margin - btnSizeSmall / 2 - btnSizeSmall * i - margin * i, p.height - margin - btnSizeSmall / 2, btnSizeSmall, btnSizeSmall, stdTs, margin, edgeRad, -1, textYShift, true, false, textCol, light, pictoPaths[i + 1], imageButtonsHoverText[i], null);
		}

		jHelper = new JsonHelper(p);

		String[] splitSavePath = p.split(mySavePath, "/");
		brightThemePath = "";
		darkThemePath = "";
		for (int i = 0; i < splitSavePath.length - 1; i++) {
			brightThemePath += splitSavePath[i] + "/";
			darkThemePath += splitSavePath[i] + "/";
		}
		brightThemePath += "colorThemeLight.json";
		darkThemePath += "colorThemeDark.json";

		setData(mySavePath);
	}

	public void render() {
		mainActivity.renderMainButtons();
		for (int i = 0; i < colorPickers.length; i++) {
			colorPickers[i].render();
			p.fill(textCol);
			p.textAlign(p.CENTER, p.CENTER);
			p.textFont(stdFont);
			p.textSize(stdTs);
			p.text(colorPickerTitles[i], colorPickers[i].getX(), colorPickers[i].getColorBarY() + colorPickers[i].getSlider().getH() + margin + stdTs / 2);
		}

		for (int i = imageButons.length - 1; i >= 0; i--) {
			imageButons[i].render();
			if (imageButons[i].getIsClicked()) {

				switch (i) {
				case 0:
					saveData();
					mainActivity.initializeLoadingScreen();
					break;
				case 1:
					setData(brightThemePath);
					break;
				case 2:
					setData(darkThemePath);
					break;
				}

				imageButons[i].setIsClicked(false);
			}
		}

	}

	private Boolean checkForContrast() {
		Boolean isEnoughContrast = true;
		int minContrast = 50;
		for (int i = 0; i < colorPickers.length; i++) {
			if (isEnoughContrast == false) {
				break;
			}
			for (int i2 = 0; i2 < colorPickers.length; i2++) {
				if (i != i2) {
					Boolean redContrastToLow = false, greenContrastToLow = false, blueContrastToLow = false;
					int col1 = colorPickers[i].getPickedCol();
					int col2 = colorPickers[i2].getPickedCol();
					if (p.abs(p.red(col1) - p.red(col2)) < minContrast) {
						redContrastToLow = true;
					}
					if (p.abs(p.green(col1) - p.green(col2)) < minContrast) {
						greenContrastToLow = true;
					}
					if (p.abs(p.blue(col1) - p.blue(col2)) < minContrast) {
						blueContrastToLow = true;
					}
					if (redContrastToLow && greenContrastToLow && blueContrastToLow) {
						isEnoughContrast = false;
						break;
					}

				}
			}
		}
		return isEnoughContrast;
	}

	private void saveData() {

		jHelper.clearArray();

		for (int i = 0; i < colorPickers.length; i++) {
			JSONObject colorPickerObject = new JSONObject();
			JSONObject colorPickerDetails = new JSONObject();

			ColorPicker cp = colorPickers[i];
			colorPickerDetails.put("index", i);
			colorPickerDetails.put("pickedCol", cp.getPickedCol());
			colorPickerDetails.put("brightness", cp.getBrightness());
			colorPickerDetails.put("pickerPosX", cp.getMarkerPos().x);
			colorPickerDetails.put("pickerPosY", cp.getMarkerPos().y);

			colorPickerObject.put("colorPicker" + i, colorPickerDetails);
			jHelper.appendObjectToArray(colorPickerObject);
		}

		jHelper.writeData(mySavePath);
	}

	private void setData(String path) {
		JSONArray loadedThemeScreenData = new JSONArray();
		loadedThemeScreenData = jHelper.getData(path);
		if (loadedThemeScreenData.isEmpty()) {
		} else if (jHelper.getIsFlawlessLoaded() == true) {
			for (int i = 0; i < colorPickers.length; i++) {
				JsonObject jsonObject = new JsonParser().parse(loadedThemeScreenData.get(i).toString()).getAsJsonObject();
				JsonObject jsonSubObject = jsonObject.getAsJsonObject("colorPicker" + i);
				int pickedCol = jsonSubObject.get("pickedCol").getAsInt();
				int brightness = jsonSubObject.get("brightness").getAsInt();
				int pickerPosX = jsonSubObject.get("pickerPosX").getAsInt();
				int pickerPosY = jsonSubObject.get("pickerPosY").getAsInt();

				ColorPicker cp = colorPickers[i];
				cp.setMarkerPos(pickerPosX, pickerPosY);
				cp.setBrightness(brightness);
			}
		}
	}

	public void onMousePressed() {

		for (int i = 0; i < mainButtons.length; i++) {
			if (mainButtons[0].getClickCount() % 2 == 0 || i == 0) {
				mainButtons[i].onMousePressed();
			}
		}

		for (int i = 0; i < colorPickers.length; i++) {
			colorPickers[i].onMousePressed();
		}
		for (int i = 0; i < imageButons.length; i++) {
			imageButons[i].onMousePressed();
		}
	}

	public void onMouseReleased() {
		for (int i = 0; i < mainButtons.length; i++) {
			if (mainButtons[0].getClickCount() % 2 == 0 || i == 0) {
				mainButtons[i].onMouseReleased();
			}
		}
		for (int i = 0; i < colorPickers.length; i++) {
			colorPickers[i].onMoueseReleased();
		}
		for (int i = 0; i < imageButons.length; i++) {
			imageButons[i].onMouseReleased();
		}
	}

	public void onKeyReleased(char k) {
	}

	public void onScroll(float e) {

	}

}
