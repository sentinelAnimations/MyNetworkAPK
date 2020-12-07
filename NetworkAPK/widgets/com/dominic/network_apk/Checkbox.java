package com.dominic.network_apk;

import java.lang.reflect.Method;

import processing.core.PApplet;
import processing.core.PFont;

public class Checkbox<T> implements Widgets {
	private int x, y, xShift, yShift, boxX, boxY, w, h, boxDim, edgeRad, margin, stdTs, bgCol, boxCol, tickCol, textCol, checkMode = 0; // checkmode 0=checked (tick), 1=unchecked (empty), 2 = cross (cross)
	private float textYShift;
	private Boolean isParented, renderBg, isChecked = false, hasThreeOptions = false;
	private String t;
	private PFont stdFont;
	private PApplet p;
	private T parent;
	private PictogramImage picto;
	private HoverText hoverText;

	public Checkbox(PApplet p, int x, int y, int w, int h, int boxDim, int edgeRad, int margin, int stdTs, int bgCol, int boxCol, int tickCol, int textCol, float textYShift, Boolean isParented, Boolean renderBg, Boolean hasThreeOptions, String t, String infoText, PFont stdFont, T parent) {
		this.p = p;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.boxDim = boxDim;
		this.edgeRad = edgeRad;
		this.margin = margin;
		this.stdTs = stdTs;
		this.bgCol = bgCol;
		this.boxCol = boxCol;
		this.tickCol = tickCol;
		this.textCol = textCol;
		this.isParented = isParented;
		this.renderBg = renderBg;
		this.hasThreeOptions = hasThreeOptions;
		this.t = t;
		this.stdFont = stdFont;
		this.parent = parent;
		boxX = x - w / 2 + margin + boxDim / 2;
		boxY = y;
		xShift = x;
		yShift = y;

		hoverText = new HoverText(p, stdTs, margin, edgeRad, 150, textCol, textYShift, infoText, "getBoxX", "getBoxY", "getBoxDim", "getBoxDim", stdFont, this);

	}

	public void render() {
		if (isParented) {
			getParentPos();
		}
		if (renderBg) {
			p.stroke(bgCol);
			p.fill(bgCol);
			p.rect(x, y, w, h, edgeRad);
			// p.fill(255,0,0);
			// p.rect(x,y,10,10);
		}
		p.stroke(boxCol);
		p.fill(boxCol);
		p.rect(boxX, boxY, boxDim, boxDim, edgeRad);

		p.textAlign(p.LEFT, p.CENTER);
		p.textFont(stdFont);
		p.textSize(stdTs);
		p.fill(textCol);
		p.text(t, boxX + boxDim / 2 + margin, boxY - stdTs * textYShift);

		if (isChecked) {
			if (hasThreeOptions) {
				if (checkMode == 2) {
					renderCross();
				}
				if (checkMode == 0) {
					renderTick();
				}
			} else {
				renderTick();
			}
			// picto.render();
		}

		hoverText.render();
	}

	private void renderCross() {
		p.strokeWeight(2);
		for (int i = 0; i <= 90; i += 90) {
			p.stroke(tickCol);
			p.line(boxX + p.cos(p.radians(225 + i)) * boxDim / 4, boxY + p.sin(p.radians(225 + i)) * boxDim / 4, boxX + p.cos(p.radians(45 + i)) * boxDim / 4, boxY + p.sin(p.radians(45 + i)) * boxDim / 4);
		}
		p.strokeWeight(1);
	}

	private void renderTick() {
		p.strokeWeight(2);
		p.stroke(tickCol);
		float centerY = boxY + boxDim / 6;
		float centerX = boxX - boxDim / 10;
		p.line(centerX + p.sin(p.radians(225)) * boxDim / 4, centerY + p.cos(p.radians(225)) * boxDim / 4, centerX, centerY);
		p.line(centerX + p.sin(p.radians(135)) * boxDim / 2, centerY + p.cos(p.radians(135)) * boxDim / 2, centerX, centerY);

		p.strokeWeight(1);
	}

	public void onMouseReleased() {
		if (mouseIsInArea()) {
			if (hasThreeOptions) {
				checkMode = (checkMode + 1) % 3;
				isChecked = true;
				if (checkMode == 1) {
					isChecked = false;
				}
			} else {
				isChecked = !isChecked;
			}
			p.println(checkMode, isChecked);
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
			boxX = x - w / 2 + margin + boxDim / 2;
			boxY = y;

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
		if (p.mouseX > boxX - boxDim / 2 && p.mouseX < boxX + boxDim / 2 && p.mouseY > boxY - boxDim / 2 && p.mouseY < boxY + boxDim / 2) {
			return true;
		} else {
			return false;
		}
	}

	public int getCheckMode() {
		return checkMode;
	}

	public Boolean getIsChecked() {
		return isChecked;
	}

	public int getBoxX() {
		return boxX;
	}

	public int getBoxY() {
		return boxY;
	}

	public int getBoxDim() {
		return boxDim;
	}

	public int getW() {
		return w;
	}

	public int getH() {
		return h;
	}

	public String getText() {
		return t;
	}

	public HoverText getHoverText() {
		return hoverText;
	}

	public void setIsChecked(Boolean state) {
		isChecked = state;
	}

	public void setCheckMode(int setMode) {
		checkMode = setMode;
	}

}
