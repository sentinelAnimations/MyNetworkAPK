package com.dominic.network_apk;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.data.StringList;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import net.davidashen.text.Hyphenator;

public class TextField<T> implements Widgets {
	private int textCol, w, h, x, y,xShift,yShift, ts;
	private float textYShift;
	private Boolean stretch,isParented;
	private String t;
	private PApplet p;
	private PFont f;
	private Hyphenator hy;
	private T parent;

	public TextField(PApplet p, int textCol, int w, int h, int x, int y, int stdTs,float textYShift, Boolean stretch,Boolean isParented, String t,
			PFont f,T parent) {
		this.p = p;
		this.textCol = textCol;
		this.w = w;
		this.h = h;
		this.x = x;
		this.y = y;
		this.ts = stdTs;
		this.stretch = stretch;
		this.textYShift=textYShift;
		this.isParented=isParented;
		this.t = t;
		this.f = f;
		this.parent=parent;
		xShift=x;
		yShift=y;

		setupHyphenator();
		processToDisplay();

	}

	public void render() {
		
		if (isParented) {
			getParentPos();
		}
		
		if (stretch == true) {
			displayTextStretched();
		} else {
			displayTextUnStretched();
		}
	}

	private void displayTextStretched() {
		String[] splitStr = PApplet.split(t, "\n");
		p.textAlign(PConstants.LEFT, PConstants.CENTER);
		p.fill(textCol);
		p.textFont(f);
		p.textSize(ts);
		for (int i = 0; i < splitStr.length; i++) {
			int spaceAmounth = Math.round((w - p.textWidth(splitStr[i])) / p.textWidth(" "));
			String[] splitStr2 = PApplet.split(splitStr[i], " ");
			String newStr = splitStr2[0];
			for (int i2 = 1; i2 < splitStr2.length; i2++) {				
				if (i2 < spaceAmounth) {
					newStr += "  " + splitStr2[i2];
				} else {
					newStr += " " + splitStr2[i2];
				}
			}
			if (spaceAmounth < splitStr[i].length() / 2) {
				p.text(newStr, x - w / 2, y - h / 2 + i * ts * 1.1f);
			} else {
				p.text(splitStr[i], x - w / 2, y - h / 2 + i * ts * 1.1f);

			}
		}
	}

	private void displayTextUnStretched() {
		p.textAlign(PConstants.BOTTOM, PConstants.CENTER);
		p.fill(textCol);
		p.textFont(f);
		p.textSize(ts);
		p.text(t, x - w / 2, y);
	}

	private void processToDisplay() {
		p.textSize(ts);
		String[] splitStr = PApplet.split(t, " ");
		String newT = splitStr[0];
		String newT2 = newT;
		for (int i = 1; i < splitStr.length; i++) {
			if (PApplet.match(splitStr[i], "\n") != null) {
				newT += "\n" + splitStr[i];
				newT2 = splitStr[i];
			} else {
				if (p.textWidth(newT2) + p.textWidth(splitStr[i]) < w) {
					newT += " " + splitStr[i];
					newT2 += " " + splitStr[i];
				} else {
					StringList l = hyphenateWords(splitStr[i]);
					newT += " ";
					newT2 += " ";
					for (int i2 = 0; i2 < l.size(); i2++) {
						if (p.textWidth(newT2) + p.textWidth(l.get(i2) + "-") < w) {
							newT += l.get(i2);
							newT2 += l.get(i2);
						} else {
							if (l.size() > 1 && i2>0) {
								newT += "-";
							}
							newT += "\n" + l.get(i2);
							newT2 = l.get(i2);
						}
					}
					// newT += "\n" + splitStr[i];
					// newT2 = splitStr[i];
				}
			}
		}
		t = newT;
	}

	private StringList hyphenateWords(String s) {
		int ind = 0, singleFirst = 0, singleLast = 0;
		StringList hyphenatedSegments = new StringList();
		String hyphenated_word = hy.hyphenate(s);
		String[] splitStr = PApplet.split(hyphenated_word, "­");
		// p.println("--", splitStr, splitStr.length);

		if (splitStr.length > 1) {
			Boolean match = false;
			if (((splitStr[0].charAt(0) == '.' || splitStr[0].charAt(0) == ',') && splitStr[0].length() < 3)) {
				match = true;
				// p.println("match first-----------------------------");
			}
			if (splitStr[0].length() < 2 || match == true) {
				splitStr[1] = splitStr[0] + splitStr[1];
				singleFirst = 1;
			}

			match = false;
			if (((splitStr[splitStr.length - 1].charAt(splitStr[splitStr.length - 1].length() - 1) == '.'
					|| splitStr[splitStr.length - 1].charAt(splitStr[splitStr.length - 1].length() - 1) == ',')
					&& splitStr[splitStr.length - 1].length() < 3)) {
				match = true;
				// p.println("match last------------------------------");

			}

			if (splitStr[splitStr.length - 1].length() < 2 || match == true) {
				splitStr[splitStr.length - 2] = splitStr[splitStr.length - 2] + splitStr[splitStr.length - 1];
				singleLast = 1;
			}
		}

		for (int i = singleFirst; i < splitStr.length - singleLast; i++) {
			String[] searchForHyph = PApplet.split(splitStr[i], "\u200b");
			for (int i2 = 0; i2 < searchForHyph.length; i2++) {
				hyphenatedSegments.append(searchForHyph[i2]);
			}
		}

		// p.println(hyphenatedSegments);
		return hyphenatedSegments;
	}

	private void setupHyphenator() {
		hy = new Hyphenator();
		try {
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			InputStream is = classloader.getResourceAsStream("texSources/hyphen.tex");
			hy.loadTable(new java.io.BufferedInputStream(is));
			//"C:\\Users\\Dominic\\git\\MyNetworkAPK\\NetworkAPK\\data\\tex\\hyphen.tex"
			//hy.loadTable(new java.io.BufferedInputStream(new java.io.FileInputStream("data/texSources/hyphen.tex")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
