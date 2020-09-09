package com.dominic.network_apk;

import processing.core.PApplet;
import processing.core.PFont;

public class HomeScreenSlaves {
    private int stdTs, edgeRad, margin, btnSize, btnSizeSmall, dark, light, lighter, lightest, textCol, textDark, border;
    private float renderMode; // rendermode --> 0=render files, 0.1=files render settings, 1=render on sheepit
    private float textYShift;
    private String[] pictoPaths;
    private PFont stdFont;
    private PApplet p;
    private ImageButton[] mainButtons;
    private ImageButton cancelRendering_ImageButton;
    private MainActivity mainActivity;
    

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
    }

    public void render() {
        p.textAlign(p.CENTER,p.CENTER);
        p.fill(textCol);
        p.text("homeScreen slaves", p.width / 2, p.height / 2);
        
        if (mainActivity.getIsMaster()) {
            mainActivity.renderMainButtonsMaster();
        } else {
            mainActivity.renderMainButtonsSlave();
        }
    }

    public void onMousePressed(int mouseButton) {
        for(int i=0;i<mainButtons.length;i++) {
            mainButtons[i].onMousePressed();
        }
    }

    public void onMouseReleased(int mouseButton) {
        for(int i=0;i<mainButtons.length;i++) {
            mainButtons[i].onMouseReleased();
        }
    }

    public void onKeyPressed(char key) {
   
    }

    public void onKeyReleased(char key) {
        for(int i=0;i<mainButtons.length;i++) {
            mainButtons[i].onKeyReleased(key);
        }
    }

    public void onScroll(float e) {

    }
}
