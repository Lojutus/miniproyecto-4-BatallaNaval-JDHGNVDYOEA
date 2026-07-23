package com.example.batallanaval.model.Classes.Ships;

import com.example.batallanaval.model.AbstractsClasses.AbstractShip;
import com.example.batallanaval.model.Classes.Utils.Coordinate;
import com.example.batallanaval.model.Classes.Utils.Orientation;

/** Acorazado — ocupa 4 casillas. */
public class Battleship extends AbstractShip {
    public Battleship(Coordinate coordinate, Orientation orientation) {
        super(coordinate, orientation, 4, "Battleship");
    }
}
