package com.customapi.fmkpl.bean;

public class ScreenProperty {
    private int screenWidthDp;
    private int screenHeightDp;
    private int screenHeightPx;
    private int screenWidthPx;
    private float density;
    public ScreenProperty(){

    }
    public ScreenProperty(int screenWidthDp, int screenHeightDp, int screenWidthPx, int screenHeightPx){
        this.screenWidthDp = screenWidthDp;
        this.screenHeightDp = screenHeightDp;
        this.screenWidthPx = screenWidthPx;
        this.screenHeightPx = screenHeightPx;
    }
    public ScreenProperty(int screenWidthPx, int screenHeightPx){
        this.screenHeightPx = screenHeightPx;
        this.screenWidthPx = screenWidthPx;
    }

    public int getScreenHeightPx() {
        return screenHeightPx;
    }

    public int getScreenWidthPx() {
        return screenWidthPx;
    }

    public void setScreenHeightPx(int screenHeightPx) {
        this.screenHeightPx = screenHeightPx;
    }

    public void setScreenWidthPx(int screenWidthPx) {
        this.screenWidthPx = screenWidthPx;
    }

    public void setScreenHeightDp(int screenHeightDp) {
        this.screenHeightDp = screenHeightDp;
    }

    public void setScreenWidthDp(int screenWidthDp) {
        this.screenWidthDp = screenWidthDp;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public int getScreenHeightDp() {
        return screenHeightDp;
    }

    public int getScreenWidthDp() {
        return screenWidthDp;
    }

    public float getDensity() {
        return density;
    }
}
