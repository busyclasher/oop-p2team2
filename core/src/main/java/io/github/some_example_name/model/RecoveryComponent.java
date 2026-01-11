package io.github.some_example_name.model;

/**
 * Account recovery component.
 * Part of the Composite pattern as a Leaf component.
 */
public class RecoveryComponent implements SecurityComponent {
    
    private final RecoveryType type;
    
    public RecoveryComponent(RecoveryType type) {
        if (type == null) {
            throw new IllegalArgumentException("Recovery type cannot be null");
        }
        this.type = type;
    }
    
    @Override
    public String getName() {
        return SecurityComponentType.RECOVERY.getDisplayName();
    }
    
    @Override
    public SecurityComponentType getType() {
        return SecurityComponentType.RECOVERY;
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
    
    public RecoveryType getRecoveryType() {
        return type;
    }
    
    @Override
    public SecurityComponent copy() {
        return new RecoveryComponent(type);
    }
    
    @Override
    public String toString() {
        return String.format("Recovery: %s (Score: %d, Risk: %d)", 
            type.getDisplayName(), 
            type.getSecurityScore(), 
            type.getRiskLevel());
    }
}
