package com.dominic.network_apk;

import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool.ManagedBlocker;

import processing.core.PApplet;
import processing.core.PFont;

public class FilesSettingsScreen {

    private int stdTs, edgeRad, margin, btnSize, btnSizeSmall, dark, light, lighter, lightest, textCol, textDark, border, onceOnStartup = 0, prevSelectedListInd;
    private float textYShift;
    private Boolean[] renderAnimation, renderStillFrame;
    private int[] startFrame, endFrame, stillFrame;
    private String[] pictoPaths;
    private PFont stdFont;
    private PApplet p;
    private MainActivity mainActivity;
    private ImageButton startRendering_imageButon;
    private HorizontalList allFiles_HorizontalList;
    private CounterArea startFrame_counterArea, endFrame_counterArea, stillFrame_counterArea;
    private Checkbox[] settings_checkboxes = new Checkbox[2];
    private ArrayList<MakeToast> makeToasts = new ArrayList<MakeToast>();

    public FilesSettingsScreen(PApplet p, int stdTs, int edgeRad, int margin, int btnSize, int btnSizeSmall, int dark, int light, int lighter, int textCol, int textDark, int border, float textYShift, String[] pictoPaths, String[] hoLiPictoPaths, String[] arrowPaths, PFont stdFont) {
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
        String[] startList = {};
        allFiles_HorizontalList = new HorizontalList(p, p.width / 2, p.height / 2 - btnSize / 2 - margin, p.width - margin * 2, btnSizeSmall + margin * 2, margin, edgeRad, stdTs, (int) p.textWidth("Files to render") + margin * 3 + btnSizeSmall, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border, textYShift, '\\', false, true, false, "Files to render", hoLiPictoPaths, startList, stdFont, null);

        settings_checkboxes[0] = new Checkbox(p, -allFiles_HorizontalList.getW() / 2 + btnSizeSmall / 2 - margin, allFiles_HorizontalList.getH() / 2 + margin + btnSizeSmall / 2, btnSizeSmall, btnSizeSmall, btnSizeSmall, edgeRad, margin, stdTs, light, light, border, textCol, textYShift, true, false, "", "Render Animation", pictoPaths[0], stdFont, allFiles_HorizontalList);

        int sfW = (int) p.width / 8;
        int sfX = (int) (settings_checkboxes[0].getBoxDim() / 2 + margin * 2 + sfW / 2);
        startFrame_counterArea = new CounterArea(p, sfX, 0, sfW, btnSizeSmall, edgeRad, margin, stdTs, 0, 1000000000, 0, light, lighter, textCol, textYShift, true, "Startframe", arrowPaths, stdFont, settings_checkboxes[0]);
        endFrame_counterArea = new CounterArea(p, sfX + sfW + margin, 0, sfW, btnSizeSmall, edgeRad, margin, stdTs, 0, 1000000000, 0, light, lighter, textCol, textYShift, true, "Endframe", arrowPaths, stdFont, settings_checkboxes[0]);
        endFrame_counterArea.getParentPos();
        settings_checkboxes[1] = new Checkbox(p, endFrame_counterArea.getW() / 2 + margin * 2 + btnSizeSmall / 2, 0, btnSizeSmall, btnSizeSmall, btnSizeSmall, edgeRad, margin, stdTs, light, light, border, textCol, textYShift, true, false, "", "Render still frame", pictoPaths[0], stdFont, endFrame_counterArea);

        sfX = (int) (settings_checkboxes[1].getBoxDim() / 2 + margin * 2 + sfW / 2);
        stillFrame_counterArea = new CounterArea(p, sfX, 0, sfW, btnSizeSmall, edgeRad, margin, stdTs, 0, 1000000000, 0, light, lighter, textCol, textYShift, true, "Still frame", arrowPaths, stdFont, settings_checkboxes[1]);

        prevSelectedListInd = allFiles_HorizontalList.getSelectedInd();

    }

    public void render() {
        if (onceOnStartup == 0) {
            startRendering_imageButon = new ImageButton(p, -margin - btnSizeSmall, 0, btnSizeSmall, btnSizeSmall, stdTs, margin, edgeRad, -1, textYShift, true, true, textCol, light, pictoPaths[0], "Save settings and start render process", mainActivity.getRenderOverview().getCancelImageButton());
            onceOnStartup++;
        }
        if (allFiles_HorizontalList.getList().length > 0) {

            // render all ----------------------------------------
            int fillUpBarW = allFiles_HorizontalList.getW() - (stillFrame_counterArea.getX() + stillFrame_counterArea.getW() / 2);
            int fillUpBarX = (stillFrame_counterArea.getX() + stillFrame_counterArea.getW() / 2) + margin + fillUpBarW / 2;
            p.fill(light);
            p.stroke(light);
            p.rect(fillUpBarX, settings_checkboxes[0].getBoxY(), fillUpBarW, settings_checkboxes[0].getH(), edgeRad);
            p.fill(textCol);
            p.textFont(stdFont);
            p.textSize(stdTs);
            p.textAlign(p.CENTER, p.CENTER);
            String textToSplit = allFiles_HorizontalList.getList()[allFiles_HorizontalList.getSelectedInd()];
            String[] splitStr = p.split(textToSplit, "\\");
            String displT = "";
            if (splitStr.length > 0) {
                displT = splitStr[splitStr.length - 1];
            } else {
                displT = textToSplit;
            }
            p.text("Render settings for: " + displT, fillUpBarX, settings_checkboxes[0].getBoxY());

           
            stillFrame_counterArea.render();
            allFiles_HorizontalList.render();
            startRendering_imageButon.render();
            for (int i = 0; i < settings_checkboxes.length; i++) {
                settings_checkboxes[i].render();
            }
            endFrame_counterArea.render();
            startFrame_counterArea.render();

            // render toasts -----------------------------------
            for (int i = 0; i < makeToasts.size(); i++) {
                MakeToast m = makeToasts.get(i);
                if (m.remove) {
                    makeToasts.remove(i);
                } else {
                    m.render();
                }
            }
            // render toasts -----------------------------------
            // render all ----------------------------------------

            // calculate all ----------------------------------------
            if (allFiles_HorizontalList.getSelectedInd() != prevSelectedListInd) {

                writeToArray();
                updateWidgets();
              

            }
        }
        if (startRendering_imageButon.getIsClicked()) {
            writeToArray();

            Boolean isRenderable = true;
            String errorMessage = "";

            for (int i = 0; i < renderAnimation.length; i++) {

                if (renderAnimation[i] == true && renderStillFrame[i] == true) {
                    if (errorMessage.length() > 0) {
                        errorMessage += " | ";
                    }
                    errorMessage += "Can't render still frame AND animation";

                    isRenderable = false;
                }

                if (renderAnimation[i] == false && renderStillFrame[i] == false) {
                    if (errorMessage.length() > 0) {
                        errorMessage += " | ";
                    }
                    errorMessage += "Select either animation or still frame";

                    isRenderable = false;
                }
                if (renderAnimation[i] == true) {
                    if (endFrame[i] < startFrame[i]) {
                        if (errorMessage.length() > 0) {
                            errorMessage += " | ";
                        }
                        isRenderable = false;
                        errorMessage += "Can't render negative frame range";
                    }
                }
            }

            if (isRenderable) {
                mainActivity.getRenderOverview().setRenderMode(0);
                mainActivity.getRenderOverview().getFilesRenderingScreen().setStartupVals();
            } else {
                makeToasts.add(new MakeToast(p, p.width / 2, p.height - stdTs * 2, stdTs, margin, edgeRad, errorMessage.length() * 2, light, textCol, textYShift, false, errorMessage, stdFont, null));
            }
            startRendering_imageButon.setIsClicked(false);

        }

        prevSelectedListInd = allFiles_HorizontalList.getSelectedInd();
        // calculate all ----------------------------------------

    }

    private void writeToArray() {
        try {
            renderAnimation[prevSelectedListInd] = settings_checkboxes[0].getIsChecked();
            renderStillFrame[prevSelectedListInd] = settings_checkboxes[1].getIsChecked();
            startFrame[prevSelectedListInd] = startFrame_counterArea.getCount();
            endFrame[prevSelectedListInd] = endFrame_counterArea.getCount();
            stillFrame[prevSelectedListInd] = stillFrame_counterArea.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void updateWidgets() {
        int curInd = allFiles_HorizontalList.getSelectedInd();
        settings_checkboxes[0].setIsChecked(renderAnimation[curInd]);
        settings_checkboxes[1].setIsChecked(renderStillFrame[curInd]);

        startFrame_counterArea.setCount(startFrame[curInd]);
        endFrame_counterArea.setCount(endFrame[curInd]);
        stillFrame_counterArea.setCount(stillFrame[curInd]);
    }

    public void onMousePressed(int mouseButton) {
        allFiles_HorizontalList.onMousePressed();
        startRendering_imageButon.onMousePressed();
        startFrame_counterArea.onMousePressed();
        endFrame_counterArea.onMousePressed();
        stillFrame_counterArea.onMousePressed();
    }

    public void onMouseReleased(int mouseButton) {
        allFiles_HorizontalList.onMouseReleased(mouseButton);
        startRendering_imageButon.onMouseReleased();
        startFrame_counterArea.onMouseReleased();
        endFrame_counterArea.onMouseReleased();
        stillFrame_counterArea.onMouseReleased();
        for (int i = 0; i < settings_checkboxes.length; i++) {
            settings_checkboxes[i].onMouseReleased();
        }
    }

    public void onKeyPressed(char key) {

    }

    public void onKeyReleased(char key) {
        if (key == p.DELETE) {
            String[] hoLi1 = allFiles_HorizontalList.getList();
            String[] newHoLi1 = new String[hoLi1.length - 1];
            Boolean[] newRenderAnimation = new Boolean[renderAnimation.length-1];
            Boolean[] newRenderStillFrame = new Boolean[renderStillFrame.length-1];
            int[] newStartFrame = new int[startFrame.length-1];
            int[] newEndFrame = new int[endFrame.length-1];
            int[] newStillFrame = new int[stillFrame.length-1];

            for (int i = 0; i < hoLi1.length; i++) {
                if (i < allFiles_HorizontalList.getMarkedInd()) {
                    newHoLi1[i] = hoLi1[i];
                    newRenderAnimation[i] = renderAnimation[i];
                    newRenderStillFrame[i] = renderStillFrame[i];
                    newStartFrame[i] = startFrame[i];
                    newEndFrame[i] = endFrame[i];
                    newStillFrame[i] = stillFrame[i];

                }
                if (i > allFiles_HorizontalList.getMarkedInd()) {
                    newHoLi1[i - 1] = hoLi1[i];
                    newRenderAnimation[i - 1] = renderAnimation[i];
                    newRenderStillFrame[i - 1] = renderStillFrame[i];
                    newStartFrame[i - 1] = startFrame[i];
                    newEndFrame[i - 1] = endFrame[i];
                    newStillFrame[i - 1] = stillFrame[i];
                }
            }
            renderAnimation=new Boolean[renderAnimation.length-1];
            renderStillFrame=new Boolean[renderStillFrame.length-1];
            startFrame=new int[startFrame.length-1];
            endFrame=new int[endFrame.length];
            stillFrame=new int[stillFrame.length];
            
            renderAnimation=newRenderAnimation;
            renderStillFrame=newRenderStillFrame;
            startFrame=newStartFrame;
            endFrame=newEndFrame;
            stillFrame=newStillFrame;
            
            allFiles_HorizontalList.setList(newHoLi1);
            updateWidgets();
        }
    }

    public void onScroll(float e) {
        allFiles_HorizontalList.onScroll(e);
        startFrame_counterArea.onScroll(e);
        endFrame_counterArea.onScroll(e);
        stillFrame_counterArea.onScroll(e);

    }

    public Boolean[] getRenderAnimation() {
        return renderAnimation;
    }

    public Boolean[] getRenderStillFrame() {
        return renderStillFrame;
    }

    public int[] getStartFrames() {
        return startFrame;
    }

    public int[] getEndFrames() {
        return endFrame;
    }

    public int[] getStilFrames() {
        return stillFrame;
    }

    public HorizontalList getHorizontalList() {
        return allFiles_HorizontalList;
    }

    public void setFileList(String[] l) {
        allFiles_HorizontalList.setList(l);
    }

    public void setStartupVals() {
        settings_checkboxes[0].setIsChecked(mainActivity.getHomeScreenMaster().getCheckboxes()[6].getIsChecked());
        settings_checkboxes[1].setIsChecked(mainActivity.getHomeScreenMaster().getCheckboxes()[7].getIsChecked());
        startFrame_counterArea.setCount(mainActivity.getHomeScreenMaster().getStartFrame_CounterArea().getCount());
        endFrame_counterArea.setCount(mainActivity.getHomeScreenMaster().getEndFrame_CounterArea().getCount());
        stillFrame_counterArea.setCount(mainActivity.getHomeScreenMaster().getStillFrame_counterArea().getCount());

        renderAnimation = new Boolean[allFiles_HorizontalList.getList().length];
        renderStillFrame = new Boolean[allFiles_HorizontalList.getList().length];
        startFrame = new int[allFiles_HorizontalList.getList().length];
        endFrame = new int[allFiles_HorizontalList.getList().length];
        stillFrame = new int[allFiles_HorizontalList.getList().length];

        for (int i = 0; i < renderAnimation.length; i++) {
            renderAnimation[i] = mainActivity.getHomeScreenMaster().getCheckboxes()[6].getIsChecked();
            renderStillFrame[i] = mainActivity.getHomeScreenMaster().getCheckboxes()[7].getIsChecked();
            startFrame[i] = mainActivity.getHomeScreenMaster().getStartFrame_CounterArea().getCount();
            endFrame[i] = mainActivity.getHomeScreenMaster().getEndFrame_CounterArea().getCount();
            stillFrame[i] = mainActivity.getHomeScreenMaster().getStillFrame_counterArea().getCount();
        }
    }
}
