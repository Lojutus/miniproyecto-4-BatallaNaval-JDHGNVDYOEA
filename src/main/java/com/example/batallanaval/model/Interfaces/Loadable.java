package com.example.batallanaval.model.Interfaces;

import com.example.batallanaval.model.Classes.GameState.GameState;
import com.example.batallanaval.model.Exceptions.GameLoadableException;

public interface Loadable {
    void save(GameState gameState) throws GameLoadableException;
    GameState load(String nickname) throws GameLoadableException;
}
