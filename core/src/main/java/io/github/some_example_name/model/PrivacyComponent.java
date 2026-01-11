package io.github.some_example_name.model;

/**
 * Privacy settings component.
 * Part of the Composite pattern as a Leaf component.
 */
public class PrivacyComponent implements SecurityComponent {
    
    private final PrivacyLevel level;
    
    public PrivacyComponent(PrivacyLevel level) {
        if (level == null) {
            throw new IllegalArgumentException("Privacy level cannot be null");
        }
        this.level = level;
    }
    
    @Override
    public String getName() {
        return SecurityComponentType.PRIVACY.getDisplayName();
    }
    
    @Override
    public SecurityComponentType getType() {
        return SecurityComponentType.PRIVACY;
    }
    
    @Override
    public String getValue() {
        return level.getDisplayName();
    }
    
    @Override
    public int getSecurityScore() {
        return level.getSecurityScore();
    }
    
    @Override
    public int getRiskLevel() {
        return level.getRiskLevel();
    }
    
    @Override
    public String getDescription() {
        return level.getDescription();
    }
    
    public PrivacyLevel getLevel() {
        return level;
    }
    
    @Override
    public SecurityComponent copy() {
        return new PrivacyComponent(level);
    }
    
    @Override
    public String toString() {
        return String.format("Privacy: %s (Score: %d, Risk: %d)", 
            level.getDisplayName(), 
            level.getSecurityScore(), 
            level.getRiskLevel());
    }
}
