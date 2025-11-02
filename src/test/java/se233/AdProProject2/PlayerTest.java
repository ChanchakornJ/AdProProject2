package se233.AdProProject2;


import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se233.project2.controller.BulletManager;
import se233.project2.model.Bullet;
import se233.project2.model.GameCharacter;
import se233.project2.model.Platform;
import se233.project2.view.GameStage;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class PlayerTest {
    Field xVelocityField, yVelocityField, yAccelerationField,canJumpField,isJumpingField,isFallingField,yMaxVelocityField;
    private GameCharacter gameCharacter;
    private BulletManager bulletManager;

    @BeforeEach
    void setUp() throws NoSuchFieldException {
        Pane root = new Pane();
        bulletManager = new BulletManager(root);
        gameCharacter = new GameCharacter(0, 30, 30, "assets/Character.png", 6, 6, 1, 65, 64, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.UP, KeyCode.DOWN, KeyCode.SPACE);
        gameCharacter.setBulletManager(bulletManager);
         // or however you normally create it

        isFallingField = gameCharacter.getClass().getDeclaredField("isFalling");
        isJumpingField = gameCharacter.getClass().getDeclaredField("isJumping");
        canJumpField = gameCharacter.getClass().getDeclaredField("canJump");
        yVelocityField = gameCharacter.getClass().getDeclaredField("yVelocity");

        isFallingField.setAccessible(true);
        isJumpingField.setAccessible(true);
        canJumpField.setAccessible(true);
        yVelocityField.setAccessible(true);
    }

    @Test
    void testMoveLeft_setsCorrectFlags() {
        gameCharacter.moveLeft();
        assertTrue(gameCharacter.isMoveLeft, "isMoveLeft should be true.");
        assertFalse(gameCharacter.isMoveRight, "isMoveRight should be false.");
        assertEquals(-1, gameCharacter.getScaleX(), "gameCharacter should face left (scaleX = -1).");
    }

    @Test
    void testMoveLeftThenRepaint_movesCharacterLeft() {
        int startX = gameCharacter.getX();
        gameCharacter.moveLeft();
        gameCharacter.repaint();
        assertTrue(gameCharacter.getX() < startX, "gameCharacter should move left after repaint().");
    }

    @Test
    void testMoveLeft_thenStop_resetsFlags() {
        gameCharacter.moveLeft();
        gameCharacter.stop();
        assertFalse(gameCharacter.isMoveLeft, "isMoveLeft should be false after stop().");
        assertFalse(gameCharacter.isMoveRight, "isMoveLeft should be false after stop().");
    }

    @Test
    void testMoveLeft_withGameWall() {
        gameCharacter.setX(0);
        gameCharacter.moveLeft();
        gameCharacter.repaint();
        gameCharacter.checkReachGameWall();
        assertEquals(0, gameCharacter.getX(), "Character should not move past the left boundary.");
    }

    @Test
    void testMoveRight_setsCorrectFlags() {
        gameCharacter.moveRight();
        assertTrue(gameCharacter.isMoveRight, "isMoveRight should be true after moveRight()");
        assertFalse(gameCharacter.isMoveLeft, "isMoveLeft should be false after moveRight()");
        assertEquals(1, gameCharacter.getScaleX(), "Character should face right (scaleX = 1)");
    }

    @Test
    void testMoveRightThenRepaint_movesCharacterRight() {
        int startX = gameCharacter.getX();

        gameCharacter.moveRight();
        gameCharacter.repaint();

        assertTrue(gameCharacter.getX() > startX,
                "Character should move right after repaint()");
    }

    @Test
    void testMoveRight_thenStop_resetsFlags() {
        gameCharacter.moveRight();
        gameCharacter.stop();

        assertFalse(gameCharacter.isMoveRight, "isMoveRight should be false after stop()");
        assertFalse(gameCharacter.isMoveLeft, "isMoveLeft should be false after stop()");
    }

    @Test
    void testMoveRight_withGameWall() throws Exception {
        int screenWidth = GameStage.WIDTH;

        int startX = screenWidth - gameCharacter.getCharacterWidth();

        gameCharacter.setX(startX);
        gameCharacter.moveRight();
        gameCharacter.repaint();
        gameCharacter.checkReachGameWall();

        int expectedX = GameStage.WIDTH - (int) gameCharacter.getCharacterWidth();
        assertEquals(expectedX, gameCharacter.getX(),
                "Character should not move past the right boundary.");
    }


    @Test
    void testStop_resetsFlagsAndVelocity() {

        gameCharacter.moveLeft();
        gameCharacter.xVelocity = 5;

        gameCharacter.stop();

        assertFalse(gameCharacter.isMoveLeft, "isMoveLeft should be false after stop()");
        assertFalse(gameCharacter.isMoveRight, "isMoveRight should be false after stop()");
        assertEquals(0, gameCharacter.xVelocity, "xVelocity should be 0 after stop()");
    }

    @Test
    void checkReachGameWall_whenXIsNegative_thenSetToZero() {
        gameCharacter.setX(-50);
        gameCharacter.checkReachGameWall();
        assertEquals(0, gameCharacter.getX(), "x should be zero when reach game wall.");
    }

    @Test
    void checkReachGameWall_whenXIsWithinBounds_thenUnchanged() {
        gameCharacter.setX(200);
        gameCharacter.checkReachGameWall();
        assertEquals(200, gameCharacter.getX(), "x should be within bounds when checking reach game wall.");
    }

    @Test
    void checkReachGameWall_whenXExceedsRightBoundary_thenClampedToEdge() {
        int screenWidth = GameStage.WIDTH;//fpjjf

        int beyondRightEdge = screenWidth - gameCharacter.getCharacterWidth();
        gameCharacter.setX(beyondRightEdge);
        gameCharacter.checkReachGameWall();

        int expectedX = screenWidth - gameCharacter.getCharacterWidth();

        assertEquals(expectedX, gameCharacter.getX(),
                "x should be clamped to the right boundary after exceeding it.");
    }


    @Test
    public void jump_givenCanJumpIsTrue_thenJumpIsInitiated() throws IllegalAccessException, NoSuchFieldException {
        yVelocityField = gameCharacter.getClass().getDeclaredField("yVelocity");
        yMaxVelocityField = gameCharacter.getClass().getDeclaredField("yMaxVelocity");

        yVelocityField.setAccessible(true);
        yMaxVelocityField.setAccessible(true);

        canJumpField.set(gameCharacter, true);
        int yMaxVelocity = yMaxVelocityField.getInt(gameCharacter);

        gameCharacter.jump();
        assertFalse(canJumpField.getBoolean(gameCharacter), "canJump should be false after jump");
        assertTrue(isJumpingField.getBoolean(gameCharacter), "isJumping should be true after jump");
        assertFalse(isFallingField.getBoolean(gameCharacter), "isFalling should be false after jump");
        assertEquals(yMaxVelocity, yVelocityField.getInt(gameCharacter), "yVelocity should equal yMaxVelocity after jump");
    }

    @Test
    public void jump_whenAirborne_thenJumpIsNotInitiated() throws IllegalAccessException, NoSuchFieldException {
        canJumpField = gameCharacter.getClass().getDeclaredField("canJump");
        isJumpingField = gameCharacter.getClass().getDeclaredField("isJumping");
        isFallingField = gameCharacter.getClass().getDeclaredField("isFalling");
        yVelocityField = gameCharacter.getClass().getDeclaredField("yVelocity");

        canJumpField.setAccessible(true);
        isJumpingField.setAccessible(true);
        isFallingField.setAccessible(true);
        yVelocityField.setAccessible(true);

        canJumpField.set(gameCharacter, false);

        boolean initialJumping = isJumpingField.getBoolean(gameCharacter);
        boolean initialFalling = isFallingField.getBoolean(gameCharacter);
        int initialYVelocity = yVelocityField.getInt(gameCharacter);

        gameCharacter.jump();

        assertFalse(canJumpField.getBoolean(gameCharacter), "canJump should remain false (still airborne)");
        assertEquals(initialJumping, isJumpingField.getBoolean(gameCharacter), "isJumping should not change while airborne");
        assertEquals(initialFalling, isFallingField.getBoolean(gameCharacter), "isFalling should not change while airborne");
        assertEquals(initialYVelocity, yVelocityField.getInt(gameCharacter), "yVelocity should not change while airborne");
    }

    @Test
    void checkReachHighest_whenJumpingAndVelocityZeroOrNegative_changesToFalling() throws Exception {

        Field isJumpingField = gameCharacter.getClass().getDeclaredField("isJumping");
        Field isFallingField = gameCharacter.getClass().getDeclaredField("isFalling");
        Field yVelocityField = gameCharacter.getClass().getDeclaredField("yVelocity");

        isJumpingField.setAccessible(true);
        isFallingField.setAccessible(true);
        yVelocityField.setAccessible(true);

        isJumpingField.set(gameCharacter, true);
        yVelocityField.set(gameCharacter, 0);

        gameCharacter.checkReachHighest();

        assertFalse(isJumpingField.getBoolean(gameCharacter), "isJumping should be false after reaching highest point");
        assertTrue(isFallingField.getBoolean(gameCharacter), "isFalling should be true after reaching highest point");
        assertEquals(0, yVelocityField.getInt(gameCharacter), "yVelocity should be 0 at highest point");
    }

    @Test
    void checkReachHighest_whenNotJumping_nothingChanges() throws Exception {
        Field isJumpingField = gameCharacter.getClass().getDeclaredField("isJumping");
        Field isFallingField = gameCharacter.getClass().getDeclaredField("isFalling");
        Field yVelocityField = gameCharacter.getClass().getDeclaredField("yVelocity");

        isJumpingField.setAccessible(true);
        isFallingField.setAccessible(true);
        yVelocityField.setAccessible(true);

        isJumpingField.set(gameCharacter, false);
        isFallingField.set(gameCharacter, false);
        yVelocityField.set(gameCharacter, -5);

        gameCharacter.checkReachHighest();

        assertFalse(isJumpingField.getBoolean(gameCharacter), "isJumping should remain false");
        assertFalse(isFallingField.getBoolean(gameCharacter), "isFalling should remain false");
        assertEquals(-5, yVelocityField.getInt(gameCharacter), "yVelocity should remain unchanged");
    }

    @Test
    void checkReachHighest_whenJumpingAndVelocityPositive_nothingChanges() throws Exception {
        Field isJumpingField = gameCharacter.getClass().getDeclaredField("isJumping");
        Field isFallingField = gameCharacter.getClass().getDeclaredField("isFalling");
        Field yVelocityField = gameCharacter.getClass().getDeclaredField("yVelocity");

        isJumpingField.setAccessible(true);
        isFallingField.setAccessible(true);
        yVelocityField.setAccessible(true);

        isJumpingField.set(gameCharacter, true);
        isFallingField.set(gameCharacter, false);
        yVelocityField.set(gameCharacter, 5);

        gameCharacter.checkReachHighest();

        assertTrue(isJumpingField.getBoolean(gameCharacter), "isJumping should remain true");
        assertFalse(isFallingField.getBoolean(gameCharacter), "isFalling should remain false");
        assertEquals(5, yVelocityField.getInt(gameCharacter), "yVelocity should remain unchanged");
    }

    @Test
    void checkReachFloor_whenFallingAndBelowGround_clampsToGround() throws Exception {
        Field isFallingField = gameCharacter.getClass().getDeclaredField("isFalling");
        Field canJumpField = gameCharacter.getClass().getDeclaredField("canJump");
        Field yVelocityField = gameCharacter.getClass().getDeclaredField("yVelocity");

        isFallingField.setAccessible(true);
        canJumpField.setAccessible(true);
        yVelocityField.setAccessible(true);

        isFallingField.set(gameCharacter, true);
        gameCharacter.setY(GameStage.GROUND + 50); // below ground
        yVelocityField.set(gameCharacter, 5);

        gameCharacter.checkReachFloor();

        assertEquals(GameStage.GROUND - gameCharacter.getCharacterHeight(), gameCharacter.getY(),
                "y should be clamped to ground level");
        assertFalse(isFallingField.getBoolean(gameCharacter), "isFalling should be false after hitting the ground.");
        assertTrue(canJumpField.getBoolean(gameCharacter), "canJump should be true after hitting the ground..");
        assertEquals(0, yVelocityField.getInt(gameCharacter), "yVelocity should be 0 after hitting the ground");
    }

    @Test
    void checkReachFloor_whenNotFalling_nothingChanges() throws Exception {
        Field isFallingField = gameCharacter.getClass().getDeclaredField("isFalling");
        Field canJumpField = gameCharacter.getClass().getDeclaredField("canJump");
        Field yVelocityField = gameCharacter.getClass().getDeclaredField("yVelocity");

        isFallingField.setAccessible(true);
        canJumpField.setAccessible(true);
        yVelocityField.setAccessible(true);

        isFallingField.set(gameCharacter, false);
        canJumpField.set(gameCharacter, false);
        gameCharacter.setY(100);
        yVelocityField.set(gameCharacter, 10);

        gameCharacter.checkReachFloor();

        assertEquals(100, gameCharacter.getY(), "y should not change if not falling");
        assertFalse(isFallingField.getBoolean(gameCharacter), "isFalling should remain false");
        assertFalse(canJumpField.getBoolean(gameCharacter), "canJump should remain unchanged");
        assertEquals(10, yVelocityField.getInt(gameCharacter), "yVelocity should remain unchanged");
    }

    @Test
    void checkReachFloor_whenFallingAboveGround_nothingChanges() throws Exception {
        Field isFallingField = gameCharacter.getClass().getDeclaredField("isFalling");
        Field canJumpField = gameCharacter.getClass().getDeclaredField("canJump");
        Field yVelocityField = gameCharacter.getClass().getDeclaredField("yVelocity");

        isFallingField.setAccessible(true);
        canJumpField.setAccessible(true);
        yVelocityField.setAccessible(true);

        isFallingField.set(gameCharacter, true);
        gameCharacter.setY(GameStage.GROUND - gameCharacter.getCharacterHeight() - 50); // above ground
        yVelocityField.set(gameCharacter, 10);

        gameCharacter.checkReachFloor();

        assertEquals(GameStage.GROUND - gameCharacter.getCharacterHeight() - 50, gameCharacter.getY(),
                "y should remain above ground if not yet reached");
        assertTrue(isFallingField.getBoolean(gameCharacter), "isFalling should remain true");
        assertFalse(canJumpField.getBoolean(gameCharacter), "canJump should remain false");
        assertEquals(10, yVelocityField.getInt(gameCharacter), "yVelocity should remain unchanged");
    }

    @Test
    void repaint_movesCharacterAndUpdatesState() throws Exception {

        gameCharacter.setY(100);
        gameCharacter.setX(50);
        gameCharacter.moveRight();

        Field isFallingField = gameCharacter.getClass().getDeclaredField("isFalling");
        isFallingField.setAccessible(true);
        isFallingField.set(gameCharacter, true);

        gameCharacter.repaint();

        assertTrue(gameCharacter.getX() > 50, "Character should move right");
        assertTrue(gameCharacter.getY() > 100, "Character should fall");
        assertTrue(isFallingField.getBoolean(gameCharacter));
    }

    @Test
    void repaint_horizontalMovement_right() {
        int startX = gameCharacter.getX();
        gameCharacter.moveRight();
        gameCharacter.repaint();
        assertTrue(gameCharacter.getX() > startX, "Character should move right after repaint()");
    }

    @Test
    void repaint_horizontalMovement_left() {
        int startX = gameCharacter.getX();
        gameCharacter.moveLeft();
        gameCharacter.repaint();
        assertTrue(gameCharacter.getX() < startX, "Character should move left after repaint()");
    }

    @Test
    void repaint_falling_updatesY() throws Exception {
        gameCharacter.setY(50);
        isFallingField.set(gameCharacter, true);
        int startY = gameCharacter.getY();

        gameCharacter.repaint();

        assertTrue(gameCharacter.getY() > startY, "Character should fall after repaint()");
    }

    @Test
    void repaint_reachesJumpApex_switchesToFalling() throws Exception {
        isJumpingField.set(gameCharacter, true);
        yVelocityField.set(gameCharacter, 0);
        isFallingField.set(gameCharacter, false);

        gameCharacter.repaint();

        assertFalse(isJumpingField.getBoolean(gameCharacter), "Character should stop jumping at apex");
        assertTrue(isFallingField.getBoolean(gameCharacter), "Character should start falling at apex");
        assertEquals(0, yVelocityField.getInt(gameCharacter), "yVelocity should be zero at apex");
    }

    @Test
    void testShootAddsBullet() {
        // shoot once
        gameCharacter.shoot();

        assertEquals(1, bulletManager.getBullets().size(), "Shooting should add one bullet");

//        Bullet bullet = bulletManager.getBullets().get(0);
//        assertTrue(bullet.fromPlayer, "Bullet should be from the player");
//        assertEquals(100 + 50, bullet.x, 0.01, "Bullet X position should match startX");
//        assertEquals(200 + 80 / 2.0, bullet.y, 0.01, "Bullet Y position should match startY");
    }

    @Test
    void testShootCooldown() throws InterruptedException {
        // first shot
        gameCharacter.shoot();

        // immediately shoot again
        gameCharacter.shoot();

        // should only have one bullet due to cooldown
        assertEquals(1, bulletManager.getBullets().size(), "Cooldown should prevent rapid fire");

        Thread.sleep(gameCharacter.getShootCooldown() / 1_000_000 + 1); // nano to millis
        gameCharacter.shoot();

        // now there should be two bullets
        assertEquals(2, bulletManager.getBullets().size(), "Bullet should be added after cooldown");
    }
}

