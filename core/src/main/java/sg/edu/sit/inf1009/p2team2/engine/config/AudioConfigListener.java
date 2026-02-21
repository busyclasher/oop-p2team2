package sg.edu.sit.inf1009.p2team2.engine.config;

import sg.edu.sit.inf1009.p2team2.engine.managers.OutputManager;
import sg.edu.sit.inf1009.p2team2.engine.output.Audio;

/**
 * Applies audio-related config updates to the runtime audio system.
 */
public class AudioConfigListener implements ConfigListener {
    private final ConfigManager configManager;
    private final OutputManager outputManager;

    public AudioConfigListener(ConfigManager configManager, OutputManager outputManager) {
        this.configManager = configManager;
        this.outputManager = outputManager;
    }

    @Override
    public void onConfigChanged(String key, ConfigVar<?> value) {
        if (!ConfigKeys.AUDIO_VOLUME.name().equals(key) || value == null || outputManager == null) {
            return;
        }

        Audio audio = outputManager.getAudio();
        if (audio != null) {
            audio.setMasterVolume(ConfigKeys.AUDIO_VOLUME.read(value));
        }
    }

    public void applyInitial() {
        if (configManager == null) {
            return;
        }
        Audio audio = outputManager == null ? null : outputManager.getAudio();
        if (audio != null) {
            audio.setMasterVolume(configManager.get(ConfigKeys.AUDIO_VOLUME));
        }
    }
}
