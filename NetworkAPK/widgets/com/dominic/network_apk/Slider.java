package com.dominic.network_apk;

import java.lang.reflect.Method;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class Slider<T> implements Widgets {
    private int x, y, sliderX, sliderY, sliderShiftX = 0, sliderShiftY = 0, xShift, yShift, startX, startY, w, h, margin, returnValBorderLow, returnValBorderHigh, startVal, d, dark, bgCol, sliderCol, stdTs, edgeRad, doOnce = 0;
    private float textYShift, startShift;
    private Boolean isParented, isHorizontalSlider, isPressed = false, isOnDrag = false, showDefault, showOnlyHandle,newValueSet=false;
    private PFont stdFont;
    private PApplet p;
    private T parent;

    public Slider(PApplet p, int x, int y, int w, int h, int d, int stdTs, int edgeRad, int margin, int returnValBorderLow, int returnValBorderHigh, int startVal, int dark, int bgCol, int sliderCol, float textYShift, Boolean isParented, Boolean isHorizontalSlider, Boolean showDefault, Boolean showOnlyHandle, PFont stdFont, T parent) {
        super();
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.d = d;
        this.dark = dark;
        this.stdTs = stdTs;
        this.edgeRad = edgeRad;
        this.margin = margin;
        this.returnValBorderLow = returnValBorderLow;
        this.returnValBorderHigh = returnValBorderHigh;
        this.startVal = startVal;
        this.bgCol = bgCol;
        this.sliderCol = sliderCol;
        this.textYShift = textYShift;
        this.isParented = isParented;
        this.isHorizontalSlider = isHorizontalSlider;
        this.showDefault = showDefault;
        this.showOnlyHandle = showOnlyHandle;
        this.stdFont = stdFont;
        this.p = p;
        this.parent = parent;

        startShift = p.map(startVal, returnValBorderLow, returnValBorderHigh, -w / 2 + d / 2, w / 2 - d / 2);
        xShift = x;
        yShift = y;
        if (!isParented) {
            if (isHorizontalSlider) {
                sliderShiftX += startShift;
                startX = (int) (x + startShift);
                startY = y;
            } else {
                sliderShiftY += startShift;
                startY = (int) (y + startShift);
                startX = x;
            }
        }

    }

    public void render() {
        if (isParented) {
            getParentPos();
        }

        if (isOnDrag) {

            if (isHorizontalSlider) {
                sliderShiftX = p.constrain(p.mouseX, x - w / 2 + d / 2, x + w / 2 - d / 2) - x;
                sliderShiftY = 0;
            } else {
                sliderShiftX = 0;
                sliderShiftY = p.constrain(p.mouseY, y - w / 2 + d / 2, y + w / 2 - d / 2) - y;
            }
        }

        sliderX = x + sliderShiftX;
        sliderY = y + sliderShiftY;
        p.noStroke();
        p.fill(bgCol);
        p.stroke(bgCol);
        if (showOnlyHandle) {
            if (isHorizontalSlider) {
                if (h > 2) {
                    p.rect(x, y, w, h, edgeRad);
                } else {
                    p.strokeWeight(h);
                    p.line(x - w / 2, y, x + w / 2, y);
                    p.strokeWeight(1);
                }
            } else {
                if (h > 2) {
                    p.rect(x, y, h, w, edgeRad);
                } else {
                    p.strokeWeight(h);
                    p.line(x, y - w / 2, x, y + w / 2);
                    p.strokeWeight(1);
                }
            }
        }
        if (showDefault) {
            p.noStroke();
            p.fill(sliderCol);
            p.ellipse(startX, startY, h, h);
        }
        renderHandle();

    }

    public void renderHandle() {
        p.noStroke();
        p.fill(sliderCol);
        p.ellipse(sliderX, sliderY, d, d);
        p.fill(bgCol);
        p.ellipse(sliderX, sliderY, d / 1.5f, d / 1.5f);
    }

    public void onMousePressed() {
        isPressed = true;
        if (p.dist(p.mouseX, p.mouseY, sliderX, sliderY) < d / 2) {
            isOnDrag = true;
        }
    }

    public void onMoueseReleased() {
        isPressed = false;
        isOnDrag = false;
    }

    @Override
    public void getParentPos() {
        Method m;
        try {
            m = parent.getClass().getMethod("getX");
            x = (int) m.invoke(parent) + xShift;

            m = parent.getClass().getMethod("getY");
            y = (int) m.invoke(parent) + yShift;

            if (doOnce == 0) {

                if (isHorizontalSlider) {
                    sliderShiftX += startShift;
                    startX = (int) (x + startShift);
                    startY = y;
                } else {
                    sliderShiftY += startShift;
                    startY = (int) (y + startShift);
                    startX = x;
                }

                doOnce++;
            }

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

    public int getD() {
        return d;
    }

    public int getSliderShift() {
        if (isHorizontalSlider) {
            return sliderShiftX;
        } else {
            return sliderShiftY;
        }
    }

    @Override
    public Boolean mouseIsInArea() {
        if (p.mouseX > x - w / 2 && p.mouseX < x + w / 2 && p.mouseY > y - h / 2 && p.mouseY < y + h / 2) {
            return true;
        } else {
            return false;
        }
    }

    public void setIsPressed(Boolean state) {
        isPressed = state;
        isOnDrag = state;
    }

    public Boolean getIsOnDrag() {
        return isOnDrag;
    }
    public Boolean getNewValueSet() {
    	return newValueSet;
    }
    public float getVal() {
        float val, convertVal;
        if (isHorizontalSlider) {
            convertVal = sliderShiftX;
        } else {
            convertVal = sliderShiftY;
        }
        val = p.map(convertVal, -w / 2 + d / 2, w / 2 - d / 2, returnValBorderLow, returnValBorderHigh);
        return val;
    }

    public void setSliderShift(int val,Boolean valIsInPercentage) {
    	int calculatedShift=val;
    	if(valIsInPercentage) {
    	    calculatedShift = (int) p.map(val, returnValBorderLow, returnValBorderHigh, -w / 2 + d / 2, w / 2 - d / 2);
            //p.println(val,calculatedShift);
    	}else {
    		calculatedShift=p.constrain(val, -w / 2 + d / 2, w / 2 - d / 2);
    	}
        if (isHorizontalSlider) {
            sliderShiftX = calculatedShift;
        } else {
            sliderShiftY = calculatedShift;
        }
        newValueSet=true;
    }
    public void setNewValueSet(Boolean state) {
    	newValueSet=state;
    }
}
