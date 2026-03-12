package sg.edu.sit.inf1009.p2team2.game.scenes;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.List;
import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.io.input.Keyboard;
import sg.edu.sit.inf1009.p2team2.engine.io.output.Renderer;
import sg.edu.sit.inf1009.p2team2.engine.scene.InputHandler;
import sg.edu.sit.inf1009.p2team2.engine.scene.ResourceLoader;
import sg.edu.sit.inf1009.p2team2.engine.scene.Scene;
import sg.edu.sit.inf1009.p2team2.engine.scene.SceneRenderer;
import sg.edu.sit.inf1009.p2team2.game.leaderboard.LeaderboardEntry;
import sg.edu.sit.inf1009.p2team2.game.leaderboard.LeaderboardManager;

/**
 * Displays the top scores and lets the player return to the main menu or retry.
 */
public class LeaderboardScene extends Scene {

    private final LeaderboardManager leaderboard;

    public LeaderboardScene(EngineContext context, LeaderboardManager leaderboard) {
        super(context);
        this.leaderboard = leaderboard;

        setInputHandler(new LeaderboardInputHandler(this));
        setSceneRenderer(new LeaderboardRenderer(this));
        setResourceLoader(new ResourceLoader() {
            @Override public void load()   { setLoaded(true); }
            @Override public void unload() { setLoaded(false); }
        });
    }

    @Override
    public void update(float dt) { /* static screen */ }

    void processInput() {
        Keyboard kb = context.getInputManager().getKeyboard();
        if (kb.isKeyPressed(Input.Keys.ESCAPE)
                || kb.isKeyPressed(Input.Keys.ENTER)
                || kb.isKeyPressed(Input.Keys.SPACE)) {
            context.getSceneManager().pop(); // back to whoever pushed us
        }
        if (kb.isKeyPressed(Input.Keys.R)) {
            context.getSceneManager().pop();
            context.getSceneManager().push(new GamePlayScene(context, leaderboard));
        }
    }

    void renderScene() {
        Renderer r  = context.getOutputManager().getRenderer();
        float ww = r.getWorldWidth();
        float wh = r.getWorldHeight();

        r.clear();
        r.begin();

        r.drawBackground("background_menu.png");
        r.drawRect(new Rectangle(0, 0, ww, wh),
            new Color(0f, 0f, 0f, 0.60f), true);

        // Title
        r.drawText("LEADERBOARD",
            new Vector2(ww / 2f - 100f, wh - 80f), "default",
            new Color(0.15f, 0.95f, 0.40f, 1f));

        r.drawText("Top Scores — Silicon Sentinel",
            new Vector2(ww / 2f - 165f, wh - 116f), "default",
            new Color(0.75f, 0.75f, 0.75f, 1f));

        // Column headers
        float tableX = ww / 2f - 220f;
        float headerY = wh - 160f;
        r.drawText("#", new Vector2(tableX,        headerY), "default", Color.YELLOW);
        r.drawText("Name",  new Vector2(tableX + 50f,  headerY), "default", Color.YELLOW);
        r.drawText("Score", new Vector2(tableX + 300f, headerY), "default", Color.YELLOW);

        // Divider
        r.drawLine(new Vector2(tableX - 10f, headerY - 10f),
            new Vector2(tableX + 440f, headerY - 10f),
            new Color(0.5f, 0.5f, 0.5f, 1f), 1f);

        // Rows
        List<LeaderboardEntry> entries = leaderboard.getEntries();
        float rowHeight = 42f;
        for (int i = 0; i < entries.size(); i++) {
            LeaderboardEntry e  = entries.get(i);
            float ry = headerY - 30f - i * rowHeight;

            Color rowColor;
            if (i == 0)      rowColor = new Color(1f, 0.84f, 0f, 1f);   // gold
            else if (i == 1) rowColor = new Color(0.75f, 0.75f, 0.75f, 1f); // silver
            else if (i == 2) rowColor = new Color(0.8f, 0.5f, 0.2f, 1f);    // bronze
            else             rowColor = Color.WHITE;

            r.drawText(String.valueOf(i + 1),
                new Vector2(tableX, ry), "default", rowColor);
            r.drawText(e.getPlayerName(),
                new Vector2(tableX + 50f, ry), "default", rowColor);
            r.drawText(String.valueOf(e.getScore()),
                new Vector2(tableX + 300f, ry), "default", rowColor);
        }

        if (entries.isEmpty()) {
            r.drawText("No scores yet — play a game first!",
                new Vector2(ww / 2f - 195f, wh / 2f), "default",
                new Color(0.6f, 0.6f, 0.6f, 1f));
        }

        // Footer
        r.drawText("ESC / Enter — Back     R — Play Again",
            new Vector2(ww / 2f - 195f, 30f), "default",
            new Color(0.55f, 0.55f, 0.55f, 1f));

        r.end();
    }

    // ── Inner classes ────────────────────────────────────────────────────────

    private static final class LeaderboardInputHandler extends InputHandler {
        private final LeaderboardScene scene;
        LeaderboardInputHandler(LeaderboardScene s) { super(s.context); this.scene = s; }
        @Override public void handleInput() { scene.processInput(); }
    }

    private static final class LeaderboardRenderer extends SceneRenderer {
        private final LeaderboardScene scene;
        LeaderboardRenderer(LeaderboardScene s) { super(s.context); this.scene = s; }
        @Override public void render() { scene.renderScene(); }
    }
}
