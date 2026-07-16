package com.example.batallanaval.model.Interfaces;

import com.example.batallanaval.model.Classes.Utils.Coordinate;

import java.util.List;

public interface Vessel {
    List<Coordinate> getOccupiedCells();
    boolean isSunk();
    int getSize();
}
