package com.example.batallanaval.model.Classes.Ships;

import com.example.batallanaval.model.AbstractsClasses.AbstractShip;
import com.example.batallanaval.model.Classes.Utils.Coordinate;
import com.example.batallanaval.model.Classes.Utils.Orientation;

public class Submarine extends AbstractShip {
    public Submarine(Coordinate origin, Orientation orientation) {
        super(origin, orientation, 3, "Submarine");
    }
}
