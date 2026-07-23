package com.example.batallanaval.controller.helpers;

import com.example.batallanaval.model.Classes.Boards.Board;
import com.example.batallanaval.model.Classes.Utils.CellState;
import com.example.batallanaval.model.Classes.Utils.Coordinate;
import com.example.batallanaval.model.Classes.Utils.ShotResult;
import com.example.batallanaval.model.Interfaces.BoardListener;

public class BoardViewUptader implements BoardListener {
    Board boardToUpdate; // AQUi debe de haber algun tipo de arreglo que referencia a lo que sea que represente la celda en la vista
    BoardViewUptader(Board boardToUpdate) {
        this.boardToUpdate = boardToUpdate;
    }
    @Override
    public void onCellsUpdate(Coordinate coordinate, CellState newState) {
        //Y aqui actualizar especificamente el objeto
    }
}
