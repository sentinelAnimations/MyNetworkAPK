package com.dominic.network_apk;

import processing.core.PApplet;
import processing.core.PFont;

public class RenderOnSheepitScreen {

	private int stdTs, edgeRad, margin, btnSizeLarge, btnSize, btnSizeSmall, dark, light, lighter, lightest, textCol, textDark, border,onceOnStartup=0;
	private float textYShift;
	private Boolean isRendering = true;
	private String[] pictoPaths;
	private PFont stdFont;
	private PApplet p;
	private MainActivity mainActivity;
	private PictogramImage sheepitRendering_PictogramImage, sheepitSleeping_PictogramImage;
	private SpriteAnimation loadingGear_SpriteAnimation;
	private TimeField timeField;

	public RenderOnSheepitScreen(PApplet p, int stdTs, int edgeRad, int margin, int btnSizeLarge, int btnSize, int btnSizeSmall, int dark, int light, int lighter, int textCol, int textDark, int border, float textYShift, String[] pictoPaths, PFont stdFont) {
		this.p = p;
		this.stdTs = stdTs;
		this.edgeRad = edgeRad;
		this.margin = margin;
		this.btnSizeLarge = btnSizeLarge;
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
		this.pictoPaths = pictoPaths;
		this.stdFont = stdFont;
		mainActivity = (MainActivity) p;

		sheepitRendering_PictogramImage = new PictogramImage(p, p.width / 2, p.height / 2, btnSizeLarge,btnSizeLarge, margin, stdTs, edgeRad, textCol, textYShift, false,false, pictoPaths[0], "Rendering on Sheepit", null);
		sheepitSleeping_PictogramImage = new PictogramImage(p, p.width / 2, p.height / 2, btnSizeLarge,btnSizeLarge, margin, stdTs, edgeRad, textCol, textYShift, false,false, pictoPaths[1], "Sheepit sleeping", null);
	}

	public void render() {
	    if(onceOnStartup==0) {
	        timeField = new TimeField(p, margin, mainActivity.getRenderOverview().getCancelImageButton().getY(),btnSizeLarge*3,stdTs+margin*2, stdTs, margin, edgeRad, textCol, light, false, false,true,"Timestamp: ","", stdFont, null);
	        timeField.setPos(timeField.getW()/2+margin, timeField.getY());
	        onceOnStartup++;
	    }
	    
		if (isRendering) {
			sheepitRendering_PictogramImage.render();
		} else {
			sheepitSleeping_PictogramImage.render();
		}
		timeField.render();
	}

	public void onMousePressed(int mouseButton) {
	}

	public void onMouseReleased(int mouseButton) {
	}

	public void onKeyPressed(char key) {

	}

	public void onKeyReleased(char key) {

	}

	public void onScroll(float e) {
	}

}
