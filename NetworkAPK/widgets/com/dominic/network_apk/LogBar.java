package com.dominic.network_apk;

import java.lang.reflect.Method;

import processing.core.PApplet;
import processing.core.PFont;

public class LogBar<T> implements Widgets{
    private int x,y,xShift,yShift,w,h,stdTs, edgeRad, margin, dark, light, lighter, lightest, textCol, textDark, border;
    private Boolean isParented;
    private String logText="";
    private float textYShift;
    private PFont stdFont;
    private PApplet p;
    private HoverText hoverText;
    private PictogramImage picto;
    private T parent;
    

    public LogBar(PApplet p,int x,int y,int w,int h, int stdTs, int edgeRad, int margin,int btnSizeSmall, int dark, int light, int lighter, int textCol, int textDark, int border,Boolean isParented, float textYShift, String pictoPath, PFont stdFont,T parent) {
        this.p = p;
        this.x=x;
        this.y=y;
        this.w=w;
        this.h=h;
        this.stdTs = stdTs;
        this.edgeRad = edgeRad;
        this.margin = margin;
        this.dark = dark;
        this.light = light;
        this.lighter = lighter;
        this.lightest = lightest;
        this.textCol = textCol;
        this.textDark = textDark;
        this.border = border;
        this.isParented=isParented;
        this.textYShift = textYShift;
        this.stdFont = stdFont;
        this.parent=parent;
        xShift=x;
        yShift=y;
        hoverText = new HoverText(p, stdTs, margin, edgeRad, 150, textCol, textYShift, "","getX","getY","getW","getH", stdFont, this);
        picto = new PictogramImage(p, -w/2+btnSizeSmall/2+margin,0, btnSizeSmall,btnSizeSmall, margin, stdTs, edgeRad, textCol, textYShift, true, false,pictoPath, "", this);
    }
    
    public void render() {
        if(isParented) {
            getParentPos();
        }
        
        p.fill(light);
        p.stroke(light);
        p.rect(x,y,w,h,edgeRad);
        
        p.textAlign(p.LEFT,p.CENTER);
        p.textFont(stdFont);
        p.textSize(stdTs);
        p.fill(textCol);
        p.text(logText, picto.getX()+picto.getW()/2+margin, y);
        
        picto.render();
        hoverText.render();
        
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
    
    public void setText(String setText) {
        String prefix="Render log: ";
        logText=prefix+setText;
        if(p.textWidth(logText)>w-picto.getW()-margin*2) {
            hoverText.setInfoText(logText);
            logText=prefix;
            for(int i=0;i<setText.length();i++) {
                if(p.textWidth(logText+setText.charAt(i)+"...")>w-picto.getW()-margin*2) {
                    logText+=setText.charAt(i);
                }else {
                    logText+="...";
                    break;
                }
            }
        }else {
            hoverText.setInfoText("");
        }
    }
    
}
