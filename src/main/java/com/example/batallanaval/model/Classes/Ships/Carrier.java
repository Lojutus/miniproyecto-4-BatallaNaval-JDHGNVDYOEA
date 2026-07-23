package com.example.batallanaval.model.Classes.Ships;

import com.example.batallanaval.model.AbstractsClasses.AbstractShip;
import com.example.batallanaval.model.Classes.Utils.Coordinate;
import com.example.batallanaval.model.Classes.Utils.Orientation;

/** Portaaviones — ocupa 5 casillas. */
public class Carrier extends AbstractShip {
    public Carrier(Coordinate coordinate, Orientation orientation) {
        super(coordinate, orientation, 5, "Carrier");
    }
}
