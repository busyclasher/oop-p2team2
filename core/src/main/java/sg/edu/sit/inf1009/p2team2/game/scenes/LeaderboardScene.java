package sg.edu.sit.inf1009.p2team2.game.scenes;

import sg.edu.sit.inf1009.p2team2.engine.io.input.Keys;
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
import sg.edu.sit.inf1009.p2team2.game.audio.GameAudio;
import sg.edu.sit.inf1009.p2team2.game.leaderboard.LeaderboardEntry;
import sg.edu.sit.inf1009.p2team2.game.leaderboard.LeaderboardManager;
import sg.edu.sit.inf1009.p2team2.game.ui.GameUiTheme;

/**
 * Displays the top scores and lets the player return to the main menu or retry.
 */
public class LeaderboardScene extends Scene {
    private static final String LEADERBOARD_TROPHY_SPRITE = "leaderboard-trophy.png";

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
        if (kb.isKeyPressed(Keys.ESCAPE)
                || kb.isKeyPressed(Keys.ENTER)
                || kb.isKeyPressed(Keys.SPACE)) {
            GameAudio.playUiClick(getContext());
            getContext().getSceneManager().pop();
            return;
        }
        if (kb.isKeyPressed(Keys.R)) {
            GameAudio.playUiClick(getContext());
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
                GameAudio.playUiClick(getContext());
                getContext().getSceneManager().pop();
            } else if (hoveredBtn == 1) {
                GameAudio.playUiClick(getContext());
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
        r.drawTextCentered("LEADERBOARD",
            new Vector2(ww / 2f, wh - 78f), GameUiTheme.FONT_TITLE_SMALL,
            GameUiTheme.TITLE_PRIMARY);
        r.drawTextCentered("Top Scores - CyberScouts",
            new Vector2(ww / 2f, wh - 114f), GameUiTheme.FONT_BODY,
            GameUiTheme.TEXT_MUTED);

        // Column headers
        float tableWidth = 540f;
        float tableX = ww / 2f - tableWidth / 2f;
        float rankX = tableX + 20f;
        float nameX = tableX + 120f;
        float scoreX = tableX + 420f;
        float headerY = wh - 170f;
        float separatorY = headerY - 22f;
        float firstRowY = separatorY - 28f;

        r.drawSprite(LEADERBOARD_TROPHY_SPRITE,
            new Vector2(rankX + 14f, headerY - 10f), 28f, 28f);
        r.drawText("Name",  new Vector2(nameX,  headerY), GameUiTheme.FONT_BODY_LARGE, GameUiTheme.TEXT_HIGHLIGHT);
        r.drawText("Score", new Vector2(scoreX, headerY), GameUiTheme.FONT_BODY_LARGE, GameUiTheme.TEXT_HIGHLIGHT);

        r.drawLine(new Vector2(tableX, separatorY),
            new Vector2(tableX + tableWidth, separatorY),
            new Color(0.5f, 0.5f, 0.5f, 1f), 1.5f);

        // Rows
        List<LeaderboardEntry> entries = leaderboard.getEntries();
        for (int i = 0; i < entries.size(); i++) {
            LeaderboardEntry e  = entries.get(i);
            float ry = firstRowY - i * 38f;

            Color rowColor;
            if (i == 0)      rowColor = new Color(1f, 0.84f, 0f, 1f);
            else if (i == 1) rowColor = new Color(0.75f, 0.75f, 0.75f, 1f);
            else if (i == 2) rowColor = new Color(0.8f, 0.5f, 0.2f, 1f);
            else             rowColor = Color.WHITE;

            r.drawText(String.valueOf(i + 1), new Vector2(rankX, ry), GameUiTheme.FONT_BODY, rowColor);
            r.drawText(e.getPlayerName(), new Vector2(nameX, ry), GameUiTheme.FONT_BODY, rowColor);
            r.drawText(String.valueOf(e.getScore()), new Vector2(scoreX, ry), GameUiTheme.FONT_BODY, rowColor);
        }

        if (entries.isEmpty()) {
            r.drawTextCentered("No scores yet - play a game first!",
                new Vector2(ww / 2f, wh / 2f), GameUiTheme.FONT_BODY,
                GameUiTheme.TEXT_MUTED);
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
        r.drawTextCentered(label, box, GameUiTheme.FONT_BODY_LARGE,
            hovered ? GameUiTheme.TEXT_HIGHLIGHT : GameUiTheme.TEXT_PRIMARY);
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
