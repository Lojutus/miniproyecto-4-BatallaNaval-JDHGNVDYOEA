package com.example.batallanaval.controller;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PointLight;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Rotate;
import javafx.animation.FadeTransition;
import javafx.util.Duration;


public class CameraController {

    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 35; // Ángulo inicial igual al de la vista

    // Conecta los movimientos del mouse para rotar el objeto 3D
    public void initRotationControls(Group cameraNode, Group root) {
        Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(35, Rotate.Y_AXIS);
        cameraNode.getTransforms().addAll(rotateX, rotateY);

        root.setOnMousePressed((MouseEvent event) -> {
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();
            anchorAngleX = rotateX.getAngle();
            anchorAngleY = rotateY.getAngle();
        });

        root.setOnMouseDragged((MouseEvent event) -> {
            rotateX.setAngle(anchorAngleX - (anchorY - event.getSceneY()));
            rotateY.setAngle(anchorAngleY + (anchorX - event.getSceneX()));
        });
    }

    // Conecta el clic del botón para simular la captura de una foto con el flash
    public void initShutterControl(Node shutterButton, PointLight pointLight) {
        shutterButton.setOnMouseClicked((MouseEvent event) -> {
            System.out.println("[📸 Model Update]: Foto capturada exitosamente.");

            // Simular destello aumentando temporalmente la intensidad de la luz
            Color originalColor = (Color) pointLight.getColor();
            pointLight.setColor(Color.WHITE);

            FadeTransition flashAnimation = new FadeTransition(Duration.millis(150));
            flashAnimation.setOnFinished(e -> pointLight.setColor(originalColor));
            flashAnimation.play();
        });
    }
}

