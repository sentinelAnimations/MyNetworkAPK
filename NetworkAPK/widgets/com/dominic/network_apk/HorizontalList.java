package com.dominic.network_apk;

import java.lang.reflect.Method;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.data.StringList;

public class HorizontalList<T> implements Widgets {
	private int x, y, xShift, yShift, w, h, margin, edgeRad, stdTs, titleBoxWidth, btnSize, btnSizeSmall, type, dark, light, lighter, textCol, textDark,border, shiftPerClick = 20, lastDisplayedInd, firstDisplayedInd, selectedInd = 0, markedInd = 0;
	private float tYShift, startListX, endListX, shiftListX = 0;
	private float[] listX;
	private char splitChar;
	private Boolean isParented, showSelected, showMarked;
	public Boolean isNewSelected = true, isNewMarked = true;
	private String title;
	private String[] list,displayList;
	private String[] pictoPaths;
	private PFont stdFont;
	private PApplet p;
	private T parent;
	private PictogramImage picto;
	public ImageButton goLeft_btn, goRight_btn;

	public HorizontalList(PApplet p, int x, int y, int w, int h, int margin, int edgeRad, int stdTs, int titleBoxWidth, int btnSize, int btnSizeSmall, int dark, int light, int lighter, int textCol, int textDark,int border,char splitChar, Boolean isParented, Boolean showSelected, Boolean showMarked, String title, String[] pictoPaths, String[] list, PFont stdFont, T parent) {
		this.p = p;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.margin = margin;
		this.edgeRad = edgeRad;
		this.stdTs = stdTs;
		this.titleBoxWidth = titleBoxWidth;
		this.btnSize = btnSize;
		this.btnSizeSmall = btnSizeSmall;
		this.dark = dark;
		this.light = light;
		this.lighter = lighter;
		this.textCol = textCol;
		this.textDark = textDark;
		this.border=border;
		this.splitChar=splitChar;
		this.showSelected = showSelected;
		this.showMarked = showMarked;
		this.isParented = isParented;
		this.title = title;
		this.pictoPaths = pictoPaths;
		this.list = list;
		this.stdFont = stdFont;
		this.parent = parent;

		
		displayList=list;
		xShift = x;
		yShift = y;
		tYShift = stdTs * 0.1f;
		listX = new float[list.length];
		picto = new PictogramImage(p, x - w / 2 + 2 * margin + btnSizeSmall / 2, y, btnSizeSmall - margin, margin, stdTs, edgeRad, textCol, false, pictoPaths[0], "", null);
		goLeft_btn = new ImageButton(p, x - w / 2 + margin * 2 + titleBoxWidth + btnSizeSmall / 2, y, btnSizeSmall, btnSizeSmall, stdTs, margin, edgeRad, -1, true, false, textCol, lighter, pictoPaths[1], "", null);
		goRight_btn = new ImageButton(p, x + w / 2 - margin - btnSizeSmall / 2, y, btnSizeSmall, btnSizeSmall, stdTs, margin, edgeRad, -1, true, false, textCol, lighter, pictoPaths[2], "", null);
		startListX = goLeft_btn.getX() + goLeft_btn.getH() / 2.0f + margin;
		endListX = goRight_btn.getX() - goRight_btn.getH() / 2 - margin;
		type = 0;
	}

	public HorizontalList(PApplet p, int x, int y, int w, int h, Boolean isParented, PFont stdFont, T parent) {
		this.p = p;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.isParented = isParented;
		this.stdFont = stdFont;
		this.parent = parent;
		xShift = x;
		yShift = y;
		type = 1;
	}

	public void render() {
		if (isParented) {
			getParentPos();
		}
		if (list.length > 0) {
			if (goLeft_btn.isClicked == true) {
				if (shiftListX < 0) {
					shiftListX += shiftPerClick;
				}
				goLeft_btn.isClicked = false;
			}

			if (goRight_btn.isClicked == true) {

				if (lastDisplayedInd < list.length - 1) {
					shiftListX -= shiftPerClick;
				}
				goRight_btn.isClicked = false;
			}
		}

		if (type == 0) {
			p.fill(light);
			p.stroke(light);
			p.rect(x, y, w, h, edgeRad);
			p.fill(lighter);
			p.rect(x - w / 2 + margin + titleBoxWidth / 2, y, titleBoxWidth, btnSizeSmall, edgeRad);
			picto.render();
			p.fill(textCol);
			p.textAlign(p.LEFT, p.CENTER);
			p.textFont(stdFont);
			p.textSize(stdTs);
			p.text(title, x - w / 2 + 3 * margin + btnSizeSmall, y - tYShift);

			if (displayList.length > 0) {
				p.textAlign(p.CENTER, p.CENTER);
				float xPos = startListX + shiftListX;
				lastDisplayedInd = listX.length - 1;
				firstDisplayedInd = 0;
				for (int i = 0; i < displayList.length; i++) {
					xPos += p.textWidth(displayList[i]) / 2 + margin;
					if (i > 0) {
						xPos += p.textWidth(displayList[i - 1]) / 2 + margin * 2;
					}
					listX[i] = xPos;

					if (xPos + p.textWidth(displayList[i]) + margin < endListX) {
						p.stroke(light);
						if(showSelected) {
							if(selectedInd==i) {
								p.stroke(textCol);
							}
						}else {
							if(showMarked) {
								if(markedInd==i) {
									p.stroke(border);
								}
						}
						}

						if (xPos - p.textWidth(displayList[i]) / 2 - margin >= startListX) {
							p.fill(lighter);
							p.rect(xPos, y, p.textWidth(displayList[i]) + margin * 2, btnSizeSmall, edgeRad);
							p.fill(textCol);
							if(p.textWidth(displayList[i])<endListX-startListX) {
							p.text(displayList[i], xPos, y - tYShift);
							}else {
								String[] splitStr=p.split(displayList[i],splitChar);
								p.text(splitStr[splitStr.length-1], xPos, y - tYShift);

							}
						} else {
							firstDisplayedInd = i + 1;
						}

					} else {
						if (i - 1 >= 0) {
							lastDisplayedInd = i - 1;
							break;
						}
					}
				}
				p.stroke(light);
				float lastX = listX[lastDisplayedInd] + p.textWidth(displayList[lastDisplayedInd]) / 2 + margin * 2;
				if (endListX - lastX > 0) {
					p.fill(lighter);
					p.rect(lastX + (endListX - lastX) / 2, y, endListX - lastX, btnSizeSmall, edgeRad);
				}

			} else {
				p.fill(lighter);
				p.rect(startListX + (endListX - startListX) / 2, y, endListX - startListX, btnSizeSmall, edgeRad);
			}

			if (firstDisplayedInd != 0) {
				if (firstDisplayedInd > listX.length - 1) {
					firstDisplayedInd = 1;
				}
				if (listX[firstDisplayedInd] - startListX - p.textWidth(displayList[firstDisplayedInd]) / 2 - margin * 2 > 0) {
					p.fill(lighter);
					p.rect(startListX + ((listX[firstDisplayedInd] - startListX - p.textWidth(displayList[firstDisplayedInd]) / 2 - margin) / 2), y, listX[firstDisplayedInd] - startListX - p.textWidth(displayList[firstDisplayedInd]) / 2 - margin * 2, btnSizeSmall, edgeRad);
				}
			}

			goLeft_btn.render();
			goRight_btn.render();

		}

		if (type == 1) {

		}
	}

	public void onMousePressed() {
		if (type == 0) {

		}

		if (type == 1) {

		}
	}

	public void onMouseReleased() {
		if (type == 0 && listX.length > 0) {
			for (int i = firstDisplayedInd; i <= lastDisplayedInd; i++) {
				if (i >= 0) {
					if (p.mouseY > y - h / 2 + margin && p.mouseY < y + h / 2 - margin) {
						if (p.mouseX > listX[i] - p.textWidth(displayList[i]) / 2 - margin && p.mouseX < listX[i] + p.textWidth(displayList[i]) / 2 + margin) {
							selectedInd = i;
							isNewSelected = true;
						}
					}
				}
			}
		}

		if (type == 1) {

		}
	}

	public void onMouseRightReleased() {
		if (type == 0 && listX.length > 0) {
			for (int i = firstDisplayedInd; i <= lastDisplayedInd; i++) {
				if (i >= 0) {
					if (p.mouseY > y - h / 2 + margin && p.mouseY < y + h / 2 - margin) {
						if (p.mouseX > listX[i] - p.textWidth(displayList[i]) / 2 - margin && p.mouseX < listX[i] + p.textWidth(displayList[i]) / 2 + margin) {
							markedInd = i;
							isNewMarked = true;
						}
					}
				}
			}
		}

		if (type == 1) {

		}
	}

	public void onScroll(float e) {
		if (type == 0) {
			if (p.mouseX > x - w / 2 && p.mouseX < x + w / 2 && p.mouseY > y - h / 2 && p.mouseY < y + h / 2) {
				if (e > 0) {
					if (lastDisplayedInd < displayList.length - 1) {
						shiftListX -= shiftPerClick / 2;
					}
				} else {
					if (shiftListX < 0) {
						shiftListX += shiftPerClick / 2;
					}
				}
			}
		}

		if (type == 1) {

		}
	}

	public void setList(String[] l) {
		if (type == 0) {
			list = new String[l.length];
			displayList = new String[l.length];

			list = l;
			listX = new float[list.length];
			isNewSelected = false;
			if (selectedInd > l.length - 1) {
				selectedInd = 0;
			}
			if (markedInd > l.length - 1) {
				markedInd = 0;
			}
			for(int i=0;i<l.length;i++) {
				if(p.textWidth(l[i])<(endListX-startListX)/4*3) {
				displayList[i]=l[i];
				}else {
					String elem="";
					String[] splitStr=p.split(l[i],splitChar);
					for(int i2=splitStr.length-1;i2>=0;i2--) {
						if(p.textWidth(elem+splitStr[i2]+"\\")<(endListX-startListX)/4*3) {
						elem=splitStr[i2]+"\\"+elem;
						}
					}
					displayList[i]=elem;
				}
			}
			firstDisplayedInd = 0;
			shiftListX = 0;
		}
	}

	public String[] getList() {
		return list;
	}

	public int getSelectedInd() {
		return selectedInd;
	}
	public int getMarkedInd() {
		return markedInd;
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

}
