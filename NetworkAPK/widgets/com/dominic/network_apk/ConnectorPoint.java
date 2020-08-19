package com.dominic.network_apk;

import java.lang.reflect.Method;

import processing.core.PApplet;

public class ConnectorPoint<T> implements Widgets {    
    private int ind,parentInd,connectedInd,x,y,xShift,yShift,connectedX,connectedY,r,strWeight,col;
    private Boolean isParented,isConnected=false,isPressed,isOnDrag=false;
    private PApplet p;
    private T parent;
    

    public ConnectorPoint(PApplet p,int ind,int parentInd,int x, int y, int r,int strWeight, int col, Boolean isParented, T parent) {
        this.ind=ind;
        this.parentInd=parentInd;
        this.x = x;
        this.y = y;
        this.r = r;
        this.strWeight=strWeight;
        this.col = col;
        this.isParented = isParented;
        this.p = p;
        this.parent = parent;
        
        xShift=xShift;
        yShift=yShift;
    }
    
    public void render() {
        if(isParented) {
            getParentPos();
        }
        if(isPressed) {
            if(!isOnDrag) {
                isOnDrag=true;
            }
        }
        
        if(isOnDrag) {
            renderCurve(x, y, p.mouseX, p.mouseY);
        }
        
        p.stroke(strWeight);
        if(isConnected) {
           // ConnectorPoint connected_connectorPoint=
        renderCurve(x, y, connectedX, connectedY);
        }
        p.fill(255,0,0);
        p.ellipse(x,y,r*2,r*2);
    }
    
    public void renderCurve(int x1,int y1,int x2,int y2) {
        p.line(x1,y1,x,y2);
    }
    
    public void onMousePressed() {
     isPressed=true;   
    }
    
    public void onMouseReleased() {
        isPressed=false;
        isOnDrag=false;
    }

    @Override
    public void getParentPos() {
        Method m;
        try {
            m = parent.getClass().getMethod("getX");
            x = (int) m.invoke(parent) + xShift;

            m = parent.getClass().getMethod("getY");
            y = (int) m.invoke(parent) + yShift;

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
        if (p.dist(p.mouseX,p.mouseY,x,y)<r) {
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
    
    public Boolean getIsConnected() {
        return isConnected;
    }
    
    public void setIsConnected(Boolean state) {
        isConnected=state;
    }

}
