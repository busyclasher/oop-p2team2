package io.github.some_example_name.validation;

import io.github.some_example_name.model.SecureAccount;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages and executes validation rules against SecureAccount objects.
 * Implements the Strategy pattern by managing a collection of Rule strategies.
 */
public class AccountValidator {
    
    private final List<Rule> rules;
    
    /**
     * Create a validator with default security rules.
     */
    public AccountValidator() {
        this.rules = new ArrayList<>();
        // Add default rules
        addRule(new StrongPasswordRule());
        addRule(new Require2FARule());
        addRule(new UpdatesEnabledRule());
        addRule(new RecoverySetRule());
        addRule(new PrivacyTightenedRule());
    }
    
    /**
     * Create a validator with custom rules.
     * 
     * @param rules List of rules to use
     */
    public AccountValidator(List<Rule> rules) {
        this.rules = new ArrayList<>(rules);
    }
    
    /**
     * Add a validation rule.
     * 
     * @param rule Rule to add
     */
    public void addRule(Rule rule) {
        if (rule != null && !rules.contains(rule)) {
            rules.add(rule);
        }
    }
    
    /**
     * Remove a validation rule.
     * 
     * @param rule Rule to remove
     */
    public void removeRule(Rule rule) {
        rules.remove(rule);
    }
    
    /**
     * Get all current rules.
     * 
     * @return List of rules
     */
    public List<Rule> getRules() {
        return new ArrayList<>(rules);
    }
    
    /**
     * Validate an account against all rules.
     * 
     * @param account Account to validate
     * @return List of validation results, one per rule
     */
    public List<ValidationResult> validateAccount(SecureAccount account) {
        List<ValidationResult> results = new ArrayList<>();
        
        for (Rule rule : rules) {
            ValidationResult result = rule.validate(account);
            results.add(result);
        }
        
        return results;
    }
    
    /**
     * Calculate total score impact from all rules.
     * 
     * @param account Account to validate
     * @return Total score from all validation results
     */
    public int calculateTotalScore(SecureAccount account) {
        List<ValidationResult> results = validateAccount(account);
        int total = 0;
        
        for (ValidationResult result : results) {
            total += result.getScoreImpact();
        }
        
        return total;
    }
    
    /**
     * Check if account passes all validation rules.
     * 
     * @param account Account to check
     * @return true if all rules pass
     */
    public boolean isValid(SecureAccount account) {
        List<ValidationResult> results = validateAccount(account);
        
        for (ValidationResult result : results) {
            if (!result.isValid()) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Get number of rules that passed.
     * 
     * @param account Account to validate
     * @return Count of passing rules
     */
    public int getPassedRuleCount(SecureAccount account) {
        List<ValidationResult> results = validateAccount(account);
        int count = 0;
        
        for (ValidationResult result : results) {
            if (result.isValid()) {
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * Get number of rules in this validator.
     * 
     * @return Rule count
     */
    public int getRuleCount() {
        return rules.size();
    }
}
