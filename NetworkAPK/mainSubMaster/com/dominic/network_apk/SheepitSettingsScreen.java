package com.dominic.network_apk;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import processing.core.PApplet;
import processing.core.PFont;

public class SheepitSettingsScreen {
	private int stdTs, edgeRad, margin, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border, onceOnStartup = 0;
	private float textYShift;
	private String[] pictoPaths;
	private PFont stdFont;
	private PApplet p;
	private MainActivity mainActivity;
	private EditText userName_EditText, passwort_EditText;
	private ImageButton start_ImageButton;
	private JsonHelper jsonHelper;

	public SheepitSettingsScreen(PApplet p, int stdTs, int edgeRad, int margin, int btnSize, int btnSizeSmall, int dark, int light, int lighter, int textCol, int textDark, int border, float textYShift, String[] pictoPaths, PFont stdFont) {
		this.p = p;
		this.stdTs = stdTs;
		this.edgeRad = edgeRad;
		this.margin = margin;
		this.btnSize = btnSize;
		this.btnSizeSmall = btnSizeSmall;
		this.dark = dark;
		this.light = light;
		this.lighter = lighter;
		this.textCol = textCol;
		this.textDark = textDark;
		this.border = border;
		this.textYShift = textYShift;
		this.pictoPaths = pictoPaths;
		this.stdFont = stdFont;
		mainActivity = (MainActivity) p;

		jsonHelper = new JsonHelper(p);

		char[] fChars = { '>', '<', ':', '"', '/', '\\', '|', '?', '*' };
		userName_EditText = new EditText(p, p.width / 2, p.height / 2 - btnSizeSmall / 2 - margin, p.width / 3, btnSizeSmall, stdTs, light, textCol, edgeRad, margin, textYShift, true, false, "Username", fChars, stdFont, null);
		passwort_EditText = new EditText(p, p.width / 2, p.height / 2 + btnSizeSmall / 2 + margin, p.width / 3, btnSizeSmall, stdTs, light, textCol, edgeRad, margin, textYShift, true, false, "Pasword", fChars, stdFont, null);
		// start_ImageButton=new ImageButton(p, p.width - margin - btnSizeSmall / 2,
		// p.height - margin - btnSizeSmall / 2, btnSizeSmall, btnSizeSmall, stdTs,
		// margin, edgeRad, -1, textYShift, true, false, textCol, light, pictoPaths[0],
		// "Start rendering on SheepIt", null);
	}

	void render() {
		if (onceOnStartup == 0) {
			start_ImageButton = new ImageButton(p, -margin - btnSizeSmall, 0, btnSizeSmall, btnSizeSmall, stdTs, margin, edgeRad, -1, textYShift, true, true, textCol, light, pictoPaths[0], "Save settings and start render process", mainActivity.getRenderOverview().getCancelImageButton());
			onceOnStartup++;
		}

		p.fill(textCol);
		p.textAlign(p.LEFT, p.BOTTOM);
		p.textFont(stdFont);
		p.textSize(stdTs);
		p.text("Sheepit Login", userName_EditText.getX() - userName_EditText.getW() / 2, userName_EditText.getY() - userName_EditText.getH() / 2 - margin);

		userName_EditText.render();
		passwort_EditText.render();
		start_ImageButton.render();

		if (start_ImageButton.getIsClicked()) {
			if (saveSheepitSettings()) {
				mainActivity.getRenderOverview().getRenderOnSheepitScreen().setupAll();
				mainActivity.getRenderOverview().setRenderMode(1);
				start_ImageButton.setIsClicked(false);
			}
		}
	}

	private Boolean saveSheepitSettings() {

		Boolean saved = false, savable = true;
		JSONArray hardwareToUseArray = new JSONArray();
		JSONObject sheepitSettings = new JSONObject();
		if (userName_EditText.getStrList().get(0).length() > 0) {
			sheepitSettings.put("username", userName_EditText.getStrList().get(0));
		} else {
			savable = false;
		}
		if (passwort_EditText.getStrList().get(0).length() > 0) {
			sheepitSettings.put("password", passwort_EditText.getStrList().get(0));
		} else {
			savable = false;
		}
		if (savable) {
			hardwareToUseArray.add(sheepitSettings);

			jsonHelper.clearArray();
			jsonHelper.setArray(hardwareToUseArray);
			saved = jsonHelper.writeData(mainActivity.getSheepitSettingsPath());
			p.println("now saved");
		}
		return saved;
	}

	public void onMousePressed(int mouseButton) {
		userName_EditText.onMousePressed();
		passwort_EditText.onMousePressed();
		start_ImageButton.onMousePressed();
	}

	public void onMouseReleased(int mouseButton) {
		userName_EditText.onMouseReleased();
		passwort_EditText.onMouseReleased();
		start_ImageButton.onMouseReleased();
	}

	public void onKeyPressed(char key) {
		userName_EditText.onKeyPressed(key);
		passwort_EditText.onKeyPressed(key);
	}

	public void onKeyReleased(char key) {
		userName_EditText.onKeyReleased(key);
		passwort_EditText.onKeyReleased(key);
	}
}
