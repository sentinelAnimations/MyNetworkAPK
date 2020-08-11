package com.dominic.network_apk;

import processing.core.PApplet;
import processing.core.PFont;

public class Node {

	private int x, y,headX,headY,bodyX,bodyY, w, h,bodyH,headH, type, edgeRad, margin, stdTs,btnSizeSmall, bgCol, textCol, lighter,doOnce=0;
	private float textYShift;
	private Boolean isOnDrag=true;
	private String[] pictoPaths;
	private PFont stdFont;
	private PApplet p;
	private PictogramImage type_picto,cpu_picto,gpu_picto;

	public Node(PApplet p, int x, int y, int w, int h, int type, int edgeRad, int margin, int stdTs,int btnSizeSmall, int bgCol, int textCol, int lighter, float textYShift, String[] pictoPaths, PFont stdFont) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.edgeRad = edgeRad;
		this.margin = margin;
		this.stdTs = stdTs;
		this.btnSizeSmall=btnSizeSmall;
		this.bgCol = bgCol;
		this.textCol = textCol;
		this.lighter = lighter;
		this.textYShift = textYShift;
		this.pictoPaths = pictoPaths;
		this.stdFont = stdFont;
		this.p = p;
		this.type = type;
		type_picto=new PictogramImage(p, 0, 0, btnSizeSmall, margin, stdTs, edgeRad, textCol, textYShift, true, pictoPaths[type], "", this);
	}

	public void render() {
		switch (type) {
		case 0:
			renderTypePC(); //Master pc
			break;
			
		case 1:
			renderTypePC(); //Pc
			break;
			
		case 2:
			renderTypePC(); // laptop
			break;
			
		case 3:
			renderTypeSwitch(); // switch
			break;
			
		case 4:
			renderTypeOutput(); //engine output
			break;
		}
	}

	private void renderTypePC() {
		if(doOnce==0) {
			doOnce++;
		}
		if(isOnDrag) {
			x=p.mouseX;
			y=p.mouseY;
		}
		
		p.fill(bgCol);
		p.rect(x,y,w,h);
		type_picto.render();
	}
	
	private void renderTypeSwitch() {

	}
	
	private void renderTypeOutput() {

	}
	
	private void isDragablePcNode() {
		
	}
	
	public void onMousePressed() {
		
	}
	
	public void onMouseReleased() {
		if(isOnDrag) {
			isOnDrag=false;
			
		}
	}
	
	public void onKeyReleased(char k) {
		
	}
	
	public void onScroll(float e) {
		
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}

}
