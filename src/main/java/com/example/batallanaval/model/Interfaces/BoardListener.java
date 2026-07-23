package com.example.batallanaval.model.Interfaces;

import com.example.batallanaval.model.Classes.Utils.CellState;
import com.example.batallanaval.model.Classes.Utils.Coordinate;

public interface BoardListener {
    void onCellsUpdate(Coordinate coordinate, CellState newState);
}
