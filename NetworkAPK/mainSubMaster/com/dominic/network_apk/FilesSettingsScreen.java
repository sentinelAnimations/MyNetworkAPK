package com.dominic.network_apk;

import java.awt.print.Book;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool.ManagedBlocker;

import processing.core.PApplet;
import processing.core.PFont;

public class FilesSettingsScreen {

	private int stdTs, edgeRad, margin, btnSize, btnSizeSmall, dark, light, lighter, lightest, textCol, textDark, border, onceOnStartup = 0, prevSelectedListInd;
	private float textYShift;
	private Boolean[] useNewResolution, renderAnimation, renderStillFrame;
	private int[] startFrame, endFrame, stillFrame, resX, resY, samples;
	private String[] pictoPaths, imageSavePaths;
	private PFont stdFont;
	private PApplet p;
	private MainActivity mainActivity;
	private PathSelector imageSavePath_pathSelector;
	private ImageButton startRendering_imageButon;
	private HorizontalList allFiles_HorizontalList;
	private CounterArea startFrame_counterArea, endFrame_counterArea, stillFrame_counterArea, resX_counterArea, resY_counterArea, sampling_counterArea;
	private Checkbox[] settings_checkboxes = new Checkbox[3];
	private ArrayList<MakeToast> makeToasts = new ArrayList<MakeToast>();

	public FilesSettingsScreen(PApplet p, int stdTs, int edgeRad, int margin, int btnSize, int btnSizeSmall, int dark, int light, int lighter, int textCol, int textDark, int border, float textYShift, String[] pictoPaths, String[] hoLiPictoPaths, String[] arrowPaths, String[] fileExplorerPaths, PFont stdFont) {
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
		this.pictoPaths = pictoPaths;
		this.stdFont = stdFont;
		mainActivity = (MainActivity) p;
		String[] startList = {};

		int colW = p.width / 4 * 3;
		int rowH = btnSizeSmall;
		int rowDist = rowH + margin;
		int startY = rowDist / 2 + ((p.height - (7 * rowDist)) / 2);

		allFiles_HorizontalList = new HorizontalList(p, p.width / 2, startY - margin, colW, rowH + margin * 2, margin, edgeRad, stdTs, (int) p.textWidth("Files to render") + margin * 3 + btnSizeSmall, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border, textYShift, '\\', false, true, false, "Files to render", hoLiPictoPaths, startList, stdFont, null);
		imageSavePath_pathSelector = new PathSelector(p, p.width / 2, startY + 2 * rowDist, colW, rowH, edgeRad, margin, stdTs, btnSizeSmall, border, light, textCol, dark, light, lighter, textDark, textYShift, true, false, "select Folder to save Results", pictoPaths[1], fileExplorerPaths, stdFont, null);
		sampling_counterArea = new CounterArea(p, p.width / 2, startY + 3 * rowDist, colW, rowH, edgeRad, margin, stdTs, 2, 1000000000, 0, light, lighter, textCol, textYShift, false, "Sampling", arrowPaths, stdFont, null);
		String[] infoTexts = { "Use Resolution", "Render Animation", "renderStillFrame" };
		Boolean[] hasThreeOptions= {false,false,false};
		for (int i = 0; i < settings_checkboxes.length; i++) {
			settings_checkboxes[i] = new Checkbox(p, p.width / 2 - colW / 2 + rowH / 2 - margin, startY + 4 * rowDist + i * rowDist, rowH, rowH, rowH, edgeRad, margin, stdTs, light, light, border, textCol, textYShift, false, false,hasThreeOptions[i], "", infoTexts[i], stdFont, null);
		}

		int sfW = (colW - rowH - margin * 2) / 2;
		int sfX = (int) (settings_checkboxes[0].getBoxDim() / 2 + margin * 2.5f + sfW / 2);
		resX_counterArea = new CounterArea(p, sfX, 0, sfW, rowH, edgeRad, margin, stdTs, 4, 1000000000, 0, light, lighter, textCol, textYShift, true, "Resoulution X", arrowPaths, stdFont, settings_checkboxes[0]);
		sfX = (int) (settings_checkboxes[0].getBoxDim() / 2 + margin * 3.5f + sfW * 1.5f);
		resY_counterArea = new CounterArea(p, sfX, 0, sfW, rowH, edgeRad, margin, stdTs, 4, 1000000000, 0, light, lighter, textCol, textYShift, true, "Resoulution X", arrowPaths, stdFont, settings_checkboxes[0]);

		sfX = (int) (settings_checkboxes[0].getBoxDim() / 2 + margin * 2.5 + sfW / 2);
		startFrame_counterArea = new CounterArea(p, sfX, 0, sfW, rowH, edgeRad, margin, stdTs, 0, 1000000000, 0, light, lighter, textCol, textYShift, true, "Startframe", arrowPaths, stdFont, settings_checkboxes[1]);
		sfX = (int) (settings_checkboxes[0].getBoxDim() / 2 + margin * 3.5 + sfW * 1.5f);
		endFrame_counterArea = new CounterArea(p, sfX, 0, sfW, rowH, edgeRad, margin, stdTs, 0, 1000000000, 0, light, lighter, textCol, textYShift, true, "Endframe", arrowPaths, stdFont, settings_checkboxes[1]);
		endFrame_counterArea.getParentPos();

		sfW = colW - rowH - margin;
		sfX = (int) (settings_checkboxes[0].getBoxDim() / 2 + margin * 2 + sfW / 2);
		stillFrame_counterArea = new CounterArea(p, sfX, 0, sfW, rowH, edgeRad, margin, stdTs, 0, 1000000000, 0, light, lighter, textCol, textYShift, true, "Still frame", arrowPaths, stdFont, settings_checkboxes[2]);

		prevSelectedListInd = allFiles_HorizontalList.getSelectedInd();

	}

	public void render() {

		if (onceOnStartup == 0) {
			startRendering_imageButon = new ImageButton(p, -margin - btnSizeSmall, 0, btnSizeSmall, btnSizeSmall, stdTs, margin, edgeRad, -1, textYShift, true, true, textCol, light, pictoPaths[0], "Save settings and start render process", mainActivity.getRenderOverview().getCancelImageButton());
			onceOnStartup++;
		}

		if (imageSavePath_pathSelector.getFileExplorerIsOpen() == false) {
			if (allFiles_HorizontalList.getList().length > 0) {

				// render all ----------------------------------------

				int fillUpBarW = allFiles_HorizontalList.getW();
				int fillUpBarY = allFiles_HorizontalList.getY() + allFiles_HorizontalList.getH();
				p.fill(light);
				p.stroke(light);
				p.rect(p.width / 2, fillUpBarY, fillUpBarW, imageSavePath_pathSelector.getH(), edgeRad);

				p.fill(textCol);
				p.textFont(stdFont);
				p.textSize(stdTs);
				p.textAlign(p.CENTER, p.CENTER);
				String selectedPath = allFiles_HorizontalList.getList()[allFiles_HorizontalList.getSelectedInd()];
				try {
					String fileName = new File(selectedPath).getPath();
					p.text("Render settings for: " + fileName, p.width / 2, fillUpBarY);
				} catch (Exception e) {
					e.printStackTrace();
				}
				stillFrame_counterArea.render();
				allFiles_HorizontalList.render();
				startRendering_imageButon.render();
				endFrame_counterArea.render();
				startFrame_counterArea.render();
				sampling_counterArea.render();
				resX_counterArea.render();
				resY_counterArea.render();
				for (int i = 0; i < settings_checkboxes.length; i++) {
					settings_checkboxes[i].render();
				}
				for (int i = 0; i < settings_checkboxes.length; i++) {
					settings_checkboxes[i].getHoverText().render();
				}
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
				// render all ----------------------------------------

				// calculate all ----------------------------------------
				if (allFiles_HorizontalList.getSelectedInd() != prevSelectedListInd) {

					writeToArray();
					updateWidgets();

				}
			}
			if (startRendering_imageButon.getIsClicked()) {
				writeToArray();

				Boolean isRenderable = true;
				String errorMessage = "";

				for (int i = 0; i < renderAnimation.length; i++) {

					if (renderAnimation[i] == true && renderStillFrame[i] == true) {
						if (errorMessage.length() > 0) {
							errorMessage += " | ";
						}
						errorMessage += "Can't render still frame AND animation";

						isRenderable = false;
					}

					if (renderAnimation[i] == false && renderStillFrame[i] == false) {
						if (errorMessage.length() > 0) {
							errorMessage += " | ";
						}
						errorMessage += "Select either animation or still frame";

						isRenderable = false;
					}
					if (renderAnimation[i] == true) {
						if (endFrame[i] < startFrame[i]) {
							if (errorMessage.length() > 0) {
								errorMessage += " | ";
							}
							isRenderable = false;
							errorMessage += "Can't render negative frame range";
						}
					}
				}

				if (isRenderable) {
					mainActivity.getRenderOverview().setRenderMode(0);
					mainActivity.getRenderOverview().getFilesRenderingScreen().setStartupVals();
					mainActivity.getRenderOverview().getFilesRenderingScreen().startFileRendering();
				} else {
					makeToasts.add(new MakeToast(p, p.width / 2, p.height - stdTs * 2, stdTs, margin, edgeRad, errorMessage.length() * 2, light, textCol, textYShift, false, errorMessage, stdFont, null));
				}
				startRendering_imageButon.setIsClicked(false);

			}

			prevSelectedListInd = allFiles_HorizontalList.getSelectedInd();
			// calculate all ----------------------------------------
		}
		imageSavePath_pathSelector.render();
	}

	private void writeToArray() {
		try {
			imageSavePaths[prevSelectedListInd] = imageSavePath_pathSelector.getPath();
			renderAnimation[prevSelectedListInd] = settings_checkboxes[1].getIsChecked();
			renderStillFrame[prevSelectedListInd] = settings_checkboxes[2].getIsChecked();
			useNewResolution[prevSelectedListInd] = settings_checkboxes[0].getIsChecked();
			startFrame[prevSelectedListInd] = startFrame_counterArea.getCount();
			endFrame[prevSelectedListInd] = endFrame_counterArea.getCount();
			stillFrame[prevSelectedListInd] = stillFrame_counterArea.getCount();
			resX[prevSelectedListInd] = resX_counterArea.getCount();
			resY[prevSelectedListInd] = resY_counterArea.getCount();
			samples[prevSelectedListInd] = sampling_counterArea.getCount();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateWidgets() {
		int curInd = allFiles_HorizontalList.getSelectedInd();
		imageSavePath_pathSelector.setPath(imageSavePaths[curInd],false);
		settings_checkboxes[0].setIsChecked(useNewResolution[curInd]);
		settings_checkboxes[1].setIsChecked(renderAnimation[curInd]);
		settings_checkboxes[2].setIsChecked(renderStillFrame[curInd]);
		startFrame_counterArea.setCount(startFrame[curInd]);
		endFrame_counterArea.setCount(endFrame[curInd]);
		stillFrame_counterArea.setCount(stillFrame[curInd]);
		resX_counterArea.setCount(resX[curInd]);
		resY_counterArea.setCount(resY[curInd]);
		sampling_counterArea.setCount(samples[curInd]);
	}

	public void onMousePressed(int mouseButton) {
		imageSavePath_pathSelector.onMousePressed(mouseButton);
		if (imageSavePath_pathSelector.getFileExplorerIsOpen() == false) {
			allFiles_HorizontalList.onMousePressed();
			startRendering_imageButon.onMousePressed();
			startFrame_counterArea.onMousePressed();
			endFrame_counterArea.onMousePressed();
			stillFrame_counterArea.onMousePressed();
			resX_counterArea.onMousePressed();
			resY_counterArea.onMousePressed();
			sampling_counterArea.onMousePressed();
		}
	}

	public void onMouseReleased(int mouseButton) {
		imageSavePath_pathSelector.onMouseReleased(mouseButton);
		if (imageSavePath_pathSelector.getFileExplorerIsOpen() == false) {
			allFiles_HorizontalList.onMouseReleased(mouseButton);
			startRendering_imageButon.onMouseReleased();
			startFrame_counterArea.onMouseReleased();
			endFrame_counterArea.onMouseReleased();
			stillFrame_counterArea.onMouseReleased();
			resX_counterArea.onMouseReleased();
			resY_counterArea.onMouseReleased();
			sampling_counterArea.onMouseReleased();
			for (int i = 0; i < settings_checkboxes.length; i++) {
				settings_checkboxes[i].onMouseReleased();
			}

		}
	}

	public void onKeyPressed(char key) {
		imageSavePath_pathSelector.onKeyPressed(key);
	}

	public void onKeyReleased(char key) {
		imageSavePath_pathSelector.onKeyReleased(key);
		if (imageSavePath_pathSelector.getFileExplorerIsOpen() == false) {
			if (key == p.DELETE) {
				String[] hoLi1 = allFiles_HorizontalList.getList();
				String[] newHoLi1 = new String[hoLi1.length - 1];
				String[] newImageSavePaths = new String[imageSavePaths.length - 1];
				Boolean[] newUseNewResolution=new Boolean[useNewResolution.length-1]; 
				Boolean[] newRenderAnimation = new Boolean[renderAnimation.length - 1];
				Boolean[] newRenderStillFrame = new Boolean[renderStillFrame.length - 1];
				int[] newStartFrame = new int[startFrame.length - 1];
				int[] newEndFrame = new int[endFrame.length - 1];
				int[] newStillFrame = new int[stillFrame.length - 1];
				int[] newResX=new int[resX.length-1];
				int[] newResY=new int[resY.length-1];
				int[] newSamples= new int[samples.length-1];

				for (int i = 0; i < hoLi1.length; i++) {
					if (i < allFiles_HorizontalList.getSelectedInd()) {
						newImageSavePaths[i] = imageSavePaths[i];
						newHoLi1[i] = hoLi1[i];
						newUseNewResolution[i]=useNewResolution[i];
						newRenderAnimation[i] = renderAnimation[i];
						newRenderStillFrame[i] = renderStillFrame[i];
						newStartFrame[i] = startFrame[i];
						newEndFrame[i] = endFrame[i];
						newStillFrame[i] = stillFrame[i];
						newResX[i]=resX[i];
						newResY[i]=resY[i];
						newSamples[i]=samples[i];

					}
					if (i > allFiles_HorizontalList.getSelectedInd()) {
						newImageSavePaths[i - 1] = imageSavePaths[i];
						newHoLi1[i - 1] = hoLi1[i];
						newUseNewResolution[i-1]=useNewResolution[i];
						newRenderAnimation[i - 1] = renderAnimation[i];
						newRenderStillFrame[i - 1] = renderStillFrame[i];
						newStartFrame[i - 1] = startFrame[i];
						newEndFrame[i - 1] = endFrame[i];
						newStillFrame[i - 1] = stillFrame[i];
						newResX[i-1]=resX[i];
						newResY[i-1]=resY[i];
						newSamples[i-1]=samples[i];
					}
				}
				imageSavePaths = new String[imageSavePaths.length - 1];
				useNewResolution=new Boolean[useNewResolution.length-1];
				renderAnimation = new Boolean[renderAnimation.length - 1];
				renderStillFrame = new Boolean[renderStillFrame.length - 1];
				startFrame = new int[startFrame.length - 1];
				endFrame = new int[endFrame.length];
				stillFrame = new int[stillFrame.length];
				resX=new int[resX.length-1];
				resY=new int[resY.length-1];
				samples=new int[samples.length-1];
				
				imageSavePaths=newImageSavePaths;
				useNewResolution=newUseNewResolution;
				renderAnimation = newRenderAnimation;
				renderStillFrame = newRenderStillFrame;
				startFrame = newStartFrame;
				endFrame = newEndFrame;
				stillFrame = newStillFrame;
				resX=newResX;
				resY=newResY;
				samples=newSamples;
				
				if (newHoLi1.length > 0) {
					allFiles_HorizontalList.setList(newHoLi1);
					prevSelectedListInd = allFiles_HorizontalList.getSelectedInd();
					updateWidgets();
				} else {
					mainActivity.setMode(mainActivity.getHomeScreenMaster().getMode());
				}
			}
		}
	}

	public void onScroll(float e) {
		imageSavePath_pathSelector.onScroll(e);
		if (imageSavePath_pathSelector.getFileExplorerIsOpen() == false) {
			allFiles_HorizontalList.onScroll(e);
			startFrame_counterArea.onScroll(e);
			endFrame_counterArea.onScroll(e);
			stillFrame_counterArea.onScroll(e);
			resX_counterArea.onScroll(e);
			resY_counterArea.onScroll(e);
			sampling_counterArea.onScroll(e);
		}

	}
	
	public Boolean[] getRenderAnimation() {
		return renderAnimation;
	}

	public Boolean[] getRenderStillFrame() {
		return renderStillFrame;
	}

	public Boolean[] getUseNewResolution() {
		return useNewResolution;
	}

	public int[] getStartFrames() {
		return startFrame;
	}

	public int[] getEndFrames() {
		return endFrame;
	}

	public int[] getStillFrames() {
		return stillFrame;
	}

	public int[] getResX() {
		return resX;
	}

	public int[] getResY() {
		return resY;
	}

	public int[] getSamples() {
		return samples;
	}
	public String[] getImageSavePaths() {
		return imageSavePaths;
	}
	public HorizontalList getHorizontalList() {
		return allFiles_HorizontalList;
	}

	public PathSelector getImageSavePath_pathSelector() {
		return imageSavePath_pathSelector;
	}

	public void setFileList(String[] l) {
		allFiles_HorizontalList.setList(l);
	}

	public void setStartupVals() {
		imageSavePath_pathSelector.setPath(mainActivity.getHomeScreenMaster().getImageSavePath_pathSelector().getPath(),false);

		settings_checkboxes[0].setIsChecked(mainActivity.getHomeScreenMaster().getCheckboxes()[8].getIsChecked()); // useNewResolution
		settings_checkboxes[1].setIsChecked(mainActivity.getHomeScreenMaster().getCheckboxes()[6].getIsChecked());// renderAnimation
		settings_checkboxes[2].setIsChecked(mainActivity.getHomeScreenMaster().getCheckboxes()[7].getIsChecked());// renderStillFrame
		
		startFrame_counterArea.setCount(mainActivity.getHomeScreenMaster().getStartFrame_CounterArea().getCount());
		endFrame_counterArea.setCount(mainActivity.getHomeScreenMaster().getEndFrame_CounterArea().getCount());
		stillFrame_counterArea.setCount(mainActivity.getHomeScreenMaster().getStillFrame_counterArea().getCount());
		resX_counterArea.setCount(mainActivity.getHomeScreenMaster().getResX_counterArea().getCount());
		resY_counterArea.setCount(mainActivity.getHomeScreenMaster().getResY_counterArea().getCount());
		sampling_counterArea.setCount(mainActivity.getHomeScreenMaster().getSamples_counterArea().getCount());

		imageSavePaths = new String[allFiles_HorizontalList.getList().length];
		useNewResolution = new Boolean[allFiles_HorizontalList.getList().length];
		renderAnimation = new Boolean[allFiles_HorizontalList.getList().length];
		renderStillFrame = new Boolean[allFiles_HorizontalList.getList().length];
		startFrame = new int[allFiles_HorizontalList.getList().length];
		endFrame = new int[allFiles_HorizontalList.getList().length];
		stillFrame = new int[allFiles_HorizontalList.getList().length];
		resX = new int[allFiles_HorizontalList.getList().length];
		resY = new int[allFiles_HorizontalList.getList().length];
		samples = new int[allFiles_HorizontalList.getList().length];

		for (int i = 0; i < renderAnimation.length; i++) {
			imageSavePaths[i] = mainActivity.getHomeScreenMaster().getImageSavePath_pathSelector().getPath(); // to do for update & write
			useNewResolution[i] = mainActivity.getHomeScreenMaster().getCheckboxes()[8].getIsChecked();
			renderAnimation[i] = mainActivity.getHomeScreenMaster().getCheckboxes()[6].getIsChecked();
			renderStillFrame[i] = mainActivity.getHomeScreenMaster().getCheckboxes()[7].getIsChecked();
			startFrame[i] = mainActivity.getHomeScreenMaster().getStartFrame_CounterArea().getCount();
			endFrame[i] = mainActivity.getHomeScreenMaster().getEndFrame_CounterArea().getCount();
			stillFrame[i] = mainActivity.getHomeScreenMaster().getStillFrame_counterArea().getCount();
			resX[i] = mainActivity.getHomeScreenMaster().getResX_counterArea().getCount();
			resY[i] = mainActivity.getHomeScreenMaster().getResY_counterArea().getCount();
			samples[i] = mainActivity.getHomeScreenMaster().getSamples_counterArea().getCount();
		}
	}
}
