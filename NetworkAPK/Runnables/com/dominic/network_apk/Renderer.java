package com.dominic.network_apk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import processing.core.PApplet;

class Renderer implements Runnable {
	private int renderJobIndex;
	private Boolean finishJob = false, allJobsStarted, isCPU;
	private String blendCommand, renderTerminalWindowName, pathToRenderJobsStatus, cpuOrGpuStr;
	private long startTime, lastLogFound;
	private PApplet p;
	private File logFile, imageCheckFile, localBlendFile, randomSeed, resolutionAndSampling, forceGPURendering;
	private MainActivity mainActivity;
	private CommandExecutionHelper commandExecutionHelper;
	private PCInfoHelper pcInfoHelper;
	private FileInteractionHelper fileInteractionHelper;
	private JsonHelper jsonHelper;

	public Renderer(PApplet p, int renderJobIndex, Boolean allJobsStarted, Boolean isCPU, String blendCommand, String renderTerminalWindowName, String pathToRenderJobsStatus, File logFile, File imageCheckFile, File localBlendFile, File randomSeed, File resolutionAndSampling, File forceGPURendering) {
		this.p = p;
		this.renderJobIndex = renderJobIndex;
		this.blendCommand = blendCommand;
		this.allJobsStarted = allJobsStarted;
		this.isCPU = isCPU;
		this.renderTerminalWindowName = renderTerminalWindowName;
		this.pathToRenderJobsStatus = pathToRenderJobsStatus;
		this.logFile = logFile;
		this.imageCheckFile = imageCheckFile;
		this.localBlendFile = localBlendFile;
		this.randomSeed = randomSeed;
		this.resolutionAndSampling = resolutionAndSampling;
		this.forceGPURendering = forceGPURendering;
		mainActivity = (MainActivity) p;
		commandExecutionHelper = new CommandExecutionHelper(p);
		pcInfoHelper = new PCInfoHelper(p);
		fileInteractionHelper = new FileInteractionHelper(p);
		jsonHelper = new JsonHelper(p);

		if (isCPU) {
			cpuOrGpuStr = "CPU";
		} else {
			cpuOrGpuStr = "GPU";
		}
	}

	public void run() {
		System.out.println(Thread.currentThread().getName() + " (Start)");
		lastLogFound = pcInfoHelper.getCurTime();

		String[] commands = { "cd " + new File(mainActivity.getSettingsScreen().getPathSelectors()[0].getPath()).getParentFile().getAbsolutePath(), "ECHO -----------------------------", "ECHO Started Renderprocess on " + cpuOrGpuStr, "ECHO -----------------------------", blendCommand, "ECHO Job finished >> " + logFile.getAbsolutePath(), "EXIT" };
		Boolean isExecuted = commandExecutionHelper.executeMultipleCommands(commands, renderTerminalWindowName, true);
		if (isExecuted) {
			

			startTime = pcInfoHelper.getCurTime();
			Boolean problemOccured = false;
			int failedCount = 0, count = 0, doOnce = 0;
			File syncedLogFile;
			if (isCPU) {
				syncedLogFile = new File(mainActivity.getRenderLogPathCPU(mainActivity.getPCName(), true));
			} else {
				syncedLogFile = new File(mainActivity.getRenderLogPathGPU(mainActivity.getPCName(), true));
			}

			while (!imageCheckFile.exists()) {
				
				sleep((int) p.random(1500, 2500));
				
				if (count % 1 == 0) {
					syncedLogFile.delete();
					int tries = 0;
					while (!logFile.exists()) { // was logCPU
						sleep(200);
						if (tries > 10) {
							break;
						}
						tries++;
					}
					fileInteractionHelper.copyFile(logFile.getAbsolutePath(), syncedLogFile.getAbsolutePath());
				}
				count++;

				if (!checkIfJobExists(localBlendFile, pathToRenderJobsStatus, renderJobIndex, cpuOrGpuStr) || finishJob) {
					finishJob = false;
					problemOccured = true;
					break;
				}

				if (!commandExecutionHelper.isWindowOpen(renderTerminalWindowName)) {
					p.println("+++stoped+++");
					failedCount++;
					if (failedCount > 1) {
						problemOccured = true;
						break;
					}
				} else {
					failedCount = 0;
				}

				if (logFile.exists()) {
					lastLogFound = pcInfoHelper.getCurTime();
					if (System.currentTimeMillis() - logFile.lastModified() > mainActivity.getLongTimeIntervall() * 1000) {
						problemOccured = true;
						break;
					}
				}

				if (pcInfoHelper.getCurTime() - lastLogFound > mainActivity.getShortTimeIntervall()) {
					problemOccured = true;
					break;
				}
				if (pcInfoHelper.getCurTime() - startTime > mainActivity.getSuperLongTimeIntervall()) {
					p.println("took too long");
					problemOccured = true;
					break;
				}

			}
			p.println("++++++ exited while loop");
			localBlendFile.delete();
			randomSeed.delete();
			resolutionAndSampling.delete();
			forceGPURendering.delete();
			p.println("cpu problem occured: " + problemOccured);
			if (problemOccured) {
				commandExecutionHelper.killTaskByWindowtitle(renderTerminalWindowName);
				allJobsStarted = false;
				handleJson(renderJobIndex, "started", p.str(false), pathToRenderJobsStatus, cpuOrGpuStr);
			} else {
				handleJson(renderJobIndex, "finished", p.str(true), pathToRenderJobsStatus, cpuOrGpuStr);
			}
			sleep((int) p.random(1000, 1500));
		} else {
			p.println("failed to start rendering on " + cpuOrGpuStr);
		}

		System.out.println(Thread.currentThread().getName() + " (End)");// prints thread name

	}

	public void handleJson(int index, String key, String value, String path, String cpuOrGpuStr) {
		Boolean valueSet = false;
		int iterations = 0;
		JsonHelper jHelper = new JsonHelper(p);
		File jsonFile = new File(path);

		while (isFilelocked(jsonFile) || !jsonFile.exists()) {
			iterations++;
			if (iterations > 500) {
				break;
			}
		}
		iterations = 0;

		while (!valueSet) {

			try {
				File file = new File(jsonFile.getAbsolutePath());
				FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
				FileLock lock = channel.lock();
				System.out.println("Locked File");

				JSONArray array = getMergedArray(path, channel);
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

					// jHelper.setArray(array);
					// jHelper.writeData(path);
					Gson gson = new GsonBuilder().setPrettyPrinting().create();
					String prettyStr = gson.toJson(array);
					p.println("now writing",index);
					jHelper.writeFileChannel(jsonFile, channel, ByteBuffer.wrap(prettyStr.getBytes()));

					if (lock != null) {
						lock.release();
					}
					channel.close();

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
		System.out.println("Released Lock");
	}

	private JSONArray getMergedArray(String path, FileChannel channel) {
		JsonHelper jHelper = new JsonHelper(p);

		// JSONArray mergedArray = jHelper.getData(path);
		JSONArray mergedArray = jHelper.getJSONArrayFromFileChannel(channel);

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

	private boolean isFilelocked(File file) {
		try {
			try (FileInputStream in = new FileInputStream(file)) {
				in.read();
				return false;
			}
		} catch (FileNotFoundException e) {
			return file.exists();
		} catch (Exception e) {
			return true;
		}
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
					handleJson(renderJobIndex, "finished", p.str(true), pathToRenderJobsStatus, cpuOrGpuStr);
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

	private void sleep(int timeToSleep) {
		try {
			Thread.sleep(timeToSleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Boolean getAllJobsStarted() {
		return allJobsStarted;
	}

	public void setFinishJob(Boolean state) {
		finishJob = state;
	}
}
