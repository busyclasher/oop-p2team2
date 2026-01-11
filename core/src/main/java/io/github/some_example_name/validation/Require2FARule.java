package io.github.some_example_name.validation;

import io.github.some_example_name.model.*;

/**
 * Rule requiring two-factor authentication.
 * Part of the Strategy pattern for validation rules.
 */
public class Require2FARule implements Rule {
    
    @Override
    public ValidationResult validate(SecureAccount account) {
        SecurityComponent twoFAComp = account.getComponent(SecurityComponentType.TWO_FA);
        
        if (twoFAComp == null) {
            return ValidationResult.fail(
                "No 2FA configured!",
                -60,
                "Two-Factor Authentication (2FA) adds a second verification step beyond your password. Without it, anyone who gets your password can access your account."
            );
        }
        
        TwoFAComponent twoFA = (TwoFAComponent) twoFAComp;
        
        if (twoFA.getTwoFAType() == TwoFAType.NONE) {
            return ValidationResult.fail(
                "2FA is disabled!",
                -60,
                "Enable 2FA to protect against password theft. Even if someone steals your password, they won't be able to access your account without the second factor."
            );
        } else if (twoFA.getTwoFAType() == TwoFAType.SMS) {
            return ValidationResult.fail(
                "SMS-based 2FA is better than nothing, but not ideal",
                -10,
                "SMS 2FA can be intercepted through SIM swapping attacks. Use an authenticator app (like Google Authenticator or Authy) or hardware key for better security."
            );
        } else if (twoFA.getTwoFAType() == TwoFAType.AUTHENTICATOR) {
            return ValidationResult.pass("Authenticator app 2FA enabled! Great choice.", 60);
        } else {
            return ValidationResult.pass("Hardware key 2FA enabled! Best security available.", 80);
        }
    }
    
    @Override
    public String getRuleName() {
        return "Two-Factor Authentication Requirement";
    }
    
    @Override
    public String getEducationalTip() {
        return "2FA (Two-Factor Authentication) requires two forms of verification: something you know (password) and something you have (phone/key). Authenticator apps > SMS > Nothing.";
    }
}
