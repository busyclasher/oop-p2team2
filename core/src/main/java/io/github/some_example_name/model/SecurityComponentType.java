package io.github.some_example_name.model;

/**
 * Enumeration of all security component types in the game.
 * Each type represents a different aspect of account security.
 */
public enum SecurityComponentType {
    PASSWORD("Password"),
    TWO_FA("Two-Factor Authentication"),
    UPDATES("Software Updates"),
    RECOVERY("Account Recovery"),
    PRIVACY("Privacy Settings");

    private final String displayName;

    SecurityComponentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
