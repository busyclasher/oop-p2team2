package io.github.some_example_name.output;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import java.util.HashMap;
import java.util.Map;

public class Audio {
    private Map<String, Sound> soundLibrary;
    private Map<String, Music> musicLibrary;
    private float masterVolume;
    private float sfxVolume;
    private float musicVolume;
    private Music currentMusic;
    
    public Audio() {
        soundLibrary = new HashMap<>();
        musicLibrary = new HashMap<>();
        masterVolume = 1.0f;
        sfxVolume = 1.0f;
        musicVolume = 1.0f;
    }
    
    public void loadSound(String filePath, String name) {
        Sound sound = Gdx.audio.newSound(Gdx.files.internal(filePath));
        soundLibrary.put(name, sound);
    }
    
    public void loadMusic(String filePath, String name) {
        Music music = Gdx.audio.newMusic(Gdx.files.internal(filePath));
        musicLibrary.put(name, music);
    }
    
    public void playSound(String name) {
        playSound(name, 1.0f, false);
    }
    
    public void playSound(String name, float volume) {
        playSound(name, volume, false);
    }
    
    public void playSound(String name, float volume, boolean loop) {
        Sound sound = soundLibrary.get(name);
        if (sound != null) {
            float finalVolume = masterVolume * sfxVolume * volume;
            if (loop) {
                sound.loop(finalVolume);
            } else {
                sound.play(finalVolume);
            }
        }
    }
    
    public void playMusic(String name, boolean loop) {
        Music music = musicLibrary.get(name);
        if (music != null) {
            if (currentMusic != null) {
                currentMusic.stop();
            }
            currentMusic = music;
            currentMusic.setVolume(masterVolume * musicVolume);
            currentMusic.setLooping(loop);
            currentMusic.play();
        }
    }
    
    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }
    
    public void pauseMusic() {
        if (currentMusic != null) {
            currentMusic.pause();
        }
    }
    
    public void resumeMusic() {
        if (currentMusic != null) {
            currentMusic.play();
        }
    }
    
    public void setMasterVolume(float volume) {
        this.masterVolume = Math.max(0f, Math.min(1f, volume));
        if (currentMusic != null) {
            currentMusic.setVolume(masterVolume * musicVolume);
        }
    }
    
    public void setSfxVolume(float volume) {
        this.sfxVolume = Math.max(0f, Math.min(1f, volume));
    }
    
    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0f, Math.min(1f, volume));
        if (currentMusic != null) {
            currentMusic.setVolume(masterVolume * musicVolume);
        }
    }
    
    public void dispose() {
        for (Sound sound : soundLibrary.values()) {
            sound.dispose();
        }
        for (Music music : musicLibrary.values()) {
            music.dispose();
        }
    }
}
