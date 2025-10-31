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

    @BeforeEach
    void setUp() {
        new JFXPanel(); // Initialize JavaFX runtime
        bulletManager = mock(BulletManager.class);
        gameCharacter = new GameCharacter(0, 30, 30, "assets/Character.png", 6, 6, 1, 65, 64, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.UP, KeyCode.SPACE);
        gameCharacter.resetScore();
    }

    private Minion createMinion(double x, double y, int hp, String imgPath) {
        return new Minion(x, y, 40, 60, 2.0, hp, imgPath, bulletManager);
    }

    @Test
    void testRegularEnemyKillGivesOnePoint() {
        Minion regular = createMinion(100, 100, 1, "/assets/Minion1.png");

        regular.takeDamage(); // dies instantly
        if (!regular.isAlive()) gameCharacter.addScore(1);

        assertEquals(1, gameCharacter.getScore(), "Killing a regular enemy should give 1 point");
    }

    @Test
    void testSecondTierEnemyKillGivesTwoPoints() {
        Minion secondTier = createMinion(200, 100, 2, "/assets/minion_tier2.png");

        secondTier.takeDamage();
        secondTier.takeDamage(); // dies after 2 hits
        if (!secondTier.isAlive()) gameCharacter.addScore(2);

        assertEquals(2, gameCharacter.getScore(), "Killing a second-tier enemy should give 2 points");
    }

    @Test
    void testSmallBossKillGivesOnePoint() {
        Minion smallBoss = createMinion(300, 100, 3, "/assets/boss_small.png");

        for (int i = 0; i < 3; i++) smallBoss.takeDamage(); // simulate full damage
        if (!smallBoss.isAlive()) gameCharacter.addScore(1);

        assertEquals(1, gameCharacter.getScore(), "Killing a small boss should give 1 point");
    }

    @Test
    void testLargeBossKillGivesTwoPoints() {
        Minion largeBoss = createMinion(400, 100, 5, "/assets/boss_large.png");

        for (int i = 0; i < 5; i++) largeBoss.takeDamage();
        if (!largeBoss.isAlive()) gameCharacter.addScore(2);

        assertEquals(2, gameCharacter.getScore(), "Killing a large boss should give 2 points");
    }

    @Test
    void testBulletHitKillsEnemyAndAwardsScore() {
        Minion enemy = createMinion(150, 150, 1, "/assets/minion_regular.png");
        Bullet bullet = new Bullet(150, 150, 0, 0, true);

        boolean hit = bullet.x >= enemy.getHitBox().getMinX() &&
                bullet.x <= enemy.getHitBox().getMaxX() &&
                bullet.y >= enemy.getHitBox().getMinY() &&
                bullet.y <= enemy.getHitBox().getMaxY();

        if (hit) enemy.takeDamage();
        if (!enemy.isAlive()) gameCharacter.addScore(1);

        assertTrue(hit, "Bullet should hit enemy");
        assertFalse(enemy.isAlive(), "Enemy should die from bullet");
        assertEquals(1, gameCharacter.getScore(), "Killing an enemy by bullet should award 1 point");
    }

    @Test
    void testNoScoreIfEnemyStillAlive() {
        Minion halfDead = createMinion(200, 200, 3, "/assets/minion_regular.png");

        halfDead.takeDamage(); // still alive
        if (!halfDead.isAlive()) gameCharacter.addScore(1);

        assertTrue(halfDead.isAlive(), "Enemy should still be alive");
        assertEquals(0, gameCharacter.getScore(), "Score should remain 0 until enemy dies");
    }

    @Test
    void testMultipleKillsAddUpScore() {
        Minion e1 = createMinion(100, 100, 1, "/assets/minion_regular.png");
        Minion e2 = createMinion(200, 100, 2, "/assets/minion_tier2.png");

        e1.takeDamage();
        e2.takeDamage();
        e2.takeDamage();

        if (!e1.isAlive()) gameCharacter.addScore(1);
        if (!e2.isAlive()) gameCharacter.addScore(2);

        assertEquals(3, gameCharacter.getScore(), "Killing both enemies should total 3 points");
    }

    @Test
    void testScoreResetWorksProperly() {
        gameCharacter.addScore(5);
        gameCharacter.resetScore();
        assertEquals(0, gameCharacter.getScore(), "Score should reset to zero");
    }

    @Test
    void testOverkillDoesNotAddExtraPoints() {
        Minion minion = createMinion(100, 100, 1, "/assets/Minion1.png");
        minion.takeDamage();
        minion.takeDamage(); // extra damage on dead minion

        if (!minion.isAlive()) gameCharacter.addScore(1);
        gameCharacter.addScore(1); // simulate double counting (shouldn't happen)

        assertEquals(1, gameCharacter.getScore(), "Score should not increase for overkill");
    }

}