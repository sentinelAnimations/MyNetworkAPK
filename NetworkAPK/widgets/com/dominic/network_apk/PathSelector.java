package com.dominic.network_apk;

import java.lang.reflect.Method;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;

public class PathSelector<T> implements Widgets {

	private int x, y, xShift, yShift, w, h, stdTs, edgeRad, margin, bgCol, textCol, textDark, btnSize;
	private Boolean isParented;
	private String t = "", hint, imgPath;
	private PFont stdFont;
	private PApplet p;
	private T parent;
	private ArrayList<MakeToast> makeToasts = new ArrayList<MakeToast>();
	public ImageButton openFileExplorer_btn;

	public PathSelector(PApplet p, int x, int y, int w, int h, int edgeRad, int margin, int stdTs, int bgCol, int textCol, int textDark, Boolean isParented, String hint, String imgPath, PFont stdFont, T parent) {
		this.p = p;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.edgeRad = edgeRad;
		this.margin = margin;
		this.stdTs = stdTs;
		this.bgCol = bgCol;
		this.textCol = textCol;
		this.textDark = textDark;
		this.isParented = isParented;
		this.hint = hint;
		this.imgPath = imgPath;
		this.stdFont = stdFont;
		this.parent = parent;
		xShift = x;
		yShift = y;
		btnSize = h - margin;
		openFileExplorer_btn = new ImageButton(p, x - w / 2 + margin + btnSize / 2, yShift, btnSize, btnSize, stdTs, margin, edgeRad, -1, false, isParented, textCol, textCol, imgPath, "open file explorer", parent);

	}

	public void render() {
		if (isParented) {
			getParentPos();
		}

		p.fill(bgCol);
		p.rect(x, y, w, h, edgeRad);
		openFileExplorer_btn.render();
		p.textAlign(PConstants.LEFT, PConstants.CENTER);
		p.textFont(stdFont);
		p.textSize(stdTs);
		if (t.length() < 1) {
			p.fill(textDark);
			p.text(hint, x - w / 2 + margin + btnSize, y - stdTs / 5);
		} else {
			p.fill(textCol);
			p.text(t, x - w / 2 + margin * 3 + btnSize, y - stdTs / 5);
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

	public void setText(String text) {
		t = text;
	}
}
