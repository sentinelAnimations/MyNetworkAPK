package com.dominic.network_apk;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import processing.core.PApplet;
import processing.core.PFont;

public class RenderOnSheepitScreen {

    private int stdTs, edgeRad, margin, btnSizeLarge, btnSize, btnSizeSmall, dark, light, lighter, lightest, textCol, textDark, border, red, green;
    private float textYShift;
    private Boolean isRendering = true;
    private String[] pictoPaths, hoLiPictoPaths, renderInfoStrings;
    private long curTime, prevTime;
    private int[] allPCStatus;
    private float[] listX, listW;
    private PFont stdFont;
    private PApplet p;
    private MainActivity mainActivity;
    private SpriteAnimation loadingGear_SpriteAnimation;
    private HorizontalList allPCs_HorizontalList;
    private PictogramImage[] allPCPictos;
    private ArrayList<Node> allConnectedNodes = new ArrayList<>();
    private SheepitRenderHelper sheepitRenderHelper;
    public RenderOnSheepitScreen(PApplet p, int stdTs, int edgeRad, int margin, int btnSizeLarge, int btnSize, int btnSizeSmall, int dark, int light, int lighter, int textCol, int textDark, int border, int red, int green, float textYShift, String[] pictoPaths, String[] hoLiPictoPaths, PFont stdFont) {
        this.p = p;
        this.stdTs = stdTs;
        this.edgeRad = edgeRad;
        this.margin = margin;
        this.btnSizeLarge = btnSizeLarge;
        this.btnSize = btnSize;
        this.btnSizeSmall = btnSizeSmall;
        this.dark = dark;
        this.light = light;
        this.lighter = lighter;
        this.lightest = lightest;
        this.textCol = textCol;
        this.textDark = textDark;
        this.border = border;
        this.red = red;
        this.green = green;
        this.textYShift = textYShift;
        this.pictoPaths = pictoPaths;
        this.hoLiPictoPaths = hoLiPictoPaths;
        this.stdFont = stdFont;
        mainActivity = (MainActivity) p;
        
        sheepitRenderHelper=new SheepitRenderHelper(p);
        //setupAll();

    }

    public void render() {

        // render allPCs_horizontalList --------------------
        allPCs_HorizontalList.render();
        if (allPCs_HorizontalList.getList().length > 0) {
            if (allPCs_HorizontalList.getIsShifted()) {

                listX = new float[allPCs_HorizontalList.getListX().length];
                listW = new float[allPCs_HorizontalList.getListW().length];
                listX = allPCs_HorizontalList.getListX();
                listW = allPCs_HorizontalList.getListW();

                for (int i = 0; i < allPCPictos.length; i++) {
                    allPCPictos[i].setPos((int) listX[i], (int) (allPCs_HorizontalList.getY() - listW[i] / 2 + margin * 2 + allPCPictos[i].getH() / 2));
                }
                allPCs_HorizontalList.setIsShifted(false);
            }
            for (int i = allPCs_HorizontalList.getFirstDisplayedInd(); i <= allPCs_HorizontalList.getLastDisplayedInd(); i++) {
                p.fill(lighter);
               
                p.stroke(lighter);
                p.strokeWeight(2);
                p.rect(listX[i], allPCs_HorizontalList.getY(), listW[i], allPCs_HorizontalList.getH() - margin * 2, edgeRad);
                p.strokeWeight(1);
                allPCPictos[i].render();

                p.fill(textCol);
                p.textFont(stdFont);
                p.textSize(stdTs);
                p.textAlign(p.CENTER, p.TOP);

                try {
                    p.text(renderInfoStrings[i], listX[i], allPCs_HorizontalList.getY());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // render allPCs_horizontalList --------------------
        
        // update PCView---------------------------------------------
        if (allConnectedNodes.size() > 0) {
            curTime = System.nanoTime() / 1000000000;
            if (curTime - prevTime > mainActivity.getSuperShortTimeIntervall()) {
                updateLists();
                prevTime = curTime;
            }
        }
        // update PCView---------------------------------------------
    }

    private void updateLists() {
        for (int i = 0; i < allPCs_HorizontalList.getList().length; i++) {
            Node n = allConnectedNodes.get(i);
            n.checkForSignsOfLife();
            // check render status if ok, update last log line -------------------
            allPCStatus[i] = n.getPcStatus();
            if (allPCStatus[i] < 2) {
                allPCPictos[i].setCol(green);
            } else {
                allPCPictos[i].setCol(red);
            }
            // cmd delete system32
            // check render status if ok, update last log line -------------------

            // prepare infoString and so on-----------------------------------
            String renderInfoString = "PC status: ";
            switch (allPCStatus[i]) {
            case 0:
                renderInfoString += "Responding";
                break;
            case 1:
                renderInfoString += "Rendering";
                break;
            case 2:
                renderInfoString += "Not responding";
            }
            renderInfoString += "\n";
            String pcName = n.getPcSelection_DropdownMenu().getSelectedItem();
            if (pcName != null && pcName.length() > 0) {
                renderInfoString += pcName;
            } else {
                renderInfoString += "---";
            }
            renderInfoStrings[i] = renderInfoString;
            // prepare infoString and so on-----------------------------------

        }
    }
    


    public void onMousePressed(int mouseButton) {
        allPCs_HorizontalList.onMousePressed();
    }

    public void onMouseReleased(int mouseButton) {
        allPCs_HorizontalList.onMouseReleased(mouseButton);
    }

    public void onKeyPressed(char key) {
    }

    public void onKeyReleased(char key) {

    }

    public void onScroll(float e) {
        allPCs_HorizontalList.onScroll(e);
    }

    public void setupAll() {

        int listH = (int) (btnSize * 3.5f);
        String listW = "";
        while (p.textWidth(listW) < listH) {
            listW += ".";
        }
        if (!mainActivity.getNodeEditor().getIsSetup()) {
            mainActivity.getNodeEditor().setupAll();
        }
        allConnectedNodes = mainActivity.getNodeEditor().getAllConnectedNodes();

        String[] startList = new String[allConnectedNodes.size()];
        for (int i = 0; i < startList.length; i++) {
            startList[i] = listW;
        }

        allPCs_HorizontalList = new HorizontalList(p, p.width / 2, p.height / 2, p.width - margin * 2, listH, margin, edgeRad, stdTs, (int) p.textWidth("Rendering PCs") + margin * 3 + btnSizeSmall, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border, textYShift, '\\', false, false, true, "Rendering PCs", hoLiPictoPaths, startList, stdFont, null);

        allPCPictos = new PictogramImage[allPCs_HorizontalList.getList().length];
        renderInfoStrings = new String[allPCs_HorizontalList.getList().length];
        allPCStatus = new int[allPCs_HorizontalList.getList().length];

        for (int i = 0; i < allPCPictos.length; i++) {
            renderInfoStrings[i] = "";
            allPCPictos[i] = new PictogramImage(p, margin + btnSize / 2, margin + btnSize / 2, btnSize, btnSize, margin, stdTs, edgeRad, textCol, textYShift, false, false, allConnectedNodes.get(i).getTypePicto().getPictoPath(), "", null);
        }
        
        
		mainActivity.getRenderOverview().saveHardwareToUse(allConnectedNodes);
        sheepitRenderHelper.setStartRenderingOnSheepit(true);
        if(mainActivity.getHomeScreenMaster().getCheckboxes()[0].getIsChecked()) {//useMaster
        sheepitRenderHelper.startRenderingOnSheepit(sheepitRenderHelper.getSheepitExePath());
        }
    }
    
    public SheepitRenderHelper getSheepitRenderHelper() {
    	return sheepitRenderHelper;
    }

}
