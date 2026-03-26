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
 * Two-page How To Play screen used both from the menu and as first-time onboarding.
 */
public class HowToPlayScene extends Scene {
    private static final String GOOD_BYTE_SPRITE       = "good_byte.png";
    private static final String SAFE_EMAIL_SPRITE      = "safe_email.png";
    private static final String GOLD_ENVELOPE_SPRITE   = "gold_envelope.png";
    private static final String PHISHING_HOOK_SPRITE   = "phishing_hook.png";
    private static final String RANSOMWARE_LOCK_SPRITE = "ransomware_lock.png";
    private static final String MALWARE_SWARM_SPRITE   = "malware_swarm.png";
    private static final String ROOTKIT_SPRITE         = "rootkit.png";
    private static final String SPYWARE_SPRITE         = "spyware.png";
    private static final String FRENZY_ORB_SPRITE      = "frenzy_orb.png";

    private static final int PAGE_COUNT = 2;
    private static final float BTN_W = 180f;
    private static final float BTN_H = 46f;
    private static final float BTN_GAP = 26f;
    private static final Color PANEL_FILL = new Color(0.05f, 0.08f, 0.12f, 0.80f);
    private static final Color PANEL_BORDER = new Color(0.33f, 0.80f, 0.92f, 0.78f);

    private final LeaderboardManager leaderboard;
    private final boolean tutorialMode;
    private int pageIndex;
    private int hoveredAction = -1;

    public HowToPlayScene(EngineContext context) {
        this(context, null, false);
    }

    public HowToPlayScene(EngineContext context, LeaderboardManager leaderboard, boolean tutorialMode) {
        super(context);
        this.leaderboard = leaderboard;
        this.tutorialMode = tutorialMode;
        this.pageIndex = 0;

        setInputHandler(new HTPInputHandler(this));
        setSceneRenderer(new HTPRenderer(this));
        setResourceLoader(new ResourceLoader() {
            @Override public void load()   { setLoaded(true); }
            @Override public void unload() { setLoaded(false); }
        });
    }

    @Override
    public void update(float dt) {}

    private Rectangle leftActionRect(float ww) {
        return new Rectangle(ww / 2f - BTN_GAP / 2f - BTN_W, 20f, BTN_W, BTN_H);
    }

    private Rectangle rightActionRect(float ww) {
        return new Rectangle(ww / 2f + BTN_GAP / 2f, 20f, BTN_W, BTN_H);
    }

    private void closeScene(boolean completedTutorial) {
        getContext().getSceneManager().pop();
    }

    private void nextPageOrClose() {
        GameAudio.playUiClick(getContext());
        if (pageIndex < PAGE_COUNT - 1) {
            pageIndex++;
        } else {
            closeScene(true);
        }
    }

    private void previousPageOrClose() {
        GameAudio.playUiClick(getContext());
        if (pageIndex > 0) {
            pageIndex--;
        } else {
            closeScene(false);
        }
    }

    void processInput() {
        Keyboard kb    = getContext().getInputManager().getKeyboard();
        Mouse    mouse = getContext().getInputManager().getMouse();
        Renderer r     = getContext().getOutputManager().getRenderer();
        float ww = r.getWorldWidth();

        if (kb.isKeyPressed(Keys.ESCAPE)) {
            previousPageOrClose();
            return;
        }
        if (kb.isKeyPressed(Keys.LEFT) || kb.isKeyPressed(Keys.A)) {
            previousPageOrClose();
            return;
        }
        if (kb.isKeyPressed(Keys.RIGHT) || kb.isKeyPressed(Keys.D)
                || kb.isKeyPressed(Keys.ENTER) || kb.isKeyPressed(Keys.SPACE)) {
            nextPageOrClose();
            return;
        }

        Vector2 mp = mouse.getPosition();
        hoveredAction = -1;
        if (leftActionRect(ww).contains(mp.x, mp.y)) {
            hoveredAction = 0;
        } else if (rightActionRect(ww).contains(mp.x, mp.y)) {
            hoveredAction = 1;
        }

        if (mouse.isButtonPressed(0)) {
            if (hoveredAction == 0) {
                previousPageOrClose();
            } else if (hoveredAction == 1) {
                nextPageOrClose();
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
        r.drawRect(new Rectangle(0, 0, ww, wh), new Color(0f, 0f, 0f, 0.72f), true);

        r.drawTextCentered("HOW TO PLAY",
            new Vector2(ww / 2f, wh - 58f), GameUiTheme.FONT_TITLE_SMALL,
            GameUiTheme.TITLE_PRIMARY);
        r.drawTextCentered(pageIndex == 0
                ? "Page 1 / 2 - Learn the goal, controls, and what to catch or avoid."
                : "Page 2 / 2 - Learn frenzy mode, quiz rewards, and character strengths.",
            new Vector2(ww / 2f, wh - 96f), GameUiTheme.FONT_BODY_SMALL,
            GameUiTheme.TEXT_MUTED);

        if (pageIndex == 0) {
            drawPageOne(r, ww, wh);
        } else {
            drawPageTwo(r, ww, wh);
        }

        drawFooterButtons(r, ww);
        r.end();
    }

    private void drawPageOne(Renderer r, float ww, float wh) {
        float margin = 52f;
        float panelGap = 28f;
        float panelY = 88f;
        float panelTop = wh - 128f;
        float panelW = (ww - margin * 2f - panelGap) / 2f;
        float panelH = panelTop - panelY;
        Rectangle leftPanel = new Rectangle(margin, panelY, panelW, panelH);
        Rectangle rightPanel = new Rectangle(margin + panelW + panelGap, panelY, panelW, panelH);

        drawPanel(r, leftPanel);
        drawPanel(r, rightPanel);

        float leftX = leftPanel.x + 24f;
        float leftY = leftPanel.y + leftPanel.height - 22f;
        leftY = drawSectionHeader(r, "OBJECTIVE", leftX, leftY, GameUiTheme.TEXT_HIGHLIGHT);
        leftY = drawInfoLine(r, "Catch safe data, avoid cyber threats, and stay alive.", leftX, leftY);
        leftY = drawInfoLine(r, "Survive until the main timer reaches 0 to win the round.", leftX, leftY);
        leftY = drawInfoLine(r, "Special objects can trigger quizzes, buffs, or Frenzy Mode.", leftX, leftY - 2f);

        leftY -= 12f;
        leftY = drawSectionHeader(r, "CONTROLS", leftX, leftY, GameUiTheme.TEXT_INFO);
        leftY = drawInfoLine(r, "A / Left Arrow      Move left", leftX, leftY);
        leftY = drawInfoLine(r, "D / Right Arrow     Move right", leftX, leftY);
        leftY = drawInfoLine(r, "SPACE / W / UP      Jump", leftX, leftY);
        leftY = drawInfoLine(r, "1 / 2 / 3 / 4       Answer quiz options", leftX, leftY);
        drawInfoLine(r, "ESC                 Pause or leave the round", leftX, leftY);

        float rightX = rightPanel.x + 24f;
        float rightY = rightPanel.y + rightPanel.height - 22f;
        rightY = drawSectionHeader(r, "GOOD DATA VS THREATS", rightX, rightY, GameUiTheme.TEXT_SUCCESS);
        rightY = drawEntityRow(r, GOOD_BYTE_SPRITE, "Good Byte", "safe catch  |  +5 pts", rightX, rightY, GameUiTheme.TEXT_INFO);
        rightY = drawEntityRow(r, SAFE_EMAIL_SPRITE, "Safe Email", "protected mail  |  +5 pts", rightX, rightY, GameUiTheme.TEXT_SUCCESS);
        rightY = drawEntityRow(r, GOLD_ENVELOPE_SPRITE, "Gold Envelope", "rare catch  |  +10 pts + quiz", rightX, rightY, GameUiTheme.TEXT_HIGHLIGHT);

        rightY -= 10f;
        rightY = drawSectionHeader(r, "THREATS", rightX, rightY, GameUiTheme.TEXT_DANGER);
        rightY = drawEntityRow(r, PHISHING_HOOK_SPRITE, "Phishing Hook", "direct hit  |  -1 life", rightX, rightY, GameUiTheme.TEXT_DANGER);
        rightY = drawEntityRow(r, RANSOMWARE_LOCK_SPRITE, "Ransomware Lock", "wrong answer  |  -1 life", rightX, rightY, GameUiTheme.TEXT_WARNING);
        rightY = drawEntityRow(r, MALWARE_SWARM_SPRITE, "Malware Swarm", "direct hit  |  -1 life", rightX, rightY, new Color(0.78f, 0.45f, 1.0f, 1f));
        drawInfoLine(r, "Tip: if it looks risky, avoid it unless a quiz asks you to act.", rightX, rightY - 2f);
    }

    private void drawPageTwo(Renderer r, float ww, float wh) {
        float margin = 52f;
        float panelGap = 28f;
        float panelY = 88f;
        float panelTop = wh - 128f;
        float panelW = (ww - margin * 2f - panelGap) / 2f;
        float panelH = panelTop - panelY;
        Rectangle leftPanel = new Rectangle(margin, panelY, panelW, panelH);
        Rectangle rightPanel = new Rectangle(margin + panelW + panelGap, panelY, panelW, panelH);

        drawPanel(r, leftPanel);
        drawPanel(r, rightPanel);

        float leftX = leftPanel.x + 24f;
        float leftY = leftPanel.y + leftPanel.height - 22f;
        leftY = drawSectionHeader(r, "FRENZY + QUIZ REWARDS", leftX, leftY, GameUiTheme.TEXT_WARNING);
        leftY = drawFrenzyOrbRow(r, leftX, leftY);
        leftY = drawInfoLine(r, "Catch the orb to trigger a 15-second Frenzy phase.", leftX, leftY);
        leftY = drawInfoLine(r, "The main timer freezes while Frenzy is active.", leftX, leftY);
        leftY = drawEntityRow(r, ROOTKIT_SPRITE, "Rootkit", "extra frenzy-only threat", leftX, leftY - 2f, GameUiTheme.TEXT_WARNING);
        leftY = drawEntityRow(r, SPYWARE_SPRITE, "Spyware", "quiz threat during frenzy", leftX, leftY, GameUiTheme.TEXT_WARNING);
        leftY -= 10f;
        leftY = drawInfoLine(r, "Correct quiz answers give +100 pts.", leftX, leftY);
        leftY = drawInfoLine(r, "Good-data quizzes can also restore +1 health.", leftX, leftY);
        drawInfoLine(r, "Wrong threat quizzes cost 1 life, so answer carefully.", leftX, leftY);

        float rightX = rightPanel.x + 24f;
        float rightY = rightPanel.y + rightPanel.height - 22f;
        rightY = drawSectionHeader(r, "CHARACTER STYLES", rightX, rightY, GameUiTheme.TEXT_HIGHLIGHT);
        rightY = drawCharacterLine(r, "Specter", "450 px/s  |  3 lives  |  x1.2 score",
            "Fastest safe pick for movement-heavy play.", rightX, rightY);
        rightY = drawCharacterLine(r, "Guardian", "300 px/s  |  5 lives  |  x1.0 score",
            "Most forgiving build with the highest survivability.", rightX, rightY);
        drawCharacterLine(r, "Cipher", "500 px/s  |  2 lives  |  x1.5 score",
            "Highest scoring option, but mistakes are punished hard.", rightX, rightY);
    }

    private void drawFooterButtons(Renderer r, float ww) {
        Rectangle leftBox = leftActionRect(ww);
        Rectangle rightBox = rightActionRect(ww);

        String leftLabel = (pageIndex == 0) ? (tutorialMode ? "Skip" : "Back") : "Previous";
        String rightLabel = (pageIndex == PAGE_COUNT - 1)
            ? (tutorialMode ? "Start Game" : "Done")
            : "Next";

        drawActionButton(r, leftBox, leftLabel, hoveredAction == 0);
        drawActionButton(r, rightBox, rightLabel, hoveredAction == 1);
        r.drawTextCentered("ESC / A - previous   D / ENTER - next",
            new Vector2(ww / 2f, 76f), GameUiTheme.FONT_BODY_SMALL, GameUiTheme.TEXT_SUBTLE);
    }

    private void drawActionButton(Renderer r, Rectangle box, String label, boolean hovered) {
        Color bg = hovered ? new Color(0.15f, 0.55f, 0.25f, 0.9f)
            : new Color(0.08f, 0.08f, 0.08f, 0.78f);
        Color border = hovered ? Color.YELLOW : new Color(0.5f, 0.5f, 0.5f, 1f);
        r.drawRect(box, bg, true);
        r.drawRect(box, border, false);
        r.drawTextCentered(label, box, GameUiTheme.FONT_BODY_LARGE,
            hovered ? GameUiTheme.TEXT_HIGHLIGHT : GameUiTheme.TEXT_PRIMARY);
    }

    private void drawPanel(Renderer r, Rectangle panel) {
        r.drawRect(panel, PANEL_FILL, true);
        r.drawRect(panel, PANEL_BORDER, false);
    }

    private float drawSectionHeader(Renderer r, String title, float x, float y, Color color) {
        r.drawText(title, new Vector2(x, y), GameUiTheme.FONT_BODY_LARGE, color);
        return y - 26f;
    }

    private float drawInfoLine(Renderer r, String text, float x, float y) {
        r.drawText(text, new Vector2(x, y), GameUiTheme.FONT_BODY_SMALL, GameUiTheme.TEXT_PRIMARY);
        return y - 20f;
    }

    private float drawEntityRow(Renderer r, String spriteId, String name, String detail, float x, float y, Color nameColor) {
        r.drawSprite(spriteId, new Vector2(x + 12f, y - 8f), 28f, 28f);
        r.drawText(name, new Vector2(x + 34f, y), GameUiTheme.FONT_BODY_SMALL, nameColor);
        r.drawText(detail, new Vector2(x + 170f, y), GameUiTheme.FONT_BODY_SMALL, GameUiTheme.TEXT_PRIMARY);
        return y - 30f;
    }

    private float drawFrenzyOrbRow(Renderer r, float x, float y) {
        r.drawSprite(FRENZY_ORB_SPRITE, new Vector2(x + 14f, y - 8f), 28f, 28f);
        r.drawText("Frenzy Orb", new Vector2(x + 34f, y), GameUiTheme.FONT_BODY_SMALL, GameUiTheme.TEXT_WARNING);
        r.drawText("catch it to trigger Frenzy Mode", new Vector2(x + 170f, y), GameUiTheme.FONT_BODY_SMALL, GameUiTheme.TEXT_PRIMARY);
        return y - 30f;
    }

    private float drawCharacterLine(Renderer r, String name, String stats, String perk, float x, float y) {
        r.drawText(name, new Vector2(x, y), GameUiTheme.FONT_BODY_SMALL, GameUiTheme.TEXT_HIGHLIGHT);
        r.drawText(stats, new Vector2(x + 88f, y), GameUiTheme.FONT_BODY_SMALL, GameUiTheme.TEXT_PRIMARY);
        r.drawText(perk, new Vector2(x + 20f, y - 18f), GameUiTheme.FONT_BODY_TINY, GameUiTheme.TEXT_MUTED);
        return y - 42f;
    }

    private static final class HTPInputHandler extends InputHandler {
        private final HowToPlayScene scene;
        HTPInputHandler(HowToPlayScene scene) {
            super(scene.getContext());
            this.scene = scene;
        }
        @Override public void handleInput() { scene.processInput(); }
    }

    private static final class HTPRenderer extends SceneRenderer {
        private final HowToPlayScene scene;
        HTPRenderer(HowToPlayScene scene) {
            super(scene.getContext());
            this.scene = scene;
        }
        @Override public void render() { scene.renderScene(); }
    }
}
