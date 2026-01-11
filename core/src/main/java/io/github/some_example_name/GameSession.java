package io.github.some_example_name;

import io.github.some_example_name.model.*;
import io.github.some_example_name.systems.*;
import io.github.some_example_name.command.*;
import io.github.some_example_name.validation.*;
import io.github.some_example_name.observer.*;

/**
 * Central game state manager that ties all systems together.
 * Manages the game loop, scoring, threats, and player progress.
 * Extends GameEventNotifier to implement the Observer pattern.
 */
public class GameSession extends GameEventNotifier {
    
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
    private ThreatEventManager threatManager;
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
    private DifficultyLevel difficulty;
    
    // Cached values for Observer notifications
    private int lastScore;
    private int lastRisk;
    private int lastCombo;
    
    // Configuration
    private static final float FACT_CHANGE_INTERVAL = 8f; // seconds
    private static final float THREAT_DISPLAY_DURATION = 5f; // seconds
    private static final int MAX_LEVELS = 5;
    
    public GameSession() {
        this(DifficultyLevel.NORMAL);
    }
    
    public GameSession(DifficultyLevel difficulty) {
        this.difficulty = difficulty;
        this.accountBuilder = new SecureAccountBuilder();
        this.scoringSystem = new ScoringSystem();
        this.riskMeter = new RiskMeter();
        this.threatManager = createThreatManager(difficulty);
        this.commandHistory = new CommandHistory();
        this.validator = new AccountValidator();
        
        this.state = GameState.MENU;
        this.currentLevel = 1;
        this.gameTime = 0;
        this.factDisplayTimer = 0;
        this.currentFact = CybersecurityFact.getRandomFact();
        this.activeThreat = null;
        this.threatDisplayTimer = 0;
        
        this.lastScore = 0;
        this.lastRisk = 0;
        this.lastCombo = 1;
        
        // Start with empty account
        this.currentAccount = new SecureAccount();
    }
    
    /**
     * Create a threat manager configured for the given difficulty.
     */
    private ThreatEventManager createThreatManager(DifficultyLevel diff) {
        return new ThreatEventManager(
            diff.getMinTimeBetweenEvents(),
            diff.getMaxTimeBetweenEvents(),
            diff.getSpawnProbability()
        );
    }
    
    /**
     * Set the difficulty level. Can be called before starting a game.
     */
    public void setDifficulty(DifficultyLevel difficulty) {
        this.difficulty = difficulty;
        this.threatManager = createThreatManager(difficulty);
    }
    
    /**
     * Cycle to the next difficulty level.
     */
    public void cycleDifficulty() {
        setDifficulty(difficulty.next());
    }
    
    /**
     * Get the current difficulty level.
     */
    public DifficultyLevel getDifficulty() {
        return difficulty;
    }
    
    /**
     * Start a new game with current difficulty.
     */
    public void startGame() {
        state = GameState.PLAYING;
        currentLevel = 1;
        gameTime = 0;
        scoringSystem.reset();
        riskMeter.reset();
        threatManager = createThreatManager(difficulty);
        threatManager.reset();
        commandHistory.clear();
        currentAccount = new SecureAccount();
        currentFact = CybersecurityFact.getRandomFact();
        activeThreat = null;
        
        lastScore = 0;
        lastRisk = 0;
        lastCombo = 1;
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
        
        // Check for Observer notifications
        checkAndNotifyChanges();
        
        // Check for game over (risk too high)
        if (riskMeter.getCurrentRisk() >= 100) {
            state = GameState.GAME_OVER;
        }
    }
    
    /**
     * Check for changes and notify observers.
     */
    private void checkAndNotifyChanges() {
        int currentScore = scoringSystem.getTotalScore();
        int currentRisk = riskMeter.getCurrentRisk();
        int currentCombo = scoringSystem.getComboMultiplier();
        
        if (currentScore != lastScore) {
            notifyScoreChanged(lastScore, currentScore, currentScore - lastScore);
            lastScore = currentScore;
        }
        
        if (currentRisk != lastRisk) {
            notifyRiskChanged(lastRisk, currentRisk);
            lastRisk = currentRisk;
        }
        
        if (currentCombo != lastCombo) {
            notifyComboChanged(lastCombo, currentCombo);
            lastCombo = currentCombo;
        }
    }
    
    /**
     * Spawn a new threat event.
     */
    private void spawnThreatEvent() {
        activeThreat = threatManager.generateRandomEvent();
        threatDisplayTimer = 0;
        
        // Calculate and apply impact with difficulty multiplier
        int baseImpact = activeThreat.calculateImpact(currentAccount);
        int impact = (int)(baseImpact * difficulty.getThreatDamageMultiplier());
        
        scoringSystem.applyPenalty(impact);
        riskMeter.addRisk(impact / 10); // Add risk based on damage
        
        // Notify observers of threat
        notifyThreatTriggered(activeThreat.getName(), impact);
    }
    
    /**
     * Add a security component to the account.
     * @param component Component to add
     */
    public void addComponent(SecurityComponent component) {
        AddComponentCommand cmd = new AddComponentCommand(currentAccount, component);
        commandHistory.executeCommand(cmd);
        riskMeter.calculateRisk(currentAccount);
        
        // Notify observers of component change
        notifyComponentChanged(component.getType().getDisplayName(), component.getValue());
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
            
            // Notify observers
            notifyComponentChanged(type.getDisplayName(), "Removed");
        }
    }
    
    /**
     * Submit the current account build for scoring.
     * @return Score earned (with difficulty multiplier applied)
     */
    public int submitBuild() {
        int baseScore = scoringSystem.submitBuild(currentAccount);
        int finalScore = (int)(baseScore * difficulty.getScoreMultiplier());
        
        // Add bonus for difficulty multiplier
        if (difficulty.getScoreMultiplier() > 1.0f) {
            scoringSystem.addBonus(finalScore - baseScore);
        }
        
        // Check if level is complete (all components + validation passed)
        if (currentAccount.isComplete() && validator.isValid(currentAccount)) {
            if (currentLevel >= MAX_LEVELS) {
                state = GameState.GAME_OVER; // Win!
            } else {
                state = GameState.LEVEL_COMPLETE;
            }
        }
        
        return finalScore;
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
