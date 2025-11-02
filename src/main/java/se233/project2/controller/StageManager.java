package se233.project2.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import se233.project2.model.GameCharacter;
import se233.project2.view.*;

public class StageManager {
    private int currentStage = 1;
    private Stage window;
    private Scene scene;
    private GameCharacter player;
    private HeartsUI heartsUI;
    private GameLoop gameLoop;
    private SessionScoreUI sessionScoreUI;

    public StageManager(Stage window) {
        this.window = window;
    }

    public void loadStage(int num) {

        if (player == null) {
            player = new GameCharacter(
                    0, 30, 30, "assets/Character.png",
                    32, 16, 2, 65, 64,
                    KeyCode.LEFT, KeyCode.RIGHT, KeyCode.UP,KeyCode.DOWN, KeyCode.SPACE
            );
        }
        if (heartsUI == null) {
            heartsUI = new HeartsUI(3);
        }
        if (sessionScoreUI == null) {
            sessionScoreUI = new SessionScoreUI();
        }

        currentStage = num;

        switch (num) {

            case 0:
                MenuPage menuPage = new MenuPage(this);
                scene = new Scene(menuPage, GameStage.WIDTH, GameStage.HEIGHT);
                window.setScene(scene);
                break;

            case 1:
                if (gameLoop != null) gameLoop.stop();

                GameStage stage1 = GameStage.stage1();
                player.setStageManager(this);
                player.resetLives();
                player.resetSessionScore();
                heartsUI.updateHearts(3);
                player.respawnToStart();

                sessionScoreUI.update(0);

                setupStage(stage1);
                break;

            case 2:
                if (gameLoop != null) gameLoop.stop();

                GameStage stage2 = GameStage.stage2();
                player.setStageManager(this);
                heartsUI.updateHearts(player.getLives());
                sessionScoreUI.update(player.getSessionScore());
                player.respawnToStart();

                setupStage(stage2);
                break;

            case 3: 
                if (gameLoop != null) gameLoop.stop();

                GameStage stage3 = GameStage.stage3();
                player.setStageManager(this);
                heartsUI.updateHearts(player.getLives());
                sessionScoreUI.update(player.getSessionScore());
                player.respawnToStart();

                setupStage(stage3);
                break;

            case 4:
                GameOverPage gameOverPage = new GameOverPage(this);
                scene = new Scene(gameOverPage, GameStage.WIDTH, GameStage.HEIGHT);
                window.setScene(scene);
                break;
        }
    }

    private void setupStage(GameStage stage) {
        scene = new Scene(stage, GameStage.WIDTH, GameStage.HEIGHT);
        window.setScene(scene);

        stage.getKeys().clear();
        scene.setOnKeyPressed(e -> stage.getKeys().add(e.getCode()));
        scene.setOnKeyReleased(e -> stage.getKeys().remove(e.getCode()));
        scene.setOnKeyReleased(e -> {
            stage.getKeys().remove(e.getCode());
            if (e.getCode() == player.getDownKey()) {
                player.isProne = false;
                player.canProne = false;
            }
        });

        stage.addPlayer(player);
        stage.getChildren().add(heartsUI);
        stage.getChildren().add(sessionScoreUI);

        gameLoop = new GameLoop(stage);
        gameLoop.start();
    }

    public int getCurrentStage() { return currentStage; }
    public HeartsUI getHeartsUI() { return heartsUI; }
    public GameCharacter getPlayer() { return player; }
    public int getTotalScore() { return player != null ? player.getTotalScore() : 0; }
    public SessionScoreUI getScoreUI() { return sessionScoreUI; }
}
