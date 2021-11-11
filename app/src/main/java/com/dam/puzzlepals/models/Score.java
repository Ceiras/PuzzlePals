package com.dam.puzzlepals.models;

import androidx.annotation.NonNull;

import com.dam.puzzlepals.enums.Level;

import java.util.Date;

public class Score {
    private long id;
    private Date date;
    private long score;
    private Level level;

    public Score(long id, Date date, long score, Level level) {
        this.id = id;
        this.date = date;
        this.score = score;
        this.level = level;
    }

    public Score(Date date, long score, Level level) {
        this.date = date;
        this.score = score;
        this.level = level;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    @NonNull
    @Override
    public String toString() {
        return "Score{" +
                "id=" + id +
                ", date=" + date +
                ", score=" + score +
                ", level=" + level +
                '}';
    }
}
