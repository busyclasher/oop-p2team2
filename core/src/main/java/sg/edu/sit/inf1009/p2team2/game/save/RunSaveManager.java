package sg.edu.sit.inf1009.p2team2.game.save;

import java.util.ArrayList;
import java.util.List;
import sg.edu.sit.inf1009.p2team2.engine.io.storage.StorageProvider;
import sg.edu.sit.inf1009.p2team2.engine.io.storage.StorageProviders;
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
    private static final StorageProvider STORAGE = StorageProviders.preferences(PREFS_NAME);

    private RunSaveManager() {
    }

    public static boolean hasSavedRun() {
        return STORAGE.getBoolean(KEY_HAS_SAVE, false);
    }

    public static void clear() {
        STORAGE.clear();
        STORAGE.flush();
    }

    public static void save(RunSnapshot snapshot) {
        if (snapshot == null || snapshot.characterType == null) {
            clear();
            return;
        }

        STORAGE.putBoolean(KEY_HAS_SAVE, true);
        STORAGE.putString("character_type", snapshot.characterType.name());
        STORAGE.putInteger("score", snapshot.score);
        STORAGE.putInteger("good_collected", snapshot.goodCollected);
        STORAGE.putInteger("total_good_collected", snapshot.totalGoodCollected);
        STORAGE.putFloat("round_timer", snapshot.roundTimer);
        STORAGE.putFloat("spawn_timer", snapshot.spawnTimer);
        STORAGE.putFloat("spawn_interval", snapshot.spawnInterval);
        STORAGE.putFloat("fall_speed", snapshot.fallSpeed);
        STORAGE.putFloat("difficulty_timer", snapshot.difficultyTimer);
        STORAGE.putBoolean("frenzy_active", snapshot.frenzyActive);
        STORAGE.putFloat("frenzy_timer", snapshot.frenzyTimer);
        STORAGE.putFloat("frenzy_diff_timer", snapshot.frenzyDiffTimer);
        STORAGE.putInteger("frenzy_count", snapshot.frenzyCount);
        STORAGE.putFloat("frenzy_orb_timer", snapshot.frenzyOrbTimer);
        STORAGE.putBoolean("frenzy_orb_spawned", snapshot.frenzyOrbSpawned);
        STORAGE.putFloat("pre_frenzy_fall_speed", snapshot.preFrenzyFallSpeed);
        STORAGE.putFloat("pre_frenzy_spawn_interval", snapshot.preFrenzySpawnInterval);
        STORAGE.putFloat("player_x", snapshot.playerX);
        STORAGE.putFloat("player_y", snapshot.playerY);
        STORAGE.putFloat("player_velocity_y", snapshot.playerVelocityY);
        STORAGE.putBoolean("player_on_ground", snapshot.playerOnGround);
        STORAGE.putInteger("current_lives", snapshot.currentLives);
        STORAGE.putInteger("max_lives", snapshot.maxLives);
        STORAGE.putInteger("next_buff_score", snapshot.nextBuffScore);
        STORAGE.putBoolean("has_revive_shield", snapshot.hasShield);
        STORAGE.putBoolean("has_bonus_life_shield", snapshot.bonusLifeShieldActive);
        STORAGE.putFloat("player_speed_bonus", snapshot.playerSpeedBonus);

        STORAGE.putInteger("falling_entity_count", snapshot.fallingEntities.size());
        for (int i = 0; i < snapshot.fallingEntities.size(); i++) {
            FallingEntitySnapshot entity = snapshot.fallingEntities.get(i);
            STORAGE.putString("falling_type_" + i, entity.type.name());
            STORAGE.putFloat("falling_x_" + i, entity.x);
            STORAGE.putFloat("falling_y_" + i, entity.y);
            STORAGE.putFloat("falling_speed_" + i, entity.speed);
        }

        STORAGE.flush();
    }

    public static RunSnapshot load() {
        if (!STORAGE.getBoolean(KEY_HAS_SAVE, false)) {
            return null;
        }

        try {
            RunSnapshot snapshot = new RunSnapshot();
            snapshot.characterType = CharacterType.valueOf(
                STORAGE.getString("character_type", CharacterType.SPECTER.name()));
            snapshot.score = STORAGE.getInteger("score", 0);
            snapshot.goodCollected = STORAGE.getInteger("good_collected", 0);
            snapshot.totalGoodCollected = STORAGE.getInteger("total_good_collected", 0);
            snapshot.roundTimer = STORAGE.getFloat("round_timer", 60f);
            snapshot.spawnTimer = STORAGE.getFloat("spawn_timer", 1.4f);
            snapshot.spawnInterval = STORAGE.getFloat("spawn_interval", 1.4f);
            snapshot.fallSpeed = STORAGE.getFloat("fall_speed", 200f);
            snapshot.difficultyTimer = STORAGE.getFloat("difficulty_timer", 0f);
            snapshot.frenzyActive = STORAGE.getBoolean("frenzy_active", false);
            snapshot.frenzyTimer = STORAGE.getFloat("frenzy_timer", 0f);
            snapshot.frenzyDiffTimer = STORAGE.getFloat("frenzy_diff_timer", 0f);
            snapshot.frenzyCount = STORAGE.getInteger("frenzy_count", 0);
            snapshot.frenzyOrbTimer = STORAGE.getFloat("frenzy_orb_timer", 20f);
            snapshot.frenzyOrbSpawned = STORAGE.getBoolean("frenzy_orb_spawned", false);
            snapshot.preFrenzyFallSpeed = STORAGE.getFloat("pre_frenzy_fall_speed", 0f);
            snapshot.preFrenzySpawnInterval = STORAGE.getFloat("pre_frenzy_spawn_interval", 0f);
            snapshot.playerX = STORAGE.getFloat("player_x", 0f);
            snapshot.playerY = STORAGE.getFloat("player_y", 0f);
            snapshot.playerVelocityY = STORAGE.getFloat("player_velocity_y", 0f);
            snapshot.playerOnGround = STORAGE.getBoolean("player_on_ground", true);
            snapshot.currentLives = STORAGE.getInteger("current_lives", snapshot.characterType.getLives());
            snapshot.maxLives = STORAGE.getInteger("max_lives", snapshot.characterType.getLives());
            snapshot.nextBuffScore = STORAGE.getInteger("next_buff_score", 200);
            snapshot.hasShield = STORAGE.getBoolean("has_revive_shield", false);
            snapshot.bonusLifeShieldActive = STORAGE.getBoolean("has_bonus_life_shield", false);
            snapshot.playerSpeedBonus = STORAGE.getFloat("player_speed_bonus", 0f);

            int count = STORAGE.getInteger("falling_entity_count", 0);
            snapshot.fallingEntities.clear();
            for (int i = 0; i < count; i++) {
                FallingEntitySnapshot entity = new FallingEntitySnapshot();
                entity.type = EntityType.valueOf(
                    STORAGE.getString("falling_type_" + i, EntityType.GOOD_BYTE.name()));
                entity.x = STORAGE.getFloat("falling_x_" + i, 0f);
                entity.y = STORAGE.getFloat("falling_y_" + i, 0f);
                entity.speed = STORAGE.getFloat("falling_speed_" + i, 0f);
                snapshot.fallingEntities.add(entity);
            }

            return snapshot;
        } catch (Exception ex) {
            clear();
            return null;
        }
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
