package com.dominic.network_apk;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;

public class HomeScreen {    
    private int btnSize, btnSizeSmall, edgeRad, margin, stdTs, dark, light, lighter, border, textCol, textDark;
    private float textYShift;
    private Boolean fileExplorerIsOpen = false;
    private PFont stdFont;
    private PApplet p;
    private PathSelector fileToRender_pathSelector;
    private CounterArea startFrame_counterArea, endFrame_counterArea, stillFrame_counterArea;
    private ImageButton startRendering_btn;
    private HorizontalList fileSelector_HorizontalList;
    private MainActivity mainActivity;
    private ImageButton[] mainButtons;
    private Checkbox[] homeSettings_checkboxes = new Checkbox[8];
    private ArrayList<MakeToast> makeToasts = new ArrayList<MakeToast>();

    public HomeScreen(PApplet p, int btnSize, int btnSizeSmall, int edgeRad, int margin, int stdTs, int dark, int light, int lighter, int border, int textCol, int textDark, float textYShift,String[] homeScreenPictoPaths, String[] arrowPaths, String[] hoLiPictoPaths, String[] fileExplorerPaths, PFont stdFont) {
        this.p = p;
        this.btnSize = btnSize;
        this.btnSizeSmall = btnSizeSmall;
        this.edgeRad = edgeRad;
        this.margin = margin;
        this.stdTs = stdTs;
        this.dark = dark;
        this.light = light;
        this.lighter = lighter;
        this.border = border;
        this.textCol = textCol;
        this.textDark = textDark;
        this.textYShift = textYShift;
        this.stdFont = stdFont;
        mainActivity = (MainActivity) p;
        mainButtons = mainActivity.getMainButtons();

        String[] checkBoxTexts = { "Render with full force", "Render only with slaves", "", "Render on Sheepit", "Use CPU", "Use GPU", "", "" };
        for (int i = 0; i < homeSettings_checkboxes.length; i++) {
            int ys = 0;
            int is = 0;
            if (i > 3) {
                ys = p.height / 8;
                is = 4;
            }
            homeSettings_checkboxes[i] = new Checkbox(p, (int) (p.width / 9 * 1.5f + (p.width / 9 * 2) * (i - is)), p.height / 5 * 2 + ys, p.width / 9, btnSizeSmall, btnSizeSmall, edgeRad, margin, stdTs, light, light, border, textCol, textYShift, false, false, checkBoxTexts[i], homeScreenPictoPaths[0], stdFont, null);
        }

        fileToRender_pathSelector = new PathSelector(p, btnSizeSmall + margin * 3, 0, p.width / 8 - margin, btnSizeSmall, edgeRad, margin, stdTs, btnSizeSmall, border, light, textCol, dark, light, lighter, textDark, textYShift, false, true, "...\\\\File.blend", homeScreenPictoPaths[1], fileExplorerPaths, stdFont, homeSettings_checkboxes[2]);

        int sfW = (int) (p.width / 16 - margin);
        int sfX = (int) ((homeSettings_checkboxes[6].getX() - homeSettings_checkboxes[6].getW() / 2 + homeSettings_checkboxes[6].getBoxDim() + margin * 2)) - homeSettings_checkboxes[6].getX() + sfW / 2;
        startFrame_counterArea = new CounterArea(p, sfX, 0, sfW, btnSizeSmall, edgeRad, margin, stdTs, 0, 1000000000, 0, light, lighter, textCol, textYShift, true, "Startframe", arrowPaths, stdFont, homeSettings_checkboxes[6]);
        endFrame_counterArea = new CounterArea(p, sfX + sfW + margin, 0, sfW, btnSizeSmall, edgeRad, margin, stdTs, 0, 1000000000, 0, light, lighter, textCol, textYShift, true, "Endframe",arrowPaths, stdFont, homeSettings_checkboxes[6]);
        sfW = (int) p.textWidth(checkBoxTexts[3]);
        sfX = (int) ((homeSettings_checkboxes[7].getX() - homeSettings_checkboxes[7].getW() / 2 + homeSettings_checkboxes[7].getBoxDim() + margin * 2)) - homeSettings_checkboxes[7].getX() + sfW / 2;
        stillFrame_counterArea = new CounterArea(p, sfX, 0, sfW, btnSizeSmall, edgeRad, margin, stdTs, 0, 1000000000, 0, light, lighter, textCol, textYShift, true, "Still frame", arrowPaths, stdFont, homeSettings_checkboxes[7]);
        startRendering_btn = new ImageButton(p, p.width / 2, p.height - p.height / 7 * 2, btnSize, btnSize, stdTs, margin, edgeRad, -1, textYShift, true, false, textCol, light, homeScreenPictoPaths[2], "Start rendering", null);
        
        String[] startList = { "askdfhsaf", "aslkdfh", "askjdhf" };
        
       int  p1=homeSettings_checkboxes[4].getBoxX()-homeSettings_checkboxes[4].getBoxDim()/2;
       int  p2=stillFrame_counterArea.getX()+stillFrame_counterArea.getW()/2;
        int hlH=p2-p1;
        int hlX=p1+hlH/2;
        p.println(hlH,hlX,stillFrame_counterArea.getX(),stillFrame_counterArea.getW(),homeSettings_checkboxes[4].getX(),sfX);
        fileSelector_HorizontalList = new HorizontalList(p,hlX, p.height / 4,hlH, btnSizeSmall + margin * 2, margin, edgeRad, stdTs, (int) p.textWidth("Files to render") + margin * 3 + btnSizeSmall, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border, textYShift, '\\', false, false, true, "Files to render", hoLiPictoPaths, startList, stdFont, null);

    }

    public void render() {
        fileExplorerIsOpen = fileToRender_pathSelector.getFileExplorerIsOpen();

        
        // render all ---------------------------------------------------------------
        if (fileExplorerIsOpen == false) {
            mainActivity.renderMainButtons();

            for (int i = 0; i < homeSettings_checkboxes.length; i++) {
                homeSettings_checkboxes[i].render();
            }
            endFrame_counterArea.render();
            startFrame_counterArea.render();
            stillFrame_counterArea.render();
            startRendering_btn.render();
            p.stroke(light);
            p.line(homeSettings_checkboxes[0].getBoxX() - homeSettings_checkboxes[0].getBoxDim() / 2, startRendering_btn.getY(), startRendering_btn.getX() - startRendering_btn.getW() / 2 - margin * 2, startRendering_btn.getY());
            p.line(startRendering_btn.getX() + startRendering_btn.getW() / 2 + margin * 2, startRendering_btn.getY(), stillFrame_counterArea.getX() + stillFrame_counterArea.getW() / 2, startRendering_btn.getY());

            fileSelector_HorizontalList.render();
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
            // render all ---------------------------------------------------------------

            // handle checkboxes ------------------------------------------------------
            if (startRendering_btn.getIsClicked() == true) {
                Boolean correctlySelected = true;
                String errorMessage = "";

                if (homeSettings_checkboxes[6].getIsChecked() == false && homeSettings_checkboxes[7].getIsChecked() == false) {
                    correctlySelected = false;
                    if (homeSettings_checkboxes[3].getIsChecked()) {
                        correctlySelected = true;
                    }
                    if (correctlySelected == false) {
                        if (errorMessage.length() > 0) {
                            errorMessage += " - ";
                        }
                        errorMessage += "Either render animation or still frame";
                    }
                }

                if (homeSettings_checkboxes[0].getIsChecked() == false && homeSettings_checkboxes[1].getIsChecked() == false) {
                    correctlySelected = false;
                    if (errorMessage.length() > 0) {
                        errorMessage += " - ";
                    }
                    errorMessage += "Either select 'Render with full force' or 'Render only with slaves'";
                }

                if (homeSettings_checkboxes[2].getIsChecked() == false && homeSettings_checkboxes[3].getIsChecked() == false) {
                    correctlySelected = false;
                    if (errorMessage.length() > 0) {
                        errorMessage += " - ";
                    }
                    errorMessage += "Either select .blend file or choose 'Render on Sheepit'";
                }

                if (homeSettings_checkboxes[4].getIsChecked() == false && homeSettings_checkboxes[5].getIsChecked() == false) {
                    correctlySelected = false;
                    if (errorMessage.length() > 0) {
                        errorMessage += " - ";
                    }
                    errorMessage += "Either use CPU or GPU";
                }

                if (homeSettings_checkboxes[2].getIsChecked() && homeSettings_checkboxes[3].getIsChecked()) {
                    correctlySelected = false;
                    if (errorMessage.length() > 0) {
                        errorMessage += " - ";
                    }
                    errorMessage += "Cant render File AND on Sheepit";
                }

                if (homeSettings_checkboxes[6].getIsChecked() && homeSettings_checkboxes[7].getIsChecked()) {
                    correctlySelected = false;
                    if (errorMessage.length() > 0) {
                        errorMessage += " - ";
                    }
                    errorMessage += "Cant render Animation AND still frame";
                }

                if (homeSettings_checkboxes[6].getIsChecked()) {
                    if (endFrame_counterArea.getCount() < startFrame_counterArea.getCount()) {
                        if (errorMessage.length() > 0) {
                            errorMessage += " - ";
                        }
                        correctlySelected = false;
                        errorMessage += "Cant render negative frame range";
                    }
                }

                if (homeSettings_checkboxes[3].getIsChecked()) {
                    if (homeSettings_checkboxes[6].getIsChecked() || homeSettings_checkboxes[7].getIsChecked()) {
                        if (errorMessage.length() > 0) {
                            errorMessage += " - ";
                        }
                        correctlySelected = false;
                        errorMessage += "Cant render frame/Animation AND on Sheepit";
                    }
                }

                if (correctlySelected) {
                    mainActivity.setMode(101);
                } else {
                    makeToasts.add(new MakeToast(p, p.width / 2, p.height - stdTs * 2, stdTs, margin, edgeRad, errorMessage.length() * 2, light, textCol, textYShift, false, errorMessage, stdFont, null));
                }

                startRendering_btn.setIsClicked(false);
            }
        }
        fileToRender_pathSelector.render();

        // handle checkboxes ------------------------------------------------------

    }

    public void onMousePressed(int mouseButton) {
        startFrame_counterArea.onMousePressed();
        endFrame_counterArea.onMousePressed();
        stillFrame_counterArea.onMousePressed();
        startRendering_btn.onMousePressed();

        if (mainActivity.getLoadingScreen().getIsFirstSetup() == false) {
            for (int i = 0; i < mainButtons.length; i++) {
                if (mainButtons[0].getClickCount() % 2 == 0 || i == 0) {
                    mainButtons[i].onMousePressed();
                }
            }
        }
        fileToRender_pathSelector.onMousePressed(mouseButton);
        fileSelector_HorizontalList.onMousePressed();
    }

    public void onMouseReleased(int mouseButton) {
        for (int i = 0; i < homeSettings_checkboxes.length; i++) {
            homeSettings_checkboxes[i].onMouseReleased();
        }
        startFrame_counterArea.onMouseReleased();
        endFrame_counterArea.onMouseReleased();
        stillFrame_counterArea.onMouseReleased();
        startRendering_btn.onMouseReleased();

        if (mainActivity.getLoadingScreen().getIsFirstSetup() == false) {
            for (int i = 0; i < mainButtons.length; i++) {
                if (mainButtons[0].getClickCount() % 2 == 0 || i == 0) {
                    mainButtons[i].onMouseReleased();
                }
            }
        }
        fileToRender_pathSelector.onMouseReleased(mouseButton);
        fileSelector_HorizontalList.onMouseReleased(mouseButton);

    }

    public void onKeyPressed(char key) {
        fileToRender_pathSelector.onKeyPressed(key);

    }

    public void onKeyReleased(char key) {
        fileToRender_pathSelector.onKeyReleased(key);
    }

    public void onScroll(float e) {
        fileToRender_pathSelector.onScroll(e);
        startFrame_counterArea.onScroll(e);
        endFrame_counterArea.onScroll(e);
        stillFrame_counterArea.onScroll(e);
        fileSelector_HorizontalList.onScroll(e);

    }

}
