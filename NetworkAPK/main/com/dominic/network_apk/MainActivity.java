package com.dominic.network_apk;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import processing.awt.PSurfaceAWT.SmoothCanvas;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PSurface;
import processing.core.PVector;
import processing.event.MouseEvent;

public class MainActivity extends PApplet {

    public static void main(String[] args) {
        PApplet.main("com.dominic.network_apk.MainActivity");
    }

    // Global variables -----------------------------------------
    int mode = 0; // 0=loadingScreen, 1=home,2=node editor,3=settings,4=download
                  // blender,5=questions,101=renderMode

    // integers-------------------------------------------------
    int windowTopBarHeight;
    // integers-------------------------------------------------

    // Booleans-------------------------------------------------
    // Booleans-------------------------------------------------

    // Colors--------------------------------------------------
    private int darkest = color(30), dark = color(26, 32, 37), light = color(39, 48, 56), lighter = color(54, 67, 78), lightest = color(64, 77, 88), border = color(255, 191, 0), darkTransparent = color(26, 32, 37, 100), red = color(255, 0, 0), green = color(0, 255, 0), textCol = color(255), textDark = color(150);
    // colors -------------------------------------------------

    // Dimens--------------------------------------------------
    private int stdTs = 12, titleTs = 22, subtitleTs = 16, btnSize = 50, btnSizeLarge = btnSize * 2, btnSizeSmall = btnSize / 2, edgeRad = btnSize / 10, padding = 5, margin = padding;
    private float textYShift = 0.1f;
    // Dimens--------------------------------------------------

    // Strings--------------------------------------------------
    private String APKName = "InSevenDays©", APKDescription = "A network solution";
    private String[] modeNames = { "Home", "Node Editor", "Settings", "Spread Blender", "Theme", "Questions" };
    // Save paths ----------------------
    private String mySettingsPath = "output/SettingsScreen/settings.json", myNodeSettingsPath = "output/NodeEditor/nodeEditor.json", myThemeScreenPath = "output/ThemeScreen/colorTheme.json";
    // Save paths ----------------------
    // Strings--------------------------------------------------

    // Fonts---------------------------------------------------
    PFont stdFont;
    // Fonts---------------------------------------------------

    // PVectors -----------------------------------------------
    private PVector stdScreenDimension = new PVector(1050, 450);
    // PVectors -----------------------------------------------

    // java Jframe --------------------------------------------
    private JFrame jf;
    // java Jframe --------------------------------------------

    // images--------------------------------------------------
    PImage screenshot;

    private String absPathPictos = "imgs/pictograms/";
    private String absPathStartImgs = "imgs/startImgs/";
    private String[] startImgPaths = { "muffins.png" };
    // images--------------------------------------------------

    // Classes--------------------------------------------------
    // Main classes-------------------------------
    private LoadingScreen loadingScreen;
    private HomeScreen homeScreen;
    private NodeEditor nodeEditor;
    private SettingsScreen settingsScreen;
    private SpreadBlender spreadBlenderScreen;
    private ThemeScreen themeScreen;
    private QuestionScreen questionScreen;
    // Main classes-------------------------------
    // widgets -----------------------------------
    private PictogramImage firstSetupPicto;
    private ImageButton firstSetupHelp_btn;
    private ImageButton[] mainButtons = new ImageButton[7];
    // widgets -----------------------------------
    // Classes--------------------------------------------------
    // Global variables
    // -----------------------------------------------------------------------

    @Override
    public void settings() {

    }

    @Override
    public void setup() {
        getSurface().setSize((int) stdScreenDimension.x, (int) stdScreenDimension.y);
        getSurface().setTitle(APKName);

        SmoothCanvas sc = (SmoothCanvas) getSurface().getNative();
        jf = (JFrame) sc.getFrame();

        Image image = null;
        ArrayList<Image> icons = new ArrayList<>();
        try {
            icons.add(new ImageIcon(getClass().getResource("/icons/apkIcon16x16.png")).getImage());
            icons.add(new ImageIcon(getClass().getResource("/icons/apkIcon32x32.png")).getImage());
            icons.add(new ImageIcon(getClass().getResource("/icons/apkIcon64x64.png")).getImage());
            icons.add(new ImageIcon(getClass().getResource("/icons/apkIcon128x128.png")).getImage());
            jf.setIconImages(icons);
        } catch (Exception e) {
            e.printStackTrace();
        }

        getSurface().setResizable(false);

        rectMode(CENTER);
        imageMode(CENTER);

        // variableInitialisation -----------------------------------------------
        stdFont = createFont("fonts/stdFont.ttf", titleTs);

        initializeLoadingScreen();

    }

    public void initializeLoadingScreen() {
        // variableInitialisation for mode 0 --> loading screen----------------
        mode = 0;
        setColorTheme();
        loadingScreen = new LoadingScreen(this, btnSize, margin, stdTs, titleTs, subtitleTs, btnSizeSmall, edgeRad, dark, textCol, textDark, light, lighter, textYShift, APKName, APKDescription, "imgs/startImgs/muffins.png", mySettingsPath, stdFont);
        // variableInitialisation for mode 0 --> loading screen----------------
    }

    public void initializeClassInstances() {

        String[] fileExplorerPaths = { absPathPictos + "volume.png", absPathPictos + "folderStructure.png", absPathPictos + "folder.png", absPathPictos + "file.png", absPathPictos + "arrowLeft.png", absPathPictos + "arrowRight.png", absPathPictos + "rename.png", absPathPictos + "search.png", absPathPictos + "copy.png", absPathPictos + "cutFolder.png", absPathPictos + "pasteFolder.png", absPathPictos + "addFolder.png", absPathPictos + "deleteFolder.png", absPathPictos + "deleteFile.png", absPathPictos + "questions.png", absPathPictos + "cross.png", absPathPictos + "checkmark.png", absPathPictos + "arrowUp.png", absPathPictos + "arrowDown.png" };

        windowTopBarHeight = (int) (jf.getBounds().getHeight() - stdScreenDimension.y);

        String[] p3 = { absPathPictos + "collapse.png", absPathPictos + "home.png", absPathPictos + "nodeEditor.png", absPathPictos + "settings.png", absPathPictos + "spreadBlender.png", absPathPictos + "themeSettings.png", absPathPictos + "questions.png" };
        for (int i = 0; i < mainButtons.length; i++) {
            String s = "";
            if (i > 0) {
                s = modeNames[i - 1];
            }
            mainButtons[i] = new ImageButton(this, btnSize / 2 + margin + btnSize * i + margin * i, btnSize / 2 + margin, btnSize, btnSize, stdTs, margin, edgeRad, -1, textYShift, true, false, textCol, light, p3[i], s, null);
        }

        // variableInitialisation for mode 1 --> home screen-------------------
       // String[] p0 = { absPathPictos + "volume.png", absPathPictos + "folderStructure.png", absPathPictos + "folder.png", absPathPictos + "file.png", absPathPictos + "arrowLeft.png", absPathPictos + "arrowRight.png", absPathPictos + "rename.png", absPathPictos + "search.png", absPathPictos + "copy.png", absPathPictos + "cutFolder.png", absPathPictos + "pasteFolder.png", absPathPictos + "addFolder.png", absPathPictos + "deleteFolder.png", absPathPictos + "deleteFile.png", absPathPictos + "questions.png", absPathPictos + "cross.png", absPathPictos + "checkmark.png", absPathPictos + "arrowUp.png", absPathPictos + "arrowDown.png" };
        String[] pp = { absPathPictos + "arrowLeft.png", absPathPictos + "arrowRight.png" };
        String[] hoLiPictoPaths = { absPathPictos + "blendFile.png", absPathPictos + "arrowLeft.png", absPathPictos + "arrowRight.png" };
        String[] homeScreenPictoPaths= { absPathPictos + "checkmark.png", absPathPictos + "selectFolder.png", absPathPictos + "startEngine.png"};
        homeScreen = new HomeScreen(this, btnSize, btnSizeSmall, edgeRad, margin, stdTs, dark, light, lighter, border, textCol, textDark, textYShift,homeScreenPictoPaths, pp, hoLiPictoPaths, fileExplorerPaths, stdFont);
        // variableInitialisation for mode 1 --> home screen-------------------

        // variableInitialisation for mode 2 --> node editor-------------------
        initializeNodeEditor();
        // variableInitialisation for mode 2 --> node editor-------------------

        // variableInitialisation for mode 3 --> settings screen---------------
        String[] p1 = { absPathPictos + "masterOrSlave.png", absPathPictos + "blenderExeFolder.png", absPathPictos + "imageFolder.png", absPathPictos + "pathToCloud.png", absPathPictos + "personalData.png", absPathPictos + "checkmark.png", absPathPictos + "selectFolder.png" };
        String[] p2 = { absPathPictos + "volume.png", absPathPictos + "folderStructure.png", absPathPictos + "folder.png", absPathPictos + "file.png", absPathPictos + "arrowLeft.png", absPathPictos + "arrowRight.png", absPathPictos + "rename.png", absPathPictos + "search.png", absPathPictos + "copy.png", absPathPictos + "cutFolder.png", absPathPictos + "pasteFolder.png", absPathPictos + "addFolder.png", absPathPictos + "deleteFolder.png", absPathPictos + "deleteFile.png", absPathPictos + "questions.png", absPathPictos + "cross.png", absPathPictos + "checkmark.png", absPathPictos + "arrowUp.png", absPathPictos + "arrowDown.png" };
        settingsScreen = new SettingsScreen(this, btnSize, btnSizeSmall, stdTs, margin, edgeRad, textCol, textDark, dark, light, lighter, border, textYShift, mySettingsPath, p1, p2, fileExplorerPaths, stdFont);

        firstSetupPicto = new PictogramImage(this, margin + btnSize / 2, margin + btnSize / 2, btnSize, margin, stdTs, edgeRad, textCol, textYShift, false, absPathPictos + "settings.png", "First setup page", null);
        firstSetupHelp_btn = new ImageButton(this, width - btnSize / 2 - margin, btnSize / 2 + margin, btnSize, btnSize, stdTs, margin, edgeRad, 8, textYShift, false, false, textCol, textCol, absPathPictos + "questions.png", "questions and infos | sortcut: ctrl+h", null);

        // variableInitialisation for mode 3 --> settings screen---------------

        // variableInitialisation for mode 4 --> blender download--------------
        String[] pp1 = { absPathPictos + "selectFolder.png", absPathPictos + "spreadBlender.png" };
        spreadBlenderScreen = new SpreadBlender(this, btnSize, btnSizeSmall, margin, stdTs, edgeRad, dark, darkest, light, lighter, lightest, border, textCol, textDark, textYShift, pp1, fileExplorerPaths, stdFont);
        // variableInitialisation for mode 4 --> blender download--------------

        // variableInitialisation for mode 5 --> Theme screen-------------------
        String[] pp2 = { absPathPictos + "colorPicker.png", absPathPictos + "restart.png", absPathPictos + "brightTheme.png", absPathPictos + "darkTheme.png" };
        themeScreen = new ThemeScreen(this, btnSize, btnSizeSmall, margin, stdTs, edgeRad, dark, darkest, light, lighter, lightest, border, textCol, textDark, textYShift, myThemeScreenPath, pp2, stdFont);
        // variableInitialisation for mode 5 --> Theme screen-------------------

        // variableInitialisation for mode 6 --> help screen-------------------
        String[] pp3 = { absPathPictos + "search.png" };

        questionScreen = new QuestionScreen(this, btnSize, btnSizeSmall, margin, stdTs, edgeRad, dark, darkest, light, lighter, lightest, border, textCol, textDark, border, textYShift, pp3, fileExplorerPaths, stdFont);
        // variableInitialisation for mode 6 --> help screen-------------------

        // variableInitialisation -----------------------------------------------

    }

    public void initializeNodeEditor() {
        String[] btnP = { absPathPictos + "clearNodetree.png", absPathPictos + "reloadFromFile.png", absPathPictos + "addNode.png", absPathPictos + "center.png", absPathPictos + "save.png" };
        String[] nodeP1 = { absPathPictos + "masterPC.png", absPathPictos + "pc.png", absPathPictos + "laptop.png", absPathPictos + "switch.png", absPathPictos + "engine.png" };
        String[] nodeP2 = { absPathPictos + "masterPC.png", absPathPictos + "pc.png", absPathPictos + "laptop.png", absPathPictos + "switch.png", absPathPictos + "engine.png", absPathPictos + "cpu.png", absPathPictos + "gpu.png", absPathPictos + "arrowLeft.png", absPathPictos + "arrowRight.png", absPathPictos + "arrowUp.png", absPathPictos + "arrowDown.png", absPathPictos + "checkmark.png" };

        nodeEditor = new NodeEditor(this, btnSize, btnSizeSmall, margin, stdTs, edgeRad, dark, darkest, light, lighter, lightest, border, textCol, textDark, textYShift, myNodeSettingsPath, btnP, nodeP1, nodeP2, stdFont);
    }

    @Override
    public void draw() {

        if (mode != 2) {
            if (jf.getBounds().getHeight() > stdScreenDimension.y + windowTopBarHeight) {
                jf.setSize((int) stdScreenDimension.x, (int) stdScreenDimension.y + windowTopBarHeight);
            }
        }

        background(dark);

        if (mode == 0) { // loadingScreen ----------------
            loadingScreen.render();
        }

        if (loadingScreen.getInstanciatedClasses()) {

            if (mode == 1) {
                homeScreen.render();
            }

            if (mode == 2) {
                nodeEditor.render();
            }

            if (mode == 3) {// setup for the first time -----------------
                if (loadingScreen.getIsFirstSetup() == true && settingsScreen.getMode() == 0) {
                    fill(light);
                    stroke(light);
                    rect(width / 2, btnSize / 2 + margin, width, btnSize + margin * 2);
                    firstSetupPicto.render();
                    firstSetupHelp_btn.render();
                    fill(textDark);
                    textAlign(LEFT, CENTER);
                    textSize(subtitleTs);
                    text("First setup", firstSetupPicto.getX() + btnSize + margin * 2, firstSetupPicto.getY());
                } else {
                    renderMainButtons();
                }
                settingsScreen.render();

                // render toasts----------------------------------------------
                for (int i = 0; i < settingsScreen.personalData_et.getToastList().size(); i++) {
                    MakeToast m = (MakeToast) settingsScreen.personalData_et.getToastList().get(i);
                    if (m.remove) {
                        settingsScreen.personalData_et.removeToast(i);
                    } else {
                        m.render();
                    }
                }

                for (int i = 0; i < settingsScreen.getToastList().size(); i++) {
                    MakeToast m = (MakeToast) settingsScreen.getToastList().get(i);
                    if (m.remove) {
                        settingsScreen.removeToast(i);
                    } else {
                        m.render();
                    }
                }
                // render toasts----------------------------------------------

            }

            if (mode == 4) {
                spreadBlenderScreen.render();
            }
            if (mode == 5) {
                themeScreen.render();
            }
            if (mode == 6) {
                questionScreen.render();
            }
        }

    }

    public void renderMainButtons() {
        if (mainButtons[0].getClickCount() % 2 == 0) {
            int fillUpBarW = width - (mainButtons[mainButtons.length - 1].getX() + btnSize / 2 + margin * 2);
            int fillUpBarX = mainButtons[mainButtons.length - 1].getX() + btnSize / 2 + margin + fillUpBarW / 2;
            fill(light);
            stroke(light);
            rect(fillUpBarX, btnSize / 2 + margin, fillUpBarW, btnSize, edgeRad);
            textFont(stdFont);
            textSize(subtitleTs);
            fill(textDark);
            textAlign(CENTER, CENTER);
            if (mode - 1 >= 0) {
                String titleBarText = APKName + " | " + APKDescription + " | " + modeNames[mode - 1];

                if (textWidth(titleBarText) < fillUpBarW) {
                    text(titleBarText, fillUpBarX, btnSize / 2 + margin);
                }
            }
        }

        for (int i = mainButtons.length - 1; i >= 0; i--) {
            if (mainButtons[0].getClickCount() % 2 == 0 || i == 0) {
                mainButtons[i].render();
            }
            if (mainButtons[i].getIsClicked() == true) {
                switch (i) {
                case 0:
                    for (int i2 = 1; i2 < mainButtons.length; i2++) {
                        if (mainButtons[0].getClickCount() % 2 != 0) {
                            mainButtons[i2].setPos(mainButtons[0].getX(), mainButtons[0].getY());
                        } else {
                            mainButtons[i2].setPos(btnSize / 2 + margin + btnSize * i2 + margin * i2, btnSize / 2 + margin);
                        }
                    }
                    break;
                default:
                    mode = i;
                    getSurface().setResizable(false);
                    getSurface().setSize((int) stdScreenDimension.x, (int) stdScreenDimension.y);
                    Dimension d = new Dimension((int) stdScreenDimension.x, (int) stdScreenDimension.y);
                    frame.setPreferredSize(d);

                    if (mode == 2) {
                        getSurface().setResizable(true);
                        // jf.setResizable(true);
                    }
                    break;
                }
                mainButtons[i].setIsClicked(false);
            }
        }
    }

    @Override
    public void mousePressed() {
        if (loadingScreen.getInstanciatedClasses()) {

            if (mode == 1) {
                homeScreen.onMousePressed(mouseButton);
            }
            if (mode == 2) {
                nodeEditor.onMousePressed(mouseButton);
            }

            if (mode == 3) {
                settingsScreen.onMousePressed(mouseButton);
            }
            if (mode == 4) {
                spreadBlenderScreen.onMousePressed(mouseButton);
            }
            if (mode == 5) {
                themeScreen.onMousePressed();
            }
            if (mode == 6) {
                questionScreen.onMousePressed();
            }
        }
    }

    @Override
    public void mouseReleased() {
        if (loadingScreen.getInstanciatedClasses()) {

            if (mode == 1) {
                homeScreen.onMouseReleased(mouseButton);
            }

            if (mode == 2) {
                nodeEditor.onMouseReleased(mouseButton);
            }

            if (mode == 3) {
                settingsScreen.onMouseReleased(mouseButton);
            }

            if (mode == 4) {
                spreadBlenderScreen.onMouseReleased(mouseButton);
            }
            if (mode == 5) {
                themeScreen.onMouseReleased();
            }
            if (mode == 6) {
                questionScreen.onMouseReleased();
            }
        }

    }

    @Override
    public void mouseWheel(MouseEvent event) {
        if (loadingScreen.getInstanciatedClasses()) {

            float e = event.getCount();

            if (mode == 1) {
                homeScreen.onScroll(e);
            }

            if (mode == 2) {
                nodeEditor.onScroll(e);
            }

            if (mode == 3) {
                settingsScreen.onScroll(e);
            }

            if (mode == 4) {
                spreadBlenderScreen.onScroll(e);
            }
            if (mode == 5) {
                themeScreen.onScroll(e);
            }
            if (mode == 6) {
                questionScreen.onScroll(e);
            }
        }
    }

    @Override
    public void keyPressed() {
        if (loadingScreen.getInstanciatedClasses()) {

            if (mode == 1) {
                homeScreen.onKeyPressed(key);
            }
            if (mode == 2) {
                nodeEditor.onKeyPressed(key);
            }

            if (mode == 3) {
                settingsScreen.onKeyPressed(key);
            }

            if (mode == 4) {
                spreadBlenderScreen.onKeyPressed(key);
            }
            if (mode == 5) {
            }
            if (mode == 6) {
                questionScreen.onKeyPressed(key);
            }
        }
    }

    @Override
    public void keyReleased() {
        if (loadingScreen.getInstanciatedClasses()) {

            if (mode == 1) {
                homeScreen.onKeyReleased(key);
            }
            if (mode == 2) {
                nodeEditor.onKeyReleased(key);
            }

            if (mode == 3) {
                settingsScreen.onKeyReleased(key);
            }

            if (mode == 4) {
                spreadBlenderScreen.onKeyReleased(key);
            }
            if (mode == 5) {
                themeScreen.onKeyReleased(key);
            }
            if (mode == 6) {
                questionScreen.onKeyReleased(key);
            }
        }
    }

    public int getMode() {
        return mode;
    }

    public PFont getStdFont() {
        return stdFont;
    }

    public PVector getStdScreenDimensions() {
        return stdScreenDimension;
    }

    public ImageButton[] getMainButtons() {
        return mainButtons;
    }

    public ImageButton getFirstSetupHelp_btn() {
        return firstSetupHelp_btn;
    }

    public LoadingScreen getLoadingScreen() {
        return loadingScreen;
    }

    public NodeEditor getNodeEditor() {
        return nodeEditor;
    }

    public SettingsScreen getSettingsScreen() {
        return settingsScreen;
    }

    public void setMode(int setMode) {
        mode = setMode;
    }

    public void setColorTheme() {
        JsonHelper jHelper = new JsonHelper(this);
        JSONArray loadedThemeScreenData = new JSONArray();
        loadedThemeScreenData = jHelper.getData(myThemeScreenPath);
        if (loadedThemeScreenData.isEmpty()) {
        } else if (jHelper.getIsFlawlessLoaded()) {
            for (int i = 0; i < loadedThemeScreenData.size(); i++) {
                JsonObject jsonObject = new JsonParser().parse(loadedThemeScreenData.get(i).toString()).getAsJsonObject();
                JsonObject jsonSubObject = jsonObject.getAsJsonObject("colorPicker" + i);

                int pickedCol = jsonSubObject.get("pickedCol").getAsInt();
                // "Dark", "Light", "Lighter", "Lightest", "Borders", "Text", "Text dark"
                switch (i) {
                case 0:
                    dark = pickedCol;
                    break;
                case 1:
                    light = pickedCol;
                    break;
                case 2:
                    lighter = pickedCol;
                    break;
                case 3:
                    lightest = pickedCol;
                    break;
                case 4:
                    border = pickedCol;
                    break;
                case 5:
                    textCol = pickedCol;
                    break;
                case 6:
                    textDark = pickedCol;
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected value: " + i);
                }
            }
        }
    }
}
