package com.dominic.network_apk;

import java.lang.reflect.Method;

import processing.core.PApplet;
import processing.core.PFont;

public class Loadingbar<T> implements Widgets {
	private int x, y, w, h, xShift, yShift, x1, x2, w1, w2, stdTs, edgeRad, margin, colOk, colNok, textCol, min, max, percentage, value;
	private float textYshift;
	private Boolean isParented;
	private PFont stdFont;
	private PApplet p;
	private T parent;

	public Loadingbar(PApplet p, int x, int y, int w, int h, int stdTs, int edgeRad, int margin, int colOk, int colNok, int textCol, int min, int max, float textYShift, Boolean isParented, PFont stdFont, T parent) {
		this.p = p;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.stdTs = stdTs;
		this.edgeRad = edgeRad;
		this.margin = margin;
		this.colNok = colNok;
		this.colOk = colOk;
		this.textCol = textCol;
		this.min = min;
		this.max = max;
		this.textYshift = textYShift;
		this.isParented = isParented;
		this.stdFont = stdFont;
		this.parent = parent;

		xShift = x;
		yShift = y;
	}

	public void render() {

		if (isParented) {
			getParentPos();
		}

		p.fill(colOk);
		p.stroke(colOk);
		if (percentage < 100) {
			if(percentage>0) {
			p.rect(x1, y, w1, h, edgeRad, 0, 0, edgeRad);
			}
			p.fill(colNok);
			p.stroke(colNok);
			
			if (percentage > 0) {
				p.rect(x2, y, w2, h, 0, edgeRad, edgeRad, 0);
			} else {
				p.rect(x2, y, w, h, edgeRad);
			}
		} else {
			p.fill(colOk);
			p.stroke(colOk);
			p.rect(x1, y, w, h, edgeRad);

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
	public int getMax() {
		return max;
	}
	public int getMin() {
		return min;
	}
	public Boolean mouseIsInArea() {
		if (p.mouseX > x - w / 2 && p.mouseX < x + w / 2 && p.mouseY > y - h / 2 && p.mouseY < y + h / 2) {
			return true;
		} else {
			return false;
		}
	}
	
	public void setValue(int setVal) {
		value = setVal;
		percentage = (int) p.map(value, min, max, 0, 100);
		calcBars();
	}

	public void calcBars() {
		w1 = (int) (w / 100.0f * percentage);
		x1 = (x - w / 2 + w1 / 2);
		w2 = w - w1;
		x2 = x1 + w1 / 2 + w2 / 2;
	}

	public void setMin(int setMin) {
		min = setMin;
		setValue(value);
	}

	public void setMax(int setMax) {
		max = setMax;
		setValue(value);
	}

	public void setPos(int xp, int yp) {
		x = xp;
		y = yp;
		xShift = xp;
		yShift = yp;
		calcBars();
	}

}
