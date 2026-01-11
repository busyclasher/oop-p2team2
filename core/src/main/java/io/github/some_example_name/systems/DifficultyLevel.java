package io.github.some_example_name.systems;

/**
 * Difficulty levels that affect threat frequency and game parameters.
 * Demonstrates use of enums with associated configuration data.
 */
public enum DifficultyLevel {
    EASY(
        "Easy",
        "Relaxed mode - fewer threats, more time to learn",
        20f,  // minTimeBetweenEvents
        45f,  // maxTimeBetweenEvents
        0.15f, // spawnProbability
        0.5f,  // threatDamageMultiplier
        1.2f   // scoreMultiplier
    ),
    NORMAL(
        "Normal",
        "Balanced challenge - standard threat frequency",
        10f,
        30f,
        0.3f,
        1.0f,
        1.0f
    ),
    HARD(
        "Hard",
        "Intense mode - frequent threats, higher stakes",
        5f,
        15f,
        0.5f,
        1.5f,
        1.5f
    ),
    EXPERT(
        "Expert",
        "For security pros - relentless attacks!",
        3f,
        10f,
        0.7f,
        2.0f,
        2.0f
    );
    
    private final String displayName;
    private final String description;
    private final float minTimeBetweenEvents;
    private final float maxTimeBetweenEvents;
    private final float spawnProbability;
    private final float threatDamageMultiplier;
    private final float scoreMultiplier;
    
    DifficultyLevel(String displayName, String description,
                    float minTimeBetweenEvents, float maxTimeBetweenEvents,
                    float spawnProbability, float threatDamageMultiplier,
                    float scoreMultiplier) {
        this.displayName = displayName;
        this.description = description;
        this.minTimeBetweenEvents = minTimeBetweenEvents;
        this.maxTimeBetweenEvents = maxTimeBetweenEvents;
        this.spawnProbability = spawnProbability;
        this.threatDamageMultiplier = threatDamageMultiplier;
        this.scoreMultiplier = scoreMultiplier;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public float getMinTimeBetweenEvents() {
        return minTimeBetweenEvents;
    }
    
    public float getMaxTimeBetweenEvents() {
        return maxTimeBetweenEvents;
    }
    
    public float getSpawnProbability() {
        return spawnProbability;
    }
    
    public float getThreatDamageMultiplier() {
        return threatDamageMultiplier;
    }
    
    public float getScoreMultiplier() {
        return scoreMultiplier;
    }
    
    /**
     * Get the next difficulty level (cycles back to EASY after EXPERT).
     */
    public DifficultyLevel next() {
        DifficultyLevel[] values = values();
        return values[(this.ordinal() + 1) % values.length];
    }
    
    /**
     * Get color for UI display (R, G, B).
     */
    public float[] getColor() {
        switch (this) {
            case EASY: return new float[]{0.3f, 0.9f, 0.4f}; // Green
            case NORMAL: return new float[]{0.3f, 0.7f, 1.0f}; // Blue
            case HARD: return new float[]{1.0f, 0.6f, 0.2f}; // Orange
            case EXPERT: return new float[]{1.0f, 0.2f, 0.3f}; // Red
            default: return new float[]{1.0f, 1.0f, 1.0f};
        }
    }
}
