package sg.edu.sit.inf1009.p2team2.engine.config;

import sg.edu.sit.inf1009.p2team2.engine.managers.OutputManager;
import sg.edu.sit.inf1009.p2team2.engine.output.Audio;

public class AudioConfigListener implements ConfigListener {
    private final ConfigManager configManager;
    private final OutputManager outputManager;

    public AudioConfigListener(ConfigManager configManager, OutputManager outputManager) {
        this.configManager = configManager;
        this.outputManager = outputManager;
    }

    @Override
    public void onConfigChanged(String key, ConfigVar value) {
        if (!ConfigKeys.AUDIO_VOLUME.name().equals(key) || value == null || outputManager == null) {
            return;
        }

        Audio audio = outputManager.getAudio();
        if (audio != null) {
            audio.setMasterVolume(value.asFloat());
        }
    }

    public void applyInitial() {
        if (configManager == null) {
            return;
        }
        onConfigChanged(ConfigKeys.AUDIO_VOLUME.name(), configManager.get(ConfigKeys.AUDIO_VOLUME.name()));
    }
}
