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
import sg.edu.sit.inf1009.p2team2.game.ui.GameUiTheme;

/**
 * How To Play screen — explains rules, controls, entities and characters.
 * ESC / Enter / Back button returns to the main menu.
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

    private static final float BTN_W = 160f, BTN_H = 46f;
    private static final Color PANEL_FILL = new Color(0.05f, 0.08f, 0.12f, 0.78f);
    private static final Color PANEL_BORDER = new Color(0.33f, 0.80f, 0.92f, 0.75f);
    private boolean backHovered = false;

    public HowToPlayScene(EngineContext context) {
        super(context);

        setInputHandler(new HTPInputHandler(this));
        setSceneRenderer(new HTPRenderer(this));
        setResourceLoader(new ResourceLoader() {
            @Override public void load()   { setLoaded(true); }
            @Override public void unload() { setLoaded(false); }
        });
    }

    @Override
    public void update(float dt) {}

    private Rectangle backRect(float ww) {
        return new Rectangle(ww / 2f - BTN_W / 2f, 20f, BTN_W, BTN_H);
    }

    void processInput() {
        Keyboard kb    = getContext().getInputManager().getKeyboard();
        Mouse    mouse = getContext().getInputManager().getMouse();
        Renderer r     = getContext().getOutputManager().getRenderer();
        float ww = r.getWorldWidth();

        if (kb.isKeyPressed(Input.Keys.ESCAPE)
                || kb.isKeyPressed(Input.Keys.ENTER)
                || kb.isKeyPressed(Input.Keys.SPACE)) {
            GameAudio.playUiClick(getContext());
            getContext().getSceneManager().pop();
            return;
        }

        Vector2 mp = mouse.getPosition();
        backHovered = backRect(ww).contains(mp.x, mp.y);
        if (backHovered && mouse.isButtonPressed(0)) {
            GameAudio.playUiClick(getContext());
            getContext().getSceneManager().pop();
        }
    }

    void renderScene() {
        Renderer r  = getContext().getOutputManager().getRenderer();
        float ww = r.getWorldWidth();
        float wh = r.getWorldHeight();

        r.clear();
        r.begin();

        r.drawBackground("menu-scene.png");
        r.drawRect(new Rectangle(0, 0, ww, wh), new Color(0f, 0f, 0f, 0.70f), true);

        // ── Title ────────────────────────────────────────────────────────────
        r.drawTextCentered("HOW TO PLAY",
            new Vector2(ww / 2f, wh - 58f), GameUiTheme.FONT_TITLE_SMALL,
            GameUiTheme.TITLE_PRIMARY);
        r.drawTextCentered("Catch the right tech, dodge the threats, and survive the countdown.",
            new Vector2(ww / 2f, wh - 96f), GameUiTheme.FONT_BODY_SMALL,
            GameUiTheme.TEXT_MUTED);

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
        leftY = drawInfoLine(r, "Catch good data, avoid cyber threats.", leftX, leftY);
        leftY = drawInfoLine(r, "Catch a Frenzy Orb to trigger Frenzy Mode.", leftX, leftY);
        leftY = drawInfoLine(r, "Survive until the timer reaches 0 to win.", leftX, leftY - 4f);

        leftY -= 12f;
        leftY = drawSectionHeader(r, "CONTROLS", leftX, leftY, GameUiTheme.TEXT_INFO);
        leftY = drawInfoLine(r, "A / Left Arrow    Move left", leftX, leftY);
        leftY = drawInfoLine(r, "D / Right Arrow   Move right", leftX, leftY);
        leftY = drawInfoLine(r, "SPACE / W / UP    Jump", leftX, leftY);
        leftY = drawInfoLine(r, "ESC               Pause menu", leftX, leftY);
        leftY = drawInfoLine(r, "1 / 2 / 3 / 4     Answer quiz", leftX, leftY - 2f);

        leftY -= 12f;
        leftY = drawSectionHeader(r, "GOOD CATCHES", leftX, leftY, GameUiTheme.TEXT_SUCCESS);
        leftY = drawEntityRow(r, GOOD_BYTE_SPRITE, "Good Byte", "+5 pts", leftX, leftY, GameUiTheme.TEXT_INFO);
        leftY = drawEntityRow(r, SAFE_EMAIL_SPRITE, "Safe Email", "+5 pts", leftX, leftY, GameUiTheme.TEXT_SUCCESS);
        leftY = drawEntityRow(r, GOLD_ENVELOPE_SPRITE, "Gold Envelope", "+10 pts + quiz trigger", leftX, leftY, GameUiTheme.TEXT_HIGHLIGHT);

        leftY -= 8f;
        leftY = drawSectionHeader(r, "THREATS", leftX, leftY, GameUiTheme.TEXT_DANGER);
        leftY = drawEntityRow(r, PHISHING_HOOK_SPRITE, "Phishing Hook", "-1 life", leftX, leftY, GameUiTheme.TEXT_DANGER);
        leftY = drawEntityRow(r, RANSOMWARE_LOCK_SPRITE, "Ransomware Lock", "wrong quiz = -1 life", leftX, leftY, GameUiTheme.TEXT_WARNING);
        drawEntityRow(r, MALWARE_SWARM_SPRITE, "Malware Swarm", "-1 life", leftX, leftY, new Color(0.78f, 0.45f, 1.0f, 1f));

        float rightX = rightPanel.x + 24f;
        float rightY = rightPanel.y + rightPanel.height - 22f;
        rightY = drawSectionHeader(r, "FRENZY MODE", rightX, rightY, GameUiTheme.TEXT_WARNING);
        rightY = drawFrenzyOrbRow(r, rightX, rightY);
        rightY = drawInfoLine(r, "The main timer freezes for 15 seconds.", rightX, rightY);
        rightY = drawEntityRow(r, ROOTKIT_SPRITE, "Rootkit", "extra frenzy-only threat", rightX, rightY - 2f, GameUiTheme.TEXT_WARNING);
        rightY = drawEntityRow(r, SPYWARE_SPRITE, "Spyware", "quiz threat during frenzy", rightX, rightY, GameUiTheme.TEXT_WARNING);

        rightY -= 12f;
        rightY = drawSectionHeader(r, "CHARACTERS", rightX, rightY, GameUiTheme.TEXT_HIGHLIGHT);
        rightY = drawCharacterLine(r, "Specter", "450 px/s  |  3 lives  |  x1.2 score", "Perk: Speed Demon - bonus pts per catch", rightX, rightY);
        rightY = drawCharacterLine(r, "Guardian", "300 px/s  |  5 lives  |  x1.0 score", "Perk: Iron Defense - 2 extra starting lives", rightX, rightY);
        rightY = drawCharacterLine(r, "Cipher", "500 px/s  |  2 lives  |  x1.5 score", "Perk: Data Rush - highest score but very risky", rightX, rightY);

        rightY -= 10f;
        rightY = drawSectionHeader(r, "QUIZ BONUS", rightX, rightY, GameUiTheme.TEXT_INFO);
        rightY = drawInfoLine(r, "Correct answers give +100 pts and +1 life.", rightX, rightY);
        drawInfoLine(r, "Wrong answers on bad quizzes cost 1 life.", rightX, rightY);

        // ── Back button ──────────────────────────────────────────────────────
        Rectangle box = backRect(ww);
        Color bg = backHovered ? new Color(0.15f, 0.55f, 0.25f, 0.9f)
                               : new Color(0.08f, 0.08f, 0.08f, 0.75f);
        r.drawRect(box, bg, true);
        r.drawRect(box, backHovered ? Color.YELLOW : new Color(0.5f, 0.5f, 0.5f, 1f), false);
        r.drawTextCentered("Back", box, GameUiTheme.FONT_BODY_LARGE,
            backHovered ? GameUiTheme.TEXT_HIGHLIGHT : GameUiTheme.TEXT_PRIMARY);

        r.end();
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

    // ── Inner classes ────────────────────────────────────────────────────────

    private static final class HTPInputHandler extends InputHandler {
        private final HowToPlayScene scene;
        HTPInputHandler(HowToPlayScene s) { super(s.getContext()); this.scene = s; }
        @Override public void handleInput() { scene.processInput(); }
    }

    private static final class HTPRenderer extends SceneRenderer {
        private final HowToPlayScene scene;
        HTPRenderer(HowToPlayScene s) { super(s.getContext()); this.scene = s; }
        @Override public void render() { scene.renderScene(); }
    }
}
