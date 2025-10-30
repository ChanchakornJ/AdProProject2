package se233.project2.controller;

import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import se233.project2.model.Boss;
import se233.project2.model.GameCharacter;
import se233.project2.view.GameStage;


import java.util.List;

public class GameLoop {
    private final GameStage gameStage;
    private final AnimationTimer timer;

    public GameLoop(GameStage gameStage) {
        this.gameStage = gameStage;
        this.timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update(gameStage.getGameCharacterList());
                checkCollisions(gameStage.getGameCharacterList());
                paint(gameStage.getGameCharacterList());
            }
        };
    }

    public void start() { timer.start(); }
    public void stop() { timer.stop(); }

    private void update(List<GameCharacter> list) {
        for (GameCharacter c : list) {
            boolean left = gameStage.getKeys().isPressed(c.getLeftKey());
            boolean right = gameStage.getKeys().isPressed(c.getRightKey());
            boolean up = gameStage.getKeys().isPressed(c.getUpKey());
            boolean shoot = gameStage.getKeys().isPressed(c.getShootKey());

            if (left && right) c.stop();
            else if (left) {
                c.getImageView().tick();
                c.moveLeft();
            } else if (right) {
                c.getImageView().tick();
                c.moveRight();
            } else {
                c.stop();
            }

            if (up) c.jump();
            if (left) c.setFacingRight(false);
            if (right) c.setFacingRight(true);
            if (shoot) c.shoot();

        }
        gameStage.getBulletManager().update();
        checkBulletBossCollision(gameStage);


    }

    private void checkCollisions(List<GameCharacter> list) {
        for (GameCharacter g : list) {
            g.checkReachGameWall();
            g.checkReachHighest();
            g.checkReachFloor();
        }
    }

    private void paint(List<GameCharacter> list) {
        for (GameCharacter g : list) {
            g.repaint(); // update translateX/Y and visuals once per frame
        }
    }
    private void checkBulletBossCollision(GameStage gameStage) {
        Boss boss = gameStage.getBoss();
        for (ImageView bullet : gameStage.getBulletManager().getBullets()) {
            Rectangle2D bossBox = boss.getHitBox();
            Rectangle2D bulletBox = new Rectangle2D(
                    bullet.getX(),
                    bullet.getY(),
                    bullet.getFitWidth(),
                    bullet.getFitHeight()
            );

            if (bossBox.intersects(bulletBox)) {
                boss.takeDamage();
                gameStage.getBulletManager().removeBullet(bullet);
                break;
            }
        }
    }


}
