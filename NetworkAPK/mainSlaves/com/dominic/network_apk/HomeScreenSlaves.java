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
	private int mode, stdTs, edgeRad, margin, btnSize, btnSizeSmall, dark, light, lighter, lightest, textCol, textDark, border, red, green, cpuCores = 0;
	private int renderMode; // rendermode --> 0=render files, 1=render on
							// sheepit,2=sleeping
	private float textYShift;
	private Boolean allWorking = true;
	private String pathToCloud, pcAlias, pcFolderName, cpuName = "", gpuName = "";
	private String[] pictoPaths;
	private long curTime, prevLastModified, lastLogTime = 0, prevTime1;
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
	private Thread getSpecInfoThread;
	private RenderHelper renderHelper;
	private SheepitRenderHelper sheepitRenderHelper;
	private CommandExecutionHelper commandExecutionHelper;

	public HomeScreenSlaves(PApplet p, int mode, int stdTs, int edgeRad, int margin, int btnSizeLarge, int btnSize, int btnSizeSmall, int dark, int light, int lighter, int textCol, int textDark, int border, int red, int green, float textYShift, String[] pictoPaths, PFont stdFont) {
		this.p = p;
		this.mode = mode;
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
		strengthTestHelper = new StrengthTestHelper(p, "logData", this);
		renderHelper = new RenderHelper(p);
		sheepitRenderHelper = new SheepitRenderHelper(p);
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

			// check for new software ----------------------------------------------------
			try {
				File newBlenderVersion=null,newSheepitVersion=null,localBlenderVersion=null,localSheepitVersion=null;
				try {
					 newBlenderVersion = new File(mainActivity.getProgrammFolderPath() + "\\" + fileInteractionHelper.getFoldersAndFiles(mainActivity.getProgrammFolderPath(), true)[0]);
					 newSheepitVersion = new File(mainActivity.getProgrammFolderPath() + "\\" + fileInteractionHelper.getFoldersAndFiles(mainActivity.getProgrammFolderPath(), false)[0]);
					 localBlenderVersion = new File(mainActivity.getLocalProgrammPath() + "\\" + fileInteractionHelper.getFoldersAndFiles(mainActivity.getLocalProgrammPath(), true)[0]);
					 localSheepitVersion = new File(mainActivity.getLocalProgrammPath() + "\\" + fileInteractionHelper.getFoldersAndFiles(mainActivity.getLocalProgrammPath(), false)[0]);
				} catch (Exception e) {
					e.printStackTrace();
				}
				p.println("check");
				if (newBlenderVersion.exists()) {
					p.println("exists",localBlenderVersion);
					//p.println(localBlenderVersion.exists(), localBlenderVersion.getAbsolutePath());
					if (localBlenderVersion==null || !localBlenderVersion.exists() || !localBlenderVersion.getName().toString().equals(newBlenderVersion.getName().toString())) {
						if(localBlenderVersion!=null && localBlenderVersion.exists()) {
						fileInteractionHelper.batchDeleteFolder(localBlenderVersion.getAbsolutePath());
						}
						String copyToPath = mainActivity.getLocalProgrammPath() + "\\";
						p.println(newBlenderVersion.getAbsolutePath(), copyToPath);
						fileInteractionHelper.copyFolder(newBlenderVersion.getAbsolutePath(),copyToPath);
						//set new blenderPath-------------------------------------------
						try {
							File blenderExe=new File(fileInteractionHelper.getPathOfFileInFolder(mainActivity.getLocalProgrammPath(),"blender.exe"));
							if(blenderExe.exists()) {
							mainActivity.getSettingsScreen().getPathSelectors()[0].setPath(blenderExe.getAbsolutePath(), false);
							JSONArray loadedArray = jsonHelper.getData(mainActivity.getSettingsPath());
							JSONObject elem0 = (JSONObject) (loadedArray.get(0));
							JSONObject settingsObj= (JSONObject) elem0.get("Settings");
							settingsObj.put("pathSelector0", blenderExe.getAbsolutePath());
							elem0.put("Settings", settingsObj);
							loadedArray.set(0, elem0);
							jsonHelper.clearArray();
							jsonHelper.setArray(loadedArray);
							jsonHelper.writeData(mainActivity.getSettingsPath());
							}	
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						//set new blenderPath-------------------------------------------

						p.println("blender new");
					}
				}
				if (newSheepitVersion.exists()) {
					p.println("exists",localBlenderVersion);
					//p.println(localBlenderVersion.exists(), localBlenderVersion.getAbsolutePath());
					if (localSheepitVersion==null || !localSheepitVersion.exists() || !localSheepitVersion.getName().toString().equals(newSheepitVersion.getName().toString())) {
						if(localSheepitVersion!=null && localSheepitVersion.exists()) {
							new File(localSheepitVersion.getAbsolutePath()).delete();
						}
						fileInteractionHelper.copyFile(newSheepitVersion.getAbsolutePath(), mainActivity.getLocalProgrammPath() + "\\" + newSheepitVersion.getName());
						p.println("sheepit new");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// check for new software ----------------------------------------------------

			lastLogTime = curTime;
		}
		if (curTime - prevTime1 > mainActivity.getSuperShortTimeIntervall()) {
			renderFiles();
			prevTime1 = curTime;
		}

		File cmdFile = new File(mainActivity.getMasterCommandFilePath());
		if (strengthTestHelper.getStrengthTestStatus() == 0 || cmdFile.lastModified() != prevLastModified) {
			// checkForCommands();
			strengthTestHelper.checkForStrengthTestCommands(cpuName, gpuName, getSpecInfoThread);
			renderFiles();
			prevLastModified = cmdFile.lastModified();
		}

	}

	private void renderFiles() {
		Boolean isRenderingJson = renderHelper.getStartRenderingFromJson();

		Boolean startRendering = renderHelper.getStartRenderingFromJson();
		if (startRendering) {
			if (!renderHelper.getAllJobsStarted() && (renderHelper.getCpuFinished() || renderHelper.getGpuFinished())) {
				Boolean[] hwToUse = mainActivity.getHardwareToRenderWith(mainActivity.getPCName());
				p.println("hw to use");
				p.println(hwToUse);
				p.println(renderHelper.getCpuFinished());
				if (hwToUse[0] && renderHelper.getCpuFinished()) {
					renderHelper.startRenderJob(mainActivity.getMasterRenderJobsFilePath(), mainActivity.getMasterRenderJobsStatusFilePath(), true);
				}
				if (hwToUse[1] && renderHelper.getGpuFinished()) {
					renderHelper.startRenderJob(mainActivity.getMasterRenderJobsFilePath(), mainActivity.getMasterRenderJobsStatusFilePath(), false);
				}
				renderMode = 0;
			} else {
				if (!renderHelper.getCpuFinished() || !renderHelper.getGpuFinished()) {
					renderMode = 0;
				} else {
					renderMode = 2;
				}
			}
		} else {
			if (!renderHelper.getCpuFinished() || !renderHelper.getGpuFinished()) {
				renderHelper.setFinishAllJobs(true, true);
			}
			Boolean startRenderingOnSheepit = sheepitRenderHelper.getStartRenderingOnSheepit(mainActivity.getMasterCommandFilePath());
			if (startRenderingOnSheepit) {
				renderMode = 1;
			} else {
				renderMode = 2;
			}
		}
	}

	public void logData() {
		jsonHelper.clearArray();
		JSONObject settingsDetails = new JSONObject();
		JSONObject settingsObject = new JSONObject();

		settingsDetails.put("cpuIsRendering", renderHelper.getCPUThreadAlive());
		settingsDetails.put("gpuIsRendering", renderHelper.getGPUThreadAlive());
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
			p.println("now deleting files ---------------------");
			fileInteractionHelper.deleteFolder(strengthTestHelper.getBlendFile().getParentFile().getAbsolutePath());
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

	public int getMode() {
		return mode;
	}

	public Thread getStartTestOnGPUThread() {
		return strengthTestHelper.getStartTestOnGPUThread();
	}

}
