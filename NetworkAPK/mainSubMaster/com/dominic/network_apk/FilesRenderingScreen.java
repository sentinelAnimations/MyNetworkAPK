package com.dominic.network_apk;

import java.io.File;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import processing.core.PApplet;
import processing.core.PFont;

public class FilesRenderingScreen {

	private int stdTs, edgeRad, margin, btnSize, btnSizeSmall, btnSizeLarge, dark, light, green, red, blue, lighter, lightest, textCol, textDark, border, curRenderingFile = 1, prevPCListSelectedInd = -1, onStartup = 0, prevSelectedFileListInd = 0;
	private float textYShift, alpha;
	private float[] listX, listW, allFiles_listX, allFiles_listW;
	private Boolean[] renderAnimation, renderStillFrame, useNewResolution, startedRenderingTiles, allFilesCopyStatus, fileIsFinished;
	private long prevTime, prevLastModified;
	private int[] startFrame, endFrame, stillFrame, resX, resY, samples, allPCStatus; // allPCStatus: 0=prog responding,1=prog is rendering, 2=prog not responding
	private String[] pictoPaths, hoLiPictoPaths, imageSavePaths;
	private String[] allPCNames, allLastLogLines, allRenderInfos;
	private PFont stdFont;
	private PApplet p;
	private MainActivity mainActivity;
	private PictogramImage[] allPCPictos;
	private ArrayList<Node> allConnectedNodes = new ArrayList<>();
	private Loadingbar[] allPCLoadingbars;
	private HorizontalList allFiles_HorizontalList, allPCs_HorizontalList;
	private LogBar logBar, fileInfo_LogBar;
	private Switch renderGraphics_switch, showGPUOrCpuLog_switch;
	private PictogramImage rendering_PictogramImage;
	private FileInteractionHelper fileInteractionHelper;
	private JsonHelper jsonHelper;
	private RenderHelper renderHelper;
	private PCInfoHelper pcInfoHelper;

	public FilesRenderingScreen(PApplet p, int stdTs, int edgeRad, int margin, int btnSizeLarge, int btnSize, int btnSizeSmall, int dark, int light, int lighter, int lightest, int textCol, int textDark, int border, int green, int red, int blue, float textYShift, String[] pictoPaths, String[] hoLiPictoPaths, PFont stdFont) {
		this.p = p;
		this.stdTs = stdTs;
		this.edgeRad = edgeRad;
		this.margin = margin;
		this.btnSizeLarge = btnSizeLarge;
		this.btnSize = btnSize;
		this.btnSizeSmall = btnSizeSmall;
		this.dark = dark;
		this.light = light;
		this.lighter = lighter;
		this.lightest = lightest;
		this.textCol = textCol;
		this.textDark = textDark;
		this.border = border;
		this.green = green;
		this.red = red;
		this.blue = blue;
		this.textYShift = textYShift;
		this.pictoPaths = pictoPaths;
		this.hoLiPictoPaths = hoLiPictoPaths;
		this.stdFont = stdFont;
		mainActivity = (MainActivity) p;
		fileInteractionHelper = new FileInteractionHelper(p);
		jsonHelper = new JsonHelper(p);
		renderHelper = new RenderHelper(p);
		pcInfoHelper = new PCInfoHelper(p);
		setupAll();
	}

	public void render() {
		if (onStartup == 0) {
			updateLists();
			onStartup++;
		}

		renderGraphics_switch.render();

		if (renderGraphics_switch.getIsChecked()) {

			// render all ----------------------------------------------

			allFiles_HorizontalList.render();
			// allFiles_horizontallist ------------------------
			if (allFiles_HorizontalList.getList().length > 0) {
				if (allFiles_HorizontalList.getIsShifted()) {
					allFiles_listX = allFiles_HorizontalList.getListX();
					allFiles_listW = allFiles_HorizontalList.getListW();
					allFiles_HorizontalList.setIsShifted(false);
				}
				for (int i = allFiles_HorizontalList.getFirstDisplayedInd(); i <= allFiles_HorizontalList.getLastDisplayedInd(); i++) {
					p.noFill();
					try {
						if (fileIsFinished[i]) {
							p.stroke(green);
						} else {
							p.stroke(lighter);
						}
					} catch (Exception e) {
						// e.printStackTrace();
						p.stroke(lighter);
					}
					if (allFilesCopyStatus[i] == false) {
						p.stroke(red);
					}
					if (allFiles_HorizontalList.getSelectedInd() == i) {
						p.stroke(255);
					}

					p.rect(allFiles_listX[i], allFiles_HorizontalList.getY(), allFiles_listW[i], allFiles_HorizontalList.getH() - margin * 2, edgeRad);
				}
			}
			// allFiles_horizontallist ------------------------
			logBar.render();
			showGPUOrCpuLog_switch.render();

			fileInfo_LogBar.render();
			// render allPCs_horizontalList --------------------
			allPCs_HorizontalList.render();
			alpha = p.abs(p.sin(p.radians(p.frameCount))) * 255;
			if (allPCs_HorizontalList.getList().length > 0) {
				if (allPCs_HorizontalList.getIsShifted()) {

					listX = new float[allPCs_HorizontalList.getListX().length];
					listW = new float[allPCs_HorizontalList.getListW().length];
					listX = allPCs_HorizontalList.getListX();
					listW = allPCs_HorizontalList.getListW();

					for (int i = 0; i < allPCLoadingbars.length; i++) {
						allPCLoadingbars[i].setPos((int) listX[i], (int) (allPCs_HorizontalList.getY() + listW[i] / 2 - margin * 3 - allPCLoadingbars[i].getH()));
						allPCPictos[i].setPos((int) listX[i], (int) (allPCs_HorizontalList.getY() - listW[i] / 2 + margin * 2 + allPCPictos[i].getH() / 2));
					}
					allPCs_HorizontalList.setIsShifted(false);
				}

				for (int i = allPCs_HorizontalList.getFirstDisplayedInd(); i <= allPCs_HorizontalList.getLastDisplayedInd(); i++) {
					p.fill(lighter);
					if (allPCs_HorizontalList.getSelectedInd() == i) {
						p.stroke(border);
					} else {
						p.stroke(lighter);
					}
					p.rect(listX[i], allPCs_HorizontalList.getY(), listW[i], allPCs_HorizontalList.getH() - margin * 2, edgeRad);
					allPCLoadingbars[i].render();
					allPCPictos[i].render();

					try {
						p.fill(textCol);
						p.textFont(stdFont);
						p.textSize(stdTs);
						p.textAlign(p.CENTER, p.CENTER);
						p.text(allRenderInfos[i], listX[i], allPCPictos[i].getY() + allPCPictos[i].getH() / 2 + margin + stdTs + ((allPCLoadingbars[i].getY() - allPCLoadingbars[i].getH() / 2) - (allPCPictos[i].getY() + allPCPictos[i].getH())) / 2);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}

			// render allPCs_horizontalList --------------------

			// render all ----------------------------------------------
		} else {
			rendering_PictogramImage.render();
		}

		// get loglines -----------------------------------

		// get loglines -----------------------------------

		// update PCView---------------------------------------------
		if (allConnectedNodes.size() > 0) {

			File renderCmds = new File(mainActivity.getMasterRenderJobsFilePath());
			if (renderCmds.lastModified() != prevLastModified) {
				checkForFinished();
				prevLastModified = renderCmds.lastModified();
			}

			if (allFiles_HorizontalList.getSelectedInd() != prevSelectedFileListInd) {
				fileInfo_LogBar.setText(getFileInfoOfSelected(allFiles_HorizontalList.getSelectedInd()));
			}

			if (allPCs_HorizontalList.getSelectedInd() != prevPCListSelectedInd) {
				logBar.setText(allLastLogLines[allPCs_HorizontalList.getSelectedInd()]);
			}

			if (pcInfoHelper.getCurTime() - prevTime > mainActivity.getSuperShortTimeIntervall()) {
				updateLists();
				p.println(renderHelper.getCpuFinished(), renderHelper.getGpuFinished(), "-----------");
				prevTime = pcInfoHelper.getCurTime();
			}
		}
		// update PCView---------------------------------------------

		prevPCListSelectedInd = allPCs_HorizontalList.getSelectedInd();
		prevSelectedFileListInd = allFiles_HorizontalList.getSelectedInd();

	}

	private void updateLists() {
		for (int i = 0; i < allPCs_HorizontalList.getList().length; i++) {
			Boolean foundLogLine = false;
			Node n = allConnectedNodes.get(i);
			n.checkForSignsOfLife();
			// check render status if ok, update last log line -------------------
			allPCStatus[i] = n.getPcStatus();
			if (allPCStatus[i] < 2) {
				allPCPictos[i].setCol(green);
				File curRenderLogFile;
				if (showGPUOrCpuLog_switch.getIsChecked()) {
					curRenderLogFile = new File(mainActivity.getRenderLogPathCPU(n.getPcSelection_DropdownMenu().getSelectedItem()));
				} else {
					curRenderLogFile = new File(mainActivity.getRenderLogPathGPU(n.getPcSelection_DropdownMenu().getSelectedItem()));
				}
				if (curRenderLogFile.exists()) {
					try {
						String[] lines = p.loadStrings(curRenderLogFile.getAbsolutePath());
						String lastLine = lines[lines.length - 1];
						allLastLogLines[i] = lastLine;
						if (allPCs_HorizontalList.getSelectedInd() == i) {
							p.println("selectedInd: "+allPCs_HorizontalList.getSelectedInd());
							logBar.setText(lastLine);
						}
						foundLogLine = true;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				allPCPictos[i].setCol(red);
			}
			// cmd delete system32
			// check render status if ok, update last log line -------------------

			// prepare infoString and so on-----------------------------------
			Boolean jobIsFinished = false;
			String[] splitStr = p.split(allLastLogLines[i], "|");
			String renderInfoString = allPCNames[i];
			if (allLastLogLines[i].equals("Job finished") == false) {
				if (allPCStatus[i] > 0) {
					renderInfoString += n.getPCStatusStrings()[allPCStatus[i]];
				}
				renderInfoString += "\n";
				for (int i2 = 0; i2 < splitStr.length; i2++) {
					if (i2 == 0) {
						String[] splStr = p.split(splitStr[i2], " ");

						renderInfoString += splStr[0] + "\n";
					} else {
						renderInfoString += splitStr[i2] + "\n";
					}
				}
			} else {
				renderInfoString = "Job finished";
				jobIsFinished = true;
			}
			if (!foundLogLine) {
				renderInfoString = "File not found!";
				if (allPCs_HorizontalList.getSelectedInd() == i) {
					logBar.setText(renderInfoString);
				}
			}
			p.println(jobIsFinished);
			allRenderInfos[i] = renderInfoString;

			if (jobIsFinished) {
				allPCLoadingbars[i].setMax((int) p.pow(10, 100));
			} else {
				String[] m1 = p.match(splitStr[splitStr.length - 1], "Tiles");
				if (m1 != null) {
					String[] splitStr2 = p.split(splitStr[splitStr.length - 1], " ");
					String[] splitStr3 = p.split(splitStr2[2], "/");
					if (splitStr3.length == 2) {
						if (startedRenderingTiles[i] == null) {
							allPCLoadingbars[i].setMin(0);
							allPCLoadingbars[i].setMax(Integer.parseInt(splitStr3[1]));
						}
						allPCLoadingbars[i].setValue(Integer.parseInt(splitStr3[0]));
						startedRenderingTiles[i] = true;
					}

				}
			}
			// prepare infoString and so on-----------------------------------
		}

	}

	private String getFileInfoOfSelected(int selectedInd) {
		String infoStr = "";
		if (renderAnimation[selectedInd]) {
			infoStr += "Rendering Frame " + startFrame[selectedInd] + " - " + endFrame[selectedInd] + " | ";
		} else {
			infoStr += "Rendering Frame " + stillFrame[selectedInd] + " | ";
		}
		if (useNewResolution[selectedInd]) {
			infoStr += "Resolution: " + resX[selectedInd] + " x " + resY[selectedInd] + " | ";
		} else {
			infoStr += "Resolution: Filesettings | ";
		}
		infoStr += "Rendersamples: " + samples[selectedInd] + " | Save folder: " + imageSavePaths[selectedInd];

		return infoStr;
	}

	private Boolean checkForFinished() {
		Boolean isFinished = false;
		// to do ------------------------
		return isFinished;
	}

	public void startFileRendering() {
		String[] fileList = getHorizontalList().getList();
		allFilesCopyStatus = new Boolean[fileList.length];
		fileInteractionHelper.deleteFolder(mainActivity.getPathToBlenderRenderFolder());
		for (int i = 0; i < fileList.length; i++) {
			File f = new File(fileList[i]);
			allFilesCopyStatus[i] = fileInteractionHelper.copyFile(f.getAbsolutePath(), mainActivity.getPathToBlenderRenderFolder() + "\\" + f.getName());
			// check if fileAlreadyExists Exception but different render settings
			if (!allFilesCopyStatus[i]) {
				ArrayList<Integer> allIndsToCheck = new ArrayList();
				for (int i2 = 0; i2 < fileList.length; i2++) {
					if (i != i2) {
						if (fileList[i].equals(fileList[i2])) {
							p.println("now", fileList[i2]);
							allIndsToCheck.add(i2);
						}
					}
				}
				for (int i2 = 0; i2 < allIndsToCheck.size(); i2++) {
					if (renderAnimation[i] && renderAnimation[allIndsToCheck.get(i2)]) {
						p.println("now1");
						if (startFrame[i] != startFrame[allIndsToCheck.get(i2)] || endFrame[i] != endFrame[allIndsToCheck.get(i2)]) {
							p.println("now2");
							allFilesCopyStatus[i] = true;
						} else {
							if (allFilesCopyStatus[allIndsToCheck.get(i2)] != null && allFilesCopyStatus[allIndsToCheck.get(i2)] != false) {
								p.println("now3");
								allFilesCopyStatus[i] = false;
								break;
							}
						}
					} else {
						allFilesCopyStatus[i] = true;

						if (renderStillFrame[i] && renderStillFrame[allIndsToCheck.get(i2)]) {
							p.println("now4");
							if (stillFrame[i] != stillFrame[allIndsToCheck.get(i)]) {
								p.println("now5");
								allFilesCopyStatus[i] = true;
							} else {
								if (allFilesCopyStatus[allIndsToCheck.get(i2)] != null && allFilesCopyStatus[allIndsToCheck.get(i2)] != false) {
									p.println("now6");
									allFilesCopyStatus[i] = false;
									break;
								}
							}
						} else {
							allFilesCopyStatus[i] = true;
						}
					}
				}
			}
			// check if fileAlreadyExists Exception but different render settings
		}
		String relativeFilePathRandomSeed = "/pythonScripts/randomSeed.py";
		String copyFromPathRandomSeed = getClass().getResource(relativeFilePathRandomSeed).getPath().toString();
		Boolean randomSeedCopied = fileInteractionHelper.copyFile(copyFromPathRandomSeed, mainActivity.getRenderPythonScriptsPath() + "\\randomSeed.py");

		String relativeFileForceGPURendering = "/pythonScripts/forceGPURendering.py";
		String copyFromPathForceGPURendering = getClass().getResource(relativeFileForceGPURendering).getPath().toString();
		Boolean forceGPURenderingCopied = fileInteractionHelper.copyFile(copyFromPathForceGPURendering, mainActivity.getRenderPythonScriptsPath() + "\\forceGPURendering.py");

		if (randomSeedCopied && forceGPURenderingCopied) {
			p.println("copied files");
			prepareRenderProcess();
		}

	}

	private void prepareRenderProcess() {
		p.println(allConnectedNodes.size(), "acNodes size");
		if (allConnectedNodes.size() > 0) {
			JSONArray renderJobsArray = new JSONArray();
			ArrayList<Integer> workingPCInds = new ArrayList<>();
			ArrayList<Integer> allStillFrameRenderjobInds = new ArrayList<>();
			ArrayList<Integer> allAnimationrenderjobInds = new ArrayList<>();

			for (int i = 0; i < allPCStatus.length; i++) {
				if (allPCStatus[i] < 2) {
					workingPCInds.add(i);
				}
			}
			if (workingPCInds.size() > 0) {
				for (int i = 0; i < renderAnimation.length; i++) {
					if (renderAnimation[i]) {
						allAnimationrenderjobInds.add(i);
					} else {
						if (renderStillFrame[i]) {
							allStillFrameRenderjobInds.add(i);
						}
					}
				}

				if (allStillFrameRenderjobInds.size() > 0) {
					for (int i = 0; i < allStillFrameRenderjobInds.size(); i++) {
						File curBlendfile = new File(allFiles_HorizontalList.getList()[i]);
						String[] keys = { "blendfile", "imageSavePath", "renderAnimation", "renderStillFrame", "animationFrame", "useNewResolution", "startFrame", "endFrame", "stillFrame", "resX", "resY", "samples", "isDone", "startedRendering", "startRenderingTime", "readableStartRenderingTime" };
						String[] values = { curBlendfile.getName(), imageSavePaths[allStillFrameRenderjobInds.get(i)], p.str(renderAnimation[allStillFrameRenderjobInds.get(i)]), p.str(renderStillFrame[allStillFrameRenderjobInds.get(i)]), p.str(0), p.str(useNewResolution[allStillFrameRenderjobInds.get(i)]), p.str(startFrame[allStillFrameRenderjobInds.get(i)]), p.str(endFrame[allStillFrameRenderjobInds.get(i)]), p.str(stillFrame[allStillFrameRenderjobInds.get(i)]), p.str(resX[allStillFrameRenderjobInds.get(i)]), p.str(resY[allStillFrameRenderjobInds.get(i)]), p.str(samples[allStillFrameRenderjobInds.get(i)]), p.str(false), p.str(false), null, null };
						JSONObject pcCmdDetails = new JSONObject();
						for (int i2 = 0; i2 < keys.length; i2++) {
							pcCmdDetails.put(keys[i2], values[i2]);
						}
						renderJobsArray.add(pcCmdDetails);
					}
				}
				if (allAnimationrenderjobInds.size() > 0) {
					for (int i = 0; i < allAnimationrenderjobInds.size(); i++) {
						File curBlendfile = new File(allFiles_HorizontalList.getList()[i]);

						for (int i2 = startFrame[allAnimationrenderjobInds.get(i)]; i2 <= endFrame[allAnimationrenderjobInds.get(i)]; i2++) {
							String[] keys = { "blendfile", "imageSavePath", "renderAnimation", "animationFrame", "renderStillFrame", "useNewResolution", "startFrame", "endFrame", "stillFrame", "resX", "resY", "samples", "isDone", "startedRendering", "startRenderingTime", "readableStartRenderingTime" };
							String[] values = { curBlendfile.getName(), imageSavePaths[allAnimationrenderjobInds.get(i)], p.str(renderAnimation[allAnimationrenderjobInds.get(i)]), p.str(i2), p.str(renderStillFrame[allAnimationrenderjobInds.get(i)]), p.str(useNewResolution[allAnimationrenderjobInds.get(i)]), p.str(startFrame[allAnimationrenderjobInds.get(i)]), p.str(endFrame[allAnimationrenderjobInds.get(i)]), p.str(stillFrame[allAnimationrenderjobInds.get(i)]), p.str(resX[allAnimationrenderjobInds.get(i)]), p.str(resY[allAnimationrenderjobInds.get(i)]), p.str(samples[allAnimationrenderjobInds.get(i)]), p.str(false), p.str(false), null, null };
							JSONObject pcCmdDetails = new JSONObject();
							for (int i3 = 0; i3 < keys.length; i3++) {
								pcCmdDetails.put(keys[i3], values[i3]);
							}
							renderJobsArray.add(pcCmdDetails);
						}
					}
				}
			}
			// save renderJobsArray------------------------------------------
			jsonHelper.clearArray();
			jsonHelper.setArray(renderJobsArray);
			jsonHelper.writeData(mainActivity.getMasterRenderJobsFilePath());
			// save renderJobsArray------------------------------------------

			if (mainActivity.getHomeScreenMaster().getCheckboxes()[0].getIsChecked()) {
				Boolean[] hwToUse = mainActivity.getHardwareToRenderWith(mainActivity.getPCName());
				p.println(hwToUse);
				if (mainActivity.getHardwareToRenderWith(mainActivity.getPCName())[0] && mainActivity.getHomeScreenMaster().getCheckboxes()[4].getIsChecked()) {
					renderHelper.setupRenderJob(mainActivity.getMasterRenderJobsFilePath(), 0, true);
				}
				if (mainActivity.getHardwareToRenderWith(mainActivity.getPCName())[1] && mainActivity.getHomeScreenMaster().getCheckboxes()[5].getIsChecked()) {
					renderHelper.setupRenderJob(mainActivity.getMasterRenderJobsFilePath(), 1, false);
				}
				setIsRendering(true);
			}
		}
	}

	public void onMousePressed(int mouseButton) {
		if (renderGraphics_switch.getIsChecked()) {
			allFiles_HorizontalList.onMousePressed();
			allPCs_HorizontalList.onMousePressed();
		}
	}

	public void onMouseReleased(int mouseButton) {
		renderGraphics_switch.onMouseReleased();
		if (renderGraphics_switch.getIsChecked()) {
			showGPUOrCpuLog_switch.onMouseReleased();
			allFiles_HorizontalList.onMouseReleased(mouseButton);
			allPCs_HorizontalList.onMouseReleased(mouseButton);
		}
	}

	public void onKeyPressed(char key) {

	}

	public void onKeyReleased(char key) {
		if (renderGraphics_switch.getIsChecked()) {
			if (key == p.DELETE) {
				String[] hoLi1 = allFiles_HorizontalList.getList();
				String[] newHoLi1 = new String[hoLi1.length - 1];
				String[] newImageSavePaths = new String[imageSavePaths.length - 1];
				Boolean[] newUseNewResolution = new Boolean[useNewResolution.length - 1];
				Boolean[] newRenderAnimation = new Boolean[renderAnimation.length - 1];
				Boolean[] newRenderStillFrame = new Boolean[renderStillFrame.length - 1];
				int[] newStartFrame = new int[startFrame.length - 1];
				int[] newEndFrame = new int[endFrame.length - 1];
				int[] newStillFrame = new int[stillFrame.length - 1];
				int[] newResX = new int[resX.length - 1];
				int[] newResY = new int[resY.length - 1];
				int[] newSamples = new int[samples.length - 1];

				for (int i = 0; i < hoLi1.length; i++) {
					if (i < allFiles_HorizontalList.getSelectedInd()) {
						newImageSavePaths[i] = imageSavePaths[i];
						newHoLi1[i] = hoLi1[i];
						newUseNewResolution[i] = useNewResolution[i];
						newRenderAnimation[i] = renderAnimation[i];
						newRenderStillFrame[i] = renderStillFrame[i];
						newStartFrame[i] = startFrame[i];
						newEndFrame[i] = endFrame[i];
						newStillFrame[i] = stillFrame[i];
						newResX[i] = resX[i];
						newResY[i] = resY[i];
						newSamples[i] = samples[i];

					}
					if (i > allFiles_HorizontalList.getSelectedInd()) {
						newImageSavePaths[i - 1] = imageSavePaths[i];
						newHoLi1[i - 1] = hoLi1[i];
						newUseNewResolution[i - 1] = useNewResolution[i];
						newRenderAnimation[i - 1] = renderAnimation[i];
						newRenderStillFrame[i - 1] = renderStillFrame[i];
						newStartFrame[i - 1] = startFrame[i];
						newEndFrame[i - 1] = endFrame[i];
						newStillFrame[i - 1] = stillFrame[i];
						newResX[i - 1] = resX[i];
						newResY[i - 1] = resY[i];
						newSamples[i - 1] = samples[i];
					}
				}
				imageSavePaths = new String[imageSavePaths.length - 1];
				useNewResolution = new Boolean[useNewResolution.length - 1];
				renderAnimation = new Boolean[renderAnimation.length - 1];
				renderStillFrame = new Boolean[renderStillFrame.length - 1];
				startFrame = new int[startFrame.length - 1];
				endFrame = new int[endFrame.length];
				stillFrame = new int[stillFrame.length];
				resX = new int[resX.length - 1];
				resY = new int[resY.length - 1];
				samples = new int[samples.length - 1];

				imageSavePaths = newImageSavePaths;
				useNewResolution = newUseNewResolution;
				renderAnimation = newRenderAnimation;
				renderStillFrame = newRenderStillFrame;
				startFrame = newStartFrame;
				endFrame = newEndFrame;
				stillFrame = newStillFrame;
				resX = newResX;
				resY = newResY;
				samples = newSamples;

				setFileList(newHoLi1);
				if (newHoLi1.length > 0) {
					prevSelectedFileListInd = allFiles_HorizontalList.getSelectedInd();
					fileInfo_LogBar.setText(getFileInfoOfSelected(allFiles_HorizontalList.getSelectedInd()));

				} else {
					setIsRendering(false);
					mainActivity.setMode(mainActivity.getHomeScreenMaster().getMode());
				}
			}
		}
	}

	public void onScroll(float e) {
		if (renderGraphics_switch.getIsChecked()) {
			allFiles_HorizontalList.onScroll(e);
			allPCs_HorizontalList.onScroll(e);
		}
	}

	public void setFileList(String[] l) {
		allFiles_HorizontalList.setList(l);

		jsonHelper.clearArray();
		JSONArray allRenderFiles = new JSONArray();
		for (int i = 0; i < l.length; i++) {
			allRenderFiles.add(l[i]);
		}
		String jsonPath = mainActivity.getAllRenderFilesJsonPath();
		jsonHelper.setArray(allRenderFiles);
		jsonHelper.writeData(jsonPath);
		p.println(l);
	}

	public HorizontalList getHorizontalList() {
		return allFiles_HorizontalList;
	}

	public void setStartupVals() {
		imageSavePaths = mainActivity.getRenderOverview().getRenderFilesSettings().getImageSavePaths();
		useNewResolution = mainActivity.getRenderOverview().getRenderFilesSettings().getUseNewResolution();
		renderAnimation = mainActivity.getRenderOverview().getRenderFilesSettings().getRenderAnimation();
		renderStillFrame = mainActivity.getRenderOverview().getRenderFilesSettings().getRenderStillFrame();
		startFrame = mainActivity.getRenderOverview().getRenderFilesSettings().getStartFrames();
		endFrame = mainActivity.getRenderOverview().getRenderFilesSettings().getEndFrames();
		stillFrame = mainActivity.getRenderOverview().getRenderFilesSettings().getStillFrames();
		resX = mainActivity.getRenderOverview().getRenderFilesSettings().getResX();
		resY = mainActivity.getRenderOverview().getRenderFilesSettings().getResY();
		samples = mainActivity.getRenderOverview().getRenderFilesSettings().getSamples();

		fileInfo_LogBar.setText(getFileInfoOfSelected(allFiles_HorizontalList.getSelectedInd()));

	}

	public void setupAll() {
		int listH = (int) (btnSize * 4.5f);
		String listW = "";
		while (p.textWidth(listW) < listH) {
			listW += ".";
		}
		if (!mainActivity.getNodeEditor().getIsSetup()) {
			mainActivity.getNodeEditor().setupAll();
		}
		allConnectedNodes = mainActivity.getNodeEditor().getAllConnectedNodes();

		String[] startList = {};
		int startH = (p.height - btnSizeSmall * 4 - listH - margin * 2) / 2 + btnSizeSmall / 2;
		allFiles_HorizontalList = new HorizontalList(p, p.width / 2, startH, p.width - margin * 2, btnSizeSmall + margin * 2, margin, edgeRad, stdTs, (int) p.textWidth("Files to render") + margin * 3 + btnSizeSmall, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border, textYShift, '\\', false, true, false, "Files to render", hoLiPictoPaths, startList, stdFont, null);

		startList = new String[allConnectedNodes.size()];
		for (int i = 0; i < startList.length; i++) {
			startList[i] = listW;
		}
		String[] allPCListPictos = { pictoPaths[2], hoLiPictoPaths[1], hoLiPictoPaths[2] };
		allPCs_HorizontalList = new HorizontalList(p, 0, allFiles_HorizontalList.getH() / 2 + margin + listH / 2, p.width - margin * 2, listH, margin, edgeRad, stdTs, (int) p.textWidth("Rendering PCs") + margin * 3 + btnSizeSmall, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border, textYShift, '\\', true, false, true, "Rendering PCs", allPCListPictos, startList, stdFont, allFiles_HorizontalList);
		logBar = new LogBar(p, 0, allPCs_HorizontalList.getH() / 2 + margin * 2 + btnSizeSmall / 2, allFiles_HorizontalList.getW(), btnSizeSmall + margin * 2, stdTs, edgeRad, margin, btnSizeSmall, dark, light, lighter, textCol, textDark, border, true, textYShift, pictoPaths[0], stdFont, allPCs_HorizontalList);
		logBar.setText("Render Log of selected PC");

		fileInfo_LogBar = new LogBar(p, 0, logBar.getH() + margin, allFiles_HorizontalList.getW(), logBar.getH(), stdTs, edgeRad, margin, btnSizeSmall, dark, light, lighter, textCol, textDark, border, true, textYShift, pictoPaths[3], stdFont, logBar);
		fileInfo_LogBar.setText("File settings of selected file");

		allPCNames = new String[allPCs_HorizontalList.getList().length];
		allLastLogLines = new String[allPCs_HorizontalList.getList().length];
		allPCPictos = new PictogramImage[allPCs_HorizontalList.getList().length];
		allPCLoadingbars = new Loadingbar[allPCs_HorizontalList.getList().length];
		startedRenderingTiles = new Boolean[allPCs_HorizontalList.getList().length];
		allRenderInfos = new String[allPCs_HorizontalList.getList().length];
		allPCStatus = new int[allPCs_HorizontalList.getList().length];
		fileIsFinished = new Boolean[allPCs_HorizontalList.getList().length];

		for (int i = 0; i < allPCLoadingbars.length; i++) {
			allPCLoadingbars[i] = new Loadingbar(p, 0, 0, listH, margin, stdTs, edgeRad, margin, border, dark, textCol, 0, 600, textYShift, false, stdFont, null);
			allPCNames[i] = allConnectedNodes.get(i).getPcSelection_DropdownMenu().getSelectedItem();
			allLastLogLines[i] = "Fra:2 Mem:142.26M (0.00M, Peak 152.21M) | Time:00:00.36 | Remaining:00:02.01 | Mem:11.84M, Peak:21.80M | Scene, View Layer | Rendered 1/680 Tiles: " + i;
			allPCPictos[i] = new PictogramImage(p, margin + btnSize / 2, margin + btnSize / 2, btnSize, btnSize, margin, stdTs, edgeRad, textCol, textYShift, false, false, allConnectedNodes.get(i).getTypePicto().getPictoPath(), "", null);
			fileIsFinished[i] = false;
		}
		p.textSize(stdTs);
		String infoString = "Display Stats";
		int switchW = (int) p.textWidth(infoString) + btnSizeSmall + margin * 2;
		renderGraphics_switch = new Switch(p, switchW / 2 + margin, p.height - btnSizeSmall / 2 - margin, switchW, btnSizeSmall, edgeRad, margin, stdTs, light, lightest, lightest, textCol, textYShift, false, true, true, infoString, stdFont, null);

		infoString = "GPU/CPU";
		switchW = (int) p.textWidth(infoString) + btnSizeSmall + margin * 2;
		showGPUOrCpuLog_switch = new Switch(p, logBar.getW() / 2 - switchW / 2 - margin, 0, switchW, btnSizeSmall, edgeRad, margin, stdTs, dark, lightest, lightest, textCol, textYShift, true, true, true, infoString, stdFont, logBar);

		rendering_PictogramImage = new PictogramImage(p, p.width / 2, p.height / 2, btnSizeLarge, btnSizeLarge, margin, stdTs, edgeRad, textCol, textYShift, false, false, pictoPaths[4], "Rendering file", null);

		updateLists();
	}

	private void setIsRendering(Boolean state) {
		Boolean allPCsAreDone = false;
		JSONArray loadedData = new JSONArray();
		JSONObject settingsDetails = new JSONObject();
		JSONObject settingsObject = new JSONObject();
		// give command to all pcs to do test -----------------------------

		loadedData = jsonHelper.getData(mainActivity.getMasterCommandFilePath());
		if (loadedData.isEmpty()) {
		} else {
			try {
				int mode = mainActivity.getHomeScreenMaster().getMode() - 1;
				p.println("controllStrengthTest");
				String modeName = mainActivity.getModeNamesMaster()[mode];
				JSONObject loadedObject = (JSONObject) (loadedData.get(mode));
				loadedObject = (JSONObject) loadedObject.get(modeName);
				loadedObject.put("startRendering", state);
				settingsObject.put(modeName, loadedObject);
				loadedData.set(mode, settingsObject);
				jsonHelper.setArray(loadedData);
				jsonHelper.writeData(mainActivity.getMasterCommandFilePath());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// give command to all pcs to do test -----------------------------
	}
}
