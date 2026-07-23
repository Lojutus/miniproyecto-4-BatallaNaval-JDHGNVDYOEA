package com.example.batallanaval.view;

import javafx.application.Application;

/**
 * Entry point para ejecutar BattleView3D.
 * Esta clase intermedia es necesaria porque en entornos modulares JavaFX
 * no permite llamar Application.launch() desde una clase que extiende Application.
 */
public class BattleView3DLauncher {
    public static void main(String[] args) {
        Application.launch(BattleView3D.class, args);
    }
}
