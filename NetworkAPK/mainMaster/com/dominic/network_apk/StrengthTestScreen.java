package com.dominic.network_apk;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class StrengthTestScreen {

    private int mode, btnSize, btnSizeSmall, margin, stdTs, edgeRad, dark, darkest, light, lighter, lightest, border, textCol, textDark, red, green;
    private float textYShift;
    private Boolean fileExplorerIsOpen = false, startedTest = false;
    private String mySavePath;
    private String[] pictoPaths, hoLiPictoPaths, startList = {}, allPCNames, pcListTexts;
    private float[] listX, listW;
    private int[] allPCStatus, allPCStrengthsCPU, allPCStrengthsGPU;
    private long curTime, prevTime, prevTime2;
    private PFont stdFont;
    private PApplet p;
    private HorizontalList strengthTest_HorizontalList;
    private ImageButton startTest_ImageButton;
    private MainActivity mainActivity;
    private ImageButton[] mainButtons;
    private ArrayList<Node> allConnectedNodes = new ArrayList<>();
    private JsonHelper jsonHelper;
    private FileInteractionHelper fileInteractionHelper;
    private Thread strengthTestThread, checkForFinishedThread;

    public StrengthTestScreen(PApplet p, int mode, int btnSize, int btnSizeSmall, int margin, int stdTs, int edgeRad, int dark, int darkest, int light, int lighter, int lightest, int border, int textCol, int textDark, int red, int green, float textYShift, String mySavePath, String[] pictoPaths, String[] hoLiPictoPaths, PFont stdFont) {
        this.mode = mode;
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
        this.red = red;
        this.green = green;
        this.textYShift = textYShift;
        this.mySavePath = mySavePath;
        this.pictoPaths = pictoPaths;
        this.hoLiPictoPaths = hoLiPictoPaths;
        this.stdFont = stdFont;
        this.p = p;

        mainActivity = (MainActivity) p;
        if (mainActivity.getIsMaster()) {
            mainButtons = mainActivity.getMainButtonsMaster();
        } else {
            mainButtons = mainActivity.getMainButtonsSlave();
        }
        jsonHelper = new JsonHelper(p);
        fileInteractionHelper = new FileInteractionHelper(p);
        setupAll();
        controllStrengthTest(false);

    }

    public void render() {
        if (mainActivity.getIsMaster()) {
            mainActivity.renderMainButtonsMaster();
        } else {
            mainActivity.renderMainButtonsSlave();
        }

        strengthTest_HorizontalList.render();
        startTest_ImageButton.render();

        if (strengthTest_HorizontalList.getList().length > 0) {
            if (strengthTest_HorizontalList.getIsShifted()) {

                listX = new float[strengthTest_HorizontalList.getListX().length];
                listW = new float[strengthTest_HorizontalList.getListW().length];
                listX = strengthTest_HorizontalList.getListX();
                listW = strengthTest_HorizontalList.getListW();

                /*
                 * for (int i = 0; i < allPCPictos.length; i++) { }
                 */
                strengthTest_HorizontalList.setIsShifted(false);
            }
            for (int i = strengthTest_HorizontalList.getFirstDisplayedInd(); i <= strengthTest_HorizontalList.getLastDisplayedInd(); i++) {
                try {
                    if (allPCStatus[i] < 2) {
                        p.stroke(green);
                    } else {
                        p.stroke(red);
                    }

                    p.fill(lighter);
                    p.rect(listX[i], strengthTest_HorizontalList.getY(), listW[i], strengthTest_HorizontalList.getH() - margin * 2, edgeRad);
                    if (pcListTexts != null) {
                        p.fill(textCol);
                        p.textFont(stdFont);
                        p.textSize(stdTs);
                        p.textAlign(p.CENTER,p.CENTER);
                            p.text(pcListTexts[i], listX[i], strengthTest_HorizontalList.getY());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // update PCList & check for
        // finished---------------------------------------------
        if (allConnectedNodes.size() > 0) {
            curTime = System.nanoTime() / 1000000000;
            if (curTime - prevTime > mainActivity.getSuperShortTimeIntervall()) {
                updateLists();
                if (startedTest) {
                    if (checkForFinishedThread == null || !checkForFinishedThread.isAlive()) {
                        checkForFinishedThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                checkForFinished();
                            }
                        });
                        checkForFinishedThread.start();
                    }
                }
                prevTime = curTime;
            }
        }
        // update PCList & check for
        // finished---------------------------------------------
        // log data---------------------------------
        if (curTime - prevTime2 > mainActivity.getShortTimeIntervall()) {
            prevTime2 = curTime;
        }
        // log data---------------------------------
        // handle buttons---------------------------------------------
        if (startTest_ImageButton.getIsClicked()) {
          if(allConnectedNodes!=null && allConnectedNodes.size()>0) {
            strengthTestThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    controllStrengthTest(startTest_ImageButton.getClickCount() % 2 != 0);
                }
            });
            strengthTestThread.start();
            startTest_ImageButton.setIsClicked(false);
        }
        }
    }

    private void controllStrengthTest(Boolean startTest) {
        Boolean allPCsAreDone = false;
        JSONArray loadedData = new JSONArray();
        JSONObject settingsDetails = new JSONObject();
        JSONObject settingsObject = new JSONObject();
        // give command to all pcs to do test -----------------------------

        loadedData = jsonHelper.getData(mainActivity.getMasterCommandFilePath());
        if (loadedData.isEmpty()) {
        } else {
            try {
                String modeName = mainActivity.getModeNamesMaster()[mode - 1];
                JSONObject loadedObject = (JSONObject) (loadedData.get(mode - 1));
                loadedObject = (JSONObject) loadedObject.get(modeName);
                p.println(loadedObject);
                loadedObject.put("startTesting", startTest);
                settingsObject.put(modeName, loadedObject);
                loadedData.set(mode - 1, settingsObject);
                jsonHelper.setArray(loadedData);
                jsonHelper.writeData(mainActivity.getMasterCommandFilePath());
                startedTest = startTest;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // give command to all pcs to do test -----------------------------
    }

    private void checkForFinished() {
        ArrayList<Node> respondingPCs = new ArrayList<>();
        ArrayList<Node> finishedPCs = new ArrayList<>();

        for (int i = 0; i < allConnectedNodes.size(); i++) {
            Node n = allConnectedNodes.get(i);
            if (allPCStatus[i] < 2) {
                respondingPCs.add(n);
                String jsonPath = mainActivity.getPathToCloud() + "\\" + mainActivity.getPCFolderName() + "\\" + n.getPcSelection_DropdownMenu().getSelectedItem() + "\\" + mainActivity.getLogFileName();
                p.println(jsonPath);
                JSONArray loadedData = jsonHelper.getData(jsonPath);
                if (loadedData.isEmpty()) {
                } else {
                    try {
                        JSONObject loadedObject = (JSONObject) (loadedData.get(0));
                        loadedObject = (JSONObject) loadedObject.get("SystemLog");

                        int strengthTestStatus = Integer.parseInt(loadedObject.get("strengthTestStatus").toString());
                        int pcStrengthCPU = Integer.parseInt(loadedObject.get("pcStrengthCPU").toString());
                        int pcStrengthGPU = Integer.parseInt(loadedObject.get("pcStrengthGPU").toString());

                        if (strengthTestStatus == 0) {
                            p.println("started");
                            allPCStrengthsCPU[i] = -1;
                            allPCStrengthsGPU[i] = -1;
                        }
                        if (strengthTestStatus == 1) {
                            allPCStrengthsCPU[i] = pcStrengthCPU;
                            allPCStrengthsGPU[i] = pcStrengthGPU;
                            finishedPCs.add(n);
                        }
                        if (strengthTestStatus == 2) {
                            p.println("failed");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        allPCStrengthsCPU[i] = -1;
                        allPCStrengthsGPU[i] = -1;

                    }
                }
            } else {
                allPCStrengthsCPU[i] = -1;
                allPCStrengthsGPU[i] = -1;
            }
        }

        if (respondingPCs.size() == finishedPCs.size()) {
            for (int i = 0; i < allConnectedNodes.size(); i++) {
                Node n = allConnectedNodes.get(i);
                n.setPCStrengthCPU(allPCStrengthsCPU[i]);
                n.setPCStrengthGPU(allPCStrengthsGPU[i]);
                p.println(allPCStrengthsCPU[i], allPCStrengthsGPU[i], "strength");
            }
            p.delay(3000);
            controllStrengthTest(false);
            mainActivity.getNodeEditor().saveNodeEditor();
        }
    }

    private void updateLists() {
        p.println("update list");
        if (allConnectedNodes.size() > 0) {
            pcListTexts = new String[allConnectedNodes.size()];
            for (int i = 0; i < strengthTest_HorizontalList.getList().length; i++) {
                Node n = allConnectedNodes.get(i);
                n.checkForSignsOfLife();

                allPCStatus[i] = n.getPcStatus();
                allPCNames[i] = n.getPcSelection_DropdownMenu().getSelectedItem();
                allPCStrengthsCPU[i] = n.getPCStrengthCPU();
                allPCStrengthsGPU[i] = n.getPCStrengthGPU();
                if(allPCNames[i].length()>0==false) {
                    allPCNames[i]="Name unknown";
                }
                String pcStrengthStrCPU = "", pcStrengthStrGPU = "";
                if (allPCStrengthsCPU[i] < 0) {
                    pcStrengthStrCPU = "Untested";
                    pcStrengthStrGPU = "Untested";
                } else {
                    pcStrengthStrCPU = p.str(allPCStrengthsCPU[i]) + "%";
                    pcStrengthStrGPU = p.str(allPCStrengthsGPU[i]) + "%";
                }
                pcListTexts[i] = allPCNames[i] + "\nCPU: " + pcStrengthStrCPU + "  |  GPU: " + pcStrengthStrGPU;
            }
        }
    }

    public void onMousePressed(int mouseButton) {
        strengthTest_HorizontalList.onMousePressed();
        startTest_ImageButton.onMousePressed();
        for (int i = 0; i < mainButtons.length; i++) {
            if (mainButtons[0].getClickCount() % 2 == 0 || i == 0) {
                mainButtons[i].onMousePressed();
            }
        }
    }

    public void onMouseReleased(int mouseButton) {
        strengthTest_HorizontalList.onMouseReleased(mouseButton);
        startTest_ImageButton.onMouseReleased();
        for (int i = 0; i < mainButtons.length; i++) {
            if (mainButtons[0].getClickCount() % 2 == 0 || i == 0) {
                mainButtons[i].onMouseReleased();
            }
        }
    }

    public void onKeyPressed(char key) {

    }

    public void onKeyReleased(char key) {

    }

    public void onScroll(float e) {

    }

    public void setupAll() {
        getConnectedNodes();
        if (allConnectedNodes.size() > 0) {
            startList = new String[allConnectedNodes.size()];
            for (int i = 0; i < startList.length; i++) {
                startList[i] = "------------------------------------";
            }
        }
        allPCStatus = new int[allConnectedNodes.size()];
        allPCNames = new String[allConnectedNodes.size()];
        allPCStrengthsCPU = new int[allConnectedNodes.size()];
        allPCStrengthsGPU = new int[allConnectedNodes.size()];

        String descriptionText = "Connected computers";
        strengthTest_HorizontalList = new HorizontalList(p, p.width / 2 - btnSize / 2 - margin / 2, p.height / 2, p.width - margin * 3 - btnSize, btnSize, margin, edgeRad, stdTs, (int) p.textWidth(descriptionText) + margin * 3 + btnSizeSmall, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border, textYShift, '\\', false, false, false, descriptionText, hoLiPictoPaths, startList, stdFont, null);
        int btnX = strengthTest_HorizontalList.getW() / 2 + margin + strengthTest_HorizontalList.getH() / 2;
        startTest_ImageButton = new ImageButton(p, btnX, 0, strengthTest_HorizontalList.getH(), strengthTest_HorizontalList.getH(), stdTs, margin, edgeRad, -1, textYShift, true, true, textCol, lighter, pictoPaths[0], "Start Testing computer Strength", strengthTest_HorizontalList);

    }

    private void getConnectedNodes() {
        if (!mainActivity.getNodeEditor().getIsSetup()) {
            mainActivity.getNodeEditor().setupAll();
        }
        allConnectedNodes = mainActivity.getNodeEditor().getAllConnectedNodes();
    }

    public int getMode() {
        return mode;
    }
}
