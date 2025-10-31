package se233.project2.model;


import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import se233.project2.Launcher;
import se233.project2.controller.BulletManager;

import java.util.ArrayList;
import java.util.List;


public class Boss extends Pane {
    double x = 500, y = 300, w = 120, h = 120;
    double speed = 2;
    int hp = 20;
    boolean alive = true;
    private Image bossImage;
    private ImageView sprite;
    private long lastShotTime = 0;
    private BulletManager bulletManager;
    private List<double[]> cannons = new ArrayList<>();

    private int totalCols = 4;
    private int totalRows = 1;
    private double frameWidth;
    private double frameHeight;
    private int frame = 0;
    private long lastFrameTime = 0;
    private long frameDelay = 120;

    public Boss(double x, double y, double w, double h, double speed, int hp, String imgName, BulletManager bulletManager){
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.speed = speed;
        this.hp = hp;
        this.bulletManager = bulletManager;


        this.bossImage = new Image(Launcher.class.getResourceAsStream(imgName));
        this.sprite = new ImageView(bossImage);
        sprite.setFitWidth(w);
        sprite.setFitHeight(h);
        getChildren().add(sprite);
        setTranslateX(x);
        setTranslateY(y);
    }

    public void update() {
        animate();
        x += speed;
        if (x < 400 || x > 700) speed *= -1;
        setTranslateX(x);
        long now = System.currentTimeMillis();
        if (now - lastShotTime > 1500) {
            for (double[] c : cannons) {
                double bulletX = getTranslateX() + c[0];
                double bulletY = getTranslateY() + c[1];
                bulletManager.shoot(bulletX, bulletY, false, false);
            }
            lastShotTime = now;
        }

        if (x < 400 || x > 700) speed *= -1; // patrol
    }
    private void animate() {
        long now = System.currentTimeMillis();
        if (bossImage == null || sprite == null) return;

        if (now - lastFrameTime > frameDelay) {
            frame = (frame + 1) % totalCols;
            sprite.setViewport(new Rectangle2D(frame * frameWidth, 0, frameWidth, frameHeight));
            lastFrameTime = now;
        }
    }

    public void addCannon(double offsetX, double offsetY) {
        cannons.add(new double[]{offsetX, offsetY});
    }

    public Rectangle2D setHitBox(double hitX, double hitY){
        double hitW = w * 0.5;
        double hitH = h * 0.4;
        return new Rectangle2D(hitX, hitY, hitW, hitH);
    }
    public Rectangle2D getHitBox() {
        double hitX = getTranslateX() + w * 0.25;
        double hitY = getTranslateY() + h * 0.3;
        double hitW = w * 0.5;
        double hitH = h * 0.4;
        return new Rectangle2D(hitX, hitY, hitW, hitH);    }


    void draw(GraphicsContext gc) {
        gc.setFill(Color.RED);
        gc.fillRect(x, y - h, w, h);
        gc.setFill(Color.WHITE);
        gc.fillText("Boss HP: " + hp, x, y - h - 10);
    }
    public void takeDamage(){
        hp--;
        if (hp <=0){
            die();
        }
    }
    public void die() {
        alive = false;

        if (getParent() instanceof Pane parent) {
            parent.getChildren().remove(this);

            ImageView explosion = new ImageView(new Image(Launcher.class.getResourceAsStream("assets/Stage1Pathway.png"))
            );
            explosion.setX(getTranslateX());
            explosion.setY(getTranslateY());
            parent.getChildren().add(explosion);
        } else {
            System.err.println("Boss has no parent when dying!");
        }
    }


    public void setAnimationConfig(int cols, int rows, long delay) {
        this.totalCols = cols;
        this.totalRows = rows;
        this.frameDelay = delay;
        frameWidth = bossImage.getWidth() / totalCols;
        frameHeight = bossImage.getHeight() / totalRows;
        sprite.setViewport(new Rectangle2D(0, 0, frameWidth, frameHeight));
    }


}
