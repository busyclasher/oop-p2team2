package io.github.some_example_name;

import io.github.some_example_name.model.*;
import io.github.some_example_name.systems.*;
import io.github.some_example_name.command.*;
import io.github.some_example_name.validation.*;

/**
 * Central game state manager that ties all systems together.
 * Manages the game loop, scoring, threats, and player progress.
 */
public class GameSession {
    
    public enum GameState {
        MENU,
        PLAYING,
        PAUSED,
        LEVEL_COMPLETE,
        GAME_OVER
    }
    
    // Core systems
    private SecureAccount currentAccount;
    private final SecureAccountBuilder accountBuilder;
    private final ScoringSystem scoringSystem;
    private final RiskMeter riskMeter;
    private final ThreatEventManager threatManager;
    private final CommandHistory commandHistory;
    private final AccountValidator validator;
    
    // Game state
    private GameState state;
    private int currentLevel;
    private float gameTime;
    private float factDisplayTimer;
    private CybersecurityFact currentFact;
    private ThreatEvent activeThreat;
    private float threatDisplayTimer;
    
    // Configuration
    private static final float FACT_CHANGE_INTERVAL = 8f; // seconds
    private static final float THREAT_DISPLAY_DURATION = 5f; // seconds
    private static final int MAX_LEVELS = 5;
    
    public GameSession() {
        this.accountBuilder = new SecureAccountBuilder();
        this.scoringSystem = new ScoringSystem();
        this.riskMeter = new RiskMeter();
        this.threatManager = new ThreatEventManager();
        this.commandHistory = new CommandHistory();
        this.validator = new AccountValidator();
        
        this.state = GameState.MENU;
        this.currentLevel = 1;
        this.gameTime = 0;
        this.factDisplayTimer = 0;
        this.currentFact = CybersecurityFact.getRandomFact();
        this.activeThreat = null;
        this.threatDisplayTimer = 0;
        
        // Start with empty account
        this.currentAccount = new SecureAccount();
    }
    
    /**
     * Start a new game.
     */
    public void startGame() {
        state = GameState.PLAYING;
        currentLevel = 1;
        gameTime = 0;
        scoringSystem.reset();
        riskMeter.reset();
        threatManager.reset();
        commandHistory.clear();
        currentAccount = new SecureAccount();
        currentFact = CybersecurityFact.getRandomFact();
    }
    
    /**
     * Main update method - call each frame.
     * @param deltaTime Time since last frame in seconds
     */
    public void update(float deltaTime) {
        if (state != GameState.PLAYING) {
            return;
        }
        
        gameTime += deltaTime;
        
        // Update fact display
        factDisplayTimer += deltaTime;
        if (factDisplayTimer >= FACT_CHANGE_INTERVAL) {
            factDisplayTimer = 0;
            currentFact = CybersecurityFact.getRandomFact();
        }
        
        // Update threat system
        threatManager.update(deltaTime);
        
        // Handle active threat display
        if (activeThreat != null) {
            threatDisplayTimer += deltaTime;
            if (threatDisplayTimer >= THREAT_DISPLAY_DURATION) {
                activeThreat = null;
                threatDisplayTimer = 0;
            }
        }
        
        // Check for new threat events
        if (activeThreat == null && threatManager.shouldSpawnEvent()) {
            spawnThreatEvent();
        }
        
        // Update risk calculation
        riskMeter.calculateRisk(currentAccount);
        
        // Check for game over (risk too high)
        if (riskMeter.getCurrentRisk() >= 100) {
            state = GameState.GAME_OVER;
        }
    }
    
    /**
     * Spawn a new threat event.
     */
    private void spawnThreatEvent() {
        activeThreat = threatManager.generateRandomEvent();
        threatDisplayTimer = 0;
        
        // Calculate and apply impact
        int impact = activeThreat.calculateImpact(currentAccount);
        scoringSystem.applyPenalty(impact);
        riskMeter.addRisk(impact / 10); // Add risk based on damage
    }
    
    /**
     * Add a security component to the account.
     * @param component Component to add
     */
    public void addComponent(SecurityComponent component) {
        AddComponentCommand cmd = new AddComponentCommand(currentAccount, component);
        commandHistory.executeCommand(cmd);
        riskMeter.calculateRisk(currentAccount);
    }
    
    /**
     * Remove a security component from the account.
     * @param type Type of component to remove
     */
    public void removeComponent(SecurityComponentType type) {
        SecurityComponent existing = currentAccount.getComponent(type);
        if (existing != null) {
            RemoveComponentCommand cmd = new RemoveComponentCommand(currentAccount, type);
            commandHistory.executeCommand(cmd);
            riskMeter.calculateRisk(currentAccount);
        }
    }
    
    /**
     * Submit the current account build for scoring.
     * @return Score earned
     */
    public int submitBuild() {
        int score = scoringSystem.submitBuild(currentAccount);
        
        // Check if level is complete (all components + validation passed)
        if (currentAccount.isComplete() && validator.isValid(currentAccount)) {
            if (currentLevel >= MAX_LEVELS) {
                state = GameState.GAME_OVER; // Win!
            } else {
                state = GameState.LEVEL_COMPLETE;
            }
        }
        
        return score;
    }
    
    /**
     * Advance to the next level.
     */
    public void nextLevel() {
        currentLevel++;
        currentAccount = new SecureAccount();
        commandHistory.clear();
        state = GameState.PLAYING;
    }
    
    /**
     * Undo the last action.
     * @return true if undo was performed
     */
    public boolean undo() {
        boolean result = commandHistory.undo();
        if (result) {
            riskMeter.calculateRisk(currentAccount);
        }
        return result;
    }
    
    /**
     * Redo the last undone action.
     * @return true if redo was performed
     */
    public boolean redo() {
        boolean result = commandHistory.redo();
        if (result) {
            riskMeter.calculateRisk(currentAccount);
        }
        return result;
    }
    
    // Getters
    public GameState getState() { return state; }
    public void setState(GameState state) { this.state = state; }
    public int getCurrentLevel() { return currentLevel; }
    public float getGameTime() { return gameTime; }
    public SecureAccount getCurrentAccount() { return currentAccount; }
    public ScoringSystem getScoringSystem() { return scoringSystem; }
    public RiskMeter getRiskMeter() { return riskMeter; }
    public CybersecurityFact getCurrentFact() { return currentFact; }
    public ThreatEvent getActiveThreat() { return activeThreat; }
    public CommandHistory getCommandHistory() { return commandHistory; }
    public AccountValidator getValidator() { return validator; }
    
    public int getTotalScore() { return scoringSystem.getTotalScore(); }
    public int getComboMultiplier() { return scoringSystem.getComboMultiplier(); }
    public int getRiskLevel() { return riskMeter.getCurrentRisk(); }
    
    public String getFormattedTime() {
        int minutes = (int) (gameTime / 60);
        int seconds = (int) (gameTime % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }
}
