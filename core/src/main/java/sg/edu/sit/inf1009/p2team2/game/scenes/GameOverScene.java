package sg.edu.sit.inf1009.p2team2.game.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.io.input.Keyboard;
import sg.edu.sit.inf1009.p2team2.engine.io.input.Mouse;
import sg.edu.sit.inf1009.p2team2.engine.io.output.Renderer;
import sg.edu.sit.inf1009.p2team2.engine.scene.InputHandler;
import sg.edu.sit.inf1009.p2team2.engine.scene.ResourceLoader;
import sg.edu.sit.inf1009.p2team2.engine.scene.Scene;
import sg.edu.sit.inf1009.p2team2.engine.scene.SceneRenderer;
import sg.edu.sit.inf1009.p2team2.game.leaderboard.LeaderboardManager;

/**
 * Displayed when the player loses all lives.
 * Step 1: Enter your name. Step 2: Back to Menu or Leaderboard.
 */
public class GameOverScene extends Scene {

    private static final int   MAX_NAME        = 12;
    private static final int   COOLDOWN_FRAMES = 10;
    private static final float BTN_W = 220f, BTN_H = 48f;
    private static final String[] OPTIONS = {"Back to Menu", "Leaderboard"};

    private final int               finalScore;
    private final LeaderboardManager leaderboard;
    private final boolean           isWin;

    // Name-entry phase
    private String  playerName     = "";
    private boolean isEnteringName = true;

    // Button-select phase
    private int selectedIndex;
    private int keyboardCooldown;

    public GameOverScene(EngineContext context, int finalScore, LeaderboardManager leaderboard) {
        this(context, finalScore, leaderboard, false);
    }

    public GameOverScene(EngineContext context, int finalScore, LeaderboardManager leaderboard,
                         boolean isWin) {
        super(context);
        this.finalScore    = finalScore;
        this.leaderboard   = leaderboard;
        this.isWin         = isWin;
        this.selectedIndex = 0;

        setInputHandler(new GameOverInputHandler(this));
        setSceneRenderer(new GameOverRenderer(this));
        setResourceLoader(new ResourceLoader() {
            @Override public void load()   { setLoaded(true); }
            @Override public void unload() { setLoaded(false); }
        });
    }

    @Override
    public void onEnter() {
        playerName       = "";
        isEnteringName   = true;
        selectedIndex    = 0;
        keyboardCooldown = COOLDOWN_FRAMES;
    }

    @Override
    public void update(float dt) {
        if (keyboardCooldown > 0) keyboardCooldown--;
    }

    // ── Name confirmation ─────────────────────────────────────────────────────

    private void confirmName() {
        String name = playerName.trim().isEmpty() ? "PLAYER" : playerName.trim().toUpperCase();
        leaderboard.addEntry(name, finalScore);
        isEnteringName   = false;
        keyboardCooldown = COOLDOWN_FRAMES;
    }

    // ── Button rect helper ────────────────────────────────────────────────────

    private Rectangle buttonRect(float ww, float wh, int index) {
        float startY = wh / 2f - 20f;
        float by     = startY - index * 65f;
        return new Rectangle(ww / 2f - BTN_W / 2f, by - 8f, BTN_W, BTN_H);
    }

    // ── Input processing ──────────────────────────────────────────────────────

    void processInput() {
        Renderer r  = context.getOutputManager().getRenderer();
        float ww = r.getWorldWidth(), wh = r.getWorldHeight();

        if (isEnteringName) {
            handleNameInput();
        } else {
            handleMenuInput(ww, wh);
        }
    }

    private void handleNameInput() {
        // Backspace
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE) && playerName.length() > 0) {
            playerName = playerName.substring(0, playerName.length() - 1);
        }

        // Letters A–Z
        for (int k = Input.Keys.A; k <= Input.Keys.Z; k++) {
            if (Gdx.input.isKeyJustPressed(k) && playerName.length() < MAX_NAME) {
                char c = (char) ('A' + (k - Input.Keys.A));
                playerName += c;
            }
        }

        // Digits 0–9 (main row: NUM_0..NUM_9)
        for (int k = Input.Keys.NUM_0; k <= Input.Keys.NUM_9; k++) {
            if (Gdx.input.isKeyJustPressed(k) && playerName.length() < MAX_NAME) {
                char c = (char) ('0' + (k - Input.Keys.NUM_0));
                playerName += c;
            }
        }

        // Confirm
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            confirmName();
        }
        // Skip with default
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            playerName = "";
            confirmName();
        }
    }

    private void handleMenuInput(float ww, float wh) {
        Keyboard kb    = context.getInputManager().getKeyboard();
        Mouse    mouse = context.getInputManager().getMouse();

        if (keyboardCooldown == 0) {
            if (kb.isKeyPressed(Input.Keys.UP) || kb.isKeyPressed(Input.Keys.W)) {
                selectedIndex    = (selectedIndex - 1 + OPTIONS.length) % OPTIONS.length;
                keyboardCooldown = COOLDOWN_FRAMES;
            } else if (kb.isKeyPressed(Input.Keys.DOWN) || kb.isKeyPressed(Input.Keys.S)) {
                selectedIndex    = (selectedIndex + 1) % OPTIONS.length;
                keyboardCooldown = COOLDOWN_FRAMES;
            }
        }

        if (kb.isKeyPressed(Input.Keys.ENTER) || kb.isKeyPressed(Input.Keys.SPACE)) {
            activate(selectedIndex);
            return;
        }

        Vector2 mp = mouse.getPosition();
        for (int i = 0; i < OPTIONS.length; i++) {
            if (buttonRect(ww, wh, i).contains(mp.x, mp.y)) {
                selectedIndex = i;
                if (mouse.isButtonPressed(0)) {
                    activate(i);
                    return;
                }
            }
        }
    }

    private void activate(int index) {
        switch (OPTIONS[index]) {
            case "Back to Menu":
                getContext().getSceneManager().pop();
                break;
            case "Leaderboard":
                getContext().getSceneManager().push(new LeaderboardScene(getContext(), leaderboard));
                break;
        }
    }

    // ── Render ────────────────────────────────────────────────────────────────

    void renderScene() {
        Renderer r  = getContext().getOutputManager().getRenderer();
        float ww = r.getWorldWidth();
        float wh = r.getWorldHeight();

        r.clear();
        r.begin();

        r.drawBackground("win-scene.png");
        r.drawRect(new Rectangle(0, 0, ww, wh), new Color(0f, 0f, 0f, 0.65f), true);

        // Header — win vs loss variant
        if (isWin) {
            r.drawText("NETWORK SECURED!",
                new Vector2(ww / 2f - 125f, wh / 2f + 165f), "default",
                new Color(0.15f, 0.95f, 0.40f, 1f));
            r.drawText("Cyber-Hydra defeated! Save your name.",
                new Vector2(ww / 2f - 215f, wh / 2f + 115f), "default", Color.WHITE);
        } else {
            r.drawText("SYSTEM CRASH",
                new Vector2(ww / 2f - 105f, wh / 2f + 165f), "default",
                new Color(0.95f, 0.25f, 0.25f, 1f));
            r.drawText("The network has been compromised.",
                new Vector2(ww / 2f - 195f, wh / 2f + 115f), "default", Color.WHITE);
        }
        r.drawText("Final Score: " + finalScore,
            new Vector2(ww / 2f - 90f, wh / 2f + 68f), "default",
            new Color(1f, 0.85f, 0.2f, 1f));

        if (isEnteringName) {
            renderNameEntry(r, ww, wh);
        } else {
            renderButtons(r, ww, wh);
        }

        r.end();
    }

    private void renderNameEntry(Renderer r, float ww, float wh) {
        float boxW  = 320f, boxH = 52f;
        float boxX  = ww / 2f - boxW / 2f;
        float boxY  = wh / 2f - 10f;

        r.drawText("Enter your name:",
            new Vector2(ww / 2f - 100f, boxY + boxH + 24f), "default",
            new Color(0.7f, 0.9f, 1f, 1f));

        // Input box
        r.drawRect(new Rectangle(boxX, boxY, boxW, boxH),
            new Color(0.05f, 0.12f, 0.22f, 0.95f), true);
        r.drawRect(new Rectangle(boxX, boxY, boxW, boxH),
            new Color(0.3f, 0.7f, 1f, 1f), false);

        // Typed text + blinking cursor
        boolean cursorOn = (System.currentTimeMillis() / 500) % 2 == 0;
        String display   = playerName + (cursorOn ? "|" : " ");
        r.drawText(display,
            new Vector2(boxX + 12f, boxY + boxH / 2f + 10f), "default", Color.WHITE);

        r.drawText("ENTER to save   ESC to skip",
            new Vector2(ww / 2f - 155f, boxY - 38f), "default",
            new Color(0.5f, 0.5f, 0.5f, 1f));
    }

    private void renderButtons(Renderer r, float ww, float wh) {
        for (int i = 0; i < OPTIONS.length; i++) {
            boolean   sel = (i == selectedIndex);
            Rectangle box = buttonRect(ww, wh, i);
            Color bg  = sel ? (isWin ? new Color(0.1f, 0.5f, 0.15f, 0.85f)
                                     : new Color(0.7f, 0.1f, 0.1f, 0.85f))
                            : new Color(0.15f, 0.15f, 0.15f, 0.65f);
            r.drawRect(box, bg, true);
            r.drawRect(box, sel ? Color.YELLOW : Color.WHITE, false);
            r.drawText(OPTIONS[i],
                new Vector2(box.x + BTN_W / 2f - 55f, box.y + BTN_H / 2f + 10f),
                "default", sel ? Color.YELLOW : Color.WHITE);
        }

        r.drawText("W / S or Mouse   Enter / Click to Select",
            new Vector2(ww / 2f - 225f, 30f), "default",
            new Color(0.55f, 0.55f, 0.55f, 1f));
    }

    // ── Inner classes ─────────────────────────────────────────────────────────

    private static final class GameOverInputHandler extends InputHandler {
        private final GameOverScene scene;
        GameOverInputHandler(GameOverScene s) { super(s.getContext()); this.scene = s; }
        @Override public void handleInput() { scene.processInput(); }
    }

    private static final class GameOverRenderer extends SceneRenderer {
        private final GameOverScene scene;
        GameOverRenderer(GameOverScene s) { super(s.getContext()); this.scene = s; }
        @Override public void render() { scene.renderScene(); }
    }
}
