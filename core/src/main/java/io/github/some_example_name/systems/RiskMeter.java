package io.github.some_example_name.systems;

import io.github.some_example_name.model.SecureAccount;
import com.badlogic.gdx.graphics.Color;

/**
 * Manages risk level calculation and visualization.
 * Risk is inversely related to security - higher risk means weaker security.
 */
public class RiskMeter {
    
    public enum RiskLevel {
        LOW("Low Risk", new Color(0.2f, 0.8f, 0.3f, 1f)),      // Green
        MEDIUM("Medium Risk", new Color(1f, 0.8f, 0.2f, 1f)),   // Yellow
        HIGH("High Risk", new Color(1f, 0.5f, 0.1f, 1f)),       // Orange
        CRITICAL("Critical Risk", new Color(0.9f, 0.2f, 0.2f, 1f)); // Red
        
        private final String displayName;
        private final Color color;
        
        RiskLevel(String displayName, Color color) {
            this.displayName = displayName;
            this.color = color;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public Color getColor() {
            return color;
        }
    }
    
    private int currentRisk;
    
    public RiskMeter() {
        this.currentRisk = 0;
    }
    
    /**
     * Calculate risk level based on account components.
     * 
     * @param account Account to evaluate
     * @return Risk value (0-100)
     */
    public int calculateRisk(SecureAccount account) {
        if (account == null || account.getComponentCount() == 0) {
            currentRisk = 100; // Maximum risk for no security
            return currentRisk;
        }
        
        // Get aggregated risk from components
        currentRisk = account.getTotalRiskLevel();
        
        // Apply penalty for incomplete account
        if (!account.isComplete()) {
            int missingComponents = 5 - account.getComponentCount();
            currentRisk += (missingComponents * 15); // +15 risk per missing component
        }
        
        // Cap at 100
        currentRisk = Math.min(currentRisk, 100);
        
        return currentRisk;
    }
    
    /**
     * Get the current risk level category.
     * 
     * @return RiskLevel enum
     */
    public RiskLevel getRiskLevel() {
        if (currentRisk <= 25) {
            return RiskLevel.LOW;
        } else if (currentRisk <= 50) {
            return RiskLevel.MEDIUM;
        } else if (currentRisk <= 75) {
            return RiskLevel.HIGH;
        } else {
            return RiskLevel.CRITICAL;
        }
    }
    
    /**
     * Get color for UI visualization based on risk level.
     * 
     * @return Color object
     */
    public Color getRiskColor() {
        return getRiskLevel().getColor();
    }
    
    /**
     * Get current risk value.
     * 
     * @return Risk (0-100)
     */
    public int getCurrentRisk() {
        return currentRisk;
    }
    
    /**
     * Get risk as a percentage (0.0 to 1.0).
     * 
     * @return Risk percentage
     */
    public float getRiskPercentage() {
        return currentRisk / 100f;
    }
    
    /**
     * Add risk (from threat events).
     * 
     * @param amount Amount to add
     */
    public void addRisk(int amount) {
        currentRisk = Math.min(100, currentRisk + amount);
    }
    
    /**
     * Reduce risk (from mitigations).
     * 
     * @param amount Amount to reduce
     */
    public void reduceRisk(int amount) {
        currentRisk = Math.max(0, currentRisk - amount);
    }
    
    /**
     * Reset risk to 0.
     */
    public void reset() {
        currentRisk = 0;
    }
    
    @Override
    public String toString() {
        return String.format("%s (%d%%)", getRiskLevel().getDisplayName(), currentRisk);
    }
}
