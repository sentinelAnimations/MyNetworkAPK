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
	private HoverText hoverText;
	private MainActivity mainActivity;
	private T parent;
	private Thread loadDataThread;

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
		mainActivity=(MainActivity)p;
		xShift = x;
		yShift = y;
		
		initialize();
		
        hoverText = new HoverText(p, stdTs, margin, edgeRad, 150, col, textYShift, infoText,"getX","getY","getW","getH", mainActivity.getStdFont(), this);

	}

	public void render() {
		if (isParented) {
			getParentPos();
		}
		if(!loadDataThread.isAlive()) {
		p.tint(col);
		p.image(img, x, y);
		hoverText.render();
		}
	}
	
	private void initialize() {
		  loadDataThread = new Thread(new Runnable() {
             @Override
             public void run() {
            		img = p.loadImage(imgPath);
            		float xDim=dim,yDim=dim;
            		if(img.width>img.height) {
            			float scale=img.width/dim;
            			xDim=dim;
            			yDim=img.height/scale;
            		}else {
            			float scale=img.height/dim;
            			yDim=dim;
            			xDim=img.width/scale;
            		}
            		if(xDim>dim) {
            			xDim=dim;
            		}
            		if(yDim>dim) {
            			yDim=dim;
            		}
            		img.resize((int)xDim, (int)yDim);
             }
         });
         loadDataThread.start();
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
	
	public int getW() {
	    return dim;
	}
	public int getH() {
	    return dim;
	}

	public void setPos(int xp, int yp) {
		x = xp;
		xShift = x;
		y = yp;
		yShift = y;
	}
	
	public void setCol(int c) {
		col=c;
	}
}
