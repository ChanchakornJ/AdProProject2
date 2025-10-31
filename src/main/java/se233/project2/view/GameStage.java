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
    private static Boss boss;
    private List<Minion> minions;

    public GameStage() {
        // minimal constructor
        gameCharacterList = new ArrayList<>();
        scoreList = new ArrayList<>();
        platforms = new ArrayList<>();
        minions = new ArrayList<>();
        keys = new Keys();
    }

    public Boss getBoss() { return boss; }
    public List<GameCharacter> getGameCharacterList() { return gameCharacterList; }
    public Keys getKeys() { return keys; }
    public List<Score> getScoreList() { return scoreList; }
    public List<Platform> getPlatforms() { return platforms; }
    public BulletManager getBulletManager() { return bulletManager; }
    public List<Minion> getMinions() { return minions; }

    public static GameStage stage1() {
        GameStage stage = new GameStage();

        // Platforms
        stage.getPlatforms().add(new Platform(0, 320, 500, 100)); // ground
        stage.getPlatforms().add(new Platform(0, 150, 150, 40));
        stage.getPlatforms().add(new Platform(0, 240, 150, 40));
        stage.getPlatforms().add(new Platform(180, 220, 40, 100));
        stage.getPlatforms().add(new Platform(310, 280, 40, 15));

        // Draw platforms
        for (Platform p : stage.getPlatforms()) {
            javafx.scene.shape.Rectangle rect =
                    new javafx.scene.shape.Rectangle(p.getX(), p.getY(), p.getWidth(), p.getHeight());
            rect.setFill(javafx.scene.paint.Color.BROWN);
            stage.getChildren().add(rect);
        }

        // Background
        stage.gameStageImg = new Image(Launcher.class.getResourceAsStream("assets/Stage1.png"));
        ImageView bg = new ImageView(stage.gameStageImg);
        bg.setFitWidth(WIDTH);
        bg.setFitHeight(HEIGHT);
        stage.getChildren().add(bg);

        // Bullet manager
        stage.bulletManager = new BulletManager(stage);

        // Boss
        stage.boss = new Boss(450, 60, 350, 350, 0.0, 10, "assets/Boss1.png", stage.bulletManager);
        stage.boss.setAnimationConfig(1, 1, 120);
        stage.getChildren().add(stage.boss);
        boss.addCannonPercent(0.2, 0.3);
        boss.addCannonPercent(0.6, 0.3);

        // Minions
        stage.getMinions().add(new Minion(500, 100, 40, 40, 1.5, 3, "assets/Minion1.png", stage.bulletManager));
        stage.getChildren().addAll(stage.getMinions());

        // Player
        GameCharacter player = new GameCharacter(
                0, 30, 30,
                "assets/Character.png",
                6, 6, 1, 65, 64,
                KeyCode.LEFT, KeyCode.RIGHT, KeyCode.UP, KeyCode.SPACE
        );
        player.setPlatforms(stage.getPlatforms());
        player.setBulletManager(stage.bulletManager);
        stage.getGameCharacterList().add(player);
        stage.getChildren().add(player);


        // Score positions
        stage.getScoreList().add(new Score(30, GROUND + 30));
        stage.getScoreList().add(new Score(WIDTH - 60, GROUND + 30));

        return stage;
    }

    public static GameStage stage2() {
        GameStage stage = new GameStage();
        // Platforms for stage2 (can be different)
        stage.getPlatforms().add(new Platform(0, 300, 500, 100)); // ground
        // add more if needed

        // Draw platforms
        for (Platform p : stage.getPlatforms()) {
            javafx.scene.shape.Rectangle rect =
                    new javafx.scene.shape.Rectangle(p.getX(), p.getY(), p.getWidth(), p.getHeight());
            rect.setFill(javafx.scene.paint.Color.BROWN);
            stage.getChildren().add(rect);
        }


        // Background
        stage.gameStageImg = new Image(Launcher.class.getResourceAsStream("assets/Stage2.png"));
        ImageView bg = new ImageView(stage.gameStageImg);
        bg.setFitWidth(WIDTH);
        bg.setFitHeight(HEIGHT);
        stage.getChildren().add(bg);


        // Bullet manager
        stage.bulletManager = new BulletManager(stage);

        // Boss
        stage.boss = new Boss(450, 60, 350, 350, 0.0, 10, "assets/Boss2.png", stage.bulletManager);
        stage.boss.setAnimationConfig(3, 1, 150);  // 6 frames, faster animation
        stage.getChildren().add(stage.boss);

        // Player
        GameCharacter player = new GameCharacter(
                0, 30, 30,
                "assets/Character.png",
                6, 6, 1, 65, 64,
                KeyCode.LEFT, KeyCode.RIGHT, KeyCode.UP, KeyCode.SPACE
        );
        player.setPlatforms(stage.getPlatforms());
        player.setBulletManager(stage.bulletManager);
        stage.getGameCharacterList().add(player);
        stage.getChildren().add(player);

        return stage;
    }

    public static GameStage stage3() {
        GameStage stage = new GameStage();
        // Platforms for stage2 (can be different)
        stage.getPlatforms().add(new Platform(0, 275, 500, 100)); // ground
        // add more if needed

        // Draw platforms
        for (Platform p : stage.getPlatforms()) {
            javafx.scene.shape.Rectangle rect =
                    new javafx.scene.shape.Rectangle(p.getX(), p.getY(), p.getWidth(), p.getHeight());
            rect.setFill(javafx.scene.paint.Color.BROWN);
            stage.getChildren().add(rect);
        }


        // Background
        stage.gameStageImg = new Image(Launcher.class.getResourceAsStream("assets/Stage3.png"));
        ImageView bg = new ImageView(stage.gameStageImg);
        bg.setFitWidth(WIDTH);
        bg.setFitHeight(HEIGHT);
        stage.getChildren().add(bg);


        // Bullet manager
        stage.bulletManager = new BulletManager(stage);

        // Boss
        stage.boss = new Boss(450, 60, 350, 350, 0.0, 10, "assets/Boss3.png", stage.bulletManager);
        stage.boss.setAnimationConfig(6, 1, 70);  // 6 frames, faster animation
        stage.getChildren().add(stage.boss);

        // Player
        GameCharacter player = new GameCharacter(
                0, 30, 30,
                "assets/Character.png",
                6, 6, 1, 65, 64,
                KeyCode.LEFT, KeyCode.RIGHT, KeyCode.UP, KeyCode.SPACE
        );
        player.setPlatforms(stage.getPlatforms());
        player.setBulletManager(stage.bulletManager);
        stage.getGameCharacterList().add(player);
        stage.getChildren().add(player);

        return stage;
    }
}
