package io.github.some_example_name.model;

/**
 * Password strength levels with associated security scores and risk values.
 */
public enum PasswordStrength {
    WEAK("Weak Password", 10, 40, "Simple or common password that's easy to guess"),
    MEDIUM("Medium Password", 50, 15, "Decent password with some complexity"),
    STRONG("Strong Password", 100, 5, "Complex password with letters, numbers, and symbols");

    private final String displayName;
    private final int securityScore;
    private final int riskLevel;
    private final String description;

    PasswordStrength(String displayName, int securityScore, int riskLevel, String description) {
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
