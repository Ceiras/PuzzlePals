package com.dam.puzzlepals.models;

import android.graphics.Bitmap;

public class PuzzlePiece {

    private Bitmap bitmap;
    private int posX;
    private int posY;
    private int width;
    private int height;

    public PuzzlePiece(Bitmap bitmap, int posX, int posY, int width, int height) {
        this.bitmap = bitmap;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
