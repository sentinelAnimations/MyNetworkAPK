package com.dominic.network_apk;

import java.lang.reflect.Method;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;

public class ConnectorPoint<T> implements Widgets {
	private int connectedInd, type, x, y, xShift, yShift, connectedX, connectedY, r, strWeight, possibleConnections, col, idLength;
	private float handlerWeight = 0;
	private Boolean isParented, isConnected = false, isPressed = false, isOnDrag = false;
	private String id, parentId, connectedId, updatedConnectedId;
	private PApplet p;
	private int[] connectableTypes;
	private PVector[] bezierPoints = new PVector[4];
	private T parent;
	private ConnectorPoint connected_connectorPoint;
	private ArrayList<ConnectorPoint> connectorPoints = new ArrayList<ConnectorPoint>();

	public ConnectorPoint(PApplet p, int type, int x, int y, int r, int strWeight, int col, Boolean isParented, int[] connectableTypes, String id, String parentId, T parent) {
		this.p = p;
		this.type = type;
		this.x = x;
		this.y = y;
		this.r = r;
		this.strWeight = strWeight;
		this.possibleConnections = possibleConnections;
		this.col = col;
		this.isParented = isParented;
		this.connectableTypes = connectableTypes;
		this.id = id;
		this.parentId = parentId;
		this.parent = parent;

		xShift = x;
		yShift = y;

		idLength = 0;
		for (int i = 0; i < id.length(); i++) {
			idLength += (int) (id.charAt(i));
		}

	}

	public void render() {
		if (isParented) {
			getParentPos();
		}
		if (isPressed) {
			if (mouseIsInArea() && !isOnDrag) {
				Boolean anotherIsOnDrag = false;
				for (int i = 0; i < getAllConnectorPoints().size(); i++) {
					ConnectorPoint cp = getAllConnectorPoints().get(i);
					if (cp.getIsOnDrag()) {
						anotherIsOnDrag = true;
					}
				}
				if (anotherIsOnDrag) {
				} else {
					if (isConnected) {
						isConnected = false;

						if (connected_connectorPoint != null) {
							connected_connectorPoint.setIsConnected(false);
						}

					}
					isOnDrag = true;
				}

			}
		}

		if (isOnDrag) {
			renderCurve(new PVector(x, y), new PVector(p.mouseX, p.mouseY), false);
			p.fill(col);
			p.ellipse(p.mouseX, p.mouseY, r, r);
		}
		if (isConnected) {
			connectorPoints = getAllConnectorPoints();

			// updatedConnectedId="";
			connectedInd = -1;
			for (int i = 0; i < connectorPoints.size(); i++) {
				ConnectorPoint cp = connectorPoints.get(i);
				if (cp.getId().equals(connectedId)) {
					// updatedConnectedId=cp.getId();
					connectedInd = i;
					break;
				}
			}
			if (connectedInd < 0) {
				isConnected = false;
			}

			if (isConnected) {
				connected_connectorPoint = connectorPoints.get(connectedInd);
				connectedX = connected_connectorPoint.getX();
				connectedY = connected_connectorPoint.getY();
				if (idLength > connected_connectorPoint.getIdLength()) {
					renderCurve(new PVector(x, y), new PVector(connectedX, connectedY), false);
				}
				// renderCurve(new PVector(x, y), new PVector(connectedX, connectedY),false);

			} else {
				connected_connectorPoint = null;
			}

		}
		/*
		 * p.textSize(10); p.textAlign(p.CENTER, p.CENTER); p.fill(255);
		 * p.text(id.substring(id.length()-5,id.length()), x, y); if(isConnected) {
		 * p.text(connectedId.substring(connectedId.length()-5,connectedId.length()), x,
		 * y+10); }
		 */

		/*
		 * p.noFill(); p.stroke(255, 0, 0); p.ellipse(x, y, r * 2, r * 2);
		 * p.textSize(10); p.textAlign(p.CENTER, p.CENTER); p.fill(255); p.text(id,x,y);
		 */
	}

	void renderCurve(PVector pv1, PVector pv2, Boolean isBezier) {

		if (!isBezier) {
			p.strokeWeight(strWeight);
			p.stroke(col);
			p.line(pv1.x, pv1.y, pv2.x, pv2.y);
			p.strokeWeight(1);
		} else {
			PVector bt, prevBt, p1 = pv1, p2 = pv2;

			if (pv1.x < pv2.x) {
				p1 = pv1;
				p2 = pv2;
			} else {
				p1 = pv2;
				p2 = pv1;
			}

			float subdiv = p.dist(p1.x, p1.y, p2.x, p2.y) / 30;
			if (subdiv < 10) {
				subdiv = 10;
			}

			bezierPoints[0] = new PVector(p1.x, p1.y);
			bezierPoints[1] = new PVector(bezierPoints[0].x + handlerWeight, bezierPoints[0].y);
			bezierPoints[3] = new PVector(p2.x, p2.y);
			bezierPoints[2] = new PVector(bezierPoints[3].x - handlerWeight, bezierPoints[3].y);
			handlerWeight = p.abs(bezierPoints[0].x - bezierPoints[3].x) / 2;

			prevBt = bezierPoints[0];
			p.stroke(col);
			p.strokeWeight(strWeight);
			for (float i = 1 / subdiv; i <= 1; i += 1 / subdiv) {
				bt = new PVector(p.pow((1 - i), 3) * bezierPoints[0].x + 3 * p.pow((1 - i), 2) * i * bezierPoints[1].x + 3 * (1 - i) * p.pow(i, 2) * bezierPoints[2].x + p.pow(i, 3) * bezierPoints[3].x, p.pow((1 - i), 3) * bezierPoints[0].y + 3 * p.pow((1 - i), 2) * i * bezierPoints[1].y + 3 * (1 - i) * p.pow(i, 2) * bezierPoints[2].y + p.pow(i, 3) * bezierPoints[3].y);
				p.line(bt.x, bt.y, prevBt.x, prevBt.y);
				prevBt = bt;
			}
			p.line(prevBt.x, prevBt.y, bezierPoints[3].x, bezierPoints[3].y);
			p.strokeWeight(1);
		}
	}

	public void onMousePressed() {
		isPressed = true;
	}

	public void onMouseReleased() {

		if (isOnDrag) {
			for (int i = 0; i < getAllConnectorPoints().size(); i++) {
				ConnectorPoint cp = getAllConnectorPoints().get(i);
				if (id.equals(cp.getId()) == false && parentId.equals(cp.getParentId()) == false) {
					if (cp.mouseIsInArea() && cp.getIsConnected() == false) {
						for (int i2 = 0; i2 < connectableTypes.length; i2++) {
							if (connectableTypes[i2] == cp.getType()) {
								connectedId = cp.getId();
								isConnected = true;
								cp.setConnectedId(id);
								cp.setIsConnected(true);
								break;
							}
						}
					}
				}
			}
		}

		isPressed = false;
		isOnDrag = false;
	}

	public ArrayList<ConnectorPoint> getAllConnectorPoints() {
		Method m;
		try {
			m = parent.getClass().getMethod("getConnectorPoints");
			return (ArrayList<ConnectorPoint>) m.invoke(parent);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void getParentPos() {
		Method m;
		try {
			m = parent.getClass().getMethod("getX");
			x = (int) m.invoke(parent) + xShift;

			m = parent.getClass().getMethod("getY");
			y = (int) m.invoke(parent) + yShift;

			m = parent.getClass().getMethod("getConnectorPoints");
			connectorPoints = (ArrayList<ConnectorPoint>) m.invoke(parent);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public Boolean mouseIsInArea() {
		if (p.dist(p.mouseX, p.mouseY, x, y) < r) {
			return true;
		} else {
			return false;
		}
	}

	public void setPos(int xp, int yp) {
		x = xp;
		xShift = x;
		y = yp;
		yShift = y;
	}

	public String getConnectedId() {
		return connectedId;
	}

	public String getId() {
		return id;
	}

	public String getUpdatedConnectedId() {
		return updatedConnectedId;
	}

	public String getParentId() {
		return parentId;
	}

	public int getType() {
		return type;
	}

	public int getIdLength() {
		return idLength;
	}

	public Boolean getIsOnDrag() {
		return isOnDrag;
	}

	public Boolean getIsConnected() {
		return isConnected;
	}

	public void setIsConnected(Boolean state) {
		isConnected = state;
	}

	public void setConnectedId(String conId) {
		connectedId = conId;
	}

}
