package com.dam.puzzlepals.holders;

import com.dam.puzzlepals.models.Puzzle;

public class PuzzleHolder {

    private final Puzzle puzzle = new Puzzle();
    private String backgroundSongName = "";
    private boolean isMute = false;

    public Puzzle getPuzzle() {
        return this.puzzle;
    }

    public String getBackgroundSongName() {
        return this.backgroundSongName;
    }

    public boolean isMute() {
        return this.isMute;
    }

    public void setBackgroundSongName(String name) {
        this.backgroundSongName = name;
    }

    public void setMute(boolean isMute) {
        this.isMute = isMute;
    }
    
    private static final PuzzleHolder puzzleHolder = new PuzzleHolder();

    public static PuzzleHolder getInstance() {
        return puzzleHolder;
    }
}
