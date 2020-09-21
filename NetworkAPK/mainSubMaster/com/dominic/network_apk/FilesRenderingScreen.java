package com.dominic.network_apk;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;

public class FilesRenderingScreen {

	private int stdTs, edgeRad, margin, btnSize, btnSizeSmall, dark, light, green, red, blue, lighter, lightest, textCol, textDark, border, curRenderingFile = 1, prevPCListSelectedInd = -1, onStartup = 0;
	private float textYShift;
	private float[] listX, listW, allFiles_listX, allFiles_listW;
	private Boolean isFreezed=false;
	private Boolean[] renderAnimation, renderStillFrame, startedRenderingTiles;
	private int[] startFrame, endFrame, stillFrame, allPCStatus; // allPCStatus: 0=prog responding, 1=prog not responding
	private String[] pictoPaths, hoLiPictoPaths;
	private String[] allPCNames, allLastLogLines, allRenderInfos;
	private PFont stdFont;
	private PApplet p;
	private MainActivity mainActivity;
	private PictogramImage[] allPCPictos;
	private ArrayList<Node> allConnectedNodes = new ArrayList<>();
	private Loadingbar[] allPCLoadingbars;
	private HorizontalList allFiles_HorizontalList, allPCs_HorizontalList;
	private LogBar logBar;
	private ImageButton freeze_imageButton;

	public FilesRenderingScreen(PApplet p, int stdTs, int edgeRad, int margin, int btnSize, int btnSizeSmall, int dark, int light, int lighter, int textCol, int textDark, int border, int green, int red, int blue, float textYShift, String[] pictoPaths, String[] hoLiPictoPaths, PFont stdFont) {
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
		this.green = green;
		this.red = red;
		this.blue = blue;
		this.textYShift = textYShift;
		this.pictoPaths = pictoPaths;
		this.hoLiPictoPaths = hoLiPictoPaths;
		this.stdFont = stdFont;
		mainActivity = (MainActivity) p;

		setupAll();
	}

	public void render() {

		if (onStartup == 0) {
			updateLists();
			onStartup++;
		}
		// render all ----------------------------------------------
		allFiles_HorizontalList.render();
		// allFiles_horizontallist ------------------------
		if (allFiles_HorizontalList.getList().length > 0) {
			if (allFiles_HorizontalList.getIsShifted()) {
				allFiles_listX = allFiles_HorizontalList.getListX();
				allFiles_listW = allFiles_HorizontalList.getListW();
				allFiles_HorizontalList.setIsShifted(false);
			}
			for (int i = allFiles_HorizontalList.getFirstDisplayedInd(); i <= allFiles_HorizontalList.getLastDisplayedInd(); i++) {
				p.noFill();
				if (i < curRenderingFile) {
					p.stroke(green);
				} else {
					if (i == curRenderingFile) {
						p.stroke(blue);
					} else {
						p.stroke(red);
					}
				}
				if (allFiles_HorizontalList.getSelectedInd() == i) {
					p.stroke(255);
				}
				p.rect(allFiles_listX[i], allFiles_HorizontalList.getY(), allFiles_listW[i], allFiles_HorizontalList.getH() - margin * 2, edgeRad);
			}
		}
		// allFiles_horizontallist ------------------------

		logBar.render();
		freeze_imageButton.render();
		// render allPCs_horizontalList --------------------
		allPCs_HorizontalList.render();
		if (allPCs_HorizontalList.getList().length > 0) {
			if (allPCs_HorizontalList.getIsShifted()) {

				listX = new float[allPCs_HorizontalList.getListX().length];
				listW = new float[allPCs_HorizontalList.getListW().length];
				listX = allPCs_HorizontalList.getListX();
				listW = allPCs_HorizontalList.getListW();

				for (int i = 0; i < allPCLoadingbars.length; i++) {
					allPCLoadingbars[i].setPos((int) listX[i], (int) (allPCs_HorizontalList.getY() + listW[i] / 2 - margin * 3 - allPCLoadingbars[i].getH() / 2));
					allPCPictos[i].setPos((int) listX[i], (int) (allPCs_HorizontalList.getY() - listW[i] / 2 + margin * 2 + allPCPictos[i].getH() / 2));
				}
				allPCs_HorizontalList.setIsShifted(false);
			}
			for (int i = allPCs_HorizontalList.getFirstDisplayedInd(); i <= allPCs_HorizontalList.getLastDisplayedInd(); i++) {
				p.fill(lighter);
				if (allPCs_HorizontalList.getSelectedInd() == i) {
					p.stroke(border);
				} else {
					p.stroke(lighter);
				}
				p.rect(listX[i], allPCs_HorizontalList.getY(), listW[i], allPCs_HorizontalList.getH() - margin * 2, edgeRad);
				allPCLoadingbars[i].render();
				allPCPictos[i].render();

				p.fill(textCol);
				p.textFont(stdFont);
				p.textSize(stdTs);
				p.textAlign(p.CENTER, p.TOP);
				try {
					p.text(allRenderInfos[i], listX[i], allPCPictos[i].getY() + allPCPictos[i].getH() / 2 + margin - stdTs / 2);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// render allPCs_horizontalList --------------------
		// render all ----------------------------------------------
		
		if(freeze_imageButton.getIsClicked()) {
		    isFreezed=!isFreezed;
		    freeze_imageButton.setIsClicked(false);
		}
		
		//get loglines -----------------------------------
		if(!isFreezed) {
		    //to do: get logLines
		}
        //get loglines -----------------------------------

		// update PCView---------------------------------------------
		if (allConnectedNodes.size() > 0) {
			if (allPCs_HorizontalList.getSelectedInd() != prevPCListSelectedInd) {
				logBar.setText(allLastLogLines[allPCs_HorizontalList.getSelectedInd()]);
			}
			if (p.frameCount % 30 == 0) {
				updateLists();
			}
		}
		// update PCView---------------------------------------------

		prevPCListSelectedInd = allPCs_HorizontalList.getSelectedInd();
	}

	private void updateLists() {

		for (int i = 0; i < allPCs_HorizontalList.getList().length; i++) {
			// check render status if ok, update last log line -------------------

			// check render status if ok, update last log line -------------------

			// prepare infoString and so on-----------------------------------
			String[] splitStr = p.split(allLastLogLines[i], "|");
			String renderInfoString = "PC name: " + allPCNames[i] + "\n";
			for (int i2 = 0; i2 < splitStr.length; i2++) {
				if (i2 == 0) {
					String[] splStr = p.split(splitStr[i2], " ");

					renderInfoString += splStr[0] + "\n";
				} else {
					renderInfoString += splitStr[i2] + "\n";
				}
			}
			allRenderInfos[i] = renderInfoString;
			String[] m1 = p.match(splitStr[splitStr.length - 1], "Tiles");
			if (m1 != null) {
				String[] splitStr2 = p.split(splitStr[splitStr.length - 1], " ");
				String[] splitStr3 = p.split(splitStr2[2], "/");
				if (splitStr3.length == 2) {
					if (startedRenderingTiles[i] == null) {
						allPCLoadingbars[i].setMin(0);
						allPCLoadingbars[i].setMax(Integer.parseInt(splitStr3[1]));
					}
					allPCLoadingbars[i].setValue(Integer.parseInt(splitStr3[0]));
					startedRenderingTiles[i] = true;
				}

			}
			// prepare infoString and so on-----------------------------------

		}
	}

	public void onMousePressed(int mouseButton) {
		allFiles_HorizontalList.onMousePressed();
		allPCs_HorizontalList.onMousePressed();
		freeze_imageButton.onMousePressed();
	}

	public void onMouseReleased(int mouseButton) {
		allFiles_HorizontalList.onMouseReleased(mouseButton);
		allPCs_HorizontalList.onMouseReleased(mouseButton);
		freeze_imageButton.onMouseReleased();
	}

	public void onKeyPressed(char key) {

	}

	public void onKeyReleased(char key) {
		if (curRenderingFile < allFiles_HorizontalList.getSelectedInd()) {
			if (key == p.DELETE) {
				String[] hoLi1 = allFiles_HorizontalList.getList();
				String[] newHoLi1 = new String[hoLi1.length - 1];
				Boolean[] newRenderAnimation = new Boolean[renderAnimation.length - 1];
				Boolean[] newRenderStillFrame = new Boolean[renderStillFrame.length - 1];
				int[] newStartFrame = new int[startFrame.length - 1];
				int[] newEndFrame = new int[endFrame.length - 1];
				int[] newStillFrame = new int[stillFrame.length - 1];

				for (int i = 0; i < hoLi1.length; i++) {
					if (i < allFiles_HorizontalList.getSelectedInd()) {
						newHoLi1[i] = hoLi1[i];
						newRenderAnimation[i] = renderAnimation[i];
						newRenderStillFrame[i] = renderStillFrame[i];
						newStartFrame[i] = startFrame[i];
						newEndFrame[i] = endFrame[i];
						newStillFrame[i] = stillFrame[i];

					}
					if (i > allFiles_HorizontalList.getSelectedInd()) {
						newHoLi1[i - 1] = hoLi1[i];
						newRenderAnimation[i - 1] = renderAnimation[i];
						newRenderStillFrame[i - 1] = renderStillFrame[i];
						newStartFrame[i - 1] = startFrame[i];
						newEndFrame[i - 1] = endFrame[i];
						newStillFrame[i - 1] = stillFrame[i];
					}
				}
				renderAnimation = new Boolean[renderAnimation.length - 1];
				renderStillFrame = new Boolean[renderStillFrame.length - 1];
				startFrame = new int[startFrame.length - 1];
				endFrame = new int[endFrame.length - 1];
				stillFrame = new int[stillFrame.length - 1];

				renderAnimation = newRenderAnimation;
				renderStillFrame = newRenderStillFrame;
				startFrame = newStartFrame;
				endFrame = newEndFrame;
				stillFrame = newStillFrame;

				allFiles_HorizontalList.setList(newHoLi1);
			}
		}
	}

	public void onScroll(float e) {
		allFiles_HorizontalList.onScroll(e);
		allPCs_HorizontalList.onScroll(e);
	}

	public HorizontalList getHorizontalList() {
		return allFiles_HorizontalList;
	}

	public void setFileList(String[] l) {
		allFiles_HorizontalList.setList(l);
	}

	public void setStartupVals() {
		renderAnimation = mainActivity.getRenderOverview().getRenderFilesSettings().getRenderAnimation();
		renderStillFrame = mainActivity.getRenderOverview().getRenderFilesSettings().getRenderStillFrame();
		startFrame = mainActivity.getRenderOverview().getRenderFilesSettings().getStartFrames();
		endFrame = mainActivity.getRenderOverview().getRenderFilesSettings().getEndFrames();
		stillFrame = mainActivity.getRenderOverview().getRenderFilesSettings().getStilFrames();
	}

	public void setupAll() {
		int listH = btnSize * 4;
		String listW = "";
		while (p.textWidth(listW) < listH) {
			listW += ".";
		}
		if (!mainActivity.getNodeEditor().getIsSetup()) {
			mainActivity.getNodeEditor().setupAll();
			// mainActivity.getNodeEditor().handleNodes();
			for (int i = 0; i < mainActivity.getNodeEditor().getNodes().size(); i++) {
				Node n = (Node) mainActivity.getNodeEditor().getNodes().get(i);
				if (n.getType() == 3) {
					n.updateSwitchConnectorPoints(false, null, true);
				}
			}

			mainActivity.getNodeEditor().calcConnectedNodes();
		} else {
			allConnectedNodes = mainActivity.getNodeEditor().getAllConnectedNodes();
		}

		String[] startList = {};
		allFiles_HorizontalList = new HorizontalList(p, p.width / 2, (p.height - btnSizeSmall * 2 - listH - margin * 2) / 2 + btnSizeSmall / 2, p.width - margin * 2, btnSizeSmall + margin * 2, margin, edgeRad, stdTs, (int) p.textWidth("Files to render") + margin * 3 + btnSizeSmall, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border, textYShift, '\\', false, true, false, "Files to render", hoLiPictoPaths, startList, stdFont, null);

		startList = new String[allConnectedNodes.size()];
		for (int i = 0; i < startList.length; i++) {
			startList[i] = listW;
		}
		allPCs_HorizontalList = new HorizontalList(p, 0, allFiles_HorizontalList.getH() / 2 + margin + listH / 2, p.width - margin * 2, listH, margin, edgeRad, stdTs, (int) p.textWidth("Rendering PCs") + margin * 3 + btnSizeSmall, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border, textYShift, '\\', true, false, true, "Rendering PCs", hoLiPictoPaths, startList, stdFont, allFiles_HorizontalList);
		logBar = new LogBar(p, 0, allPCs_HorizontalList.getH() / 2 + margin * 2 + btnSizeSmall / 2, allFiles_HorizontalList.getW(), btnSizeSmall + margin * 2, stdTs, edgeRad, margin, btnSizeSmall, dark, light, lighter, textCol, textDark, border, true, textYShift, pictoPaths[0], stdFont, allPCs_HorizontalList);
		logBar.setText("Render Log of selected PC");
        freeze_imageButton = new ImageButton(p, logBar.getW()/2-btnSizeSmall/2-margin,0, btnSizeSmall, btnSizeSmall, stdTs, margin, edgeRad, -1, textYShift, true, true, textCol, lighter, pictoPaths[1], "Start rendering", logBar);


		allPCNames = new String[allPCs_HorizontalList.getList().length];
		allLastLogLines = new String[allPCs_HorizontalList.getList().length];
		allPCPictos = new PictogramImage[allPCs_HorizontalList.getList().length];
		allPCLoadingbars = new Loadingbar[allPCs_HorizontalList.getList().length];
		startedRenderingTiles = new Boolean[allPCs_HorizontalList.getList().length];
		allRenderInfos = new String[allPCs_HorizontalList.getList().length];
		allPCStatus = new int[allPCs_HorizontalList.getList().length];

		for (int i = 0; i < allPCLoadingbars.length; i++) {
			allPCLoadingbars[i] = new Loadingbar(p, 0, 0, listH, margin, stdTs, edgeRad, margin, border, dark, textCol, 0, 600, textYShift, false, stdFont, null);
			allPCNames[i] = allConnectedNodes.get(i).getPcSelection_DropdownMenu().getSelectedItem();
			allLastLogLines[i] = "Fra:2 Mem:142.26M (0.00M, Peak 152.21M) | Time:00:00.36 | Remaining:00:02.01 | Mem:11.84M, Peak:21.80M | Scene, View Layer | Rendered 1/680 Tiles: " + i;
			allPCPictos[i] = new PictogramImage(p, margin + btnSize / 2, margin + btnSize / 2, btnSize,btnSize, margin, stdTs, edgeRad, textCol, textYShift, false,false, allConnectedNodes.get(i).getTypePicto().getPictoPath(), "", null);
		}
	}

}
