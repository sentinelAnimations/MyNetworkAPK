package com.dominic.network_apk;

import java.lang.reflect.Method;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;

public class HorizontalList<T> implements Widgets {
    private int x = 0, y = 0, xShift, yShift, w, h, margin, edgeRad, stdTs, titleBoxWidth, btnSize, btnSizeSmall, dark, light, lighter, textCol, textDark, border, shiftPerClick = 20, lastDisplayedInd, firstDisplayedInd, selectedInd = 0, markedInd = 0, onceOnStartup = 0;
    private float textYShift, startListX, endListX, shiftListX = 0;
    private float[] listX, listW;
    private char splitChar;
    private Boolean isParented, showSelected, showMarked;
    public Boolean isNewSelected = false, isNewMarked = true, isShifted = true;
    private String title;
    private String[] list, displayList;
    private String[] pictoPaths;
    private PFont stdFont;
    private PApplet p;
    private T parent;
    private PictogramImage picto;
    public ImageButton goLeft_btn, goRight_btn;

    public HorizontalList(PApplet p, int x, int y, int w, int h, int margin, int edgeRad, int stdTs, int titleBoxWidth, int btnSize, int btnSizeSmall, int dark, int light, int lighter, int textCol, int textDark, int border, float textYShift, char splitChar, Boolean isParented, Boolean showSelected, Boolean showMarked, String title, String[] pictoPaths, String[] list, PFont stdFont, T parent) {
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
        this.border = border;
        this.textYShift = textYShift;
        this.splitChar = splitChar;
        this.showSelected = showSelected;
        this.showMarked = showMarked;
        this.isParented = isParented;
        this.title = title;
        this.pictoPaths = pictoPaths;
        this.list = list;
        this.stdFont = stdFont;
        this.parent = parent;

        displayList = list;
        xShift = x;
        yShift = y;
        listX = new float[list.length];
        listW = new float[list.length];

        picto = new PictogramImage(p, -w / 2 + margin * 2 + btnSizeSmall / 2, 0, btnSizeSmall - margin, btnSizeSmall - margin, margin, stdTs, edgeRad, textCol, textYShift, true, false, pictoPaths[0], "", this);
        goLeft_btn = new ImageButton(p, -w / 2 + margin * 2 + titleBoxWidth + btnSizeSmall / 2, 0, btnSizeSmall, h - margin * 2, stdTs, margin, edgeRad, -1, textYShift, true, true, textCol, lighter, pictoPaths[1], "", this);
        goRight_btn = new ImageButton(p, w / 2 - margin - btnSizeSmall / 2, 0, btnSizeSmall, h - margin * 2, stdTs, margin, edgeRad, -1, textYShift, true, true, textCol, lighter, pictoPaths[2], "", this);

        goLeft_btn.getParentPos();
        goRight_btn.getParentPos();
        startListX = goLeft_btn.getX() + goLeft_btn.getW() / 2.0f + margin;
        endListX = goRight_btn.getX() - goRight_btn.getW() / 2 - margin;
    }

    public void render() {
        if (isParented) {
            getParentPos();
            if (onceOnStartup == 0) {
                goLeft_btn.getParentPos();
                goRight_btn.getParentPos();
                startListX = goLeft_btn.getX() + goLeft_btn.getW() / 2.0f + margin;
                endListX = goRight_btn.getX() - goRight_btn.getW() / 2 - margin;
                onceOnStartup++;
            }
        }
        
        if (list.length > 0) {
            if (goLeft_btn.getIsClicked() == true) {
                if (shiftListX < 0) {
                    shiftListX += shiftPerClick;
                    isShifted = true;
                }
                goLeft_btn.setIsClicked(false);
            }

            if (goRight_btn.getIsClicked() == true) {

                if (lastDisplayedInd < list.length - 1) {
                    shiftListX -= shiftPerClick;
                    isShifted = true;
                }
                goRight_btn.setIsClicked(false);
            }
        }

        p.fill(light);
        p.stroke(light);
        p.rect(x, y, w, h, edgeRad);
        p.fill(lighter);
        p.rect(x - w / 2 + margin + titleBoxWidth / 2, y, titleBoxWidth, h - margin * 2, edgeRad);
        picto.render();
        p.stroke(light);
        p.fill(textCol);
        p.textAlign(PConstants.LEFT, PConstants.CENTER);
        p.textFont(stdFont);
        p.textSize(stdTs);
        p.text(title, x - w / 2 + 3 * margin + btnSizeSmall, y - textYShift);

        if (displayList.length > 0) {
            p.textAlign(PConstants.CENTER, PConstants.CENTER);
            float xPos = startListX + shiftListX;
            lastDisplayedInd = listX.length - 1;
            firstDisplayedInd = 0;
            for (int i = 0; i < displayList.length; i++) {
                xPos += p.textWidth(displayList[i]) / 2 + margin;
                if (i > 0) {
                    xPos += p.textWidth(displayList[i - 1]) / 2 + margin * 2;
                }
                listX[i] = xPos;
                listW[i] = p.textWidth(displayList[i]) + margin * 2;
                if (xPos - listW[i] / 2 + p.textWidth(displayList[i]) + margin < endListX) {
                    p.stroke(light);
                    if (showSelected) {
                        if (selectedInd == i) {
                            p.stroke(textCol);
                        }
                    } else {
                        if (showMarked) {
                            if (markedInd == i) {
                                p.stroke(border);
                            }
                        }
                    }

                    if (xPos - p.textWidth(displayList[i]) / 2 - margin >= startListX) {
                        p.fill(lighter);
                        p.rect(xPos, y, p.textWidth(displayList[i]) + margin * 2, h - margin * 2, edgeRad);
                        p.fill(textCol);
                        if (p.textWidth(displayList[i]) < endListX - startListX) {
                            p.text(displayList[i], xPos, y - textYShift);
                        } else {
                            String[] splitStr = PApplet.split(displayList[i], splitChar);
                            p.text(splitStr[splitStr.length - 1], xPos, y - textYShift);

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
                // p.noFill();
                p.rect(lastX + (endListX - lastX) / 2, y, endListX - lastX, h - margin * 2, edgeRad);
            }

        } else {
            p.fill(lighter);
            p.rect(startListX + (endListX - startListX) / 2, y, endListX - startListX, h - margin * 2, edgeRad);
        }

        if (firstDisplayedInd != 0) {
            if (firstDisplayedInd > listX.length - 1) {
                firstDisplayedInd = 1;
            }
            if (listX[firstDisplayedInd] - startListX - p.textWidth(displayList[firstDisplayedInd]) / 2 - margin * 2 > 0) {
                p.fill(lighter);
                p.rect(startListX + ((listX[firstDisplayedInd] - startListX - p.textWidth(displayList[firstDisplayedInd]) / 2 - margin * 2) / 2), y, listX[firstDisplayedInd] - startListX - p.textWidth(displayList[firstDisplayedInd]) / 2 - margin * 2, h - margin * 2, edgeRad);
            }
        }

        goLeft_btn.render();
        goRight_btn.render();

    }

    public void onMousePressed() {
        goLeft_btn.onMousePressed();
        goRight_btn.onMousePressed();
    }

    public void onMouseReleased(int mouseButton) {
        if (mouseButton == p.RIGHT) {
            if (listX.length > 0) {
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
        }
        if (mouseButton == p.LEFT) {
            if (listX.length > 0) {
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
                goLeft_btn.onMouseReleased();
                goRight_btn.onMouseReleased();
            }

        }

        goLeft_btn.onMouseReleased();
        goRight_btn.onMouseReleased();

    }

    public void onScroll(float e) {
        if (p.mouseX > x - w / 2 && p.mouseX < x + w / 2 && p.mouseY > y - h / 2 && p.mouseY < y + h / 2) {
            if (e > 0) {
                if (lastDisplayedInd < displayList.length - 1) {
                    shiftListX -= shiftPerClick / 2;
                    isShifted = true;
                }
            } else {
                if (shiftListX < 0) {
                    shiftListX += shiftPerClick / 2;
                    isShifted = true;
                }
            }
        }

    }

    public void setList(String[] l) {
        try {
            if (l != null) {
                list = new String[l.length];
                displayList = new String[l.length];

                list = l;
                listX = new float[list.length];
                listW = new float[list.length];

                isNewSelected = false;
                if (selectedInd > l.length - 1) {
                    selectedInd = 0;
                }
                if (markedInd > l.length - 1) {
                    markedInd = 0;
                }
                for (int i = 0; i < l.length; i++) {
                    if (p.textWidth(l[i]) < (endListX - startListX) / 3) {
                        displayList[i] = l[i];
                    } else {
                        String elem = "";
                        String[] splitStr = PApplet.split(l[i], splitChar);
                        for (int i2 = splitStr.length - 1; i2 >= 0; i2--) {
                            if (p.textWidth(elem + splitStr[i2] + "\\") < (endListX - startListX) / 3) {
                                elem = splitStr[i2] + "\\" + elem;
                            }
                        }
                        displayList[i] = elem;
                    }
                }
                firstDisplayedInd = 0;
                shiftListX = 0;
                isShifted = true;

            } else {
                list = new String[0];
                displayList = new String[0];
            }

        } catch (Exception e) {
            e.printStackTrace();
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

    @Override
    public Boolean mouseIsInArea() {
        if (p.mouseX > x - w / 2 && p.mouseX < x + w / 2 && p.mouseY > y - h / 2 && p.mouseY < y + h / 2) {
            return true;
        } else {
            return false;
        }
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public int getFirstDisplayedInd() {
        return firstDisplayedInd;
    }

    public int getLastDisplayedInd() {
        return lastDisplayedInd;
    }

    public Boolean getIsShifted() {
        return isShifted;
    }

    public String getSelectedItem() {
        return list[selectedInd];
    }

    public float[] getListX() {
        return listX;
    }

    public float[] getListW() {
        return listW;
    }

    public void setSelectedInd(int setInd) {
        selectedInd = setInd;
    }

    public void setIsShifted(Boolean setIsShifted) {
        isShifted = setIsShifted;
    }

}
