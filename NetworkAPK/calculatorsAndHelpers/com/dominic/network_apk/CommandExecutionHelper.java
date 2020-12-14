package com.dominic.network_apk;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.UUID;

import org.json.simple.JSONArray;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import processing.core.PApplet;

public class CommandExecutionHelper {
	private PApplet p;
	private MainActivity mainActivity;
	private TxtStringHelper txtStringHelper;

	public CommandExecutionHelper(PApplet p) {
		this.p = p;
		mainActivity = (MainActivity) p;
		txtStringHelper = new TxtStringHelper(p);
	}

	public Boolean executeMultipleCommands(String[] commands, String windowTitle, Boolean startMinimized) {
		Boolean executed = false;
		String commandStr = "cmd /c start ";
		if (startMinimized) {
			commandStr += "/min ";
		}
		commandStr += "\"" + windowTitle + "\" cmd.exe /K \"";
		for (int i = 0; i < commands.length; i++) {
			commandStr += commands[i];
			if (i < commands.length - 1) {
				commandStr += " && ";
			}
		}
		commandStr += "\"";
		Process proc;
		try {
			proc = Runtime.getRuntime().exec(commandStr);
			executed = true;
			try {
				proc.waitFor();
				final int exitValue = proc.waitFor();
				if (exitValue == 0) {
					executed = true;
				} else {
					System.out.println("Failed to execute the following command: " + commandStr + " due to the following error(s):");
					try (final BufferedReader b = new BufferedReader(new InputStreamReader(proc.getErrorStream()))) {
						String line;
						if ((line = b.readLine()) != null)
							System.out.println(line);
						executed = true;
					} catch (final IOException e) {
						e.printStackTrace();
						executed = false;
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				executed = false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return executed;
	}

	public Boolean killTaskByWindowtitle(String windowtitle) {
		Boolean taskIsKilled = false;
		try {
			Runtime rt = Runtime.getRuntime();
			String[] commands = { "set windowtitle=" + windowtitle, "for /f \"tokens=2\" %a in ('tasklist /V /FI \"IMAGENAME eq cmd.exe\" ^| find /i \"%windowtitle%\"') do taskkill /pid %a" };
			String commandStr = "cmd /c "; // evtl "cmd /c \"";
			for (int i = 0; i < commands.length; i++) {
				commandStr += commands[i];
				if (i < commands.length - 1) {
					commandStr += " && ";
				}
			}
			commandStr += "\"";
			Process proc = rt.exec(commandStr);
			taskIsKilled = true;
			p.println("task was killed");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return taskIsKilled;
	}
	
	public Boolean executeCommand(String command) {
		Boolean executed=false;
		try {
			Runtime rt = Runtime.getRuntime();
			String commandStr = "cmd /c "+command;
			Process proc = rt.exec(commandStr);
			proc.waitFor();
			executed = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return executed;
	}

	public Boolean isWindowOpen(String windowtitle) {
		Boolean isOpen = false;
		String basePath = mainActivity.getOpenCheckPath();
		File logFile = new File(basePath + "\\" + UUID.randomUUID().toString() + "openCheckLog.txt");
		File batFile = new File(basePath + "\\" + UUID.randomUUID().toString() + "isOpenTest.bat");
		String batCmd = "set windowtitle=" + windowtitle + "\nfor /f \"tokens=2\" %%a in ('tasklist /V /FI \"IMAGENAME eq cmd.exe\" ^| find /i \"%windowtitle%\"') do echo %%a >>" + logFile.getAbsolutePath();
		txtStringHelper.writeToFile(batCmd, batFile.getAbsolutePath());
		try {
			Runtime rt = Runtime.getRuntime();
			String[] commands = { batFile.getAbsolutePath() };
			String commandStr = "cmd /c \""; // evtl "cmd /c \"";
			for (int i = 0; i < commands.length; i++) {
				commandStr += commands[i];
				if (i < commands.length - 1) {
					commandStr += " && ";
				}
			}
			commandStr += "\"";
			Process proc = rt.exec(commandStr);
			proc.waitFor();
			final int exitValue = proc.waitFor();
			if (exitValue == 0) {
				try {
					String[] lines = p.loadStrings(logFile.getAbsolutePath());
					if (lines != null && lines.length > 0) {
						isOpen = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		logFile.delete();
		batFile.delete();

		return isOpen;
	}
	
	public Boolean isWindowOpenSimple(String windowtitle) {
		Boolean isOpen=false;
		File logFile=new File(mainActivity.getOpenCheckPath()+"\\opencheckSimple.txt");
		executeCommand("tasklist /FI \"windowtitle eq "+windowtitle+"\" >>"+logFile.getAbsolutePath());
		try {
			String[] lines = p.loadStrings(logFile.getAbsolutePath());
			if (lines != null && lines.length > 0 && p.match(lines[0],"INFO")==null) {
				isOpen = true;
			}
			logFile.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isOpen;
	}
}
