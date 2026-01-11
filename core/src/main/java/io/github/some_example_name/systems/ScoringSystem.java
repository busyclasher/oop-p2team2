package io.github.some_example_name.systems;

import io.github.some_example_name.model.SecureAccount;
import io.github.some_example_name.validation.AccountValidator;
import io.github.some_example_name.validation.ValidationResult;
import java.util.List;

/**
 * Manages player scoring with combo multiplier system.
 * Rewards consecutive successful builds with increasing multipliers.
 */
public class ScoringSystem {
    
    private int totalScore;
    private int comboMultiplier;
    private int consecutiveGoodBuilds;
    private final AccountValidator validator;
    
    public ScoringSystem() {
        this.totalScore = 0;
        this.comboMultiplier = 1;
        this.consecutiveGoodBuilds = 0;
        this.validator = new AccountValidator();
    }
    
    /**
     * Calculate score for an account build.
     * Uses base score from components + validation bonuses/penalties.
     * Applies combo multiplier to final score.
     * 
     * @param account The account to score
     * @return Score for this build
     */
    public int calculateScore(SecureAccount account) {
        if (account == null) {
            return 0;
        }
        
        // Base score from components
        int baseScore = account.getTotalSecurityScore();
        
        // Add validation impacts
        List<ValidationResult> results = validator.validateAccount(account);
        for (ValidationResult result : results) {
            baseScore += result.getScoreImpact();
        }
        
        // Apply combo multiplier (minimum 0)
        int finalScore = Math.max(0, baseScore * comboMultiplier);
        
        return finalScore;
    }
    
    /**
     * Add points to total score.
     * 
     * @param points Points to add
     */
    public void addScore(int points) {
        totalScore += points;
        totalScore = Math.max(0, totalScore); // Don't go negative
    }
    
    /**
     * Submit an account build and update combo based on performance.
     * 
     * @param account Account that was built
     * @return Score earned for this build
     */
    public int submitBuild(SecureAccount account) {
        int score = calculateScore(account);
        addScore(score);
        
        // Check if this was a "good" build (passed all validation)
        boolean isGoodBuild = validator.isValid(account);
        
        if (isGoodBuild) {
            incrementCombo();
        } else {
            resetCombo();
        }
        
        return score;
    }
    
    /**
     * Increment combo multiplier for consecutive good builds.
     */
    public void incrementCombo() {
        consecutiveGoodBuilds++;
        
        // Increase multiplier every good build, cap at 5x
        if (consecutiveGoodBuilds >= 5) {
            comboMultiplier = 5;
        } else if (consecutiveGoodBuilds >= 3) {
            comboMultiplier = 3;
        } else if (consecutiveGoodBuilds >= 2) {
            comboMultiplier = 2;
        }
    }
    
    /**
     * Reset combo multiplier to 1x.
     */
    public void resetCombo() {
        comboMultiplier = 1;
        consecutiveGoodBuilds = 0;
    }
    
    /**
     * Apply a penalty (threat events, mistakes).
     * 
     * @param penalty Penalty amount (positive number)
     */
    public void applyPenalty(int penalty) {
        totalScore -= penalty;
        totalScore = Math.max(0, totalScore);
    }
    
    /**
     * Apply a bonus (achievements, perfects).
     * 
     * @param bonus Bonus amount
     */
    public void addBonus(int bonus) {
        totalScore += bonus;
    }
    
    public int getTotalScore() {
        return totalScore;
    }
    
    public int getComboMultiplier() {
        return comboMultiplier;
    }
    
    public int getConsecutiveGoodBuilds() {
        return consecutiveGoodBuilds;
    }
    
    /**
     * Reset all scoring to initial state.
     */
    public void reset() {
        totalScore = 0;
        comboMultiplier = 1;
        consecutiveGoodBuilds = 0;
    }
    
    @Override
    public String toString() {
        return String.format("Score: %d | Combo: %dx | Streak: %d", 
            totalScore, comboMultiplier, consecutiveGoodBuilds);
    }
}
