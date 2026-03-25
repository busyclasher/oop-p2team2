package sg.edu.sit.inf1009.p2team2.game.scenes;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import sg.edu.sit.inf1009.p2team2.engine.config.ConfigKeys;
import sg.edu.sit.inf1009.p2team2.engine.config.ConfigManager;
import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.io.input.Keyboard;
import sg.edu.sit.inf1009.p2team2.engine.io.input.Mouse;
import sg.edu.sit.inf1009.p2team2.engine.io.output.Audio;
import sg.edu.sit.inf1009.p2team2.engine.io.output.Display;
import sg.edu.sit.inf1009.p2team2.engine.io.output.Renderer;
import sg.edu.sit.inf1009.p2team2.engine.scene.InputHandler;
import sg.edu.sit.inf1009.p2team2.engine.scene.ResourceLoader;
import sg.edu.sit.inf1009.p2team2.engine.scene.Scene;
import sg.edu.sit.inf1009.p2team2.engine.scene.SceneRenderer;
import sg.edu.sit.inf1009.p2team2.game.audio.GameAudio;

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
    private static final float ROW_SPACING  = 90f;

    private static final String[] LABELS = { "Master Volume", "Music Volume", "SFX Volume" };
    private static final int ROW_MASTER = 0;
    private static final int ROW_MUSIC = 1;
    private static final int ROW_SFX = 2;
    private static final int ROW_RESOLUTION = 3;
    private static final int ROW_FULLSCREEN = 4;
    private static final int ROW_COUNT = 5;
    private static final int[][] RESOLUTION_OPTIONS = {
        { 800, 600 },
        { 1024, 576 },
        { 1280, 720 },
        { 1600, 900 },
        { 1920, 1080 }
    };

    private int   selectedRow;
    private int   keyboardCooldown;
    private int   selectedResolutionIndex;
    private boolean fullscreenEnabled;

    public SettingsScene(EngineContext context) {
        super(context);
        this.selectedRow     = 0;
        this.keyboardCooldown = 0;
        this.selectedResolutionIndex = 2;
        this.fullscreenEnabled = false;

        setInputHandler(new SettingsInputHandler(this));
        setSceneRenderer(new SettingsRenderer(this));
        setResourceLoader(new ResourceLoader() {
            @Override public void load()   { setLoaded(true); }
            @Override public void unload() { setLoaded(false); }
        });
    }

    @Override
    public void onEnter() {
        selectedRow = 0;
        keyboardCooldown = 0;
        loadDisplaySettings();
    }

    @Override
    public void onExit() {
        saveSettings();
    }

    @Override
    public void update(float dt) {
        if (keyboardCooldown > 0) keyboardCooldown--;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private float[] getVolumes() {
        Audio a = getContext().getOutputManager().getAudio();
        return new float[]{ a.getMasterVolume(), a.getMusicVolume(), a.getSfxVolume() };
    }

    private void setVolume(int row, float value) {
        Audio a = getContext().getOutputManager().getAudio();
        value = Math.max(0f, Math.min(1f, value));
        switch (row) {
            case ROW_MASTER: a.setMasterVolume(value); break;
            case ROW_MUSIC: a.setMusicVolume(value);  break;
            case ROW_SFX: a.setSfxVolume(value);    break;
            default: break;
        }
    }

    private void loadDisplaySettings() {
        ConfigManager config = getContext().getConfigManager();
        if (config == null) {
            return;
        }

        ensureConfig(config, ConfigKeys.DISPLAY_WIDTH);
        ensureConfig(config, ConfigKeys.DISPLAY_HEIGHT);
        ensureConfig(config, ConfigKeys.DISPLAY_FULLSCREEN);

        Display display = getContext().getOutputManager() == null ? null : getContext().getOutputManager().getDisplay();
        fullscreenEnabled = config.get(ConfigKeys.DISPLAY_FULLSCREEN);

        int width = config.get(ConfigKeys.DISPLAY_WIDTH);
        int height = config.get(ConfigKeys.DISPLAY_HEIGHT);
        if (display != null && !display.isFullscreen()) {
            width = display.getWidth();
            height = display.getHeight();
            fullscreenEnabled = false;
        }

        selectedResolutionIndex = findClosestResolutionIndex(width, height);
    }

    private void saveSettings() {
        Audio audio = getContext().getOutputManager().getAudio();
        if (audio != null) {
            audio.saveSettings();
        }

        ConfigManager config = getContext().getConfigManager();
        if (config == null) {
            return;
        }

        int[] resolution = RESOLUTION_OPTIONS[selectedResolutionIndex];
        config.set(ConfigKeys.DISPLAY_WIDTH, Integer.valueOf(resolution[0]));
        config.set(ConfigKeys.DISPLAY_HEIGHT, Integer.valueOf(resolution[1]));
        config.set(ConfigKeys.DISPLAY_FULLSCREEN, Boolean.valueOf(fullscreenEnabled));

        if (audio != null) {
            config.set(ConfigKeys.AUDIO_VOLUME, Float.valueOf(audio.getMasterVolume()));
        }

        config.save(null);
    }

    private <T> void ensureConfig(ConfigManager config, sg.edu.sit.inf1009.p2team2.engine.config.ConfigKey<T> key) {
        if (config != null && key != null && !config.has(key)) {
            config.set(key, key.defaultValue());
        }
    }

    private void adjustSelectedSetting(int direction, float[] volumes) {
        switch (selectedRow) {
            case ROW_MASTER:
            case ROW_MUSIC:
            case ROW_SFX:
                setVolume(selectedRow, volumes[selectedRow] + direction * STEP);
                break;
            case ROW_RESOLUTION:
                adjustResolution(direction);
                break;
            case ROW_FULLSCREEN:
                toggleFullscreenSelection();
                break;
            default:
                break;
        }
    }

    private void adjustResolution(int direction) {
        if (direction == 0) {
            return;
        }
        int count = RESOLUTION_OPTIONS.length;
        selectedResolutionIndex = (selectedResolutionIndex + direction + count) % count;
    }

    private void toggleFullscreenSelection() {
        fullscreenEnabled = !fullscreenEnabled;
    }

    private int findClosestResolutionIndex(int width, int height) {
        int bestIndex = 0;
        int bestDelta = Integer.MAX_VALUE;
        for (int i = 0; i < RESOLUTION_OPTIONS.length; i++) {
            int[] option = RESOLUTION_OPTIONS[i];
            int delta = Math.abs(option[0] - width) + Math.abs(option[1] - height);
            if (delta < bestDelta) {
                bestDelta = delta;
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    private String currentResolutionLabel() {
        int[] resolution = RESOLUTION_OPTIONS[selectedResolutionIndex];
        return resolution[0] + " x " + resolution[1];
    }

    /** X position of left edge of the slider bar for a given row. */
    private float sliderX(Renderer r) {
        return r.getWorldWidth() / 2f - SLIDER_W / 2f;
    }

    /** Y centre of the slider bar for a given row. */
    private float sliderY(Renderer r, int row) {
        float cy = r.getWorldHeight() / 2f;
        return cy + 160f - row * ROW_SPACING;
    }

    private Rectangle optionRect(Renderer r, int row) {
        float sx = sliderX(r);
        float sy = sliderY(r, row);
        return new Rectangle(sx, sy - 22f, SLIDER_W, 44f);
    }

    // ── Input ────────────────────────────────────────────────────────────────

    void processInput() {
        Keyboard kb    = getContext().getInputManager().getKeyboard();
        Mouse    mouse = getContext().getInputManager().getMouse();
        float[]  vols  = getVolumes();
        Renderer r     = getContext().getOutputManager().getRenderer();

        if (kb.isKeyPressed(Input.Keys.ESCAPE)) {
            GameAudio.playUiClick(getContext());
            getContext().getSceneManager().pop();
            return;
        }

        if (keyboardCooldown == 0) {
            if (kb.isKeyPressed(Input.Keys.UP) || kb.isKeyPressed(Input.Keys.W)) {
                selectedRow = (selectedRow - 1 + ROW_COUNT) % ROW_COUNT;
                keyboardCooldown = COOLDOWN_MAX;
                GameAudio.playUiClick(getContext());
            } else if (kb.isKeyPressed(Input.Keys.DOWN) || kb.isKeyPressed(Input.Keys.S)) {
                selectedRow = (selectedRow + 1) % ROW_COUNT;
                keyboardCooldown = COOLDOWN_MAX;
                GameAudio.playUiClick(getContext());
            } else if (kb.isKeyPressed(Input.Keys.LEFT) || kb.isKeyPressed(Input.Keys.A)) {
                adjustSelectedSetting(-1, vols);
                keyboardCooldown = COOLDOWN_MAX;
                GameAudio.playUiClick(getContext());
            } else if (kb.isKeyPressed(Input.Keys.RIGHT) || kb.isKeyPressed(Input.Keys.D)) {
                adjustSelectedSetting(1, vols);
                keyboardCooldown = COOLDOWN_MAX;
                GameAudio.playUiClick(getContext());
            } else if (kb.isKeyPressed(Input.Keys.ENTER) || kb.isKeyPressed(Input.Keys.SPACE)) {
                if (selectedRow == ROW_FULLSCREEN) {
                    toggleFullscreenSelection();
                    keyboardCooldown = COOLDOWN_MAX;
                    GameAudio.playUiClick(getContext());
                } else if (selectedRow == ROW_RESOLUTION) {
                    adjustResolution(1);
                    keyboardCooldown = COOLDOWN_MAX;
                    GameAudio.playUiClick(getContext());
                }
            }
        }

        // Mouse click/drag — set volume to mouse position while held
        boolean mousePressed = mouse.isButtonPressed(0);
        if (mouse.isButtonDown(0)) {
            Vector2 mp = mouse.getPosition();
            float sx = sliderX(r);
            for (int i = 0; i < LABELS.length; i++) {
                float sy = sliderY(r, i);
                Rectangle hitArea = new Rectangle(sx, sy - SLIDER_H * 2f, SLIDER_W, SLIDER_H * 4f);
                if (hitArea.contains(mp.x, mp.y)) {
                    selectedRow = i;
                    if (mousePressed) {
                        GameAudio.playUiClick(getContext());
                    }
                    setVolume(i, (mp.x - sx) / SLIDER_W);
                    break;
                }
            }
        }

        if (mousePressed) {
            Vector2 mp = mouse.getPosition();
            Rectangle resolutionRect = optionRect(r, ROW_RESOLUTION);
            Rectangle fullscreenRect = optionRect(r, ROW_FULLSCREEN);

            if (resolutionRect.contains(mp.x, mp.y)) {
                selectedRow = ROW_RESOLUTION;
                adjustResolution(mp.x < resolutionRect.x + resolutionRect.width / 2f ? -1 : 1);
                GameAudio.playUiClick(getContext());
            } else if (fullscreenRect.contains(mp.x, mp.y)) {
                selectedRow = ROW_FULLSCREEN;
                toggleFullscreenSelection();
                GameAudio.playUiClick(getContext());
            }
        }
    }

    // ── Render ───────────────────────────────────────────────────────────────

    void renderScene() {
        Renderer r  = getContext().getOutputManager().getRenderer();
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

        drawOptionRow(r, ROW_RESOLUTION, "Resolution", currentResolutionLabel(), Color.WHITE, false);
        drawOptionRow(r, ROW_FULLSCREEN, "Fullscreen", fullscreenEnabled ? "ON" : "OFF",
            fullscreenEnabled ? new Color(0.30f, 0.95f, 0.45f, 1f) : new Color(0.95f, 0.45f, 0.45f, 1f), true);

        // Footer
        r.drawText("Up/Down - select   Left/Right - adjust   Enter/Space - toggle",
            new Vector2(ww / 2f - 315f, 30f), "default",
            new Color(0.55f, 0.55f, 0.55f, 1f));
        r.drawText("Drag sliders   Click resolution/fullscreen   ESC - save + back",
            new Vector2(ww / 2f - 310f, 10f), "default",
            new Color(0.55f, 0.55f, 0.55f, 1f));

        r.end();
    }

    private void drawOptionRow(Renderer r, int row, String label, String value, Color valueColor, boolean toggleStyle) {
        float sx = sliderX(r);
        float sy = sliderY(r, row);
        boolean selected = (row == selectedRow);
        Rectangle rect = optionRect(r, row);

        r.drawText(label, new Vector2(sx, sy + 28f), "default", selected ? Color.YELLOW : Color.WHITE);
        r.drawRect(rect, new Color(0.12f, 0.12f, 0.12f, 0.9f), true);
        r.drawRect(rect, selected ? Color.YELLOW : new Color(0.5f, 0.5f, 0.5f, 1f), false);

        String decoratedValue = toggleStyle ? "[ " + value + " ]" : "<  " + value + "  >";
        r.drawText(decoratedValue,
            new Vector2(rect.x + 16f, sy + 6f), "default", valueColor);
    }

    // ── Inner classes ────────────────────────────────────────────────────────

    private static final class SettingsInputHandler extends InputHandler {
        private final SettingsScene scene;
        SettingsInputHandler(SettingsScene s) { super(s.getContext()); this.scene = s; }
        @Override public void handleInput() { scene.processInput(); }
    }

    private static final class SettingsRenderer extends SceneRenderer {
        private final SettingsScene scene;
        SettingsRenderer(SettingsScene s) { super(s.getContext()); this.scene = s; }
        @Override public void render() { scene.renderScene(); }
    }
}
