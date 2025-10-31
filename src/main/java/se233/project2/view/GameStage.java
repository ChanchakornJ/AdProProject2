package se233.project2.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import se233.project2.Launcher;
import se233.project2.controller.BulletManager;
import se233.project2.model.*;

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
    private BulletManager bulletManager;
    private Boss boss;
    private List<Minion> minions;




    public GameStage() {
        gameStageImg = new Image(Launcher.class.getResourceAsStream("assets/Stage1.png"));
        ImageView backgroundImg = new ImageView(gameStageImg);
        backgroundImg.setFitHeight(HEIGHT);
        backgroundImg.setFitWidth(WIDTH);
        gameCharacterList = new ArrayList<>();
        scoreList = new ArrayList<>();
        platforms = new ArrayList<>();
        minions = new ArrayList<>();
        keys = new Keys();
        platforms = new ArrayList<>();
        platforms.add(new Platform(0, 320, 500, 100)); // ground
        platforms.add(new Platform(0, 150, 150, 40));
        platforms.add(new Platform(0, 240, 150, 40));
        platforms.add(new Platform(180, 220, 40, 100));
        platforms.add(new Platform(310, 280, 40, 15));


        for (Platform p : platforms) {
            javafx.scene.shape.Rectangle rect =
                    new javafx.scene.shape.Rectangle(p.getX(), p.getY(), p.getWidth(), p.getHeight());
            rect.setFill(javafx.scene.paint.Color.BROWN);
            this.getChildren().add(rect);
        }

        gameCharacterList.add(new GameCharacter(
                0, 30, 30,
                "assets/Character.png",
                6, 6, 1, 65, 64,
                KeyCode.LEFT, KeyCode.RIGHT, KeyCode.UP, KeyCode.SPACE
        ));
        gameCharacterList.get(0).setPlatforms(platforms);

        bulletManager = new BulletManager(this);
        gameCharacterList.get(0).setBulletManager(bulletManager);
        minions.add(new Minion(500, 100, 40, 40, 1.5, 3, "assets/Minion1.png", bulletManager));


        boss = new Boss(450.0, 60.0, 350.0, 350.0, 0.0, 10, "assets/Boss1.png", bulletManager);
        scoreList.add(new Score(30, GROUND + 30));
        scoreList.add(new Score(GameStage.WIDTH - 60, GROUND + 30));
        getChildren().add(backgroundImg);
        getChildren().addAll(gameCharacterList);
        getChildren().add(boss);
        getChildren().addAll(minions);




    }
    public Boss getBoss() {
        return boss;
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
    public BulletManager getBulletManager() {
        return bulletManager;
    }
    public List<Minion> getMinions() { return minions; }

    public static GameStage stage1() {
        return new GameStage(); 
    }





}

