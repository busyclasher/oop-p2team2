package sg.edu.sit.inf1009.p2team2.engine.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import sg.edu.sit.inf1009.p2team2.engine.scenes.Scene;

class SceneManagerTest {

    @Test
    void pushPeekUpdateRenderAndPopLifecycle() {
        SceneManager manager = new SceneManager(null);
        TestScene first = new TestScene(manager);
        TestScene second = new TestScene(manager);

        manager.push(first);
        assertSame(first, manager.peek());
        assertEquals(1, first.enterCalls);
        assertEquals(1, first.loadCalls);

        manager.update(0.1f);
        manager.render();
        assertEquals(1, first.inputCalls);
        assertEquals(1, first.updateCalls);
        assertEquals(1, first.renderCalls);

        manager.push(second);
        assertSame(second, manager.peek());
        assertEquals(1, first.exitCalls);
        assertEquals(1, second.enterCalls);

        manager.pop();
        assertSame(first, manager.peek());
        assertEquals(1, second.exitCalls);
        assertEquals(1, second.unloadCalls);

        manager.pop();
        assertNull(manager.peek());
        assertTrue(manager.isEmpty());
    }

    private static final class TestScene extends Scene {
        int enterCalls;
        int exitCalls;
        int loadCalls;
        int unloadCalls;
        int inputCalls;
        int updateCalls;
        int renderCalls;

        TestScene(SceneManager manager) {
            super(null);
        }

        @Override
        public void onEnter() {
            enterCalls++;
        }

        @Override
        public void onExit() {
            exitCalls++;
        }

        @Override
        public void load() {
            loadCalls++;
        }

        @Override
        public void unload() {
            unloadCalls++;
        }

        @Override
        public void update(float dt) {
            updateCalls++;
        }

        @Override
        public void render() {
            renderCalls++;
        }

        @Override
        public void handleInput() {
            inputCalls++;
        }
    }
}
