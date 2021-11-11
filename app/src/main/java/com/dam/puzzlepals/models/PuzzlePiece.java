package com.dam.puzzlepals.models;

import android.graphics.Bitmap;

public class PuzzlePiece {

    private int postion;
    private int posY;
    private int posX;
    private Bitmap imagePiece;

    public PuzzlePiece(int postion, int posY, int posX) {
        this.postion = postion;
        this.posY = posY;
        this.posX = posX;
    }

    public PuzzlePiece(Bitmap bitmap) {
        this.imagePiece = bitmap;
    }

    public Bitmap getImagePiece() {
        return imagePiece;
    }

    public void setImagePiece(Bitmap imagePiece) {
        this.imagePiece = imagePiece;
    }

}
