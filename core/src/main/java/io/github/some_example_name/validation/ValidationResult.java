package io.github.some_example_name.validation;

/**
 * Result of a validation rule check.
 * Contains whether validation passed, score impact, and feedback messages.
 */
public class ValidationResult {
    
    private final boolean valid;
    private final int scoreImpact;
    private final String message;
    private final String educationalTip;
    
    /**
     * Create a validation result.
     * 
     * @param valid Whether the rule passed
     * @param scoreImpact Points to add (positive) or deduct (negative)
     * @param message Feedback message
     * @param educationalTip Educational content explaining the rule
     */
    public ValidationResult(boolean valid, int scoreImpact, String message, String educationalTip) {
        this.valid = valid;
        this.scoreImpact = scoreImpact;
        this.message = message;
        this.educationalTip = educationalTip;
    }
    
    /**
     * Create a passing validation result.
     */
    public static ValidationResult pass(String message, int bonus) {
        return new ValidationResult(true, bonus, message, "");
    }
    
    /**
     * Create a failing validation result.
     */
    public static ValidationResult fail(String message, int penalty, String tip) {
        return new ValidationResult(false, penalty, message, tip);
    }
    
    public boolean isValid() {
        return valid;
    }
    
    public int getScoreImpact() {
        return scoreImpact;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getEducationalTip() {
        return educationalTip;
    }
    
    @Override
    public String toString() {
        return String.format("ValidationResult{valid=%s, score=%+d, message='%s'}", 
            valid, scoreImpact, message);
    }
}
