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
    private long lastShotTime = 0;
    private BulletManager bulletManager;
    private List<double[]> cannons = new ArrayList<>();




    public Boss(double x, double y, double w, double h, double speed, int hp, String imgName, BulletManager bulletManager){
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.speed = speed;
        this.hp = hp;
        this.bulletManager = bulletManager;


        this.bossImage = new Image(Launcher.class.getResourceAsStream(imgName));
        ImageView sprite = new ImageView(bossImage);
        sprite.setFitWidth(w);
        sprite.setFitHeight(h);
        getChildren().add(sprite);
        setTranslateX(x);
        setTranslateY(y);
    }

    void update() {
        x += speed;
        long now = System.currentTimeMillis();
        if (now - lastShotTime > 1500) {
            double bulletX = getTranslateX();
            double bulletY = getTranslateY() + getHeight() / 2;
            bulletManager.shoot(bulletX, bulletY, false, false);
            lastShotTime = now;
        }
        if (x < 400 || x > 700) speed *= -1; // patrol
    }
    public void addCannon(double offsetX, double offsetY) {
        cannons.add(new double[]{offsetX, offsetY});
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




}
