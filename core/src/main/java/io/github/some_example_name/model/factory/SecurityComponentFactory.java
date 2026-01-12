package io.github.some_example_name.model.factory;

import io.github.some_example_name.model.*;

/**
 * Factory class for creating SecurityComponent instances.
 * Implements the Factory Method pattern to encapsulate component creation logic.
 * Eliminates verbose switch statements and centralizes component instantiation.
 */
public class SecurityComponentFactory {
    
    /**
     * Create the next variant of a component based on current account state.
     * Cycles through available options for the given component type.
     * 
     * @param type Component type to create
     * @param account Current account (to check existing component)
     * @return Next component variant, or first variant if none exists
     */
    public SecurityComponent createNextComponent(SecurityComponentType type, SecureAccount account) {
        if (type == null || account == null) {
            return null;
        }
        
        return switch (type) {
            case PASSWORD -> createNextPassword(account);
            case TWO_FA -> createNextTwoFA(account);
            case UPDATES -> createNextUpdates(account);
            case RECOVERY -> createNextRecovery(account);
            case PRIVACY -> createNextPrivacy(account);
        };
    }
    
    /**
     * Create a specific component by type and index.
     * Useful for direct component creation without cycling.
     * 
     * @param type Component type
     * @param index Index of the variant (0-based)
     * @return Component instance, or null if invalid index
     */
    public SecurityComponent createComponent(SecurityComponentType type, int index) {
        if (type == null || index < 0) {
            return null;
        }
        
        return switch (type) {
            case PASSWORD -> createPassword(index);
            case TWO_FA -> createTwoFA(index);
            case UPDATES -> createUpdates(index);
            case RECOVERY -> createRecovery(index);
            case PRIVACY -> createPrivacy(index);
        };
    }
    
    // ==================== Password Components ====================
    
    private SecurityComponent createNextPassword(SecureAccount account) {
        PasswordStrength[] strengths = PasswordStrength.values();
        PasswordComponent current = (PasswordComponent) account.getComponent(SecurityComponentType.PASSWORD);
        
        int nextIndex = (current == null) ? 0 : (current.getStrength().ordinal() + 1) % strengths.length;
        return new PasswordComponent(strengths[nextIndex]);
    }
    
    private SecurityComponent createPassword(int index) {
        PasswordStrength[] strengths = PasswordStrength.values();
        if (index >= strengths.length) return null;
        return new PasswordComponent(strengths[index]);
    }
    
    // ==================== 2FA Components ====================
    
    private SecurityComponent createNextTwoFA(SecureAccount account) {
        TwoFAType[] types = TwoFAType.values();
        TwoFAComponent current = (TwoFAComponent) account.getComponent(SecurityComponentType.TWO_FA);
        
        int nextIndex = (current == null) ? 0 : (current.getTwoFAType().ordinal() + 1) % types.length;
        return new TwoFAComponent(types[nextIndex]);
    }
    
    private SecurityComponent createTwoFA(int index) {
        TwoFAType[] types = TwoFAType.values();
        if (index >= types.length) return null;
        return new TwoFAComponent(types[index]);
    }
    
    // ==================== Update Components ====================
    
    private SecurityComponent createNextUpdates(SecureAccount account) {
        UpdateSetting[] settings = UpdateSetting.values();
        UpdateComponent current = (UpdateComponent) account.getComponent(SecurityComponentType.UPDATES);
        
        int nextIndex = (current == null) ? 0 : (current.getSetting().ordinal() + 1) % settings.length;
        return new UpdateComponent(settings[nextIndex]);
    }
    
    private SecurityComponent createUpdates(int index) {
        UpdateSetting[] settings = UpdateSetting.values();
        if (index >= settings.length) return null;
        return new UpdateComponent(settings[index]);
    }
    
    // ==================== Recovery Components ====================
    
    private SecurityComponent createNextRecovery(SecureAccount account) {
        RecoveryType[] types = RecoveryType.values();
        RecoveryComponent current = (RecoveryComponent) account.getComponent(SecurityComponentType.RECOVERY);
        
        int nextIndex = (current == null) ? 0 : (current.getRecoveryType().ordinal() + 1) % types.length;
        return new RecoveryComponent(types[nextIndex]);
    }
    
    private SecurityComponent createRecovery(int index) {
        RecoveryType[] types = RecoveryType.values();
        if (index >= types.length) return null;
        return new RecoveryComponent(types[index]);
    }
    
    // ==================== Privacy Components ====================
    
    private SecurityComponent createNextPrivacy(SecureAccount account) {
        PrivacyLevel[] levels = PrivacyLevel.values();
        PrivacyComponent current = (PrivacyComponent) account.getComponent(SecurityComponentType.PRIVACY);
        
        int nextIndex = (current == null) ? 0 : (current.getLevel().ordinal() + 1) % levels.length;
        return new PrivacyComponent(levels[nextIndex]);
    }
    
    private SecurityComponent createPrivacy(int index) {
        PrivacyLevel[] levels = PrivacyLevel.values();
        if (index >= levels.length) return null;
        return new PrivacyComponent(levels[index]);
    }
    
    /**
     * Get the number of variants available for a component type.
     * 
     * @param type Component type
     * @return Number of variants
     */
    public int getVariantCount(SecurityComponentType type) {
        if (type == null) return 0;
        
        return switch (type) {
            case PASSWORD -> PasswordStrength.values().length;
            case TWO_FA -> TwoFAType.values().length;
            case UPDATES -> UpdateSetting.values().length;
            case RECOVERY -> RecoveryType.values().length;
            case PRIVACY -> PrivacyLevel.values().length;
        };
    }
}
