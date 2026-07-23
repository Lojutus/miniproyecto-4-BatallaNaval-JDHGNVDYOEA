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
 * Modelo 3D de un barco de superficie.
 * Usado para tamaños 2 (Destroyer) y 4 (AircraftCarrier).
 *
 * createModel() devuelve un Group listo para colocarse sobre Board3D:
 *  - Escalado para que ocupe exactamente (size * STEP) en el eje elegido.
 *  - Origen en el centro de la celda [0,0] del barco.
 */
public class Ship3D extends Application {

    // ── Fábrica reutilizable ──────────────────────────────────────────────────

    /**
     * @param size        número de celdas que ocupa el barco (1–4)
     * @param orientation HORIZONTAL o VERTICAL
     * @return Group con la geometría escalada y orientada, listo para añadir a Board3D
     */
    public static Group createModel(int size, Orientation orientation) {
        PhongMaterial matCasco    = new PhongMaterial(Color.DARKRED);
        PhongMaterial matCabina   = new PhongMaterial(Color.web("#d0d0d0"));
        PhongMaterial matChimenea = new PhongMaterial(Color.DARKSLATEGRAY);

        // Geometría base: el casco mide 200 unidades de largo (eje X), 40 alto, 80 profundo
        Box casco = new Box(200, 40, 80);
        casco.setMaterial(matCasco);
        casco.setTranslateY(20);

        Box cabina = new Box(100, 50, 60);
        cabina.setMaterial(matCabina);
        cabina.setTranslateY(-25);
        cabina.setTranslateX(-10);

        Cylinder chimenea = new Cylinder(10, 40);
        chimenea.setMaterial(matChimenea);
        chimenea.setTranslateY(-70);
        chimenea.setTranslateX(-20);

        Group model = new Group(casco, cabina, chimenea);

        // Escalar para que el largo (200 u) encaje en (size * STEP) píxeles del tablero
        double targetLength = size * Board3D.STEP;
        double scaleFactor  = targetLength / 200.0;
        // Alto/ancho se escalan proporcionalmente pero capped para no sobresalir de la celda
        double scaleXZ = Math.min(scaleFactor, (Board3D.CELL) / 80.0);

        model.getTransforms().add(new Scale(scaleFactor, scaleXZ, scaleXZ, 0, 0, 0));

        // Rotar 90° en Y si es vertical (el modelo apunta en X por defecto)
        if (orientation == Orientation.VERTICAL) {
            model.getTransforms().add(new Rotate(90, Rotate.Y_AXIS));
        }

        return model;
    }

    // ── Standalone demo ──────────────────────────────────────────────────────

    @Override
    public void start(Stage primaryStage) {
        Group barco = createModel(3, Orientation.HORINZONTAL);
        barco.setRotate(35);
        barco.setRotationAxis(Rotate.Y_AXIS);

        AmbientLight luzAmbiental = new AmbientLight(Color.rgb(80, 80, 80));
        PointLight luzPuntual = new PointLight(Color.WHITE);
        luzPuntual.setTranslateX(150);
        luzPuntual.setTranslateY(-200);
        luzPuntual.setTranslateZ(-300);

        Group root = new Group(barco, luzAmbiental, luzPuntual);
        Scene scene = new Scene(root, 800, 600, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.LIGHTBLUE);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(1000.0);
        camera.setTranslateZ(-600);
        scene.setCamera(camera);

        primaryStage.setTitle("Barco 3D en JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
