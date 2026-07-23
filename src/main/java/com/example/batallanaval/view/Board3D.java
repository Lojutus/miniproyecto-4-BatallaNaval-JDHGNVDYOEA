package com.example.batallanaval.view;

import com.example.batallanaval.model.Classes.Utils.CellState;
import com.example.batallanaval.model.Classes.Utils.Coordinate;
import javafx.animation.*;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.util.function.BiConsumer;

/**
 * Vista 3D de un tablero de Batalla Naval 10x10.
 *
 * Estados visuales:
 *  WATER   → azul liso
 *  SHIP    → gris (durante placement se usa markAsPlaced → verde)
 *  FAIL    → gris claro + marca X en 3D (agua, fallo)
 *  HIT     → rojo + animación de explosión de esferas naranjas
 *  SUNK    → rojo oscuro + explosión más grande
 */
public class Board3D extends Group {

    // ── Constantes de grilla ──────────────────────────────────────────────────
    public static final int   GRID        = 10;
    public static final float CELL        = 40f;
    public static final float GAP         = 2f;
    public static final float STEP        = CELL + GAP;
    public static final float CELL_HEIGHT = 8f;

    // ── Colores ───────────────────────────────────────────────────────────────
    private static final Color COLOR_WATER  = Color.web("#1a6fa8");
    private static final Color COLOR_SHIP   = Color.web("#607d8b");
    private static final Color COLOR_HIT    = Color.web("#e53935");
    private static final Color COLOR_SUNK   = Color.web("#880e0e");
    private static final Color COLOR_FAIL   = Color.web("#90a4ae");
    private static final Color COLOR_HOVER  = Color.web("#ffe082");
    private static final Color COLOR_PLACED = Color.web("#00c853");

    // ── Nodos ─────────────────────────────────────────────────────────────────
    private final Box[][]             cells     = new Box[GRID][GRID];
    private final PhongMaterial[][]   materials = new PhongMaterial[GRID][GRID];
    private final boolean[][]         placed    = new boolean[GRID][GRID];
    private final CellState[][]       state     = new CellState[GRID][GRID];

    // ── Callbacks ─────────────────────────────────────────────────────────────
    private BiConsumer<Integer, Integer> onCellClicked;
    private BiConsumer<Integer, Integer> onCellDoubleClicked;
    private BiConsumer<Integer, Integer> onCellHover;
    private boolean clickable = false;

    // ─────────────────────────────────────────────────────────────────────────
    // CONSTRUCTOR
    // ─────────────────────────────────────────────────────────────────────────

    public Board3D(String label, boolean showShips) {
        for (int r = 0; r < GRID; r++)
            for (int c = 0; c < GRID; c++)
                state[r][c] = CellState.WATER;

        buildGrid();
        addLabels(label);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CONSTRUCCIÓN DE GRILLA
    // ─────────────────────────────────────────────────────────────────────────

    private void buildGrid() {
        for (int row = 0; row < GRID; row++) {
            for (int col = 0; col < GRID; col++) {
                PhongMaterial mat = new PhongMaterial(COLOR_WATER);
                Box cell = new Box(CELL, CELL_HEIGHT, CELL);
                cell.setMaterial(mat);

                double offsetX = (col - GRID / 2.0 + 0.5) * STEP;
                double offsetZ = (row - GRID / 2.0 + 0.5) * STEP;
                cell.setTranslateX(offsetX);
                cell.setTranslateZ(offsetZ);

                final int r = row, c = col;
                cell.setOnMouseEntered(e -> {
                    if (clickable) mat.setDiffuseColor(COLOR_HOVER);
                    if (onCellHover != null) onCellHover.accept(r, c);
                });
                cell.setOnMouseExited(e -> {
                    if (clickable)
                        mat.setDiffuseColor(placed[r][c] ? COLOR_PLACED : colorFor(state[r][c]));
                });
                cell.setOnMouseClicked(e -> {
                    if (!clickable) return;
                    if (e.getClickCount() == 2 && onCellDoubleClicked != null)
                        onCellDoubleClicked.accept(r, c);
                    else if (e.getClickCount() == 1 && onCellClicked != null)
                        onCellClicked.accept(r, c);
                });

                cells[row][col]    = cell;
                materials[row][col] = mat;
                getChildren().add(cell);
            }
        }

        // Base del tablero
        double boardSize = GRID * STEP + GAP;
        Box base = new Box(boardSize, 4, boardSize);
        base.setMaterial(new PhongMaterial(Color.web("#0d3b60")));
        base.setTranslateY(CELL_HEIGHT / 2.0 + 2);
        getChildren().add(base);
    }

    private void addLabels(String titleText) {
        double half = GRID * STEP / 2.0;

        // Título del tablero — tumbado sobre el piso, encima de la base
        Text title = new Text(titleText);
        title.setFont(Font.font("Arial Bold", 16));
        title.setFill(Color.WHITE);
        title.getTransforms().add(new Rotate(-90, Rotate.X_AXIS));
        title.setTranslateX(-half);
        title.setTranslateY(CELL_HEIGHT / 2.0 + 3);   // sobre la superficie de la base
        title.setTranslateZ(-half - 28);
        getChildren().add(title);

        for (int i = 0; i < GRID; i++) {
            double offset = (i - GRID / 2.0 + 0.5) * STEP;

            // Letras de columna (A–J) — franja delantera del tablero
            Text col = new Text(String.valueOf((char)('A' + i)));
            col.setFont(Font.font("Arial", 11));
            col.setFill(Color.LIGHTCYAN);
            col.getTransforms().add(new Rotate(-90, Rotate.X_AXIS));
            col.setTranslateX(offset - 5);
            col.setTranslateY(CELL_HEIGHT / 2.0 + 3);  // ras del suelo
            col.setTranslateZ(-half - 16);
            getChildren().add(col);

            // Números de fila (1–10) — franja lateral del tablero
            Text row = new Text(String.valueOf(i + 1));
            row.setFont(Font.font("Arial", 11));
            row.setFill(Color.LIGHTCYAN);
            row.getTransforms().add(new Rotate(-90, Rotate.X_AXIS));
            row.setTranslateX(-half - 20);
            row.setTranslateY(CELL_HEIGHT / 2.0 + 3);  // ras del suelo
            row.setTranslateZ(offset - 4);
            getChildren().add(row);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ACTUALIZACIÓN DE CELDAS
    // ─────────────────────────────────────────────────────────────────────────

    /** Actualiza color + efecto visual según el nuevo estado. */
    public void updateCell(Coordinate coord, CellState newState) {
        int r = coord.getPosX();
        int c = coord.getPosY();
        if (r < 0 || r >= GRID || c < 0 || c >= GRID) return;

        state[r][c] = newState;
        materials[r][c].setDiffuseColor(colorFor(newState));

        switch (newState) {
            case FAIL -> placeXMarker(r, c);
            case HIT  -> playExplosion(r, c, false);
            case SUNK -> playExplosion(r, c, true);
        }
    }

    public void refreshAll(CellState[][] grid) {
        for (int r = 0; r < GRID; r++)
            for (int c = 0; c < GRID; c++) {
                state[r][c] = grid[r][c];
                materials[r][c].setDiffuseColor(colorFor(grid[r][c]));
            }
    }

    public void markAsPlaced(int row, int col) {
        if (row < 0 || row >= GRID || col < 0 || col >= GRID) return;
        placed[row][col] = true;
        materials[row][col].setDiffuseColor(COLOR_PLACED);
    }

    private static Color colorFor(CellState s) {
        return switch (s) {
            case WATER -> COLOR_WATER;
            case SHIP  -> COLOR_SHIP;
            case HIT   -> COLOR_HIT;
            case SUNK  -> COLOR_SUNK;
            case FAIL  -> COLOR_FAIL;
        };
    }

    // ─────────────────────────────────────────────────────────────────────────
    // EFECTOS VISUALES
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * FAIL (agua): coloca una X hecha de dos cilindros delgados sobre la celda.
     * La X es permanente.
     */
    private void placeXMarker(int r, int c) {
        double cx = (c - GRID / 2.0 + 0.5) * STEP;
        double cz = (r - GRID / 2.0 + 0.5) * STEP;
        double cy = -CELL_HEIGHT - 2;

        PhongMaterial mat = new PhongMaterial(Color.WHITE);
        mat.setSpecularColor(Color.LIGHTGRAY);

        // Brazo 1 (45°)
        Cylinder arm1 = new Cylinder(2, CELL * 0.75);
        arm1.setMaterial(mat);
        arm1.setRotationAxis(Rotate.Y_AXIS);
        arm1.setRotate(45);
        arm1.setRotationAxis(Rotate.Z_AXIS);
        arm1.setRotate(90);

        // Brazo 2 (-45°)
        Cylinder arm2 = new Cylinder(2, CELL * 0.75);
        arm2.setMaterial(mat);
        arm2.setRotationAxis(Rotate.Z_AXIS);
        arm2.setRotate(90);

        Group xGroup = new Group(arm1, arm2);

        // Rotar el grupo para que ambos brazos formen X vista desde arriba
        Rotate yRot1 = new Rotate(45,  Rotate.Y_AXIS);
        Rotate yRot2 = new Rotate(-45, Rotate.Y_AXIS);
        arm1.getTransforms().add(yRot1);
        arm2.getTransforms().add(yRot2);

        xGroup.setTranslateX(cx);
        xGroup.setTranslateY(cy);
        xGroup.setTranslateZ(cz);

        getChildren().add(xGroup);

        // Pequeña animación de aparición
        xGroup.setScaleX(0); xGroup.setScaleZ(0);
        Timeline appear = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(xGroup.scaleXProperty(), 0),
                new KeyValue(xGroup.scaleZProperty(), 0)),
            new KeyFrame(Duration.millis(250),
                new KeyValue(xGroup.scaleXProperty(), 1, Interpolator.EASE_OUT),
                new KeyValue(xGroup.scaleZProperty(), 1, Interpolator.EASE_OUT))
        );
        appear.play();
    }

    /**
     * HIT / SUNK: explosión de esferas naranjas/rojas que suben y desaparecen.
     * Si isBig=true (SUNK) la explosión es más grande.
     */
    private void playExplosion(int r, int c, boolean isBig) {
        double cx = (c - GRID / 2.0 + 0.5) * STEP;
        double cz = (r - GRID / 2.0 + 0.5) * STEP;
        double cy = -CELL_HEIGHT - 4;

        int count    = isBig ? 10 : 6;
        double range = isBig ? CELL * 0.7 : CELL * 0.4;
        double maxY  = isBig ? -40 : -25;

        Group explosionGroup = new Group();
        explosionGroup.setTranslateX(cx);
        explosionGroup.setTranslateY(cy);
        explosionGroup.setTranslateZ(cz);
        getChildren().add(explosionGroup);

        for (int i = 0; i < count; i++) {
            double angle  = (2 * Math.PI / count) * i;
            double radius = range * (0.4 + 0.6 * Math.random());

            Sphere spark = new Sphere(isBig ? 5 : 3.5);
            PhongMaterial mat = new PhongMaterial(
                i % 2 == 0 ? Color.ORANGERED : Color.YELLOW
            );
            mat.setSpecularColor(Color.WHITE);
            spark.setMaterial(mat);

            double startX = Math.cos(angle) * radius * 0.2;
            double startZ = Math.sin(angle) * radius * 0.2;
            spark.setTranslateX(startX);
            spark.setTranslateZ(startZ);
            spark.setTranslateY(0);
            spark.setOpacity(1.0);

            explosionGroup.getChildren().add(spark);

            double endX = Math.cos(angle) * radius;
            double endZ = Math.sin(angle) * radius;
            double delay = i * 30;

            Timeline tl = new Timeline(
                new KeyFrame(Duration.millis(delay),
                    new KeyValue(spark.translateXProperty(), startX),
                    new KeyValue(spark.translateZProperty(), startZ),
                    new KeyValue(spark.translateYProperty(), 0),
                    new KeyValue(spark.opacityProperty(),    1.0)),
                new KeyFrame(Duration.millis(delay + 350),
                    new KeyValue(spark.translateXProperty(), endX,   Interpolator.EASE_OUT),
                    new KeyValue(spark.translateZProperty(), endZ,   Interpolator.EASE_OUT),
                    new KeyValue(spark.translateYProperty(), maxY,   Interpolator.EASE_OUT),
                    new KeyValue(spark.opacityProperty(),    0.0,    Interpolator.EASE_IN))
            );
            tl.play();
        }

        // Núcleo central de la explosión (flash)
        Sphere core = new Sphere(isBig ? 12 : 8);
        PhongMaterial coreMat = new PhongMaterial(Color.WHITE);
        coreMat.setSpecularColor(Color.YELLOW);
        core.setMaterial(coreMat);
        core.setOpacity(0.9);
        explosionGroup.getChildren().add(core);

        Timeline coreAnim = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(core.scaleXProperty(), 0.1),
                new KeyValue(core.scaleYProperty(), 0.1),
                new KeyValue(core.scaleZProperty(), 0.1),
                new KeyValue(core.opacityProperty(), 0.9)),
            new KeyFrame(Duration.millis(150),
                new KeyValue(core.scaleXProperty(), 1.0, Interpolator.EASE_OUT),
                new KeyValue(core.scaleYProperty(), 1.0, Interpolator.EASE_OUT),
                new KeyValue(core.scaleZProperty(), 1.0, Interpolator.EASE_OUT),
                new KeyValue(core.opacityProperty(), 0.8)),
            new KeyFrame(Duration.millis(500),
                new KeyValue(core.scaleXProperty(), 1.5),
                new KeyValue(core.scaleYProperty(), 1.5),
                new KeyValue(core.scaleZProperty(), 1.5),
                new KeyValue(core.opacityProperty(), 0.0, Interpolator.EASE_IN))
        );
        // Eliminar el grupo cuando termina (las chispas ya desaparecieron)
        coreAnim.setOnFinished(e -> getChildren().remove(explosionGroup));
        coreAnim.play();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // INTERACTIVIDAD
    // ─────────────────────────────────────────────────────────────────────────

    public void setClickable(boolean value)                               { this.clickable = value; }
    public boolean isClickable()                                          { return clickable; }
    public void setOnCellClicked(BiConsumer<Integer,Integer> h)           { this.onCellClicked = h; }
    public void setOnCellDoubleClicked(BiConsumer<Integer,Integer> h)     { this.onCellDoubleClicked = h; }
    public void setCellHoverListener(BiConsumer<Integer,Integer> h)       { this.onCellHover = h; }

    // ─────────────────────────────────────────────────────────────────────────
    // MODO DEBUG — revelar/ocultar barcos por color de celda
    // ─────────────────────────────────────────────────────────────────────────

    private static final Color COLOR_REVEAL = Color.web("#ffd600"); // amarillo debug
    private final java.util.List<int[]> revealedCells = new java.util.ArrayList<>();

    /**
     * Pinta de amarillo las celdas que ocupan los barcos de la máquina.
     * Solo pinta celdas que aún no han sido disparadas (WATER/SHIP).
     */
    public void revealShipCells(java.util.List<Coordinate> coords) {
        revealedCells.clear();
        for (Coordinate coord : coords) {
            int r = coord.getPosX();
            int c = coord.getPosY();
            if (r < 0 || r >= GRID || c < 0 || c >= GRID) continue;
            if (state[r][c] == CellState.HIT || state[r][c] == CellState.SUNK
                    || state[r][c] == CellState.FAIL) continue;
            materials[r][c].setDiffuseColor(COLOR_REVEAL);
            revealedCells.add(new int[]{r, c});
        }
    }

    /** Restaura el color original de las celdas reveladas. */
    public void hideShipCells() {
        for (int[] rc : revealedCells) {
            materials[rc[0]][rc[1]].setDiffuseColor(colorFor(state[rc[0]][rc[1]]));
        }
        revealedCells.clear();
    }
}