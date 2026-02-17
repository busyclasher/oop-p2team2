package sg.edu.sit.inf1009.p2team2.engine.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class InputMapTest {

    @Test
    void bindUnbindAndActionQueriesWorkWithKeyboard() {
        InputMap map = new InputMap("test");
        map.bindAction("jump", 32);

        FakeKeyboard keyboard = new FakeKeyboard();

        assertFalse(map.isActionActive("jump", keyboard));
        keyboard.setDown(32, true);
        assertTrue(map.isActionActive("jump", keyboard));
        assertTrue(map.isActionPressed("jump", keyboard));

        keyboard.nextFrame();
        assertFalse(map.isActionPressed("jump", keyboard));

        keyboard.setDown(32, false);
        assertTrue(map.isActionReleased("jump", keyboard));

        map.unbindAction("jump");
        assertFalse(map.hasAction("jump"));
        assertEquals(-1, map.getBoundKey("jump"));
    }

    @Test
    void noGdxOverloadsFailSafeWhenRuntimeInputUnavailable() {
        InputMap map = new InputMap("test");
        map.bindAction("shoot", 65);

        assertFalse(map.isActionActive("shoot"));
        assertFalse(map.isActionPressed("shoot"));
        assertFalse(map.isActionReleased("shoot"));
    }

    private static final class FakeKeyboard extends Keyboard {
        private final Map<Integer, Boolean> current = new HashMap<>();
        private final Map<Integer, Boolean> previous = new HashMap<>();

        void setDown(int keyCode, boolean down) {
            current.put(keyCode, down);
        }

        void nextFrame() {
            previous.clear();
            previous.putAll(current);
        }

        @Override
        public boolean isKeyDown(int keyCode) {
            return current.getOrDefault(keyCode, false);
        }

        @Override
        public boolean isKeyPressed(int keyCode) {
            return isKeyDown(keyCode) && !previous.getOrDefault(keyCode, false);
        }

        @Override
        public boolean isKeyReleased(int keyCode) {
            return !isKeyDown(keyCode) && previous.getOrDefault(keyCode, false);
        }
    }
}
