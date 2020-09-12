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
    // IsMaster==true:
    // 0=loadingScreen, 1=home,2=node editor,3=settings,4=download
    // blender,5=questions,101=renderMode

    // IsMaster==false:
    // 0=isSleeping, 1=isRendering

    int mode = 0;

    // integers-------------------------------------------------
    int windowTopBarHeight;
    // integers-------------------------------------------------

    // Booleans-------------------------------------------------
    private Boolean isMaster = true;
    // Booleans-------------------------------------------------

    // Colors--------------------------------------------------
    private int darkest = color(30), dark = color(26, 32, 37), light = color(39, 48, 56), lighter = color(54, 67, 78), lightest = color(64, 77, 88), border = color(255, 191, 0), darkTransparent = color(26, 32, 37, 100), red = color(255, 0, 0), green = color(0, 255, 0), textCol = color(255), textDark = color(150);
    // colors -------------------------------------------------

    // Dimens--------------------------------------------------
    private int stdTs = 12, titleTs = 22, subtitleTs = 16, btnSize = 50, btnSizeLarge = btnSize * 2, btnSizeSmall = btnSize / 2, edgeRad = btnSize / 10, padding = 5, margin = padding;
    private float textYShift = 0.1f;
    // Dimens--------------------------------------------------

    // Strings--------------------------------------------------
    private String APKName = "InSevenDays�", APKDescription = "A network solution";
    private String[] modeNamesMaster = { "Home", "Node Editor", "Settings", "Spread Blender", "Theme", "Questions" };
    private String[] modeNamesSlaves = { modeNamesMaster[0], modeNamesMaster[2], modeNamesMaster[4], modeNamesMaster[5] };

    // Save paths ----------------------
    // Local -----------
    private String mySettingsPath = "localOutput/SettingsScreen/settings.json", myNodeSettingsPath = "localOutput/NodeEditor/nodeEditor.json", myThemeScreenPath = "localOutput/ThemeScreen/colorTheme.json",masterCommandSavePath="";
    // Local -----------
    // shared ----------

    // shared ----------
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

    // string arrays -------------------
    private String[] fileExplorerPaths = { absPathPictos + "volume.png", absPathPictos + "folderStructure.png", absPathPictos + "folder.png", absPathPictos + "file.png", absPathPictos + "arrowLeft.png", absPathPictos + "arrowRight.png", absPathPictos + "rename.png", absPathPictos + "search.png", absPathPictos+"home.png",absPathPictos + "copy.png", absPathPictos + "cutFolder.png", absPathPictos + "pasteFolder.png", absPathPictos + "addFolder.png", absPathPictos + "deleteFolder.png", absPathPictos + "deleteFile.png", absPathPictos + "questions.png", absPathPictos + "cross.png", absPathPictos + "checkmark.png", absPathPictos + "arrowUp.png", absPathPictos + "arrowDown.png" };
    // string arrays -------------------
    // images--------------------------------------------------

    // Classes--------------------------------------------------
    // Main classes-------------------------------
    // Master ------------------------
    private LoadingScreen loadingScreen;
    private HomeScreenMaster homeScreen;
    private NodeEditor nodeEditor;
    private SettingsScreen settingsScreen;
    private SpreadBlender spreadBlenderScreen;
    private ThemeScreen themeScreen;
    private QuestionScreen questionScreen;
    private RenderOverview renderOverview;
    // Master ------------------------
    // Slaves-------------------------
    private HomeScreenSlaves homeScreenSlaves;
    // Slaves-------------------------
    // Main classes-------------------------------

    // widgets -----------------------------------
    private ImageButton[] mainButtonsMaster = new ImageButton[7];
    private ImageButton[] mainButtonsSlave = new ImageButton[4];
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
        windowTopBarHeight = (int) (jf.getBounds().getHeight() - stdScreenDimension.y);

        mode = 0;
        setColorTheme();
        loadingScreen = new LoadingScreen(this, btnSize, margin, stdTs, titleTs, subtitleTs, btnSizeSmall, edgeRad, dark, textCol, textDark, light, lighter, textYShift, APKName, APKDescription, "imgs/startImgs/muffins.png", mySettingsPath, stdFont);
        // variableInitialisation for mode 0 --> loading screen----------------
    }

    public void initializeClassInstancesMaster() {

        String[] p3 = { absPathPictos + "collapse.png", absPathPictos + "home.png", absPathPictos + "nodeEditor.png", absPathPictos + "settings.png", absPathPictos + "share.png", absPathPictos + "themeSettings.png", absPathPictos + "questions.png" };
        for (int i = 0; i < mainButtonsMaster.length; i++) {
            String s = "";
            if (i > 0) {
                s = modeNamesMaster[i - 1];
            }
            mainButtonsMaster[i] = new ImageButton(this, btnSize / 2 + margin + btnSize * i + margin * i, btnSize / 2 + margin, btnSize, btnSize, stdTs, margin, edgeRad, -1, textYShift, true, false, textCol, light, p3[i], s, null);
        }

        // variableInitialisation for mode 1 --> home screen-------------------
        String[] pp = { absPathPictos + "arrowLeft.png", absPathPictos + "arrowRight.png" };
        String[] hoLiPictoPathsHome = { absPathPictos + "blendFile.png", absPathPictos + "arrowLeft.png", absPathPictos + "arrowRight.png" };
        String[] homeScreenPictoPaths = { absPathPictos + "checkmark.png", absPathPictos + "selectFolder.png", absPathPictos + "startEngine.png" };
        homeScreen = new HomeScreenMaster(this, btnSize, btnSizeSmall, edgeRad, margin, stdTs, dark, light, lighter, border, textCol, textDark, textYShift, homeScreenPictoPaths, pp, hoLiPictoPathsHome, fileExplorerPaths, stdFont);
        // variableInitialisation for mode 1 --> home screen-------------------

        // variableInitialisation for mode 2 --> node editor-------------------
        initializeNodeEditor();
        // variableInitialisation for mode 2 --> node editor-------------------

        // variableInitialisation for mode 3 --> settings screen---------------
        initializeSettingsScreen();
        // variableInitialisation for mode 3 --> settings screen---------------

        // variableInitialisation for mode 4 --> blender download--------------
        String[] pp1 = { absPathPictos + "selectFolder.png", absPathPictos + "blender.png", absPathPictos + "sheepit.png" };
        spreadBlenderScreen = new SpreadBlender(this, btnSize, btnSizeSmall, margin, stdTs, edgeRad, dark, darkest, light, lighter, lightest, border, textCol, textDark, textYShift, pp1, fileExplorerPaths, stdFont);
        // variableInitialisation for mode 4 --> blender download--------------

        // variableInitialisation for mode 5 --> Theme screen-------------------
        initializeThemeScreen();
        // variableInitialisation for mode 5 --> Theme screen-------------------

        // variableInitialisation for mode 6 --> help screen-------------------
        initializeQuestionScreen();
        // variableInitialisation for mode 6 --> help screen-------------------

        // variableInitialisation for mode 101 --> RenderOverview -------------
        String[] rOpp = { absPathPictos + "cross.png", absPathPictos + "sheepit.png",absPathPictos+"sleeping.png" };
        String[] hoLiPictoPathsRenderOverview = { absPathPictos + "blendFile.png", absPathPictos + "arrowLeft.png", absPathPictos + "arrowRight.png" };
        renderOverview = new RenderOverview(this, stdTs, edgeRad, margin, btnSizeLarge, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border, textYShift,masterCommandSavePath, rOpp, hoLiPictoPathsRenderOverview, stdFont);
        // variableInitialisation for mode 101 --> RenderOverview -------------

        // variableInitialisation -----------------------------------------------

    }

    public void initializeNodeEditor() {
        String[] btnP = { absPathPictos + "clearNodetree.png", absPathPictos + "reloadFromFile.png", absPathPictos + "addNode.png", absPathPictos + "center.png", absPathPictos + "save.png" };
        String[] nodeP1 = { absPathPictos + "masterPC.png", absPathPictos + "pc.png", absPathPictos + "laptop.png", absPathPictos + "switch.png", absPathPictos + "engine.png" };
        String[] nodeP2 = { absPathPictos + "masterPC.png", absPathPictos + "pc.png", absPathPictos + "laptop.png", absPathPictos + "switch.png", absPathPictos + "engine.png", absPathPictos + "cpu.png", absPathPictos + "gpu.png", absPathPictos + "arrowLeft.png", absPathPictos + "arrowRight.png", absPathPictos + "arrowUp.png", absPathPictos + "arrowDown.png", absPathPictos + "checkmark.png" };

        nodeEditor = new NodeEditor(this, btnSize, btnSizeSmall, margin, stdTs, edgeRad, dark, darkest, light, lighter, lightest, border, textCol, textDark, textYShift, myNodeSettingsPath, btnP, nodeP1, nodeP2, stdFont);
    }

    public void initializeThemeScreen() {
        String[] pp2 = { absPathPictos + "colorPicker.png", absPathPictos + "restart.png", absPathPictos + "brightTheme.png", absPathPictos + "darkTheme.png" };
        themeScreen = new ThemeScreen(this, btnSize, btnSizeSmall, margin, stdTs, edgeRad, dark, darkest, light, lighter, lightest, border, textCol, textDark, textYShift, myThemeScreenPath, pp2, stdFont);
    }

    public void initializeSettingsScreen() {
        String[] p1 = { absPathPictos + "masterOrSlave.png", absPathPictos + "blenderExeFolder.png", absPathPictos + "imageFolder.png", absPathPictos + "pathToCloud.png", absPathPictos + "personalData.png", absPathPictos + "checkmark.png", absPathPictos + "selectFolder.png" };
        String[] p2 = { absPathPictos + "volume.png", absPathPictos + "folderStructure.png", absPathPictos + "folder.png", absPathPictos + "file.png", absPathPictos + "arrowLeft.png", absPathPictos + "arrowRight.png", absPathPictos + "rename.png", absPathPictos + "search.png", absPathPictos + "copy.png", absPathPictos + "cutFolder.png", absPathPictos + "pasteFolder.png", absPathPictos + "addFolder.png", absPathPictos + "deleteFolder.png", absPathPictos + "deleteFile.png", absPathPictos + "questions.png", absPathPictos + "cross.png", absPathPictos + "checkmark.png", absPathPictos + "arrowUp.png", absPathPictos + "arrowDown.png" };
        String[] fsetupPictos = { absPathPictos + "settings.png", absPathPictos + "questions.png" };
        settingsScreen = new SettingsScreen(this, btnSize, btnSizeSmall, stdTs, subtitleTs, margin, edgeRad, textCol, textDark, dark, light, lighter, border, textYShift, mySettingsPath, p1, p2, fileExplorerPaths, fsetupPictos, stdFont);

    }

    private void initializeQuestionScreen() {
        String[] pp3 = { absPathPictos + "search.png" };
        questionScreen = new QuestionScreen(this, btnSize, btnSizeSmall, margin, stdTs, edgeRad, dark, darkest, light, lighter, lightest, border, textCol, textDark, border, textYShift, pp3, fileExplorerPaths, stdFont);
    }

    public void initializeClassInstancesSlave() {

        if (mode != 2) {
            if (jf.getBounds().getHeight() > stdScreenDimension.y + windowTopBarHeight) {
                jf.setSize((int) stdScreenDimension.x, (int) stdScreenDimension.y + windowTopBarHeight);
            }
        }

        String[] p3 = { absPathPictos + "home.png", absPathPictos + "settings.png", absPathPictos + "themeSettings.png", absPathPictos + "questions.png" };
        for (int i = 0; i < mainButtonsSlave.length; i++) {
            mainButtonsSlave[i] = new ImageButton(this, btnSize / 2 + margin + btnSize * i + margin * i, btnSize / 2 + margin, btnSize, btnSize, stdTs, margin, edgeRad, -1, textYShift, true, false, textCol, light, p3[i], modeNamesSlaves[i], null);
        }

        // initialize mode 0 --> homeScreenSlaves-----------------
        String[] hSSpp = {};
        homeScreenSlaves = new HomeScreenSlaves(this, stdTs, edgeRad, margin, btnSizeLarge, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border, textYShift, hSSpp, stdFont);
        // initialize mode 0 --> homeScreenSlaves-----------------

        initializeSettingsScreen();
        initializeThemeScreen();
        initializeQuestionScreen();
    }

    @Override
    public void draw() {

        background(dark);

        if (mode == 0) {
            loadingScreen.render();
        }

        if (isMaster) {
            if (mode != 2) {
                if (jf.getBounds().getHeight() > stdScreenDimension.y + windowTopBarHeight) {
                    jf.setSize((int) stdScreenDimension.x, (int) stdScreenDimension.y + windowTopBarHeight);
                }
            }

            if (loadingScreen.getInstanciatedClasses()) {

                if (mode == 1) {
                    homeScreen.render();
                }

                if (mode == 2) {
                    nodeEditor.render();
                }

                if (mode == 3) {
                    settingsScreen.render();
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
                if (mode == 101) {
                    renderOverview.render();
                }
            }
        } else {
            if (loadingScreen.getInstanciatedClasses()) {

                if (mode == 1) {
                    homeScreenSlaves.render();
                }
                if (mode == 2) {
                    settingsScreen.render();
                }
                if (mode == 3) {
                    themeScreen.render();
                }
                if (mode == 4) {
                    questionScreen.render();
                }
            }
        }
    }

    public void renderMainButtonsMaster() {
        if (mainButtonsMaster[0].getClickCount() % 2 == 0) {
            int fillUpBarW = width - (mainButtonsMaster[mainButtonsMaster.length - 1].getX() + btnSize / 2 + margin * 2);
            int fillUpBarX = mainButtonsMaster[mainButtonsMaster.length - 1].getX() + btnSize / 2 + margin + fillUpBarW / 2;
            fill(light);
            stroke(light);
            rect(fillUpBarX, btnSize / 2 + margin, fillUpBarW, btnSize, edgeRad);
            textFont(stdFont);
            textSize(subtitleTs);
            fill(textDark);
            textAlign(CENTER, CENTER);
            if (mode - 1 >= 0) {
                String titleBarText = APKName + " | " + APKDescription + " | " + modeNamesMaster[mode - 1];

                if (textWidth(titleBarText) < fillUpBarW) {
                    text(titleBarText, fillUpBarX, btnSize / 2 + margin);
                }
            }
        }

        for (int i = mainButtonsMaster.length - 1; i >= 0; i--) {
            if (mainButtonsMaster[0].getClickCount() % 2 == 0 || i == 0) {
                mainButtonsMaster[i].render();
            }
            if (mainButtonsMaster[i].getIsClicked() == true) {
                switch (i) {
                case 0:
                    for (int i2 = 1; i2 < mainButtonsMaster.length; i2++) {
                        if (mainButtonsMaster[0].getClickCount() % 2 != 0) {
                            mainButtonsMaster[i2].setPos(mainButtonsMaster[0].getX(), mainButtonsMaster[0].getY());
                        } else {
                            mainButtonsMaster[i2].setPos(btnSize / 2 + margin + btnSize * i2 + margin * i2, btnSize / 2 + margin);
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
                mainButtonsMaster[i].setIsClicked(false);
            }
        }
    }

    public void renderMainButtonsSlave() {
        for (int i = mainButtonsSlave.length - 1; i >= 0; i--) {
            mainButtonsSlave[i].render();
            if (mainButtonsSlave[i].getIsClicked()) {
                mode = i + 1;
                mainButtonsSlave[i].setIsClicked(false);
            }
        }
        fill(mainButtonsSlave[0].getBgCol());
        stroke(mainButtonsSlave[0].getBgCol());
        int fillUpBarW = width - (mainButtonsSlave[mainButtonsSlave.length - 1].getX() + btnSize / 2 + margin * 2);
        int fillUpBarX = mainButtonsSlave[mainButtonsSlave.length - 1].getX() + btnSize / 2 + margin + fillUpBarW / 2;
        rect(fillUpBarX, mainButtonsSlave[0].getY(), fillUpBarW, mainButtonsSlave[0].getH(), edgeRad);
        textFont(stdFont);
        textSize(subtitleTs);
        fill(textDark);
        textAlign(CENTER, CENTER);
        if (mode - 1 >= 0) {
            String titleBarText = APKName + " | " + APKDescription + " | " + modeNamesSlaves[mode - 1];

            if (textWidth(titleBarText) < fillUpBarW) {
                text(titleBarText, fillUpBarX, btnSize / 2 + margin);
            }
        }
    }

    @Override
    public void mousePressed() {
        if (isMaster) {
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
                if (mode == 101) {
                    renderOverview.onMousePressed(mouseButton);
                }
            }
        } else {
            if (loadingScreen.getInstanciatedClasses()) {
                if (mode == 1) {
                    homeScreenSlaves.onMousePressed(mouseButton);
                }
                if (mode == 2) {
                    settingsScreen.onMousePressed(mouseButton);
                }
                if (mode == 3) {
                    themeScreen.onMousePressed();
                }
                if (mode == 4) {
                    questionScreen.onMousePressed();
                }
            }
        }
    }

    @Override
    public void mouseReleased() {
        if (isMaster) {
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
                if (mode == 101) {
                    renderOverview.onMouseReleased(mouseButton);
                }
            }
        } else {
            if (loadingScreen.getInstanciatedClasses()) {
                if (mode == 1) {
                    homeScreenSlaves.onMouseReleased(mouseButton);
                }
                if (mode == 2) {
                    settingsScreen.onMouseReleased(mouseButton);
                }
                if (mode == 3) {
                    themeScreen.onMouseReleased();
                }
                if (mode == 4) {
                    questionScreen.onMouseReleased();
                }
            }
        }

    }

    @Override
    public void mouseWheel(MouseEvent event) {
        float e = event.getCount();

        if (isMaster) {
            if (loadingScreen.getInstanciatedClasses()) {

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
                if (mode == 101) {
                    renderOverview.onScroll(e);
                }
            }
        } else {
            if (loadingScreen.getInstanciatedClasses()) {
                if (mode == 1) {
                    homeScreenSlaves.onScroll(e);
                }
                if (mode == 2) {
                    settingsScreen.onScroll(e);
                }
                if (mode == 3) {
                    themeScreen.onScroll(e);
                }
                if (mode == 4) {
                    questionScreen.onScroll(e);
                }
            }
        }
    }

    @Override
    public void keyPressed() {
        if (isMaster) {
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
                if (mode == 101) {
                    renderOverview.onKeyPressed(key);
                }
            }
        } else {
            if (loadingScreen.getInstanciatedClasses()) {
                if (mode == 1) {
                    homeScreenSlaves.onKeyPressed(key);
                }
                if (mode == 2) {
                    settingsScreen.onKeyPressed(key);
                }
                if (mode == 3) {

                }
                if (mode == 4) {
                    questionScreen.onKeyPressed(key);
                }
            }
        }
    }

    @Override
    public void keyReleased() {
        if (isMaster) {
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
                if (mode == 101) {
                    renderOverview.onKeyReleased(key);
                }
            }
        } else {
            if (loadingScreen.getInstanciatedClasses()) {
                if (mode == 1) {
                    homeScreenSlaves.onKeyReleased(key);
                }
                if (mode == 2) {
                    settingsScreen.onKeyReleased(key);
                }
                if (mode == 3) {
                }
                if (mode == 4) {
                    questionScreen.onKeyReleased(key);
                }
            }
        }
    }

    public int getMode() {
        return mode;
    }

    public Boolean getIsMaster() {
        return isMaster;
    }

    public PFont getStdFont() {
        return stdFont;
    }

    public PVector getStdScreenDimensions() {
        return stdScreenDimension;
    }

    public ImageButton[] getMainButtonsMaster() {
        return mainButtonsMaster;
    }

    public ImageButton[] getMainButtonsSlave() {
        return mainButtonsSlave;
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

    public RenderOverview getRenderOverview() {
        return renderOverview;
    }

    public String getPCName() {
        if (loadingScreen.getInstanciatedClasses()) {
            EditText et = settingsScreen.getEditText();
            if (et.getStrList().get(0).length() > 0) {
                return et.getStrList().get(0);
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    public String getSavePath(String basePath) {
        return "output" + getPCName() + "/" + basePath;
    }

    public void setMode(int setMode) {
        mode = setMode;
    }

    public void setIsMaster(Boolean state) {
        isMaster = state;
    }
    public void setMasterComandPath(String setComPath) {
    	masterCommandSavePath=setComPath;
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