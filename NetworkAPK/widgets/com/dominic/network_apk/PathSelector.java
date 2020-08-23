package com.dominic.network_apk;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PImage;

public class PathSelector<T> implements Widgets {

	private int x, y, xShift, yShift, w, h, stdTs, edgeRad, margin, bgCol, textCol, textDark, btnSize, hoverTime = 0, textStartX = 0;
	private float textYShift;
	private Boolean isParented, selectFolder, isHovering = false, fileExplorerIsOpen = false,renderPathSelector=true;
	private String t = "", displayText = "", hint, imgPath;
	private String[] fileExplorerPictoPaths;
	private PFont stdFont;
	private PApplet p;
	private PImage screenshot;
	private T parent;
	private ArrayList<MakeToast> makeToasts = new ArrayList<MakeToast>();
	private ImageButton openFileExplorer_btn;
	private FileExplorer fileExplorer;

	public PathSelector(PApplet p, int x, int y, int w, int h, int edgeRad, int margin, int stdTs, int btnSizeSmall, int border, int bgCol, int textCol, int dark, int light, int lighter, int textDark, float textYShift, Boolean selectFolder, Boolean isParented, String hint, String imgPath, String[] fileExplorerPictoPaths, PFont stdFont, T parent) {
		this.p = p;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.edgeRad = edgeRad;
		this.margin = margin;
		this.stdTs = stdTs;
		this.bgCol = bgCol;
		this.textCol = textCol;
		this.textDark = textDark;
		this.textYShift = textYShift;
		this.selectFolder = selectFolder;
		this.isParented = isParented;
		this.hint = hint;
		this.imgPath = imgPath;
		this.stdFont = stdFont;
		this.parent = parent;
		xShift = x;
		yShift = y;
		btnSize = h - margin;
		openFileExplorer_btn = new ImageButton(p, x - w / 2 + margin + btnSize / 2, yShift, btnSize, btnSize, stdTs, margin, edgeRad, -1, textYShift, false, isParented, textCol, textCol, imgPath, "open file explorer", parent);
		textStartX = x - w / 2 + margin + btnSize;
		calcDisplayText();
		fileExplorer = new FileExplorer(p, p.width / 2, p.height / 2, p.width - margin * 2, 6 * btnSizeSmall + 19 * margin, stdTs, edgeRad, margin, dark, light, lighter, textCol, textDark, border, btnSize, btnSizeSmall, textYShift, fileExplorerPictoPaths, stdFont);

	}

	public void render() {
		if (isParented) {
			getParentPos();
		}
		if (fileExplorerIsOpen == false && renderPathSelector==true) {
			p.fill(bgCol);
			p.rect(x, y, w, h, edgeRad);
			openFileExplorer_btn.render();
			p.textAlign(PConstants.LEFT, PConstants.CENTER);
			p.textFont(stdFont);
			p.textSize(stdTs);
			if (t.length() < 1) {
				p.fill(textDark);
				p.text(hint, textStartX, y - stdTs * textYShift);
			} else {
				p.fill(textCol);
				p.text(displayText, x - w / 2 + margin + btnSize, y - stdTs * textYShift);
			}
			onHover();
		}

		if (openFileExplorer_btn.getIsClicked() == true) {

			if (fileExplorerIsOpen == false) {
				p.saveFrame("data\\imgs\\screenshots\\homeScreen.png");
				screenshot = p.loadImage("data\\imgs\\screenshots\\homeScreen.png");
				screenshot = new ImageBlurHelper(p).blur(screenshot, 3);
				fileExplorerIsOpen = true;
				renderPathSelector=false;
			}
			p.image(screenshot, p.width / 2, p.height / 2);
			fileExplorer.render();

			if (fileExplorer.getIsClosed()) {
				if (fileExplorer.getIsCanceled()) {
				} else {
					String[] splitStr = p.split(fileExplorer.getPath(), "\\");
					String setPath = "";
					if (selectFolder) {
						for (int i = 0; i < splitStr.length; i++) {
							String[] splitStr2 = p.split(splitStr[i], ".");
							if (splitStr2.length > 1) {
								break;
							}
							setPath += splitStr[i] + "\\";
						}
					} else {
						File f = new File(fileExplorer.getPath());
						if (f.isDirectory()) {
							p.println("no file selected");
						} else {
							setPath = fileExplorer.getPath();
						}
					}
					if (setPath.length() > 0) {
						setText(setPath);
					}
				}
				fileExplorer.setIsClosed(false);
				fileExplorer.setIsCanceled(false);
				fileExplorerIsOpen = false;
				renderPathSelector=true;
				openFileExplorer_btn.setIsClicked(false);
			}
		} else {
			fileExplorer.setIsCanceled(false);
			fileExplorer.setIsClosed(false);
		}

	}

	private void calcDisplayText() {

		displayText = "";
		int td = (x + w / 2 - margin) - textStartX;

		String[] splitStr = p.split(t, "\\");
		for (int i = splitStr.length - 1; i >= 0; i--) {
			if (p.textWidth("..." + displayText + splitStr[i]) < td) {
				if (splitStr[i].length() > 0) {
					displayText = splitStr[i] + "\\" + displayText;
				}
			} else {
				if (displayText.length() < 5) {
					for (int i2 = splitStr[splitStr.length - 1].length() - 1; i2 >= 0; i2--) {
						if (p.textWidth("..." + displayText + splitStr[splitStr.length - 1].charAt(i2)) < td) {
							displayText = splitStr[splitStr.length - 1].charAt(i2) + displayText;
						} else {
							break;
						}
					}
					displayText = "..." + displayText;
				} else {
					displayText = "..." + displayText;
				}
				break;
			}
		}
		if (displayText.length() > 0) {
			while (displayText.charAt(displayText.length() - 1) == '\\') {
				displayText = displayText.substring(0, displayText.length() - 1);
			}
		}
	}

	private void onHover() {
		if (t.length() > 0) {
			if (p.mouseX > x - w / 2 + h + margin && p.mouseX < x + w / 2 && p.mouseY > y - h / 2 && p.mouseY < y + h / 2) {
				if (isHovering) {
					hoverTime++;
				}
				isHovering = true;
			} else {
				hoverTime = 0;
				isHovering = false;
			}
			if (hoverTime > 72) {
				int tw = (int) p.textWidth(t) + margin * 2;
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

				p.fill(0, 200);
				p.noStroke();
				p.rect(mx + tw / 2, my + stdTs, PApplet.abs(tw) + margin * 2, stdTs * 2, edgeRad);
				p.fill(textCol);
				p.text(t, mx + tw, my + stdTs - stdTs * textYShift);
			}
		}
	}

	public void onMousePressed(int mouseButton) {
		if(fileExplorerIsOpen==false) {
		openFileExplorer_btn.onMousePressed();
		}else {
		fileExplorer.onMousePressed();
		}
	}

	public void onMouseReleased(int mouseButton) {
		if(fileExplorerIsOpen==false) {
		openFileExplorer_btn.onMouseReleased();
		}else {
		fileExplorer.onMouseReleased(mouseButton);
		}
	}

	public void onKeyReleased(char key) {
		if(fileExplorerIsOpen==false) {
		openFileExplorer_btn.onKeyReleased(key);
		}else {
		fileExplorer.onKeyReleased(key);
		}
	}

	public void onScroll(float e) {
		if(fileExplorerIsOpen) {
		fileExplorer.onScroll(e);
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
			textStartX = x - w / 2 + margin + btnSize;

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

	// whether PathSelector should be used to select Folder or File
	public Boolean getSelectFolder() {
		return selectFolder;
	}

	public Boolean getFileExplorerIsOpen() {
		return fileExplorerIsOpen;
	}

	public String getPath() {
		return t;
	}

	public void setText(String text) {
		t = text;
		if (t.length() > 0) {
			try {
				while (t.charAt(t.length() - 1) == '\\') {
					t = t.substring(0, t.length() - 1);
				}	
			} catch (Exception e) {
				// TODO: handle exception
			}
			
		}
		calcDisplayText();
	}

	public ImageButton getOpenFileExplorer_btn() {
		return openFileExplorer_btn;
	}
	public void setRenderPathSelector(Boolean state) {
		renderPathSelector=state;
	}
}
