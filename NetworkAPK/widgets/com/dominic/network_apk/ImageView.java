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

	private int x, y, w, h, xShift, yShift, scrollShift = 0, stdTs, edgeRad, margin, btnSize, btnSizeSmall, dark, light, lighter, lightest, textCol, textDark, border, pictoDimens, selectedInd = 0, loadedImgs, allImgsSize;
	private float textYShift;
	private Boolean isParented, isDoubleClicked = false;
	private long lastTimeClicked = 0;
	private int[] borderCols;
	private ArrayList<PictogramImage> pictos = new ArrayList();
	private ArrayList<PVector> pictoPositions = new ArrayList();
	ArrayList<String> allImgs = new ArrayList();
	private PFont stdFont;
	private PApplet p;
	private T parent;
	private PictogramImage largeImage_PictogramImage;
	private MainActivity mainActivity;
	private FileInteractionHelper fileInteractionHelper;
	private Thread loadingThread;

	public ImageView(PApplet p, int x, int y, int w, int h, int stdTs, int edgeRad, int margin, int btnSizeLarge, int btnSize, int btnSizeSmall, int dark, int light, int lighter, int lightest, int textCol, int textDark, int border, float textYShift, Boolean isParented, PFont stdFont, T parent) {
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
		this.dark = dark;
		this.light = light;
		this.lighter = lighter;
		this.lightest = lightest;
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
		if (isParented) {
			getParentPos();
		}

		// setFolder("D:\\algemeine Bilder");
		// setFolder("C:\\Users\\domin\\OneDrive\\Pictures\\Eigene Aufnahmen");
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
						if (i < borderCols.length) {
							p.stroke(borderCols[i]);
						}
					}
					p.noFill();
					p.rect(pic.getX(), pic.getY(), pictoDimens, pictoDimens, edgeRad);
					pic.render();
				}
			}
		}

		for (int i = pictos.size() - 1; i >= 0; i--) {
			pictos.get(i).getHoverText().render();
		}

		if (loadingThread != null && loadingThread.isAlive()) {
			String loadingString = "Loading Images: " + loadedImgs + "/" + allImgsSize;
			p.fill(lighter);
			p.stroke(lighter);
			p.rect(x, y + h / 2 - stdTs / 2 - margin * 2, p.textWidth(loadingString) + margin * 2, stdTs + margin * 2, edgeRad);
			p.fill(textCol);
			p.textFont(stdFont);
			p.textAlign(p.CENTER, p.CENTER);
			p.textSize(stdTs);
			p.text(loadingString, x, y + h / 2 - stdTs / 2 - margin * 2);

		}
		if (isDoubleClicked) {
			p.fill(dark, 200);
			p.noStroke();
			p.rect(x, y, w, h, edgeRad);
			largeImage_PictogramImage.render();
		}
	}

	public void onMousePressed(int mouseButton) {
	}

	public void onMouseReleased(int mouseButton) {
		if (pictos.size() > 0) {
			if (mouseIsInArea()) {
				isDoubleClicked = false;
				// lastTimeClicked = System.nanoTime() / 1000000000;
			}
			for (int i = pictos.size() - 1; i >= 0; i--) {
				PictogramImage pic = pictos.get(i);
				if (pic.mouseIsInArea()) {
					selectedInd = i;
					long curTime = System.nanoTime() / 100000000;
					if ((curTime - lastTimeClicked) / 10.0f <= 0.5) {
						isDoubleClicked = true;
						largeImage_PictogramImage = new PictogramImage(p, x, y, w, h, margin, stdTs, edgeRad, textCol, textYShift, false, false, allImgs.get(selectedInd), "", null);
						largeImage_PictogramImage.setLightCol(lightest);
					} else {
						isDoubleClicked = false;
					}

				}
			}
			lastTimeClicked = System.nanoTime() / 100000000;
		}
	}

	public void onKeyReleased(char key) {
		if (key == p.CODED) {
			if (p.keyCode == p.UP) {
				doScrollAction(true);
			}
			if (p.keyCode == p.DOWN) {
				doScrollAction(false);
			}
		}
	}

	public void onScroll(float e) {
		if (e < 0) {
			doScrollAction(true);
		} else {
			doScrollAction(false);
		}
	}

	private void doScrollAction(Boolean scrollUpwards) {
		int scrollSpeed = pictoDimens + margin;
		if (pictos.size() > 0) {
			if (scrollUpwards) {
				if (pictos.get(0).getY() < y - h / 2 + pictoDimens / 2 + margin) {
					for (int i = 0; i < pictos.size(); i++) {
						PictogramImage p = pictos.get(i);
						p.setPos(p.getX(), p.getY() + scrollSpeed);
					}
					scrollShift += scrollSpeed;
				}
			} else {
				if (pictos.get(pictos.size() - 1).getY() > y - h / 2 + pictoDimens / 2 + margin) {
					for (int i = 0; i < pictos.size(); i++) {
						PictogramImage p = pictos.get(i);
						p.setPos(p.getX(), p.getY() - scrollSpeed);
					}
					scrollShift -= scrollSpeed;
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

	public void setPath(String path) {

		loadingThread = new Thread(new Runnable() {
			@Override
			public void run() {
				pictos.clear();
				String[] allFiles = fileInteractionHelper.getFoldersAndFiles(path, false);
				allImgs.clear();
				try {
					for (int i = 0; i < allFiles.length; i++) {
						File file = new File(allFiles[i]);
						try {
							String mimetype = Files.probeContentType(file.toPath());
							if (mimetype != null && mimetype.split("/")[0].equals("image")) { // -->check if file is an image
								allImgs.add(path + "\\" + allFiles[i]);

								// pictos.add(new PictogramImage(p, p.width / 2, p.height / 2, pictoDimens -
								// edgeRad, margin, stdTs, edgeRad, textCol, textYShift, false,
								// allImgs.get(allImgs.size() - 1), file.getName(), null));

							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					allImgsSize = allImgs.size();
					Boolean allInstanciated = false;
					Boolean lastLoaded = true;
					int cores = Runtime.getRuntime().availableProcessors();

					for (int i = 0; i < cores; i++) {
						if (pictos.size() < cores && i < allImgs.size()) {
							String[] splStr = p.split(allImgs.get(i), "\\");
							String infoText = splStr[splStr.length - 1];
							pictos.add(new PictogramImage(p, p.width / 2, p.height / 2, pictoDimens - edgeRad, pictoDimens - edgeRad, margin, stdTs, edgeRad, textCol, textYShift, false, false, allImgs.get(i), infoText, null));
							pictos.get(pictos.size() - 1).setLightCol(lightest);
						} else {
							allInstanciated = true;
							break;
						}
					}
					setPictoPositions();
					while (allInstanciated == false) {

						if (pictos.size() < allImgs.size()) {
							Boolean addedNewPicto = false;
							for (int i = pictos.size() - cores; i < pictos.size(); i++) {
								PictogramImage pic = pictos.get(i);
								if (pic.getIsLoaded()) {
									String curImg = allImgs.get(pictos.size());
									String[] splStr = p.split(curImg, "\\");
									String infoText = splStr[splStr.length - 1];
									pictos.add(new PictogramImage(p, p.width / 2, p.height / 2, pictoDimens - edgeRad, pictoDimens - edgeRad, margin, stdTs, edgeRad, textCol, textYShift, false, false, curImg, infoText, null));
									pictos.get(pictos.size() - 1).setLightCol(lightest);
									addedNewPicto = true;
								}
							}
							if (addedNewPicto) {
								setPictoPositions();
								loadedImgs = pictos.size();
								if (loadedImgs == allImgs.size()) {
									allInstanciated = true;
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				// setPictoPositions();
			}
		});
		loadingThread.start();
	}

	private void setPictoPositions() {
		pictoPositions.clear();
		if (pictos.size() > 0) {
			borderCols = new int[pictos.size()];
			int px = x - w / 2 + pictoDimens / 2 + margin, py = y - h / 2 + pictoDimens / 2 + margin;
			if (pictos.size() > 0) {
				py += scrollShift;
			}
			for (int i = 0; i < pictos.size(); i++) {
				pictoPositions.add(new PVector(px, py));
				pictos.get(i).setPos(px, py);

				px += margin + pictoDimens;
				if (px > x + w / 2 - pictoDimens / 4) {
					px = x - w / 2 + pictoDimens / 2 + margin;
					py += margin + pictoDimens;
				}
				borderCols[i] = lighter;
			}
		}
	}

	public Boolean getIsLoaded() {
		return !loadingThread.isAlive();
	}

	public ArrayList<String> getAllImgsList() {
		return allImgs;
	}

	public void setBorderCols(int[] setBorderCols) {
		borderCols = setBorderCols;
	}

}
