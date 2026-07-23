package com.example.batallanaval.view;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Vista principal 2D del juego Batalla Naval.
 * Muestra el título, animación de ondas en el fondo,
 * campo de nombre y botones para iniciar o continuar la batalla 3D.
 */
public class MainMenuView extends Application {

    private static final int W = 800;
    private static final int H = 600;

    private double waveOffset = 0;
    private final List<Bubble> bubbles = new ArrayList<>();
    private final Random rng = new Random();

    @Override
    public void start(Stage stage) {

        // ── Fondo animado ────────────────────────────────────────────────────
        Canvas canvas = new Canvas(W, H);
        initBubbles();
        AnimationTimer bgTimer = new AnimationTimer() {
            @Override public void handle(long now) {
                waveOffset += 0.8;
                drawBackground(canvas.getGraphicsContext2D(), waveOffset);
            }
        };
        bgTimer.start();

        // ── Título ────────────────────────────────────────────────────────────
        Text titleLine1 = new Text("BATALLA");
        titleLine1.setFont(Font.font("Impact", FontWeight.BOLD, 80));
        titleLine1.setFill(Color.WHITE);
        DropShadow titleGlow = new DropShadow(20, Color.CYAN);
        titleGlow.setSpread(0.3);
        titleLine1.setEffect(titleGlow);

        Text titleLine2 = new Text("NAVAL");
        titleLine2.setFont(Font.font("Impact", FontWeight.BOLD, 80));
        titleLine2.setFill(Color.web("#00e5ff"));
        Glow glow2 = new Glow(0.8);
        titleLine2.setEffect(glow2);

        ScaleTransition pulse = new ScaleTransition(Duration.seconds(2), titleLine2);
        pulse.setFromX(1.0); pulse.setToX(1.05);
        pulse.setFromY(1.0); pulse.setToY(1.05);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();

        Text subtitle = new Text("3 D");
        subtitle.setFont(Font.font("Consolas", FontWeight.BOLD, 28));
        subtitle.setFill(Color.web("#ffd600"));

        VBox titleBox = new VBox(0, titleLine1, titleLine2, subtitle);
        titleBox.setAlignment(Pos.CENTER);

        // ── Separador ─────────────────────────────────────────────────────────
        Rectangle sep = new Rectangle(320, 3);
        sep.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT),
                new Stop(0.5, Color.web("#00e5ff")),
                new Stop(1, Color.TRANSPARENT)));

        // ── Nombre ────────────────────────────────────────────────────────────
        Label nameLabel = new Label("INGRESA TU NOMBRE:");
        nameLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 14));
        nameLabel.setTextFill(Color.web("#b2ebf2"));

        TextField nameField = new TextField();
        nameField.setPromptText("Comandante...");
        nameField.setMaxWidth(280);
        nameField.setFont(Font.font("Consolas", 16));
        nameField.setStyle(
            "-fx-background-color: rgba(0,20,40,0.85);" +
            "-fx-text-fill: #ffffff;" +
            "-fx-prompt-text-fill: #546e7a;" +
            "-fx-border-color: #00acc1;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 4;" +
            "-fx-background-radius: 4;" +
            "-fx-padding: 8 12;"
        );

        // ── Botón Nueva Partida ───────────────────────────────────────────────
        Button playBtn = new Button("⚓  INICIAR BATALLA");
        playBtn.setFont(Font.font("Consolas", FontWeight.BOLD, 16));
        playBtn.setPrefWidth(280);
        playBtn.setPrefHeight(48);
        styleBtn(playBtn, "#006064", "#00e5ff");
        playBtn.setOnMouseEntered(e -> styleBtn(playBtn, "#00838f", "#ffd600"));
        playBtn.setOnMouseExited(e ->  styleBtn(playBtn, "#006064", "#00e5ff"));

        // ── Botón Continuar Partida ───────────────────────────────────────────
        Button continueBtn = new Button("📂  CONTINUAR PARTIDA");
        continueBtn.setFont(Font.font("Consolas", FontWeight.BOLD, 14));
        continueBtn.setPrefWidth(280);
        continueBtn.setPrefHeight(40);
        continueBtn.setDisable(true);
        styleBtnDisabled(continueBtn);

        // Habilitar cuando exista save para el nombre escrito
        nameField.textProperty().addListener((obs, old, val) -> {
            boolean has = com.example.batallanaval.controller.GameManager.hasSavedGame(val.trim());
            continueBtn.setDisable(!has);
            if (has) {
                styleBtn(continueBtn, "#1b5e20", "#66bb6a");
                continueBtn.setOnMouseEntered(e -> styleBtn(continueBtn, "#2e7d32", "#a5d6a7"));
                continueBtn.setOnMouseExited(e ->  styleBtn(continueBtn, "#1b5e20", "#66bb6a"));
            } else {
                styleBtnDisabled(continueBtn);
            }
        });

        // ── Acciones ──────────────────────────────────────────────────────────
        Runnable launchNew = () -> {
            String name = nameField.getText().trim().isEmpty()
                    ? "Comandante" : nameField.getText().trim();
            bgTimer.stop();
            stage.close();
            launchBattle(name, false);
        };

        continueBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) return;
            bgTimer.stop();
            stage.close();
            launchBattle(name, true);
        });

        playBtn.setOnAction(e -> launchNew.run());
        nameField.setOnAction(e -> launchNew.run());

        // ── Créditos ──────────────────────────────────────────────────────────
        Label credits = new Label("POE · Programación Orientada a Eventos  |  2025");
        credits.setFont(Font.font("Consolas", 11));
        credits.setTextFill(Color.web("#37474f"));

        // ── Card central ──────────────────────────────────────────────────────
        VBox formBox = new VBox(12, nameLabel, nameField, playBtn, continueBtn);
        formBox.setAlignment(Pos.CENTER);

        VBox cardContent = new VBox(24, titleBox, sep, formBox);
        cardContent.setAlignment(Pos.CENTER);
        cardContent.setPadding(new Insets(36, 40, 36, 40));
        cardContent.setStyle(
            "-fx-background-color: rgba(0,13,26,0.72);" +
            "-fx-border-color: rgba(0,172,193,0.5);" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;"
        );
        // Limitar el ancho para que no crezca más de 380px
        cardContent.setMaxWidth(380);

        StackPane card = new StackPane(cardContent);
        card.setMaxWidth(380);

        // Animación de entrada
        card.setOpacity(0);
        card.setTranslateY(30);
        new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(card.opacityProperty(), 0),
                new KeyValue(card.translateYProperty(), 30)),
            new KeyFrame(Duration.millis(700),
                new KeyValue(card.opacityProperty(), 1, Interpolator.EASE_OUT),
                new KeyValue(card.translateYProperty(), 0, Interpolator.EASE_OUT))
        ).play();

        VBox bottom = new VBox(credits);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(0, 0, 16, 0));

        BorderPane root = new BorderPane();
        root.setCenter(card);
        root.setBottom(bottom);

        StackPane layered = new StackPane(canvas, root);
        Scene scene = new Scene(layered, W, H);
        scene.setFill(Color.web("#000d1a"));

        stage.setTitle("Batalla Naval 3D");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        nameField.requestFocus();
    }

    // ── Helpers de estilo ─────────────────────────────────────────────────────

    private static void styleBtn(Button btn, String bg, String border) {
        btn.setStyle(
            "-fx-background-color: " + bg + ";" +
            "-fx-text-fill: #ffffff;" +
            "-fx-border-color: " + border + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;"
        );
    }

    private static void styleBtnDisabled(Button btn) {
        btn.setStyle(
            "-fx-background-color: #37474f;" +
            "-fx-text-fill: #90a4ae;" +
            "-fx-border-color: #546e7a;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: default;"
        );
    }

    // ── Lanzar batalla ────────────────────────────────────────────────────────

    private void launchBattle(String playerName, boolean loadSave) {
        BattleView3D.pendingPlayerName = playerName;
        BattleView3D.pendingLoadSave   = loadSave;
        Stage battleStage = new Stage();
        try {
            new BattleView3D().start(battleStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Fondo animado ─────────────────────────────────────────────────────────

    private void drawBackground(GraphicsContext gc, double offset) {
        gc.setFill(Color.web("#000d1a"));
        gc.fillRect(0, 0, W, H);

        gc.setFill(Color.web("#ffffff", 0.4));
        rng.setSeed(42);
        for (int i = 0; i < 80; i++) {
            double sx = rng.nextDouble() * W;
            double sy = rng.nextDouble() * (H * 0.45);
            double sr = rng.nextDouble() * 1.5 + 0.5;
            gc.fillOval(sx, sy, sr, sr);
        }

        gc.setFill(new LinearGradient(0, H * 0.4, 0, H, false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#001a2e", 0.9)),
                new Stop(1, Color.web("#003550", 0.95))));
        gc.fillRect(0, H * 0.4, W, H * 0.6);

        drawWaveLayer(gc, offset,       H * 0.55, Color.web("#00acc1", 0.12), 1.0);
        drawWaveLayer(gc, offset * 0.7, H * 0.62, Color.web("#006064", 0.18), 1.4);
        drawWaveLayer(gc, offset * 0.4, H * 0.70, Color.web("#004d5c", 0.25), 1.8);

        for (Bubble b : bubbles) {
            b.y -= b.speed;
            if (b.y < H * 0.35) { b.y = H - 20; b.x = rng.nextDouble() * W; }
            gc.setFill(Color.web("#00e5ff", b.alpha));
            gc.fillOval(b.x, b.y, b.size, b.size);
        }
    }

    private void drawWaveLayer(GraphicsContext gc, double offset,
                                double yBase, Color color, double freq) {
        gc.setFill(color);
        gc.beginPath();
        gc.moveTo(0, H);
        for (double x = 0; x <= W; x += 4) {
            double y = yBase + Math.sin((x * freq + offset) * 0.03) * 12
                             + Math.sin((x * 0.6 + offset * 1.3) * 0.05) * 6;
            gc.lineTo(x, y);
        }
        gc.lineTo(W, H);
        gc.closePath();
        gc.fill();
    }

    private void initBubbles() {
        for (int i = 0; i < 25; i++) {
            Bubble b = new Bubble();
            b.x     = rng.nextDouble() * W;
            b.y     = rng.nextDouble() * H * 0.5 + H * 0.4;
            b.size  = rng.nextDouble() * 4 + 1;
            b.speed = rng.nextDouble() * 0.4 + 0.2;
            b.alpha = rng.nextDouble() * 0.15 + 0.05;
            bubbles.add(b);
        }
    }

    private static class Bubble { double x, y, size, speed, alpha; }

    public static void main(String[] args) { launch(args); }
}
