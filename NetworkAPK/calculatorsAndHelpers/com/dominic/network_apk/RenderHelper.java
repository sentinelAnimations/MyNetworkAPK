package com.dominic.network_apk;

import java.io.File;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import processing.core.PApplet;

public class RenderHelper {
	private int renderJobIndex = 0;
	private Boolean cpuFinished = false, gpuFinished = false, allJobsFinished = false;
	private long prevCheckFinishedTime, startTimeCpu, startTimeGpu;
	private String renderTerminalWindowNameCPU = "renderTerminalCPU", renderTerminalWindowNameGPU = "renderTerminalGPU", blendCommandCPU, blendCommandGPU;
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
	private File resultFileCPU, resultFileGPU;

	public RenderHelper(PApplet p) {
		this.p = p;
		mainActivity = (MainActivity) p;
		pcInfoHelper = new PCInfoHelper(p);
		jsonHelper = new JsonHelper(p);
		commandExecutionHelper = new CommandExecutionHelper(p);
		fileInteractionHelper = new FileInteractionHelper(p);
		txtStringHelper = new TxtStringHelper(p);
	}

	private Boolean[] checkIfFinished() {
		Boolean[] jobsFinished = { false, false };
		allJobsFinished = false;
		return jobsFinished;
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

	public Boolean getAllJobsFinished() {
		return allJobsFinished;
	}

	public void setupRenderJob(String pathToRenderJobs, int setJobIndex, Boolean useCPU) {
		try {
			int startFrame, endFrame, stillFrame, resX, resY, samples, animationFrame;
			Boolean renderAnimation, renderStillFrame, useNewResolution;
			String filePath, imageSavePath, blendFileName;

			renderJobIndex = setJobIndex;
			allRenderJobs = jsonHelper.getData(pathToRenderJobs);
			p.println(allRenderJobs);

			renderJob = (JSONObject) allRenderJobs.get(renderJobIndex);
			p.println(renderJob);
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
			File logFileCPU=new File(mainActivity.getRenderLogPathCPU(mainActivity.getPCName()));
			File logFileGPU=new File(mainActivity.getRenderLogPathGPU(mainActivity.getPCName()));

			File blendFile = new File(mainActivity.getPathToBlenderRenderFolder() + "\\" + renderJob.get("blendfile").toString()); // to do: change to local blendfile path
			blendFileName = blendFile.getName().replaceFirst("[.][^.]+$", "");

			File localBlendfile = new File(mainActivity.getLocalRenderBlendfiles() + "\\" + blendFile.getName());
			if (!localBlendfile.exists()) {
				fileInteractionHelper.copyFile(blendFile.getAbsolutePath(), localBlendfile.getAbsolutePath());
			}
			File randomSeedFile = new File(mainActivity.getRenderPythonScriptsPath() + "\\randomSeed.py");
			File forceGPURenderingFile = new File(mainActivity.getRenderPythonScriptsPath() + "\\forceGPURendering.py");
			if (useCPU) {
				resultFileCPU = new File(mainActivity.getPathToImageFolder() + "\\" + blendFileName + "\\#####");
				fileInteractionHelper.createParentFolders(resultFileCPU.getAbsolutePath());
				if (resultFileCPU.exists()) {
					resultFileCPU.delete();
				}
			} else {
				resultFileGPU = new File(mainActivity.getPathToImageFolder() + "\\" + blendFileName + "\\#####");
				fileInteractionHelper.createParentFolders(resultFileGPU.getAbsolutePath());
				if (resultFileGPU.exists()) {
					resultFileGPU.delete();
				}
			}
			File resolutionAndSampling = new File(mainActivity.getPathToBlenderRenderFolder() + "\\" + blendFileName + "job_" + setJobIndex + "_resolutionAndSampling.py");
			fileInteractionHelper.createParentFolders(logFileCPU.getAbsolutePath());
			fileInteractionHelper.createParentFolders(logFileGPU.getAbsolutePath());

			blendCommandCPU = "blender -b \"" + localBlendfile.getAbsolutePath() + "\" -P \"" + randomSeedFile.getAbsolutePath() + "\"";
			blendCommandGPU = blendCommandCPU;

			String resAndSamplingStr = "import bpy\r\n" + "bpy.context.scene.cycles.samples = " + samples;
			if (useNewResolution) {
				resAndSamplingStr += "\r\n" + "for scene in bpy.data.scenes:\r\n" + "    scene.render.resolution_x = " + resX + "\r\n" + "    scene.render.resolution_y = " + resY + "\r\n";
			}
			txtStringHelper.writeToFile(resAndSamplingStr, resolutionAndSampling.getAbsolutePath());
			// txtStringHelper.writeToFile("import bpy\r\n" + "\r\n" + "for scene in
			// bpy.data.scenes:\r\n" + " scene.render.resolution_x = " + resX + "\r\n" + "
			// scene.render.resolution_y = " + resY + "\r\n" + "\r\n" +
			// "bpy.context.scene.cycles.samples = " + samples,
			// resolutionAndSampling.getAbsolutePath());
			String resolutionSamplingStr = " -P \"" + resolutionAndSampling.getAbsolutePath() + "\"";
			blendCommandCPU += resolutionSamplingStr;
			blendCommandGPU += resolutionSamplingStr;

			blendCommandGPU += " -P \"" + forceGPURenderingFile.getAbsolutePath() + "\"";
			p.println(resolutionAndSampling.getAbsolutePath());
			if (useCPU) {
				blendCommandCPU += " -o \"" + resultFileCPU.getAbsolutePath() + "\" -F PNG -f " + frameToRender + " >>" + logFileCPU.getAbsolutePath();
			} else {
				blendCommandGPU += " -o \"" + resultFileGPU.getAbsolutePath() + "\" -F PNG -f " + frameToRender + " >>" + logFileGPU.getAbsolutePath();
			}
			if (localBlendfile.exists() && randomSeedFile.exists() && forceGPURenderingFile.exists() && ((useNewResolution && resolutionAndSampling.exists()) || (!useNewResolution))) {
				
				logFileCPU.delete();
				logFileGPU.delete();
				
				if (useCPU) {
					p.println("started on cpu");
					cpuThread = new Thread(new Runnable() {
						@Override
						public void run() {
							String[] commands = { "cd " + new File(mainActivity.getSettingsScreen().getPathSelectors()[0].getPath()).getParentFile().getAbsolutePath(), "ECHO -----------------------------", "ECHO Started Renderprocess on CPU", "ECHO -----------------------------", blendCommandCPU, "ECHO \"Job finished\" >> " + logFileCPU.getAbsolutePath(), "EXIT" };
							Boolean isExecuted = commandExecutionHelper.executeMultipleCommands(commands, renderTerminalWindowNameCPU);
							if (isExecuted) {
								startTimeCpu = pcInfoHelper.getCurTime();
								long prevCheckTime = 0;
								while (!resultFileCPU.exists() && pcInfoHelper.getCurTime() - prevCheckTime > mainActivity.getStdTimeIntervall()) {
									prevCheckTime = pcInfoHelper.getCurTime();

									if (pcInfoHelper.getCurTime() - startTimeCpu > mainActivity.getSuperLongTimeIntervall()) {
										break;
									}
								}
							} else {
								p.println("failed to start rendering on cpu");
							}
						}
					});
					cpuThread.start();

				} else {
					p.println("started on gpu");

					gpuThread = new Thread(new Runnable() {
						@Override
						public void run() {
							String[] commands = { "cd " + new File(mainActivity.getSettingsScreen().getPathSelectors()[0].getPath()).getParentFile().getAbsolutePath(), "ECHO -----------------------------", "ECHO Started Renderprocess on GPU", "ECHO -----------------------------", blendCommandGPU, "ECHO \"Job finished\" >> " + logFileGPU.getAbsolutePath(), "EXIT" };
							Boolean isExecuted = commandExecutionHelper.executeMultipleCommands(commands, renderTerminalWindowNameGPU);
							if (isExecuted) {
								startTimeGpu = pcInfoHelper.getCurTime();
							} else {
								p.println("failed to start rendering on gpu");
							}
						}
					});
					gpuThread.start();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}