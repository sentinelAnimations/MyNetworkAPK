package com.dominic.network_apk;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;

import processing.core.PApplet;

public class PCInfoHelper {
    private PApplet p;
    private FileInteractionHelper fileInteractionHelper;
    private MainActivity mainActivity;

    public PCInfoHelper(PApplet p) {
        this.p = p;
        fileInteractionHelper = new FileInteractionHelper(p);
        mainActivity = (MainActivity) p;
    }

    public int getAvailableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

    public String getCPUName() {
        String cpuName = "";
        String[][] commands = new String[][] { { "CMD", "/C", "WMIC cpu get Name" } };

        for (int i = 0; i < commands.length; i++) {
            try {
                String[] com = commands[i];
                Process process = Runtime.getRuntime().exec(com);
                process.getOutputStream().close();
                // Closing output stream of the process
                String s = null;
                // Reading sucessful output of the command
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                while ((s = reader.readLine()) != null) {
                    String[] m1 = p.match(s.toUpperCase(), "NAME");
                    if (s.length() > 0 && m1 == null) {
                        cpuName = s;
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
                cpuName = "";
            }
        }
        return cpuName.trim();
    }

    public String getGPUName() {
        String gpuName = "";

        try {

            // ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            // InputStream is =
            // classloader.getResourceAsStream(mainActivity.getHomeScreenSlavePath()+
            // "\\SystemInformations.txt");

            String relativeFilePath = mainActivity.getHomeScreenSlavePath() + "\\SystemInformations.txt";
            String absoluteFilePath = fileInteractionHelper.getAbsolutePath(relativeFilePath);

            // Use "dxdiag /t" variant to redirect output to a given file
            new FileInteractionHelper(p).createParentFolders(absoluteFilePath);
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "dxdiag", "/t", absoluteFilePath);
            Process proc = pb.start();
            proc.waitFor();
            BufferedReader br = new BufferedReader(new FileReader(absoluteFilePath));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().startsWith("Card name:")) {
                    String[] splitStr = p.split(line.trim(), ":");
                    if (splitStr.length == 2) {
                        gpuName = splitStr[1].trim();
                    } else {
                        gpuName = line.trim();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return gpuName;
    }

    public long getCurTime() {
        return System.nanoTime() / 1000000000;
    }
    
    public String getReadableTime() {
        return p.str(p.hour()) + " : " + p.str(p.minute()) + " : " + p.str(p.second());
    }

}
