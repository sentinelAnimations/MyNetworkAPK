package com.dominic.network_apk;

import java.io.File;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class SettingsScreen {
    private int btnSize, btnSizeSmall, stdTs, subtitleTs, margin, edgeRad, textCol, textDark, dark, light, lighter, mode = 0, doOnce = 0;
    private Boolean successfullySaved = false;
    private float textYShift;
    private String mySavePath, savePathPrefix = "";
    private String[] imgPaths;
    private PFont stdFont;
    private PImage screenshot;
    private PApplet p;
    private MainActivity mainActivity;
    private PictogramImage firstSetupPicto;
    private PictogramImage[] setting_pictos;
    public PathSelector[] pathSelectors;
    public ImageButton saveSettings_btn, firstSetupHelp_btn;
    public EditText personalData_et;
    public DropdownMenu masterOrSlave_dropdown;
    private JsonHelper jHelper;
    private JSONArray loadedSettingsData = new JSONArray();
    private ImageButton[] mainButtons;
    private ArrayList<MakeToast> makeToasts = new ArrayList<MakeToast>();

    public SettingsScreen(PApplet p, int btnSize, int btnSizeSmall, int stdTs, int subtitleTs, int margin, int edgeRad, int textCol, int textDark, int dark, int light, int lighter, int border, float textYShift, String mySavePath, String[] imgPaths, String[] HorizontalListPictoPaths, String[] fileExplorerPaths, String[] firstSetupPictos, PFont stdFont) {
        this.p = p;
        this.btnSize = btnSize;
        this.btnSizeSmall = btnSizeSmall;
        this.stdTs = stdTs;
        this.subtitleTs = subtitleTs;
        this.margin = margin;
        this.edgeRad = edgeRad;
        this.textYShift = textYShift;
        this.textCol = textCol;
        this.textDark = textDark;
        this.dark = dark;
        this.light = light;
        this.lighter = lighter;
        this.mySavePath = mySavePath;
        this.imgPaths = imgPaths;
        this.stdFont = stdFont;
        mainActivity = (MainActivity) p;

        if (mainActivity.getIsMaster()) {
            mainButtons = mainActivity.getMainButtonsMaster();
        } else {
            mainButtons = mainActivity.getMainButtonsSlave();
        }

        setting_pictos = new PictogramImage[imgPaths.length - 2];
        pathSelectors = new PathSelector[imgPaths.length - 4];
        Boolean[] selectFolder = { false, true, true };
        String[] description = { "Setup this Pc as Slave or Master", "Select Blender.exe Folder", "Select image output Folder", "Select Path to Cloud", "Enter desired Name of PC", "Save Settings and move on | shortcut: ctrl+s" };
        String[] pathSelectorHints = { "...\\\\Blender.exe", "...\\\\images", "...\\\\Cloud" };

        for (int i = 0; i < setting_pictos.length; i++) {
            setting_pictos[i] = new PictogramImage(p, (p.width / 8 * 4) / 2 + p.width / 8 * (i), p.height / 2 - btnSize / 2, btnSize, margin, stdTs, edgeRad, textCol, textYShift, false, imgPaths[i], description[i], null);
            if (i > 0 && i < pathSelectors.length + 1) {

                pathSelectors[i - 1] = new PathSelector(p, 0, btnSize, p.width / 8 - margin * 2, btnSizeSmall, edgeRad, margin, stdTs, btnSizeSmall, border, light, textCol, dark, light, lighter, textDark, textYShift, selectFolder[i - 1], true, pathSelectorHints[i - 1], imgPaths[imgPaths.length - 1], fileExplorerPaths, stdFont, setting_pictos[i]);
            }
        }

        saveSettings_btn = new ImageButton(p, p.width - margin - btnSizeSmall / 2, p.height - margin - btnSizeSmall / 2, btnSizeSmall, btnSizeSmall, stdTs, margin, edgeRad, 19, textYShift, true, false, textCol, light, imgPaths[5], description[5], null);
        char[] fChars = { '>', '<', ':', '"', '/', '\\', '|', '?', '*' };
        personalData_et = new EditText(p, 0, btnSize, p.width / 8 - margin * 2, btnSizeSmall, stdTs, light, textCol, edgeRad, margin, textYShift, true, true, "Enter PC name", fChars, stdFont, setting_pictos[setting_pictos.length - 1]);

        String[] dropdownList = { "Master", "Slave" };
        String[] ddPaths = { HorizontalListPictoPaths[HorizontalListPictoPaths.length - 1], HorizontalListPictoPaths[HorizontalListPictoPaths.length - 2] };
        masterOrSlave_dropdown = new DropdownMenu(p, 0, btnSize, p.width / 8 - margin * 2, btnSizeSmall, p.height / 4 + btnSizeSmall + margin * 2, edgeRad, margin, stdTs, light, lighter, textCol, textDark, textYShift, "Master or Slave", ddPaths, dropdownList, stdFont, true, setting_pictos[0]);

        firstSetupPicto = new PictogramImage(p, margin + btnSize / 2, margin + btnSize / 2, btnSize, margin, stdTs, edgeRad, textCol, textYShift, false, firstSetupPictos[0], "First setup page", null);
        firstSetupHelp_btn = new ImageButton(p, p.width - btnSize / 2 - margin, btnSize / 2 + margin, btnSize, btnSize, stdTs, margin, edgeRad, 8, textYShift, false, false, textCol, textCol, firstSetupPictos[1], "questions and infos | sortcut: ctrl+h", null);

        jHelper = new JsonHelper(p);

        setData();
    }

    public void render() {

        // check if some fileexplorer is open -------------------------

        for (int i = 0; i < pathSelectors.length; i++) {
            if (pathSelectors[i].getFileExplorerIsOpen()) {
                mode = 1;
                doOnce = 0;
                break;
            }
            if (i == pathSelectors.length - 1) {
                mode = 0;
                doOnce = 1;
            }
        }

        if (doOnce == 0) {
            for (int i = 0; i < pathSelectors.length; i++) {
                pathSelectors[i].setRenderPathSelector(false);
            }
            doOnce++;
        } else {
            if (doOnce > 0) {
                for (int i = 0; i < pathSelectors.length; i++) {
                    pathSelectors[i].setRenderPathSelector(true);
                }
                doOnce++;
            }
        }
        // check if some fileexplorer is open -------------------------

        // render firstSetup and !firstSetup ------------------------
        if (mainActivity.getLoadingScreen().getIsFirstSetup() == true && mode == 0) {
            p.fill(light);
            p.stroke(light);
            p.rect(p.width / 2, btnSize / 2 + margin, p.width, btnSize + margin * 2);
            firstSetupPicto.render();
            firstSetupHelp_btn.render();
            p.fill(textDark);
            p.textAlign(p.LEFT, p.CENTER);
            p.textSize(subtitleTs);
            p.text("First setup", firstSetupPicto.getX() + btnSize + margin * 2, firstSetupPicto.getY());
        } else {
            if (mainActivity.getIsMaster()) {
                mainActivity.renderMainButtonsMaster();
            } else {
                mainActivity.renderMainButtonsSlave();
            }
        }
        // render firstSetup and !firstSetup ------------------------

        // render toasts----------------------------------------------
        for (int i = 0; i < personalData_et.getToastList().size(); i++) {
            MakeToast m = (MakeToast) personalData_et.getToastList().get(i);
            if (m.remove) {
                personalData_et.removeToast(i);
            } else {
                m.render();
            }
        }

        for (int i = 0; i < getToastList().size(); i++) {
            MakeToast m = (MakeToast) getToastList().get(i);
            if (m.remove) {
                removeToast(i);
            } else {
                m.render();
            }
        }
        // render toasts----------------------------------------------

        // render edittext before path selectors
        if (mode == 0) {
            personalData_et.render();
        }
        // render edittext before path selectors

        // render pathselector --------------------------------------
        for (int i = pathSelectors.length - 1; i >= 0; i--) {
            PathSelector ps = pathSelectors[i];
            ps.render();
            if (ps.getOpenFileExplorer_btn().getIsClicked()) {
                mode = 1;
            }
        }
        // render pathselector --------------------------------------

        if (mode == 0) { // normal mode
            if (mainActivity.getLoadingScreen().getIsFirstSetup() == false) {
                if (mainActivity.getIsMaster()) {
                    mainActivity.renderMainButtonsMaster();
                } else {
                    mainActivity.renderMainButtonsSlave();
                }
            }

            for (int i = setting_pictos.length - 1; i >= 0; i--) {
                setting_pictos[i].render();
            }

            saveSettings_btn.render();
            masterOrSlave_dropdown.render();

            // handle save button ------------------------------------

            if (saveSettings_btn.getIsClicked() == true) {
                // check if all is set
                Boolean allSet = true;
                JSONObject settingsDetails = new JSONObject();
                JSONObject settingsObject = new JSONObject();

                allSet = masterOrSlave_dropdown.getIsSelected();
                settingsDetails.put("masterOrSlave_dropdown_selectedInd", masterOrSlave_dropdown.getSelectedInd());

                for (int i = pathSelectors.length - 1; i >= 0; i--) {
                    PathSelector ps = pathSelectors[i];
                    if (ps.getPath().length() < 1) {
                        allSet = false;
                    } else {
                        settingsDetails.put("pathSelector" + i, ps.getPath());
                    }

                }

                if (personalData_et.getStrList().get(0).length() < 1) {
                    allSet = false;
                } else {
                    settingsDetails.put("personalData_et", personalData_et.getStrList().get(0));
                }
                // write to jsonfile;
                if (allSet == true) {
                    jHelper.clearArray();

                    settingsObject.put("Settings", settingsDetails);
                    jHelper.appendObjectToArray(settingsObject);
                    jHelper.writeData(mySavePath);

                    successfullySaved = true;
                    mainActivity.initializeLoadingScreen();
                    /*
                     * mainActivity.setMode(1);
                     * 
                     * if (masterOrSlave_dropdown.getSelectedInd() == 0) {
                     * mainActivity.setIsMaster(true); } else { mainActivity.setIsMaster(false); }
                     */
                    mainActivity.getLoadingScreen().setIsFirstSetup(false);
                    makeToasts.add(new MakeToast(p, p.width / 2, p.height - stdTs * 2, stdTs, margin, edgeRad, 100, light, textCol, textYShift, false, "Saved settings", stdFont, null));

                } else {
                    makeToasts.add(new MakeToast(p, p.width / 2, p.height - stdTs * 2, stdTs, margin, edgeRad, 100, light, textCol, textYShift, false, "Set all required data", stdFont, null));
                }
                saveSettings_btn.setIsClicked(false);
            }
            // handle save button ------------------------------------

        }

    }

    public void onMousePressed(int mouseButton) {
        if (mainActivity.getLoadingScreen().getIsFirstSetup() == true && mode == 0) {
            firstSetupHelp_btn.onMousePressed();
        }
        if (mode == 0) {
            if (mainActivity.getLoadingScreen().getIsFirstSetup() == false) {
                if (mainActivity.getIsMaster()) {
                    for (int i = 0; i < mainButtons.length; i++) {
                        if (mainButtons[0].getClickCount() % 2 == 0 || i == 0) {
                            mainButtons[i].onMousePressed();
                        }
                    }
                } else {
                    for (int i = 0; i < mainButtons.length; i++) {
                        mainButtons[i].onMousePressed();
                    }
                }
            }

            saveSettings_btn.onMousePressed();
            masterOrSlave_dropdown.dropdown_btn.onMousePressed();
        }
        for (int i = 0; i < pathSelectors.length; i++) {
            pathSelectors[i].onMousePressed(mouseButton);
        }
    }

    public void onMouseReleased(int mouseButton) {
        if (mode == 0) {
            if (mainActivity.getLoadingScreen().getIsFirstSetup() == true) {
                firstSetupHelp_btn.onMouseReleased();
            }

            if (mainActivity.getLoadingScreen().getIsFirstSetup() == false && mode == 0) {
                if (mainActivity.getIsMaster()) {
                    for (int i = 0; i < mainButtons.length; i++) {
                        if (mainButtons[0].getClickCount() % 2 == 0 || i == 0) {
                            mainButtons[i].onMouseReleased();
                        }
                    }
                } else {
                    for (int i = 0; i < mainButtons.length; i++) {
                        mainButtons[i].onMouseReleased();
                    }
                }
            }

            saveSettings_btn.onMouseReleased();
            personalData_et.onMouseReleased();

            masterOrSlave_dropdown.dropdown_btn.onMouseReleased();
            masterOrSlave_dropdown.onMouseReleased();
        }
        for (int i = 0; i < pathSelectors.length; i++) {
            pathSelectors[i].onMouseReleased(mouseButton);
        }
    }

    public void onKeyPressed(char key) {
        if (mode == 0) {
            personalData_et.onKeyPressed(key);
        }
    }

    public void onKeyReleased(char k) {
        if (mode == 0) {
            if (mainActivity.getLoadingScreen().getIsFirstSetup() == true) {
                firstSetupHelp_btn.onKeyReleased(k);
            }

            personalData_et.onKeyReleased(k);
            saveSettings_btn.onKeyReleased(k);
        }
        for (int i = 0; i < pathSelectors.length; i++) {
            pathSelectors[i].onKeyReleased(k);
        }
    }

    public void onScroll(float e) {
        masterOrSlave_dropdown.onScroll(e);
        for (int i = 0; i < pathSelectors.length; i++) {
            pathSelectors[i].onScroll(e);
        }
    }

    private void setData() {
        // load settings info, if not available, goto settingsPage----------------------
        loadedSettingsData = jHelper.getData(mySavePath);
        if (loadedSettingsData.isEmpty()) {
        } else {
            JsonObject jsonObject = new JsonParser().parse(loadedSettingsData.get(0).toString()).getAsJsonObject();
            int selectedInd = Integer.parseInt(jsonObject.getAsJsonObject("Settings").get("masterOrSlave_dropdown_selectedInd").getAsString());
            masterOrSlave_dropdown.setIsSelected(selectedInd);
            for (int i = pathSelectors.length - 1; i >= 0; i--) {
                PathSelector ps = pathSelectors[i];
                String t = jsonObject.getAsJsonObject("Settings").get("pathSelector" + i).getAsString();
                ps.setText(t);
            }
            String t = jsonObject.getAsJsonObject("Settings").get("personalData_et").getAsString();
            personalData_et.setText(t);
        }
        // load settings info, if not available, goto settingsPage----------------------
    }

    public int getMode() {
        return mode;
    }

    public ArrayList getToastList() {
        return makeToasts;
    }

    public PathSelector[] getPathSelectors() {
        return pathSelectors;
    }
    
    public EditText getEditText() {
        return personalData_et;
    }
    public void removeToast(int i) {
        makeToasts.remove(i);
    }

}