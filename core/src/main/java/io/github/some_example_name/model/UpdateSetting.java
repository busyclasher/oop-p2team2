package io.github.some_example_name.model;

/**
 * Software update settings with security implications.
 */
public enum UpdateSetting {
    OFF("Updates Disabled", 0, 45, "Software remains unpatched and vulnerable"),
    MANUAL("Manual Updates", 50, 20, "Updates require manual installation"),
    AUTO("Automatic Updates", 80, 5, "Security patches applied automatically");

    private final String displayName;
    private final int securityScore;
    private final int riskLevel;
    private final String description;

    UpdateSetting(String displayName, int securityScore, int riskLevel, String description) {
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
