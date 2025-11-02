package se233.project2.view;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class SessionScoreUI extends Pane {
    private Label label;

    public SessionScoreUI() {
        label = new Label("Score: 0");
        label.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        label.setTextFill(Color.WHITE);

        setTranslateX(600); // next to hearts
        setTranslateY(10);

        getChildren().add(label);
    }

    public void update(int score) {
        label.setText("Score: " + score);
    }
}
