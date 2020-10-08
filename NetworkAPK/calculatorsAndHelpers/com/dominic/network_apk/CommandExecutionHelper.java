package com.dominic.network_apk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import processing.core.PApplet;

public class CommandExecutionHelper {
    PApplet p;

    public CommandExecutionHelper(PApplet p) {
        this.p = p;
    }

    public Boolean executeMultipleCommands(String[] commands, String windowTitle) {
        Boolean executed = false;
        String commandStr = "cmd /c start \"" + windowTitle + "\" cmd.exe /K \"";
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
            executed=true;
            try {
                proc.waitFor();
                final int exitValue = proc.waitFor();
                if (exitValue == 0) {
                    executed=true;
                } else {
                    System.out.println("Failed to execute the following command: " + commandStr + " due to the following error(s):");
                    try (final BufferedReader b = new BufferedReader(new InputStreamReader(proc.getErrorStream()))) {
                        String line;
                        if ((line = b.readLine()) != null)
                            System.out.println(line);
                        executed=true;
                    } catch (final IOException e) {
                        e.printStackTrace();
                        executed=false;
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
        Boolean tastIsKilled = false;
        ArrayList<Integer> allPIDs = new ArrayList();

        try {
            Runtime rt = Runtime.getRuntime();
            String[] commands= {"set windowtitle="+windowtitle,"for /f \"tokens=2\" %a in ('tasklist /V /FI \"IMAGENAME eq cmd.exe\" ^| find /i \"%windowtitle%\"') do taskkill /pid %a"};
            String commandStr = "cmd /c ";
            for (int i = 0; i < commands.length; i++) {
                commandStr += commands[i];
                if (i < commands.length - 1) {
                    commandStr += " && ";
                }
            }
            commandStr += "\"";
            p.println(commandStr);
            //String[] commands = { "cmd", "/c", "TASKLIST", "/FI", "\"WINDOWTITLE eq " + windowtitle + "*\"" };
            Process proc = rt.exec(commandStr);

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            // Read the output from the command
            String s = null;
            int count = 0;
            while ((s = stdInput.readLine()) != null) {
                // println(s, "--");
                String[] splitStr = p.split(s, " ");
                // println("--",s,"--",count);
                if (splitStr.length > 0) {
                    if (count >= 2) {
                        for (int i = 1; i < splitStr.length; i++) {
                            if (splitStr[i].trim().length() > 0) {
                                // println(splitStr[i].trim(),"------");
                                try {
                                    int pid = Integer.parseInt(splitStr[i]);
                                    allPIDs.add(pid);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                        }
                    }
                }
                if (s.trim().length() > 0) {
                    count++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        p.println(allPIDs);
        if (allPIDs.size() > 0) {
            for (int i = 0; i < allPIDs.size(); i++) {
                try {
                    Runtime.getRuntime().exec(new String[] { "cmd", "/c", "TASKKILL", "/PID", p.str(allPIDs.get(i)) });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return tastIsKilled;
    }
}
