package com.dominic.network_apk;

import processing.core.PApplet;
import processing.core.PFont;

public class RenderOverview {

	private int stdTs, edgeRad, margin, btnSize, btnSizeSmall, dark, light, lighter, lightest, textCol, textDark, border;
	private float renderMode; // rendermode --> 0=render files, 0.1=files render settings, 1=render on sheepit
								// 2=imageView
	private float textYShift;
	private String mySavePath;
	private String[] pictoPaths;
	private PFont stdFont;
	private PApplet p;
	private ImageButton cancelRendering_ImageButton, viewImages_imageButton;
	private MainActivity mainActivity;
	private FilesSettingsScreen filesSettingsScreen;
	private FilesRenderingScreen filesRenderingScreen;
	private RenderOnSheepitScreen renderOnSheepitScreen;
	private ImageViewScreen imageViewScreen;

	public RenderOverview(PApplet p, int stdTs, int edgeRad, int margin, int btnSizeLarge, int btnSize, int btnSizeSmall, int dark, int light, int lighter,int lightest, int textCol, int textDark, int border,int green,int red, int blue,float textYShift, String mySavePath, String[] pictoPaths, String[] hoLiPictoPaths, String[] arrowPaths,String[] fileExplorerPaths, PFont stdFont) {
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
		this.mySavePath = mySavePath;
		this.pictoPaths = pictoPaths;
		this.stdFont = stdFont;
		mainActivity = (MainActivity) p;
		cancelRendering_ImageButton = new ImageButton(p, p.width - margin - btnSizeSmall / 2, p.height - margin - btnSizeSmall / 2, btnSizeSmall, btnSizeSmall, stdTs, margin, edgeRad, -1, textYShift, true, false, textCol, light, pictoPaths[0], "Quit render process", null);
		viewImages_imageButton = new ImageButton(p, p.width - margin*2 - btnSizeSmall / 2-btnSizeSmall, p.height - margin - btnSizeSmall / 2, btnSizeSmall, btnSizeSmall, stdTs, margin, edgeRad, -1, textYShift, true, false, textCol, light, pictoPaths[5], "Image view", null);

		String[] rFSPictoPaths = { pictoPaths[3] };
		filesSettingsScreen = new FilesSettingsScreen(p, stdTs, edgeRad, margin, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border, textYShift, rFSPictoPaths, hoLiPictoPaths, arrowPaths, stdFont);
		String[] fRSPictoPaths = { pictoPaths[4] };
		filesRenderingScreen = new FilesRenderingScreen(p, stdTs, edgeRad, margin, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border,green,red,blue, textYShift, fRSPictoPaths, hoLiPictoPaths, stdFont);
		String[] rOSPictoPaths = { pictoPaths[1], pictoPaths[2] };
		renderOnSheepitScreen = new RenderOnSheepitScreen(p, stdTs, edgeRad, margin, btnSizeLarge, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border, textYShift, rOSPictoPaths, stdFont);
		String[] iVSPictoPaths = { pictoPaths[6] };
		imageViewScreen = new ImageViewScreen(p, stdTs, edgeRad, margin, btnSizeLarge, btnSize, btnSizeSmall, dark, light, lighter,lightest, textCol, textDark, border, textYShift, iVSPictoPaths,fileExplorerPaths, stdFont);

	}

	public void render() {

		// render all ---------------------------------------
		if (renderMode == 0.1f) {
			uploadBlenderFiles();
			filesSettingsScreen.render();
		}
		if (renderMode == 0) {
			filesRenderingScreen.render();
			viewImages_imageButton.render();
		}
		if (renderMode == 1) {
			renderOnSheepitScreen.render();
			startSheepit();
		}
		if (renderMode == 2) {
			imageViewScreen.render();
		}

		cancelRendering_ImageButton.render();
		// render all ---------------------------------------

		// button handling -------------------------
		if (cancelRendering_ImageButton.getIsClicked()) {
		    if(renderMode==0.1f) {
		        mainActivity.setMode(1);
		    }
			if (renderMode == 0) {
				cancelFileRendering();
				mainActivity.setMode(1);
			}
			if (renderMode == 1) {
				cancelSheepitRendering();
				mainActivity.setMode(1);
			}
			if (renderMode == 2) {
				renderMode = 0;
			}
			cancelRendering_ImageButton.setIsClicked(false);
		}

		if (renderMode == 0) {
			if (viewImages_imageButton.getIsClicked()) {
				renderMode = 2;
				viewImages_imageButton.setIsClicked(false);
			}
		}
		// button handling -------------------------
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
			viewImages_imageButton.onMousePressed();
		}
		if (renderMode == 1) {

		}
		if (renderMode == 2) {
			imageViewScreen.onMousePressed(mouseButton);
		}

		cancelRendering_ImageButton.onMousePressed();
	}

	public void onMouseReleased(int mouseButton) {

		if (renderMode == 0.1f) {
			filesSettingsScreen.onMouseReleased(mouseButton);
		}
		if (renderMode == 0) {
			filesRenderingScreen.onMouseReleased(mouseButton);
			viewImages_imageButton.onMouseReleased();
		}
		if (renderMode == 1) {

		}
		if (renderMode == 2) {
			imageViewScreen.onMouseReleased(mouseButton);
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
		if (renderMode == 2) {
			imageViewScreen.onKeyPressed(key);
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
		if (renderMode == 2) {
			imageViewScreen.onKeyReleased(key);
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
		if (renderMode == 2) {
			imageViewScreen.onScroll(e);
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
