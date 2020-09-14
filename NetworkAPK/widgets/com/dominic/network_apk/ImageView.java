package com.dominic.network_apk;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;

public class ImageView<T> implements Widgets {

	private int x, y, w, h, xShift, yShift, scrollShift = 0, stdTs, edgeRad, margin, btnSize, btnSizeSmall, light, lighter, textCol, textDark, border, pictoDimens, selectedInd = 0;
	private float textYShift;
	private Boolean isParented;
	private ArrayList<PictogramImage> pictos = new ArrayList();
	private ArrayList<PVector> pictoPositions = new ArrayList();
	private PFont stdFont;
	private PApplet p;
	private T parent;
	private MainActivity mainActivity;
	private FileInteractionHelper fileInteractionHelper;

	public ImageView(PApplet p, int x, int y, int w, int h, int stdTs, int edgeRad, int margin, int btnSizeLarge, int btnSize, int btnSizeSmall, int light, int lighter, int textCol, int textDark, int border, float textYShift, Boolean isParented, PFont stdFont, T parent) {
		this.p = p;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.stdTs = stdTs;
		this.edgeRad = edgeRad;
		this.margin = margin;
		this.btnSize = btnSize;
		this.btnSizeSmall = btnSizeSmall;
		this.light = light;
		this.lighter = lighter;
		this.textCol = textCol;
		this.textDark = textDark;
		this.border = border;
		this.textYShift = textYShift;
		this.isParented = isParented;
		this.stdFont = stdFont;
		this.parent = parent;
		mainActivity = (MainActivity) p;

		fileInteractionHelper = new FileInteractionHelper(p);
		xShift = x;
		yShift = y;
		int subivider = 7;
		pictoDimens = (w - margin * 2 - margin * (subivider - 1)) / (subivider);
		p.println(pictoDimens);
		if (isParented) {
			getParentPos();
		}

		setFolder("D:\\algemeine Bilder");
	}

	public void render() {
		if (isParented) {
			getParentPos();
		}
		p.fill(light);
		p.stroke(light);
		p.rect(x, y, w, h, edgeRad);

		if (pictos.size() > 0) {
			for (int i = pictos.size() - 1; i >= 0; i--) {
				PictogramImage pic = pictos.get(i);
				if (pic.getY() > y - h / 2 + pictoDimens / 2 && pic.getY() < y + h / 2 - pictoDimens / 2 - margin) {
					if (i == selectedInd) {
						p.stroke(border);
					} else {
						p.stroke(lighter);
					}
					p.noFill();
					p.rect(pic.getX(), pic.getY(), pictoDimens, pictoDimens, edgeRad);
					pic.render();
				}
			}
		}

	}

	public void onMousePressed(int mouseButton) {
	}

	public void onMouseReleased(int mouseButton) {
		if (pictos.size() > 0) {
			for (int i = pictos.size() - 1; i >= 0; i--) {
				PictogramImage pic = pictos.get(i);
				if (pic.mouseIsInArea()) {
					selectedInd=i;
				}
			}
		}
	}

	public void onScroll(float e) {
		int scrollSpeed = pictoDimens + margin;
		if (e < 0) {
			if (pictos.get(0).getY() < y - h / 2 + pictoDimens / 2 + margin) {
				for (int i = 0; i < pictos.size(); i++) {
					PictogramImage p = pictos.get(i);
					p.setPos(p.getX(), p.getY() + scrollSpeed);
				}
			}
		} else {
			if (pictos.get(pictos.size() - 1).getY() > y - h / 2 + pictoDimens / 2 + margin) {
				for (int i = 0; i < pictos.size(); i++) {
					PictogramImage p = pictos.get(i);
					p.setPos(p.getX(), p.getY() - scrollSpeed);
				}
			}
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

	public Boolean mouseIsInArea() {
		if (p.mouseX > x - w / 2 && p.mouseX < x + w / 2 && p.mouseY > y - h / 2 && p.mouseY < y + h / 2) {
			return true;
		} else {
			return false;
		}
	}

	public void setFolder(String path) {
		pictos.clear();
		String[] allFiles = fileInteractionHelper.getFoldersAndFiles(path, false);
		ArrayList<String> allImgs = new ArrayList();
		try {
			for (int i = 0; i < allFiles.length; i++) {
				File file = new File(allFiles[i]);
				try {
					String mimetype = Files.probeContentType(file.toPath());
					// mimetype should be something like "image/png"

					if (mimetype != null && mimetype.split("/")[0].equals("image")) {
						// System.out.println("it is an image");
						allImgs.add(path + "\\" + allFiles[i]);

						pictos.add(new PictogramImage(p, p.width / 2, p.height / 2, pictoDimens - edgeRad, margin, stdTs, edgeRad, textCol, textYShift, false, allImgs.get(allImgs.size() - 1), file.getName(), null));

					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		setPictoPositions();
	}

	private void setPictoPositions() {
		pictoPositions.clear();
		if (pictos.size() > 0) {
			int px = x - w / 2 + pictoDimens / 2 + margin, py = y - h / 2 + pictoDimens / 2 + margin;
			for (int i = 0; i < pictos.size(); i++) {
				pictoPositions.add(new PVector(px, py));
				pictos.get(i).setPos(px, py);

				px += margin + pictoDimens;
				if (px > x + w / 2 - pictoDimens / 4) {
					px = x - w / 2 + pictoDimens / 2 + margin;
					py += margin + pictoDimens;
				}

			}
		}
	}

}
