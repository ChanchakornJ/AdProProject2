package se233.project2.controller;

import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import se233.project2.model.Boss;
import se233.project2.model.FlyingMinion;
import se233.project2.model.GameCharacter;
import se233.project2.model.Minion;
import se233.project2.view.GameStage;

import java.util.List;

public class GameLoop {
    private final GameStage gameStage;
    private final AnimationTimer timer;
    private boolean minionSpawned = false;
    private boolean stageSwitched = false;


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

        Boss boss = gameStage.getBoss();
        if (boss != null && boss.isAlive()) {
            boss.update(); // pass in timestamp for animation
        }


        for (Minion m : gameStage.getMinions()) {
            if (m.isAlive()) m.update();
        }
        if (gameStage.getBoss() != null) {
            gameStage.getBoss().update();
        }

        if (!minionSpawned && !gameStage.getBulletManager().getBullets().isEmpty()) {
            for (Minion m : gameStage.getMinions()) {
                m.setActive(true);
            }
            minionSpawned = true;
        }

        for (Minion m : gameStage.getMinions()) {
            if (m instanceof FlyingMinion fm && fm.isAlive()) {
                fm.update();
            } else if (m.isAlive()) {
                m.update();
            }
        }


        checkBulletBossCollision(gameStage);
        checkBulletMinionCollision(gameStage);
        checkBulletPlayerCollision(gameStage);
        checkPlayerBossCollision(gameStage);
        checkStageTransition(gameStage);


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
            g.repaint();
        }
    }

    private void checkBulletBossCollision(GameStage gameStage) {
        Boss boss = gameStage.getBoss();
        Rectangle2D hitBox = boss.getHitBox();

        for (ImageView bullet : gameStage.getBulletManager().getBullets()) {
            BulletManager.BulletMeta meta = (BulletManager.BulletMeta) bullet.getUserData();
            if (!meta.fromPlayer) continue;

            Rectangle2D bulletBox = new Rectangle2D(
                    bullet.getX(), bullet.getY(),
                    bullet.getFitWidth(), bullet.getFitHeight()
            );

            if (bulletBox.intersects(hitBox)) {
                boss.takeDamage();
                gameStage.getBulletManager().removeBullet(bullet);
                break;
            }
        }
    }

    private void checkBulletMinionCollision(GameStage gameStage) {
        var bullets = gameStage.getBulletManager().getBullets();
        var minions = gameStage.getMinions();

        var bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            var bullet = bulletIterator.next();
            BulletManager.BulletMeta meta = (BulletManager.BulletMeta) bullet.getUserData();
            if (!meta.fromPlayer) continue;

            Rectangle2D bulletBox = new Rectangle2D(
                    bullet.getX(), bullet.getY(),
                    bullet.getFitWidth(), bullet.getFitHeight()
            );

            for (var minion : minions) {
                if (minion.isAlive() && bulletBox.intersects(minion.getHitBox())) {
                    minion.takeDamage();
                    bulletIterator.remove();
                    ((Pane) bullet.getParent()).getChildren().remove(bullet);
                    break;
                }
            }
        }
    }
    private void checkPlayerBossCollision(GameStage gameStage) {
        Boss boss = gameStage.getBoss();
        if (boss == null || !boss.isAlive()) return;

        for (GameCharacter player : gameStage.getGameCharacterList()) {
            Rectangle2D bossBox = boss.getHitBox();
            Rectangle2D playerBox = new Rectangle2D(
                    player.getTranslateX(),
                    player.getTranslateY(),
                    player.getCharacterWidth(),
                    player.getCharacterHeight()
            );

            if (playerBox.intersects(bossBox)) {
                if (player.getTranslateX() < boss.getTranslateX()) {
                    player.setTranslateX(boss.getTranslateX() - player.getCharacterWidth());
                } else {
                    player.setTranslateX(boss.getTranslateX() + boss.getWidth());
                }
            }
        }
    }


    private void checkBulletPlayerCollision(GameStage gameStage) {
        var bullets = gameStage.getBulletManager().getBullets();
        var players = gameStage.getGameCharacterList();

        for (var bullet : bullets) {
            BulletManager.BulletMeta meta = (BulletManager.BulletMeta) bullet.getUserData();
            if (meta.fromPlayer) continue;

            Rectangle2D bulletBox = new Rectangle2D(
                    bullet.getX(), bullet.getY(),
                    bullet.getFitWidth(), bullet.getFitHeight()
            );

            for (GameCharacter player : players) {
                Rectangle2D playerBox = new Rectangle2D(
                        player.getTranslateX(),
                        player.getTranslateY(),
                        player.getCharacterWidth(),
                        player.getCharacterHeight()
                );

                if (bulletBox.intersects(playerBox)) {
                    player.takeDamage();
                    gameStage.getBulletManager().removeBullet(bullet);
                    break;
                }
            }
        }

    }
    private void checkMinionPlayerCollision(GameStage gameStage) {
        var gameCharacters = gameStage.getGameCharacterList();
        var minions = gameStage.getMinions();

        for (var minion : minions) {
            if (minion instanceof FlyingMinion fm && fm.isAlive()) {
                Rectangle2D minionBox = fm.getHitBox();
                for (GameCharacter gameCharacter : gameCharacters) {
                    Rectangle2D playerBox = new Rectangle2D(
                            gameCharacter.getTranslateX(),
                            gameCharacter.getTranslateY(),
                            gameCharacter.getCharacterWidth(),
                            gameCharacter.getCharacterHeight()
                    );
                    if (minionBox.intersects(playerBox)) {
                        gameCharacter.respawn(); // หรือ player.takeDamage();
                        return;
                    }
                }
            }
        }
    }

    private void checkStageTransition(GameStage stage) {
        if (stageSwitched) return;

        GameCharacter player = stage.getGameCharacterList().get(0);

        if (player.getTranslateX() + player.getCharacterWidth() >= GameStage.WIDTH + 20) {
            stageSwitched = true;
            StageManager sm = player.getStageManager();
            sm.loadStage(sm.getCurrentStage() + 1);
        }
    }

}
