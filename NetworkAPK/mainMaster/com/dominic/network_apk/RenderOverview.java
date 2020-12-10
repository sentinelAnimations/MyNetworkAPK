package com.dominic.network_apk;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import processing.core.PApplet;
import processing.core.PFont;

public class RenderOverview {

	private int mode, stdTs, edgeRad, margin, btnSize, btnSizeSmall, dark, light, lighter, lightest, textCol, textDark, border;
	private float renderMode; // rendermode --> 0=render files, 0.1=files render settings, 1=render on
								// sheepit,1.1=sheepitSettings
								// 2=imageView
	private float textYShift;
	private Boolean anyFileExplorerIsOpen = false;
	private String mySavePath;
	private String[] pictoPaths;
	private PFont stdFont;
	private PApplet p;
	private ImageButton cancelRendering_ImageButton, imageView_imageButton, sleep_ImageButton,collect_ImageButton;
	private MainActivity mainActivity;
	private FilesSettingsScreen filesSettingsScreen;
	private FilesRenderingScreen filesRenderingScreen;
	private RenderOnSheepitScreen renderOnSheepitScreen;
	private ImageViewScreen imageViewScreen;
	private SheepitSettingsScreen sheepitSettingsScreen;
	private JsonHelper jsonHelper;

	public RenderOverview(PApplet p, int mode, int stdTs, int edgeRad, int margin, int btnSizeLarge, int btnSize, int btnSizeSmall, int dark, int light, int lighter, int lightest, int textCol, int textDark, int border, int green, int red, int blue, float textYShift, String mySavePath, String[] pictoPaths, String[] hoLiPictoPaths, String[] arrowPaths, String[] fileExplorerPaths, PFont stdFont) {
		this.mode = mode;
		this.p = p;
		this.stdTs = stdTs;
		this.edgeRad = edgeRad;
		this.margin = margin;
		this.btnSize = btnSize;
		this.btnSizeSmall = btnSizeSmall;
		this.dark = dark;
		this.light = light;
		this.lighter = lighter;
		this.lightest = lightest;
		this.textCol = textCol;
		this.textDark = textDark;
		this.border = border;
		this.textYShift = textYShift;
		this.mySavePath = mySavePath;
		this.pictoPaths = pictoPaths;
		this.stdFont = stdFont;
		mainActivity = (MainActivity) p;
		cancelRendering_ImageButton = new ImageButton(p, p.width - margin - btnSizeSmall / 2, p.height - margin - btnSizeSmall / 2, btnSizeSmall, btnSizeSmall, stdTs, margin, edgeRad, -1, textYShift, true, false, textCol, light, pictoPaths[0], "Quit render process", null);
		imageView_imageButton = new ImageButton(p, p.width - margin * 2 - btnSizeSmall / 2 - btnSizeSmall, p.height - margin - btnSizeSmall / 2, btnSizeSmall, btnSizeSmall, stdTs, margin, edgeRad, -1, textYShift, true, false, textCol, light, pictoPaths[5], "Image view", null);
		sleep_ImageButton = new ImageButton(p, p.width - margin * 3 - btnSizeSmall / 2 - btnSizeSmall * 2, p.height - margin - btnSizeSmall / 2, btnSizeSmall, btnSizeSmall, stdTs, margin, edgeRad, -1, textYShift, true, false, textCol, light, pictoPaths[2], "Sleep", null);
		collect_ImageButton = new ImageButton(p, p.width - margin * 4 - btnSizeSmall / 2 - btnSizeSmall * 3, p.height - margin - btnSizeSmall / 2, btnSizeSmall, btnSizeSmall, stdTs, margin, edgeRad, -1, textYShift, true, false, textCol, light, pictoPaths[12], "Collect", null);

		String[] rFSPictoPaths = { pictoPaths[3], pictoPaths[6] };
		filesSettingsScreen = new FilesSettingsScreen(p, stdTs, edgeRad, margin, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border, textYShift, rFSPictoPaths, hoLiPictoPaths, arrowPaths, fileExplorerPaths, stdFont);
		String[] fRSPictoPaths = { pictoPaths[4], pictoPaths[7], pictoPaths[9], pictoPaths[10], pictoPaths[11], pictoPaths[2],pictoPaths[13] };
		filesRenderingScreen = new FilesRenderingScreen(p, stdTs, edgeRad, margin, btnSizeLarge, btnSize, btnSizeSmall, dark, light, lighter, lightest, textCol, textDark, border, green, red, blue, textYShift, fRSPictoPaths, hoLiPictoPaths, stdFont);
		String[] rOSPictoPaths = { pictoPaths[1], pictoPaths[2] };
		renderOnSheepitScreen = new RenderOnSheepitScreen(p, stdTs, edgeRad, margin, btnSizeLarge, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border, red, green, textYShift, rOSPictoPaths, hoLiPictoPaths, stdFont);
		String[] iVSPictoPaths = { pictoPaths[6], pictoPaths[8] };
		imageViewScreen = new ImageViewScreen(p, stdTs, edgeRad, margin, btnSizeLarge, btnSize, btnSizeSmall, dark, light, lighter, lightest, textCol, textDark, border, green, textYShift, iVSPictoPaths, fileExplorerPaths, stdFont);
		sheepitSettingsScreen = new SheepitSettingsScreen(p, stdTs, edgeRad, margin, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border, textYShift, new String[] { pictoPaths[3] }, stdFont);
		jsonHelper = new JsonHelper(p);
	}

	public void render() {

		if (imageViewScreen.getImageView_PathSelector().getFileExplorerIsOpen() == true || filesSettingsScreen.getImageSavePath_pathSelector().getFileExplorerIsOpen() == true) {
			anyFileExplorerIsOpen = true;
		} else {
			anyFileExplorerIsOpen = false;
		}

		// render all ---------------------------------------

		if (renderMode == 0.1f) {
			filesSettingsScreen.render();
		}
		if (renderMode == 0) {
			filesRenderingScreen.render();
			imageView_imageButton.render();
			sleep_ImageButton.render();
			collect_ImageButton.render();
		}
		if (renderMode == 1.1f) {
			sheepitSettingsScreen.render();
		}
		if (renderMode == 1) {
			renderOnSheepitScreen.render();
			startSheepit();
		}
		if (renderMode == 2) {
			imageViewScreen.render();
		}

		if (anyFileExplorerIsOpen == false) {
			cancelRendering_ImageButton.render();
		}
		imageView_imageButton.getHoverText().render();
		sleep_ImageButton.getHoverText().render();
		cancelRendering_ImageButton.getHoverText().render();
		collect_ImageButton.getHoverText().render();
		// render all ---------------------------------------

		// button handling -------------------------
		if (cancelRendering_ImageButton.getIsClicked()) {
			if (renderMode == 0.1f) {
				mainActivity.setMode(1);
			}
			if (renderMode == 0) {
				filesRenderingScreen.setIsRendering(false);
				mainActivity.setMode(1);
			}
			if (renderMode == 1.1f) {
				mainActivity.setMode(1);
			}
			if (renderMode == 1) {
				mainActivity.getRenderOverview().getRenderOnSheepitScreen().getSheepitRenderHelper().setStartRenderingOnSheepit(false);
				mainActivity.setMode(1);
			}
			if (renderMode == 2) {
				renderMode = 0;
			}

			cancelRendering_ImageButton.setIsClicked(false);
		}
		
		if (sleep_ImageButton.getIsClicked()) {
			if (renderMode == 0) {
				filesRenderingScreen.setIsRendering(!filesRenderingScreen.getRenderHelper().getStartRenderingFromJson());
			}
			sleep_ImageButton.setIsClicked(false);
		}
		if(collect_ImageButton.getIsClicked()) {
			filesRenderingScreen.collectImages();
			collect_ImageButton.setIsClicked(false);
		}

		if (renderMode == 0) {
			if (imageView_imageButton.getIsClicked()) {
				renderMode = 2;
				String setPath = "";
				if (filesSettingsScreen.getImageSavePath_pathSelector().getPath().length() > 0) {
					setPath = filesSettingsScreen.getImageSavePath_pathSelector().getPath();
				} else {
					setPath = mainActivity.getPathToImageFolder();
				}
				if (setPath.length() > 0) {
					imageViewScreen.setPath(setPath);
				}
				imageView_imageButton.setIsClicked(false);
			}
		}
		// button handling -------------------------
	}

	public void startSheepit() {

	}

	public void saveHardwareToUse(ArrayList<Node> allConnectedNodes) {
		// save and create hardwareToUseArray------------------------------
		JSONArray hardwareToUseArray = new JSONArray();
		for (int i = 0; i < allConnectedNodes.size(); i++) {
			Node n = allConnectedNodes.get(i);
			JSONObject hardwareDetails = new JSONObject();
			String curPCName = n.getPcSelection_DropdownMenu().getSelectedItem();
			Boolean useCpu = false, useGPU = false;
			Boolean[] hwToUse = mainActivity.getHardwareToRenderWith(curPCName, false);
			if (hwToUse[0] && mainActivity.getHomeScreenMaster().getCheckboxes()[4].getIsChecked()) {
				useCpu = true;
			}
			if (hwToUse[1] && mainActivity.getHomeScreenMaster().getCheckboxes()[5].getIsChecked()) {
				useGPU = true;
			}
			hardwareDetails.put("useCPU", p.str(useCpu));
			hardwareDetails.put("useGPU", p.str(useGPU));
			hardwareDetails.put("pcName", curPCName);

			hardwareToUseArray.add(hardwareDetails);
		}
		jsonHelper.clearArray();
		jsonHelper.setArray(hardwareToUseArray);
		jsonHelper.writeData(mainActivity.getHardwareToUseFilePath());
		// save and create hardwareToUseArray------------------------------
	}

	public void onMousePressed(int mouseButton) {
		if (renderMode == 0.1f) {
			filesSettingsScreen.onMousePressed(mouseButton);
		}
		if (renderMode == 0) {
			filesRenderingScreen.onMousePressed(mouseButton);
			imageView_imageButton.onMousePressed();
			sleep_ImageButton.onMousePressed();
			collect_ImageButton.onMousePressed();
		}
		if (renderMode == 1.1f) {
			sheepitSettingsScreen.onMousePressed(mouseButton);
		}
		if (renderMode == 1) {
			renderOnSheepitScreen.onMousePressed(mouseButton);
		}
		if (renderMode == 2) {
			imageViewScreen.onMousePressed(mouseButton);
		}
		if (anyFileExplorerIsOpen == false) {
			cancelRendering_ImageButton.onMousePressed();
		}
	}

	public void onMouseReleased(int mouseButton) {

		if (renderMode == 0.1f) {
			filesSettingsScreen.onMouseReleased(mouseButton);
		}
		if (renderMode == 0) {
			filesRenderingScreen.onMouseReleased(mouseButton);
			imageView_imageButton.onMouseReleased();
			sleep_ImageButton.onMouseReleased();
			collect_ImageButton.onMouseReleased();
		}
		if (renderMode == 1.1f) {
			sheepitSettingsScreen.onMouseReleased(mouseButton);
		}
		if (renderMode == 1) {
			renderOnSheepitScreen.onMouseReleased(mouseButton);
		}
		if (renderMode == 2) {
			imageViewScreen.onMouseReleased(mouseButton);
		}
		if (anyFileExplorerIsOpen == false) {
			cancelRendering_ImageButton.onMouseReleased();
		}
	}

	public void onKeyPressed(char key) {
		if (renderMode == 0.1f) {
			filesSettingsScreen.onKeyPressed(key);
		}
		if (renderMode == 0) {
			filesRenderingScreen.onKeyPressed(key);
		}
		if (renderMode == 1.1f) {
			sheepitSettingsScreen.onKeyPressed(key);
		}
		if (renderMode == 1) {
			renderOnSheepitScreen.onKeyPressed(key);
		}
		if (renderMode == 2) {
			imageViewScreen.onKeyPressed(key);
		}

	}

	public void onKeyReleased(char key) {
		if (renderMode == 0.1f) {
			filesSettingsScreen.onKeyReleased(key);
		}
		if (renderMode == 0) {
			filesRenderingScreen.onKeyReleased(key);
		}
		if (renderMode == 1.1f) {
			sheepitSettingsScreen.onKeyReleased(key);
		}
		if (renderMode == 1) {
			renderOnSheepitScreen.onKeyReleased(key);
		}
		if (renderMode == 2) {
			imageViewScreen.onKeyReleased(key);
		}
	}

	public void onScroll(float e) {
		if (renderMode == 0.1f) {
			filesSettingsScreen.onScroll(e);
		}
		if (renderMode == 0) {
			filesRenderingScreen.onScroll(e);
		}
		if (renderMode == 1) {
			renderOnSheepitScreen.onScroll(e);
		}
		if (renderMode == 2) {
			imageViewScreen.onScroll(e);
		}
	}

	public int getMode() {
		return mode;
	}

	public ImageButton getCancelImageButton() {
		return cancelRendering_ImageButton;
	}

	public ImageButton getImageViewImageButton() {
		return imageView_imageButton;
	}

	public ImageButton getSleepImageButton() {
		return sleep_ImageButton;
	}

	public FilesSettingsScreen getRenderFilesSettings() {
		return filesSettingsScreen;
	}

	public FilesRenderingScreen getFilesRenderingScreen() {
		return filesRenderingScreen;
	}

	public SheepitSettingsScreen getSheepitSettingsScreen() {
		return sheepitSettingsScreen;
	}

	public RenderOnSheepitScreen getRenderOnSheepitScreen() {
		return renderOnSheepitScreen;
	}

	public void setRenderMode(float setM) {
		renderMode = setM;
	}

	public void setFileList(String[] l) {
		filesSettingsScreen.setFileList(l);
		filesRenderingScreen.setFileList(l);
	}
}
