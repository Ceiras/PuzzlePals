package com.dam.puzzlepals.ui;

import static com.dam.puzzlepals.entities.PuzzleModel.DIFICULTAD_DIFICIL;
import static com.dam.puzzlepals.entities.PuzzleModel.DIFICULTAD_FACIL;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dam.puzzlepals.MainActivityViewModel;
import com.dam.puzzlepals.controller.PuzzleController;
import com.dam.puzzlepals.entities.PuzzleModel;
import com.dam.puzzlepals.R;
import com.dam.puzzlepals.entities.Pieza;
import com.dam.puzzlepals.enums.Levels;
import com.dam.puzzlepals.sqlite.ScoreAPI;

import java.util.ArrayList;
import java.util.List;


public class PuzzleActivity extends AppCompatActivity {

    public static Bitmap bitmap;
    private PuzzleModel viewModel;
    public static Integer dificult;
    private PuzzleController puzzleController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new PuzzleModel();
        puzzleController = new PuzzleController(viewModel);
        viewModel.setDificultad(dificult);
        viewModel.setImagen(bitmap);

        setContentView(R.layout.acitivity_puzzle);
        GridView puzzleGridView = findViewById(R.id.puzzle_grid_view);

        int numColumns = 0;
        if(dificult == PuzzleModel.DIFICULTAD_FACIL){
            numColumns = 3;
        } else if(dificult == PuzzleModel.DIFICULTAD_DIFICIL){
            numColumns = 4;
        }

        List<Pieza> piezas = generarPiezas();
        viewModel.setPiezas(piezas);

        puzzleController.iniciarPuzzle();
        puzzleController.barajarPiezas();

        puzzleGridView.setNumColumns(numColumns);

        ArrayAdapter adaptador = new ArrayAdapter<Pieza>(PuzzleActivity.this, R.layout.acitivity_puzzle, viewModel.getPiezas()){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View celda = LayoutInflater.from(PuzzleActivity.this).inflate(R.layout.celda_puzzle_grid_view, parent, false);
                ImageView imagenPieza = celda.findViewById(R.id.celda_puzzle_imagen);
                Pieza pieza = (Pieza) getItem(position);
                imagenPieza.setImageBitmap(pieza.getImagen());
                return celda;
            }
        };

        puzzleGridView.setAdapter(adaptador);

        List<Integer> piezasAIntercambiar = new ArrayList<>();
        puzzleGridView.setOnItemClickListener((parent, view, position, id) -> {
            view.findViewById(R.id.celda_puzzle_imagen).setBackgroundColor(Color.MAGENTA);
            piezasAIntercambiar.add(position);
            if(piezasAIntercambiar.size() == 2){
                //intercambiar piezas
                puzzleController.intercambiarPiezas(piezasAIntercambiar.get(0), piezasAIntercambiar.get(1));
                this.checkPuzzleAndContinue();
                adaptador.notifyDataSetChanged();
                piezasAIntercambiar.clear();
            }
        });
    }

    private void checkPuzzleAndContinue(){
        boolean resultado = puzzleController.comprobarPuzzleResuelto();
        if(resultado){
            long puntuacion = puzzleController.finalizarJuego();
            int dificultad = viewModel.getDificultad();

            ScoreAPI scoreApi = new ScoreAPI(PuzzleActivity.this);
            scoreApi.addScore(puntuacion, Levels.EASY);

            final Dialog fbDialogue = new Dialog(PuzzleActivity.this, android.R.style.Theme_Black_NoTitleBar);
            fbDialogue.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
            fbDialogue.setContentView(R.layout.congrats_dialogue);
            fbDialogue.setCancelable(true);
            fbDialogue.show();
            TextView text2 = (TextView) fbDialogue.findViewById(R.id.score);
            String score2 = String.valueOf(Math.abs(puntuacion));
            text2.setText(score2);
            Button button1 = (Button) fbDialogue.findViewById(R.id.buttonToHome);
            Button button2 = (Button) fbDialogue.findViewById(R.id.buttonToSelectImg);
            button1.setOnClickListener(view -> {
                fbDialogue.hide();
            });
            button2.setOnClickListener(view -> {
                fbDialogue.hide();
            });
        }
    }

    private List<Bitmap> splitImage(Bitmap bitmap) {
        int rows = 1;
        int cols = 1;

        if (dificult == PuzzleModel.DIFICULTAD_FACIL) {
            rows = 3;
            cols = 3;
        } else if (dificult == PuzzleModel.DIFICULTAD_DIFICIL) {
            rows = 4;
            cols = 4;
        }

        List<Bitmap> pieces = new ArrayList<>();

        // Calculate the with and height of the pieces
        int pieceWidth = bitmap.getWidth() / cols;
        int pieceHeight = bitmap.getHeight() / rows;

        // Create each bitmap piece and add it to the resulting array
        int yCoord = 0;
        for (int row = 0; row < rows; row++) {
            int xCoord = 0;
            for (int col = 0; col < cols; col++) {
                pieces.add(Bitmap.createBitmap(bitmap, xCoord, yCoord, pieceWidth, pieceHeight));
                xCoord += pieceWidth;
            }
            yCoord += pieceHeight;
        }

        return pieces;
    }

    private List<Pieza> generarPiezas(){
        List<Bitmap> bitmaps = this.splitImage(bitmap);
        List<Pieza> piezas = new ArrayList<>();
        for (Bitmap bitmap : bitmaps) {
            Pieza p = new Pieza(bitmap);
            piezas.add(p);
        }
        return piezas;
    }

   /* @Override
    protected void onStart() {
        super.onStart();

        GridView puzzleGridView = findViewById(R.id.puzzle_grid_view);
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        PuzzleModel hola = viewModel.getPuzzleModel();
        int numColumns = 0;
        if(viewModel.getPuzzleModel().getDificultad() == PuzzleModel.DIFICULTAD_FACIL){
            numColumns = 3;
        } else if(viewModel.getPuzzleModel().getDificultad() == PuzzleModel.DIFICULTAD_DIFICIL){
            numColumns = 4;
        }

        puzzleGridView.setNumColumns(numColumns);
        puzzleGridView.setAdapter(
                new ArrayAdapter(PuzzleActivity.this, R.layout.acitivity_puzzle, viewModel.getPuzzleModel().getPiezas()){
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View celda = LayoutInflater.from(PuzzleActivity.this).inflate(R.layout.celda_puzzle_grid_view, parent, false);
                        ImageView imagenPieza = celda.findViewById(R.id.celda_puzzle_imagen);
                        Pieza pieza = (Pieza) getItem(position);
                        imagenPieza.setImageBitmap(pieza.getImagen());
                        return celda;
                    }
                }
        );


    }*/
}