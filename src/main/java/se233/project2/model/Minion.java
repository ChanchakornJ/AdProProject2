package se233.project2.model;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import se233.project2.Launcher;
import se233.project2.controller.BulletManager;

public class Minion extends Pane {
    private double x, y, w, h, speed;
    private int hp;
    private boolean alive = true;
    private Image minion;
    private boolean startedMoving = false;
    private boolean finishedWalking = false;
    private ImageView sprite;
    private int frame = 0;
    private int totalCols = 4;
    private double frameWidth;
    private double frameHeight;
    private long lastFrameTime = 0;
    private long lastShotTime = 0;
    private BulletManager bulletManager;
    private boolean active = false;



    public Minion(double x, double y, double w, double h, double speed, int hp, String img, BulletManager bulletManager){
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.speed = speed;
        this.hp = hp;

        minion = new Image(Launcher.class.getResourceAsStream(img));
        sprite = new ImageView(minion);

        frameWidth = minion.getWidth() / totalCols;
        frameHeight = minion.getHeight();
        sprite.setViewport(new Rectangle2D(0, 0, frameWidth, frameHeight));
        sprite.setFitWidth(w);
        sprite.setFitHeight(h);
        this.bulletManager = bulletManager;


        getChildren().add(sprite);
        setTranslateX(x);
        setTranslateY(y);
    }

    public Minion() {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.speed = speed;
        this.hp = hp;
    }

    public void update() {
        if (!active) return;

        if (!finishedWalking) {
            animateOnce();
        } else {
            shoot();
        }
    }


    private void animateOnce() {
        long now = System.currentTimeMillis();
        if (now - lastFrameTime > 150) {
            frame++;
            if (frame >= totalCols - 1) {
                frame = totalCols - 1;
                finishedWalking = true;
            }
            sprite.setViewport(new Rectangle2D(frame * frameWidth, 0, frameWidth, frameHeight));
            lastFrameTime = now;
        }
    }
    private void shoot() {
        long now = System.currentTimeMillis();
        if (now - lastShotTime > 1000) {
            double bulletX = getTranslateX() + w / 2;
            double bulletY = getTranslateY() + h / 2;
            bulletManager.shoot(bulletX, bulletY, false, false);
            lastShotTime = now;
        }
    }

    public void takeDamage() {
        hp--;
        if (hp <= 0) die();
    }

    private void die() {
        alive = false;
        Pane parent = (Pane) getParent();
        if (parent != null) {
            parent.getChildren().remove(this);
        }
    }
    public void setActive(boolean value) {
        this.active = value;
    }



    public boolean isAlive() { return alive; }

    public Rectangle2D getHitBox() {
        return new Rectangle2D(getTranslateX(), getTranslateY(), w, h);
    }
}