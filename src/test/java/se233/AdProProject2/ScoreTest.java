package se233.AdProProject2;

import javafx.application.Platform;
import javafx.scene.control.Label;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se233.project2.view.Score;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class ScoreTest {
    private Score score;
    private Field pointField;

    @BeforeAll
    static void initJFX() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        latch.await(5, TimeUnit.SECONDS);
    }

    @BeforeEach
    void setUp() throws Exception {
        score = new Score(100, 200);

        pointField = Score.class.getDeclaredField("point");
        pointField.setAccessible(true);
    }

    @Test
    void testInitialScoreAndPosition() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            Label label = getLabel();
            assertEquals("0", label.getText());
            assertEquals(100, score.getTranslateX());
            assertEquals(200, score.getTranslateY());
            latch.countDown();
        });

        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    void testSetPointUpdatesLabel() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            score.setPoint(42);
            assertEquals("42", getLabel().getText());

            score.setPoint(100);
            assertEquals("100", getLabel().getText());

            latch.countDown();
        });

        latch.await(5, TimeUnit.SECONDS);
    }

    private Label getLabel() {
        try {
            return (Label) pointField.get(score);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
