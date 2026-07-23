package com.example.batallanaval.view;

import com.example.batallanaval.controller.GameManager;
import com.example.batallanaval.model.Classes.FactoryMethod.ShipFactory;
import com.example.batallanaval.model.Classes.Utils.Orientation;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona la fase de colocación de barcos sobre el tablero del jugador.
 *
 * Flujo:
 *  1. Presenta los barcos de la flota uno por uno (orden: 4,3,3,2,2,2,1,1,1,1).
 *  2. El jugador mueve el cursor sobre las celdas del tablero → el preview
 *     del barco sigue el cursor en tiempo real.
 *  3. Tecla R (o botón) → rota 90° la orientación del barco actual.
 *  4. Clic sobre una celda válida → confirma la posición.
 *  5. Cuando los 10 barcos están colocados llama al callback onAllPlaced.
 */
public class ShipPlacer3D {

    // ── Fleet ─────────────────────────────────────────────────────────────────
    // Flota: 1 AircraftCarrier(4), 2 Submarine(3), 3 Destroyer(2), 4 Frigate(1)
    private static final int[]    FLEET = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};
    private static final String[] NAMES = {
        "Portaaviones (4)",
        "Submarino (3)",  "Submarino (3)",
        "Destructor (2)", "Destructor (2)", "Destructor (2)",
        "Fragata (1)",    "Fragata (1)",    "Fragata (1)",   "Fragata (1)"
    };
    private static final String[] TYPES = {
        "AircraftCarrier",
        "Submarine",  "Submarine",
        "Destroyer",  "Destroyer",  "Destroyer",
        "Frigate",    "Frigate",    "Frigate",   "Frigate"
    };

    // ── Estado ────────────────────────────────────────────────────────────────
    private final GameManager   game;
    private final Board3D       board;
    private final Group         world;          // grupo 3D de la escena (Board3D vive aquí)
    private       Runnable      onAllPlaced;

    private int         shipIndex   = 0;        // qué barco estamos colocando ahora
    private Orientation orientation = Orientation.HORINZONTAL;
    private int         hoverRow    = 0;
    private int         hoverCol    = 0;

    // Preview visual del barco que se está arrastrando
    private Group previewModel;
    // Overlay de celdas coloreadas que indica dónde quedará el barco
    private final List<Box> highlightBoxes = new ArrayList<>();

    // HUD nodes que el llamador puede añadir a su layout 2D
    private final Label  infoLabel   = new Label();
    private final Label  nameLabel   = new Label();
    private final Button rotateBtn   = new Button("Rotar  [ R ]");
    private final HBox   hudRow;

    // Material para celdas válidas / inválidas del preview
    private static final Color PREVIEW_OK  = Color.web("#00e676", 0.7);
    private static final Color PREVIEW_BAD = Color.web("#ff1744", 0.6);

    // ── Constructor ───────────────────────────────────────────────────────────

    public ShipPlacer3D(GameManager game, Board3D board, Group world) {
        this.game  = game;
        this.board = board;
        this.world = world;

        // HUD
        styleLabel(infoLabel, 14, "#b2ebf2");
        styleLabel(nameLabel, 16, "#fff176");
        rotateBtn.setStyle(
            "-fx-background-color: #1565c0; -fx-text-fill: white; " +
            "-fx-font-family: 'Consolas'; -fx-font-size: 13px; -fx-cursor: hand;");
        rotateBtn.setOnAction(e -> rotate());

        hudRow = new HBox(20, nameLabel, infoLabel, rotateBtn);
        hudRow.setAlignment(Pos.CENTER);
        hudRow.setPadding(new Insets(8));
        hudRow.setStyle("-fx-background-color: #0d2137;");

        // El tablero recibe hover y clic para la colocación
        board.setOnCellDoubleClicked(this::onCellClicked);
        attachHoverListeners();

        refreshPreview();
    }

    // ── API pública ───────────────────────────────────────────────────────────

    /** Nodo 2D del HUD que muestra el nombre del barco actual + botón rotar. */
    public HBox getHudRow() { return hudRow; }

    /** Callback invocado cuando los 10 barcos han sido colocados correctamente. */
    public void setOnAllPlaced(Runnable r) { this.onAllPlaced = r; }

    /** Llamar desde la escena para propagar la tecla R. */
    public void onKeyR() { rotate(); }

    /** Llamar desde la escena para confirmar la posición actual con Enter. */
    public void onKeyEnter() {
        if (!isFinished()) onCellClicked(hoverRow, hoverCol);
    }

    /** Devuelve true si ya terminó la colocación. */
    public boolean isFinished() { return shipIndex >= FLEET.length; }

    // ── Hover ─────────────────────────────────────────────────────────────────

    private void attachHoverListeners() {
        // Reutilizamos el mecanismo de clic existente de Board3D, pero necesitamos
        // también hover → añadimos mouse-entered/exited a cada celda del tablero
        // a través de un listener de celdas personalizado.
        board.setCellHoverListener((row, col) -> {
            hoverRow = row;
            hoverCol = col;
            refreshHighlight();
        });
        // Habilitar interactividad
        board.setClickable(true);
    }

    // ── Lógica de colocación ──────────────────────────────────────────────────

    private void onCellClicked(int row, int col) {
        if (isFinished()) return;
        int size = FLEET[shipIndex];

        // Validar que el barco cabe
        if (!fits(row, col, size, orientation)) {
            infoLabel.setText("¡No cabe ahí! Elige otra posición.");
            return;
        }

        // Intentar colocar en el modelo (pasamos el tipo para distinguir Submarine/Boat)
        String orientStr = (orientation == Orientation.HORINZONTAL) ? "HORINZONTAL" : "VERTICAL";
        String type      = TYPES[shipIndex];
        boolean ok = Boolean.TRUE.equals(game.createShip(size, row, col, orientStr, type));
        if (!ok) {
            // Distinguir entre "sale del borde" y "celda ocupada"
            if (!fits(row, col, size, orientation)) {
                infoLabel.setText("¡El barco se sale del tablero!");
            } else {
                infoLabel.setText("Posición ocupada. Intenta otro lugar.");
            }
            return;
        }

        // Fijar el modelo 3D en el tablero (permanente)
        placeModelPermanently(row, col, size, orientation);

        // Pintar las celdas de verde en el tablero
        for (int[] rc : occupiedCells(row, col, size, orientation)) {
            board.markAsPlaced(rc[0], rc[1]);
        }

        shipIndex++;

        if (isFinished()) {
            board.setClickable(false);
            board.setOnCellDoubleClicked(null);
            board.setOnCellClicked(null);
            board.setCellHoverListener(null);
            clearHighlight();
            removePreview();
            if (onAllPlaced != null) onAllPlaced.run();
        } else {
            orientation = Orientation.HORINZONTAL; // resetear orientación cada barco
            refreshPreview();
            refreshHighlight();
        }
    }

    private void rotate() {
        if (isFinished()) return;
        orientation = (orientation == Orientation.HORINZONTAL)
                ? Orientation.VERTICAL : Orientation.HORINZONTAL;
        refreshPreview();
        refreshHighlight();
    }

    // ── Preview / highlight ───────────────────────────────────────────────────

    private void refreshPreview() {
        removePreview();

        int size = FLEET[shipIndex];
        nameLabel.setText("Colocando: " + NAMES[shipIndex]
                + "   [" + (shipIndex + 1) + "/" + FLEET.length + "]");
        infoLabel.setText("Shift para colocar  |  R para rotar");

        previewModel = buildModel(size, orientation);
        // El preview flota ligeramente sobre el tablero
        positionModel(previewModel, hoverRow, hoverCol, size, orientation);
        previewModel.setTranslateY(-Board3D.CELL_HEIGHT * 2.5);  // flota encima

        // Semitransparencia del preview → ajustar opacidad
        previewModel.setOpacity(0.75);
        world.getChildren().add(previewModel);

        refreshHighlight();
    }

    private void refreshHighlight() {
        clearHighlight();
        if (isFinished()) return;

        int size = FLEET[shipIndex];
        boolean valid = fits(hoverRow, hoverCol, size, orientation);

        List<int[]> cells = occupiedCells(hoverRow, hoverCol, size, orientation);
        for (int[] rc : cells) {
            int r = rc[0], c = rc[1];
            if (r < 0 || r >= Board3D.GRID || c < 0 || c >= Board3D.GRID) continue;

            Box overlay = new Box(Board3D.CELL - 2, Board3D.CELL_HEIGHT + 4, Board3D.CELL - 2);
            PhongMaterial mat = new PhongMaterial(valid ? PREVIEW_OK : PREVIEW_BAD);
            mat.setSpecularColor(Color.WHITE);
            overlay.setMaterial(mat);
            overlay.setOpacity(0.55);

            double ox = (c - Board3D.GRID / 2.0 + 0.5) * Board3D.STEP;
            double oz = (r - Board3D.GRID / 2.0 + 0.5) * Board3D.STEP;
            overlay.setTranslateX(ox + board.getTranslateX());
            overlay.setTranslateY(-Board3D.CELL_HEIGHT / 2.0 - 1);
            overlay.setTranslateZ(oz + board.getTranslateZ());

            highlightBoxes.add(overlay);
            world.getChildren().add(overlay);
        }

        // Mover preview al hover actual
        if (previewModel != null) {
            positionModel(previewModel, hoverRow, hoverCol, size, orientation);
            previewModel.setTranslateY(-Board3D.CELL_HEIGHT * 2.5);
        }
    }

    private void clearHighlight() {
        world.getChildren().removeAll(highlightBoxes);
        highlightBoxes.clear();
    }

    private void removePreview() {
        if (previewModel != null) {
            world.getChildren().remove(previewModel);
            previewModel = null;
        }
    }

    // ── Colocación permanente ─────────────────────────────────────────────────

    private void placeModelPermanently(int row, int col, int size, Orientation ori) {
        Group model = buildModel(size, ori);
        positionModel(model, row, col, size, ori);
        model.setTranslateY(-Board3D.CELL_HEIGHT);
        world.getChildren().add(model);
    }

    // ── Helpers geométricos ───────────────────────────────────────────────────

    /**
     * Construye el modelo 3D adecuado según tamaño y tipo:
     *   size 5            → Carrier3D
     *   size 4            → Battleship3D
     *   size 3 / Submarine → Submarine3D
     *   size 3 / Boat      → Boat3D
     *   size 2            → Ship3D (Destroyer)
     */
    private Group buildModel(int size, Orientation ori) {
        String type = TYPES[shipIndex];
        return switch (type) {
            case "AircraftCarrier" -> Ship3D.createModel(size, ori);    // portaaviones rojo grande
            case "Submarine"       -> Submarine3D.createModel(size, ori); // submarino dorado
            case "Destroyer"       -> Ship3D.createModel(size, ori);    // destructor rojo
            default                -> Ship3D.createModel(size, ori);    // fragata (1 celda)
        };
    }

    /** Posiciona un Group en el centro de las celdas que ocupa el barco. */
    private void positionModel(Group model, int row, int col, int size, Orientation ori) {
        List<int[]> cells = occupiedCells(row, col, size, ori);
        // Centro = promedio de posiciones
        double cx = 0, cz = 0;
        for (int[] rc : cells) {
            cx += (rc[1] - Board3D.GRID / 2.0 + 0.5) * Board3D.STEP;
            cz += (rc[0] - Board3D.GRID / 2.0 + 0.5) * Board3D.STEP;
        }
        cx /= cells.size();
        cz /= cells.size();

        model.setTranslateX(cx + board.getTranslateX());
        model.setTranslateZ(cz + board.getTranslateZ());
    }

    /** Lista de celdas (row,col) que ocupa el barco con inicio en (row,col). */
    private static List<int[]> occupiedCells(int row, int col, int size, Orientation ori) {
        List<int[]> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (ori == Orientation.HORINZONTAL) {
                list.add(new int[]{row, col + i});
            } else {
                list.add(new int[]{row + i, col});
            }
        }
        return list;
    }

    /** Verifica que el barco cabe dentro del tablero 10×10. */
    private static boolean fits(int row, int col, int size, Orientation ori) {
        List<int[]> cells = occupiedCells(row, col, size, ori);
        for (int[] rc : cells) {
            if (rc[0] < 0 || rc[0] >= Board3D.GRID) return false;
            if (rc[1] < 0 || rc[1] >= Board3D.GRID) return false;
        }
        return true;
    }

    // ── Helpers UI ────────────────────────────────────────────────────────────

    private static void styleLabel(Label l, int size, String color) {
        l.setStyle("-fx-text-fill: " + color + "; -fx-font-size: " + size + "px; "
                 + "-fx-font-family: 'Consolas';");
    }
}
