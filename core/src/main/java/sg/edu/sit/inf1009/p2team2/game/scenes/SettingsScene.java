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
import sg.edu.sit.inf1009.p2team2.game.ui.GameUiTheme;

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
    private static final float DROPDOWN_OPTION_H = 40f;
    private static final float DROPDOWN_GAP = 6f;

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
    private boolean resolutionDropdownOpen;
    private int hoveredResolutionIndex;

    public SettingsScene(EngineContext context) {
        super(context);
        this.selectedRow     = 0;
        this.keyboardCooldown = 0;
        this.selectedResolutionIndex = 2;
        this.fullscreenEnabled = false;
        this.resolutionDropdownOpen = false;
        this.hoveredResolutionIndex = -1;

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
        resolutionDropdownOpen = false;
        hoveredResolutionIndex = -1;
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

    private float volumeLabelBaseline(int row, Renderer r) {
        return sliderY(r, row) + 44f;
    }

    private float optionLabelBaseline(int row, Renderer r) {
        return sliderY(r, row) + 52f;
    }

    private Rectangle optionRect(Renderer r, int row) {
        float sx = sliderX(r);
        float sy = sliderY(r, row);
        return new Rectangle(sx, sy - 24f, SLIDER_W, 48f);
    }

    private Rectangle dropdownOptionRect(Renderer r, int index) {
        Rectangle base = optionRect(r, ROW_RESOLUTION);
        float y = base.y - DROPDOWN_GAP - (index + 1) * DROPDOWN_OPTION_H - index * DROPDOWN_GAP;
        return new Rectangle(base.x, y, base.width, DROPDOWN_OPTION_H);
    }

    private Rectangle dropdownBounds(Renderer r) {
        Rectangle base = optionRect(r, ROW_RESOLUTION);
        float totalHeight = RESOLUTION_OPTIONS.length * DROPDOWN_OPTION_H
            + (RESOLUTION_OPTIONS.length - 1) * DROPDOWN_GAP;
        return new Rectangle(base.x, base.y - DROPDOWN_GAP - totalHeight, base.width, totalHeight);
    }

    private void updateMouseHover(Renderer r, Vector2 mp) {
        hoveredResolutionIndex = -1;

        float sx = sliderX(r);
        for (int i = 0; i < LABELS.length; i++) {
            float sy = sliderY(r, i);
            Rectangle hitArea = new Rectangle(sx - 42f, sy - SLIDER_H * 2f, SLIDER_W + 42f, SLIDER_H * 4f);
            if (hitArea.contains(mp.x, mp.y)) {
                selectedRow = i;
            }
        }

        Rectangle resolutionRect = optionRect(r, ROW_RESOLUTION);
        Rectangle fullscreenRect = optionRect(r, ROW_FULLSCREEN);
        if (resolutionRect.contains(mp.x, mp.y)) {
            selectedRow = ROW_RESOLUTION;
        } else if (fullscreenRect.contains(mp.x, mp.y)) {
            selectedRow = ROW_FULLSCREEN;
        }

        if (resolutionDropdownOpen) {
            for (int i = 0; i < RESOLUTION_OPTIONS.length; i++) {
                Rectangle option = dropdownOptionRect(r, i);
                if (option.contains(mp.x, mp.y)) {
                    hoveredResolutionIndex = i;
                    selectedRow = ROW_RESOLUTION;
                    break;
                }
            }
        }
    }

    // ── Input ────────────────────────────────────────────────────────────────

    void processInput() {
        Keyboard kb    = getContext().getInputManager().getKeyboard();
        Mouse    mouse = getContext().getInputManager().getMouse();
        float[]  vols  = getVolumes();
        Renderer r     = getContext().getOutputManager().getRenderer();

        if (kb.isKeyPressed(Input.Keys.ESCAPE)) {
            if (resolutionDropdownOpen) {
                resolutionDropdownOpen = false;
                GameAudio.playUiClick(getContext());
                return;
            }
            GameAudio.playUiClick(getContext());
            getContext().getSceneManager().pop();
            return;
        }

        Vector2 mp = mouse.getPosition();
        updateMouseHover(r, mp);

        if (keyboardCooldown == 0) {
            if (resolutionDropdownOpen) {
                if (kb.isKeyPressed(Input.Keys.UP) || kb.isKeyPressed(Input.Keys.W)) {
                    adjustResolution(-1);
                    keyboardCooldown = COOLDOWN_MAX;
                    GameAudio.playUiClick(getContext());
                } else if (kb.isKeyPressed(Input.Keys.DOWN) || kb.isKeyPressed(Input.Keys.S)) {
                    adjustResolution(1);
                    keyboardCooldown = COOLDOWN_MAX;
                    GameAudio.playUiClick(getContext());
                } else if (kb.isKeyPressed(Input.Keys.ENTER) || kb.isKeyPressed(Input.Keys.SPACE)) {
                    resolutionDropdownOpen = false;
                    keyboardCooldown = COOLDOWN_MAX;
                    GameAudio.playUiClick(getContext());
                } else if (kb.isKeyPressed(Input.Keys.LEFT) || kb.isKeyPressed(Input.Keys.A)) {
                    adjustResolution(-1);
                    keyboardCooldown = COOLDOWN_MAX;
                    GameAudio.playUiClick(getContext());
                } else if (kb.isKeyPressed(Input.Keys.RIGHT) || kb.isKeyPressed(Input.Keys.D)) {
                    adjustResolution(1);
                    keyboardCooldown = COOLDOWN_MAX;
                    GameAudio.playUiClick(getContext());
                }
            } else {
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
                        resolutionDropdownOpen = !resolutionDropdownOpen;
                        keyboardCooldown = COOLDOWN_MAX;
                        GameAudio.playUiClick(getContext());
                    }
                }
            }
        }

        // Mouse click/drag — set volume to mouse position while held
        boolean mousePressed = mouse.isButtonPressed(0);
        if (mouse.isButtonDown(0)) {
            float sx = sliderX(r);
            for (int i = 0; i < LABELS.length; i++) {
                float sy = sliderY(r, i);
                Rectangle hitArea = new Rectangle(sx - 42f, sy - SLIDER_H * 2f, SLIDER_W + 42f, SLIDER_H * 4f);
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
            Rectangle resolutionRect = optionRect(r, ROW_RESOLUTION);
            Rectangle fullscreenRect = optionRect(r, ROW_FULLSCREEN);

            if (resolutionDropdownOpen && hoveredResolutionIndex >= 0) {
                selectedResolutionIndex = hoveredResolutionIndex;
                resolutionDropdownOpen = false;
                GameAudio.playUiClick(getContext());
            } else if (resolutionRect.contains(mp.x, mp.y)) {
                selectedRow = ROW_RESOLUTION;
                resolutionDropdownOpen = !resolutionDropdownOpen;
                GameAudio.playUiClick(getContext());
            } else if (fullscreenRect.contains(mp.x, mp.y)) {
                selectedRow = ROW_FULLSCREEN;
                resolutionDropdownOpen = false;
                toggleFullscreenSelection();
                GameAudio.playUiClick(getContext());
            } else if (resolutionDropdownOpen && !dropdownBounds(r).contains(mp.x, mp.y)) {
                resolutionDropdownOpen = false;
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
        r.drawTextCentered("SETTINGS",
            new Vector2(ww / 2f, wh - 78f), GameUiTheme.FONT_TITLE_SMALL,
            GameUiTheme.TITLE_PRIMARY);

        float sx = sliderX(r);

        for (int i = 0; i < LABELS.length; i++) {
            float sy   = sliderY(r, i);
            float labelY = volumeLabelBaseline(i, r);
            boolean sel = (i == selectedRow);

            Color labelColor = sel ? GameUiTheme.TEXT_HIGHLIGHT : GameUiTheme.TEXT_PRIMARY;

            // Volume icon — mute when at 0, speaker otherwise
            String icon = (vols[i] <= 0f) ? "volume-mute.png" : "volume-on.png";
            r.drawSprite(icon, new Vector2(sx - 26f, labelY - 10f), 30f, 30f);

            // Label + percentage
            int pct = Math.round(vols[i] * 100f);
            r.drawText(LABELS[i] + "   " + pct + "%",
                new Vector2(sx, labelY), GameUiTheme.FONT_BODY_LARGE, labelColor);

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
        if (resolutionDropdownOpen) {
            drawResolutionDropdown(r);
        }

        // Footer
        r.drawTextCentered("Up/Down - select   Left/Right - adjust   Enter/Space - toggle",
            new Vector2(ww / 2f, 30f), GameUiTheme.FONT_BODY_SMALL,
            GameUiTheme.TEXT_SUBTLE);
        r.drawTextCentered("Drag sliders   Click resolution/fullscreen   ESC - save + back",
            new Vector2(ww / 2f, 10f), GameUiTheme.FONT_BODY_SMALL,
            GameUiTheme.TEXT_SUBTLE);

        r.end();
    }

    private void drawOptionRow(Renderer r, int row, String label, String value, Color valueColor, boolean toggleStyle) {
        float sx = sliderX(r);
        float sy = sliderY(r, row);
        float labelY = optionLabelBaseline(row, r);
        boolean selected = (row == selectedRow);
        Rectangle rect = optionRect(r, row);

        r.drawText(label, new Vector2(sx, labelY), GameUiTheme.FONT_BODY_LARGE,
            selected ? GameUiTheme.TEXT_HIGHLIGHT : GameUiTheme.TEXT_PRIMARY);
        r.drawRect(rect, new Color(0.12f, 0.12f, 0.12f, 0.9f), true);
        r.drawRect(rect, selected ? Color.YELLOW : new Color(0.5f, 0.5f, 0.5f, 1f), false);

        String decoratedValue;
        if (toggleStyle) {
            decoratedValue = "[ " + value + " ]";
        } else {
            decoratedValue = resolutionDropdownOpen ? "v  " + value + "  v" : "<  " + value + "  >";
        }
        r.drawTextCentered(decoratedValue, rect, GameUiTheme.FONT_BODY_LARGE, valueColor);
    }

    private void drawResolutionDropdown(Renderer r) {
        Rectangle bounds = dropdownBounds(r);
        r.drawRect(bounds, new Color(0.06f, 0.08f, 0.10f, 0.95f), true);
        r.drawRect(bounds, new Color(0.50f, 0.80f, 1.0f, 1f), false);

        for (int i = 0; i < RESOLUTION_OPTIONS.length; i++) {
            Rectangle option = dropdownOptionRect(r, i);
            boolean selected = (i == selectedResolutionIndex);
            boolean hovered = (i == hoveredResolutionIndex);

            Color fill = hovered
                ? new Color(0.12f, 0.28f, 0.40f, 1f)
                : selected
                    ? new Color(0.10f, 0.20f, 0.18f, 1f)
                    : new Color(0.10f, 0.10f, 0.10f, 0.95f);
            Color textColor = hovered || selected
                ? GameUiTheme.TEXT_HIGHLIGHT
                : GameUiTheme.TEXT_PRIMARY;

            r.drawRect(option, fill, true);
            r.drawRect(option,
                hovered ? Color.YELLOW : new Color(0.35f, 0.45f, 0.55f, 1f), false);
            int[] resolution = RESOLUTION_OPTIONS[i];
            r.drawTextCentered(resolution[0] + " x " + resolution[1],
                option, GameUiTheme.FONT_BODY, textColor);
        }
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
