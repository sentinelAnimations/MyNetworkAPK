package com.dominic.network_apk;

import java.lang.reflect.Method;

import processing.core.PApplet;
import processing.core.PFont;

public class SearchBar<T> implements Widgets {
    private int x, y, w, h, xShift, yShift, edgeRad, margin, stdTs, textCol, textDark, bgCol, bs;
    private float textYShift;
    private String hint, t;
    private Boolean isParented;
    private PFont stdFont;
    private PApplet p;
    private T parent;
    private EditText searchBar_et;
    private ImageButton search_btn;

    public SearchBar(PApplet p, int x, int y, int w, int h, int edgeRad, int margin, int stdTs, int textCol, int textDark, int bgCol, float textYShift, Boolean isParented, String hint, String pictoPath, PFont stdFont, T parent) {
        this.p = p;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.edgeRad = edgeRad;
        this.margin = margin;
        this.stdTs = stdTs;
        this.textCol = textCol;
        this.textDark = textDark;
        this.bgCol = bgCol;
        this.textYShift = textYShift;
        this.isParented = isParented;
        this.hint = hint;
        this.stdFont = stdFont;
        this.parent = parent;
        xShift = x;
        yShift = y;
        search_btn = new ImageButton(p, x + w / 2 - h / 2, y, h, h, stdTs, margin, edgeRad, 10, textYShift, true, false, textCol, bgCol, pictoPath, "Search | Shortcut: ctrl+Enter", null);
        char[] fChars = { '>', '<', ':', '"', '/', '\\', '|', '?', '*' };
        searchBar_et = new EditText(p, x - h / 2 - margin, y, w - margin * 6, h, stdTs, bgCol, textCol, edgeRad, margin, textYShift, true, false, hint, fChars, stdFont, null);

    }

    public void render() {
        if (isParented) {
            getParentPos();
        } 
        p.fill(bgCol);
        p.stroke(bgCol);
        searchBar_et.render();
        search_btn.render();
    }

    public void onMousePressed() {
    	search_btn.onMousePressed();
    }

    public void onMouseReleased() {
        search_btn.onMouseReleased();
        searchBar_et.onMouseReleased();

    }
    
    public void onKeyPressed(char key){
		searchBar_et.onKeyPressed(key);
	}

    public void onKeyReleased(char k) {
        search_btn.onKeyReleased(k);
        searchBar_et.onKeyReleased(k);

    }

    public String getText() {
        return t;
    }

    public int getW() {
        return w;
    }
    public int getH() {
        return h;
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
    
    public ImageButton getButton() {
        return search_btn;
    }
    
    public EditText getEditText() {
        return searchBar_et;
    }
}
