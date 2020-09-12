package com.dominic.network_apk;

import org.json.simple.JSONArray;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PVector;

public class LoadingScreen {
    private int btnSize, margin, stdTs, btnSizeSmall, edgeRad, titleTs, subtitleTs, dark, light, lighter, textCol, textDark;
    private float textYShift;
    private boolean firstSetup = true, initializedClasses = false;
    private String APKDescription, APKName, loadingStatus = "Loading Data", mySavePath;
    private PApplet p;
    private PImage img;
    private PVector c;
    private PFont stdFont;
    private TextField tf;
    private SpriteAnimation loadingGearSprite;
    private JsonHelper jHelper;
    private MainActivity mainActivity;

    public LoadingScreen(PApplet p, int btnSize, int margin, int stdTs, int titleTs, int subtitleTs, int btnSizeSmall, int edgeRad, int dark, int textCol, int textDark, int light, int lighter, float textYShift, String APKName, String APKDescription, String imgPath, String mySavePath, PFont stdFont) {
        this.p = p;
        this.btnSize = btnSize;
        this.margin = margin;
        this.stdTs = stdTs;
        this.btnSizeSmall = btnSizeSmall;
        this.edgeRad = edgeRad;
        this.titleTs = titleTs;
        this.subtitleTs = subtitleTs;
        this.dark = dark;
        this.light = light;
        this.lighter = lighter;
        this.textCol = textCol;
        this.textDark = textDark;
        this.textYShift = textYShift;
        this.APKDescription = APKDescription;
        this.APKName = APKName;
        this.mySavePath = mySavePath;
        this.stdFont = stdFont;
        img = p.loadImage(imgPath);
        img.resize(p.width, p.height);
        c = new PVector(p.width / 2, p.height / 2);

        String s = new TxtStringLoader(p).getStringFromFile("textSources/loadingScreen_softwareDescriptions.txt");

        tf = new TextField(p, p.width / 8, p.height / 2, p.width / 4 - margin * 4 - btnSizeSmall / 2, p.height / 2, stdTs, margin, btnSizeSmall, edgeRad, dark, light, lighter, textDark, textYShift, true, false, false, s, stdFont, null);
        loadingGearSprite = new SpriteAnimation(p, margin * 2 + btnSize / 2, p.height - p.height / 8, btnSize, btnSize, 0, 129, textCol, false, "imgs/sprites/loadingGears/", null); // endInd=129, obwohl letztes bild '0128.png' --> weil start bei '0000.png'
        mainActivity = (MainActivity) p;
        jHelper = new JsonHelper(p);

        Thread initializeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONArray loadedSettingsData = jHelper.getData(mySavePath);
                int selectedInd=0;
                if (loadedSettingsData.isEmpty()) {
                } else {
                    JsonObject jsonObject = new JsonParser().parse(loadedSettingsData.get(0).toString()).getAsJsonObject();
                    selectedInd = Integer.parseInt(jsonObject.getAsJsonObject("Settings").get("masterOrSlave_dropdown_selectedInd").getAsString());
                    String comPath =jsonObject.getAsJsonObject("Settings").get("pathSelector2").getAsString();

                    mainActivity.setMasterComandPath(comPath);
                }
               
                switch (selectedInd) {
                case 0:
                    mainActivity.setIsMaster(true);
                    mainActivity.initializeClassInstancesMaster();
                    break;
                case 1:
                    mainActivity.setIsMaster(false);
                    mainActivity.initializeClassInstancesSlave();
                    break;
                }

                initializedClasses = true;
            }
        });
        initializeThread.start();

    }

    public void render() {
        if (initializedClasses) {
            /*
             * p.background(dark); mainActivity.getNodeEditor().render();
             * p.background(dark);
             */
            Thread loadDataThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    loadData();
                }
            });
            loadDataThread.start();
        }

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

    private void loadData() {
        // load settings info, if not available, goto settingsPage----------------------
        JSONArray loadedSettingsData = jHelper.getData(mySavePath);
        if (loadedSettingsData.isEmpty()) {
            if (mainActivity.getIsMaster()) {
                mainActivity.setMode(3);
            } else {
                mainActivity.setMode(0);
            }
        } else {
            mainActivity.setMode(1);
            firstSetup = false;
        }
        // load settings info, if not available, goto settingsPage----------------------

    }

    public Boolean getIsFirstSetup() {
        return firstSetup;
    }

    public Boolean getInstanciatedClasses() {
        return initializedClasses;
    }

    public void setIsFirstSetup(Boolean state) {
        firstSetup = state;
    }

}