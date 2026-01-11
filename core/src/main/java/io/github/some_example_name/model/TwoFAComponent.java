package io.github.some_example_name.model;

/**
 * Two-Factor Authentication component.
 * Part of the Composite pattern as a Leaf component.
 */
public class TwoFAComponent implements SecurityComponent {
    
    private final TwoFAType type;
    
    public TwoFAComponent(TwoFAType type) {
        if (type == null) {
            throw new IllegalArgumentException("2FA type cannot be null");
        }
        this.type = type;
    }
    
    @Override
    public String getName() {
        return SecurityComponentType.TWO_FA.getDisplayName();
    }
    
    @Override
    public SecurityComponentType getType() {
        return SecurityComponentType.TWO_FA;
    }
    
    @Override
    public String getValue() {
        return type.getDisplayName();
    }
    
    @Override
    public int getSecurityScore() {
        return type.getSecurityScore();
    }
    
    @Override
    public int getRiskLevel() {
        return type.getRiskLevel();
    }
    
    @Override
    public String getDescription() {
        return type.getDescription();
    }
    
    public TwoFAType getTwoFAType() {
        return type;
    }
    
    @Override
    public SecurityComponent copy() {
        return new TwoFAComponent(type);
    }
    
    @Override
    public String toString() {
        return String.format("2FA: %s (Score: %d, Risk: %d)", 
            type.getDisplayName(), 
            type.getSecurityScore(), 
            type.getRiskLevel());
    }
}
