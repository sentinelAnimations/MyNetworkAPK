package com.dominic.network_apk;

import org.json.simple.JSONObject;

import processing.core.PApplet;
import processing.core.PFont;

public class HomeScreenSlaves {
    private int stdTs, edgeRad, margin, btnSize, btnSizeSmall, dark, light, lighter, lightest, textCol, textDark, border;
    private float renderMode; // rendermode --> 0=render files, 1=render on
                              // sheepit,2=sleeping
    private float textYShift;
    private String pathToCloud,pcAlias;
    private String[] pictoPaths;
    private PFont stdFont;
    private PApplet p;
    private ImageButton[] mainButtons;
    private ImageButton cancelRendering_ImageButton;
    private MainActivity mainActivity;
    private PictogramImage fileRendering_PictogramImage,sheepitRendering_PictogramImage,sleeping_PictogramImage;
    private TimeField timeField;
    private JsonHelper jsonHelper;

    public HomeScreenSlaves(PApplet p, int stdTs, int edgeRad, int margin, int btnSizeLarge, int btnSize, int btnSizeSmall, int dark, int light, int lighter, int textCol, int textDark, int border, float textYShift, String[] pictoPaths, PFont stdFont) {
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

        if (mainActivity.getIsMaster()) {
            mainButtons = mainActivity.getMainButtonsMaster();
        } else {
            mainButtons = mainActivity.getMainButtonsSlave();
        }
        
        sheepitRendering_PictogramImage = new PictogramImage(p, p.width / 2, p.height / 2, btnSizeLarge,btnSizeLarge, margin, stdTs, edgeRad, textCol, textYShift, false,false, pictoPaths[0], "Rendering on Sheepit", null);
        sleeping_PictogramImage = new PictogramImage(p, p.width / 2, p.height / 2, btnSizeLarge,btnSizeLarge, margin, stdTs, edgeRad, textCol, textYShift, false, false,pictoPaths[1], "sleeping", null);
        fileRendering_PictogramImage = new PictogramImage(p, p.width / 2, p.height / 2, btnSizeLarge,btnSizeLarge, margin, stdTs, edgeRad, textCol, textYShift, false,false, pictoPaths[2], "Rendering file", null);
        timeField = new TimeField(p, margin,p.height-btnSizeSmall/2-margin, stdTs, margin, edgeRad, textCol, light, false, false,"Timestamp: ","", stdFont, null);
        timeField.setPos(timeField.getW()/2+margin, timeField.getY());
        p.println("now setting paths");
        pathToCloud=mainActivity.getPathToCloud();
        pcAlias=mainActivity.getPCName();
        while(p.str(pathToCloud.charAt(pathToCloud.length()-1))=="\\") {
            pathToCloud=pathToCloud.substring(0,pathToCloud.length()-1);
        }
        if(pathToCloud==null) {
            pathToCloud="";
        }
        if(pcAlias==null) {
            pcAlias="";
        }
        jsonHelper=new JsonHelper(p);
        p.println(pathToCloud+"\\"+pcAlias);
    }

    public void render() {
        if (mainActivity.getIsMaster()) {
            mainActivity.renderMainButtonsMaster();
        } else {
            mainActivity.renderMainButtonsSlave();
        }
        
        if(renderMode==0) {
            fileRendering_PictogramImage.render();
        }
        if(renderMode==1) {
            sheepitRendering_PictogramImage.render();
        }
        if(renderMode==2) {
           sleeping_PictogramImage.render(); 
        }
        timeField.render();
        
        if(p.frameCount%50==0) {
            logData();
        }
    }
    private void logData() {
        long curTime = System.nanoTime() / 1000000000;

        jsonHelper.clearArray();
        JSONObject settingsDetails = new JSONObject();
        JSONObject settingsObject = new JSONObject();
        
        settingsDetails.put("logTime",curTime);
        settingsObject.put("Settings", settingsDetails);
        jsonHelper.appendObjectToArray(settingsObject);
        jsonHelper.writeData(pathToCloud+"\\"+pcAlias);

    }

    public void onMousePressed(int mouseButton) {
        for (int i = 0; i < mainButtons.length; i++) {
            mainButtons[i].onMousePressed();
        }
    }

    public void onMouseReleased(int mouseButton) {
        for (int i = 0; i < mainButtons.length; i++) {
            mainButtons[i].onMouseReleased();
        }
    }

    public void onKeyPressed(char key) {

    }

    public void onKeyReleased(char key) {
        for (int i = 0; i < mainButtons.length; i++) {
            mainButtons[i].onKeyReleased(key);
        }
    }

    public void onScroll(float e) {

    }
}
