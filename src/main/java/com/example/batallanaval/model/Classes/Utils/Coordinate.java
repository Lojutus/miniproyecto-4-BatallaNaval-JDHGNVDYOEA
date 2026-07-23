package com.example.batallanaval.model.Classes.Utils;

import java.io.PipedOutputStream;
import java.io.Serializable;
import java.util.Objects;

public class Coordinate implements Serializable {
    private final int posX;
    private final int posY;

    public Coordinate(int posX, int posY){
        this.posX = posX;
        this.posY = posY;
    }

    public int getPosX() { return posX; }
    public int getPosY() { return posY;}

    public boolean isOutOfBoard(int row, int col){
        return posX >= 0 && posX < row && posY >= 0 && posY < col;
    }

    //Compara por contenido y no por Identidad
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordinate)) return false;
        Coordinate other = (Coordinate) o;
        return posX == other.posX && posY == other.posY;
    }

    //Se debe modificar el codigo Hash
    @Override
    public int hashCode() {
        return Objects.hash(posX, posY);
    }

    //Depurar con las coordenadas
    @Override
    public String toString() {
        return "(" + posX + ", " + posY + ")";
    }


}
