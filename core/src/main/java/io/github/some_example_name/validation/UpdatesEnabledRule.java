package io.github.some_example_name.validation;

import io.github.some_example_name.model.*;

/**
 * Rule requiring automatic updates to be enabled.
 * Part of the Strategy pattern for validation rules.
 */
public class UpdatesEnabledRule implements Rule {
    
    @Override
    public ValidationResult validate(SecureAccount account) {
        SecurityComponent updateComp = account.getComponent(SecurityComponentType.UPDATES);
        
        if (updateComp == null) {
            return ValidationResult.fail(
                "Update settings not configured!",
                -40,
                "Software updates patch security vulnerabilities. Attackers actively exploit unpatched systems. Always keep software up-to-date."
            );
        }
        
        UpdateComponent updates = (UpdateComponent) updateComp;
        
        if (updates.getSetting() == UpdateSetting.OFF) {
            return ValidationResult.fail(
                "Updates are disabled!",
                -45,
                "Disabling updates leaves your system vulnerable to known exploits. The WannaCry ransomware attack affected 200,000+ computers that hadn't installed a critical update."
            );
        } else if (updates.getSetting() == UpdateSetting.MANUAL) {
            return ValidationResult.fail(
                "Manual updates can lead to delays",
                -15,
                "Manual updates rely on you remembering to check. Auto-updates ensure you're protected as soon as patches are available."
            );
        } else {
            return ValidationResult.pass("Automatic updates enabled! Excellent security hygiene.", 55);
        }
    }
    
    @Override
    public String getRuleName() {
        return "Automatic Updates Requirement";
    }
    
    @Override
    public String getEducationalTip() {
        return "Software updates patch security vulnerabilities discovered after release. Enable automatic updates to ensure you're always protected against the latest threats.";
    }
}
