package com.dominic.network_apk;

import processing.core.PApplet;

public class TxtStringHelper {
	PApplet p;
	public TxtStringHelper(PApplet p) {
		this.p=p;
	}
	
	public String getStringFromFile(String path) {
		String s = "";

		String[] lines = p.loadStrings(path);
		for (int i = 0; i < lines.length; i++) {
			String[] m1 = p.match(lines[i], "////");
			String[] splitStr = null;
			if (m1 == null) {
				if (lines[i].length() > 0) {
					splitStr = p.split(lines[i], "\\n");
					if (splitStr.length > 1) {
						Boolean allEmpty = true;
						for (int i2 = 0; i2 < splitStr.length; i2++) {
							if (splitStr[i2].length() > 0) {
								allEmpty = false;
								s += splitStr[i2] + "\n";
							}
						}
						if (allEmpty) {
							s += "\n";
						}
					} else {
						s += lines[i];

					}
				}
			}
		}
		return s;
	}
}
