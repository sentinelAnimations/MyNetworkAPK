package com.dominic.network_apk;

import processing.core.PApplet;
import processing.core.PFont;
import processing.event.MouseEvent;

public class MainActivity extends PApplet {

	public static void main(String[] args) {
		PApplet.main("com.dominic.network_apk.MainActivity");
	}

	// Global variables
	// -----------------------------------------------------------------------
	static int mode = 0; // 0=loadingScreen, 1=first setup

	// Colors--------------------------------------------------
	public int dark = color(26, 32, 37), light = color(39, 48, 56), lighter = color(54, 67, 78), border = color(255, 191, 0), darkTransparent = color(26, 32, 37, 100), red = color(255, 0, 0), green = color(0, 255, 0), textCol = color(255), textDark = color(150);
	// colors -------------------------------------------------

	// Dimens--------------------------------------------------
	public int stdTs = 12, titleTs = 22, subtitleTs = 16, btnSize = 50, btnSizeLarge = btnSize * 2, btnSizeSmall = btnSize / 2, edgeRad = btnSize / 10, padding = 5, margin = padding;
	public float textYShift = 0.1f;
	// Dimens--------------------------------------------------

	// Strings--------------------------------------------------
	public String APKName = "InSevenDays©", APKDescription = "-A network solution-", mySettingsPath = "savedData/settings/mySettings.json";
	// Strings--------------------------------------------------

	// Fonts---------------------------------------------------
	PFont stdFont;
	// Fonts---------------------------------------------------

	// images--------------------------------------------------
	public String absPathPictos = "imgs/pictograms/";
	public String absPathStartImgs = "imgs/startImgs/";
	public String[] startImgPaths = { "muffins.png" };
	// images--------------------------------------------------

	// Classes--------------------------------------------------
	// Main classes-------------------------------
	public LoadingScreen loadingScreen;
	public SettingsScreen settingsScreen;
	// Main classes-------------------------------
	// widgets -----------------------------------
	public PictogramImage firstSetupPicto;
	public ImageButton firstSetupHelp_btn;
	// widgets -----------------------------------
	// Classes--------------------------------------------------
	// Global variables
	// -----------------------------------------------------------------------

	@Override
	public void settings() {
		pixelDensity(2);
	}

	@Override
	public void setup() {
		getSurface().setSize(1050, 450);

		rectMode(CENTER);
		imageMode(CENTER);
		// variableInitialisation -----------------------------------------------
		stdFont = createFont("fonts/stdFont.ttf", titleTs);

		loadingScreen = new LoadingScreen(this, btnSize, margin, stdTs, titleTs, subtitleTs, dark, textCol, textDark, textYShift, APKName, APKDescription, "imgs/startImgs/muffins.png", mySettingsPath, stdFont);

		String[] p1 = { absPathPictos + "masterOrSlave.png", absPathPictos + "blenderExeFolder.png", absPathPictos + "imageFolder.png", absPathPictos + "pathToCloud.png", absPathPictos + "personalData.png", absPathPictos + "checkmark.png", absPathPictos + "selectFolder.png" };
		String[] p2 = { absPathPictos + "volume.png", absPathPictos + "folderStructure.png", absPathPictos + "folder.png", absPathPictos + "file.png", absPathPictos + "arrowLeft.png", absPathPictos + "arrowRight.png", absPathPictos + "rename.png", absPathPictos + "search.png", absPathPictos + "copy.png", absPathPictos + "cutFolder.png", absPathPictos + "pasteFolder.png", absPathPictos + "addFolder.png", absPathPictos + "deleteFolder.png", absPathPictos + "deleteFile.png", absPathPictos + "questions.png", absPathPictos + "cross.png", absPathPictos + "checkmark.png", absPathPictos + "arrowUp.png", absPathPictos + "arrowDown.png" };
		settingsScreen = new SettingsScreen(this, btnSize, btnSizeSmall, stdTs, margin, edgeRad, textCol, textDark, dark, light, lighter, border, textYShift, mySettingsPath, p1, p2, stdFont);

		firstSetupPicto = new PictogramImage(this, margin + btnSize / 2, margin + btnSize / 2, btnSize, margin, stdTs, edgeRad, textCol, textYShift, false, absPathPictos + "settings.png", "First setup page", null);
		firstSetupHelp_btn = new ImageButton(this, width - btnSize / 2 - margin, btnSize / 2 + margin, btnSize, btnSize, stdTs, margin, edgeRad, 8, textYShift, false, false, textCol, textCol, absPathPictos + "questions.png", "questions and infos | sortcut: ctrl+h", null);
		// variableInitialisation -----------------------------------------------

	}

	@Override
	public void draw() {
		background(dark);
		if (mode == 0) { // loadingScreen ----------------
			loadingScreen.render();
		}

		if (mode == 1) {// setup for the first time -----------------
			if (loadingScreen.firstSetup == true && settingsScreen.getMode() == 0) {
				fill(light);
				stroke(light);
				rect(width / 2, btnSize / 2 + margin, width, btnSize + margin * 2);
				firstSetupPicto.render();
				firstSetupHelp_btn.render();
				fill(textDark);
				textAlign(LEFT, CENTER);
				text("First setup", firstSetupPicto.getX() + btnSize + margin * 2, firstSetupPicto.getY());
			} else {
				renderMainButtons();
			}
			settingsScreen.render();

			// render toasts----------------------------------------------
			for (int i = 0; i < settingsScreen.personalData_et.getToastList().size(); i++) {
				MakeToast m = (MakeToast) settingsScreen.personalData_et.getToastList().get(i);
				if (m.remove) {
					settingsScreen.personalData_et.removeToast(i);
				} else {
					m.render();
				}
			}

			for (int i = 0; i < settingsScreen.fileExplorer.searchBar.searchBar_et.getToastList().size(); i++) {
				MakeToast m = (MakeToast) settingsScreen.fileExplorer.searchBar.searchBar_et.getToastList().get(i);
				if (m.remove) {
					settingsScreen.fileExplorer.searchBar.searchBar_et.removeToast(i);
				} else {
					m.render();
				}
			}

			for (int i = 0; i < settingsScreen.getToastList().size(); i++) {
				MakeToast m = (MakeToast) settingsScreen.getToastList().get(i);
				if (m.remove) {
					settingsScreen.removeToast(i);
				} else {
					m.render();
				}
			}
			// render toasts----------------------------------------------

		}

	}

	@Override
	public void mousePressed() {
		if (mode == 1) {
			if (loadingScreen.firstSetup == true && settingsScreen.getMode() == 0) {
				firstSetupHelp_btn.onMousePressed();
			}
			settingsScreen.saveSettings_btn.onMousePressed();
			for (int i = 0; i < settingsScreen.pathSelectors.length; i++) {
				settingsScreen.pathSelectors[i].openFileExplorer_btn.onMousePressed();
			}
			for (int i = 0; i < settingsScreen.fileExplorer.horizontalLists.length; i++) {
				settingsScreen.fileExplorer.horizontalLists[i].goLeft_btn.onMousePressed();
				settingsScreen.fileExplorer.horizontalLists[i].goRight_btn.onMousePressed();
			}
			settingsScreen.fileExplorer.searchBar.search_btn.onMousePressed();
			settingsScreen.fileExplorer.rename_btn.onMousePressed();
			for (int i = settingsScreen.fileExplorer.fileExplorer_btns.length - 1; i >= 0; i--) {
				settingsScreen.fileExplorer.fileExplorer_btns[i].onMousePressed();
			}
			settingsScreen.masterOrSlave_dropdown.dropdown_btn.onMousePressed();

		}
	}

	@Override
	public void mouseReleased() {
		if (mode == 1) {
			if (loadingScreen.firstSetup == true) {
				firstSetupHelp_btn.onMouseReleased();
			}
			settingsScreen.saveSettings_btn.onMouseReleased();
			settingsScreen.personalData_et.onMouseReleased();
			for (int i = 0; i < settingsScreen.pathSelectors.length; i++) {
				settingsScreen.pathSelectors[i].openFileExplorer_btn.onMouseReleased();
			}
			for (int i = 0; i < settingsScreen.fileExplorer.horizontalLists.length; i++) {
				if (mouseButton == RIGHT) {
					settingsScreen.fileExplorer.horizontalLists[i].onMouseRightReleased();
				}
				if (mouseButton == LEFT) {
					settingsScreen.fileExplorer.horizontalLists[i].onMouseReleased();
				}
				settingsScreen.fileExplorer.horizontalLists[i].goLeft_btn.onMouseReleased();
				settingsScreen.fileExplorer.horizontalLists[i].goRight_btn.onMouseReleased();
			}
			settingsScreen.fileExplorer.rename_et.onMouseReleased();
			settingsScreen.fileExplorer.searchBar.search_btn.onMouseReleased();
			settingsScreen.fileExplorer.searchBar.searchBar_et.onMouseReleased();
			settingsScreen.fileExplorer.rename_btn.onMouseReleased();
			for (int i = settingsScreen.fileExplorer.fileExplorer_btns.length - 1; i >= 0; i--) {
				settingsScreen.fileExplorer.fileExplorer_btns[i].onMouseReleased();
			}
			settingsScreen.masterOrSlave_dropdown.dropdown_btn.onMouseReleased();
			settingsScreen.masterOrSlave_dropdown.onMouseReleased();
		}
	}

	@Override
	public void mouseWheel(MouseEvent event) {
		float e = event.getCount();
		if (mode == 1) {
			for (int i = 0; i < settingsScreen.fileExplorer.horizontalLists.length; i++) {
				settingsScreen.fileExplorer.horizontalLists[i].onScroll(e);
				settingsScreen.fileExplorer.horizontalLists[i].onScroll(e);
			}
			settingsScreen.masterOrSlave_dropdown.onScroll(e);
		}
	}

	@Override
	public void keyReleased() {
		if (mode == 1) {
			if (loadingScreen.firstSetup == true) {
				firstSetupHelp_btn.onKeyReleased(key);
			}
			settingsScreen.personalData_et.onKeyReleased(key);
			settingsScreen.saveSettings_btn.onKeyReleased(key);

			for (int i = 0; i < settingsScreen.fileExplorer.horizontalLists.length; i++) {
				settingsScreen.fileExplorer.horizontalLists[i].goLeft_btn.onKeyReleased(key);
				settingsScreen.fileExplorer.horizontalLists[i].goRight_btn.onKeyReleased(key);
			}
			settingsScreen.fileExplorer.rename_et.onKeyReleased(key);
			settingsScreen.fileExplorer.searchBar.searchBar_et.onKeyReleased(key);
			settingsScreen.fileExplorer.rename_btn.onKeyReleased(key);
			settingsScreen.fileExplorer.searchBar.search_btn.onKeyReleased(key);
			for (int i = settingsScreen.fileExplorer.fileExplorer_btns.length - 1; i >= 0; i--) {
				settingsScreen.fileExplorer.fileExplorer_btns[i].onKeyReleased(key);
			}

		}
	}

	public void renderMainButtons() {

	}
}
