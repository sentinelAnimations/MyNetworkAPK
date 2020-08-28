package com.dominic.network_apk;

import java.lang.reflect.Method;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.data.StringList;

public class DropdownMenu<T> implements Widgets {
	private int x, y, xShift, yShift, w, h, colH, maxH, dropdownH, dropdownY, edgeRad, margin, stdTs, light, lighter, textCol, textDark, selectedInd, unfoldTime = 5, elapsedUnfoldTime = 0, curDropdownH, calcOnce = 0, shiftListInd,maxDisplayableItems=0,hoverTime=72;
	private float textYShift;
	private float[] itemsY;
	private Boolean isParented, unfold = false, isUnfolded = false, isSelected = false,isHovering=false;
	private String title;
	private String[] list, displList,pictoPaths;
	private PFont stdFont;
	private PApplet p;
	private T parent;

	public ImageButton dropdown_btn;

	public DropdownMenu(PApplet p, int x, int y, int w, int h, int maxH, int edgeRad, int margin, int stdTs, int light, int lighter, int textCol, int textDark, float textYShift, String title, String[] pictoPaths, String[] list, PFont stdFont, Boolean isParented, T parent) {
		this.p = p;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.maxH = maxH;
		this.edgeRad = edgeRad;
		this.margin = margin;
		this.stdTs = stdTs;
		this.textYShift = textYShift;
		this.light = light;
		this.lighter = lighter;
		this.textCol = textCol;
		this.textDark = textDark;
		this.title = title;
		this.pictoPaths = pictoPaths;
		this.list = list;
		this.stdFont = stdFont;
		this.isParented = isParented;
		this.parent = parent;

		xShift = x;
		yShift = y;
		colH = h;
		itemsY = new float[list.length];
		for (int i = 0; i < itemsY.length; i++) {
			itemsY[i] = y;
		}

		dropdown_btn = new ImageButton(p, x + w / 2 - margin - (h - margin * 2) / 2, yShift, h - margin * 2, h - margin * 2, stdTs, margin, edgeRad, -1, textYShift, false, true, textCol, textCol, pictoPaths[0], "", parent);

		calcDropdownDimens();
		calcDisplList(list);
	}

	public void render() {
		if (isParented) {
			getParentPos();
		}
		
		p.stroke(light);
		p.fill(light);
		p.rect(x, y, w, h, edgeRad);

		p.textAlign(p.LEFT, p.CENTER);
		p.textFont(stdFont);
		p.textSize(stdTs);
		if (isSelected) {
			p.fill(textCol);
			p.text(displList[selectedInd], x - w / 2 + margin, y - stdTs * textYShift);
		} else {
			p.fill(textDark);
			p.text(title, x - w / 2 + margin, y - stdTs * textYShift);
		}
		dropdown_btn.render();
		onHover();
		dropdown();

		if (dropdown_btn.getIsClicked() == true) {
			unfold = !unfold;
			if (unfold == true) {
				dropdown_btn.setPicto(pictoPaths[1]);
			} else {
				dropdown_btn.setPicto(pictoPaths[0]);
			}
			dropdown_btn.setIsClicked(false);
		}
	}

	private void dropdown() {
		if (unfold) {
			if (elapsedUnfoldTime < unfoldTime) {
				elapsedUnfoldTime++;
				curDropdownH += dropdownH / unfoldTime;
				p.stroke(light);
				p.fill(light);
				p.rect(x, y + h / 2 + margin + curDropdownH / 2, w, curDropdownH, edgeRad);
			} else {
				isUnfolded = true;
			}
			if (isUnfolded) {
				p.stroke(light);
				p.fill(light);
				p.rect(x, dropdownY, w, dropdownH, edgeRad);
				elapsedUnfoldTime += 255 / unfoldTime;
				
				for (int i = 0; i < list.length; i++) {
					itemsY[i] = y + h / 2 + margin * 2 + colH / 2 + (i-shiftListInd) * colH + (i -shiftListInd)* margin;
					if (i>=shiftListInd&&i<shiftListInd+maxDisplayableItems) {
						p.fill(lighter, elapsedUnfoldTime - unfoldTime);
						p.rect(x, itemsY[i], w - margin * 2, colH, edgeRad);
						p.textAlign(p.CENTER, p.CENTER);
						p.textFont(stdFont);
						p.textSize(stdTs);
						p.fill(textCol, elapsedUnfoldTime - unfoldTime);
						p.text(displList[i], x, itemsY[i] - stdTs * textYShift);
					}else {
						if(i==shiftListInd+maxDisplayableItems) {
							float lastRectH,lastRectY;
							lastRectH=(dropdownY+dropdownH/2)-(itemsY[i-1]+colH/2+margin*2);
							if(lastRectH>margin) {
							lastRectY=itemsY[i-1]+colH/2+margin+lastRectH/2;
							p.fill(lighter,elapsedUnfoldTime - unfoldTime);
							p.rect(x,lastRectY,w-margin*2,lastRectH,edgeRad);
							p.fill(textDark);
							p.text("---",x,lastRectY-stdTs*textYShift);
							}
						}
					}
				}
			}
		} else {
			curDropdownH = 0;
			elapsedUnfoldTime = 0;
			isUnfolded = false;
		}
	}


	public void onMouseReleased() {
		if (p.mouseX > x - w / 2 && p.mouseX < x + w / 2 && p.mouseY > dropdownY - dropdownH / 2 && p.mouseY < dropdownY + dropdownH / 2) {
			if(isUnfolded) {
			for(int i=shiftListInd;i<shiftListInd+maxDisplayableItems;i++) {
				if(p.mouseX>x-w/2+margin && p.mouseX<x+w/2-margin && p.mouseY>itemsY[i]-colH/2 && p.mouseY<itemsY[i]+colH/2) {
					isSelected=true;
					selectedInd=i;
					curDropdownH = 0;
					elapsedUnfoldTime = 0;
					isUnfolded = false;
					unfold=false;
					dropdown_btn.setPicto(pictoPaths[0]);
				}
			}
			}
		}else {
			if(p.mouseX<dropdown_btn.getX()-dropdown_btn.getH()/2 && p.mouseX<dropdown_btn.getX()+dropdown_btn.getH()/2 && p.mouseY<dropdown_btn.getY()-dropdown_btn.getH()/2 && p.mouseY>dropdown_btn.getX()+dropdown_btn.getH()/2) {
			curDropdownH = 0;
			elapsedUnfoldTime = 0;
			isUnfolded = false;
			unfold=false;
			dropdown_btn.setPicto(pictoPaths[0]);
			}
		}
	}

	public void onScroll(float e) {
		if (p.mouseX > x - w / 2 && p.mouseX < x + w / 2 && p.mouseY > dropdownY - dropdownH / 2 && p.mouseY < dropdownY + dropdownH / 2) {
			if (e > 0) {
				if (shiftListInd < list.length-maxDisplayableItems) {
					shiftListInd++;
				}
			} else {
				if (shiftListInd > 0) {
					shiftListInd--;
				}
			}
		}
	}

	
	private void onHover() {
		Boolean show = false;
		if (isSelected==true && list[selectedInd].equals(displList[selectedInd])==false) {
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
				int tw = (int) p.textWidth(list[selectedInd]) + margin * 2;
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
				
				if (hoverTime > 120) {
					show = false;
				} else {
					show = true;
				}
			if(show) {	
				p.fill(0, 200);
				p.noStroke();
				p.rect(mx + tw / 2, my + stdTs, PApplet.abs(tw) + margin * 2, stdTs * 2, edgeRad);
				p.fill(textCol);
				p.text(list[selectedInd], mx + tw, my + stdTs - stdTs * textYShift);
			}
			}
		}
	}
	
	private void calcDropdownDimens() {
		dropdownH = (y + maxH) - (y + h / 2 + margin);
		if (dropdownH > list.length * colH + (list.length + 1) * margin) {
			dropdownH = list.length * colH + (list.length + 1) * margin;
		}
		dropdownY = y + h / 2 + margin + dropdownH / 2;
		
		maxDisplayableItems=(dropdownH-margin)/(colH+margin);
	}
	
	private void calcDisplList(String[] l){
		displList=new String[l.length];
		for(int i=0;i<l.length;i++) {
			
			int maxW=(int)(p.abs(dropdown_btn.getX()-(x-w/2)))-margin*2-dropdown_btn.getW()/2;
			
			if(p.textWidth(l[i])>maxW) {
				String s="";
				for(int i2=l[i].length()-1;i2>=0;i2--) {
				if(p.textWidth(s+l[i].charAt(i2))<maxW) {
					s=l[i].charAt(i2)+s;
				}
				}
				displList[i]=s;
			}else {
				displList[i]=l[i];
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
			calcDropdownDimens();

			/*if (calcOnce == 0) {
				calcOnce++;
			}*/

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
		if(p.mouseX > x - w / 2 && p.mouseX < x + w / 2 && p.mouseY > y - h / 2 && p.mouseY < y + h / 2) {
			return true;
		}else {
			return false;
		}
	}
	public int getSelectedInd() {
		return selectedInd;
	}
	
	public String[] getList() {
		return list;
	}
	public Boolean getIsSelected() {
		return isSelected;
	}
	
	public void setIsSelected(int selInd) {
		 isSelected=true;
		 selectedInd=selInd;
	}
	
	public Boolean getIsUnfolded() {
		return isUnfolded;
	}
	
	public String getSelectedItem() {
		return list[selectedInd];
	}

	public void setList(String[] l) {
		list = l;
		
		calcDisplList(l);
		
		itemsY = new float[list.length];
		calcDropdownDimens();
	}

}
