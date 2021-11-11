package com.dam.puzzlepals.holders;

import com.dam.puzzlepals.models.Puzzle;

public class PuzzleHolder {

    private final Puzzle puzzle = new Puzzle();

    public Puzzle getPuzzle() {
        return this.puzzle;
    }

    private static final PuzzleHolder puzzleHolder = new PuzzleHolder();

    public static PuzzleHolder getInstance() {
        return puzzleHolder;
    }
}
