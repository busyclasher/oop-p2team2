package sg.edu.sit.inf1009.p2team2.game.leaderboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import sg.edu.sit.inf1009.p2team2.engine.io.storage.StorageProvider;
import sg.edu.sit.inf1009.p2team2.engine.io.storage.StorageProviders;
import sg.edu.sit.inf1009.p2team2.game.entities.CharacterType;

/**
 * Leaderboard with persistence via libGDX Preferences.
 * Scores are kept sorted highest-first and capped at maxEntries.
 */
public class LeaderboardManager {

    private static final int    DEFAULT_MAX_ENTRIES = 10;
    private static final String PREFS_NAME          = "cyberscouts-leaderboard";
    private static final StorageProvider STORAGE = StorageProviders.preferences(PREFS_NAME);

    private final List<LeaderboardEntry> entries;
    private final int                    maxEntries;
    private CharacterType                lastCharacter; // character used in the most recent game

    public LeaderboardManager() {
        this(DEFAULT_MAX_ENTRIES);
    }

    public LeaderboardManager(int maxEntries) {
        this.maxEntries = maxEntries;
        this.entries    = new ArrayList<>();
        load();
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

    // ── Persistence ──────────────────────────────────────────────────────────

    public void save() {
        STORAGE.putInteger("count", entries.size());
        for (int i = 0; i < entries.size(); i++) {
            STORAGE.putString("name_"  + i, entries.get(i).getPlayerName());
            STORAGE.putInteger("score_" + i, entries.get(i).getScore());
        }
        STORAGE.flush();
    }

    public void load() {
        int count = STORAGE.getInteger("count", 0);
        entries.clear();
        for (int i = 0; i < count; i++) {
            String name  = STORAGE.getString("name_"  + i, "PLAYER");
            int    score = STORAGE.getInteger("score_" + i, 0);
            entries.add(new LeaderboardEntry(name, score));
        }
        Collections.sort(entries);
    }
}
