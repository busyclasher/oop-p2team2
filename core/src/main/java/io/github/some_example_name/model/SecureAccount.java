package io.github.some_example_name.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

/**
 * Composite class that contains multiple SecurityComponent objects.
 * This is the Composite in the Composite design pattern.
 * A SecureAccount is composed of various security components that work together
 * to provide overall account security.
 */
public class SecureAccount {
    
    private final Map<SecurityComponentType, SecurityComponent> components;
    
    /**
     * Create an empty SecureAccount with no components.
     */
    public SecureAccount() {
        this.components = new HashMap<>();
    }
    
    /**
     * Add a security component to this account.
     * If a component of the same type already exists, it will be replaced.
     * 
     * @param component The component to add
     * @throws IllegalArgumentException if component is null
     */
    public void addComponent(SecurityComponent component) {
        if (component == null) {
            throw new IllegalArgumentException("Component cannot be null");
        }
        components.put(component.getType(), component);
    }
    
    /**
     * Remove a component by its type.
     * 
     * @param type The type of component to remove
     * @return The removed component, or null if it didn't exist
     */
    public SecurityComponent removeComponent(SecurityComponentType type) {
        return components.remove(type);
    }
    
    /**
     * Get a specific component by type.
     * 
     * @param type The type of component to retrieve
     * @return The component, or null if not present
     */
    public SecurityComponent getComponent(SecurityComponentType type) {
        return components.get(type);
    }
    
    /**
     * Check if a component of the given type exists.
     * 
     * @param type The type to check
     * @return true if the component exists
     */
    public boolean hasComponent(SecurityComponentType type) {
        return components.containsKey(type);
    }
    
    /**
     * Get all components in this account.
     * 
     * @return Collection of all components
     */
    public Collection<SecurityComponent> getAllComponents() {
        return components.values();
    }
    
    /**
     * Calculate the total security score by aggregating all component scores.
     * This demonstrates the Composite pattern's ability to treat individual
     * and composite objects uniformly.
     * 
     * @return Total security score
     */
    public int getTotalSecurityScore() {
        int total = 0;
        for (SecurityComponent component : components.values()) {
            total += component.getSecurityScore();
        }
        return total;
    }
    
    /**
     * Calculate the total risk level by aggregating all component risks.
     * 
     * @return Total risk level (0-100+)
     */
    public int getTotalRiskLevel() {
        int total = 0;
        for (SecurityComponent component : components.values()) {
            total += component.getRiskLevel();
        }
        return Math.min(total, 100); // Cap at 100
    }
    
    /**
     * Check if all five required components are present.
     * 
     * @return true if account is complete
     */
    public boolean isComplete() {
        return components.size() == 5 &&
               hasComponent(SecurityComponentType.PASSWORD) &&
               hasComponent(SecurityComponentType.TWO_FA) &&
               hasComponent(SecurityComponentType.UPDATES) &&
               hasComponent(SecurityComponentType.RECOVERY) &&
               hasComponent(SecurityComponentType.PRIVACY);
    }
    
    /**
     * Get the number of components in this account.
     * 
     * @return Component count
     */
    public int getComponentCount() {
        return components.size();
    }
    
    /**
     * Reset the account by removing all components.
     */
    public void clear() {
        components.clear();
    }
    
    /**
     * Create a deep copy of this account.
     * Useful for undo/redo functionality.
     * 
     * @return A new SecureAccount with copied components
     */
    public SecureAccount copy() {
        SecureAccount copy = new SecureAccount();
        for (SecurityComponent component : components.values()) {
            copy.addComponent(component.copy());
        }
        return copy;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SecureAccount {\n");
        sb.append("  Components: ").append(components.size()).append("/5\n");
        sb.append("  Total Score: ").append(getTotalSecurityScore()).append("\n");
        sb.append("  Total Risk: ").append(getTotalRiskLevel()).append("\n");
        for (SecurityComponent component : components.values()) {
            sb.append("  - ").append(component.toString()).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
