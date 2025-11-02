package se233.project2.model;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
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
    private Rectangle2D hitBox;
    private List<HitPart> hitParts = new ArrayList<>();
    private String pathwayImage;




    public Boss(double x, double y, double w, double h, double speed, int hp, String imgName, BulletManager bulletManager, String pathwayImage){
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.speed = speed;
        this.hp = hp;
        this.bulletManager = bulletManager;
        this.pathwayImage = pathwayImage;


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
        double nx = getTranslateX() + speed;
        if (nx < 400 || nx > 700) speed *= -1;
        setTranslateX(getTranslateX() + speed);

        updateHitBox();
        updateHitParts();

        long now = System.currentTimeMillis();
        if (now - lastShotTime > 1500) {
            for (double[] c : cannons) {
                double bulletX = getTranslateX() + c[0];
                double bulletY = getTranslateY() + c[1];
                bulletManager.shoot(bulletX, bulletY, false, false);
            }
            lastShotTime = now;
        }
        for (HitPart p : hitParts) {
            if (p.isDestroyed()) continue;
            Rectangle r = new Rectangle(
                    p.getBox().getMinX() - getTranslateX(),
                    p.getBox().getMinY() - getTranslateY(),
                    p.getBox().getWidth(),
                    p.getBox().getHeight()
            );


            r.setStroke(Color.RED);
            r.setFill(Color.TRANSPARENT);
            if (!getChildren().contains(r)) getChildren().add(r);
        }

        if (alive && allHitPartsDestroyed()) {
            die();
            return;
        }


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




    public Rectangle2D getHitBox() {
        return hitBox;
    }
    private void updateHitBox() {
        hitBox = new Rectangle2D(
                getTranslateX() + sprite.getFitWidth() * 0.25,
                getTranslateY() + sprite.getFitHeight() * 0.3,
                sprite.getFitWidth() * 0.5,
                sprite.getFitHeight() * 0.4
        );
    }

    public static class HitPart {
        private Rectangle2D box;
        private int hp = 3;
        private boolean destroyed = false;
        double offsetX, offsetY, widthPercent, heightPercent;

        public Rectangle2D getBox() { return box; }
        public boolean isDestroyed() { return destroyed; }
        public int getHp() { return hp; }

        public void takeHit() {
            if (destroyed) return;
            hp--;
            if (hp <= 0) destroyed = true;
        }
    }

    public void addHitPart(double offsetX, double offsetY, double widthPercent, double heightPercent, int hp) {
        HitPart part = new HitPart();
        part.offsetX = offsetX;
        part.offsetY = offsetY;
        part.widthPercent = widthPercent;
        part.heightPercent = heightPercent;
        part.hp = hp;
        updateHitPart(part);
        hitParts.add(part);
    }
    private void updateHitParts() {
        for (HitPart part : hitParts) {
            if (part.destroyed) continue;
            updateHitPart(part);
        }
    }

    private void updateHitPart(HitPart part) {
        part.box = new Rectangle2D(
                getTranslateX() + w * part.offsetX,
                getTranslateY() + h * part.offsetY,
                w * part.widthPercent,
                h * part.heightPercent
        );
    }
    public void explodeAt(Rectangle2D box) {
        ImageView explosion = new ImageView(
                new Image(Launcher.class.getResourceAsStream("assets/BossBullet.png"))
        );

        explosion.setX(box.getMinX());
        explosion.setY(box.getMinY());
        explosion.setFitWidth(box.getWidth());
        explosion.setFitHeight(box.getHeight());

        Pane root = (Pane) getParent(); // GameStage
        root.getChildren().add(explosion);

        new Timeline(
                new KeyFrame(Duration.millis(400),
                        e -> root.getChildren().remove(explosion))
        ).play();
    }

    public boolean allHitPartsDestroyed() {
        return hitParts.stream().allMatch(p -> p.destroyed);
    }








//    void draw(GraphicsContext gc) {
//        gc.setFill(Color.RED);
//        gc.fillRect(x, y - h, w, h);
//        gc.setFill(Color.WHITE);
//        gc.fillText("Boss HP: " + hp, x, y - h - 10);
//    }
    public void takeDamage() {
        if (allHitPartsDestroyed()) {
            hp--;
            if (hp <= 0) die();
        }
    }

    public void die() {
        alive = false;

        if (getParent() instanceof Pane parent) {
            parent.getChildren().remove(this);

            ImageView pathway = new ImageView(new Image(
                    Launcher.class.getResourceAsStream(pathwayImage)
            ));
            pathway.setX(350);
            pathway.setY(250);
            this.setVisible(false);
            parent.getChildren().add(pathway);
            pathway.toFront();
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
    public void addCannonPercent(double percentX, double percentY) {
        cannons.add(new double[]{percentX * w, percentY * h});
    }


    public boolean isAlive() {
        return alive;
    }

    public double getBossWidth() {
        return sprite.getFitWidth();
    }

    public List<HitPart> getHitParts() {
        return hitParts;
    }

    public Rectangle2D getSpriteBounds() {
        var b = sprite.localToScene(sprite.getBoundsInLocal());
        return new Rectangle2D(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
    }







}
