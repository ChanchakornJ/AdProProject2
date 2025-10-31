package se233.AdProProject2;

import javafx.embed.swing.JFXPanel;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se233.project2.controller.BulletManager;
import se233.project2.model.Bullet;
import se233.project2.model.GameCharacter;
import se233.project2.model.Minion;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class ScoringTest {
    private GameCharacter gameCharacter;
    private BulletManager bulletManager;
    private Minion minion;

    @BeforeEach
    void setUp() {
        new JFXPanel();

        bulletManager = mock(BulletManager.class);

        gameCharacter = new GameCharacter(0, 30, 30, "assets/Character.png", 6, 6, 1, 65, 64, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.UP, KeyCode.SPACE);
        gameCharacter.resetScore();

        minion = new Minion(200, 200, 40, 60, 2.0, 3, "/assets/Minion1.png", bulletManager);
        minion.setActive(true);
    }

    @Test
    void testScoreIncrease_whenMinionDies() {
        minion.takeDamage();
        minion.takeDamage();
        minion.takeDamage(); // dead now

        if (!minion.isAlive()) {
            gameCharacter.addScore(100);
        }

        assertEquals(100, gameCharacter.getScore(), "Player should earn 100 points for killing one minion");
    }

    @Test
    void testScoreDoesNotIncreaseIfMinionStillAlive() {
        minion.takeDamage();
        minion.takeDamage(); // still alive

        if (!minion.isAlive()) {
            gameCharacter.addScore(100);
        }

        assertEquals(0, gameCharacter.getScore(), "Score should remain 0 until minion actually dies");
    }

    @Test
    void testMultipleKillsAddUpScore() {
        // First minion
        Minion m1 = new Minion(100, 100, 40, 60, 2.0, 1, "/assets/minion.png", bulletManager);
        m1.takeDamage(); // dies
        if (!m1.isAlive()) gameCharacter.addScore(100);

        // Second minion
        Minion m2 = new Minion(300, 100, 40, 60, 2.0, 2, "/assets/minion.png", bulletManager);
        m2.takeDamage();
        m2.takeDamage(); // dies
        if (!m2.isAlive()) gameCharacter.addScore(100);

        assertEquals(200, gameCharacter.getScore(), "Player should have 200 points after killing two minions");
    }

    @Test
    void testScoreResetsToZero() {
        gameCharacter.addScore(250);
        gameCharacter.resetScore();
        assertEquals(0, gameCharacter.getScore(), "Score should reset to 0 after resetScore() call");
    }

    @Test
    void testBulletHitKillsMinionAndAddsScore() {
        // Mock bullet hitting logic
        Bullet bullet = new Bullet(200, 200, 0, 0, true);

        // Simulate hit detection (simple bounding box logic)
        boolean hit = bullet.x >= minion.getHitBox().getMinX() &&
                bullet.x <= minion.getHitBox().getMaxX() &&
                bullet.y >= minion.getHitBox().getMinY() &&
                bullet.y <= minion.getHitBox().getMaxY();

        if (hit) {
            minion.takeDamage();
            minion.takeDamage();
            minion.takeDamage();
        }

        if (!minion.isAlive()) {
            gameCharacter.addScore(100);
        }

        assertTrue(hit, "Bullet should collide with minion");
        assertFalse(minion.isAlive(), "Minion should die after bullet hit damage");
        assertEquals(100, gameCharacter.getScore(), "Score should increase by 100 after bullet kill");
    }

    @Test
    void testNoScoreIfBulletMisses() {
        Bullet bullet = new Bullet(10, 10, 0, 0, true); // way outside hitbox

        boolean hit = bullet.x >= minion.getHitBox().getMinX() &&
                bullet.x <= minion.getHitBox().getMaxX() &&
                bullet.y >= minion.getHitBox().getMinY() &&
                bullet.y <= minion.getHitBox().getMaxY();

        if (hit) {
            minion.takeDamage();
            gameCharacter.addScore(100);
        }

        assertFalse(hit, "Bullet should not hit minion");
        assertTrue(minion.isAlive(), "Minion should remain alive when not hit");
        assertEquals(0, gameCharacter.getScore(), "Score should not change when bullet misses");
    }

}

