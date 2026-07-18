package com.example.batallanaval.controller.helpers;

import com.example.batallanaval.model.Classes.Utils.Coordinate;
import com.example.batallanaval.model.Classes.Utils.Orientation;

import java.io.IOException;

public class ShipsManager {

    private final int[] shipsAdd = new int[4];
    // Fila 0 con 4 elementos;
    public ShipsManager(){

    }


    public Boolean canConstructShips(int size, Coordinate coordinate, Orientation orientation) throws IOException {
        return Verifier.getInstance().verificate(size) && Verifier.getInstance().verificate(coordinate) && Verifier.getInstance().verificate(orientation);
    }
    public Boolean fullShip(){
        int sum = 0;
        for (int i = 1; i <= 4; i++){
            sum = sum + shipsAdd[i];
        }
        return sum>=10;
    }
    public Boolean shipAddByPlayer(int size){
        if (size<1 || size>4){return false;}
        switch(size){
            case 1:
                if(shipsAdd[size]>1)
                    return false;
                break;
            case 2:
                if(shipsAdd[size]>2)
                    return false;
                break;
            case 3:
                if(shipsAdd[size]>3)
                    return false;
                break;
            case 4:
                if(shipsAdd[size]>4)
                    return false;
                break;

        }
        shipsAdd[size]++;
        return true;
    }

    /**
     * Holder for the lazy-initialized singleton instance (initialization-on-demand holder idiom).
     */
    private static class GameHolder {
        private static ShipsManager INSTANCE;
    }

    /**
     * Retrieves the single instance of GameStage (Singleton pattern).
     *
     * If the instance does not exist, it creates one by initializing a new GameStage.
     *
     * @return the single instance of FileManager
     *
     */
    public static ShipsManager getInstance(){
        if(GameHolder.INSTANCE == null){
            GameHolder.INSTANCE = new ShipsManager();
        }

        return GameHolder.INSTANCE;
    }
    public static void restartInstance(){
        if(GameHolder.INSTANCE != null){
            GameHolder.INSTANCE = new ShipsManager();
        }

    }
}
