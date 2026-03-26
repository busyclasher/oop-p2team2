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
import sg.edu.sit.inf1009.p2team2.game.entities.CharacterType;
import sg.edu.sit.inf1009.p2team2.game.leaderboard.LeaderboardManager;
import sg.edu.sit.inf1009.p2team2.game.ui.GameUiTheme;

/**
 * Character selection screen.
 * Left/Right arrows or mouse click to select, Enter/click to confirm.
 */
public class CharacterSelectScene extends Scene {

    private static final CharacterType[] CHARS = CharacterType.values();
    private static final float CARD_W  = 240f;
    private static final float CARD_H  = 430f;
    private static final float CHAR_W  = 148f;
    private static final float CHAR_H  = 170f;
    private static final int   COOLDOWN = 12;

    private final LeaderboardManager leaderboard;
    private int selectedIndex;
    private int keyboardCooldown;

    public CharacterSelectScene(EngineContext context, LeaderboardManager leaderboard) {
        super(context);
        this.leaderboard      = leaderboard;
        this.selectedIndex    = 0;
        this.keyboardCooldown = 0;

        setInputHandler(new CharSelectInputHandler(this));
        setSceneRenderer(new CharSelectRenderer(this));
        setResourceLoader(new ResourceLoader() {
            @Override public void load()   { setLoaded(true); }
            @Override public void unload() { setLoaded(false); }
        });
    }

    @Override
    public void onEnter() {
        selectedIndex    = 0;
        keyboardCooldown = COOLDOWN;
    }

    @Override
    public void update(float dt) {
        if (keyboardCooldown > 0) keyboardCooldown--;
    }

    // ── Input ────────────────────────────────────────────────────────────────

    void processInput() {
        Keyboard kb    = getContext().getInputManager().getKeyboard();
        Mouse    mouse = getContext().getInputManager().getMouse();
        Renderer r     = getContext().getOutputManager().getRenderer();

        if (kb.isKeyPressed(Input.Keys.ESCAPE)) {
            GameAudio.playUiClick(getContext());
            getContext().getSceneManager().pop();
            return;
        }

        if (keyboardCooldown == 0) {
            if (kb.isKeyPressed(Input.Keys.LEFT) || kb.isKeyPressed(Input.Keys.A)) {
                selectedIndex    = (selectedIndex - 1 + CHARS.length) % CHARS.length;
                keyboardCooldown = COOLDOWN;
                GameAudio.playUiClick(getContext());
            } else if (kb.isKeyPressed(Input.Keys.RIGHT) || kb.isKeyPressed(Input.Keys.D)) {
                selectedIndex    = (selectedIndex + 1) % CHARS.length;
                keyboardCooldown = COOLDOWN;
                GameAudio.playUiClick(getContext());
            }
        }

        if (kb.isKeyPressed(Input.Keys.ENTER) || kb.isKeyPressed(Input.Keys.SPACE)) {
            startGame();
            return;
        }

        // Mouse click on a card
        Vector2 mp = mouse.getPosition();
        float cx = r.getWorldWidth() / 2f;
        float cy = r.getWorldHeight() / 2f;
        for (int i = 0; i < CHARS.length; i++) {
            Rectangle card = cardRect(cx, cy, i);
            if (mouse.isButtonPressed(0) && card.contains(mp.x, mp.y)) {
                if (selectedIndex == i) {
                    startGame();
                } else {
                    selectedIndex = i;
                    GameAudio.playUiClick(getContext());
                }
                return;
            }
            if (card.contains(mp.x, mp.y)) {
                selectedIndex = i;
            }
        }
    }

    private Rectangle cardRect(float cx, float cy, int index) {
        float spacing = CARD_W + 60f;
        float totalW  = CHARS.length * CARD_W + (CHARS.length - 1) * 60f;
        float startX  = cx - totalW / 2f;
        float cardX   = startX + index * spacing;
        return new Rectangle(cardX, cy - CARD_H / 2f - 20f, CARD_W, CARD_H);
    }

    private void startGame() {
        GameAudio.playUiClick(getContext());
        CharacterType chosen = CHARS[selectedIndex];
        leaderboard.setLastCharacter(chosen);
        getContext().getSceneManager().pop();                                       // remove CharacterSelectScene
        getContext().getSceneManager().push(new GamePlayScene(getContext(), leaderboard, chosen));
        getContext().getSceneManager().push(new HowToPlayScene(getContext(), leaderboard, true));
    }

    // ── Render ───────────────────────────────────────────────────────────────

    void renderScene() {
        Renderer r  = getContext().getOutputManager().getRenderer();
        float    ww = r.getWorldWidth();
        float    wh = r.getWorldHeight();
        float    cx = ww / 2f, cy = wh / 2f;

        r.clear();
        r.begin();

        r.drawBackground("menu-scene.png");
        r.drawRect(new Rectangle(0, 0, ww, wh), new Color(0f, 0f, 0f, 0.65f), true);

        // Title
        r.drawTextCentered("SELECT YOUR CHARACTER",
            new Vector2(cx, wh - 58f), GameUiTheme.FONT_TITLE_SMALL,
            GameUiTheme.TITLE_PRIMARY);
        r.drawTextCentered("Click a card or use Left / Right arrows, then Enter to confirm",
            new Vector2(cx, wh - 94f), GameUiTheme.FONT_BODY,
            GameUiTheme.TEXT_MUTED);

        for (int i = 0; i < CHARS.length; i++) {
            CharacterType ch  = CHARS[i];
            boolean       sel = (i == selectedIndex);
            Rectangle     card = cardRect(cx, cy, i);

            // Card background
            Color cardBg = sel
                ? new Color(0.10f, 0.40f, 0.18f, 0.90f)
                : new Color(0.08f, 0.08f, 0.08f, 0.80f);
            r.drawRect(card, cardBg, true);
            r.drawRect(card, sel ? Color.YELLOW : new Color(0.45f, 0.45f, 0.45f, 1f), false);

            // Character sprite
            float spriteX = card.x + (CARD_W - CHAR_W) / 2f + CHAR_W / 2f;
            float spriteY = card.y + CARD_H - 28f - CHAR_H / 2f;
            r.drawSprite(ch.getSprite(), new Vector2(spriteX, spriteY), CHAR_W, CHAR_H);

            // Name
            Color nameColor = sel ? GameUiTheme.TEXT_HIGHLIGHT : GameUiTheme.TEXT_PRIMARY;
            r.drawTextCentered(ch.getName(),
                new Rectangle(card.x, card.y + CARD_H - CHAR_H - 92f, CARD_W, 34f),
                GameUiTheme.FONT_BODY_LARGE, nameColor);

            // Stats
            float statsY = card.y + CARD_H - CHAR_H - 114f;
            r.drawText("Speed:  " + (int) ch.getSpeed() + " px/s",
                new Vector2(card.x + 14f, statsY),         GameUiTheme.FONT_BODY, GameUiTheme.TEXT_PRIMARY);
            r.drawText("Lives:  " + ch.getLives(),
                new Vector2(card.x + 14f, statsY - 28f),   GameUiTheme.FONT_BODY, GameUiTheme.TEXT_PRIMARY);
            r.drawText("Score:  x" + ch.getScoreMultiplier(),
                new Vector2(card.x + 14f, statsY - 56f),   GameUiTheme.FONT_BODY, GameUiTheme.TEXT_PRIMARY);

            // Perk
            r.drawText("Perk: " + ch.getPerkName(),
                new Vector2(card.x + 14f, statsY - 90f),   GameUiTheme.FONT_BODY, GameUiTheme.TEXT_SUCCESS);
            // perk desc (two lines split on \n)
            String[] descLines = ch.getPerkDesc().split("\n");
            for (int l = 0; l < descLines.length; l++) {
                r.drawText(descLines[l],
                    new Vector2(card.x + 14f, statsY - 114f - l * 24f),
                    GameUiTheme.FONT_BODY_SMALL, GameUiTheme.TEXT_MUTED);
            }
        }

        // Footer
        r.drawTextCentered("ESC - Back   Enter / Double-click - Confirm",
            new Vector2(cx, 28f), GameUiTheme.FONT_BODY_SMALL,
            GameUiTheme.TEXT_SUBTLE);

        r.end();
    }

    // ── Inner classes ────────────────────────────────────────────────────────

    private static final class CharSelectInputHandler extends InputHandler {
        private final CharacterSelectScene scene;
        CharSelectInputHandler(CharacterSelectScene s) { super(s.getContext()); this.scene = s; }
        @Override public void handleInput() { scene.processInput(); }
    }

    private static final class CharSelectRenderer extends SceneRenderer {
        private final CharacterSelectScene scene;
        CharSelectRenderer(CharacterSelectScene s) { super(s.getContext()); this.scene = s; }
        @Override public void render() { scene.renderScene(); }
    }
}
