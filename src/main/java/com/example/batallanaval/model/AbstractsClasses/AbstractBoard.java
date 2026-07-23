package com.example.batallanaval.model.AbstractsClasses;

import com.example.batallanaval.model.Classes.Utils.CellState;
import com.example.batallanaval.model.Classes.Utils.Coordinate;
import com.example.batallanaval.model.Classes.Utils.ShotResult;
import com.example.batallanaval.model.Exceptions.InvalidShotException;
import com.example.batallanaval.model.Exceptions.OutOfBoardException;
import com.example.batallanaval.model.Exceptions.OverlappingShipException;
import com.example.batallanaval.model.Interfaces.BoardListener;
import com.example.batallanaval.model.Interfaces.Shootable;
import com.example.batallanaval.model.Interfaces.Vessel;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBoard implements Shootable {
    protected static final int SIZE = 10;

    protected CellState[][] board;
    protected List<Vessel> ships;
    protected List<BoardListener> listeners;

    public AbstractBoard() {
        this.board = new CellState[SIZE][SIZE];
        this.ships = new ArrayList<>();
        this.listeners = new ArrayList<>();
        initializeBoard();
    }

    private void initializeBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                board[row][col] = CellState.WATER;
            }
        }
    }
    private void validateCoordinate(Coordinate c) throws OutOfBoardException {
        if (!c.isOutOfBoard(SIZE, SIZE)) {
            throw new OutOfBoardException("Coordenadas fuera de rango: " + c);
        }
    }

    private void validateNotOccupied(Coordinate c) throws OverlappingShipException {
        if (board[c.getPosX()][c.getPosY()] == CellState.SHIP) {
            throw new OverlappingShipException("Esta celda se encuentra ocupada " + c);
        }
    }
    private void validateNotAlreadyShot(Coordinate c) throws InvalidShotException {
        CellState state = board[c.getPosX()][c.getPosY()];
        if (state == CellState.HIT || state == CellState.FAIL || state == CellState.SUNK) {
            throw new InvalidShotException("Esta celda ya fue disparada " + c);
        }
    }

    @Override
    public ShotResult shoot(Coordinate coordinate) throws OutOfBoardException, InvalidShotException {
        validateCoordinate(coordinate);
        validateNotAlreadyShot(coordinate);

        Vessel target = findShipAt(coordinate);
        ShotResult result;

        if (target != null) {
            result = ((Shootable) target).shoot(coordinate);
        } else {
            result = ShotResult.WATER;
        }

        updateCell(coordinate, result);
        notifyListeners(coordinate, board[coordinate.getPosX()][coordinate.getPosY()]);
        return result;
    }

    public void placeShip(Vessel ship) throws OutOfBoardException, OverlappingShipException {
        for (Coordinate cell : ship.getOccupiedCells()) {
            validateCoordinate(cell);
            validateNotOccupied(cell);
        }
        for (Coordinate cell : ship.getOccupiedCells()) {
            board[cell.getPosX()][cell.getPosY()] = CellState.SHIP;
        }
        ships.add(ship);
    }

    private Vessel findShipAt(Coordinate c) {
        for (Vessel ship : ships) {
            if (ship.getOccupiedCells().contains(c)) {
                return ship;
            }
        }
        return null;
    }

    private void updateCell(Coordinate coor, ShotResult shot){
        CellState newState = (shot == ShotResult.WATER) ? CellState.FAIL
                : (shot == ShotResult.HIT) ? CellState.HIT : CellState.SUNK;
        board[coor.getPosX()][coor.getPosY()] = newState;
    }

    public void addListener(BoardListener listener) {
        listeners.add(listener);
    }

    public void removeListener(BoardListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(Coordinate c, CellState state) {
        for (BoardListener l : listeners) {
            if (l != null) l.onCellsUpdate(c, state);
        }
    }

    public boolean allShipsSunk(){
        for (Vessel ship : ships) {
            if (!ship.isSunk()) {
                return false;
            }
        }
        return true;
    }
    public boolean wasAlreadyShot(Coordinate c){
        CellState state = board[c.getPosX()][c.getPosY()];
        return state == CellState.HIT || state == CellState.FAIL || state == CellState.SUNK;
    }

    /** Expone la lista de barcos para comprobación externa (modo debug). */
    public java.util.List<Vessel> getShips() {
        return java.util.Collections.unmodifiableList(ships);
    }

}
