package com.share.in.main;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.Set;

public class FileModel {

    String path;
    String title;
    int resImg;
    boolean isSelected;
    private int position;
    String parentDir;
    String fileType;
    Drawable image;
    String size;

    public static Set<String> getFilePath() {
        return filePath;
    }

    public static void setFilePath(Set<String> filePath) {
        FileModel.filePath = filePath;
    }

    FileModel(){

    }
    static Set<String> filePath;
    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getParentDir() {
        return parentDir;
    }

    public void setParentDir(String parentDir) {
        this.parentDir = parentDir;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    boolean isDirectory;



    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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