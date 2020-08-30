package com.dominic.network_apk;

import java.lang.reflect.Method;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.data.StringList;

public class EditText<T> implements Widgets {

	private int x, y, xShift, yShift, w, h, stdTs, light, textCol, edgeRad, margin, cursorInd = 0, cursorX, cursorY, textStartX, textStartY, row = 0, maxRows, cursorAlpha = 0;
	private float textYShift;
	private Boolean isParented, doOnceOnStartup = true, useBg;
	public Boolean isActive = false;
	private String hint, displT;
	private char[] forbiddenChars;
	private PApplet p;
	private PFont stdFont;
	private StringList strList = new StringList();
	private T parent;
	private ArrayList<MakeToast> makeToasts = new ArrayList<MakeToast>();

	public EditText(PApplet p, int x, int y, int w, int h, int stdTs, int light, int textCol, int edgeRad, int margin, float textYShift, Boolean useBg, Boolean isParented, String hint, char[] forbiddenChars, PFont stdFont, T parent) {
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
		this.textYShift = textYShift;
		this.useBg = useBg;
		this.isParented = isParented;
		this.hint = hint;
		this.forbiddenChars = forbiddenChars;
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

		p.textAlign(PConstants.LEFT, PConstants.TOP);
		if (useBg) {
			p.fill(light);
			p.stroke(light);
			p.rect(x, y, w, h, edgeRad);
		}

		Boolean showText = false;
		for (int i = 0; i < strList.size(); i++) {
			if (strList.get(i).length() > 0) {
				showText = true;
				break;
			}
		}

		if (showText == false) {
			p.fill(textCol, 100);
			p.textFont(stdFont);
			p.textSize(stdTs);
			p.text(hint, textStartX, y - stdTs / 2 - stdTs * textYShift);
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
		p.stroke(textCol, PApplet.abs(PApplet.sin(PApplet.radians(cursorAlpha))) * 255 + 100);
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

	public void onMousePressed() {
		if (mouseIsInArea() == false) {
			isActive = false;
		}
	}

	public void onMouseReleased() {
		if (mouseIsInArea()) {
			getCursorPosByCoordinates(p.mouseX, p.mouseY);
			isActive = true;
		} else {
			isActive = false;
		}
	}

	public void onKeyPressed(char key) {
		onBackspace(key);
	}

	public void onKeyReleased(char k) {

		p.textSize(stdTs);

		Boolean charIsForbidden = false;
		for (int i = 0; i < forbiddenChars.length; i++) {
			if (k == forbiddenChars[i]) {
				charIsForbidden = true;
			}
		}

		if (charIsForbidden == false) {
			if (isActive) {
				checkForOutOfBounds();
				String t;

				if (strList.size() <= maxRows) {
					if (strList.size() + 1 > maxRows && p.textWidth(strList.get(row) + k) > w - margin * 2) {
						if (makeToasts.size() < 2) {
							makeToasts.add(new MakeToast(p, p.width / 2, p.height - stdTs * 2, stdTs, margin, edgeRad, 100, light, textCol, textYShift, false, "Maximum rows reached now", stdFont, null));
						}
					} else {
						if (k > 31 && k < 127) {
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
					if (k == PConstants.ENTER && strList.size() < maxRows) {

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

					if (strList.size() == maxRows && makeToasts.size() < 2 && k == PConstants.ENTER) {
						makeToasts.add(new MakeToast(p, p.width / 2, p.height - stdTs * 2, stdTs, margin, edgeRad, 100, light, textCol, textYShift, false, "Maximum rows reached now", stdFont, null));
					}
				}

				/*
				 * if (k == PConstants.BACKSPACE) { t = strList.get(row); if (cursorInd > 1) {
				 * strList.set(row, t.substring(0, cursorInd - 1) + t.substring(cursorInd));
				 * cursorInd--; } else { if (cursorInd > 0) { strList.set(row, t.substring(1));
				 * cursorInd--; } else { if (row > 0) { cursorInd = strList.get(row -
				 * 1).length(); strList.set(row - 1, strList.get(row - 1) + strList.get(row));
				 * strList.remove(row); row--;
				 * 
				 * } } } }
				 */

				if (k == PConstants.CODED) {
					if (p.keyCode == PConstants.LEFT) {
						cursorInd--;
						if (cursorInd < 0) {
							row--;
							if (row >= 0) {
								cursorInd = strList.get(row).length();
							}
						}
					}
					if (p.keyCode == PConstants.RIGHT) {
						cursorInd++;
						if (cursorInd > strList.get(row).length()) {
							row++;
							if (row < strList.size()) {
								cursorInd = 0;
							}
						}

					}
					if (p.keyCode == PConstants.UP) {
						row--;
					}
					if (p.keyCode == PConstants.DOWN) {
						row++;
					}
				}

				checkForOutOfBounds();

				prepareStringList();
				getCursorPos();

			}
		}
	}

	private void onBackspace(char k) {
		String t;
		Boolean charIsForbidden = false;
		for (int i = 0; i < forbiddenChars.length; i++) {
			if (k == forbiddenChars[i]) {
				charIsForbidden = true;
			}
		}
		if (charIsForbidden == false) {
			if (isActive) {
				checkForOutOfBounds();
				if (k == PConstants.BACKSPACE) {
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
			}
		}
		checkForOutOfBounds();

		prepareStringList();
		getCursorPos();
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

		p.textSize(stdTs);

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

		p.textSize(stdTs);

		row = (yPos - textStartY) / stdTs;
		if (row >= maxRows) {
			row = maxRows - 1;
		}
		cursorY = textStartY + row * stdTs;

		if (strList.size() > 0 && row < strList.size()) {
			int d = p.width, sInd = 0;
			for (int i = 0; i < strList.get(row).length(); i++) {
				if (PApplet.dist(xPos, 0, textStartX + p.textWidth(strList.get(row).substring(0, i)), 0) < d) {
					sInd = i;
					d = (int) PApplet.dist(xPos, 0, textStartX + p.textWidth(strList.get(row).substring(0, i)), 0);
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
		if (cursorX > x - w / 2 && cursorX < x + w / 2 && cursorY > y - h / 2 && cursorY < y + h / 2) {
		} else {
			getCursorPosByCoordinates(x - w / 2 + margin, y - h / 2 + stdTs / 2);
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

	public Boolean mouseIsInArea() {
		if (p.mouseX > x - w / 2 && p.mouseX < x + w / 2 && p.mouseY > y - h / 2 && p.mouseY < y + h / 2) {
			return true;
		} else {
			return false;
		}
	}

	public void setText(String t) {
		String s = "";
		StringList tempList = new StringList();
		for (int i = 0; i < t.length(); i++) {
			if (p.textWidth(s + t.charAt(i)) < w - margin * 2) {
				s += t.charAt(i);
				if (i == t.length() - 1) {
					tempList.append(s);
				}
			} else {
				tempList.append(s);
				s = "";
			}
		}
		if (tempList.size() > maxRows) {
			p.println("Text to long for EditText field");
		} else {
			strList.clear();
			strList.append(tempList);
		}
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

	public void setPos(int xp, int yp) {
		x = xp;
		xShift = x;
		y = yp;
		yShift = y;
	}

}
