package sg.edu.sit.inf1009.p2team2.game.entities;

/**
 * Defines the selectable player characters, each with unique attributes.
 */
public enum CharacterType {

    SPECTER(
        "char-1.png",
        "Specter",
        "Speed Demon",
        "Moves faster but has fewer lives.\nBonus points per catch.",
        450f,   // speed (px/s)
        3,      // lives
        1.2f,   // score multiplier
        420f    // jump strength (px/s upward impulse)
    ),

    GUARDIAN(
        "char-2.png",
        "Guardian",
        "Iron Defense",
        "Slower movement but starts\nwith two extra lives.",
        300f,   // speed (px/s)
        5,      // lives
        1.0f,   // score multiplier
        350f    // jump strength
    ),

    CIPHER(
        "char-3.png",
        "Cipher",
        "Data Rush",
        "Ultra-fast but risky.\nMassive score multiplier per catch.",
        500f,   // speed (px/s)
        2,      // lives
        1.5f,   // score multiplier
        380f    // jump strength
    );

    private final String sprite;
    private final String name;
    private final String perkName;
    private final String perkDesc;
    private final float  speed;
    private final int    lives;
    private final float  scoreMultiplier;
    private final float  jumpStrength;

    CharacterType(String sprite, String name, String perkName, String perkDesc,
                  float speed, int lives, float scoreMultiplier, float jumpStrength) {
        this.sprite          = sprite;
        this.name            = name;
        this.perkName        = perkName;
        this.perkDesc        = perkDesc;
        this.speed           = speed;
        this.lives           = lives;
        this.scoreMultiplier = scoreMultiplier;
        this.jumpStrength    = jumpStrength;
    }

    public String getSprite()          { return sprite; }
    public String getName()            { return name; }
    public String getPerkName()        { return perkName; }
    public String getPerkDesc()        { return perkDesc; }
    public float  getSpeed()           { return speed; }
    public int    getLives()           { return lives; }
    public float  getScoreMultiplier() { return scoreMultiplier; }
    public float  getJumpStrength()    { return jumpStrength; }
}
