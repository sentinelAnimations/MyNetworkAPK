package com.dominic.network_apk;

import java.lang.reflect.Method;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;
import processing.data.StringList;

public class EditText<T> implements Widgets {

	private int x, y, xShift, yShift, w, h, stdTs, light, textCol, edgeRad, margin, cursorInd = 0, cursorX, cursorY, textStartX, textStartY, row = 0, maxRows, cursorAlpha = 0;
	private Boolean isParented, doOnceOnStartup = true,useBg;
	public Boolean isActive = false;
	private String hint, displT;
	private char[] forbiddenChars;
	private PApplet p;
	private PFont stdFont;
	private StringList strList = new StringList();
	private T parent;
	private ArrayList<MakeToast> makeToasts = new ArrayList<MakeToast>();

	public EditText(PApplet p, int x, int y, int w, int h, int stdTs, int light, int textCol, int edgeRad, int margin,Boolean useBg, Boolean isParented, String hint,char[] forbiddenChars, PFont stdFont, T parent) {
		this.p = p;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.stdTs = stdTs;
		this.light = light;
		this.textCol = textCol;
		this.edgeRad = edgeRad;
		this.margin = margin;
		this.useBg=useBg;
		this.isParented = isParented;
		this.hint = hint;
		this.forbiddenChars=forbiddenChars;
		this.stdFont = stdFont;
		this.parent = parent;
		xShift = x;
		yShift = y;

		// this.h = h * 2;
		strList.append("");
		maxRows = (this.h - margin * 2) / stdTs;
		
		textStartX = x - w / 2 + margin;
		textStartY = y - h / 2 + margin;
	}

	public void render() {
		if (isParented) {
			getParentPos();
		}
		if (doOnceOnStartup) {
			cursorX = textStartX;
			cursorY = textStartY;
			doOnceOnStartup = false;
		}

		p.textAlign(p.LEFT, p.TOP);
		if(useBg) {
		p.fill(light);
		p.stroke(light);
		p.rect(x, y, w, h, edgeRad);
		}
		if (strList.get(0).length() < 1) {
			p.fill(textCol, 100);
			p.textFont(stdFont);
			p.textSize(stdTs);
			p.text(hint, textStartX, y - stdTs / 2);
		} else {
			p.fill(textCol);
			p.textFont(stdFont);
			p.textSize(stdTs);
			displayText();
		}

		if (isActive) {
			displayCursor();
		}

	}

	private void displayText() {
		for (int i = 0; i < strList.size(); i++) {
			if (i < maxRows) {
				p.text(strList.get(i), textStartX, textStartY + i * stdTs);
			}
		}
	}

	private void displayCursor() {
		cursorAlpha++;
		p.stroke(textCol, p.abs(p.sin(p.radians(cursorAlpha))) * 255 + 100);
		p.line(cursorX, cursorY, cursorX, cursorY + stdTs);

	}

	private void prepareStringList() {

		for (int i = 0; i < strList.size(); i++) {
			if (p.textWidth(strList.get(i)) > w - margin * 2) {
				String str = "", str2 = "";
				for (int i2 = 0; i2 < strList.get(i).length(); i2++) {
					if (p.textWidth(str + strList.get(i).charAt(i2)) < w - margin * 2) {
						str += strList.get(i).charAt(i2);
					} else {
						str2 += strList.get(i).charAt(i2);
					}
				}
				if (row < maxRows - 1) {
					if (row == i) {
						if (cursorInd > str.length()) {
							row++;
							cursorInd -= str.length() - 1;
							if (row < strList.size()) {
								if (strList.get(row).length() > 0) {
									cursorInd--;
								}
							}
						}
					}
					strList.set(i, str);
					if (i + 1 < strList.size()) {
						if (str2.length() > 2) {
							strList.insert(i + 1, str2);
						} else {
							strList.set(i + 1, str2 + strList.get(i + 1));
						}
					} else {
						strList.append(str2);
					}
				}
			}
		}
		// p.println(strList);

	}

	public void onMouseReleased() {
		if (p.mouseX > x - w / 2 && p.mouseX < x + w / 2 && p.mouseY > y - h / 2 && p.mouseY < y + h / 2) {
			getCursorPosByCoordinates(p.mouseX, p.mouseY);
			isActive = true;
		} else {
			isActive = false;
		}
	}

	public void onKeyReleased(char k) {
		Boolean charIsForbidden=false;
		for(int i=0;i<forbiddenChars.length;i++) {
			if(k==forbiddenChars[i]) {
				charIsForbidden=true;
			}
		}
		
		if(charIsForbidden==false) {
		if (isActive) {
			checkForOutOfBounds();
			String t;

			if (strList.size() <= maxRows) {
				if (strList.size() + 1 > maxRows && p.textWidth(strList.get(row) + k) > w - margin * 2) {
					if (makeToasts.size() < 2) {
						makeToasts.add(new MakeToast(p, p.width / 2, p.height - stdTs * 2, stdTs, margin, edgeRad, light, textCol, false, "Maximum rows reached now", stdFont, null));
					}
				} else {
					if ((int) k > 31 && (int) k < 127) {
						t = strList.get(row);
						if (cursorInd < t.length()) {
							if (cursorInd == 0) {
								t = k + t;
							} else {
								t = t.substring(0, cursorInd) + k + t.substring(cursorInd);
							}
						} else {
							t += k;
						}
						cursorInd++;
						strList.set(row, t);
					}
				}
				if (k == p.ENTER && strList.size() < maxRows) {

					if (cursorInd == strList.get(row).length()) {
						if (row == strList.size() - 1) {
							strList.append("");
						} else {
							strList.insert(row + 1, "");
						}
					} else {
						t = strList.get(row);
						if (row == strList.size() - 1) {
							strList.set(row, t.substring(0, cursorInd));
							strList.append(t.substring(cursorInd));
						} else {
							strList.set(row, t.substring(0, cursorInd));
							strList.insert(row + 1, t.substring(cursorInd));
						}
					}
					row++;
					cursorInd = 0;
				}

				if (strList.size() == maxRows && makeToasts.size() < 2 && k == p.ENTER) {
					makeToasts.add(new MakeToast(p, p.width / 2, p.height - stdTs * 2, stdTs, margin, edgeRad, light, textCol, false, "Maximum rows reached now", stdFont, null));
				}
			}
			if (k == p.BACKSPACE) {
				t = strList.get(row);
				if (cursorInd > 1) {
					strList.set(row, t.substring(0, cursorInd - 1) + t.substring(cursorInd));
					cursorInd--;
				} else {
					if (cursorInd > 0) {
						strList.set(row, t.substring(1));
						cursorInd--;
					} else {
						if (row > 0) {
							cursorInd = strList.get(row - 1).length();
							strList.set(row - 1, strList.get(row - 1) + strList.get(row));
							strList.remove(row);
							row--;

						}
					}
				}
			}

			if (k == p.CODED) {
				if (p.keyCode == p.LEFT) {
					cursorInd--;
					if (cursorInd < 0) {
						row--;
						if (row >= 0) {
							cursorInd = strList.get(row).length();
						}
					}
				}
				if (p.keyCode == p.RIGHT) {
					cursorInd++;
					if (cursorInd > strList.get(row).length()) {
						row++;
						if (row < strList.size()) {
							cursorInd = 0;
						}
					}

				}
				if (p.keyCode == p.UP) {
					row--;
				}
				if (p.keyCode == p.DOWN) {
					row++;
				}
			}

			checkForOutOfBounds();

			prepareStringList();
			getCursorPos();

		}
	}
	}

	private void checkForOutOfBounds() {
		if (row >= strList.size()) {
			row = strList.size() - 1;
		}
		if (row < 0) {
			row = 0;
		}
		if (cursorInd > strList.get(row).length()) {
			cursorInd = strList.get(row).length();
		}
		if (cursorInd < 0) {
			cursorInd = 0;
		}
	}

	private void getCursorPos() {
		cursorY = textStartY + row * stdTs;
		if (strList.get(row).length() > 1) {
			cursorX = (int) (textStartX + p.textWidth(strList.get(row).substring(0, cursorInd)));
		} else {
			if (cursorInd == 0) {
				cursorX = textStartX;
			} else {
				if (strList.get(row).length() > 0) {
					cursorX = (int) (textStartX + p.textWidth(strList.get(row).charAt(0)));
				}
			}
		}
	}

	private void getCursorPosByCoordinates(int xPos, int yPos) {
		row = (yPos - textStartY) / stdTs;
		if (row >= maxRows) {
			row = maxRows - 1;
		}
		cursorY = textStartY + row * stdTs;

		if (strList.size() > 0 && row < strList.size()) {
			int d = p.width, sInd = 0;
			for (int i = 0; i < strList.get(row).length(); i++) {
				if (p.dist(xPos, 0, textStartX + p.textWidth(strList.get(row).substring(0, i)), 0) < d) {
					sInd = i;
					d = (int) p.dist(xPos, 0, textStartX + p.textWidth(strList.get(row).substring(0, i)), 0);
				}
			}

			cursorInd = sInd;
			if (xPos > textStartX + p.textWidth((strList.get(row)))) {
				cursorInd = strList.get(row).length();

			}
			if (strList.get(row).length() > 0) {
				cursorX = (int) (textStartX + p.textWidth(strList.get(row).substring(0, cursorInd)));
			} else {
				cursorX = textStartX;
			}

		} else {
			if (strList.size() < 0) {
				cursorX = textStartX;
				cursorY = textStartY;
			}
			if (row > strList.size()) {
				cursorX = (int) p.textWidth(strList.get(strList.size() - 1));
				cursorY = (strList.size() - 1) * stdTs;
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

			textStartX = x - w / 2 + margin;
			textStartY = y - h / 2 + margin;

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
	
	public StringList getStrList() {
		return strList;
	}

	public ArrayList getToastList() {
		return makeToasts;
	}

	public void removeToast(int i) {
		makeToasts.remove(i);
	}

}
