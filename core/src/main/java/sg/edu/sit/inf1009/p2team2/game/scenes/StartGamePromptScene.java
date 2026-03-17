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
import sg.edu.sit.inf1009.p2team2.game.entities.CharacterType;
import sg.edu.sit.inf1009.p2team2.game.leaderboard.LeaderboardManager;

/**
 * Shown when "Start Game" is pressed and a previous character exists.
 * Lets the player continue with the same character or pick a new one.
 */
public class StartGamePromptScene extends Scene {

    private static final float BTN_W = 300f, BTN_H = 58f, BTN_GAP = 24f;
    private static final int   COOLDOWN_FRAMES = 10;

    private final LeaderboardManager leaderboard;
    private final CharacterType      lastCharacter;

    private int selectedIndex    = 0; // 0 = continue, 1 = new game
    private int keyboardCooldown = 0;

    public StartGamePromptScene(EngineContext context, LeaderboardManager leaderboard,
                                CharacterType lastCharacter) {
        super(context);
        this.leaderboard   = leaderboard;
        this.lastCharacter = lastCharacter;

        setInputHandler(new PromptInputHandler(this));
        setSceneRenderer(new PromptRenderer(this));
        setResourceLoader(new ResourceLoader() {
            @Override public void load()   { setLoaded(true); }
            @Override public void unload() { setLoaded(false); }
        });
    }

    @Override
    public void onEnter() {
        selectedIndex    = 0;
        keyboardCooldown = COOLDOWN_FRAMES;
    }

    @Override
    public void update(float dt) {
        if (keyboardCooldown > 0) keyboardCooldown--;
    }

    private Rectangle continueRect(float cx, float cy) {
        return new Rectangle(cx - BTN_W / 2f, cy + BTN_GAP / 2f, BTN_W, BTN_H);
    }

    private Rectangle newGameRect(float cx, float cy) {
        return new Rectangle(cx - BTN_W / 2f, cy - BTN_H - BTN_GAP / 2f, BTN_W, BTN_H);
    }

    void processInput() {
        Keyboard kb    = getContext().getInputManager().getKeyboard();
        Mouse    mouse = getContext().getInputManager().getMouse();
        Renderer r     = getContext().getOutputManager().getRenderer();
        float cx = r.getWorldWidth() / 2f, cy = r.getWorldHeight() / 2f;

        if (kb.isKeyPressed(Input.Keys.ESCAPE)) {
            getContext().getSceneManager().pop();
            return;
        }

        if (keyboardCooldown == 0) {
            if (kb.isKeyPressed(Input.Keys.UP) || kb.isKeyPressed(Input.Keys.W)) {
                selectedIndex    = (selectedIndex + 1) % 2;
                keyboardCooldown = COOLDOWN_FRAMES;
            } else if (kb.isKeyPressed(Input.Keys.DOWN) || kb.isKeyPressed(Input.Keys.S)) {
                selectedIndex    = (selectedIndex + 1) % 2;
                keyboardCooldown = COOLDOWN_FRAMES;
            }
        }

        if (kb.isKeyPressed(Input.Keys.ENTER) || kb.isKeyPressed(Input.Keys.SPACE)) {
            activate(selectedIndex);
            return;
        }

        // Mouse hover
        Vector2 mp = mouse.getPosition();
        if (continueRect(cx, cy).contains(mp.x, mp.y)) selectedIndex = 0;
        if (newGameRect(cx, cy).contains(mp.x, mp.y))  selectedIndex = 1;

        if (mouse.isButtonPressed(0)) {
            if (continueRect(cx, cy).contains(mp.x, mp.y)) { activate(0); return; }
            if (newGameRect(cx, cy).contains(mp.x, mp.y))  { activate(1); return; }
        }
    }

    private void activate(int index) {
        getContext().getSceneManager().pop(); // remove this prompt
        if (index == 0) {
            // Continue with same character — straight into game
            getContext().getSceneManager().push(
                new GamePlayScene(getContext(), leaderboard, lastCharacter));
        } else {
            // New Game — pick a different character
            getContext().getSceneManager().push(
                new CharacterSelectScene(getContext(), leaderboard));
        }
    }

    // ── Inner classes ─────────────────────────────────────────────────────────

    private static final class PromptInputHandler extends InputHandler {
        private final StartGamePromptScene scene;
        PromptInputHandler(StartGamePromptScene s) { super(s.getContext()); this.scene = s; }
        @Override public void handleInput() { scene.processInput(); }
    }

    private static final class PromptRenderer extends SceneRenderer {
        private final StartGamePromptScene scene;
        PromptRenderer(StartGamePromptScene s) { super(s.getContext()); this.scene = s; }

        @Override
        public void render() {
            Renderer r  = getContext().getOutputManager().getRenderer();
            float ww = r.getWorldWidth(), wh = r.getWorldHeight();
            float cx = ww / 2f, cy = wh / 2f;

            r.clear();
            r.begin();

            r.drawBackground("menu-scene.png");
            r.drawRect(new com.badlogic.gdx.math.Rectangle(0, 0, ww, wh),
                new Color(0f, 0f, 0f, 0.70f), true);

            // Card
            float cw = 440f, ch = 280f;
            float cardX = cx - cw / 2f, cardY = cy - ch / 2f;
            r.drawRect(new com.badlogic.gdx.math.Rectangle(cardX, cardY, cw, ch),
                new Color(0.06f, 0.10f, 0.06f, 0.95f), true);
            r.drawRect(new com.badlogic.gdx.math.Rectangle(cardX, cardY, cw, ch),
                new Color(0.2f, 0.8f, 0.3f, 1f), false);

            // Title
            r.drawText("WELCOME BACK!",
                new Vector2(cx - 115f, cardY + ch - 36f), "default",
                new Color(0.2f, 0.9f, 0.4f, 1f));
            r.drawText("Last played: " + scene.lastCharacter.getName(),
                new Vector2(cx - 110f, cardY + ch - 72f), "default",
                new Color(0.75f, 0.75f, 0.75f, 1f));

            // Buttons
            drawBtn(r, scene.continueRect(cx, cy), 0,
                "Continue as " + scene.lastCharacter.getName(), scene.selectedIndex);
            drawBtn(r, scene.newGameRect(cx, cy),  1,
                "New Game (Change Character)",                    scene.selectedIndex);

            // Footer
            r.drawText("ESC - Back",
                new Vector2(cx - 45f, cardY + 18f), "default",
                new Color(0.5f, 0.5f, 0.5f, 1f));

            r.end();
        }

        private void drawBtn(Renderer r, com.badlogic.gdx.math.Rectangle box,
                             int idx, String label, int sel) {
            boolean active = (idx == sel);
            Color bg     = active ? new Color(0.15f, 0.55f, 0.25f, 0.9f)
                                  : new Color(0.10f, 0.10f, 0.10f, 0.70f);
            Color border = active ? Color.YELLOW : new Color(0.5f, 0.5f, 0.5f, 1f);
            r.drawRect(box, bg, true);
            r.drawRect(box, border, false);
            r.drawText(label,
                new Vector2(box.x + 16f, box.y + box.height / 2f + 10f),
                "default", active ? Color.YELLOW : Color.WHITE);
        }
    }
}
