import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Connect4UI extends Application {

    // ── Palette ────────────────────────────────────────────────────────────
    private static final Color BOARD_BLUE      = Color.web("#185FA5");
    private static final Color PLAYER_RED      = Color.web("#E24B4A");
    private static final Color AI_AMBER        = Color.web("#EF9F27");
    private static final Color CELL_EMPTY      = Color.web("#E8F1FB");
    private static final Color BG_DARK         = Color.web("#0F172A");
    private static final Color SURFACE         = Color.web("#1E293B");
    private static final Color SURFACE_RAISED  = Color.web("#263147");
    private static final Color TEXT_PRIMARY    = Color.web("#F1F5F9");
    private static final Color TEXT_MUTED      = Color.web("#94A3B8");
    private static final Color BORDER          = Color.web("#334155");

    // ── Game state ─────────────────────────────────────────────────────────
    private Board board = new Board();
    private AIPlayer ai = new AIPlayer();
    private Circle[][] circles = new Circle[Board.ROWS][Board.COLS];
    private Button[] colButtons = new Button[Board.COLS];

    private Label statusLabel;
    private Circle statusDot;
    private Label playerScoreLabel;
    private Label aiScoreLabel;
    private HBox playerCard;
    private HBox aiCard;

    private int playerWins = 0;
    private int aiWins = 0;

    private boolean gameOver = false;
    private boolean aiThinking = false;
    private Timeline dotPulse;

    private ExecutorService aiExecutor = Executors.newSingleThreadExecutor();

    // ── Entry point ────────────────────────────────────────────────────────
    @Override
    public void start(Stage stage) {
        VBox root = new VBox(16);
        root.setStyle("-fx-background-color: #0F172A;");
        root.setPadding(new Insets(24, 28, 28, 28));
        root.setAlignment(Pos.TOP_CENTER);

        root.getChildren().addAll(
            buildHeader(),
            buildScoreboard(),
            buildStatusBar(),
            buildGameArea()
        );

        Scene scene = new Scene(root, 520, 620);
        scene.setFill(BG_DARK);

        stage.setTitle("Connect 4  ·  AI");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        highlightTurn(true);
    }

    // ── Header ─────────────────────────────────────────────────────────────
    private HBox buildHeader() {
        Label title = new Label("Connect");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #F1F5F9;");

        Label four = new Label("4");
        four.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #E24B4A;");

        Label sub = new Label(" AI");
        sub.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #94A3B8;");

        HBox titleBox = new HBox(0, title, four, sub);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Button newGame = styledButton("↺  New Game");
        newGame.setOnAction(e -> resetGame());
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        HBox header = new HBox(12, titleBox, newGame);
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    // ── Scoreboard ─────────────────────────────────────────────────────────
    private HBox buildScoreboard() {
        playerScoreLabel = scoreNumber("0");
        aiScoreLabel     = scoreNumber("0");

        playerCard = scoreCard(PLAYER_RED, "You", "Player", playerScoreLabel);
        aiCard     = scoreCard(AI_AMBER,  "AI",  "Minimax", aiScoreLabel);

        HBox board = new HBox(12, playerCard, aiCard);
        board.setAlignment(Pos.CENTER);
        HBox.setHgrow(playerCard, Priority.ALWAYS);
        HBox.setHgrow(aiCard,     Priority.ALWAYS);
        playerCard.setMaxWidth(Double.MAX_VALUE);
        aiCard.setMaxWidth(Double.MAX_VALUE);
        return board;
    }

    private HBox scoreCard(Color tokenColor, String roleText, String name, Label scoreLabel) {
        Circle token = new Circle(16);
        token.setFill(tokenColor);
        applyTokenShadow(token);

        Label role  = new Label(roleText.toUpperCase());
        role.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #64748B; -fx-letter-spacing: 1px;");

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #F1F5F9;");

        VBox info = new VBox(2, role, nameLabel);
        info.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(info, Priority.ALWAYS);

        HBox card = new HBox(12, token, info, scoreLabel);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(14, 18, 14, 14));
        card.setStyle(surfaceStyle(false));
        return card;
    }

    private Label scoreNumber(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #F1F5F9;");
        return l;
    }

    // ── Status bar ─────────────────────────────────────────────────────────
    private HBox buildStatusBar() {
        statusDot = new Circle(5, PLAYER_RED);

        statusLabel = new Label("Your turn — pick a column");
        statusLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #94A3B8;");

        HBox bar = new HBox(10, statusDot, statusLabel);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(10, 16, 10, 16));
        bar.setStyle(surfaceStyle(false));
        return bar;
    }

    // ── Game board + column buttons ────────────────────────────────────────
    private VBox buildGameArea() {
        GridPane colBtnGrid = new GridPane();
        colBtnGrid.setHgap(5);

        for (int col = 0; col < Board.COLS; col++) {
            final int c = col;
            Button btn = new Button("↓");
            btn.setPrefWidth(60);
            btn.setPrefHeight(34);
            btn.setStyle(colBtnStyle(false));
            btn.setOnMouseEntered(e -> { if (!btn.isDisabled()) btn.setStyle(colBtnStyle(true)); });
            btn.setOnMouseExited(e  -> btn.setStyle(colBtnStyle(false)));
            btn.setOnAction(e -> handleHumanMove(c));
            colButtons[col] = btn;
            colBtnGrid.add(btn, col, 0);
        }

        GridPane boardGrid = new GridPane();
        boardGrid.setHgap(6);
        boardGrid.setVgap(6);
        boardGrid.setPadding(new Insets(10));
        boardGrid.setStyle("-fx-background-color: #185FA5; -fx-background-radius: 14;");

        for (int r = 0; r < Board.ROWS; r++) {
            for (int c = 0; c < Board.COLS; c++) {
                Circle circle = new Circle(26);
                circle.setFill(CELL_EMPTY);
                circles[r][c] = circle;
                boardGrid.add(circle, c, r);
            }
        }

        VBox area = new VBox(6, colBtnGrid, boardGrid);
        area.setAlignment(Pos.CENTER);
        return area;
    }

    // ── Move handling ──────────────────────────────────────────────────────
    private void handleHumanMove(int col) {
        if (gameOver || aiThinking) return;
        if (!board.isValidMove(col)) {
            flashStatus("Column is full — try another", PLAYER_RED);
            return;
        }

        board.dropPiece(col, 1);
        updateBoardUI();

        if (board.checkWin(1)) {
            playerWins++;
            playerScoreLabel.setText(String.valueOf(playerWins));
            setStatus("You win! 🎉", PLAYER_RED, false);
            highlightTurn(false);
            gameOver = true;
            setColButtonsDisabled(true);
            return;
        }
        if (board.isBoardFull()) {
            setStatus("Draw — well played!", TEXT_MUTED, false);
            gameOver = true;
            setColButtonsDisabled(true);
            return;
        }

        setColButtonsDisabled(true);
        setStatus("AI is thinking...", AI_AMBER, true);
        highlightTurn(false); // neither yet
        aiThinking = true;

        Board boardCopy = board.copy();
        aiExecutor.submit(() -> {
            int aiMove = ai.getMove(boardCopy);
            Platform.runLater(() -> {
                if (gameOver) return;
                board.dropPiece(aiMove, 2);
                updateBoardUI();

                if (board.checkWin(2)) {
                    aiWins++;
                    aiScoreLabel.setText(String.valueOf(aiWins));
                    setStatus("AI wins this round!", AI_AMBER, false);
                    highlightTurn(false);
                    gameOver = true;
                    aiThinking = false;
                    return;
                }
                if (board.isBoardFull()) {
                    setStatus("Draw — well played!", TEXT_MUTED, false);
                    gameOver = true;
                    aiThinking = false;
                    return;
                }

                aiThinking = false;
                setColButtonsDisabled(false);
                setStatus("Your turn — pick a column", PLAYER_RED, false);
                highlightTurn(true);
            });
        });
    }

    private void resetGame() {
        board = new Board();
        gameOver = false;
        aiThinking = false;
        updateBoardUI();
        setColButtonsDisabled(false);
        setStatus("Your turn — pick a column", PLAYER_RED, false);
        highlightTurn(true);
    }

    // ── UI helpers ─────────────────────────────────────────────────────────
    private void updateBoardUI() {
        for (int r = 0; r < Board.ROWS; r++) {
            for (int c = 0; c < Board.COLS; c++) {
                int cell = board.getCell(r, c);
                Circle circle = circles[r][c];
                if (cell == 1) {
                    circle.setFill(PLAYER_RED);
                    applyTokenShadow(circle);
                } else if (cell == 2) {
                    circle.setFill(AI_AMBER);
                    applyTokenShadow(circle);
                } else {
                    circle.setFill(CELL_EMPTY);
                    circle.setEffect(null);
                }
            }
        }
    }

    private void applyTokenShadow(Circle circle) {
        InnerShadow inner = new InnerShadow();
        inner.setOffsetY(-3);
        inner.setRadius(4);
        inner.setColor(Color.rgb(0, 0, 0, 0.25));
        circle.setEffect(inner);
    }

    private void setStatus(String text, Color dotColor, boolean pulse) {
        statusLabel.setText(text);
        statusDot.setFill(dotColor);

        if (dotPulse != null) dotPulse.stop();
        statusDot.setOpacity(1.0);

        if (pulse) {
            dotPulse = new Timeline(
                new KeyFrame(Duration.ZERO,    new KeyValue(statusDot.opacityProperty(), 1.0)),
                new KeyFrame(Duration.millis(500), new KeyValue(statusDot.opacityProperty(), 0.25)),
                new KeyFrame(Duration.millis(1000), new KeyValue(statusDot.opacityProperty(), 1.0))
            );
            dotPulse.setCycleCount(Timeline.INDEFINITE);
            dotPulse.play();
        }
    }

    private void flashStatus(String text, Color dotColor) {
        String original = statusLabel.getText();
        Color originalColor = (Color) statusDot.getFill();
        setStatus(text, dotColor, false);
        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
        pause.setOnFinished(e -> setStatus(original, originalColor, false));
        pause.play();
    }

    private void highlightTurn(boolean playerTurn) {
        if (playerTurn) {
            playerCard.setStyle(surfaceStyle(true) + " -fx-border-color: #E24B4A; -fx-border-width: 1.5; -fx-border-radius: 10;");
            aiCard.setStyle(surfaceStyle(false));
        } else {
            playerCard.setStyle(surfaceStyle(false));
            aiCard.setStyle(surfaceStyle(false));
        }
    }

    private void setColButtonsDisabled(boolean disabled) {
        for (Button b : colButtons) b.setDisable(disabled);
    }

    // ── Style strings ──────────────────────────────────────────────────────
    private String surfaceStyle(boolean highlighted) {
        return "-fx-background-color: #1E293B;" +
               "-fx-background-radius: 10;" +
               "-fx-border-radius: 10;" +
               "-fx-border-width: 1;" +
               "-fx-border-color: " + (highlighted ? "#E24B4A" : "#334155") + ";";
    }

    private String colBtnStyle(boolean hovered) {
        return "-fx-background-color: " + (hovered ? "#1E293B" : "#263147") + ";" +
               "-fx-text-fill: " + (hovered ? "#E24B4A" : "#64748B") + ";" +
               "-fx-font-size: 16px;" +
               "-fx-background-radius: 8;" +
               "-fx-border-radius: 8;" +
               "-fx-border-color: " + (hovered ? "#E24B4A" : "#334155") + ";" +
               "-fx-border-width: 1;" +
               "-fx-cursor: hand;";
    }

    private Button styledButton(String text) {
        Button b = new Button(text);
        b.setStyle(
            "-fx-background-color: #1E293B;" +
            "-fx-text-fill: #94A3B8;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-border-radius: 8;" +
            "-fx-border-color: #334155;" +
            "-fx-border-width: 1;" +
            "-fx-padding: 7 16 7 16;" +
            "-fx-cursor: hand;"
        );
        b.setOnMouseEntered(e -> b.setStyle(b.getStyle().replace("#94A3B8", "#F1F5F9").replace("#1E293B", "#263147")));
        b.setOnMouseExited(e -> b.setStyle(
            "-fx-background-color: #1E293B;" +
            "-fx-text-fill: #94A3B8;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-border-radius: 8;" +
            "-fx-border-color: #334155;" +
            "-fx-border-width: 1;" +
            "-fx-padding: 7 16 7 16;" +
            "-fx-cursor: hand;"
        ));
        return b;
    }

    // ── Lifecycle ──────────────────────────────────────────────────────────
    @Override
    public void stop() {
        aiExecutor.shutdownNow();
    }

    public static void main(String[] args) {
        launch(args);
    }
}