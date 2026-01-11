package io.github.some_example_name.model;

/**
 * Privacy setting levels controlling data sharing and visibility.
 */
public enum PrivacyLevel {
    OPEN("Open/Public", 20, 35, "Profile and data widely shared"),
    MODERATE("Moderate Privacy", 60, 15, "Balanced privacy with some sharing"),
    TIGHT("Tight Privacy", 85, 5, "Minimal data sharing, maximum privacy");

    private final String displayName;
    private final int securityScore;
    private final int riskLevel;
    private final String description;

    PrivacyLevel(String displayName, int securityScore, int riskLevel, String description) {
        this.displayName = displayName;
        this.securityScore = securityScore;
        this.riskLevel = riskLevel;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getSecurityScore() {
        return securityScore;
    }

    public int getRiskLevel() {
        return riskLevel;
    }

    public String getDescription() {
        return description;
    }
}
