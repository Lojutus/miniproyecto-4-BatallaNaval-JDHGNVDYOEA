package com.example.batallanaval.view;

import javafx.application.Application;

/**
 * Entry point para ejecutar Camera3D de forma independiente.
 * Necesario porque JavaFX no permite llamar Application.launch()
 * desde una clase que extiende Application directamente en módulos.
 */
public class Camera3DLauncher {
    public static void main(String[] args) {
        Application.launch(Camera3D.class, args);
    }
}
