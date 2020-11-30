package com.dominic.network_apk;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import org.json.simple.JSONArray;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import processing.core.PApplet;
import processing.core.PFont;

public class Node<T> {

	private int x, y, dragShiftX, dragShiftY, headY, bodyY, w, h, startW, bodyH, headH, type, edgeRad, margin, stdTs, btnSizeSmall, dark, darkest, bgCol, textCol, textDark, lighter, lightest, border, red, doOnce = 0, anzTypes = 5, conS, prevPortCount = 0, cpuCores, pcStatus = 2, prevAllFilesInPCFolderSize = -1;
	private float textYShift;
	private Boolean isTypePC = false, mouseIsPressed = false, isGrabbed = true, isSelected = false, isDeleted = false, isCheckedForConnection = false, isReady = true;
	private String id, cpuText = "CPU cores: 0", gpuText = "GPUs: 0", cpuName = "", gpuName = "";
	private String[] pictoPaths;
	private String[] pcStatusStrings = { "Alive", "Rendering", "Not responding" };
	private long lastLogTime, prevLastModified = 0;
	private PFont stdFont;
	private PApplet p;
	private MainActivity mainActivity;
	private NodeEditor parent;
	private PictogramImage type_picto, cpu_picto, gpu_picto;
	private Checkbox useCpu_checkbox, useGpu_checkbox;
	private DropdownMenu pcSelection_DropdownMenu;
	private CounterArea switchPort_CounterArea;
	private EditText switchName_editText;
	private ConnectorPoint output_connectorPoint, input_connectorPoint;
	private JsonHelper jsonHelper;
	private ArrayList<String> switch_connectorPointIds = new ArrayList<String>();
	private ArrayList<ConnectorPoint> switch_connectorPoints = new ArrayList<ConnectorPoint>();
	private PCInfoHelper pcInfoHelper;
	private FileInteractionHelper fileInteractionHelper;

	public Node(PApplet p, int x, int y, int w, int h, int type, int edgeRad, int margin, int stdTs, int btnSizeSmall, int dark, int darkest, int bgCol, int textCol, int textDark, int lighter, int lightest, int border, int red, float textYShift, String id, String stdConnectorId, String[] pictoPaths, PFont stdFont, T parent) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.edgeRad = edgeRad;
		this.margin = margin;
		this.stdTs = stdTs;
		this.btnSizeSmall = btnSizeSmall;
		this.dark = dark;
		this.darkest = darkest;
		this.bgCol = bgCol;
		this.textCol = textCol;
		this.textDark = textDark;
		this.lighter = lighter;
		this.lightest = lightest;
		this.border = border;
		this.red = red;
		this.textYShift = textYShift;
		this.id = id;
		this.pictoPaths = pictoPaths;
		this.stdFont = stdFont;
		this.p = p;
		this.type = type;
		this.parent = (NodeEditor) parent;

		startW = w;

		jsonHelper = new JsonHelper(p);
		pcInfoHelper = new PCInfoHelper(p);
		fileInteractionHelper = new FileInteractionHelper(p);
		// cpuCores = (int) (p.random(24));

		mainActivity = (MainActivity) p;
		conS = btnSizeSmall - margin;
		headH = btnSizeSmall + margin * 2;
		bodyH = h - headH - margin;
		calcBodyAndHeadPos();
		if (type < 3) {
			type_picto = new PictogramImage(p, w / 2 - btnSizeSmall / 2 - margin, headY - y, btnSizeSmall, btnSizeSmall, margin, stdTs, edgeRad, textCol, textYShift, true, false, pictoPaths[type], "", this);
			cpu_picto = new PictogramImage(p, -btnSizeSmall / 2 - margin, bodyY - y - bodyH / 2 + margin + btnSizeSmall / 2, btnSizeSmall, btnSizeSmall, margin, stdTs, edgeRad, textCol, textYShift, true, false, pictoPaths[anzTypes], "", this);
			gpu_picto = new PictogramImage(p, +btnSizeSmall + btnSizeSmall / 2 + margin * 2, bodyY - y - bodyH / 2 + margin + btnSizeSmall / 2, btnSizeSmall, btnSizeSmall, margin, stdTs, edgeRad, textCol, textYShift, true, false, pictoPaths[anzTypes + 1], "", this);
			useCpu_checkbox = new Checkbox(p, -btnSizeSmall - btnSizeSmall / 2 - margin * 2, bodyY - y - bodyH / 2 + margin + btnSizeSmall / 2, btnSizeSmall, btnSizeSmall, btnSizeSmall - margin, edgeRad, margin, stdTs, lighter, lighter, border, textCol, textYShift, true, false, "", "", pictoPaths[anzTypes + 6], stdFont, this);
			useGpu_checkbox = new Checkbox(p, +btnSizeSmall / 2 + margin, bodyY - y - bodyH / 2 + margin + btnSizeSmall / 2, btnSizeSmall, btnSizeSmall, btnSizeSmall - margin, edgeRad, margin, stdTs, lighter, lighter, border, textCol, textYShift, true, false, "", "", pictoPaths[anzTypes + 6], stdFont, this);

			String[] pcList = new FileInteractionHelper(p).getFoldersAndFiles(mainActivity.getPathToPCFolder(), true);
			String[] ddPaths = { pictoPaths[anzTypes + 5], pictoPaths[anzTypes + 4] };
			pcSelection_DropdownMenu = new DropdownMenu(p, -btnSizeSmall / 2 - margin, headY - y, w - margin * 3 - btnSizeSmall, btnSizeSmall, h + btnSizeSmall + margin * 2, edgeRad, margin, stdTs, lighter, lightest, textCol, textDark, textYShift, "PC", ddPaths, pcList, stdFont, true, this);

			int[] conT = { 1, 2 };
			// String connectorId = UUID.randomUUID().toString();
			mainActivity.getNodeEditor().addConnectorPoint(p, 0, w / 2, bodyY - y, conS / 2, 2, bgCol, true, conT, stdConnectorId, id, this);
			output_connectorPoint = getConnectorPoints().get(getConnectorPoints().size() - 1);
			isTypePC = true;

		}
		if (type == 3) {

			type_picto = new PictogramImage(p, w / 2 - btnSizeSmall / 2 - margin, headY - y, btnSizeSmall, btnSizeSmall, margin, stdTs, edgeRad, textCol, textYShift, true, false, pictoPaths[type], "", this);
			String[] pp = { pictoPaths[anzTypes + 2], pictoPaths[anzTypes + 3] };
			switchPort_CounterArea = new CounterArea(p, 0, bodyY - y - bodyH / 2 + margin + btnSizeSmall / 2, w - margin * 2, btnSizeSmall, edgeRad, margin, stdTs, 2, 48, 8, lighter, textCol, textCol, textYShift, true, "Port Count", pp, stdFont, this);
			char[] fChars = { '>', '<', ':', '"', '/', '\\', '|', '?', '*' };
			switchName_editText = new EditText(p, -btnSizeSmall / 2 - margin, headY - y, w - margin * 3 - btnSizeSmall, btnSizeSmall, stdTs, lighter, textCol, edgeRad, margin, textYShift, true, true, "Switch Name", fChars, stdFont, this);

		}
		if (type == 4) {
			type_picto = new PictogramImage(p, w / 2 - btnSizeSmall / 2 - margin, headY - y, btnSizeSmall, btnSizeSmall, margin, stdTs, edgeRad, textCol, textYShift, true, false, pictoPaths[type], "", this);
			int[] conT = { 0, 1 };
			// String connectorId = UUID.randomUUID().toString();
			mainActivity.getNodeEditor().addConnectorPoint(p, 2, -w / 2, bodyY - y, conS / 2, 2, bgCol, true, conT, stdConnectorId, id, this); // type 0 = pc, type 1=switch, type 2=output
			input_connectorPoint = getConnectorPoints().get(getConnectorPoints().size() - 1);
		}

	}

	public void render() {
		switch (type) {
		case 0:
			renderTypePC(); // Master pc
			break;

		case 1:
			renderTypePC(); // pc
			break;

		case 2:
			renderTypePC(); // laptop
			break;

		case 3:
			renderTypeSwitch(); // Switch
			break;

		case 4:
			renderTypeOutput(); // engine output
			break;
		}
	}

	private void renderTypePC() {
		if (mouseIsPressed) {
			if (isGrabbed == false) {
				if (isDragablePcNode()) {
					isGrabbed = true;
					dragShiftX = p.mouseX - x;
					dragShiftY = p.mouseY - y;
				}
			}
			output_connectorPoint.onMousePressed();
		}

		if (isGrabbed) {
			x = p.mouseX - dragShiftX;
			y = p.mouseY - dragShiftY;
			calcBodyAndHeadPos();
		}

		if (isSelected) {
			p.stroke(border);
		} else {
			p.stroke(lighter);
		}
		p.fill(bgCol);
		p.rect(x, bodyY, w, bodyH, 0, 0, edgeRad, edgeRad);
		p.rect(x, headY, w, headH, edgeRad, edgeRad, 0, 0);

		type_picto.render();
		gpu_picto.render();
		cpu_picto.render();
		useGpu_checkbox.render();
		useCpu_checkbox.render();
		p.fill(textCol);
		p.textFont(stdFont);
		p.textSize(stdTs);
		p.textAlign(p.LEFT, p.CENTER);
		p.text(cpuName, x - w / 2 + margin, useCpu_checkbox.getY() + useCpu_checkbox.getBoxDim() + margin - stdTs * textYShift);
		p.text("CPU cores: " + cpuCores, x - w / 2 + margin, useCpu_checkbox.getY() + useCpu_checkbox.getBoxDim() + margin - stdTs * textYShift + stdTs);
		p.text(gpuName, x - w / 2 + margin, useCpu_checkbox.getY() + useCpu_checkbox.getBoxDim() + margin - stdTs * textYShift + stdTs * 2);
		p.textAlign(p.LEFT, p.TOP);
		String statusString = "";
		if (pcStatus >= 0) {
			statusString = pcStatusStrings[pcStatus];
		}
		p.text("Status: " + statusString, x - w / 2 + margin, useCpu_checkbox.getY() + useCpu_checkbox.getBoxDim() + margin - stdTs * textYShift + stdTs * 2.5f);
		
		renderConnector(x + w / 2, bodyY, false, "");
		output_connectorPoint.render();
		pcSelection_DropdownMenu.render();

		File f = new File(mainActivity.getPathToPCFolder() + "\\" + pcSelection_DropdownMenu.getSelectedItem() + "\\" + mainActivity.getLogFileName());
		if (pcInfoHelper.getCurTime() - lastLogTime > mainActivity.getShortTimeIntervall() || f.lastModified() != prevLastModified) {
			checkForSignsOfLife();
			prevLastModified = f.lastModified();
			lastLogTime = pcInfoHelper.getCurTime();
		}

		String[] allFoldersInPcFolder = fileInteractionHelper.getFoldersAndFiles(mainActivity.getPathToPCFolder(), true);
		if (allFoldersInPcFolder.length != prevAllFilesInPCFolderSize) {
			setupDropdown(pcSelection_DropdownMenu.getSelectedInd(), pcSelection_DropdownMenu.getSelectedItem(), allFoldersInPcFolder);
			prevAllFilesInPCFolderSize = allFoldersInPcFolder.length;
		}
	}

	public void checkForSignsOfLife() {
		pcStatus = getPCStatus();
		setIsReady(pcStatus < 2);
	}

	private int getPCStatus() {
		int calculatedPcStatus = 2;
		Boolean pcIsAlive = true;
		String pcAlias = pcSelection_DropdownMenu.getSelectedItem();
		try {
			if (pcAlias.length() > 0) {
				JSONArray loadedSettingsData = jsonHelper.getData(mainActivity.getPathToPCFolder() + "\\" + pcAlias + "\\" + mainActivity.getLogFileName());
				if (loadedSettingsData.isEmpty()) {
					pcIsAlive = false;
					calculatedPcStatus = 2;
				} else {
					JsonObject jsonObject = new JsonParser().parse(loadedSettingsData.get(0).toString()).getAsJsonObject();
					long logTime = Long.parseLong(jsonObject.getAsJsonObject("SystemLog").get("logTime").getAsString());
					// Atention for condition (1!=1 only for testing)
					if (pcInfoHelper.getCurTime() - logTime > mainActivity.getStdTimeIntervall() && 1 != 1) {
						pcIsAlive = false;
					}

					if (pcIsAlive) {
						
						try {
							String cpuNameLoaded = jsonObject.getAsJsonObject("SystemLog").get("cpuName").getAsString();
							String gpuNameLoaded = jsonObject.getAsJsonObject("SystemLog").get("gpuName").getAsString();
							cpuCores = Integer.parseInt((jsonObject.getAsJsonObject("SystemLog").get("cpuCores").getAsString()));
							setCPUName(cpuNameLoaded);
							setGPUName(gpuNameLoaded);

						} catch (Exception e) {
							e.printStackTrace();
						}
						calculatedPcStatus = 0;
						int renderStatus = Integer.parseInt(jsonObject.getAsJsonObject("SystemLog").get("renderMode").getAsString());
						Boolean cpuIsRendering = Boolean.parseBoolean(jsonObject.getAsJsonObject("SystemLog").get("cpuIsRendering").getAsString());
						Boolean gpuIsRendering = Boolean.parseBoolean(jsonObject.getAsJsonObject("SystemLog").get("gpuIsRendering").getAsString());

						if (renderStatus == 1) {
							if (cpuIsRendering || gpuIsRendering) {
								calculatedPcStatus = 1;
							} else {
								calculatedPcStatus = 0;
							}
						}
					} else {
						calculatedPcStatus = 2;
					}
				}
			} else {
				pcIsAlive = false;
				calculatedPcStatus = 2;
			}
		} catch (Exception e) {
			calculatedPcStatus = 2;
			e.printStackTrace();
		}
		return calculatedPcStatus;
	}

	private void renderTypeSwitch() {

		if (mouseIsPressed) {
			if (isGrabbed == false) {
				if (isDragableSwitchNode()) {
					isGrabbed = true;
					dragShiftX = p.mouseX - x;
					dragShiftY = p.mouseY - y;
				}
			}
		}

		if (isGrabbed) {
			x = p.mouseX - dragShiftX;
			y = p.mouseY - dragShiftY;
			calcBodyAndHeadPos();
		}

		if (switchPort_CounterArea.getValueHasChanged()) {
			h = headH + margin + (switchPort_CounterArea.getH() + margin * 2 + (p.ceil(switchPort_CounterArea.getCount() / 2.0f) * conS) + (p.ceil(switchPort_CounterArea.getCount() / 2.0f) * margin));
			bodyH = h - headH - margin;
			calcBodyAndHeadPos();

			type_picto.setPos(w / 2 - btnSizeSmall / 2 - margin, headY - y);
			switchPort_CounterArea.setPos(0, bodyY - y - bodyH / 2 + margin + btnSizeSmall / 2);
			switchName_editText.setPos(-btnSizeSmall / 2 - margin, headY - y);

			switchPort_CounterArea.setValueHasChanged(false);
		}

		if (isSelected) {
			p.stroke(border);
		} else {
			p.stroke(lighter);
		}
		p.fill(bgCol);
		p.rect(x, bodyY, w, bodyH, 0, 0, edgeRad, edgeRad);
		p.rect(x, headY, w, headH, edgeRad, edgeRad, 0, 0);
		switchName_editText.render();
		type_picto.render();
		switchPort_CounterArea.render();

		updateSwitchConnectorPoints(false, null, false);

		for (int i = 0; i < switch_connectorPoints.size(); i++) {
			ConnectorPoint cp = switch_connectorPoints.get(i);
			cp.render();
		}

		prevPortCount = switchPort_CounterArea.getCount();

	}

	public void updateSwitchConnectorPoints(Boolean useIds, String[] conIds, Boolean onlyDoCalculations) {

		Boolean isEven;
		int conBorder;
		if (switchPort_CounterArea.getCount() % 2 == 0) {
			conBorder = p.ceil(switchPort_CounterArea.getCount() / 2) - 1;
			isEven = true;
		} else {
			conBorder = p.ceil(switchPort_CounterArea.getCount() / 2);
			isEven = false;
		}

		if (switch_connectorPointIds.size() > switchPort_CounterArea.getCount()) {
			for (int i = switch_connectorPointIds.size() - 1; i >= prevPortCount; i--) {

				mainActivity.getNodeEditor().removeConnectorPoint(switch_connectorPointIds.get(i));
				switch_connectorPointIds.remove(i);
				switch_connectorPoints.remove(i);
			}
		}

		for (int i = switchPort_CounterArea.getCount() - 1; i >= 0; i--) {
			int xp, yp;

			if (i <= conBorder) {
				xp = x - w / 2 - 1;
				yp = conS / 2 + bodyY - bodyH / 2 + margin * 2 + switchPort_CounterArea.getH() + i * conS + i * margin;
				if (onlyDoCalculations == false) { // for startup calculations if true, else rendering
					renderConnector(xp, yp, true, "Port: " + (i + 1));
				}
			} else {
				int ind = i - p.ceil(switchPort_CounterArea.getCount() / 2);
				if (!isEven) {
					ind--;
				}
				xp = x + w / 2;
				yp = conS / 2 + bodyY - bodyH / 2 + margin * 2 + switchPort_CounterArea.getH() + ind * conS + ind * margin;
				if (onlyDoCalculations == false) {
					renderConnector(xp, yp, false, "Port: " + (i + 1));
				}
			}

			if (switch_connectorPoints.size() < switchPort_CounterArea.getCount()) {
				int[] conT = { 0, 1, 2 };
				String connectorId;
				if (useIds) {
					connectorId = conIds[i];
				} else {
					connectorId = UUID.randomUUID().toString();
				}
				mainActivity.getNodeEditor().addConnectorPoint(p, 1, xp - x, yp - y, conS / 2, 2, bgCol, true, conT, connectorId, id, this);
				switch_connectorPoints.add(getConnectorPoints().get(getConnectorPoints().size() - 1));
				switch_connectorPointIds.add(connectorId);
			} else {
				switch_connectorPoints.get(i).setPos(xp - x, yp - y);
			}
		}
	}

	private void renderTypeOutput() {
		if (mouseIsPressed) {
			if (isGrabbed == false) {
				if (isDragableOutputNode()) {
					isGrabbed = true;
					dragShiftX = p.mouseX - x;
					dragShiftY = p.mouseY - y;
				}
			}
			input_connectorPoint.onMousePressed();
		}

		if (isGrabbed) {
			x = p.mouseX - dragShiftX;
			y = p.mouseY - dragShiftY;
			calcBodyAndHeadPos();
		}

		if (isSelected) {
			p.stroke(border);
		} else {
			p.stroke(lighter);
		}
		p.fill(bgCol);
		p.rect(x, bodyY, w, bodyH, 0, 0, edgeRad, edgeRad);
		p.rect(x, headY, w, headH, edgeRad, edgeRad, 0, 0);

		type_picto.render();
		p.fill(textCol);
		p.textFont(stdFont);
		p.textSize(stdTs);
		p.textAlign(p.LEFT, p.CENTER);
		p.text("Output", x - w / 2 + margin, headY - stdTs * textYShift);
		p.text(cpuText + "\n" + gpuText, x - w / 2 + margin + conS / 2, bodyY - stdTs * textYShift);

		renderConnector(x - w / 2, bodyY, true, "");
		input_connectorPoint.render();
	}

	private void renderConnector(int conX, int conY, Boolean isLeft, String text) {
		float a1, a2;
		if (isLeft) {
			a1 = 2 * p.PI - p.HALF_PI;
			a2 = 3 * p.PI - p.HALF_PI;

			p.fill(textCol);
			p.textFont(stdFont);
			p.textSize(stdTs);
			p.textAlign(p.LEFT, p.CENTER);
			p.text(text, conX + conS / 2 + margin, conY - stdTs * textYShift);
		} else {
			a1 = p.HALF_PI;
			a2 = 2 * p.PI - p.HALF_PI;

			p.fill(textCol);
			p.textFont(stdFont);
			p.textSize(stdTs);
			p.textAlign(p.RIGHT, p.CENTER);
			p.text(text, conX - conS / 2 - margin, conY - stdTs * textYShift);
		}

		p.fill(dark);
		p.stroke(dark);
		p.arc(conX, conY, conS, conS, a1, a2, p.PIE);

		p.noFill();
		if (isSelected) {
			p.stroke(border);
		} else {
			p.stroke(lighter);
		}
		p.arc(conX, conY, conS, conS, a1, a2, p.OPEN);

		p.stroke(dark);
		p.fill(bgCol);
		p.ellipse(conX, conY, conS - margin, conS - margin);
	}

	private void calcBodyAndHeadPos() {
		bodyY = y + headH / 2 + margin / 2;
		headY = bodyY - bodyH / 2 - headH / 2 - margin;
	}

	private Boolean isDragablePcNode() {
		Boolean isD = true;

		for (int i = 0; i < mainActivity.getNodeEditor().getNodes().size(); i++) {
			Node n = (Node) mainActivity.getNodeEditor().getNodes().get(i);
			if (n.getIsGrabbed() && n.getId() != id) {
				isD = false;
				break;
			}
		}

		for (int i = 0; i < mainActivity.getNodeEditor().getConnectorPoints().size(); i++) {
			ConnectorPoint cp = (ConnectorPoint) mainActivity.getNodeEditor().getConnectorPoints().get(i);
			if (cp.getIsOnDrag()) {
				isD = false;
				break;
			}
		}

		if (output_connectorPoint.mouseIsInArea()) {
			isD = false;
		}

		if (mouseIsInArea()) {
			if (useGpu_checkbox.mouseIsInArea()) {
				p.fill(255, 0, 0);
				isD = false;
			}
			if (useCpu_checkbox.mouseIsInArea()) {
				isD = false;
			}
			if (pcSelection_DropdownMenu.getDropdownBtn().mouseIsInArea()) {
				isD = false;

			}
			if (pcSelection_DropdownMenu.getIsUnfolded()) {
				isD = false;

			}
		} else {
			isD = false;
		}
		return isD;
	}

	private Boolean isDragableSwitchNode() {
		Boolean isD = true;

		for (int i = 0; i < mainActivity.getNodeEditor().getNodes().size(); i++) {
			Node n = (Node) mainActivity.getNodeEditor().getNodes().get(i);
			if (n.getIsGrabbed() && n.getId() != id) {
				isD = false;
				break;
			}
		}

		if (mouseIsInArea()) {

			// check if mouse is in connectorpoint Area to do --->

			for (int i = 0; i < getConnectorPoints().size(); i++) {
				ConnectorPoint cp = getConnectorPoints().get(i);
				if (cp.mouseIsInArea() || cp.getIsOnDrag()) {
					isD = false;
					break;
				}
			}

			if (switchPort_CounterArea.mouseIsInArea()) {
				isD = false;
			}
			if (switchName_editText.mouseIsInArea()) {
				isD = false;
			}
		} else {
			isD = false;
		}
		return isD;
	}

	public Boolean isDragableOutputNode() {
		Boolean isD = true;

		for (int i = 0; i < mainActivity.getNodeEditor().getNodes().size(); i++) {
			Node n = (Node) mainActivity.getNodeEditor().getNodes().get(i);
			if (n.getIsGrabbed() && n.getId() != id) {
				isD = false;
				break;
			}
		}

		for (int i = 0; i < mainActivity.getNodeEditor().getConnectorPoints().size(); i++) {
			ConnectorPoint cp = (ConnectorPoint) mainActivity.getNodeEditor().getConnectorPoints().get(i);
			if (cp.getIsOnDrag()) {
				isD = false;
				break;
			}
		}

		if (input_connectorPoint.mouseIsInArea()) {
			isD = false;
		}

		if (mouseIsInArea()) {

		} else {
			isD = false;
		}
		return isD;
	}

	private void calcMinNodeWidth() {
		String textToCheck = "";

		if (cpuName.length() > gpuName.length()) {
			textToCheck = cpuName;
		} else {
			textToCheck = gpuName;
		}
		if (p.textWidth(textToCheck) > w - margin * 4) {
			w = (int) (p.textWidth(textToCheck) + margin * 4);
		} else {
			w = (int) (p.textWidth(textToCheck) + margin * 4);
			if (w < startW) {
				w = startW;
			}
		}
		output_connectorPoint.setPos(w / 2, bodyY - y);

	}

	public void onMousePressed(int mouseButton) {
		if (mouseButton == p.LEFT) {
			mouseIsPressed = true;

			if (isTypePC) {
				pcSelection_DropdownMenu.onMousePressed();
			}

			if (type == 3) {
				switchPort_CounterArea.onMousePressed();
				switchName_editText.onMousePressed();

				for (int i = 0; i < mainActivity.getNodeEditor().getConnectorPoints().size(); i++) {
					ConnectorPoint cp = (ConnectorPoint) mainActivity.getNodeEditor().getConnectorPoints().get(i);
					cp.onMousePressed();
				}

			}
		}
	}

	public void onMouseReleased(int mouseButton) {
		if (mouseButton == p.LEFT) {
			if (isGrabbed) {
				isGrabbed = false;
			}
			if (mouseIsInArea() == false) {
				isSelected = false;
			}
			if (isTypePC) {
				if (pcSelection_DropdownMenu.getIsUnfolded() == false) {
					useGpu_checkbox.onMouseReleased();
					useCpu_checkbox.onMouseReleased();
				}
				pcSelection_DropdownMenu.onMouseReleased();
				pcSelection_DropdownMenu.onMouseReleased();
				output_connectorPoint.onMouseReleased();
			}

			if (type == 3) {
				switchPort_CounterArea.onMouseReleased();
				switchName_editText.onMouseReleased();
				for (int i = 0; i < mainActivity.getNodeEditor().getConnectorPoints().size(); i++) {
					ConnectorPoint cp = (ConnectorPoint) mainActivity.getNodeEditor().getConnectorPoints().get(i);
					cp.onMouseReleased();
				}
			}
			if (type == 4) {
				input_connectorPoint.onMouseReleased();
			}
			mouseIsPressed = false;

		}

		if (mouseButton == p.RIGHT) {
			if (mouseIsInArea()) {
				isSelected = !isSelected;
			}
		} else {
			isSelected = false;
		}

	}

	public void onKeyReleased(char key) {
		if (key == p.DELETE) {
			if (isSelected) {
				isDeleted = true;
			}
		}
		if (isTypePC) {
			pcSelection_DropdownMenu.onKeyReleased(key);
		}
		if (type == 3) {
			switchName_editText.onKeyReleased(key);
		}
	}

	public void onScroll(float e) {
		if (isTypePC) {
			pcSelection_DropdownMenu.onScroll(e);
		}

		if (type == 3) {
			switchPort_CounterArea.onScroll(e);
		}
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getType() {
		return type;
	}

	public ConnectorPoint getOutputConnectorPoint() {
		return output_connectorPoint;
	}

	public ConnectorPoint getInputConnectorPoint() {
		return input_connectorPoint;
	}

	public ArrayList<ConnectorPoint> getSwitchConnectorPoints() {
		return switch_connectorPoints;
	}

	public ArrayList<ConnectorPoint> getConnectorPoints() {
		return ((NodeEditor) parent).getConnectorPoints();
		// return mainActivity.getNodeEditor().getConnectorPoints();
	}

	public Boolean mouseIsInArea() {
		if (p.mouseX > x - w / 2 && p.mouseX < x + w / 2 && p.mouseY > y - h / 2 && p.mouseY < y + h / 2) {
			return true;
		} else {
			return false;
		}
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public Boolean getIsGrabbed() {
		return isGrabbed;
	}

	public Boolean getIsTypePc() {
		return isTypePC;
	}

	public Boolean getIsReady() {
		return isReady;
	}

	public Boolean getCheckedForConnection() {
		return isCheckedForConnection;
	}

	public int getCpuCores() {
		return cpuCores;
	}

	public String getId() {
		return id;
	}

	public int getPcStatus() {
		if (isTypePC) {
			return pcStatus;
		} else {
			return -1;
		}
	}

	

	public int getCPUCores() {
		return cpuCores;
	}

	public String getCPUName() {
		return cpuName;
	}

	public String getGPUName() {
		return gpuName;
	}

	public String[] getPCStatusStrings() {
		return pcStatusStrings;
	}

	public Checkbox[] getCheckoxes() {
		Checkbox[] cboxex = { useCpu_checkbox, useGpu_checkbox };
		return cboxex;
	}

	public CounterArea getSwitchPort_CounterArea() {
		return switchPort_CounterArea;
	}

	public DropdownMenu getPcSelection_DropdownMenu() {
		return pcSelection_DropdownMenu;
	}

	public EditText getSwitchNameEditText() {
		return switchName_editText;
	}

	public PictogramImage getTypePicto() {
		return type_picto;
	}

	public void setPos(int xp, int yp) {
		x = xp;
		y = yp;
		dragShiftX = 0;
		dragShiftY = 0;
		calcBodyAndHeadPos();
	}

	public void setIsReady(Boolean state) {
		isReady = state;
		if (isReady) {
			type_picto.setCol(textCol);
		} else {
			type_picto.setCol(red);
		}
	}

	public void setCheckedForConnection(Boolean state) {
		isCheckedForConnection = state;
	}

	public void setIsGrabbed(Boolean state) {
		isGrabbed = state;
	}

	public void setIsDeleted(Boolean state) {
		isDeleted = state;
	}

	public void setCpuTextOutput(String cpuT) {
		cpuText = cpuT;
	}

	public void setGpuTextOutput(String gpuT) {
		gpuText = gpuT;
	}

	public void setGPUName(String setGPUName) {
		if (setGPUName.equals(gpuName) == false) {
			gpuName = setGPUName;
			calcMinNodeWidth();
		}
	}

	public void setCPUName(String setCPUName) {
		if (setCPUName.equals(cpuName) == false) {
			cpuName = setCPUName;
			calcMinNodeWidth();
		}
	}

	public void setCPUCores(int setCPUCores) {
		cpuCores = setCPUCores;
	}

	public void setPCList(String[] setPCList) {
		pcSelection_DropdownMenu.setList(setPCList);
	}

	public void setupDropdown(int selectedInd, String selectedItem, String[] newList) {
		pcSelection_DropdownMenu.setList(newList);
		if (selectedInd >= 0) {
			for (int i2 = 0; i2 < newList.length; i2++) {
				if (newList[i2].toUpperCase().equals(selectedItem.toUpperCase())) {
					pcSelection_DropdownMenu.setSelectedInd(i2);
					break;
				} else {
					if (i2 == newList.length - 1) {
						setIsReady(false);
						pcSelection_DropdownMenu.setSelectedInd(-1);
					}
				}

			}
		}
		prevAllFilesInPCFolderSize = newList.length;
	}
}
