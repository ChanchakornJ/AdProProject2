package se233.project2.model;

public class Bullet {
    double x, y, speedX, speedY;
    boolean fromPlayer; // true if player bullet, false if boss bullet
    Bullet(double x, double y, double speedX, double speedY, boolean fromPlayer) {
        this.x = x;
        this.y = y;
        this.speedX = speedX;
        this.speedY = speedY;
        this.fromPlayer = fromPlayer;
    }
}