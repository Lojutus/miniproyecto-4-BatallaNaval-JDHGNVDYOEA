package com.example.batallanaval.model.Classes.Players;

import com.example.batallanaval.model.AbstractsClasses.AbstractPlayer;
import com.example.batallanaval.model.Classes.Boards.Board;
import com.example.batallanaval.model.Classes.FactoryMethod.ShipFactory;
import com.example.batallanaval.model.Classes.Utils.Coordinate;
import com.example.batallanaval.model.Classes.Utils.Orientation;
import com.example.batallanaval.model.Exceptions.OutOfBoardException;
import com.example.batallanaval.model.Exceptions.OverlappingShipException;
import com.example.batallanaval.model.Interfaces.Vessel;

import java.util.List;
import java.util.Random;

public class Machine extends AbstractPlayer {
    private Random random;

    public Machine(String nickname, Board ownBoard, Board enemyBoard) {
        super(nickname, ownBoard, enemyBoard);
        this.random = new Random();
    }

    @Override
    public void placeShips() throws OutOfBoardException, OverlappingShipException {
        List<Integer> sizes = ShipFactory.getFleetSizes();
        for (int size : sizes) {
            placeShipRandomly(size);
        }
    }

    private void placeShipRandomly(int size) throws OutOfBoardException, OverlappingShipException {
        boolean placed = false;
        while (!placed) {
            try {
                int row = random.nextInt(10);
                int col = random.nextInt(10);
                Orientation orientation = random.nextBoolean() ? Orientation.HORINZONTAL : Orientation.VERTICAL;
                Vessel ship = ShipFactory.createShip(size, new Coordinate(row, col), orientation);
                ownBoard.placeShip(ship);
                placed = true;
            } catch (OutOfBoardException | OverlappingShipException e) {
                    System.out.println("No se pudo colocar el barco");
            }
        }
    }

    @Override
    public Coordinate chooseShot(Coordinate selection) {
        Coordinate coordinate;
        do {
            int row = random.nextInt(10);
            int col = random.nextInt(10);
            coordinate = new Coordinate(row, col);
        } while (enemyBoard.wasAlreadyShot(coordinate));
        return coordinate;
    }
}
