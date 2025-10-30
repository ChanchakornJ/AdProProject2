package se233.project2.model;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import se233.project2.Launcher;

public class Minion extends Pane {
    private double x, y, w, h, speed;
    private int hp;
    private boolean alive = true;
    private Image minion;

    public Minion(double x, double y, double w, double h, double speed, int hp, String img) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.speed = speed;
        this.hp = hp;

        minion = new Image(Launcher.class.getResourceAsStream(img));
        ImageView sprite = new ImageView(minion);
        sprite.setFitWidth(w);
        sprite.setFitHeight(h);
        getChildren().add(sprite);
        setTranslateX(x);
        setTranslateY(y);
    }

    public void update() {
        x += Math.sin(System.currentTimeMillis() * 0.001) * speed;
        setTranslateX(x);
    }

    public void takeDamage() {
        hp--;
        if (hp <= 0) die();
    }

    public void die() {
        alive = false;
        ((Pane)getParent()).getChildren().remove(this);
    }

    public boolean isAlive() { return alive; }

    public Rectangle2D getHitBox() {
        return new Rectangle2D(getTranslateX(), getTranslateY(), w, h);
    }
}
