package com.example.batallanaval;

import com.example.batallanaval.view.MainMenuView;
import javafx.application.Application;

/**
 * Entry point principal del juego.
 * Abre el menú principal 2D (MainMenuView) que luego lanza BattleView3D.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(MainMenuView.class, args);
    }
}
