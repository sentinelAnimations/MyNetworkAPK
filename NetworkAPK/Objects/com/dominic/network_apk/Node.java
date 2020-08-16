package com.dominic.network_apk;

import processing.core.PApplet;
import processing.core.PFont;

public class Node {

	private int index, x, y, dragShiftX, dragShiftY, headY, bodyY, w, h, bodyH, headH, type, edgeRad, margin, stdTs, btnSizeSmall, dark, darkest, bgCol, textCol, textDark, lighter, lightest, border, doOnce = 0, anzTypes = 5, conS;
	private float textYShift;
	private Boolean isOnDrag = true, isTypePC = false, mouseIsPressed = false, isGrabbed = true, isSelected = false, isDeleted = false;
	private String[] pictoPaths;
	private PFont stdFont;
	private PApplet p;
	private MainActivity mainActivity;
	private PictogramImage type_picto, cpu_picto, gpu_picto;
	private Checkbox useCpu_checkbox, useGpu_checkbox;
	private DropdownMenu pcSelection_DropdownMenu;
	private CounterArea switchPort_CounterArea;
	private EditText switchName_editText;

	public Node(PApplet p, int index, int x, int y, int w, int h, int type, int edgeRad, int margin, int stdTs, int btnSizeSmall, int dark, int darkest, int bgCol, int textCol, int textDark, int lighter, int lightest, int border, float textYShift, String[] pictoPaths, PFont stdFont) {
		this.index = index;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.edgeRad = edgeRad;
		this.margin = margin;
		this.stdTs = stdTs;
		this.btnSizeSmall = btnSizeSmall;
		this.dark = dark;
		this.darkest = darkest;
		this.bgCol = bgCol;
		this.textCol = textCol;
		this.textDark = textDark;
		this.lighter = lighter;
		this.lightest = lightest;
		this.border = border;
		this.textYShift = textYShift;
		this.pictoPaths = pictoPaths;
		this.stdFont = stdFont;
		this.p = p;
		this.type = type;

		mainActivity = (MainActivity) p;
		conS = btnSizeSmall - margin;
		headH = btnSizeSmall + margin * 2;
		bodyH = h - headH - margin;
		calcBodyAndHeadPos();

	}

	public void render() {
		switch (type) {
		case 0:
			renderTypePC(); // Master pc
			isTypePC = true;
			break;

		case 1:
			renderTypePC(); // Pc
			isTypePC = true;
			break;

		case 2:
			renderTypePC(); // laptop
			isTypePC = true;
			break;

		case 3:
			renderTypeSwitch(); // switch
			break;

		case 4:
			renderTypeOutput(); // engine output
			break;
		}
	}

	private void renderTypePC() {
		if (doOnce == 0) {
			type_picto = new PictogramImage(p, w / 2 - btnSizeSmall / 2 - margin, headY - y, btnSizeSmall, margin, stdTs, edgeRad, textCol, textYShift, true, pictoPaths[type], "", this);
			cpu_picto = new PictogramImage(p, -btnSizeSmall / 2 - margin, bodyY - y - bodyH / 2 + margin + btnSizeSmall / 2, btnSizeSmall, margin, stdTs, edgeRad, textCol, textYShift, true, pictoPaths[anzTypes], "", this);
			gpu_picto = new PictogramImage(p, +btnSizeSmall + btnSizeSmall / 2 + margin * 2, bodyY - y - bodyH / 2 + margin + btnSizeSmall / 2, btnSizeSmall, margin, stdTs, edgeRad, textCol, textYShift, true, pictoPaths[anzTypes + 1], "", this);
			useCpu_checkbox = new Checkbox(p, -btnSizeSmall - btnSizeSmall / 2 - margin * 2, bodyY - y - bodyH / 2 + margin + btnSizeSmall / 2, btnSizeSmall, btnSizeSmall, btnSizeSmall - margin, edgeRad, margin, stdTs, lighter, lighter, border, textCol, textYShift, true, false, "", pictoPaths[anzTypes + 6], stdFont, this);
			useGpu_checkbox = new Checkbox(p, +btnSizeSmall / 2 + margin, bodyY - y - bodyH / 2 + margin + btnSizeSmall / 2, btnSizeSmall, btnSizeSmall, btnSizeSmall - margin, edgeRad, margin, stdTs, lighter, lighter, border, textCol, textYShift, true, false, "", pictoPaths[anzTypes + 6], stdFont, this);
			String[] tempList = { "dies", "und", "das", "kfdjakjfaskdjfasdkf", "askdfjasdkfjjjjjjjjj" };
			String[] ddPaths = { pictoPaths[anzTypes + 5], pictoPaths[anzTypes + 4] };
			pcSelection_DropdownMenu = new DropdownMenu(p, -btnSizeSmall / 2 - margin, headY - y, w - margin * 3 - btnSizeSmall, btnSizeSmall, h + btnSizeSmall + margin * 2, edgeRad, margin, stdTs, lighter, lightest, textCol, textDark, textYShift, "PC", ddPaths, tempList, stdFont, true, this);

			doOnce++;
		}

		if (mouseIsPressed) {
			if (isGrabbed == false) {
				if (isDragablePcNode()) {
					isGrabbed = true;
					dragShiftX = p.mouseX - x;
					dragShiftY = p.mouseY - y;
				}
			}

		}

		if (isGrabbed) {
			x = p.mouseX - dragShiftX;
			y = p.mouseY - dragShiftY;
			calcBodyAndHeadPos();
		}

		if (isSelected) {
			p.stroke(lightest);
		} else {
			p.stroke(darkest);
		}
		p.fill(bgCol);
		p.rect(x, bodyY, w, bodyH, 0, 0, edgeRad, edgeRad);
		p.rect(x, headY, w, headH, edgeRad, edgeRad, 0, 0);

		type_picto.render();
		gpu_picto.render();
		cpu_picto.render();
		useGpu_checkbox.render();
		useCpu_checkbox.render();
		p.fill(textCol);
		p.textFont(stdFont);
		p.textSize(stdTs);
		p.textAlign(p.LEFT, p.CENTER);
		p.text("CPU name", x - w / 2 + margin, useCpu_checkbox.getY() + useCpu_checkbox.getBoxDim() + margin - stdTs * textYShift);
		p.text("GPU name", x - w / 2 + margin, useCpu_checkbox.getY() + useCpu_checkbox.getBoxDim() + margin - stdTs * textYShift + stdTs);
		p.text("Status: ok", x - w / 2 + margin, useCpu_checkbox.getY() + useCpu_checkbox.getBoxDim() + margin - stdTs * textYShift + stdTs * 2);

		renderConnector(x + w / 2, bodyY, false);

		pcSelection_DropdownMenu.render();

	}

	private void renderTypeSwitch() {
		if (doOnce == 0) {
			type_picto = new PictogramImage(p, w / 2 - btnSizeSmall / 2 - margin, headY - y, btnSizeSmall, margin, stdTs, edgeRad, textCol, textYShift, true, pictoPaths[type], "", this);
			String[] pp = { pictoPaths[anzTypes + 2], pictoPaths[anzTypes + 3] };
			switchPort_CounterArea = new CounterArea(p, 0, bodyY - y - bodyH / 2 + margin + btnSizeSmall / 2, w - margin * 2, btnSizeSmall, edgeRad, margin, stdTs, 2, 48, 8, lighter, textCol, textCol, textYShift, true, "Port Count", pp, stdFont, this);
			char[] fChars = { '>', '<', ':', '"', '/', '\\', '|', '?', '*' };
			switchName_editText = new EditText(p, -btnSizeSmall / 2 - margin, headY - y, w - margin * 3 - btnSizeSmall, btnSizeSmall, stdTs, lighter, textCol, edgeRad, margin, textYShift, true, true, "Switch Name", fChars, stdFont, this);

			doOnce++;
		}

		if (mouseIsPressed) {
			if (isGrabbed == false) {
				if (isDragableSwitchNode()) {
					isGrabbed = true;
					dragShiftX = p.mouseX - x;
					dragShiftY = p.mouseY - y;
				}
			}
		}

		if (isGrabbed) {
			x = p.mouseX - dragShiftX;
			y = p.mouseY - dragShiftY;
			calcBodyAndHeadPos();
		}

		if (switchPort_CounterArea.getValueHasChanged()) {
			h = headH + margin + (switchPort_CounterArea.getH() + margin * 2 + (p.ceil(switchPort_CounterArea.getCount() / 2.0f) * conS) + (p.ceil(switchPort_CounterArea.getCount() / 2.0f) * margin));
			bodyH = h - headH - margin;
			p.println(p.ceil(switchPort_CounterArea.getCount() / 2.0f));
			calcBodyAndHeadPos();

			type_picto.setPos(w / 2 - btnSizeSmall / 2 - margin, headY - y);
			switchPort_CounterArea.setPos(0, bodyY - y - bodyH / 2 + margin + btnSizeSmall / 2);
			switchName_editText.setPos(-btnSizeSmall / 2 - margin, headY - y);

			switchPort_CounterArea.setValueHasChanged(false);
		}

		if (isSelected) {
			p.stroke(lightest);
		} else {
			p.stroke(darkest);
		}
		p.fill(bgCol);
		p.rect(x, bodyY, w, bodyH, 0, 0, edgeRad, edgeRad);
		p.rect(x, headY, w, headH, edgeRad, edgeRad, 0, 0);
		switchName_editText.render();
		type_picto.render();
		switchPort_CounterArea.render();

		
		Boolean isEven;
		int conBorder;
		if (switchPort_CounterArea.getCount() % 2 == 0) {
			conBorder = p.ceil(switchPort_CounterArea.getCount() / 2)-1;
			isEven=true;
		} else {
			conBorder=p.ceil(switchPort_CounterArea.getCount() / 2);
			isEven=false;
		}
		
		for (int i = 0; i < switchPort_CounterArea.getCount(); i++) {
			int xp, yp;
			
			
			if (i <= conBorder) {
				xp = x - w / 2-1;
				yp = conS / 2 + bodyY - bodyH / 2 + margin * 2 + switchPort_CounterArea.getH() + i * conS + i * margin;
				renderConnector(xp, yp, true);
				p.fill(textCol);
				p.textFont(stdFont);
				p.textSize(stdTs);
				p.textAlign(p.LEFT,p.CENTER);
				p.text("Port: "+(i+1),xp+conS/2+margin,yp-stdTs*textYShift);
			} else {
				int ind = i - p.ceil(switchPort_CounterArea.getCount() / 2);
				if(!isEven) {
					ind--;
				}
				xp = x + w / 2;
				yp = conS / 2 + bodyY - bodyH / 2 + margin * 2 + switchPort_CounterArea.getH() + ind * conS + ind * margin;
				renderConnector(xp, yp, false);
				p.fill(textCol);
				p.textFont(stdFont);
				p.textSize(stdTs);
				p.textAlign(p.RIGHT,p.CENTER);
				p.text("Port: "+(i+1),xp-conS/2-margin,yp-stdTs*textYShift);
			}
		}

	}

	private void renderTypeOutput() {

	}

	private void renderConnector(int conX, int conY, Boolean isLeft) {
		float a1, a2;
		if (isLeft) {
			a1 = 2 * p.PI - p.HALF_PI;
			a2 = 3 * p.PI - p.HALF_PI;
		} else {
			a1 = p.HALF_PI;
			a2 = 2 * p.PI - p.HALF_PI;
		}

		p.fill(dark);
		p.stroke(dark);
		p.arc(conX, conY, conS, conS, a1, a2, p.PIE);
		
		p.noFill();
		if (isSelected) {
			p.stroke(lightest);
		} else {
			p.stroke(darkest);
		}
		p.arc(conX, conY, conS, conS, a1, a2, p.OPEN);

		p.stroke(dark);
		p.fill(bgCol);
		p.ellipse(conX, conY, conS - margin, conS - margin);
	}

	private void calcBodyAndHeadPos() {
		bodyY = y + headH / 2 + margin / 2;
		headY = bodyY - bodyH / 2 - headH / 2 - margin;
	}

	private Boolean isDragablePcNode() {
		Boolean isD = true;

		for (int i = 0; i < mainActivity.getNodeEditor().getNodes().size(); i++) {
			Node n = (Node) mainActivity.getNodeEditor().getNodes().get(i);
			if (n.getIsGrabbed() && i != index) {
				isD = false;
				break;
			}
		}

		if (mouseIsInArea()) {
			if (useGpu_checkbox.mouseIsInArea()) {
				p.fill(255, 0, 0);
				isD = false;
			}
			if (useCpu_checkbox.mouseIsInArea()) {
				isD = false;
			}
			if (pcSelection_DropdownMenu.dropdown_btn.mouseIsInArea()) {
				isD = false;

			}
			if (pcSelection_DropdownMenu.getIsUnfolded()) {
				isD = false;

			}
		} else {
			isD = false;
		}
		return isD;
	}

	private Boolean isDragableSwitchNode() {
		Boolean isD = true;

		for (int i = 0; i < mainActivity.getNodeEditor().getNodes().size(); i++) {
			Node n = (Node) mainActivity.getNodeEditor().getNodes().get(i);
			if (n.getIsGrabbed() && i != index) {
				isD = false;
				break;
			}
		}

		if (mouseIsInArea()) {
			if (switchPort_CounterArea.mouseIsInArea()) {
				isD = false;
			}
			if (switchName_editText.mouseIsInArea()) {
				isD = false;
			}
		} else {
			isD = false;
		}
		return isD;
	}

	public void onMousePressed(int mouseButton) {
		if (mouseButton == p.LEFT) {
			mouseIsPressed = true;

			if (isTypePC) {
				pcSelection_DropdownMenu.dropdown_btn.onMousePressed();
			}

			if (type == 3) {
				switchPort_CounterArea.onMousePressed();
			}
		}
	}

	public void onMouseReleased(int mouseButton) {
		if (mouseButton == p.LEFT) {
			if (isGrabbed) {
				isGrabbed = false;
			}
			if (mouseIsInArea() == false) {
				isSelected = false;
			}
			if (isTypePC) {
				if (pcSelection_DropdownMenu.getIsUnfolded() == false) {
					useGpu_checkbox.onMouseReleased();
					useCpu_checkbox.onMouseReleased();
				}
				pcSelection_DropdownMenu.dropdown_btn.onMouseReleased();
				pcSelection_DropdownMenu.onMouseReleased();
			}

			if (type == 3) {
				switchPort_CounterArea.onMouseReleased();
				switchName_editText.onMouseReleased();
			}

			mouseIsPressed = false;

		}

		if (mouseButton == p.RIGHT) {
			if (mouseIsInArea()) {
				isSelected = !isSelected;
			}
		} else {
			isSelected = false;
		}

	}

	public void onKeyReleased(char k) {
		if (k == p.DELETE) {
			if (isSelected) {
				isDeleted = true;
			}
		}
		if (type == 3) {
			switchName_editText.onKeyReleased(k);
		}
	}

	public void onScroll(float e) {
		if (isTypePC) {
			pcSelection_DropdownMenu.onScroll(e);
		}

		if (type == 3) {
			switchPort_CounterArea.onScroll(e);
		}
	}

	public int getX() {
		return x;
	}

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

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public Boolean getIsGrabbed() {
		return isGrabbed;
	}

}