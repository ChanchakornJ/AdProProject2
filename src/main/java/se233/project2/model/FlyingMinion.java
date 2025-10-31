package se233.project2.model;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import se233.project2.Launcher;

public class FlyingMinion extends Pane {
    private double x, y, w, h;
    private double baseY;
    private double amplitude = 30; // ความสูงของการบินขึ้นลง
    private double speed = 2;
    private double angle = 0;
    private boolean alive = true;
    private ImageView sprite;
    private Image image;

    public FlyingMinion(double startX, double startY, double width, double height, String imgPath) {
        this.x = startX;
        this.y = startY;
        this.w = width;
        this.h = height;
        this.baseY = startY;

        this.image = new Image(Launcher.class.getResourceAsStream(imgPath));
        sprite = new ImageView(image);
        sprite.setFitWidth(w);
        sprite.setFitHeight(h);
        getChildren().add(sprite);
        setTranslateX(x);
        setTranslateY(y);
    }

    public void update() {
        if (!alive) return;

        // บินจากขวาไปซ้าย
        x -= speed;

        // เคลื่อนขึ้นลง (sin wave)
        angle += 0.1;
        y = baseY + Math.sin(angle) * amplitude;

        // ถ้าออกจอซ้าย → วนกลับขวาใหม่
        if (x < -w) {
            x = 800 + Math.random() * 200; // เริ่มใหม่ทางขวา
            baseY = 100 + Math.random() * 200; // random ความสูง
        }

        setTranslateX(x);
        setTranslateY(y);
    }

    public Rectangle2D getHitBox() {
        return new Rectangle2D(getTranslateX(), getTranslateY(), w, h);
    }

    public void takeDamage() {
        alive = false;
        Pane parent = (Pane) getParent();
        if (parent != null) {
            parent.getChildren().remove(this);
        }
    }

    public boolean isAlive() { return alive; }
}
