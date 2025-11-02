package se233.project2.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import se233.project2.Launcher;

import java.util.ArrayList;
import java.util.List;

public class HeartsUI extends Pane {
    private final List<ImageView> hearts = new ArrayList<>();

    public HeartsUI(int lives) {
        Image img = new Image(Launcher.class.getResourceAsStream("assets/heart.png"));

        for (int i = 0; i < lives; i++) {
            ImageView heart = new ImageView(img);
            heart.setFitWidth(35);
            heart.setFitHeight(35);
            heart.setTranslateX(20 + (i * 40)); // space between hearts
            heart.setTranslateY(350); // bottom of screen
            hearts.add(heart);
            getChildren().add(heart);
        }
    }

    public void updateHearts(int livesLeft) {
        for (int i = 0; i < hearts.size(); i++) {
            hearts.get(i).setVisible(i < livesLeft);
        }
    }


}
