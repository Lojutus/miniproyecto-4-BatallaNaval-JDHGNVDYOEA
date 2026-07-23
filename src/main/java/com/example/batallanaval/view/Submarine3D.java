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
 * Modelo 3D de un submarino.
 * Usado para tamaños 3 (Submarine).
 *
 * createModel() devuelve un Group listo para colocarse sobre Board3D:
 *  - Escalado para que ocupe exactamente (size * STEP) en el eje elegido.
 *  - Origen en el centro geométrico del cuerpo principal.
 */
public class Submarine3D extends Application {

    // ── Fábrica reutilizable ──────────────────────────────────────────────────

    /**
     * @param size        número de celdas que ocupa el submarino (típicamente 3)
     * @param orientation HORIZONTAL o VERTICAL
     * @return Group con la geometría escalada y orientada, listo para añadir a Board3D
     */
    public static Group createModel(int size, Orientation orientation) {
        PhongMaterial bodyMat   = new PhongMaterial(Color.GOLD);
        PhongMaterial detailMat = new PhongMaterial(Color.DARKSLATEGRAY);

        // Geometría base: el cuerpo principal mide 180 unidades de largo (eje X, vía Rotate Z)
        Cylinder body = new Cylinder(35, 180);
        body.setMaterial(bodyMat);
        body.setRotationAxis(Rotate.Z_AXIS);
        body.setRotate(90);

        Sphere bow = new Sphere(35);
        bow.setMaterial(bodyMat);
        bow.setTranslateX(90);

        Sphere stern = new Sphere(35);
        stern.setMaterial(bodyMat);
        stern.setTranslateX(-90);

        Box tower = new Box(40, 45, 25);
        tower.setMaterial(bodyMat);
        tower.setTranslateY(-45);
        tower.setTranslateX(15);

        Cylinder periscope = new Cylinder(3, 25);
        periscope.setMaterial(detailMat);
        periscope.setTranslateY(-75);
        periscope.setTranslateX(25);

        Box horizontalFin = new Box(30, 5, 80);
        horizontalFin.setMaterial(detailMat);
        horizontalFin.setTranslateX(-80);

        Box verticalFin = new Box(30, 60, 5);
        verticalFin.setMaterial(detailMat);
        verticalFin.setTranslateX(-80);

        Group model = new Group(body, bow, stern, tower, periscope, horizontalFin, verticalFin);

        // Escalar para que el largo total (~360 u: 180 cuerpo + 2×90 esferas) quede en size*STEP
        double totalLength  = 360.0;
        double targetLength = size * Board3D.STEP;
        double scaleFactor  = targetLength / totalLength;
        double scaleXZ      = Math.min(scaleFactor, (Board3D.CELL) / 80.0);

        model.getTransforms().add(new Scale(scaleFactor, scaleXZ, scaleXZ, 0, 0, 0));

        // Rotar 90° en Y si es vertical
        if (orientation == Orientation.VERTICAL) {
            model.getTransforms().add(new Rotate(90, Rotate.Y_AXIS));
        }

        return model;
    }

    // ── Standalone demo ──────────────────────────────────────────────────────

    @Override
    public void start(Stage primaryStage) {
        Group submarine = createModel(3, Orientation.HORINZONTAL);
        submarine.setRotationAxis(Rotate.Y_AXIS);
        submarine.setRotate(40);

        AmbientLight ambientLight = new AmbientLight(Color.rgb(60, 70, 90));
        PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateX(200);
        pointLight.setTranslateY(-250);
        pointLight.setTranslateZ(-300);

        Group root = new Group(submarine, ambientLight, pointLight);
        Scene scene = new Scene(root, 800, 600, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.web("#0e2f44"));

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(1000.0);
        camera.setTranslateZ(-550);
        scene.setCamera(camera);

        primaryStage.setTitle("3D Submarine in JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
