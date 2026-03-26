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
import sg.edu.sit.inf1009.p2team2.game.audio.GameAudio;
import sg.edu.sit.inf1009.p2team2.game.entities.CharacterType;
import sg.edu.sit.inf1009.p2team2.game.leaderboard.LeaderboardManager;
import sg.edu.sit.inf1009.p2team2.game.save.RunSaveManager;
import sg.edu.sit.inf1009.p2team2.game.ui.GameUiTheme;

/**
 * Shown when "Start Game" is pressed and a saved run exists.
 * Lets the player continue the saved run or start a fresh game.
 */
public class StartGamePromptScene extends Scene {

    private static final float BTN_W = 420f, BTN_H = 58f, BTN_GAP = 20f;
    private static final int   COOLDOWN_FRAMES = 10;

    private final LeaderboardManager leaderboard;
    private final RunSaveManager.RunSnapshot savedRun;

    private int selectedIndex    = 0; // 0 = continue, 1 = new game
    private int keyboardCooldown = 0;

    public StartGamePromptScene(EngineContext context, LeaderboardManager leaderboard,
                                RunSaveManager.RunSnapshot savedRun) {
        super(context);
        this.leaderboard   = leaderboard;
        this.savedRun = savedRun;

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
            GameAudio.playUiClick(getContext());
            getContext().getSceneManager().pop();
            return;
        }

        if (keyboardCooldown == 0) {
            if (kb.isKeyPressed(Input.Keys.UP) || kb.isKeyPressed(Input.Keys.W)) {
                selectedIndex    = (selectedIndex - 1 + 2) % 2;
                keyboardCooldown = COOLDOWN_FRAMES;
                GameAudio.playUiClick(getContext());
            } else if (kb.isKeyPressed(Input.Keys.DOWN) || kb.isKeyPressed(Input.Keys.S)) {
                selectedIndex    = (selectedIndex + 1) % 2;
                keyboardCooldown = COOLDOWN_FRAMES;
                GameAudio.playUiClick(getContext());
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
        GameAudio.playUiClick(getContext());
        getContext().getSceneManager().pop(); // remove this prompt
        if (index == 0 && savedRun != null) {
            getContext().getSceneManager().push(
                new GamePlayScene(getContext(), leaderboard, savedRun));
        } else {
            RunSaveManager.clear();
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
            CharacterType savedCharacter = scene.savedRun != null
                ? scene.savedRun.characterType
                : CharacterType.SPECTER;

            r.clear();
            r.begin();

            r.drawBackground("menu-scene.png");
            r.drawRect(new com.badlogic.gdx.math.Rectangle(0, 0, ww, wh),
                new Color(0f, 0f, 0f, 0.70f), true);

            // Card
            float cw = 560f, ch = 320f;
            float cardX = cx - cw / 2f, cardY = cy - ch / 2f;
            r.drawRect(new com.badlogic.gdx.math.Rectangle(cardX, cardY, cw, ch),
                new Color(0.06f, 0.10f, 0.06f, 0.95f), true);
            r.drawRect(new com.badlogic.gdx.math.Rectangle(cardX, cardY, cw, ch),
                new Color(0.2f, 0.8f, 0.3f, 1f), false);

            // Title
            r.drawTextCentered("CONTINUE RUN?",
                new Vector2(cx, cardY + ch - 28f), GameUiTheme.FONT_TITLE_SMALL,
                GameUiTheme.TITLE_PRIMARY);
            r.drawTextCentered("Saved progress found for " + savedCharacter.getName(),
                new Vector2(cx, cardY + ch - 66f), GameUiTheme.FONT_BODY,
                GameUiTheme.TEXT_MUTED);

            // Buttons
            drawBtn(r, scene.continueRect(cx, cy), 0,
                "Continue Run", scene.selectedIndex);
            drawBtn(r, scene.newGameRect(cx, cy),  1,
                "New Game",                    scene.selectedIndex);

            // Footer
            r.drawTextCentered("ESC - Back",
                new Vector2(cx, cardY + 18f), GameUiTheme.FONT_BODY_SMALL,
                GameUiTheme.TEXT_SUBTLE);

            r.end();
        }

        private void drawBtn(Renderer r, com.badlogic.gdx.math.Rectangle box,
                             int idx, String label, int sel) {
            boolean active = (idx == sel);
            Color bg     = active ? new Color(0.15f, 0.55f, 0.25f, 0.9f)
                                  : new Color(0.10f, 0.10f, 0.10f, 0.70f);
            Color border = active ? Color.YELLOW : new Color(0.5f, 0.5f, 0.5f, 1f);
            String font = r.measureTextWidth(label, GameUiTheme.FONT_BODY_LARGE) > box.width - 48f
                ? GameUiTheme.FONT_BODY
                : GameUiTheme.FONT_BODY_LARGE;
            r.drawRect(box, bg, true);
            r.drawRect(box, border, false);
            r.drawTextCentered(label, box, font,
                active ? GameUiTheme.TEXT_HIGHLIGHT : GameUiTheme.TEXT_PRIMARY);
        }
    }
}
