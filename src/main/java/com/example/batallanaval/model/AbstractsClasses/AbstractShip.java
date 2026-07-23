package com.example.batallanaval.model.AbstractsClasses;

import com.example.batallanaval.model.Classes.Utils.Coordinate;
import com.example.batallanaval.model.Classes.Utils.Orientation;
import com.example.batallanaval.model.Classes.Utils.ShotResult;
import com.example.batallanaval.model.Exceptions.OutOfBoardException;
import com.example.batallanaval.model.Interfaces.Shootable;
import com.example.batallanaval.model.Interfaces.Vessel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public abstract class AbstractShip implements Shootable, Vessel {
    protected Coordinate coordinateShip;
    protected Orientation orientation;
    protected List<Coordinate> occupiedCells;
    protected Set<Coordinate> hitCells;
    protected final int size;
    protected final String name;


    public AbstractShip(Coordinate coordinate, Orientation orientation, int size, String name) {
        this.size = size;           // asignar PRIMERO, calculateOccupiedCells() lo necesita
        this.name = name;
        coordinateShip = coordinate;
        this.orientation = orientation;
        this.occupiedCells = calculateOccupiedCells();
        this.hitCells = new HashSet<>();
    }

    private List<Coordinate> calculateOccupiedCells() {

        if (size <= 0) {
            throw new IllegalArgumentException("El tamaño del barco debe ser mayor a 0");
        }

        List<Coordinate> cells = new ArrayList<>();
        for (int i = 0; i < size; i++){
            int row = coordinateShip.getPosX();
            int col = coordinateShip.getPosY();

            if (Orientation.HORINZONTAL == orientation) {
                col += i;
            } else {
                row += i;
            }
            cells.add(new Coordinate(row, col));
        }
        return cells;
    }


    @Override
    public List<Coordinate> getOccupiedCells() {
        return occupiedCells;
    }

    @Override
    public boolean isSunk() {
        return hitCells.size() == getSize();
    }

    @Override
    public ShotResult shoot(Coordinate coordinate) {
        if (!occupiedCells.contains(coordinate)) {
            throw new OutOfBoardException("Las coordenadas no coinciden con las del barco");
        }
        hitCells.add(coordinate);
        return isSunk() ? ShotResult.SUNK : ShotResult.HIT;
    }

    public int getSize(){
        return size;
    }
    public String getName(){
        return name;
    }
    public Orientation getOrientation() {
        return orientation;
    }
    public Coordinate getCoordinateShip() {
        return coordinateShip;
    }

}
