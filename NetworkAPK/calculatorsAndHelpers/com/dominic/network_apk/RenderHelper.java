package com.dominic.network_apk;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jdk.jshell.spi.ExecutionControl.ExecutionControlException;
import processing.core.PApplet;

public class RenderHelper {
	private int renderJobIndex = 0, renderJobIndexCPU, renderJobIndexGPU, jobsDone = 0,filenameDigits=6;
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

	public int getJobsDone() {
		return jobsDone;
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

	public Boolean getCPUIsRendering() {
		return cpuThread != null;
	}

	public Boolean getGPUIsRendering() {
		return gpuThread != null;
	}

	public Boolean getAllJobsStarted() {
		return allJobsStarted;
	}

	public Boolean getAllJobsFinished(String pathToRenderJobsStatus, String[] allPCNames) {
		Boolean allFinished = true;
		JSONArray allRenderJobsStatus = jsonHelper.getData(pathToRenderJobsStatus);
		renderedFrames = new int[allPCNames.length];
		Arrays.fill(renderedFrames, 0);
		for (int i = 0; i < allRenderJobsStatus.size(); i++) {
			JSONObject curObj = (JSONObject) allRenderJobsStatus.get(i);
			Boolean startedJob = Boolean.parseBoolean(curObj.get("started").toString());
			Boolean finishedJob = Boolean.parseBoolean(curObj.get("finished").toString());

			if (!finishedJob || !startedJob) {
				allFinished = false;
				if (i > jobsDone) {
					jobsDone = i;
				}
				if(!finishedJob && !startedJob) {
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
		return allFinished;
	}

	public int[] getRenderedFrames() {
		return renderedFrames;
	}

	private int getNextJob(JSONArray allRenderJobsStatus, String pathToRenderJobsStatus, String cpuOrGPUStr) {
		int nextInd = -1;
		Boolean nextJobFound = false;

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
				String fName="\\";
				for(int i=0;i<filenameDigits;i++) {
					fName+="#";
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
					File deleteFile = new File(resultFileCPU.getParentFile().getAbsolutePath() + "\\" + p.nf(frameToRender, filenameDigits) + ".png");
					if (deleteFile.exists()) {
						deleteFile.delete();
					}
					// delete existing resultFile----------------------

					if (useCPU) {
						renderJobIndexCPU = renderJobIndex;
						File localFileCPU = localBlendfile;
						File checkFile = deleteFile;
						File randomSeedCPU = randomSeedFile;
						File resolutionAndSamplingCPU = resolutionAndSampling;
						File forceGPURenderingCPU = forceGPURenderingFile;
						File logCPU = logFileCPU;
						lastCPULogFound = pcInfoHelper.getCurTime();
						handleJson(renderJobIndexCPU, "started", p.str(true), pathToRenderJobsStatus, "CPU");

						cpuThread = new Thread(new Runnable() {
							@Override
							public void run() {
								String[] commands = { "cd " + new File(mainActivity.getSettingsScreen().getPathSelectors()[0].getPath()).getParentFile().getAbsolutePath(), "ECHO -----------------------------", "ECHO Started Renderprocess on CPU", "ECHO -----------------------------", blendCommandCPU, "ECHO Job finished >> " + logFileCPU.getAbsolutePath(), "EXIT" };
								Boolean isExecuted = commandExecutionHelper.executeMultipleCommands(commands, renderTerminalWindowNameCPU, true);
								if (isExecuted) {
									startTimeCpu = pcInfoHelper.getCurTime();
									Boolean problemOccured = false;
									int failedCount = 0, count = 0;
									File syncedLogFile = new File(mainActivity.getRenderLogPathCPU(mainActivity.getPCName(), true));

									while (!checkFile.exists()) {

										if (count % 2 == 0) {
											syncedLogFile.delete();
											int tries = 0;
											while (!logCPU.exists()) {
												// waitOnFinish(startTimeCpu, 200);
												if (tries > 10) {
													break;
												}
												tries++;
											}
											fileInteractionHelper.copyFile(logCPU.getAbsolutePath(), syncedLogFile.getAbsolutePath());
										}
										count++;

										if (!checkIfJobExists(localFileCPU, pathToRenderJobsStatus, renderJobIndexCPU, "CPU") || finishCPUJob) {
											commandExecutionHelper.killTaskByWindowtitle(renderTerminalWindowNameCPU);
											finishCPUJob = false;
											problemOccured = true;
											break;
										}

										if (!commandExecutionHelper.isWindowOpen(renderTerminalWindowNameCPU)) {
											p.println("+++stoped+++");
											failedCount++;
											if (failedCount > 1) {
												problemOccured = true;
												break;
											}
										} else {
											failedCount = 0;
										}

										if (logCPU.exists()) {
											lastCPULogFound = pcInfoHelper.getCurTime();
											if (System.currentTimeMillis() - logCPU.lastModified() > mainActivity.getLongTimeIntervall() * 1000) {
												commandExecutionHelper.killTaskByWindowtitle(renderTerminalWindowNameCPU);
												problemOccured = true;
												break;
											}
										}

										if (pcInfoHelper.getCurTime() - lastCPULogFound > mainActivity.getShortTimeIntervall()) {
											problemOccured = true;
											commandExecutionHelper.killTaskByWindowtitle(renderTerminalWindowNameCPU);
											break;
										}

										waitOnFinish(startTimeGpu, (int) p.random(1500, 2500));
									}
									localFileCPU.delete();
									randomSeedCPU.delete();
									resolutionAndSamplingCPU.delete();
									forceGPURenderingCPU.delete();
									p.println("cpu problem occured: " + problemOccured);
									if (problemOccured) {
										allJobsStarted = false;
										handleJson(renderJobIndexCPU, "started", p.str(false), pathToRenderJobsStatus, "CPU");
									} else {
										handleJson(renderJobIndexCPU, "finished", p.str(true), pathToRenderJobsStatus, "CPU");
									}
									waitOnFinish(startTimeCpu, (int) p.random(1000, 1500));
								} else {
									p.println("failed to start rendering on cpu");
								}
							}
						});
						cpuThread.start();

					} else {
						renderJobIndexGPU = renderJobIndex;
						File localFileGPU = localBlendfile;
						File checkFile = deleteFile;
						File randomSeedGPU = randomSeedFile;
						File resolutionAndSamplingGPU = resolutionAndSampling;
						File forceGPURenderingGPU = forceGPURenderingFile;
						File logGPU = logFileGPU;
						p.println(resolutionAndSampling.getAbsolutePath(), "---", forceGPURenderingFile.getAbsolutePath());
						handleJson(renderJobIndexGPU, "started", p.str(true), pathToRenderJobsStatus, "GPU");

						lastGPULogFound = pcInfoHelper.getCurTime();

						gpuThread = new Thread(new Runnable() {
							@Override
							public void run() {

								String[] commands = { "cd " + new File(mainActivity.getSettingsScreen().getPathSelectors()[0].getPath()).getParentFile().getAbsolutePath(), "ECHO -----------------------------", "ECHO Started Renderprocess on GPU", "ECHO -----------------------------", blendCommandGPU, "ECHO Job finished >> " + logFileGPU.getAbsolutePath(), "EXIT" };
								Boolean isExecuted = commandExecutionHelper.executeMultipleCommands(commands, renderTerminalWindowNameGPU, true);
								if (isExecuted) {
									startTimeGpu = pcInfoHelper.getCurTime();
									Boolean problemOccured = false;
									int failedCount = 0, count = 0;
									File syncedLogFile = new File(mainActivity.getRenderLogPathGPU(mainActivity.getPCName(), true));

									while (!checkFile.exists()) {

										if (count % 2 == 0) {
											syncedLogFile.delete();
											int tries = 0;
											while (!logGPU.exists()) {
												// waitOnFinish(startTimeCpu, 200);
												if (tries > 10) {
													break;
												}
												tries++;
											}
											fileInteractionHelper.copyFile(logGPU.getAbsolutePath(), syncedLogFile.getAbsolutePath());
										}
										count++;

										if (!checkIfJobExists(localFileGPU, pathToRenderJobsStatus, renderJobIndexGPU, "GPU") || finishGPUJob) {
											commandExecutionHelper.killTaskByWindowtitle(renderTerminalWindowNameGPU);
											finishGPUJob = false;
											problemOccured = true;
											break;
										}

										if (!commandExecutionHelper.isWindowOpen(renderTerminalWindowNameGPU)) {
											p.println("+++stoped+++");
											failedCount++;
											if (failedCount > 1) {
												problemOccured = true;
												break;
											}
										} else {
											failedCount = 0;
										}
										if (logGPU.exists()) {
											lastGPULogFound = pcInfoHelper.getCurTime();
											if (System.currentTimeMillis() - logGPU.lastModified() > mainActivity.getLongTimeIntervall() * 1000) {
												commandExecutionHelper.killTaskByWindowtitle(renderTerminalWindowNameGPU);
												problemOccured = true;
												break;
											}
										}
										if (pcInfoHelper.getCurTime() - lastGPULogFound > mainActivity.getShortTimeIntervall()) {
											problemOccured = true;
											commandExecutionHelper.killTaskByWindowtitle(renderTerminalWindowNameGPU);
											break;
										}

										waitOnFinish(startTimeGpu, (int) p.random(1500, 2500));
									}

									localFileGPU.delete();
									randomSeedGPU.delete();
									resolutionAndSamplingGPU.delete();
									forceGPURenderingGPU.delete();
									p.println("gpu problem occured: " + problemOccured);

									if (problemOccured) {
										allJobsStarted = false;
										handleJson(renderJobIndexGPU, "started", p.str(false), pathToRenderJobsStatus, "GPU");
									} else {
										handleJson(renderJobIndexGPU, "finished", p.str(true), pathToRenderJobsStatus, "GPU");
									}
									waitOnFinish(startTimeCpu, (int) p.random(1000, 1500));
								} else {
									p.println("failed to start rendering on gpu");
								}
							}
						});
						gpuThread.start();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Boolean waitOnFinish(long startTime, int timeToSleep) {
		try {
			Thread.sleep(timeToSleep - System.currentTimeMillis() % timeToSleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (pcInfoHelper.getCurTime() - startTime > mainActivity.getSuperLongTimeIntervall()) {
			return true;
		} else {
			return false;
		}
	}

	private void handleJson(int index, String key, String value, String path, String cpuOrGpuStr) {
		Boolean valueSet = false;
		int iterations = 0;
		JsonHelper jHelper = new JsonHelper(p);
		File jsonFile = new File(path);
		while (!jsonFile.canWrite()) {
		}
		try {
			FileOutputStream fos = new FileOutputStream("file.txt");
			FileLock fl = fos.getChannel().tryLock();
			if (fl != null) {
				System.out.println("Locked File");
				// Thread.sleep(100);
				p.println("now writing");
				while (!valueSet) {
					try {
						JSONArray array = getMergedArray(path);
						JSONObject curObj = (JSONObject) array.get(index);
						if (array.size() > 0) {
							jHelper.clearArray();
							if (key.equals("started")) {
								curObj.put("startedBy", mainActivity.getPCName());
								curObj.put("hardware", cpuOrGpuStr);
							}
							p.println(index, key, value, cpuOrGpuStr);
							curObj.put(key, value);
							array.set(index, curObj);
							jHelper.setArray(array);
							jHelper.writeData(path);
							valueSet = true;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					iterations++;
					if (iterations > 500) {
						break;
					}
				}
				fl.release();
				System.out.println("Released Lock");
			}
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JSONArray getMergedArray(String path) {
		JsonHelper jHelper = new JsonHelper(p);

		JSONArray mergedArray = jHelper.getData(path);
		File mergeFile = new File(path);
		String[] paths = fileInteractionHelper.getFoldersAndFiles(mergeFile.getParentFile().getAbsolutePath(), false);
		ArrayList<String> duplicates = new ArrayList<>();
		p.println("merge");
		if (paths != null && paths.length > 0) {
			// find duplicated/unmerged files ------------------------
			p.println(paths);
			for (int i = 0; i < paths.length; i++) {
				String withoutExtension = fileInteractionHelper.getNameWithoutExtension(mergeFile);
				p.println(withoutExtension, paths[i]);
				if (p.match(paths[i], withoutExtension) != null && withoutExtension.equals(fileInteractionHelper.getNameWithoutExtension(new File(paths[i]))) == false) {
					duplicates.add(paths[i]);
					p.println("-------------" + duplicates.get(duplicates.size() - 1));
				}
			}
			// find duplicated/unmerged files ------------------------
			if (duplicates.size() > 0) { // else no duplicates
				// load duplicated jsonArrays--------------------------
				p.println("duplicates found");
				p.println(mergedArray);
				JSONArray[] loadedArrays = new JSONArray[duplicates.size()];
				for (int i = 0; i < duplicates.size(); i++) {
					loadedArrays[i] = jHelper.getData(mergeFile.getParentFile().getAbsolutePath() + "\\" + duplicates.get(i));
					p.println(loadedArrays[i]);
				}
				// load duplicated jsonArrays--------------------------

				// merge duplicated jsonArrays------------------------------
				for (int i = 0; i < mergedArray.size(); i++) {
					Boolean noneTrue = true;
					for (int i2 = 0; i2 < loadedArrays.length; i2++) {
						try {
							Boolean bothTrue = false;
							JSONObject curMergeObj = (JSONObject) mergedArray.get(i);
							JSONObject curObj = (JSONObject) loadedArrays[i2].get(i);

							Boolean startedMerge = Boolean.parseBoolean(curMergeObj.get("started").toString());
							Boolean finishedMerge = Boolean.parseBoolean(curMergeObj.get("finished").toString());
							// Boolean startedByMerge =
							// Boolean.parseBoolean(curMergeObj.get("startedBy").toString());

							Boolean started = Boolean.parseBoolean(curObj.get("started").toString());
							Boolean finished = Boolean.parseBoolean(curObj.get("finished").toString());
							Boolean startedBy = Boolean.parseBoolean(curObj.get("startedBy").toString());

							if (started || finished || startedMerge || finishedMerge) {
								noneTrue = false;
							}
							if (!startedMerge && started) {
								curMergeObj.put("started", true);
								curMergeObj.put("startedBy", startedBy);
							}
							if (!finishedMerge && finished) {
								curMergeObj.put("finished", true);
							}
							mergedArray.set(i, curMergeObj);
							if (started || startedMerge && finished || finishedMerge) {
								bothTrue = true;
								break;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (noneTrue) {
						break;
					}
				}
				// merge duplicated jsonArrays------------------------------

				// delete duplicates ---------------------------------------
				for (int i = 0; i < duplicates.size(); i++) {
					new File(mergeFile.getParentFile().getAbsolutePath() + "\\" + duplicates.get(i)).delete();
				}
				// delete duplicates ---------------------------------------
			}
		}
		return mergedArray;
	}

	private Boolean checkIfJobExists(File fileToCheck, String pathToRenderJobsStatus, int rendJobInd, String cpuOrGpuStr) {
		Boolean jobExists = false;
		JSONArray allRenderFiles = jsonHelper.getData(mainActivity.getAllRenderFilesJsonPath());
		for (int i = 0; i < allRenderFiles.size(); i++) {
			if (new File(allRenderFiles.get(i).toString()).getName().equals(fileToCheck.getName())) {
				jobExists = true;
				break;
			}
		}
		if (!jobExists) {
			Boolean fileWritten = false;
			int iterations = 0;
			while (!fileWritten) {
				try {
					handleJson(renderJobIndexCPU, "finished", p.str(true), pathToRenderJobsStatus, cpuOrGpuStr);
					fileWritten = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (iterations > 500) {
					break;
				} else {
					iterations++;
				}
			}
		}
		return jobExists;
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
		finishCPUJob = cpuState;
		finishGPUJob = gpuState;
	}
	public int getFilenameDigits() {
		return filenameDigits;
	}

}
