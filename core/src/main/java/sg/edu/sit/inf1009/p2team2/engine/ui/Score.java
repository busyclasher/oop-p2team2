package sg.edu.sit.inf1009.p2team2.engine.ui;

/**
 * Simple score model for leaderboard-style scenes.
 */
public class Score {
    private final String name;
    private final int value;

    public Score(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public int getPoints() {
        return value;
    }
}
