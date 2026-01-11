package io.github.some_example_name.model;

/**
 * Account recovery options for regaining access.
 */
public enum RecoveryType {
    NONE("No Recovery", 0, 40, "Cannot recover account if locked out"),
    EMAIL("Email Recovery", 60, 15, "Recovery via email verification"),
    PHONE("Phone Recovery", 70, 12, "Recovery via SMS or phone call"),
    BACKUP_CODES("Backup Codes", 90, 5, "Pre-generated recovery codes (best practice)");

    private final String displayName;
    private final int securityScore;
    private final int riskLevel;
    private final String description;

    RecoveryType(String displayName, int securityScore, int riskLevel, String description) {
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
