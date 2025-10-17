package se233.project2.model;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ContraBossFight extends Application {

    // Player variables
    double playerX = 100, playerY = 300;
    double playerWidth = 40, playerHeight = 60;
    double velocityX = 0, velocityY = 0;
    boolean onGround = true;

    // Game constants
    final double GRAVITY = 0.6;
    final double JUMP_STRENGTH = -12;
    final double MOVE_SPEED = 5;

    // Bullets
//    class Bullet {
//        double x, y, speedX, speedY;
//        boolean fromPlayer; // true if player bullet, false if boss bullet
//        Bullet(double x, double y, double speedX, double speedY, boolean fromPlayer) {
//            this.x = x;
//            this.y = y;
//            this.speedX = speedX;
//            this.speedY = speedY;
//            this.fromPlayer = fromPlayer;
//        }
//    }
    List<Bullet> bullets = new ArrayList<>();

    // Boss
    class Boss {
        double x = 500, y = 300, w = 120, h = 120;
        double speed = 2;
        int hp = 20;
        boolean alive = true;

        void update() {
            x += speed;
            if (x < 400 || x > 700) speed *= -1; // patrol
        }

        void draw(GraphicsContext gc) {
            gc.setFill(Color.RED);
            gc.fillRect(x, y - h, w, h);
            gc.setFill(Color.WHITE);
            gc.fillText("Boss HP: " + hp, x, y - h - 10);
        }
    }
    Boss boss = new Boss();

    // Key handling
    boolean left, right, shooting;
    boolean gameOver = false;
    boolean gameWin = false;

    @Override
    public void start(Stage stage) {
        Pane root = new Pane();
        Canvas canvas = new Canvas(800, 600);
        root.getChildren().add(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Contra Boss Fight (JavaFX)");
        stage.show();

        // Key events
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) left = true;
            if (e.getCode() == KeyCode.RIGHT) right = true;
            if (e.getCode() == KeyCode.UP && onGround) {
                velocityY = JUMP_STRENGTH;
                onGround = false;
            }
            if (e.getCode() == KeyCode.SPACE) shooting = true;
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT) left = false;
            if (e.getCode() == KeyCode.RIGHT) right = false;
            if (e.getCode() == KeyCode.SPACE) shooting = false;
        });

        // Game loop
        AnimationTimer gameLoop = new AnimationTimer() {
            long lastShoot = 0;
            long lastBossShoot = 0;
            Random rand = new Random();

            @Override
            public void handle(long now) {
                if (gameOver || gameWin) {
                    gc.setFill(Color.BLACK);
                    gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                    gc.setFill(Color.WHITE);
                    if (gameWin) gc.fillText("YOU WIN! Boss Defeated!", 320, 300);
                    else gc.fillText("GAME OVER! You Died!", 320, 300);
                    return;
                }

                // Player movement
                velocityX = 0;
                if (left) velocityX = -MOVE_SPEED;
                if (right) velocityX = MOVE_SPEED;
                playerX += velocityX;

                // Gravity + Jump
                playerY += velocityY;
                velocityY += GRAVITY;
                if (playerY >= 300) { // ground
                    playerY = 300;
                    velocityY = 0;
                    onGround = true;
                }

                // Player shooting (limit fire rate)
                if (shooting && now - lastShoot > 200_000_000) { // 200ms
                    bullets.add(new Bullet(playerX + playerWidth, playerY - playerHeight / 2,
                            10, 0, true));
                    lastShoot = now;
                }

                // Boss update
                if (boss.alive) {
                    boss.update();

                    // Boss shooting every 1 sec
                    if (now - lastBossShoot > 1_000_000_000) {
                        bullets.add(new Bullet(boss.x, boss.y - boss.h / 2,
                                -6, rand.nextInt(5) - 2, false));
                        lastBossShoot = now;
                    }
                }

                // Update bullets
                Iterator<Bullet> bulletIt = bullets.iterator();
                while (bulletIt.hasNext()) {
                    Bullet b = bulletIt.next();
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
                for (Bullet b : bullets) {
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

    public static void main(String[] args) {
        launch();
    }
}

