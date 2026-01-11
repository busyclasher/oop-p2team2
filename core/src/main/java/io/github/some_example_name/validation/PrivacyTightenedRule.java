package io.github.some_example_name.validation;

import io.github.some_example_name.model.*;

/**
 * Rule requiring tight privacy settings.
 * Part of the Strategy pattern for validation rules.
 */
public class PrivacyTightenedRule implements Rule {
    
    @Override
    public ValidationResult validate(SecureAccount account) {
        SecurityComponent privacyComp = account.getComponent(SecurityComponentType.PRIVACY);
        
        if (privacyComp == null) {
            return ValidationResult.fail(
                "Privacy settings not configured!",
                -30,
                "Privacy settings control who can see your information. Open settings expose you to targeted attacks, identity theft, and data mining."
            );
        }
        
        PrivacyComponent privacy = (PrivacyComponent) privacyComp;
        
        if (privacy.getLevel() == PrivacyLevel.OPEN) {
            return ValidationResult.fail(
                "Privacy settings are too open!",
                -30,
                "Public profiles expose personal information that attackers use for social engineering and phishing. Tighten your privacy settings to share only what's necessary."
            );
        } else if (privacy.getLevel() == PrivacyLevel.MODERATE) {
            return ValidationResult.pass("Moderate privacy settings. Consider tightening further.", 35);
        } else {
            return ValidationResult.pass("Tight privacy settings! Your data is well protected.", 50);
        }
    }
    
    @Override
    public String getRuleName() {
        return "Privacy Settings Requirement";
    }
    
    @Override
    public String getEducationalTip() {
        return "Tight privacy limits data exposure. Attackers use public information for targeted phishing attacks. Share only what's absolutely necessary with the minimum audience.";
    }
}
