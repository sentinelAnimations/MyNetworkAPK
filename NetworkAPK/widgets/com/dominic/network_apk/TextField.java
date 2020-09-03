package com.dominic.network_apk;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.data.StringList;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import net.davidashen.text.Hyphenator;

public class TextField<T> implements Widgets {
    private int textCol, w, h, x, y, xShift, yShift, stdTs, margin, btnSizeSmall, edgeRad, dark, light, lighter, doOnce = 0, textScrollShift, textH;
    private float textYShift;
    private Boolean stretch, isParented, isScrollable;
    private String t = "";
    private PApplet p;
    private PFont stdFont;
    private Hyphenator hy;
    private T parent;
    private Slider textScroll_slider;

    public TextField(PApplet p, int x, int y, int w, int h, int stdTs, int margin, int btnSizeSmall, int edgeRad, int dark, int light, int lighter, int textCol, float textYShift, Boolean stretch, Boolean isParented, Boolean isScrollable, String t, PFont stdFont, T parent) {
        this.p = p;
        this.textCol = textCol;
        this.w = w;
        this.h = h;
        this.x = x;
        this.y = y;
        this.stdTs = stdTs;
        this.margin = margin;
        this.btnSizeSmall = btnSizeSmall;
        this.edgeRad = edgeRad;
        this.dark = dark;
        this.light = light;
        this.lighter = lighter;
        this.stretch = stretch;
        this.textYShift = textYShift;
        this.isParented = isParented;
        this.isScrollable = isScrollable;
        this.t = t;
        this.stdFont = stdFont;
        this.parent = parent;
        xShift = x;
        yShift = y;

        setupHyphenator();
        processToDisplay();

    }

    public void render() {

        if (isParented) {
            getParentPos();
        }
        if (doOnce == 0) {
            if (isScrollable) {
                textScroll_slider = new Slider(p, x + w / 2, y, h, 2, btnSizeSmall / 2, stdTs, edgeRad, margin, 0, 100, 0, dark, light, lighter, textYShift, isParented, false, false, true, stdFont, parent);
            }
            doOnce++;
        }

        // p.noFill(); p.stroke(light); p.rect(x, y, w, h,edgeRad);

        if (isScrollable) {
            textH = p.split(t, "\n").length * stdTs - h;
            textScrollShift = (int) (textH / 100.0f * textScroll_slider.getVal()) * -1;
        }

        if (isScrollable) {
            textScroll_slider.render();
        }
        if (stretch == true) {
            displayTextStretched();
        } else {
            displayTextUnStretched();
        }

    }

    private void displayTextStretched() {
        String[] splitStr = p.split(t, "\n");
        p.textAlign(PConstants.LEFT, PConstants.CENTER);
        p.fill(textCol);
        p.textFont(stdFont);
        p.textSize(stdTs);
        for (int i = 0; i < splitStr.length; i++) {
            int spaceAmounth = Math.round((w - p.textWidth(splitStr[i])) / p.textWidth(" "));
            String[] splitStr2 = PApplet.split(splitStr[i], " ");
            String newStr = splitStr2[0];
            for (int i2 = 1; i2 < splitStr2.length; i2++) {
                if (i2 < spaceAmounth) {
                    newStr += "  " + splitStr2[i2];
                } else {
                    newStr += " " + splitStr2[i2];
                }
            }
            String dT = splitStr[i];
            if (spaceAmounth < splitStr[i].length() / 2) {
                dT = newStr;
            }
            int yText = getYPosByLineIndex(i);
            if (yText >= y - h / 2 + margin + stdTs / 2 && yText < y + h / 2 - margin - stdTs / 2) {
                p.text(dT, x - w / 2, yText);
            }
        }
    }

    private void displayTextUnStretched() {
        String[] splitStr = p.split(t, "\n");

        p.textAlign(PConstants.LEFT, PConstants.CENTER);
        p.fill(textCol);
        p.textFont(stdFont);
        p.textSize(stdTs);
        for (int i = 0; i < splitStr.length; i++) {
            int yText = getYPosByLineIndex(i);
            if (yText >= y - h / 2 + margin + stdTs / 2 && yText < y + h / 2 - margin - stdTs / 2) {
                p.text(splitStr[i], x - w / 2 + margin, yText);
            }
        }
    }

    private void processToDisplay() {
        p.textSize(stdTs);
        String[] splitStr = PApplet.split(t, " ");
        String newT = splitStr[0];
        String newT2 = newT;
        for (int i = 1; i < splitStr.length; i++) {
            if (p.match(splitStr[i], "\n") != null) {
                newT += splitStr[i];
                newT2 = splitStr[i];
            } else {

                if (p.textWidth(newT2) + p.textWidth(splitStr[i]) < w) {
                    newT += " " + splitStr[i];
                    newT2 += " " + splitStr[i];
                } else {
                    StringList l = hyphenateWords(splitStr[i]);
                    newT += " ";
                    newT2 += " ";

                    for (int i2 = 0; i2 < l.size(); i2++) {
                        if (p.textWidth(newT2) + p.textWidth(l.get(i2) + "-") < w) {
                            newT += l.get(i2);
                            newT2 += l.get(i2);
                        } else {
                            if (l.size() > 1 && i2 > 0) {
                                newT += "-";
                            }
                            newT += "\n" + l.get(i2);
                            newT2 = l.get(i2);
                        }
                    }

                }

            }
        }
        t = newT;
    }

    private StringList hyphenateWords(String s) {
        int ind = 0, singleFirst = 0, singleLast = 0;
        StringList hyphenatedSegments = new StringList();
        String hyphenated_word = hy.hyphenate(s);
        String[] splitStr = PApplet.split(hyphenated_word, "­");
        // p.println("--", splitStr, splitStr.length);

        if (splitStr.length > 1) {
            Boolean match = false;
            if (((splitStr[0].charAt(0) == '.' || splitStr[0].charAt(0) == ',') && splitStr[0].length() < 3)) {
                match = true;
                // p.println("match first-----------------------------");
            }
            if (splitStr[0].length() < 2 || match == true) {
                splitStr[1] = splitStr[0] + splitStr[1];
                singleFirst = 1;
            }

            match = false;
            if (((splitStr[splitStr.length - 1].charAt(splitStr[splitStr.length - 1].length() - 1) == '.' || splitStr[splitStr.length - 1].charAt(splitStr[splitStr.length - 1].length() - 1) == ',') && splitStr[splitStr.length - 1].length() < 3)) {
                match = true;
                // p.println("match last------------------------------");

            }

            if (splitStr[splitStr.length - 1].length() < 2 || match == true) {
                splitStr[splitStr.length - 2] = splitStr[splitStr.length - 2] + splitStr[splitStr.length - 1];
                singleLast = 1;
            }
        }

        for (int i = singleFirst; i < splitStr.length - singleLast; i++) {
            String[] searchForHyph = PApplet.split(splitStr[i], "\u200b");
            for (int i2 = 0; i2 < searchForHyph.length; i2++) {
                hyphenatedSegments.append(searchForHyph[i2]);
            }
        }

        // p.println(hyphenatedSegments);
        return hyphenatedSegments;
    }

    private void setupHyphenator() {
        hy = new Hyphenator();
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream("texSources/hyphen.tex");
            hy.loadTable(new java.io.BufferedInputStream(is));
            // "C:\\Users\\Dominic\\git\\MyNetworkAPK\\NetworkAPK\\data\\tex\\hyphen.tex"
            // hy.loadTable(new java.io.BufferedInputStream(new
            // java.io.FileInputStream("data/texSources/hyphen.tex")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onMousePressed() {
        textScroll_slider.onMousePressed();
    }

    public void onMouseReleased() {
        textScroll_slider.onMoueseReleased();
    }

    public void onScroll(float e) {
        if (mouseIsInArea()) {
            int step = stdTs;
            if (e > 0) {
                textScroll_slider.setSliderVal((int) textScroll_slider.getSliderShift() + step);
            } else {
                textScroll_slider.setSliderVal((int) textScroll_slider.getSliderShift() - step);
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

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public int getTs() {
        return stdTs;
    }

    public int getYPosByLineIndex(int lineInd) {
        return y - h / 2 + margin + stdTs / 2 + stdTs * lineInd + textScrollShift;
    }
    public int getYPosByLineIndexUnshifted(int lineInd) {
        return stdTs * lineInd;
    }
    
    public int getTextScrollShift() {
        return textScrollShift;
    }

    public int getTextH() {
        return p.split(t, "\n").length * stdTs;
    }

    public String getText() {
        return t;
    }

    public Slider getSlider() {
        return textScroll_slider;
    }
}
