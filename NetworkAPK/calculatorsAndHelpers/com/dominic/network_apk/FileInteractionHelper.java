package com.dominic.network_apk;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.data.StringList;

public class FileInteractionHelper {
	private StringList allInPathsDeleteFolder=new StringList();
	private PApplet p;
	public FileInteractionHelper(PApplet p) {
		this.p=p;
	}
	

	public void copyFolder(String copyFolderPath, String destination) {
		if (copyFolderPath.equals(destination) == false) {
			File f;
			String[] splitStr1 = p.split(copyFolderPath, "\\");
			String[] basePath0 = p.split(copyFolderPath, "\\");
			
			ArrayList<String> basePath=new ArrayList<>();
			for(int i=0;i<basePath0.length;i++) {
				if(basePath0[i].length()>0) {
					basePath.add(basePath0[i]);
				}
			}

			ArrayList<File> allFiles = listFilesRecursive(copyFolderPath);
			for (int i = 0; i < allFiles.size(); i++) {
				if (allFiles.get(i).isDirectory()) {
					String[] relativePath = p.split(allFiles.get(i).toString(), "\\");
					String path = splitStr1[splitStr1.length - 1] + "\\";
					for (int i2 = basePath.size(); i2 < relativePath.length; i2++) {
						path += relativePath[i2] + "\\";
					}
					f = new File(destination + "\\" + path);
					f.mkdir();
				} else {
					String[] splitStr = p.split(allFiles.get(i).toString(), "\\");

					String[] relativePath = p.split(allFiles.get(i).toString(), "\\");
					String path = "";
					for (int i2 = basePath.size(); i2 < relativePath.length; i2++) {
						path += relativePath[i2] + "\\";
					}

					Path oldFile = Paths.get(allFiles.get(i).toString());
					Path newFile = Paths.get(destination + splitStr1[splitStr1.length - 1] + "\\" + path);
					try {
						Files.copy(oldFile, newFile);
					} catch (IOException e) {
						p.println(e);
					}
				}
			}
		} else {
			p.println("cant copy to same path!");
		}
	}

	public void deleteFolder(String path) {
		allInPathsDeleteFolder.clear();
		allInPathsDeleteFolder.append(path);
		recursGetPaths(path);

		if (allInPathsDeleteFolder.size() > 1) {
			PApplet.println("data in folder");
		}
		p.println(allInPathsDeleteFolder);
		for (int i = allInPathsDeleteFolder.size() - 1; i >= 0; i--) {
			File curFile = new File(allInPathsDeleteFolder.get(i));
			curFile.delete();
		}
	}

	public void recursGetPaths(String pa) {
		File index = new File(pa);
		String[] entries = index.list();
		if (index.list() != null) {
			for (String s : entries) {
				allInPathsDeleteFolder.append(pa + "/" + s);
				String[] splitString = PApplet.split(s, ".");
				if (splitString.length == 1) {
					recursGetPaths(pa + "/" + s);
				}
			}
		} else {
			p.println("Cant delete path");
		}
	}
	ArrayList<File> listFilesRecursive(String dir) {
		ArrayList<File> fileList = new ArrayList<File>();
		recurseDir(fileList, dir);
		return fileList;
	}
	// Recursive function to traverse subdirectories
	void recurseDir(ArrayList<File> a, String dir) {
		File file = new File(dir);
		if (file.isDirectory()) {
			// If you want to include directories in the list
			a.add(file);
			File[] subfiles = file.listFiles();
			if (subfiles != null) {
				for (int i = 0; i < subfiles.length; i++) {
					// Call this function on all files in this directory
					recurseDir(a, subfiles[i].getAbsolutePath());
				}
			}
		} else {
			a.add(file);
		}
	}

	public String[] getVolumes() {
		File[] roots = File.listRoots();
		String[] result = new String[roots.length];
		for (int i = 0; i < roots.length; i++) {
			result[i] = roots[i].getAbsolutePath();
		}
		return result;
	}

	public String[] getFoldersAndFiles(String path, Boolean getFolders) {
		String[] result;
		StringList folders = new StringList();
		File file = new File(path);

		if (file.isDirectory()) {
			File[] files = file.listFiles();
			String names[] = file.list();
			// return names;
			try {
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory() == getFolders) {
						if (Files.isReadable(files[i].getAbsoluteFile().toPath())) {
							folders.append(names[i]);
						} else {
						}
					}
				}
				result = new String[folders.size()];
				for (int i = 0; i < result.length; i++) {
					result[i] = folders.get(i);
				}

				return result;
			} catch (Exception e) {
				return null;
			}
		} else {
			return null;
		}
	}
	
	public String cleanupPath(String pathToClean) {
	    String cleanPath="";
	    for(int i=0;i<pathToClean.length();i++) {
	        if(p.str(cleanPath.charAt(cleanPath.length()-1))=="\\" && p.str(pathToClean.charAt(i))=="\\") {
	        }else {
	            cleanPath+=pathToClean.charAt(i);
            }
	        
	    }
	    return cleanPath;
	}
}
