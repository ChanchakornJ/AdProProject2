package se233.project2.controller;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import se233.project2.Launcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BulletManager {
    private final Pane root;
    private final List<ImageView> bullets = new ArrayList<>();
    private final Image bulletImg = new Image(Launcher.class.getResourceAsStream("assets/Bullet.png"));
    private final double bulletSpeed = 6;

    public BulletManager(Pane root) {
        this.root = root;
    }

    public void shoot(double startX, double startY, boolean facingRight) {
        ImageView bullet = new ImageView(bulletImg);
        bullet.setFitWidth(10);
        bullet.setFitHeight(5);
        bullet.setX(startX);
        bullet.setY(startY);
        root.getChildren().add(bullet);
        bullets.add(bullet);
        bullet.setUserData(facingRight ? bulletSpeed : -bulletSpeed);
    }

    public void update() {
        Iterator<ImageView> it = bullets.iterator();
        while (it.hasNext()) {
            ImageView b = it.next();
            double speed = (double) b.getUserData();
            b.setX(b.getX() + speed);

            if (b.getX() < 0 || b.getX() > root.getWidth()) {
                root.getChildren().remove(b);
                it.remove();
            }
        }
    }
    public void removeBullet(ImageView b) {
        root.getChildren().remove(b);
        bullets.remove(b);
    }


    public List<ImageView> getBullets() {
        return bullets;
    }
}
