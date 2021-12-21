package com.dam.puzzlepals.models;

public class User {

    private String email;
    private String name;
    private Long puzzleNumber;

    public User() {
    }

    public User(String email, String name, Long puzzleNumber) {
        this.email = email;
        this.name = name;
        this.puzzleNumber = puzzleNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPuzzleNumber() {
        return puzzleNumber;
    }

    public void setPuzzleNumber(Long puzzleNumber) {
        this.puzzleNumber = puzzleNumber;
    }

}
