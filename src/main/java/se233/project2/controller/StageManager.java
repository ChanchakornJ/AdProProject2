package se233.project2.controller;

import javafx.scene.Scene;
import javafx.stage.Stage;
import se233.project2.view.GameStage;
import se233.project2.view.MenuPage;

public class StageManager {
    private int currentStage = 1;
    private Stage window;
    private Scene scene;

    public StageManager(Stage window) {
        this.window = window;
    }

    public void loadStage(int num) {
        switch (num) {
            case 0: // Menu page
                MenuPage menuPage = new MenuPage(this);
                scene = new Scene(menuPage, GameStage.WIDTH, GameStage.HEIGHT);
                window.setScene(scene);
                break;

            case 1: // Game stage
                GameStage stage1 = GameStage.stage1();
                scene = new Scene(stage1, GameStage.WIDTH, GameStage.HEIGHT);
                scene.setOnKeyPressed(event -> stage1.getKeys().add(event.getCode()));
                scene.setOnKeyReleased(event -> stage1.getKeys().remove(event.getCode()));
                window.setScene(scene);
                new GameLoop(stage1).start();
                break;

            case 2: // Game stage
                GameStage stage2 = GameStage.stage2();
                scene = new Scene(stage2, GameStage.WIDTH, GameStage.HEIGHT);
                scene.setOnKeyPressed(event -> stage2.getKeys().add(event.getCode()));
                scene.setOnKeyReleased(event -> stage2.getKeys().remove(event.getCode()));
                window.setScene(scene);
                new GameLoop(stage2).start();
                break;

            case 3: // Game stage
                GameStage stage3 = GameStage.stage3();
                scene = new Scene(stage3, GameStage.WIDTH, GameStage.HEIGHT);
                scene.setOnKeyPressed(event -> stage3.getKeys().add(event.getCode()));
                scene.setOnKeyReleased(event -> stage3.getKeys().remove(event.getCode()));
                window.setScene(scene);
                new GameLoop(stage3).start();
                break;

            default:
                throw new IllegalArgumentException("Invalid stage number: " + num);
        }
    }
}
