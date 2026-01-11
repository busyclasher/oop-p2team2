package io.github.some_example_name.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Subject/Observable base class for the Observer pattern.
 * Manages a list of listeners and provides notification methods.
 */
public class GameEventNotifier {
    
    private final List<GameEventListener> listeners;
    
    public GameEventNotifier() {
        this.listeners = new ArrayList<>();
    }
    
    /**
     * Register a listener for game events.
     * @param listener Listener to add
     */
    public void addListener(GameEventListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    /**
     * Remove a registered listener.
     * @param listener Listener to remove
     */
    public void removeListener(GameEventListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Clear all listeners.
     */
    public void clearListeners() {
        listeners.clear();
    }
    
    /**
     * Notify all listeners of a score change.
     */
    protected void notifyScoreChanged(int oldScore, int newScore, int delta) {
        for (GameEventListener listener : listeners) {
            listener.onScoreChanged(oldScore, newScore, delta);
        }
    }
    
    /**
     * Notify all listeners of a risk change.
     */
    protected void notifyRiskChanged(int oldRisk, int newRisk) {
        for (GameEventListener listener : listeners) {
            listener.onRiskChanged(oldRisk, newRisk);
        }
    }
    
    /**
     * Notify all listeners of a combo change.
     */
    protected void notifyComboChanged(int oldCombo, int newCombo) {
        for (GameEventListener listener : listeners) {
            listener.onComboChanged(oldCombo, newCombo);
        }
    }
    
    /**
     * Notify all listeners of a threat event.
     */
    protected void notifyThreatTriggered(String threatName, int damage) {
        for (GameEventListener listener : listeners) {
            listener.onThreatTriggered(threatName, damage);
        }
    }
    
    /**
     * Notify all listeners of a component change.
     */
    protected void notifyComponentChanged(String componentType, String componentValue) {
        for (GameEventListener listener : listeners) {
            listener.onComponentChanged(componentType, componentValue);
        }
    }
    
    /**
     * Get the number of registered listeners.
     */
    public int getListenerCount() {
        return listeners.size();
    }
}
