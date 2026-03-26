package sg.edu.sit.inf1009.p2team2.game.scenes;

import sg.edu.sit.inf1009.p2team2.engine.io.input.Keys;
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
import sg.edu.sit.inf1009.p2team2.game.audio.GameAudio;
import sg.edu.sit.inf1009.p2team2.game.leaderboard.LeaderboardManager;
import sg.edu.sit.inf1009.p2team2.game.ui.GameUiTheme;

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
        GameAudio.playUiClick(getContext());
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
        Renderer r  = getContext().getOutputManager().getRenderer();
        float ww = r.getWorldWidth(), wh = r.getWorldHeight();
        Keyboard kb = getContext().getInputManager().getKeyboard();

        if (isEnteringName) {
            handleNameInput(kb);
        } else {
            handleMenuInput(ww, wh);
        }
    }

    private void handleNameInput(Keyboard kb) {
        // Backspace
        if (kb.isKeyPressed(Keys.BACKSPACE) && playerName.length() > 0) {
            playerName = playerName.substring(0, playerName.length() - 1);
        }

        // Letters A–Z
        for (int k = Keys.A; k <= Keys.Z; k++) {
            if (kb.isKeyPressed(k) && playerName.length() < MAX_NAME) {
                char c = (char) ('A' + (k - Keys.A));
                playerName += c;
            }
        }

        // Digits 0–9 (main row: NUM_0..NUM_9)
        for (int k = Keys.NUM_0; k <= Keys.NUM_9; k++) {
            if (kb.isKeyPressed(k) && playerName.length() < MAX_NAME) {
                char c = (char) ('0' + (k - Keys.NUM_0));
                playerName += c;
            }
        }

        // Confirm
        if (kb.isKeyPressed(Keys.ENTER)) {
            confirmName();
        }
        // Skip with default
        if (kb.isKeyPressed(Keys.ESCAPE)) {
            playerName = "";
            confirmName();
        }
    }

    private void handleMenuInput(float ww, float wh) {
        Keyboard kb    = getContext().getInputManager().getKeyboard();
        Mouse    mouse = getContext().getInputManager().getMouse();

        if (keyboardCooldown == 0) {
            if (kb.isKeyPressed(Keys.UP) || kb.isKeyPressed(Keys.W)) {
                selectedIndex    = (selectedIndex - 1 + OPTIONS.length) % OPTIONS.length;
                keyboardCooldown = COOLDOWN_FRAMES;
                GameAudio.playUiClick(getContext());
            } else if (kb.isKeyPressed(Keys.DOWN) || kb.isKeyPressed(Keys.S)) {
                selectedIndex    = (selectedIndex + 1) % OPTIONS.length;
                keyboardCooldown = COOLDOWN_FRAMES;
                GameAudio.playUiClick(getContext());
            }
        }

        if (kb.isKeyPressed(Keys.ENTER) || kb.isKeyPressed(Keys.SPACE)) {
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
        GameAudio.playUiClick(getContext());
        switch (OPTIONS[index]) {
            case "Back to Menu":
                getContext().getSceneManager().pop();
                break;
            case "Leaderboard":
                // Replace this scene instead of stacking on top of it to
                // keep menu/leaderboard navigation deterministic.
                getContext().getSceneManager().pop();
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
            r.drawTextCentered("NETWORK SECURED!",
                new Vector2(ww / 2f, wh / 2f + 165f), GameUiTheme.FONT_TITLE_SMALL,
                GameUiTheme.TEXT_SUCCESS);
            r.drawTextCentered("Cyber-Hydra defeated! Save your name.",
                new Vector2(ww / 2f, wh / 2f + 116f), GameUiTheme.FONT_BODY_LARGE,
                GameUiTheme.TEXT_PRIMARY);
        } else {
            r.drawTextCentered("SYSTEM CRASH",
                new Vector2(ww / 2f, wh / 2f + 165f), GameUiTheme.FONT_TITLE_SMALL,
                GameUiTheme.TEXT_DANGER);
            r.drawTextCentered("The network has been compromised.",
                new Vector2(ww / 2f, wh / 2f + 116f), GameUiTheme.FONT_BODY_LARGE,
                GameUiTheme.TEXT_PRIMARY);
        }
        r.drawTextCentered("Final Score: " + finalScore,
            new Vector2(ww / 2f, wh / 2f + 70f), GameUiTheme.FONT_BODY_LARGE,
            GameUiTheme.TEXT_HIGHLIGHT);

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
        float boxY  = wh / 2f - 70f;

        r.drawTextCentered("Enter your name:",
            new Vector2(ww / 2f, boxY + boxH + 26f), GameUiTheme.FONT_BODY_LARGE,
            GameUiTheme.TEXT_INFO);

        // Input box
        r.drawRect(new Rectangle(boxX, boxY, boxW, boxH),
            new Color(0.05f, 0.12f, 0.22f, 0.95f), true);
        r.drawRect(new Rectangle(boxX, boxY, boxW, boxH),
            new Color(0.3f, 0.7f, 1f, 1f), false);

        // Typed text + blinking cursor
        boolean cursorOn = (System.currentTimeMillis() / 500) % 2 == 0;
        String display   = playerName + (cursorOn ? "|" : " ");
        r.drawText(display,
            new Vector2(boxX + 12f, boxY + boxH / 2f + 10f), GameUiTheme.FONT_BODY_LARGE,
            GameUiTheme.TEXT_PRIMARY);

        r.drawTextCentered("ENTER to save   ESC to skip",
            new Vector2(ww / 2f, boxY - 36f), GameUiTheme.FONT_BODY_SMALL,
            GameUiTheme.TEXT_SUBTLE);
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
            r.drawTextCentered(OPTIONS[i], box, GameUiTheme.FONT_BODY_LARGE,
                sel ? GameUiTheme.TEXT_HIGHLIGHT : GameUiTheme.TEXT_PRIMARY);
        }

        r.drawTextCentered("W / S or Mouse   Enter / Click to Select",
            new Vector2(ww / 2f, 30f), GameUiTheme.FONT_BODY_SMALL,
            GameUiTheme.TEXT_SUBTLE);
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
