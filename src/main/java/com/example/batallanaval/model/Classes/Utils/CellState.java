package com.example.batallanaval.model.Classes.Utils;

/**
 * Represents the possible states of a cell on the board.
 */
public enum CellState {
    /** Shot missed and recorded as fail. */
    FAIL,
    /** A ship occupies the cell. */
    SHIP,
    /** Cell contains water and has not been shot. */
    WATER,
    /** Cell has been hit successfully. */
    HIT,
    /** Cell belonged to a ship that was sunk. */
    SUNK
}
