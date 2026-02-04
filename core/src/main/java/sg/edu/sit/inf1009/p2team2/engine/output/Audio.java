package sg.edu.sit.inf1009.p2team2.engine.output;

import java.util.HashMap;
import java.util.Map;

/**
 * Audio façade for sound effects and music.
 *
 * This skeleton provides the API surface from the UML; the underlying libGDX
 * audio calls should be implemented by the OutputManager owner.
 */
public class Audio {
    private final Map<String, SoundBuffer> soundLibrary = new HashMap<>();
    private final Map<String, MusicTrack> musicLibrary = new HashMap<>();

    private float masterVolume = 1f;
    private float sfxVolume = 1f;
    private float musicVolume = 1f;

    private MusicTrack currentMusic;

    public Audio() {
    }

    public void loadSound(String filePath, String name) {
        // TODO(HongYih): load/caches a SoundBuffer into soundLibrary.
    }

    public void loadMusic(String filePath, String name) {
        // TODO(HongYih): load/caches a MusicTrack into musicLibrary.
    }

    public void playSound(String name) {
        // TODO(HongYih): play a cached sound using master/sfx volume.
    }

    public void playSound(String name, float volume) {
        // TODO(HongYih): play a cached sound at a specific volume.
    }

    public void playSound(String name, float volume, boolean loop) {
        // TODO(HongYih): play a cached sound with loop option.
    }

    public void playMusic(String name, boolean loop) {
        // TODO(HongYih): start music track, assign currentMusic, handle looping.
    }

    public void stopMusic() {
        // TODO(HongYih): stop current music playback.
    }

    public void pauseMusic() {
        // TODO(HongYih): pause current music playback.
    }

    public void resumeMusic() {
        // TODO(HongYih): resume current music playback.
    }

    public void setMasterVolume(float volume) {
        this.masterVolume = volume;
    }

    public void setSfxVolume(float volume) {
        this.sfxVolume = volume;
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = volume;
    }

    public float getMasterVolume() {
        return masterVolume;
    }

    public float getSfxVolume() {
        return sfxVolume;
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public void dispose() {
        // TODO(HongYih): dispose all loaded sound/music resources.
    }
}

