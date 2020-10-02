package com.dominic.network_apk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sun.source.tree.Tree;

import processing.core.PApplet;
import processing.core.PFont;

public class HomeScreenSlaves {
    private int stdTs, edgeRad, margin, btnSize, btnSizeSmall, dark, light, lighter, lightest, textCol, textDark, border, red, green, pcStrength = -1, strengthTestStatus = -1, cpuCores;
    private int renderMode; // rendermode --> 0=render files, 1=render on
                            // sheepit,2=sleeping
    private float textYShift;
    private Boolean allWorking = true, startStrengthTest = false, finishedTesting = false;
    private String pathToCloud, pcAlias, pcFolderName, cpuName, gpuName;
    private String[] pictoPaths;
    private long curTime, lastLogTime, lastLogTime2;
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
        while (p.str(pathToCloud.charAt(pathToCloud.length() - 1)) == "\\") {
            pathToCloud = pathToCloud.substring(0, pathToCloud.length() - 1);
        }
        if (pathToCloud == null) {
            pathToCloud = "";
        }
        if (pcAlias == null) {
            pcAlias = "";
        }

        cpuName = getCPUName();
        gpuName = getGPUName();
        cpuCores = Runtime.getRuntime().availableProcessors();

        jsonHelper = new JsonHelper(p);
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
            checkForCommands();

            lastLogTime = curTime;
        }
        if (curTime - lastLogTime2 > mainActivity.getShortTimeIntervall()) {
            if (startStrengthTest) {
                checkIfStrengthTestIsFinished();
            }
            lastLogTime2 = curTime;
        }

        if (startStrengthTest) {
            if (strengthTestStatus != 0) {
                startStrengthTest();
                strengthTestStatus = 0;
            }
        }
    }

    private void logData() {

        jsonHelper.clearArray();
        JSONObject settingsDetails = new JSONObject();
        JSONObject settingsObject = new JSONObject();

        settingsDetails.put("logTime", curTime);
        settingsDetails.put("readableTime", timeField.getTimeString());
        settingsDetails.put("renderMode", renderMode);
        settingsDetails.put("strengthTestStatus", strengthTestStatus);
        settingsDetails.put("cpuCores", cpuCores);
        settingsDetails.put("cpuName", cpuName);
        settingsDetails.put("gpuName", gpuName);
        p.println(finishedTesting);
        if (finishedTesting) {
            settingsDetails.put("pcStrength", pcStrength);
            finishedTesting = false;
            startStrengthTest = false;
        }
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
                    startStrengthTest = startTesting;
                    if (!startStrengthTest) {
                        strengthTestStatus = -1;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startStrengthTest() {
        p.println("started strengthTestStatus");
        String message = "Strength test started";
        makeToasts.add(new MakeToast(p, p.width / 2, timeField.getY(), stdTs, margin, edgeRad, message.length() * 3, light, textCol, textYShift, false, message, stdFont, null));

    }

    private void checkIfStrengthTestIsFinished() {
        finishedTesting = false;
        finishedTesting = true;
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
                System.out.println();
                String s = null;
                // Reading sucessful output of the command
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                while ((s = reader.readLine()) != null) {
                    String[] m1 = p.match(s.toUpperCase(), "NAME");
                    if (s.length() > 0 && m1 == null) {
                        p.println(s, "--");
                        cpuName = s;
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
                cpuName = "";
            }
        }
        return cpuName;
    }

    private String getGPUName() {
        String gpuName = "";

        try {

            // String filePath =
            // mainActivity.getHomeScreenSlavePath()+"\\SystemInformations.txt";
            // String
            // filePath="C:\\Users\\domin\\Documents\\Processing\\SystemInformations.txt";

            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
           // InputStream is = classloader.getResourceAsStream(mainActivity.getHomeScreenSlavePath() + "\\SystemInformations.txt");
            InputStream is = classloader.getResourceAsStream("C:\\Users\\domin\\Documents\\Processing"+ "\\SystemInformations.txt");
            String filePath = new java.io.BufferedInputStream(is).toString();
            //filePath=new java.io.BufferedInputStream(is);
            // new
            // FileInteractionHelper(p).createParentFolders(mainActivity.getHomeScreenSlavePath());
            // String filePath =
            // getClass().getResource(mainActivity.getHomeScreenSlavePath() +
            // "\\SystemInformations.txt").getPath();
            
            FileWriter file = new FileWriter("C:\\Users\\domin\\Documents\\Processing"+ "\\SystemInformations.txt");

            p.println(filePath, is, "--", new java.io.BufferedInputStream(is));
            // Use "dxdiag /t" variant to redirect output to a given file
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "dxdiag", "/t", filePath);
            Process proc = pb.start();
            proc.waitFor();
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;
            p.println(String.format("-- Printing %1$1s info --", filePath));
            while ((line = br.readLine()) != null) {
                if (line.trim().startsWith("Card name:")) {
                    System.out.println(line.trim());
                    gpuName = line.trim();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return gpuName;
    }
}
