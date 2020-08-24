package com.dominic.network_apk;

import java.util.ArrayList;
import java.util.UUID;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class NodeEditor<T> {
    private int nodeW, nodeH, nodeAdderBoxW, nodeAdderBoxH, btnSize, btnSizeSmall, gridSize, margin, stdTs, edgeRad, dark, darkest, light, lighter, lightest, border, textCol, textDark;
    private float textYShift;
    private Boolean renderNodeMenu = false;
    private String[] nodePaths1, nodePaths2, pcPaths;
    private PFont stdFont;
    private PImage screenshot;
    private PApplet p;
    private MainActivity mainActivity;
    private ImageButton[] mainButtons;
    private ImageButton[] nodeEditorButtons = new ImageButton[4];
    private ImageButton[] nodeAdderButtons = new ImageButton[5];
    private ArrayList<Node> nodes = new ArrayList<>();
    private ArrayList<ConnectorPoint> connectorPoints = new ArrayList<ConnectorPoint>();
    private ArrayList<Node> allConnectedNodes = new ArrayList<Node>();

    public NodeEditor(PApplet p, int btnSize, int btnSizeSmall, int margin, int stdTs, int edgeRad, int dark, int darkest, int light, int lighter, int lightest, int border, int textCol, int textDark, float textYShift, String[] buttonPaths, String[] nodePaths1, String[] nodePaths2, PFont stdFont) {
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
        this.nodePaths1 = nodePaths1;
        this.nodePaths2 = nodePaths2;
        this.stdFont = stdFont;
        this.p = p;
        nodeW = (int) (btnSize * 2.5f);
        nodeH = (int) (btnSize * 2.5f);
        mainActivity = (MainActivity) p;
        gridSize = btnSizeSmall / 2;
        mainButtons = mainActivity.getMainButtons();

        String[] btnDescriptions = { "Pan View | shorcut: ctrl+p", "Add node | shortcut: ctrl+a", "Focus/center nodes | shortcut: ctrl+f ", "Save | shortcut: ctrl+s" };
        int[] shortcuts = { 13, 1, 6, 19 };
        for (int i = 0; i < nodeEditorButtons.length; i++) {
            nodeEditorButtons[i] = new ImageButton(p, mainButtons[0].getX(), mainButtons[0].getY() + mainButtons[0].getH() + margin + btnSize * i + margin * i, btnSize, btnSize, stdTs, margin, edgeRad, shortcuts[i], textYShift, true, false, textCol, light, buttonPaths[i], btnDescriptions[i], null);
        }

        String[] nodeAdderBtnDescriptions = { "Add Master-PC", "Add Slave-PC", "Add Laptop", "Add Switch", "Add Output" };
        for (int i = 0; i < nodeAdderButtons.length; i++) {
            nodeAdderButtons[i] = new ImageButton(p, p.width / 2 - (nodeAdderButtons.length / 2) * btnSize - (nodeAdderButtons.length / 2) * margin + i * btnSize + i * margin, p.height / 2, btnSize, btnSize, stdTs, margin, edgeRad, -1, textYShift, true, false, textCol, light, nodePaths1[i], nodeAdderBtnDescriptions[i], null);
        }
        nodeAdderBoxW = nodeAdderButtons.length * btnSize + nodeAdderButtons.length * margin + margin;
        nodeAdderBoxH = btnSize + margin * 2;

    }

    public void render() {
        renderGrid();
        if (renderNodeMenu == false) {

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
        mainActivity.renderMainButtons();

        if (mainButtons[0].getClickCount() % 2 == 0) {
            for (int i = 0; i < nodeEditorButtons.length; i++) {
                nodeEditorButtons[i].render();
            }

            int rectH = p.height - (nodeEditorButtons[nodeEditorButtons.length - 1].getY() + nodeEditorButtons[nodeEditorButtons.length - 1].getH() / 2 + margin * 2);
            int rectY = nodeEditorButtons[nodeEditorButtons.length - 1].getY() + nodeEditorButtons[nodeEditorButtons.length - 1].getH() / 2 + margin + rectH / 2;
            p.fill(light);
            p.stroke(light);
            p.rect(nodeEditorButtons[nodeEditorButtons.length - 1].getX(), rectY, btnSize, rectH, edgeRad);
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
        handleButtons();

        if (p.frameCount % 10 == 0) {
            calcConnectedNodes();
        }
    }

    public void renderGrid() {
        int alpha = 5;
        for (int i = 0; i < p.width; i += gridSize) {
            for (int i2 = 0; i2 < p.height; i2 += gridSize) {
                p.noFill();
                p.stroke(255, alpha);
                p.rect(i, i2, gridSize, gridSize);
            }
        }
    }

    private void calcConnectedNodes() {
        
        allConnectedNodes.clear();

        // search for output node -------------
        Node outputNode=null;
        for (int i = 0; i < nodes.size(); i++) {
            Node n = nodes.get(i);
            if (n.getType() == nodeAdderButtons.length - 1) {
                outputNode=n;
                n.getInputConnectorPoint().setCheckedForConnection(false);
            }
            if(n.getIsTypePc()) {
                n.getOutputConnectorPoint().setCheckedForConnection(false);
            }
            if(n.getType()==3) {
                for(int i2=0;i2<n.getSwitchConnectorPoints().size();i2++) {
                   ConnectorPoint cp= (ConnectorPoint) n.getSwitchConnectorPoints().get(i2);
                   cp.setCheckedForConnection(false);
                }
            }
            
        }
        if(outputNode!=null) {
        recursiveGetConnectedNodes(outputNode);
        }
        // search for output node -------------

    }

    private void recursiveGetConnectedNodes(Node n) {
        Boolean uncheckedConnector=false;
        if(n.getIsTypePc()) {
            if(n.getOutputConnectorPoint().getCheckedForConnection()==false) {
                uncheckedConnector=true;
            }
        }
        if(n.getType()==3) {
            for(int i=0;i<n.getSwitchConnectorPoints().size();i++) {
                ConnectorPoint cp = (ConnectorPoint) n.getSwitchConnectorPoints().get(i);
                if(cp.getCheckedForConnection()==false) {
                 uncheckedConnector=true;   
                }
            }
        }
        if(n.getType()==nodeAdderButtons.length-1) {
            if(n.getInputConnectorPoint().getCheckedForConnection()==false) {
                uncheckedConnector=true;
            }
        }
        
        if(uncheckedConnector) {
        if (n.getIsTypePc()) {
            allConnectedNodes.add(n);
            n.getOutputConnectorPoint().setCheckedForConnection(true);
        }

        if (n.getType() == 3) {
            for (int i = 0; i < n.getSwitchConnectorPoints().size(); i++) {
                ConnectorPoint cp = (ConnectorPoint) n.getSwitchConnectorPoints().get(i);
                cp.setCheckedForConnection(true);
                if (cp.getIsConnected()) {
                    for (int i2 = 0; i2 < nodes.size(); i2++) {
                        Node n2 = nodes.get(i2);

                        if (n2.getIsTypePc()) {
                            ConnectorPoint cp2 = n2.getOutputConnectorPoint();
                            if (cp.getConnectedId().equals(cp2.getId())) {
                                allConnectedNodes.add(n2);
                                break;
                            }
                        }

                        if (n2.getType() == 3) {
                            for (int i3 = 0; i3 < n2.getSwitchConnectorPoints().size(); i3++) {
                                ConnectorPoint cp2 = (ConnectorPoint) n2.getSwitchConnectorPoints().get(i2);
                                if (cp.getIsConnected()) {
                                    recursiveGetConnectedNodes(n2);
                                    break;
                                }
                            }
                        }

                    }
                }
            }
        }
        }

        if (n.getType() == nodeAdderButtons.length - 1) {
            n.getInputConnectorPoint().setCheckedForConnection(true);
            if (n.getInputConnectorPoint().getIsConnected()) {
                for (int i = 0; i < nodes.size(); i++) {
                    Node n2 = nodes.get(i);
                    if (n2.getIsTypePc()) {
                        if (n2.getOutputConnectorPoint().getId().equals(n.getInputConnectorPoint().getId())) {
                            allConnectedNodes.add(n2);
                            break;
                        }
                    }
                    if (n2.getType() == 3) {
                        for (int i2 = 0; i2 < n2.getSwitchConnectorPoints().size(); i2++) {
                            ConnectorPoint cp = (ConnectorPoint) n2.getSwitchConnectorPoints().get(i2);
                            if (cp.getIsConnected()) {
                                recursiveGetConnectedNodes(n2);
                                break;
                            }
                        }
                    }

                }
            }
        }
    }

    private void handleButtons() {
        for (int i = 0; i < nodeEditorButtons.length; i++) {
            if (nodeEditorButtons[i].getIsClicked() == true) {
                switch (i) {
                case 0:
                    break;
                case 1:
                    renderNodeMenu = true;
                    p.saveFrame("data\\imgs\\screenshots\\homeScreen.png");
                    screenshot = p.loadImage("data\\imgs\\screenshots\\homeScreen.png");
                    screenshot = new ImageBlurHelper(p).blur(screenshot, 3);
                    break;
                case 2:
                    break;
                case 3:
                    break;
                }

                nodeEditorButtons[i].setIsClicked(false);
            }
        }

        for (int i = 0; i < nodeAdderButtons.length; i++) {
            if (nodeAdderButtons[i].getIsClicked() == true) {
                int nh = nodeH;
                if (i == nodeAdderButtons.length - 1) {
                    nh = btnSizeSmall + stdTs * 2 + margin * 7;
                }
                String nodeId = UUID.randomUUID().toString();
                nodes.add(new Node(p, p.mouseX, p.mouseY, nodeW, nh, i, edgeRad, margin, stdTs, btnSizeSmall, dark, darkest, light, textCol, textDark, lighter, lightest, border, textYShift, nodeId, nodePaths2, stdFont, this));
                renderNodeMenu = false;
                nodeAdderButtons[i].setIsClicked(false);
            }
        }
    }

    public void onMousePressed(int mouseButton) {
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

    public void onKeyReleased(char k) {
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

    public ArrayList getNodes() {
        return nodes;
    }

    public ArrayList<ConnectorPoint> getConnectorPoints() {
        return connectorPoints;
    }
}
