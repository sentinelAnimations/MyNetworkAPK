package com.dominic.network_apk;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;

public class ImageViewScreen {

	private int stdTs, edgeRad, margin, btnSize, btnSizeSmall, dark, light, lighter, lightest, textCol, textDark, border, green, foundSearchResults = 0;
	private float textYShift;
	private Boolean fileExplorerIsOpen = false, prevFileExplorerIsOpen = false, startedSearching = false;
	private String[] pictoPaths;
	private PFont stdFont;
	private PApplet p;
	private Boolean[] searchResultList;
	private MainActivity mainActivity;
	private PathSelector imageFolder_pathSelector;
	private ImageView allImgs_ImageView;
	private SearchBar searchBar;

	public ImageViewScreen(PApplet p, int stdTs, int edgeRad, int margin, int btnSizeLarge, int btnSize, int btnSizeSmall, int dark, int light, int lighter, int lightest, int textCol, int textDark, int border, int green, float textYShift, String[] pictoPaths, String[] fileExplorerPaths, PFont stdFont) {
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
		this.green = green;
		this.textYShift = textYShift;
		this.pictoPaths = pictoPaths;
		this.stdFont = stdFont;
		mainActivity = (MainActivity) p;

		int widgetsW = p.width - margin * 4 - btnSizeSmall * 4;
		allImgs_ImageView = new ImageView(p, p.width / 2, p.height / 2 + btnSizeSmall / 2 + margin / 2, widgetsW, p.height - btnSizeSmall - margin * 3, stdTs, edgeRad, margin, btnSizeLarge, btnSize, btnSizeSmall,dark, light, lighter,lightest, textCol, textDark, border, textYShift, false, stdFont, null);
		imageFolder_pathSelector = new PathSelector(p, allImgs_ImageView.getX() - allImgs_ImageView.getW() / 2 - margin / 2 + widgetsW / 4, btnSizeSmall / 2 + margin, widgetsW / 2 - margin, btnSizeSmall, edgeRad, margin, stdTs, btnSizeSmall, border, light, textCol, dark, light, lighter, textDark, textYShift, true, false, "...\\\\Image Folder", pictoPaths[0], fileExplorerPaths, stdFont, null);
		searchBar = new SearchBar(p, allImgs_ImageView.getX() + allImgs_ImageView.getW() / 2 + margin / 2 - widgetsW / 4, btnSizeSmall / 2 + margin, widgetsW / 2 - margin, btnSizeSmall, edgeRad, margin, stdTs, textCol, textDark, light, textYShift, false, "Search", pictoPaths[1], stdFont, null);

	}

	public void render() {
		fileExplorerIsOpen = imageFolder_pathSelector.getFileExplorerIsOpen();

		if (fileExplorerIsOpen != prevFileExplorerIsOpen) {
			if (fileExplorerIsOpen == false) {
				allImgs_ImageView.setPath(imageFolder_pathSelector.getPath());
				startedSearching=false;
				foundSearchResults=0;
			}
		}

		if (fileExplorerIsOpen == false) {
			allImgs_ImageView.render();
			searchBar.render();
			if (searchBar.getButton().getIsClicked()) {
				if (allImgs_ImageView.getIsLoaded()) {
					if (searchBar.getEditText().getStrList().get(0).length() > 0 && allImgs_ImageView.getAllImgsList().size() > 0) {
						String[] sList = new String[allImgs_ImageView.getAllImgsList().size()];
						for (int i = 0; i < sList.length; i++) {
							sList[i] = (String) allImgs_ImageView.getAllImgsList().get(i);
						}
						searchForString(searchBar.getEditText().getStrList().get(0), sList);
					}
				}
				
				searchBar.getButton().setIsClicked(false);
			}
			if (startedSearching) {
				String foundResStr = "Found " + foundSearchResults + " results";
				p.fill(lighter);
				p.stroke(lighter);
				p.rect(allImgs_ImageView.getX(), allImgs_ImageView.getY() - allImgs_ImageView.getH() / 2 + stdTs / 2 + margin * 2, p.textWidth(foundResStr) + margin * 2, stdTs + margin * 2, edgeRad);
				p.fill(textCol);
				p.textFont(stdFont);
				p.textAlign(p.CENTER, p.CENTER);
				p.textSize(stdTs);
				p.text(foundResStr, allImgs_ImageView.getX(), allImgs_ImageView.getY() - allImgs_ImageView.getH() / 2 + stdTs / 2 + margin * 2);
			}
		}
		imageFolder_pathSelector.render();
		prevFileExplorerIsOpen = fileExplorerIsOpen;
	}

	private void searchForString(String searchStr, String[] searchList) {
		startedSearching = true;
		foundSearchResults = 0;
		searchResultList = new Boolean[searchList.length];
		int[] borderCols = new int[searchList.length];
		for (int i = 0; i < searchList.length; i++) {
			String[] m1 = p.match(searchList[i].toUpperCase(), searchStr.toUpperCase());
			if (m1 != null) {
				searchResultList[i] = true;
				borderCols[i] = green;
				foundSearchResults++;
			} else {
				searchResultList[i] = false;
				borderCols[i] = lighter;
			}
		}
		p.println(borderCols);
		allImgs_ImageView.setBorderCols(borderCols);
	}

	public void onMousePressed(int mouseButton) {
		if (fileExplorerIsOpen == false) {
			allImgs_ImageView.onMousePressed(mouseButton);
			searchBar.onMousePressed();
		}
		imageFolder_pathSelector.onMousePressed(mouseButton);
	}

	public void onMouseReleased(int mouseButton) {
		if (fileExplorerIsOpen == false) {
			allImgs_ImageView.onMouseReleased(mouseButton);
			searchBar.onMouseReleased();
		}
		imageFolder_pathSelector.onMouseReleased(mouseButton);
	}

	public void onKeyPressed(char key) {
		if (fileExplorerIsOpen == false) {
			searchBar.onKeyPressed(key);
		}
		imageFolder_pathSelector.onKeyPressed(key);
	}

	public void onKeyReleased(char key) {
		if (fileExplorerIsOpen == false) {
			searchBar.onKeyReleased(key);
			allImgs_ImageView.onKeyReleased(key);
		}
		imageFolder_pathSelector.onKeyReleased(key);
	}

	public void onScroll(float e) {
		if (fileExplorerIsOpen == false) {
			allImgs_ImageView.onScroll(e);
		}
		imageFolder_pathSelector.onScroll(e);
	}

	public PathSelector getImageView_PathSelector() {
		return imageFolder_pathSelector;
	}
	
	public void setPath(String setPath) {
	    allImgs_ImageView.setPath(setPath);
	    imageFolder_pathSelector.setPath(setPath,false);
	}

}
