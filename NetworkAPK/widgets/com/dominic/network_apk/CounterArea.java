package com.dominic.network_apk;

import java.lang.reflect.Method;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;

public class CounterArea<T> implements Widgets {
	private int x, y, xShift, yShift, w, h, edgeRad, margin, stdTs, bgCol, textCol, lighter, calcOnceOnStartup = 0, count, counterBorderLow, counterBorderHigh, hoverTime;
	private float textYShift;
	private Boolean isParented, isHovering = false, valueHasChanged = true, renderHoverText = true;
	private String infoText;
	private String[] pictoPaths;
	private PFont stdFont;
	private PApplet p;
	private HoverText hoverText;
	private T parent;
	private ImageButton add_btn, subtract_btn;

	public CounterArea(PApplet p, int x, int y, int w, int h, int edgeRad, int margin, int stdTs, int counterBorderLow, int counterBorderHigh, int startVal, int bgCol, int lighter, int textCol, float textYShift, Boolean isParented, String infoText, String[] pictoPaths, PFont stdFont, T parent) {
		this.p = p;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.edgeRad = edgeRad;
		this.margin = margin;
		this.stdTs = stdTs;
		this.counterBorderLow = counterBorderLow;
		this.counterBorderHigh = counterBorderHigh;
		this.bgCol = bgCol;
		this.textCol = textCol;
		this.lighter = lighter;
		this.isParented = isParented;
		this.infoText = infoText;
		this.pictoPaths = pictoPaths;
		this.stdFont = stdFont;
		this.parent = parent;
		xShift = x;
		yShift = y;
		count = startVal;
		initializePictoImage();
		hoverText = new HoverText(p, stdTs, margin, edgeRad, 150, textCol, textYShift, infoText, "getX", "getY", "getW", "getH", stdFont, this);
		if (isParented) {
			getParentPos();
		}
	}

	public void render() {
		if (isParented) {
			getParentPos();
		}
		checkForBorder();

		p.fill(bgCol);
		p.stroke(bgCol);
		p.rect(x, y, w, h, edgeRad);
		p.textAlign(p.CENTER, p.CENTER);
		p.textFont(stdFont);
		p.textSize(stdTs);
		p.fill(textCol);
		String displText = p.str(count);
		renderHoverText = true;
		if (p.textWidth(infoText + ": " + p.str(count)) < (add_btn.getX() - add_btn.getW() / 2 - margin) - (subtract_btn.getX() + subtract_btn.getW() / 2 + margin)) {
			displText = infoText + ": " + p.str(count);
			renderHoverText = false;
		}
		p.text(displText, x, y - stdTs * textYShift);
		add_btn.render();
		subtract_btn.render();
		// onHover();
		if (renderHoverText) {
			hoverText.render();
		}
		if (add_btn.getIsClicked() == true) {
			count++;
			checkForBorder();
			valueHasChanged = true;
			add_btn.setIsClicked(false);
		}

		if (subtract_btn.getIsClicked() == true) {
			count--;
			checkForBorder();
			valueHasChanged = true;
			subtract_btn.setIsClicked(false);
		}

	}

	public void onMouseReleased() {
		add_btn.onMouseReleased();
		subtract_btn.onMouseReleased();
		if (mouseIsInArea()) {
			if (p.mouseX < x) {
				subtract_btn.setIsClicked(true);
			} else {
				add_btn.setIsClicked(true);
			}
		}
	}

	public void onMousePressed() {
		add_btn.onMousePressed();
		subtract_btn.onMousePressed();
	}

	public void onScroll(float e) {
		if (mouseIsInArea()) {
			if (e > 0) {
				count += 10;
			} else {
				count -= 10;
			}
			checkForBorder();
			valueHasChanged = true;
		}
	}

	private void initializePictoImage() {
		add_btn = new ImageButton(p, xShift + w / 2 - w / 8, yShift, h / 2, h / 2, stdTs, margin, edgeRad, -1, textYShift, false, isParented, lighter, textCol, pictoPaths[1], "", parent);
		subtract_btn = new ImageButton(p, xShift - w / 2 + w / 8, yShift, h / 2, h / 2, stdTs, margin, edgeRad, -1, textYShift, false, isParented, lighter, textCol, pictoPaths[0], "", parent);

	}

	private void checkForBorder() {
		if (count < counterBorderLow) {
			count = counterBorderLow;
		}
		if (count > counterBorderHigh) {
			count = counterBorderHigh;
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
			if (calcOnceOnStartup == 0) {
				initializePictoImage();
				calcOnceOnStartup++;
			}

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

	public int getCount() {
		return count;
	}

	public Boolean mouseIsInArea() {
		if (p.mouseX > x - w / 2 && p.mouseX < x + w / 2 && p.mouseY > y - h / 2 && p.mouseY < y + h / 2) {
			return true;
		} else {
			return false;
		}
	}

	public Boolean getValueHasChanged() {
		return valueHasChanged;
	}

	public void setValueHasChanged(Boolean state) {
		valueHasChanged = state;
	}

	public void setCount(int cou) {
		count = cou;
		valueHasChanged = true;
	}

	public void setPos(int xp, int yp) {
		x = xp;
		xShift = x;
		y = yp;
		yShift = y;
		add_btn.setPos(xShift + w / 2 - w / 8, yShift);
		subtract_btn.setPos(xShift - w / 2 + w / 8, yShift);
	}
}
