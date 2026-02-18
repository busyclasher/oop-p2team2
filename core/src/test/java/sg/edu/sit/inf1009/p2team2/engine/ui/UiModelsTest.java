package sg.edu.sit.inf1009.p2team2.engine.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.Vector2;

class UiModelsTest {

    @Test
    void scorePointsMatchesValue() {
        Score score = new Score("Player", 1234);
        assertEquals(1234, score.getValue());
        assertEquals(1234, score.getPoints());
    }

    @Test
    void sliderClampsToRange() {
        Slider slider = new Slider(0f, 100f, 50f);

        slider.setValue(150f);
        assertEquals(100f, slider.getValue(), 0.0001f);

        slider.setValue(-10f);
        assertEquals(0f, slider.getValue(), 0.0001f);

        slider.setMin(20f);
        slider.setMax(80f);
        slider.setValue(10f);
        assertEquals(20f, slider.getValue(), 0.0001f);
    }

    @Test
    void toggleBehavesAsBooleanState() {
        Toggle toggle = new Toggle(false);
        assertFalse(toggle.isOn());

        toggle.toggle();
        assertTrue(toggle.isOn());
        assertTrue(toggle.isEnabled());
        assertEquals(1f, toggle.getValue(), 0.0001f);

        toggle.setValue(false);
        assertFalse(toggle.isOn());
        assertEquals(0f, toggle.getValue(), 0.0001f);
    }

    @Test
    void buttonClickRunsOnlyWhenEnabled() {
        Button button = new Button("Test", new Vector2(10f, 10f));
        final int[] calls = {0};

        button.setOnClick(() -> calls[0]++);
        button.click();
        assertEquals(1, calls[0]);

        button.setEnabled(false);
        button.click();
        assertEquals(1, calls[0]);
    }
}
