package com.dominic.network_apk;

import java.lang.reflect.Method;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;

public class Switch<T> implements Widgets {
	private int x, y, xShift, yShift, sliderRad, w, h, edgeRad, margin, stdTs, bgCol, handleColChecked,handleColUnchecked, textCol;
	private float textYShift, transitionSpeed = 5,sliderX,textX;
	private Boolean isParented, showText, isChecked, isInTransition = false;
	private String infoText;
	private PFont stdFont;
	private PApplet p;
	private T parent;
	private HoverText hoverText;

	public Switch(PApplet p, int x, int y, int w, int h, int edgeRad, int margin, int stdTs, int bgcol, int handleColChecked,int handleColUnchecked, int textCol, float textYShift, Boolean isParented, Boolean showText, Boolean isChecked, String infoText, PFont stdFont, T parent) {
		this.p = p;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.edgeRad = edgeRad;
		this.margin = margin;
		this.stdTs = stdTs;
		this.bgCol = bgcol;
		this.handleColChecked = handleColChecked;
		this.handleColUnchecked=handleColUnchecked;
		this.textCol = textCol;
		this.textYShift = textYShift;
		this.isParented = isParented;
		this.showText = showText;
		this.isChecked = isChecked;
		this.infoText = infoText;
		this.stdFont = stdFont;
		this.parent = parent;

		xShift = x;
		yShift = y;
		sliderRad = h / 2;
		if (!showText) {
			hoverText = new HoverText(p, stdTs, margin, edgeRad, 150, textCol, textYShift, infoText, "getX", "getY", "getW", "getH", stdFont, this);
		}
	}

	public void render() {
		if (isParented) {
			getParentPos();
		}
		if (isInTransition) {
			if (isChecked) {
				if (sliderX > x - w / 2 + sliderRad) {
					sliderX -= (w - sliderRad * 2) / transitionSpeed;
				} else {
					sliderX=x-w/2+sliderRad;
					isInTransition = false;
					isChecked = !isChecked;
				}
			} else {
				if (sliderX < x + w / 2 - sliderRad) {
					sliderX += (w - sliderRad * 2) / transitionSpeed;
				} else {
					sliderX=x+w/2-sliderRad;
					isInTransition = false;
					isChecked = !isChecked;
				}
			}
		} else {
			if (isChecked) {
				sliderX = x + w / 2 - sliderRad;
			} else {
				sliderX = x - w / 2 + sliderRad;
			}
		}
		sliderX=p.constrain(sliderX, x-w/2+edgeRad, x+w/2-edgeRad);
		textX =  p.map(sliderX-((x - w / 2) + sliderRad), 0, (w / 2 - sliderRad) - (-w / 2 + sliderRad), sliderRad*2+margin,margin);
		textX=p.constrain(textX, margin, sliderRad*2+margin)+x-w/2-margin;
	
		textX+=margin;

		p.stroke(bgCol);
		p.fill(bgCol);
		p.rect(x, y, w, h, h / 2);

		if (showText) {
			p.textAlign(p.LEFT, p.CENTER);
			p.textFont(stdFont);
			p.textSize(stdTs);
			p.fill(textCol);
			p.text(infoText, textX, y - stdTs * textYShift);
		} else {
			hoverText.render();
		}
		if(isChecked) {
		p.stroke(handleColChecked);
		p.fill(handleColChecked);
		}else {
			p.stroke(handleColUnchecked);
			p.fill(handleColUnchecked);
		}
		p.ellipse(sliderX, y, sliderRad * 2, sliderRad * 2);
		p.fill(bgCol);
		p.ellipse(sliderX, y, sliderRad * 2 - margin, sliderRad * 2 - margin);

	}

	public void onMouseReleased() {
		if (p.dist(p.mouseX, p.mouseY, sliderX, y) < sliderRad) {
			isInTransition = true;

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
		if (p.mouseX > x - w / 2 && p.mouseX < x + w / 2 && p.mouseY > y - h / 2 && p.mouseY < y + h / 2) {
			return true;
		} else {
			return false;
		}
	}

	public int getW() {
		return w;
	}

	public int getH() {
		return h;
	}

	public Boolean getIsChecked() {
		return isChecked;
	}
}
