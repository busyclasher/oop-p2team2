package sg.edu.sit.inf1009.p2team2.engine.scenes;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.output.Audio;
import sg.edu.sit.inf1009.p2team2.engine.output.Display;
import sg.edu.sit.inf1009.p2team2.engine.ui.Slider;
import sg.edu.sit.inf1009.p2team2.engine.ui.Toggle;

public class SettingsScene extends Scene {
    private Slider volumeSlider;
    private Toggle fullscreenToggle;

    public SettingsScene(EngineContext context) {
        super(context);
    }

    @Override
    public void onEnter() {
        loadSettings();
    }

    @Override
    public void onExit() {
        saveSettings();
    }

    @Override
    public void load() {
        volumeSlider = new Slider(new Vector2(400, 200));
        fullscreenToggle = new Toggle(400, 300);
    }

    @Override
    public void unload() {
        volumeSlider = null;
        fullscreenToggle = null;
    }

    @Override
    public void update(float dt) {
        if (volumeSlider == null || fullscreenToggle == null) {
            return;
        }

        volumeSlider.update();
        fullscreenToggle.update();

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
        renderer.drawText("SETTINGS", new Vector2(350, 700), "default", Color.WHITE);
        renderer.drawText("Volume", new Vector2(200, 560), "default", Color.WHITE);
        renderer.drawText("Fullscreen", new Vector2(200, 460), "default", Color.WHITE);

        if (volumeSlider != null) {
            volumeSlider.render(renderer, true);
        }
        if (fullscreenToggle != null) {
            fullscreenToggle.render(renderer, false);
        }

        renderer.drawText("Press ESC to return", new Vector2(20, 30), "default", Color.LIGHT_GRAY);
        renderer.end();
    }

    @Override
    public void handleInput() {
        if (context.getInputManager().getKeyboard().isKeyPressed(Input.Keys.ESCAPE)) {
            context.getSceneManager().pop();
        }
    }

    private void saveSettings() {
        if (fullscreenToggle == null) {
            return;
        }

        var config = context.getConfigManager();
        config.setValue("display.fullscreen", fullscreenToggle.isEnabled());
        config.setValue("audio.volume", volumeSlider == null ? 0.7f : volumeSlider.getValue() / 100f);
        config.saveConfig();

        Display display = context.getOutputManager().getDisplay();
        boolean targetFullscreen = fullscreenToggle.isEnabled();
        if (display != null && targetFullscreen != display.isFullscreen()) {
            display.toggleFullscreen();
        }
    }

    private void loadSettings() {
        if (volumeSlider == null || fullscreenToggle == null) {
            return;
        }

        var config = context.getConfigManager();
        float currentVolume = config.getFloat("audio.volume");
        boolean isFullscreen = config.getBool("display.fullscreen");

        volumeSlider.setValue(currentVolume * 100f);
        fullscreenToggle.setValue(isFullscreen);
    }
}
