package com.example.batallanaval.model.Interfaces;

import com.example.batallanaval.model.Classes.Utils.Coordinate;
import com.example.batallanaval.model.Classes.Utils.ShotResult;
import com.example.batallanaval.model.Exceptions.InvalidShotException;
import com.example.batallanaval.model.Exceptions.OutOfBoardException;

/**
 * Abstraction for objects that can be shot at.
 *
 * @see com.example.batallanaval.model.Classes.Utils.ShotResult
 */
public interface Shootable {
    /**
     * Performs a shot at the given coordinate and returns the result.
     *
     * @param coordinate target coordinate
     * @return result of the shot
     * @throws InvalidShotException if the shot is invalid (already shot)
     * @throws OutOfBoardException if the coordinate is outside bounds
     */
    ShotResult shoot(Coordinate coordinate) throws InvalidShotException, OutOfBoardException;
}
