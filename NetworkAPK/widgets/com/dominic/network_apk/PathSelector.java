package com.dominic.network_apk;

import java.lang.reflect.Method;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;

public class PathSelector<T> implements Widgets {

	private int x, y, xShift, yShift, w, h, stdTs, edgeRad, margin, bgCol, textCol, textDark, btnSize, hoverTime = 0, textStartX = 0;
	private Boolean isParented, selectFolder, isHovering = false;
	private String t = "",displayText="", hint, imgPath;
	private PFont stdFont;
	private PApplet p;
	private T parent;
	private ArrayList<MakeToast> makeToasts = new ArrayList<MakeToast>();
	public ImageButton openFileExplorer_btn;

	public PathSelector(PApplet p, int x, int y, int w, int h, int edgeRad, int margin, int stdTs, int bgCol, int textCol, int textDark, Boolean selectFolder, Boolean isParented, String hint, String imgPath, PFont stdFont, T parent) {
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
		this.selectFolder = selectFolder;
		this.isParented = isParented;
		this.hint = hint;
		this.imgPath = imgPath;
		this.stdFont = stdFont;
		this.parent = parent;
		xShift = x;
		yShift = y;
		btnSize = h - margin;
		openFileExplorer_btn = new ImageButton(p, x - w / 2 + margin + btnSize / 2, yShift, btnSize, btnSize, stdTs, margin, edgeRad, -1, false, isParented, textCol, textCol, imgPath, "open file explorer", parent);
		textStartX = x - w / 2 + margin + btnSize;
		calcDisplayText();

	}

	public void render() {
		if (isParented) {
			getParentPos();
		}

		p.fill(bgCol);
		p.rect(x, y, w, h, edgeRad);
		openFileExplorer_btn.render();
		p.textAlign(PConstants.LEFT, PConstants.CENTER);
		p.textFont(stdFont);
		p.textSize(stdTs);
		if (t.length() < 1) {
			p.fill(textDark);
			p.text(hint, textStartX, y - stdTs / 5);
		} else {
			p.fill(textCol);
			p.text(displayText, x - w / 2 + margin + btnSize, y - stdTs / 5);
		}
		onHover();
	}
	
	private void calcDisplayText() {
		
		displayText="";
		int td=(x+w/2-margin)-textStartX;
		
		String[] splitStr = p.split(t, "\\");
		for(int i=splitStr.length-1;i>=0;i--) {
			if(splitStr[i].length()>0) {
			if(p.textWidth("..."+displayText+splitStr[i])<td) {
				displayText=splitStr[i]+"\\"+displayText;	
			}else {
				if(i==splitStr.length-1) {
					for(int i2=splitStr[splitStr.length-1].length()-1;i2>=0;i2--) {
						if(p.textWidth("..."+displayText+splitStr[splitStr.length-1].charAt(i2))<td) {
							displayText=splitStr[splitStr.length-1].charAt(i2)+displayText;
						}else {
							break;
						}	
					}
					displayText="..."+displayText;
				}else {
					displayText="..."+displayText;
				}
				break;
			}
		}
		}
		if(displayText.length()>0) {
			while(displayText.charAt(displayText.length()-1)=='\\') {
				displayText=displayText.substring(0,displayText.length()-1);
			}
		}
	}
	
	private void onHover() {
		if (t.length() > 0) {
			if (p.mouseX > x - w / 2 && p.mouseX < x + w / 2 && p.mouseY > y - h / 2 && p.mouseY < y + h / 2) {
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
				p.text(t, mx + tw, my + stdTs / 1.1f);
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

	public Boolean getSelectFolder() {
		return selectFolder;
	}

	public void setText(String text) {
		t = text;
		calcDisplayText();
	}

}
