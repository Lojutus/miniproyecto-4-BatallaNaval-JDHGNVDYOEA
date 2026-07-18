package com.example.batallanaval.controller.helpers;

import com.example.batallanaval.model.Classes.Utils.Coordinate;
import com.example.batallanaval.model.Classes.Utils.Orientation;

import java.io.IOException;
// CLASE DE AYUDA, MUY UTIL EN LAS PRUEBAS
public class Verifier {

    public Verifier(){

    }
    /**
     * Holder for the lazy-initialized singleton instance (initialization-on-demand holder idiom).
     */
    private static class GameHolder {
        private static Verifier INSTANCE;
    }

    /**
     * Retrieves the single instance of GameStage (Singleton pattern).
     *
     * If the instance does not exist, it creates one by initializing a new GameStage.
     *
     * @return the single instance of Verifier
     * @throws IOException if the Verifier cannot be instantiated due to FXML loading errors
     */
    public static Verifier getInstance() throws IOException{
        if(GameHolder.INSTANCE == null){
            GameHolder.INSTANCE = new Verifier();
        }

        return GameHolder.INSTANCE;
    }
 //Clase de verificaciones con polimorfismo
     public Boolean verificate(Coordinate coordinate){
        return coordinate.isOutOfBoard(coordinate.getPosX(), coordinate.getPosY());

     }
    public Boolean verificate(Orientation orientation){
       switch (orientation){
           case HORINZONTAL, VERTICAL:
               return true;
       }
        return  false;

    }
    public Boolean verificate(int size){
        return ( size>1 && size<4);
    }



}
