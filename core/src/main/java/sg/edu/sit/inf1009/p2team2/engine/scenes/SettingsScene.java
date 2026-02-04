package sg.edu.sit.inf1009.p2team2.engine.scenes;

import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.ui.Slider;
import sg.edu.sit.inf1009.p2team2.engine.ui.Toggle;

public class SettingsScene extends Scene {
    private Slider volumeSlider;
    private Toggle fullscreenToggle;
    private boolean isMuted;

    public SettingsScene(EngineContext context) {
        super(context);
    }

    @Override
    public void onEnter() {
        // TODO(Ivan): load current settings into UI controls.
    }

    @Override
    public void onExit() {
        // TODO(Ivan): persist settings if needed.
    }

    @Override
    public void load() {
        // TODO(Ivan): initialize UI controls and resources.
    }

    @Override
    public void unload() {
        // TODO(Ivan): dispose/unload UI resources.
    }

    @Override
    public void update() {
        // TODO(Ivan): update controls and apply preview changes.
    }

    @Override
    public void render() {
        // TODO(Ivan): render settings controls.
    }

    private void saveSettings() {
        // TODO(Ivan): write settings to ConfigManager.
    }
}

