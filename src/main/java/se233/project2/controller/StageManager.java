package se233.project2.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import se233.project2.model.GameCharacter;
import se233.project2.view.GameOverPage;
import se233.project2.view.GameStage;
import se233.project2.view.HeartsUI;
import se233.project2.view.MenuPage;

public class StageManager {
    private int currentStage = 1;
    private Stage window;
    private Scene scene;
    private GameCharacter player;
    private HeartsUI heartsUI;
    private GameLoop gameLoop;


    public StageManager(Stage window) {
        this.window = window;

    }

    public void loadStage(int num) {
        if (player == null) {
            player = new GameCharacter(
                    0, 30, 30, "assets/Character.png",
                    6, 6, 1, 65, 64,
                    KeyCode.LEFT, KeyCode.RIGHT, KeyCode.UP, KeyCode.SPACE
            );
        }
        if (heartsUI == null) {
            heartsUI = new HeartsUI(3);
        }
        currentStage = num;

        switch (num) {
            case 0: // Menu page
                MenuPage menuPage = new MenuPage(this);
                scene = new Scene(menuPage, GameStage.WIDTH, GameStage.HEIGHT);
                window.setScene(scene);

                break;

            case 1:
                if (gameLoop != null) gameLoop.stop(); // ✅ stop old loop

                GameStage stage1 = GameStage.stage1();
                player.setStageManager(this);
                player.resetLives();
                heartsUI.updateHearts(3);
                player.respawnToStart();

                scene = new Scene(stage1, GameStage.WIDTH, GameStage.HEIGHT);
                window.setScene(scene);

                stage1.getKeys().clear();
                scene.setOnKeyPressed(null);
                scene.setOnKeyReleased(null);
                scene.setOnKeyPressed(e -> stage1.getKeys().add(e.getCode()));
                scene.setOnKeyReleased(e -> stage1.getKeys().remove(e.getCode()));

                stage1.addPlayer(player);
                stage1.getChildren().add(heartsUI);

                gameLoop = new GameLoop(stage1); // ✅ store the loop
                gameLoop.start();
                break;



            case 2:
                if (gameLoop != null) gameLoop.stop(); // ✅ stop old loop

                GameStage stage2 = GameStage.stage2();
                player.setStageManager(this);
                player.resetLives();
                heartsUI.updateHearts(3);
                player.respawnToStart();

                scene = new Scene(stage2, GameStage.WIDTH, GameStage.HEIGHT);
                window.setScene(scene);

                stage2.getKeys().clear();
                scene.setOnKeyPressed(e -> stage2.getKeys().add(e.getCode()));
                scene.setOnKeyReleased(e -> stage2.getKeys().remove(e.getCode()));

                stage2.addPlayer(player);
                stage2.getChildren().add(heartsUI);

                gameLoop = new GameLoop(stage2);
                gameLoop.start();
                break;

            case 3:
                if (gameLoop != null) gameLoop.stop();

                GameStage stage3 = GameStage.stage3();
                player.setStageManager(this);
                player.respawnToStart();

                scene = new Scene(stage3, GameStage.WIDTH, GameStage.HEIGHT);
                window.setScene(scene);

                stage3.getKeys().clear();
                scene.setOnKeyPressed(e -> stage3.getKeys().add(e.getCode()));
                scene.setOnKeyReleased(e -> stage3.getKeys().remove(e.getCode()));

                stage3.addPlayer(player);
                stage3.getChildren().add(heartsUI);

                gameLoop = new GameLoop(stage3); 
                gameLoop.start();
                break;

            case 4:
                GameOverPage gameOverPage = new GameOverPage(this);
                scene = new Scene(gameOverPage, GameStage.WIDTH, GameStage.HEIGHT);
                window.setScene(scene);

                break;

            default:
                throw new IllegalArgumentException("Invalid stage number: " + num);
        }
    }

    public int getCurrentStage() {
        return currentStage;
    }
    public HeartsUI getHeartsUI() {
        return heartsUI;
    }public GameCharacter getPlayer() { return player; }

}
