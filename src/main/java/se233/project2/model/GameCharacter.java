package se233.project2.model;


import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import se233.project2.Launcher;
import se233.project2.controller.BulletManager;
import se233.project2.controller.StageManager;
import se233.project2.view.GameStage;
import se233.project2.view.MenuPage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class GameCharacter extends Pane {
    private static final Logger logger = LogManager.getLogger(GameCharacter.class);
    private List<Platform> platforms;
    private Image characterImg;
    private AnimatedSprite imageView;
    private int x;
    private int y;
    private int startX;
    private int startY;
    private int characterWidth;
    private int characterHeight;
    private int score=0;
    private KeyCode leftKey;
    private KeyCode rightKey;
    private KeyCode upKey;
    private KeyCode shootKey;
    public int xVelocity = 0;
    int yVelocity = 0;
    int xAcceleration = 1;
    int yAcceleration = 1;
    int xMaxVelocity = 7;
    int yMaxVelocity = 16;
    public boolean isMoveLeft = false;
    public boolean isMoveRight = false;
    boolean isFalling = true;
    boolean canJump = false;
    public boolean isJumping = false;
    private int lastX;
    private int lastY;
    private int hp = 5;
    private int lives = 3;

    //Bullet
    private List<Bullet> bullets = new ArrayList<>();
    private double bulletSpeed = 5.0;
    private boolean facingRight = true;
    private BulletManager bulletManager;
    private long lastShotTime = 0;
    private static final long SHOOT_COOLDOWN = 200_000_000; // 0.1 second = 100 million nanoseconds



    public GameCharacter(int id, int x, int y, String imgName, int count, int column, int row, int width, int height, KeyCode leftKey, KeyCode rightKey, KeyCode upKey, KeyCode shootKey) {
        this.x = x;
        this.y = y;
        this.startX = x;
        this.startY = y;
        this.setTranslateX(x);
        this.setTranslateY(y);
        this.characterWidth = width;
        this.characterHeight = height;
        this.characterImg = new Image(Launcher.class.getResourceAsStream(imgName));
        this.imageView = new AnimatedSprite(characterImg, count, column, row, 0, 0, width, height);
        this.imageView.setFitWidth((int) (width * 2));
        this.imageView.setFitHeight((int) (height * 2));
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        this.upKey = upKey;
        this.shootKey = shootKey;
        this.getChildren().addAll(this.imageView);
        setScaleX(id % 2 * 2 - 1);
    }
    public void moveLeft() {
        setScaleX(-1);
        isMoveLeft = true;
        isMoveRight = false;
    }
    public void moveRight() {
        setScaleX(1);
        isMoveLeft = false;
        isMoveRight = true;
    }
    public void stop() {
        isMoveLeft = false;
        isMoveRight = false;
        xVelocity = 0;
    }

    public void moveX() {
        if (isMoveLeft) {
            xVelocity = Math.min(xVelocity + xAcceleration, xMaxVelocity);
            x -= xVelocity;
        }
        if (isMoveRight) {
            xVelocity = Math.min(xVelocity + xAcceleration, xMaxVelocity);
            x += xVelocity;
        }
        setTranslateX(x);
    }





    public void moveY() {
        setTranslateY(y);
        if(isFalling) {
            yVelocity = yVelocity >= yMaxVelocity? yMaxVelocity : yVelocity+yAcceleration;
            y = y + yVelocity;
        } else if(isJumping) {
            yVelocity = yVelocity <= 0 ? 0 : yVelocity-yAcceleration;
            y = y - yVelocity;
        }
    }
    public void checkReachGameWall() {
        if (x <= 0) {
            x = 0;
        }
    }

    public void jump() {
        System.out.println("Trying to jump. canJump=" + canJump + ", isJumping=" + isJumping);
        if (canJump) {
            yVelocity = yMaxVelocity;
            canJump = false;
            isJumping = true;
            isFalling = false;
            System.out.println("Jump started!");

        }
    }
    public void checkReachHighest () {
        if(isJumping && yVelocity <= 0) {
            isJumping = false;
            isFalling = true;
            yVelocity = 0;
        }
    }
    public void checkReachFloor() {
        if(isFalling && y >= GameStage.GROUND - this.characterHeight) {
            y = GameStage.GROUND - this.characterHeight;
            isFalling = false;
            canJump = true;
            yVelocity = 0;
        }
    }
    public void checkPlatforms(List<Platform> platforms) {
        if (platforms == null) return;
        boolean onSomething = false;

        for (Platform p : platforms) {
            if (platforms == null) return;
            if (isJumping) return;
            // ถ้าตัวละครอยู่เหนือ platform และกำลังตกลงมา
            if (this.y + this.characterHeight <= p.getY() &&
                    this.y + this.characterHeight + yVelocity >= p.getY() &&
                    this.x + this.characterWidth > p.getX() &&
                    this.x < p.getX() + p.getWidth()) {

                // วางตัวละครไว้บน platform
                this.y = p.getY() - this.characterHeight;
                this.isFalling = false;
                this.canJump = true;
                this.yVelocity = 0;
                return;
            }
        }

        // ถ้าไม่ชน platform ใด ๆ เลย
        if (!onSomething && !isJumping) {
            this.isFalling = true;
        }
    }


    public void repaint() {
        moveX();
        checkBossCollision();
        moveY();
        checkPlatforms(platforms);
        checkReachHighest();
        checkReachFloor();
        checkReachGameWall();
        trace();
    }
    public boolean collided(GameCharacter c) {
        if (this == c) return false;

        if (this.isMoveLeft && this.x > c.getX()) {
            this.x = Math.max(this.x, c.getX() + c.getCharacterWidth());
            this.stop();
        } else if (this.isMoveRight && this.x < c.getX()) {
            this.x = Math.min(this.x, c.getX() - this.characterWidth);
            this.stop();
        }

        if (this.isFalling && this.y < c.getY()) {
            score++;
            this.y = Math.min(GameStage.GROUND - this.characterHeight, c.getY());
            this.repaint();
            c.collapsed();
            c.respawn();
            return true;
        }
        return false;
    }


    public void collapsed() {
        this.imageView.setFitWidth((int) (this.getWidth() * 2));
        this.imageView.setFitHeight((int) (this.getHeight() * 2));
        this.y = 5;

        this.repaint();
        try {
            TimeUnit.MILLISECONDS.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void respawn() {
        this.x = this.startX;
        this.y = this.startY;
        this.imageView.setFitWidth(this.characterWidth * 2);
        this.imageView.setFitHeight(this.characterHeight * 2);
        this.isMoveLeft = false;
        this.isMoveRight = false;
        this.isFalling = true;
        this. canJump = false;
        this.isJumping = false;
    }
    public KeyCode getLeftKey() {

        return leftKey;
    }
    public KeyCode getRightKey() {

        return rightKey;
    }
    public KeyCode getUpKey() {

        return upKey;
    }

    public KeyCode getShootKey() {
        return shootKey;
    }

    public AnimatedSprite getImageView() {

        return imageView;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getCharacterWidth() {
        return characterWidth;
    }

    public int getCharacterHeight() {
        return characterHeight;
    }

    public int getScore() {
        return score;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void trace(){
        if (x != lastX || y != lastY) {
            logger.info("Moved to x:{} y:{} (vx={}, vy={})", x, y, xVelocity, yVelocity);
            lastX = x;
            lastY = y;
        }
//        logger.info(String.format("x:%d y:%d vx:%d vy:%d", x, y, xVelocity, yVelocity));
//        System.out.println("x=" + x + " y=" + y + " vx=" + xVelocity + " vy=" + yVelocity);
    }
    public void setPlatforms(List<Platform> platforms) {
        this.platforms = platforms;
    }



    public void setBulletManager(BulletManager manager) {
        this.bulletManager = manager;
    }

    public void shoot() {
        if (bulletManager == null) return;

        long now = System.nanoTime();
        if (now - lastShotTime < SHOOT_COOLDOWN) {
            return;
        }
        lastShotTime = now;

        double startX = getTranslateX() + (facingRight ? characterWidth : 0);
        double startY = getTranslateY() + characterHeight / 2.0;
        bulletManager.shoot(startX, startY, facingRight, true);
    }


    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }
    public void takeDamage() {
        hp--;
        if (hp <= 0) {
            lives--;
            if(lives <=0){
                stageManager.loadStage(0);
                return;
            }
            hp = 5;
            respawn();
        }
    }
    private StageManager stageManager;

    public void setStageManager(StageManager stageManager) {
        this.stageManager = stageManager;
    }
    public StageManager getStageManager() {
        return stageManager;
    }
    public Rectangle2D getSpriteBounds() {
        var b = imageView.localToScene(imageView.getBoundsInLocal());
        double padding = 50;
        return new Rectangle2D(
                b.getMinX() + padding,
                b.getMinY(),
                b.getWidth() - padding * 2,
                b.getHeight()
        );
    }



    public void stopHorizontal() {
        xVelocity = 0;
    }
    private void checkBossCollision() {
        if (!(getParent() instanceof GameStage stage)) return;
        Boss boss = stage.getBoss();
        if (boss == null || !boss.isAlive()) return;

        Rectangle2D bossBox = boss.getSpriteBounds();
        Rectangle2D playerBox = getSpriteBounds();

        if (!playerBox.intersects(bossBox)) return;

        double overlapLeft = playerBox.getMaxX() - bossBox.getMinX();
        double overlapRight = bossBox.getMaxX() - playerBox.getMinX();

        if (overlapLeft < overlapRight) {
            x -= overlapLeft;
        } else {
            x += overlapRight;
        }

        xVelocity = 0;
        setTranslateX(x);
    }

    private boolean willCollideWithBoss(int nextX) {
        if (!(getParent() instanceof GameStage stage)) return false;
        Boss boss = stage.getBoss();
        if (boss == null || !boss.isAlive()) return false;

        Rectangle2D nextBounds = new Rectangle2D(nextX, y, characterWidth, characterHeight);
        return nextBounds.intersects(boss.getSpriteBounds());
    }






}



