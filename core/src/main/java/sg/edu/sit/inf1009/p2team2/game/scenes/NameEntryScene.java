package sg.edu.sit.inf1009.p2team2.game.scenes;

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
 * Allows the player to enter a display name (up to 12 characters) before
 * starting the game. The name is persisted in LeaderboardManager and used
 * for all future leaderboard entries.
 */
public class NameEntryScene extends Scene {

    private static final int   MAX_LEN    = 12;
    private static final float CARD_W     = 500f;
    private static final float CARD_H     = 260f;
    private static final float BTN_W      = 200f;
    private static final float BTN_H      = 50f;
    private static final float CURSOR_BLINK_RATE = 0.5f;
    private static final int   COOLDOWN_FRAMES   = 6;

    private final LeaderboardManager leaderboard;
    private final boolean            firstTime;
    private final StringBuilder      nameBuffer;

    private float cursorTimer;
    private boolean cursorVisible;
    private int keyboardCooldown;

    /**
     * @param firstTime true when the player has never set a name (blocks ESC back)
     */
    public NameEntryScene(EngineContext context, LeaderboardManager leaderboard, boolean firstTime) {
        super(context);
        this.leaderboard = leaderboard;
        this.firstTime   = firstTime;
        this.nameBuffer  = new StringBuilder(
            leaderboard.hasPlayerName() ? leaderboard.getPlayerName() : "");

        setInputHandler(new NameEntryInputHandler(this));
        setSceneRenderer(new NameEntryRenderer(this));
        setResourceLoader(new ResourceLoader() {
            @Override public void load()   { setLoaded(true); }
            @Override public void unload() { setLoaded(false); }
        });
    }

    @Override
    public void onEnter() {
        keyboardCooldown = COOLDOWN_FRAMES;
        cursorTimer      = 0;
        cursorVisible    = true;
    }

    @Override
    public void update(float dt) {
        if (keyboardCooldown > 0) keyboardCooldown--;
        cursorTimer += dt;
        if (cursorTimer >= CURSOR_BLINK_RATE) {
            cursorTimer -= CURSOR_BLINK_RATE;
            cursorVisible = !cursorVisible;
        }
    }

    void processInput() {
        Keyboard kb    = context.getInputManager().getKeyboard();
        Mouse    mouse = context.getInputManager().getMouse();
        Renderer r     = context.getOutputManager().getRenderer();
        float cx = r.getWorldWidth() / 2f, cy = r.getWorldHeight() / 2f;

        if (!firstTime && kb.isKeyPressed(Input.Keys.ESCAPE)) {
            context.getSceneManager().pop();
            return;
        }

        if (kb.isKeyPressed(Input.Keys.ENTER)) {
            confirm();
            return;
        }

        if (kb.isKeyPressed(Input.Keys.BACKSPACE) || kb.isKeyPressed(Input.Keys.DEL)) {
            if (nameBuffer.length() > 0) {
                nameBuffer.deleteCharAt(nameBuffer.length() - 1);
            }
            return;
        }

        if (nameBuffer.length() < MAX_LEN) {
            for (int key = Input.Keys.A; key <= Input.Keys.Z; key++) {
                if (kb.isKeyPressed(key)) {
                    char c = (char) ('A' + (key - Input.Keys.A));
                    nameBuffer.append(c);
                    return;
                }
            }
            for (int key = Input.Keys.NUM_0; key <= Input.Keys.NUM_9; key++) {
                if (kb.isKeyPressed(key)) {
                    char c = (char) ('0' + (key - Input.Keys.NUM_0));
                    nameBuffer.append(c);
                    return;
                }
            }
            if (kb.isKeyPressed(Input.Keys.SPACE) && nameBuffer.length() > 0) {
                nameBuffer.append(' ');
                return;
            }
            if (kb.isKeyPressed(Input.Keys.MINUS)) {
                nameBuffer.append('-');
                return;
            }
            if (kb.isKeyPressed(Input.Keys.PERIOD)) {
                nameBuffer.append('.');
                return;
            }
        }

        Rectangle btn = confirmBtnRect(cx, cy);
        if (mouse.isButtonPressed(0) && btn.contains(mouse.getPosition().x, mouse.getPosition().y)) {
            confirm();
        }
    }

    private void confirm() {
        String name = nameBuffer.toString().trim();
        if (name.isEmpty()) return;
        leaderboard.setPlayerName(name);
        context.getSceneManager().pop();
    }

    private Rectangle confirmBtnRect(float cx, float cy) {
        return new Rectangle(cx - BTN_W / 2f, cy - CARD_H / 2f + 30f, BTN_W, BTN_H);
    }

    void renderScene() {
        Renderer r  = context.getOutputManager().getRenderer();
        float ww = r.getWorldWidth(), wh = r.getWorldHeight();
        float cx = ww / 2f, cy = wh / 2f;

        r.clear();
        r.begin();

        r.drawBackground("menu-scene.png");
        r.drawRect(new Rectangle(0, 0, ww, wh), new Color(0f, 0f, 0f, 0.70f), true);

        float cardX = cx - CARD_W / 2f, cardY = cy - CARD_H / 2f;
        r.drawRect(new Rectangle(cardX, cardY, CARD_W, CARD_H),
            new Color(0.06f, 0.10f, 0.06f, 0.95f), true);
        r.drawRect(new Rectangle(cardX, cardY, CARD_W, CARD_H),
            new Color(0.2f, 0.8f, 0.3f, 1f), false);

        r.drawText("ENTER YOUR NAME",
            new Vector2(cx - 130f, cardY + CARD_H - 36f), "default",
            new Color(0.2f, 0.9f, 0.4f, 1f));

        // Input field
        float fieldW = 360f, fieldH = 44f;
        float fieldX = cx - fieldW / 2f, fieldY = cy + 10f;
        r.drawRect(new Rectangle(fieldX, fieldY, fieldW, fieldH),
            new Color(0.02f, 0.02f, 0.02f, 0.9f), true);
        r.drawRect(new Rectangle(fieldX, fieldY, fieldW, fieldH),
            new Color(0.4f, 0.9f, 0.5f, 1f), false);

        String display = nameBuffer.toString() + (cursorVisible ? "_" : " ");
        r.drawText(display,
            new Vector2(fieldX + 12f, fieldY + fieldH / 2f + 10f), "default", Color.WHITE);

        r.drawText(nameBuffer.length() + " / " + MAX_LEN,
            new Vector2(fieldX + fieldW + 12f, fieldY + fieldH / 2f + 8f), "default",
            new Color(0.6f, 0.6f, 0.6f, 1f));

        // Confirm button
        Rectangle btn = confirmBtnRect(cx, cy);
        boolean hasName = nameBuffer.toString().trim().length() > 0;
        Color btnBg = hasName ? new Color(0.15f, 0.55f, 0.25f, 0.9f)
                              : new Color(0.10f, 0.10f, 0.10f, 0.40f);
        r.drawRect(btn, btnBg, true);
        r.drawRect(btn, hasName ? new Color(0.2f, 0.8f, 0.3f, 1f)
                                : new Color(0.3f, 0.3f, 0.3f, 1f), false);
        r.drawText("Confirm",
            new Vector2(btn.x + BTN_W / 2f - 42f, btn.y + BTN_H / 2f + 10f),
            "default", hasName ? Color.WHITE : new Color(0.4f, 0.4f, 0.4f, 1f));

        // Hints
        r.drawText("Type A-Z, 0-9   |   Backspace to delete   |   Enter to confirm",
            new Vector2(cx - 280f, cardY + 14f), "default",
            new Color(0.5f, 0.5f, 0.5f, 1f));

        r.end();
    }

    // ── Inner classes ────────────────────────────────────────────────────────

    private static final class NameEntryInputHandler extends InputHandler {
        private final NameEntryScene scene;
        NameEntryInputHandler(NameEntryScene s) { super(s.context); this.scene = s; }
        @Override public void handleInput() { scene.processInput(); }
    }

    private static final class NameEntryRenderer extends SceneRenderer {
        private final NameEntryScene scene;
        NameEntryRenderer(NameEntryScene s) { super(s.context); this.scene = s; }
        @Override public void render() { scene.renderScene(); }
    }
}
