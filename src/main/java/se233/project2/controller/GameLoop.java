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
import se233.project2.view.Score;

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
            boolean down = gameStage.getKeys().isPressed(c.getDownKey());

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
            if (down) {
                c.prone();
            } else {
                c.isProne = false;
            }
        }

        gameStage.getBulletManager().update();

        Boss boss = gameStage.getBoss();
        if (boss != null && boss.isAlive()) {
            boss.update();
        }


        for (Minion m : gameStage.getMinions()) {
            if (m.isAlive()) m.update();
        }
        for (Score s : gameStage.getScoreList()) {
            s.setPoint(gameStage.getGameCharacterList().get(0).getScore());
        }
        StageManager sm = gameStage.getGameCharacterList().get(0).getStageManager();
        if (sm.getScoreUI() != null) {
            sm.getScoreUI().update(gameStage.getGameCharacterList().get(0).getSessionScore());
        }


//        if (gameStage.getBoss() != null) {
//            gameStage.getBoss().update();
//        }

        if (!minionSpawned && !gameStage.getBulletManager().getBullets().isEmpty()) {
            for (Minion m : gameStage.getMinions()) {
                m.setActive(true);
            }
            minionSpawned = true;
        }

//        for (Minion m : gameStage.getMinions()) {
//            if (m instanceof FlyingMinion fm && fm.isAlive()) {
//                fm.update();
//            } else if (m.isAlive()) {
//                m.update();
//            }
//        }

        // Update all existing minions
        for (Minion m : gameStage.getMinions()) {
            if (m.isAlive()) m.update();
        }

        if (gameStage.getStageNumber() == 3 || gameStage.getStageNumber() == 2 ) {
            long flyingCount = gameStage.getMinions()
                    .stream()
                    .filter(m -> m instanceof FlyingMinion && m.isAlive())
                    .count();

            if (flyingCount < 8 && Math.random() < 0.02) {
                spawnFlyingMinion();
            }
        }


        checkBulletBossCollision(gameStage);
        checkBulletMinionCollision(gameStage);
        checkBulletPlayerCollision(gameStage);
        checkPlayerBossCollision(gameStage);
        checkStageTransition(gameStage);
        checkMinionPlayerCollision(gameStage);


    }

    private void spawnFlyingMinion() {
        FlyingMinion fm = new FlyingMinion(
                800 + (Math.random() *5) * 120,
                100 + (Math.random() *5) * 40,
                60, 60,
                "assets/FlyingMinion.png"
        );
        fm.setTranslateX(GameStage.WIDTH + Math.random() * 1000);
        fm.setTranslateY(500 + Math.random() * 500);

        gameStage.getMinions().add(fm);

        gameStage.getChildren().add(fm);
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
        if (boss == null || !boss.isAlive()) return;

        var bullets = gameStage.getBulletManager().getBullets();

        for (ImageView bullet : bullets) {
            BulletManager.BulletMeta meta = (BulletManager.BulletMeta) bullet.getUserData();
            if (!meta.fromPlayer) continue;

            Rectangle2D bulletBox = new Rectangle2D(
                    bullet.getX(), bullet.getY(),
                    bullet.getFitWidth(), bullet.getFitHeight()
            );

            for (Boss.HitPart part : boss.getHitParts()) {
                if (part.isDestroyed()) continue;

                if (bulletBox.intersects(part.getBox())) {
                    part.takeHit();
                    double effectX = bullet.getX() + bullet.getFitWidth() / 2;
                    double effectY = bullet.getY() + bullet.getFitHeight() / 2;
                    boss.showHitEffect(gameStage, effectX, effectY);

                    gameStage.getBulletManager().removeBullet(bullet);

                    if (part.isDestroyed()) {
                        boss.explodeAt(part.getBox());
                    }
                    boss.takeDamage();
                    return;
                }
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

        Rectangle2D bossBox = boss.getSpriteBounds();

        for (GameCharacter player : gameStage.getGameCharacterList()) {
            Rectangle2D playerBox = player.getSpriteBounds();

            if (playerBox.intersects(bossBox)) {
                double pushLeft  = playerBox.getMaxX() - bossBox.getMinX();
                double pushRight = bossBox.getMaxX() - playerBox.getMinX();

                if (pushLeft < pushRight) {
                    player.setTranslateX(player.getTranslateX() - pushLeft);
                } else {
                    player.setTranslateX(player.getTranslateX() + pushRight);
                }
                player.stopHorizontal();
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
                        gameCharacter.takeDamage();
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
