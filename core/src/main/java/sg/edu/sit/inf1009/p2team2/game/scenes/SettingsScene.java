package sg.edu.sit.inf1009.p2team2.game.scenes;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.io.input.Keyboard;
import sg.edu.sit.inf1009.p2team2.engine.io.input.Mouse;
import sg.edu.sit.inf1009.p2team2.engine.io.output.Audio;
import sg.edu.sit.inf1009.p2team2.engine.io.output.Renderer;
import sg.edu.sit.inf1009.p2team2.engine.scene.InputHandler;
import sg.edu.sit.inf1009.p2team2.engine.scene.ResourceLoader;
import sg.edu.sit.inf1009.p2team2.engine.scene.Scene;
import sg.edu.sit.inf1009.p2team2.engine.scene.SceneRenderer;

/**
 * Settings screen — adjust master, music, and SFX volume with sliders.
 * Navigation: Up/Down to select row, Left/Right to adjust, ESC to go back.
 * Mouse: click anywhere on a slider bar to set the value directly.
 */
public class SettingsScene extends Scene {

    private static final float SLIDER_W     = 400f;
    private static final float SLIDER_H     = 18f;
    private static final float STEP         = 0.05f;
    private static final int   COOLDOWN_MAX = 6;

    private static final String[] LABELS = { "Master Volume", "Music Volume", "SFX Volume" };

    private int   selectedRow;
    private int   keyboardCooldown;

    public SettingsScene(EngineContext context) {
        super(context);
        this.selectedRow     = 0;
        this.keyboardCooldown = 0;

        setInputHandler(new SettingsInputHandler(this));
        setSceneRenderer(new SettingsRenderer(this));
        setResourceLoader(new ResourceLoader() {
            @Override public void load()   { setLoaded(true); }
            @Override public void unload() { setLoaded(false); }
        });
    }

    @Override
    public void update(float dt) {
        if (keyboardCooldown > 0) keyboardCooldown--;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private float[] getVolumes() {
        Audio a = context.getOutputManager().getAudio();
        return new float[]{ a.getMasterVolume(), a.getMusicVolume(), a.getSfxVolume() };
    }

    private void setVolume(int row, float value) {
        Audio a = context.getOutputManager().getAudio();
        value = Math.max(0f, Math.min(1f, value));
        switch (row) {
            case 0: a.setMasterVolume(value); break;
            case 1: a.setMusicVolume(value);  break;
            case 2: a.setSfxVolume(value);    break;
        }
    }

    /** X position of left edge of the slider bar for a given row. */
    private float sliderX(Renderer r) {
        return r.getWorldWidth() / 2f - SLIDER_W / 2f;
    }

    /** Y centre of the slider bar for a given row. */
    private float sliderY(Renderer r, int row) {
        float cy = r.getWorldHeight() / 2f;
        return cy + 80f - row * 110f;
    }

    // ── Input ────────────────────────────────────────────────────────────────

    void processInput() {
        Keyboard kb    = context.getInputManager().getKeyboard();
        Mouse    mouse = context.getInputManager().getMouse();
        float[]  vols  = getVolumes();
        Renderer r     = context.getOutputManager().getRenderer();

        if (kb.isKeyPressed(Input.Keys.ESCAPE)) {
            context.getOutputManager().getAudio().saveSettings();
            context.getSceneManager().pop();
            return;
        }

        if (keyboardCooldown == 0) {
            if (kb.isKeyPressed(Input.Keys.UP) || kb.isKeyPressed(Input.Keys.W)) {
                selectedRow = (selectedRow - 1 + LABELS.length) % LABELS.length;
                keyboardCooldown = COOLDOWN_MAX;
            } else if (kb.isKeyPressed(Input.Keys.DOWN) || kb.isKeyPressed(Input.Keys.S)) {
                selectedRow = (selectedRow + 1) % LABELS.length;
                keyboardCooldown = COOLDOWN_MAX;
            } else if (kb.isKeyPressed(Input.Keys.LEFT) || kb.isKeyPressed(Input.Keys.A)) {
                setVolume(selectedRow, vols[selectedRow] - STEP);
                keyboardCooldown = COOLDOWN_MAX;
            } else if (kb.isKeyPressed(Input.Keys.RIGHT) || kb.isKeyPressed(Input.Keys.D)) {
                setVolume(selectedRow, vols[selectedRow] + STEP);
                keyboardCooldown = COOLDOWN_MAX;
            }
        }

        // Mouse click/drag — set volume to mouse position while held
        if (mouse.isButtonDown(0)) {
            Vector2 mp = mouse.getPosition();
            float sx = sliderX(r);
            for (int i = 0; i < LABELS.length; i++) {
                float sy = sliderY(r, i);
                Rectangle hitArea = new Rectangle(sx, sy - SLIDER_H * 2f, SLIDER_W, SLIDER_H * 4f);
                if (hitArea.contains(mp.x, mp.y)) {
                    selectedRow = i;
                    setVolume(i, (mp.x - sx) / SLIDER_W);
                    break;
                }
            }
        }
    }

    // ── Render ───────────────────────────────────────────────────────────────

    void renderScene() {
        Renderer r  = context.getOutputManager().getRenderer();
        float    ww = r.getWorldWidth();
        float    wh = r.getWorldHeight();
        float[]  vols = getVolumes();

        r.clear();
        r.begin();

        r.drawBackground("menu-scene.png");
        r.drawRect(new Rectangle(0, 0, ww, wh), new Color(0f, 0f, 0f, 0.65f), true);

        // Title
        r.drawText("SETTINGS",
            new Vector2(ww / 2f - 70f, wh - 80f), "default",
            new Color(0.2f, 0.9f, 0.4f, 1f));

        float sx = sliderX(r);

        for (int i = 0; i < LABELS.length; i++) {
            float sy   = sliderY(r, i);
            boolean sel = (i == selectedRow);

            Color labelColor = sel ? Color.YELLOW : Color.WHITE;

            // Volume icon — mute when at 0, speaker otherwise
            String icon = (vols[i] <= 0f) ? "volume-mute.png" : "volume-on.png";
            r.drawSprite(icon, new Vector2(sx - 26f, sy + 16f), 32f, 32f);

            // Label + percentage
            int pct = Math.round(vols[i] * 100f);
            r.drawText(LABELS[i] + "   " + pct + "%",
                new Vector2(sx, sy + 28f), "default", labelColor);

            // Track (background bar)
            r.drawRect(new Rectangle(sx, sy - SLIDER_H / 2f, SLIDER_W, SLIDER_H),
                new Color(0.25f, 0.25f, 0.25f, 1f), true);

            // Fill
            Color fillColor = sel
                ? new Color(0.2f, 0.85f, 0.35f, 1f)
                : new Color(0.15f, 0.55f, 0.25f, 1f);
            float fillW = Math.max(0f, vols[i] * SLIDER_W);
            if (fillW > 0f) {
                r.drawRect(new Rectangle(sx, sy - SLIDER_H / 2f, fillW, SLIDER_H),
                    fillColor, true);
            }

            // Track outline
            r.drawRect(new Rectangle(sx, sy - SLIDER_H / 2f, SLIDER_W, SLIDER_H),
                sel ? Color.YELLOW : new Color(0.5f, 0.5f, 0.5f, 1f), false);
        }

        // Footer
        r.drawText("Up/Down - select   Left/Right - adjust   ESC - back",
            new Vector2(ww / 2f - 265f, 30f), "default",
            new Color(0.55f, 0.55f, 0.55f, 1f));

        r.end();
    }

    // ── Inner classes ────────────────────────────────────────────────────────

    private static final class SettingsInputHandler extends InputHandler {
        private final SettingsScene scene;
        SettingsInputHandler(SettingsScene s) { super(s.context); this.scene = s; }
        @Override public void handleInput() { scene.processInput(); }
    }

    private static final class SettingsRenderer extends SceneRenderer {
        private final SettingsScene scene;
        SettingsRenderer(SettingsScene s) { super(s.context); this.scene = s; }
        @Override public void render() { scene.renderScene(); }
    }
}
