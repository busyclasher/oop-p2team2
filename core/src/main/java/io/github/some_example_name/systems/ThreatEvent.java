package io.github.some_example_name.systems;

import io.github.some_example_name.model.*;

/**
 * Represents a random security threat event that can occur during gameplay.
 * Events test whether the player's account setup can handle real-world threats.
 */
public class ThreatEvent {
    
    public enum EventType {
        PHISHING_ATTACK,
        DEVICE_LOST,
        DATA_BREACH,
        MALWARE_INFECTION,
        SIM_SWAP_ATTACK,
        SOCIAL_ENGINEERING,
        UNPATCHED_VULNERABILITY
    }
    
    private final EventType type;
    private final String name;
    private final String description;
    private final SecurityComponentType[] affectedComponents;
    
    /**
     * Create a threat event.
     * 
     * @param type Event type
     * @param name Event name
     * @param description What happened
     * @param affectedComponents Components that mitigate this threat
     */
    public ThreatEvent(EventType type, String name, String description, 
                      SecurityComponentType... affectedComponents) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.affectedComponents = affectedComponents;
    }
    
    /**
     * Calculate impact (damage/penalty) based on account's protections.
     * If account has proper protections, impact is minimal.
     * If missing protections, impact is severe.
     * 
     * @param account Account to test
     * @return Penalty amount (points deducted)
     */
    public int calculateImpact(SecureAccount account) {
        int basePenalty = 100; // Base damage
        int mitigation = 0;
        
        // Check if account has components that protect against this threat
        for (SecurityComponentType componentType : affectedComponents) {
            SecurityComponent component = account.getComponent(componentType);
            
            if (component != null) {
                // Component exists - add its security score as mitigation
                mitigation += component.getSecurityScore();
            }
        }
        
        // Calculate final penalty (more mitigation = less penalty)
        // Mitigation can reduce penalty by up to 90%
        int mitigationPercent = Math.min(90, (mitigation * 100) / 200);
        int finalPenalty = basePenalty - (basePenalty * mitigationPercent / 100);
        
        return finalPenalty;
    }
    
    /**
     * Get explanation of how this threat was mitigated (or not).
     * 
     * @param account Account that faced the threat
     * @return Explanation message
     */
    public String getMitigationExplanation(SecureAccount account) {
        StringBuilder sb = new StringBuilder();
        int penalty = calculateImpact(account);
        
        sb.append("Impact: -").append(penalty).append(" points\n\n");
        
        for (SecurityComponentType componentType : affectedComponents) {
            SecurityComponent component = account.getComponent(componentType);
            
            if (component != null) {
                sb.append("✓ ").append(component.getName())
                  .append(" (").append(component.getValue()).append(") ")
                  .append("helped protect you!\n");
            } else {
                sb.append("✗ Missing ").append(componentType.getDisplayName())
                  .append(" - increased damage!\n");
            }
        }
        
        return sb.toString();
    }
    
    public EventType getType() {
        return type;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public SecurityComponentType[] getAffectedComponents() {
        return affectedComponents;
    }
    
    @Override
    public String toString() {
        return name + ": " + description;
    }
}
