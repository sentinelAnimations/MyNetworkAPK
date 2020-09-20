package com.dominic.network_apk;

import java.awt.Image;
import java.io.File;
import java.lang.reflect.Method;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class PictogramImage<T> implements Widgets {
	private int x, y, xShift, yShift, w, h, col, lightCol, hoverTime = 0, btnSize, margin, stdTs, edgeRad;
	private float textYShift;
	private Boolean isHovering = false, isParented, loadedImage = false, useThumbnail;
	private String imgPath, infoText;
	private PApplet p;
	private PImage img;
	private PFont stdFont;
	private HoverText hoverText;
	private MainActivity mainActivity;
	private T parent;
	private Thread loadDataThread;

	public PictogramImage(PApplet p, int x, int y, int w, int h, int margin, int stdTs, int edgeRad, int col, float textYShift, Boolean isParented, Boolean useThumbnail, String imgPath, String infoText, T parent) {
		this.p = p;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.margin = margin;
		this.stdTs = stdTs;
		this.edgeRad = edgeRad;
		this.col = col;
		this.isParented = isParented;
		this.useThumbnail = useThumbnail;
		this.textYShift = textYShift;
		this.imgPath = imgPath;
		this.infoText = infoText;
		this.parent = parent;
		mainActivity = (MainActivity) p;
		xShift = x;
		yShift = y;
		stdFont = mainActivity.getStdFont();
		lightCol = col;
		initialize();

		hoverText = new HoverText(p, stdTs, margin, edgeRad, 150, col, textYShift, infoText, "getX", "getY", "getW", "getH", stdFont, this);

	}

	public void render() {
		if (isParented) {
			getParentPos();
		}
		if (!loadDataThread.isAlive()) {
			p.tint(col);
			p.image(img, x, y);
			hoverText.render();
		} else {
			p.fill(lightCol);
			p.textAlign(p.CENTER, p.CENTER);
			p.textFont(stdFont);
			p.textSize(stdTs);
			p.text("Loading...", x, y);
		}
	}

	private void initialize() {
		loadDataThread = new Thread(new Runnable() {
			@Override
			public void run() {
				if (useThumbnail) {
					File file= new File(imgPath);
					Icon ico = FileSystemView.getFileSystemView().getSystemIcon(file);
					 Image image = ((ImageIcon) ico).getImage();
					 img =new PImage(image);
				} else {
					img = p.loadImage(imgPath);
				}
					float xDim = w, yDim = h;
					// if (img.width > w || img.height > h) {
					if (img.width > img.height) {
						float scale = (float) img.width / (float) w;
						xDim = img.width / scale;
						yDim = img.height / scale;
					} else {
						float scale = (float) img.height / (float) h;
						yDim = img.height / scale;
						xDim = img.width / scale;
					}
					while (xDim > w || yDim > h) {
						if (xDim > w) {
							// xDim = w;
							float scale = xDim / w;
							xDim = xDim / scale;
							yDim = yDim / scale;
						}
						if (yDim > h) {
							// yDim = h;

							float scale = yDim / h;
							xDim = xDim / scale;
							yDim = yDim / scale;
						}
					}
					img.resize((int) xDim, (int) yDim);
					// }
					loadedImage = true;
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

	public Boolean getIsLoaded() {
		return !loadDataThread.isAlive();
	}

	public String getPictoPath() {
		return imgPath;
	}

	public void setPos(int xp, int yp) {
		x = xp;
		xShift = x;
		y = yp;
		yShift = y;
	}

	public void setCol(int c) {
		col = c;
	}

	public void setLightCol(int setC) {
		lightCol = setC;
	}

	public void setSize(int setW, int setH) {
		w = setW;
		h = setH;
		initialize();
	}
}
