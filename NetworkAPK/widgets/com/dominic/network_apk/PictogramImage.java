package com.dominic.network_apk;

import java.lang.reflect.Method;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

public class PictogramImage<T> implements Widgets {
	private int x, y, xShift, yShift, dim, col, hoverTime = 0, btnSize, margin, stdTs, edgeRad;
	private float textYShift;
	private Boolean isHovering = false, isParented;
	private String imgPath, infoText;
	private PApplet p;
	private PImage img;
	private T parent;

	public PictogramImage(PApplet p, int x, int y, int dim, int margin, int stdTs, int edgeRad, int col, float textYShift, Boolean isParented, String imgPath, String infoText, T parent) {
		this.p = p;
		this.x = x;
		this.y = y;
		this.dim = dim;
		this.margin = margin;
		this.stdTs = stdTs;
		this.edgeRad = edgeRad;
		this.col = col;
		this.isParented = isParented;
		this.textYShift = textYShift;
		this.imgPath = imgPath;
		this.infoText = infoText;
		this.parent = parent;
		xShift = x;
		yShift = y;
		img = p.loadImage(imgPath);
		img.resize(dim, dim);
	}

	public void render() {
		if (isParented) {
			getParentPos();
		}
		p.tint(col);
		p.image(img, x, y);
		onHover();
	}

	private void onHover() {
		if (infoText.length() > 0) {
			if (p.mouseX > x - dim / 2 && p.mouseX < x + dim / 2 && p.mouseY > y - dim / 2 && p.mouseY < y + dim / 2) {
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
				p.textSize(stdTs);
				p.text(infoText, mx + tw, my + stdTs - stdTs * textYShift);
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
		if (p.mouseX > x - dim / 2 && p.mouseX < x + dim / 2 && p.mouseY > y - dim / 2 && p.mouseY < y + dim / 2) {
			return true;
		} else {
			return false;
		}
	}

	public void setPos(int xp, int yp) {
		x = xp;
		xShift = x;
		y = yp;
		yShift = y;
	}
}
