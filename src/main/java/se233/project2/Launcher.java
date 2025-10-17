package se233.project2;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import se233.project2.controller.DrawingLoop;
import se233.project2.controller.GameLoop;
import se233.project2.model.ContraBossFight;
import se233.project2.view.GameStage;
import se233.project2.model.Bullet;


import java.util.Iterator;
import java.util.Random;

public class Launcher extends Application {
    public static void main(String[] args) { launch(args); }


        @Override
        public void start(Stage primaryStage) {
            GameStage gameStage = new GameStage();
            GameLoop gameLoop = new GameLoop(gameStage);
            DrawingLoop drawingLoop = new DrawingLoop(gameStage);
            Scene scene = new Scene(gameStage, gameStage.WIDTH, gameStage.HEIGHT);
            scene.setOnKeyPressed(event-> gameStage.getKeys().add(event.getCode()));
            scene.setOnKeyReleased(event ->  gameStage.getKeys().remove(event.getCode()));
            primaryStage.setTitle("2D Platformer");
            primaryStage.setScene(scene);
            primaryStage.show();
            (new Thread(gameLoop)).start();
            (new Thread(drawingLoop)).start();
        }
        Pane root = new Pane();
        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();


        // Key events
//        scene.setOnKeyPressed(e -> {
//            if (e.getCode() == KeyCode.LEFT) left = true;
//            if (e.getCode() == KeyCode.RIGHT) right = true;
//            if (e.getCode() == KeyCode.UP && onGround) {
//                velocityY = JUMP_STRENGTH;
//                onGround = false;
//            }
//            if (e.getCode() == KeyCode.SPACE) shooting = true;
//        });
//
//        scene.setOnKeyReleased(e -> {
//            if (e.getCode() == KeyCode.LEFT) left = false;
//            if (e.getCode() == KeyCode.RIGHT) right = false;
//            if (e.getCode() == KeyCode.SPACE) shooting = false;
//        });

        // Game loop
        AnimationTimer gameLoop = new AnimationTimer() {
            long lastShoot = 0;
            long lastBossShoot = 0;
            Random rand = new Random();

            @Override
            public void handle(long now) {
//                if (gameOver || gameWin) {
//                    gc.setFill(Color.BLACK);
//                    gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
//                    gc.setFill(Color.WHITE);
//                    if (gameWin) gc.fillText("YOU WIN! Boss Defeated!", 320, 300);
//                    else gc.fillText("GAME OVER! You Died!", 320, 300);
//                    return;
//                }



                // Player shooting (limit fire rate)
                if (shooting && now - lastShoot > 200_000_000) { // 200ms
                    bullets.add(new ContraBossFight.Bullet(playerX + playerWidth, playerY - playerHeight / 2,
                            10, 0, true));
                    lastShoot = now;
                }

                // Boss update
                if (boss.alive) {
                    boss.update();

                    // Boss shooting every 1 sec
                    if (now - lastBossShoot > 1_000_000_000) {
                        bullets.add(new ContraBossFight.Bullet(boss.x, boss.y - boss.h / 2,
                                -6, rand.nextInt(5) - 2, false));
                        lastBossShoot = now;
                    }
                }

                // Update bullets
                Iterator<ContraBossFight.Bullet> bulletIt = bullets.iterator();
                while (bulletIt.hasNext()) {
                    ContraBossFight.Bullet b = bulletIt.next();
                    b.x += b.speedX;
                    b.y += b.speedY;

                    // Remove off-screen
                    if (b.x < 0 || b.x > canvas.getWidth() || b.y < 0 || b.y > canvas.getHeight()) {
                        bulletIt.remove();
                        continue;
                    }

                    if (b.fromPlayer && boss.alive) {
                        // Player bullet hits boss
                        if (b.x < boss.x + boss.w &&
                                b.x + 10 > boss.x &&
                                b.y < boss.y &&
                                b.y + 5 > boss.y - boss.h) {
                            boss.hp--;
                            bulletIt.remove();
                            if (boss.hp <= 0) {
                                boss.alive = false;
                                gameWin = true;
                            }
                            continue;
                        }
                    } else if (!b.fromPlayer) {
                        // Boss bullet hits player
                        if (b.x < playerX + playerWidth &&
                                b.x + 10 > playerX &&
                                b.y < playerY &&
                                b.y + 5 > playerY - playerHeight) {
                            gameOver = true;
                            bulletIt.remove();
                            continue;
                        }
                    }
                }

                // Render
                gc.setFill(Color.BLACK);
                gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

                // Draw player
                gc.setFill(Color.BLUE);
                gc.fillRect(playerX, playerY - playerHeight, playerWidth, playerHeight);

                // Draw bullets
                for (ContraBossFight.Bullet b : bullets) {
                    gc.setFill(b.fromPlayer ? Color.YELLOW : Color.ORANGE);
                    gc.fillOval(b.x, b.y, 10, 5);
                }

                // Draw boss
                if (boss.alive) {
                    boss.draw(gc);
                }
            }
        };
        gameLoop.start();
    }
}