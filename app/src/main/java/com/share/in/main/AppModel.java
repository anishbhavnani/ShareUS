package com.share.in.main;

import android.graphics.drawable.Drawable;

public class AppModel {

    Drawable image;
    String title;
    int resImg;
    boolean isSelected;
    private int position;
    String path;
    String appSize;

    public String getAppSize() {
        return appSize;
    }

    public void setAppSize(String appSize) {
        this.appSize = appSize;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getResImg() {
        return resImg;
    }

    public void setResImg(int resImg) {
        this.resImg = resImg;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }


}