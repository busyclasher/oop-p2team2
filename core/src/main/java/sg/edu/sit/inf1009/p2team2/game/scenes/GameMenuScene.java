package sg.edu.sit.inf1009.p2team2.game.scenes;

import sg.edu.sit.inf1009.p2team2.engine.io.input.Keys;
import sg.edu.sit.inf1009.p2team2.engine.io.output.EngineColor;
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
import sg.edu.sit.inf1009.p2team2.game.save.RunSaveManager;
import sg.edu.sit.inf1009.p2team2.game.ui.GameUiTheme;

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

        if (kb.isKeyPressed(Keys.UP) || kb.isKeyPressed(Keys.W)) {
            selectedIndex    = (selectedIndex - 1 + menuItems.size()) % menuItems.size();
            keyboardCooldown = COOLDOWN_FRAMES;
            GameAudio.playUiClick(getContext());
        } else if (kb.isKeyPressed(Keys.DOWN) || kb.isKeyPressed(Keys.S)) {
            selectedIndex    = (selectedIndex + 1) % menuItems.size();
            keyboardCooldown = COOLDOWN_FRAMES;
            GameAudio.playUiClick(getContext());
        }

        if (kb.isKeyPressed(Keys.ENTER) || kb.isKeyPressed(Keys.SPACE)) {
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
        r.drawTextCentered("CYBERSCOUTS",
            new Vector2(cx, cy + 272f), GameUiTheme.FONT_TITLE, GameUiTheme.TITLE_PRIMARY);
        r.drawTextCentered("Protect the Network - Catch Good Data, Neutralize Threats",
            new Vector2(cx, cy + 230f), GameUiTheme.FONT_BODY, GameUiTheme.TEXT_MUTED);

        for (int i = 0; i < menuItems.size(); i++) {
            menuItems.get(i).render(r, i == selectedIndex);
        }

        r.drawTextCentered("Arrow Keys / WASD / Mouse  |  Enter / Click to select",
            new Vector2(cx, 28f), GameUiTheme.FONT_BODY_SMALL, GameUiTheme.TEXT_SUBTLE);

        r.end();
    }

    private void activate(int index) {
        GameAudio.playUiClick(getContext());
        switch (menuItems.get(index).label) {
            case "Start Game":
                RunSaveManager.RunSnapshot savedRun = RunSaveManager.load();
                if (savedRun != null) {
                    getContext().getSceneManager().push(
                        new StartGamePromptScene(getContext(), leaderboard, savedRun));
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
                getContext().exit();
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
            EngineColor bg  = selected ? new EngineColor(0.15f, 0.55f, 0.25f, 0.85f)
                                 : new EngineColor(0.10f, 0.10f, 0.10f, 0.65f);
            EngineColor txt = selected ? GameUiTheme.TEXT_HIGHLIGHT : GameUiTheme.TEXT_PRIMARY;
            r.drawRect(box, bg,         true);
            r.drawRect(box, EngineColor.WHITE, false);
            r.drawTextCentered(label, box, GameUiTheme.FONT_BODY_LARGE, txt);
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
