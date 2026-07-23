package com.example.batallanaval.controller.helpers;

import com.example.batallanaval.model.Classes.Utils.Coordinate;
import com.example.batallanaval.model.Classes.Utils.Orientation;

import java.io.IOException;

/**
 * Controla cuántos barcos de cada tamaño ha colocado el jugador.
 *
 * Flota:
 *   size 4 → 1 × AircraftCarrier
 *   size 3 → 2 × Submarine
 *   size 2 → 3 × Destroyer
 *   size 1 → 4 × Frigate
 *   Total: 10 barcos
 *
 * Array shipsAdd: índice = size - 1  → [0]=size1, [1]=size2, [2]=size3, [3]=size4
 */
public class ShipsManager {

    private final int[] shipsAdd = new int[4];

    // Límite por tamaño: size1→4, size2→3, size3→2, size4→1
    private static final int[] MAX_PER_SIZE = {4, 3, 2, 1};

    private static final int FLEET_TOTAL = 10;

    public ShipsManager() {}

    public Boolean canConstructShips(int size, Coordinate coordinate,
                                     Orientation orientation) throws IOException {
        return Verifier.getInstance().verificate(size)
            && Verifier.getInstance().verificate(coordinate)
            && Verifier.getInstance().verificate(orientation);
    }

    public Boolean fullShip() {
        int sum = 0;
        for (int count : shipsAdd) sum += count;
        return sum >= FLEET_TOTAL;
    }

    public Boolean shipAddByPlayer(int size) {
        if (size < 1 || size > 4) return false;
        int idx = size - 1;
        if (shipsAdd[idx] >= MAX_PER_SIZE[idx]) return false;
        shipsAdd[idx]++;
        return true;
    }

    // ── Singleton ─────────────────────────────────────────────────────────────

    private static class GameHolder {
        private static ShipsManager INSTANCE;
    }

    public static ShipsManager getInstance() {
        if (GameHolder.INSTANCE == null) {
            GameHolder.INSTANCE = new ShipsManager();
        }
        return GameHolder.INSTANCE;
    }

    public static void restartInstance() {
        GameHolder.INSTANCE = new ShipsManager();
    }
}
