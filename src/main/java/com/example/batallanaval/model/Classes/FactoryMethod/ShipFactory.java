package com.example.batallanaval.model.Classes.FactoryMethod;

import com.example.batallanaval.model.Classes.Ships.AircraftCarrier;
import com.example.batallanaval.model.Classes.Ships.Destroyer;
import com.example.batallanaval.model.Classes.Ships.Frigate;
import com.example.batallanaval.model.Classes.Ships.Submarine;
import com.example.batallanaval.model.Classes.Utils.Coordinate;
import com.example.batallanaval.model.Classes.Utils.Orientation;
import com.example.batallanaval.model.Interfaces.Vessel;

import java.util.ArrayList;
import java.util.List;

public class ShipFactory {
    private static final int[] FLEET_SIZES = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};

    private ShipFactory() {
    }

    public static List<Integer> getFleetSizes() {
        List<Integer> sizes = new ArrayList<>();
        for (int size : FLEET_SIZES) {
            sizes.add(size);
        }
        return sizes;
    }

    public static Vessel createShip(int size, Coordinate coordinate, Orientation orientation) {
        switch (size) {
            case 4:
                return new AircraftCarrier(coordinate, orientation);
            case 3:
                return new Submarine(coordinate, orientation);
            case 2:
                return new Destroyer(coordinate, orientation);
            case 1:
                return new Frigate(coordinate, orientation);
            default:
                throw new IllegalArgumentException("Tamaño Invalido, lo sentimos: " + size);
        }
    }
}
