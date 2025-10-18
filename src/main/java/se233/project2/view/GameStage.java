package se233.project2.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import se233.project2.Launcher;
import se233.project2.model.GameCharacter;
import se233.project2.model.Keys;
import se233.project2.model.Platform;

import java.util.ArrayList;
import java.util.List;

public class GameStage extends Pane {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 400;
    public final static int GROUND = HEIGHT - 50;
    private Image gameStageImg;
    private List<GameCharacter> gameCharacterList;
    private List<Score> scoreList;
    private Keys keys;
    private List<Platform> platforms;

    public GameStage() {
        platforms = new ArrayList<>();
        platforms.add(new Platform(0, GameStage.HEIGHT - 50, GameStage.WIDTH, 20)); // ground
        platforms.add(new Platform(0, 170, 150, 40));
        platforms.add(new Platform(0, 260, 150, 40));
        platforms.add(new Platform(150, 230, 100, 100));
        platforms.add(new Platform(300, 300, 80, 15));


        for (Platform p : platforms) {
            javafx.scene.shape.Rectangle rect =
                    new javafx.scene.shape.Rectangle(p.getX(), p.getY(), p.getWidth(), p.getHeight());
            rect.setFill(javafx.scene.paint.Color.BROWN);
            this.getChildren().add(rect);
        }

        gameCharacterList = new ArrayList<>();
        scoreList = new ArrayList();
        keys = new Keys();
        gameStageImg = new Image(Launcher.class.getResourceAsStream("assets/Stage1.png"));
        ImageView backgroundImg = new ImageView(gameStageImg);
        backgroundImg.setFitHeight(HEIGHT);
        backgroundImg.setFitWidth(WIDTH);
        gameCharacterList.add(new GameCharacter(0, 30, 30, "assets/Character.png", 6, 6, 1, 65, 64, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.UP));
        gameCharacterList.get(0).setPlatforms(platforms);
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

    public List<Platform> getPlatforms() {
        return platforms;
    }
}


