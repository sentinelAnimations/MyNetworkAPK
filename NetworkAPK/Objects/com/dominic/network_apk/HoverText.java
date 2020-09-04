package com.dominic.network_apk;

import java.lang.reflect.Method;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;

public class HoverText<T> {
    private int parentX, parentY, parentW, parentH, stdTs, margin, edgeRad, textCol, hoverTime = 0,maxShowTime=0;
    private float textYShift;
    private Boolean mouseIsInArea = false, isHovering = false;
    private String infoText;
    private PFont stdFont;
    private PApplet p;
    private T parent;

    public HoverText(PApplet p, int stdTs, int margin, int edgeRad,int maxShowTime, int textCol, float textYShift, String infoText, PFont stdFont, T parent) {
        this.p = p;
        this.stdTs = stdTs;
        this.margin = margin;
        this.edgeRad = edgeRad;
        this.maxShowTime=maxShowTime;
        this.textCol = textCol;
        this.textYShift = textYShift;
        this.stdFont = stdFont;
        this.infoText = infoText;
        this.parent = parent;
    }

    public void render() {
        getParentPos();
        Boolean show = false;
        if (infoText.length() > 0) {
            if (mouseIsInArea()) {
                if (isHovering) {
                    hoverTime++;
                }
                isHovering = true;
            } else {
                hoverTime = 0;
                isHovering = false;
            }
            if (hoverTime > 72) {
                int tw = (int) p.textWidth(infoText) + margin * 2;
                int mx, my;
                if (p.mouseX + tw < p.width) {
                    p.textAlign(PConstants.RIGHT, PConstants.CENTER);
                } else {
                    tw *= -1;
                    p.textAlign(PConstants.LEFT, PConstants.CENTER);
                }
                mx = p.mouseX;
                my = p.mouseY;
                if (p.mouseY < stdTs) {
                    my = stdTs;
                }
                if (p.mouseY > p.height - stdTs * 2) {
                    my = p.height - stdTs * 2;
                }

                if (hoverTime > maxShowTime) {
                    show = false;
                } else {
                    show = true;
                }
                if (show) {
                    p.fill(255 - p.brightness(textCol));
                    p.noStroke();
                    p.rect(mx + tw / 2, my + stdTs, PApplet.abs(tw) + margin * 2, stdTs * 2, edgeRad);
                    p.fill(textCol);
                    p.text(infoText, mx + tw, my + stdTs - stdTs * textYShift);
                }
            }
        }
    }

    public Boolean mouseIsInArea() {
        if (p.mouseX > parentX - parentW / 2 && p.mouseX < parentX + parentW / 2 && p.mouseY > parentY - parentH / 2 && p.mouseY < parentY + parentH / 2) {
            return true;
        } else {
            return false;
        }
    }

    public void getParentPos() {
        Method m;
        try {

            m = parent.getClass().getMethod("getX");
            parentX = (int) m.invoke(parent);
            m = parent.getClass().getMethod("getY");
            parentY = (int) m.invoke(parent);

            m = parent.getClass().getMethod("getW");
            parentW = (int) m.invoke(parent);
            m = parent.getClass().getMethod("getH");
            parentH = (int) m.invoke(parent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setInfoText(String newT) {
        infoText=newT;
    }

}
