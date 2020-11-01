package com.dominic.network_apk;

import java.io.File;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.gson.JsonObject;

import processing.core.PApplet;

public class RenderHelper {
	private int renderJobIndex = 0;
	private Boolean isStarted = false, isFinished = false, allJobsFinished = false;
	private long curTime, prevCheckFinishedTime;
	private String renderTerminalWindowName = "renderTerminal";
	private PApplet p;
	private JSONObject renderJob;
	private JSONArray allRenderJobs;
	private MainActivity mainActivity;
	private PCInfoHelper pcInfoHelper;
	private JsonHelper jsonHelper;
	private CommandExecutionHelper commandExecutionHelper;
	private FileInteractionHelper fileInteractionHelper;
	private TxtStringHelper txtStringHelper;

	public RenderHelper(PApplet p) {
		this.p = p;
		mainActivity = (MainActivity) p;
		pcInfoHelper = new PCInfoHelper(p);
		jsonHelper = new JsonHelper(p);
		commandExecutionHelper = new CommandExecutionHelper(p);
		fileInteractionHelper = new FileInteractionHelper(p);
		txtStringHelper = new TxtStringHelper(p);
	}

	public void calculate() {

		curTime = pcInfoHelper.getCurTime();
		if (curTime - prevCheckFinishedTime > mainActivity.getStdTimeIntervall()) {
			isFinished = checkIfFinished();
			prevCheckFinishedTime = curTime;
		}
	}

	private Boolean checkIfFinished() {
		Boolean jobFinished = false;
		allJobsFinished = false;
		return jobFinished;
	}

	public Boolean getisStarted() {
		return isStarted;
	}

	public Boolean getIsFinished() {
		return isFinished;
	}

	public Boolean getAllJobsFinished() {
		return allJobsFinished;
	}

	public void setIsStarted(Boolean state) {
		isStarted = state;
	}

	public void setIsFinished(Boolean state) {
		isFinished = state;
	}

	public void setupRenderJob(String pathToRenderJobs, int setJobIndex, Boolean useCPU) {
		try {
			int startFrame, endFrame, stillFrame, resX, resY, samples, animationFrame;
			Boolean renderAnimation, renderStillFrame, useNewResolution;
			String filePath, blendFileName;

			renderJobIndex = setJobIndex;
			allRenderJobs = jsonHelper.getData(pathToRenderJobs);
			p.println(allRenderJobs);

			renderJob = (JSONObject) allRenderJobs.get(renderJobIndex);
			p.println(renderJob);

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
			
			//copy blendfile to localfolder,if it doesnt already exist (one folder for CPU, one for GPU)

			File blendFile = new File(mainActivity.getPathToBlenderRenderFolder() + "\\" + renderJob.get("blendfile").toString());  //to do: change to local blendfile path
			blendFileName = blendFile.getName().replaceFirst("[.][^.]+$", "");
			File randomSeedFile = new File(mainActivity.getRenderPythonScriptsPath() + "\\randomSeed.py");
			File forceGPURenderingFile = new File(mainActivity.getRenderPythonScriptsPath() + "\\forceGPURendering.py");
			File saveResultsFile = new File(mainActivity.getPathToImageFolder() + "\\" + blendFileName + "\\render_####");
			File resolutionAndSampling = new File(mainActivity.getPathToBlenderRenderFolder() + "\\" + blendFileName + "job_" + setJobIndex + "_resolutionAndSampling.py");
			fileInteractionHelper.createParentFolders(saveResultsFile.getAbsolutePath());
			fileInteractionHelper.createParentFolders(mainActivity.getRenderLogPath());

			String blendCommandCPU = "blender -b \"" + blendFile.getAbsolutePath() + "\" -P \"" + randomSeedFile.getAbsolutePath() + "\"";
			String blendCommandGPU;

			if (useNewResolution) {
				txtStringHelper.writeToFile("import bpy\r\n" + "\r\n" + "for scene in bpy.data.scenes:\r\n" + "    scene.render.resolution_x = " + resX + "\r\n" + "    scene.render.resolution_y = " + resY + "\r\n" + "\r\n" + "bpy.context.scene.cycles.samples = " + samples, resolutionAndSampling.getAbsolutePath());
				blendCommandCPU += " -P \"" + resolutionAndSampling.getAbsolutePath() + "\"";
				p.println(resolutionAndSampling.getAbsolutePath());
			}

			blendCommandGPU = blendCommandCPU;
			blendCommandGPU += " -P \"" + forceGPURenderingFile.getAbsolutePath() + "\"";

			String endBlendCmd = " -o \"" + saveResultsFile.getAbsolutePath() + "\" -F PNG -f 2 >>" + mainActivity.getRenderLogPath();
			blendCommandCPU += endBlendCmd;
			blendCommandGPU += endBlendCmd;


			if (useCPU) {
				String[] commandsCPU = { "cd " + new File(mainActivity.getSettingsScreen().getPathSelectors()[0].getPath()).getParentFile().getAbsolutePath(), "ECHO -----------------------------", "ECHO Started Renderprocess on CPU", "ECHO -----------------------------", blendCommandCPU, "EXIT" };
				Boolean isExecuted = commandExecutionHelper.executeMultipleCommands(commandsCPU, renderTerminalWindowName);
				if (isExecuted) {
					p.println("startedRendering on cpu");
				} else {
					p.println("failed to start rendering on cpu");
				}
			}
			else {
				String[] commandsCPU = { "cd " + new File(mainActivity.getSettingsScreen().getPathSelectors()[0].getPath()).getParentFile().getAbsolutePath(), "ECHO -----------------------------", "ECHO Started Renderprocess on GPU", "ECHO -----------------------------", blendCommandGPU, "EXIT" };
				Boolean isExecuted = commandExecutionHelper.executeMultipleCommands(commandsCPU, renderTerminalWindowName);
				if (isExecuted) {
					p.println("startedRendering on cpu");
				} else {
					p.println("failed to start rendering on cpu");
				}
			}
			isStarted = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
