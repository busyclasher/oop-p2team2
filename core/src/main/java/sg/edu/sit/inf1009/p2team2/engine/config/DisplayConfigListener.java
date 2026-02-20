package sg.edu.sit.inf1009.p2team2.engine.config;

import sg.edu.sit.inf1009.p2team2.engine.managers.OutputManager;
import sg.edu.sit.inf1009.p2team2.engine.output.Display;

public class DisplayConfigListener implements ConfigListener {
    private final ConfigManager configManager;
    private final OutputManager outputManager;

    public DisplayConfigListener(ConfigManager configManager, OutputManager outputManager) {
        this.configManager = configManager;
        this.outputManager = outputManager;
    }

    @Override
    public void onConfigChanged(String key, ConfigVar<?> value) {
        if (key == null || value == null || outputManager == null) {
            return;
        }

        Display display = outputManager.getDisplay();
        if (display == null) {
            return;
        }

        if (ConfigKeys.DISPLAY_FULLSCREEN.name().equals(key)) {
            boolean target = ConfigKeys.DISPLAY_FULLSCREEN.read(value);
            if (display.isFullscreen() != target) {
                display.toggleFullscreen();
            }
            return;
        }

        if (ConfigKeys.DISPLAY_TITLE.name().equals(key)) {
            display.setTitle(ConfigKeys.DISPLAY_TITLE.read(value));
            return;
        }

        if (ConfigKeys.DISPLAY_WIDTH.name().equals(key) || ConfigKeys.DISPLAY_HEIGHT.name().equals(key)) {
            applySizeFromConfig();
        }
    }

    public void applyInitial() {
        if (configManager == null) {
            return;
        }
        Display display = outputManager == null ? null : outputManager.getDisplay();
        if (display == null) {
            return;
        }
        display.setTitle(configManager.get(ConfigKeys.DISPLAY_TITLE));
        boolean targetFullscreen = configManager.get(ConfigKeys.DISPLAY_FULLSCREEN);
        if (display.isFullscreen() != targetFullscreen) {
            display.toggleFullscreen();
        }
        applySizeFromConfig();
    }

    private void applySizeFromConfig() {
        if (configManager == null || outputManager == null || outputManager.getDisplay() == null) {
            return;
        }

        int width = configManager.get(ConfigKeys.DISPLAY_WIDTH);
        int height = configManager.get(ConfigKeys.DISPLAY_HEIGHT);
        if (width > 0 && height > 0) {
            outputManager.getDisplay().resize(width, height);
        }
    }
}
