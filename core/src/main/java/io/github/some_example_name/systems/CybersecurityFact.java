package io.github.some_example_name.systems;

import io.github.some_example_name.model.SecurityComponentType;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Manages educational cybersecurity facts and tips displayed during gameplay.
 * Provides contextual learning moments to enhance player knowledge.
 */
public class CybersecurityFact {
    
    private final String fact;
    private final SecurityComponentType category;
    
    private static final Random random = new Random();
    private static final List<CybersecurityFact> ALL_FACTS = new ArrayList<>();
    
    static {
        // Password facts
        ALL_FACTS.add(new CybersecurityFact(
            "A 12-character password is 62 trillion times harder to crack than a 6-character one!",
            SecurityComponentType.PASSWORD
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "Using a password manager is safer than reusing passwords across sites.",
            SecurityComponentType.PASSWORD
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "'123456' is still the most common password. Don't be that person!",
            SecurityComponentType.PASSWORD
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "Passphrases like 'correct-horse-battery-staple' are both strong and memorable!",
            SecurityComponentType.PASSWORD
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "Brute force attacks can crack an 8-character password in under an hour!",
            SecurityComponentType.PASSWORD
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "Adding just one special character increases password strength by 10x!",
            SecurityComponentType.PASSWORD
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "Never use personal info like birthdays or pet names in passwords.",
            SecurityComponentType.PASSWORD
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "The average person has 100+ online accounts requiring passwords!",
            SecurityComponentType.PASSWORD
        ));
        
        // 2FA facts
        ALL_FACTS.add(new CybersecurityFact(
            "Two-factor authentication blocks 99.9% of automated attacks!",
            SecurityComponentType.TWO_FA
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "Hardware security keys are the most secure form of 2FA available.",
            SecurityComponentType.TWO_FA
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "SMS 2FA is better than nothing, but authenticator apps are more secure.",
            SecurityComponentType.TWO_FA
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "SIM swap attacks can bypass SMS 2FA - consider using an authenticator app!",
            SecurityComponentType.TWO_FA
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "TOTP codes change every 30 seconds, making them hard to intercept!",
            SecurityComponentType.TWO_FA
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "Biometric 2FA (fingerprint/face) is convenient but can't be changed if compromised.",
            SecurityComponentType.TWO_FA
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "Always save backup codes when setting up 2FA - you'll need them!",
            SecurityComponentType.TWO_FA
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "Push notification 2FA can be vulnerable to 'MFA fatigue' attacks.",
            SecurityComponentType.TWO_FA
        ));
        
        // Update facts
        ALL_FACTS.add(new CybersecurityFact(
            "60% of data breaches involve unpatched vulnerabilities.",
            SecurityComponentType.UPDATES
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "Automatic updates protect you from zero-day exploits faster!",
            SecurityComponentType.UPDATES
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "The WannaCry ransomware exploited systems that hadn't updated for months.",
            SecurityComponentType.UPDATES
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "Critical security patches are released within 24 hours of discovery.",
            SecurityComponentType.UPDATES
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "Outdated software is responsible for 34% of successful cyberattacks.",
            SecurityComponentType.UPDATES
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "Browser updates often fix critical security holes - keep them current!",
            SecurityComponentType.UPDATES
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "IoT devices often go unpatched - update your smart home devices too!",
            SecurityComponentType.UPDATES
        ));
        
        // Recovery facts
        ALL_FACTS.add(new CybersecurityFact(
            "Backup codes should be stored in a secure location, not on your phone!",
            SecurityComponentType.RECOVERY
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "A trusted contact for recovery should be someone you trust with your data.",
            SecurityComponentType.RECOVERY
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "Recovery emails should be on a different provider than your main account.",
            SecurityComponentType.RECOVERY
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "Print your backup codes and store them in a safe or safety deposit box.",
            SecurityComponentType.RECOVERY
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "Security questions are often guessable - treat them as secondary passwords!",
            SecurityComponentType.RECOVERY
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "Test your recovery process before you actually need it!",
            SecurityComponentType.RECOVERY
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "Multiple recovery options give you flexibility when one method fails.",
            SecurityComponentType.RECOVERY
        ));
        
        // Privacy facts
        ALL_FACTS.add(new CybersecurityFact(
            "Public social media profiles make social engineering attacks easier.",
            SecurityComponentType.PRIVACY
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "Location data in photos can reveal your home address to strangers!",
            SecurityComponentType.PRIVACY
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "Limiting third-party app access reduces your attack surface.",
            SecurityComponentType.PRIVACY
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "Regular privacy audits help you spot unauthorized access to your accounts.",
            SecurityComponentType.PRIVACY
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "Data brokers collect info from public profiles - limit what you share!",
            SecurityComponentType.PRIVACY
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "Review app permissions regularly - many request more access than needed.",
            SecurityComponentType.PRIVACY
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "Your email address is often used to link your accounts across services.",
            SecurityComponentType.PRIVACY
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "Private browsing doesn't hide your activity from your ISP or employer.",
            SecurityComponentType.PRIVACY
        ));
        ALL_FACTS.add(new CybersecurityFact(
            "Metadata in documents can reveal your name, location, and software used.",
            SecurityComponentType.PRIVACY
        ));
    }
    
    public CybersecurityFact(String fact, SecurityComponentType category) {
        this.fact = fact;
        this.category = category;
    }
    
    public String getFact() {
        return fact;
    }
    
    public SecurityComponentType getCategory() {
        return category;
    }
    
    /**
     * Get a random cybersecurity fact.
     * @return Random fact
     */
    public static CybersecurityFact getRandomFact() {
        return ALL_FACTS.get(random.nextInt(ALL_FACTS.size()));
    }
    
    /**
     * Get a random fact for a specific category.
     * @param category The security component type
     * @return Random fact for that category, or any random fact if none match
     */
    public static CybersecurityFact getRandomFactByCategory(SecurityComponentType category) {
        List<CybersecurityFact> categoryFacts = new ArrayList<>();
        for (CybersecurityFact fact : ALL_FACTS) {
            if (fact.getCategory() == category) {
                categoryFacts.add(fact);
            }
        }
        
        if (categoryFacts.isEmpty()) {
            return getRandomFact();
        }
        
        return categoryFacts.get(random.nextInt(categoryFacts.size()));
    }
    
    /**
     * Get all available facts.
     * @return List of all facts
     */
    public static List<CybersecurityFact> getAllFacts() {
        return new ArrayList<>(ALL_FACTS);
    }
    
    /**
     * Get the total number of facts available.
     * @return Fact count
     */
    public static int getFactCount() {
        return ALL_FACTS.size();
    }
    
    @Override
    public String toString() {
        return "[" + category.getDisplayName() + "] " + fact;
    }
}
