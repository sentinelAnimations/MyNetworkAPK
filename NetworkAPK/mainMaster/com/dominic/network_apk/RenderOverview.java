package com.dominic.network_apk;

import processing.core.PApplet;
import processing.core.PFont;

public class RenderOverview {

    private int stdTs, edgeRad, margin, btnSize, btnSizeSmall, dark, light, lighter, lightest, textCol, textDark, border;
    private float renderMode; // rendermode --> 0=render files, 0.1=files render settings, 1=render on sheepit
    private float textYShift;
    private String[] pictoPaths;
    private PFont stdFont;
    private PApplet p;
    private ImageButton cancelRendering_ImageButton;
    private MainActivity mainActivity;
    private RenderFilesSettings renderFilesSettings;
    private FilesRenderingScreen filesRenderingScreen;
    private PictogramImage sheepitRendering_PictogramImage;
    private SpriteAnimation loadingGear_SpriteAnimation;

    public RenderOverview(PApplet p, int stdTs, int edgeRad, int margin,int btnSizeLarge, int btnSize, int btnSizeSmall, int dark, int light, int lighter, int textCol, int textDark, int border, float textYShift, String[] pictoPaths, String[] hoLiPictoPaths, PFont stdFont) {
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

        renderFilesSettings = new RenderFilesSettings(p, stdTs, edgeRad, margin, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border, textYShift, pictoPaths, hoLiPictoPaths, stdFont);
        filesRenderingScreen = new FilesRenderingScreen(p, stdTs, edgeRad, margin, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border, textYShift, pictoPaths, hoLiPictoPaths, stdFont);

        sheepitRendering_PictogramImage = new PictogramImage(p, p.width/2-btnSizeLarge/2-margin/2,p.height/2, btnSizeLarge, margin, stdTs, edgeRad, textCol, textYShift, false, pictoPaths[1],"Rendering on Sheepit", null);
        loadingGear_SpriteAnimation = new SpriteAnimation(p, p.width/2+btnSizeLarge/2+margin/2, p.height/2, btnSizeLarge, btnSizeLarge, 0, 129, textCol, false, "imgs/sprites/loadingGears/", null); // endInd=129, obwohl letztes bild '0128.png' --> weil start bei '0000.png'

    }

    public void render() {

        // render all ---------------------------------------
        if (renderMode == 0.1f) {
            uploadBlenderFiles();
            renderFilesSettings.render();
        }
        if (renderMode == 0) {
            filesRenderingScreen.render();
        }
        if (renderMode == 1) {
            startSheepit();
            sheepitRendering_PictogramImage.render();
            loadingGear_SpriteAnimation.render();
        }

        cancelRendering_ImageButton.render();
        // render all ---------------------------------------
        
        if(cancelRendering_ImageButton.getIsClicked()) {
            cancelRendering();
            mainActivity.setMode(1);
            cancelRendering_ImageButton.setIsClicked(false);
        }

    }
    
    private void uploadBlenderFiles() {
        
    }
    
    private void startSheepit() {
        
    }
    
    private void cancelRendering() {
        
    }

    public void onMousePressed(int mouseButton) {

        if (renderMode == 0.1) {
            renderFilesSettings.onMousePressed(mouseButton);
        }
        if (renderMode == 0) {
            filesRenderingScreen.onMousePressed(mouseButton);
        }
        if (renderMode == 1) {

        }

        cancelRendering_ImageButton.onMousePressed();
    }

    public void onMouseReleased(int mouseButton) {

        if (renderMode == 0.1) {
            renderFilesSettings.onMouseReleased(mouseButton);
        }
        if (renderMode == 0) {
            filesRenderingScreen.onMouseReleased(mouseButton);
        }
        if (renderMode == 1) {

        }
        cancelRendering_ImageButton.onMouseReleased();
    }

    public void onKeyPressed(char key) {
        if (renderMode == 0.1) {
           renderFilesSettings.onKeyPressed(key);
        }
        if (renderMode == 0) {
            filesRenderingScreen.onKeyPressed(key);
        }
        if (renderMode == 1) {

        }

    }

    public void onKeyReleased(char key) {
        if (renderMode == 0.1) {
            renderFilesSettings.onKeyReleased(key);
        }
        if (renderMode == 0) {
            filesRenderingScreen.onKeyReleased(key);
        }
        if (renderMode == 1) {

        }
    }

    public void onScroll(float e) {
        if (renderMode == 0.1) {
            renderFilesSettings.onScroll(e);
        }
        if (renderMode == 0) {
            filesRenderingScreen.onScroll(e);
        }
        if (renderMode == 1) {

        }
    }

    public RenderFilesSettings getRenderFilesSettings() {
        return renderFilesSettings;
    }

    public void setRenderMode(float setM) {
        renderMode = setM;
    }

}
