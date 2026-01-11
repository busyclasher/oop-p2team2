package io.github.some_example_name.validation;

import io.github.some_example_name.model.*;

/**
 * Rule requiring account recovery method to be set.
 * Part of the Strategy pattern for validation rules.
 */
public class RecoverySetRule implements Rule {
    
    @Override
    public ValidationResult validate(SecureAccount account) {
        SecurityComponent recoveryComp = account.getComponent(SecurityComponentType.RECOVERY);
        
        if (recoveryComp == null) {
            return ValidationResult.fail(
                "No recovery method configured!",
                -35,
                "Account recovery methods let you regain access if you forget your password or lose your 2FA device. Without recovery options, you could be permanently locked out."
            );
        }
        
        RecoveryComponent recovery = (RecoveryComponent) recoveryComp;
        
        if (recovery.getRecoveryType() == RecoveryType.NONE) {
            return ValidationResult.fail(
                "No recovery method set!",
                -35,
                "If you lose access to your account (forgotten password, lost 2FA device), you'll be permanently locked out. Always set up recovery options."
            );
        } else if (recovery.getRecoveryType() == RecoveryType.EMAIL || 
                   recovery.getRecoveryType() == RecoveryType.PHONE) {
            return ValidationResult.pass("Recovery method configured. Good!", 40);
        } else {
            return ValidationResult.pass("Backup codes saved! Best recovery practice.", 55);
        }
    }
    
    @Override
    public String getRuleName() {
        return "Recovery Method Requirement";
    }
    
    @Override
    public String getEducationalTip() {
        return "Recovery methods prevent permanent account lockout. Backup codes are best because they work even if your email or phone is compromised. Store them securely!";
    }
}
