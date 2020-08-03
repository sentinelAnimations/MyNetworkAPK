package com.dominic.network_apk;

import java.lang.reflect.Method;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;

public class MakeToast<T> implements Widgets {
	private int x, y, xShift, yShift, w, h, stdTs, margin, edgeRad, bgCol, textCol, alpha = 0, alifeTime = 100,
			fadeOutTimer;
	public Boolean remove = false;
	private Boolean fadeOut = false,isParented;
	private String t;
	private PFont stdFont;
	private PApplet p;
	private T parent;

	public MakeToast(PApplet p, int x, int y, int stdTs, int margin, int edgeRad, int bgCol, int textCol, Boolean isParented,String t,
			PFont stdFont, T parent) {
		this.p = p;
		this.x = x;
		this.y = y;
		this.stdTs = stdTs;
		this.margin = margin;
		this.edgeRad = edgeRad;
		this.bgCol = bgCol;
		this.textCol = textCol;
		this.isParented=isParented;
		this.t = t;
		this.stdFont = stdFont;
		this.parent = parent;
		xShift=this.x;
		yShift=this.y;
		w = (int) (p.textWidth(t) + margin * 2);
		h = stdTs + margin * 2;
	}

	public void render() {
		if (isParented) {
			getParentPos();
		}
		if (alpha < 255 && fadeOut == false) {
			alpha += 10;
		} else {
			fadeOut = true;
		}
		if (fadeOut == true) {
			if (fadeOutTimer < alifeTime) {
				fadeOutTimer++;
			} else {
				alpha -= 10;
				if (alpha < 0) {
					remove = true;
				}
			}
		}

		p.textAlign(PConstants.LEFT, PConstants.CENTER);
		p.noStroke();
		p.fill(bgCol, alpha);
		p.rect(x, y, w, h, edgeRad);
		p.fill(textCol, alpha);
		p.textFont(stdFont);
		p.textSize(stdTs);
		p.text(t, x - w / 2 + margin, y - stdTs / 5);
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

}
