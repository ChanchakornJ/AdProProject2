package se233.project2.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import se233.project2.Launcher;
import se233.project2.controller.StageManager;

public class MenuPage extends StackPane {
    private Font arcadeFont;

    public MenuPage(StageManager stageManager) {

        setPrefSize(GameStage.WIDTH, GameStage.HEIGHT);

        arcadeFont = Font.loadFont(getClass().getResourceAsStream("/se233/project2/assets/PressStart2P-Regular.ttf"), 24);
        if (arcadeFont == null) {
            arcadeFont = Font.font("Impact", 24);
        }
        // Background
        Image bgImage;
        try {
            bgImage = new Image(Launcher.class.getResourceAsStream("assets/Stage1.png"));
            if (bgImage == null) {
                throw new CustomException("Background image unavailable.");
            }

            ImageView bg = new ImageView(bgImage);
            bg.setFitWidth(GameStage.WIDTH);
            bg.setFitHeight(GameStage.HEIGHT);
            bg.setStyle("-fx-background-color: #222222;");
            getChildren().add(bg);

        } catch (Exception e) {
            e.printStackTrace();
        }


        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);"); // 60% black overlay
        overlay.setPrefSize(GameStage.WIDTH, GameStage.HEIGHT);
        getChildren().add(overlay);

        // Title
        Text title = new Text("CONTRA");
        title.setFont(Font.font(arcadeFont.getFamily(), 48));
        title.setFill(Color.ORANGE);

        // Buttons
        Button startBtn = new Button("Start Game");
        Button helpBtn = new Button("How to Play");
        Button exitBtn = new Button("Exit");
        Button stage2Btn = new Button("Stage 2");
        Button stage3Btn = new Button("Stage 3");

        // Style
        for (Button btn : new Button[]{startBtn, helpBtn, exitBtn, stage2Btn, stage3Btn}) {
            btn.setFont(Font.font(arcadeFont.getFamily(), 18));
            btn.setStyle(
                            "-fx-background-color: #031cc1;" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 10;" +
                            "-fx-cursor: hand;"
            );
            btn.setOnMouseEntered(e -> btn.setStyle(
                            "-fx-background-color: #01982d;" +
                            "-fx-text-fill: black;" +
                            "-fx-background-radius: 10;"
            ));
            btn.setOnMouseExited(e -> btn.setStyle(
                            "-fx-background-color: #031cc1;" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 10;"
            ));
        }

        // Button actions
        startBtn.setOnAction(e -> stageManager.loadStage(1));
        helpBtn.setOnAction(e -> showHelpPopup());
        exitBtn.setOnAction(e -> System.exit(0));
        stage2Btn.setOnAction(e -> stageManager.loadStage(2));
        stage3Btn.setOnAction(e -> stageManager.loadStage(3));

        // Layout
        VBox vbox = new VBox(20, title, startBtn, helpBtn, exitBtn, stage2Btn, stage3Btn);
        vbox.setAlignment(Pos.CENTER);

        getChildren().add(vbox);
    }

    private void showHelpPopup() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("How to Play");
        alert.setHeaderText("Contra Controls");
        alert.setContentText("""
                ← / → : Move left or right
                ↑ : Jump
                SPACE : Shoot
                Defeat enemies and survive!
                """);
        alert.showAndWait();
    }
}
