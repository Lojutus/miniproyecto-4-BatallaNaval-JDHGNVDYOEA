package com.example.batallanaval.controller;

import com.example.batallanaval.controller.helpers.BoardViewUptader;
import com.example.batallanaval.controller.helpers.FileManager;
import com.example.batallanaval.controller.helpers.ShipsManager;
import com.example.batallanaval.model.Classes.Boards.Board;
import com.example.batallanaval.model.Classes.FactoryMethod.ShipFactory;
import com.example.batallanaval.model.Classes.GameState.GameState;
import com.example.batallanaval.model.Classes.Players.Human;
import com.example.batallanaval.model.Classes.Players.Machine;
import com.example.batallanaval.model.Classes.Utils.Coordinate;
import com.example.batallanaval.model.Classes.Utils.Orientation;
import com.example.batallanaval.model.Classes.Utils.ShotResult;
import com.example.batallanaval.model.Exceptions.GameLoadableException;
import com.example.batallanaval.model.Interfaces.BoardListener;

/*
Comentario de funcionamiento para borrar en la documentacion:
Esta clase esta diseñada para manejar logica del funcionamiento del juego, para no sobrecargar el Handle de acciones de la vista
se encarga de recibir La informacion del juego y devolcer true o false a las condiciones preguntadas por el handle


 */
public class GameManager
{
    public GameManager(){}
    private Boolean ready = false;
    BoardViewUptader boardListenerHuman;
    BoardViewUptader boardListenerMachine;


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
            machine = new Machine("machine", mainBoard, positionBoard);
            machine.placeShips();  // la máquina coloca sus barcos aleatoriamente
            return updateListeners();
        }
        catch (Exception e){
            System.out.println("[createPlayerAndMachine] fallo: " + e.getMessage());
            return false;
        }
    }
    private Boolean updateListeners(){
        // boardListenerHuman y boardListenerMachine son opcionales (legacy).
        // Solo agregar si no son null para evitar NPE en notifyListeners.
        if (boardListenerHuman  != null) positionBoard.addListener(boardListenerHuman);
        if (boardListenerMachine != null) mainBoard.addListener(boardListenerMachine);
        return true;
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


    public Boolean createShip(int size, int corX, int corY, String orientation) {
        return createShip(size, corX, corY, orientation, null);
    }

    public Boolean createShip(int size, int corX, int corY, String orientation, String type) {
        Coordinate coordinate = new Coordinate(corX, corY);
        Orientation orientationEnum = null;

        if (orientation.equals("HORINZONTAL")) orientationEnum = Orientation.HORINZONTAL;
        if (orientation.equals("VERTICAL"))    orientationEnum = Orientation.VERTICAL;
        if (orientationEnum == null)           return false;

        try {
            if (!ShipsManager.getInstance().canConstructShips(size, coordinate, orientationEnum)) {
                return false;
            }

            var ship = (type != null)
                ? ShipFactory.createShip(size, type, coordinate, orientationEnum)
                : ShipFactory.createShip(size, coordinate, orientationEnum);

            player.placeSingleShip(ship);
            ShipsManager.getInstance().shipAddByPlayer(size);

        } catch (Exception e) {
            System.out.println("[createShip] fallo: " + e.getMessage());
            return false;
        }

        if (isReady()) ready = true;
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

    public Boolean playerShot( int corX , int corY){
        Coordinate coordinate = new Coordinate(corX, corY);
        return mainBoard.shoot(coordinate) != ShotResult.WATER;
    }
    public void machineShot(){
         machine.chooseShot(null) ;
    }

    /** Tablero propio del jugador (la máquina dispara aquí). */
    public Board getPlayerBoard() {
        return positionBoard;
    }

    /** Tablero de la máquina (el jugador dispara aquí). */
    public Board getMachineBoard() {
        return mainBoard;
    }

    /** Retorna true si todos los barcos de la máquina están hundidos → jugador gana. */
    public boolean playerWins() {
        return mainBoard.allShipsSunk();
    }

    /** Retorna true si todos los barcos del jugador están hundidos → máquina gana. */
    public boolean machineWins() {
        return positionBoard.allShipsSunk();
    }

    /** Guarda el estado actual de la partida. */
    public boolean saveGame() {
        try {
            if (player == null) return false;
            GameState state = new GameState(
                player.getNickname(), positionBoard, mainBoard,
                player.getEnemyShipSunk()
            );
            FileManager.getInstance().save(state);
            return true;
        } catch (Exception e) {
            System.out.println("[saveGame] fallo: " + e.getMessage());
            return false;
        }
    }

    /** Retorna true si existe un archivo de partida guardada para ese nickname. */
    public static boolean hasSavedGame(String nickname) {
        if (nickname == null || nickname.isBlank()) return false;
        java.io.File f = new java.io.File("saves/" + nickname + ".ser");
        return f.exists();
    }

    /** Devuelve el nickname del jugador actual (null si aún no se ha inicializado). */
    public String getPlayerNickname() {
        return player != null ? player.getNickname() : null;
    }

    /**
     * Devuelve todas las coordenadas ocupadas por los barcos de la máquina.
     * Usado para el modo debug/comprobación del profesor.
     */
    public java.util.List<com.example.batallanaval.model.Classes.Utils.Coordinate> getMachineShipCoordinates() {
        java.util.List<com.example.batallanaval.model.Classes.Utils.Coordinate> coords = new java.util.ArrayList<>();
        for (com.example.batallanaval.model.Interfaces.Vessel ship : mainBoard.getShips()) {
            coords.addAll(ship.getOccupiedCells());
        }
        return coords;
    }

    /**
     * Devuelve la lista completa de barcos de la máquina con sus datos de posición.
     * Usado para renderizar los modelos 3D en modo debug.
     */
    public java.util.List<com.example.batallanaval.model.AbstractsClasses.AbstractShip> getMachineShips() {
        java.util.List<com.example.batallanaval.model.AbstractsClasses.AbstractShip> result = new java.util.ArrayList<>();
        for (com.example.batallanaval.model.Interfaces.Vessel v : mainBoard.getShips()) {
            if (v instanceof com.example.batallanaval.model.AbstractsClasses.AbstractShip s) {
                result.add(s);
            }
        }
        return result;
    }
}



