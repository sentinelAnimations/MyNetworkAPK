package com.dominic.network_apk;

import java.lang.reflect.Method;

import processing.core.PApplet;
import processing.core.PFont;

public class ColorPicker<T> implements Widgets {
    private int x, y, xShift, yShift, w, h, r, dark, stdTs, edgeRad, accuracy, light, lighter, textCol;
    private Boolean isParented;
    private float textYShift, brightness;
    private PFont stdFont;
    private PApplet p;
    private T parent;

    public ColorPicker(PApplet p, int x, int y, int w, int h, int r, int dark, int stdTs, int edgeRad, int accuracy, int light, int lighter, int textCol, float textYShift, Boolean isParented, PFont stdFont, T parent) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.r = r;
        this.dark = dark;
        this.stdTs = stdTs;
        this.edgeRad = edgeRad;
        this.accuracy = accuracy;
        this.light = light;
        this.lighter = lighter;
        this.textCol = textCol;
        this.isParented = isParented;
        this.textYShift = textYShift;
        this.stdFont = stdFont;
        this.p = p;
        this.parent = parent;

        xShift = x;
        yShift = y;
    }

    public void render() {
        if (isParented) {
            getParentPos();
        }

        for (int i = -r; i < r; i += accuracy) {
            for (int i2 = -r; i2 < r; i2 += accuracy) {
                if (p.dist(x + i, y + i2, x, y) < r) {
                    int col = getColOfPos(x, y, x + i2, y + i);
                    int convertetCol = p.color(p.red(col) * 2 + 255 * brightness, p.green(col) * 2 + 255 * brightness, p.blue(col) * 2 + 255 * brightness);
                    p.stroke(convertetCol);
                    p.fill(convertetCol);
                    // fill(red(col)*brightness, green(col)*brightness, blue(col)*brightness);
                    p.rect(x + i2, y + i, accuracy, accuracy);
                }
            }
        }
        p.strokeWeight(3);
        p.stroke(light);
        p.noFill();
        p.ellipse(x, y, r * 2, r * 2);
        p.strokeWeight(1);

        if (p.dist(p.mouseX, p.mouseY, x, y) < r) {
            int col = getColOfPos(x, y, p.mouseX, p.mouseY);
            p.stroke(light);
            p.fill(p.red(col) * 2 + 255 * brightness, p.green(col) * 2 + 255 * brightness, p.blue(col) * 2 + 255 * brightness);
            p.ellipse(p.mouseX, p.mouseY, 20, 20);
        }
    }
    
    private void calcRGBCircle() {
        
    }
 

    private int getColOfPos(float cx, float cy, float xp, float yp) {
        float ar = 0, ag = 0, ab = 0, d = 255;
        float r = 0, g = 0, b = 0;
        ar = getAngle(cx, cy, xp, yp);
        ab = ar - 120;
        if (ab < 0) {
            ab = (360 - 120) + (120 - p.abs(ab));
        }

        ag = ar - 240;
        if (ag < 0) {
            ag = (360 - 240) + (240 - p.abs(ag));
        }
        if (ar < 240) {
            r = p.map(p.abs(120 - ar), 0, 120, 0, 1) * d;
            if (ar >= 120) {
                r = 0;
            }
        } else {
            r = p.map(p.abs(240 - ar), 0, 120, 0, 1) * d;
        }

        if (ag < 240) {
            g = p.map(p.abs(120 - ag), 0, 120, 0, 1) * d;
            if (ag >= 120) {
                g = 0;
            }
        } else {
            g = p.map(p.abs(240 - ag), 0, 120, 0, 1) * d;
        }

        if (ab < 240) {
            b = p.map(p.abs(120 - ab), 0, 120, 0, 1) * d;
            if (ab >= 120) {
                b = 0;
            }
        } else {
            b = p.map(p.abs(240 - ab), 0, 120, 0, 1) * d;
        }
        // r=map(abs(180-ar), 0, 180, 0, 1)*dist(cx, cy, xp, yp);
        // g=map(abs(180-ag), 0, 180, 0, 1)*dist(cx, cy, xp, yp);
        // b=map(abs(180-ab), 0, 180, 0, 1)*dist(cx, cy, xp, yp);

        int c = p.color(r, g, b);
        return c;
    }

    private float getAngle(float cx, float cy, float xp, float yp) {
        float angle = 0, geg = 0, an = 0;

        geg = cy - yp;
        an = cx - xp;
        angle = p.abs(p.atan(geg / an));

        if (yp <= cy && xp <= cx) {
            angle = p.PI / 2 + (p.PI / 2 - p.abs(p.atan(geg / an)));
        }
        if (yp >= cy && xp <= cx) {
            angle = p.PI + p.abs(p.atan(geg / an));
        }
        if (yp >= cy && xp >= cx) {
            angle = p.PI + p.PI / 2 + (p.PI / 2 - p.abs(p.atan(geg / an)));
        }
        return p.degrees(angle);
    }

    public void onMousePressed() {

    }

    public void onMoueseReleased() {

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
        if (p.mouseX > x - w / 2 && p.mouseX < x + w / 2 && p.mouseY > y - h / 2 && p.mouseY < y + h / 2) {
            return true;
        } else {
            return false;
        }
    }

    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(float val) {
        brightness = val;
    }

}
