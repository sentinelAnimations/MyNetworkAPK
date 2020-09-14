package com.dominic.network_apk;

import processing.core.PApplet;
import processing.core.PFont;

public class ImageViewScreen {

	private int stdTs, edgeRad, margin, btnSize, btnSizeSmall, dark, light, lighter, lightest, textCol, textDark, border;
	private float textYShift;
	private Boolean fileExplorerIsOpen = false;
	private String[] pictoPaths;
	private PFont stdFont;
	private PApplet p;
	private MainActivity mainActivity;
	private PathSelector imageFolder_pathSelector;
	private ImageView allImgs_ImageView;

	public ImageViewScreen(PApplet p, int stdTs, int edgeRad, int margin, int btnSizeLarge, int btnSize, int btnSizeSmall, int dark, int light, int lighter, int lightest, int textCol, int textDark, int border, float textYShift, String[] pictoPaths, String[] fileExplorerPaths, PFont stdFont) {
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
		this.pictoPaths = pictoPaths;
		this.stdFont = stdFont;
		mainActivity = (MainActivity) p;
		
		int  widgetsW=p.width-margin*4-btnSizeSmall*4;
		imageFolder_pathSelector = new PathSelector(p, p.width / 2, btnSizeSmall / 2 + margin, widgetsW, btnSizeSmall, edgeRad, margin, stdTs, btnSizeSmall, border, light, textCol, dark, light, lighter, textDark, textYShift, true, false, "...\\\\Image Folder", pictoPaths[0], fileExplorerPaths, stdFont, null);
		allImgs_ImageView = new ImageView(p,p.width/2,p.height/2+imageFolder_pathSelector.getH()/2+margin/2,widgetsW,p.height-imageFolder_pathSelector.getH()-margin*3, stdTs, edgeRad, margin, btnSizeLarge, btnSize, btnSizeSmall, light,lighter, textCol, textDark, border, textYShift, false, stdFont, null);

	}

	public void render() {
		fileExplorerIsOpen = imageFolder_pathSelector.getFileExplorerIsOpen();
		if (fileExplorerIsOpen == false) {
			allImgs_ImageView.render();
		}
		imageFolder_pathSelector.render();
	}

	public void onMousePressed(int mouseButton) {
		if (fileExplorerIsOpen == false) {
			allImgs_ImageView.onMousePressed(mouseButton);
		}
		imageFolder_pathSelector.onMousePressed(mouseButton);
	}

	public void onMouseReleased(int mouseButton) {
		if (fileExplorerIsOpen == false) {
			allImgs_ImageView.onMouseReleased(mouseButton);
		}
		imageFolder_pathSelector.onMouseReleased(mouseButton);
	}

	public void onKeyPressed(char key) {
		imageFolder_pathSelector.onKeyPressed(key);
	}

	public void onKeyReleased(char key) {
		imageFolder_pathSelector.onKeyReleased(key);
	}

	public void onScroll(float e) {
		if (fileExplorerIsOpen == false) {
			allImgs_ImageView.onScroll(e);
		}
		imageFolder_pathSelector.onScroll(e);
	}

}
