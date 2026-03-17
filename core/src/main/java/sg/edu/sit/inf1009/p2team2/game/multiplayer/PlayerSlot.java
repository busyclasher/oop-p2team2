package sg.edu.sit.inf1009.p2team2.game.multiplayer;

import com.badlogic.gdx.Input;
import sg.edu.sit.inf1009.p2team2.engine.entity.Entity;
import sg.edu.sit.inf1009.p2team2.game.components.HealthComponent;
import sg.edu.sit.inf1009.p2team2.game.entities.CharacterType;

/**
 * Encapsulates per-player state for multi-player support.
 *
 * Each player slot holds its own entity, character choice, score, key
 * bindings, and health reference. In single-player mode only slot 0 is
 * used; adding a second slot enables cooperative/competitive play.
 *
 * Design decisions still needed (see requirements.md #10):
 * - Shared screen vs split-screen layout
 * - Cooperative (shared score) vs competitive (separate scores)
 * - Shared or independent life pools
 * - Leaderboard integration for two-player entries
 */
public class PlayerSlot {

    private final int           slotIndex;
    private final int           leftKey;
    private final int           rightKey;
    private final int           jumpKey;
    private CharacterType       characterType;
    private Entity              entity;
    private HealthComponent     health;
    private int                 score;
    private String              playerName;

    /** Slot 0 = Player 1 (A/D/Space), Slot 1 = Player 2 (Left/Right/Up). */
    public PlayerSlot(int slotIndex) {
        this.slotIndex = slotIndex;
        if (slotIndex == 0) {
            leftKey  = Input.Keys.A;
            rightKey = Input.Keys.D;
            jumpKey  = Input.Keys.SPACE;
        } else {
            leftKey  = Input.Keys.LEFT;
            rightKey = Input.Keys.RIGHT;
            jumpKey  = Input.Keys.UP;
        }
    }

    public int            getSlotIndex()    { return slotIndex; }
    public int            getLeftKey()      { return leftKey; }
    public int            getRightKey()     { return rightKey; }
    public int            getJumpKey()      { return jumpKey; }
    public CharacterType  getCharacterType(){ return characterType; }
    public Entity         getEntity()       { return entity; }
    public HealthComponent getHealth()      { return health; }
    public int            getScore()        { return score; }
    public String         getPlayerName()   { return playerName; }

    public void setCharacterType(CharacterType c) { this.characterType = c; }
    public void setEntity(Entity e)               { this.entity = e; }
    public void setHealth(HealthComponent h)      { this.health = h; }
    public void setScore(int s)                   { this.score = s; }
    public void addScore(int amount)              { this.score += amount; }
    public void setPlayerName(String name)        { this.playerName = name; }

    public void reset() {
        entity = null;
        health = null;
        score  = 0;
    }
}
