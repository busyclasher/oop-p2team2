package io.github.some_example_name.observer;

/**
 * Observer interface for game events.
 * Part of the Observer design pattern - allows UI elements to react to game state changes.
 */
public interface GameEventListener {
    
    /**
     * Called when the player's score changes.
     * @param oldScore Previous score
     * @param newScore Current score
     * @param delta Change amount (positive for gain, negative for loss)
     */
    void onScoreChanged(int oldScore, int newScore, int delta);
    
    /**
     * Called when the risk level changes.
     * @param oldRisk Previous risk (0-100)
     * @param newRisk Current risk (0-100)
     */
    void onRiskChanged(int oldRisk, int newRisk);
    
    /**
     * Called when the combo multiplier changes.
     * @param oldCombo Previous combo
     * @param newCombo Current combo
     */
    void onComboChanged(int oldCombo, int newCombo);
    
    /**
     * Called when a threat event is triggered.
     * @param threatName Name of the threat
     * @param damage Damage dealt
     */
    void onThreatTriggered(String threatName, int damage);
    
    /**
     * Called when a component is added or changed.
     * @param componentType Type of component
     * @param componentValue New value
     */
    void onComponentChanged(String componentType, String componentValue);
}
