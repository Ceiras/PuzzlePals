package com.dam.puzzlepals;

import androidx.lifecycle.ViewModel;

import com.dam.puzzlepals.entities.PuzzleModel;

import java.io.Serializable;


public class MainActivityViewModel extends ViewModel implements Serializable {

    private PuzzleModel puzzleModel = new PuzzleModel();

    public PuzzleModel getPuzzleModel() {
        return puzzleModel;
    }

}
