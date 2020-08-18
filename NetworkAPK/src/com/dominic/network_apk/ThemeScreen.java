package com.dominic.network_apk;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class ThemeScreen {
    private int btnSize, btnSizeSmall, margin, stdTs, edgeRad, dark, darkest, light, lighter, lightest, border, textCol, textDark;
    private float textYShift;
    private Boolean renderFileExplorer = false;
    private String[] nodePaths1, nodePaths2, pcPaths;
    private PFont stdFont;
    private PImage screenshot;
    private PApplet p;
    private MainActivity mainActivity;
    private ImageButton[] mainButtons;
    private ColorPicker colorPicker;
    

    public ThemeScreen(PApplet p,int btnSize, int btnSizeSmall, int margin, int stdTs, int edgeRad, int dark, int darkest, int light, int lighter, int lightest, int border, int textCol, int textDark, float textYShift, String[] pictoPaths, PFont stdFont) {
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
        this.nodePaths1 = nodePaths1;
        this.nodePaths2 = nodePaths2;
        this.stdFont = stdFont;
        this.p = p;
        mainActivity = (MainActivity)p;
        mainButtons=mainActivity.getMainButtons();
        colorPicker=new ColorPicker(p, p.width/2, p.height/2, btnSize, btnSizeSmall, btnSize*2, darkest, stdTs, edgeRad, 1, lightest, lighter, textCol, textYShift, false, stdFont, null);
    }
    
    public void render() {
        mainActivity.renderMainButtons();
        colorPicker.render();
    }
    
    public void onMousePressed() {
        for (int i = 0; i < mainButtons.length; i++) {
            if (mainButtons[0].getClickCount() % 2 == 0 || i == 0) {
                mainButtons[i].onMousePressed();
            }
        }
    }
    public void onMouseReleased() {
        for (int i = 0; i < mainButtons.length; i++) {
            if (mainButtons[0].getClickCount() % 2 == 0 || i == 0) {
                mainButtons[i].onMouseReleased();
            }
        }
    }
    
    public void onKeyReleased(char k) {
    }
    
    public void onScroll(float e) {
        
    }
    

}


