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
import java.util.Collections;
import java.util.List;

/**
 * AbstractBoard provides a common board implementation for battleship-style games.
 *
 * <p>This class stores the board cell states, placed vessels and manages shooting
 * logic, validation and listener notification. Subclasses may extend behavior
 * for specific rules or persistence needs.</p>
 */
public abstract class AbstractBoard implements Shootable, java.io.Serializable {
    private static final long serialVersionUID = 1L;
    /** The fixed board dimension (rows and columns). */
    protected static final int SIZE = 10;

    /** 2D array representing the state of each board cell. */
    protected CellState[][] board;
    /** List of vessels currently placed on the board. */
    protected List<Vessel> ships;
    /** Transient listeners interested in board updates (not serialized). */
    protected transient List<BoardListener> listeners;

    /**
     * Constructs a new empty board and initializes internal structures.
     */
    public AbstractBoard() {
        this.board = new CellState[SIZE][SIZE];
        this.ships = new ArrayList<>();
        this.listeners = new ArrayList<>();
        initializeBoard();
    }

    /** Initialize every cell in the board to {@link CellState#WATER}. */
    private void initializeBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                board[row][col] = CellState.WATER;
            }
        }
    }
    /**
     * Validates that the given coordinate lies inside the board bounds.
     *
     * @param c coordinate to validate
     * @throws OutOfBoardException if the coordinate is outside the board
     */
    private void validateCoordinate(Coordinate c) throws OutOfBoardException {
        if (!c.isOutOfBoard(SIZE, SIZE)) {
            throw new OutOfBoardException("Coordinate out of range: " + c);
        }
    }

    /**
     * Ensures the target cell is not already occupied by a ship.
     *
     * @param c coordinate to check
     * @throws OverlappingShipException if a ship already occupies the cell
     */
    private void validateNotOccupied(Coordinate c) throws OverlappingShipException {
        if (board[c.getPosX()][c.getPosY()] == CellState.SHIP) {
            throw new OverlappingShipException("Cell already occupied: " + c);
        }
    }
    /**
     * Validates that the coordinate has not been previously shot at.
     *
     * @param c coordinate to check
     * @throws InvalidShotException if the cell has already been targeted
     */
    private void validateNotAlreadyShot(Coordinate c) throws InvalidShotException {
        CellState state = board[c.getPosX()][c.getPosY()];
        if (state == CellState.HIT || state == CellState.FAIL || state == CellState.SUNK) {
            throw new InvalidShotException("Cell has already been shot: " + c);
        }
    }

    /**
     * Performs a shot at the given coordinate and returns the result.
     *
     * <p>This method validates the coordinate and whether the cell was
     * previously shot, delegates to a vessel if present, updates the cell
     * state and notifies listeners of the change.</p>
     *
     * @param coordinate target coordinate
     * @return result of the shot ({@link ShotResult})
     * @throws OutOfBoardException if the coordinate is outside bounds
     * @throws InvalidShotException if the cell was already shot
     */
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

    /**
     * Places a vessel on the board after validating all occupied cells.
     *
     * @param ship vessel to place
     * @throws OutOfBoardException if any occupied cell is outside the board
     * @throws OverlappingShipException if any occupied cell is already used
     */
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

    /**
     * Returns the vessel occupying the given coordinate, or null if none.
     *
     * @param c coordinate to inspect
     * @return vessel at coordinate or null
     */
    private Vessel findShipAt(Coordinate c) {
        for (Vessel ship : ships) {
            if (ship.getOccupiedCells().contains(c)) {
                return ship;
            }
        }
        return null;
    }

    /**
     * Updates the internal cell state according to the shot result.
     *
     * @param coor target coordinate
     * @param shot result of the shot
     */
    private void updateCell(Coordinate coor, ShotResult shot){
        CellState newState = (shot == ShotResult.WATER) ? CellState.FAIL
                : (shot == ShotResult.HIT) ? CellState.HIT : CellState.SUNK;
        board[coor.getPosX()][coor.getPosY()] = newState;
    }

    /**
     * Registers a {@link BoardListener} to receive cell update events.
     *
     * @param listener listener to add
     */
    public void addListener(BoardListener listener) {
        if(listeners == null) { listeners = new ArrayList<BoardListener>(); }
        listeners.add(listener);
    }

    /**
     * Removes a previously registered {@link BoardListener}.
     *
     * @param listener listener to remove
     */
    public void removeListener(BoardListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies all registered listeners about a cell state change.
     *
     * @param c coordinate that changed
     * @param state new cell state
     */
    private void notifyListeners(Coordinate c, CellState state) {
        for (BoardListener l : listeners) {
            if (l != null) l.onCellsUpdate(c, state);
        }
    }

    /**
     * Checks whether all placed ships are sunk.
     *
     * @return true if every ship reports sunk, false otherwise
     */
    public boolean allShipsSunk(){
        for (Vessel ship : ships) {
            if (!ship.isSunk()) {
                return false;
            }
        }
        return true;
    }
    /**
     * Returns whether the specified coordinate has been previously targeted.
     *
     * @param c coordinate to check
     * @return true if the cell has been shot before
     */
    public boolean wasAlreadyShot(Coordinate c){
        CellState state = board[c.getPosX()][c.getPosY()];
        return state == CellState.HIT || state == CellState.FAIL || state == CellState.SUNK;
    }

    /**
     * Exposes an unmodifiable view of the placed ships. Intended for debugging
     * or external inspection only.
     *
     * @return unmodifiable list of vessels placed on the board
     */
    public java.util.List<Vessel> getShips() {
        return java.util.Collections.unmodifiableList(ships);
    }

}
