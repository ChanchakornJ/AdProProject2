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
                GameStage stage = GameStage.stage1();
                scene = new Scene(stage, GameStage.WIDTH, GameStage.HEIGHT);
                scene.setOnKeyPressed(event -> stage.getKeys().add(event.getCode()));
                scene.setOnKeyReleased(event -> stage.getKeys().remove(event.getCode()));
                window.setScene(scene);
                new GameLoop(stage).start();
                break;

            default:
                throw new IllegalArgumentException("Invalid stage number: " + num);
        }
    }
}
