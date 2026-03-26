package sg.edu.sit.inf1009.p2team2.game.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import java.util.ArrayList;
import java.util.List;
import sg.edu.sit.inf1009.p2team2.game.entities.CharacterType;
import sg.edu.sit.inf1009.p2team2.game.entities.EntityType;

/**
 * Persists resumable in-progress runs for the game layer.
 *
 * A saved run is gameplay state rather than engine configuration, so it is
 * intentionally kept outside the abstract engine layer.
 */
public final class RunSaveManager {

    private static final String PREFS_NAME = "cyberscouts-run-save";
    private static final String KEY_HAS_SAVE = "has_save";

    private RunSaveManager() {
    }

    public static boolean hasSavedRun() {
        return prefs().getBoolean(KEY_HAS_SAVE, false);
    }

    public static void clear() {
        Preferences prefs = prefs();
        prefs.clear();
        prefs.flush();
    }

    public static void save(RunSnapshot snapshot) {
        if (snapshot == null || snapshot.characterType == null) {
            clear();
            return;
        }

        Preferences prefs = prefs();
        prefs.putBoolean(KEY_HAS_SAVE, true);
        prefs.putString("character_type", snapshot.characterType.name());
        prefs.putInteger("score", snapshot.score);
        prefs.putInteger("good_collected", snapshot.goodCollected);
        prefs.putInteger("total_good_collected", snapshot.totalGoodCollected);
        prefs.putFloat("round_timer", snapshot.roundTimer);
        prefs.putFloat("spawn_timer", snapshot.spawnTimer);
        prefs.putFloat("spawn_interval", snapshot.spawnInterval);
        prefs.putFloat("fall_speed", snapshot.fallSpeed);
        prefs.putFloat("difficulty_timer", snapshot.difficultyTimer);
        prefs.putBoolean("frenzy_active", snapshot.frenzyActive);
        prefs.putFloat("frenzy_timer", snapshot.frenzyTimer);
        prefs.putFloat("frenzy_diff_timer", snapshot.frenzyDiffTimer);
        prefs.putInteger("frenzy_count", snapshot.frenzyCount);
        prefs.putFloat("frenzy_orb_timer", snapshot.frenzyOrbTimer);
        prefs.putBoolean("frenzy_orb_spawned", snapshot.frenzyOrbSpawned);
        prefs.putFloat("pre_frenzy_fall_speed", snapshot.preFrenzyFallSpeed);
        prefs.putFloat("pre_frenzy_spawn_interval", snapshot.preFrenzySpawnInterval);
        prefs.putFloat("player_x", snapshot.playerX);
        prefs.putFloat("player_y", snapshot.playerY);
        prefs.putFloat("player_velocity_y", snapshot.playerVelocityY);
        prefs.putBoolean("player_on_ground", snapshot.playerOnGround);
        prefs.putInteger("current_lives", snapshot.currentLives);
        prefs.putInteger("max_lives", snapshot.maxLives);
        prefs.putInteger("next_buff_score", snapshot.nextBuffScore);
        prefs.putBoolean("has_revive_shield", snapshot.hasShield);
        prefs.putBoolean("has_bonus_life_shield", snapshot.bonusLifeShieldActive);
        prefs.putFloat("player_speed_bonus", snapshot.playerSpeedBonus);

        prefs.putInteger("falling_entity_count", snapshot.fallingEntities.size());
        for (int i = 0; i < snapshot.fallingEntities.size(); i++) {
            FallingEntitySnapshot entity = snapshot.fallingEntities.get(i);
            prefs.putString("falling_type_" + i, entity.type.name());
            prefs.putFloat("falling_x_" + i, entity.x);
            prefs.putFloat("falling_y_" + i, entity.y);
            prefs.putFloat("falling_speed_" + i, entity.speed);
        }

        prefs.flush();
    }

    public static RunSnapshot load() {
        Preferences prefs = prefs();
        if (!prefs.getBoolean(KEY_HAS_SAVE, false)) {
            return null;
        }

        try {
            RunSnapshot snapshot = new RunSnapshot();
            snapshot.characterType = CharacterType.valueOf(
                prefs.getString("character_type", CharacterType.SPECTER.name()));
            snapshot.score = prefs.getInteger("score", 0);
            snapshot.goodCollected = prefs.getInteger("good_collected", 0);
            snapshot.totalGoodCollected = prefs.getInteger("total_good_collected", 0);
            snapshot.roundTimer = prefs.getFloat("round_timer", 60f);
            snapshot.spawnTimer = prefs.getFloat("spawn_timer", 1.4f);
            snapshot.spawnInterval = prefs.getFloat("spawn_interval", 1.4f);
            snapshot.fallSpeed = prefs.getFloat("fall_speed", 200f);
            snapshot.difficultyTimer = prefs.getFloat("difficulty_timer", 0f);
            snapshot.frenzyActive = prefs.getBoolean("frenzy_active", false);
            snapshot.frenzyTimer = prefs.getFloat("frenzy_timer", 0f);
            snapshot.frenzyDiffTimer = prefs.getFloat("frenzy_diff_timer", 0f);
            snapshot.frenzyCount = prefs.getInteger("frenzy_count", 0);
            snapshot.frenzyOrbTimer = prefs.getFloat("frenzy_orb_timer", 20f);
            snapshot.frenzyOrbSpawned = prefs.getBoolean("frenzy_orb_spawned", false);
            snapshot.preFrenzyFallSpeed = prefs.getFloat("pre_frenzy_fall_speed", 0f);
            snapshot.preFrenzySpawnInterval = prefs.getFloat("pre_frenzy_spawn_interval", 0f);
            snapshot.playerX = prefs.getFloat("player_x", 0f);
            snapshot.playerY = prefs.getFloat("player_y", 0f);
            snapshot.playerVelocityY = prefs.getFloat("player_velocity_y", 0f);
            snapshot.playerOnGround = prefs.getBoolean("player_on_ground", true);
            snapshot.currentLives = prefs.getInteger("current_lives", snapshot.characterType.getLives());
            snapshot.maxLives = prefs.getInteger("max_lives", snapshot.characterType.getLives());
            snapshot.nextBuffScore = prefs.getInteger("next_buff_score", 200);
            snapshot.hasShield = prefs.getBoolean("has_revive_shield", false);
            snapshot.bonusLifeShieldActive = prefs.getBoolean("has_bonus_life_shield", false);
            snapshot.playerSpeedBonus = prefs.getFloat("player_speed_bonus", 0f);

            int count = prefs.getInteger("falling_entity_count", 0);
            snapshot.fallingEntities.clear();
            for (int i = 0; i < count; i++) {
                FallingEntitySnapshot entity = new FallingEntitySnapshot();
                entity.type = EntityType.valueOf(
                    prefs.getString("falling_type_" + i, EntityType.GOOD_BYTE.name()));
                entity.x = prefs.getFloat("falling_x_" + i, 0f);
                entity.y = prefs.getFloat("falling_y_" + i, 0f);
                entity.speed = prefs.getFloat("falling_speed_" + i, 0f);
                snapshot.fallingEntities.add(entity);
            }

            return snapshot;
        } catch (Exception ex) {
            clear();
            return null;
        }
    }

    private static Preferences prefs() {
        return Gdx.app.getPreferences(PREFS_NAME);
    }

    public static final class RunSnapshot {
        public CharacterType characterType;
        public int score;
        public int goodCollected;
        public int totalGoodCollected;
        public float roundTimer;
        public float spawnTimer;
        public float spawnInterval;
        public float fallSpeed;
        public float difficultyTimer;
        public boolean frenzyActive;
        public float frenzyTimer;
        public float frenzyDiffTimer;
        public int frenzyCount;
        public float frenzyOrbTimer;
        public boolean frenzyOrbSpawned;
        public float preFrenzyFallSpeed;
        public float preFrenzySpawnInterval;
        public float playerX;
        public float playerY;
        public float playerVelocityY;
        public boolean playerOnGround;
        public int currentLives;
        public int maxLives;
        public int nextBuffScore;
        public boolean hasShield;
        public boolean bonusLifeShieldActive;
        public float playerSpeedBonus;
        public final List<FallingEntitySnapshot> fallingEntities = new ArrayList<>();
    }

    public static final class FallingEntitySnapshot {
        public EntityType type;
        public float x;
        public float y;
        public float speed;

        public FallingEntitySnapshot() {
        }

        public FallingEntitySnapshot(EntityType type, float x, float y, float speed) {
            this.type = type;
            this.x = x;
            this.y = y;
            this.speed = speed;
        }
    }
}
