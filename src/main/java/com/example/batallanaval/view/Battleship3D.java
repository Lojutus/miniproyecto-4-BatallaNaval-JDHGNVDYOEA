package com.example.batallanaval.view;

import com.example.batallanaval.model.Classes.Utils.Orientation;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

/**
 * Acorazado 3D — ocupa 4 casillas.
 * Casco alto con torretas de cañones en proa y popa.
 */
public class Battleship3D extends Application {

    public static Group createModel(int size, Orientation orientation) {
        PhongMaterial matCasco   = new PhongMaterial(Color.web("#3a3a4a")); // gris oscuro
        PhongMaterial matTorreta = new PhongMaterial(Color.web("#2a2a3a"));
        PhongMaterial matCanon   = new PhongMaterial(Color.web("#1a1a1a"));
        PhongMaterial matPuente  = new PhongMaterial(Color.web("#55556a"));

        // Casco principal — más alto y robusto que el barco normal
        Box casco = new Box(210, 44, 70);
        casco.setMaterial(matCasco);
        casco.setTranslateY(10);

        // Superestructura central (puente)
        Box puente = new Box(70, 35, 50);
        puente.setMaterial(matPuente);
        puente.setTranslateY(-26);
        puente.setTranslateX(10);

        // Torreta delantera
        Box torreta1 = new Box(28, 18, 28);
        torreta1.setMaterial(matTorreta);
        torreta1.setTranslateY(-28);
        torreta1.setTranslateX(70);

        // Cañones de la torreta delantera
        Cylinder canon1a = new Cylinder(3, 50);
        canon1a.setMaterial(matCanon);
        canon1a.setRotationAxis(Rotate.Z_AXIS);
        canon1a.setRotate(90);
        canon1a.setTranslateX(95);
        canon1a.setTranslateY(-30);
        canon1a.setTranslateZ(-8);

        Cylinder canon1b = new Cylinder(3, 50);
        canon1b.setMaterial(matCanon);
        canon1b.setRotationAxis(Rotate.Z_AXIS);
        canon1b.setRotate(90);
        canon1b.setTranslateX(95);
        canon1b.setTranslateY(-30);
        canon1b.setTranslateZ(8);

        // Torreta trasera
        Box torreta2 = new Box(28, 18, 28);
        torreta2.setMaterial(matTorreta);
        torreta2.setTranslateY(-28);
        torreta2.setTranslateX(-70);

        Cylinder canon2a = new Cylinder(3, 50);
        canon2a.setMaterial(matCanon);
        canon2a.setRotationAxis(Rotate.Z_AXIS);
        canon2a.setRotate(90);
        canon2a.setTranslateX(-95);
        canon2a.setTranslateY(-30);
        canon2a.setTranslateZ(-8);

        Cylinder canon2b = new Cylinder(3, 50);
        canon2b.setMaterial(matCanon);
        canon2b.setRotationAxis(Rotate.Z_AXIS);
        canon2b.setRotate(90);
        canon2b.setTranslateX(-95);
        canon2b.setTranslateY(-30);
        canon2b.setTranslateZ(8);

        // Chimenea central
        Cylinder chimenea = new Cylinder(7, 30);
        chimenea.setMaterial(matCanon);
        chimenea.setTranslateY(-52);
        chimenea.setTranslateX(10);

        Group model = new Group(casco, puente,
                torreta1, canon1a, canon1b,
                torreta2, canon2a, canon2b,
                chimenea);

        // Escalar: base 210u → size * STEP
        double targetLength = size * Board3D.STEP;
        double scaleFactor  = targetLength / 210.0;
        double scaleXZ      = Math.min(scaleFactor, Board3D.CELL / 70.0);
        model.getTransforms().add(new Scale(scaleFactor, scaleXZ, scaleXZ, 0, 0, 0));

        if (orientation == Orientation.VERTICAL) {
            model.getTransforms().add(new Rotate(90, Rotate.Y_AXIS));
        }

        return model;
    }

    // ── Demo standalone ───────────────────────────────────────────────────────

    @Override
    public void start(Stage stage) {
        Group model = createModel(4, Orientation.HORINZONTAL);
        model.setRotate(30);
        model.setRotationAxis(Rotate.Y_AXIS);

        AmbientLight ambient = new AmbientLight(Color.rgb(70, 80, 100));
        PointLight   light   = new PointLight(Color.WHITE);
        light.setTranslateX(200); light.setTranslateY(-300); light.setTranslateZ(-300);

        Group root = new Group(model, ambient, light);
        Scene scene = new Scene(root, 800, 600, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.web("#0a1a2e"));

        PerspectiveCamera cam = new PerspectiveCamera(true);
        cam.setNearClip(0.1); cam.setFarClip(2000); cam.setTranslateZ(-600);
        scene.setCamera(cam);

        stage.setTitle("Battleship 3D"); stage.setScene(scene); stage.show();
    }

    public static void main(String[] args) { launch(args); }
}
