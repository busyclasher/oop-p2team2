package io.github.some_example_name.model;

/**
 * Software update component.
 * Part of the Composite pattern as a Leaf component.
 */
public class UpdateComponent implements SecurityComponent {
    
    private final UpdateSetting setting;
    
    public UpdateComponent(UpdateSetting setting) {
        if (setting == null) {
            throw new IllegalArgumentException("Update setting cannot be null");
        }
        this.setting = setting;
    }
    
    @Override
    public String getName() {
        return SecurityComponentType.UPDATES.getDisplayName();
    }
    
    @Override
    public SecurityComponentType getType() {
        return SecurityComponentType.UPDATES;
    }
    
    @Override
    public String getValue() {
        return setting.getDisplayName();
    }
    
    @Override
    public int getSecurityScore() {
        return setting.getSecurityScore();
    }
    
    @Override
    public int getRiskLevel() {
        return setting.getRiskLevel();
    }
    
    @Override
    public String getDescription() {
        return setting.getDescription();
    }
    
    public UpdateSetting getSetting() {
        return setting;
    }
    
    @Override
    public SecurityComponent copy() {
        return new UpdateComponent(setting);
    }
    
    @Override
    public String toString() {
        return String.format("Updates: %s (Score: %d, Risk: %d)", 
            setting.getDisplayName(), 
            setting.getSecurityScore(), 
            setting.getRiskLevel());
    }
}
