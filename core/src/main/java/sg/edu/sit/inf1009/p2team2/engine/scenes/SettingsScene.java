package sg.edu.sit.inf1009.p2team2.engine.scenes;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import sg.edu.sit.inf1009.p2team2.engine.config.ConfigManager;
import sg.edu.sit.inf1009.p2team2.engine.config.ConfigVar;
import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.input.Keyboard;
import sg.edu.sit.inf1009.p2team2.engine.output.Audio;
import sg.edu.sit.inf1009.p2team2.engine.output.Display;
import sg.edu.sit.inf1009.p2team2.engine.ui.Slider;
import sg.edu.sit.inf1009.p2team2.engine.ui.Toggle;

public class SettingsScene extends Scene {
    private static final int[] ENTITY_PRESETS = {20, 100, 400};

    private static final int ROW_VOLUME = 0;
    private static final int ROW_FULLSCREEN = 1;
    private static final int ROW_FRICTION = 2;
    private static final int ROW_GRAVITY = 3;
    private static final int ROW_SPEED = 4;
    private static final int ROW_COLLISIONS = 5;
    private static final int ROW_PRESET = 6;
    private static final int ROW_COUNT = 7;

    private Slider volumeSlider;
    private Slider frictionSlider;
    private Slider gravitySlider;
    private Slider speedSlider;
    private Toggle fullscreenToggle;
    private Toggle collisionsToggle;
    private int presetIndex;
    private int selectedRow;

    private static final String BACKGROUND_SPRITE = "settings.png";

    public SettingsScene(EngineContext context) {
        super(context);
        this.presetIndex = 0;
        this.selectedRow = 0;
    }

    @Override
    public void onEnter() {
        selectedRow = 0;
        loadSettings();
    }

    @Override
    public void onExit() {
        saveSettings();
    }

    @Override
    public void load() {
        volumeSlider = new Slider(new Vector2(440, 560));
        volumeSlider.setMin(0f);
        volumeSlider.setMax(100f);

        fullscreenToggle = new Toggle(440, 500);

        frictionSlider = new Slider(new Vector2(440, 440));
        frictionSlider.setMin(0f);
        frictionSlider.setMax(1f);

        gravitySlider = new Slider(new Vector2(440, 380));
        gravitySlider.setMin(-20f);
        gravitySlider.setMax(20f);

        speedSlider = new Slider(new Vector2(440, 320));
        speedSlider.setMin(60f);
        speedSlider.setMax(500f);

        collisionsToggle = new Toggle(440, 260);
    }

    @Override
    public void unload() {
        volumeSlider = null;
        frictionSlider = null;
        gravitySlider = null;
        speedSlider = null;
        fullscreenToggle = null;
        collisionsToggle = null;
    }

    @Override
    public void update(float dt) {
        if (volumeSlider == null
            || fullscreenToggle == null
            || frictionSlider == null
            || gravitySlider == null
            || speedSlider == null
            || collisionsToggle == null) {
            return;
        }

        volumeSlider.update();
        frictionSlider.update();
        gravitySlider.update();
        speedSlider.update();
        fullscreenToggle.update();
        collisionsToggle.update();

        Audio audio = context.getOutputManager().getAudio();
        if (audio != null) {
            audio.setMasterVolume(volumeSlider.getValue() / 100.0f);
        }
    }

    @Override
    public void render() {
        var renderer = context.getOutputManager().getRenderer();

        renderer.clear();
        renderer.begin();
        renderer.drawText("SETTINGS", new Vector2(330, 730), "default", Color.WHITE);

        if (volumeSlider != null) {
            volumeSlider.render(renderer, selectedRow == ROW_VOLUME);
            renderer.drawText(label("Volume", ROW_VOLUME), new Vector2(180, 565), "default", rowColor(ROW_VOLUME));
            renderer.drawText(formatPercent(volumeSlider.getValue()), new Vector2(630, 565), "default", rowColor(ROW_VOLUME));
        }
        if (fullscreenToggle != null) {
            fullscreenToggle.render(renderer, selectedRow == ROW_FULLSCREEN);
            renderer.drawText(label("Fullscreen", ROW_FULLSCREEN), new Vector2(180, 505), "default", rowColor(ROW_FULLSCREEN));
        }
        if (frictionSlider != null) {
            frictionSlider.render(renderer, selectedRow == ROW_FRICTION);
            renderer.drawText(label("Friction", ROW_FRICTION), new Vector2(180, 445), "default", rowColor(ROW_FRICTION));
            renderer.drawText(formatFloat(frictionSlider.getValue()), new Vector2(630, 445), "default", rowColor(ROW_FRICTION));
        }
        if (gravitySlider != null) {
            gravitySlider.render(renderer, selectedRow == ROW_GRAVITY);
            renderer.drawText(label("Gravity Y", ROW_GRAVITY), new Vector2(180, 385), "default", rowColor(ROW_GRAVITY));
            renderer.drawText(formatFloat(gravitySlider.getValue()), new Vector2(630, 385), "default", rowColor(ROW_GRAVITY));
        }
        if (speedSlider != null) {
            speedSlider.render(renderer, selectedRow == ROW_SPEED);
            renderer.drawText(label("Player Speed", ROW_SPEED), new Vector2(180, 325), "default", rowColor(ROW_SPEED));
            renderer.drawText(formatInt(speedSlider.getValue()), new Vector2(630, 325), "default", rowColor(ROW_SPEED));
        }
        if (collisionsToggle != null) {
            collisionsToggle.render(renderer, selectedRow == ROW_COLLISIONS);
            renderer.drawText(label("Collisions", ROW_COLLISIONS), new Vector2(180, 265), "default", rowColor(ROW_COLLISIONS));
        }

        renderer.drawText(label("Entity Preset", ROW_PRESET), new Vector2(180, 205), "default", rowColor(ROW_PRESET));
        renderer.drawText(String.valueOf(ENTITY_PRESETS[presetIndex]), new Vector2(630, 205), "default", rowColor(ROW_PRESET));

        renderer.drawText("UP/DOWN select | LEFT/RIGHT adjust | SPACE/ENTER toggle", new Vector2(20, 48), "default", Color.LIGHT_GRAY);
        renderer.drawText("R reset defaults | ESC save + return", new Vector2(20, 24), "default", Color.LIGHT_GRAY);
        renderer.end();
    }

    @Override
    public void handleInput() {
        Keyboard keyboard = context.getInputManager().getKeyboard();
        if (keyboard == null) {
            return;
        }

        if (keyboard.isKeyPressed(Input.Keys.ESCAPE)) {
            context.getSceneManager().pop();
            return;
        }

        if (keyboard.isKeyPressed(Input.Keys.UP)) {
            selectedRow = (selectedRow - 1 + ROW_COUNT) % ROW_COUNT;
        } else if (keyboard.isKeyPressed(Input.Keys.DOWN)) {
            selectedRow = (selectedRow + 1) % ROW_COUNT;
        }

        if (keyboard.isKeyPressed(Input.Keys.LEFT)) {
            adjustSelected(-1);
        } else if (keyboard.isKeyPressed(Input.Keys.RIGHT)) {
            adjustSelected(1);
        }

        if (keyboard.isKeyPressed(Input.Keys.ENTER) || keyboard.isKeyPressed(Input.Keys.SPACE)) {
            activateSelected();
        }

        if (keyboard.isKeyPressed(Input.Keys.R)) {
            resetDefaults();
        }
    }

    private void saveSettings() {
        if (fullscreenToggle == null || volumeSlider == null || frictionSlider == null
            || gravitySlider == null || speedSlider == null || collisionsToggle == null) {
            return;
        }

        ConfigManager config = context.getConfigManager();
        boolean fullscreen = fullscreenToggle.isEnabled();
        float volume = volumeSlider.getValue() / 100f;
        float friction = frictionSlider.getValue();
        float gravityY = gravitySlider.getValue();
        float speed = speedSlider.getValue();
        boolean collisions = collisionsToggle.isEnabled();
        config.set("display.fullscreen", new ConfigVar(fullscreen, false));
        config.set("audio.volume", new ConfigVar(volume, 0.7f));
        config.set("simulation.friction", new ConfigVar(friction, 0.10f));
        config.set("simulation.gravityY", new ConfigVar(gravityY, 0f));
        config.set("simulation.playerSpeed", new ConfigVar(speed, 240f));
        config.set("simulation.collisionsEnabled", new ConfigVar(collisions, true));
        config.set("simulation.presetIndex", new ConfigVar(Integer.valueOf(presetIndex), Integer.valueOf(0)));
        config.save(null);

        Display display = context.getOutputManager().getDisplay();
        boolean targetFullscreen = fullscreen;
        if (display != null && targetFullscreen != display.isFullscreen()) {
            display.toggleFullscreen();
        }
    }

    private void loadSettings() {
        if (volumeSlider == null || fullscreenToggle == null || frictionSlider == null
            || gravitySlider == null || speedSlider == null || collisionsToggle == null) {
            return;
        }

        ConfigManager config = context.getConfigManager();
        ensureConfig(config, "audio.volume", Float.valueOf(0.7f));
        ensureConfig(config, "display.fullscreen", Boolean.FALSE);
        ensureConfig(config, "simulation.friction", Float.valueOf(0.10f));
        ensureConfig(config, "simulation.gravityY", Float.valueOf(0f));
        ensureConfig(config, "simulation.playerSpeed", Float.valueOf(240f));
        ensureConfig(config, "simulation.collisionsEnabled", Boolean.TRUE);
        ensureConfig(config, "simulation.presetIndex", Integer.valueOf(0));

        ConfigVar volumeSetting = config.get("audio.volume");
        ConfigVar fullscreenSetting = config.get("display.fullscreen");
        ConfigVar frictionSetting = config.get("simulation.friction");
        ConfigVar gravitySetting = config.get("simulation.gravityY");
        ConfigVar speedSetting = config.get("simulation.playerSpeed");
        ConfigVar collisionSetting = config.get("simulation.collisionsEnabled");
        ConfigVar presetSetting = config.get("simulation.presetIndex");

        float currentVolume = volumeSetting == null ? 0.7f : volumeSetting.asFloat();
        boolean isFullscreen = fullscreenSetting != null && fullscreenSetting.asBool();
        float friction = frictionSetting == null ? 0.10f : frictionSetting.asFloat();
        float gravityY = gravitySetting == null ? 0f : gravitySetting.asFloat();
        float speed = speedSetting == null ? 240f : speedSetting.asFloat();
        boolean collisions = collisionSetting == null || collisionSetting.asBool();
        int loadedPreset = presetSetting == null ? 0 : presetSetting.asInt();

        volumeSlider.setValue(currentVolume * 100f);
        fullscreenToggle.setValue(isFullscreen);
        frictionSlider.setValue(friction);
        gravitySlider.setValue(gravityY);
        speedSlider.setValue(speed);
        collisionsToggle.setValue(collisions);
        presetIndex = clampPresetIndex(loadedPreset);
    }

    private void adjustSelected(int direction) {
        if (direction == 0) {
            return;
        }

        switch (selectedRow) {
            case ROW_VOLUME:
                volumeSlider.setValue(volumeSlider.getValue() + direction * 5f);
                break;
            case ROW_FRICTION:
                frictionSlider.setValue(frictionSlider.getValue() + direction * 0.02f);
                break;
            case ROW_GRAVITY:
                gravitySlider.setValue(gravitySlider.getValue() + direction * 0.5f);
                break;
            case ROW_SPEED:
                speedSlider.setValue(speedSlider.getValue() + direction * 10f);
                break;
            case ROW_PRESET:
                presetIndex = clampPresetIndex(presetIndex + direction);
                break;
            case ROW_FULLSCREEN:
            case ROW_COLLISIONS:
                activateSelected();
                break;
            default:
                break;
        }
    }

    private void activateSelected() {
        switch (selectedRow) {
            case ROW_FULLSCREEN:
                fullscreenToggle.toggle();
                break;
            case ROW_COLLISIONS:
                collisionsToggle.toggle();
                break;
            case ROW_PRESET:
                presetIndex = (presetIndex + 1) % ENTITY_PRESETS.length;
                break;
            default:
                break;
        }
    }

    private void resetDefaults() {
        volumeSlider.setValue(70f);
        fullscreenToggle.setValue(false);
        frictionSlider.setValue(0.10f);
        gravitySlider.setValue(0f);
        speedSlider.setValue(240f);
        collisionsToggle.setValue(true);
        presetIndex = 0;
    }

    private void ensureConfig(ConfigManager config, String key, Object value) {
        if (config.get(key) == null) {
            config.set(key, new ConfigVar(value, value));
        }
    }

    private int clampPresetIndex(int index) {
        if (index < 0) {
            return 0;
        }
        if (index >= ENTITY_PRESETS.length) {
            return ENTITY_PRESETS.length - 1;
        }
        return index;
    }

    private Color rowColor(int row) {
        return selectedRow == row ? Color.YELLOW : Color.WHITE;
    }

    private String label(String text, int row) {
        return (selectedRow == row ? "> " : "  ") + text;
    }

    private String formatPercent(float value) {
        return formatInt(value) + "%";
    }

    private String formatFloat(float value) {
        return String.format("%.2f", value);
    }

    private String formatInt(float value) {
        return String.valueOf(Math.round(value));
    }
}
