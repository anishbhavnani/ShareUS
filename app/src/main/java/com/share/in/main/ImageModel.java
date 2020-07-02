package com.share.in.main;

import java.util.Set;

public class ImageModel {

    String image;
    String title;
    int resImg;
    boolean isSelected;
    private int position;
    String path;
    static Set<String> filePath;
    public static Set<String> getFilePath() {
        return filePath;
    }

    public static void setFilePath(Set<String> filePath) {
        FileModel.filePath = filePath;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
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