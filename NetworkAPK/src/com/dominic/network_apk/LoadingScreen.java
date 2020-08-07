package com.dominic.network_apk;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PVector;
import processing.data.JSONObject;

public class LoadingScreen {
	private int btnSize, margin, dark, textCol, textDark, stdTs, titleTs, subtitleTs;
	private float textYShift;
	public boolean firstSetup = true;
	private String APKDescription, APKName, loadingStatus = "Loading Data", mySettingsPath;
	private PApplet p;
	private PImage img;
	private PVector c;
	private PFont stdFont;
	private JSONObject settingsJson;
	private TextField tf;
	private SpriteAnimation loadingGearSprite;

	public LoadingScreen(PApplet p, int btnSize, int margin, int stdTs, int titleTs, int subtitleTs, int dark, int textCol, int textDark,float textYShift, String APKName, String APKDescription, String imgPath, String mySettingsPath, PFont stdFont) {
		this.p = p;
		this.btnSize = btnSize;
		this.margin = margin;
		this.stdTs = stdTs;
		this.titleTs = titleTs;
		this.subtitleTs = subtitleTs;
		this.dark = dark;
		this.textCol = textCol;
		this.textDark = textDark;
		this.textYShift=textYShift;
		this.APKDescription = APKDescription;
		this.APKName = APKName;
		this.stdFont = stdFont;
		img = p.loadImage(imgPath);
		img.resize(p.width, p.height);
		c = new PVector(p.width / 2, p.height / 2);
		String s = "InSevenDays 1.0 - Software written to simplify the management of network rendering based on Blenders internal Engine 'Cycles'. Discover the full untaimed force of an infinite extendable amounth of PCs while rendering and enjoy creative workflow on a new level. \nWith builtin features such as a node-based network-editor, easy render control options, and a straightforward software update possibility, these program allows you to spend your time on more valuable tasks than worrying about rendertime. \nDeveloped and designed by Dominic Gut";
		tf = new TextField(p, textDark, p.width / 4 - margin * 4, p.height / 2, p.width / 8, p.height / 2, stdTs, textYShift, true, false, s, stdFont, null);
		loadingGearSprite = new SpriteAnimation(p, margin * 2 + btnSize / 2, p.height - p.height / 8, btnSize, btnSize, 0, 129, textDark, false, "imgs/sprites/loadingGears/", null); // endInd=129, obwohl letztes bild '0128.png' --> weil start bei '0000.png'

		// load settings info, if not available, goto settingsPage----------------------
		try {
			settingsJson = p.loadJSONObject(mySettingsPath);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		if (settingsJson == null) {
			// if(1==1) {
			MainActivity.mode = 1;
		} else {
			firstSetup = false;
			loadSettings();
		}
		// load settings info, if not available, goto settingsPage----------------------

	}

	public void render() {
		p.tint(255);
		p.image(img, c.x + p.width / 8, c.y);
		p.fill(dark);
		p.stroke(dark);
		p.rect(p.width / 8, c.y, p.width / 4, p.height);
		p.textAlign(PConstants.CENTER, PConstants.CENTER);

		p.fill(textCol);
		p.textFont(stdFont);
		p.textSize(titleTs);
		p.text(APKName, p.width / 8, p.height / 8);
		p.textSize(subtitleTs);
		p.text(APKDescription, p.width / 8, p.height / 8 + titleTs / 2 + subtitleTs / 2);
		p.textAlign(PConstants.RIGHT, PConstants.CENTER);
		p.textSize(stdTs);
		p.fill(textDark);
		p.text(loadingStatus, p.width / 4 - margin * 2, p.height - p.height / 8 - stdTs / 6);
		p.stroke(textDark);
		p.line(margin * 4 + btnSize, p.height - p.height / 8, p.width / 4 - margin * 4 - p.textWidth(loadingStatus), p.height - p.height / 8);
		tf.render();
		loadingGearSprite.render();
	}

	private void loadSettings() {

	}

}
