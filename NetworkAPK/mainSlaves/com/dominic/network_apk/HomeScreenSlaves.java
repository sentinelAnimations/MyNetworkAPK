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
    private int stdTs, edgeRad, margin, btnSize, btnSizeSmall, dark, light, lighter, lightest, textCol, textDark, border, red, green, pcStrengthCPU = -1, pcStrengthGPU = -1, strengthTestStatus = -1, cpuCores, maxTestTime = 30 * 60, referenceRendertime = 60; // strengthTestStatus: -1 = null, 0=started,1=finished
    private int renderMode; // rendermode --> 0=render files, 1=render on
                            // sheepit,2=sleeping
    private float textYShift;
    private Boolean allWorking = true, finishedTestingCPU = false, finishedTestingGPU = false, startStrengthTest = false, prevStartTesting = false;
    private String pathToCloud, pcAlias, pcFolderName, cpuName="", gpuName="", strengthTestTerminalWindowName = "strengthTestTerminal", pathToStrengthTestResult = "";
    private String[] pictoPaths;
    private long curTime, lastLogTime, lastLogTime2, startTimeCPUTest, startTimeGPUTest, cpuTestDuration, gpuTestDuration, prevLastModified;
    private PFont stdFont;
    private PApplet p;
    private ImageButton[] mainButtons;
    private ImageButton cancelRendering_ImageButton;
    private MainActivity mainActivity;
    private PictogramImage fileRendering_PictogramImage, sheepitRendering_PictogramImage, sleeping_PictogramImage;
    private TimeField timeField;
    private JsonHelper jsonHelper;
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

        fileInteractionHelper = new FileInteractionHelper(p);
        commandExecutionHelper = new CommandExecutionHelper(p);

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

        cpuName = getCPUName();
        getSpecInfoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                gpuName = getGPUName();
            }
        });
        getSpecInfoThread.start();
        cpuCores = Runtime.getRuntime().availableProcessors();

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

        curTime = System.nanoTime() / 1000000000;
        if (curTime - lastLogTime > mainActivity.getStdTimeIntervall()) {
            logData();
            // checkForCommands();
            lastLogTime = curTime;
        }

        File cmdFile = new File(mainActivity.getMasterCommandFilePath());
        if (cmdFile.lastModified() != prevLastModified) {
            checkForCommands();
            prevLastModified = cmdFile.lastModified();
        }
        if (curTime - lastLogTime2 > mainActivity.getShortTimeIntervall()) {
            if (strengthTestStatus == 0) {
                checkIfStrengthTestCPUIsFinished();
                checkIfStrengthTestGPUIsFinished();
}
            lastLogTime2 = curTime;
        }

        if (startStrengthTest && strengthTestStatus < 0) {
            startStrengthTest();
            strengthTestStatus = 0;
            startStrengthTest = false;
        }
        // p.println("infoThreadAlive",getSpecInfoThread.isAlive());
        p.println(strengthTestStatus);
    }

    private void logData() {
        jsonHelper.clearArray();
        JSONObject settingsDetails = new JSONObject();
        JSONObject settingsObject = new JSONObject();

        settingsDetails.put("logTime", curTime);
        settingsDetails.put("readableTime", timeField.getTimeString());
        settingsDetails.put("renderMode", renderMode);
        settingsDetails.put("cpuCores", cpuCores);
        settingsDetails.put("cpuName", cpuName);
        settingsDetails.put("gpuName", gpuName);
        settingsDetails.put("pcStrengthCPU", pcStrengthCPU);
        settingsDetails.put("pcStrengthGPU", pcStrengthGPU);
        if (finishedTestingCPU && finishedTestingGPU) {
            p.println("pcStrength: ",pcStrengthCPU,pcStrengthGPU);
            finishedTestingCPU = false;
            finishedTestingGPU = false;
            strengthTestStatus = 1;
        }
        settingsDetails.put("strengthTestStatus", strengthTestStatus);

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
                            pcStrengthCPU = loadedPcStrengthCPU;
                        }
                        if (loadedPcStrengthGPU >= 0) {
                            pcStrengthGPU = loadedPcStrengthGPU;
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
                        strengthTestStatus = -1;
                        startStrengthTest = startTesting;
                    }
                    prevStartTesting = startTesting;

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startStrengthTest() {
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
                    while (getSpecInfoThread.isAlive()) {
                        p.delay(1000);
                    }
                    if (gpuName.length() > 0) {
                        while (!checkIfStrengthTestCPUIsFinished()) {
                            p.delay(1000);
                            p.println("while check cpuForFinished()");
                        }

                        cpuTestDuration = curTime - startTimeCPUTest;
                        pcStrengthCPU = calcStrength((int) cpuTestDuration);
                        p.println("strTestStat: ",strengthTestStatus);
                        if (strengthTestStatus != -1) {
                            startTestOnGPU();
                            String message = "Strength test started on GPU";
                            makeToasts.add(new MakeToast(p, p.width / 2, timeField.getY(), stdTs, margin, edgeRad, message.length() * 3, light, textCol, textYShift, false, message, stdFont, null));

                        }
                    } else {
                        finishedTestingGPU = true;
                    }
                }
            });
            startTestOnGPUThread.start();
        }
    }

    private void startTestOnCPU() {
        startTimeCPUTest = System.nanoTime() / 1000000000;
        copyStrengthTestResources(true);
        if (blendFile.exists() && randomSeedFile.exists() && forceGPURenderingFile.exists()) {
            // create starter .bat ---------------------------
            String pathToRenderLog = blendFile.getParentFile().getAbsolutePath() + "\\log\\logfileRendering.txt";
            pathToStrengthTestResult = blendFile.getParentFile().getAbsolutePath() + "\\results\\strengthTestRenderResultCPU";
            fileInteractionHelper.createParentFolders(pathToRenderLog);
            fileInteractionHelper.createParentFolders(pathToStrengthTestResult);
            String[] commands = { "cd " + new File(mainActivity.getSettingsScreen().getPathSelectors()[0].getPath()).getParentFile().getAbsolutePath(), "ECHO -----------------------------", "ECHO Started Renderprocess on CPU", "ECHO -----------------------------", "blender -b \"" + blendFile.getAbsolutePath() + "\" -P \"" + randomSeedFile.getAbsolutePath() + "\" -o \"" + pathToStrengthTestResult + "\" -F PNG -f 2 >>" + pathToRenderLog, "EXIT" };

            Boolean isExecuted = commandExecutionHelper.executeMultipleCommands(commands, strengthTestTerminalWindowName);
            if (isExecuted) {
            } else {
                p.println("failed to start on CPU");
            }
        }
    }

    private void startTestOnGPU() {
        startTimeGPUTest = System.nanoTime() / 1000000000;
        copyStrengthTestResources(cpuName.length() > 0 == false);

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
                p.println("failed to start on GPU");
            }
        }
    }

    private void stopStrengthTest() {
        p.println("stoping sterngtht test");
        commandExecutionHelper.killTaskByWindowtitle(strengthTestTerminalWindowName);
        strengthTestStatus = -1;
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
                logData();
            }
        }
        return finishedTestingCPU;
    }

    private Boolean checkIfStrengthTestGPUIsFinished() {
        finishedTestingGPU = false;
        if (gpuName.length() > 0 == false) {
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
                logData();
            }
            fileInteractionHelper.deleteFolder(blendFile.getParentFile().getAbsolutePath());
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
        return (blendFileCopied && randomSeedCopied && forceGPURenderingCopied) == true;
    }

    private int calcStrength(int duration) {
        return (int) (100.0 / (duration / (referenceRendertime / 100.0)) * 100.0);
    }

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

    private String getCPUName() {
        String cpuName = "";
        String[][] commands = new String[][] { { "CMD", "/C", "WMIC cpu get Name" } };

        for (int i = 0; i < commands.length; i++) {
            try {
                String[] com = commands[i];
                Process process = Runtime.getRuntime().exec(com);
                process.getOutputStream().close();
                // Closing output stream of the process
                String s = null;
                // Reading sucessful output of the command
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                while ((s = reader.readLine()) != null) {
                    String[] m1 = p.match(s.toUpperCase(), "NAME");
                    if (s.length() > 0 && m1 == null) {
                        cpuName = s;
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
                cpuName = "";
            }
        }
        return cpuName.trim();
    }

    private String getGPUName() {
        String gpuName = "";

        try {

            // ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            // InputStream is =
            // classloader.getResourceAsStream(mainActivity.getHomeScreenSlavePath()+
            // "\\SystemInformations.txt");

            String relativeFilePath = mainActivity.getHomeScreenSlavePath() + "\\SystemInformations.txt";
            String absoluteFilePath = fileInteractionHelper.getAbsolutePath(relativeFilePath);

            // Use "dxdiag /t" variant to redirect output to a given file
            new FileInteractionHelper(p).createParentFolders(absoluteFilePath);
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "dxdiag", "/t", absoluteFilePath);
            Process proc = pb.start();
            proc.waitFor();
            BufferedReader br = new BufferedReader(new FileReader(absoluteFilePath));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().startsWith("Card name:")) {
                    String[] splitStr = p.split(line.trim(), ":");
                    if (splitStr.length == 2) {
                        gpuName = splitStr[1].trim();
                    } else {
                        gpuName = line.trim();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return gpuName;
    }
}
