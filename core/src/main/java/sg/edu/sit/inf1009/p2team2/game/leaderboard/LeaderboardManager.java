package sg.edu.sit.inf1009.p2team2.game.leaderboard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import sg.edu.sit.inf1009.p2team2.game.entities.CharacterType;

/**
 * Leaderboard with persistence via libGDX Preferences.
 * Scores are kept sorted highest-first and capped at maxEntries.
 */
public class LeaderboardManager {

    private static final int    DEFAULT_MAX_ENTRIES = 10;
    private static final String PREFS_NAME          = "silicon-sentinel-leaderboard";

    private static final int    MAX_NAME_LENGTH = 12;

    private final List<LeaderboardEntry> entries;
    private final int                    maxEntries;
    private CharacterType                lastCharacter;
    private String                       playerName;
    private boolean                      seenTutorial;

    public LeaderboardManager() {
        this(DEFAULT_MAX_ENTRIES);
    }

    public LeaderboardManager(int maxEntries) {
        this.maxEntries = maxEntries;
        this.entries    = new ArrayList<>();
        load();
        loadPlayerName();
        loadTutorialFlag();
    }

    /**
     * Adds an entry, keeps the list sorted and capped, then persists.
     */
    public void addEntry(String name, int score) {
        entries.add(new LeaderboardEntry(name, score));
        Collections.sort(entries);
        if (entries.size() > maxEntries) {
            entries.subList(maxEntries, entries.size()).clear();
        }
        save();
    }

    /** Returns an unmodifiable, sorted view of all entries. */
    public List<LeaderboardEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    /** True if the given score would make it onto the board. */
    public boolean isHighScore(int score) {
        if (entries.size() < maxEntries) return true;
        return score > entries.get(entries.size() - 1).getScore();
    }

    public int size() { return entries.size(); }

    public CharacterType getLastCharacter()               { return lastCharacter; }
    public void          setLastCharacter(CharacterType c){ this.lastCharacter = c; }

    /** Returns the persisted player name, or null if none has been set. */
    public String getPlayerName() { return playerName; }

    /** Returns true if a player name has been set. */
    public boolean hasPlayerName() { return playerName != null && !playerName.isEmpty(); }

    /** Saves the player name (capped at MAX_NAME_LENGTH) to preferences. */
    public void setPlayerName(String name) {
        if (name == null || name.trim().isEmpty()) return;
        this.playerName = name.trim().substring(0, Math.min(name.trim().length(), MAX_NAME_LENGTH));
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.putString("player_name", this.playerName);
        prefs.flush();
    }

    private void loadPlayerName() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        String saved = prefs.getString("player_name", "");
        this.playerName = saved.isEmpty() ? null : saved;
    }

    /** Returns true if the player has already seen the How-To-Play tutorial. */
    public boolean hasSeenTutorial() { return seenTutorial; }

    /** Marks the tutorial as seen and persists the flag. */
    public void setSeenTutorial() {
        this.seenTutorial = true;
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.putBoolean("seen_tutorial", true);
        prefs.flush();
    }

    private void loadTutorialFlag() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        this.seenTutorial = prefs.getBoolean("seen_tutorial", false);
    }

    // ── Persistence ──────────────────────────────────────────────────────────

    public void save() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.putInteger("count", entries.size());
        for (int i = 0; i < entries.size(); i++) {
            prefs.putString("name_"  + i, entries.get(i).getPlayerName());
            prefs.putInteger("score_" + i, entries.get(i).getScore());
        }
        prefs.flush();
    }

    public void load() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        int count = prefs.getInteger("count", 0);
        entries.clear();
        for (int i = 0; i < count; i++) {
            String name  = prefs.getString( "name_"  + i, "PLAYER");
            int    score = prefs.getInteger("score_" + i, 0);
            entries.add(new LeaderboardEntry(name, score));
        }
        Collections.sort(entries);
    }
}
