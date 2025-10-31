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
    private final Image bossBullet = new Image(Launcher.class.getResourceAsStream("assets/BossBullet.png"));
    private final double bulletSpeed = 6;

    public class BulletMeta {
        public double speed;
        public boolean fromPlayer;
        public BulletMeta(double speed, boolean fromPlayer) {
            this.speed = speed;
            this.fromPlayer = fromPlayer;
        }
    }

    public BulletManager(Pane root) {
        this.root = root;
    }

    public void shoot(double startX, double startY, boolean facingRight, boolean fromPlayer) {
        ImageView bullet;
        if(fromPlayer){
            bullet = new ImageView(bulletImg);
        }else{
            bullet = new ImageView(bossBullet);
        }
        bullet.setFitWidth(10);
        bullet.setFitHeight(5);
        bullet.setX(startX);
        bullet.setY(startY);
        root.getChildren().add(bullet);
        bullets.add(bullet);

        double speed = facingRight ? bulletSpeed : -bulletSpeed;
        bullet.setUserData(new BulletMeta(speed, fromPlayer));
    }

    public void update() {
        Iterator<ImageView> it = bullets.iterator();
        while (it.hasNext()) {
            ImageView b = it.next();
            BulletMeta meta = (BulletMeta) b.getUserData();
            b.setX(b.getX() + meta.speed);

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
