package com.example.batallanaval.model.Classes.Ships;

import com.example.batallanaval.model.AbstractsClasses.AbstractShip;
import com.example.batallanaval.model.Classes.Utils.Coordinate;
import com.example.batallanaval.model.Classes.Utils.Orientation;

/** Bote — ocupa 3 casillas. */
public class Boat extends AbstractShip {
    public Boat(Coordinate coordinate, Orientation orientation) {
        super(coordinate, orientation, 3, "Boat");
    }
}
