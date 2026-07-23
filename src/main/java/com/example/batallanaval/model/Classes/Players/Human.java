package com.example.batallanaval.model.Classes.Players;

import com.example.batallanaval.model.AbstractsClasses.AbstractPlayer;
import com.example.batallanaval.model.Classes.Boards.Board;
import com.example.batallanaval.model.Classes.Utils.Coordinate;
import com.example.batallanaval.model.Exceptions.OutOfBoardException;
import com.example.batallanaval.model.Exceptions.OverlappingShipException;
import com.example.batallanaval.model.Interfaces.Vessel;

public class Human extends AbstractPlayer {
    public Human(String nickname, Board ownBoard, Board enemyBoard) {
        super(nickname, ownBoard, enemyBoard);
    }

    @Override
    public void placeShips() throws OutOfBoardException, OverlappingShipException {
       //Este metodo no se implementa, y se apoya en placeSIngleShip
    }

    public void placeSingleShip(Vessel ship) throws OutOfBoardException, OverlappingShipException {
        ownBoard.placeShip(ship);
    }

    @Override
    public Coordinate chooseShot(Coordinate selection) {
        return selection;

    }

}
