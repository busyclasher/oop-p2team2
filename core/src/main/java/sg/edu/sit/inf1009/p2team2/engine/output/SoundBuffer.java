package sg.edu.sit.inf1009.p2team2.engine.output;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class SoundBuffer {
    private final String filePath;
    private Sound sound;

    public SoundBuffer(String filePath) {
        this.filePath = filePath;
        try {
            this.sound = Gdx.audio.newSound(Gdx.files.internal(filePath));
        } catch (Exception e) {
            Gdx.app.error("SoundBuffer", "Failed to load: " + filePath, e);
        }
    }

    public void play(float volume) {
        if (sound == null) return;
        sound.play(volume);
    }

    public void loop(float volume) {
        if (sound == null) return;
        sound.loop(volume);
    }

    public String getFilePath() {
        return filePath;
    }

    public void dispose() {
        if (sound != null) {
            sound.dispose();
            sound = null;
        }
    }
}