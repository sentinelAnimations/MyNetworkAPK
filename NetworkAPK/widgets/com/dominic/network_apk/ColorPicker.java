package com.dominic.network_apk;

import java.lang.reflect.Method;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PVector;

public class ColorPicker<T> implements Widgets {
    private int x, y, xShift, yShift, markerX, markerY, w, h, r, dark, stdTs, edgeRad, margin, btnSize, btnSizeSmall, bgCol, lighter, lightest, textCol, pickedCol, doOnce = 0, borderW;
    private float textYShift, brightness;
    private Boolean isParented, isPressed = false, isUnfolded = false, renderBg, stayOpen;
    private String pictoPath;
    private PFont stdFont;
    private PImage rgbCircle;
    private PApplet p;
    private T parent;
    private Slider brightness_slider;
    private PictogramImage picto;

    public ColorPicker(PApplet p, int x, int y, int w, int h, int r, int dark, int stdTs, int edgeRad, int margin, int btnSize, int btnSizeSmall, int bgCol, int lighter, int lightest, int textCol, float textYShift, Boolean isParented, Boolean renderBg, Boolean stayOpen, String pictoPath, PFont stdFont, T parent) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.r = r;
        this.dark = dark;
        this.stdTs = stdTs;
        this.edgeRad = edgeRad;
        this.margin = margin;
        this.btnSize = btnSize;
        this.btnSizeSmall = btnSizeSmall;
        this.bgCol = bgCol;
        this.lighter = lighter;
        this.lightest = lightest;
        this.textCol = textCol;
        this.isParented = isParented;
        this.renderBg = renderBg;
        this.stayOpen = stayOpen;
        this.textYShift = textYShift;
        this.pictoPath = pictoPath;
        this.stdFont = stdFont;
        this.p = p;
        this.parent = parent;

        pickedCol = bgCol;
        xShift = x;
        yShift = y;
        markerX = x + 1;
        markerY = y;
        int bgC;
        if (renderBg) {
            bgC = dark;
        } else {
            bgC = bgCol;
        }

        picto = new PictogramImage(p, xShift, yShift, btnSizeSmall - margin, margin, stdTs, edgeRad, textCol, textYShift, false, pictoPath, "", parent);

        brightness_slider = new Slider(p, 0, margin + r + btnSizeSmall / 2, r * 2, btnSizeSmall / 4, btnSizeSmall - margin, stdTs, edgeRad, margin, 0, 100, 0, dark, bgC, lightest, textYShift, true, true, false, true, stdFont, this);
        brightness_slider.render();

    }

    public void render() {
        if (isParented) {
            getParentPos();
            if (doOnce == 0) {
                brightness = brightness_slider.getVal();
                rgbCircle = calcRGBCircle();
            }
        } else {
            if (doOnce == 0) {
                brightness = brightness_slider.getVal();
                rgbCircle = calcRGBCircle();
                doOnce++;
            }
        }
        if (stayOpen && !isUnfolded) {
            isUnfolded = true;
        }

        if (!isUnfolded) {
            p.fill(pickedCol);
            p.stroke(lighter);
            p.rect(x, y, w, h, edgeRad);
            picto.render();
        } else {

            if (!mouseIsInColorPickArea() && !isPressed) {
                isUnfolded = false;
                brightness_slider.setIsPressed(false);
            }

            if (isPressed) {
                if (p.dist(p.mouseX, p.mouseY, x, y) < r) {
                    markerX = p.mouseX;
                    markerY = p.mouseY;
                }
            }

            if (renderBg) {
                p.fill(bgCol);
                p.rect(x, y + btnSizeSmall, r * 2 + margin * 2, r * 2 + margin * 3 + btnSizeSmall * 1.5f, edgeRad);
            }
            p.noTint();
            p.image(rgbCircle, x, y);
            p.strokeWeight(3);
            p.stroke(dark);
            if (renderBg) {
                p.stroke(bgCol);
            }
            p.noFill();
            p.ellipse(x, y, r * 2, r * 2);
            p.strokeWeight(1);

            int col = getColOfPos(x, y, markerX, markerY, r);
            pickedCol = p.color(p.red(col) - (p.red(col) / 100.0f * brightness), p.green(col) - (p.green(col) / 100.0f * brightness), p.blue(col) - (p.blue(col) / 100.0f * brightness));

            p.stroke(dark);
            p.fill(col);
            p.ellipse(markerX, markerY, btnSizeSmall / 2, btnSizeSmall / 2);

            brightness_slider.render();

            p.stroke(pickedCol);
            p.fill(pickedCol);
            p.rect(x, brightness_slider.getY() + brightness_slider.getD(), brightness_slider.getW(), brightness_slider.getH(), edgeRad);

            if (brightness_slider.getIsOnDrag() || brightness_slider.getNewValueSet()){
                brightness = brightness_slider.getVal();
                int d = btnSizeSmall - margin;
                // brightness_slider.setSliderVal((int)brightness);
                if (brightness >= 100) {
                    markerX = x;
                    markerY = y;
                }
                brightness_slider.setNewValueSet(false);
            }
        }

    }

    private PImage calcRGBCircle() {
        int xp = 0, yp = 0, scale = 3;
        PImage img = p.createImage(r * 2 * scale, r * 2 * scale, p.ARGB);
        img.loadPixels();
        for (int i = 0; i < img.pixels.length; i++) {
            xp++;
            if (xp >= img.width) {
                xp = 0;
                yp++;
            }
            img.pixels[i] = p.color(255, 0, 0);

            if (p.dist(xp, yp, img.width / 2, img.height / 2) <= r * scale) {
                if (xp == img.width && yp == img.height) {
                    img.pixels[i] = p.color(0, 0);
                } else {
                    int col = getColOfPos(img.width / 2, img.height / 2, xp, yp, r * 3);
                    // int convertetCol = p.color(p.red(col) * 2 + 255 * brightness, p.green(col) *
                    // 2 + 255 * brightness, p.blue(col) * 2 + 255 * brightness);
                    int convertetCol = p.color(p.red(col) - (p.red(col) / 100.0f * brightness), p.green(col) - (p.green(col) / 100.0f * brightness), p.blue(col) - (p.blue(col) / 100.0f * brightness));

                    // img.pixels[i] = convertetCol;
                    img.pixels[i] = col;

                }
            } else {
                img.pixels[i] = p.color(0, 0);
            }
        }
        img.updatePixels();
        img.resize(r * 2 - borderW, r * 2 - borderW);
        // p.image(img, 17, 17);
        return img;
    }

    private int getColOfPos(float cx, float cy, float xp, float yp, int rad) {
        int c;
        float ar = 0, ag = 0, ab = 0;
        float red = 0, green = 0, blue = 0;
        float d = 0;

        ar = getAngle(cx, cy, xp, yp);
        ab = ar - 120;

        d = 255 - p.map(p.dist(cx, cy, xp, yp), 0, rad, 0, 255);
        // p.println(d,p.dist(cx, cy, xp, yp));
        if (ab < 0) {
            ab = (360 - 120) + (120 - p.abs(ab));
        }

        ag = ar - 240;

        if (ar != ar || ag != ag || ab != ab) { // if one angle = NaN
            c = p.color(255);
        } else {
            if (ag < 0) {
                ag = (360 - 240) + (240 - p.abs(ag));
            }
            if (ar < 240) {
                red = p.map(p.abs(120 - ar), 0, 120, 0, 1) * 255;
                if (ar >= 120) {
                    red = 0;
                }
            } else {
                red = p.map(p.abs(240 - ar), 0, 120, 0, 1) * 255;
            }

            if (ag < 240) {
                green = p.map(p.abs(120 - ag), 0, 120, 0, 1) * 255;
                if (ag >= 120) {
                    green = 0;
                }
            } else {
                green = p.map(p.abs(240 - ag), 0, 120, 0, 1) * 255;
            }

            if (ab < 240) {
                blue = p.map(p.abs(120 - ab), 0, 120, 0, 1) * 255;
                if (ab >= 120) {
                    blue = 0;
                }
            } else {
                blue = p.map(p.abs(240 - ab), 0, 120, 0, 1) * 255;
            }
            // r=map(abs(180-ar), 0, 180, 0, 1)*dist(cx, cy, xp, yp);
            // g=map(abs(180-ag), 0, 180, 0, 1)*dist(cx, cy, xp, yp);
            // b=map(abs(180-ab), 0, 180, 0, 1)*dist(cx, cy, xp, yp);
            red *= 2;
            green *= 2;
            blue *= 2;

            red += d;
            green += d;
            blue += d;
            c = p.color(red, green, blue);
        }

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
        isPressed = true;
        brightness_slider.onMousePressed();
    }

    public void onMoueseReleased() {
        isPressed = false;
        if (isUnfolded) {
            brightness_slider.onMoueseReleased();
            if (p.dist(p.mouseX, p.mouseY, x, y) < r) {
                markerX = p.mouseX;
                markerY = p.mouseY;
            }
        } else {
            if (mouseIsInArea()) {
                isUnfolded = true;
            }
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
            if (doOnce == 0) {
                markerX = x;
                markerY = y;
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

    @Override
    public Boolean mouseIsInArea() {
        if (p.mouseX > x - w / 2 && p.mouseX < x + w / 2 && p.mouseY > y - h / 2 && p.mouseY < y + h / 2) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean mouseIsInColorPickArea() {
        // x,y+btnSizeSmall/2,r*2+margin*2,r*2+margin*3+btnSizeSmall, r * 2 + margin * 3
        // + btnSizeSmall * 1.5f

        // x, y + btnSizeSmall, r * 2 + margin * 2, r * 2 + margin * 3 + btnSizeSmall *
        // 1.5f,
        if (p.mouseX > x - (r + margin) && p.mouseX < x + (r + margin) && p.mouseY > (y + btnSizeSmall) - (r * 2 + margin * 3 + btnSizeSmall * 1.5f) / 2 && p.mouseY < (y + btnSizeSmall) + (r * 2 + margin * 3 + btnSizeSmall * 1.5f) / 2) {
            return true;
        } else {
            return false;
        }
    }

    public int getPickedCol() {
        return pickedCol;
    }

    public Slider getSlider() {
        return brightness_slider;
    }

    public int getColorBarY() {
        return brightness_slider.getY() + brightness_slider.getD();
    }

    public PVector getMarkerPos() {
        return new PVector(markerX, markerY);
    }

    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(int val) {
        brightness_slider.setSliderShift(val,true);
    }

    public void setMarkerPos(int mx, int my) {
        markerX = mx;
        markerY = my;
    }

}
