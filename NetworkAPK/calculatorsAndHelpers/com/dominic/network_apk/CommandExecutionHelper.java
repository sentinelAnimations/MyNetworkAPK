package com.dominic.network_apk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import processing.core.PApplet;

public class CommandExecutionHelper {
    PApplet p;
    public CommandExecutionHelper(PApplet p) {
        this.p=p;
    }
    public Boolean executeMultipleCommands(String[] commands,String windowTitle) {
      Boolean executed=false;
      String commandStr="cmd /c start \""+windowTitle+"\" cmd.exe /K \"";
      for(int i=0;i<commands.length;i++) {
          commandStr+=commands[i];
          if(i<commands.length-1) {
              commandStr+=" && ";
          }
      }
      commandStr+="\"";
      p.println(commandStr);
      try {
          Process p = Runtime
            .getRuntime()
            .exec(commandStr);
          executed=true;
        } 
        catch (IOException e) {
          e.printStackTrace();
        }
      return executed;
    }
    
    public Boolean killTaskByWindowtitle(String windowtitle) {
        Boolean tastIsKilled=false;
        ArrayList<Integer> allPIDs= new ArrayList();

        try {
            Runtime rt = Runtime.getRuntime();
            String[] commands = {"cmd", "/c", "TASKLIST", "/FI", "\"WINDOWTITLE eq "+windowtitle+"*\""};
            Process proc = rt.exec(commands);

            BufferedReader stdInput = new BufferedReader(new 
              InputStreamReader(proc.getInputStream()));

            // Read the output from the command
            String s = null;
            int count=0;
            while ((s = stdInput.readLine()) != null) {
              //println(s, "--");
              String[] splitStr = p.split(s, " ");
              //println("--",s,"--",count);
              if (splitStr.length>0) {
                if (count>=2) {
                  for (int i=1; i<splitStr.length; i++) {
                    if (splitStr[i].trim().length()>0) {
                      // println(splitStr[i].trim(),"------");
                      try {
                        int pid=Integer.parseInt(splitStr[i]);
                        allPIDs.add(pid);
                      }
                      catch(Exception e) {
                        e.printStackTrace();
                      }
                      break;
                    }
                  }
                }
              }
              if (s.trim().length()>0) {
                count++;
              }
            }
          }
          catch(Exception e) {
            e.printStackTrace();
          }
          p.println(allPIDs);
          if (allPIDs.size()>0) {
            for (int i=0; i<allPIDs.size(); i++) {
              try {
                p.println("kill");
                Runtime.getRuntime().exec(new String[]{"cmd","/c","TASKKILL","/PID",p.str(allPIDs.get(i))});
              }
              catch(Exception e) {
                e.printStackTrace();
              }
            }
          }
        return tastIsKilled;
    }
}
