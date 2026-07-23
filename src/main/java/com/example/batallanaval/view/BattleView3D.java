package com.example.batallanaval.view;

import com.example.batallanaval.controller.GameManager;
import com.example.batallanaval.model.Classes.Utils.CellState;
import com.example.batallanaval.model.Classes.Utils.Coordinate;
import com.example.batallanaval.model.Classes.Utils.Orientation;
import com.example.batallanaval.model.Interfaces.BoardListener;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Escena principal de Batalla Naval 3D.
 *
 * Fases:
 *  1. SETUP     — diálogo para ingresar nombre e inicializar el juego.
 *  2. PLACEMENT — el jugador coloca sus 10 barcos sobre su tablero con ShipPlacer3D.
 *  3. BATTLE    — turnos alternados:
 *       · Turno JUGADOR  → solo tablero enemigo visible, clickable para disparar.
 *       · Turno MÁQUINA  → ambos tableros visibles, máquina dispara automáticamente.
 */
public class BattleView3D extends Application {

    /** Nombre pre-cargado desde el menú principal. Si no es null, salta el diálogo. */
    public static String  pendingPlayerName = null;
    /** Si true, carga la partida guardada en vez de iniciar nueva. */
    public static boolean pendingLoadSave   = false;

    // ── Constantes de layout ─────────────────────────────────────────────────
    private static final int    W         = 1100;
    private static final int    H         = 720;
    private static final int    HUD_H     = 100;
    private static final double BOARD_SEP = Board3D.GRID * (Board3D.CELL + Board3D.GAP) + 80;

    // ── Fases del juego ───────────────────────────────────────────────────────
    private enum Phase { SETUP, PLACEMENT, BATTLE }
    private Phase phase = Phase.SETUP;

    // ── Turnos (dentro de BATTLE) ─────────────────────────────────────────────
    private enum Turn { PLAYER, MACHINE }
    private Turn currentTurn = Turn.PLAYER;

    // ── Modelo ────────────────────────────────────────────────────────────────
    private final GameManager game = new GameManager();

    // ── Nodos 3D ─────────────────────────────────────────────────────────────
    private Board3D      playerBoard;
    private Board3D      machineBoard;
    private ShipPlacer3D placer;

    private final Group  world   = new Group();
    private final Rotate rotateX = new Rotate(28, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(-18, Rotate.Y_AXIS);

    private double mouseAnchorX, mouseAnchorY;
    private double anchorAngleX, anchorAngleY;

    // ── HUD 2D ────────────────────────────────────────────────────────────────
    private Label  statusLabel;
    private Label  turnLabel;
    private Button revealBtn;
    private Button saveBtn;
    private HBox   hudRow;
    private VBox   hudContainer;

    // ── Escena / SubScene ─────────────────────────────────────────────────────
    private SubScene subScene3D;
    private Scene    mainScene;

    // ═══════════════════════════════════════════════════════════════════════════
    // ARRANQUE
    // ═══════════════════════════════════════════════════════════════════════════

    @Override
    public void start(Stage stage) {
        // 1. Tableros (creados una vez; machineBoard empieza oculto)
        playerBoard  = new Board3D("TU TABLERO",      true);
        machineBoard = new Board3D("TABLERO ENEMIGO", false);
        playerBoard .setTranslateX(-BOARD_SEP / 2);
        machineBoard.setTranslateX( BOARD_SEP / 2);
        machineBoard.setVisible(false);

        // 2. Iluminación
        AmbientLight ambient = new AmbientLight(Color.rgb(90, 110, 140));
        PointLight   light1  = new PointLight(Color.rgb(220, 220, 255));
        light1.setTranslateX(200); light1.setTranslateY(-450); light1.setTranslateZ(-300);
        PointLight   light2  = new PointLight(Color.rgb(160, 200, 200));
        light2.setTranslateX(-200); light2.setTranslateY(-180); light2.setTranslateZ(200);

        // 3. Océano decorativo
        Box ocean = new Box(W * 2.0, 4, H * 2.0);
        ocean.setMaterial(new PhongMaterial(Color.web("#0a3d5c")));
        ocean.setTranslateY(32);

        world.getChildren().addAll(ocean, playerBoard, machineBoard, ambient, light1, light2);
        world.getTransforms().addAll(rotateX, rotateY);

        // 4. Cámara 3D
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setFieldOfView(52);
        camera.setNearClip(0.1);
        camera.setFarClip(6000);
        camera.setTranslateZ(-950);
        camera.setTranslateY(-160);

        // 5. SubScene 3D
        subScene3D = new SubScene(world, W, H - HUD_H, true, SceneAntialiasing.BALANCED);
        subScene3D.setFill(Color.web("#061a2e"));
        subScene3D.setCamera(camera);
        attachMouseControls(subScene3D);

        // 6. HUD inicial (vacío — se rellena en cada fase)
        statusLabel  = new Label("Bienvenido a Batalla Naval 3D");
        turnLabel    = new Label("");
        styleLabel(statusLabel, 15, "#e0f7fa");
        styleLabel(turnLabel,   13, "#b0bec5");

        // Botón debug — revela/oculta barcos de la máquina
        revealBtn = new Button("👁 Revelar barcos (debug)");
        revealBtn.setStyle(
            "-fx-background-color: #f57f17; -fx-text-fill: white; " +
            "-fx-font-family: 'Consolas'; -fx-font-size: 12px; -fx-cursor: hand;");
        revealBtn.setVisible(false);
        revealBtn.setOnAction(e -> toggleMachineShipReveal());

        // Botón guardar partida
        Button saveBtn = new Button("💾 Guardar Partida");
        saveBtn.setStyle(
            "-fx-background-color: #1565c0; -fx-text-fill: white; " +
            "-fx-font-family: 'Consolas'; -fx-font-size: 12px; -fx-cursor: hand;");
        saveBtn.setVisible(false);
        saveBtn.setOnAction(e -> {
            if (game.saveGame()) {
                saveBtn.setText("✅ Guardado");
                new Timeline(new KeyFrame(Duration.millis(2000),
                    ev -> saveBtn.setText("💾 Guardar Partida"))).play();
            } else {
                saveBtn.setText("❌ Error al guardar");
                new Timeline(new KeyFrame(Duration.millis(2000),
                    ev -> saveBtn.setText("💾 Guardar Partida"))).play();
            }
        });

        // Guardamos referencia para activarlo al iniciar batalla
        this.saveBtn = saveBtn;

        hudRow = new HBox();
        hudRow.setAlignment(Pos.CENTER);
        hudRow.setPadding(new Insets(8, 16, 8, 16));
        hudRow.setStyle("-fx-background-color: #0b1e30;");

        HBox topRow = new HBox(24, statusLabel, turnLabel, revealBtn, saveBtn);
        topRow.setAlignment(Pos.CENTER);
        topRow.setPadding(new Insets(6, 16, 6, 16));
        topRow.setStyle("-fx-background-color: #0d2137;");

        hudContainer = new VBox(topRow, hudRow);
        hudContainer.setPrefHeight(HUD_H);

        // 7. Escena raíz
        VBox root = new VBox(subScene3D, hudContainer);
        root.setStyle("-fx-background-color: #061a2e;");
        mainScene = new Scene(root, W, H);
        mainScene.setFill(Color.web("#061a2e"));

        // Teclas durante colocación: R → rotar, Shift → confirmar posición
        mainScene.setOnKeyPressed(e -> {
            if (phase != Phase.PLACEMENT || placer == null) return;
            if (e.getCode() == KeyCode.R) {
                placer.onKeyR();
            } else if (e.getCode() == KeyCode.SHIFT) {
                placer.onKeyEnter();
            }
        });

        stage.setTitle("Batalla Naval 3D");
        stage.setScene(mainScene);
        stage.show();

        // 8. Fase 1: si viene del menú principal, usar el nombre directo; si no, diálogo
        if (pendingPlayerName != null) {
            String name = pendingPlayerName;
            boolean load = pendingLoadSave;
            pendingPlayerName = null;
            pendingLoadSave   = false;
            if (load) {
                initGameFromSave(name);
            } else {
                initGameAndStartPlacement(name);
            }
        } else {
            showSetupDialog(stage);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // FASE 1 — SETUP (diálogo de nombre)
    // ═══════════════════════════════════════════════════════════════════════════

    private void showSetupDialog(Stage owner) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.setTitle("Batalla Naval 3D");

        Label  lbl = new Label("Ingresa tu nombre:");
        lbl.setStyle("-fx-font-size: 14px;");
        TextField tf = new TextField("Jugador1");
        tf.setPrefWidth(220);
        Button ok = new Button("Comenzar");
        ok.setStyle("-fx-background-color: #1565c0; -fx-text-fill: white; " +
                    "-fx-font-size: 13px; -fx-cursor: hand;");

        ok.setOnAction(e -> {
            String name = tf.getText().trim().isEmpty() ? "Jugador" : tf.getText().trim();
            dialog.close();
            initGameAndStartPlacement(name);
        });
        tf.setOnAction(e -> ok.fire());

        VBox box = new VBox(14, lbl, tf, ok);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(28));
        box.setStyle("-fx-background-color: #0d2137;");
        dialog.setScene(new Scene(box, 300, 160));
        dialog.showAndWait();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // FASE 1b — CARGAR PARTIDA GUARDADA
    // ═══════════════════════════════════════════════════════════════════════════

    private void initGameFromSave(String nickname) {
        boolean loaded = game.LoadGame(nickname);

        if (!loaded) {
            // Si falla la carga, iniciar partida nueva
            setStatus("No se encontró partida guardada. Iniciando nueva...");
            initGameAndStartPlacement(nickname);
            return;
        }

        // Conectar listeners
        attachBoardListeners();

        // Sincronizar la vista con el estado cargado (pintar celdas ya disparadas)
        syncBoardView();

        setStatus("Partida cargada. ¡Continuamos la batalla!");
        playerBoard.setTranslateX(-BOARD_SEP / 2);
        playerBoard.setVisible(true);
        playerBoard.revealShipCells(game.getPlayerShipCoordinates());
        //Aqui debe de ir el renderizado llamado con  java.util.List<com.example.batallanaval.model.AbstractsClasses.AbstractShip> getPlayerShips()

        for (com.example.batallanaval.model.AbstractsClasses.AbstractShip ship : game.getPlayerShips()) {
            int size = ship.getSize();
            Orientation ori = ship.getOrientation();

            int row = ship.getCoordinateShip().getPosX();
            int col = ship.getCoordinateShip().getPosY();
            String type = ship.getClass().getSimpleName();

            Group model = switch (type) {
                case "AircraftCarrier" -> Ship3D.createModel(size, ori);
                case "Submarine"       -> Submarine3D.createModel(size, ori);
                case "Destroyer"       -> Ship3D.createModel(size, ori);
                default                -> Ship3D.createModel(size, ori);
            };

            java.util.List<int[]> cells = new java.util.ArrayList<>();
            for (int i = 0; i < size; i++) {
                cells.add(ori == Orientation.HORINZONTAL ? new int[]{row, col + i} : new int[]{row + i, col});
            }
            double cx = 0, cz = 0;
            for (int[] rc : cells) {
                cx += (rc[1] - Board3D.GRID / 2.0 + 0.5) * Board3D.STEP;
                cz += (rc[0] - Board3D.GRID / 2.0 + 0.5) * Board3D.STEP;
            }
            cx /= cells.size();
            cz /= cells.size();

            model.setTranslateX(cx + playerBoard.getTranslateX());
            model.setTranslateZ(cz + playerBoard.getTranslateZ());
            model.setTranslateY(-Board3D.CELL_HEIGHT);

            world.getChildren().add(model);
        }


        // Ir directo a batalla sin placement
        startBattle();
    }

    /** Pinta en los tableros 2D el estado actual del modelo cargado. */
    private void syncBoardView() {
        var machineShips = game.getMachineShipCoordinates();
        // Los listeners notificarán cambios futuros; el estado inicial
        // se refleja leyendo el CellState directamente del Board.
        // Como Board no expone la matriz completa, disparamos una notificación
        // manual recorriendo todas las celdas.
        var pb = game.getPlayerBoard();
        var mb = game.getMachineBoard();
        for (int r = 0; r < Board3D.GRID; r++) {
            for (int c = 0; c < Board3D.GRID; c++) {
                var coordP = new com.example.batallanaval.model.Classes.Utils.Coordinate(r, c);
                var coordM = new com.example.batallanaval.model.Classes.Utils.Coordinate(r, c);
                // Los boards notifican solo en cambios; para sincronizar
                // pintamos el color leyendo wasAlreadyShot y los barcos
                // (solo marcamos las celdas disparadas, los barcos intactos
                // se verán al revelar con el botón debug)
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // FASE 2 — PLACEMENT (colocar barcos con ShipPlacer3D)
    // ═══════════════════════════════════════════════════════════════════════════
    private void initGameAndStartPlacement(String nickname) {
        try {
            game.initGameState(nickname);
        } catch (Exception ex) {
            setStatus("Error iniciando juego: " + ex.getMessage());
            return;
        }

        // Conectar listeners del modelo a la vista ANTES de que empiece el juego
        attachBoardListeners();

        phase = Phase.PLACEMENT;
        setStatus("Coloca tus barcos — Shift para confirmar, R para rotar");
        turnLabel.setText("[ FASE: COLOCACIÓN ]");
        turnLabel.setStyle("-fx-text-fill: #fff176; -fx-font-size: 13px; -fx-font-family: 'Consolas';");

        // Tablero del jugador en su posición FINAL de batalla desde ya,
        // así los modelos 3D de los barcos quedarán alineados cuando aparezca el enemigo.
        playerBoard.setTranslateX(-BOARD_SEP / 2);
        playerBoard.setVisible(true);
        machineBoard.setVisible(false);

        // Crear el placer y conectarlo al mundo 3D y al tablero
        placer = new ShipPlacer3D(game, playerBoard, world);
        placer.setOnAllPlaced(this::startBattle);

        // El HUD inferior muestra el nombre del barco actual + botón Rotar
        hudRow.getChildren().setAll(placer.getHudRow());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // FASE 3 — BATTLE (turnos alternados)
    // ═══════════════════════════════════════════════════════════════════════════

    private void startBattle() {
        phase = Phase.BATTLE;

        // Limpiar HUD de colocación
        hudRow.getChildren().clear();

        // Activar botones de battle
        revealBtn.setVisible(true);
        saveBtn.setVisible(true);

        // playerBoard ya está en -BOARD_SEP/2 desde el placement — no hay que moverlo.
        machineBoard.setTranslateX(BOARD_SEP / 2);
        machineBoard.setVisible(true);

        setStatus("¡Batalla iniciada! Haz clic en el tablero enemigo para disparar.");
        turnLabel.setText("[ INICIANDO BATALLA ]");
        turnLabel.setStyle("-fx-text-fill: #ff9800; -fx-font-size: 13px; -fx-font-family: 'Consolas';");

        // Breve pausa y arranca el primer turno del jugador
        new Timeline(new KeyFrame(Duration.millis(1200),
                e -> setTurnToPlayer())).play();
    }

    // ── Turno JUGADOR ─────────────────────────────────────────────────────────

    private void setTurnToPlayer() {
        currentTurn = Turn.PLAYER;
        turnLabel.setText("[ TURNO: JUGADOR ]");
        turnLabel.setStyle("-fx-text-fill: #64ffda; -fx-font-size: 13px; -fx-font-family: 'Consolas';");
        setStatus("Tu turno — haz clic en el tablero enemigo para disparar");

        // Ambos tableros siempre visibles, solo habilitar clics en el enemigo
        playerBoard.setVisible(true);
        machineBoard.setVisible(true);
        machineBoard.setClickable(true);
        machineBoard.setOnCellClicked(this::onPlayerShot);
        playerBoard.setClickable(false);
    }

    // ── Turno MÁQUINA ─────────────────────────────────────────────────────────

    private void setTurnToMachine() {
        currentTurn = Turn.MACHINE;
        turnLabel.setText("[ TURNO: MÁQUINA ]");
        turnLabel.setStyle("-fx-text-fill: #ff5252; -fx-font-size: 13px; -fx-font-family: 'Consolas';");
        setStatus("Turno de la máquina...");

        // Deshabilitar clics mientras dispara la máquina
        machineBoard.setClickable(false);
        playerBoard.setClickable(false);

        // La máquina dispara tras una pequeña pausa
        new Timeline(new KeyFrame(Duration.millis(1200),
                ev -> triggerMachineShot())).play();
    }

    // ── Disparos ──────────────────────────────────────────────────────────────

    private void onPlayerShot(int row, int col) {
        if (currentTurn != Turn.PLAYER || phase != Phase.BATTLE) return;
        machineBoard.setClickable(false);

        boolean hit = game.playerShot(row, col);

        if (hit) {
            // IMPACTO — el jugador sigue disparando
            setStatus("¡IMPACTO en " + (char)('A' + col) + (row + 1) + "! Vuelve a disparar.");
            if (checkGameOver()) return;
            // Reactivar clics para que el jugador dispare de nuevo
            new Timeline(new KeyFrame(Duration.millis(500),
                e -> machineBoard.setClickable(true))).play();
        } else {
            // AGUA — turno pasa a la máquina
            setStatus("Agua en " + (char)('A' + col) + (row + 1) + "... Turno de la máquina.");
            if (checkGameOver()) return;
            new Timeline(new KeyFrame(Duration.millis(900),
                e -> setTurnToMachine())).play();
        }
    }

    private void triggerMachineShot() {
        if (currentTurn != Turn.MACHINE || phase != Phase.BATTLE) return;
        game.machineShot();
        setStatus("¡La máquina ha disparado!");

        if (checkGameOver()) return;

        new Timeline(new KeyFrame(Duration.millis(1300), e -> {
            setStatus("Tu turno — haz clic en el tablero enemigo.");
            setTurnToPlayer();
        })).play();
    }

    private boolean checkGameOver() {
        if (game.playerWins()) {
            showGameOver(true);
            return true;
        }
        if (game.machineWins()) {
            showGameOver(false);
            return true;
        }
        return false;
    }

    private void showGameOver(boolean playerWon) {
        phase = Phase.SETUP; // detener el juego
        machineBoard.setClickable(false);
        playerBoard.setClickable(false);

        String msg     = playerWon ? "¡GANASTE! 🏆 Hundiste toda la flota enemiga."
                                   : "Perdiste... La máquina hundió toda tu flota.";
        String color   = playerWon ? "#64ffda" : "#ff5252";
        String btnText = playerWon ? "👑 ¡GANASTE!" : "💀 Perdiste";

        Platform.runLater(() -> {
            turnLabel.setText(btnText);
            turnLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 15px; " +
                               "-fx-font-family: 'Consolas'; -fx-font-weight: bold;");
            setStatus(msg);

            // Alerta emergente
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Fin del juego");
            alert.setHeaderText(playerWon ? "¡Victoria!" : "¡Derrota!");
            alert.setContentText(msg + "\n\nReinicia la aplicación para jugar de nuevo.");
            alert.show();
        });
    }

    // ── Modo debug: revelar/ocultar barcos de la máquina ─────────────────────

    private boolean debugVisible = false;

    private void toggleMachineShipReveal() {
        if (debugVisible) {
            machineBoard.hideShipCells();
            debugVisible = false;
            revealBtn.setText("👁 Revelar barcos (debug)");
        } else {
            var coords = game.getMachineShipCoordinates();
            machineBoard.revealShipCells(coords);
            debugVisible = true;
            revealBtn.setText("🙈 Ocultar barcos (debug)");
        }
    }

    // ── BoardListeners ────────────────────────────────────────────────────────

    private void attachBoardListeners() {
        game.getPlayerBoard().addListener((c, state) ->
            Platform.runLater(() -> playerBoard.updateCell(c, state)));

        game.getMachineBoard().addListener((c, state) ->
            Platform.runLater(() -> machineBoard.updateCell(c, state)));
    }

    // ── Controles de mouse ────────────────────────────────────────────────────

    private void attachMouseControls(SubScene sub) {
        sub.setOnMousePressed(e -> {
            mouseAnchorX = e.getSceneX();
            mouseAnchorY = e.getSceneY();
            anchorAngleX = rotateX.getAngle();
            anchorAngleY = rotateY.getAngle();
        });
        sub.setOnMouseDragged(e -> {
            // Durante la colocación la rotación es libre; en batalla también
            rotateX.setAngle(anchorAngleX - (e.getSceneY() - mouseAnchorY) * 0.35);
            rotateY.setAngle(anchorAngleY + (e.getSceneX() - mouseAnchorX) * 0.35);
        });
        sub.setOnScroll(e ->
            world.setTranslateZ(world.getTranslateZ() + e.getDeltaY() * 1.4));
    }

    // ── Animación traslación X ────────────────────────────────────────────────

    private void animateTranslateX(Node node, double from, double to,
                                   double ms, Runnable onFinished) {
        Timeline tl = new Timeline(
            new KeyFrame(Duration.ZERO,        new KeyValue(node.translateXProperty(), from)),
            new KeyFrame(Duration.millis(ms),  new KeyValue(node.translateXProperty(), to, Interpolator.EASE_BOTH))
        );
        if (onFinished != null) tl.setOnFinished(e -> onFinished.run());
        tl.play();
    }

    // ── Helpers UI ────────────────────────────────────────────────────────────

    private void styleLabel(Label l, int size, String color) {
        l.setStyle("-fx-text-fill: " + color + "; -fx-font-size: " + size + "px; " +
                   "-fx-font-family: 'Consolas';");
    }

    private void setStatus(String msg) {
        Platform.runLater(() -> statusLabel.setText(msg));
    }

    public static void main(String[] args) { launch(args); }
}
