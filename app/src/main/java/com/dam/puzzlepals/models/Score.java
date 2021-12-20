package com.dam.puzzlepals.models;

import com.dam.puzzlepals.enums.Level;

import java.util.Date;

public class Score {
    private long id;
    private Date date;
    private long score;
    private Level level;
    private String image;

    public Score(long id, Date date, long score, Level level, String image) {
        this.id = id;
        this.date = date;
        this.score = score;
        this.level = level;
        this.image = image;
    }

    public Score(long id, Date date, long score, Level level) {
        this.id = id;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Score{" +
                "id=" + id +
                ", date=" + date +
                ", score=" + score +
                ", level=" + level +
                ", image='" + image + '\'' +
                '}';
    }
}
