package se233.AdProProject2;


import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se233.project2.model.GameCharacter;
import se233.project2.model.Platform;
import se233.project2.view.GameStage;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {
    Field xVelocityField, yVelocityField, yAccelerationField,canJumpField,isJumpingField,isFallingField,yMaxVelocityField;
    private GameCharacter gameCharacter;

    @BeforeEach
    void setUp() throws NoSuchFieldException {
        gameCharacter = new GameCharacter(0, 30, 30, "assets/Character.png", 6, 6, 1, 65, 64, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.UP, KeyCode.SPACE);

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

        int expectedX = 800 - (int) gameCharacter.getWidth();
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
        int screenWidth = GameStage.WIDTH;

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
        assertFalse(isFallingField.getBoolean(gameCharacter), "isFalling should be false after hitting the ground");
        assertTrue(canJumpField.getBoolean(gameCharacter), "canJump should be true after hitting the ground");
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
    void checkPlatforms_whenPlatformsNull_doesNothing() {
        gameCharacter.setPlatforms(null);
        gameCharacter.checkPlatforms(null);
        assertEquals(100, gameCharacter.getY());
    }

    @Test
    void checkPlatforms_whenJumping_doesNothing() throws Exception {
        Field isJumpingField = gameCharacter.getClass().getDeclaredField("isJumping");
        isJumpingField.setAccessible(true);
        isJumpingField.set(gameCharacter, true);

        List<Platform> platforms = new ArrayList<>();
        platforms.add(new Platform(80, 150, 100, 20));

        gameCharacter.checkPlatforms(platforms);

        assertTrue(isJumpingField.getBoolean(gameCharacter), "isJumping should remain true");
        assertEquals(100, gameCharacter.getY(), "y should remain unchanged while jumping");
    }

    @Test
    void checkPlatforms_whenLandingOnPlatform_setsCharacterOnTop() throws Exception {
        List<Platform> platforms = new ArrayList<>();
        Platform platform = new Platform(80, 150, 100, 20);
        platforms.add(platform);

        Field yVelocityField = gameCharacter.getClass().getDeclaredField("yVelocity");
        Field isFallingField = gameCharacter.getClass().getDeclaredField("isFalling");
        Field canJumpField = gameCharacter.getClass().getDeclaredField("canJump");

        yVelocityField.setAccessible(true);
        isFallingField.setAccessible(true);
        canJumpField.setAccessible(true);

        yVelocityField.set(gameCharacter, 10);
        isFallingField.set(gameCharacter, true);
        canJumpField.set(gameCharacter, false);

        gameCharacter.setY(95);
        gameCharacter.setX(90);
        gameCharacter.checkPlatforms(platforms);

        assertEquals(platform.getY() - gameCharacter.getCharacterHeight(), gameCharacter.getY(),
                "Character should be placed on top of platform");
        assertFalse(isFallingField.getBoolean(gameCharacter), "isFalling should be false after landing");
        assertTrue(canJumpField.getBoolean(gameCharacter), "canJump should be true after landing");
        assertEquals(0, yVelocityField.getInt(gameCharacter), "yVelocity should be reset to 0 after landing");
    }

    @Test
    void checkPlatforms_whenNotLandingOnPlatform_setsFallingTrue() throws Exception {
        List<Platform> platforms = new ArrayList<>();
        Platform platform = new Platform(500, 150, 100, 20); // far away platform
        platforms.add(platform);

        Field isFallingField = gameCharacter.getClass().getDeclaredField("isFalling");
        isFallingField.setAccessible(true);
        isFallingField.set(gameCharacter, false);

        gameCharacter.setY(100);
        gameCharacter.setX(100);

        gameCharacter.checkPlatforms(platforms);

        assertTrue(isFallingField.getBoolean(gameCharacter), "isFalling should be true if not standing on any platform");
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
    void repaint_landsOnFloor_stopsFalling() throws Exception {
        isFallingField.set(gameCharacter, true);
        canJumpField.set(gameCharacter, false);
        yVelocityField.set(gameCharacter, 10);
        gameCharacter.setY(GameStage.GROUND + 10);

        gameCharacter.repaint();

        assertEquals(GameStage.GROUND - gameCharacter.getCharacterHeight(), gameCharacter.getY(),
                "Character y should be clamped to floor");
        assertFalse(isFallingField.getBoolean(gameCharacter), "Character should stop falling after hitting floor.");
        assertTrue(canJumpField.getBoolean(gameCharacter), "Character can jump after hitting floor.");
        assertEquals(0, yVelocityField.getInt(gameCharacter), "yVelocity should reset after floor collision.");
    }

    @Test
    void repaint_landsOnPlatform_stopsFalling() throws Exception {
        Platform platform = new Platform(90, 150, 100, 20);
        List<Platform> platforms = new ArrayList<>();
        platforms.add(platform);
        gameCharacter.setPlatforms(platforms);

        gameCharacter.setX(100);
        gameCharacter.setY(95); // just above platform
        isFallingField.set(gameCharacter, true);
        canJumpField.set(gameCharacter, false);
        yVelocityField.set(gameCharacter, 10);

        gameCharacter.repaint();

        assertEquals(platform.getY() - gameCharacter.getCharacterHeight(), gameCharacter.getY(),
                "Character should land on platform");
        assertFalse(isFallingField.getBoolean(gameCharacter), "isFalling should be false after landing on platform.");
        assertTrue(canJumpField.getBoolean(gameCharacter), "canJump should be true after landing.");
        assertEquals(0, yVelocityField.getInt(gameCharacter), "yVelocity should be 0 after landing.");
    }

    @Test
    void repaint_hitsLeftWall_doesNotExceed() {
        gameCharacter.setX(0);
        gameCharacter.moveLeft();
        gameCharacter.repaint();
        gameCharacter.checkReachGameWall();
        assertEquals(0, gameCharacter.getX(), "Character should not move past left wall");
    }

    @Test
    void repaint_hitsRightWall_doesNotExceed() throws Exception {
        int screenWidth = GameStage.WIDTH;
        int startX = screenWidth - gameCharacter.getCharacterHeight();

        gameCharacter.setX(startX);
        gameCharacter.moveRight();
        gameCharacter.repaint();
        gameCharacter.checkReachGameWall();

        int expectedX = screenWidth - gameCharacter.getCharacterWidth();
        assertEquals(expectedX, gameCharacter.getX(), "Character should not move past right wall");
    }

    @Test
    void collapsed_adjustsHeightAndY() {
        int originalY = gameCharacter.getY();
        int originalHeight = gameCharacter.getCharacterHeight();

        gameCharacter.collapsed();

        assertEquals(5, gameCharacter.getImageView().getFitHeight(),
                "ImageView height should be set to 5 after collapse");

        assertEquals(originalY + originalHeight - 5, gameCharacter.getY(),
                "Character y should move down by originalHeight - 5 after collapse");
    }

    @Test
    void respawn_resetsCharacterState() throws IllegalAccessException {
        // Arrange: modify character state
        gameCharacter.setX(200);
        gameCharacter.setY(300);
        gameCharacter.moveLeft();

        isFallingField.set(gameCharacter, false);
        canJumpField.set(gameCharacter, true);
        isJumpingField.set(gameCharacter, true);

        // Act
        gameCharacter.respawn();

        assertEquals(gameCharacter.getStartX(), gameCharacter.getX(), "x should reset to startX.");
        assertEquals(gameCharacter.getStartY(), gameCharacter.getY(), "y should reset to startY.");

        assertEquals(gameCharacter.getCharacterWidth(), gameCharacter.getImageView().getFitWidth(), "Width should reset.");
        assertEquals(gameCharacter.getCharacterHeight(), gameCharacter.getImageView().getFitHeight(), "Height should reset.");

        assertFalse(gameCharacter.isMoveLeft, "isMoveLeft should be false.");
        assertFalse(gameCharacter.isMoveRight, "isMoveRight should be false.");

        assertTrue(isFallingField.getBoolean(gameCharacter), "isFalling should be true.");
        assertFalse(canJumpField.getBoolean(gameCharacter), "canJump should be false.");
        assertFalse(isJumpingField.getBoolean(gameCharacter), "isJumping should be false.");
    }

}

