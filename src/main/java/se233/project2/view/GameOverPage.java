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
import se233.project2.model.CustomException;

public class GameOverPage extends StackPane {
    private Font arcadeFont;

    public GameOverPage(StageManager stageManager) throws CustomException {

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
            throw new CustomException("Background image unavailable.", e);
        }

        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);"); // 60% black overlay
        overlay.setPrefSize(GameStage.WIDTH, GameStage.HEIGHT);
        getChildren().add(overlay);

        // Title
        Text title = new Text("GAME OVER!");
        title.setFont(Font.font(arcadeFont.getFamily(), 40));
        title.setFill(Color.ORANGE);
        Text text = new Text("You lost all 3 lives.");
        text.setFont(Font.font(arcadeFont.getFamily(), 25));
        text.setFill(Color.ORANGE);

        Button backButton = new Button("<- Back");
        for (Button btn : new Button[]{backButton}) {
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

        backButton.setOnAction(e -> stageManager.loadStage(0));

        VBox vbox = new VBox(20, title, text, backButton);
        vbox.setAlignment(Pos.CENTER);

        getChildren().add(vbox);

    }
}