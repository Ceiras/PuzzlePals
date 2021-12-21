package com.dam.puzzlepals.models;

import android.graphics.Bitmap;

import com.dam.puzzlepals.enums.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Puzzle {

    private Bitmap image;
    private int number;
    private Level level;
    private List<PuzzlePiece> puzzlePieces;
    private List<PuzzlePiece> shuffledPuzzlePieces;
    private Long startGame;
    private Long endGame;

    public Puzzle() {
        this.puzzlePieces = new ArrayList<>();
        this.shuffledPuzzlePieces = new ArrayList<>();
        this.startGame = 0L;
        this.endGame = 0L;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public List<PuzzlePiece> getPuzzlePieces() {
        return puzzlePieces;
    }

    public void setPuzzlePieces(List<PuzzlePiece> puzzlePieces) {
        this.puzzlePieces = puzzlePieces;
    }

    public List<PuzzlePiece> getShuffledPuzzlePieces() {
        return shuffledPuzzlePieces;
    }

    public void setShuffledPuzzlePieces(List<PuzzlePiece> shuffledPuzzlePieces) {
        this.shuffledPuzzlePieces = shuffledPuzzlePieces;
    }

    public Long getStartGame() {
        return startGame;
    }

    public void setStartGame(Long startGame) {
        this.startGame = startGame;
    }

    public Long getEndGame() {
        return endGame;
    }

    public void setEndGame(Long endGame) {
        this.endGame = endGame;
    }

    public void start() {
        this.setStartGame(System.currentTimeMillis());
    }

    public void finish() {
        this.setEndGame(System.currentTimeMillis());
    }

    public long getScore() {
        return this.getEndGame() - this.getStartGame();
    }

    public void splitPuzzle() {
        this.puzzlePieces.clear();

        int rows = 1;
        int cols = 1;

        switch (this.getLevel()) {
            case EASY:
                rows = 3;
                cols = 3;
                break;
            case HARD:
                rows = 4;
                cols = 4;
                break;
        }

        Bitmap image = this.getImage();

        int pieceWidth = image.getWidth() / cols;
        int pieceHeight = image.getHeight() / rows;

        int posY = 0;
        for (int row = 0; row < rows; row++) {
            int posX = 0;
            for (int col = 0; col < cols; col++) {
                Bitmap bitmap = Bitmap.createBitmap(image, posX, posY, pieceWidth, pieceHeight);
                PuzzlePiece puzzlePiece = new PuzzlePiece(bitmap, posX, posY, pieceWidth, pieceHeight);
                this.puzzlePieces.add(puzzlePiece);
                posX += pieceWidth;
            }
            posY += pieceHeight;
        }
    }

    public void shuffle() {
        List<PuzzlePiece> shufflePieces = new ArrayList<>(this.getPuzzlePieces());
        Collections.shuffle(shufflePieces);
        this.setShuffledPuzzlePieces(shufflePieces);
    }

    public void exchangePieces(int posA, int posB) {
        Collections.swap(this.getShuffledPuzzlePieces(), posA, posB);
    }

    public boolean isComplete() {
        List<PuzzlePiece> sortedPieces = this.getPuzzlePieces();
        List<PuzzlePiece> shufflePieces = this.getShuffledPuzzlePieces();

        for (int i = 0; i < sortedPieces.size(); i++) {
            PuzzlePiece sortPieces = sortedPieces.get(i);
            PuzzlePiece shufflePiece = shufflePieces.get(i);
            if (sortPieces != shufflePiece) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return "Puzzle{" +
                "image=" + image +
                ", level=" + level +
                ", puzzlePieces=" + puzzlePieces +
                ", shuffledPuzzlePieces=" + shuffledPuzzlePieces +
                ", startGame=" + startGame +
                ", endGame=" + endGame +
                '}';
    }

}
