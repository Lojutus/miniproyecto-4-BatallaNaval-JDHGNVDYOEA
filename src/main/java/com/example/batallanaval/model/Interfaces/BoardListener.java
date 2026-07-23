package com.example.batallanaval.model.Interfaces;

import com.example.batallanaval.model.Classes.Utils.CellState;
import com.example.batallanaval.model.Classes.Utils.Coordinate;

/**
 * Listener contract to receive notifications when a board cell changes state.
 */
public interface BoardListener {
    /**
     * Called when a cell has been updated.
     *
     * @param coordinate updated coordinate
     * @param newState new state of the cell
     */
    void onCellsUpdate(Coordinate coordinate, CellState newState);
}
