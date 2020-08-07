package com.dominic.network_apk;

import java.lang.reflect.Method;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

public class ImageButton<T> implements Widgets {
	private int x, y, xShift, yShift, w, h, stdTs, edgeRad, shortcut, col, bgCol, margin, onceOnClick = 0, hoverTime;
	private float textYShift;
	private String imgPath, infoText;
	public Boolean isClicked = false,isPressed=false, isParented;
	private Boolean useBg, isHovering;
	private PApplet p;
	private PImage picto;
	private T parent;

	public ImageButton(PApplet p, int x, int y, int w, int h, int stdTs, int margin, int edgeRad, int shortcut,float textYShift, Boolean useBg, Boolean isParented, int col, int bgCol, String imgPath, String infoText, T parent) {
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
		this.textYShift=textYShift;
		this.imgPath = imgPath;
		this.infoText = infoText;
		this.parent = parent;
		xShift = x;
		yShift = y;
		loadPicto();
	}

	public void render() {

		if (isParented) {
			getParentPos();
		}

		if (useBg) {
			p.fill(bgCol);
			p.stroke(bgCol);
			p.rect(x, y, w, h, edgeRad);
		}
		p.tint(col);
		p.image(picto, x, y);

		onHover();
	}

	public void onMousePressed() {
		isPressed=true;
		if (p.mouseX > x - w / 2 && p.mouseX < x + w / 2 && p.mouseY > y - h / 2 && p.mouseY < y + h / 2) {
			if (onceOnClick == 0) {
				picto.resize(picto.width - margin, picto.height - margin);
				w -= margin;
				h -= margin;
				onceOnClick = 1;
			}
		}
	}

	public void onMouseReleased() {
		if (onceOnClick != 0) {
			w += margin;
			h += margin;
			onceOnClick = 0;
			loadPicto();
		}
		if (p.mouseX > x - w / 2 && p.mouseX < x + w / 2 && p.mouseY > y - h / 2 && p.mouseY < y + h / 2) {
			isClicked = true;
		}
		isPressed=false;
	}

	public void onKeyReleased(char k) {
		if (shortcut >= 0) {
			if (k == shortcut) {
				isClicked = true;
			}
		}
	}

	private void onHover() {
		if(infoText.length()>0) {
		if (p.mouseX > x - w / 2 && p.mouseX < x + w / 2 && p.mouseY > y - h / 2 && p.mouseY < y + h / 2) {
			if (isHovering) {
				hoverTime++;
			}
			isHovering = true;
		} else {
			hoverTime = 0;
			isHovering = false;
		}
		if (hoverTime > 72) {
			int tw = (int) p.textWidth(infoText) + margin * 2;
			int mx, my;
			if (p.mouseX + tw < p.width) {
				p.textAlign(PConstants.RIGHT, PConstants.CENTER);
			} else {
				tw *= -1;
				p.textAlign(PConstants.LEFT, PConstants.CENTER);
			}
			mx = p.mouseX;
			my = p.mouseY;
			if (p.mouseY < stdTs) {
				my = stdTs;
			}
			if (p.mouseY > p.height - stdTs * 2) {
				my = p.height - stdTs * 2;
			}

			p.fill(0, 200);
			p.noStroke();
			p.rect(mx + tw / 2, my + stdTs, PApplet.abs(tw) + margin * 2, stdTs * 2, edgeRad);
			p.fill(col);
			p.text(infoText, mx + tw, my + stdTs -stdTs*textYShift );
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
	
	public void setPicto(String path) {
		imgPath=path;
		loadPicto();
	}
}
