package com.dominic.network_apk;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jdk.jshell.spi.ExecutionControl.ExecutionControlException;
import processing.core.PApplet;

public class RenderHelper {
	private int renderJobIndex = 0, renderJobIndexCPU, renderJobIndexGPU, imageFilenameDigits = 6;
	private Boolean cpuFinished = false, gpuFinished = false, allJobsStarted = false, finishCPUJob = false, finishGPUJob = false;
	private long prevCheckFinishedTime, startTimeCpu, startTimeGpu, lastCPULogFound, lastGPULogFound;
	private String renderTerminalWindowNameCPU = "renderTerminalCPU", renderTerminalWindowNameGPU = "renderTerminalGPU", blendCommandCPU, blendCommandGPU;
	private int[] renderedFrames;
	private PApplet p;
	private JSONObject renderJob;
	private JSONArray allRenderJobs;
	private MainActivity mainActivity;
	private PCInfoHelper pcInfoHelper;
	private JsonHelper jsonHelper;
	private CommandExecutionHelper commandExecutionHelper;
	private FileInteractionHelper fileInteractionHelper;
	private TxtStringHelper txtStringHelper;
	private Thread cpuThread, gpuThread;
	private Renderer rendererCPU, rendererGPU;
	private File resultFileCPU, resultFileGPU;
	// private Runnable cpuRenderer, gpuRenderer;
	// ExecutorService executor = Executors.newFixedThreadPool(2);// creating a pool
	// of 5 threads

	public RenderHelper(PApplet p) {
		this.p = p;
		mainActivity = (MainActivity) p;
		pcInfoHelper = new PCInfoHelper(p);
		jsonHelper = new JsonHelper(p);
		commandExecutionHelper = new CommandExecutionHelper(p);
		fileInteractionHelper = new FileInteractionHelper(p);
		txtStringHelper = new TxtStringHelper(p);
	}

	public void startRenderJob(String pathToRenderJobs, String pathToRenderJobsStatus, Boolean useCPU) {
		try {
			int startFrame, endFrame, stillFrame, resX, resY, samples, animationFrame;
			Boolean renderAnimation, renderStillFrame, useNewResolution;
			String filePath, imageSavePath, blendFileName;

			allRenderJobs = jsonHelper.getData(pathToRenderJobs);
			String cpuOrGpuStr;
			if (useCPU) {
				cpuOrGpuStr = "CPU";
			} else {
				cpuOrGpuStr = "GPU";
			}
			renderJobIndex = getNextJob(jsonHelper.getData(pathToRenderJobsStatus), pathToRenderJobsStatus, cpuOrGpuStr);

			if (renderJobIndex >= 0) {

				renderJob = (JSONObject) allRenderJobs.get(renderJobIndex);
				imageSavePath = renderJob.get("imageSavePath").toString();

				startFrame = Integer.parseInt(renderJob.get("startFrame").toString());
				endFrame = Integer.parseInt(renderJob.get("endFrame").toString());
				stillFrame = Integer.parseInt(renderJob.get("stillFrame").toString());
				resX = Integer.parseInt(renderJob.get("resX").toString());
				resY = Integer.parseInt(renderJob.get("resY").toString());
				samples = Integer.parseInt(renderJob.get("samples").toString());
				animationFrame = Integer.parseInt(renderJob.get("animationFrame").toString());

				renderAnimation = Boolean.parseBoolean(renderJob.get("renderAnimation").toString());
				renderStillFrame = Boolean.parseBoolean(renderJob.get("renderStillFrame").toString());
				useNewResolution = Boolean.parseBoolean(renderJob.get("useNewResolution").toString());

				int frameToRender = 0;
				if (renderAnimation) {
					frameToRender = animationFrame;
				} else {
					frameToRender = stillFrame;
				}
				// copy blendfile to localfolder,if it doesnt already exist (one folder for CPU,
				// one for GPU)
				File logFileCPU = new File(mainActivity.getRenderLogPathCPU(mainActivity.getPCName(), false));
				File logFileGPU = new File(mainActivity.getRenderLogPathGPU(mainActivity.getPCName(), false));

				File blendFile = new File(mainActivity.getPathToBlenderRenderFolder() + "\\" + renderJob.get("blendfile").toString());
				blendFileName = fileInteractionHelper.getNameWithoutExtension(blendFile);
				// blendFileName = blendFile.getName().replaceFirst("[.][^.]+$", "");

				File localBlendfile = new File(mainActivity.getLocalRenderBlendfiles() + "\\" + blendFile.getName());
				if (!localBlendfile.exists()) {
					fileInteractionHelper.copyFile(blendFile.getAbsolutePath(), localBlendfile.getAbsolutePath());
				}

				File randomSeedFile = new File(mainActivity.getPathToBlenderRenderFolder() + "\\job_" + renderJobIndex + "_randomSeed.py");
				if (!randomSeedFile.exists()) {
					fileInteractionHelper.copyFile(mainActivity.getRenderPythonScriptsPath() + "\\randomSeed.py", randomSeedFile.getAbsolutePath());
				}

				File forceGPURenderingFile = new File(mainActivity.getPathToBlenderRenderFolder() + "\\job_" + renderJobIndex + "_forceGPURendering.py");
				if (!forceGPURenderingFile.exists()) {
					fileInteractionHelper.copyFile(mainActivity.getRenderPythonScriptsPath() + "/forceGPURendering.py", forceGPURenderingFile.getAbsolutePath());
				}
				String fName = "\\";
				for (int i = 0; i < imageFilenameDigits; i++) {
					fName += "#";
				}
				resultFileCPU = new File(mainActivity.getCloudImageFolder() + "\\" + blendFileName + fName);
				resultFileGPU = resultFileCPU;
				if (useCPU) {
					fileInteractionHelper.createParentFolders(resultFileCPU.getAbsolutePath());
					if (resultFileCPU.exists()) {
						resultFileCPU.delete();
					}
				} else {
					fileInteractionHelper.createParentFolders(resultFileGPU.getAbsolutePath());
					if (resultFileGPU.exists()) {
						resultFileGPU.delete();
					}
				}
				File resolutionAndSampling = new File(mainActivity.getPathToBlenderRenderFolder() + "\\" + blendFileName + "job_" + renderJobIndex + "_resolutionAndSampling.py");
				fileInteractionHelper.createParentFolders(logFileCPU.getAbsolutePath());
				fileInteractionHelper.createParentFolders(logFileGPU.getAbsolutePath());

				blendCommandCPU = "blender -b \"" + localBlendfile.getAbsolutePath() + "\" -P \"" + randomSeedFile.getAbsolutePath() + "\"";
				blendCommandGPU = blendCommandCPU;

				String resAndSamplingStr = "import bpy\r\n" + "bpy.context.scene.cycles.samples = " + samples;
				if (useNewResolution) {
					resAndSamplingStr += "\r\n" + "for scene in bpy.data.scenes:\r\n" + "    scene.render.resolution_x = " + resX + "\r\n" + "    scene.render.resolution_y = " + resY + "\r\n";
				}
				txtStringHelper.writeToFile(resAndSamplingStr, resolutionAndSampling.getAbsolutePath());

				String resolutionSamplingStr = " -P \"" + resolutionAndSampling.getAbsolutePath() + "\"";
				blendCommandCPU += resolutionSamplingStr;
				blendCommandGPU += resolutionSamplingStr;

				blendCommandGPU += " -P \"" + forceGPURenderingFile.getAbsolutePath() + "\"";
				if (useCPU) {
					blendCommandCPU += " -o \"" + resultFileCPU.getAbsolutePath() + "\" -F PNG -f " + frameToRender + " >>" + logFileCPU.getAbsolutePath();
				} else {
					blendCommandGPU += " -o \"" + resultFileGPU.getAbsolutePath() + "\" -F PNG -f " + frameToRender + " >>" + logFileGPU.getAbsolutePath();
				}
				if (localBlendfile.exists() && randomSeedFile.exists() && forceGPURenderingFile.exists() && ((useNewResolution && resolutionAndSampling.exists()) || (!useNewResolution))) {

					logFileCPU.delete();
					logFileGPU.delete();

					// delete existing resultFile----------------------
					File deleteFile = new File(resultFileCPU.getParentFile().getAbsolutePath() + "\\" + p.nf(frameToRender, imageFilenameDigits) + ".png"); // file to check --> deleteFile=checkFile
					if (deleteFile.exists()) {
						deleteFile.delete();
					}
					// delete existing resultFile----------------------

					if (useCPU) {
				
						Renderer rendererCPU = new Renderer(p, renderJobIndex, allJobsStarted,useCPU, blendCommandCPU, renderTerminalWindowNameCPU, pathToRenderJobsStatus, logFileCPU, deleteFile, localBlendfile, randomSeedFile, resolutionAndSampling, forceGPURenderingFile);
						cpuThread = new Thread(rendererCPU);
						cpuThread.start();
						rendererCPU.handleJson(renderJobIndex, "started", p.str(true), pathToRenderJobsStatus, cpuOrGpuStr);

						p.println("--------", rendererCPU.getAllJobsStarted(), cpuThread.isAlive());
					} else {		
						Renderer rendererGPU = new Renderer(p, renderJobIndex, allJobsStarted,useCPU, blendCommandGPU, renderTerminalWindowNameGPU, pathToRenderJobsStatus, logFileGPU, deleteFile, localBlendfile, randomSeedFile, resolutionAndSampling, forceGPURenderingFile);
						gpuThread = new Thread(rendererGPU);
						gpuThread.start();
						rendererGPU.handleJson(renderJobIndex, "started", p.str(true), pathToRenderJobsStatus, cpuOrGpuStr);
						p.println("--------", rendererGPU.getAllJobsStarted(), gpuThread.isAlive());
					}
					// System.gc();

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void checkForRestart(String pathToRenderJobsStatus) {
		JSONArray loadedArray = jsonHelper.getData(mainActivity.getRestartCommandFilePath());
		if (loadedArray != null && loadedArray.size() > 0) {
			try {
				for (int i = 0; i < loadedArray.size(); i++) {
					JSONObject restartObj = (JSONObject) loadedArray.get(i);
					String pcAlias = restartObj.get("pcName").toString();
					if (mainActivity.getPCName().equals(pcAlias)) {
						Boolean restart = Boolean.parseBoolean(restartObj.get("restart").toString());
						if (restart) {
							setRestartValue(loadedArray, i, "restart", false);
							allJobsStarted = false;
							if (rendererCPU != null && cpuThread.isAlive()) {
								p.println("stop cpu");
								commandExecutionHelper.killTaskByWindowtitle(renderTerminalWindowNameCPU);
								rendererCPU.handleJson(renderJobIndexCPU, "started", p.str(false), pathToRenderJobsStatus, "CPU");
							}
							if (rendererGPU != null && gpuThread.isAlive()) {
								p.println("stop gpu");
								commandExecutionHelper.killTaskByWindowtitle(renderTerminalWindowNameGPU);
								rendererGPU.handleJson(renderJobIndexGPU, "started", p.str(false), pathToRenderJobsStatus, "GPU");
							}
							p.println("init loading");
							mainActivity.initializeLoadingScreen();
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	public void setRestartValue(JSONArray arr, int ind, String key, Boolean state) {
		try {
			setFinishAllJobs(true, true);
			JSONObject curObj = (JSONObject) arr.get(ind);
			curObj.put(key, p.str(state));
			arr.set(ind, curObj);
			jsonHelper.clearArray();
			jsonHelper.setArray(arr);
			jsonHelper.writeData(mainActivity.getRestartCommandFilePath());
			p.println("value set");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Boolean getAllJobsFinished(String pathToRenderJobsStatus, String[] allPCNames) {
		Boolean allFinished = true;
		JSONArray allRenderJobsStatus = jsonHelper.getData(pathToRenderJobsStatus);
		renderedFrames = new int[allPCNames.length];
		Arrays.fill(renderedFrames, 0);
		if (allRenderJobsStatus != null && allRenderJobsStatus.size() > 0) {
			for (int i = 0; i < allRenderJobsStatus.size(); i++) {
				JSONObject curObj = (JSONObject) allRenderJobsStatus.get(i);
				Boolean startedJob = Boolean.parseBoolean(curObj.get("started").toString());
				Boolean finishedJob = Boolean.parseBoolean(curObj.get("finished").toString());

				if (!finishedJob || !startedJob) {
					allFinished = false;
					if (!finishedJob && !startedJob) {
						break;
					}
				}
				if (finishedJob) {
					for (int i2 = 0; i2 < renderedFrames.length; i2++) {
						Boolean isEqual = false;
						try {
							isEqual = curObj.get("startedBy").toString().equals(allPCNames[i2]);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (isEqual) {
							renderedFrames[i2] = renderedFrames[i2] + 1;
						}
					}
				}
			}
		} else {
			allFinished = false;
		}
		return allFinished;
	}

	public int[] getRenderedFrames() {
		return renderedFrames;
	}

	private int getNextJob(JSONArray allRenderJobsStatus, String pathToRenderJobsStatus, String cpuOrGPUStr) {
		int nextInd = -1;
		Boolean nextJobFound = false;

		if (allRenderJobsStatus != null && allRenderJobsStatus.size() > 0) {
			for (int i = 0; i < allRenderJobsStatus.size(); i++) {
				JSONObject curObj = (JSONObject) allRenderJobsStatus.get(i);
				if (!nextJobFound) {
					if (!Boolean.parseBoolean(curObj.get("started").toString())) {
						nextInd = i;
						nextJobFound = true;
						break;
					}
				}
			}
		} else {
			// evtl to do--------------
		}
		if (nextInd < 0) {
			allJobsStarted = true;
		}
		return nextInd;
	}

	public Boolean getCPUThreadAlive() {
		if (cpuThread != null && cpuThread.isAlive()) {
			return true;
		} else {
			return false;
		}
	}

	public Boolean getGPUThreadAlive() {
		if (gpuThread != null && gpuThread.isAlive()) {
			return true;
		} else {
			return false;
		}
	}

	public Boolean getStartRenderingFromJson() {
		try {
			int mode;
			String modeName;
			if (mainActivity.getIsMaster()) {
				mode = mainActivity.getHomeScreenMaster().getMode() - 1;
				modeName = mainActivity.getModeNamesMaster()[mode];
			} else {
				mode = mainActivity.getHomeScreenSlaves().getMode() - 1;
				modeName = mainActivity.getModeNamesSlaves()[mode];
			}
			JSONObject homeObj = (JSONObject) (jsonHelper.getData(mainActivity.getMasterCommandFilePath()).get(mode));
			JSONObject innerHomeObj = (JSONObject) homeObj.get(modeName);
			return Boolean.parseBoolean(innerHomeObj.get("startRendering").toString());

		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}

	public void setFinishAllJobs(Boolean cpuState, Boolean gpuState) {
		// finishCPUJob = cpuState;
		// finishGPUJob = gpuState;
		if (rendererCPU != null && cpuThread.isAlive()) {
			rendererCPU.setFinishJob(cpuState);
		}
		if (rendererGPU != null && gpuThread.isAlive()) {
			rendererGPU.setFinishJob(gpuState);
		}
	}

	public Boolean getCpuFinished() {
		if (cpuThread == null || !cpuThread.isAlive()) {
			return true;
		} else {
			return false;
		}
	}

	public Boolean getGpuFinished() {
		if (gpuThread == null || !gpuThread.isAlive()) {
			return true;
		} else {
			return false;
		}
	}

	public Boolean getAllJobsStarted() {
		return allJobsStarted;
	}

	public int getImageFilenameDigits() {
		return imageFilenameDigits;
	}
	public Thread getCPUThread() {
		return cpuThread;
	}
	public Thread getGPUThread() {
		return gpuThread;
	}
}
