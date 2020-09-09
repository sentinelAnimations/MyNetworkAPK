package com.dominic.network_apk;

import java.lang.reflect.Method;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

public class ImageButton<T> implements Widgets {
	private int x, y, xShift, yShift, w, h, stdTs, edgeRad, shortcut, col, bgCol, margin, onceOnClick = 0, hoverTime, clickCount = 0, waitAfterTransform = 0;
	private float textYShift;
	private String imgPath, infoText;
	private Boolean isClicked = false, isPressed = false, isParented, useBg, isHovering = false;
	private PApplet p;
	private PImage picto;
	private HoverText hoverText;
	private MainActivity mainActivity;
	private T parent;

	public ImageButton(PApplet p, int x, int y, int w, int h, int stdTs, int margin, int edgeRad, int shortcut, float textYShift, Boolean useBg, Boolean isParented, int col, int bgCol, String imgPath, String infoText, T parent) {
		this.p = p;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.stdTs = stdTs;
		this.margin = margin;
		this.useBg = useBg;
		this.isParented = isParented;
		this.col = col;
		this.bgCol = bgCol;
		this.edgeRad = edgeRad;
		this.shortcut = shortcut;
		this.textYShift = textYShift;
		this.imgPath = imgPath;
		this.infoText = infoText;
		this.parent = parent;
		xShift = x;
		yShift = y;
		mainActivity=(MainActivity)p;
		loadPicto();
        hoverText = new HoverText(p, stdTs, margin, edgeRad, 150, col, textYShift, infoText,"getX","getY","getW","getH", mainActivity.getStdFont(), this);
	}

	public void render() {

		if (isParented) {
			getParentPos();
		}

		if (waitAfterTransform < 0) {
			waitAfterTransform++;
		}

		if (useBg) {
			p.fill(bgCol);
			p.stroke(bgCol);
			p.rect(x, y, w, h, edgeRad);
		}
		p.tint(col);
		p.image(picto, x, y);
		hoverText.render();
	}

	public void onMousePressed() {
		isPressed = true;
		if (mouseIsInArea()) {
			if (onceOnClick == 0) {
				picto.resize(picto.width - margin, picto.height - margin);
				w -= margin;
				h -= margin;
				onceOnClick = 1;
			}
		}
	}

	public void onMouseReleased() {
		if (waitAfterTransform >= 0) {
			if (onceOnClick != 0) {
				w += margin;
				h += margin;
				onceOnClick = 0;
				loadPicto();
			}
			if (mouseIsInArea()) {
				clickCount++;
				isClicked = true;
			}
		}
		isPressed = false;
	}

	public void onKeyReleased(char k) {
		if (shortcut >= 0) {
			if (k == shortcut) {
				isClicked = true;
			}
		}
	}
	
	private void loadPicto() {
		picto = p.loadImage(imgPath);
		int dim = 0;
		if (w > h) {
			dim = h;
		} else {
			dim = w;
		}
		if (useBg) {
			picto.resize(dim - margin * 2, dim - margin * 2);
		} else {
			picto.resize(dim, dim);
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

	public int getClickCount() {
		return clickCount;
	}

	public Boolean getIsClicked() {
		return isClicked;
	}
	
	public int getCol() {
	    return col;
	}
	
	public int getBgCol() {
	    return bgCol;
	}

	public Boolean mouseIsInArea() {
		if (p.mouseX > x - w / 2 && p.mouseX < x + w / 2 && p.mouseY > y - h / 2 && p.mouseY < y + h / 2) {
			return true;
		} else {
			return false;
		}
	}

	public void setIsClicked(Boolean state) {
		isClicked = state;
	}

	public void setPos(int xp, int yp) {
		if (isParented) {
			xShift = xp;
			yShift = yp;
		} else {
			x = xp;
			y = yp;
		}
		waitAfterTransform = -1;
	}

	public void setPicto(String path) {
		imgPath = path;
		loadPicto();
	}
	
	
	
}
