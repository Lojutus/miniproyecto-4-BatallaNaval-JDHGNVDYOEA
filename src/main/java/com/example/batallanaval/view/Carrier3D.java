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
 * Portaaviones 3D — ocupa 5 casillas.
 * Casco largo y ancho con pista de aterrizaje en la cubierta.
 */
public class Carrier3D extends Application {

    public static Group createModel(int size, Orientation orientation) {
        PhongMaterial matCasco   = new PhongMaterial(Color.web("#4a4a6a")); // gris azulado
        PhongMaterial matCubierta = new PhongMaterial(Color.web("#8d8d9a"));
        PhongMaterial matIsla    = new PhongMaterial(Color.web("#2e2e3e"));
        PhongMaterial matPista   = new PhongMaterial(Color.web("#222222"));
        PhongMaterial matChimenea = new PhongMaterial(Color.DARKGRAY);

        // Casco principal — largo y con cubierta plana
        Box casco = new Box(260, 30, 90);
        casco.setMaterial(matCasco);
        casco.setTranslateY(10);

        // Cubierta plana encima
        Box cubierta = new Box(260, 6, 90);
        cubierta.setMaterial(matCubierta);
        cubierta.setTranslateY(-8);

        // Pista de aterrizaje (franja central)
        Box pista = new Box(220, 2, 20);
        pista.setMaterial(matPista);
        pista.setTranslateY(-12);
        pista.setTranslateX(-10);

        // Isla de mando (superestructura lateral derecha)
        Box isla = new Box(45, 40, 22);
        isla.setMaterial(matIsla);
        isla.setTranslateY(-28);
        isla.setTranslateX(80);
        isla.setTranslateZ(-28);

        // Torre de control encima de la isla
        Box torre = new Box(20, 20, 12);
        torre.setMaterial(matIsla);
        torre.setTranslateY(-48);
        torre.setTranslateX(80);
        torre.setTranslateZ(-28);

        // Chimeneas
        Cylinder ch1 = new Cylinder(4, 22);
        ch1.setMaterial(matChimenea);
        ch1.setTranslateY(-58);
        ch1.setTranslateX(70);
        ch1.setTranslateZ(-28);

        Cylinder ch2 = new Cylinder(4, 18);
        ch2.setMaterial(matChimenea);
        ch2.setTranslateY(-55);
        ch2.setTranslateX(85);
        ch2.setTranslateZ(-28);

        Group model = new Group(casco, cubierta, pista, isla, torre, ch1, ch2);

        // Escalar: casco base 260u → size * STEP
        double targetLength = size * Board3D.STEP;
        double scaleFactor  = targetLength / 260.0;
        double scaleXZ      = Math.min(scaleFactor, Board3D.CELL / 90.0);
        model.getTransforms().add(new Scale(scaleFactor, scaleXZ, scaleXZ, 0, 0, 0));

        if (orientation == Orientation.VERTICAL) {
            model.getTransforms().add(new Rotate(90, Rotate.Y_AXIS));
        }

        return model;
    }

    // ── Demo standalone ───────────────────────────────────────────────────────

    @Override
    public void start(Stage stage) {
        Group model = createModel(5, Orientation.HORINZONTAL);
        model.setRotate(30);
        model.setRotationAxis(Rotate.Y_AXIS);

        AmbientLight ambient = new AmbientLight(Color.rgb(80, 90, 110));
        PointLight   light   = new PointLight(Color.WHITE);
        light.setTranslateX(200); light.setTranslateY(-300); light.setTranslateZ(-300);

        Group root = new Group(model, ambient, light);
        Scene scene = new Scene(root, 800, 600, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.web("#0a1a2e"));

        PerspectiveCamera cam = new PerspectiveCamera(true);
        cam.setNearClip(0.1); cam.setFarClip(2000); cam.setTranslateZ(-700);
        scene.setCamera(cam);

        stage.setTitle("Carrier 3D"); stage.setScene(scene); stage.show();
    }

    public static void main(String[] args) { launch(args); }
}
