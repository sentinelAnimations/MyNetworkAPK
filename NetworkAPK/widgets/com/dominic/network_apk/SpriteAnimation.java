package com.dominic.network_apk;

import java.lang.reflect.Method;

import processing.core.PApplet;
import processing.core.PImage;

public class SpriteAnimation<T> implements Widgets {
	private int ind = 0, x, y,xShift,yShift, w, h, endInd, startInd, col;
	private Boolean isLoaded = false,isParented;
	private String path;
	private PImage startImg;
	private PImage[] imgs;
	private PApplet p;
	private T parent;

	public SpriteAnimation(PApplet p, int x, int y, int w, int h, int startInd, int endInd, int col,Boolean isParented, String path,T parent) {
		this.p = p;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.endInd = endInd;
		this.startInd = startInd;
		this.col = col;
		this.isParented=isParented;
		this.path = path;
		this.parent=parent;
		xShift=x;
		yShift=y;
		startImg = p.loadImage(path + PApplet.nf(startInd, 4) + ".png");
		startImg.resize(w, h);
		Thread loadingThread = new Thread(new Runnable() {
			@Override
			public void run() {
				loadImgs();
				isLoaded = true;
			}
		});
		loadingThread.start();
	}

	void render() {
		
		if (isParented) {
			getParentPos();
		}
		
		if (isLoaded) {
			ind++;
			if (ind > imgs.length - 1) {
				ind = 0;
			}
			p.tint(col);
			p.image(imgs[ind], x, y);
		}else {
			p.tint(col);
			p.image(startImg, x, y);
		}
	}

	public void loadImgs() {
		imgs = new PImage[endInd - startInd];
		for (int i = startInd; i < endInd; i++) {
			imgs[ind] = p.loadImage(path + PApplet.nf(i, 4) + ".png");
			imgs[ind].resize(w, h);
			ind++;
		}
		ind = 0;
	}
	@Override
	public void getParentPos() {
		 Method m;
		try {
			m = parent.getClass().getMethod("getX");
	       x=(int) m.invoke(parent)+xShift;
	       
	       m = parent.getClass().getMethod("getY");
	       y=(int) m.invoke(parent)+yShift;
	        
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
}
