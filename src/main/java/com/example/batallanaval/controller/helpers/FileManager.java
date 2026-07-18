package com.example.batallanaval.controller.helpers;

import com.example.batallanaval.model.Adapters.BoardFileAdapter;

public class FileManager extends BoardFileAdapter {

     public FileManager(){

     }
    /**
     * Holder for the lazy-initialized singleton instance (initialization-on-demand holder idiom).
     */
    private static class GameHolder {
        private static FileManager INSTANCE;
    }

    /**
     * Retrieves the single instance of GameStage (Singleton pattern).
     *
     * If the instance does not exist, it creates one by initializing a new GameStage.
     *
     * @return the single instance of FileManager
     *
     */
    public static FileManager getInstance(){
        if(GameHolder.INSTANCE == null){
            GameHolder.INSTANCE = new FileManager();
        }

        return GameHolder.INSTANCE;
    }
}
