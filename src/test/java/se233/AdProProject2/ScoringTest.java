package se233.AdProProject2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se233.project2.model.Bullet;
import se233.project2.model.ContraDemoWithEnemies;
import se233.project2.model.GameCharacter;

import static org.junit.jupiter.api.Assertions.*;

public class ScoringTest {
    private GameCharacter gameCharacter;

    @BeforeEach
    void setUp() {
        gameCharacter = new GameCharacter();
        gameCharacter.setScore(0); // start fresh
    }

    @Test
    void testScoreIncreasesWhenEnemyHit() {
        ContraDemoWithEnemies.Enemy enemy = new ContraDemoWithEnemies.Enemy(100, 100, 50, 50);
        Bullet bullet = new Bullet(120, 120, 0, 0, true); // inside enemy bounds

        // Simulate bullet hitting enemy
        if (enemy.isHit(bullet)) {
            gameCharacter.addScore(100); // award points
            enemy.die();
        }

        assertEquals(100, gameCharacter.getScore(), "Player should earn 100 points for hitting an enemy");
        assertFalse(enemy.isAlive(), "Enemy should die when hit");
    }

    @Test
    void testScoreDoesNotIncreaseWhenMiss() {
        ContraDemoWithEnemies.Enemy enemy = new ContraDemoWithEnemies.Enemy(100, 100, 50, 50);
        Bullet bullet = new Bullet(10, 10, 0, 0, true); // far away, missed

        if (enemy.isHit(bullet)) {
            gameCharacter.addScore(100);
            enemy.die();
        }

        assertEquals(0, gameCharacter.getScore(), "Score should not increase if bullet misses enemy");
        assertTrue(enemy.isAlive(), "Enemy should remain alive if not hit");
    }
}
