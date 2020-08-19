package com.dominic.network_apk;

import java.lang.reflect.Method;
import java.util.ArrayList;

import processing.core.PApplet;

public class ConnectorPoint<T> implements Widgets {
	private int ind, parentInd, connectedInd,updatedConnectedInd, type, x, y, xShift, yShift, connectedX, connectedY, r, strWeight, possibleConnections, col;
	private Boolean isParented, isConnected = false, isPressed = false, isOnDrag = false;
	private PApplet p;
	private int[] connectableTypes;
	private T parent;
	private ConnectorPoint connected_connectorPoint;
	private ArrayList<ConnectorPoint> connectorPoints = new ArrayList<ConnectorPoint>();

	public ConnectorPoint(PApplet p, int ind, int parentInd, int type, int x, int y, int r, int strWeight, int col, Boolean isParented, int[] connectableTypes, T parent) {
		this.p = p;
		this.ind = ind;
		this.parentInd = parentInd;
		this.type = type;
		this.x = x;
		this.y = y;
		this.r = r;
		this.strWeight = strWeight;
		this.possibleConnections = possibleConnections;
		this.col = col;
		this.isParented = isParented;
		this.connectableTypes = connectableTypes;
		this.parent = parent;

		xShift = x;
		yShift = y;
		
		p.println(ind);

	}

	public void render() {
		if (isParented) {
			getParentPos();
		}
		if (isPressed) {
			if (mouseIsInArea() && !isOnDrag) {
				Boolean anotherIsOnDrag=false;
				for(int i=0;i<getAllConnectorPoints().size();i++) {
					ConnectorPoint cp=getAllConnectorPoints().get(i);
					if(cp.getIsOnDrag()) {
						anotherIsOnDrag=true;
					}
				}
				if(anotherIsOnDrag) {
				}else {
				if(isConnected) {
					isConnected=false;
					connected_connectorPoint.setIsConnected(false);
				}
				isOnDrag = true;
				}
				
			}
		}

		if (isOnDrag) {
			renderCurve(x, y, p.mouseX, p.mouseY);
		}
		if (isConnected) {
			connectorPoints = getAllConnectorPoints();
			//p.println(connectorPoints.size(),"-------");

			 updatedConnectedInd=0;
			for(int i=0;i<connectorPoints.size();i++) {
				ConnectorPoint cp=connectorPoints.get(i);
				if(cp.getInd()==connectedInd) {
					updatedConnectedInd=i;
					p.println(i,"now");
					break;
				}
			}
			//p.println(updatedConnectedInd,"--");
			//if(updatedConnectedInd>=0&&updatedConnectedInd<connectorPoints.size()) {
			connected_connectorPoint = connectorPoints.get(updatedConnectedInd);
			connectedX = connected_connectorPoint.getX();
			connectedY = connected_connectorPoint.getY();
			renderCurve(x, y, connectedX, connectedY);
			//}
			
			
		}
		p.noFill();
		p.stroke(255, 0, 0);
		p.ellipse(x, y, r * 2, r * 2);
		p.textSize(10);
		p.textAlign(p.CENTER,p.CENTER);
		p.fill(255);
		p.text(ind,x,y);
	}

	public void renderCurve(int x1, int y1, int x2, int y2) {
		p.strokeWeight(strWeight);
		p.stroke(col);
		p.line(x1, y1, x2, y2);
		p.strokeWeight(1);
	}

	public void onMousePressed() {
		isPressed = true;
	}

	public void onMouseReleased() {

		if (isOnDrag) {
			for (int i = 0; i < getAllConnectorPoints().size(); i++) {
				ConnectorPoint cp = getAllConnectorPoints().get(i);
				if (i != ind && parentInd != cp.getParentInd()) {
					if (cp.mouseIsInArea() && cp.getIsConnected() == false) {
						for (int i2 = 0; i2 < connectableTypes.length; i2++) {
							if (connectableTypes[i2] == cp.getType()) {
								connectedInd = i;
								isConnected = true;
								cp.setConnectedInd(ind);
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

	public int getConnectedInd() {
		return connectedInd;
	}

	public int getInd() {
		return ind;
	}
	public int getUpdatedConnectedInd() {
		return updatedConnectedInd;
	}
	
	public int getParentInd() {
		return parentInd;
	}

	public int getType() {
		return type;
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

	public void setConnectedInd(int conInd) {
		connectedInd = conInd;
		p.println("ind set");
	}

}
