package sg.edu.sit.inf1009.p2team2.game.scenes;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.io.input.Keyboard;
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

    private static final String[] OPTIONS = {"Retry", "Main Menu", "Leaderboard"};
    private static final int COOLDOWN_FRAMES = 10;

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
    public void update(float dt) {
        if (keyboardCooldown > 0) keyboardCooldown--;
    }

    void processInput() {
        Keyboard kb = context.getInputManager().getKeyboard();

        if (kb.isKeyPressed(Input.Keys.UP) || kb.isKeyPressed(Input.Keys.W)) {
            selectedIndex    = (selectedIndex - 1 + OPTIONS.length) % OPTIONS.length;
            keyboardCooldown = COOLDOWN_FRAMES;
        } else if (kb.isKeyPressed(Input.Keys.DOWN) || kb.isKeyPressed(Input.Keys.S)) {
            selectedIndex    = (selectedIndex + 1) % OPTIONS.length;
            keyboardCooldown = COOLDOWN_FRAMES;
        }

        if (kb.isKeyPressed(Input.Keys.ENTER) || kb.isKeyPressed(Input.Keys.SPACE)) {
            activate(selectedIndex);
        }
    }

    private void activate(int index) {
        switch (OPTIONS[index]) {
            case "Retry":
                context.getSceneManager().pop();
                context.getSceneManager().push(new GamePlayScene(context, leaderboard));
                break;
            case "Main Menu":
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

        r.drawBackground("background_menu.png");

        // Dim overlay
        r.drawRect(new Rectangle(0, 0, ww, wh),
            new Color(0f, 0f, 0f, 0.65f), true);

        // Title
        r.drawText("SYSTEM CRASH",
            new Vector2(ww / 2f - 105f, wh / 2f + 150f), "default",
            new Color(0.95f, 0.25f, 0.25f, 1f));

        r.drawText("The network has been compromised.",
            new Vector2(ww / 2f - 195f, wh / 2f + 100f), "default", Color.WHITE);

        r.drawText("Final Score: " + finalScore,
            new Vector2(ww / 2f - 90f, wh / 2f + 55f), "default",
            new Color(1f, 0.85f, 0.2f, 1f));

        // Options
        float spacing = 65f;
        float startY  = wh / 2f - 20f;
        for (int i = 0; i < OPTIONS.length; i++) {
            boolean sel = (i == selectedIndex);
            float bx = ww / 2f - 110f, by = startY - i * spacing;
            Color bg  = sel ? new Color(0.7f, 0.1f, 0.1f, 0.85f)
                            : new Color(0.15f, 0.15f, 0.15f, 0.65f);
            r.drawRect(new Rectangle(bx, by - 8f, 220f, 48f), bg, true);
            r.drawRect(new Rectangle(bx, by - 8f, 220f, 48f), Color.WHITE, false);
            r.drawText(OPTIONS[i],
                new Vector2(bx + 70f, by + 28f), "default",
                sel ? Color.YELLOW : Color.WHITE);
        }

        r.drawText("↑↓ Navigate   Enter Select",
            new Vector2(ww / 2f - 155f, 30f), "default",
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
