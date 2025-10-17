package se233.project2.controller;

import se233.project2.model.GameCharacter;
import se233.project2.view.GameStage;

import java.util.List;

public class DrawingLoop implements Runnable {
    private GameStage gameStage;
    private int frameRate;
    private float interval;
    private boolean running;
    public DrawingLoop(GameStage gameStage) {
        this.gameStage = gameStage;
        frameRate = 60;
        interval = 1000.0f / frameRate; // 1000 ms = 1 second
        running = true;
    }
    private void checkDrawCollisions(List<GameCharacter> gameCharacterList) {
        // อัพเดตการชนกำแพง/พื้น
        for (GameCharacter gameCharacter : gameCharacterList) {
            gameCharacter.checkReachGameWall();
            gameCharacter.checkReachHighest();
            gameCharacter.checkReachFloor();
        }

        // สมมุติ player อยู่ index 0 เสมอ
        if (gameCharacterList.size() > 0) {
            GameCharacter player = gameCharacterList.get(0);

            // เช็ค player vs enemies/boss
            for (int i = 1; i < gameCharacterList.size(); i++) {
                GameCharacter other = gameCharacterList.get(i);

                if (player.getBoundsInParent().intersects(other.getBoundsInParent())) {
                    if (!player.collided(other)) {
                        other.collided(player);
                    }
                }
            }
        }
    }

    private void paint(List<GameCharacter> gameCharacterList) {
        for (GameCharacter gameCharacter : gameCharacterList) {
            gameCharacter.repaint();
        }
    }
    @Override
    public void run() {
        while (running) {
            float time = System.currentTimeMillis();

            checkDrawCollisions(gameStage.getGameCharacterList());
            paint(gameStage.getGameCharacterList());

            time = System.currentTimeMillis() - time;
            if (time < interval) {
                try {
                    Thread.sleep((long) (interval - time));
                } catch (InterruptedException e) {
                }
            } else {
                try {
                    Thread.sleep((long) (interval - (interval % time)));
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
