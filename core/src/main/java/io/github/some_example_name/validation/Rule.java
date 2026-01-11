package io.github.some_example_name.validation;

import io.github.some_example_name.model.SecureAccount;

/**
 * Interface for validation rules using the Strategy pattern.
 * Each rule encapsulates a specific security best practice check.
 * Rules can be easily added, removed, or modified without changing
 * the validation system (Open/Closed Principle).
 */
public interface Rule {
    
    /**
     * Validate the given account against this rule.
     * 
     * @param account The account to validate
     * @return ValidationResult containing pass/fail, score impact, and feedback
     */
    ValidationResult validate(SecureAccount account);
    
    /**
     * Get the name of this rule.
     * 
     * @return Human-readable rule name
     */
    String getRuleName();
    
    /**
     * Get educational content explaining why this rule is important.
     * 
     * @return Educational tip
     */
    String getEducationalTip();
}
