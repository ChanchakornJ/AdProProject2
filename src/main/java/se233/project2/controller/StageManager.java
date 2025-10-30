package se233.project2.controller;

import javafx.scene.Scene;
import javafx.stage.Stage;
import se233.project2.view.GameStage;

public class StageManager {
    private int currentStage = 1;
    private Stage window;
    private Scene scene;

    public StageManager(Stage window) {
        this.window = window;
    }

    public void loadStage(int num) {
        GameStage stage = switch (num) {
            case 1 -> GameStage.stage1();

            default -> null;
        };

        if (stage != null) {
            scene = new Scene(stage, GameStage.WIDTH, GameStage.HEIGHT);
            scene.setOnKeyPressed(event -> stage.getKeys().add(event.getCode()));
            scene.setOnKeyReleased(event -> stage.getKeys().remove(event.getCode()));
            window.setScene(scene);

            new GameLoop(stage).start();
        }
    }
}

