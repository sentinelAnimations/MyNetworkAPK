package com.dominic.network_apk;

import java.io.File;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.event.MouseEvent;

public class MainActivity extends PApplet {

	public static void main(String[] args) {
		PApplet.main("com.dominic.network_apk.MainActivity");
	}

	// Global variables -----------------------------------------
	static int mode = 0; // 0=loadingScreen, 1=home,2=node editor,3=settings,4=download
							// blender,5=questions,101=renderMode

	// integers-------------------------------------------------
	// integers-------------------------------------------------

	// Booleans-------------------------------------------------
	Boolean fileExplorerIsOpen = false;
	// Booleans-------------------------------------------------

	// Colors--------------------------------------------------
	private int darkest = color(30),dark = color(26, 32, 37), light = color(39, 48, 56), lighter = color(54, 67, 78),lightest=color(64,77,88), border = color(255, 191, 0), darkTransparent = color(26, 32, 37, 100), red = color(255, 0, 0), green = color(0, 255, 0), textCol = color(255), textDark = color(150);
	// colors -------------------------------------------------

	// Dimens--------------------------------------------------
	private int stdTs = 12, titleTs = 22, subtitleTs = 16, btnSize = 50, btnSizeLarge = btnSize * 2, btnSizeSmall = btnSize / 2, edgeRad = btnSize / 10, padding = 5, margin = padding;
	private float textYShift = 0.1f;
	// Dimens--------------------------------------------------

	// Strings--------------------------------------------------
	private String APKName = "InSevenDays©", APKDescription = "A network solution", mySettingsPath = "output/sub/settings.json";
	private String[] modeNames = { "Home", "Node Editor", "Settings", "Spread Blender","Theme", "Questions" };
	// Strings--------------------------------------------------

	// Fonts---------------------------------------------------
	PFont stdFont;
	// Fonts---------------------------------------------------

	// images--------------------------------------------------
	PImage screenshot;

	private String absPathPictos = "imgs/pictograms/";
	private String absPathStartImgs = "imgs/startImgs/";
	private String[] startImgPaths = { "muffins.png" };
	// images--------------------------------------------------

	// Classes--------------------------------------------------
	// Main classes-------------------------------
	private LoadingScreen loadingScreen;
	private NodeEditor nodeEditor;
	private SettingsScreen settingsScreen;
	private SpreadBlender spreadBlenderScreen;
	private ThemeScreen themeScreen;
	private QuestionScreen questionScreen;
	// Main classes-------------------------------
	// widgets -----------------------------------
	private PictogramImage firstSetupPicto;
	private ImageButton firstSetupHelp_btn;
	private PathSelector fileToRender_pathSelector;
	private FileExplorer selectBlendFile_fileExplorer;
	private CounterArea startFrame_counterArea, endFrame_counterArea, stillFrame_counterArea;
	private ImageButton startRendering_btn;
	private ImageButton[] mainButtons = new ImageButton[7];
	private Checkbox[] homeSettings_checkboxes = new Checkbox[8];
	private ArrayList<MakeToast> makeToasts = new ArrayList<MakeToast>();

	// widgets -----------------------------------
	// Classes--------------------------------------------------
	// Global variables
	// -----------------------------------------------------------------------

	@Override
	public void settings() {

	}

	@Override
	public void setup() {
		getSurface().setSize(1050, 450);

		rectMode(CENTER);
		imageMode(CENTER);
		// variableInitialisation -----------------------------------------------
		stdFont = createFont("fonts/stdFont.ttf", titleTs);

		String[] p3 = { absPathPictos + "collapse.png", absPathPictos + "home.png", absPathPictos + "nodeEditor.png", absPathPictos + "settings.png", absPathPictos + "downloadBlender.png", absPathPictos + "themeSettings.png",absPathPictos+"questions.png" };
		for (int i = 0; i < mainButtons.length; i++) {
			String s = "";
			if (i > 0) {
				s = modeNames[i - 1];
			}
			mainButtons[i] = new ImageButton(this, btnSize / 2 + margin + btnSize * i + margin * i, btnSize / 2 + margin, btnSize, btnSize, stdTs, margin, edgeRad, -1, textYShift, true, false, textCol, light, p3[i], s, null);
		}

		// variableInitialisation for mode 0 --> loading screen----------------
		loadingScreen = new LoadingScreen(this, btnSize, margin, stdTs, titleTs, subtitleTs, dark, textCol, textDark, textYShift, APKName, APKDescription, "imgs/startImgs/muffins.png", mySettingsPath, stdFont);
		// variableInitialisation for mode 0 --> loading screen----------------

		// variableInitialisation for mode 1 --> home screen-------------------
		String[] checkBoxTexts = { "Render with full force", "Render only with slaves", "", "Render on Sheepit", "Use CPU", "Use GPU", "", "" };
		for (int i = 0; i < homeSettings_checkboxes.length; i++) {
			int ys = 0;
			int is = 0;
			if (i > 3) {
				ys = height / 8;
				is = 4;
			}
			homeSettings_checkboxes[i] = new Checkbox(this, (int) (width / 9 * 1.5f + (width / 9 * 2) * (i - is)), height / 5 * 2 + ys, width / 9, btnSizeSmall, btnSizeSmall, edgeRad, margin, stdTs, light, light, border, textCol, textYShift, false, false, checkBoxTexts[i], absPathPictos + "checkmark.png", stdFont, null);
		}

		fileToRender_pathSelector = new PathSelector(this, btnSizeSmall + margin * 3, 0, width / 8 - margin, btnSizeSmall, edgeRad, margin, stdTs, light, textCol, textDark, textYShift, false, true, "...\\\\File.blend", absPathPictos + "selectFolder.png", stdFont, homeSettings_checkboxes[2]);
		String[] p0 = { absPathPictos + "volume.png", absPathPictos + "folderStructure.png", absPathPictos + "folder.png", absPathPictos + "file.png", absPathPictos + "arrowLeft.png", absPathPictos + "arrowRight.png", absPathPictos + "rename.png", absPathPictos + "search.png", absPathPictos + "copy.png", absPathPictos + "cutFolder.png", absPathPictos + "pasteFolder.png", absPathPictos + "addFolder.png", absPathPictos + "deleteFolder.png", absPathPictos + "deleteFile.png", absPathPictos + "questions.png", absPathPictos + "cross.png", absPathPictos + "checkmark.png", absPathPictos + "arrowUp.png", absPathPictos + "arrowDown.png" };
		selectBlendFile_fileExplorer = new FileExplorer(this, width / 2, height / 2, width - margin * 2, 6 * btnSizeSmall + 19 * margin, stdTs, edgeRad, margin, dark, light, lighter, textCol, textDark, border, btnSize, btnSizeSmall, textYShift, p0, stdFont);
		String[] pp = { absPathPictos + "arrowLeft.png", absPathPictos + "arrowRight.png" };
		int sfW = (int) (width / 16 - margin);
		int sfX = (int) ((homeSettings_checkboxes[6].getX() - homeSettings_checkboxes[6].getW() / 2 + homeSettings_checkboxes[6].getBoxDim() + margin * 2)) - homeSettings_checkboxes[6].getX() + sfW / 2;
		startFrame_counterArea = new CounterArea(this, sfX, 0, sfW, btnSizeSmall, edgeRad, margin, stdTs, 0, 1000000000,0, light, lighter, textCol, textYShift, true, "Startframe", pp, stdFont, homeSettings_checkboxes[6]);
		endFrame_counterArea = new CounterArea(this, sfX + sfW + margin, 0, sfW, btnSizeSmall, edgeRad, margin, stdTs, 0, 1000000000,0, light, lighter, textCol, textYShift, true, "Endframe", pp, stdFont, homeSettings_checkboxes[6]);
		sfW = (int) textWidth(checkBoxTexts[3]);
		sfX = (int) ((homeSettings_checkboxes[7].getX() - homeSettings_checkboxes[7].getW() / 2 + homeSettings_checkboxes[7].getBoxDim() + margin * 2)) - homeSettings_checkboxes[7].getX() + sfW / 2;
		stillFrame_counterArea = new CounterArea(this, sfX, 0, sfW, btnSizeSmall, edgeRad, margin, stdTs, 0, 1000000000,0, light, lighter, textCol, textYShift, true, "Still frame", pp, stdFont, homeSettings_checkboxes[7]);
		startRendering_btn = new ImageButton(this, width / 2, height - height / 7 * 2, btnSize, btnSize, stdTs, margin, edgeRad, -1, textYShift, true, false, textCol, light, absPathPictos + "startEngine.png", "Start rendering", null);
		// variableInitialisation for mode 1 --> home screen-------------------

		// variableInitialisation for mode 2 --> node editor-------------------
		String[] btnP = { absPathPictos + "panView.png", absPathPictos + "addNode.png", absPathPictos + "center.png", absPathPictos + "save.png" };
		String[] nodeP1 = { absPathPictos + "masterPC.png", absPathPictos + "pc.png", absPathPictos + "laptop.png", absPathPictos + "switch.png", absPathPictos + "engine.png" };
		String[] nodeP2 = { absPathPictos + "masterPC.png", absPathPictos + "pc.png", absPathPictos + "laptop.png", absPathPictos + "switch.png", absPathPictos + "engine.png", absPathPictos + "cpu.png", absPathPictos + "gpu.png", absPathPictos + "arrowLeft.png", absPathPictos + "arrowRight.png", absPathPictos + "arrowUp.png", absPathPictos + "arrowDown.png" ,absPathPictos+"checkmark.png"};

		nodeEditor = new NodeEditor(this, btnSize, btnSizeSmall, margin, stdTs, edgeRad, dark,darkest, light, lighter,lightest, border, textCol, textDark, textYShift, btnP, nodeP1, nodeP2, stdFont);
		// variableInitialisation for mode 2 --> node editor-------------------

		// variableInitialisation for mode 3 --> settings screen---------------
		String[] p1 = { absPathPictos + "masterOrSlave.png", absPathPictos + "blenderExeFolder.png", absPathPictos + "imageFolder.png", absPathPictos + "pathToCloud.png", absPathPictos + "personalData.png", absPathPictos + "checkmark.png", absPathPictos + "selectFolder.png" };
		String[] p2 = { absPathPictos + "volume.png", absPathPictos + "folderStructure.png", absPathPictos + "folder.png", absPathPictos + "file.png", absPathPictos + "arrowLeft.png", absPathPictos + "arrowRight.png", absPathPictos + "rename.png", absPathPictos + "search.png", absPathPictos + "copy.png", absPathPictos + "cutFolder.png", absPathPictos + "pasteFolder.png", absPathPictos + "addFolder.png", absPathPictos + "deleteFolder.png", absPathPictos + "deleteFile.png", absPathPictos + "questions.png", absPathPictos + "cross.png", absPathPictos + "checkmark.png", absPathPictos + "arrowUp.png", absPathPictos + "arrowDown.png" };
		settingsScreen = new SettingsScreen(this, btnSize, btnSizeSmall, stdTs, margin, edgeRad, textCol, textDark, dark, light, lighter, border, textYShift, mySettingsPath, p1, p2, stdFont);

		firstSetupPicto = new PictogramImage(this, margin + btnSize / 2, margin + btnSize / 2, btnSize, margin, stdTs, edgeRad, textCol, textYShift, false, absPathPictos + "settings.png", "First setup page", null);
		firstSetupHelp_btn = new ImageButton(this, width - btnSize / 2 - margin, btnSize / 2 + margin, btnSize, btnSize, stdTs, margin, edgeRad, 8, textYShift, false, false, textCol, textCol, absPathPictos + "questions.png", "questions and infos | sortcut: ctrl+h", null);

		// variableInitialisation for mode 3 --> settings screen---------------

		// variableInitialisation for mode 4 --> blender download--------------
		String[] pp1= {absPathPictos + "selectFolder.png",absPathPictos + "spreadBlender.png"};
		spreadBlenderScreen=new SpreadBlender(this, btnSize, btnSizeSmall, margin, stdTs, edgeRad, dark, darkest, light, lighter, lightest, border, textCol, textDark, textYShift, pp1, stdFont);
		// variableInitialisation for mode 4 --> blender download--------------

		// variableInitialisation for mode 5 --> Theme screen-------------------
		String[] pp2= {absPathPictos+"colorPicker.png"};
        themeScreen=new ThemeScreen(this, btnSize, btnSizeSmall, margin, stdTs, edgeRad, dark, darkest, light, lighter, lightest, border, textCol, textDark, textYShift, pp2, stdFont);      
		// variableInitialisation for mode 5 --> Theme screen-------------------
		
		// variableInitialisation for mode 6 --> help screen-------------------
		String[] pp3= {absPathPictos + "search.png"};
        questionScreen=new QuestionScreen(this, btnSize, btnSizeSmall, margin, stdTs, edgeRad, dark, darkest, light, lighter, lightest, border, textCol, textDark, textYShift, pp3, stdFont);
		// variableInitialisation for mode 6 --> help screen-------------------


		// variableInitialisation -----------------------------------------------

	}

	@Override
	public void draw() {
		background(dark);
		if (mode == 0) { // loadingScreen ----------------
			loadingScreen.render();
		}

		if (mode == 1) {

			// render all ---------------------------------------------------------------
			renderMainButtons();

			for (int i = 0; i < homeSettings_checkboxes.length; i++) {
				homeSettings_checkboxes[i].render();
			}
			fileToRender_pathSelector.render();
			endFrame_counterArea.render();
			startFrame_counterArea.render();
			stillFrame_counterArea.render();
			startRendering_btn.render();
			stroke(light);
			line(homeSettings_checkboxes[0].getBoxX() - homeSettings_checkboxes[0].getBoxDim() / 2, startRendering_btn.getY(), startRendering_btn.getX() - startRendering_btn.getW() / 2 - margin * 2, startRendering_btn.getY());
			line(startRendering_btn.getX() + startRendering_btn.getW() / 2 + margin * 2, startRendering_btn.getY(), stillFrame_counterArea.getX() + stillFrame_counterArea.getW() / 2, startRendering_btn.getY());

			// render toasts -----------------------------------
			for (int i = 0; i < makeToasts.size(); i++) {
				MakeToast m = makeToasts.get(i);
				if (m.remove) {
					makeToasts.remove(i);
				} else {
					m.render();
				}
			}
			// render toasts -----------------------------------

			// file explorer ------------------------------------------------------
			if (fileToRender_pathSelector.openFileExplorer_btn.getIsClicked() == true) {

				if (fileExplorerIsOpen == false) {
					saveFrame("data\\imgs\\screenshots\\homeScreen.png");
					screenshot = loadImage("data\\imgs\\screenshots\\homeScreen.png");
					screenshot = new ImageBlurHelper(this).blur(screenshot, 3);
					fileExplorerIsOpen = true;
				}
				image(screenshot, width / 2, height / 2);
				selectBlendFile_fileExplorer.render();

				if (selectBlendFile_fileExplorer.getIsClosed()) {
					if (selectBlendFile_fileExplorer.getIsCanceled()) {
					} else {
						String[] splitStr = split(selectBlendFile_fileExplorer.getPath(), "\\");
						String setPath = "";
						if (fileToRender_pathSelector.getSelectFolder()) {
							for (int i = 0; i < splitStr.length; i++) {
								String[] splitStr2 = split(splitStr[i], ".");
								if (splitStr2.length > 1) {
									break;
								}
								setPath += splitStr[i] + "\\";
							}
						} else {
							File f = new File(selectBlendFile_fileExplorer.getPath());
							if (f.isDirectory()) {
								println("no file selected");
							} else {
								setPath = selectBlendFile_fileExplorer.getPath();
							}
						}
						if (setPath.length() > 0) {
							fileToRender_pathSelector.setText(setPath);
						}
					}
					selectBlendFile_fileExplorer.setIsClosed(false);
					selectBlendFile_fileExplorer.setIsCanceled(false);
					fileExplorerIsOpen = false;
					fileToRender_pathSelector.openFileExplorer_btn.setIsClicked(false);
				}
			}
			// file explorer ------------------------------------------------------
			// render all ---------------------------------------------------------------

			// handle Buttons ------------------------------------------------------
			if (startRendering_btn.getIsClicked() == true) {
				Boolean correctlySelected = true;
				String errorMessage = "";

				if (homeSettings_checkboxes[6].getIsChecked() == false && homeSettings_checkboxes[7].getIsChecked() == false) {
					correctlySelected = false;
					if (homeSettings_checkboxes[3].getIsChecked()) {
						correctlySelected = true;
					}
					if (correctlySelected == false) {
						if (errorMessage.length() > 0) {
							errorMessage += " - ";
						}
						errorMessage += "Either render animation or still frame";
					}
				}

				if (homeSettings_checkboxes[0].getIsChecked() == false && homeSettings_checkboxes[1].getIsChecked() == false) {
					correctlySelected = false;
					if (errorMessage.length() > 0) {
						errorMessage += " - ";
					}
					errorMessage += "Either select 'Render with full force' or 'Render only with slaves'";
				}

				if (homeSettings_checkboxes[2].getIsChecked() == false && homeSettings_checkboxes[3].getIsChecked() == false) {
					correctlySelected = false;
					if (errorMessage.length() > 0) {
						errorMessage += " - ";
					}
					errorMessage += "Either select .blend file or choose 'Render on Sheepit'";
				}

				if (homeSettings_checkboxes[4].getIsChecked() == false && homeSettings_checkboxes[5].getIsChecked() == false) {
					correctlySelected = false;
					if (errorMessage.length() > 0) {
						errorMessage += " - ";
					}
					errorMessage += "Either use CPU or GPU";
				}

				if (homeSettings_checkboxes[2].getIsChecked() && homeSettings_checkboxes[3].getIsChecked()) {
					correctlySelected = false;
					if (errorMessage.length() > 0) {
						errorMessage += " - ";
					}
					errorMessage += "Cant render File AND on Sheepit";
				}

				if (homeSettings_checkboxes[6].getIsChecked() && homeSettings_checkboxes[7].getIsChecked()) {
					correctlySelected = false;
					if (errorMessage.length() > 0) {
						errorMessage += " - ";
					}
					errorMessage += "Cant render Animation AND still frame";
				}

				if (homeSettings_checkboxes[6].getIsChecked()) {
					if (endFrame_counterArea.getCount() < startFrame_counterArea.getCount()) {
						if (errorMessage.length() > 0) {
							errorMessage += " - ";
						}
						correctlySelected = false;
						errorMessage += "Cant render negative frame range";
					}
				}

				if (homeSettings_checkboxes[3].getIsChecked()) {
					if (homeSettings_checkboxes[6].getIsChecked() || homeSettings_checkboxes[7].getIsChecked()) {
						if (errorMessage.length() > 0) {
							errorMessage += " - ";
						}
						correctlySelected = false;
						errorMessage += "Cant render frame/Animation AND on Sheepit";
					}
				}

				if (correctlySelected) {
					mode = 101;
				} else {
					makeToasts.add(new MakeToast(this, width / 2, height - stdTs * 2, stdTs, margin, edgeRad, errorMessage.length() * 2, light, textCol, textYShift, false, errorMessage, stdFont, null));
				}

				startRendering_btn.setIsClicked(false);
			}
			// handle Buttons ------------------------------------------------------

		}

		if (mode == 2) {
			nodeEditor.render();
		}

		if (mode == 3) {// setup for the first time -----------------
			if (loadingScreen.firstSetup == true && settingsScreen.getMode() == 0) {
				fill(light);
				stroke(light);
				rect(width / 2, btnSize / 2 + margin, width, btnSize + margin * 2);
				firstSetupPicto.render();
				firstSetupHelp_btn.render();
				fill(textDark);
				textAlign(LEFT, CENTER);
				text("First setup", firstSetupPicto.getX() + btnSize + margin * 2, firstSetupPicto.getY());
				if (settingsScreen.getSuccessfullySaved() == true) {
					loadingScreen.firstSetup = true;
					mode = 1;
				}
			} else {
				renderMainButtons();
			}
			settingsScreen.render();

			// render toasts----------------------------------------------
			for (int i = 0; i < settingsScreen.personalData_et.getToastList().size(); i++) {
				MakeToast m = (MakeToast) settingsScreen.personalData_et.getToastList().get(i);
				if (m.remove) {
					settingsScreen.personalData_et.removeToast(i);
				} else {
					m.render();
				}
			}

			for (int i = 0; i < settingsScreen.fileExplorer.searchBar.searchBar_et.getToastList().size(); i++) {
				MakeToast m = (MakeToast) settingsScreen.fileExplorer.searchBar.searchBar_et.getToastList().get(i);
				if (m.remove) {
					settingsScreen.fileExplorer.searchBar.searchBar_et.removeToast(i);
				} else {
					m.render();
				}
			}

			for (int i = 0; i < settingsScreen.getToastList().size(); i++) {
				MakeToast m = (MakeToast) settingsScreen.getToastList().get(i);
				if (m.remove) {
					settingsScreen.removeToast(i);
				} else {
					m.render();
				}
			}
			// render toasts----------------------------------------------

		}

		if (mode == 4) {
		    spreadBlenderScreen.render();
		}
		if (mode == 5) {
			themeScreen.render();
		}
		if(mode==6) {
		    questionScreen.render();
		}

	}

	public void renderMainButtons() {
		if (mainButtons[0].getClickCount() % 2 == 0) {
			int fillUpBarW = width - (mainButtons[mainButtons.length - 1].getX() + btnSize / 2 + margin * 2);
			int fillUpBarX = mainButtons[mainButtons.length - 1].getX() + btnSize / 2 + margin + fillUpBarW / 2;
			fill(light);
			stroke(light);
			rect(fillUpBarX, btnSize / 2 + margin, fillUpBarW, btnSize, edgeRad);
			textFont(stdFont);
			textSize(subtitleTs);
			fill(textDark);
			textAlign(CENTER, CENTER);
			text(APKName + " | " + APKDescription + " | " + modeNames[mode - 1], fillUpBarX, btnSize / 2 + margin);
		}

		for (int i = mainButtons.length - 1; i >= 0; i--) {
			if (mainButtons[0].getClickCount() % 2 == 0 || i == 0) {
				mainButtons[i].render();
			}
			if (mainButtons[i].getIsClicked() == true) {
				switch (i) {
				case 0:
					for (int i2 = 1; i2 < mainButtons.length; i2++) {
						if (mainButtons[0].getClickCount() % 2 != 0) {
							mainButtons[i2].setPos(mainButtons[0].getX(), mainButtons[0].getY());
						} else {
							mainButtons[i2].setPos(btnSize / 2 + margin + btnSize * i2 + margin * i2, btnSize / 2 + margin);
						}
					}
					break;
				default:
					mode = i;
					break;
				}
				mainButtons[i].setIsClicked(false);
			}
		}
	}

	@Override
	public void mousePressed() {
		if (mode == 1) {
			if (fileExplorerIsOpen == false) {
				fileToRender_pathSelector.openFileExplorer_btn.onMousePressed();
				startFrame_counterArea.onMousePressed();
				endFrame_counterArea.onMousePressed();
				stillFrame_counterArea.onMousePressed();
				startRendering_btn.onMousePressed();

				if (loadingScreen.firstSetup == false) {
					for (int i = 0; i < mainButtons.length; i++) {
						if (mainButtons[0].getClickCount() % 2 == 0 || i == 0) {
							mainButtons[i].onMousePressed();
						}
					}
				}

			} else {
				selectBlendFile_fileExplorer.onMousePressed();
			}
		}
		if (mode == 2) {
			nodeEditor.onMousePressed(mouseButton);
		}

		if (mode == 3) {
			settingsScreen.onMousePressed();
		}
		if(mode==4) {
		    spreadBlenderScreen.onMousePressed();
		}
		if(mode==5) {
			themeScreen.onMousePressed();
		}
		if(mode==6) {
		    questionScreen.onMousePressed();
		}
	}

	@Override
	public void mouseReleased() {

		if (mode == 1) {
			if (fileExplorerIsOpen == false) {
				for (int i = 0; i < homeSettings_checkboxes.length; i++) {
					homeSettings_checkboxes[i].onMouseReleased();
				}
				fileToRender_pathSelector.openFileExplorer_btn.onMouseReleased();
				startFrame_counterArea.onMouseReleased();
				endFrame_counterArea.onMouseReleased();
				stillFrame_counterArea.onMouseReleased();
				startRendering_btn.onMouseReleased();

				if (loadingScreen.firstSetup == false) {
					for (int i = 0; i < mainButtons.length; i++) {
						if (mainButtons[0].getClickCount() % 2 == 0 || i == 0) {
							mainButtons[i].onMouseReleased();
						}
					}
				}

			} else {
				selectBlendFile_fileExplorer.onMouseReleased(mouseButton);
			}

		}

		if (mode == 2) {
			nodeEditor.onMouseReleased(mouseButton);
		}

		if (mode == 3) {
			settingsScreen.onMouseReleased(mouseButton);
		}
		
		if(mode==4) {
            spreadBlenderScreen.onMouseReleased();
        }
        if(mode==5) {
        	themeScreen.onMouseReleased();
        }
        if(mode==6) {
            questionScreen.onMouseReleased();
        }

	}

	@Override
	public void mouseWheel(MouseEvent event) {
		float e = event.getCount();

		if (mode == 1) {
			if (fileExplorerIsOpen == false) {
				startFrame_counterArea.onScroll(e);
				endFrame_counterArea.onScroll(e);
				stillFrame_counterArea.onScroll(e);
			} else {
				selectBlendFile_fileExplorer.onScroll(e);
			}
		}

		if (mode == 2) {
			nodeEditor.onScroll(e);
		}

		if (mode == 3) {
			settingsScreen.onScroll(e);
		}
		
		if(mode==4) {
        }
        if(mode==5) {
        	themeScreen.onScroll(e);
        }
        if(mode==6) {
            questionScreen.onScroll(e);
        }
	}

	@Override
	public void keyReleased() {

		if (mode == 1) {
			if (fileExplorerIsOpen == false) {

			} else {
				selectBlendFile_fileExplorer.onKeyReleased(key);
			}
		}
		if (mode == 2) {
			nodeEditor.onKeyReleased(key);
		}

		if (mode == 3) {
			settingsScreen.onKeyReleased(key);
		}
		
		if(mode==4) {
            spreadBlenderScreen.onKeyReleased(key);
        }
        if(mode==5) {
        	themeScreen.onKeyReleased(key);
        }
        if(mode==6) {
            questionScreen.onKeyReleased(key);
        }
	}

	public ImageButton[] getMainButtons() {
		return mainButtons;
	}
	
	public ImageButton getFirstSetupHelp_btn() {
		return firstSetupHelp_btn;
	}
	public LoadingScreen getLoadingScreen() {
		return loadingScreen;
	}
	public NodeEditor getNodeEditor() {
		return nodeEditor;
	}
	
}
