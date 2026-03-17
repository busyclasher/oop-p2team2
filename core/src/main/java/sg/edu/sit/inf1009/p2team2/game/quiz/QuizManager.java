package sg.edu.sit.inf1009.p2team2.game.quiz;

import sg.edu.sit.inf1009.p2team2.engine.entity.Entity;
import sg.edu.sit.inf1009.p2team2.game.components.GameEntityComponent;

/**
 * Manages the quiz state for CyberScouts.
 *
 * When a quiz-trigger entity is caught/hit, the scene calls
 * {@link #triggerQuiz(Entity)} to start the overlay.  The player then
 * selects an answer (1–4) and the scene calls {@link #submitAnswer(int)}
 * to get the result and clear quiz state.
 *
 * Design pattern: State — the manager is either IDLE or ACTIVE.
 */
public class QuizManager {

    private final QuizBank     quizBank;
    private QuizQuestion       currentQuestion;
    private Entity             triggeringEntity;
    private boolean            active;

    public QuizManager(QuizBank quizBank) {
        this.quizBank  = quizBank;
        this.active    = false;
    }

    /**
     * Starts a quiz for the entity that triggered it.
     * Does nothing if a quiz is already active.
     */
    public void triggerQuiz(Entity entity) {
        if (active) return;
        currentQuestion  = quizBank.getRandomQuestion();
        triggeringEntity = entity;
        active           = true;
    }

    /**
     * Records the player's answer (0-based index) and deactivates the quiz.
     *
     * @param selectedIndex 0-based option index chosen by the player
     * @return {@link QuizResult#CORRECT} or {@link QuizResult#WRONG}
     */
    public QuizResult submitAnswer(int selectedIndex) {
        if (!active) {
            throw new IllegalStateException("No quiz is currently active.");
        }
        QuizResult result = currentQuestion.isCorrect(selectedIndex)
            ? QuizResult.CORRECT : QuizResult.WRONG;
        active = false;
        return result;
    }

    public boolean       isActive()           { return active; }
    public QuizQuestion  getCurrentQuestion()  { return currentQuestion; }
    public Entity        getTriggeringEntity() { return triggeringEntity; }

    /** True when the triggering entity is a bad (harmful) one. */
    public boolean isBadEntityQuiz() {
        if (triggeringEntity == null) return false;
        GameEntityComponent gec = triggeringEntity.get(GameEntityComponent.class);
        return gec != null && gec.isBad();
    }
}
