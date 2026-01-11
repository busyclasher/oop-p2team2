package io.github.some_example_name.model;

/**
 * Password component representing password strength choice.
 * Part of the Composite pattern as a Leaf component.
 */
public class PasswordComponent implements SecurityComponent {
    
    private final PasswordStrength strength;
    
    public PasswordComponent(PasswordStrength strength) {
        if (strength == null) {
            throw new IllegalArgumentException("Password strength cannot be null");
        }
        this.strength = strength;
    }
    
    @Override
    public String getName() {
        return SecurityComponentType.PASSWORD.getDisplayName();
    }
    
    @Override
    public SecurityComponentType getType() {
        return SecurityComponentType.PASSWORD;
    }
    
    @Override
    public String getValue() {
        return strength.getDisplayName();
    }
    
    @Override
    public int getSecurityScore() {
        return strength.getSecurityScore();
    }
    
    @Override
    public int getRiskLevel() {
        return strength.getRiskLevel();
    }
    
    @Override
    public String getDescription() {
        return strength.getDescription();
    }
    
    public PasswordStrength getStrength() {
        return strength;
    }
    
    @Override
    public SecurityComponent copy() {
        return new PasswordComponent(strength);
    }
    
    @Override
    public String toString() {
        return String.format("Password: %s (Score: %d, Risk: %d)", 
            strength.getDisplayName(), 
            strength.getSecurityScore(), 
            strength.getRiskLevel());
    }
}
