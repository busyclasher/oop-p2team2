package sg.edu.sit.inf1009.p2team2.game.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
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
import sg.edu.sit.inf1009.p2team2.game.leaderboard.LeaderboardManager;

/**
 * CyberScouts main menu.
 *
 * Menu items: Start Game | Leaderboard | Exit
 * Navigation: Arrow keys / WASD / Mouse hover + click.
 */
public class GameMenuScene extends Scene {

    private static final String BACKGROUND = "menu-scene.png";

    private final LeaderboardManager leaderboard;
    private final List<MenuItem>     menuItems;
    private int  selectedIndex;
    private int  keyboardCooldown;
    private static final int COOLDOWN_FRAMES = 10;

    public GameMenuScene(EngineContext context, LeaderboardManager leaderboard) {
        super(context);
        this.leaderboard      = leaderboard;
        this.menuItems        = new ArrayList<>();
        this.selectedIndex    = 0;
        this.keyboardCooldown = 0;

        setInputHandler(new GameMenuInputHandler(this));
        setSceneRenderer(new GameMenuRenderer(this));
        setResourceLoader(new ResourceLoader() {
            @Override public void load()   { setLoaded(true); }
            @Override public void unload() { setLoaded(false); }
        });
    }

    // ── Scene lifecycle ─────────────────────────────────────────────────────
    
    
    @Override
    public void onEnter() {
        selectedIndex    = 0;
        keyboardCooldown = 0;
        buildMenuItems();
        updateLayout();
    }

    @Override
    public void update(float dt) {
        if (keyboardCooldown > 0) keyboardCooldown--;
        updateLayout();
    }

    // ── Internal helpers ────────────────────────────────────────────────────

    private void buildMenuItems() {
        menuItems.clear();
        menuItems.add(new MenuItem("Start Game"));
        menuItems.add(new MenuItem("Leaderboard"));
        menuItems.add(new MenuItem("How to Play"));
        menuItems.add(new MenuItem("Settings"));
        menuItems.add(new MenuItem("Exit"));
    }

    void processInput() {
        Keyboard kb    = getContext().getInputManager().getKeyboard();
        Mouse    mouse = getContext().getInputManager().getMouse();

        if (kb.isKeyPressed(Input.Keys.UP) || kb.isKeyPressed(Input.Keys.W)) {
            selectedIndex    = (selectedIndex - 1 + menuItems.size()) % menuItems.size();
            keyboardCooldown = COOLDOWN_FRAMES;
            GameAudio.playUiClick(getContext());
        } else if (kb.isKeyPressed(Input.Keys.DOWN) || kb.isKeyPressed(Input.Keys.S)) {
            selectedIndex    = (selectedIndex + 1) % menuItems.size();
            keyboardCooldown = COOLDOWN_FRAMES;
            GameAudio.playUiClick(getContext());
        }

        if (kb.isKeyPressed(Input.Keys.ENTER) || kb.isKeyPressed(Input.Keys.SPACE)) {
            activate(selectedIndex);
        }

        if (keyboardCooldown == 0) {
            Vector2 mp = mouse.getPosition();
            for (int i = 0; i < menuItems.size(); i++) {
                if (menuItems.get(i).contains(mp)) {
                    selectedIndex = i;
                    break;
                }
            }
        }

        if (mouse.isButtonPressed(0)) {
            Vector2 mp = mouse.getPosition();
            for (int i = 0; i < menuItems.size(); i++) {
                if (menuItems.get(i).contains(mp)) {
                    activate(i);
                    break;
                }
            }
        }
    }

    void renderScene() {
        Renderer r = getContext().getOutputManager().getRenderer();
        r.clear();
        r.begin();

        r.drawBackground(BACKGROUND);

        float cx = r.getWorldWidth()  / 2f;
        float cy = r.getWorldHeight() / 2f;

        // Title — kept well above the menu items (first item top ≈ cy+140)
        r.drawText("CYBERSCOUTS",
            new Vector2(cx - 140f, cy + 270f), "default", new Color(0.2f, 0.9f, 0.4f, 1f));
        r.drawText("Protect the Network - Catch Good Data, Neutralize Threats",
            new Vector2(cx - 290f, cy + 230f), "default", new Color(0.75f, 0.75f, 0.75f, 1f));

        for (int i = 0; i < menuItems.size(); i++) {
            menuItems.get(i).render(r, i == selectedIndex);
        }

        r.drawText("Arrow Keys / WASD / Mouse  |  Enter / Click to select",
            new Vector2(cx - 240f, 28f), "default", new Color(0.6f, 0.6f, 0.6f, 1f));

        r.end();
    }

    private void activate(int index) {
        GameAudio.playUiClick(getContext());
        switch (menuItems.get(index).label) {
            case "Start Game":
                if (leaderboard.getLastCharacter() != null) {
                    getContext().getSceneManager().push(
                        new StartGamePromptScene(getContext(), leaderboard, leaderboard.getLastCharacter()));
                } else {
                    getContext().getSceneManager().push(new CharacterSelectScene(getContext(), leaderboard));
                }
                break;
            case "Leaderboard":
                getContext().getSceneManager().push(new LeaderboardScene(getContext(), leaderboard));
                break;
            case "How to Play":
                getContext().getSceneManager().push(new HowToPlayScene(getContext()));
                break;
            case "Settings":
                getContext().getSceneManager().push(new SettingsScene(getContext()));
                break;
            case "Exit":
                getContext().stop();
                Gdx.app.exit();
                break;
        }
    }

    private void updateLayout() {
        Renderer r     = getContext().getOutputManager().getRenderer();
        float    cx    = r.getWorldWidth()  / 2f;
        float    cy    = r.getWorldHeight() / 2f;
        float    space = 75f;
        float    startY = cy + (menuItems.size() - 1) * space / 2f;

        for (int i = 0; i < menuItems.size(); i++) {
            menuItems.get(i).position.set(cx, startY - i * space);
        }
    }

    // ── Inner helpers ────────────────────────────────────────────────────────

    private static final class MenuItem {
        final String  label;
        final Vector2 position = new Vector2();
        static final float W = 220f, H = 54f;

        MenuItem(String label) { this.label = label; }

        boolean contains(Vector2 p) {
            return p.x >= position.x - W / 2 && p.x <= position.x + W / 2
                && p.y >= position.y - H / 2 && p.y <= position.y + H / 2;
        }

        void render(Renderer r, boolean selected) {
            float l = position.x - W / 2, b = position.y - H / 2;
            com.badlogic.gdx.math.Rectangle box = new com.badlogic.gdx.math.Rectangle(l, b, W, H);
            Color bg  = selected ? new Color(0.15f, 0.55f, 0.25f, 0.85f)
                                 : new Color(0.10f, 0.10f, 0.10f, 0.65f);
            Color txt = selected ? Color.YELLOW : Color.WHITE;
            r.drawRect(box, bg,         true);
            r.drawRect(box, Color.WHITE, false);
            r.drawText(label, new Vector2(position.x - 65f, position.y + 8f), "default", txt);
        }
    }

    // ── Inner InputHandler ───────────────────────────────────────────────────

    private static final class GameMenuInputHandler extends InputHandler {
        private final GameMenuScene scene;
        GameMenuInputHandler(GameMenuScene scene) {
            super(scene.getContext());
            this.scene = scene;
        }
        @Override public void handleInput() { scene.processInput(); }
    }

    // ── Inner SceneRenderer ──────────────────────────────────────────────────

    private static final class GameMenuRenderer extends SceneRenderer {
        private final GameMenuScene scene;
        GameMenuRenderer(GameMenuScene scene) {
            super(scene.getContext());
            this.scene = scene;
        }
        @Override public void render() { scene.renderScene(); }
    }
}
