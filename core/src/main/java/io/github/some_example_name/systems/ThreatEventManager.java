package io.github.some_example_name.systems;

import io.github.some_example_name.model.SecurityComponentType;
import io.github.some_example_name.systems.ThreatEvent.EventType;
import java.util.Random;

/**
 * Manages generation and spawning of threat events during gameplay.
 */
public class ThreatEventManager {
    
    private final Random random;
    private float timeSinceLastEvent;
    private final float minTimeBetweenEvents;
    private final float maxTimeBetweenEvents;
    private final float spawnProbability;
    
    /**
     * Create a threat event manager.
     * 
     * @param minTimeBetweenEvents Minimum seconds between events
     * @param maxTimeBetweenEvents Maximum seconds between events
     * @param spawnProbability Probability of spawn when checked (0-1)
     */
    public ThreatEventManager(float minTimeBetweenEvents, float maxTimeBetweenEvents, 
                             float spawnProbability) {
        this.random = new Random();
        this.timeSinceLastEvent = 0;
        this.minTimeBetweenEvents = minTimeBetweenEvents;
        this.maxTimeBetweenEvents = maxTimeBetweenEvents;
        this.spawnProbability = spawnProbability;
    }
    
    /**
     * Create with default settings (events every 10-30 seconds, 30% chance).
     */
    public ThreatEventManager() {
        this(10f, 30f, 0.3f);
    }
    
    /**
     * Update time tracker.
     * 
     * @param deltaTime Time since last update (seconds)
     */
    public void update(float deltaTime) {
        timeSinceLastEvent += deltaTime;
    }
    
    /**
     * Check if an event should spawn based on time and probability.
     * 
     * @return true if event should spawn
     */
    public boolean shouldSpawnEvent() {
        if (timeSinceLastEvent < minTimeBetweenEvents) {
            return false; // Too soon
        }
        
        if (timeSinceLastEvent > maxTimeBetweenEvents) {
            timeSinceLastEvent = 0;
            return true; // Guaranteed spawn
        }
        
        // Probability-based spawn
        if (random.nextFloat() < spawnProbability) {
            timeSinceLastEvent = 0;
            return true;
        }
        
        return false;
    }
    
    /**
     * Generate a random threat event.
     * 
     * @return New ThreatEvent
     */
    public ThreatEvent generateRandomEvent() {
        EventType[] types = EventType.values();
        EventType type = types[random.nextInt(types.length)];
        
        return createEvent(type);
    }
    
    /**
     * Create a specific threat event.
     * 
     * @param type Type of event to create
     * @return ThreatEvent
     */
    public ThreatEvent createEvent(EventType type) {
        switch (type) {
            case PHISHING_ATTACK:
                return new ThreatEvent(
                    type,
                    "Phishing Attack!",
                    "You received a fake email trying to steal your credentials. Did your security stop it?",
                    SecurityComponentType.PASSWORD,
                    SecurityComponentType.TWO_FA
                );
                
            case DEVICE_LOST:
                return new ThreatEvent(
                    type,
                    "Device Lost!",
                    "Your phone was lost or stolen! Can you recover your account?",
                    SecurityComponentType.TWO_FA,
                    SecurityComponentType.RECOVERY
                );
                
            case DATA_BREACH:
                return new ThreatEvent(
                    type,
                    "Data Breach!",
                    "A service you use was breached. Your password may be compromised!",
                    SecurityComponentType.PASSWORD,
                    SecurityComponentType.TWO_FA,
                    SecurityComponentType.PRIVACY
                );
                
            case MALWARE_INFECTION:
                return new ThreatEvent(
                    type,
                    "Malware Detected!",
                    "Malicious software tried to infiltrate your system!",
                    SecurityComponentType.UPDATES,
                    SecurityComponentType.PRIVACY
                );
                
            case SIM_SWAP_ATTACK:
                return new ThreatEvent(
                    type,
                    "SIM Swap Attack!",
                    "Attacker tried to take over your phone number for SMS 2FA bypass!",
                    SecurityComponentType.TWO_FA,
                    SecurityComponentType.RECOVERY
                );
                
            case SOCIAL_ENGINEERING:
                return new ThreatEvent(
                    type,
                    "Social Engineering!",
                    "Someone tried to trick you into revealing personal information!",
                    SecurityComponentType.PRIVACY,
                    SecurityComponentType.TWO_FA
                );
                
            case UNPATCHED_VULNERABILITY:
                return new ThreatEvent(
                    type,
                    "Zero-Day Exploit!",
                    "A new software vulnerability was discovered and exploited!",
                    SecurityComponentType.UPDATES,
                    SecurityComponentType.PRIVACY
                );
                
            default:
                return generateRandomEvent();
        }
    }
    
    /**
     * Reset the timer.
     */
    public void reset() {
        timeSinceLastEvent = 0;
    }
    
    public float getTimeSinceLastEvent() {
        return timeSinceLastEvent;
    }
}
