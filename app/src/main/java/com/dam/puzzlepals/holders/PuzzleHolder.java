package com.dam.puzzlepals.holders;

import com.dam.puzzlepals.models.Puzzle;
import com.dam.puzzlepals.models.User;

public class PuzzleHolder {

    private final Puzzle puzzle = new Puzzle();
    private User user = new User();
    private String backgroundSongName = "";
    private boolean isMute = false;
    
    private static final PuzzleHolder puzzleHolder = new PuzzleHolder();

    public static PuzzleHolder getInstance() {
        return puzzleHolder;
    }

    public Puzzle getPuzzle() {
        return puzzle;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBackgroundSongName() {
        return backgroundSongName;
    }

    public void setBackgroundSongName(String backgroundSongName) {
        this.backgroundSongName = backgroundSongName;
    }

    public boolean isMute() {
        return isMute;
    }

    public void setMute(boolean mute) {
        isMute = mute;
    }

}
