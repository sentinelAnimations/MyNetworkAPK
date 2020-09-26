package com.dominic.network_apk;

import java.lang.reflect.Method;

import processing.core.PApplet;
import processing.core.PFont;

public class TimeField<T> implements Widgets {
	private int x, y, w, h, textW, xShift, yShift, stdTs, margin, edgeRad, textCol, bgCol;
	private String time = "", prefix, postfix, displT;
	private Boolean isParented, renderBg, isDynamical;
	private PFont stdFont;
	private T parent;
	private PApplet p;
	private MainActivity mainActivity;

	public TimeField(PApplet p, int x, int y, int w, int h, int stdTs, int margin, int edgeRad, int textCol, int bgCol, Boolean isParented, Boolean renderBg, Boolean isDynamical, String prefix, String postfix, PFont stdFont, T parent) {
		this.p = p;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.stdTs = stdTs;
		this.margin = margin;
		this.edgeRad = edgeRad;
		this.textCol = textCol;
		this.bgCol = bgCol;
		this.isParented = isParented;
		this.renderBg = renderBg;
		this.isDynamical = isDynamical;
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
		renderBg = true;
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
		p.text(displT, x, y);
	}

	private void calcTime() {
		int prevLeftBorder = x - w / 2;
		time = p.str(p.hour()) + " : " + p.str(p.minute()) + " : " + p.str(p.second());
		displT = prefix + time + postfix;

		if (!isDynamical) {
			String displT2 = "";
			if(p.textWidth(displT)>w-margin*2) {
			displT=time;
			}
			for (int i = 0; i < displT.length(); i++) {
				if (p.textWidth(displT2 + displT.charAt(i)) < w - margin * 2) {
					displT2 += displT.charAt(i);
				} else {
					break;
				}
			}
			displT = displT2;
		}
		textW = (int) p.textWidth(displT) + margin * 2;
		if (isDynamical) {
			x = prevLeftBorder + textW / 2;
			w=textW;
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

	public void setPrefix(String setPrefix) {
		prefix = setPrefix;
	}

	public void setPostfix(String setPostfix) {
		postfix = setPostfix;
	}

	public void setPos(int xp, int yp) {
		x = xp;
		y = yp;
		xShift = x;
		yShift = y;
	}

	public void setCol(int setCol) {
		textCol = setCol;
	}
}
