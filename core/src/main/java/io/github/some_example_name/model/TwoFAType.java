package io.github.some_example_name.model;

/**
 * Two-Factor Authentication types with security ratings.
 */
public enum TwoFAType {
    NONE("No 2FA", 0, 50, "Account protected only by password"),
    SMS("SMS-based 2FA", 40, 25, "Text message verification codes"),
    AUTHENTICATOR("Authenticator App", 90, 8, "Time-based one-time passwords (TOTP)"),
    HARDWARE("Hardware Key", 120, 3, "Physical security key (most secure)");

    private final String displayName;
    private final int securityScore;
    private final int riskLevel;
    private final String description;

    TwoFAType(String displayName, int securityScore, int riskLevel, String description) {
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
