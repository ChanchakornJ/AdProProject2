package se233.project2.model;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import se233.project2.Launcher;

public class FlyingMinion extends Minion {
    private double x, y, w, h;
    private double vx, vy;
//    private double baseY;
//    private double amplitude = 30; // ความสูงของการบินขึ้นลง
//    private double speed = 2;
//    private double angle = 0;
    private boolean alive = true;

    private ImageView sprite;
    private Image image;
    private int totalCols = 4; // number of frames in row
    private int totalRows = 1;
    private double frameWidth;
    private double frameHeight;
    private int frame = 0;
    private long lastFrameTime = 0;
    private long frameDelay = 120;

    public FlyingMinion(double startX, double startY, double width, double height, String imgPath) {
        super();
        this.x = startX;
        this.y = startY;
        this.w = width;
        this.h = height;
//        this.baseY = startY;
        this.vx = 1 + Math.random() * 2;
        this.vy = 0.5 + Math.random() * 1.5;
        if (Math.random() < 0.5) vx = -vx;
        if (Math.random() < 0.5) vy = -vy;

        this.image = new Image(Launcher.class.getResourceAsStream(imgPath));
        sprite = new ImageView(image);
        sprite.setFitWidth(w);
        sprite.setFitHeight(h);
        getChildren().add(sprite);
        setTranslateX(x);
        setTranslateY(y);
    }

//    public void update() {
//        if (!alive) return;
//
//        // บินจากขวาไปซ้าย
//        x -= speed;
//
//        // เคลื่อนขึ้นลง (sin wave)
//        angle += 0.1;
//        y = baseY + Math.sin(angle) * amplitude;
//
//        // ถ้าออกจอซ้าย → วนกลับขวาใหม่
//        if (x < -w) {
//            x = 800 + Math.random() * 200; // เริ่มใหม่ทางขวา
//            baseY = 100 + Math.random() * 200; // random ความสูง
//        }
//
//        setTranslateX(x);
//        setTranslateY(y);
//    }

    public void update() {
        if (!alive) return;

        // เคลื่อนที่แบบสุ่ม (bounce when hitting boundaries)
        x += vx;
        y += vy;

        if (x < 0 || x + w > 800) vx = -vx; // bounce X
        if (y < 0 || y + h > 400) vy = -vy; // bounce Y

        setTranslateX(x);
        setTranslateY(y);

        // Animate sprite sheet
        animate();
    }

    private void animate() {
        if (frameWidth == 0 || frameHeight == 0) {
            frameWidth = image.getWidth() / totalCols;
            frameHeight = image.getHeight() / totalRows;
        }

        long now = System.currentTimeMillis();
        if (now - lastFrameTime < frameDelay) return;
        lastFrameTime = now;

        frame = (frame + 1) % totalCols; // loop through frames
        int col = frame % totalCols;
        int row = frame / totalCols;

        sprite.setViewport(new Rectangle2D(col * frameWidth, row * frameHeight, frameWidth, frameHeight));
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

    public void setAnimationConfig(int cols, int rows, long delay) {
        this.totalCols = cols;
        this.totalRows = rows;
        this.frameDelay = delay;
        frameWidth = image.getWidth() / totalCols;
        frameHeight = image.getHeight() / totalRows;
        sprite.setViewport(new Rectangle2D(0, 0, frameWidth, frameHeight));
    }
}
