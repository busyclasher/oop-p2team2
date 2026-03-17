package sg.edu.sit.inf1009.p2team2.game.cards;

/**
 * Represents a single power-up card that the player can collect and activate.
 *
 * Design decisions still needed (see requirements.md #9):
 * - How cards are obtained (random drops, score milestones, quiz rewards)
 * - When/how cards are played (automatic, player-activated, between rounds)
 * - Card inventory limits (max hand size, one-time use vs reusable)
 */
public class Card {

    public enum CardType {
        SLOW_ALL      ("Slow Motion",   "Slows all falling entities for 5 seconds."),
        SHIELD        ("Shield",        "Blocks the next bad entity hit."),
        DOUBLE_POINTS ("Double Points", "Doubles score for the next 10 seconds."),
        EXTRA_LIFE    ("Extra Life",    "Grants +1 life immediately."),
        MAGNET        ("Magnet",        "Attracts nearby good entities toward the player.");

        private final String displayName;
        private final String description;

        CardType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }

    private final CardType type;
    private final float    duration;
    private boolean        used;

    public Card(CardType type, float duration) {
        this.type     = type;
        this.duration = duration;
        this.used     = false;
    }

    public CardType getType()     { return type; }
    public float    getDuration() { return duration; }
    public boolean  isUsed()      { return used; }

    public void markUsed() { this.used = true; }
}
