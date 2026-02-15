package sg.edu.sit.inf1009.p2team2.engine.output;

/**
 * Lightweight wrapper for a sound effect resource.
 *
 * Implementation detail (libGDX Sound, caching, etc.) is left as TODO to keep the
 * engine skeleton non-contextual.
 */
public class SoundBuffer {
    private final String filePath;

    public SoundBuffer(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void dispose() {
        // TODO(HongYih): release underlying sound resources.
    }
}

