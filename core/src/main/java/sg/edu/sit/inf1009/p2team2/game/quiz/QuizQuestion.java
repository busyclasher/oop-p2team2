package sg.edu.sit.inf1009.p2team2.game.quiz;

/**
 * Immutable value object representing a single multiple-choice question.
 */
public class QuizQuestion {

    private final String   question;
    private final String[] options;        // exactly 4 options
    private final int      correctIndex;   // 0-based index into options[]

    public QuizQuestion(String question, String[] options, int correctIndex) {
        if (options == null || options.length != 4) {
            throw new IllegalArgumentException("QuizQuestion requires exactly 4 options.");
        }
        if (correctIndex < 0 || correctIndex > 3) {
            throw new IllegalArgumentException("correctIndex must be 0-3.");
        }
        this.question     = question;
        this.options      = options.clone();
        this.correctIndex = correctIndex;
    }

    public String   getQuestion()              { return question; }
    public String[] getOptions()               { return options.clone(); }
    public int      getCorrectIndex()          { return correctIndex; }
    public String   getCorrectAnswer()         { return options[correctIndex]; }

    public boolean isCorrect(int selectedIndex) {
        return selectedIndex == correctIndex;
    }
}
