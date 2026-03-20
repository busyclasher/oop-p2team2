package sg.edu.sit.inf1009.p2team2.game.scenes;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.List;
import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.io.input.Keyboard;
import sg.edu.sit.inf1009.p2team2.engine.io.input.Mouse;
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

    // Two footer buttons: 0 = Back, 1 = Play Again
    private static final float BTN_W = 200f, BTN_H = 46f, BTN_GAP = 30f;
    private int hoveredBtn = -1;

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

    private Rectangle backRect(float ww) {
        float totalW = BTN_W * 2 + BTN_GAP;
        float startX = ww / 2f - totalW / 2f;
        return new Rectangle(startX, 20f, BTN_W, BTN_H);
    }

    private Rectangle playAgainRect(float ww) {
        float totalW = BTN_W * 2 + BTN_GAP;
        float startX = ww / 2f - totalW / 2f;
        return new Rectangle(startX + BTN_W + BTN_GAP, 20f, BTN_W, BTN_H);
    }

    void processInput() {
        Keyboard kb    = getContext().getInputManager().getKeyboard();
        Mouse    mouse = getContext().getInputManager().getMouse();
        Renderer r     = getContext().getOutputManager().getRenderer();
        float ww = r.getWorldWidth();

        // Keyboard
        if (kb.isKeyPressed(Input.Keys.ESCAPE)
                || kb.isKeyPressed(Input.Keys.ENTER)
                || kb.isKeyPressed(Input.Keys.SPACE)) {
            getContext().getSceneManager().pop();
            return;
        }
        if (kb.isKeyPressed(Input.Keys.R)) {
            getContext().getSceneManager().pop();
            getContext().getSceneManager().push(new CharacterSelectScene(getContext(), leaderboard));
            return;
        }

        // Mouse hover
        Vector2 mp = mouse.getPosition();
        hoveredBtn = -1;
        if (backRect(ww).contains(mp.x, mp.y))       hoveredBtn = 0;
        if (playAgainRect(ww).contains(mp.x, mp.y))  hoveredBtn = 1;

        // Mouse click
        if (mouse.isButtonPressed(0)) {
            if (hoveredBtn == 0) {
                getContext().getSceneManager().pop();
            } else if (hoveredBtn == 1) {
                getContext().getSceneManager().pop();
                getContext().getSceneManager().push(new CharacterSelectScene(getContext(), leaderboard));
            }
        }
    }

    void renderScene() {
        Renderer r  = getContext().getOutputManager().getRenderer();
        float ww = r.getWorldWidth();
        float wh = r.getWorldHeight();

        r.clear();
        r.begin();

        r.drawBackground("menu-scene.png");
        r.drawRect(new Rectangle(0, 0, ww, wh), new Color(0f, 0f, 0f, 0.60f), true);

        // Title
        r.drawText("LEADERBOARD",
            new Vector2(ww / 2f - 100f, wh - 80f), "default",
            new Color(0.15f, 0.95f, 0.40f, 1f));
        r.drawText("Top Scores - Silicon Sentinel",
            new Vector2(ww / 2f - 165f, wh - 116f), "default",
            new Color(0.75f, 0.75f, 0.75f, 1f));

        // Column headers
        float tableX = ww / 2f - 220f;
        float headerY = wh - 160f;
        r.drawText("#",     new Vector2(tableX,        headerY), "default", Color.YELLOW);
        r.drawText("Name",  new Vector2(tableX + 50f,  headerY), "default", Color.YELLOW);
        r.drawText("Score", new Vector2(tableX + 300f, headerY), "default", Color.YELLOW);

        r.drawLine(new Vector2(tableX - 10f, headerY - 10f),
            new Vector2(tableX + 440f, headerY - 10f),
            new Color(0.5f, 0.5f, 0.5f, 1f), 1f);

        // Rows
        List<LeaderboardEntry> entries = leaderboard.getEntries();
        for (int i = 0; i < entries.size(); i++) {
            LeaderboardEntry e  = entries.get(i);
            float ry = headerY - 30f - i * 42f;

            Color rowColor;
            if (i == 0)      rowColor = new Color(1f, 0.84f, 0f, 1f);
            else if (i == 1) rowColor = new Color(0.75f, 0.75f, 0.75f, 1f);
            else if (i == 2) rowColor = new Color(0.8f, 0.5f, 0.2f, 1f);
            else             rowColor = Color.WHITE;

            r.drawText(String.valueOf(i + 1),   new Vector2(tableX,        ry), "default", rowColor);
            r.drawText(e.getPlayerName(),        new Vector2(tableX + 50f,  ry), "default", rowColor);
            r.drawText(String.valueOf(e.getScore()), new Vector2(tableX + 300f, ry), "default", rowColor);
        }

        if (entries.isEmpty()) {
            r.drawText("No scores yet - play a game first!",
                new Vector2(ww / 2f - 195f, wh / 2f), "default",
                new Color(0.6f, 0.6f, 0.6f, 1f));
        }

        // Footer buttons
        drawButton(r, backRect(ww),      "Back",       hoveredBtn == 0);
        drawButton(r, playAgainRect(ww), "Play Again", hoveredBtn == 1);

        r.end();
    }

    private void drawButton(Renderer r, Rectangle box, String label, boolean hovered) {
        Color bg  = hovered ? new Color(0.15f, 0.55f, 0.25f, 0.9f)
                            : new Color(0.08f, 0.08f, 0.08f, 0.75f);
        Color border = hovered ? Color.YELLOW : new Color(0.5f, 0.5f, 0.5f, 1f);
        r.drawRect(box, bg, true);
        r.drawRect(box, border, false);
        r.drawText(label, new Vector2(box.x + box.width / 2f - 45f, box.y + box.height / 2f + 8f),
            "default", hovered ? Color.YELLOW : Color.WHITE);
    }

    // ── Inner classes ────────────────────────────────────────────────────────

    private static final class LeaderboardInputHandler extends InputHandler {
        private final LeaderboardScene scene;
        LeaderboardInputHandler(LeaderboardScene s) { super(s.getContext()); this.scene = s; }
        @Override public void handleInput() { scene.processInput(); }
    }

    private static final class LeaderboardRenderer extends SceneRenderer {
        private final LeaderboardScene scene;
        LeaderboardRenderer(LeaderboardScene s) { super(s.getContext()); this.scene = s; }
        @Override public void render() { scene.renderScene(); }
    }
}
