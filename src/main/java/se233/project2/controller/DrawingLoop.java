package se233.project2.controller;

import se233.project2.model.GameCharacter;
import se233.project2.view.GameStage;

import java.util.List;
import javafx.animation.AnimationTimer;

public class DrawingLoop {
    private GameStage gameStage;
    private AnimationTimer timer;

    public DrawingLoop(GameStage gameStage) {
        this.gameStage = gameStage;
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                checkDrawCollisions(gameStage.getGameCharacterList());
                paint(gameStage.getGameCharacterList());
            }
        };
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    private void checkDrawCollisions(List<GameCharacter> list) {
        for (GameCharacter g : list) {
            g.checkReachGameWall();
            g.checkReachHighest();
            g.checkReachFloor();
        }
    }

    private void paint(List<GameCharacter> list) {
        for (GameCharacter g : list) {
            g.repaint(); // safe — same thread as UI
        }
    }
}
