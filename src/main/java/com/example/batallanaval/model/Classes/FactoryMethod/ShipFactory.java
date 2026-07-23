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

/**
 * Flota original:
 *  1 × AircraftCarrier  (4 casillas)
 *  2 × Submarine        (3 casillas)
 *  3 × Destroyer        (2 casillas)
 *  4 × Frigate          (1 casilla)
 *  Total: 10 barcos
 */
public class ShipFactory {

    private static final int[] FLEET_SIZES = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};

    private ShipFactory() {}

    public static List<Integer> getFleetSizes() {
        List<Integer> sizes = new ArrayList<>();
        for (int size : FLEET_SIZES) {
            sizes.add(size);
        }
        return sizes;
    }

    public static Vessel createShip(int size, Coordinate coordinate, Orientation orientation) {
        return switch (size) {
            case 4 -> new AircraftCarrier(coordinate, orientation);
            case 3 -> new Submarine(coordinate, orientation);
            case 2 -> new Destroyer(coordinate, orientation);
            case 1 -> new Frigate(coordinate, orientation);
            default -> throw new IllegalArgumentException("Tamaño inválido: " + size);
        };
    }

    /** Sobrecarga para compatibilidad con código que pasa un tipo explícito. */
    public static Vessel createShip(int size, String type,
                                    Coordinate coordinate, Orientation orientation) {
        return createShip(size, coordinate, orientation);
    }
}
