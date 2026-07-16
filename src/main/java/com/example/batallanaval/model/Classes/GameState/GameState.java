package com.example.batallanaval.model.Classes.GameState;

import com.example.batallanaval.model.Classes.Boards.Board;

import java.io.Serializable;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nickname;
    private Board positionBoard;
    private Board mainBoard;
    private int enemyShipsSunk;

    public GameState(String nickname, Board positionBoard, Board mainBoard, int enemyShipsSunk) {
        this.nickname = nickname;
        this.positionBoard = positionBoard;
        this.mainBoard = mainBoard;
        this.enemyShipsSunk = enemyShipsSunk;
    }

    public String getNickname() {
        return nickname;
    }

    public Board getPositionBoard() {
        return positionBoard;
    }

    public Board getMainBoard() {
        return mainBoard;
    }

    public int getEnemyShipsSunk() {
        return enemyShipsSunk;
    }
}
