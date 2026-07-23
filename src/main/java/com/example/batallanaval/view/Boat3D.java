package com.example.batallanaval.view;

import com.example.batallanaval.model.Classes.Utils.Orientation;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

/**
 * Bote 3D — ocupa 3 casillas.
 * Embarcación pequeña y ágil con proa puntiaguda.
 */
public class Boat3D extends Application {

    public static Group createModel(int size, Orientation orientation) {
        PhongMaterial matCasco  = new PhongMaterial(Color.web("#1b5e20")); // verde militar
        PhongMaterial matCabina = new PhongMaterial(Color.web("#a5d6a7"));
        PhongMaterial matMotor  = new PhongMaterial(Color.DARKGRAY);

        // Casco compacto
        Box casco = new Box(140, 28, 55);
        casco.setMaterial(matCasco);
        casco.setTranslateY(8);

        // Proa puntiaguda (esfera achatada)
        Sphere proa = new Sphere(27);
        proa.setMaterial(matCasco);
        proa.setTranslateX(70);
        proa.setScaleZ(0.9);
        proa.setScaleY(0.5);

        // Cabina pequeña
        Box cabina = new Box(45, 28, 40);
        cabina.setMaterial(matCabina);
        cabina.setTranslateY(-16);
        cabina.setTranslateX(-10);

        // Motor en popa
        Cylinder motor = new Cylinder(8, 20);
        motor.setMaterial(matMotor);
        motor.setTranslateX(-72);
        motor.setTranslateY(-2);
        motor.setRotationAxis(Rotate.Z_AXIS);
        motor.setRotate(90);

        Group model = new Group(casco, proa, cabina, motor);

        // Escalar: base 140u → size * STEP
        double targetLength = size * Board3D.STEP;
        double scaleFactor  = targetLength / 140.0;
        double scaleXZ      = Math.min(scaleFactor, Board3D.CELL / 55.0);
        model.getTransforms().add(new Scale(scaleFactor, scaleXZ, scaleXZ, 0, 0, 0));

        if (orientation == Orientation.VERTICAL) {
            model.getTransforms().add(new Rotate(90, Rotate.Y_AXIS));
        }

        return model;
    }

    // ── Demo standalone ───────────────────────────────────────────────────────

    @Override
    public void start(Stage stage) {
        Group model = createModel(3, Orientation.HORINZONTAL);
        model.setRotate(30);
        model.setRotationAxis(Rotate.Y_AXIS);

        AmbientLight ambient = new AmbientLight(Color.rgb(80, 100, 80));
        PointLight   light   = new PointLight(Color.WHITE);
        light.setTranslateX(150); light.setTranslateY(-200); light.setTranslateZ(-250);

        Group root = new Group(model, ambient, light);
        Scene scene = new Scene(root, 800, 600, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.web("#0e2a0e"));

        PerspectiveCamera cam = new PerspectiveCamera(true);
        cam.setNearClip(0.1); cam.setFarClip(2000); cam.setTranslateZ(-500);
        scene.setCamera(cam);

        stage.setTitle("Boat 3D"); stage.setScene(scene); stage.show();
    }

    public static void main(String[] args) { launch(args); }
}
