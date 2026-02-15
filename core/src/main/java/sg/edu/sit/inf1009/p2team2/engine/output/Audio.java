package sg.edu.sit.inf1009.p2team2.engine.output;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;
import java.util.Map;

/**
 * AUDIO - Abstract Engine
 * Manages sound effects and music playback.
 * 
 * Provides a simple interface for loading and playing audio.
 */
public class Audio {
    
    // Sound effects library (short sounds)
    private Map<String, Sound> soundLibrary;
    
    // Music library (long streaming audio)
    private Map<String, Music> musicLibrary;
    
    // Volume controls
    private float masterVolume;
    private float sfxVolume;
    private float musicVolume;
    
    // Currently playing music
    private Music currentMusic;
    
    /**
     * Constructor
     */
    public Audio() {
        this.soundLibrary = new HashMap<>();
        this.musicLibrary = new HashMap<>();
        this.masterVolume = 1.0f;
        this.sfxVolume = 1.0f;
        this.musicVolume = 0.7f;
        this.currentMusic = null;
    }
    
    // ===== LOADING =====
    
    /**
     * Load a sound effect
     * 
     * @param filePath Path to sound file (e.g., "sounds/explosion.ogg")
     * @param name Identifier to reference this sound (e.g., "explosion")
     */
    public void loadSound(String filePath, String name) {
        try {
            Sound sound = Gdx.audio.newSound(Gdx.files.internal(filePath));
            soundLibrary.put(name, sound);
            Gdx.app.log("Audio", "Loaded sound: " + name + " from " + filePath);
        } catch (Exception e) {
            Gdx.app.error("Audio", "Failed to load sound: " + filePath, e);
        }
    }
    
    /**
     * Load a music track
     * 
     * @param filePath Path to music file (e.g., "music/theme.ogg")
     * @param name Identifier to reference this music (e.g., "main_theme")
     */
    public void loadMusic(String filePath, String name) {
        try {
            Music music = Gdx.audio.newMusic(Gdx.files.internal(filePath));
            musicLibrary.put(name, music);
            Gdx.app.log("Audio", "Loaded music: " + name + " from " + filePath);
        } catch (Exception e) {
            Gdx.app.error("Audio", "Failed to load music: " + filePath, e);
        }
    }
    
    // ===== SOUND EFFECTS =====
    
    /**
     * Play a sound effect at default volume
     * 
     * @param name Sound identifier
     */
    public void playSound(String name) {
        playSound(name, 1.0f);
    }
    
    /**
     * Play a sound effect with custom volume
     * 
     * @param name Sound identifier
     * @param volume Volume multiplier (0.0 to 1.0)
     */
    public void playSound(String name, float volume) {
        playSound(name, volume, false);
    }
    
    /**
     * Play a sound effect with custom volume and looping
     * 
     * @param name Sound identifier
     * @param volume Volume multiplier (0.0 to 1.0)
     * @param loop true to loop, false to play once
     */
    public void playSound(String name, float volume, boolean loop) {
        Sound sound = soundLibrary.get(name);
        
        if (sound == null) {
            Gdx.app.error("Audio", "Sound not found: " + name);
            return;
        }
        
        float finalVolume = masterVolume * sfxVolume * volume;
        
        if (loop) {
            sound.loop(finalVolume);
        } else {
            sound.play(finalVolume);
        }
    }
    
    // ===== MUSIC =====
    
    /**
     * Play a music track
     * 
     * @param name Music identifier
     * @param loop true to loop, false to play once
     */
    public void playMusic(String name, boolean loop) {
        // Stop current music if playing
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.stop();
        }
        
        Music music = musicLibrary.get(name);
        
        if (music == null) {
            Gdx.app.error("Audio", "Music not found: " + name);
            return;
        }
        
        currentMusic = music;
        currentMusic.setLooping(loop);
        currentMusic.setVolume(masterVolume * musicVolume);
        currentMusic.play();
    }
    
    /**
     * Stop currently playing music
     */
    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }
    
    /**
     * Pause currently playing music
     */
    public void pauseMusic() {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.pause();
        }
    }
    
    /**
     * Resume paused music
     */
    public void resumeMusic() {
        if (currentMusic != null && !currentMusic.isPlaying()) {
            currentMusic.play();
        }
    }
    
    // ===== VOLUME CONTROL =====
    
    /**
     * Set master volume (affects all audio)
     * 
     * @param volume Volume (0.0 to 1.0)
     */
    public void setMasterVolume(float volume) {
        this.masterVolume = Math.max(0, Math.min(1, volume));
        
        // Update current music volume
        if (currentMusic != null) {
            currentMusic.setVolume(masterVolume * musicVolume);
        }
    }
    
    /**
     * Set sound effects volume
     * 
     * @param volume Volume (0.0 to 1.0)
     */
    public void setSfxVolume(float volume) {
        this.sfxVolume = Math.max(0, Math.min(1, volume));
    }
    
    /**
     * Set music volume
     * 
     * @param volume Volume (0.0 to 1.0)
     */
    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0, Math.min(1, volume));
        
        // Update current music volume
        if (currentMusic != null) {
            currentMusic.setVolume(masterVolume * musicVolume);
        }
    }
    
    /**
     * Get master volume
     */
    public float getMasterVolume() {
        return masterVolume;
    }
    
    /**
     * Get SFX volume
     */
    public float getSfxVolume() {
        return sfxVolume;
    }
    
    /**
     * Get music volume
     */
    public float getMusicVolume() {
        return musicVolume;
    }
    
    /**
     * Clean up resources
     */
    public void dispose() {
        // Dispose all sounds
        for (Sound sound : soundLibrary.values()) {
            sound.dispose();
        }
        soundLibrary.clear();
        
        // Dispose all music
        for (Music music : musicLibrary.values()) {
            music.dispose();
        }
        musicLibrary.clear();
        
        currentMusic = null;
    }
}