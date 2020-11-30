package com.dominic.network_apk;

import java.lang.reflect.Method;

import processing.core.PApplet;
import processing.core.PFont;

public class TextPopup<T> implements Widgets {
	private int x, y, xShift, yShift, w, h, btnSizeSmall, edgeRad, stdTs, margin, bgCol, lighter, textCol;
	private float textYShift;
	private Boolean isParented, showPopup = false;
	private String textSource;
	private String[] pictoPaths;
	private PFont stdFont;
	private PApplet p;
	private TextField textField;
	private ImageButton close_ImageButton;
	private MainActivity mainActivity;
	private T parent;

	public TextPopup(PApplet p, int x, int y, int w, int h, int btnSizeSmall, int edgeRad, int stdTs, int margin, int bgCol, int lighter, int textCol, float textYShift, Boolean isParented, String textSource, String[] pictoPaths, PFont stdFont, T parent) {
		this.p = p;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.btnSizeSmall = btnSizeSmall;
		this.edgeRad = edgeRad;
		this.stdTs = stdTs;
		this.margin = margin;
		this.bgCol = bgCol;
		this.lighter = lighter;
		this.textCol = textCol;
		this.textYShift = textYShift;
		this.isParented = isParented;
		this.textSource = textSource;
		this.pictoPaths = pictoPaths;
		this.stdFont = stdFont;
		this.parent = parent;
		mainActivity = (MainActivity) p;

		xShift = x;
		yShift = y;

		String s = new TxtStringHelper(p).getStringFromFile(textSource);
		textField = new TextField(p, 0, 0, w - btnSizeSmall * 2, h - margin * 2, stdTs, margin, btnSizeSmall, edgeRad, bgCol, lighter, lighter, textCol, textYShift, true, true, true, s, stdFont, this);
		int closeBtnSize=(int)(btnSizeSmall/2);
		close_ImageButton = new ImageButton(p, w / 2-closeBtnSize/2-margin, -h / 2+closeBtnSize/2+margin, closeBtnSize, closeBtnSize, stdTs, margin, edgeRad, -1, textYShift, true, true, textCol, lighter, pictoPaths[0], "Close", this);
	}

	public void render() {
		if (showPopup) {
			if (isParented) {
				getParentPos();
			}
			
			p.fill(bgCol);
			p.stroke(bgCol);
			p.rect(x,y,w,h,edgeRad);
			textField.render();
			close_ImageButton.render();
			
			p.stroke(255,0,0);
			int r=close_ImageButton.getW()/4;
			p.line(close_ImageButton.getX()+p.cos(p.radians(-45))*r,close_ImageButton.getY()+p.sin(p.radians(-45))*r,close_ImageButton.getX()+p.cos(p.radians(135))*r,close_ImageButton.getY()+p.sin(p.radians(135))*r);
			p.line(close_ImageButton.getX()+p.cos(p.radians(45))*r,close_ImageButton.getY()+p.sin(p.radians(45))*r,close_ImageButton.getX()+p.cos(p.radians(225))*r,close_ImageButton.getY()+p.sin(p.radians(225))*r);

			if (close_ImageButton.getIsClicked()) {
				showPopup = false;
				close_ImageButton.setIsClicked(false);
			}
		}
	}

	public void onMousePressed() {
		if (showPopup) {
			textField.onMousePressed();
			close_ImageButton.onMousePressed();
		}
	}

	public void onMouseReleased() {
		if (showPopup) {
			textField.onMouseReleased();
			close_ImageButton.onMouseReleased();
		}
	}

	public void onScroll(float e) {
		if (showPopup) {
			textField.onScroll(e);
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

	@Override
	public Boolean mouseIsInArea() {
		if (p.mouseX > x - w / 2 && p.mouseX < x + w / 2 && p.mouseY > y - h / 2 && p.mouseY < y + h / 2) {
			return true;
		} else {
			return false;
		}
	}

	public Boolean getShowPopup() {
		return showPopup;
	}

	public void setShowPopup(Boolean state) {
		p.println("now",state);
		showPopup = state;
	}

}
