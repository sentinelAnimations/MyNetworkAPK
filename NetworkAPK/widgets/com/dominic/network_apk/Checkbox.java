package com.dominic.network_apk;

import java.lang.reflect.Method;

import processing.core.PApplet;
import processing.core.PFont;

public class Checkbox<T> implements Widgets {
	private int x, y, xShift, yShift, boxX, boxY, w, h, boxDim, edgeRad, margin, stdTs, bgCol, boxCol, tickCol, textCol, calcOnceOnStartup = 0;
	private float textYShift;
	private Boolean isParented, renderBg, isChecked = false;
	private String t,pictoPath;
	private PFont stdFont;
	private PApplet p;
	private T parent;
	private PictogramImage picto;
	private HoverText hoverText;


	public Checkbox(PApplet p, int x, int y, int w, int h, int boxDim, int edgeRad, int margin, int stdTs, int bgCol, int boxCol, int tickCol, int textCol, float textYShift, Boolean isParented, Boolean renderBg, String t,String infoText,String pictoPath, PFont stdFont, T parent) {
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
		this.t = t;
		this.pictoPath=pictoPath;
		this.stdFont = stdFont;
		this.parent = parent;
		boxX = x - w / 2 + margin + boxDim / 2;
		boxY = y;
		xShift = x;
		yShift = y;
		
        hoverText = new HoverText(p, stdTs, margin, edgeRad, 150, textCol, textYShift, infoText,"getBoxX","getBoxY","getBoxDim","getBoxDim", stdFont, this);

		initializePictoImage();

	}

	public void render() {
		if (isParented) {
			getParentPos();
		}
		if (renderBg) {
			p.stroke(bgCol);
			p.fill(bgCol);
			p.rect(x, y, w, h, edgeRad);
			//p.fill(255,0,0);
			//p.rect(x,y,10,10);
		}
		p.stroke(boxCol);
		p.fill(boxCol);
		p.rect(boxX, boxY, boxDim, boxDim, edgeRad);

		p.textAlign(p.LEFT, p.CENTER);
		p.textFont(stdFont);
		p.textSize(stdTs);
		p.fill(textCol);
		p.text(t, boxX + boxDim / 2 + margin, boxY-stdTs*textYShift);

		if (isChecked) {
			//renderTick();
			picto.render();
		}
		hoverText.render();
	}
	
	private void renderTick() {
		p.strokeWeight(1);
		for (int i = 0; i <= 90; i += 90) {
			p.stroke(tickCol);
			p.line(boxX+p.cos(p.radians(225+i))*boxDim/4,boxY+p.sin(p.radians(225+i))*boxDim/4,boxX+p.cos(p.radians(45+i))*boxDim/4,boxY+p.sin(p.radians(45+i))*boxDim/4);
		}
		p.strokeWeight(1);
	}

	public void onMouseReleased() {
		if (mouseIsInArea()) {
			isChecked = !isChecked;
		}
	}
	
	private void initializePictoImage(){
		picto = new PictogramImage(p, xShift-(x-boxX),yShift-(y-boxY), boxDim - margin,boxDim-margin, margin, stdTs, edgeRad, tickCol, textYShift,isParented,false, pictoPath, "", parent);
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
	@Override
	public Boolean mouseIsInArea() {
		if(p.mouseX > boxX - boxDim / 2 && p.mouseX < boxX + boxDim / 2 && p.mouseY > boxY - boxDim / 2 && p.mouseY < boxY + boxDim / 2) {
			return true;
		}else {
			return false;
		}
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
	    isChecked=state;
	}
	 
}
