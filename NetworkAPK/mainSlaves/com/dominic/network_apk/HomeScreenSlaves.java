package com.dominic.network_apk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.css.ElementCSSInlineStyle;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.source.tree.Tree;

import processing.core.PApplet;
import processing.core.PFont;

public class HomeScreenSlaves {
    private int stdTs, edgeRad, margin, btnSize, btnSizeSmall, dark, light, lighter, lightest, textCol, textDark, border, red, green, cpuCores=0;
    private int renderMode; // rendermode --> 0=render files, 1=render on
                            // sheepit,2=sleeping
    private float textYShift;
    private Boolean allWorking = true;
    private String pathToCloud, pcAlias, pcFolderName, cpuName = "", gpuName = "";
    private String[] pictoPaths;
    private long curTime, prevLastModified,lastLogTime=0;
    private PFont stdFont;
    private PApplet p;
    private ImageButton[] mainButtons;
    private ImageButton cancelRendering_ImageButton;
    private MainActivity mainActivity;
    private PictogramImage fileRendering_PictogramImage, sheepitRendering_PictogramImage, sleeping_PictogramImage;
    private TimeField timeField;
    private StrengthTestHelper strengthTestHelper;
    private JsonHelper jsonHelper;
    private PCInfoHelper pcInfoHelper;
    private FileInteractionHelper fileInteractionHelper;
    private ArrayList<MakeToast> makeToasts = new ArrayList<MakeToast>();
    private Thread getSpecInfoThread, startTestOnGPUThread;
    // StrengthTest files --------------------------------------------------
    private File blendFile, ressourceBlendFile, ressourceRandomSeed, ressourceForceGPURendering, randomSeedFile, forceGPURenderingFile;
    // StrengthTest files --------------------------------------------------

    private CommandExecutionHelper commandExecutionHelper;

    public HomeScreenSlaves(PApplet p, int stdTs, int edgeRad, int margin, int btnSizeLarge, int btnSize, int btnSizeSmall, int dark, int light, int lighter, int textCol, int textDark, int border, int red, int green, float textYShift, String[] pictoPaths, PFont stdFont) {
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
        this.red = red;
        this.green = green;
        this.textYShift = textYShift;
        this.pictoPaths = pictoPaths;
        this.stdFont = stdFont;
        mainActivity = (MainActivity) p;

        pcInfoHelper = new PCInfoHelper(p);
        fileInteractionHelper = new FileInteractionHelper(p);
        commandExecutionHelper = new CommandExecutionHelper(p);
        strengthTestHelper= new StrengthTestHelper(p, "logData",this);

        if (mainActivity.getIsMaster()) {
            mainButtons = mainActivity.getMainButtonsMaster();
        } else {
            mainButtons = mainActivity.getMainButtonsSlave();
        }

        sheepitRendering_PictogramImage = new PictogramImage(p, p.width / 2, p.height / 2, btnSizeLarge, btnSizeLarge, margin, stdTs, edgeRad, textCol, textYShift, false, false, pictoPaths[0], "Rendering on Sheepit", null);
        sleeping_PictogramImage = new PictogramImage(p, p.width / 2, p.height / 2, btnSizeLarge, btnSizeLarge, margin, stdTs, edgeRad, textCol, textYShift, false, false, pictoPaths[1], "sleeping", null);
        fileRendering_PictogramImage = new PictogramImage(p, p.width / 2, p.height / 2, btnSizeLarge, btnSizeLarge, margin, stdTs, edgeRad, textCol, textYShift, false, false, pictoPaths[2], "Rendering file", null);

        timeField = new TimeField(p, margin, p.height - btnSizeSmall / 2 - margin, btnSizeLarge, stdTs + margin * 2, stdTs, margin, edgeRad, textCol, light, false, false, true, "Timestamp: ", "", stdFont, null);
        timeField.setPos(timeField.getW() / 2 + margin, timeField.getY());
        pathToCloud = mainActivity.getPathToCloud();
        pcAlias = mainActivity.getPCName();
        pcFolderName = mainActivity.getPCFolderName();
        if (pathToCloud.length() > 0) {
            while (p.str(pathToCloud.charAt(pathToCloud.length() - 1)) == "\\") {
                pathToCloud = pathToCloud.substring(0, pathToCloud.length() - 1);
            }
        }
        if (pathToCloud == null) {
            pathToCloud = "";
        }
        if (pcAlias == null) {
            pcAlias = "";
        }

        cpuName = pcInfoHelper.getCPUName();
        getSpecInfoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                gpuName = pcInfoHelper.getGPUName();
            }
        });
        getSpecInfoThread.start();
        cpuCores = pcInfoHelper.getAvailableProcessors();

        jsonHelper = new JsonHelper(p);

        setData();
    }

    public void render() {
        if (mainActivity.getIsMaster()) {
            mainActivity.renderMainButtonsMaster();
        } else {
            mainActivity.renderMainButtonsSlave();
        }

        if (renderMode == 0) {
            fileRendering_PictogramImage.render();
        }
        if (renderMode == 1) {
            sheepitRendering_PictogramImage.render();
        }
        if (renderMode == 2) {
            sleeping_PictogramImage.render();
        }
        timeField.render();

        for (int i = 0; i < makeToasts.size(); i++) {
            MakeToast m = makeToasts.get(i);
            if (m.remove) {
                makeToasts.remove(i);
            } else {
                m.render();
            }
        }
    }

    public void calcBackgroundTasks() {
        curTime = pcInfoHelper.getCurTime();
        if (curTime - lastLogTime > mainActivity.getStdTimeIntervall()) {
            logData();
            lastLogTime = curTime;
        }

        File cmdFile = new File(mainActivity.getMasterCommandFilePath());
        if (strengthTestHelper.getStrengthTestStatus()==0 || cmdFile.lastModified() != prevLastModified) {
            //checkForCommands();
            strengthTestHelper.checkForStrengthTestCommands(cpuName, gpuName, getSpecInfoThread);
            prevLastModified = cmdFile.lastModified();
        }
      
        /*
        if (curTime - lastLogTime2 > mainActivity.getSuperShortTimeIntervall()) {
            if (strengthTestStatus == 0) {
                p.println("check", cpuName, gpuName);
                //checkIfStrengthTestCPUIsFinished();
                //checkIfStrengthTestGPUIsFinished();
            }
            lastLogTime2 = curTime;
        }

        if (startStrengthTest && strengthTestStatus < 0) {
            //startStrengthTest();
            strengthTestStatus = 0;
            startStrengthTest = false;
        }*/
        // p.println("infoThreadAlive",getSpecInfoThread.isAlive());
        // p.println(strengthTestStatus);
        if (startTestOnGPUThread != null) {
            // p.println(startTestOnGPUThread.isAlive());
        }
    }

    private void logData() {
        jsonHelper.clearArray();
        JSONObject settingsDetails = new JSONObject();
        JSONObject settingsObject = new JSONObject();

        settingsDetails.put("logTime", curTime);
        settingsDetails.put("readableTime", pcInfoHelper.getReadableTime());
        settingsDetails.put("renderMode", renderMode);
        settingsDetails.put("cpuCores", cpuCores);
        settingsDetails.put("cpuName", cpuName);
        settingsDetails.put("gpuName", gpuName);
        settingsDetails.put("pcStrengthCPU", strengthTestHelper.getStrengthCPU());
        settingsDetails.put("pcStrengthGPU", strengthTestHelper.getStrengthGPU());
        if (strengthTestHelper.getFinishedTestingCPU() && strengthTestHelper.getFinishedTestingGPU()) {
           // p.println("pcStrength: ", pcStrengthCPU, pcStrengthGPU);
            p.println("pcStrength: ", strengthTestHelper.getStrengthCPU(), strengthTestHelper.getStrengthGPU());
            strengthTestHelper.setFinishedTestingCPU(false);
            strengthTestHelper.setFinishedTestingGPU(false);
            strengthTestHelper.setStrengthTestStatus(1);
            p.println("now deleting ---------------------");
            fileInteractionHelper.deleteFolder(blendFile.getParentFile().getAbsolutePath());

        }
        settingsDetails.put("strengthTestStatus", strengthTestHelper.getStrengthTestStatus());

        settingsObject.put("SystemLog", settingsDetails);

        String jsonPath = mainActivity.getPathToPCFolder() + "\\" + pcAlias + "\\" + mainActivity.getLogFileName();
        if (fileInteractionHelper.createParentFolders(jsonPath)) {
            jsonHelper.appendObjectToArray(settingsObject);
            allWorking = jsonHelper.writeData(jsonPath);
            if (allWorking) {
                makeToasts.add(new MakeToast(p, p.width / 2, timeField.getY(), stdTs, margin, edgeRad, 100, light, textCol, textYShift, false, "Saved LogFile", stdFont, null));
                timeField.setCol(green);
                timeField.setPostfix(" -Everything working correctly");
            } else {
                String errorMessage = "Error: can't write logFile.Path to cloud: '" + mainActivity.getPathToCloud() + "' might be incorrect";
                makeToasts.add(new MakeToast(p, p.width / 2, timeField.getY(), stdTs, margin, edgeRad, errorMessage.length() * 3, light, textCol, textYShift, false, errorMessage, stdFont, null));
                timeField.setCol(red);
                timeField.setPostfix(" -Some errors occured");
            }
        }
    }

    private void setData() {
        try {
            if (pcAlias.length() > 0) {
                String jsonPath = mainActivity.getPathToPCFolder() + "\\" + pcAlias + "\\" + mainActivity.getLogFileName();
                JSONArray loadedSettingsData = jsonHelper.getData(jsonPath);
                if (loadedSettingsData.isEmpty()) {

                } else {
                    JsonObject jsonObject = new JsonParser().parse(loadedSettingsData.get(0).toString()).getAsJsonObject();
                    try {
                        int loadedPcStrengthCPU = Integer.parseInt(jsonObject.getAsJsonObject("SystemLog").get("pcStrengthCPU").getAsString());
                        int loadedPcStrengthGPU = Integer.parseInt(jsonObject.getAsJsonObject("SystemLog").get("pcStrengthGPU").getAsString());

                        if (loadedPcStrengthCPU >= 0) {
                            strengthTestHelper.setStrengthCPU(loadedPcStrengthCPU);
                        }
                        if (loadedPcStrengthGPU >= 0) {
                            strengthTestHelper.setStrengthGPU(loadedPcStrengthGPU);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
/*
    private void checkForCommands() {
        JSONArray loadedData = jsonHelper.getData(mainActivity.getMasterCommandFilePath());
        if (loadedData.isEmpty()) {
        } else {
            try {
                String modeName = mainActivity.getModeNamesMaster()[5];
                JSONObject loadedObject = (JSONObject) (loadedData.get(5));
                loadedObject = (JSONObject) loadedObject.get(modeName);
                Boolean startTesting = Boolean.parseBoolean(loadedObject.get("startTesting").toString());
                if (startTesting != null) {
                    if (startTesting == false) {
                        startStrengthTest = false;
                        p.println("stop from check for commands");
                        stopStrengthTest();
                        if (strengthTestStatus == 0) {
                            strengthTestStatus = -1;
                            p.println("set to -1");
                        }
                    }
                    if (startTesting != prevStartTesting) {
                        if (!startTesting) {
                            p.println("set to -1 2");
                            strengthTestStatus = -1;
                        }
                        startStrengthTest = startTesting;
                    }
                    prevStartTesting = startTesting;

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // strengthtTest ---------------------------------
    private void startStrengthTest() {
        copyStrengthTestResources(true);

        if (cpuName.length() > 0) {
            startTestOnCPU();
            String message = "Strength test started on CPU";
            makeToasts.add(new MakeToast(p, p.width / 2, timeField.getY(), stdTs, margin, edgeRad, message.length() * 3, light, textCol, textYShift, false, message, stdFont, null));
        } else {
            finishedTestingCPU = true;
        }
        if ((getSpecInfoThread.isAlive() || gpuName.length() > 0) && (startTestOnGPUThread == null || !startTestOnGPUThread.isAlive())) {
            startTestOnGPUThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!Thread.currentThread().isInterrupted()) {
                        while (getSpecInfoThread.isAlive()) {
                            startTimeGPUTest = curTime;
                            p.delay(1000);
                            p.println("get gpu name");
                        }
                        if (gpuName.length() > 0) {
                            while (!checkIfStrengthTestCPUIsFinished()) {
                                startTimeGPUTest = curTime;
                                p.delay(1000);
                                p.println("while check cpuForFinished()");
                            }

                            cpuTestDuration = curTime - startTimeCPUTest;
                            pcStrengthCPU = calcStrength((int) cpuTestDuration);
                            p.println("strTestStat: ", strengthTestStatus);
                            if (strengthTestStatus != -1) {
                                startTestOnGPU();
                                String message = "Strength test started on GPU";
                                makeToasts.add(new MakeToast(p, p.width / 2, timeField.getY(), stdTs, margin, edgeRad, message.length() * 3, light, textCol, textYShift, false, message, stdFont, null));
                                while (strengthTestStatus == 0) {
                                    p.delay(1000);
                                }
                            }
                        } else {
                            finishedTestingGPU = true;
                        }
                    }
                }

            });
            startTestOnGPUThread.start();
        }
    }

    private void startTestOnCPU() {
        startTimeCPUTest = System.nanoTime() / 1000000000;
        if (blendFile.exists() && randomSeedFile.exists() && forceGPURenderingFile.exists()) {
            // create starter .bat ---------------------------
            String pathToRenderLog = blendFile.getParentFile().getAbsolutePath() + "\\log\\logfileRendering.txt";
            pathToStrengthTestResult = blendFile.getParentFile().getAbsolutePath() + "\\results\\strengthTestRenderResultCPU";
            fileInteractionHelper.createParentFolders(pathToRenderLog);
            fileInteractionHelper.createParentFolders(pathToStrengthTestResult);
            p.println();
            String[] commands = { "cd " + new File(mainActivity.getSettingsScreen().getPathSelectors()[0].getPath()).getParentFile().getAbsolutePath(), "ECHO -----------------------------", "ECHO Started Renderprocess on CPU", "ECHO -----------------------------", "blender -b \"" + blendFile.getAbsolutePath() + "\" -P \"" + randomSeedFile.getAbsolutePath() + "\" -o \"" + pathToStrengthTestResult + "\" -F PNG -f 2 >>" + pathToRenderLog, "EXIT" };

            Boolean isExecuted = commandExecutionHelper.executeMultipleCommands(commands, strengthTestTerminalWindowName);
            if (isExecuted) {
            } else {
                strengthTestStatus = -1;
                p.println("failed to start on CPU");
            }
        }
    }

    private void startTestOnGPU() {
        startTimeGPUTest = System.nanoTime() / 1000000000;
        if (blendFile.exists() && randomSeedFile.exists() && forceGPURenderingFile.exists()) {
            p.println("started on gpu");
            // create starter .bat ---------------------------
            String pathToRenderLog = blendFile.getParentFile().getAbsolutePath() + "\\log\\logfileRendering.txt";
            pathToStrengthTestResult = blendFile.getParentFile().getAbsolutePath() + "\\results\\strengthTestRenderResultGPU";
            fileInteractionHelper.createParentFolders(pathToRenderLog);
            fileInteractionHelper.createParentFolders(pathToStrengthTestResult);
            String[] commands = { "cd " + new File(mainActivity.getSettingsScreen().getPathSelectors()[0].getPath()).getParentFile().getAbsolutePath(), "ECHO -----------------------------", "ECHO Started Renderprocess on GPU", "ECHO -----------------------------", "blender -b \"" + blendFile.getAbsolutePath() + "\" -P \"" + randomSeedFile.getAbsolutePath() + "\" -P \"" + forceGPURenderingFile.getAbsolutePath() + "\" -o \"" + pathToStrengthTestResult + "\" -F PNG -f 2 >>" + pathToRenderLog, "EXIT" };

            Boolean isExecuted = commandExecutionHelper.executeMultipleCommands(commands, strengthTestTerminalWindowName);
            if (isExecuted) {
            } else {
                strengthTestStatus = -1;
                p.println("failed to start on GPU");
            }
        }
    }

    private void stopStrengthTest() {
        p.println("stoping sterngtht test");
        commandExecutionHelper.killTaskByWindowtitle(strengthTestTerminalWindowName);
        strengthTestStatus = -1;
        startTestOnGPUThread.interrupt();
        p.println("interupt: ", startTestOnGPUThread.isAlive());
    }

    private Boolean checkIfStrengthTestCPUIsFinished() {
        finishedTestingCPU = false;
        if (cpuName.length() > 0 == false) {
            finishedTestingCPU = true;
        } else {
            File strengthTestResultFile = new File(pathToStrengthTestResult);
            String[] strengthTestResults = fileInteractionHelper.getFoldersAndFiles(strengthTestResultFile.getParentFile().getAbsolutePath(), false);
            // finishedTestingCPU = strengthTestResults.length > 0 == true;
            if (strengthTestResults != null && strengthTestResults.length > 0) {
                for (int i = 0; i < strengthTestResults.length; i++) {
                    String[] m1 = p.match(strengthTestResults[i].toUpperCase(), "CPU");
                    if (m1 != null) {
                        finishedTestingCPU = true;
                        break;
                    }
                }
            }
            if (curTime - startTimeCPUTest > maxTestTime) {
                finishedTestingCPU = true;
                p.println("stop from checkCPUFinished");
                stopStrengthTest();
            }
        }
        if (strengthTestStatus != 0) {
            finishedTestingCPU = true;
        }
        if (finishedTestingCPU) {
            if (!startTestOnGPUThread.isAlive()) {
                p.println("loging from cpuCheck");
                logData();
            }
        }
        return finishedTestingCPU;
    }

    private Boolean checkIfStrengthTestGPUIsFinished() {
        finishedTestingGPU = false;
        if (gpuName.length() > 0 == false && !getSpecInfoThread.isAlive()) {
            finishedTestingGPU = true;
        } else {
            File strengthTestResultFile = new File(pathToStrengthTestResult);
            String[] strengthTestResults = fileInteractionHelper.getFoldersAndFiles(strengthTestResultFile.getParentFile().getAbsolutePath(), false);
            if (strengthTestResults != null && strengthTestResults.length > 0) {
                for (int i = 0; i < strengthTestResults.length; i++) {
                    String[] m1 = p.match(strengthTestResults[i].toUpperCase(), "GPU");
                    if (m1 != null) {
                        finishedTestingGPU = true;
                        break;
                    }
                }
            }
            if (curTime - startTimeGPUTest > maxTestTime) {
                finishedTestingGPU = true;
                p.println("stop from checkGPUFinished");

                stopStrengthTest();
            }
        }
        if (strengthTestStatus != 0) {
            finishedTestingGPU = true;
        }
        if (finishedTestingGPU) {
            gpuTestDuration = curTime - startTimeGPUTest;
            pcStrengthGPU = calcStrength((int) gpuTestDuration);
            if (strengthTestStatus != -1) {
                p.println("log data from gpu");
                logData();
            }
        }
        return finishedTestingGPU;
    }

    private Boolean copyStrengthTestResources(Boolean deleteResultsFolder) {
        Boolean copiedFiles = true, blendFileCopied = false, randomSeedCopied = false, forceGPURenderingCopied = false;

        String relativeFilePathBlendfile = "/blendFiles/strengthTest.blend";
        String copyFromPathBlendfile = getClass().getResource(relativeFilePathBlendfile).getPath().toString();
        ressourceBlendFile = new File(copyFromPathBlendfile);
        String destinationBlendfile = fileInteractionHelper.getAbsolutePath(mainActivity.getStrengthTestBlendfilePath()) + "\\" + ressourceBlendFile.getName();
        if (deleteResultsFolder) {
            fileInteractionHelper.deleteFolder(new File(destinationBlendfile).getParentFile().getAbsolutePath());
        }
        blendFileCopied = fileInteractionHelper.copyFile(copyFromPathBlendfile, destinationBlendfile);
        blendFile = new File(destinationBlendfile);

        String relativeFilePathRandomSeed = "/pythonScripts/randomSeed.py";
        String copyFromPathRandomSeed = getClass().getResource(relativeFilePathRandomSeed).getPath().toString();
        ressourceRandomSeed = new File(copyFromPathRandomSeed);
        String destinationRandomSeed = fileInteractionHelper.getAbsolutePath(mainActivity.getStrengthTestBlendfilePath()) + "\\" + ressourceRandomSeed.getName();
        randomSeedCopied = fileInteractionHelper.copyFile(copyFromPathRandomSeed, destinationRandomSeed);
        randomSeedFile = new File(destinationRandomSeed);

        String relativeFileForceGPURendering = "/pythonScripts/forceGPURendering.py";
        String copyFromPathForceGPURendering = getClass().getResource(relativeFileForceGPURendering).getPath().toString();
        ressourceForceGPURendering = new File(copyFromPathForceGPURendering);
        String destinationForceGPURendering = fileInteractionHelper.getAbsolutePath(mainActivity.getStrengthTestBlendfilePath()) + "\\" + ressourceForceGPURendering.getName();
        forceGPURenderingCopied = fileInteractionHelper.copyFile(copyFromPathForceGPURendering, destinationForceGPURendering);
        forceGPURenderingFile = new File(destinationForceGPURendering);

        p.println(blendFile.getAbsolutePath(), "\n", randomSeedFile.getAbsolutePath(), "\n", forceGPURenderingFile.getAbsolutePath());

        return (blendFileCopied && randomSeedCopied && forceGPURenderingCopied) == true;
    }

    private int calcStrength(int duration) {
        return (int) (100.0 / (duration / (referenceRendertime / 100.0)) * 100.0);
    }

    // strengthTest --------------------------------------------
     */
    public void onMousePressed(int mouseButton) {
        for (int i = 0; i < mainButtons.length; i++) {
            mainButtons[i].onMousePressed();
        }
    }

    public void onMouseReleased(int mouseButton) {
        for (int i = 0; i < mainButtons.length; i++) {
            mainButtons[i].onMouseReleased();
        }
    }

    public void onKeyPressed(char key) {
    }

    public void onKeyReleased(char key) {
        for (int i = 0; i < mainButtons.length; i++) {
            mainButtons[i].onKeyReleased(key);
        }
    }

    public void onScroll(float e) {

    }

    public Thread getStartTestOnGPUThread() {
        return startTestOnGPUThread;
    }

}
