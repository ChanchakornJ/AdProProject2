package se233.project2.model;

public class Bullet {
    public double x;
    public double y;
    public double speedX;
    public double speedY;
    public boolean fromPlayer; // true if player bullet, false if boss bullet
    public Bullet(double x, double y, double speedX, double speedY, boolean fromPlayer) {
        this.x = x;
        this.y = y;
        this.speedX = speedX;
        this.speedY = speedY;
        this.fromPlayer = fromPlayer;
    }
}