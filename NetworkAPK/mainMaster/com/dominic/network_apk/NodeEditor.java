package com.dominic.network_apk;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PVector;

public class NodeEditor<T> {
	private int mode, nodeW, nodeH, panViewStartX, panViewStartY, nodeAdderBoxW, nodeAdderBoxH, screenX, screenY, prevScreenX, prevScreenY, btnSize, btnSizeSmall, gridSize, margin, stdTs, edgeRad, dark, darkest, light, lighter, lightest, border, textCol, textDark, red, doOnceOnPressed = 0, doOnceOnStart = 0;
	private float textYShift;
	private Boolean renderNodeMenu = false, mouseIsPressed = false, middleMouseWasPressed = false, isSetup = false;
	private String mySavePath;
	private String[] nodePaths1, nodePaths2, pcPaths;
	private long curTime, prevTime;
	private PFont stdFont;
	private PImage screenshot;
	private PApplet p;
	private MainActivity mainActivity;
	private JsonHelper jHelper;
	private ImageButton[] mainButtons;
	private ImageButton[] nodeEditorButtons = new ImageButton[5];
	private ImageButton[] nodeAdderButtons = new ImageButton[5];
	private ArrayList<Node> nodes = new ArrayList<>();
	private ArrayList<ConnectorPoint> connectorPoints = new ArrayList<ConnectorPoint>();
	private ArrayList<Node> allConnectedNodes = new ArrayList<Node>();
	private ArrayList<PVector> allNodePosOnMousePressed = new ArrayList<PVector>();
	private ArrayList<MakeToast> makeToasts = new ArrayList<MakeToast>();
	private JSONArray loadedData = new JSONArray();
	private FileInteractionHelper fileInteractionHelper;
	private PCInfoHelper pcInfoHelper;

	public NodeEditor(PApplet p, int mode, int btnSize, int btnSizeSmall, int margin, int stdTs, int edgeRad, int dark, int darkest, int light, int lighter, int lightest, int border, int textCol, int textDark, int red, float textYShift, String mySavePath, String[] buttonPaths, String[] nodePaths1, String[] nodePaths2, PFont stdFont) {
		this.mode = mode;
		this.btnSize = btnSize;
		this.btnSizeSmall = btnSizeSmall;
		this.margin = margin;
		this.stdTs = stdTs;
		this.edgeRad = edgeRad;
		this.textYShift = textYShift;
		this.dark = dark;
		this.darkest = darkest;
		this.light = light;
		this.lighter = lighter;
		this.lightest = lightest;
		this.border = border;
		this.textCol = textCol;
		this.textDark = textDark;
		this.red = red;
		this.nodePaths1 = nodePaths1;
		this.nodePaths2 = nodePaths2;
		this.stdFont = stdFont;
		this.mySavePath = mySavePath;
		this.p = p;

		nodeW = (int) (btnSize * 4f);
		nodeH = (int) (btnSize * 2.8f);
		mainActivity = (MainActivity) p;
		gridSize = btnSizeSmall / 2;
		if (mainActivity.getIsMaster()) {
			mainButtons = mainActivity.getMainButtonsMaster();
		} else {
			mainButtons = mainActivity.getMainButtonsSlave();
		}
		screenX = p.width;
		screenY = p.height;
		prevScreenX = p.width;
		prevScreenY = p.height;

		String[] btnDescriptions = { "Delete all Nodes | shorcut: ctrl+d", "Reload from file | shortcut: ctrl+r", "Add node | shortcut: ctrl+a", "Center nodes | shortcut: ctrl+c ", "Save | shortcut: ctrl+s" };
		int[] shortcuts = { 4, 18, 1, 3, 19 };
		for (int i = 0; i < nodeEditorButtons.length; i++) {
			nodeEditorButtons[i] = new ImageButton(p, mainButtons[0].getX(), mainButtons[0].getY() + mainButtons[0].getH() + margin + btnSize * i + margin * i, btnSize, btnSize, stdTs, margin, edgeRad, shortcuts[i], textYShift, true, false, textCol, light, buttonPaths[i], btnDescriptions[i], null);
		}

		String[] nodeAdderBtnDescriptions = { "Add Master-PC", "Add Slave-PC", "Add Laptop", "Add Switch", "Add Output" };
		for (int i = 0; i < nodeAdderButtons.length; i++) {
			nodeAdderButtons[i] = new ImageButton(p, p.width / 2 - (nodeAdderButtons.length / 2) * btnSize - (nodeAdderButtons.length / 2) * margin + i * btnSize + i * margin, p.height / 2, btnSize, btnSize, stdTs, margin, edgeRad, -1, textYShift, true, false, textCol, light, nodePaths1[i], nodeAdderBtnDescriptions[i], null);
		}
		nodeAdderBoxW = nodeAdderButtons.length * btnSize + nodeAdderButtons.length * margin + margin;
		nodeAdderBoxH = btnSize + margin * 2;
		jHelper = new JsonHelper(p);

		fileInteractionHelper = new FileInteractionHelper(p);
		pcInfoHelper = new PCInfoHelper(p);

	}

	public void render() {

		if (doOnceOnStart == 0) {
			setupAll();
			doOnceOnStart++;
		}

		if (mouseIsPressed) {
			if (nodes.size() > 0 && allNodePosOnMousePressed.size() == nodes.size() && middleMouseWasPressed) {
				for (int i = 0; i < nodes.size(); i++) {
					Node n = nodes.get(i);
					n.setPos((int) allNodePosOnMousePressed.get(i).x + p.mouseX - panViewStartX, (int) allNodePosOnMousePressed.get(i).y + p.mouseY - panViewStartY);
				}
			}
		}

		screenX = p.width;
		screenY = p.height;
		if (screenX != prevScreenX || screenY != prevScreenY) {

			for (int i = 0; i < nodeAdderButtons.length; i++) {
				nodeAdderButtons[i].setPos(p.width / 2 - (nodeAdderButtons.length / 2) * btnSize - (nodeAdderButtons.length / 2) * margin + i * btnSize + i * margin, p.height / 2);
			}
			prevScreenX = screenX;
			prevScreenY = screenY;
		}

		renderGrid();
		if (renderNodeMenu == false) {
			handleNodes();
		}
		if (mainActivity.getIsMaster()) {
			mainActivity.renderMainButtonsMaster();
		} else {
			mainActivity.renderMainButtonsSlave();
		}
		if (mainButtons[0].getClickCount() % 2 == 0) {

			int rectH = p.height - (nodeEditorButtons[nodeEditorButtons.length - 1].getY() + nodeEditorButtons[nodeEditorButtons.length - 1].getH() / 2 + margin * 2);
			int rectY = nodeEditorButtons[nodeEditorButtons.length - 1].getY() + nodeEditorButtons[nodeEditorButtons.length - 1].getH() / 2 + margin + rectH / 2;
			p.fill(light);
			p.stroke(light);
			p.rect(nodeEditorButtons[nodeEditorButtons.length - 1].getX(), rectY, btnSize, rectH, edgeRad);

			for (int i = 0; i < nodeEditorButtons.length; i++) {
				nodeEditorButtons[i].render();
			}
			for (int i = 0; i < nodeEditorButtons.length; i++) {
				nodeEditorButtons[i].getHoverText().render();
			}
		}

		if (renderNodeMenu == true) {
			p.image(screenshot, p.width / 2, p.height / 2);
			p.fill(dark);
			p.stroke(dark);
			p.rect(p.width / 2, p.height / 2, nodeAdderBoxW, nodeAdderBoxH, edgeRad);
			for (int i = nodeAdderButtons.length - 1; i >= 0; i--) {
				nodeAdderButtons[i].render();
			}
		}

		// render toasts ---------------
		for (int i = 0; i < makeToasts.size(); i++) {
			MakeToast m = makeToasts.get(i);
			if (m.remove) {
				makeToasts.remove(i);
			} else {
				m.render();
			}
		}
		// render toasts ---------------

		handleButtons();
		curTime = pcInfoHelper.getCurTime();
		if (curTime - prevTime > mainActivity.getSuperShortTimeIntervall()) {
			// node cpu/gpu checkboxes have to be checked for output count
			calcConnectedNodes();
			prevTime = curTime;

		}
	}

	public void renderGrid() {
		int alpha = 2;
		for (int i = 0; i < p.width; i += gridSize) {
			for (int i2 = 0; i2 < p.height; i2 += gridSize) {
				p.noFill();
				p.stroke(255, alpha);
				p.rect(i, i2, gridSize, gridSize);
			}
		}
	}

	public void handleNodes() {
		for (int i = nodes.size() - 1; i >= 0; i--) {
			Node n = nodes.get(i);
			if (n.getIsDeleted()) {
				if (n.getType() < 3) {
					connectorPoints.remove(n.getOutputConnectorPoint());
				}
				if (n.getType() == 3) {
					for (int i2 = n.getSwitchConnectorPoints().size() - 1; i2 >= 0; i2--) {
						ConnectorPoint cp = (ConnectorPoint) n.getSwitchConnectorPoints().get(i2);
						connectorPoints.remove(cp);
					}
				}
				if (n.getType() == 4) {
					connectorPoints.remove(n.getInputConnectorPoint());
				}
				nodes.remove(i);
			} else {
				n.render();
			}
		}
	}

	public void setupAll() {
		if (isSetup == false) {
			try {
				setData();
				isSetup = true;
			} catch (Exception e) {
				e.printStackTrace();
				if (nodes.size() > 0) {
					for (int i = nodes.size() - 1; i >= 0; i--) {
						nodes.remove(i);
					}
				}
			}
		}
	}

	public void calcConnectedNodes() {
		allConnectedNodes.clear();

		// search for output node -------------
		Node outputNode = null;
		for (int i = 0; i < nodes.size(); i++) {
			Node n = nodes.get(i);
			if (n.getType() == nodeAdderButtons.length - 1) {
				outputNode = n;
			}
			n.setCheckedForConnection(false);
		}
		if (outputNode != null) {
			recursiveGetConnectedNodes(outputNode);
			int connectedCpus = 0;
			int connectedGpus = 0;
			for (int i = 0; i < allConnectedNodes.size(); i++) {
				Node n = allConnectedNodes.get(i);

				if (n.getIsTypePc()) {
					if (n.getCheckoxes()[0].getIsChecked() && n.getCPUName().length()>0) {
						connectedCpus += n.getCpuCores();
					}
					if (n.getCheckoxes()[1].getIsChecked() && n.getGPUName().length()>0) {
						connectedGpus++;
					}
				}
			}

			outputNode.setCpuTextOutput("CPU threads: " + connectedCpus);
			outputNode.setGpuTextOutput("GPUs: " + connectedGpus);
		}
		// search for output node -------------

	}

	private void recursiveGetConnectedNodes(Node n) {
		if (n.getCheckedForConnection() == false) {
			if (n.getIsTypePc()) {
				allConnectedNodes.add(n);
			}
			if (n.getType() == 3) {
				for (int i = 0; i < n.getSwitchConnectorPoints().size(); i++) {
					ConnectorPoint cp = (ConnectorPoint) n.getSwitchConnectorPoints().get(i);
					if (cp.getIsConnected()) {
						n.setCheckedForConnection(true);
						recursiveGetConnectedNodes(getNodeByConnectorId(cp.getConnectedId()));
					}
				}
			}
			if (n.getType() == nodeAdderButtons.length - 1) {
				if (n.getInputConnectorPoint().getIsConnected()) {
					Node nextNode = getNodeByConnectorId(n.getInputConnectorPoint().getConnectedId());
					recursiveGetConnectedNodes(nextNode);
				}
			}
		}

		n.setCheckedForConnection(true);

	}

	private Node getNodeByConnectorId(String connectorId) {
		Node resultNode = null;
		for (int i = 0; i < nodes.size(); i++) {
			Node n = nodes.get(i);
			if (n.getIsTypePc()) {
				if (n.getOutputConnectorPoint().getId().equals(connectorId)) {
					resultNode = n;
					break;
				}
			}
			if (n.getType() == 3) {
				for (int i2 = 0; i2 < n.getSwitchConnectorPoints().size(); i2++) {
					ConnectorPoint cp = (ConnectorPoint) n.getSwitchConnectorPoints().get(i2);
					if (cp.getId().equals(connectorId)) {
						resultNode = n;
						break;
					}
				}
			}
			if (n.getType() == nodeAdderButtons.length - 1) {
				if (n.getInputConnectorPoint().getId().equals(connectorId)) {
					resultNode = n;
					break;
				}
			}
		}
		return resultNode;
	}

	private void handleButtons() {

		// Edit node tree buttons --------------------------------------------
		for (int i = 0; i < nodeEditorButtons.length; i++) {

			if (nodeEditorButtons[i].getIsClicked() == true) {
				switch (i) {
				case 0:
					for (int i2 = 0; i2 < nodes.size(); i2++) {
						Node n = nodes.get(i2);
						n.setIsDeleted(true);
					}
					break;
				case 1:
					mainActivity.initializeNodeEditor();
					break;
				case 2:
					renderNodeMenu = true;
					p.saveFrame("data\\imgs\\screenshots\\homeScreen.png");
					screenshot = p.loadImage("data\\imgs\\screenshots\\homeScreen.png");
					screenshot = new ImageBlurHelper(p).blur(screenshot, 3);
					break;

				case 3:
					if (nodes.size() > 0) {
						int averageX = 0, averageY = 0;
						for (int i2 = 0; i2 < nodes.size(); i2++) {
							Node n = nodes.get(i2);
							averageX += n.getX();
							averageY += n.getY();
						}
						averageX /= nodes.size();
						averageY /= nodes.size();

						for (int i2 = 0; i2 < nodes.size(); i2++) {
							Node n = nodes.get(i2);
							n.setPos(n.getX() + (p.width / 2 - averageX), n.getY() + (p.height / 2 - averageY));
						}
					}
					break;

				case 4:
					saveNodeEditor();
					makeToasts.add(new MakeToast(p, p.width / 2, p.height - stdTs * 2, stdTs, margin, edgeRad, 100, light, textCol, textYShift, false, "Saved Node settings", stdFont, null));
					break;
				}

				nodeEditorButtons[i].setIsClicked(false);
			}
		}
		// Edit node tree buttons --------------------------------------------

		// node adder menu ---------------------------------------------------
		for (int i = 0; i < nodeAdderButtons.length; i++) {
			if (nodeAdderButtons[i].getIsClicked() == true) {
				int nh = nodeH;
				if (i == nodeAdderButtons.length - 1) {
					nh = btnSizeSmall + stdTs * 2 + margin * 7;
				}
				String nodeId = UUID.randomUUID().toString();
				nodes.add(new Node(p, p.mouseX, p.mouseY, nodeW, nh, i, edgeRad, margin, stdTs, btnSizeSmall, dark, darkest, light, textCol, textDark, lighter, lightest, border, red, textYShift, nodeId, UUID.randomUUID().toString(), nodePaths2, stdFont, this));
				renderNodeMenu = false;
				nodeAdderButtons[i].setIsClicked(false);
			}
		}
		// node adder menu ---------------------------------------------------

	}

	public void saveNodeEditor() {
		if (nodes.size() > 0) {
			jHelper.clearArray();

			for (int i = 0; i < nodes.size(); i++) {
				Node n = nodes.get(i);
				JSONObject nodeDetails = new JSONObject();
				JSONObject nodeObject = new JSONObject();

				if (n.getIsTypePc()) {
					int dd_selectedInd = -1;
					if (n.getPcSelection_DropdownMenu().getIsSelected()) {
						dd_selectedInd = n.getPcSelection_DropdownMenu().getSelectedInd();
					}
					nodeDetails.put("nodeType", n.getType());
					nodeDetails.put("nodeId", n.getId());
					nodeDetails.put("posX", n.getX());
					nodeDetails.put("posY", n.getY());
					nodeDetails.put("connectorPointId", n.getOutputConnectorPoint().getId());
					nodeDetails.put("connectorPointIsConnected", n.getOutputConnectorPoint().getIsConnected());
					if (n.getOutputConnectorPoint().getIsConnected()) {
						nodeDetails.put("connectorPointConnectedId", n.getOutputConnectorPoint().getConnectedId());
					} else {
						nodeDetails.put("connectorPointConnectedId", "-1");
					}
					nodeDetails.put("selectPc_dropdown", dd_selectedInd);
					nodeDetails.put("selectPc_dropdownItem", n.getPcSelection_DropdownMenu().getSelectedItem());
					nodeDetails.put("useCpu_checkbox", n.getCheckoxes()[0].getIsChecked());
					nodeDetails.put("useGpu_checkbox", n.getCheckoxes()[1].getIsChecked());
					nodeDetails.put("cpuName", n.getCPUName());
					nodeDetails.put("gpuName", n.getGPUName());
					nodeDetails.put("cpuCores", n.getCpuCores());
				}

				if (n.getType() == 3) {
					nodeDetails.put("nodeType", n.getType());
					nodeDetails.put("nodeId", n.getId());
					nodeDetails.put("posX", n.getX());
					nodeDetails.put("posY", n.getY());

					JSONArray connectorPointIds = new JSONArray();
					JSONArray connectorPointIsConnectedArray = new JSONArray();
					JSONArray connectorPointConnectedIds = new JSONArray();

					for (int i2 = 0; i2 < n.getSwitchConnectorPoints().size(); i2++) {
						ConnectorPoint cp = (ConnectorPoint) n.getSwitchConnectorPoints().get(i2);
						connectorPointIds.add(cp.getId());
						connectorPointIsConnectedArray.add(cp.getIsConnected());
						connectorPointConnectedIds.add(cp.getConnectedId());
					}

					// JSONArray ja = new JSONArray();
					// ja.add(jo);

					nodeDetails.put("connectorPointIds", connectorPointIds);
					nodeDetails.put("connectorPointIsConnectedArray", connectorPointIsConnectedArray);
					nodeDetails.put("connectorPointConnectedIds", connectorPointConnectedIds);

					nodeDetails.put("switchPort_CounterArea", n.getSwitchPort_CounterArea().getCount());
					nodeDetails.put("switchName_editText", n.getSwitchNameEditText().getStrList().get(0));
				}

				if (n.getType() == nodeAdderButtons.length - 1) {
					nodeDetails.put("nodeType", n.getType());
					nodeDetails.put("nodeId", n.getId());
					nodeDetails.put("posX", n.getX());
					nodeDetails.put("posY", n.getY());
					nodeDetails.put("connectorPointId", n.getInputConnectorPoint().getId());
					nodeDetails.put("connectorPointIsConnected", n.getInputConnectorPoint().getIsConnected());
					// nodeDetails.put("connectorPointConnectedId",
					// n.getInputConnectorPoint().getConnectedId());
					if (n.getInputConnectorPoint().getIsConnected()) {
						nodeDetails.put("connectorPointConnectedId", n.getInputConnectorPoint().getConnectedId());
					} else {
						nodeDetails.put("connectorPointConnectedId", "-1");
					}
				}

				nodeObject.put("Node" + i, nodeDetails);
				jHelper.appendObjectToArray(nodeObject);

			}
			jHelper.writeData(mySavePath);
		}
	}

	private void setData() throws Exception {
		loadedData = jHelper.getData(mySavePath);
		if (loadedData.isEmpty()) {
		} else {
			for (int i = 0; i < loadedData.size(); i++) {
				int nh = nodeH;

				JsonObject jsonObject = new JsonParser().parse(loadedData.get(i).toString()).getAsJsonObject();
				String nodeId = jsonObject.getAsJsonObject("Node" + i).get("nodeId").getAsString();
				int nodeType = Integer.parseInt((jsonObject.getAsJsonObject("Node" + i).get("nodeType").getAsString()));

				// set data for nodes of type pc
				// -----------------------------------------------------
				if (nodeType < 3) {
					String cpuName = "No CPU", gpuName = "No GPU";
					int pcStrengthCPU = -1, pcStrengthGPU = -1, cpuCores = 0;

					String connectorPointConnectedId = jsonObject.getAsJsonObject("Node" + i).get("connectorPointConnectedId").getAsString();
					String connectorPointId = jsonObject.getAsJsonObject("Node" + i).get("connectorPointId").getAsString();
					int xp = Integer.parseInt((jsonObject.getAsJsonObject("Node" + i).get("posX").getAsString()));
					int yp = Integer.parseInt((jsonObject.getAsJsonObject("Node" + i).get("posY").getAsString()));
					int selectPc_dropdown = Integer.parseInt((jsonObject.getAsJsonObject("Node" + i).get("selectPc_dropdown").getAsString()));
					try {
						cpuCores = Integer.parseInt((jsonObject.getAsJsonObject("Node" + i).get("cpuCores").getAsString()));
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						cpuName = jsonObject.getAsJsonObject("Node" + i).get("cpuName").toString();
						gpuName = jsonObject.getAsJsonObject("Node" + i).get("gpuName").toString();
					} catch (Exception e) {
						e.printStackTrace();
					}
					Boolean isConnectorPointConnected = Boolean.parseBoolean((jsonObject.getAsJsonObject("Node" + i).get("connectorPointIsConnected").getAsString()));
					Boolean useCpuCheckbox = Boolean.parseBoolean((jsonObject.getAsJsonObject("Node" + i).get("useCpu_checkbox").getAsString()));
					Boolean useGpuCheckbox = Boolean.parseBoolean((jsonObject.getAsJsonObject("Node" + i).get("useGpu_checkbox").getAsString()));
					String selectPc_dropdownItem = jsonObject.getAsJsonObject("Node" + i).get("selectPc_dropdownItem").getAsString();

					nodes.add(new Node(p, xp, yp, nodeW, nh, nodeType, edgeRad, margin, stdTs, btnSizeSmall, dark, darkest, light, textCol, textDark, lighter, lightest, border, red, textYShift, nodeId, connectorPointId, nodePaths2, stdFont, this));
					Node<T> n = nodes.get(nodes.size() - 1);

					n.setCPUName(cpuName);
					n.setGPUName(gpuName);

					n.setIsGrabbed(false);
					n.getOutputConnectorPoint().setIsConnected(isConnectorPointConnected);
					if (isConnectorPointConnected) {
						n.getOutputConnectorPoint().setConnectedId(connectorPointConnectedId);
					}
					n.getCheckoxes()[0].setIsChecked(useCpuCheckbox);
					n.getCheckoxes()[1].setIsChecked(useGpuCheckbox);

					String[] allFoldersInPcFolder = fileInteractionHelper.getFoldersAndFiles(mainActivity.getPathToPCFolder(), true);
					n.setupDropdown(selectPc_dropdown,selectPc_dropdownItem, allFoldersInPcFolder);

					// check if pc is ready (apk running + ready)----------------------
					Boolean FolderFound = false;
					for (int i2 = 0; i2 < allFoldersInPcFolder.length; i2++) {
						if (selectPc_dropdownItem.toUpperCase().equals(allFoldersInPcFolder[i2].toUpperCase())) {
							FolderFound = true;
							break;
						}
					}
					if (!FolderFound || selectPc_dropdownItem.length() < 1) {
						n.setIsReady(false);
					}
					// check if pc is ready (apk running + ready)----------------------

				}
				// set data for nodes of type pc
				// -----------------------------------------------------
				if (nodeType == 3) {

					int switchPort_CounterArea = Integer.parseInt((jsonObject.getAsJsonObject("Node" + i).get("switchPort_CounterArea").getAsString()));
					String switchName_editText = jsonObject.getAsJsonObject("Node" + i).get("switchName_editText").getAsString();

					String[] connectorPointConnectedIds = new String[switchPort_CounterArea];
					String[] connectorPointIds = new String[switchPort_CounterArea];
					Boolean[] connectorPointIsConnectedArray = new Boolean[switchPort_CounterArea];

					for (int i2 = connectorPointIds.length - 1; i2 >= 0; i2--) {

						int ind = i2;

						connectorPointConnectedIds[i2] = jsonObject.getAsJsonObject("Node" + i).get("connectorPointConnectedIds").getAsJsonArray().get(ind).toString().replace("\"", "");
						connectorPointIds[connectorPointIds.length - i2 - 1] = jsonObject.getAsJsonObject("Node" + i).get("connectorPointIds").getAsJsonArray().get(ind).toString().replace("\"", "");
						connectorPointIsConnectedArray[i2] = Boolean.parseBoolean(jsonObject.getAsJsonObject("Node" + i).get("connectorPointIsConnectedArray").getAsJsonArray().get(ind).toString());

					}

					int xp = Integer.parseInt((jsonObject.getAsJsonObject("Node" + i).get("posX").getAsString()));
					int yp = Integer.parseInt((jsonObject.getAsJsonObject("Node" + i).get("posY").getAsString()));

					nodes.add(new Node(p, xp, yp, nodeW, nh, nodeType, edgeRad, margin, stdTs, btnSizeSmall, dark, darkest, light, textCol, textDark, lighter, lightest, border, red, textYShift, nodeId, UUID.randomUUID().toString(), nodePaths2, stdFont, this));
					Node<T> n = nodes.get(nodes.size() - 1);
					n.setIsGrabbed(false);
					if (switchName_editText.length() > 0) {
						n.getSwitchNameEditText().setText(switchName_editText);
					} else {
						n.getSwitchNameEditText().setText("No name");

					}
					n.getSwitchPort_CounterArea().setCount(switchPort_CounterArea);
					n.updateSwitchConnectorPoints(true, connectorPointIds, true);

					for (int i2 = n.getSwitchConnectorPoints().size() - 1; i2 >= 0; i2--) {
						ConnectorPoint<T> cp = n.getSwitchConnectorPoints().get(i2);
						if (connectorPointIsConnectedArray[i2] == true) {
							cp.setIsConnected(true);
							cp.setConnectedId(connectorPointConnectedIds[i2]);
						}
					}
				}
				if (nodeType == nodeAdderButtons.length - 1) {
					nh = btnSizeSmall + stdTs * 2 + margin * 7;

					String connectorPointConnectedId = jsonObject.getAsJsonObject("Node" + i).get("connectorPointConnectedId").getAsString();
					String connectorPointId = jsonObject.getAsJsonObject("Node" + i).get("connectorPointId").getAsString();
					int xp = Integer.parseInt((jsonObject.getAsJsonObject("Node" + i).get("posX").getAsString()));
					int yp = Integer.parseInt((jsonObject.getAsJsonObject("Node" + i).get("posY").getAsString()));
					Boolean isConnectorPointConnected = Boolean.parseBoolean((jsonObject.getAsJsonObject("Node" + i).get("connectorPointIsConnected").getAsString()));

					nodes.add(new Node(p, xp, yp, nodeW, nh, nodeType, edgeRad, margin, stdTs, btnSizeSmall, dark, darkest, light, textCol, textDark, lighter, lightest, border, red, textYShift, nodeId, connectorPointId, nodePaths2, stdFont, this));
					Node<T> n = nodes.get(nodes.size() - 1);
					n.setIsGrabbed(false);
					n.getInputConnectorPoint().setIsConnected(isConnectorPointConnected);
					if (isConnectorPointConnected) {
						n.getInputConnectorPoint().setConnectedId(connectorPointConnectedId);
					}

				}
			}

			for (int i = 0; i < nodes.size(); i++) {
				Node n = nodes.get(i);
				if (n.getType() == 3) {
					n.updateSwitchConnectorPoints(false, null, true);
				}
			}

			calcConnectedNodes();
		}
	}

	public void onMousePressed(int mouseButton) {
		mouseIsPressed = true;
		if (mainButtons[0].getClickCount() % 2 == 0 && renderNodeMenu == false) {
			for (int i = 0; i < nodeEditorButtons.length; i++) {
				nodeEditorButtons[i].onMousePressed();
			}
		}

		if (renderNodeMenu) {
			for (int i = 0; i < nodeAdderButtons.length; i++) {
				nodeAdderButtons[i].onMousePressed();
			}
		} else {
			if (doOnceOnPressed == 0 && mouseButton == p.CENTER) {
				panViewStartX = p.mouseX;
				panViewStartY = p.mouseY;
				allNodePosOnMousePressed.clear();
				for (int i = 0; i < nodes.size(); i++) {
					Node n = nodes.get(i);
					allNodePosOnMousePressed.add(new PVector(n.getX(), n.getY()));
				}
				middleMouseWasPressed = true;
				doOnceOnPressed++;
			}
			for (int i = 0; i < nodes.size(); i++) {
				Node n = nodes.get(i);
				n.onMousePressed(mouseButton);
			}
			for (int i = 0; i < mainButtons.length; i++) {
				if (mainButtons[0].getClickCount() % 2 == 0 || i == 0) {
					mainButtons[i].onMousePressed();
				}
			}
		}

	}

	public void onMouseReleased(int mouseButton) {
		doOnceOnPressed = 0;
		mouseIsPressed = false;
		middleMouseWasPressed = false;
		if (mainButtons[0].getClickCount() % 2 == 0 && renderNodeMenu == false) {
			for (int i = 0; i < nodeEditorButtons.length; i++) {
				nodeEditorButtons[i].onMouseReleased();
			}
		}

		if (renderNodeMenu) {
			for (int i = 0; i < nodeAdderButtons.length; i++) {
				nodeAdderButtons[i].onMouseReleased();
			}

			if (p.mouseX > p.width / 2 - nodeAdderBoxW / 2 && p.mouseX < p.width / 2 + nodeAdderBoxW / 2 && p.mouseY > p.height / 2 - nodeAdderBoxH / 2 && p.mouseY < p.height / 2 + nodeAdderBoxH / 2) {
			} else {
				renderNodeMenu = false;
			}
		} else {

			if (mouseButton == p.CENTER) {

			}

			for (int i = 0; i < nodes.size(); i++) {
				Node n = nodes.get(i);
				n.onMouseReleased(mouseButton);
			}

			for (int i = 0; i < mainButtons.length; i++) {
				if (mainButtons[0].getClickCount() % 2 == 0 || i == 0) {
					mainButtons[i].onMouseReleased();
				}
			}
		}
	}

	public void onKeyPressed(char key) {

		for (int i = 0; i < nodes.size(); i++) {
			Node n = nodes.get(i);
			if (n.getType() == 3) {
				n.getSwitchNameEditText().onKeyPressed(key);
			}
		}

	
		if (key == 13) {

			Boolean noEditTextAcitve = true;
			for (int i = 0; i < nodes.size(); i++) {
				Node n = nodes.get(i);
				if (n.getType() == 3) {
					n.getSwitchNameEditText().setIsActive(false);
				}
			}

			if (noEditTextAcitve) {
				panViewStartX = p.mouseX;
				panViewStartY = p.mouseY;
				allNodePosOnMousePressed.clear();
				for (int i = 0; i < nodes.size(); i++) {
					Node n = nodes.get(i);
					allNodePosOnMousePressed.add(new PVector(n.getX(), n.getY()));
				}
				mouseIsPressed = true;
				middleMouseWasPressed = true;
			}
		} else {
			middleMouseWasPressed = false;
			mouseIsPressed = false;
		}
	
	}

	public void onKeyReleased(char k) {
		middleMouseWasPressed = false;
		mouseIsPressed = false;

		if (renderNodeMenu == false) {
			if (mainButtons[0].getClickCount() % 2 == 0 && renderNodeMenu == false) {
				for (int i = 0; i < nodeEditorButtons.length; i++) {
					nodeEditorButtons[i].onKeyReleased(k);
				}
			}

			for (int i = 0; i < nodes.size(); i++) {
				Node n = nodes.get(i);
				n.onKeyReleased(k);
			}
		}
	}

	public void onScroll(float e) {
		if (renderNodeMenu == false) {
			for (int i = 0; i < nodes.size(); i++) {
				Node n = nodes.get(i);
				n.onScroll(e);
			}
		}
	}

	public Boolean getIsSetup() {
		return isSetup;
	}

	public void removeConnectorPoint(String remId) {
		ConnectorPoint remCon;
		for (int i = 0; i < connectorPoints.size(); i++) {
			ConnectorPoint cp = connectorPoints.get(i);
			if (cp.getId().equals(remId)) {
				remCon = connectorPoints.get(i);
				connectorPoints.remove(remCon);
				break;
			}
		}

	}

	public void addConnectorPoint(PApplet _p, int _type, int _x, int _y, int _r, int _strWeight, int _col, Boolean _isParented, int[] _connectableTypes, String _id, String _parentId, T _parent) {
		connectorPoints.add(new ConnectorPoint(_p, _type, _x, _y, _r, _strWeight, _col, _isParented, _connectableTypes, _id, _parentId, _parent));
	}

	public int getMode() {
		return mode;
	}

	public ArrayList getNodes() {
		return nodes;
	}

	public Node getMasterNode() {
		Node masterNode = null;
		calcConnectedNodes();

		if (allConnectedNodes.size() > 0) {
			for (int i = 0; i < allConnectedNodes.size(); i++) {
				Node n = allConnectedNodes.get(i);
				if (n.getPcSelection_DropdownMenu().getSelectedItem().toUpperCase().equals(mainActivity.getPCName().toUpperCase())) {
					masterNode = n;
					break;
				}
			}
		}
		//p.println(masterNode.getPcSelection_DropdownMenu().getSelectedItem(),"--");
		return masterNode;
	}

	public ArrayList<ConnectorPoint> getConnectorPoints() {
		return connectorPoints;
	}

	public ArrayList<Node> getAllConnectedNodes() {
		return allConnectedNodes;
	}
}
