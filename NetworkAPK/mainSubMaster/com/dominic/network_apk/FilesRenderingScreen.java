package com.dominic.network_apk;


import processing.core.PApplet;
import processing.core.PFont;

public class FilesRenderingScreen {

    private int stdTs, edgeRad, margin, btnSize, btnSizeSmall, dark, light, lighter, lightest, textCol, textDark, border;
    private float textYShift;
    private Boolean[] renderAnimation,renderStillFrame;
    private int[] startFrames,endFrames,stillFrames;
    private String[] pictoPaths;
    private PFont stdFont;
    private PApplet p;
    private MainActivity mainActivity;
    private HorizontalList allFiles_HorizontalList;
    private LogBar logBar;

    public FilesRenderingScreen(PApplet p, int stdTs, int edgeRad, int margin, int btnSize, int btnSizeSmall, int dark, int light, int lighter, int textCol, int textDark, int border, float textYShift, String[] pictoPaths, String[] hoLiPictoPaths, PFont stdFont) {
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

        String[] startList= {};
        allFiles_HorizontalList = new HorizontalList(p, p.width/2, p.height/2-btnSize/2-margin, p.width-margin*2, btnSizeSmall + margin * 2, margin, edgeRad, stdTs, (int) p.textWidth("Files to render") + margin * 3 + btnSizeSmall, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border, textYShift, '\\', false, false, true, "Files to render", hoLiPictoPaths, startList, stdFont, null);
        logBar=new LogBar(p, 0,allFiles_HorizontalList.getH()/2+margin*3+btnSizeSmall/2, allFiles_HorizontalList.getW(), btnSizeSmall+margin*2, stdTs, edgeRad, margin, btnSizeSmall, dark, light, lighter, textCol, textDark, border, true, textYShift, pictoPaths[0], stdFont, allFiles_HorizontalList);
        logBar.setText("aslkdjföasljdflasdjflkjalsökjdf");
    }

    public void render() {
        allFiles_HorizontalList.render();
        logBar.render();
    }

    public void onMousePressed(int mouseButton) {
        allFiles_HorizontalList.onMousePressed();
    }

    public void onMouseReleased(int mouseButton) {
        allFiles_HorizontalList.onMouseReleased(mouseButton);
    }

    public void onKeyPressed(char key) {
        
    }

    public void onKeyReleased(char key) {

    }

    public void onScroll(float e) {
        allFiles_HorizontalList.onScroll(e);
    }
    
    public HorizontalList getHorizontalList() {
        return allFiles_HorizontalList;
    }

    public void setFileList(String[] l) {
    	allFiles_HorizontalList.setList(l);
    }
    public void setStartupVals() {
    	renderAnimation=mainActivity.getRenderOverview().getRenderFilesSettings().getRenderAnimation();
    	renderStillFrame=mainActivity.getRenderOverview().getRenderFilesSettings().getRenderStillFrame();
    	startFrames=mainActivity.getRenderOverview().getRenderFilesSettings().getStartFrames();
    	endFrames=mainActivity.getRenderOverview().getRenderFilesSettings().getEndFrames();
    	stillFrames=mainActivity.getRenderOverview().getRenderFilesSettings().getStilFrames();
    }
    
}


