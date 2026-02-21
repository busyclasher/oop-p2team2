package sg.edu.sit.inf1009.p2team2.engine.output;

import java.util.HashMap;
import java.util.Map;

/**
 * AUDIO - Abstract Engine
 * Manages sound effect and music resources with generic engine wrappers.
 */
public class Audio {

    private final Map<String, SoundBuffer> soundLibrary;
    private final Map<String, MusicTrack> musicLibrary;
    private float masterVolume;
    private float sfxVolume;
    private float musicVolume;
    private MusicTrack currentMusic;

    public Audio() {
        this.soundLibrary = new HashMap<>();
        this.musicLibrary = new HashMap<>();
        this.masterVolume = 1.0f;
        this.sfxVolume = 1.0f;
        this.musicVolume = 0.7f;
        this.currentMusic = null;
    }

    public void loadSound(String filePath, String name) {
        if (name == null || name.isBlank() || filePath == null || filePath.isBlank()) {
            return;
        }
        soundLibrary.put(name, new SoundBuffer(filePath));
    }

    public void loadMusic(String filePath, String name) {
        if (name == null || name.isBlank() || filePath == null || filePath.isBlank()) {
            return;
        }
        musicLibrary.put(name, new MusicTrack(filePath));
    }

    public void playSound(String name) {
        playSound(name, 1.0f);
    }

    public void playSound(String name, float volume) {
        playSound(name, volume, false);
    }

    public void playSound(String name, float volume, boolean loop) {
        SoundBuffer sound = soundLibrary.get(name);
        if (sound == null) {
            return;
        }
        float effectiveVolume = clamp01(masterVolume * sfxVolume * volume);
        if (loop) {
            sound.loop(effectiveVolume);
        } else {
            sound.play(effectiveVolume);
        }
    }

    public void playMusic(String name, boolean loop) {
        // Stop whatever is currently playing
        if (currentMusic != null) {
            currentMusic.stop();
        }

        MusicTrack music = musicLibrary.get(name);
        if (music == null) {
            return;
        }

        currentMusic = music;
        float effectiveVolume = clamp01(masterVolume * musicVolume);
        currentMusic.play(loop, effectiveVolume);
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null;
        }
    }

    public void pauseMusic() {
        if (currentMusic != null) {
            currentMusic.pause();
        }
    }

    public void resumeMusic() {
        if (currentMusic != null) {
            currentMusic.resume();
        }
    }

    public void setMasterVolume(float volume) {
        this.masterVolume = clamp01(volume);
        if (currentMusic != null) {
            currentMusic.setVolume(clamp01(this.masterVolume * this.musicVolume)); 
        }
    }

    public void setSfxVolume(float volume) {
        this.sfxVolume = clamp01(volume);
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = clamp01(volume);
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
        for (SoundBuffer sound : soundLibrary.values()) {
            if (sound != null) {
                sound.dispose();
            }
        }
        soundLibrary.clear();

        for (MusicTrack music : musicLibrary.values()) {
            if (music != null) {
                music.dispose();
            }
        }
        musicLibrary.clear();
        currentMusic = null;
    }

    private float clamp01(float value) {
        return Math.max(0f, Math.min(1f, value));
    }
}
