package com.dominic.network_apk;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class SpreadBlender {
    private int btnSize, btnSizeSmall, margin, stdTs, edgeRad, dark, darkest, light, lighter, lightest, border, textCol, textDark;
    private float textYShift;
    private Boolean renderFileExplorer = false;
    private String[] pictoPaths;
    private PFont stdFont;
    private PImage screenshot;
    private PApplet p;
    private MainActivity mainActivity;
    private ImageButton[] mainButtons;
    private ImageButton spreadBlender_ImageButton;
    private FileExplorer fileExplorer;
    

    public SpreadBlender(PApplet p,int btnSize, int btnSizeSmall, int margin, int stdTs, int edgeRad, int dark, int darkest, int light, int lighter, int lightest, int border, int textCol, int textDark, float textYShift, String[] pictoPaths, PFont stdFont) {
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
        this.pictoPaths=pictoPaths;
        this.stdFont = stdFont;
        this.p = p;
        mainActivity = (MainActivity)p;
        mainButtons=mainActivity.getMainButtons();
        spreadBlender_ImageButton = new ImageButton(p, p.width/2,p.height/2, btnSizeSmall, btnSizeSmall, stdTs, margin, edgeRad, -1, textYShift, true, false, textCol, lighter, pictoPaths[1], "Rename Selected Folder", null);

    }
    
    public void render() {
        mainActivity.renderMainButtons();
        spreadBlender_ImageButton.render();

    }
    
    public void onMousePressed() {
        spreadBlender_ImageButton.onMousePressed();
        for (int i = 0; i < mainButtons.length; i++) {
            if (mainButtons[0].getClickCount() % 2 == 0 || i == 0) {
                mainButtons[i].onMousePressed();
            }
        }
    }
    public void onMouseReleased() {
        spreadBlender_ImageButton.onMouseReleased();
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
