package com.example.batallanaval.model.AbstractsClasses;

import com.example.batallanaval.model.Classes.Boards.Board;
import com.example.batallanaval.model.Classes.Utils.Coordinate;
import com.example.batallanaval.model.Exceptions.OutOfBoardException;
import com.example.batallanaval.model.Exceptions.OverlappingShipException;

public abstract class AbstractPlayer {
    protected String nickname;
    protected Board ownBoard;
    protected Board enemyBoard;
    protected int enemyShipSunk;

    public AbstractPlayer(String nickname, Board ownBoard, Board enemyBoard){
        this.nickname = nickname;
        this.ownBoard = ownBoard;
        this.enemyBoard = enemyBoard;
        enemyShipSunk = 0;
    }

    public String getNickname() {
        return nickname;
    }

    public Board getOwnBoard() {
        return ownBoard;
    }

    public Board getEnemyBoard() {
        return enemyBoard;
    }

    public int getEnemyShipSunk() {
        return enemyShipSunk;
    }

    public void setEnemyShipSunk(){
        enemyShipSunk++;
    }

    public abstract void placeShips() throws OutOfBoardException, OverlappingShipException;

    public abstract Coordinate chooseShot(Coordinate selection);

}
