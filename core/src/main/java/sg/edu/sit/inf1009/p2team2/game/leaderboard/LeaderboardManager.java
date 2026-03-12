package sg.edu.sit.inf1009.p2team2.game.leaderboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * In-memory leaderboard with a configurable max-entry cap.
 *
 * Scores are kept sorted highest-first.  The list is session-local;
 * persistence can be wired in later via the ConfigManager if needed.
 */
public class LeaderboardManager {

    private static final int DEFAULT_MAX_ENTRIES = 10;

    private final List<LeaderboardEntry> entries;
    private final int                    maxEntries;

    public LeaderboardManager() {
        this(DEFAULT_MAX_ENTRIES);
    }

    public LeaderboardManager(int maxEntries) {
        this.maxEntries = maxEntries;
        this.entries    = new ArrayList<>();
    }

    /**
     * Adds an entry and keeps the list sorted and capped.
     *
     * @param name  display name for this entry
     * @param score final score
     */
    public void addEntry(String name, int score) {
        entries.add(new LeaderboardEntry(name, score));
        Collections.sort(entries);
        if (entries.size() > maxEntries) {
            entries.subList(maxEntries, entries.size()).clear();
        }
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
}
