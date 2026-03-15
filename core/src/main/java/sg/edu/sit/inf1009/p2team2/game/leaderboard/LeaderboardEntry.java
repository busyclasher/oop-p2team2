package sg.edu.sit.inf1009.p2team2.game.leaderboard;

/**
 * Immutable value object holding a single leaderboard record.
 */
public class LeaderboardEntry implements Comparable<LeaderboardEntry> {

    private final String playerName;
    private final int    score;
    private final long   timestamp;

    public LeaderboardEntry(String playerName, int score) {
        this.playerName = playerName;
        this.score      = score;
        this.timestamp  = System.currentTimeMillis();
    }

    public String getPlayerName() { return playerName; }
    public int    getScore()      { return score; }
    public long   getTimestamp()  { return timestamp; }

    /** Higher scores rank first. */
    @Override
    public int compareTo(LeaderboardEntry other) {
        return Integer.compare(other.score, this.score);
    }

    @Override
    public String toString() {
        return playerName + ":" + score + ":" + timestamp;
    }
}
