package com.dam.puzzlepals.holders;

import com.dam.puzzlepals.models.Puzzle;

public class PuzzleHolder {

    private final Puzzle puzzle = new Puzzle();
    private boolean isMute = false;

    public Puzzle getPuzzle() {
        return this.puzzle;
    }
    public boolean isMute() {
        return this.isMute;
    }

    public void setMute(boolean isMute) {
        this.isMute = isMute;
    }

    private static final PuzzleHolder puzzleHolder = new PuzzleHolder();

    public static PuzzleHolder getInstance() {
        return puzzleHolder;
    }
}
