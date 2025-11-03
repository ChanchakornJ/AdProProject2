package se233.AdProProject2;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se233.project2.model.ExceptionHandler;
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

        // ใช้ reflection เพื่อเข้าถึง field 'point'
        pointField = Score.class.getDeclaredField("point");
        pointField.setAccessible(true); // ให้เข้าถึง private/package-private
    }

    @Test
    void testInitialScoreAndPosition() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                Label label = (Label) pointField.get(score);

                assertEquals("0", label.getText());
                assertEquals(10, score.getTranslateX());
                assertEquals(20, score.getTranslateY());
            } catch (Exception e) {
                ExceptionHandler.handleException(e);;
            } finally {
                latch.countDown();
            }
        });
        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    void testSetPointUpdatesLabel() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                score.setPoint(42);
                Label label = (Label) pointField.get(score);
                assertEquals("42", label.getText());

                score.setPoint(100);
                assertEquals("100", label.getText());
            } catch (Exception e) {
                ExceptionHandler.handleException(e);
            } finally {
                latch.countDown();
            }
        });
        latch.await(5, TimeUnit.SECONDS);//ijio
    }
}
