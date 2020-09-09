package com.dominic.network_apk;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;

public class HomeScreenMaster {
    private int btnSize, btnSizeSmall, edgeRad, margin, stdTs, dark, light, lighter, border, textCol, textDark;
    private float textYShift;
    private Boolean fileExplorerIsOpen = false, prevFileExplorerIsOpen = false;
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

    public HomeScreenMaster(PApplet p, int btnSize, int btnSizeSmall, int edgeRad, int margin, int stdTs, int dark, int light, int lighter, int border, int textCol, int textDark, float textYShift, String[] homeScreenPictoPaths, String[] arrowPaths, String[] hoLiPictoPaths, String[] fileExplorerPaths, PFont stdFont) {
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
        if (mainActivity.getIsMaster()) {
            mainButtons = mainActivity.getMainButtonsMaster();
        } else {
            mainButtons = mainActivity.getMainButtonsSlave();
        }
        int rowDist = btnSize;
        int startY = mainButtons[0].getH() + (p.height - mainButtons[0].getH() - rowDist * 3 - btnSizeSmall) / 2;

        String[] checkBoxTexts = { "Render with full force", "Render only with slaves", "", "Render on Sheepit", "Use CPU", "Use GPU", "", "" };
        String[] checkBoxHoverTexts = { "", "", "Render listed .blend files", "", "", "", "", "" };
        for (int i = 0; i < homeSettings_checkboxes.length; i++) {
            int ys = rowDist;
            int is = 0;
            if (i > 3) {
                ys = rowDist * 2;
                is = 4;
            }
            homeSettings_checkboxes[i] = new Checkbox(p, (int) (p.width / 9 * 1.5f + (p.width / 9 * 2) * (i - is)), startY + ys, p.width / 9, btnSizeSmall, btnSizeSmall, edgeRad, margin, stdTs, light, light, border, textCol, textYShift, false, false, checkBoxTexts[i], checkBoxHoverTexts[i], homeScreenPictoPaths[0], stdFont, null);
        }

        fileToRender_pathSelector = new PathSelector(p, btnSizeSmall + margin * 3, 0, p.width / 8 - margin, btnSizeSmall, edgeRad, margin, stdTs, btnSizeSmall, border, light, textCol, dark, light, lighter, textDark, textYShift, false, true, "...\\\\File.blend", homeScreenPictoPaths[1], fileExplorerPaths, stdFont, homeSettings_checkboxes[2]);

        int sfW = (int) (p.width / 16 - margin);
        int sfX = (int) ((homeSettings_checkboxes[6].getX() - homeSettings_checkboxes[6].getW() / 2 + homeSettings_checkboxes[6].getBoxDim() + margin * 2)) - homeSettings_checkboxes[6].getX() + sfW / 2;
        startFrame_counterArea = new CounterArea(p, sfX, 0, sfW, btnSizeSmall, edgeRad, margin, stdTs, 0, 1000000000, 0, light, lighter, textCol, textYShift, true, "Startframe", arrowPaths, stdFont, homeSettings_checkboxes[6]);
        endFrame_counterArea = new CounterArea(p, sfX + sfW + margin, 0, sfW, btnSizeSmall, edgeRad, margin, stdTs, 0, 1000000000, 0, light, lighter, textCol, textYShift, true, "Endframe", arrowPaths, stdFont, homeSettings_checkboxes[6]);
        sfW = (int) p.textWidth(checkBoxTexts[3]);
        sfX = (int) ((homeSettings_checkboxes[7].getX() - homeSettings_checkboxes[7].getW() / 2 + homeSettings_checkboxes[7].getBoxDim() + margin * 2)) - homeSettings_checkboxes[7].getX() + sfW / 2;
        stillFrame_counterArea = new CounterArea(p, sfX, 0, sfW, btnSizeSmall, edgeRad, margin, stdTs, 0, 1000000000, 0, light, lighter, textCol, textYShift, true, "Still frame", arrowPaths, stdFont, homeSettings_checkboxes[7]);

        startRendering_btn = new ImageButton(p, p.width / 2, startY + rowDist * 3, btnSize, btnSize, stdTs, margin, edgeRad, -1, textYShift, true, false, textCol, light, homeScreenPictoPaths[2], "Start rendering", null);

        String[] startList = {};

        int p1 = homeSettings_checkboxes[4].getBoxX() - homeSettings_checkboxes[4].getBoxDim() / 2;
        int p2 = stillFrame_counterArea.getX() + stillFrame_counterArea.getW() / 2;
        int hlH = p2 - p1;
        int hlX = p1 + hlH / 2;
        fileSelector_HorizontalList = new HorizontalList(p, hlX, startY, hlH, btnSizeSmall + margin * 2, margin, edgeRad, stdTs, (int) p.textWidth("Files to render") + margin * 3 + btnSizeSmall, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border, textYShift, '\\', false, false, true, "Files to render", hoLiPictoPaths, startList, stdFont, null);
    }

    public void render() {
        fileExplorerIsOpen = fileToRender_pathSelector.getFileExplorerIsOpen();
        // add to list --------------------------------------------------
        /*
         * String[] m2 =
         * p.match("C:\\blenderFiles\\\\cache_fluid\\\\config\\\\config_0001.uni",
         * ".blend"); if(m2!=null) { p.println("not null"); }
         */
        if (fileExplorerIsOpen == false) {
            if (fileExplorerIsOpen != prevFileExplorerIsOpen) {
                String newBlendFile = fileToRender_pathSelector.getPath();
                String[] splitStr = p.split(newBlendFile, "\\");
                String[] m1 = p.match(splitStr[splitStr.length - 1], ".blend");
                if (m1 != null) {
                    // p.println("is blendfile");
                    String[] holi1 = fileSelector_HorizontalList.getList();
                    String[] newHoLi1 = new String[holi1.length + 1];
                    for (int i = 0; i < holi1.length; i++) {
                        newHoLi1[i] = holi1[i];
                    }
                    newHoLi1[newHoLi1.length - 1] = newBlendFile;
                    fileSelector_HorizontalList.setList(newHoLi1);
                } else {
                    p.println("no blendfile");
                }
            }
        }
        // add to list --------------------------------------------------

        // render all ---------------------------------------------------------------
        fileToRender_pathSelector.render();

        if (fileExplorerIsOpen == false) {
            if (mainActivity.getIsMaster()) {
                mainActivity.renderMainButtonsMaster();
            } else {
                mainActivity.renderMainButtonsSlave();
            }
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

                if (fileSelector_HorizontalList.getList().length < 1 && homeSettings_checkboxes[2].getIsChecked()) {
                    if (errorMessage.length() > 0) {
                        errorMessage += " - ";
                    }
                    errorMessage += "No .blend detected";
                    correctlySelected = false;
                }

                // Atention --------------------------------------------
                correctlySelected = true;
                // Atention --------------------------------------------

                if (correctlySelected) {
                    mainActivity.setMode(101);
                    if (homeSettings_checkboxes[2].getIsChecked()) {
                        mainActivity.getRenderOverview().setRenderMode(0.1f);
                    }
                    if (homeSettings_checkboxes[3].getIsChecked()) {
                        mainActivity.getRenderOverview().setRenderMode(1);
                    }

                } else {
                    makeToasts.add(new MakeToast(p, p.width / 2, p.height - stdTs * 2, stdTs, margin, edgeRad, errorMessage.length() * 2, light, textCol, textYShift, false, errorMessage, stdFont, null));
                }

                startRendering_btn.setIsClicked(false);
            }
        }

        prevFileExplorerIsOpen = fileExplorerIsOpen;
        // handle checkboxes ------------------------------------------------------

    }

    public void onMousePressed(int mouseButton) {
        if (fileExplorerIsOpen == false) {

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
            fileSelector_HorizontalList.onMousePressed();
        }
        fileToRender_pathSelector.onMousePressed(mouseButton);
    }

    public void onMouseReleased(int mouseButton) {
        if (fileExplorerIsOpen == false) {

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
            fileSelector_HorizontalList.onMouseReleased(mouseButton);
        }
        fileToRender_pathSelector.onMouseReleased(mouseButton);

    }

    public void onKeyPressed(char key) {
        fileToRender_pathSelector.onKeyPressed(key);

    }

    public void onKeyReleased(char key) {
        fileToRender_pathSelector.onKeyReleased(key);
        if (key == p.DELETE) {
            String[] hoLi1 = fileSelector_HorizontalList.getList();
            String[] newHoLi1 = new String[hoLi1.length - 1];
            for (int i = 0; i < hoLi1.length; i++) {
                if (i < fileSelector_HorizontalList.getMarkedInd()) {
                    newHoLi1[i] = hoLi1[i];
                }
                if (i > fileSelector_HorizontalList.getMarkedInd()) {
                    newHoLi1[i - 1] = hoLi1[i];
                }
            }
            fileSelector_HorizontalList.setList(newHoLi1);
        }
    }

    public void onScroll(float e) {
        fileToRender_pathSelector.onScroll(e);
        if (fileExplorerIsOpen == false) {
            startFrame_counterArea.onScroll(e);
            endFrame_counterArea.onScroll(e);
            stillFrame_counterArea.onScroll(e);
            fileSelector_HorizontalList.onScroll(e);
        }
    }

}
