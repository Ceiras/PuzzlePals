package com.dam.puzzlepals.models;

import com.dam.puzzlepals.enums.Level;

import java.util.Date;

public class Score {

    private Date date;
    private String player;
    private Level level;
    private Long puzzleNumber;
    private Long score;

    public Score(Date date, String name, Level level, Long puzzleNumber, Long score) {
        this.date = date;
        this.player = name;
        this.level = level;
        this.puzzleNumber = puzzleNumber;
        this.score = score;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Long getPuzzleNumber() {
        return puzzleNumber;
    }

    public void setPuzzleNumber(Long puzzleNumber) {
        this.puzzleNumber = puzzleNumber;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

}
