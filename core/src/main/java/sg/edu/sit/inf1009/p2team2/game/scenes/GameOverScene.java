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
 * Displayed when the player loses all lives.
 * Options: Retry (restart game) | Menu | Leaderboard
 */
public class GameOverScene extends Scene {

    private final int               finalScore;
    private final LeaderboardManager leaderboard;
    private int  selectedIndex;
    private int  keyboardCooldown;

    private static final String[] OPTIONS       = {"Back to Menu", "Leaderboard"};
    private static final int      COOLDOWN_FRAMES = 10;
    private static final float    BTN_W = 220f, BTN_H = 48f;

    public GameOverScene(EngineContext context, int finalScore, LeaderboardManager leaderboard) {
        super(context);
        this.finalScore   = finalScore;
        this.leaderboard  = leaderboard;
        this.selectedIndex = 0;

        leaderboard.addEntry("PLAYER", finalScore);

        setInputHandler(new GameOverInputHandler(this));
        setSceneRenderer(new GameOverRenderer(this));
        setResourceLoader(new ResourceLoader() {
            @Override public void load()   { setLoaded(true); }
            @Override public void unload() { setLoaded(false); }
        });
    }

    @Override
    public void onEnter() {
        keyboardCooldown = COOLDOWN_FRAMES;
    }

    @Override
    public void update(float dt) {
        if (keyboardCooldown > 0) keyboardCooldown--;
    }

    private Rectangle buttonRect(float ww, float wh, int index) {
        float startY = wh / 2f - 20f;
        float by     = startY - index * 65f;
        return new Rectangle(ww / 2f - BTN_W / 2f, by - 8f, BTN_W, BTN_H);
    }

    void processInput() {
        Keyboard kb    = context.getInputManager().getKeyboard();
        Mouse    mouse = context.getInputManager().getMouse();
        Renderer r     = context.getOutputManager().getRenderer();
        float ww = r.getWorldWidth(), wh = r.getWorldHeight();

        // Keyboard navigation
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

        // Mouse hover + click
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
                context.getSceneManager().pop();
                break;
            case "Leaderboard":
                context.getSceneManager().push(new LeaderboardScene(context, leaderboard));
                break;
        }
    }

    void renderScene() {
        Renderer r  = context.getOutputManager().getRenderer();
        float ww = r.getWorldWidth();
        float wh = r.getWorldHeight();

        r.clear();
        r.begin();

        r.drawBackground("win-scene.png");
        r.drawRect(new Rectangle(0, 0, ww, wh), new Color(0f, 0f, 0f, 0.65f), true);

        // Title
        r.drawText("SYSTEM CRASH",
            new Vector2(ww / 2f - 105f, wh / 2f + 150f), "default",
            new Color(0.95f, 0.25f, 0.25f, 1f));
        r.drawText("The network has been compromised.",
            new Vector2(ww / 2f - 195f, wh / 2f + 100f), "default", Color.WHITE);
        r.drawText("Final Score: " + finalScore,
            new Vector2(ww / 2f - 90f, wh / 2f + 55f), "default",
            new Color(1f, 0.85f, 0.2f, 1f));

        // Buttons
        for (int i = 0; i < OPTIONS.length; i++) {
            boolean   sel  = (i == selectedIndex);
            Rectangle box  = buttonRect(ww, wh, i);
            Color bg  = sel ? new Color(0.7f, 0.1f, 0.1f, 0.85f)
                            : new Color(0.15f, 0.15f, 0.15f, 0.65f);
            r.drawRect(box, bg, true);
            r.drawRect(box, sel ? Color.YELLOW : Color.WHITE, false);
            r.drawText(OPTIONS[i],
                new Vector2(box.x + BTN_W / 2f - 55f, box.y + BTN_H / 2f + 10f),
                "default", sel ? Color.YELLOW : Color.WHITE);
        }

        r.drawText("W / S or Mouse   Enter / Click to Select   Play Again via Main Menu",
            new Vector2(ww / 2f - 295f, 30f), "default",
            new Color(0.55f, 0.55f, 0.55f, 1f));

        r.end();
    }

    // ── Inner classes ────────────────────────────────────────────────────────

    private static final class GameOverInputHandler extends InputHandler {
        private final GameOverScene scene;
        GameOverInputHandler(GameOverScene s) { super(s.context); this.scene = s; }
        @Override public void handleInput() { scene.processInput(); }
    }

    private static final class GameOverRenderer extends SceneRenderer {
        private final GameOverScene scene;
        GameOverRenderer(GameOverScene s) { super(s.context); this.scene = s; }
        @Override public void render() { scene.renderScene(); }
    }
}
