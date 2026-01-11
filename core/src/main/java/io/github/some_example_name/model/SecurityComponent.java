package io.github.some_example_name.model;

/**
 * Interface for all security components using the Composite Pattern.
 * Each component contributes to the overall security of an account.
 * This is part of the Composite design pattern where SecurityComponent
 * is the Component interface.
 */
public interface SecurityComponent {
    
    /**
     * Get the display name of this component.
     * @return Human-readable name
     */
    String getName();
    
    /**
     * Get the type of this security component.
     * @return Component type enum
     */
    SecurityComponentType getType();
    
    /**
     * Get a string representation of the current value/setting.
     * @return Current value as string
     */
    String getValue();
    
    /**
     * Calculate the security score contribution of this component.
     * Higher scores indicate better security.
     * @return Security score (typically 0-120)
     */
    int getSecurityScore();
    
    /**
     * Calculate the risk level contribution of this component.
     * Higher values indicate greater risk.
     * @return Risk level (typically 0-50)
     */
    int getRiskLevel();
    
    /**
     * Get a description explaining this component's purpose and state.
     * @return Educational description
     */
    String getDescription();
    
    /**
     * Create a copy of this component.
     * Used for undo/redo functionality.
     * @return A new instance with the same values
     */
    SecurityComponent copy();
}
