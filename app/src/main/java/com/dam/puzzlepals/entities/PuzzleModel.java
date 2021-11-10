package com.dam.puzzlepals.entities;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.dam.puzzlepals.entities.Pieza;

import java.io.Serializable;
import java.util.List;

public class PuzzleModel implements Parcelable {

    public final static int DIFICULTAD_FACIL = 1;
    public final static int DIFICULTAD_DIFICIL = 2;

    private Bitmap imagen;

    private Integer dificultad;

    private List<Pieza> piezas;

    private List<Pieza> piezasDesordenadas;

    private Long inicioJuego;

    public PuzzleModel(){

    }

    public PuzzleModel(Parcel in) {
        imagen = in.readParcelable(Bitmap.class.getClassLoader());
        if (in.readByte() == 0) {
            dificultad = null;
        } else {
            dificultad = in.readInt();
        }
        if (in.readByte() == 0) {
            inicioJuego = null;
        } else {
            inicioJuego = in.readLong();
        }
    }

    public static final Creator<PuzzleModel> CREATOR = new Creator<PuzzleModel>() {
        @Override
        public PuzzleModel createFromParcel(Parcel in) {
            return new PuzzleModel(in);
        }

        @Override
        public PuzzleModel[] newArray(int size) {
            return new PuzzleModel[size];
        }
    };


    public Bitmap getImagen() {
        return imagen;
    }

    public void setImagen(Bitmap imagen) {
        this.imagen = imagen;
    }

    public Integer getDificultad() {
        return dificultad;
    }

    public void setDificultad(Integer dificultad) {
        this.dificultad = dificultad;
    }

    public List<Pieza> getPiezas() {
        return piezas;
    }

    public void setPiezas(List<Pieza> piezas) {
        this.piezas = piezas;
    }

    public Long getInicioJuego() {
        return inicioJuego;
    }

    public void setInicioJuego(Long inicioJuego) {
        this.inicioJuego = inicioJuego;
    }

    public List<Pieza> getPiezasDesordenadas() {
        return piezasDesordenadas;
    }

    public void setPiezasDesordenadas(List<Pieza> piezasDesordenadas) {
        this.piezasDesordenadas = piezasDesordenadas;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(imagen, i);
        if (dificultad == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(dificultad);
        }
        if (inicioJuego == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(inicioJuego);
        }
    }
}
