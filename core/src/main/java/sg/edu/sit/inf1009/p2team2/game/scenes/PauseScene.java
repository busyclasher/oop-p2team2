package sg.edu.sit.inf1009.p2team2.game.scenes;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;
import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.io.input.Keyboard;
import sg.edu.sit.inf1009.p2team2.engine.io.input.Mouse;
import sg.edu.sit.inf1009.p2team2.engine.io.output.Renderer;
import sg.edu.sit.inf1009.p2team2.engine.scene.InputHandler;
import sg.edu.sit.inf1009.p2team2.engine.scene.ResourceLoader;
import sg.edu.sit.inf1009.p2team2.engine.scene.Scene;
import sg.edu.sit.inf1009.p2team2.engine.scene.SceneRenderer;

/**
 * Pause screen — pushed on top of GamePlayScene when ESC is pressed.
 * Options: Resume | Settings | Exit to Menu
 */
public class PauseScene extends Scene {

    private static final int   COOLDOWN_FRAMES = 10;
    private static final float BTN_W = 260f, BTN_H = 54f;

    private final List<String> items;
    private int selectedIndex;
    private int keyboardCooldown;

    public PauseScene(EngineContext context) {
        super(context);
        this.items            = new ArrayList<>();
        this.selectedIndex    = 0;
        this.keyboardCooldown = 0;

        setInputHandler(new PauseInputHandler(this));
        setSceneRenderer(new PauseRenderer(this));
        setResourceLoader(new ResourceLoader() {
            @Override public void load()   { setLoaded(true); }
            @Override public void unload() { setLoaded(false); }
        });
    }

    @Override
    public void onEnter() {
        items.clear();
        items.add("Resume");
        items.add("Settings");
        items.add("Exit to Menu");
        selectedIndex    = 0;
        keyboardCooldown = COOLDOWN_FRAMES;
    }

    @Override
    public void update(float dt) {
        if (keyboardCooldown > 0) keyboardCooldown--;
    }

    // ── Input ────────────────────────────────────────────────────────────────

    void processInput() {
        Keyboard kb    = getContext().getInputManager().getKeyboard();
        Mouse    mouse = getContext().getInputManager().getMouse();
        Renderer r     = getContext().getOutputManager().getRenderer();

        if (kb.isKeyPressed(Input.Keys.ESCAPE)) {
            getContext().getSceneManager().pop(); // resume
            return;
        }

        if (keyboardCooldown == 0) {
            if (kb.isKeyPressed(Input.Keys.UP) || kb.isKeyPressed(Input.Keys.W)) {
                selectedIndex = (selectedIndex - 1 + items.size()) % items.size();
                keyboardCooldown = COOLDOWN_FRAMES;
            } else if (kb.isKeyPressed(Input.Keys.DOWN) || kb.isKeyPressed(Input.Keys.S)) {
                selectedIndex = (selectedIndex + 1) % items.size();
                keyboardCooldown = COOLDOWN_FRAMES;
            }
        }

        if (kb.isKeyPressed(Input.Keys.ENTER) || kb.isKeyPressed(Input.Keys.SPACE)) {
            activate(selectedIndex);
            return;
        }

        // Mouse hover
        Vector2 mp = mouse.getPosition();
        float cx = r.getWorldWidth() / 2f, cy = r.getWorldHeight() / 2f;
        for (int i = 0; i < items.size(); i++) {
            if (buttonRect(cx, cy, i).contains(mp.x, mp.y)) {
                selectedIndex = i;
                break;
            }
        }

        if (mouse.isButtonPressed(0)) {
            for (int i = 0; i < items.size(); i++) {
                if (buttonRect(cx, cy, i).contains(mp.x, mp.y)) {
                    activate(i);
                    return;
                }
            }
        }
    }

    private Rectangle buttonRect(float cx, float cy, int index) {
        float space = 80f;
        float startY = cy + (items.size() - 1) * space / 2f;
        float by = startY - index * space;
        return new Rectangle(cx - BTN_W / 2f, by - BTN_H / 2f, BTN_W, BTN_H);
    }

    private void activate(int index) {
        switch (items.get(index)) {
            case "Resume":
                getContext().getSceneManager().pop();
                break;
            case "Settings":
                getContext().getSceneManager().push(new SettingsScene(getContext()));
                break;
            case "Exit to Menu":
                getContext().getSceneManager().pop(); // pop PauseScene
                getContext().getSceneManager().pop(); // pop GamePlayScene → back to menu
                break;
        }
    }

    // ── Render ───────────────────────────────────────────────────────────────

    void renderScene() {
        Renderer r  = getContext().getOutputManager().getRenderer();
        float    ww = r.getWorldWidth();
        float    wh = r.getWorldHeight();
        float    cx = ww / 2f, cy = wh / 2f;

        r.clear();
        r.begin();

        r.drawBackground("game-scene.png");
        r.drawRect(new Rectangle(0, 0, ww, wh), new Color(0f, 0f, 0f, 0.72f), true);

        // Title
        r.drawText("PAUSED",
            new Vector2(cx - 55f, cy + 180f), "default",
            new Color(0.2f, 0.9f, 0.4f, 1f));

        // Buttons
        for (int i = 0; i < items.size(); i++) {
            boolean sel = (i == selectedIndex);
            Rectangle box = buttonRect(cx, cy, i);
            Color bg  = sel ? new Color(0.15f, 0.55f, 0.25f, 0.9f)
                            : new Color(0.08f, 0.08f, 0.08f, 0.75f);
            Color txt = sel ? Color.YELLOW : Color.WHITE;
            r.drawRect(box, bg, true);
            r.drawRect(box, sel ? Color.YELLOW : new Color(0.5f, 0.5f, 0.5f, 1f), false);
            r.drawText(items.get(i),
                new Vector2(box.x + BTN_W / 2f - 65f, box.y + BTN_H / 2f + 8f),
                "default", txt);
        }

        // Footer
        r.drawText("ESC — Resume   Arrow Keys / Enter to navigate",
            new Vector2(cx - 230f, 30f), "default",
            new Color(0.55f, 0.55f, 0.55f, 1f));

        r.end();
    }

    // ── Inner classes ────────────────────────────────────────────────────────

    private static final class PauseInputHandler extends InputHandler {
        private final PauseScene scene;
        PauseInputHandler(PauseScene s) { super(s.getContext()); this.scene = s; }
        @Override public void handleInput() { scene.processInput(); }
    }

    private static final class PauseRenderer extends SceneRenderer {
        private final PauseScene scene;
        PauseRenderer(PauseScene s) { super(s.getContext()); this.scene = s; }
        @Override public void render() { scene.renderScene(); }
    }
}
