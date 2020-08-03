package com.dominic.network_apk;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import processing.core.PApplet;
import processing.core.PFont;
import processing.data.StringList;

public class FileExplorer {
	private int x, y, w, h, stdTs, edgeRad, margin, dark, light, lighter, textCol, textDark, border, btnSize, btnSizeSmall, startXBtns, bs, editBarX, editBarY, editBarW;
	private Boolean isParented, isClosed = false, isCanceled = false, finishedListing = false, isListing = false, isSearching = false, finishedSearching = false;
	private String selectedPath, searchDir,pathToCopy;
	private String[] searchedStr;
	private PFont stdFont;
	private PApplet p;
	private StringList allInPathsDeleteFolder = new StringList();
	private ArrayList<File> allDirsAndFiles = new ArrayList<File>();
	private SpriteAnimation searching_sprAnim;
	public HorizontalList[] horizontalLists = new HorizontalList[5];
	public ImageButton[] fileExplorer_btns = new ImageButton[9];
	public ImageButton rename_btn;
	public EditText rename_et;
	public SearchBar searchBar;

	public FileExplorer(PApplet p, int x, int y, int w, int h, int stdTs, int edgeRad, int margin, int dark, int light, int lighter, int textCol, int textDark, int border, int btnSize, int btnSizeSmall, String[] PictoPaths, PFont stdFont) {
		this.p = p;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.stdTs = stdTs;
		this.edgeRad = edgeRad;
		this.margin = margin;
		this.dark = dark;
		this.light = light;
		this.lighter = lighter;
		this.textCol = textCol;
		this.textDark = textDark;
		this.btnSize = btnSize;
		this.btnSizeSmall = btnSizeSmall;
		this.isParented = isParented;
		this.stdFont = stdFont;
		String[][] l = { {}, {}, {}, {}, {} };
		String[] titles = { "Volumes", "Current path", "Folders", "Files", "Search Results" };
		Boolean[] showSelected = { true, false, false, false, false };
		Boolean[] showMarked = { false, false, true, true, false };

		bs = btnSizeSmall + margin * 2;
		startXBtns = p.width - bs / 2 - margin * 3 - fileExplorer_btns.length * bs;
		editBarY = y + 4 * btnSizeSmall - btnSizeSmall / 2 + margin * 2 + margin / 2;
		editBarW = (startXBtns - bs / 2 - margin * 3);
		editBarX = margin * 2 + editBarW / 2;

		for (int i = 0; i < horizontalLists.length - 1; i++) {
			String[] hoLiPictoPaths = new String[3];
			hoLiPictoPaths[0] = PictoPaths[i];
			hoLiPictoPaths[1] = PictoPaths[4];
			hoLiPictoPaths[2] = PictoPaths[5];
			horizontalLists[i] = new HorizontalList(p, x, y - 3 * btnSizeSmall - btnSizeSmall / 2 - margin * 3 + margin / 2 + i * btnSizeSmall + i * (margin * 3), w - margin * 2, btnSizeSmall + margin * 2, margin, edgeRad, stdTs, (int) p.textWidth("Search Results") + margin * 3 + btnSizeSmall, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border, '\\', false, showSelected[i], showMarked[i], titles[i], hoLiPictoPaths, l[i], stdFont, null);
		}
		String[] hoLiPictoPaths = new String[3];
		hoLiPictoPaths[0] = PictoPaths[7];
		hoLiPictoPaths[1] = PictoPaths[4];
		hoLiPictoPaths[2] = PictoPaths[5];
		int lastHListInd = horizontalLists.length - 1;
		horizontalLists[lastHListInd] = new HorizontalList(p, x, y - 3 * btnSizeSmall - btnSizeSmall / 2 - margin * 3 + margin / 2 + lastHListInd * btnSizeSmall + lastHListInd * (margin * 3), w - margin * 2, btnSizeSmall + margin * 2, margin, edgeRad, stdTs, (int) p.textWidth("Search Results") + margin * 3 + btnSizeSmall, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border, '\\', false, showSelected[lastHListInd], showMarked[lastHListInd], titles[lastHListInd], hoLiPictoPaths, l[lastHListInd], stdFont, null);

		horizontalLists[0].setList(getVolumes());
		horizontalLists[0].isNewSelected = true;
		horizontalLists[2].isNewSelected = false;
		horizontalLists[3].isNewSelected = false;
		horizontalLists[4].isNewSelected = false;

		String[] infoTexts = { "Copy", "Cut", "Paste", "New folder", "Delete folder", "Delete file", "Help", "", "" };
		for (int i = 0; i < fileExplorer_btns.length; i++) {
			fileExplorer_btns[i] = new ImageButton(p, startXBtns + (i * bs + i * margin), editBarY, bs, bs, stdTs, margin, edgeRad, -1, true, false, textCol, light, PictoPaths[i + 8], infoTexts[i], null);
		}

		searchBar = new SearchBar(p, margin * 2 + editBarW / 4 * 3, editBarY, editBarW / 2 - margin * 2, btnSizeSmall, edgeRad, margin, stdTs, textCol, textDark, lighter, false, "Search", PictoPaths[7], stdFont, null);
		char[] fChars = { '>', '<', ':', '"', '/', '\\', '|', '?', '*' };
		rename_et = new EditText(p, margin * 2 + editBarW / 4 - btnSizeSmall + margin * 2, editBarY, editBarW / 2 - margin * 3 - btnSizeSmall, btnSizeSmall, stdTs, lighter, textCol, edgeRad, margin, true, false, "Rename Selected Folder", fChars, stdFont, null);
		rename_btn = new ImageButton(p, editBarX - btnSizeSmall / 2 - margin, editBarY, btnSizeSmall, btnSizeSmall, stdTs, margin, edgeRad, -1, true, false, textCol, lighter, PictoPaths[6], "Rename Selected Folder", null);

		searching_sprAnim = new SpriteAnimation(p, searchBar.search_btn.getX(), editBarY, btnSizeSmall - margin, btnSizeSmall - margin, 0, 129, textCol, false, "imgs/sprites/loadingGears/", null);
	}

	public void render() {
		p.fill(dark);
		p.stroke(dark);
		p.rect(x, y, w, h, edgeRad);
		p.fill(light);
		p.stroke(light);
		p.rect(editBarX, editBarY, editBarW, bs, edgeRad);
		for (int i = horizontalLists.length - 1; i >= 0; i--) {
			horizontalLists[i].render();
		}

		for (int i = fileExplorer_btns.length - 1; i >= 0; i--) {
			fileExplorer_btns[i].render();
		}
		searchBar.render();
		rename_et.render();
		rename_btn.render();

		handleButtons();
		handleLists();

		// p.println(p.frameCount);

	}

	private void handleLists() {
		if (horizontalLists[0].isNewSelected == true) {
			String[] l0 = horizontalLists[0].getList();
			String[] l1 = { l0[horizontalLists[0].getSelectedInd()] };
			horizontalLists[1].setList(l1);
			horizontalLists[2].setList(getFoldersAndFiles(l1[0], true));
			horizontalLists[3].setList(getFoldersAndFiles(l1[0], false));
			horizontalLists[0].isNewSelected = false;
		}

		if (horizontalLists[2].isNewSelected == true) {
			String[] l2 = horizontalLists[2].getList();
			String[] l1 = horizontalLists[1].getList();
			String[] l1New = new String[l1.length + 1];
			for (int i = 0; i < l1.length; i++) {
				l1New[i] = l1[i];
			}
			l1New[l1New.length - 1] = l2[horizontalLists[2].getSelectedInd()] + "\\\\";
			String path = "";
			for (int i = 0; i < l1New.length; i++) {
				path += l1New[i];
			}

			horizontalLists[1].setList(l1New);
			horizontalLists[2].setList(getFoldersAndFiles(path, true));
			horizontalLists[3].setList(getFoldersAndFiles(path, false));
			horizontalLists[2].isNewSelected = false;
		}

		if (horizontalLists[1].isNewSelected == true) {
			String path = "";
			String[] l1 = horizontalLists[1].getList();
			String[] l1New = new String[horizontalLists[1].getSelectedInd() + 1];
			if (l1.length > 0) {
				for (int i = 0; i < l1New.length; i++) {
					l1New[i] = l1[i];
					path += l1New[i];
				}
				PApplet.println(path);
				horizontalLists[2].setList(getFoldersAndFiles(path, true));
				horizontalLists[3].setList(getFoldersAndFiles(path, false));
				horizontalLists[1].setList(l1New);
				horizontalLists[1].isNewSelected = false;
			}
		}

		if (horizontalLists[4].isNewSelected == true) {
			String path = "";
			String[] l4 = horizontalLists[4].getList();
			String[] splitStr = PApplet.split(l4[horizontalLists[4].getSelectedInd()], "\\");
			String[] handOverStr = new String[splitStr.length];

			if (splitStr.length > 0) {
				String[] splitStr2 = PApplet.split(splitStr[splitStr.length - 1], ".");
				PApplet.println(splitStr2);
				if (splitStr2.length > 1) {
					PApplet.println("now");
					handOverStr = new String[splitStr.length - 1];
					handOverStr = Arrays.copyOf(splitStr, splitStr.length - 1);
				} else {
					handOverStr = splitStr;
				}

				for (int i = 0; i < handOverStr.length; i++) {
					path += handOverStr[i] + "\\";
					handOverStr[i] = handOverStr[i] + "\\";
				}

				horizontalLists[1].setList(handOverStr);
				horizontalLists[2].setList(getFoldersAndFiles(path, true));
				horizontalLists[3].setList(getFoldersAndFiles(path, false));
				horizontalLists[4].isNewSelected = false;
			}
		}

		String[] emptyList = {};
		for (int i = fileExplorer_btns.length - 1; i >= 0; i--) {
			if (fileExplorer_btns[i].isClicked == true) {
				horizontalLists[4].setList(emptyList);
			}
		}
		if (rename_btn.isClicked == true) {
			horizontalLists[4].setList(emptyList);
		}

	}

	public void handleButtons() {

		// rename button -------------------------------------------------

		if (rename_btn.isClicked == true) {
			if (horizontalLists[2].isNewMarked == true && horizontalLists[2].getList().length > 0 && rename_et.getStrList().get(0).length() > 0) {
				File originalFile, newFile;
				String originalFileName = "", newFileName;
				String[] l2 = horizontalLists[2].getList();
				if (l2.length > 0) {
					String[] l1 = horizontalLists[1].getList();
					for (int i = 0; i < horizontalLists[1].getList().length; i++) {
						originalFileName += l1[i];
					}
					originalFile = new File(originalFileName + l2[horizontalLists[2].getMarkedInd()]);
					newFileName = originalFileName + rename_et.getStrList().get(0);
					newFile = new File(newFileName);
					originalFile.renameTo(newFile);
					horizontalLists[2].setList(getFoldersAndFiles(originalFileName, true));
					horizontalLists[3].setList(getFoldersAndFiles(originalFileName, false));
				}
			}
			rename_btn.isClicked = false;
		}

		// rename button -------------------------------------------------

		// search button -------------------------------------------------

		if (searchBar.search_btn.isClicked == true) {
			if (searchBar.searchBar_et.getStrList().get(0).length() > 0) {
				for (int i = allDirsAndFiles.size() - 1; i >= 0; i--) {
					allDirsAndFiles.remove(i);
				}
				finishedListing = false;
				searchDir = "";
				String[] l1 = horizontalLists[1].getList();
				for (int i = 0; i < horizontalLists[1].getList().length; i++) {
					searchDir += l1[i];
				}

				Thread listFilesThread = new Thread(new Runnable() {
					@Override
					public void run() {
						isListing = true;
						allDirsAndFiles = listFilesRecursive(searchDir);
						finishedListing = true;
					}
				});
				listFilesThread.start();

			}
			searchBar.search_btn.isClicked = false;
		}

		if (finishedListing == true) {
			// p.println(allDirsAndFiles);
			String[] allDirsAndFilesStr = new String[allDirsAndFiles.size()];
			for (int i = 0; i < allDirsAndFiles.size(); i++) {
				allDirsAndFilesStr[i] = allDirsAndFiles.get(i).toString();
			}

			Thread searchInFilesThread = new Thread(new Runnable() {
				@Override
				public void run() {
					isSearching = true;
					searchedStr = searchForString(searchBar.searchBar_et.getStrList().get(0), allDirsAndFilesStr);
				}
			});
			searchInFilesThread.start();
			finishedListing = false;
		}
		if (isSearching == true) {
			if (finishedSearching == true) {
				if (searchedStr != null) {
					horizontalLists[4].setList(searchedStr);
					PApplet.println("set list");
				}
				isSearching = false;
				isListing = false;
			}
		}

		if (isListing == true || isSearching == true) {
			// p.println("searching");
			p.stroke(lighter);
			p.fill(lighter);
			p.rect(searchBar.search_btn.getX(), searchBar.search_btn.getY(), searchBar.search_btn.getW(), searchBar.search_btn.getH(), edgeRad);
			searching_sprAnim.render();
		}
		// search button -------------------------------------------------

		// bassline buttons------------------------------------------------
		for (int i = 0; i < fileExplorer_btns.length; i++) {
			if (fileExplorer_btns[i].isClicked == true) {
				switch (i) {
				case 0: // copy folder
					String[] list1=horizontalLists[2].getList();
					for(int i2=0;i2<list1.length;i2++) {
						pathToCopy+=list1[i2];
					}
					break;
				case 1: // cut folder
					break;

				case 2: // past folder
					String destination="";
					String[] l1=horizontalLists[2].getList();
					for(int i2=0;i2<l1.length;i2++) {
						destination+=l1[i2];
					}
					if(pathToCopy.length()>0 && destination.length()>0) {
					copyFolder(pathToCopy,destination);
					 pathToCopy="";
					}
					break;

				case 3: // new folder
					String c3curPath="";
					String[] c3List=horizontalLists[2].getList();
					for(int i2=0;i2<c3List.length;i2++) {
						c3curPath+=c3List[i2];
					}
					File f = new File(c3curPath);
					f.mkdir();
					break;

				case 4: // delete folder
					break;

				case 5: // delete file
					break;

				case 6: // questions
					break;

				case 7: // cancel selection
					break;

				case 8: // select
					break;

				}
				fileExplorer_btns[i].isClicked = false;
			}
		}
		// bassline buttons------------------------------------------------

	}

	void copyFolder(String copyFolderPath, String destination) {
		  if (copyFolderPath.equals(destination)==false) { 
		    File f=new File(destination);
		    String[] basePath=PApplet.split(copyFolderPath, "\\");
		    f.mkdir();
		    ArrayList<File> allFiles=listFilesRecursive(copyFolderPath);    
		    for (int i=0; i<allFiles.size(); i++) {
		      if (allFiles.get(i).isDirectory()) {
		        String[] relativePath=PApplet.split(allFiles.get(i).toString(), "\\");
		        String path="";
		        for (int i2=basePath.length; i2<relativePath.length; i2++) {
		          path+=relativePath[i2]+"\\";
		        }
		        f=new File(destination+"\\"+path);
		        f.mkdir();
		      }else{
		        String[] splitStr=PApplet.split(allFiles.get(i).toString(), "\\");

		        String[] relativePath=PApplet.split(allFiles.get(i).toString(), "\\");
		        String path="";
		        for (int i2=basePath.length; i2<relativePath.length; i2++) {
		          path+=relativePath[i2]+"\\";
		        }

		        Path oldFile = Paths.get(allFiles.get(i).toString());
		        Path newFile = Paths.get(destination+"\\"+path);
		        try {
		          Files.copy(oldFile, newFile);
		        } 
		        catch (IOException e) {
		          PApplet.println(e);
		        }
		      }
		        
		    }
		  }else{
		   PApplet.println("cant copy to same path!"); 
		  }
		}


	public void deleteFolder(String path) {
		allInPathsDeleteFolder.clear();
		allInPathsDeleteFolder.append(path);
		recursGetPaths(path);

		if (allInPathsDeleteFolder.size() > 1) {
			PApplet.println("data in folder");
		}

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
					recursGetPaths(p + "/" + s);
				}
			}
		} else {
			PApplet.println("Cant delete path");
		}
	}

	public String[] searchForString(String searchStr, String[] searchArray) {
		ArrayList<String> resultsFiles = new ArrayList<String>();
		ArrayList<String> resultsFolders = new ArrayList<String>();
		int count = 0;

		for (int i = 0; i < searchArray.length; i++) {
			String[] splitStr = PApplet.split(searchArray[i], "\\");
			String[] splitStr2 = PApplet.split(splitStr[splitStr.length - 1], ".");
			// p.println(searchArray[i]);
			if (splitStr2.length > 1) {
				String[] m1 = PApplet.match(splitStr[splitStr.length - 1].toUpperCase(), searchStr.toUpperCase());
				if (m1 != null) {
					resultsFiles.add(searchArray[i]);
				}
			} else {
				for (int i2 = splitStr.length - 1; i2 >= 0; i2--) {
					String[] m1 = PApplet.match(splitStr[i2].toUpperCase(), searchStr.toUpperCase());
					PApplet.println(splitStr[i2].toUpperCase(), searchStr.toUpperCase());
					if (m1 != null) {
						Boolean noOtherMatch = true;
						for (int i3 = 0; i3 < resultsFolders.size(); i3++) {
							String[] splitStr3 = PApplet.split(resultsFolders.get(i3), "\\");
							for (int i4 = splitStr3.length - 1; i4 >= 0; i4--) {
								String[] m2 = PApplet.match(splitStr3[i4], searchStr);

								if (m2 != null) {
									if (i2 == i4) {
										noOtherMatch = false;
										break;
									}
								}
							}
						}
						// noOtherMatch = true;
						if (noOtherMatch == true) {
							resultsFolders.add(searchArray[i]);
							PApplet.println("match found ---------------------");
							count++;
							break;
						}
					}
				}
			}
		}

		String[] resStr = new String[resultsFolders.size() + resultsFiles.size()];
		for (int i = 0; i < resStr.length; i++) {
			if (i < resultsFolders.size()) {
				resStr[i] = resultsFolders.get(i);
			} else {
				resStr[i] = resultsFiles.get(i - resultsFolders.size());
			}
		}
		finishedSearching = true;
		return resStr;
	}

	// Function to get a list of all files in a directory and all subdirectories
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

	String[] listFileNames(String dir) {
		File file = new File(dir);
		if (file.isDirectory()) {
			String names[] = file.list();
			return names;
		} else {
			// If it's not a directory
			return null;
		}
	}

	public Boolean getIsCanceled() {
		return isCanceled;
	}

	public Boolean getIsClosed() {
		return isClosed;
	}

	public String getPath() {
		return selectedPath;
	}
}
