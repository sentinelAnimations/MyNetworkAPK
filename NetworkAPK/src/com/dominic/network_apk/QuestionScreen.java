package com.dominic.network_apk;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class QuestionScreen {
	private int btnSize, btnSizeSmall, margin, stdTs, edgeRad, dark, darkest, light, lighter, lightest, border, textCol, textDark;
	private float textYShift;
	private Boolean renderFileExplorer = false;
	private String[] pictoPaths;
	private PFont stdFont;
	private PImage screenshot;
	private PApplet p;
	private MainActivity mainActivity;
	private ImageButton[] mainButtons;
	private SearchBar searchBar;
	private TextField answers_TextField;

	public QuestionScreen(PApplet p, int btnSize, int btnSizeSmall, int margin, int stdTs, int edgeRad, int dark, int darkest, int light, int lighter, int lightest, int border, int textCol, int textDark, float textYShift, String[] pictoPaths, String[] fileExplorerPictoPaths, PFont stdFont) {
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
		this.pictoPaths = pictoPaths;
		this.stdFont = stdFont;
		this.p = p;
		mainActivity = (MainActivity) p;
		mainButtons = mainActivity.getMainButtons();
		searchBar = new SearchBar(p, p.width / 2, mainButtons[0].getY() + mainButtons[0].getH() / 2 + margin + btnSizeSmall / 2 + btnSize, p.width - btnSize * 2, btnSizeSmall, edgeRad, margin, stdTs, textCol, textDark, light, textYShift, false, "Search", pictoPaths[0], stdFont, null);

		String s = new TxtStringLoader(p).getStringFromFile("textSources/questionsScreen_Answers.txt");
		
		answers_TextField = new TextField(p, p.width / 2, searchBar.getY() + (p.height - (searchBar.getY() + searchBar.getH() / 2 + margin)) / 2, searchBar.getW(), p.height - (searchBar.getY() + searchBar.getH() / 2 + margin) - btnSize, stdTs, margin, btnSizeSmall, edgeRad, dark, light, lighter, textDark, textYShift, true, false, true, s, stdFont, null);

	}

	public void render() {
		mainActivity.renderMainButtons();
		searchBar.render();
		answers_TextField.render();
	}

	public void onMousePressed() {
		for (int i = 0; i < mainButtons.length; i++) {
			if (mainButtons[0].getClickCount() % 2 == 0 || i == 0) {
				mainButtons[i].onMousePressed();
			}
		}
		searchBar.onMousePressed();
		answers_TextField.onMousePressed();
	}

	public void onMouseReleased() {
		for (int i = 0; i < mainButtons.length; i++) {
			if (mainButtons[0].getClickCount() % 2 == 0 || i == 0) {
				mainButtons[i].onMouseReleased();
			}
		}
		searchBar.onMouseReleased();
		answers_TextField.onMouseReleased();
	}

	public void onKeyPressed(char key) {
		searchBar.onKeyPressed(key);
	}

	public void onKeyReleased(char k) {
		searchBar.onKeyReleased(k);
	}

	public void onScroll(float e) {
		answers_TextField.onScroll(e);
	}

}
