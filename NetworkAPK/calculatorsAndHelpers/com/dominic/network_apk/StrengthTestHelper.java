package com.dominic.network_apk;

import java.io.File;
import java.lang.reflect.Method;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import processing.core.PApplet;

public class StrengthTestHelper<T> {
    private int pcStrengthCPU, pcStrengthGPU, strengthTestStatus = -1, referenceRendertime = 60, maxTestTime = 10 * 60; // strengthTestStatus: -1 = null, 0=started,1=finished
    private Boolean finishedTestingCPU = false, finishedTestingGPU = false, startStrengthTest = false, prevStartTesting = false;
    private long curTime, startTimeGPUTest, startTimeCPUTest, cpuTestDuration, gpuTestDuration, lastLogTime = 0, prevLastModifiedCmdFile = 0;
    private String cpuName, gpuName, strengthTestTerminalWindowName = "strengthTestTerminal", pathToStrengthTestResult = "", logFunctionName;
    private File ressourceBlendFile, blendFile, ressourceRandomSeed, randomSeedFile, ressourceForceGPURendering, forceGPURenderingFile;
    private PApplet p;
    private Thread startTestOnGPUThread, getSpecInfoThread;
    private PCInfoHelper pcInfoHelper;
    private FileInteractionHelper fileInteractionHelper;
    private CommandExecutionHelper commandExecutionHelper;
    private JsonHelper jsonHelper;
    private MainActivity mainActivity;
    private T parent;

    public StrengthTestHelper(PApplet p, String logFunctionName, T parent) {
        this.p = p;
        this.logFunctionName = logFunctionName;
        this.parent = parent;
        fileInteractionHelper = new FileInteractionHelper(p);
        pcInfoHelper = new PCInfoHelper(p);
        commandExecutionHelper = new CommandExecutionHelper(p);
        jsonHelper = new JsonHelper(p);
        mainActivity = (MainActivity) p;

    }

    public void checkForStrengthTestCommands(String cpuName, String gpuName, Thread getSpecInfoThread) {

        File cmdFile = new File(mainActivity.getMasterCommandFilePath());
        if (cmdFile.lastModified() != prevLastModifiedCmdFile) {
            
            p.println("datafile changed");
            this.cpuName = cpuName;
            this.gpuName = gpuName;
            this.getSpecInfoThread = getSpecInfoThread;

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
            prevLastModifiedCmdFile = cmdFile.lastModified();
        }
        calcBackgroundTasks();
    }

    private void calcBackgroundTasks() {
        curTime = pcInfoHelper.getCurTime();

        p.println(startStrengthTest, strengthTestStatus);
        if (startStrengthTest && (strengthTestStatus < 0 || strengthTestStatus == 1)) {
            startStrengthTest();
            strengthTestStatus = 0;
            startStrengthTest = false;
            p.println("----------------------------------------------");
        }
        if (curTime - lastLogTime > mainActivity.getSuperShortTimeIntervall()) {
            p.println("strTStat: ", strengthTestStatus);
            if (strengthTestStatus == 0) {
                p.println("check", cpuName, gpuName);
                checkIfStrengthTestCPUIsFinished();
                checkIfStrengthTestGPUIsFinished();
            }
            lastLogTime = curTime;
        }
    }

    public void startStrengthTest() {
        copyStrengthTestResources(true);
        if (cpuName.length() > 0) {
            startTestOnCPU();
            String message = "Strength test started on CPU";
            // makeToasts.add(new MakeToast(p, p.width / 2, timeField.getY(), stdTs, margin,
            // edgeRad, message.length() * 3, light, textCol, textYShift, false, message,
            // stdFont, null));
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
                            logData();

                            p.println("strTestStat: ", strengthTestStatus);
                            if (strengthTestStatus != -1) {
                                startTestOnGPU();
                                String message = "Strength test started on GPU";
                                // makeToasts.add(new MakeToast(p, p.width / 2, timeField.getY(), stdTs, margin,
                                // edgeRad, message.length() * 3, light, textCol, textYShift, false, message,
                                // stdFont, null));
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
        if (startTestOnGPUThread != null) {
            startTestOnGPUThread.interrupt();
            p.println("interupt: ", startTestOnGPUThread.isAlive());
        }
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
                // logData();
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
        p.println("check from gpu finish: ", finishedTestingGPU);
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

    private void logData() {
        p.println("try to log from strengthTestHelper");
        Method m;
        try {
            m = parent.getClass().getMethod(logFunctionName);
            m.invoke(parent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean getFinishedTestingCPU() {
        return finishedTestingCPU;
    }

    public Boolean getFinishedTestingGPU() {
        return finishedTestingGPU;
    }

    public int getStrengthTestStatus() {
        return strengthTestStatus;
    }

    public int getStrengthCPU() {
        return pcStrengthCPU;
    }

    public int getStrengthGPU() {
        return pcStrengthGPU;
    }
    
    public void setFinishedTestingCPU(Boolean state) {
        finishedTestingCPU = state;
    }

    public void setFinishedTestingGPU(Boolean state) {
        finishedTestingGPU = state;
    }
    public void setStrengthCPU(int setStrengthCPU) {
        pcStrengthCPU=setStrengthCPU;
    }
    public void setStrengthGPU(int setStrengthGPU) {
        pcStrengthGPU=setStrengthGPU;
    }

    public void setStrengthTestStatus(int setStatus) {
        strengthTestStatus = setStatus;
    }

    public File getBlendFile() {
        return blendFile;
    }
}
