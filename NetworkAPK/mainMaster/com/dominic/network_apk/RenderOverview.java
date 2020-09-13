package com.dominic.network_apk;

import processing.core.PApplet;
import processing.core.PFont;

public class RenderOverview {

	private int stdTs, edgeRad, margin, btnSize, btnSizeSmall, dark, light, lighter, lightest, textCol, textDark, border;
	private float renderMode; // rendermode --> 0=render files, 0.1=files render settings, 1=render on sheepit
	private float textYShift;
	private String mySavePath;
	private String[] pictoPaths;
	private PFont stdFont;
	private PApplet p;
	private ImageButton cancelRendering_ImageButton;
	private MainActivity mainActivity;
	private FilesSettingsScreen filesSettingsScreen;
	private FilesRenderingScreen filesRenderingScreen;
	private RenderOnSheepitScreen renderOnSheepitScreen;

	public RenderOverview(PApplet p, int stdTs, int edgeRad, int margin, int btnSizeLarge, int btnSize, int btnSizeSmall, int dark, int light, int lighter, int textCol, int textDark, int border, float textYShift,String mySavePath, String[] pictoPaths, String[] hoLiPictoPaths,String[] arrowPaths, PFont stdFont) {
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
		this.textYShift = textYShift;
		this.mySavePath=mySavePath;
		this.pictoPaths = pictoPaths;
		this.stdFont = stdFont;
		mainActivity = (MainActivity) p;
		cancelRendering_ImageButton = new ImageButton(p, p.width - margin - btnSizeSmall / 2, p.height - margin - btnSizeSmall / 2, btnSizeSmall, btnSizeSmall, stdTs, margin, edgeRad, -1, textYShift, true, false, textCol, light, pictoPaths[0], "Quit render process", null);

		String[] rFSPictoPaths = {pictoPaths[3]};
		filesSettingsScreen = new FilesSettingsScreen(p, stdTs, edgeRad, margin, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border, textYShift, rFSPictoPaths, hoLiPictoPaths,arrowPaths, stdFont);
		String[] fRSPictoPaths = {};
		filesRenderingScreen = new FilesRenderingScreen(p, stdTs, edgeRad, margin, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border, textYShift, fRSPictoPaths, hoLiPictoPaths, stdFont);
		String[] rOSPictoPaths = { pictoPaths[1], pictoPaths[2] };
		renderOnSheepitScreen = new RenderOnSheepitScreen(p, stdTs, edgeRad, margin, btnSizeLarge, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border, textYShift, rOSPictoPaths, stdFont);
	}

	public void render() {

		// render all ---------------------------------------
		if (renderMode == 0.1f) {
			uploadBlenderFiles();
			filesSettingsScreen.render();
		}
		if (renderMode == 0) {
			filesRenderingScreen.render();
		}
		if (renderMode == 1) {
			renderOnSheepitScreen.render();
			startSheepit();
		}

		cancelRendering_ImageButton.render();
		// render all ---------------------------------------
		
		//button handling -------------------------
		if (cancelRendering_ImageButton.getIsClicked()) {
			if (renderMode == 0) {
				cancelFileRendering();
			}
			if (renderMode == 1) {
				cancelSheepitRendering();
			}
			mainActivity.setMode(1);
			cancelRendering_ImageButton.setIsClicked(false);
		}
		//button handling -------------------------

	}

	private void uploadBlenderFiles() {

	}

	private void startSheepit() {

	}

	private void cancelFileRendering() {

	}
	private void cancelSheepitRendering() {
		
	}

	public void onMousePressed(int mouseButton) {
		if (renderMode == 0.1f) {
			filesSettingsScreen.onMousePressed(mouseButton);
		}
		if (renderMode == 0) {
			filesRenderingScreen.onMousePressed(mouseButton);
		}
		if (renderMode == 1) {

		}

		cancelRendering_ImageButton.onMousePressed();
	}

	public void onMouseReleased(int mouseButton) {

		if (renderMode == 0.1f) {
			filesSettingsScreen.onMouseReleased(mouseButton);
		}
		if (renderMode == 0) {
			filesRenderingScreen.onMouseReleased(mouseButton);
		}
		if (renderMode == 1) {

		}
		cancelRendering_ImageButton.onMouseReleased();
	}

	public void onKeyPressed(char key) {
		if (renderMode == 0.1f) {
			filesSettingsScreen.onKeyPressed(key);
		}
		if (renderMode == 0) {
			filesRenderingScreen.onKeyPressed(key);
		}
		if (renderMode == 1) {

		}

	}

	public void onKeyReleased(char key) {
		if (renderMode == 0.1f) {
			filesSettingsScreen.onKeyReleased(key);
		}
		if (renderMode == 0) {
			filesRenderingScreen.onKeyReleased(key);
		}
		if (renderMode == 1) {

		}
	}

	public void onScroll(float e) {
		if (renderMode == 0.1f) {
			filesSettingsScreen.onScroll(e);
		}
		if (renderMode == 0) {
			filesRenderingScreen.onScroll(e);
		}
		if (renderMode == 1) {

		}
	}
	
	public ImageButton getCancelImageButton() {
		return cancelRendering_ImageButton;
	}

	public FilesSettingsScreen getRenderFilesSettings() {
		return filesSettingsScreen;
	}
	public FilesRenderingScreen getFilesRenderingScreen() {
		return filesRenderingScreen;
	}

	public void setRenderMode(float setM) {
		renderMode = setM;
	}

	public void setFileList(String[] l) {
		filesSettingsScreen.setFileList(l);
		filesRenderingScreen.setFileList(l);
	}
}
