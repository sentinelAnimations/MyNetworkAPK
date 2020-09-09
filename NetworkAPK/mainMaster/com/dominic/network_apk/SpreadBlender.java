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
    private ImageButton spreadBlender_ImageButton,spreadSheepit_ImageButton;
    private PathSelector spreadBlender_pathSelector,spreadSheepit_PathSelector;
    private FileInteractionHelper fileInteractionHelper;

    public SpreadBlender(PApplet p, int btnSize, int btnSizeSmall, int margin, int stdTs, int edgeRad, int dark, int darkest, int light, int lighter, int lightest, int border, int textCol, int textDark, float textYShift, String[] pictoPaths, String[] fileExplStr, PFont stdFont) {
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
        if (mainActivity.getIsMaster()) {
            mainButtons = mainActivity.getMainButtonsMaster();
        } else {
            mainButtons = mainActivity.getMainButtonsSlave();
        }
        int psW = p.width / 3;
        spreadBlender_pathSelector = new PathSelector(p, p.width / 2 - btnSizeSmall / 2, p.height / 2-btnSizeSmall/2-margin/2, psW, btnSizeSmall, edgeRad, margin, stdTs, btnSizeSmall, border, light, textCol, dark, light, lighter, textDark, textYShift, true, false, "...\\\\Folder to copy Blender from", pictoPaths[0], fileExplStr, stdFont, null);
        spreadBlender_ImageButton = new ImageButton(p, psW / 2 + margin + btnSizeSmall / 2, 0, btnSizeSmall, btnSizeSmall, stdTs, margin, edgeRad, -1, textYShift, true, true, textCol, lighter, pictoPaths[1], "Share Blender", spreadBlender_pathSelector);
        
        spreadSheepit_PathSelector= new PathSelector(p, p.width / 2 - btnSizeSmall / 2, p.height / 2+btnSizeSmall/2+margin/2, psW, btnSizeSmall, edgeRad, margin, stdTs, btnSizeSmall, border, light, textCol, dark, light, lighter, textDark, textYShift, true, false, "...\\\\Folder to copy Blender from", pictoPaths[0], fileExplStr, stdFont, null);
        spreadSheepit_ImageButton = new ImageButton(p, psW / 2 + margin + btnSizeSmall / 2, 0, btnSizeSmall, btnSizeSmall, stdTs, margin, edgeRad, -1, textYShift, true, true, textCol, lighter, pictoPaths[1], "Share Blender", spreadBlender_pathSelector);
        
        fileInteractionHelper = new FileInteractionHelper(p);
    }

    public void render() {
        if (mainActivity.getIsMaster()) {
            mainActivity.renderMainButtonsMaster();
        } else {
            mainActivity.renderMainButtonsSlave();
        }
        spreadBlender_ImageButton.render();
        spreadBlender_pathSelector.render();
        spreadSheepit_ImageButton.render();
        spreadSheepit_PathSelector.render();
        
        if (spreadBlender_ImageButton.getIsClicked()) {
            if (spreadBlender_pathSelector.getPath().length() > 0) {
                String copyToPath = mainActivity.getSettingsScreen().getPathSelectors()[2].getPath();
                if (copyToPath.charAt(copyToPath.length() - 1) != '\\' || copyToPath.charAt(copyToPath.length() - 1) != '/') {
                    copyToPath += "\\";
                }
                // String
                // copyToPath="C:\\Users\\Dominic\\OneDrive\\Dokumente\\NetworkRendering\\";
                String copyFromPath = spreadBlender_pathSelector.getPath();
                fileInteractionHelper.copyFolder(copyFromPath, copyToPath);
            }
            spreadBlender_ImageButton.setIsClicked(false);
        }

    }

    public void onMousePressed(int mouseButton) {
        spreadBlender_pathSelector.onMousePressed(mouseButton);
        if (spreadBlender_pathSelector.getFileExplorerIsOpen()) {
        } else {
            spreadBlender_ImageButton.onMousePressed();
            for (int i = 0; i < mainButtons.length; i++) {
                if (mainButtons[0].getClickCount() % 2 == 0 || i == 0) {
                    mainButtons[i].onMousePressed();
                }
            }
        }
    }

    public void onMouseReleased(int mouseButton) {
        spreadBlender_pathSelector.onMouseReleased(mouseButton);
        if (spreadBlender_pathSelector.getFileExplorerIsOpen()) {
        } else {
            spreadBlender_ImageButton.onMouseReleased();
            for (int i = 0; i < mainButtons.length; i++) {
                if (mainButtons[0].getClickCount() % 2 == 0 || i == 0) {
                    mainButtons[i].onMouseReleased();
                }
            }
        }
    }

    public void onKeyPressed(char key) {
        spreadBlender_pathSelector.onKeyPressed(key);
    }

    public void onKeyReleased(char key) {
        spreadBlender_pathSelector.onKeyReleased(key);
        ;
        if (spreadBlender_pathSelector.getFileExplorerIsOpen()) {
        } else {

        }
    }

    public void onScroll(float e) {
        spreadBlender_pathSelector.onScroll(e);
        if (spreadBlender_pathSelector.getFileExplorerIsOpen()) {
        } else {

        }
    }

}
