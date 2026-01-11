package io.github.some_example_name.validation;

import io.github.some_example_name.model.*;

/**
 * Rule requiring strong password.
 * Part of the Strategy pattern for validation rules.
 */
public class StrongPasswordRule implements Rule {
    
    @Override
    public ValidationResult validate(SecureAccount account) {
        SecurityComponent passwordComp = account.getComponent(SecurityComponentType.PASSWORD);
        
        if (passwordComp == null) {
            return ValidationResult.fail(
                "No password set!",
                -50,
                "A password is the most basic security requirement. Always use a strong password with at least 12 characters, including uppercase, lowercase, numbers, and symbols."
            );
        }
        
        PasswordComponent password = (PasswordComponent) passwordComp;
        
        if (password.getStrength() == PasswordStrength.STRONG) {
            return ValidationResult.pass("Strong password detected! Excellent security practice.", 50);
        } else if (password.getStrength() == PasswordStrength.MEDIUM) {
            return ValidationResult.fail(
                "Password could be stronger",
                -20,
                "Medium passwords provide some protection but can be cracked with enough time. Use a password manager to generate and store complex passwords."
            );
        } else {
            return ValidationResult.fail(
                "Weak password detected!",
                -40,
                "Weak passwords are the #1 cause of account breaches. Common passwords can be cracked in seconds. Always use unique, complex passwords for each account."
            );
        }
    }
    
    @Override
    public String getRuleName() {
        return "Strong Password Requirement";
    }
    
    @Override
    public String getEducationalTip() {
        return "Strong passwords are at least 12 characters long and combine uppercase, lowercase, numbers, and symbols. Never reuse passwords across accounts. Consider using a password manager.";
    }
}
