package com.example.batallanaval.model.Interfaces;

import com.example.batallanaval.model.Classes.Utils.Coordinate;
import com.example.batallanaval.model.Classes.Utils.ShotResult;
import com.example.batallanaval.model.Exceptions.InvalidShotException;
import com.example.batallanaval.model.Exceptions.OutOfBoardException;

public interface Shootable {
    ShotResult shoot(Coordinate coordinate) throws InvalidShotException, OutOfBoardException;
}
