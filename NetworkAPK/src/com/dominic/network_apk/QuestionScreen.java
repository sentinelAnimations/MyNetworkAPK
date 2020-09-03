package com.dominic.network_apk;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class QuestionScreen {
    private int btnSize, btnSizeSmall, margin, stdTs, edgeRad, dark, darkest, light, lighter, lightest, border, textCol, textDark, markerCol;
    private float textYShift;
    private Boolean isSearching = false;
    private String[] pictoPaths;
    private PFont stdFont;
    private PImage screenshot;
    private PApplet p;
    private ArrayList<Integer> foundStringLineIndex = new ArrayList<>();
    private MainActivity mainActivity;
    private ImageButton[] mainButtons;
    private SearchBar searchBar;
    private TextField answers_TextField;

    public QuestionScreen(PApplet p, int btnSize, int btnSizeSmall, int margin, int stdTs, int edgeRad, int dark, int darkest, int light, int lighter, int lightest, int border, int textCol, int textDark, int markerCol, float textYShift, String[] pictoPaths, String[] fileExplorerPictoPaths, PFont stdFont) {
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
        this.markerCol = markerCol;
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

        if (searchBar.getButton().getIsClicked()) {
            String etStr = searchBar.getEditText().getStrList().get(0);
            if (etStr.length() > 0) {
                searchForString(etStr.toUpperCase(), answers_TextField.getText().toUpperCase());
            }
            searchBar.getButton().setIsClicked(false);
        }
        if (isSearching) {
            p.fill(255, 0, 0);
            p.ellipse(p.width / 2, p.height / 2, 10, 10);
        } else {
            for (int i = 0; i < foundStringLineIndex.size(); i++) {

                //float markerY = answers_TextField.getYPosByLineIndex(foundStringLineIndex.get(i)) + answers_TextField.getTs() * textYShift;
                float markerY2 = p.map(answers_TextField.getYPosByLineIndexUnshifted(foundStringLineIndex.get(i)), 0, answers_TextField.getTextH(), 0, answers_TextField.getH()) + answers_TextField.getY()-answers_TextField.getH()/2;
                /*if (markerY > answers_TextField.getY() - answers_TextField.getH() / 2 && markerY < answers_TextField.getY() + answers_TextField.getH() / 2) {
                    p.fill(markerCol);
                    p.rect(answers_TextField.getX() - answers_TextField.getW() / 2 - stdTs / 2, markerY, stdTs / 2, stdTs / 2, edgeRad);
                }*/
                int dp=0;
                if(answers_TextField.getSlider().getW()>answers_TextField.getSlider().getH()) {
                    dp=answers_TextField.getSlider().getH();
                }else {
                    dp=answers_TextField.getSlider().getW();
                }
                p.fill(markerCol);
                p.rect(answers_TextField.getSlider().getX(), markerY2,dp,dp*2, edgeRad);
                
            }
        }
        answers_TextField.getSlider().renderHandle();
    }

    private void searchForString(String searchStr, String sourceStr) {
        foundStringLineIndex.clear();
        Thread searchThread = new Thread(new Runnable() {
            @Override
            public void run() {
                isSearching = true;
                String[] splitStr = p.split(sourceStr, "\n");
                for (int i = 0; i < splitStr.length; i++) {
                    String[] m1 = p.match(splitStr[i], searchStr);
                    if (m1 != null) {
                        foundStringLineIndex.add(i);
                    }
                }
                isSearching = false;
            }
        });
        searchThread.start();
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
