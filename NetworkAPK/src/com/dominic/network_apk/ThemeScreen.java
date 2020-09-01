package com.dominic.network_apk;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class ThemeScreen {
    private int btnSize, btnSizeSmall, margin, stdTs, edgeRad, dark, darkest, light, lighter, lightest, border, textCol, textDark;
    private float textYShift;
    private Boolean renderFileExplorer = false;
    private String[] nodePaths1, nodePaths2, pcPaths,colorPickerTitles= {"Dark","Light","Lighter","Lightest","Borders","Text","Text dark"};
    private PFont stdFont;
    private PImage screenshot;
    private PApplet p;
    private MainActivity mainActivity;
    private ImageButton[] mainButtons;
    private ColorPicker[] colorPickers = new ColorPicker[7];
    private ImageButton restart_ImageButton;

    public ThemeScreen(PApplet p, int btnSize, int btnSizeSmall, int margin, int stdTs, int edgeRad, int dark, int darkest, int light, int lighter, int lightest, int border, int textCol, int textDark, float textYShift, String[] pictoPaths, PFont stdFont) {
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
        mainActivity = (MainActivity) p;
        mainButtons = mainActivity.getMainButtons();
        for (int i = 0; i < colorPickers.length; i++) {
            colorPickers[i] = new ColorPicker(p,p.width/(colorPickers.length+1)+p.width / (colorPickers.length+1) * i, p.height / 2, btnSize, btnSizeSmall, (int) (btnSize * 1.2f), dark, stdTs, edgeRad, margin, btnSize, btnSizeSmall, light, lighter, lightest, textCol, textYShift, false, false, true, pictoPaths[0], stdFont, null); // isParented,renderBg,stayOpen
        }
        restart_ImageButton= new ImageButton(p, p.width -margin -btnSizeSmall/2,p.height-margin-btnSizeSmall/2, btnSizeSmall, btnSizeSmall, stdTs, margin, edgeRad, -1, textYShift, true, false, textCol, light, pictoPaths[1],"Restart applikation", null);

    }

    public void render() {
        mainActivity.renderMainButtons();
        for (int i = 0; i < colorPickers.length; i++) {
        colorPickers[i].render();
        p.fill(textCol);
        p.textAlign(p.CENTER,p.CENTER);
        p.textFont(stdFont);
        p.textSize(stdTs);
        p.text(colorPickerTitles[i],colorPickers[i].getX(),colorPickers[i].getColorBarY()+colorPickers[i].getSlider().getH()+margin+stdTs/2);
        }
        restart_ImageButton.render();
        
        if(restart_ImageButton.getIsClicked()) {
            
            restart_ImageButton.setIsClicked(false);
        }
        
    }

    public void onMousePressed() {
        
        for (int i = 0; i < mainButtons.length; i++) {
            if (mainButtons[0].getClickCount() % 2 == 0 || i == 0) {
                mainButtons[i].onMousePressed();
            }
        }
        
        for (int i = 0; i < colorPickers.length; i++) {
        colorPickers[i].onMousePressed();
        }
        restart_ImageButton.onMousePressed();
    }

    public void onMouseReleased() {
        for (int i = 0; i < mainButtons.length; i++) {
            if (mainButtons[0].getClickCount() % 2 == 0 || i == 0) {
                mainButtons[i].onMouseReleased();
            }
        }
        for (int i = 0; i < colorPickers.length; i++) {
        colorPickers[i].onMoueseReleased();
        }
        restart_ImageButton.onMouseReleased();
    }

    public void onKeyReleased(char k) {
    }

    public void onScroll(float e) {

    }

}
