package se233.project2.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import se233.project2.Launcher;

public class GameWinPage extends StackPane {
    private Font arcadeFont;

    public GameWinPage()  {
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
                throw new Exception("Background image unavailable.");
            }

            ImageView bg = new ImageView(bgImage);
            bg.setFitWidth(GameStage.WIDTH);
            bg.setFitHeight(GameStage.HEIGHT);
            bg.setStyle("-fx-background-color: #222222;");
            getChildren().add(bg);

        } catch (Exception e) {
//            throw new Exception("Background image unavailable.", e);
        }

        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);"); // 60% black overlay
        overlay.setPrefSize(GameStage.WIDTH, GameStage.HEIGHT);
        getChildren().add(overlay);

        // Title
        Text title = new Text("YOU WIN!");
        title.setFont(Font.font(arcadeFont.getFamily(), 48));
        title.setFill(Color.ORANGE);
        Text text = new Text("You passed all 3 stages.");
        text.setFont(Font.font(arcadeFont.getFamily(), 30));
        text.setFill(Color.ORANGE);

    }
    }
