package sg.edu.sit.inf1009.p2team2.game.scenes;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Iterator;
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
    private enum GameState { PLAYING, FRENZY, QUIZ, TRANSITION_TO_FRENZY, GAME_OVER, WIN }

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
    private int       goodCollected;   // counter toward GOAL_COUNT
    private float     spawnTimer;
    private float     spawnInterval;
    private float     fallSpeed;
    private float     transitionTimer; // used for TRANSITION_TO_FRENZY pause

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
                // Input handler drives quiz; nothing else updates
                break;
            case GAME_OVER:
            case WIN:
                // handled in input
                break;
        }
    }

    private void updateGameplay(float dt) {
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

        float speed = characterType.getSpeed();
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
            gameState = GameState.QUIZ;
        } else if (gec.isBad()) {
            // Standard bad entity — instant damage
            playerHealth.takeDamage();
            context.getOutputManager().getAudio().playSound(SFX_COLLECT, 0.5f);
            toRemove.add(entity);
            checkGameOver();
        } else {
            // Good entity
            score += Math.round(gec.getScoreValue() * characterType.getScoreMultiplier());
            goodCollected++;
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
                // Neutralised — no damage
            } else {
                playerHealth.takeDamage();
            }
        } else {
            // Good entity quiz (Gold Envelope)
            if (result == QuizResult.CORRECT) {
                playerHealth.gainLife();
            }
            // Wrong answer → no bonus life, but entity still counted as collected
            score        += triggered.get(GameEntityComponent.class).getScoreValue();
            goodCollected++;
        }

        entityManager.removeEntity(triggered.getId());

        if (playerHealth.isDead()) {
            gameState = GameState.GAME_OVER;
        } else {
            GameState returnState = (gameState == GameState.QUIZ)
                ? (goodCollected >= GOAL_COUNT && isBadQuiz ? checkGoalState() : getActivePlayState())
                : gameState;
            gameState = returnState;
            // Re-check goal in case a good entity quiz pushed us over
            if (!isBadQuiz) checkGoal();
        }
    }

    private GameState getActivePlayState() {
        // Decide which play state to return to after a quiz
        return (score > GOAL_COUNT * 5 && goodCollected < GOAL_COUNT) ? GameState.PLAYING : GameState.PLAYING;
    }

    private GameState checkGoalState() { return GameState.PLAYING; }

    // ── Goal/death checks ────────────────────────────────────────────────────

    private void checkGoal() {
        if (gameState == GameState.PLAYING && goodCollected >= FRENZY_COUNT) {
            gameState       = GameState.TRANSITION_TO_FRENZY;
            transitionTimer = 3f;
            goodCollected   = 0;
        } else if (gameState == GameState.FRENZY && goodCollected >= GOAL_COUNT) {
            gameState = GameState.WIN;
        }
    }

    private void checkGameOver() {
        if (playerHealth.isDead()) {
            gameState = GameState.GAME_OVER;
        }
    }

    private void startFrenzyMode() {
        gameState     = GameState.FRENZY;
        fallSpeed     = 320f;
        spawnInterval = 0.7f;
        spawnTimer    = spawnInterval;
        entityManager.clear(); // clear old entities
        // re-add player (clear removed it)
        entityManager.addEntity(playerEntity);
    }

    // ── Transition to next scene ─────────────────────────────────────────────

    void goToGameOver() {
        context.getSceneManager().pop();
        context.getSceneManager().push(new GameOverScene(context, score, leaderboard));
    }

    void goToLeaderboard() {
        leaderboard.addEntry("PLAYER", score);
        context.getSceneManager().pop();
        context.getSceneManager().push(new LeaderboardScene(context, leaderboard));
    }

    // ── Reset ────────────────────────────────────────────────────────────────

    private void resetGame() {
        entityManager.clear();
        gameState     = GameState.PLAYING;
        score         = 0;
        goodCollected = 0;
        fallSpeed     = 200f;
        spawnInterval = 1.4f;
        spawnTimer    = spawnInterval;
        transitionTimer = 0;

        Renderer r = context.getOutputManager().getRenderer();
        playerEntity  = entityFactory.createPlayer(r.getWorldWidth() / 2f, WORLD_FLOOR, characterType.getLives());
        playerHealth  = playerEntity.get(HealthComponent.class);
    }

    // ── Accessors for renderer / input handler ───────────────────────────────

    GameState        getGameState()    { return gameState; }
    int              getScore()        { return score; }
    int              getGoodCollected(){ return goodCollected; }
    HealthComponent  getPlayerHealth() { return playerHealth; }
    Entity           getPlayerEntity() { return playerEntity; }
    EntityManager    getEntityManager(){ return entityManager; }
    QuizManager      getQuizManager()  { return quizManager; }
    float            getTransitionTimer(){ return transitionTimer; }

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
            if (kb.isKeyPressed(Input.Keys.NUM_1)) scene.submitQuizAnswer(0);
            else if (kb.isKeyPressed(Input.Keys.NUM_2)) scene.submitQuizAnswer(1);
            else if (kb.isKeyPressed(Input.Keys.NUM_3)) scene.submitQuizAnswer(2);
            else if (kb.isKeyPressed(Input.Keys.NUM_4)) scene.submitQuizAnswer(3);
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
            String bg;
            if (scene.gameState == GameState.FRENZY) bg = BACKGROUND_FRENZY;
            else if (scene.gameState == GameState.TRANSITION_TO_FRENZY) bg = BACKGROUND_TRANSITION;
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

            // Progress
            String modeLabel = (scene.gameState == GameState.FRENZY) ? "FRENZY" : "NORMAL";
            r.drawText(modeLabel + "  " + scene.goodCollected + " / " + FRENZY_COUNT,
                new Vector2(ww - 240f, wh - 14f), "default",
                scene.gameState == GameState.FRENZY ? COL_FRENZY_BANNER : Color.CYAN);

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
                ? "THREAT IDENTIFIED! Answer correctly to neutralise:"
                : "RARE FIND! Answer correctly for +1 Life:";
            Color headerCol = qm.isBadEntityQuiz()
                ? new Color(1f, 0.4f, 0.4f, 1f) : new Color(1f, 0.85f, 0.2f, 1f);
            r.drawText(header, new Vector2(cx + 20f, cy + ch - 28f), "default", headerCol);

            // Question text (simple word-wrap at ~60 chars)
            drawWrappedText(r, q, cx + 20f, cy + ch - 70f, 60, Color.WHITE);

            // Options
            String[] labels = {"1", "2", "3", "4"};
            for (int i = 0; i < 4; i++) {
                float oy = cy + ch - 160f - i * 46f;
                r.drawRect(new Rectangle(cx + 20f, oy, cw - 40f, 38f),
                    new Color(0.1f, 0.15f, 0.3f, 0.85f), true);
                r.drawRect(new Rectangle(cx + 20f, oy, cw - 40f, 38f),
                    new Color(0.4f, 0.6f, 0.9f, 0.7f), false);
                r.drawText("[" + labels[i] + "]  " + opts[i],
                    new Vector2(cx + 32f, oy + 26f), "default", Color.WHITE);
            }

            r.drawText("Press 1 - 4 to answer",
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
