package sg.edu.sit.inf1009.p2team2.engine.output;

/**
 * Lightweight wrapper for a music resource.
 *
 * The concrete implementation (libGDX Music, streaming, etc.) is left as TODO.
 */
public class MusicTrack {
    private final String filePath;

    public MusicTrack(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void dispose() {
        // TODO(HongYih): release underlying music resources.
    }
}

