package com.example.batallanaval.controller;

import com.example.batallanaval.controller.helpers.FileManager;
import com.example.batallanaval.controller.helpers.ShipsManager;
import com.example.batallanaval.model.Classes.Boards.Board;
import com.example.batallanaval.model.Classes.FactoryMethod.ShipFactory;
import com.example.batallanaval.model.Classes.GameState.GameState;
import com.example.batallanaval.model.Classes.Players.Human;
import com.example.batallanaval.model.Classes.Players.Machine;
import com.example.batallanaval.model.Classes.Utils.Coordinate;
import com.example.batallanaval.model.Classes.Utils.Orientation;
import com.example.batallanaval.model.Exceptions.GameLoadableException;

/*
Comentario de funcionamiento para borrar en la documentacion:
Esta clase esta diseñada para manejar logica del funcionamiento del juego, para no sobrecargar el Handle de acciones de la vista
se encarga de recibir La informacion del juego y devolcer true o false a las condiciones preguntadas por el handle


 */
public class GameManager
{
    public GameManager(){}
    private Boolean ready = false;


    GameState gameState;
    // PARA INICIALIZAR UN JUEGO SE NECESITA LAS POSICIONES DEL JUGADOR Y LA MAQUINA
    Board positionBoard = new Board();
    Board mainBoard = new Board();
    //crear los jugadores
    Human player;
    Machine machine;

    private Boolean createPlayerAndMachine(String nickname){
        try{
            player = new Human(nickname, positionBoard, mainBoard);
            machine = new Machine("machine", mainBoard , positionBoard);
            return true;
        }
        catch (GameLoadableException e){
            return false;
        }
    }
    // Despues de crear los tableros y referencialos a los jugadores se les debe añadir los barcos creandolos o  cargalos de un archivo


    //Carga el juego
    public Boolean LoadGame(String nickname){
        try{
            gameState =  FileManager.getInstance().load(nickname);
            positionBoard = gameState.getPositionBoard();
            mainBoard = gameState.getMainBoard();
            return true;
        }
        catch (GameLoadableException e){
            return false;
        }
    }


    public Boolean createShip(int size, int corX , int corY, String orientation ){
        Coordinate coordinate = new Coordinate(corX, corY);
        Orientation orientationEnum = null;

        if(orientation.equals("HORINZONTAL")){
             orientationEnum = Orientation.HORINZONTAL;
        }
        if(orientation.equals("VERTICAL")){
             orientationEnum = Orientation.VERTICAL;
        }
        try{
           if( ShipsManager.getInstance().canConstructShips(size , coordinate , orientationEnum) ){
                if(ShipsManager.getInstance().shipAddByPlayer(size)){
                    player.placeSingleShip(ShipFactory.createShip(size , coordinate , orientationEnum));
                }


           };

        }
        catch (Exception e){
            return false;
        }
        if(isReady()){
            ready = true;
        }
        return true;
    }

    public Boolean initGameState(String nickname ) throws GameLoadableException {
        if(!createPlayerAndMachine(nickname)){
            return false;
        }
        if( gameState == null ){
            if( LoadGame(nickname) ){
                ready = true; // Se creo el jugador junto la maquina y se obtuvo la partida anterior
            }
        }
        else{
            gameState = new GameState(nickname , positionBoard , mainBoard ,0);
        }
        return true;
    }
    public Boolean isReady()  {
        return ShipsManager.getInstance().fullShip();
    }
    public Boolean getReady(){
        return ready;
    }

    public void restartGame(){
        ready = false;
        gameState = null;
        positionBoard = new Board();
        mainBoard = new Board();
        ShipsManager.restartInstance();
    }

}



