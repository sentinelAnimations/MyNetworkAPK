package com.dominic.network_apk;

import java.lang.reflect.Method;

import processing.core.PApplet;
import processing.core.PFont;

public class TimeField<T> implements Widgets {
    private int x, y, w, h, xShift, yShift, stdTs, margin, edgeRad, textCol, bgCol;
    private String time = "", prefix, postfix;
    private Boolean isParented, renderBg;
    private PFont stdFont;
    private T parent;
    private PApplet p;
    private MainActivity mainActivity;

    public TimeField(PApplet p, int x, int y, int stdTs, int margin, int edgeRad, int textCol, int bgCol, Boolean isParented, Boolean renderBg, String prefix, String postfix, PFont stdFont, T parent) {
        this.p = p;
        this.x = x;
        this.y = y;
        this.stdTs = stdTs;
        this.margin = margin;
        this.edgeRad = edgeRad;
        this.textCol = textCol;
        this.bgCol = bgCol;
        this.isParented = isParented;
        this.renderBg = renderBg;
        this.prefix = prefix;
        this.postfix = postfix;
        this.stdFont = stdFont;
        this.parent = parent;
        mainActivity = (MainActivity) p;
        xShift = x;
        yShift = y;

        calcTime();
    }

    public void render() {
        if (isParented) {
            getParentPos();
        }

        if (renderBg) {
            p.fill(bgCol);
            p.stroke(bgCol);
            p.rect(x, y, w, h, edgeRad);
        }
        calcTime();
        p.fill(textCol);
        p.textFont(stdFont);
        p.textAlign(p.CENTER, p.CENTER);
        p.textSize(stdTs);
        p.text(time, x, y);
    }

    private void calcTime() {
        time = prefix + p.str(p.hour()) + " : " + p.str(p.minute()) + " : " + p.str(p.second()) + postfix;
        w = (int) p.textWidth(time) + margin * 2;
        h = stdTs + margin * 2;
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

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public Boolean mouseIsInArea() {
        if (p.mouseX > x - w / 2 && p.mouseX < x + w / 2 && p.mouseY > y - h / 2 && p.mouseY < y + h / 2) {
            return true;
        } else {
            return false;
        }
    }

    public String getTimeString() {
        return time;
    }
    public void setPos(int xp,int yp) {
        x=xp;
        y=yp;
        xShift=x;
        yShift=y;
    }
}
