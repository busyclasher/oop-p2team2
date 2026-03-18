package sg.edu.sit.inf1009.p2team2.game.entities;

import com.badlogic.gdx.graphics.Color;

/**
 * Buff cards offered to the player every 200 points.
 * Each entry carries display text and a highlight colour for the card UI.
 */
public enum BuffType {

    EXTRA_LIFE(
        "Extra Life",
        "+1 life restored.",
        new Color(0.20f, 0.85f, 0.35f, 1f)
    ),
    SHIELD(
        "Firewall Shield",
        "Absorb the next\nbad-entity hit for free.",
        new Color(0.20f, 0.55f, 1.00f, 1f)
    ),
    SPEED_SURGE(
        "Speed Surge",
        "Move 25% faster\nfor the rest of the game.",
        new Color(1.00f, 0.85f, 0.10f, 1f)
    ),
    SCORE_BOOST(
        "Score Boost",
        "Next 20 good items\ngive +75% bonus score.",
        new Color(1.00f, 0.50f, 0.10f, 1f)
    ),
    SLOW_FIELD(
        "Slow Field",
        "Entities fall 15%\nslower permanently.",
        new Color(0.30f, 0.90f, 0.90f, 1f)
    ),
    SCORE_BURST(
        "Score Burst",
        "Instantly gain\n+300 bonus points.",
        new Color(1.00f, 0.90f, 0.20f, 1f)
    );

    public final String name;
    public final String desc;
    public final Color  color;

    BuffType(String name, String desc, Color color) {
        this.name  = name;
        this.desc  = desc;
        this.color = color;
    }
}
