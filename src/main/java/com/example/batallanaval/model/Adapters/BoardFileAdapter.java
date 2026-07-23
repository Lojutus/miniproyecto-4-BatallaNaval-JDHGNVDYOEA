package com.example.batallanaval.model.Adapters;

import com.example.batallanaval.model.Classes.Boards.Board;
import com.example.batallanaval.model.Classes.GameState.GameState;
import com.example.batallanaval.model.Exceptions.GameLoadableException;
import com.example.batallanaval.model.Interfaces.Loadable;

import java.io.*;

public class BoardFileAdapter implements Loadable {

    private static final String SAVE_DIRECTORY = "controller/helpers/saves/";
    private static final String BOARD_EXTENSION = ".ser";
    private static final String INFO_EXTENSION = ".txt";

    @Override
    public void save(GameState state) throws GameLoadableException {
        saveBoard(state);
        saveInfo(state);
    }

    private void saveBoard(GameState state) throws GameLoadableException {
        String path = SAVE_DIRECTORY + state.getNickname() + BOARD_EXTENSION;
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(state.getPositionBoard());
            out.writeObject(state.getMainBoard());
        } catch (IOException e) {
            throw new GameLoadableException("Error guardando el tablero: " + e.getMessage());
        }
    }

    private void saveInfo(GameState state) throws GameLoadableException {
        String path = SAVE_DIRECTORY + state.getNickname() + INFO_EXTENSION;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(state.getNickname());
            writer.newLine();
            writer.write(String.valueOf(state.getEnemyShipsSunk()));
        } catch (IOException e) {
            throw new GameLoadableException("Error guardando la informacion: " + e.getMessage());
        }
    }

    @Override
    public GameState load(String nickname) throws GameLoadableException {
        Board[] boards = loadBoard(nickname);
        int enemyShipsSunk = loadInfo(nickname);
        return new GameState(nickname, boards[0], boards[1], enemyShipsSunk);
    }

    private Board[] loadBoard(String nickname) throws GameLoadableException {
        String path = SAVE_DIRECTORY + nickname + BOARD_EXTENSION;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
            Board ownBoard = (Board) in.readObject();
            Board enemyBoard = (Board) in.readObject();
            return new Board[] { ownBoard, enemyBoard };
        } catch (IOException | ClassNotFoundException e) {
            throw new GameLoadableException("Error cargando el tablero: " + e.getMessage());
        }
    }

    private int loadInfo(String nickname) throws GameLoadableException {
        String path = SAVE_DIRECTORY + nickname + INFO_EXTENSION;
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            reader.readLine(); // nickname, ya lo tenemos como parámetro
            String sunkLine = reader.readLine();
            return Integer.parseInt(sunkLine);
        } catch (IOException e) {
            throw new GameLoadableException("Eror cargando la informacion: " + e.getMessage());
        }
    }
}
