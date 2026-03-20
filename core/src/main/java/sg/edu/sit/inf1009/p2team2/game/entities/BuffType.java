package sg.edu.sit.inf1009.p2team2.game.entities;

import com.badlogic.gdx.graphics.Color;

/**
 * Buff cards offered to the player every 200 points.
 * Each entry matches exactly one card image asset.
 */
public enum BuffType {

    EXTRA_LIFE(
        "Extra Life",
        "+1 life. Stacks with\neach pick.",
        new Color(1.00f, 0.85f, 0.20f, 1f),   // bright gold — matches healthy-card
        "healthy-card.png"
    ),
    SPEED_SURGE(
        "Haste I",
        "+10% move speed\npermanently.",
        new Color(0.25f, 0.60f, 1.00f, 1f),   // blue — matches haste-i-card
        "haste-i-card.png"
    ),
    SHIELD(
        "Death Defier",
        "Free revive on death.\nOne at a time.",
        new Color(1.00f, 0.60f, 0.10f, 1f),   // deep amber — matches death-defier-card
        "death-defier-card.png"
    );

    private final String displayName;
    private final String description;
    private final Color accentColor;
    private final String cardSprite;

    BuffType(String displayName, String description, Color accentColor, String cardSprite) {
        this.displayName = displayName;
        this.description = description;
        this.accentColor = accentColor;
        this.cardSprite = cardSprite;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public Color getAccentColor() {
        return accentColor;
    }

    public String getCardSprite() {
        return cardSprite;
    }
}
