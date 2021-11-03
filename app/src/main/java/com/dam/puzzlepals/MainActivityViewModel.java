package com.dam.puzzlepals;

import androidx.lifecycle.ViewModel;


public class MainActivityViewModel extends ViewModel {

    private PuzzleModel puzzleModel = new PuzzleModel();

    public PuzzleModel getPuzzleModel() {
        return puzzleModel;
    }

}
