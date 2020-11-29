package com.dominic.network_apk;

import java.io.File;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import processing.core.PApplet;
import processing.core.PFont;

public class HomeScreenMaster {
	private int mode, btnSize, btnSizeSmall, edgeRad, margin, stdTs, dark, light, lighter, border, textCol, textDark;
	private float textYShift;
	private Boolean fileExplorerIsOpen = false, prevFileExplorerIsOpen = false;
	private PFont stdFont;
	private PApplet p;
	private PathSelector fileToRender_pathSelector, imageSavePath_PathSelector;
	private CounterArea startFrame_counterArea, endFrame_counterArea, stillFrame_counterArea, resX_counterArea, resY_counterArea, samples_counterArea;
	private ImageButton startRendering_btn, saveHome_ImageButton;
	private HorizontalList fileSelector_HorizontalList;
	private MainActivity mainActivity;
	private ImageButton[] mainButtons;
	private Checkbox[] homeSettings_checkboxes = new Checkbox[9];
	private ArrayList<MakeToast> makeToasts = new ArrayList<MakeToast>();
	private JsonHelper jsonHelper;

	public HomeScreenMaster(PApplet p, int mode, int btnSize, int btnSizeSmall, int edgeRad, int margin, int stdTs, int dark, int light, int lighter, int border, int textCol, int textDark, float textYShift, String[] homeScreenPictoPaths, String[] arrowPaths, String[] hoLiPictoPaths, String[] fileExplorerPaths, PFont stdFont) {
		this.p = p;
		this.mode = mode;
		this.btnSize = btnSize;
		this.btnSizeSmall = btnSizeSmall;
		this.edgeRad = edgeRad;
		this.margin = margin;
		this.stdTs = stdTs;
		this.dark = dark;
		this.light = light;
		this.lighter = lighter;
		this.border = border;
		this.textCol = textCol;
		this.textDark = textDark;
		this.textYShift = textYShift;
		this.stdFont = stdFont;
		mainActivity = (MainActivity) p;
		jsonHelper = new JsonHelper(p);
		if (mainActivity.getIsMaster()) {
			mainButtons = mainActivity.getMainButtonsMaster();
		} else {
			mainButtons = mainActivity.getMainButtonsSlave();
		}
		int rowDist = btnSize, colDist = (p.width / 9 * 2);
		int startY = mainButtons[0].getH() + (p.height - mainButtons[0].getH() - rowDist * 4 - btnSizeSmall) / 2;
		int startX = colDist / 2 + (p.width - homeSettings_checkboxes.length / 2 * colDist) / 2;
		String[] checkBoxTexts = { "Render with full force", "Render only with slaves", "", "Render on Sheepit", "Use CPU", "Use GPU", "", "", "" };
		String[] checkBoxHoverTexts = { "", "", "Render listed .blend files", "", "", "", "", "", "Use new Resolution" };
		int ys = rowDist;
		int is = 0;
		for (int i = 0; i < homeSettings_checkboxes.length; i++) {
			if (i != 0) {
				if (i % 4 == 0) {
					ys += rowDist;
					is += 4;
				}
			}
			homeSettings_checkboxes[i] = new Checkbox(p, (int) (startX + colDist * (i - is)), startY + ys, (int) (colDist / 1.5f), btnSizeSmall, btnSizeSmall, edgeRad, margin, stdTs, light, light, border, textCol, textYShift, false, false, checkBoxTexts[i], checkBoxHoverTexts[i], homeScreenPictoPaths[0], stdFont, null);
		}
		int psW = homeSettings_checkboxes[2].getW() - homeSettings_checkboxes[2].getBoxDim() - margin;
		fileToRender_pathSelector = new PathSelector(p, -homeSettings_checkboxes[2].getW() / 2 + homeSettings_checkboxes[2].getBoxDim() + margin * 2 + psW / 2, 0, psW, btnSizeSmall, edgeRad, margin, stdTs, btnSizeSmall, border, light, textCol, dark, light, lighter, textDark, textYShift, false, true, "...\\\\File.blend", homeScreenPictoPaths[1], fileExplorerPaths, stdFont, homeSettings_checkboxes[2]);

		int sfW = (int) (p.width / 16 - margin);
		int sfX = (int) ((homeSettings_checkboxes[6].getX() - homeSettings_checkboxes[6].getW() / 2 + homeSettings_checkboxes[6].getBoxDim() + margin * 2)) - homeSettings_checkboxes[6].getX() + sfW / 2;
		startFrame_counterArea = new CounterArea(p, sfX, 0, sfW, btnSizeSmall, edgeRad, margin, stdTs, 0, 1000000000, 0, light, lighter, textCol, textYShift, true, "Startframe", arrowPaths, stdFont, homeSettings_checkboxes[6]);
		endFrame_counterArea = new CounterArea(p, sfX + sfW + margin, 0, sfW, btnSizeSmall, edgeRad, margin, stdTs, 0, 1000000000, 250, light, lighter, textCol, textYShift, true, "Endframe", arrowPaths, stdFont, homeSettings_checkboxes[6]);
		// sfW = (int) p.textWidth(checkBoxTexts[3]);
		sfW = fileToRender_pathSelector.getW();
		sfX = (int) ((homeSettings_checkboxes[7].getX() - homeSettings_checkboxes[7].getW() / 2 + homeSettings_checkboxes[7].getBoxDim() + margin * 2)) - homeSettings_checkboxes[7].getX() + sfW / 2;
		stillFrame_counterArea = new CounterArea(p, sfX, 0, sfW, btnSizeSmall, edgeRad, margin, stdTs, 0, 1000000000, 0, light, lighter, textCol, textYShift, true, "Still frame", arrowPaths, stdFont, homeSettings_checkboxes[7]);

		sfW = rowDist * 4 - homeSettings_checkboxes[8].getBoxDim() + margin;
		sfX = (-(homeSettings_checkboxes[8].getX() - homeSettings_checkboxes[8].getBoxX()) + homeSettings_checkboxes[8].getBoxDim() / 2 + margin + sfW / 2);
		resX_counterArea = new CounterArea(p, sfX, 0, sfW, btnSizeSmall, edgeRad, margin, stdTs, 4, 1000000000, 2520, light, lighter, textCol, textYShift, true, "Resolution X", arrowPaths, stdFont, homeSettings_checkboxes[8]);

		sfX = sfW + margin;
		resY_counterArea = new CounterArea(p, sfX, 0, sfW, btnSizeSmall, edgeRad, margin, stdTs, 4, 1000000000, 1080, light, lighter, textCol, textYShift, true, "Resolution Y", arrowPaths, stdFont, resX_counterArea);

		sfW = homeSettings_checkboxes[2].getBoxDim() + margin + fileToRender_pathSelector.getW();
		sfX = (homeSettings_checkboxes[2].getBoxX() - homeSettings_checkboxes[2].getX()) - homeSettings_checkboxes[2].getBoxDim() / 2 + sfW / 2;
		samples_counterArea = new CounterArea(p, sfX, rowDist, sfW, btnSizeSmall, edgeRad, margin, stdTs, 2, 1000000000, 128, light, lighter, textCol, textYShift, true, "Sampling", arrowPaths, stdFont, homeSettings_checkboxes[6]);

		sfW = homeSettings_checkboxes[3].getBoxDim() + margin + fileToRender_pathSelector.getW();
		sfX = (homeSettings_checkboxes[3].getBoxX() - homeSettings_checkboxes[3].getX()) - homeSettings_checkboxes[3].getBoxDim() / 2 + sfW / 2;
		imageSavePath_PathSelector = new PathSelector(p, sfX, rowDist, sfW, btnSizeSmall, edgeRad, margin, stdTs, btnSizeSmall, border, light, textCol, dark, light, lighter, textDark, textYShift, true, true, "...\\\\File.blend", homeScreenPictoPaths[1], fileExplorerPaths, stdFont, homeSettings_checkboxes[7]);
		startRendering_btn = new ImageButton(p, p.width / 2, startY + rowDist * 4 + btnSize / 2, btnSize, btnSize, stdTs, margin, edgeRad, -1, textYShift, true, false, textCol, light, homeScreenPictoPaths[2], "Start rendering", homeSettings_checkboxes[7]);

		String[] startList = {};

		int p1 = homeSettings_checkboxes[4].getBoxX() - homeSettings_checkboxes[4].getBoxDim() / 2 - margin / 2;
		int p2 = stillFrame_counterArea.getX() + stillFrame_counterArea.getW() / 2;
		int hlH = p2 - p1;
		int hlX = p1 + hlH / 2;
		fileSelector_HorizontalList = new HorizontalList(p, hlX, startY, hlH, btnSizeSmall + margin * 2, margin, edgeRad, stdTs, (int) p.textWidth("Files to render") + margin * 3 + btnSizeSmall, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border, textYShift, '\\', false, true, false, "Files to render", hoLiPictoPaths, startList, stdFont, null);

		saveHome_ImageButton = new ImageButton(p, p.width - margin - btnSizeSmall / 2, p.height - margin - btnSizeSmall / 2, btnSizeSmall, btnSizeSmall, stdTs, margin, edgeRad, 19, textYShift, true, false, textCol, light, homeScreenPictoPaths[3], "Save home settings", null);

		setData();
	}

	public void render() {
		if (imageSavePath_PathSelector.getPath().length() > 0 == false) {
			imageSavePath_PathSelector.setPath(mainActivity.getPathToImageFolder(),false);
		}

		fileExplorerIsOpen = false;
		if (fileToRender_pathSelector.getFileExplorerIsOpen() || imageSavePath_PathSelector.getFileExplorerIsOpen()) {
			fileExplorerIsOpen = true;
		}
		// add to list --------------------------------------------------
		if (fileExplorerIsOpen == false) {
			if (fileExplorerIsOpen != prevFileExplorerIsOpen && !fileToRender_pathSelector.getIsCanceled()) {
				String newBlendFile = fileToRender_pathSelector.getPath();
				String[] splitStr = p.split(newBlendFile, "\\");
				String[] m1 = p.match(splitStr[splitStr.length - 1], ".blend");
				if (m1 != null) {
					// p.println("is blendfile");
					String[] holi1 = fileSelector_HorizontalList.getList();
					String[] newHoLi1 = new String[holi1.length + 1];
					for (int i = 0; i < holi1.length; i++) {
						newHoLi1[i] = holi1[i];
					}
					newHoLi1[newHoLi1.length - 1] = newBlendFile;
					fileSelector_HorizontalList.setList(newHoLi1);
				} else {
					p.println("no blendfile");
				}
			}
			// }
			// add to list --------------------------------------------------

			// render all ---------------------------------------------------------------
			// if (fileExplorerIsOpen == false) {
			if (mainActivity.getIsMaster()) {
				mainActivity.renderMainButtonsMaster();
			} else {
				mainActivity.renderMainButtonsSlave();
			}

			resX_counterArea.render();
			resY_counterArea.render();
			samples_counterArea.render();

			for (int i = 0; i < homeSettings_checkboxes.length; i++) {
				homeSettings_checkboxes[i].render();
			}
			endFrame_counterArea.render();
			startFrame_counterArea.render();
			stillFrame_counterArea.render();
			startRendering_btn.render();
			p.stroke(light);
			p.line(homeSettings_checkboxes[0].getBoxX() - homeSettings_checkboxes[0].getBoxDim() / 2, startRendering_btn.getY(), startRendering_btn.getX() - startRendering_btn.getW() / 2 - margin * 2, startRendering_btn.getY());
			p.line(startRendering_btn.getX() + startRendering_btn.getW() / 2 + margin * 2, startRendering_btn.getY(), stillFrame_counterArea.getX() + stillFrame_counterArea.getW() / 2, startRendering_btn.getY());

			fileSelector_HorizontalList.render();
			saveHome_ImageButton.render();

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

			// render all ---------------------------------------------------------------

			// handle checkboxes ------------------------------------------------------
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

				if (fileSelector_HorizontalList.getList().length < 1 && homeSettings_checkboxes[2].getIsChecked()) {
					if (errorMessage.length() > 0) {
						errorMessage += " - ";
					}
					errorMessage += "No .blend detected";
					correctlySelected = false;
				}

				// Atention --------------------------------------------
				// correctlySelected = true;
				// Atention --------------------------------------------

				if (correctlySelected) {
					mainActivity.setMode(101);

					if (homeSettings_checkboxes[2].getIsChecked()) { // renderFile
						mainActivity.getRenderOverview().getFilesRenderingScreen().setupAll();
						mainActivity.getRenderOverview().setRenderMode(0.1f);
						mainActivity.getRenderOverview().setFileList(fileSelector_HorizontalList.getList());
						mainActivity.getRenderOverview().getRenderFilesSettings().setStartupVals();
						mainActivity.getRenderOverview().getRenderFilesSettings().getImageSavePath_pathSelector().setPath(mainActivity.getPathToImageFolder(),false);
					}
					if (homeSettings_checkboxes[3].getIsChecked()) { // render on sheepit
						mainActivity.getRenderOverview().getSheepitSettingsScreen().setData();
						mainActivity.getRenderOverview().setRenderMode(1.1f);
					}

				} else {
					makeToasts.add(new MakeToast(p, p.width / 2, p.height - stdTs * 2, stdTs, margin, edgeRad, errorMessage.length() * 2, light, textCol, textYShift, false, errorMessage, stdFont, null));
				}

				startRendering_btn.setIsClicked(false);
			}

			if (saveHome_ImageButton.getIsClicked()) {
				saveData();
				saveHome_ImageButton.setIsClicked(false);
			}
		}

		if (imageSavePath_PathSelector.getFileExplorerIsOpen() == false) {
			fileToRender_pathSelector.render();
		}
		if (fileToRender_pathSelector.getFileExplorerIsOpen() == false) {
			imageSavePath_PathSelector.render();
		}
		homeSettings_checkboxes[2].getHoverText().render();
		prevFileExplorerIsOpen = fileExplorerIsOpen;
		// handle checkboxes ------------------------------------------------------

	}

	private void saveData() {
		jsonHelper.clearArray();
		JSONArray homeScreenSettings = new JSONArray();
		JSONObject settingsObj = new JSONObject();

		for (int i = 0; i < homeSettings_checkboxes.length; i++) {
			settingsObj.put("checkbox" + i, homeSettings_checkboxes[i].getIsChecked());
		}
		settingsObj.put("resX", resX_counterArea.getCount());
		settingsObj.put("resY", resY_counterArea.getCount());
		settingsObj.put("samples", samples_counterArea.getCount());
		settingsObj.put("stillFrame", stillFrame_counterArea.getCount());
		settingsObj.put("startFrame", startFrame_counterArea.getCount());
		settingsObj.put("endFrame", endFrame_counterArea.getCount());
		settingsObj.put("imageSavePath", imageSavePath_PathSelector.getPath());

		JSONArray fileArray = new JSONArray();
		for (int i = 0; i < fileSelector_HorizontalList.getList().length; i++) {
			fileArray.add(fileSelector_HorizontalList.getList()[i]);
		}
		settingsObj.put("fileList", fileArray);
		homeScreenSettings.add(settingsObj);
		p.println(homeScreenSettings);
		String jsonPath = mainActivity.getHomeScreenMasterSettingsPath();
		p.println(jsonPath);
		jsonHelper.setArray(homeScreenSettings);
		jsonHelper.writeData(jsonPath);
	}

	private void setData() {
		JSONArray loadedData = new JSONArray();
		JSONObject settingsObject = new JSONObject();
		// give command to all pcs to do test -----------------------------

		loadedData = jsonHelper.getData(mainActivity.getHomeScreenMasterSettingsPath());
		if (loadedData.isEmpty()) {
		} else {
			try {
				settingsObject = (JSONObject) loadedData.get(0);
				for (int i = 0; i < homeSettings_checkboxes.length; i++) {
					homeSettings_checkboxes[i].setIsChecked(Boolean.parseBoolean(settingsObject.get("checkbox" + i).toString()));
				}
				resX_counterArea.setCount(Integer.parseInt(settingsObject.get("resX").toString()));
				resY_counterArea.setCount(Integer.parseInt(settingsObject.get("resY").toString()));
				samples_counterArea.setCount(Integer.parseInt(settingsObject.get("samples").toString()));
				stillFrame_counterArea.setCount(Integer.parseInt(settingsObject.get("stillFrame").toString()));
				startFrame_counterArea.setCount(Integer.parseInt(settingsObject.get("startFrame").toString()));
				endFrame_counterArea.setCount(Integer.parseInt(settingsObject.get("endFrame").toString()));
				imageSavePath_PathSelector.setPath(settingsObject.get("imageSavePath").toString(),false);

				JSONArray fileArray = (JSONArray) settingsObject.get("fileList");
				String[] fileArrayStr;
				ArrayList<String> fileList = new ArrayList<>();
				for (int i = 0; i < fileArray.size(); i++) {
					File curBlendFile = new File(fileArray.get(i).toString());
					if (curBlendFile.exists()) {
						fileList.add(curBlendFile.getAbsolutePath());
					}
				}
				fileArrayStr = new String[fileList.size()];
				for (int i = 0; i < fileList.size(); i++) {
					fileArrayStr[i] = fileList.get(i);
				}
				fileSelector_HorizontalList.setList(fileArrayStr);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void onMousePressed(int mouseButton) {
		if (fileExplorerIsOpen == false) {

			startFrame_counterArea.onMousePressed();
			endFrame_counterArea.onMousePressed();
			stillFrame_counterArea.onMousePressed();
			startRendering_btn.onMousePressed();

			resX_counterArea.onMousePressed();
			resY_counterArea.onMousePressed();
			samples_counterArea.onMousePressed();
			saveHome_ImageButton.onMousePressed();
			if (mainActivity.getLoadingScreen().getIsFirstSetup() == false) {
				for (int i = 0; i < mainButtons.length; i++) {
					if (mainButtons[0].getClickCount() % 2 == 0 || i == 0) {
						mainButtons[i].onMousePressed();
					}
				}
			}
			fileSelector_HorizontalList.onMousePressed();
		}
		fileToRender_pathSelector.onMousePressed(mouseButton);
		imageSavePath_PathSelector.onMousePressed(mouseButton);
	}

	public void onMouseReleased(int mouseButton) {
		if (fileExplorerIsOpen == false) {

			for (int i = 0; i < homeSettings_checkboxes.length; i++) {
				homeSettings_checkboxes[i].onMouseReleased();
			}
			startFrame_counterArea.onMouseReleased();
			endFrame_counterArea.onMouseReleased();
			stillFrame_counterArea.onMouseReleased();
			startRendering_btn.onMouseReleased();

			resX_counterArea.onMouseReleased();
			resY_counterArea.onMouseReleased();
			samples_counterArea.onMouseReleased();
			saveHome_ImageButton.onMouseReleased();
			// if (mainActivity.getLoadingScreen().getIsFirstSetup() == false) {
			for (int i = 0; i < mainButtons.length; i++) {
				if (mainButtons[0].getClickCount() % 2 == 0 || i == 0) {
					mainButtons[i].onMouseReleased();
				}
			}
			// }
			fileSelector_HorizontalList.onMouseReleased(mouseButton);
		}
		fileToRender_pathSelector.onMouseReleased(mouseButton);
		imageSavePath_PathSelector.onMouseReleased(mouseButton);

	}

	public void onKeyPressed(char key) {
		fileToRender_pathSelector.onKeyPressed(key);
		imageSavePath_PathSelector.onKeyPressed(key);
	}

	public void onKeyReleased(char key) {
		fileToRender_pathSelector.onKeyReleased(key);
		imageSavePath_PathSelector.onKeyReleased(key);
		if (fileExplorerIsOpen == false) {
			if (key == p.DELETE) {
				String[] hoLi1 = fileSelector_HorizontalList.getList();
				String[] newHoLi1 = new String[hoLi1.length - 1];
				for (int i = 0; i < hoLi1.length; i++) {
					if (i < fileSelector_HorizontalList.getSelectedInd()) {
						newHoLi1[i] = hoLi1[i];
					}
					if (i > fileSelector_HorizontalList.getSelectedInd()) {
						newHoLi1[i - 1] = hoLi1[i];
					}
				}
				fileSelector_HorizontalList.setList(newHoLi1);
			}
		}
	}

	public void onScroll(float e) {
		fileToRender_pathSelector.onScroll(e);
		imageSavePath_PathSelector.onScroll(e);
		if (fileExplorerIsOpen == false) {
			startFrame_counterArea.onScroll(e);
			endFrame_counterArea.onScroll(e);
			stillFrame_counterArea.onScroll(e);
			fileSelector_HorizontalList.onScroll(e);

			resX_counterArea.onScroll(e);
			resY_counterArea.onScroll(e);
			samples_counterArea.onScroll(e);
		}
	}

	public int getMode() {
		return mode;
	}

	public Checkbox[] getCheckboxes() {
		return homeSettings_checkboxes;
	}

	public CounterArea getStartFrame_CounterArea() {
		return startFrame_counterArea;
	}

	public CounterArea getEndFrame_CounterArea() {
		return endFrame_counterArea;
	}

	public CounterArea getStillFrame_counterArea() {
		return stillFrame_counterArea;
	}

	public CounterArea getResX_counterArea() {
		return resX_counterArea;
	}

	public CounterArea getResY_counterArea() {
		return resY_counterArea;
	}

	public CounterArea getSamples_counterArea() {
		return samples_counterArea;
	}

	public PathSelector getFileToRender_pathSelector() {
		return fileToRender_pathSelector;
	}

	public PathSelector getImageSavePath_pathSelector() {
		return imageSavePath_PathSelector;
	}
}
