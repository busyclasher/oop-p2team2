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
 * Silicon Sentinel — main gameplay scene.
 *
 * The T-Rex (player) moves left/right at the bottom of the screen.
 * Entities fall from the top; the player catches good ones and avoids bad ones.
 *
 * Game flow:
 *   Standard mode  → collect 100 good entities  → Frenzy mode
 *   Frenzy mode    → collect 100 good entities  → Win → LeaderboardScene
 *   0 lives         → GameOverScene
 *
 * Design patterns used:
 *   Factory  – EntityFactory creates entities by type.
 *   Strategy – InputHandler / SceneRenderer per-scene strategy.
 *   State    – GameState enum drives update/render branching.
 *   Observer – QuizManager decouples quiz triggers from gameplay state.
 */
public class GamePlayScene extends Scene {

    // ── Constants ────────────────────────────────────────────────────────────
    private static final String BACKGROUND_NORMAL     = "game-scene.png";
    private static final String BACKGROUND_FRENZY     = "frenzy-scene.png"; // swap if a frenzy bg exists
    private static final String BACKGROUND_TRANSITION = "headphone-girl-listening.png"; // swap for transition bg
    private static final String MUSIC_ID          = "game-theme";
    private static final String SFX_COLLECT       = "spawn-marker";

    private static final float WORLD_FLOOR        = 110f;   // y where entities "land"

    private static final float SPAWN_Y            = 750f;
    private static final float SPAWN_MARGIN       = 60f;
    private static final int FRENZY_COUNT         = 10;
    private static final int   GOAL_COUNT         = 100;

    // Difficulty scaling (PLAYING mode only)
    private static final float DIFF_TICK          = 15f;   // seconds between each ramp-up
    private static final float FALL_SPEED_STEP    = 20f;   // px/s added per tick
    private static final float FALL_SPEED_MAX     = 300f;  // cap before frenzy takes over
    private static final float SPAWN_INTERVAL_STEP= 0.10f; // seconds removed per tick
    private static final float SPAWN_INTERVAL_MIN = 0.65f; // floor before frenzy

    // Frenzy mode
    private static final float FRENZY_DURATION    = 30f;   // seconds before returning to PLAYING
    private static final float FRENZY_DIFF_TICK   = 6f;    // ramp-up interval inside frenzy
    private static final float FRENZY_FALL_STEP   = 20f;   // px/s added per frenzy tick
    private static final float FRENZY_SPAWN_STEP  = 0.05f; // interval removed per frenzy tick
    private static final float FRENZY_FALL_MAX    = 600f;  // speed cap inside frenzy
    private static final float FRENZY_SPAWN_MIN   = 0.30f; // interval floor inside frenzy

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
    private int       goodCollected;      // resets each frenzy cycle (frenzy trigger)
    private int       totalGoodCollected; // running total for WIN condition
    private float     spawnTimer;
    private float     spawnInterval;
    private float     fallSpeed;
    private float     transitionTimer; // used for TRANSITION_TO_FRENZY pause
    private float     difficultyTimer; // accumulates time for difficulty ramp-up
    float             frenzyTimer;     // counts down while in FRENZY
    private float     frenzyDiffTimer; // ramp-up timer inside frenzy
    private int       frenzyCount;     // how many frenzy cycles have ended

    // Quiz feedback
    private QuizResult lastQuizResult;
    private boolean    lastQuizWasBad;
    int                hoveredQuizOption = -1; // -1 = none
    private float      feedbackTimer;
    private GameState  postFeedbackState;
    private GameState  preQuizState;      // state before quiz was triggered

    // Buff system — card offered every BUFF_INTERVAL points
    private static final int BUFF_INTERVAL = 200;
    private int        nextBuffScore    = BUFF_INTERVAL;
    BuffType[]         buffChoices      = new BuffType[3]; // package-private for renderer
    int                buffHoveredIdx   = 0;               // package-private for renderer
    private GameState  preBuffState;

    // Active buff state
    boolean  hasShield        = false; // package-private for HUD indicator
    private float    playerSpeedBonus = 0f;
    private float    buffScoreMulti   = 1f;
    private int      buffScoreItems   = 0;
    boolean  hasScoreBoost    = false; // package-private for HUD indicator

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
        context.getOutputManager().getAudio().playMusic(MUSIC_ID, true);
    }

    @Override
    public void onExit() {
        context.getOutputManager().getAudio().stopMusic();
    }

    private void loadResources() {
        Audio audio = context.getOutputManager().getAudio();
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
            // Progressive difficulty ramp in PLAYING mode
            difficultyTimer += dt;
            if (difficultyTimer >= DIFF_TICK) {
                difficultyTimer -= DIFF_TICK;
                fallSpeed     = Math.min(fallSpeed     + FALL_SPEED_STEP,    FALL_SPEED_MAX);
                spawnInterval = Math.max(spawnInterval - SPAWN_INTERVAL_STEP, SPAWN_INTERVAL_MIN);
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
            if (tf.getPosition().y + EntityFactory.ENTITY_SIZE < 0) {
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
        Keyboard kb = context.getInputManager().getKeyboard();
        TransformComponent tf = playerEntity.get(TransformComponent.class);
        Renderer r  = context.getOutputManager().getRenderer();

        float speed = characterType.getSpeed() + playerSpeedBonus;
        float dx = 0;
        if (kb.isKeyDown(Input.Keys.LEFT)  || kb.isKeyDown(Input.Keys.A)) dx -= speed * dt;
        if (kb.isKeyDown(Input.Keys.RIGHT) || kb.isKeyDown(Input.Keys.D)) dx += speed * dt;

        float newX = tf.getPosition().x + dx;
        float half = EntityFactory.PLAYER_WIDTH / 2f;
        newX = Math.max(half, Math.min(r.getWorldWidth() - half, newX));
        tf.getPosition().x = newX;
    }

    private void spawnRandomEntity() {
        EntityType[] pool = (gameState == GameState.FRENZY) ? FRENZY_TYPES : STANDARD_TYPES;
        EntityType type   = pool[random.nextInt(pool.length)];

        Renderer r  = context.getOutputManager().getRenderer();
        float spawnX = SPAWN_MARGIN + random.nextFloat() * (r.getWorldWidth() - SPAWN_MARGIN * 2);

        entityFactory.createFallingEntity(type, spawnX, SPAWN_Y, fallSpeed);
    }

    private boolean overlapsPlayer(Entity entity) {
        TransformComponent playerTf = playerEntity.get(TransformComponent.class);
        TransformComponent entTf    = entity.get(TransformComponent.class);

        float pw = EntityFactory.PLAYER_WIDTH;
        float ph = EntityFactory.PLAYER_HEIGHT;
        float ew = EntityFactory.ENTITY_SIZE;

        float px = playerTf.getPosition().x - pw / 2;
        float py = playerTf.getPosition().y;
        float ex = entTf.getPosition().x  - ew / 2;
        float ey = entTf.getPosition().y  - ew / 2;

        return px < ex + ew && px + pw > ex
            && py < ey + ew && py + ph > ey;
    }

    private void handleCollision(Entity entity, List<Entity> toRemove) {
        GameEntityComponent gec = entity.get(GameEntityComponent.class);
        if (gec == null || gec.isCollected()) return;
        gec.markCollected();

        if (gec.isQuizTrigger()) {
            // Freeze game and show quiz overlay
            entity.get(FallingComponent.class).deactivate();
            quizManager.triggerQuiz(entity);
            preQuizState = gameState;
            gameState = GameState.QUIZ;
        } else if (gec.isBad()) {
            if (hasShield) {
                // Shield absorbs the hit
                hasShield = false;
                context.getOutputManager().getAudio().playSound(SFX_COLLECT, 1.0f);
            } else {
                playerHealth.takeDamage();
                context.getOutputManager().getAudio().playSound(SFX_COLLECT, 0.5f);
                checkGameOver();
            }
            toRemove.add(entity);
        } else {
            // Good entity — apply score-boost buff if active
            float effective = characterType.getScoreMultiplier();
            if (buffScoreItems > 0) {
                effective *= buffScoreMulti;
                if (--buffScoreItems == 0) {
                    buffScoreMulti  = 1f;
                    hasScoreBoost   = false;
                }
            }
            score += Math.round(gec.getScoreValue() * effective);
            goodCollected++;
            totalGoodCollected++;
            context.getOutputManager().getAudio().playSound(SFX_COLLECT, 0.8f);
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
                playerHealth.takeDamage();
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
            if (!isBadQuiz) checkGoal(); // may update gameState to WIN or TRANSITION_TO_FRENZY
            postFeedbackState = gameState;
        }

        lastQuizResult = result;
        lastQuizWasBad = isBadQuiz;
        feedbackTimer  = 1.5f;
        gameState      = GameState.QUIZ_FEEDBACK;
    }

    // ── Goal/death checks ────────────────────────────────────────────────────

    private void checkGoal() {
        // Overall win condition — 100 total good items across both modes
        if (totalGoodCollected >= GOAL_COUNT) {
            gameState = GameState.WIN;
            return;
        }
        // Buff card threshold — offer every BUFF_INTERVAL points
        if (score >= nextBuffScore && gameState != GameState.BUFF_SELECT) {
            triggerBuffSelect();
            return; // frenzy check deferred; applyBuff() calls checkGoal() again
        }
        // Frenzy trigger — 10 items in the current PLAYING cycle
        if (gameState == GameState.PLAYING && goodCollected >= FRENZY_COUNT) {
            gameState       = GameState.TRANSITION_TO_FRENZY;
            transitionTimer = 3f;
            goodCollected   = 0;
        }
    }

    private void triggerBuffSelect() {
        nextBuffScore += BUFF_INTERVAL;
        // Fisher-Yates shuffle of all buff types, take first 3
        BuffType[] all = BuffType.values().clone();
        for (int i = all.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            BuffType tmp = all[i]; all[i] = all[j]; all[j] = tmp;
        }
        buffChoices    = new BuffType[]{all[0], all[1], all[2]};
        buffHoveredIdx = 0;
        preBuffState   = gameState;
        gameState      = GameState.BUFF_SELECT;
    }

    void applyBuff(BuffType buff) {
        switch (buff) {
            case EXTRA_LIFE:
                playerHealth.gainLife();
                break;
            case SHIELD:
                hasShield = true;
                break;
            case SPEED_SURGE:
                playerSpeedBonus += characterType.getSpeed() * 0.25f;
                break;
            case SCORE_BOOST:
                buffScoreMulti = 1.75f;
                buffScoreItems = 20;
                hasScoreBoost  = true;
                break;
            case SLOW_FIELD:
                fallSpeed = Math.max(fallSpeed * 0.85f, 60f);
                break;
            case SCORE_BURST:
                score += 300;
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

    private void startFrenzyMode() {
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
        // Each completed frenzy cycle permanently raises the PLAYING base difficulty
        int bonusTicks = frenzyCount * 5;
        fallSpeed     = Math.min(200f + bonusTicks * FALL_SPEED_STEP,    450f);
        spawnInterval = Math.max(1.4f - bonusTicks * SPAWN_INTERVAL_STEP, 0.45f);
        spawnTimer      = spawnInterval;
        difficultyTimer = 0;
        goodCollected   = 0;
        gameState       = GameState.PLAYING;
        entityManager.clear();
        entityManager.addEntity(playerEntity);
    }

    // ── Transition to next scene ─────────────────────────────────────────────

    void goToGameOver() {
        context.getSceneManager().pop();
        context.getSceneManager().push(new GameOverScene(context, score, leaderboard));
    }

    void goToLeaderboard() {
        context.getSceneManager().pop();
        context.getSceneManager().push(new GameOverScene(context, score, leaderboard, true));
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
        transitionTimer = 0;
        difficultyTimer = 0;
        frenzyTimer     = 0;
        frenzyDiffTimer = 0;
        frenzyCount     = 0;
        // Buff state
        nextBuffScore    = BUFF_INTERVAL;
        hasShield        = false;
        playerSpeedBonus = 0f;
        buffScoreMulti   = 1f;
        buffScoreItems   = 0;
        hasScoreBoost    = false;

        Renderer r = context.getOutputManager().getRenderer();
        playerEntity  = entityFactory.createPlayer(r.getWorldWidth() / 2f, WORLD_FLOOR, characterType.getLives());
        playerHealth  = playerEntity.get(HealthComponent.class);
    }

    // ── Accessors for renderer / input handler ───────────────────────────────

    /** Shared card rectangle used by both input handler and renderer. */
    Rectangle buffCardRect(int idx, float ww, float wh) {
        float cardW   = 200f, cardH = 280f, gap = 30f;
        float totalW  = 3 * cardW + 2 * gap;
        float startX  = ww / 2f - totalW / 2f;
        float x       = startX + idx * (cardW + gap);
        float y       = wh / 2f - cardH / 2f;
        return new Rectangle(x, y, cardW, cardH);
    }

    GameState        getGameState()         { return gameState; }
    int              getScore()             { return score; }
    int              getGoodCollected()     { return goodCollected; }
    int              getTotalGoodCollected(){ return totalGoodCollected; }
    HealthComponent  getPlayerHealth() { return playerHealth; }
    Entity           getPlayerEntity() { return playerEntity; }
    EntityManager    getEntityManager(){ return entityManager; }
    QuizManager      getQuizManager()  { return quizManager; }
    float            getTransitionTimer(){ return transitionTimer; }
    QuizResult       getLastQuizResult(){ return lastQuizResult; }
    boolean          isLastQuizBad()   { return lastQuizWasBad; }
    float            getFeedbackTimer() { return feedbackTimer; }

    // =========================================================================
    // Inner - InputHandler
    // =========================================================================

    private static final class GamePlayInputHandler extends InputHandler {
        private final GamePlayScene scene;

        GamePlayInputHandler(GamePlayScene scene) {
            super(scene.context);
            this.scene = scene;
        }

        @Override
        public void handleInput() {
            Keyboard kb = context.getInputManager().getKeyboard();

            switch (scene.gameState) {
                case PLAYING:
                case FRENZY:
                    if (kb.isKeyPressed(Input.Keys.ESCAPE)) {
                        scene.context.getSceneManager().push(new PauseScene(scene.context));
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
            Mouse  mouse = scene.context.getInputManager().getMouse();
            Renderer r   = scene.context.getOutputManager().getRenderer();
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
            Mouse    mouse = scene.context.getInputManager().getMouse();
            Renderer r     = scene.context.getOutputManager().getRenderer();
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
            super(scene.context);
            this.scene = scene;
        }

        @Override
        public void render() {
            Renderer r = context.getOutputManager().getRenderer();
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
                    w = h = EntityFactory.ENTITY_SIZE;
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

            // Progress / frenzy countdown
            String progressText;
            Color  progressColor;
            GameState displayState = (scene.gameState == GameState.BUFF_SELECT)
                ? scene.preBuffState : scene.gameState;
            if (displayState == GameState.FRENZY) {
                int secsLeft = Math.max(0, (int) Math.ceil(scene.frenzyTimer));
                progressText  = "FRENZY " + secsLeft + "s  TOTAL " + scene.totalGoodCollected + "/" + GOAL_COUNT;
                progressColor = COL_FRENZY_BANNER;
            } else {
                progressText  = "NEXT " + scene.goodCollected + "/" + FRENZY_COUNT
                              + "  TOTAL " + scene.totalGoodCollected + "/" + GOAL_COUNT;
                progressColor = Color.CYAN;
            }
            r.drawText(progressText, new Vector2(ww - 360f, wh - 14f), "default", progressColor);

            // Active buff indicators (bottom-right)
            float ix = ww - 20f;
            if (scene.hasShield) {
                r.drawText("[SHIELD]", new Vector2(ix - 130f, 12f),
                    "default", new Color(0.2f, 0.55f, 1f, 1f));
                ix -= 140f;
            }
            if (scene.hasScoreBoost) {
                r.drawText("[BOOST x" + scene.buffScoreItems + "]",
                    new Vector2(ix - 160f, 12f), "default", new Color(1f, 0.5f, 0.1f, 1f));
                ix -= 170f;
            }

            // Controls hint
            r.drawText("A or D to Move   ESC Quit",
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

            // Dim the game world behind the cards
            r.drawRect(new Rectangle(0, 0, ww, wh), new Color(0f, 0f, 0f, 0.70f), true);

            // Title
            r.drawText("SYSTEM UPGRADE!",
                new Vector2(ww / 2f - 130f, wh / 2f + 175f), "default",
                new Color(0.3f, 1f, 0.6f, 1f));
            r.drawText("Choose a buff:",
                new Vector2(ww / 2f - 78f, wh / 2f + 140f), "default",
                new Color(0.8f, 0.8f, 0.8f, 1f));

            // Three cards
            for (int i = 0; i < 3; i++) {
                BuffType buff = scene.buffChoices[i];
                Rectangle card = scene.buffCardRect(i, ww, wh);
                boolean   sel  = (i == scene.buffHoveredIdx);

                // Card background + border (tinted with buff colour)
                Color dimBg = new Color(buff.color.r * 0.15f, buff.color.g * 0.15f,
                                        buff.color.b * 0.15f, 0.92f);
                r.drawRect(card, sel ? dimBg : new Color(0.08f, 0.08f, 0.08f, 0.88f), true);
                r.drawRect(card, sel ? buff.color : new Color(0.4f, 0.4f, 0.4f, 1f), false);

                // Number hint
                r.drawText("[" + (i + 1) + "]",
                    new Vector2(card.x + 8f, card.y + card.height - 22f),
                    "default", new Color(0.55f, 0.55f, 0.55f, 1f));

                // Buff name (coloured)
                r.drawText(buff.name,
                    new Vector2(card.x + 10f, card.y + card.height - 58f),
                    "default", sel ? buff.color : Color.WHITE);

                // Divider line
                r.drawRect(new Rectangle(card.x + 8f, card.y + card.height - 72f,
                    card.width - 16f, 1f), new Color(0.35f, 0.35f, 0.35f, 1f), true);

                // Description (two lines if \n present)
                String[] lines = buff.desc.split("\n");
                for (int l = 0; l < lines.length; l++) {
                    r.drawText(lines[l],
                        new Vector2(card.x + 10f, card.y + card.height - 102f - l * 26f),
                        "default", new Color(0.80f, 0.80f, 0.80f, 1f));
                }
            }

            // Footer hint
            r.drawText("← → / A D to navigate   1 2 3 or Enter to pick",
                new Vector2(ww / 2f - 270f, wh / 2f - 175f), "default",
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
