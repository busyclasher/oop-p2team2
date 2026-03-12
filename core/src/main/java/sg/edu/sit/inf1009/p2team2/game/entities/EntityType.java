package sg.edu.sit.inf1009.p2team2.game.entities;

import com.badlogic.gdx.graphics.Color;

/**
 * Enumerates every falling entity type in Silicon Sentinel.
 *
 * Each constant carries its display color, score reward, harm flag,
 * and whether it triggers an MCQ quiz on contact.
 */
public enum EntityType {

    // ── Good entities ────────────────────────────────────────────────
    GOOD_BYTE   (new Color(0.27f, 0.51f, 0.93f, 1f), 5,  false, false),
    SAFE_EMAIL  (new Color(0.30f, 0.80f, 0.50f, 1f), 5,  false, false),
    GOLD_ENVELOPE(new Color(1.00f, 0.84f, 0.00f, 1f), 10, false, true),

    // ── Bad entities (standard mode) ─────────────────────────────────
    PHISHING_HOOK  (new Color(0.90f, 0.20f, 0.20f, 1f), 0, true, false),
    RANSOMWARE_LOCK(new Color(0.95f, 0.85f, 0.00f, 1f), 0, true, true),
    MALWARE_SWARM  (new Color(0.55f, 0.15f, 0.70f, 1f), 0, true, false),

    // ── Frenzy-only bad entities ─────────────────────────────────────
    ROOTKIT (new Color(1.00f, 0.45f, 0.00f, 1f), 0, true, false),
    SPYWARE (new Color(0.80f, 0.10f, 0.80f, 1f), 0, true, true),

    // ── Player (not a falling entity, used for entity factory) ───────
    PLAYER  (new Color(0.15f, 0.85f, 0.30f, 1f), 0, false, false);

    private final Color   color;
    private final int     scoreValue;
    private final boolean bad;
    private final boolean quizTrigger;

    EntityType(Color color, int scoreValue, boolean bad, boolean quizTrigger) {
        this.color       = color;
        this.scoreValue  = scoreValue;
        this.bad         = bad;
        this.quizTrigger = quizTrigger;
    }

    public Color   getColor()      { return color; }
    public int     getScoreValue() { return scoreValue; }
    public boolean isBad()         { return bad; }
    public boolean isQuizTrigger() { return quizTrigger; }

    /** True for GOOD_BYTE, SAFE_EMAIL, and GOLD_ENVELOPE. */
    public boolean isGood() { return !bad; }

    /** True only for the standard-mode entity types (not frenzy-exclusive). */
    public boolean isStandardMode() {
        return this == GOOD_BYTE || this == SAFE_EMAIL || this == GOLD_ENVELOPE
            || this == PHISHING_HOOK || this == RANSOMWARE_LOCK || this == MALWARE_SWARM;
    }
}
