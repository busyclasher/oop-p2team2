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

    private static final float BTN_W = 160f, BTN_H = 46f;
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

        float col1 = 60f;
        float col2 = ww / 2f + 20f;
        float y    = wh - 110f;
        float gap  = 26f;

        // ── Objective ────────────────────────────────────────────────────────
        r.drawText("OBJECTIVE", new Vector2(col1, y), GameUiTheme.FONT_BODY_LARGE, GameUiTheme.TEXT_HIGHLIGHT);
        y -= gap;
        r.drawText("Catch good data, avoid cyber threats.", new Vector2(col1, y), GameUiTheme.FONT_BODY_SMALL, GameUiTheme.TEXT_PRIMARY);
        y -= gap;
        r.drawText("Catch a Frenzy Orb to trigger Frenzy Mode.", new Vector2(col1, y), GameUiTheme.FONT_BODY_SMALL, GameUiTheme.TEXT_PRIMARY);
        y -= gap;
        r.drawText("Survive until the timer reaches 0 to win.", new Vector2(col1, y), GameUiTheme.FONT_BODY_SMALL, GameUiTheme.TEXT_PRIMARY);
        y -= gap * 1.5f;

        // ── Controls ─────────────────────────────────────────────────────────
        r.drawText("CONTROLS", new Vector2(col1, y), GameUiTheme.FONT_BODY_LARGE, GameUiTheme.TEXT_HIGHLIGHT);
        y -= gap;
        r.drawText("A / Left Arrow    Move left",  new Vector2(col1, y), GameUiTheme.FONT_BODY_SMALL, GameUiTheme.TEXT_PRIMARY); y -= gap;
        r.drawText("D / Right Arrow   Move right", new Vector2(col1, y), GameUiTheme.FONT_BODY_SMALL, GameUiTheme.TEXT_PRIMARY); y -= gap;
        r.drawText("SPACE / W / UP    Jump",       new Vector2(col1, y), GameUiTheme.FONT_BODY_SMALL, GameUiTheme.TEXT_PRIMARY); y -= gap;
        r.drawText("ESC               Pause menu", new Vector2(col1, y), GameUiTheme.FONT_BODY_SMALL, GameUiTheme.TEXT_PRIMARY); y -= gap;
        r.drawText("1 / 2 / 3 / 4     Answer quiz", new Vector2(col1, y), GameUiTheme.FONT_BODY_SMALL, GameUiTheme.TEXT_PRIMARY);
        y -= gap * 1.5f;

        // ── Good Entities ─────────────────────────────────────────────────────
        r.drawText("GOOD ENTITIES  (catch these!)", new Vector2(col1, y), GameUiTheme.FONT_BODY_LARGE, GameUiTheme.TEXT_SUCCESS);
        y -= gap;
        r.drawText("Laptop       +5 pts",  new Vector2(col1, y), GameUiTheme.FONT_BODY_SMALL, new Color(0.52f, 0.84f, 1.0f, 1f)); y -= gap;
        r.drawText("Shield       +5 pts",  new Vector2(col1, y), GameUiTheme.FONT_BODY_SMALL, new Color(0.45f, 1.0f, 0.65f, 1f)); y -= gap;
        r.drawText("Phone        +10 pts  (triggers quiz - correct = +100 pts & +1 Life)",
            new Vector2(col1, y), GameUiTheme.FONT_BODY_SMALL, GameUiTheme.TEXT_HIGHLIGHT);
        y -= gap * 1.5f;

        // ── Bad Entities ──────────────────────────────────────────────────────
        float y2 = wh - 110f;
        r.drawText("BAD ENTITIES  (avoid!)", new Vector2(col2, y2), GameUiTheme.FONT_BODY_LARGE, GameUiTheme.TEXT_DANGER);
        y2 -= gap;
        r.drawText("Phishing Hook   -1 Life", new Vector2(col2, y2), GameUiTheme.FONT_BODY_SMALL, new Color(1.0f, 0.42f, 0.48f, 1f)); y2 -= gap;
        r.drawText("Ransomware      triggers quiz - wrong = -1 Life", new Vector2(col2, y2), GameUiTheme.FONT_BODY_SMALL, GameUiTheme.TEXT_HIGHLIGHT); y2 -= gap;
        r.drawText("Malware Swarm   -1 Life", new Vector2(col2, y2), GameUiTheme.FONT_BODY_SMALL, new Color(0.78f, 0.45f, 1.0f, 1f));
        y2 -= gap * 1.5f;

        // ── Frenzy Mode ───────────────────────────────────────────────────────
        r.drawText("FRENZY MODE", new Vector2(col2, y2), GameUiTheme.FONT_BODY_LARGE, GameUiTheme.TEXT_WARNING);
        y2 -= gap;
        r.drawText("Catch a glowing Frenzy Orb to trigger it.", new Vector2(col2, y2), GameUiTheme.FONT_BODY_SMALL, GameUiTheme.TEXT_PRIMARY); y2 -= gap;
        r.drawText("Frenzy lasts 15 seconds and freezes the main timer.", new Vector2(col2, y2), GameUiTheme.FONT_BODY_SMALL, GameUiTheme.TEXT_PRIMARY); y2 -= gap;
        r.drawText("Extra enemies: Rootkit and Spyware.", new Vector2(col2, y2), GameUiTheme.FONT_BODY_SMALL, GameUiTheme.TEXT_PRIMARY);
        y2 -= gap * 1.5f;

        // ── Characters ───────────────────────────────────────────────────────
        r.drawText("CHARACTERS", new Vector2(col2, y2), GameUiTheme.FONT_BODY_LARGE, GameUiTheme.TEXT_HIGHLIGHT);
        y2 -= gap;
        r.drawText("Specter   450 px/s  3 lives  x1.2 score", new Vector2(col2, y2), GameUiTheme.FONT_BODY_SMALL, GameUiTheme.TEXT_PRIMARY); y2 -= gap;
        r.drawText("           Perk: Speed Demon - bonus pts per catch", new Vector2(col2, y2), GameUiTheme.FONT_BODY_TINY, GameUiTheme.TEXT_MUTED); y2 -= gap;
        r.drawText("Guardian  300 px/s  5 lives  x1.0 score", new Vector2(col2, y2), GameUiTheme.FONT_BODY_SMALL, GameUiTheme.TEXT_PRIMARY); y2 -= gap;
        r.drawText("           Perk: Iron Defense - 2 extra starting lives", new Vector2(col2, y2), GameUiTheme.FONT_BODY_TINY, GameUiTheme.TEXT_MUTED); y2 -= gap;
        r.drawText("Cipher    500 px/s  2 lives  x1.5 score", new Vector2(col2, y2), GameUiTheme.FONT_BODY_SMALL, GameUiTheme.TEXT_PRIMARY); y2 -= gap;
        r.drawText("           Perk: Data Rush - highest score but very risky", new Vector2(col2, y2), GameUiTheme.FONT_BODY_TINY, GameUiTheme.TEXT_MUTED);

        // ── Quiz tip ─────────────────────────────────────────────────────────
        r.drawText("TIP: Answer quiz questions correctly for +100 pts bonus!",
            new Vector2(col1, y), GameUiTheme.FONT_BODY_SMALL, GameUiTheme.TEXT_INFO);

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
