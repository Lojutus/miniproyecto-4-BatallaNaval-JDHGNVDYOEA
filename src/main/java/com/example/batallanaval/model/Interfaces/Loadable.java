package com.example.batallanaval.model.Interfaces;

import com.example.batallanaval.model.Classes.GameState.GameState;
import com.example.batallanaval.model.Exceptions.GameLoadableException;

/**
 * Interface for saving and loading game state objects.
 */
public interface Loadable {
    /**
     * Persists the provided game state.
     *
     * @param gameState game state to save
     * @throws GameLoadableException when saving fails
     */
    void save(GameState gameState) throws GameLoadableException;

    /**
     * Loads a previously saved game state by player nickname.
     *
     * @param nickname player identifier used for lookup
     * @return loaded GameState
     * @throws GameLoadableException when loading fails or not found
     */
    GameState load(String nickname) throws GameLoadableException;
}
