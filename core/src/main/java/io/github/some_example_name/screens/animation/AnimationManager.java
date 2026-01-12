package io.github.some_example_name.screens.animation;

/**
 * Manages all animation timers and state for the game screen.
 * Separates animation concerns from rendering logic.
 * Demonstrates the Single Responsibility Principle.
 */
public class AnimationManager {
    
    // General animation timers
    private float animTime;
    private float scorePopTimer;
    private float factFadeAlpha;
    private float threatPulse;
    
    // Slot-specific glow timers
    private final float[] slotGlowTimers;
    
    // Observer effect timers
    private float comboFlashTimer;
    private float riskFlashTimer;
    private String lastEventMessage;
    private float eventMessageTimer;
    
    // Last tracked score for detecting changes
    private int lastScore;
    
    private static final int NUM_SLOTS = 5;
    
    public AnimationManager() {
        this.animTime = 0f;
        this.scorePopTimer = 0f;
        this.factFadeAlpha = 1f;
        this.threatPulse = 0f;
        this.slotGlowTimers = new float[NUM_SLOTS];
        this.comboFlashTimer = 0f;
        this.riskFlashTimer = 0f;
        this.lastEventMessage = "";
        this.eventMessageTimer = 0f;
        this.lastScore = 0;
    }
    
    /**
     * Update all animation timers.
     * 
     * @param delta Time since last frame in seconds
     * @param currentScore Current score to detect changes
     */
    public void update(float delta, int currentScore) {
        // Update general timers
        animTime += delta;
        threatPulse += delta * 8f;
        
        // Fact fade animation
        factFadeAlpha = 0.7f + 0.3f * (float) Math.sin(animTime * 0.5f);
        
        // Score pop animation
        if (scorePopTimer > 0) {
            scorePopTimer -= delta;
        }
        
        // Detect score changes
        if (currentScore != lastScore) {
            scorePopTimer = 0.5f;
            lastScore = currentScore;
        }
        
        // Update slot glow timers
        for (int i = 0; i < slotGlowTimers.length; i++) {
            if (slotGlowTimers[i] > 0) {
                slotGlowTimers[i] -= delta;
            }
        }
        
        // Update observer effect timers
        if (comboFlashTimer > 0) {
            comboFlashTimer -= delta;
        }
        if (riskFlashTimer > 0) {
            riskFlashTimer -= delta;
        }
        if (eventMessageTimer > 0) {
            eventMessageTimer -= delta;
        }
    }
    
    /**
     * Trigger a glow effect for a specific slot.
     * 
     * @param slotIndex Index of the slot (0-4)
     */
    public void triggerSlotGlow(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < NUM_SLOTS) {
            slotGlowTimers[slotIndex] = 0.5f;
        }
    }
    
    /**
     * Trigger score pop animation.
     */
    public void triggerScorePop() {
        scorePopTimer = 0.5f;
    }
    
    /**
     * Trigger combo flash effect.
     */
    public void triggerComboFlash() {
        comboFlashTimer = 0.5f;
    }
    
    /**
     * Trigger risk flash effect.
     */
    public void triggerRiskFlash() {
        riskFlashTimer = 0.5f;
    }
    
    /**
     * Set an event message to display temporarily.
     * 
     * @param message Message to display
     * @param duration Duration in seconds
     */
    public void setEventMessage(String message, float duration) {
        this.lastEventMessage = message;
        this.eventMessageTimer = duration;
    }
    
    // ==================== Getters ====================
    
    public float getAnimTime() {
        return animTime;
    }
    
    public float getScorePopTimer() {
        return scorePopTimer;
    }
    
    public float getFactFadeAlpha() {
        return factFadeAlpha;
    }
    
    public float getThreatPulse() {
        return threatPulse;
    }
    
    public float getSlotGlowTimer(int index) {
        if (index >= 0 && index < NUM_SLOTS) {
            return slotGlowTimers[index];
        }
        return 0f;
    }
    
    public float getComboFlashTimer() {
        return comboFlashTimer;
    }
    
    public float getRiskFlashTimer() {
        return riskFlashTimer;
    }
    
    public String getLastEventMessage() {
        return lastEventMessage;
    }
    
    public float getEventMessageTimer() {
        return eventMessageTimer;
    }
    
    /**
     * Get the scale factor for score pop animation.
     * 
     * @return Scale multiplier (1.0 to 1.3)
     */
    public float getScorePopScale() {
        if (scorePopTimer > 0) {
            return 1f + 0.3f * (scorePopTimer / 0.5f);
        }
        return 1f;
    }
    
    /**
     * Reset all timers to initial state.
     */
    public void reset() {
        animTime = 0f;
        scorePopTimer = 0f;
        factFadeAlpha = 1f;
        threatPulse = 0f;
        for (int i = 0; i < slotGlowTimers.length; i++) {
            slotGlowTimers[i] = 0f;
        }
        comboFlashTimer = 0f;
        riskFlashTimer = 0f;
        lastEventMessage = "";
        eventMessageTimer = 0f;
        lastScore = 0;
    }
}
