package sg.edu.sit.inf1009.p2team2.game.scenes;

import sg.edu.sit.inf1009.p2team2.engine.io.input.Keys;
import sg.edu.sit.inf1009.p2team2.engine.io.output.EngineColor;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.collision.CollisionDetector;
import sg.edu.sit.inf1009.p2team2.engine.entity.Entity;
import sg.edu.sit.inf1009.p2team2.engine.entity.EntityManager;
import sg.edu.sit.inf1009.p2team2.engine.entity.components.ColliderComponent;
import sg.edu.sit.inf1009.p2team2.engine.entity.components.InputComponent;
import sg.edu.sit.inf1009.p2team2.engine.entity.components.TransformComponent;
import sg.edu.sit.inf1009.p2team2.engine.entity.components.VelocityComponent;
import sg.edu.sit.inf1009.p2team2.engine.io.output.Audio;
import sg.edu.sit.inf1009.p2team2.engine.io.input.Keyboard;
import sg.edu.sit.inf1009.p2team2.engine.io.output.Renderer;
import sg.edu.sit.inf1009.p2team2.engine.movement.MovementSystem;
import sg.edu.sit.inf1009.p2team2.engine.scene.ResourceLoader;
import sg.edu.sit.inf1009.p2team2.engine.scene.Scene;
import sg.edu.sit.inf1009.p2team2.game.audio.GameAudio;
import sg.edu.sit.inf1009.p2team2.game.components.FallingComponent;
import sg.edu.sit.inf1009.p2team2.game.components.GameEntityComponent;
import sg.edu.sit.inf1009.p2team2.game.components.HealthComponent;
import sg.edu.sit.inf1009.p2team2.game.entities.BuffType;
import sg.edu.sit.inf1009.p2team2.game.entities.CharacterType;
import sg.edu.sit.inf1009.p2team2.game.entities.EntityFactory;
import sg.edu.sit.inf1009.p2team2.game.entities.EntityType;
import sg.edu.sit.inf1009.p2team2.game.leaderboard.LeaderboardManager;
import sg.edu.sit.inf1009.p2team2.game.quiz.QuizManager;
import sg.edu.sit.inf1009.p2team2.game.quiz.QuizBank;
import sg.edu.sit.inf1009.p2team2.game.quiz.QuizResult;
import sg.edu.sit.inf1009.p2team2.game.save.RunSaveManager;
import sg.edu.sit.inf1009.p2team2.game.ui.GameUiTheme;

/**
 * Main gameplay scene.
 *
 * The player moves left/right near the bottom of the screen.
 * Entities fall from the top; the player catches good ones and avoids bad ones.
 *
 * Current game flow:
 *   PLAYING: timer-driven survival and score-based buff choices.
 *   FRENZY: temporary fast phase triggered by catching a frenzy orb.
 *   WIN: reach timer zero in PLAYING mode.
 *   GAME_OVER: lose all lives.
 *
 * Design patterns used:
 *   Factory  - EntityFactory creates entities by type.
 *   Strategy - InputHandler / SceneRenderer per-scene strategy.
 *   State    - GameState enum drives update/render branching.
 *   Observer - QuizManager decouples quiz triggers from gameplay state.
 */
public class GamePlayScene extends Scene {

    // ── Constants ────────────────────────────────────────────────────────────
    static final String BACKGROUND_NORMAL     = "game-scene.png";
    static final String BACKGROUND_FRENZY     = "cyber-hydra-frenzy.jpeg";
    static final String BACKGROUND_TRANSITION = "headphone-girl-listening.png"; // swap for transition bg
    static final String GOOD_BYTE_SPRITE      = "good_byte.png";
    static final String SAFE_EMAIL_SPRITE     = "safe_email.png";
    static final String GOLD_ENVELOPE_SPRITE  = "gold_envelope.png";
    static final String PHISHING_HOOK_SPRITE  = "phishing_hook.png";
    static final String RANSOMWARE_LOCK_SPRITE= "ransomware_lock.png";
    static final String MALWARE_SWARM_SPRITE  = "malware_swarm.png";
    static final String ROOTKIT_SPRITE        = "rootkit.png";
    static final String SPYWARE_SPRITE        = "spyware.png";
    static final String FRENZY_ORB_SPRITE     = "frenzy_orb.png";
    private static final String GAMEPLAY_MUSIC_ID = "game-theme";
    private static final String FRENZY_MUSIC_ID   = "game-theme-frenzy";
    private static final String SFX_COLLECT       = "spawn-marker";

    private static final float WORLD_FLOOR        = 30f;    // y where entities "land"
    private static final float SPAWN_Y            = 750f;
    private static final float SPAWN_MARGIN       = 60f;
    private static final float STANDARD_DURATION  = 60f;   // seconds of standard mode
    static final int   QUIZ_BONUS_POINTS = 100;
    private static final float STATUS_FLASH_INTERVAL = 0.12f;
    private static final float HEALTH_FEEDBACK_DURATION = 3f;
    private static final float REVIVE_FEEDBACK_DURATION = 5f;

    // Difficulty scaling (PLAYING mode only)
    private static final float DIFF_TICK          = 15f;   // seconds between each ramp-up
    private static final float FALL_SPEED_STEP    = 20f;   // px/s added per tick
    private static final float FALL_SPEED_MAX     = 300f;  // cap before frenzy takes over
    private static final float SPAWN_INTERVAL_STEP= 0.10f; // seconds removed per tick
    private static final float SPAWN_INTERVAL_MIN = 0.65f; // floor before frenzy

    // Frenzy mode
    private static final float FRENZY_DURATION    = 15f;   // seconds of frenzy before returning to PLAYING
    private static final float FRENZY_DIFF_TICK   = 6f;    // ramp-up interval inside frenzy
    private static final float FRENZY_FALL_STEP   = 20f;   // px/s added per frenzy tick
    private static final float FRENZY_SPAWN_STEP  = 0.05f; // interval removed per frenzy tick
    private static final float FRENZY_FALL_MAX    = 600f;  // speed cap inside frenzy
    private static final float FRENZY_SPAWN_MIN   = 0.30f; // interval floor inside frenzy
    private static final float FRENZY_ORB_INTERVAL= 20f;   // seconds between frenzy orb spawns

    // Entity type pools per mode
    private static final EntityType[] STANDARD_TYPES = {
        EntityType.GOOD_BYTE, EntityType.GOOD_BYTE, EntityType.GOOD_BYTE,
        EntityType.SAFE_EMAIL, EntityType.SAFE_EMAIL,
        EntityType.GOLD_ENVELOPE,
        EntityType.PHISHING_HOOK, EntityType.PHISHING_HOOK,
        EntityType.RANSOMWARE_LOCK,
        EntityType.MALWARE_SWARM, EntityType.MALWARE_SWARM
    };

    private static final EntityType[] FRENZY_TYPES = {
        EntityType.GOOD_BYTE, EntityType.GOOD_BYTE,
        EntityType.SAFE_EMAIL,
        EntityType.GOLD_ENVELOPE,
        EntityType.PHISHING_HOOK, EntityType.PHISHING_HOOK,
        EntityType.RANSOMWARE_LOCK,
        EntityType.MALWARE_SWARM, EntityType.MALWARE_SWARM,
        EntityType.ROOTKIT,
        EntityType.SPYWARE
    };

    // ── Game state ───────────────────────────────────────────────────────────
    enum GameState { PLAYING, FRENZY, QUIZ, QUIZ_FEEDBACK, BUFF_SELECT, TRANSITION_TO_FRENZY, GAME_OVER, WIN }

    // ── Fields ───────────────────────────────────────────────────────────────
    private final LeaderboardManager leaderboard;
    private final CharacterType      characterType;
    private final EntityManager      entityManager;
    private final EntityFactory      entityFactory;
    private final QuizManager        quizManager;
    private final CollisionDetector  collisionDetector;
    private final MovementSystem     movementSystem;
    private final Random             random;

    private Entity          playerEntity;
    private HealthComponent playerHealth;

    private GameState gameState;
    private int       score;
    private int       goodCollected;      // cosmetic counter
    private int       totalGoodCollected; // running total for stats/display
    private float     roundTimer;      // counts down in PLAYING mode
    private float     spawnTimer;
    private float     spawnInterval;
    private float     fallSpeed;
    private float     transitionTimer; // used for TRANSITION_TO_FRENZY pause
    private float     difficultyTimer; // accumulates time for difficulty ramp-up
    private float     frenzyTimer;     // counts down while in FRENZY
    private float     frenzyDiffTimer; // ramp-up timer inside frenzy
    private int       frenzyCount;     // how many frenzy cycles have ended
    private float     frenzyOrbTimer;  // counts down to next frenzy orb spawn
    private boolean   frenzyOrbSpawned;
    private float     preFrenzyFallSpeed;
    private float     preFrenzySpawnInterval;

    private static final float GRAVITY = -900f;
    private float     playerVelocityY;
    private boolean   playerOnGround;

    // Quiz feedback
    private QuizResult lastQuizResult;
    private boolean    lastQuizWasBad;
    private int       hoveredQuizOption = -1; // -1 = none
    private float      feedbackTimer;
    private GameState  postFeedbackState;
    private GameState  preQuizState;      // state before quiz was triggered

    // Buff system — card offered every BUFF_INTERVAL points
    private static final int BUFF_INTERVAL = 200;
    private int        nextBuffScore    = BUFF_INTERVAL;
    private BuffType[]   buffChoices      = new BuffType[3];
    private int          buffHoveredIdx   = 0;
    private GameState  preBuffState;

    // Active buff state
    private boolean hasShield        = false;
    private boolean bonusLifeShieldActive = false;
    private float   playerSpeedBonus = 0f;

    // Center-screen status feedback
    private String  statusBannerText = "";
    private EngineColor   statusBannerColor = GameUiTheme.TEXT_HIGHLIGHT.cpy();
    private float   statusBannerTimeRemaining = 0f;
    private float   statusBannerFlashTimer = 0f;
    private boolean statusBannerVisible = false;
    private float   hudAnimTime = 0f;
    private RunSaveManager.RunSnapshot pendingResumeSnapshot;

    // ── Constructor ──────────────────────────────────────────────────────────

    public GamePlayScene(EngineContext context, LeaderboardManager leaderboard) {
        this(context, leaderboard, CharacterType.SPECTER);
    }

    public GamePlayScene(EngineContext context, LeaderboardManager leaderboard, CharacterType characterType) {
        super(context);
        this.leaderboard   = leaderboard;
        this.characterType = characterType;
        this.entityManager = new EntityManager();
        this.entityFactory = new EntityFactory(entityManager);
        this.quizManager   = new QuizManager(new QuizBank());
        this.collisionDetector = new CollisionDetector();
        this.movementSystem = new MovementSystem();
        this.random        = new Random();

        setInputHandler(new GamePlayInputHandler(this));
        setSceneRenderer(new GamePlayRenderer(this));
        setResourceLoader(new ResourceLoader() {
            @Override public void load()   { loadResources(); setLoaded(true); }
            @Override public void unload() { unloadResources(); setLoaded(false); }
        });
    }

    public GamePlayScene(EngineContext context, LeaderboardManager leaderboard,
                         RunSaveManager.RunSnapshot savedRun) {
        this(context, leaderboard,
            savedRun != null && savedRun.characterType != null
                ? savedRun.characterType
                : CharacterType.SPECTER);
        this.pendingResumeSnapshot = savedRun;
    }

    // ── Scene lifecycle ──────────────────────────────────────────────────────

    @Override
    public void onEnter() {
        if (pendingResumeSnapshot != null) {
            restoreGame(pendingResumeSnapshot);
            pendingResumeSnapshot = null;
        } else if (playerEntity == null || playerHealth == null) {
            resetGame();
        }
        playCurrentSceneMusic();
    }

    @Override
    public void onExit() {
        getContext().getOutputManager().getAudio().stopMusic();
    }

    private void loadResources() {
        Audio audio = getContext().getOutputManager().getAudio();
        audio.loadMusic("audio/cyberscouts-theme.ogg", GAMEPLAY_MUSIC_ID);
        audio.loadMusic("audio/cyberscouts-frenzy.ogg", FRENZY_MUSIC_ID);
        audio.loadSound("audio/spawn_click.wav", SFX_COLLECT);
    }

    private void unloadResources() {
        entityManager.clear();
    }

    private void playCurrentSceneMusic() {
        Audio audio = getContext().getOutputManager().getAudio();
        String musicId = (gameState == GameState.FRENZY) ? FRENZY_MUSIC_ID : GAMEPLAY_MUSIC_ID;
        audio.playMusic(musicId, true);
    }

    // ── Update ───────────────────────────────────────────────────────────────

    @Override
    public void update(float dt) {
        hudAnimTime += dt;
        updateStatusBanner(dt);

        switch (gameState) {
            case PLAYING:
            case FRENZY:
                updateGameplay(dt);
                break;
            case TRANSITION_TO_FRENZY:
                transitionTimer -= dt;
                if (transitionTimer <= 0) {
                    startFrenzyMode();
                }
                break;
            case QUIZ:
            case BUFF_SELECT:
                // Input handler drives selection; nothing else updates
                break;
            case QUIZ_FEEDBACK:
                feedbackTimer -= dt;
                if (feedbackTimer <= 0) {
                    if (postFeedbackState == GameState.GAME_OVER) {
                        goToGameOver();
                    } else {
                        gameState = postFeedbackState;
                    }
                }
                break;
            case GAME_OVER:
            case WIN:
                // handled in input
                break;
        }
    }

    private void updateGameplay(float dt) {
        if (gameState == GameState.PLAYING) {
            roundTimer -= dt;
            if (roundTimer <= 0) {
                roundTimer = 0;
                GameAudio.playGameComplete(getContext());
                gameState  = GameState.WIN;
                return;
            }
            // Progressive difficulty ramp in PLAYING mode
            difficultyTimer += dt;
            if (difficultyTimer >= DIFF_TICK) {
                difficultyTimer -= DIFF_TICK;
                fallSpeed     = Math.min(fallSpeed     + FALL_SPEED_STEP,    FALL_SPEED_MAX);
                spawnInterval = Math.max(spawnInterval - SPAWN_INTERVAL_STEP, SPAWN_INTERVAL_MIN);
            }
            frenzyOrbTimer -= dt;
            if (frenzyOrbTimer <= 0 && !frenzyOrbSpawned) {
                spawnFrenzyOrb();
                frenzyOrbSpawned = true;
                frenzyOrbTimer   = FRENZY_ORB_INTERVAL;
            }
        } else if (gameState == GameState.FRENZY) {
            // Ramp up inside frenzy every FRENZY_DIFF_TICK seconds
            frenzyDiffTimer += dt;
            if (frenzyDiffTimer >= FRENZY_DIFF_TICK) {
                frenzyDiffTimer -= FRENZY_DIFF_TICK;
                fallSpeed     = Math.min(fallSpeed     + FRENZY_FALL_STEP,  FRENZY_FALL_MAX);
                spawnInterval = Math.max(spawnInterval - FRENZY_SPAWN_STEP, FRENZY_SPAWN_MIN);
            }
            // Frenzy countdown — return to PLAYING when timer expires
            frenzyTimer -= dt;
            if (frenzyTimer <= 0) {
                endFrenzyMode();
                return;
            }
        }

        // Move player
        movePlayer(dt);

        // Advance spawn timer
        spawnTimer -= dt;
        if (spawnTimer <= 0) {
            spawnRandomEntity();
            spawnTimer = spawnInterval;
        }

        // Update falling positions and check collisions
        List<Entity> toRemove = new ArrayList<>();
        for (Entity entity : entityManager.getAllEntities()) {
            if (entity == playerEntity) continue;

            FallingComponent fall = entity.get(FallingComponent.class);
            if (fall == null || !fall.isActive()) { toRemove.add(entity); continue; }

            TransformComponent tf = entity.get(TransformComponent.class);
            VelocityComponent velocity = entity.get(VelocityComponent.class);
            if (velocity != null) {
                velocity.getVelocity().set(0f, -fall.getSpeed());
                velocity.getAcceleration().set(0f, 0f);
                movementSystem.integrate(tf, velocity, dt);
            } else {
                tf.getPosition().y -= fall.getSpeed() * dt;
            }
            syncCollider(entity);

            // Off screen below → remove quietly
            if (tf.getPosition().y + tf.getScale().y < 0) {
                GameEntityComponent gec = entity.get(GameEntityComponent.class);
                if (gec != null && gec.getEntityType() == EntityType.FRENZY_ORB) {
                    frenzyOrbSpawned = false;
                    frenzyOrbTimer = FRENZY_ORB_INTERVAL;
                }
                toRemove.add(entity);
                continue;
            }

            // Collision with player
            if (collidesWithPlayer(entity)) {
                handleCollision(entity, toRemove);
            }
        }

        toRemove.forEach(e -> entityManager.removeEntity(e.getId()));
    }

    private void movePlayer(float dt) {
        Keyboard kb = getContext().getInputManager().getKeyboard();
        TransformComponent tf = playerEntity.get(TransformComponent.class);
        VelocityComponent velocity = playerEntity.get(VelocityComponent.class);
        InputComponent input = playerEntity.get(InputComponent.class);
        Renderer r  = getContext().getOutputManager().getRenderer();

        if (input != null && !input.isEnabled()) {
            return;
        }

        float speed = characterType.getSpeed() + playerSpeedBonus;
        float horizontalVelocity = 0f;
        if (kb.isKeyDown(Keys.LEFT)  || kb.isKeyDown(Keys.A)) horizontalVelocity -= speed;
        if (kb.isKeyDown(Keys.RIGHT) || kb.isKeyDown(Keys.D)) horizontalVelocity += speed;

        if (playerOnGround && (kb.isKeyPressed(Keys.SPACE)
                || kb.isKeyPressed(Keys.W)
                || kb.isKeyPressed(Keys.UP))) {
            GameAudio.playJump(getContext());
            playerVelocityY = characterType.getJumpStrength();
            if (velocity != null) {
                velocity.getVelocity().y = playerVelocityY;
            }
            playerOnGround  = false;
        }

        if (velocity != null) {
            velocity.getVelocity().x = horizontalVelocity;
            velocity.getAcceleration().x = 0f;
            velocity.getAcceleration().y = playerOnGround ? 0f : GRAVITY;
            movementSystem.integrate(tf, velocity, dt);
        } else {
            tf.getPosition().x += horizontalVelocity * dt;
            if (!playerOnGround) {
                playerVelocityY += GRAVITY * dt;
                tf.getPosition().y += playerVelocityY * dt;
            }
        }

        float half = EntityFactory.PLAYER_WIDTH / 2f;
        tf.getPosition().x = Math.max(half, Math.min(r.getWorldWidth() - half, tf.getPosition().x));

        if (tf.getPosition().y <= WORLD_FLOOR) {
            tf.getPosition().y = WORLD_FLOOR;
            playerVelocityY = 0f;
            playerOnGround = true;
            if (velocity != null) {
                velocity.getVelocity().y = 0f;
                velocity.getAcceleration().y = 0f;
            }
        } else if (velocity != null) {
            playerVelocityY = velocity.getVelocity().y;
            playerOnGround = false;
        }

        syncCollider(playerEntity);
    }

    private void spawnRandomEntity() {
        EntityType[] pool = (gameState == GameState.FRENZY) ? FRENZY_TYPES : STANDARD_TYPES;
        EntityType type   = pool[random.nextInt(pool.length)];

        Renderer r  = getContext().getOutputManager().getRenderer();
        float spawnX = SPAWN_MARGIN + random.nextFloat() * (r.getWorldWidth() - SPAWN_MARGIN * 2);

        float speed = fallSpeed * type.getSpeedMultiplier();
        speed *= 0.9f + random.nextFloat() * 0.2f;
        entityFactory.createFallingEntity(type, spawnX, SPAWN_Y, speed);
    }

    private boolean collidesWithPlayer(Entity entity) {
        syncCollider(playerEntity);
        syncCollider(entity);
        return collisionDetector.checkCollision(playerEntity, entity) != null;
    }

    private void syncCollider(Entity entity) {
        if (entity == null) {
            return;
        }

        TransformComponent tf = entity.get(TransformComponent.class);
        ColliderComponent collider = entity.get(ColliderComponent.class);
        if (tf == null || collider == null) {
            return;
        }

        float width = tf.getScale().x;
        float height = tf.getScale().y;
        GameEntityComponent gec = entity.get(GameEntityComponent.class);
        boolean isPlayer = entity == playerEntity
            || (gec != null && gec.getEntityType() == EntityType.PLAYER);
        float x = tf.getPosition().x - width / 2f;
        float y = isPlayer ? tf.getPosition().y : tf.getPosition().y - height / 2f;
        collider.setBounds(new Rectangle(x, y, width, height));
    }

    private void handleCollision(Entity entity, List<Entity> toRemove) {
        GameEntityComponent gec = entity.get(GameEntityComponent.class);
        if (gec == null || gec.isCollected()) return;
        gec.markCollected();

        if (gec.getEntityType() == EntityType.FRENZY_ORB) {
            getContext().getOutputManager().getAudio().playSound(SFX_COLLECT, 1.0f);
            toRemove.add(entity);
            frenzyOrbSpawned = false;
            gameState        = GameState.TRANSITION_TO_FRENZY;
            transitionTimer  = 3f;
        } else if (gec.isQuizTrigger()) {
            // Freeze game and show quiz overlay
            entity.get(FallingComponent.class).deactivate();
            quizManager.triggerQuiz(entity);
            preQuizState = gameState;
            gameState = GameState.QUIZ;
        } else if (gec.isBad()) {
            applyDamageWithDeathDefier();
            toRemove.add(entity);
        } else {
            score += Math.round(gec.getScoreValue() * characterType.getScoreMultiplier());
            goodCollected++;
            totalGoodCollected++;
            getContext().getOutputManager().getAudio().playSound(SFX_COLLECT, 0.8f);
            toRemove.add(entity);
            checkGoal();
        }
    }

    // ── Quiz answer resolution ───────────────────────────────────────────────

    void submitQuizAnswer(int index) {
        if (!quizManager.isActive()) return;
        GameAudio.playUiClick(getContext());

        Entity  triggered = quizManager.getTriggeringEntity();
        boolean isBadQuiz = quizManager.isBadEntityQuiz();
        QuizResult result = quizManager.submitAnswer(index);

        if (isBadQuiz) {
            if (result == QuizResult.CORRECT) {
                // Neutralised — no damage, bonus points
                score += QUIZ_BONUS_POINTS;
            } else {
                applyDamageWithDeathDefier();
            }
        } else {
            // Good entity quiz (Gold Envelope)
            if (result == QuizResult.CORRECT) {
                awardHealthBonusOrOverflowShield();
                score += QUIZ_BONUS_POINTS;
            }
            // Quiz entity is still collected regardless of answer.
            goodCollected++;
            totalGoodCollected++;
        }

        entityManager.removeEntity(triggered.getId());

        // Determine post-feedback destination before entering feedback state
        if (playerHealth.isDead()) {
            postFeedbackState = GameState.GAME_OVER;
        } else {
            // Restore pre-quiz play state so checkGoal() can evaluate correctly
            gameState = preQuizState;
            if (!isBadQuiz) checkGoal(); // may update gameState to buff selection
            postFeedbackState = gameState;
        }

        lastQuizResult = result;
        lastQuizWasBad = isBadQuiz;
        feedbackTimer  = 1.5f;
        gameState      = GameState.QUIZ_FEEDBACK;
    }

    // ── Goal/death checks ────────────────────────────────────────────────────

    private void checkGoal() {
        if (score >= nextBuffScore && gameState != GameState.BUFF_SELECT) {
            triggerBuffSelect();
        }
    }

    private void triggerBuffSelect() {
        nextBuffScore += BUFF_INTERVAL;
        // Always present all three cards, one per card image
        buffChoices    = new BuffType[]{ BuffType.EXTRA_LIFE, BuffType.SPEED_SURGE, BuffType.SHIELD };
        preBuffState   = gameState;
        gameState      = GameState.BUFF_SELECT;
    }

    void applyBuff(BuffType buff) {
        GameAudio.playUiClick(getContext());
        switch (buff) {
            case EXTRA_LIFE:
                int livesBefore = playerHealth.getCurrentLives();
                playerHealth.increaseMaxLives();
                if (playerHealth.getCurrentLives() > livesBefore) {
                    triggerHealthGainBanner();
                }
                break;
            case SPEED_SURGE:
                playerSpeedBonus += characterType.getSpeed() * 0.10f;
                break;
            case SHIELD:
                hasShield = true;
                break;
        }
        gameState = preBuffState;
        checkGoal(); // handle any deferred frenzy/win trigger
    }

    private void checkGameOver() {
        if (playerHealth.isDead()) {
            goToGameOver();
        }
    }

    /**
     * Applies one damage instance and consumes Death Defier only on lethal hits.
     */
    private void applyDamageWithDeathDefier() {
        if (bonusLifeShieldActive) {
            bonusLifeShieldActive = false;
            GameAudio.playLoseLife(getContext());
            triggerHealthLossBanner();
            return;
        }

        playerHealth.takeDamage();
        if (playerHealth.isDead() && hasShield) {
            GameAudio.playLoseLife(getContext());
            playerHealth.gainLife();
            hasShield = false;
            triggerReviveBanner();
            return;
        }

        if (!playerHealth.isDead()) {
            GameAudio.playLoseLife(getContext());
            triggerHealthLossBanner();
        }
        checkGameOver();
    }

    private void startFrenzyMode() {
        preFrenzyFallSpeed     = fallSpeed;
        preFrenzySpawnInterval = spawnInterval;
        gameState       = GameState.FRENZY;
        playCurrentSceneMusic();
        fallSpeed       = 320f;
        spawnInterval   = 0.7f;
        spawnTimer      = spawnInterval;
        frenzyTimer     = FRENZY_DURATION;
        frenzyDiffTimer = 0;
        entityManager.clear();
        entityManager.addEntity(playerEntity);
    }

    private void endFrenzyMode() {
        frenzyCount++;
        // Restore pre-frenzy pacing for timer-based gameplay.
        fallSpeed       = preFrenzyFallSpeed > 0f ? preFrenzyFallSpeed : 200f;
        spawnInterval   = preFrenzySpawnInterval > 0f ? preFrenzySpawnInterval : 1.4f;
        spawnTimer      = spawnInterval;
        difficultyTimer = 0;
        goodCollected   = 0;
        frenzyOrbSpawned = false;
        frenzyOrbTimer   = FRENZY_ORB_INTERVAL;
        gameState       = GameState.PLAYING;
        playCurrentSceneMusic();
        entityManager.clear();
        entityManager.addEntity(playerEntity);
    }

    private void spawnFrenzyOrb() {
        Renderer r  = getContext().getOutputManager().getRenderer();
        float spawnX = SPAWN_MARGIN + random.nextFloat() * (r.getWorldWidth() - SPAWN_MARGIN * 2);
        float speed  = fallSpeed * EntityType.FRENZY_ORB.getSpeedMultiplier();
        entityFactory.createFallingEntity(EntityType.FRENZY_ORB, spawnX, SPAWN_Y, speed);
    }

    // ── Transition to next scene ─────────────────────────────────────────────

    void goToGameOver() {
        RunSaveManager.clear();
        GameAudio.playGameOver(getContext());
        getContext().getSceneManager().pop();
        getContext().getSceneManager().push(new GameOverScene(getContext(), score, leaderboard));
    }

    void goToLeaderboard() {
        RunSaveManager.clear();
        getContext().getSceneManager().pop();
        getContext().getSceneManager().push(new GameOverScene(getContext(), score, leaderboard, true));
    }

    // ── Reset ────────────────────────────────────────────────────────────────

    private void resetGame() {
        entityManager.clear();
        gameState     = GameState.PLAYING;
        score              = 0;
        goodCollected      = 0;
        totalGoodCollected = 0;
        fallSpeed       = 200f;
        spawnInterval   = 1.4f;
        spawnTimer      = spawnInterval;
        roundTimer      = STANDARD_DURATION;
        transitionTimer = 0;
        difficultyTimer = 0;
        frenzyTimer     = 0;
        frenzyDiffTimer = 0;
        frenzyCount     = 0;
        frenzyOrbTimer  = FRENZY_ORB_INTERVAL;
        frenzyOrbSpawned = false;
        preFrenzyFallSpeed     = 0;
        preFrenzySpawnInterval = 0;

        playerVelocityY = 0;
        playerOnGround  = true;
        // Buff state
        nextBuffScore    = BUFF_INTERVAL;
        hasShield        = false;
        bonusLifeShieldActive = false;
        playerSpeedBonus = 0f;
        statusBannerText = "";
        statusBannerTimeRemaining = 0f;
        statusBannerFlashTimer = 0f;
        statusBannerVisible = false;

        Renderer r = getContext().getOutputManager().getRenderer();
        playerEntity  = entityFactory.createPlayer(r.getWorldWidth() / 2f, WORLD_FLOOR, characterType.getLives());
        playerHealth  = playerEntity.get(HealthComponent.class);
        syncCollider(playerEntity);
    }

    void saveCurrentRun() {
        if (gameState != GameState.PLAYING && gameState != GameState.FRENZY) {
            return;
        }
        RunSaveManager.save(buildRunSnapshot());
    }

    private RunSaveManager.RunSnapshot buildRunSnapshot() {
        RunSaveManager.RunSnapshot snapshot = new RunSaveManager.RunSnapshot();
        TransformComponent playerTf = playerEntity.get(TransformComponent.class);

        snapshot.characterType = characterType;
        snapshot.score = score;
        snapshot.goodCollected = goodCollected;
        snapshot.totalGoodCollected = totalGoodCollected;
        snapshot.roundTimer = roundTimer;
        snapshot.spawnTimer = spawnTimer;
        snapshot.spawnInterval = spawnInterval;
        snapshot.fallSpeed = fallSpeed;
        snapshot.difficultyTimer = difficultyTimer;
        snapshot.frenzyActive = gameState == GameState.FRENZY;
        snapshot.frenzyTimer = frenzyTimer;
        snapshot.frenzyDiffTimer = frenzyDiffTimer;
        snapshot.frenzyCount = frenzyCount;
        snapshot.frenzyOrbTimer = frenzyOrbTimer;
        snapshot.frenzyOrbSpawned = frenzyOrbSpawned;
        snapshot.preFrenzyFallSpeed = preFrenzyFallSpeed;
        snapshot.preFrenzySpawnInterval = preFrenzySpawnInterval;
        snapshot.playerX = playerTf.getPosition().x;
        snapshot.playerY = playerTf.getPosition().y;
        snapshot.playerVelocityY = playerVelocityY;
        snapshot.playerOnGround = playerOnGround;
        snapshot.currentLives = playerHealth.getCurrentLives();
        snapshot.maxLives = playerHealth.getMaxLives();
        snapshot.nextBuffScore = nextBuffScore;
        snapshot.hasShield = hasShield;
        snapshot.bonusLifeShieldActive = bonusLifeShieldActive;
        snapshot.playerSpeedBonus = playerSpeedBonus;

        for (Entity entity : entityManager.getAllEntities()) {
            if (entity == playerEntity) {
                continue;
            }

            TransformComponent tf = entity.get(TransformComponent.class);
            FallingComponent fall = entity.get(FallingComponent.class);
            GameEntityComponent gec = entity.get(GameEntityComponent.class);
            if (tf == null || fall == null || gec == null) {
                continue;
            }

            snapshot.fallingEntities.add(new RunSaveManager.FallingEntitySnapshot(
                gec.getEntityType(),
                tf.getPosition().x,
                tf.getPosition().y,
                fall.getSpeed()));
        }

        return snapshot;
    }

    private void restoreGame(RunSaveManager.RunSnapshot snapshot) {
        entityManager.clear();

        gameState = snapshot.frenzyActive ? GameState.FRENZY : GameState.PLAYING;
        score = snapshot.score;
        goodCollected = snapshot.goodCollected;
        totalGoodCollected = snapshot.totalGoodCollected;
        roundTimer = snapshot.roundTimer;
        spawnTimer = snapshot.spawnTimer;
        spawnInterval = snapshot.spawnInterval;
        fallSpeed = snapshot.fallSpeed;
        transitionTimer = 0f;
        difficultyTimer = snapshot.difficultyTimer;
        frenzyTimer = snapshot.frenzyTimer;
        frenzyDiffTimer = snapshot.frenzyDiffTimer;
        frenzyCount = snapshot.frenzyCount;
        frenzyOrbTimer = snapshot.frenzyOrbTimer;
        frenzyOrbSpawned = snapshot.frenzyOrbSpawned;
        preFrenzyFallSpeed = snapshot.preFrenzyFallSpeed;
        preFrenzySpawnInterval = snapshot.preFrenzySpawnInterval;

        playerVelocityY = snapshot.playerVelocityY;
        playerOnGround = snapshot.playerOnGround;
        nextBuffScore = snapshot.nextBuffScore;
        hasShield = snapshot.hasShield;
        bonusLifeShieldActive = snapshot.bonusLifeShieldActive;
        playerSpeedBonus = snapshot.playerSpeedBonus;

        lastQuizResult = null;
        lastQuizWasBad = false;
        hoveredQuizOption = -1;
        feedbackTimer = 0f;
        postFeedbackState = gameState;
        preQuizState = gameState;
        preBuffState = gameState;
        buffHoveredIdx = 0;
        statusBannerText = "";
        statusBannerTimeRemaining = 0f;
        statusBannerFlashTimer = 0f;
        statusBannerVisible = false;

        playerEntity = entityFactory.createPlayer(snapshot.playerX, WORLD_FLOOR, snapshot.maxLives);
        playerHealth = playerEntity.get(HealthComponent.class);
        while (playerHealth.getCurrentLives() > snapshot.currentLives) {
            playerHealth.takeDamage();
        }

        TransformComponent playerTf = playerEntity.get(TransformComponent.class);
        playerTf.getPosition().x = snapshot.playerX;
        playerTf.getPosition().y = Math.max(WORLD_FLOOR, snapshot.playerY);
        VelocityComponent playerVelocity = playerEntity.get(VelocityComponent.class);
        if (playerVelocity != null) {
            playerVelocity.getVelocity().set(0f, playerVelocityY);
            playerVelocity.getAcceleration().set(0f, playerOnGround ? 0f : GRAVITY);
        }
        syncCollider(playerEntity);

        for (RunSaveManager.FallingEntitySnapshot entityState : snapshot.fallingEntities) {
            if (entityState == null || entityState.type == null || entityState.type == EntityType.PLAYER) {
                continue;
            }

            Entity entity = entityFactory.createFallingEntity(
                entityState.type, entityState.x, entityState.y, entityState.speed);
            TransformComponent tf = entity.get(TransformComponent.class);
            tf.getPosition().x = entityState.x;
            tf.getPosition().y = entityState.y;
            VelocityComponent velocity = entity.get(VelocityComponent.class);
            if (velocity != null) {
                velocity.getVelocity().set(0f, -entityState.speed);
            }
            syncCollider(entity);
        }
    }

    private void triggerReviveBanner() {
        triggerStatusBanner("REVIVED", GameUiTheme.TEXT_HIGHLIGHT, REVIVE_FEEDBACK_DURATION);
    }

    private void triggerHealthGainBanner() {
        triggerStatusBanner("+1 HEALTH", GameUiTheme.TEXT_SUCCESS, HEALTH_FEEDBACK_DURATION);
    }

    private void triggerHealthLossBanner() {
        triggerStatusBanner("-1 HEALTH", GameUiTheme.TEXT_DANGER, HEALTH_FEEDBACK_DURATION);
    }

    private void awardHealthBonusOrOverflowShield() {
        if (playerHealth.getCurrentLives() < playerHealth.getMaxLives()) {
            playerHealth.gainLife();
            triggerHealthGainBanner();
        } else if (!bonusLifeShieldActive) {
            bonusLifeShieldActive = true;
            triggerHealthGainBanner();
        }
    }

    private void triggerStatusBanner(String text, EngineColor color, float durationSeconds) {
        statusBannerText = text;
        statusBannerColor = color.cpy();
        statusBannerTimeRemaining = durationSeconds;
        statusBannerFlashTimer = 0f;
        statusBannerVisible = true;
    }

    private void updateStatusBanner(float dt) {
        if (statusBannerTimeRemaining <= 0f) {
            statusBannerVisible = false;
            return;
        }

        statusBannerTimeRemaining -= dt;
        if (statusBannerTimeRemaining <= 0f) {
            statusBannerVisible = false;
            statusBannerTimeRemaining = 0f;
            return;
        }

        statusBannerFlashTimer += dt;
        statusBannerVisible = ((int) (statusBannerFlashTimer / STATUS_FLASH_INTERVAL) % 2) == 0;
    }

    // ── Accessors for renderer / input handler ───────────────────────────────

    /** Shared card rectangle used by both input handler and renderer. */
    Rectangle buffCardRect(int idx, float ww, float wh) {
        float cardW   = 250f, cardH = 389f, gap = 40f;
        float totalW  = 3 * cardW + 2 * gap;
        float startX  = ww / 2f - totalW / 2f;
        float x       = startX + idx * (cardW + gap);
        float y       = wh / 2f - cardH / 2f - 16f;
        return new Rectangle(x, y, cardW, cardH);
    }
    GameState getGameState()              { return gameState; }
    int getScore()                        { return score; }
    int getGoodCollected()                { return goodCollected; }
    float getRoundTimer()                 { return roundTimer; }
    int getTotalGoodCollected()           { return totalGoodCollected; }
    HealthComponent getPlayerHealth()     { return playerHealth; }
    Entity getPlayerEntity()              { return playerEntity; }
    EntityManager getEntityManager()      { return entityManager; }
    QuizManager getQuizManager()          { return quizManager; }
    float getTransitionTimer()            { return transitionTimer; }
    QuizResult getLastQuizResult()        { return lastQuizResult; }
    boolean isLastQuizBad()               { return lastQuizWasBad; }
    float getFeedbackTimer()              { return feedbackTimer; }
    GameState getPostFeedbackState()      { return postFeedbackState; }
    GameState getPreQuizState()           { return preQuizState; }
    GameState getPreBuffState()           { return preBuffState; }
    boolean hasShield()                   { return hasShield; }
    boolean isBonusLifeShieldActive()     { return bonusLifeShieldActive; }
    float getFrenzyTimer()                { return frenzyTimer; }
    float getHudAnimTime()                { return hudAnimTime; }
    CharacterType getCharacterType()      { return characterType; }
    String getStatusBannerText()          { return statusBannerText; }
    EngineColor getStatusBannerColor()          { return statusBannerColor; }
    boolean isStatusBannerVisible()       { return statusBannerVisible; }
    int getHoveredQuizOption()            { return hoveredQuizOption; }
    void setHoveredQuizOption(int idx)    { hoveredQuizOption = idx; }
    int getBuffHoveredIdx()               { return buffHoveredIdx; }
    void setBuffHoveredIdx(int idx)       { buffHoveredIdx = idx; }
    BuffType getBuffChoice(int idx)       { return buffChoices[idx]; }
    void clearQuizFeedbackTimer()         { feedbackTimer = 0f; }
    void setGameState(GameState state)    { gameState = state; }
    void openPauseMenu() {
        GameAudio.playUiClick(getContext());
        getContext().getSceneManager().push(new PauseScene(getContext(), this));
    }
}
