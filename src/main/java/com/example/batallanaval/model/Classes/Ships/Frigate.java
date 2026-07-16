package com.example.batallanaval.model.Classes.Ships;

import com.example.batallanaval.model.AbstractsClasses.AbstractShip;
import com.example.batallanaval.model.Classes.Utils.Coordinate;
import com.example.batallanaval.model.Classes.Utils.Orientation;

public class Frigate extends AbstractShip {
    public Frigate(Coordinate coordinate, Orientation orientation) {
        super(coordinate, orientation, 1, "Frigate");
    }
}
