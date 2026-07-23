package com.example.batallanaval.model.Interfaces;

import com.example.batallanaval.model.Classes.Utils.Coordinate;

import java.util.List;

/**
 * Represents a vessel that occupies cells on the board.
 *
 * <p>Implementations provide the occupied coordinates, size and sunk
 * status. The game logic queries these methods to determine hits and
 * whether the vessel is destroyed.</p>
 */
public interface Vessel {
    /**
     * Returns the list of coordinates occupied by this vessel.
     *
     * @return unmodifiable list of occupied coordinates
     */
    List<Coordinate> getOccupiedCells();

    /**
     * Indicates whether the vessel has been sunk.
     *
     * @return true when the vessel is sunk
     */
    boolean isSunk();

    /**
     * Returns the vessel size in cells.
     *
     * @return size of the vessel
     */
    int getSize();
}
