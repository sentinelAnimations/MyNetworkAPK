package com.dominic.network_apk;

import java.io.File;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class SpreadBlender {
	private int mode, btnSize, btnSizeSmall, margin, stdTs, edgeRad, dark, darkest, light, lighter, lightest, border, textCol, textDark;
	private float textYShift;
	private Boolean fileExplorerIsOpen = false;
	private String[] pictoPaths;
	private PFont stdFont;
	private PImage screenshot;
	private PApplet p;
	private MainActivity mainActivity;
	private ImageButton[] mainButtons;
	private ImageButton spreadBlender_ImageButton, spreadSheepit_ImageButton;
	private PathSelector spreadBlender_pathSelector, spreadSheepit_PathSelector;
	private FileInteractionHelper fileInteractionHelper;

	public SpreadBlender(PApplet p, int mode, int btnSize, int btnSizeSmall, int margin, int stdTs, int edgeRad, int dark, int darkest, int light, int lighter, int lightest, int border, int textCol, int textDark, float textYShift, String[] pictoPaths, String[] fileExplStr, PFont stdFont) {
		this.mode = mode;
		this.btnSize = btnSize;
		this.btnSizeSmall = btnSizeSmall;
		this.margin = margin;
		this.stdTs = stdTs;
		this.edgeRad = edgeRad;
		this.dark = dark;
		this.darkest = darkest;
		this.light = light;
		this.lighter = lighter;
		this.lightest = lightest;
		this.border = border;
		this.textCol = textCol;
		this.textDark = textDark;
		this.textYShift = textYShift;
		this.pictoPaths = pictoPaths;
		this.stdFont = stdFont;
		this.p = p;

		mainActivity = (MainActivity) p;
		if (mainActivity.getIsMaster()) {
			mainButtons = mainActivity.getMainButtonsMaster();
		} else {
			mainButtons = mainActivity.getMainButtonsSlave();
		}
		int psW = p.width / 3;
		spreadBlender_pathSelector = new PathSelector(p, p.width / 2 - btnSizeSmall / 2, p.height / 2 - btnSizeSmall / 2 - margin, psW, btnSizeSmall, edgeRad, margin, stdTs, btnSizeSmall, border, light, textCol, dark, light, lighter, textDark, textYShift, true, false, "...\\\\Folder to copy Blender from", pictoPaths[0], fileExplStr, stdFont, null);
		spreadBlender_ImageButton = new ImageButton(p, psW / 2 + margin + btnSizeSmall / 2, 0, btnSizeSmall, btnSizeSmall, stdTs, margin, edgeRad, -1, textYShift, true, true, textCol, lighter, pictoPaths[1], "Share Blender", spreadBlender_pathSelector);

		spreadSheepit_PathSelector = new PathSelector(p, p.width / 2 - btnSizeSmall / 2, p.height / 2 + btnSizeSmall / 2 + margin, psW, btnSizeSmall, edgeRad, margin, stdTs, btnSizeSmall, border, light, textCol, dark, light, lighter, textDark, textYShift, false, false, "...\\\\Folder to copy Sheepit from", pictoPaths[0], fileExplStr, stdFont, null);
		spreadSheepit_ImageButton = new ImageButton(p, psW / 2 + margin + btnSizeSmall / 2, 0, btnSizeSmall, btnSizeSmall, stdTs, margin, edgeRad, -1, textYShift, true, true, textCol, lighter, pictoPaths[2], "Share Sheepit", spreadSheepit_PathSelector);

		spreadBlender_pathSelector.setPath(mainActivity.getPathToBlender(), false);
		fileInteractionHelper = new FileInteractionHelper(p);
	}

	public void render() {
		fileExplorerIsOpen = false;
		if (spreadBlender_pathSelector.getFileExplorerIsOpen() || spreadSheepit_PathSelector.getFileExplorerIsOpen()) {
			fileExplorerIsOpen = true;
		}

		if (fileExplorerIsOpen == false) {
			if (mainActivity.getIsMaster()) {
				mainActivity.renderMainButtonsMaster();
			} else {
				mainActivity.renderMainButtonsSlave();
			}
			spreadBlender_ImageButton.render();
			spreadSheepit_ImageButton.render();

			// share blender folder with slaves ---------------------------------
			String date = p.str(p.year()) + p.nf(p.month(), 2) + p.nf(p.day(), 2) + p.nf(p.hour(), 2) + p.nf(p.minute(), 2);
			if (spreadBlender_ImageButton.getIsClicked()) {
				if (spreadBlender_pathSelector.getPath().length() > 0) {
					try {
						String[] curCloudBlenderFolders = fileInteractionHelper.getFoldersAndFiles(mainActivity.getProgrammFolderPath(), true);
						if (curCloudBlenderFolders != null && curCloudBlenderFolders.length > 0) {
							for (int i = 0; i < curCloudBlenderFolders.length; i++) {
								fileInteractionHelper.batchDeleteFolder(mainActivity.getProgrammFolderPath() + "\\" + curCloudBlenderFolders[i]);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					String copyToPath = mainActivity.getProgrammFolderPath() + "\\";

					String copyFromPath = spreadBlender_pathSelector.getPath();
					fileInteractionHelper.copyFolder(copyFromPath, copyToPath);
					new File(copyToPath + "\\" + fileInteractionHelper.getFoldersAndFiles(copyToPath, true)[0]).renameTo(new File(new File(copyToPath).getAbsolutePath() + "\\newBlenderVersion" + date));
					
					
				}
				spreadBlender_ImageButton.setIsClicked(false);
			}
			// share blender folder with slaves ---------------------------------

			// share sheepit with slaves ---------------------------------
			if (spreadSheepit_ImageButton.getIsClicked()) {
				if (spreadSheepit_PathSelector.getPath().length() > 0) {
					try {
						String[] curCloudSheepitFile = fileInteractionHelper.getFoldersAndFiles(mainActivity.getProgrammFolderPath(), false);
						if (curCloudSheepitFile != null && curCloudSheepitFile.length > 0) {
							for (int i = 0; i < curCloudSheepitFile.length; i++) {
								new File(mainActivity.getProgrammFolderPath() + "\\" + curCloudSheepitFile[i]).delete();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					String fileName="sheepit" + date + ".exe";
					String copyFromPath = spreadSheepit_PathSelector.getPath();
					String copyToPath = mainActivity.getProgrammFolderPath() +"\\"+fileName;
					File copyToFile = new File(copyToPath);
					if (copyToFile.exists()) {
						copyToFile.delete();
					}
					fileInteractionHelper.copyFile(copyFromPath, copyToPath);
					
					//copy sheepit to localProgramFolder too ------------------------------
					try {
						File localSheepitVersion = new File(mainActivity.getLocalProgrammPath() + "\\" + fileInteractionHelper.getFoldersAndFiles(mainActivity.getLocalProgrammPath(), false)[0]);
						if(localSheepitVersion.exists()) {
							localSheepitVersion.delete();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					p.println(copyToPath,mainActivity.getLocalProgrammPath()+"\\"+fileName);
					fileInteractionHelper.copyFile(copyToPath, mainActivity.getLocalProgrammPath()+"\\"+fileName);
					//copy sheepit to localProgramFolder too ------------------------------
					
				}
				spreadSheepit_ImageButton.setIsClicked(false);
			}
			// share sheepit with slaves ---------------------------------

		}
		if (spreadSheepit_PathSelector.getFileExplorerIsOpen() == false) {
			spreadBlender_pathSelector.render();
		}
		if (spreadBlender_pathSelector.getFileExplorerIsOpen() == false) {
			spreadSheepit_PathSelector.render();
		}
		if(!spreadBlender_pathSelector.getFileExplorerIsOpen() && !spreadSheepit_PathSelector.getFileExplorerIsOpen()) {
			spreadBlender_pathSelector.getHoverText().render();
		}
	}

	public void onMousePressed(int mouseButton) {
		spreadBlender_pathSelector.onMousePressed(mouseButton);
		spreadSheepit_PathSelector.onMousePressed(mouseButton);
		if (fileExplorerIsOpen) {
		} else {
			spreadBlender_ImageButton.onMousePressed();
			spreadSheepit_ImageButton.onMousePressed();
			for (int i = 0; i < mainButtons.length; i++) {
				if (mainButtons[0].getClickCount() % 2 == 0 || i == 0) {
					mainButtons[i].onMousePressed();
				}
			}
		}
	}

	public void onMouseReleased(int mouseButton) {
		spreadBlender_pathSelector.onMouseReleased(mouseButton);
		spreadSheepit_PathSelector.onMouseReleased(mouseButton);
		if (fileExplorerIsOpen) {
		} else {
			spreadBlender_ImageButton.onMouseReleased();
			spreadSheepit_ImageButton.onMouseReleased();
			for (int i = 0; i < mainButtons.length; i++) {
				if (mainButtons[0].getClickCount() % 2 == 0 || i == 0) {
					mainButtons[i].onMouseReleased();
				}
			}
		}
	}

	public void onKeyPressed(char key) {
		spreadBlender_pathSelector.onKeyPressed(key);
		spreadSheepit_PathSelector.onKeyPressed(key);
	}

	public void onKeyReleased(char key) {
		spreadBlender_pathSelector.onKeyReleased(key);
		spreadSheepit_PathSelector.onKeyReleased(key);
		if (fileExplorerIsOpen) {
		} else {

		}
	}

	public void onScroll(float e) {
		spreadBlender_pathSelector.onScroll(e);
		spreadSheepit_PathSelector.onScroll(e);
		if (fileExplorerIsOpen) {
		} else {

		}
	}

	public int getMode() {
		return mode;
	}
}
