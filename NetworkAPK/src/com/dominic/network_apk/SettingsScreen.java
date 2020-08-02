package com.dominic.network_apk;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class SettingsScreen {
	private int btnSize, btnSizeSmall, stdTs, margin, edgeRad, textCol, textDark,dark, light, lighter, mode = 0, activePathSelectorInd = 0;
	private String[] imgPaths;
	private PFont stdFont;
	private PImage screenshot;
	private PApplet p;
	private PictogramImage[] setting_pictos;
	public PathSelector[] pathSelectors;
	public ImageButton saveSettings_btn;
	public EditText personalData_et;
	public FileExplorer fileExplorer;

	public SettingsScreen(PApplet p, int btnSize, int btnSizeSmall, int stdTs, int margin, int edgeRad, int textCol, int textDark,int dark, int light, int lighter,int border, String[] imgPaths,String[] HorizontalListPictoPaths, PFont stdFont) {
		this.p = p;
		this.btnSize = btnSize;
		this.btnSizeSmall = btnSizeSmall;
		this.stdTs = stdTs;
		this.margin = margin;
		this.edgeRad = edgeRad;
		this.textCol = textCol;
		this.textDark = textDark;
		this.dark=dark;
		this.light = light;
		this.lighter=lighter;
		this.imgPaths = imgPaths;
		this.stdFont = stdFont;

		setting_pictos = new PictogramImage[imgPaths.length - 2];
		pathSelectors = new PathSelector[imgPaths.length - 4];
		String[] description = { "Setup this Pc as Slave or Master", "Select Blender.exe Folder", "Select image output Folder", "Select Path to Cloud", "Enter desired Name of PC", "Save Settings and move on | shortcut: ctrl+s" };
		String[] pathSelectorHints = { "../Blender.exe", "../images", "../Cloud" };

		for (int i = 0; i < setting_pictos.length; i++) {
			setting_pictos[i] = new PictogramImage(p, (p.width / 8 * 4) / 2 + p.width / 8 * (i), p.height / 2 - btnSize / 2, btnSize, margin, stdTs, edgeRad, textCol, false, imgPaths[i], description[i], null);
			if (i > 0 && i < pathSelectors.length + 1) {
				pathSelectors[i - 1] = new PathSelector(p, 0, btnSize, p.width / 8 - margin * 2, btnSizeSmall, edgeRad, margin, stdTs, light, textCol, textDark, true, pathSelectorHints[i - 1], imgPaths[imgPaths.length - 1], stdFont, setting_pictos[i]);
			}
		}

		saveSettings_btn = new ImageButton(p, p.width - margin - btnSizeSmall / 2, p.height - margin - btnSizeSmall / 2, btnSizeSmall, btnSizeSmall, stdTs, margin, edgeRad, 19, true, false, textCol, light, imgPaths[5], description[5], null);
		char[] fChars= {'>','<',':','"','/','\\','|','?','*'};
		personalData_et = new EditText(p, 0, btnSize, p.width / 8 - margin * 2, btnSizeSmall, stdTs, light, textCol, edgeRad, margin,true, true, "Enter PC name",fChars, stdFont, setting_pictos[setting_pictos.length - 1]);

		fileExplorer = new FileExplorer(p, p.width / 2, p.height / 2, p.width - margin*2, 6 * btnSizeSmall + 19 * margin, stdTs, edgeRad, margin, dark, light, lighter, textCol,textDark,border,btnSize,btnSizeSmall, HorizontalListPictoPaths,stdFont);

	}

	public void render() {
		if (mode == 0) { // normal mode
			for (int i = setting_pictos.length - 1; i >= 0; i--) {
				setting_pictos[i].render();
			}
			for (int i = pathSelectors.length - 1; i >= 0; i--) {
				PathSelector ps = pathSelectors[i];
				ps.render();
				if (ps.openFileExplorer_btn.isClicked) {
					mode = 1;
					activePathSelectorInd = i;

				}
			}
			saveSettings_btn.render();
			personalData_et.render();
			if (mode == 1) {
				p.saveFrame("data\\imgs\\screenshots\\settingsScreen.png");
				screenshot = p.loadImage("data\\imgs\\screenshots\\settingsScreen.png");
				screenshot = new ImageBlurHelper(p).blur(screenshot, 3);
			}
		}

		if (mode == 1) { // fileExplorer mode
			renderFileExplorer();
		}

	}

	private void renderFileExplorer() {
		p.image(screenshot, p.width / 2, p.height / 2);
		
		PathSelector ps = pathSelectors[activePathSelectorInd];

		if (ps.openFileExplorer_btn.isClicked) {
			fileExplorer.render();

			if (fileExplorer.getIsClosed()) {
				if (fileExplorer.getIsCanceled()) {
				} else {
					ps.setText(fileExplorer.getPath());
				}
				ps.openFileExplorer_btn.isClicked = false;
				mode = 0;
			}
		}
	}

	public int getMode() {
		return mode;
	}
}
