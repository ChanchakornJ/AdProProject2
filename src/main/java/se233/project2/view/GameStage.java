package se233.project2.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import se233.project2.Launcher;
import se233.project2.model.GameCharacter;
import se233.project2.model.Keys;

import java.util.ArrayList;
import java.util.List;

public class GameStage extends Pane {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 400;
    public final static int GROUND = 300;
    private Image gameStageImg;
    private List<GameCharacter> gameCharacterList;
    private List<Score> scoreList;
    private Keys keys;

    public GameStage() {
        gameCharacterList = new ArrayList<>();
        scoreList = new ArrayList();
        keys = new Keys();
        gameStageImg = new Image(Launcher.class.getResourceAsStream("assets/Stage1.png"));
        ImageView backgroundImg = new ImageView(gameStageImg);
        backgroundImg.setFitHeight(HEIGHT);
        backgroundImg.setFitWidth(WIDTH);
        gameCharacterList.add(new GameCharacter(0, 30, 30, "assets/Character.png", 10, 10, 1, 65, 64, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.UP));
        scoreList.add(new Score(30, GROUND + 30));
        scoreList.add(new Score(GameStage.WIDTH - 60, GROUND + 30));
        getChildren().add(backgroundImg);
        getChildren().addAll(gameCharacterList);
        getChildren().addAll(scoreList);
    }

    public List<GameCharacter> getGameCharacterList() {

        return gameCharacterList;
    }

    public Keys getKeys() {

        return keys;
    }
    public List<Score> getScoreList() {
        return scoreList;
    }

}


