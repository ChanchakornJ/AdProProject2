package se233.AdProProject2;

import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se233.project2.model.GameCharacter;
import se233.project2.model.Minion;
import se233.project2.view.GameStage;
import javafx.collections.FXCollections;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class EnemyTest {
    private Minion minion;
    private GameStage mockStage;
    private GameCharacter mockPlayer;

    @BeforeEach
    void setUp() {
        minion = spy(new Minion());
        minion.setHp(1);

        mockPlayer = mock(GameCharacter.class);

        mockStage = mock(GameStage.class);

        when(mockStage.getGameCharacterList()).thenReturn(List.of(mockPlayer));

        when(mockStage.getChildren()).thenReturn(FXCollections.observableArrayList(minion));

        doReturn(mockStage).when(minion).getParent();
    }

    @Test
    void testTakeDamageReducesHp() {
        minion.setHp(5);
        minion.takeDamage();
        assertEquals(4, minion.getHp());
    }

    @Test
    void testTakeDamageKillsAndAddsScore() {
        minion.setHp(1);
        minion.takeDamage();

        verify(minion).die();

        verify(mockPlayer).addScore(10);
    }

    @Test
    void testTakeDamageDoesNotKillWhenHpAboveZero() {
        minion.setHp(2);
        minion.takeDamage();

        assertEquals(1, minion.getHp());

        verify(minion, never()).die();

        verify(mockPlayer, never()).addScore(anyInt());
    }
}
