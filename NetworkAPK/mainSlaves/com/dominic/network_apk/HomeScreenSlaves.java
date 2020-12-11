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
	private int mode, stdTs, edgeRad, margin, btnSize, btnSizeSmall, dark, light, lighter, lightest, textCol, textDark, border, red, green, cpuCores = 0, checkIterations = 0;
	private int renderMode; // rendermode --> 0=render files, 1=render on
							// sheepit,2=sleeping
	private float textYShift;
	private Boolean allWorking = true, copying = false;
	private String pathToCloud, pcAlias, pcFolderName, cpuName = "", gpuName = "";
	private String[] pictoPaths;
	private long curTime, prevLastModified, lastLogTime = 0, lastLogTime2 = 0, prevTime1, restartLastModified;
	private PFont stdFont;
	private PApplet p;
	private ImageButton[] mainButtons;
	private ImageButton cancelRendering_ImageButton;
	private MainActivity mainActivity;
	private PictogramImage fileRendering_PictogramImage, sheepitRendering_PictogramImage, sleeping_PictogramImage;
	private TimeField timeField;
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
		if (p.frameCount % 60 == 0) {
			File restartCmdFile = new File(mainActivity.getRestartCommandFilePath());
			if (restartCmdFile.exists() && restartCmdFile.lastModified() != restartLastModified) {
				renderHelper.checkForRestart(mainActivity.getMasterRenderJobsStatusFilePath());
				restartLastModified = restartCmdFile.lastModified();
			}
		}
		curTime = pcInfoHelper.getCurTime();
		if (curTime - lastLogTime > mainActivity.getStdTimeIntervall()) {
			logData();
			lastLogTime = curTime;
		}
		if (curTime - prevTime1 > mainActivity.getSuperShortTimeIntervall()) {
			renderFiles();
			prevTime1 = curTime;
		}

		File cmdFile = new File(mainActivity.getMasterCommandFilePath());
		if (cmdFile.lastModified() != prevLastModified) {
			// checkForCommands();
			renderFiles();
			prevLastModified = cmdFile.lastModified();
		}

	}

	private void renderFiles() {

		Boolean isRenderingJson = renderHelper.getStartRenderingFromJson();

		Boolean startRendering = renderHelper.getStartRenderingFromJson();
		
		p.textSize(30);
		p.fill(255, 0, 0);
		p.text(p.str(startRendering), 100, 100);

		if (startRendering) {
			if (!renderHelper.getAllJobsStarted() && (renderHelper.getCpuFinished() || renderHelper.getGpuFinished())) {
				Boolean[] hwToUse = mainActivity.getHardwareToRenderWith(mainActivity.getPCName(), true);
				p.println("hw to use");
				p.println(hwToUse);

				p.fill(255, 0, 0);
				p.text(p.str(hwToUse[0]) + ":" + p.str(hwToUse[1]), 100, 200);

				p.println(renderHelper.getCpuFinished());
				if (hwToUse[0] && renderHelper.getCpuFinished()) {
					renderHelper.startRenderJob(mainActivity.getMasterRenderJobsFilePath(), mainActivity.getMasterRenderJobsStatusFilePath(), true);
				}
				if (hwToUse[1] && renderHelper.getGpuFinished()) {
					renderHelper.startRenderJob(mainActivity.getMasterRenderJobsFilePath(), mainActivity.getMasterRenderJobsStatusFilePath(), false);
				}
				renderMode = 0;
				if (!hwToUse[0] && !hwToUse[1]) {
					renderMode = 2;

					p.fill(255, 0, 0);
					p.text("NO HW TO USE", 100, 300);
				}
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
				if (!sheepitRenderHelper.getIsRendering()) {
					String sheepitPath = sheepitRenderHelper.getSheepitExePath();
					if (sheepitPath.length() > 0) {
						sheepitRenderHelper.startRenderingOnSheepit(sheepitPath);
					} else {
						renderMode = 2;
					}
				}
			} else {
				if (sheepitRenderHelper.getIsRendering()) {
					sheepitRenderHelper.finishRenderingOnSheepit();
				}
				renderMode = 2;
			}
			if (sheepitRenderHelper.getIsRendering()) {
				if (sheepitRenderHelper.getWindowIsOpen()) {
					renderMode = 1;
					checkIterations = 0;
				} else {
					p.println(checkIterations);
					String sheepitPath = sheepitRenderHelper.getSheepitExePath();
					if (sheepitPath.length() > 0 && checkIterations > 10) {
						sheepitRenderHelper.startRenderingOnSheepit(sheepitPath);
						checkIterations = 0;
					} else {
						checkIterations++;
						renderMode = 2;
					}
					renderMode = 2;
				}
			} else {
				renderMode = 2;
			}
		}
	}

	public void checkForNewSoftware() {
		// check for new software ----------------------------------------------------
		if (curTime - lastLogTime2 > mainActivity.getStdTimeIntervall() && !copying) {
			try {
				File newBlenderVersion = null, newSheepitVersion = null, localBlenderVersion = null, localSheepitVersion = null;
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
					p.println("exists", localBlenderVersion);

					if (localBlenderVersion == null || !localBlenderVersion.exists() || !localBlenderVersion.getName().toString().equals(newBlenderVersion.getName().toString())) {
						copying = true;
						if (localBlenderVersion != null && localBlenderVersion.exists()) {
							p.println("delete local");
							fileInteractionHelper.batchDeleteFolder(localBlenderVersion.getAbsolutePath());
						}
						String copyToPath = mainActivity.getLocalProgrammPath() + "\\";
						p.println(newBlenderVersion.getAbsolutePath(), copyToPath);
						fileInteractionHelper.copyFolder(newBlenderVersion.getAbsolutePath(), copyToPath);
						// set new blenderPath-------------------------------------------
						try {
							File blenderExe = new File(fileInteractionHelper.getPathOfFileInFolder(mainActivity.getLocalProgrammPath(), "blender.exe"));
							if (blenderExe.exists()) {
								mainActivity.getSettingsScreen().getPathSelectors()[0].setPath(blenderExe.getAbsolutePath(), false);
								JSONArray loadedArray = jsonHelper.getData(mainActivity.getSettingsPath());
								JSONObject elem0 = (JSONObject) (loadedArray.get(0));
								JSONObject settingsObj = (JSONObject) elem0.get("Settings");
								settingsObj.put("pathSelector0", blenderExe.getAbsolutePath());
								elem0.put("Settings", settingsObj);
								loadedArray.set(0, elem0);
								jsonHelper.clearArray();
								jsonHelper.setArray(loadedArray);
								jsonHelper.writeData(mainActivity.getSettingsPath());
								p.println("now written");
								mainActivity.getSettingsScreen().setData();
								copying = false;
								p.println("now set");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						// set new blenderPath-------------------------------------------

						p.println("blender new");
					}
				}
				if (newSheepitVersion.exists()) {
					p.println("exists", localBlenderVersion);
					// p.println(localBlenderVersion.exists(),
					// localBlenderVersion.getAbsolutePath());
					if (localSheepitVersion == null || !localSheepitVersion.exists() || !localSheepitVersion.getName().toString().equals(newSheepitVersion.getName().toString())) {
						if (localSheepitVersion != null && localSheepitVersion.exists()) {
							new File(localSheepitVersion.getAbsolutePath()).delete();
						}
						fileInteractionHelper.copyFile(newSheepitVersion.getAbsolutePath(), mainActivity.getLocalProgrammPath() + "\\" + newSheepitVersion.getName());
						p.println("sheepit new");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			lastLogTime2 = curTime;
		}

		// check for new software ----------------------------------------------------
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

}
