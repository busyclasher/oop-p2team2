package sg.edu.sit.inf1009.p2team2.game.quiz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Pool of cybersecurity-themed MCQ questions for Silicon Sentinel.
 *
 * Questions are drawn at random without immediate repetition to keep the
 * educational experience fresh across a play session.
 */
public class QuizBank {

    private final List<QuizQuestion> questions;
    private final List<QuizQuestion> remaining;
    private final Random             random;

    public QuizBank() {
        this.questions = new ArrayList<>();
        this.remaining = new ArrayList<>();
        this.random    = new Random();
        populate();
        refill();
    }

    /** Returns a random question, cycling through the whole pool before repeating. */
    public QuizQuestion getRandomQuestion() {
        if (remaining.isEmpty()) {
            refill();
        }
        return remaining.remove(random.nextInt(remaining.size()));
    }

    public int totalQuestions() { return questions.size(); }

    // ──────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ──────────────────────────────────────────────────────────────────────────

    private void refill() {
        remaining.clear();
        remaining.addAll(questions);
        Collections.shuffle(remaining, random);
    }

    private void add(String question, String[] options, int correctIndex) {
        questions.add(new QuizQuestion(question, options, correctIndex));
    }

    /** Populates the bank with cybersecurity awareness questions. */
    private void populate() {
        add("What does 'phishing' mean?",
            new String[]{
                "A fishing simulation game",
                "Stealing credentials via fake messages",
                "A network speed test",
                "Encrypting your own files"
            }, 1);

        add("What is two-factor authentication (2FA)?",
            new String[]{
                "A second password",
                "Using two devices to log in",
                "An extra verification step beyond your password",
                "Logging in twice"
            }, 2);

        add("What is ransomware?",
            new String[]{
                "A game about hackers",
                "Software that locks your files and demands payment",
                "A firewall type",
                "An antivirus program"
            }, 1);

        add("Which of the following is a strong password?",
            new String[]{
                "password123",
                "myname1990",
                "T!g3r#9kL$2z",
                "qwerty"
            }, 2);

        add("What does a VPN do?",
            new String[]{
                "Speeds up your internet",
                "Encrypts your internet traffic and hides your IP",
                "Removes viruses",
                "Backs up your files"
            }, 1);

        add("What is malware?",
            new String[]{
                "A healthy email",
                "Software designed to damage or infiltrate systems",
                "A type of hardware",
                "A secure browser"
            }, 1);

        add("Which of these is a sign of a phishing email?",
            new String[]{
                "Email from your known bank with your full name",
                "Urgent tone asking to click a suspicious link",
                "A newsletter you subscribed to",
                "An email with no attachments"
            }, 1);

        add("What is a firewall?",
            new String[]{
                "A physical wall in a server room",
                "A network security system that monitors traffic",
                "A type of virus",
                "A password manager"
            }, 1);

        add("What should you do if you receive an unexpected password reset email?",
            new String[]{
                "Click the link immediately",
                "Ignore it and never change your password",
                "Verify with the service through official channels first",
                "Forward it to friends"
            }, 2);

        add("What is a 'zero-day' vulnerability?",
            new String[]{
                "A vulnerability with no known fix yet",
                "A bug fixed within zero days",
                "A safe software update",
                "An expired security certificate"
            }, 0);

        add("What does HTTPS indicate in a website address?",
            new String[]{
                "The site is fast",
                "The site is popular",
                "The connection is encrypted",
                "The site is government-owned"
            }, 2);

        add("What is social engineering in cybersecurity?",
            new String[]{
                "Building software with social features",
                "Manipulating people into revealing confidential info",
                "A type of network topology",
                "A programming paradigm"
            }, 1);

        add("Which action best protects against malware?",
            new String[]{
                "Opening all email attachments",
                "Keeping software and OS updated",
                "Sharing your password securely",
                "Disabling your firewall for speed"
            }, 1);

        add("What is a rootkit?",
            new String[]{
                "A garden tool manager app",
                "Malware that hides itself deep within the OS",
                "A type of antivirus",
                "A password hashing algorithm"
            }, 1);

        add("What does spyware do?",
            new String[]{
                "Speeds up your computer",
                "Monitors and steals your activity without consent",
                "Protects your files",
                "Scans for viruses"
            }, 1);
    }
}
