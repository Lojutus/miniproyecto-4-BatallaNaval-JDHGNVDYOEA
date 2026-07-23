package com.example.batallanaval.view;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.stage.Stage;
import com.example.batallanaval.controller.CameraController;

public class Camera3D extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Instanciar el Controlador de la arquitectura MVC
        CameraController controller = new CameraController();

        // Materiales
        PhongMaterial bodyMaterial = new PhongMaterial(Color.web("#2b2b2b"));
        PhongMaterial accentMaterial = new PhongMaterial(Color.SILVER);
        PhongMaterial lensMaterial = new PhongMaterial(Color.BLACK);
        PhongMaterial glassMaterial = new PhongMaterial(Color.LIGHTBLUE);
        PhongMaterial shutterMaterial = new PhongMaterial(Color.RED);

        // Componentes visuales
        Box mainBody = new Box(160, 100, 60);
        mainBody.setMaterial(bodyMaterial);

        Box topPlate = new Box(160, 15, 58);
        topPlate.setMaterial(accentMaterial);
        topPlate.setTranslateY(-50);

        Cylinder lensBarrel = new Cylinder(35, 40);
        lensBarrel.setMaterial(lensMaterial);
        lensBarrel.setRotationAxis(javafx.scene.transform.Rotate.X_AXIS);
        lensBarrel.setRotate(90);
        lensBarrel.setTranslateZ(-35);

        Cylinder lensGlass = new Cylinder(30, 2);
        lensGlass.setMaterial(glassMaterial);
        lensGlass.setRotationAxis(javafx.scene.transform.Rotate.X_AXIS);
        lensGlass.setRotate(90);
        lensGlass.setTranslateZ(-56);

        Cylinder shutterButton = new Cylinder(8, 12);
        shutterButton.setMaterial(shutterMaterial);
        shutterButton.setTranslateY(-62);
        shutterButton.setTranslateX(-50);

        Box flashMesh = new Box(30, 20, 15);
        flashMesh.setMaterial(accentMaterial);
        flashMesh.setTranslateY(-30);
        flashMesh.setTranslateX(50);
        flashMesh.setTranslateZ(-25);

        // Agrupación de la vista
        Group cameraNode = new Group(mainBody, topPlate, lensBarrel, lensGlass, shutterButton, flashMesh);

        // Luces
        AmbientLight ambientLight = new AmbientLight(Color.rgb(100, 100, 100));
        PointLight pointLight = new PointLight(Color.rgb(180, 180, 180));
        pointLight.setTranslateX(200);
        pointLight.setTranslateY(-200);
        pointLight.setTranslateZ(-300);

        Group root = new Group(cameraNode, ambientLight, pointLight);

        // --- VINCULACIÓN CON EL CONTROLADOR ---
        controller.initRotationControls(cameraNode, root);
        controller.initShutterControl(shutterButton, pointLight);
        // --------------------------------------

        // Escena y Cámara 3D del visor
        Scene scene = new Scene(root, 800, 600, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.web("#e0e0e0"));

        PerspectiveCamera sceneCamera = new PerspectiveCamera(true);
        sceneCamera.setNearClip(0.1);
        sceneCamera.setFarClip(1000.0);
        sceneCamera.setTranslateZ(-500);
        scene.setCamera(sceneCamera);

        primaryStage.setTitle("Interactive 3D Camera (MVC)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


