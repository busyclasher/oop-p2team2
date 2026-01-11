package io.github.some_example_name.model;

/**
 * Builder pattern implementation for constructing SecureAccount objects.
 * Provides a fluent API for step-by-step account construction with method chaining.
 * 
 * Example usage:
 * <pre>
 * SecureAccount account = new SecureAccountBuilder()
 *     .withPassword(PasswordStrength.STRONG)
 *     .withTwoFA(TwoFAType.AUTHENTICATOR)
 *     .withUpdates(UpdateSetting.AUTO)
 *     .withRecovery(RecoveryType.BACKUP_CODES)
 *     .withPrivacy(PrivacyLevel.TIGHT)
 *     .build();
 * </pre>
 */
public class SecureAccountBuilder {
    
    private PasswordStrength password;
    private TwoFAType twoFA;
    private UpdateSetting updates;
    private RecoveryType recovery;
    private PrivacyLevel privacy;
    
    /**
     * Create a new builder instance.
     */
    public SecureAccountBuilder() {
        // Start with empty/default values
    }
    
    /**
     * Set the password strength.
     * 
     * @param strength Password strength level
     * @return This builder for method chaining
     */
    public SecureAccountBuilder withPassword(PasswordStrength strength) {
        this.password = strength;
        return this;
    }
    
    /**
     * Set the two-factor authentication type.
     * 
     * @param type 2FA type
     * @return This builder for method chaining
     */
    public SecureAccountBuilder withTwoFA(TwoFAType type) {
        this.twoFA = type;
        return this;
    }
    
    /**
     * Set the update settings.
     * 
     * @param setting Update configuration
     * @return This builder for method chaining
     */
    public SecureAccountBuilder withUpdates(UpdateSetting setting) {
        this.updates = setting;
        return this;
    }
    
    /**
     * Set the recovery method.
     * 
     * @param type Recovery type
     * @return This builder for method chaining
     */
    public SecureAccountBuilder withRecovery(RecoveryType type) {
        this.recovery = type;
        return this;
    }
    
    /**
     * Set the privacy level.
     * 
     * @param level Privacy configuration
     * @return This builder for method chaining
     */
    public SecureAccountBuilder withPrivacy(PrivacyLevel level) {
        this.privacy = level;
        return this;
    }
    
    /**
     * Build the SecureAccount with the configured components.
     * Only adds components that have been set (non-null).
     * 
     * @return A new SecureAccount instance
     */
    public SecureAccount build() {
        SecureAccount account = new SecureAccount();
        
        if (password != null) {
            account.addComponent(new PasswordComponent(password));
        }
        
        if (twoFA != null) {
            account.addComponent(new TwoFAComponent(twoFA));
        }
        
        if (updates != null) {
            account.addComponent(new UpdateComponent(updates));
        }
        
        if (recovery != null) {
            account.addComponent(new RecoveryComponent(recovery));
        }
        
        if (privacy != null) {
            account.addComponent(new PrivacyComponent(privacy));
        }
        
        return account;
    }
    
    /**
     * Reset all settings to null.
     * 
     * @return This builder for method chaining
     */
    public SecureAccountBuilder reset() {
        this.password = null;
        this.twoFA = null;
        this.updates = null;
        this.recovery = null;
        this.privacy = null;
        return this;
    }
}
