package sg.edu.sit.inf1009.p2team2.engine.output;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class MusicTrack {
    private final String filePath;
    private Music music;

    public MusicTrack(String filePath) {
        this.filePath = filePath;
        try {
            this.music = Gdx.audio.newMusic(Gdx.files.internal(filePath));
        } catch (Exception e) {
            Gdx.app.error("MusicTrack", "Failed to load: " + filePath, e);
        }
    }

    public void play(boolean loop, float volume) {
        if (music == null) return;
        music.setLooping(loop);
        music.setVolume(volume);
        music.play();
    }

    public void stop() {
        if (music == null) return;
        music.stop();
    }

    public void pause() {
        if (music == null) return;
        music.pause();
    }

    public void resume() {
        if (music == null) return;
        music.play();
    }

    public void setVolume(float volume) {
        if (music == null) return;
        music.setVolume(volume);
    }

    public boolean isPlaying() {
        return music != null && music.isPlaying();
    }

    public String getFilePath() {
        return filePath;
    }

    public void dispose() {
        if (music != null) {
            music.dispose();
            music = null;
        }
    }
}