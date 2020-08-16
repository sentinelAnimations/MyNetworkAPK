package com.dominic.network_apk;

import java.lang.reflect.Method;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;

public class CounterArea<T> implements Widgets {
	private int x, y, xShift, yShift, w, h, edgeRad, margin, stdTs, bgCol, textCol, lighter, calcOnceOnStartup = 0, count, counterBorderLow, counterBorderHigh, hoverTime;
	private float textYShift;
	private Boolean isParented, isHovering = false, valueHasChanged = true;
	private String infoText;
	private String[] pictoPaths;
	private PFont stdFont;
	private PApplet p;
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
	}

	public void render() {
		if (isParented) {
			getParentPos();
		}

		p.fill(bgCol);
		p.stroke(bgCol);
		p.rect(x, y, w, h, edgeRad);
		p.textAlign(p.CENTER, p.CENTER);
		p.textFont(stdFont);
		p.textSize(stdTs);
		p.fill(textCol);
		p.text(count, x, y - stdTs * textYShift);
		add_btn.render();
		subtract_btn.render();
		onHover();

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

	private void onHover() {
		Boolean show = false;
		if (infoText.length() > 0) {
			if (mouseIsInArea()) {
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

				if (hoverTime > 120) {
					show = false;
				} else {
					show = true;
				}
				if (show) {
					p.fill(0, 200);
					p.noStroke();
					p.rect(mx + tw / 2, my + stdTs, PApplet.abs(tw) + margin * 2, stdTs * 2, edgeRad);
					p.fill(textCol);
					p.text(infoText, mx + tw, my + stdTs - stdTs * textYShift);
				}
			}
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

	public Boolean mouseIsInArea() {
		if (p.mouseX > x - w / 2 && p.mouseX < x + w / 2 && p.mouseY > y - h / 2 && p.mouseY < y + h / 2) {
			return true;
		} else {
			return false;
		}
	}

	public int getW() {
		return w;
	}
	public int  getH() {
		return h;
	}

	public int getCount() {
		return count;
	}

	public Boolean getValueHasChanged() {
		return valueHasChanged;
	}

	public void setValueHasChanged(Boolean state) {
		valueHasChanged = state;
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
