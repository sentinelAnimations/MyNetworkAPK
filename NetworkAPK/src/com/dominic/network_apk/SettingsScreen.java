package com.dominic.network_apk;

import java.io.File;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class SettingsScreen {
	private int btnSize, btnSizeSmall, stdTs, margin, edgeRad, textCol, textDark, dark, light, lighter, mode = 0, activePathSelectorInd = 0;
	private float textYShift;
	private String mySettingsPath;
	private String[] imgPaths;
	private PFont stdFont;
	private PImage screenshot;
	private PApplet p;
	private PictogramImage[] setting_pictos;
	public PathSelector[] pathSelectors;
	public ImageButton saveSettings_btn;
	public EditText personalData_et;
	public DropdownMenu masterOrSlave_dropdown;
	public FileExplorer fileExplorer;
	private ArrayList<MakeToast> makeToasts = new ArrayList<MakeToast>();


	public SettingsScreen(PApplet p, int btnSize, int btnSizeSmall, int stdTs, int margin, int edgeRad, int textCol, int textDark, int dark, int light, int lighter, int border,float textYShift,String mySettingsPath, String[] imgPaths, String[] HorizontalListPictoPaths, PFont stdFont) {
		this.p = p;
		this.btnSize = btnSize;
		this.btnSizeSmall = btnSizeSmall;
		this.stdTs = stdTs;
		this.margin = margin;
		this.edgeRad = edgeRad;
		this.textYShift=textYShift;
		this.textCol = textCol;
		this.textDark = textDark;
		this.dark = dark;
		this.light = light;
		this.lighter = lighter;
		this.mySettingsPath=mySettingsPath;
		this.imgPaths = imgPaths;
		this.stdFont = stdFont;

		setting_pictos = new PictogramImage[imgPaths.length - 2];
		pathSelectors = new PathSelector[imgPaths.length - 4];
		Boolean[] selectFolder = { false, true, true };
		String[] description = { "Setup this Pc as Slave or Master", "Select Blender.exe Folder", "Select image output Folder", "Select Path to Cloud", "Enter desired Name of PC", "Save Settings and move on | shortcut: ctrl+s" };
		String[] pathSelectorHints = { "...\\\\Blender.exe", "...\\\\images", "...\\\\Cloud" };

		for (int i = 0; i < setting_pictos.length; i++) {
			setting_pictos[i] = new PictogramImage(p, (p.width / 8 * 4) / 2 + p.width / 8 * (i), p.height / 2 - btnSize / 2, btnSize, margin, stdTs, edgeRad, textCol,textYShift, false, imgPaths[i], description[i], null);
			if (i > 0 && i < pathSelectors.length + 1) {
				pathSelectors[i - 1] = new PathSelector(p, 0, btnSize, p.width / 8 - margin * 2, btnSizeSmall, edgeRad, margin, stdTs, light, textCol, textDark,textYShift, selectFolder[i - 1], true, pathSelectorHints[i - 1], imgPaths[imgPaths.length - 1], stdFont, setting_pictos[i]);
			}
		}

		saveSettings_btn = new ImageButton(p, p.width - margin - btnSizeSmall / 2, p.height - margin - btnSizeSmall / 2, btnSizeSmall, btnSizeSmall, stdTs, margin, edgeRad, 19,textYShift, true, false, textCol, light, imgPaths[5], description[5], null);
		char[] fChars = { '>', '<', ':', '"', '/', '\\', '|', '?', '*' };
		personalData_et = new EditText(p, 0, btnSize, p.width / 8 - margin * 2, btnSizeSmall, stdTs, light, textCol, edgeRad, margin,textYShift, true, true, "Enter PC name", fChars, stdFont, setting_pictos[setting_pictos.length - 1]);
		
		String[] dropdownList= {"Master","Slave"};
		String[] ddPaths= {HorizontalListPictoPaths[HorizontalListPictoPaths.length-1],HorizontalListPictoPaths[HorizontalListPictoPaths.length-2]};
		masterOrSlave_dropdown=new DropdownMenu( p,0, btnSize,p.width / 8 - margin * 2, btnSizeSmall,p.height/4+btnSizeSmall+margin*2, edgeRad, margin, stdTs, light, lighter, textCol,textDark,textYShift,"Master or Slave",ddPaths, dropdownList, stdFont, true,setting_pictos[0]);
		
		fileExplorer = new FileExplorer(p, p.width / 2, p.height / 2, p.width - margin * 2, 6 * btnSizeSmall + 19 * margin, stdTs, edgeRad, margin, dark, light, lighter, textCol, textDark, border, btnSize, btnSizeSmall,textYShift, HorizontalListPictoPaths, stdFont);

	}

	public void render() {
		if (mode == 0) { // normal mode
			for (int i = setting_pictos.length - 1; i >= 0; i--) {
				setting_pictos[i].render();
			}

			personalData_et.render();
			saveSettings_btn.render();
			masterOrSlave_dropdown.render();
			for (int i = pathSelectors.length - 1; i >= 0; i--) {
				PathSelector ps = pathSelectors[i];
				ps.render();
				if (ps.openFileExplorer_btn.isClicked) {
					mode = 1;
					activePathSelectorInd = i;

				}
			}
			
			if(saveSettings_btn.isClicked==true) {
				//check if all is set
				Boolean allSet=true;
				allSet=masterOrSlave_dropdown.getIsSelected();
				
				for (int i = pathSelectors.length - 1; i >= 0; i--) {
					PathSelector ps = pathSelectors[i];
					if(ps.getPath().length()<1) {
						allSet=false;
						break;
					}
					
				}
				
				if(personalData_et.getStrList().get(0).length() < 1) {
					allSet=false;
				}
				//write to jsonfile;
				if(allSet==true) {
					makeToasts.add(new MakeToast(p, p.width / 2, p.height - stdTs * 2, stdTs, margin, edgeRad, light, textCol,textYShift, false, "Saved settings", stdFont, null));

				}else {
					makeToasts.add(new MakeToast(p, p.width / 2, p.height - stdTs * 2, stdTs, margin, edgeRad, light, textCol,textYShift, false, "Set all required data", stdFont, null));
				}
				saveSettings_btn.isClicked=false;
			}
			
			
			if (mode == 1) {
				fileExplorer.setIsClosed(false);
				fileExplorer.setIsCanceled(false);
				p.saveFrame("data\\imgs\\screenshots\\settingsScreen.png");
				screenshot = p.loadImage("data\\imgs\\screenshots\\settingsScreen.png");
				screenshot = new ImageBlurHelper(p).blur(screenshot, 3);
			}
		}

		if (mode == 1) { // fileExplorer mode
			renderFileExplorer();
		}

	}

	private void renderFileExplorer() {
		p.image(screenshot, p.width / 2, p.height / 2);

		PathSelector ps = pathSelectors[activePathSelectorInd];

		if (ps.openFileExplorer_btn.isClicked) {
			fileExplorer.render();

			if (fileExplorer.getIsClosed()) {
				if (fileExplorer.getIsCanceled()) {
				} else {
					String[] splitStr = p.split(fileExplorer.getPath(), "\\");
					String setPath = "";
					if (ps.getSelectFolder()) {
						for (int i = 0; i < splitStr.length; i++) {
							String[] splitStr2 = p.split(splitStr[i], ".");
							if (splitStr2.length > 1) {
								break;
							}
							setPath += splitStr[i] + "\\";
						}
					} else {
						File f = new File(fileExplorer.getPath());
						if (f.isDirectory()) {
							p.println("no file selected");
						} else {
							setPath = fileExplorer.getPath();
						}
					}
					if(setPath.length()>0) {
					ps.setText(setPath);
					}
				}
				ps.openFileExplorer_btn.isClicked = false;
				mode = 0;
			}
		}
	}

	public int getMode() {
		return mode;
	}
	
	public ArrayList getToastList() {
		return makeToasts;
	}
	public void removeToast(int i) {
		makeToasts.remove(i);
	}
}
