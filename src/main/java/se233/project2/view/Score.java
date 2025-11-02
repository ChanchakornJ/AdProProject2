package se233.project2.view;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Score extends Pane {
    private static final Logger scoreLogger = LogManager.getLogger("ScoreLogger");

    Label point;
    public Score(int x, int y) {
        point = new Label("0");
        setTranslateX(x);
        setTranslateY(y);
        point.setFont(Font.font("Verdana", FontWeight.BOLD,30));
        point.setTextFill(Color.web("#FFF"));
        getChildren().addAll(point);
    }
    public void setPoint(int score) {
        this.point.setText(Integer.toString(score));
        scoreLogger.info("Score updated: {}", score);
    }
}
