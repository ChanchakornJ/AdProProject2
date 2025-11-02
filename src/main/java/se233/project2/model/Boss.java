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
import se233.project2.view.GameStage;

import java.util.ArrayList;
import java.util.List;


public class Boss extends Pane {
    double x = 500, y = 300, w = 120, h = 120;
    double speed = 2;
    int hp = 20;
    boolean alive = true;
    private Image bossImage;
    private BossSprite sprite;
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
    private String pathwayImage;private int sheetCols = 1;
    private int sheetRows = 1;
    private int startIndex = 0;
    private int endIndex = 0;






    public Boss(double x, double y, double w, double h, double speed, int hp,
                String imgName, BulletManager bulletManager, String pathwayImage) {

        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.speed = speed;
        this.hp = hp;
        this.bulletManager = bulletManager;
        this.pathwayImage = pathwayImage;

        Image img = new Image(Launcher.class.getResourceAsStream(imgName));
        sprite = new BossSprite(img);
        sprite.setFitWidth(w);
        sprite.setFitHeight(h);
        getChildren().add(sprite);

        setTranslateX(x);
        setTranslateY(y);
    }



    public void update() {
        sprite.tick();
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
            GameStage stage = (GameStage) getParent();
            GameCharacter player = stage.getGameCharacterList().get(0);
            player.addScore(100);
            

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
        Image spriteSheet = new Image(Launcher.class.getResourceAsStream("assets/Explosion.png"));
        ImageView explosion = new ImageView(spriteSheet);


        // 🔹 ใส่ข้อมูลพิกัดและขนาดของแต่ละเฟรม (ต้องใส่ให้ตรงกับ sprite sheet จริง)
        // ตัวอย่างการกำหนด rectangle ของแต่ละเฟรม
        Rectangle2D[] frames = {
                new Rectangle2D(0, 0, 8, 32),
                new Rectangle2D(9, 0, 8, 32),
                new Rectangle2D(18, 0, 8, 32),
                new Rectangle2D(27, 0, 8, 32),
                new Rectangle2D(36, 0, 16, 32),
                new Rectangle2D(53, 0, 32, 32),
                new Rectangle2D(86, 0, 24, 32),
                new Rectangle2D(111, 0, 16, 32)
        };


        explosion.setViewport(frames[0]);
        explosion.setViewport(frames[0]);

        explosion.setPreserveRatio(true);
        explosion.setFitWidth(frames[0].getWidth() * 30);   // 2 เท่าของเฟรม
        explosion.setFitHeight(frames[0].getHeight() * 30);

        explosion.setX(box.getMinX() + (box.getWidth() / 2) - (explosion.getFitWidth() / 2));
        explosion.setY(box.getMinY() + (box.getHeight() / 2) - (explosion.getFitHeight() / 2));

        Pane root = (Pane) getParent();
        root.getChildren().add(explosion);

        Timeline anim = new Timeline();
        for (int i = 0; i < frames.length; i++) {
            int index = i;
            anim.getKeyFrames().add(
                    new KeyFrame(Duration.millis(i * 100),
                            e -> {
                                Rectangle2D frame = frames[index];
                                explosion.setViewport(frame);
                                explosion.setX(box.getMinX() + (box.getWidth() / 2) - (frame.getWidth() / 2));
                                explosion.setY(box.getMinY() + (box.getHeight() / 2) - (frame.getHeight() / 2));
                            }
                    )
            );
        }

        anim.getKeyFrames().add(new KeyFrame(Duration.millis(frames.length * 100 + 100),
                e -> root.getChildren().remove(explosion)));

        anim.play();
    }


    public boolean allHitPartsDestroyed() {
        return hitParts.stream().allMatch(p -> p.destroyed);
    }

    public void showHitEffect(Pane root, double x, double y) {
        Image spriteSheet = new Image(Launcher.class.getResourceAsStream("assets/shootEffect.png"));

        int totalFrames = 3; // มี 3 เฟรมในแนวนอน
        double frameWidth = spriteSheet.getWidth() / totalFrames;
        double frameHeight = spriteSheet.getHeight();

        ImageView effect = new ImageView(spriteSheet);
        effect.setTranslateX(x - frameWidth / 2.0);
        effect.setTranslateY(y - frameHeight / 2.0);
        effect.setViewport(new Rectangle2D(0, 0, frameWidth, frameHeight));

        root.getChildren().add(effect);

        Timeline anim = new Timeline(
                new KeyFrame(Duration.millis(0), e -> effect.setViewport(new Rectangle2D(0, 0, frameWidth, frameHeight))),
                new KeyFrame(Duration.millis(80), e -> effect.setViewport(new Rectangle2D(frameWidth, 0, frameWidth, frameHeight))),
                new KeyFrame(Duration.millis(160), e -> effect.setViewport(new Rectangle2D(frameWidth * 2, 0, frameWidth, frameHeight))),
                new KeyFrame(Duration.millis(240), e -> root.getChildren().remove(effect))
        );
        anim.play();
    }

    public void takeDamage() {
        if (allHitPartsDestroyed()) {

                GameStage stage = (GameStage) getParent();
                GameCharacter player = stage.getGameCharacterList().get(0);
                player.addScore(100);
                player.addSessionScore(100);

                die();}

    }

    public void die() {
        alive = false;

        if (getParent() instanceof Pane parent) {
            parent.getChildren().remove(this);

            ImageView pathway = new ImageView(new Image(
                    Launcher.class.getResourceAsStream(pathwayImage)
            ));
            pathway.setX(455);
            pathway.setY(235);
            pathway.setFitHeight(160);
            pathway.setFitWidth(340);

            this.setVisible(false);

            GameStage stage = (GameStage) parent;

            GameCharacter player = stage.getGameCharacterList().get(0);

            int playerIndex = parent.getChildren().indexOf(player);

            parent.getChildren().add(playerIndex, pathway);
        }
    }




    public void setAnimationConfig(int cols, int rows, int startFrame, int endFrame, int delay) {
        if (sprite != null) {
            sprite.configure(cols, rows, startFrame, endFrame, delay);
        }
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
