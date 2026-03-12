package sg.edu.sit.inf1009.p2team2.game.components;

import sg.edu.sit.inf1009.p2team2.engine.entity.ComponentAdapter;
import sg.edu.sit.inf1009.p2team2.game.entities.EntityType;

/**
 * ECS component that classifies a game entity and controls its gameplay behavior.
 *
 * Attached to every falling entity (good/bad) and the player.
 * The GamePlayScene reads this component to decide what happens on collision.
 */
public class GameEntityComponent implements ComponentAdapter {

    private final EntityType entityType;
    private final boolean bad;
    private final boolean quizTrigger;
    private final int scoreValue;
    private boolean collected;

    public GameEntityComponent(EntityType entityType) {
        this.entityType = entityType;
        this.bad         = entityType.isBad();
        this.quizTrigger = entityType.isQuizTrigger();
        this.scoreValue  = entityType.getScoreValue();
        this.collected   = false;
    }

    public EntityType getEntityType()  { return entityType; }
    public boolean    isBad()          { return bad; }
    public boolean    isQuizTrigger()  { return quizTrigger; }
    public int        getScoreValue()  { return scoreValue; }
    public boolean    isCollected()    { return collected; }
    public void       markCollected()  { this.collected = true; }
}
