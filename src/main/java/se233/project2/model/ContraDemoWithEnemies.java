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

public class ContraDemoWithEnemies extends Application {

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
    class Bullet {
        double x, y, speed = 10;
        Bullet(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
    List<Bullet> bullets = new ArrayList<>();

    // Enemies
    class Enemy {
        double x, y, w = 40, h = 60;
        double speed = 2;
        boolean alive = true;

        Enemy(double x, double y) {
            this.x = x;
            this.y = y;
        }

        void update() {
            x += speed;
            if (x < 100 || x > 700) speed *= -1; // patrol
        }
    }
    List<Enemy> enemies = new ArrayList<>();

    // Key handling
    boolean left, right, shooting;

    @Override
    public void start(Stage stage) {
        Pane root = new Pane();
        Canvas canvas = new Canvas(800, 600);
        root.getChildren().add(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Contra Demo with Enemies (JavaFX)");
        stage.show();

        // Add some enemies
        enemies.add(new Enemy(500, 300));
        enemies.add(new Enemy(650, 300));

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

            @Override
            public void handle(long now) {
                // Movement
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

                // Shooting (limit fire rate)
                if (shooting && now - lastShoot > 200_000_000) { // 200ms
                    bullets.add(new Bullet(playerX + playerWidth, playerY - playerHeight / 2));
                    lastShoot = now;
                }

                // Update bullets
                Iterator<Bullet> bulletIt = bullets.iterator();
                while (bulletIt.hasNext()) {
                    Bullet b = bulletIt.next();
                    b.x += b.speed;
                    if (b.x > canvas.getWidth()) {
                        bulletIt.remove();
                        continue;
                    }

                    // Check collision with enemies
                    for (Enemy enemy : enemies) {
                        if (enemy.alive &&
                                b.x < enemy.x + enemy.w &&
                                b.x + 10 > enemy.x &&
                                b.y < enemy.y &&
                                b.y + 5 > enemy.y - enemy.h) {
                            enemy.alive = false;
                            bulletIt.remove();
                            break;
                        }
                    }
                }

                // Update enemies
                for (Enemy enemy : enemies) {
                    if (enemy.alive) {
                        enemy.update();
                    }
                }

                // Render
                gc.setFill(Color.BLACK);
                gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

                // Draw player
                gc.setFill(Color.BLUE);
                gc.fillRect(playerX, playerY - playerHeight, playerWidth, playerHeight);

                // Draw bullets
                gc.setFill(Color.YELLOW);
                for (Bullet b : bullets) {
                    gc.fillOval(b.x, b.y, 10, 5);
                }

                // Draw enemies
                gc.setFill(Color.RED);
                for (Enemy enemy : enemies) {
                    if (enemy.alive) {
                        gc.fillRect(enemy.x, enemy.y - enemy.h, enemy.w, enemy.h);
                    }
                }
            }
        };
        gameLoop.start();
    }

    public static void main(String[] args) {
        launch();
    }
}

