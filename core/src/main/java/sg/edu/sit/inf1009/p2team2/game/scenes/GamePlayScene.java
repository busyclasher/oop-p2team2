package sg.edu.sit.inf1009.p2team2.game.scenes;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.entity.Entity;
import sg.edu.sit.inf1009.p2team2.engine.entity.EntityManager;
import sg.edu.sit.inf1009.p2team2.engine.entity.components.TransformComponent;
import sg.edu.sit.inf1009.p2team2.engine.io.input.Keyboard;
import sg.edu.sit.inf1009.p2team2.engine.io.input.Mouse;
import sg.edu.sit.inf1009.p2team2.engine.io.output.Audio;
import sg.edu.sit.inf1009.p2team2.engine.io.output.Renderer;
import sg.edu.sit.inf1009.p2team2.engine.scene.InputHandler;
import sg.edu.sit.inf1009.p2team2.engine.scene.ResourceLoader;
import sg.edu.sit.inf1009.p2team2.engine.scene.Scene;
import sg.edu.sit.inf1009.p2team2.engine.scene.SceneRenderer;
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
    private static final String BACKGROUND_NORMAL     = "game-scene.png";
    private static final String BACKGROUND_FRENZY     = "frenzy-scene.png"; // swap if a frenzy bg exists
    private static final String BACKGROUND_TRANSITION = "headphone-girl-listening.png"; // swap for transition bg
    private static final String MUSIC_ID          = "game-theme";
    private static final String SFX_COLLECT       = "spawn-marker";

    private static final float WORLD_FLOOR        = 30f;    // y where entities "land"
    private static final float SPAWN_Y            = 750f;
    private static final float SPAWN_MARGIN       = 60f;
    private static final float STANDARD_DURATION  = 60f;   // seconds of standard mode

    // Difficulty scaling (PLAYING mode only)
    private static final float DIFF_TICK          = 15f;   // seconds between each ramp-up
    private static final float FALL_SPEED_STEP    = 20f;   // px/s added per tick
    private static final float FALL_SPEED_MAX     = 300f;  // cap before frenzy takes over
    private static final float SPAWN_INTERVAL_STEP= 0.10f; // seconds removed per tick
    private static final float SPAWN_INTERVAL_MIN = 0.65f; // floor before frenzy

    // Frenzy mode
    private static final float FRENZY_DURATION    = 8f;    // seconds of frenzy before returning to PLAYING
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
    private enum GameState { PLAYING, FRENZY, QUIZ, QUIZ_FEEDBACK, BUFF_SELECT, TRANSITION_TO_FRENZY, GAME_OVER, WIN }

    // ── Fields ───────────────────────────────────────────────────────────────
    private final LeaderboardManager leaderboard;
    private final CharacterType      characterType;
    private final EntityManager      entityManager;
    private final EntityFactory      entityFactory;
    private final QuizManager        quizManager;
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
    private float   playerSpeedBonus = 0f;

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
        this.random        = new Random();

        setInputHandler(new GamePlayInputHandler(this));
        setSceneRenderer(new GamePlayRenderer(this));
        setResourceLoader(new ResourceLoader() {
            @Override public void load()   { loadResources(); setLoaded(true); }
            @Override public void unload() { unloadResources(); setLoaded(false); }
        });
    }

    // ── Scene lifecycle ──────────────────────────────────────────────────────

    @Override
    public void onEnter() {
        resetGame();
        getContext().getOutputManager().getAudio().playMusic(MUSIC_ID, true);
    }

    @Override
    public void onExit() {
        getContext().getOutputManager().getAudio().stopMusic();
    }

    private void loadResources() {
        Audio audio = getContext().getOutputManager().getAudio();
        audio.loadMusic("audio/nightstarsmix.ogg", MUSIC_ID);
        audio.loadSound("audio/spawn_click.wav", SFX_COLLECT);
    }

    private void unloadResources() {
        entityManager.clear();
    }

    // ── Update ───────────────────────────────────────────────────────────────

    @Override
    public void update(float dt) {
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
            tf.getPosition().y -= fall.getSpeed() * dt;

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
            if (overlapsPlayer(entity)) {
                handleCollision(entity, toRemove);
            }
        }

        toRemove.forEach(e -> entityManager.removeEntity(e.getId()));
    }

    private void movePlayer(float dt) {
        Keyboard kb = getContext().getInputManager().getKeyboard();
        TransformComponent tf = playerEntity.get(TransformComponent.class);
        Renderer r  = getContext().getOutputManager().getRenderer();

        float speed = characterType.getSpeed() + playerSpeedBonus;
        float dx = 0;
        if (kb.isKeyDown(Input.Keys.LEFT)  || kb.isKeyDown(Input.Keys.A)) dx -= speed * dt;
        if (kb.isKeyDown(Input.Keys.RIGHT) || kb.isKeyDown(Input.Keys.D)) dx += speed * dt;

        float newX = tf.getPosition().x + dx;
        float half = EntityFactory.PLAYER_WIDTH / 2f;
        newX = Math.max(half, Math.min(r.getWorldWidth() - half, newX));
        tf.getPosition().x = newX;

        if (playerOnGround && (kb.isKeyPressed(Input.Keys.SPACE)
                || kb.isKeyPressed(Input.Keys.W)
                || kb.isKeyPressed(Input.Keys.UP))) {
            playerVelocityY = characterType.getJumpStrength();
            playerOnGround  = false;
        }

        if (!playerOnGround) {
            playerVelocityY += GRAVITY * dt;
            float newY = tf.getPosition().y + playerVelocityY * dt;
            if (newY <= WORLD_FLOOR) {
                newY            = WORLD_FLOOR;
                playerVelocityY = 0;
                playerOnGround  = true;
            }
            tf.getPosition().y = newY;
        }
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

    private boolean overlapsPlayer(Entity entity) {
        TransformComponent playerTf = playerEntity.get(TransformComponent.class);
        TransformComponent entTf    = entity.get(TransformComponent.class);

        float pw = EntityFactory.PLAYER_WIDTH;
        float ph = EntityFactory.PLAYER_HEIGHT;
        float ew = entTf.getScale().x;
        float eh = entTf.getScale().y;

        float px = playerTf.getPosition().x - pw / 2;
        float py = playerTf.getPosition().y;
        float ex = entTf.getPosition().x  - ew / 2;
        float ey = entTf.getPosition().y  - eh / 2;

        return px < ex + ew && px + pw > ex
            && py < ey + eh && py + ph > ey;
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

        Entity  triggered = quizManager.getTriggeringEntity();
        boolean isBadQuiz = quizManager.isBadEntityQuiz();
        QuizResult result = quizManager.submitAnswer(index);

        if (isBadQuiz) {
            if (result == QuizResult.CORRECT) {
                // Neutralised — no damage, bonus points
                score += Math.round(100 * characterType.getScoreMultiplier());
            } else {
                applyDamageWithDeathDefier();
            }
        } else {
            // Good entity quiz (Gold Envelope)
            if (result == QuizResult.CORRECT) {
                playerHealth.gainLife();
                score += Math.round(100 * characterType.getScoreMultiplier());
            }
            // Wrong answer → no bonus life, but entity still counted as collected
            score        += triggered.get(GameEntityComponent.class).getScoreValue();
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
        switch (buff) {
            case EXTRA_LIFE:
                playerHealth.increaseMaxLives();
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
        playerHealth.takeDamage();
        if (playerHealth.isDead() && hasShield) {
            playerHealth.gainLife();
            hasShield = false;
            getContext().getOutputManager().getAudio().playSound(SFX_COLLECT, 1.0f);
            return;
        }

        getContext().getOutputManager().getAudio().playSound(SFX_COLLECT, 0.5f);
        checkGameOver();
    }

    private void startFrenzyMode() {
        preFrenzyFallSpeed     = fallSpeed;
        preFrenzySpawnInterval = spawnInterval;
        gameState       = GameState.FRENZY;
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
        getContext().getSceneManager().pop();
        getContext().getSceneManager().push(new GameOverScene(getContext(), score, leaderboard));
    }

    void goToLeaderboard() {
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
        playerSpeedBonus = 0f;

        Renderer r = getContext().getOutputManager().getRenderer();
        playerEntity  = entityFactory.createPlayer(r.getWorldWidth() / 2f, WORLD_FLOOR, characterType.getLives());
        playerHealth  = playerEntity.get(HealthComponent.class);
    }

    // ── Accessors for renderer / input handler ───────────────────────────────

    /** Shared card rectangle used by both input handler and renderer. */
    private Rectangle buffCardRect(int idx, float ww, float wh) {
        float cardW   = 250f, cardH = 389f, gap = 40f;
        float totalW  = 3 * cardW + 2 * gap;
        float startX  = ww / 2f - totalW / 2f;
        float x       = startX + idx * (cardW + gap);
        float y       = wh / 2f - cardH / 2f;
        return new Rectangle(x, y, cardW, cardH);
    }
    private GameState getGameState()         { return gameState; }
    private int getScore()             { return score; }
    private int getGoodCollected()     { return goodCollected; }
    private float getRoundTimer()      { return roundTimer; }
    private int getTotalGoodCollected(){ return totalGoodCollected; }
    private HealthComponent getPlayerHealth() { return playerHealth; }
    private Entity getPlayerEntity() { return playerEntity; }
    private EntityManager getEntityManager(){ return entityManager; }
    private QuizManager getQuizManager()  { return quizManager; }
    private float getTransitionTimer(){ return transitionTimer; }
    private QuizResult getLastQuizResult(){ return lastQuizResult; }
    private boolean isLastQuizBad()   { return lastQuizWasBad; }
    private float getFeedbackTimer() { return feedbackTimer; }

    // =========================================================================
    // Inner - InputHandler
    // =========================================================================

    private static final class GamePlayInputHandler extends InputHandler {
        private final GamePlayScene scene;

        GamePlayInputHandler(GamePlayScene scene) {
            super(scene.getContext());
            this.scene = scene;
        }

        @Override
        public void handleInput() {
            Keyboard kb = scene.getContext().getInputManager().getKeyboard();

            switch (scene.gameState) {
                case PLAYING:
                case FRENZY:
                    if (kb.isKeyPressed(Input.Keys.ESCAPE)) {
                        scene.getContext().getSceneManager().push(new PauseScene(scene.getContext()));
                    }
                    break;

                case QUIZ:
                    handleQuizInput(kb);
                    break;

                case QUIZ_FEEDBACK:
                    if (kb.isKeyPressed(Input.Keys.ENTER) || kb.isKeyPressed(Input.Keys.SPACE)) {
                        scene.feedbackTimer = 0;
                        if (scene.postFeedbackState == GameState.GAME_OVER) {
                            scene.goToGameOver();
                        } else {
                            scene.gameState = scene.postFeedbackState;
                        }
                    }
                    break;

                case BUFF_SELECT:
                    handleBuffInput(kb);
                    break;

                case GAME_OVER:
                    if (kb.isKeyPressed(Input.Keys.ENTER) || kb.isKeyPressed(Input.Keys.SPACE)) {
                        scene.goToGameOver();
                    }
                    break;

                case WIN:
                    if (kb.isKeyPressed(Input.Keys.ENTER) || kb.isKeyPressed(Input.Keys.SPACE)) {
                        scene.goToLeaderboard();
                    }
                    break;

                default:
                    break;
            }
        }

        private void handleQuizInput(Keyboard kb) {
            // Keyboard
            if (kb.isKeyPressed(Input.Keys.NUM_1)) { scene.submitQuizAnswer(0); return; }
            if (kb.isKeyPressed(Input.Keys.NUM_2)) { scene.submitQuizAnswer(1); return; }
            if (kb.isKeyPressed(Input.Keys.NUM_3)) { scene.submitQuizAnswer(2); return; }
            if (kb.isKeyPressed(Input.Keys.NUM_4)) { scene.submitQuizAnswer(3); return; }

            // Mouse
            Mouse  mouse = scene.getContext().getInputManager().getMouse();
            Renderer r   = scene.getContext().getOutputManager().getRenderer();
            float ww = r.getWorldWidth(), wh = r.getWorldHeight();
            float cw = 680f, ch = 360f;
            float cx = (ww - cw) / 2f, cy = (wh - ch) / 2f;
            com.badlogic.gdx.math.Vector2 mp = mouse.getPosition();

            scene.hoveredQuizOption = -1;
            for (int i = 0; i < 4; i++) {
                float oy = cy + ch - 160f - i * 46f;
                com.badlogic.gdx.math.Rectangle box =
                    new com.badlogic.gdx.math.Rectangle(cx + 20f, oy, cw - 40f, 38f);
                if (box.contains(mp.x, mp.y)) {
                    scene.hoveredQuizOption = i;
                    if (mouse.isButtonPressed(0)) scene.submitQuizAnswer(i);
                    break;
                }
            }
        }

        private void handleBuffInput(Keyboard kb) {
            // Keyboard left/right to navigate cards
            if (kb.isKeyPressed(Input.Keys.LEFT) || kb.isKeyPressed(Input.Keys.A)) {
                scene.buffHoveredIdx = (scene.buffHoveredIdx - 1 + 3) % 3;
            } else if (kb.isKeyPressed(Input.Keys.RIGHT) || kb.isKeyPressed(Input.Keys.D)) {
                scene.buffHoveredIdx = (scene.buffHoveredIdx + 1) % 3;
            }
            // 1/2/3 hotkeys
            if (kb.isKeyPressed(Input.Keys.NUM_1)) { scene.applyBuff(scene.buffChoices[0]); return; }
            if (kb.isKeyPressed(Input.Keys.NUM_2)) { scene.applyBuff(scene.buffChoices[1]); return; }
            if (kb.isKeyPressed(Input.Keys.NUM_3)) { scene.applyBuff(scene.buffChoices[2]); return; }
            // Enter/Space confirms hovered card
            if (kb.isKeyPressed(Input.Keys.ENTER) || kb.isKeyPressed(Input.Keys.SPACE)) {
                scene.applyBuff(scene.buffChoices[scene.buffHoveredIdx]);
                return;
            }
            // Mouse hover + click
            Mouse    mouse = scene.getContext().getInputManager().getMouse();
            Renderer r     = scene.getContext().getOutputManager().getRenderer();
            Vector2  mp    = mouse.getPosition();
            float ww = r.getWorldWidth(), wh = r.getWorldHeight();
            for (int i = 0; i < 3; i++) {
                if (scene.buffCardRect(i, ww, wh).contains(mp.x, mp.y)) {
                    scene.buffHoveredIdx = i;
                    if (mouse.isButtonPressed(0)) {
                        scene.applyBuff(scene.buffChoices[i]);
                        return;
                    }
                }
            }
        }
    }

    // =========================================================================
    // Inner - SceneRenderer
    // =========================================================================

    private static final class GamePlayRenderer extends SceneRenderer {
        private final GamePlayScene scene;

        // Colors
        private static final Color COL_HUD_BG   = new Color(0f, 0f, 0f, 0.55f);
        private static final Color COL_HEART     = new Color(0.95f, 0.25f, 0.25f, 1f);
        private static final Color COL_HEART_EMPTY = new Color(0.4f, 0.15f, 0.15f, 0.6f);
        private static final Color COL_FRENZY_BANNER = new Color(1f, 0.35f, 0f, 1f);
        private static final Color COL_OVERLAY   = new Color(0f, 0f, 0f, 0.72f);
        private static final Color COL_QUIZ_BG   = new Color(0.05f, 0.08f, 0.18f, 0.95f);
        private static final Color COL_WIN       = new Color(0.15f, 0.95f, 0.40f, 1f);
        private static final Color COL_LOSE      = new Color(0.95f, 0.25f, 0.25f, 1f);

        GamePlayRenderer(GamePlayScene scene) {
            super(scene.getContext());
            this.scene = scene;
        }

        @Override
        public void render() {
            Renderer r = scene.getContext().getOutputManager().getRenderer();
            r.clear();
            r.begin();

            drawBackground(r);
            drawEntities(r);
            drawHUD(r);

            switch (scene.gameState) {
                case QUIZ:
                    drawQuizOverlay(r);
                    break;
                case QUIZ_FEEDBACK:
                    drawQuizFeedback(r);
                    break;
                case BUFF_SELECT:
                    drawBuffSelect(r);
                    break;
                case TRANSITION_TO_FRENZY:
                    drawFrenzyTransition(r);
                    break;
                case GAME_OVER:
                    drawGameOverOverlay(r);
                    break;
                case WIN:
                    drawWinOverlay(r);
                    break;
                default:
                    break;
            }

            r.end();
        }

        // ── Background ──────────────────────────────────────────────────────

        private void drawBackground(Renderer r) {
            // In feedback state use the background of the state we're returning to
            GameState bg_state = (scene.gameState == GameState.QUIZ_FEEDBACK) ? scene.postFeedbackState
                               : (scene.gameState == GameState.BUFF_SELECT)   ? scene.preBuffState
                               : scene.gameState;
            String bg;
            if (bg_state == GameState.FRENZY) bg = BACKGROUND_FRENZY;
            else if (bg_state == GameState.TRANSITION_TO_FRENZY) bg = BACKGROUND_TRANSITION;
            else bg = BACKGROUND_NORMAL;
            r.drawBackground(bg);
        }

        // ── Entities ────────────────────────────────────────────────────────

        private void drawEntities(Renderer r) {
            for (Entity entity : scene.entityManager.getAllEntities()) {
                TransformComponent tf = entity.get(TransformComponent.class);
                if (tf == null) continue;

                GameEntityComponent gec = entity.get(GameEntityComponent.class);
                if (gec == null) continue;

                Color color = gec.getEntityType().getColor();
                float w, h;

                if (gec.getEntityType() == EntityType.PLAYER) {
                    w = EntityFactory.PLAYER_WIDTH;
                    h = EntityFactory.PLAYER_HEIGHT;
                    drawPlayer(r, tf.getPosition(), w, h, color);
                } else {
                    w = tf.getScale().x;
                    h = tf.getScale().y;
                    drawFallingEntity(r, tf.getPosition(), w, h, gec.getEntityType(), color);
                }
            }
        }

        private void drawPlayer(Renderer r, Vector2 pos, float w, float h, Color color) {
            r.drawSprite(scene.characterType.getSprite(),
                new Vector2(pos.x, pos.y + h / 2f), w, h);
        }

        private void drawFallingEntity(Renderer r, Vector2 pos, float w, float h,
                                       EntityType type, Color color) {
            switch (type) {
                case GOOD_BYTE:       r.drawSprite("laptop.png",             pos, w, h); break;
                case SAFE_EMAIL:      r.drawSprite("shield.png",             pos, w, h); break;
                case GOLD_ENVELOPE:   r.drawSprite("phone.png",              pos, w, h); break;
                case PHISHING_HOOK:   r.drawSprite("fraud.png",              pos, w, h); break;
                case RANSOMWARE_LOCK: r.drawSprite("hoax.png",               pos, w, h); break;
                case MALWARE_SWARM:   r.drawSprite("virus.png",              pos, w, h); break;
                case ROOTKIT:         r.drawSprite("old-pc.png",             pos, w, h); break;
                case SPYWARE:         r.drawSprite("magnifiying-glass.png",  pos, w, h); break;
                case FRENZY_ORB: {
                    r.drawCircle(new Vector2(pos.x, pos.y), w / 2f,
                        new Color(0.90f, 0.20f, 0.95f, 0.7f), true);
                    r.drawCircle(new Vector2(pos.x, pos.y), w / 2.5f,
                        new Color(1f, 0.5f, 1f, 0.9f), true);
                    r.drawCircle(new Vector2(pos.x, pos.y), w / 2f, Color.WHITE, false);
                    break;
                }
                default: {
                    float x = pos.x - w / 2, y = pos.y - h / 2;
                    r.drawRect(new Rectangle(x, y, w, h), color, true);
                    r.drawRect(new Rectangle(x, y, w, h), Color.WHITE, false);
                }
            }
        }

        // ── HUD ─────────────────────────────────────────────────────────────

        private void drawHUD(Renderer r) {
            float ww = r.getWorldWidth();
            float wh = r.getWorldHeight();

            // HUD bar background
            r.drawRect(new Rectangle(0, wh - 56f, ww, 56f), COL_HUD_BG, true);

            // Lives (hearts)
            int lives = scene.playerHealth.getCurrentLives();
            int maxL  = scene.playerHealth.getMaxLives();
            for (int i = 0; i < maxL; i++) {
                Color c = (i < lives) ? COL_HEART : COL_HEART_EMPTY;
                float hx = 20f + i * 36f;
                float hy = wh - 44f;
                drawHeart(r, hx, hy, c);
            }

            // Score
            r.drawText("SCORE: " + scene.score,
                new Vector2(ww / 2f - 70f, wh - 14f), "default", Color.WHITE);

            // Timer / mode display
            String progressText;
            Color  progressColor;
            GameState displayState = (scene.gameState == GameState.BUFF_SELECT)
                ? scene.preBuffState : scene.gameState;
            if (displayState == GameState.FRENZY) {
                int secsLeft = Math.max(0, (int) Math.ceil(scene.frenzyTimer));
                progressText  = "FRENZY " + secsLeft + "s  TOTAL " + scene.totalGoodCollected;
                progressColor = COL_FRENZY_BANNER;
            } else {
                int secsLeft = Math.max(0, (int) Math.ceil(scene.getRoundTimer()));
                progressText  = "TIME " + secsLeft + "s  TOTAL " + scene.totalGoodCollected;
                progressColor = secsLeft <= 10 ? new Color(1f, 0.3f, 0.3f, 1f) : Color.CYAN;
            }
            r.drawText(progressText, new Vector2(ww - 300f, wh - 14f), "default", progressColor);

            // Active buff indicators (bottom-right)
            if (scene.hasShield) {
                r.drawText("[REVIVE READY]", new Vector2(ww - 230f, 12f),
                    "default", new Color(0.2f, 0.55f, 1f, 1f));
            }

            // Controls hint
            r.drawText("A/D Move  |  SPACE Jump  |  ESC Quit",
                new Vector2(20f, 12f), "default", new Color(0.6f, 0.6f, 0.6f, 1f));
        }

        private void drawHeart(Renderer r, float x, float y, Color c) {
            r.drawCircle(new Vector2(x + 6f,  y + 8f), 6f, c, true);
            r.drawCircle(new Vector2(x + 16f, y + 8f), 6f, c, true);
            r.drawRect(new Rectangle(x, y, 22f, 10f), c, true);
            // bottom triangle approximated by two lines
            r.drawLine(new Vector2(x, y), new Vector2(x + 11f, y - 8f), c, 3f);
            r.drawLine(new Vector2(x + 22f, y), new Vector2(x + 11f, y - 8f), c, 3f);
        }

        // ── Quiz overlay ─────────────────────────────────────────────────────

        private void drawQuizOverlay(Renderer r) {
            float ww = r.getWorldWidth();
            float wh = r.getWorldHeight();

            // Dim background
            r.drawRect(new Rectangle(0, 0, ww, wh), COL_OVERLAY, true);

            // Quiz card
            float cw = 680f, ch = 360f;
            float cx = (ww - cw) / 2f, cy = (wh - ch) / 2f;
            r.drawRect(new Rectangle(cx, cy, cw, ch), COL_QUIZ_BG, true);
            r.drawRect(new Rectangle(cx, cy, cw, ch), Color.CYAN, false);

            QuizManager qm = scene.quizManager;
            String[] opts = qm.getCurrentQuestion().getOptions();
            String   q    = qm.getCurrentQuestion().getQuestion();

            // Header
            String header = qm.isBadEntityQuiz()
                ? "THREAT IDENTIFIED! Answer correctly to neutralise (+100 pts):"
                : "RARE FIND! Answer correctly for +1 Life & +100 pts:";
            Color headerCol = qm.isBadEntityQuiz()
                ? new Color(1f, 0.4f, 0.4f, 1f) : new Color(1f, 0.85f, 0.2f, 1f);
            r.drawText(header, new Vector2(cx + 20f, cy + ch - 28f), "default", headerCol);

            // Question text (simple word-wrap at ~60 chars)
            drawWrappedText(r, q, cx + 20f, cy + ch - 70f, 60, Color.WHITE);

            // Options
            String[] labels = {"1", "2", "3", "4"};
            for (int i = 0; i < 4; i++) {
                float oy = cy + ch - 160f - i * 46f;
                boolean hov = (i == scene.hoveredQuizOption);
                Color bg     = hov ? new Color(0.15f, 0.30f, 0.60f, 0.95f)
                                   : new Color(0.1f,  0.15f, 0.30f, 0.85f);
                Color border = hov ? Color.YELLOW : new Color(0.4f, 0.6f, 0.9f, 0.7f);
                r.drawRect(new Rectangle(cx + 20f, oy, cw - 40f, 38f), bg, true);
                r.drawRect(new Rectangle(cx + 20f, oy, cw - 40f, 38f), border, false);
                r.drawText("[" + labels[i] + "]  " + opts[i],
                    new Vector2(cx + 32f, oy + 26f), "default", hov ? Color.YELLOW : Color.WHITE);
            }

            r.drawText("Press 1 - 4  or  Click to answer",
                new Vector2(cx + 20f, cy + 12f), "default", new Color(0.6f, 0.6f, 0.6f, 1f));
        }

        private void drawWrappedText(Renderer r, String text, float x, float startY,
                                     int lineLen, Color color) {
            String[] words = text.split(" ");
            StringBuilder line = new StringBuilder();
            float y = startY;
            for (String word : words) {
                if (line.length() + word.length() + 1 > lineLen && line.length() > 0) {
                    r.drawText(line.toString(), new Vector2(x, y), "default", color);
                    y -= 22f;
                    line = new StringBuilder();
                }
                if (line.length() > 0) line.append(" ");
                line.append(word);
            }
            if (line.length() > 0) {
                r.drawText(line.toString(), new Vector2(x, y), "default", color);
            }
        }

        // ── Quiz feedback banner ──────────────────────────────────────────────

        private void drawQuizFeedback(Renderer r) {
            float ww = r.getWorldWidth();
            float wh = r.getWorldHeight();

            boolean correct = (scene.getLastQuizResult() == QuizResult.CORRECT);
            boolean wasBad  = scene.isLastQuizBad();

            // Dim overlay
            r.drawRect(new Rectangle(0, 0, ww, wh), new Color(0f, 0f, 0f, 0.55f), true);

            // Banner card
            float cw = 580f, ch = 160f;
            float cx = (ww - cw) / 2f, cy = (wh - ch) / 2f + 30f;
            Color bgColor = correct
                ? new Color(0.05f, 0.25f, 0.08f, 0.95f)
                : new Color(0.25f, 0.05f, 0.05f, 0.95f);
            Color borderColor = correct ? new Color(0.2f, 0.9f, 0.3f, 1f) : new Color(0.9f, 0.2f, 0.2f, 1f);
            r.drawRect(new Rectangle(cx, cy, cw, ch), bgColor, true);
            r.drawRect(new Rectangle(cx, cy, cw, ch), borderColor, false);

            // Result heading
            String heading = correct ? "CORRECT!" : "WRONG!";
            r.drawText(heading,
                new Vector2(ww / 2f - 55f, cy + ch - 34f),
                "default", borderColor);

            // Detail line
            String detail;
            if (correct) {
                int bonus = Math.round(100 * scene.characterType.getScoreMultiplier());
                detail = wasBad
                    ? "Threat neutralised! +" + bonus + " pts"
                    : "+" + bonus + " pts & +1 Life!";
            } else {
                detail = wasBad ? "-1 Life - stay alert!" : "No bonus this time.";
            }
            r.drawText(detail,
                new Vector2(ww / 2f - 140f, cy + ch - 76f),
                "default", Color.WHITE);

            // Progress bar (shrinks as timer counts down)
            float barW = cw - 40f;
            float progress = Math.min(1f, scene.getFeedbackTimer() / 1.5f);
            r.drawRect(new Rectangle(cx + 20f, cy + 18f, barW, 10f),
                new Color(0.2f, 0.2f, 0.2f, 1f), true);
            r.drawRect(new Rectangle(cx + 20f, cy + 18f, barW * progress, 10f),
                borderColor, true);

            r.drawText("SPACE / ENTER to continue",
                new Vector2(ww / 2f - 140f, cy + 42f),
                "default", new Color(0.6f, 0.6f, 0.6f, 1f));
        }

        // ── Frenzy transition banner ─────────────────────────────────────────

        private void drawFrenzyTransition(Renderer r) {
            float ww = r.getWorldWidth();
            float wh = r.getWorldHeight();
            r.drawRect(new Rectangle(0, 0, ww, wh), new Color(0f, 0f, 0f, 0.6f), true);
            r.drawText("CYBER-HYDRA AWAKENS!", new Vector2(ww / 2f - 160f, wh / 2f + 40f),
                "default", COL_FRENZY_BANNER);
            r.drawText("FRENZY MODE INCOMING...",
                new Vector2(ww / 2f - 170f, wh / 2f), "default", Color.YELLOW);
            int secs = (int) Math.ceil(scene.transitionTimer);
            r.drawText("Starting in " + secs + "...",
                new Vector2(ww / 2f - 80f, wh / 2f - 50f), "default", Color.WHITE);
        }

        // ── Buff card selection overlay ──────────────────────────────────────

        private void drawBuffSelect(Renderer r) {
            float ww = r.getWorldWidth(), wh = r.getWorldHeight();

            // Dim the game world behind the overlay
            r.drawRect(new Rectangle(0, 0, ww, wh), new Color(0f, 0f, 0f, 0.72f), true);

            // Title — cardH=389, so card top at wh/2+194; give 20px gap above
            r.drawText("SYSTEM UPGRADE!",
                new Vector2(ww / 2f - 130f, wh / 2f + 228f), "default",
                new Color(0.3f, 1f, 0.6f, 1f));
            r.drawText("Choose a buff:",
                new Vector2(ww / 2f - 78f, wh / 2f + 212f), "default",
                new Color(0.8f, 0.8f, 0.8f, 1f));

            for (int i = 0; i < 3; i++) {
                BuffType  buff = scene.buffChoices[i];
                Rectangle card = scene.buffCardRect(i, ww, wh);
                boolean   sel  = (i == scene.buffHoveredIdx);

                // Sprite fills the entire card at its natural 832x1295 proportions
                r.drawSprite(buff.getCardSprite(),
                    new Vector2(card.x + card.width / 2f, card.y + card.height / 2f),
                    card.width, card.height);

                // Border — full colour when selected, half-brightness when not
                Color accent = buff.getAccentColor();
                float bri = sel ? 1.0f : 0.45f;
                r.drawRect(card,
                    new Color(accent.r * bri, accent.g * bri, accent.b * bri, 1f), false);
                if (sel) {
                    r.drawRect(new Rectangle(card.x + 2f, card.y + 2f,
                        card.width - 4f, card.height - 4f), accent, false);
                }
            }

            // Footer hint
            r.drawText("← → / A D to navigate   1 2 3 or Enter to pick",
                new Vector2(ww / 2f - 270f, wh / 2f - 220f), "default",
                new Color(0.50f, 0.50f, 0.50f, 1f));
        }

        // ── Game-over overlay ────────────────────────────────────────────────

        private void drawGameOverOverlay(Renderer r) {
            float ww = r.getWorldWidth();
            float wh = r.getWorldHeight();
            r.drawRect(new Rectangle(0, 0, ww, wh), new Color(0f, 0f, 0f, 0.75f), true);
            r.drawText("SYSTEM CRASH!", new Vector2(ww / 2f - 100f, wh / 2f + 60f),
                "default", COL_LOSE);
            r.drawText("The network is down. Score: " + scene.score,
                new Vector2(ww / 2f - 170f, wh / 2f + 10f), "default", Color.WHITE);
            r.drawText("Press ENTER to continue",
                new Vector2(ww / 2f - 140f, wh / 2f - 50f), "default",
                new Color(0.7f, 0.7f, 0.7f, 1f));
        }

        // ── Win overlay ──────────────────────────────────────────────────────

        private void drawWinOverlay(Renderer r) {
            float ww = r.getWorldWidth();
            float wh = r.getWorldHeight();
            r.drawRect(new Rectangle(0, 0, ww, wh), new Color(0f, 0f, 0f, 0.72f), true);
            r.drawText("NETWORK SECURED!", new Vector2(ww / 2f - 120f, wh / 2f + 60f),
                "default", COL_WIN);
            r.drawText("Cyber-Hydra defeated! Final score: " + scene.score,
                new Vector2(ww / 2f - 210f, wh / 2f + 10f), "default", Color.WHITE);
            r.drawText("Press ENTER for leaderboard",
                new Vector2(ww / 2f - 165f, wh / 2f - 50f), "default",
                new Color(0.7f, 0.7f, 0.7f, 1f));
        }
    }
}
