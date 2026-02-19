package sg.edu.sit.inf1009.p2team2.engine.scenes;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import sg.edu.sit.inf1009.p2team2.engine.config.ConfigKey;
import sg.edu.sit.inf1009.p2team2.engine.config.ConfigKeys;
import sg.edu.sit.inf1009.p2team2.engine.config.ConfigManager;
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

    private static final String BACKGROUND_SPRITE = "setting.png";

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

        float centerX = 400f;
        float centerY = 300f;
        var display = context.getOutputManager().getDisplay();
        if (display != null) {
            centerX = display.getWidth() / 2f;
            centerY = display.getHeight() / 2f;
        }

        float rowSpacing = 60f;
        float totalHeight = (ROW_COUNT - 1) * rowSpacing;
        float topY = centerY + totalHeight / 2f;
        float labelX = centerX - 220f;
        float sliderX = centerX + 40f;
        float valueX = centerX + 230f;

        updateControlPositions(sliderX, topY, rowSpacing);

        renderer.clear();
        renderer.begin();
        
        // Draw background centered and scaled to the window
        renderer.drawBackground(BACKGROUND_SPRITE);
        
        // Draw semi-transparent overlay box around the settings controls
        float overlayPadding = 30f;
        float overlayLeft = labelX - overlayPadding;
        float overlayRight = valueX + overlayPadding;
        float overlayTop = topY + 40f;
        float overlayBottom = topY - (ROW_PRESET * rowSpacing) - overlayPadding;
        
        com.badlogic.gdx.math.Rectangle overlayBox = new com.badlogic.gdx.math.Rectangle(
            overlayLeft, overlayBottom, 
            overlayRight - overlayLeft, 
            overlayTop - overlayBottom
        );
        Color overlayColor = new Color(0.3f, 0.3f, 0.3f, 0.6f); // Grey with 60% opacity
        renderer.drawRect(overlayBox, overlayColor, true);
        // Draw white outline
        renderer.drawRect(overlayBox, Color.WHITE, false);
        
        // Left-align settings within the overlay box
        float boxLabelX = overlayLeft + 20f;
        float boxSliderX = boxLabelX + 140f;
        float boxValueX = overlayRight - 80f;
        
        updateControlPositions(boxSliderX, topY, rowSpacing);
        
        float titleX = overlayLeft + 120f;
        float titleY = topY + 70f;
        renderer.drawText("SETTINGS", new Vector2(titleX, titleY), "default", Color.WHITE);

        if (volumeSlider != null) {
            volumeSlider.render(renderer, selectedRow == ROW_VOLUME);
            float rowY = topY - (ROW_VOLUME * rowSpacing);
            renderer.drawText(label("Volume", ROW_VOLUME), new Vector2(boxLabelX, rowY + 5f), "default", rowColor(ROW_VOLUME));
            renderer.drawText(formatPercent(volumeSlider.getValue()), new Vector2(boxValueX, rowY + 5f), "default", rowColor(ROW_VOLUME));
        }
        if (fullscreenToggle != null) {
            fullscreenToggle.render(renderer, selectedRow == ROW_FULLSCREEN);
            float rowY = topY - (ROW_FULLSCREEN * rowSpacing);
            renderer.drawText(label("Fullscreen", ROW_FULLSCREEN), new Vector2(boxLabelX, rowY + 5f), "default", rowColor(ROW_FULLSCREEN));
        }
        if (frictionSlider != null) {
            frictionSlider.render(renderer, selectedRow == ROW_FRICTION);
            float rowY = topY - (ROW_FRICTION * rowSpacing);
            renderer.drawText(label("Friction", ROW_FRICTION), new Vector2(boxLabelX, rowY + 5f), "default", rowColor(ROW_FRICTION));
            renderer.drawText(formatFloat(frictionSlider.getValue()), new Vector2(boxValueX, rowY + 5f), "default", rowColor(ROW_FRICTION));
        }
        if (gravitySlider != null) {
            gravitySlider.render(renderer, selectedRow == ROW_GRAVITY);
            float rowY = topY - (ROW_GRAVITY * rowSpacing);
            renderer.drawText(label("Gravity Y", ROW_GRAVITY), new Vector2(boxLabelX, rowY + 5f), "default", rowColor(ROW_GRAVITY));
            renderer.drawText(formatFloat(gravitySlider.getValue()), new Vector2(boxValueX, rowY + 5f), "default", rowColor(ROW_GRAVITY));
        }
        if (speedSlider != null) {
            speedSlider.render(renderer, selectedRow == ROW_SPEED);
            float rowY = topY - (ROW_SPEED * rowSpacing);
            renderer.drawText(label("Player Speed", ROW_SPEED), new Vector2(boxLabelX, rowY + 5f), "default", rowColor(ROW_SPEED));
            renderer.drawText(formatInt(speedSlider.getValue()), new Vector2(boxValueX, rowY + 5f), "default", rowColor(ROW_SPEED));
        }
        if (collisionsToggle != null) {
            collisionsToggle.render(renderer, selectedRow == ROW_COLLISIONS);
            float rowY = topY - (ROW_COLLISIONS * rowSpacing);
            renderer.drawText(label("Collisions", ROW_COLLISIONS), new Vector2(boxLabelX, rowY + 5f), "default", rowColor(ROW_COLLISIONS));
        }

        float presetY = topY - (ROW_PRESET * rowSpacing);
        renderer.drawText(label("Entity Preset", ROW_PRESET), new Vector2(boxLabelX, presetY + 5f), "default", rowColor(ROW_PRESET));
        renderer.drawText(String.valueOf(ENTITY_PRESETS[presetIndex]), new Vector2(boxValueX, presetY + 5f), "default", rowColor(ROW_PRESET));

        float hintY = (topY - totalHeight) - 40f;
        renderer.drawText("UP/DOWN select | LEFT/RIGHT adjust | SPACE/ENTER toggle", new Vector2(centerX - 280f, hintY), "default", Color.LIGHT_GRAY);
        renderer.drawText("R reset defaults | ESC save + return", new Vector2(centerX - 280f, hintY - 24f), "default", Color.LIGHT_GRAY);
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
        config.set(ConfigKeys.DISPLAY_FULLSCREEN, Boolean.valueOf(fullscreen));
        config.set(ConfigKeys.AUDIO_VOLUME, Float.valueOf(volume));
        config.set(SimulationConfigKeys.SIMULATION_FRICTION, Float.valueOf(friction));
        config.set(SimulationConfigKeys.SIMULATION_GRAVITY_Y, Float.valueOf(gravityY));
        config.set(SimulationConfigKeys.SIMULATION_PLAYER_SPEED, Float.valueOf(speed));
        config.set(SimulationConfigKeys.SIMULATION_COLLISIONS_ENABLED, Boolean.valueOf(collisions));
        config.set(SimulationConfigKeys.SIMULATION_PRESET_INDEX, Integer.valueOf(presetIndex));
        config.save(null);

        Display display = context.getOutputManager().getDisplay();
        boolean targetFullscreen = fullscreen;
        if (display != null && targetFullscreen != display.isFullscreen()) {
            display.toggleFullscreen();
        }
    }

    private void updateControlPositions(float sliderX, float topY, float rowSpacing) {
        if (volumeSlider != null) {
            volumeSlider.setPosition(sliderX, topY - (ROW_VOLUME * rowSpacing));
        }
        if (fullscreenToggle != null) {
            fullscreenToggle.setPosition(sliderX, topY - (ROW_FULLSCREEN * rowSpacing));
        }
        if (frictionSlider != null) {
            frictionSlider.setPosition(sliderX, topY - (ROW_FRICTION * rowSpacing));
        }
        if (gravitySlider != null) {
            gravitySlider.setPosition(sliderX, topY - (ROW_GRAVITY * rowSpacing));
        }
        if (speedSlider != null) {
            speedSlider.setPosition(sliderX, topY - (ROW_SPEED * rowSpacing));
        }
        if (collisionsToggle != null) {
            collisionsToggle.setPosition(sliderX, topY - (ROW_COLLISIONS * rowSpacing));
        }
    }

    private void loadSettings() {
        if (volumeSlider == null || fullscreenToggle == null || frictionSlider == null
            || gravitySlider == null || speedSlider == null || collisionsToggle == null) {
            return;
        }

        ConfigManager config = context.getConfigManager();
        ensureConfig(config, ConfigKeys.AUDIO_VOLUME);
        ensureConfig(config, ConfigKeys.DISPLAY_FULLSCREEN);
        ensureConfig(config, SimulationConfigKeys.SIMULATION_FRICTION);
        ensureConfig(config, SimulationConfigKeys.SIMULATION_GRAVITY_Y);
        ensureConfig(config, SimulationConfigKeys.SIMULATION_PLAYER_SPEED);
        ensureConfig(config, SimulationConfigKeys.SIMULATION_COLLISIONS_ENABLED);
        ensureConfig(config, SimulationConfigKeys.SIMULATION_PRESET_INDEX);

        float currentVolume = config.get(ConfigKeys.AUDIO_VOLUME);
        Display display = context.getOutputManager() == null ? null : context.getOutputManager().getDisplay();
        boolean isFullscreen = display != null ? display.isFullscreen() : config.get(ConfigKeys.DISPLAY_FULLSCREEN);
        float friction = config.get(SimulationConfigKeys.SIMULATION_FRICTION);
        float gravityY = config.get(SimulationConfigKeys.SIMULATION_GRAVITY_Y);
        float speed = config.get(SimulationConfigKeys.SIMULATION_PLAYER_SPEED);
        boolean collisions = config.get(SimulationConfigKeys.SIMULATION_COLLISIONS_ENABLED);
        int loadedPreset = config.get(SimulationConfigKeys.SIMULATION_PRESET_INDEX);

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

    private <T> void ensureConfig(ConfigManager config, ConfigKey<T> key) {
        if (!config.has(key)) {
            config.set(key, key.defaultValue());
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
