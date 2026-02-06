package sg.edu.sit.inf1009.p2team2.engine.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.ui.Slider;
import sg.edu.sit.inf1009.p2team2.engine.ui.Toggle;
import sg.edu.sit.inf1009.p2team2.engine.output.Audio;
import sg.edu.sit.inf1009.p2team2.engine.output.Display;

public class SettingsScene extends Scene {
    private Slider volumeSlider;
    private Toggle fullscreenToggle;

    public SettingsScene(EngineContext context) {
        super(context);
    }

    @Override
    public void onEnter() {
        // TODO(Ivan): load current settings into UI controls.
    // Coordination: Load current settings from ConfigManager into UI controls
        var config = context.getConfigManager();
        
        float currentVolume = config.getFloat("audio.volume");
        boolean isFullscreen = config.getBool("display.fullscreen");
        
        volumeSlider.setValue((int)(currentVolume * 100));
        fullscreenToggle.setValue(isFullscreen);
        
        System.out.println("[SettingsScene] Settings loaded into UI.");
    }

    @Override
    public void onExit() {
        // TODO(Ivan): persist settings if needed.
        saveSettings();
    }

    @Override
    public void load() {
        // TODO(Ivan): initialize UI controls and resources.
        // Initialize UI controls with their screen positions
        System.out.println("[SettingsScene] Loading settings resources...");
        volumeSlider = new Slider(new Vector2(400, 200));
        fullscreenToggle = new Toggle(400, 300);
    }

    @Override
    public void unload() {
        // TODO(Ivan): dispose/unload UI resources.
        // Cleanup UI resources to free memory
        volumeSlider = null;
        fullscreenToggle = null;
        System.out.println("[SettingsScene] Settings resources unloaded.");
    }

    @Override
    public void update() {
        // TODO(Ivan): update controls and apply preview changes.
        // Update UI logic and apply audio changes in real-time
        volumeSlider.update();
        fullscreenToggle.update();
        
        // Coordination: Directly update the Audio system for immediate feedback
        float volume = volumeSlider.getValue() / 100.0f;
        var audio = context.getOutputManager().getAudio();
        if (audio != null) {
            ((Audio) audio).setMasterVolume(volume);
        }
    }

    @Override
    public void render() {
        // TODO(Ivan): render settings controls.
        // Render the settings UI using the Renderer
        var renderer = context.getOutputManager().getRenderer();
        
        renderer.drawText("SETTINGS", new Vector2(350, 100), "title_font", Color.WHITE);
        
        renderer.drawText("Volume", new Vector2(200, 200), "label_font", Color.WHITE);
        volumeSlider.render(renderer, true); // true if selected
        
        renderer.drawText("Fullscreen", new Vector2(200, 300), "label_font", Color.WHITE);
        fullscreenToggle.render(renderer, false);
    }

    private void saveSettings() {
        // TODO(Ivan): write settings to ConfigManager.
       var config = context.getConfigManager();
    
        // Save the preference to your config
        config.setValue("display.fullscreen", fullscreenToggle.isEnabled());
        config.saveConfig();

        // Coordinate with OutputManager to apply the change immediately
        Display display = (Display) context.getOutputManager().getDisplay();
        if ((Boolean) fullscreenToggle.isEnabled() != display.isFullscreen()) {
            display.toggleFullscreen();
        }
    }
}

