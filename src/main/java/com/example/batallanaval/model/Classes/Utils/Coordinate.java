package com.example.batallanaval.model.Classes.Utils;

import java.io.PipedOutputStream;
import java.io.Serializable;
import java.util.Objects;

/**
 * Immutable coordinate on a 2D board.
 */
public class Coordinate implements Serializable {
    private final int posX;
    private final int posY;

    /**
     * Creates a coordinate with the given x and y positions.
     *
     * @param posX zero-based row index
     * @param posY zero-based column index
     */
    public Coordinate(int posX, int posY){
        this.posX = posX;
        this.posY = posY;
    }

    /** Row index (zero-based). */
    public int getPosX() { return posX; }
    /** Column index (zero-based). */
    public int getPosY() { return posY;}

    /**
     * Checks whether the coordinate is inside provided board bounds.
     *
     * @param row number of rows
     * @param col number of columns
     * @return true when inside bounds
     */
    public boolean isOutOfBoard(int row, int col){
        return posX >= 0 && posX < row && posY >= 0 && posY < col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordinate)) return false;
        Coordinate other = (Coordinate) o;
        return posX == other.posX && posY == other.posY;
    }

    @Override
    public int hashCode() {
        return Objects.hash(posX, posY);
    }

    @Override
    public String toString() {
        return "(" + posX + ", " + posY + ")";
    }

}
