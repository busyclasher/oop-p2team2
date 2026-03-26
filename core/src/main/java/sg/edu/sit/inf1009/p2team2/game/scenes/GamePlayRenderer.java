package sg.edu.sit.inf1009.p2team2.game.scenes;

import static sg.edu.sit.inf1009.p2team2.game.scenes.GamePlayScene.*;

import sg.edu.sit.inf1009.p2team2.engine.io.output.EngineColor;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import sg.edu.sit.inf1009.p2team2.engine.entity.Entity;
import sg.edu.sit.inf1009.p2team2.engine.entity.components.TransformComponent;
import sg.edu.sit.inf1009.p2team2.engine.io.output.Renderer;
import sg.edu.sit.inf1009.p2team2.engine.scene.SceneRenderer;
import sg.edu.sit.inf1009.p2team2.game.components.GameEntityComponent;
import sg.edu.sit.inf1009.p2team2.game.entities.BuffType;
import sg.edu.sit.inf1009.p2team2.game.entities.EntityFactory;
import sg.edu.sit.inf1009.p2team2.game.entities.EntityType;
import sg.edu.sit.inf1009.p2team2.game.quiz.QuizManager;
import sg.edu.sit.inf1009.p2team2.game.quiz.QuizResult;
import sg.edu.sit.inf1009.p2team2.game.scenes.GamePlayScene.GameState;
import sg.edu.sit.inf1009.p2team2.game.ui.GameUiTheme;

final class GamePlayRenderer extends SceneRenderer {
        private final GamePlayScene scene;

        // Colors
        private static final EngineColor COL_HUD_BG   = new EngineColor(0f, 0f, 0f, 0.55f);
        private static final EngineColor COL_HEART     = new EngineColor(0.95f, 0.25f, 0.25f, 1f);
        private static final EngineColor COL_HEART_EMPTY = new EngineColor(0.4f, 0.15f, 0.15f, 0.6f);
        private static final EngineColor COL_FRENZY_BANNER = new EngineColor(1f, 0.35f, 0f, 1f);
        private static final EngineColor COL_TIMER_ICON_BG = new EngineColor(0.10f, 0.16f, 0.22f, 0.85f);
        private static final EngineColor COL_TIMER_ICON_RING = new EngineColor(0.52f, 0.84f, 1.0f, 1f);
        private static final EngineColor COL_WARNING = new EngineColor(1f, 0.25f, 0.25f, 1f);
        private static final EngineColor COL_OVERLAY   = new EngineColor(0f, 0f, 0f, 0.72f);
        private static final EngineColor COL_QUIZ_BG   = new EngineColor(0.05f, 0.08f, 0.18f, 0.95f);
        private static final EngineColor COL_WIN       = new EngineColor(0.15f, 0.95f, 0.40f, 1f);
        private static final EngineColor COL_LOSE      = new EngineColor(0.95f, 0.25f, 0.25f, 1f);

        GamePlayRenderer(GamePlayScene scene) {
            super(scene.getContext());
            this.scene = scene;
        }

        @Override
        public void render() {
            Renderer r = scene.getContext().getOutputManager().getRenderer();
            r.clear();
            r.begin();

            drawBackground(r);
            drawEntities(r);
            drawHUD(r);

            switch (scene.getGameState()) {
                case QUIZ:
                    drawQuizOverlay(r);
                    break;
                case QUIZ_FEEDBACK:
                    drawQuizFeedback(r);
                    break;
                case BUFF_SELECT:
                    drawBuffSelect(r);
                    break;
                case TRANSITION_TO_FRENZY:
                    drawFrenzyTransition(r);
                    break;
                case GAME_OVER:
                    drawGameOverOverlay(r);
                    break;
                case WIN:
                    drawWinOverlay(r);
                    break;
                default:
                    break;
            }

            drawStatusBanner(r);

            r.end();
        }

        // ── Background ──────────────────────────────────────────────────────

        private void drawBackground(Renderer r) {
            // In feedback state use the background of the state we're returning to
            GameState bg_state = (scene.getGameState() == GameState.QUIZ)          ? scene.getPreQuizState()
                               : (scene.getGameState() == GameState.QUIZ_FEEDBACK) ? scene.getPostFeedbackState()
                               : (scene.getGameState() == GameState.BUFF_SELECT)   ? scene.getPreBuffState()
                               : scene.getGameState();
            String bg;
            if (bg_state == GameState.FRENZY) bg = BACKGROUND_FRENZY;
            else if (bg_state == GameState.TRANSITION_TO_FRENZY) bg = BACKGROUND_TRANSITION;
            else bg = BACKGROUND_NORMAL;
            r.drawBackground(bg);
        }

        // ── Entities ────────────────────────────────────────────────────────

        private void drawEntities(Renderer r) {
            for (Entity entity : scene.getEntityManager().getAllEntities()) {
                TransformComponent tf = entity.get(TransformComponent.class);
                if (tf == null) continue;

                GameEntityComponent gec = entity.get(GameEntityComponent.class);
                if (gec == null) continue;

                EngineColor color = gec.getEntityType().getColor();
                float w, h;

                if (gec.getEntityType() == EntityType.PLAYER) {
                    w = EntityFactory.PLAYER_WIDTH;
                    h = EntityFactory.PLAYER_HEIGHT;
                    drawPlayer(r, tf.getPosition(), w, h, color);
                } else {
                    w = tf.getScale().x;
                    h = tf.getScale().y;
                    drawFallingEntity(r, tf.getPosition(), w, h, gec.getEntityType(), color);
                }
            }
        }

        private void drawPlayer(Renderer r, Vector2 pos, float w, float h, EngineColor color) {
            if (scene.isBonusLifeShieldActive()) {
                float shieldPulse = 1.0f + 0.05f * (float) Math.sin(scene.getHudAnimTime() * 4.5f);
                Vector2 shieldCenter = new Vector2(pos.x, pos.y + h / 2f);
                r.drawCircle(shieldCenter, (w * 0.62f) * shieldPulse,
                    new EngineColor(0.16f, 0.82f, 1.0f, 0.12f), true);
                r.drawCircle(shieldCenter, (w * 0.74f) * shieldPulse,
                    new EngineColor(0.55f, 0.90f, 1.0f, 0.18f), false);
                r.drawCircle(shieldCenter, (w * 0.79f) * shieldPulse,
                    new EngineColor(0.80f, 0.96f, 1.0f, 0.90f), false);
                r.drawCircle(shieldCenter, (w * 0.86f) * shieldPulse,
                    new EngineColor(0.22f, 0.70f, 1.0f, 0.40f), false);
            }
            r.drawSprite(scene.getCharacterType().getSprite(),
                new Vector2(pos.x, pos.y + h / 2f), w, h);
        }

        private void drawFallingEntity(Renderer r, Vector2 pos, float w, float h,
                                       EntityType type, EngineColor color) {
            switch (type) {
                case GOOD_BYTE:       r.drawSprite(GOOD_BYTE_SPRITE,          pos, w, h); break;
                case SAFE_EMAIL:      r.drawSprite(SAFE_EMAIL_SPRITE,         pos, w, h); break;
                case GOLD_ENVELOPE:   r.drawSprite(GOLD_ENVELOPE_SPRITE,      pos, w, h); break;
                case PHISHING_HOOK:   r.drawSprite(PHISHING_HOOK_SPRITE,      pos, w, h); break;
                case RANSOMWARE_LOCK: r.drawSprite(RANSOMWARE_LOCK_SPRITE,    pos, w, h); break;
                case MALWARE_SWARM:   r.drawSprite(MALWARE_SWARM_SPRITE,      pos, w, h); break;
                case ROOTKIT:         r.drawSprite(ROOTKIT_SPRITE,            pos, w, h); break;
                case SPYWARE:         r.drawSprite(SPYWARE_SPRITE,            pos, w, h); break;
                case FRENZY_ORB: {
                    boolean orbFlashOn = ((int) (scene.getHudAnimTime() * 7f) % 2) == 0;
                    EngineColor orbAura = orbFlashOn
                        ? new EngineColor(1f, 0.10f, 0.18f, 0.24f)
                        : new EngineColor(1f, 0.42f, 0.64f, 0.16f);
                    EngineColor orbRing = orbFlashOn
                        ? new EngineColor(1f, 0.20f, 0.22f, 1f)
                        : new EngineColor(1f, 0.78f, 0.86f, 1f);
                    r.drawCircle(new Vector2(pos.x, pos.y), w / 2.05f, orbAura, true);
                    r.drawSprite(FRENZY_ORB_SPRITE, pos, w, h);
                    if (orbFlashOn) {
                        r.drawCircle(new Vector2(pos.x, pos.y), w / 4.2f,
                            new EngineColor(1f, 0.16f, 0.22f, 0.22f), true);
                    }
                    r.drawCircle(new Vector2(pos.x, pos.y), w / 2f, orbRing, false);
                    break;
                }
                default: {
                    float x = pos.x - w / 2, y = pos.y - h / 2;
                    r.drawRect(new Rectangle(x, y, w, h), color, true);
                    r.drawRect(new Rectangle(x, y, w, h), EngineColor.WHITE, false);
                }
            }
        }

        // ── HUD ─────────────────────────────────────────────────────────────

        private void drawHUD(Renderer r) {
            float ww = r.getWorldWidth();
            float wh = r.getWorldHeight();

            // HUD bar background
            r.drawRect(new Rectangle(0, wh - 56f, ww, 56f), COL_HUD_BG, true);

            // Lives (hearts)
            int lives = scene.getPlayerHealth().getCurrentLives();
            int maxL  = scene.getPlayerHealth().getMaxLives();
            for (int i = 0; i < maxL; i++) {
                EngineColor c = (i < lives) ? COL_HEART : COL_HEART_EMPTY;
                float hx = 20f + i * 36f;
                float hy = wh - 40f;
                drawHeart(r, hx, hy, c);
            }

            // Score
            r.drawTextCentered("SCORE: " + scene.getScore(),
                new Vector2(ww / 2f, wh - 14f), GameUiTheme.FONT_BODY_LARGE, GameUiTheme.TEXT_PRIMARY);

            // Timer / mode display
            String progressText;
            EngineColor  progressColor;
            GameState displayState = (scene.getGameState() == GameState.QUIZ)
                ? scene.getPreQuizState()
                : (scene.getGameState() == GameState.QUIZ_FEEDBACK)
                    ? scene.getPostFeedbackState()
                    : (scene.getGameState() == GameState.BUFF_SELECT)
                        ? scene.getPreBuffState()
                        : scene.getGameState();
            int secsLeft;
            EngineColor baseProgressColor;
            if (displayState == GameState.FRENZY) {
                secsLeft = Math.max(0, (int) Math.ceil(scene.getFrenzyTimer()));
                progressText  = "FRENZY " + secsLeft + "s  PTS " + scene.getScore();
                baseProgressColor = COL_FRENZY_BANNER;
            } else {
                secsLeft = Math.max(0, (int) Math.ceil(scene.getRoundTimer()));
                progressText  = "TIME " + secsLeft + "s  PTS " + scene.getScore();
                baseProgressColor = EngineColor.CYAN;
            }
            boolean dangerFlash = secsLeft <= 5;
            boolean flashOn = ((int) (scene.getHudAnimTime() * 6f) % 2) == 0;
            progressColor = dangerFlash && flashOn ? COL_WARNING : baseProgressColor;

            float textWidth = r.measureTextWidth(progressText, GameUiTheme.FONT_BODY);
            float iconDiameter = 28f;
            float gap = 10f;
            float progressX = ww - textWidth - iconDiameter - gap - 24f;
            Vector2 iconCenter = new Vector2(progressX + iconDiameter / 2f, wh - 21f);
            EngineColor iconAccent = dangerFlash && flashOn ? COL_WARNING : baseProgressColor;
            EngineColor iconFrame = dangerFlash && flashOn
                ? new EngineColor(1f, 0.92f, 0.92f, 1f)
                : GameUiTheme.TEXT_PRIMARY;

            drawTimerIcon(r, iconCenter, iconFrame, iconAccent, dangerFlash);
            r.drawText(progressText, new Vector2(progressX + iconDiameter + gap, wh - 18f),
                GameUiTheme.FONT_BODY, progressColor);

            // Active buff indicators (bottom-right)
            if (scene.hasShield()) {
                r.drawText("[REVIVE READY]", new Vector2(ww - 230f, 12f),
                    GameUiTheme.FONT_BODY_SMALL, GameUiTheme.TEXT_INFO);
            }

            // Controls hint
            r.drawText("A/D Move  |  SPACE Jump  |  ESC Quit",
                new Vector2(20f, 18f), GameUiTheme.FONT_BODY_SMALL, GameUiTheme.TEXT_SUBTLE);
        }

        private void drawStatusBanner(Renderer r) {
            if (!scene.isStatusBannerVisible() || scene.getStatusBannerText().isEmpty()) {
                return;
            }

            float ww = r.getWorldWidth();
            float wh = r.getWorldHeight();
            Vector2 textPos = new Vector2(ww / 2f, wh / 2f + 90f);
            EngineColor shadow = new EngineColor(0.18f, 0.10f, 0.02f, 0.95f);
            EngineColor bannerColor = scene.getStatusBannerColor();

            r.drawTextCentered(scene.getStatusBannerText(), new Vector2(textPos.x + 2f, textPos.y - 2f),
                GameUiTheme.FONT_TITLE_SMALL, shadow);
            r.drawTextCentered(scene.getStatusBannerText(), textPos, GameUiTheme.FONT_TITLE_SMALL, bannerColor);
        }

        private void drawHeart(Renderer r, float x, float y, EngineColor c) {
            r.drawCircle(new Vector2(x + 6f,  y + 8f), 6f, c, true);
            r.drawCircle(new Vector2(x + 16f, y + 8f), 6f, c, true);
            r.drawRect(new Rectangle(x, y, 22f, 10f), c, true);
            // bottom triangle approximated by two lines
            r.drawLine(new Vector2(x, y), new Vector2(x + 11f, y - 8f), c, 3f);
            r.drawLine(new Vector2(x + 22f, y), new Vector2(x + 11f, y - 8f), c, 3f);
        }

        private void drawTimerIcon(Renderer r, Vector2 center, EngineColor frameColor, EngineColor accentColor, boolean warning) {
            float pulse = warning
                ? 13.0f + (float) Math.sin(scene.getHudAnimTime() * 10f) * 1.0f
                : 13.0f;
            EngineColor badgeColor = warning
                ? new EngineColor(0.32f, 0.05f, 0.08f, 0.97f)
                : COL_TIMER_ICON_BG;
            EngineColor ringColor = warning ? accentColor : COL_TIMER_ICON_RING;

            r.drawCircle(center, pulse, badgeColor, true);
            r.drawCircle(center, pulse + 1.4f, ringColor, false);

            float thickness = 1.7f;
            float halfBar = 6.0f;
            float sideInset = 4.8f;
            float topY = center.y + 7.0f;
            float upperMidY = center.y + 2.1f;
            float lowerMidY = center.y - 2.1f;
            float bottomY = center.y - 7.0f;

            r.drawLine(new Vector2(center.x - halfBar, topY), new Vector2(center.x + halfBar, topY),
                frameColor, thickness);
            r.drawLine(new Vector2(center.x - halfBar, bottomY), new Vector2(center.x + halfBar, bottomY),
                frameColor, thickness);
            r.drawLine(new Vector2(center.x - sideInset, topY - 0.8f), new Vector2(center.x, upperMidY),
                frameColor, thickness);
            r.drawLine(new Vector2(center.x + sideInset, topY - 0.8f), new Vector2(center.x, upperMidY),
                frameColor, thickness);
            r.drawLine(new Vector2(center.x - sideInset, bottomY + 0.8f), new Vector2(center.x, lowerMidY),
                frameColor, thickness);
            r.drawLine(new Vector2(center.x + sideInset, bottomY + 0.8f), new Vector2(center.x, lowerMidY),
                frameColor, thickness);
            r.drawLine(new Vector2(center.x, upperMidY), new Vector2(center.x, lowerMidY),
                frameColor, 1.0f);

            r.drawLine(new Vector2(center.x - 2.6f, center.y + 3.3f), new Vector2(center.x + 2.6f, center.y + 3.3f),
                accentColor, 1.7f);
            r.drawLine(new Vector2(center.x - 1.9f, center.y + 1.8f), new Vector2(center.x + 1.9f, center.y + 1.8f),
                accentColor, 1.5f);
            r.drawLine(new Vector2(center.x, center.y + 0.2f), new Vector2(center.x, center.y - 2.8f),
                accentColor, 0.9f);
            r.drawCircle(new Vector2(center.x, center.y - 4.3f), 1.8f, accentColor, true);
        }

        // ── Quiz overlay ─────────────────────────────────────────────────────

        private void drawQuizOverlay(Renderer r) {
            float ww = r.getWorldWidth();
            float wh = r.getWorldHeight();

            // Dim background
            r.drawRect(new Rectangle(0, 0, ww, wh), COL_OVERLAY, true);

            // Quiz card
            float cw = 680f, ch = 360f;
            float cx = (ww - cw) / 2f, cy = (wh - ch) / 2f;
            r.drawRect(new Rectangle(cx, cy, cw, ch), COL_QUIZ_BG, true);
            r.drawRect(new Rectangle(cx, cy, cw, ch), EngineColor.CYAN, false);

            QuizManager qm = scene.getQuizManager();
            String[] opts = qm.getCurrentQuestion().getOptions();
            String   q    = qm.getCurrentQuestion().getQuestion();

            // Header
            String header = qm.isBadEntityQuiz()
                ? "THREAT IDENTIFIED! Answer correctly to neutralise (+100 pts):"
                : "RARE FIND! Answer correctly for +1 Life & +100 pts:";
            EngineColor headerCol = qm.isBadEntityQuiz()
                ? new EngineColor(1f, 0.4f, 0.4f, 1f) : new EngineColor(1f, 0.85f, 0.2f, 1f);
            r.drawText(header, new Vector2(cx + 20f, cy + ch - 28f), GameUiTheme.FONT_BODY, headerCol);

            // Question text (simple word-wrap at ~60 chars)
            drawWrappedText(r, q, cx + 20f, cy + ch - 70f, 60, EngineColor.WHITE);

            // Options
            String[] labels = {"1", "2", "3", "4"};
            for (int i = 0; i < 4; i++) {
                float oy = cy + ch - 160f - i * 46f;
                boolean hov = (i == scene.getHoveredQuizOption());
                EngineColor bg     = hov ? new EngineColor(0.15f, 0.30f, 0.60f, 0.95f)
                                   : new EngineColor(0.1f,  0.15f, 0.30f, 0.85f);
                EngineColor border = hov ? EngineColor.YELLOW : new EngineColor(0.4f, 0.6f, 0.9f, 0.7f);
                r.drawRect(new Rectangle(cx + 20f, oy, cw - 40f, 38f), bg, true);
                r.drawRect(new Rectangle(cx + 20f, oy, cw - 40f, 38f), border, false);
                r.drawText("[" + labels[i] + "]  " + opts[i],
                    new Vector2(cx + 32f, oy + 27f), GameUiTheme.FONT_BODY, hov ? GameUiTheme.TEXT_HIGHLIGHT : GameUiTheme.TEXT_PRIMARY);
            }

            r.drawText("Press 1 - 4  or  Click to answer",
                new Vector2(cx + 20f, cy + 22f), GameUiTheme.FONT_BODY_SMALL, GameUiTheme.TEXT_SUBTLE);
        }

        private void drawWrappedText(Renderer r, String text, float x, float startY,
                                     int lineLen, EngineColor color) {
            String[] words = text.split(" ");
            StringBuilder line = new StringBuilder();
            float y = startY;
            float lineStep = r.getLineHeight(GameUiTheme.FONT_BODY) + 4f;
            for (String word : words) {
                if (line.length() + word.length() + 1 > lineLen && line.length() > 0) {
                    r.drawText(line.toString(), new Vector2(x, y), GameUiTheme.FONT_BODY, color);
                    y -= lineStep;
                    line = new StringBuilder();
                }
                if (line.length() > 0) line.append(" ");
                line.append(word);
            }
            if (line.length() > 0) {
                r.drawText(line.toString(), new Vector2(x, y), GameUiTheme.FONT_BODY, color);
            }
        }

        // ── Quiz feedback banner ──────────────────────────────────────────────

        private void drawQuizFeedback(Renderer r) {
            float ww = r.getWorldWidth();
            float wh = r.getWorldHeight();

            boolean correct = (scene.getLastQuizResult() == QuizResult.CORRECT);
            boolean wasBad  = scene.isLastQuizBad();

            // Dim overlay
            r.drawRect(new Rectangle(0, 0, ww, wh), new EngineColor(0f, 0f, 0f, 0.55f), true);

            // Banner card
            float cw = 580f, ch = 160f;
            float cx = (ww - cw) / 2f, cy = (wh - ch) / 2f + 30f;
            EngineColor bgColor = correct
                ? new EngineColor(0.05f, 0.25f, 0.08f, 0.95f)
                : new EngineColor(0.25f, 0.05f, 0.05f, 0.95f);
            EngineColor borderColor = correct ? new EngineColor(0.2f, 0.9f, 0.3f, 1f) : new EngineColor(0.9f, 0.2f, 0.2f, 1f);
            r.drawRect(new Rectangle(cx, cy, cw, ch), bgColor, true);
            r.drawRect(new Rectangle(cx, cy, cw, ch), borderColor, false);

            // Result heading
            String heading = correct ? "CORRECT!" : "WRONG!";
            r.drawTextCentered(heading,
                new Vector2(ww / 2f, cy + ch - 34f),
                GameUiTheme.FONT_TITLE_SMALL, borderColor);

            // Detail line
            String detail;
            if (correct) {
                int bonus = QUIZ_BONUS_POINTS;
                detail = wasBad
                    ? "Threat neutralised! +" + bonus + " pts"
                    : "+" + bonus + " pts & +1 Life!";
            } else {
                detail = wasBad ? "-1 Life - stay alert!" : "No bonus this time.";
            }
            r.drawTextCentered(detail,
                new Vector2(ww / 2f, cy + ch - 76f),
                GameUiTheme.FONT_BODY_LARGE, GameUiTheme.TEXT_PRIMARY);

            // Progress bar (shrinks as timer counts down)
            float barW = cw - 40f;
            float progress = Math.min(1f, scene.getFeedbackTimer() / 1.5f);
            r.drawRect(new Rectangle(cx + 20f, cy + 18f, barW, 10f),
                new EngineColor(0.2f, 0.2f, 0.2f, 1f), true);
            r.drawRect(new Rectangle(cx + 20f, cy + 18f, barW * progress, 10f),
                borderColor, true);

            r.drawTextCentered("SPACE / ENTER to continue",
                new Vector2(ww / 2f, cy + 42f),
                GameUiTheme.FONT_BODY_SMALL, GameUiTheme.TEXT_SUBTLE);
        }

        // ── Frenzy transition banner ─────────────────────────────────────────

        private void drawFrenzyTransition(Renderer r) {
            float ww = r.getWorldWidth();
            float wh = r.getWorldHeight();
            r.drawRect(new Rectangle(0, 0, ww, wh), new EngineColor(0f, 0f, 0f, 0.6f), true);
            r.drawTextCentered("CYBER-HYDRA AWAKENS!", new Vector2(ww / 2f, wh / 2f + 42f),
                GameUiTheme.FONT_TITLE_SMALL, COL_FRENZY_BANNER);
            r.drawTextCentered("FRENZY MODE INCOMING...",
                new Vector2(ww / 2f, wh / 2f), GameUiTheme.FONT_BODY_LARGE, GameUiTheme.TEXT_HIGHLIGHT);
            int secs = (int) Math.ceil(scene.getTransitionTimer());
            r.drawTextCentered("Starting in " + secs + "...",
                new Vector2(ww / 2f, wh / 2f - 50f), GameUiTheme.FONT_BODY_LARGE, GameUiTheme.TEXT_PRIMARY);
        }

        // ── Buff card selection overlay ──────────────────────────────────────

        private void drawBuffSelect(Renderer r) {
            float ww = r.getWorldWidth(), wh = r.getWorldHeight();

            // Dim the game world behind the overlay
            r.drawRect(new Rectangle(0, 0, ww, wh), new EngineColor(0f, 0f, 0f, 0.72f), true);

            // Title — cardH=389, so card top at wh/2+194; give 20px gap above
            r.drawTextCentered("SYSTEM UPGRADE!",
                new Vector2(ww / 2f, wh / 2f + 242f), GameUiTheme.FONT_TITLE_SMALL,
                GameUiTheme.TEXT_SUCCESS);
            r.drawTextCentered("Choose a buff:",
                new Vector2(ww / 2f, wh / 2f + 210f), GameUiTheme.FONT_BODY_LARGE,
                GameUiTheme.TEXT_MUTED);

            for (int i = 0; i < 3; i++) {
                BuffType  buff = scene.getBuffChoice(i);
                Rectangle card = scene.buffCardRect(i, ww, wh);
                boolean   sel  = (i == scene.getBuffHoveredIdx());

                // Sprite fills the entire card at its natural 832x1295 proportions
                r.drawSprite(buff.getCardSprite(),
                    new Vector2(card.x + card.width / 2f, card.y + card.height / 2f),
                    card.width, card.height);

                // Border — full colour when selected, half-brightness when not
                EngineColor accent = buff.getAccentColor();
                float bri = sel ? 1.0f : 0.45f;
                r.drawRect(card,
                    new EngineColor(accent.r * bri, accent.g * bri, accent.b * bri, 1f), false);
                if (sel) {
                    r.drawRect(new Rectangle(card.x + 2f, card.y + 2f,
                        card.width - 4f, card.height - 4f), accent, false);
                }
            }

            // Footer hint
            r.drawTextCentered("< > / A D to navigate   1 2 3 or Enter to pick",
                new Vector2(ww / 2f, wh / 2f - 220f), GameUiTheme.FONT_BODY_SMALL,
                GameUiTheme.TEXT_SUBTLE);
        }

        // ── Game-over overlay ────────────────────────────────────────────────

        private void drawGameOverOverlay(Renderer r) {
            float ww = r.getWorldWidth();
            float wh = r.getWorldHeight();
            r.drawRect(new Rectangle(0, 0, ww, wh), new EngineColor(0f, 0f, 0f, 0.75f), true);
            r.drawTextCentered("SYSTEM CRASH!", new Vector2(ww / 2f, wh / 2f + 60f),
                GameUiTheme.FONT_TITLE_SMALL, COL_LOSE);
            r.drawTextCentered("The network is down. Score: " + scene.getScore(),
                new Vector2(ww / 2f, wh / 2f + 10f), GameUiTheme.FONT_BODY_LARGE, GameUiTheme.TEXT_PRIMARY);
            r.drawTextCentered("Press ENTER to continue",
                new Vector2(ww / 2f, wh / 2f - 50f), GameUiTheme.FONT_BODY_SMALL,
                GameUiTheme.TEXT_MUTED);
        }

        // ── Win overlay ──────────────────────────────────────────────────────

        private void drawWinOverlay(Renderer r) {
            float ww = r.getWorldWidth();
            float wh = r.getWorldHeight();
            r.drawRect(new Rectangle(0, 0, ww, wh), new EngineColor(0f, 0f, 0f, 0.72f), true);
            r.drawTextCentered("NETWORK SECURED!", new Vector2(ww / 2f, wh / 2f + 60f),
                GameUiTheme.FONT_TITLE_SMALL, COL_WIN);
            r.drawTextCentered("Cyber-Hydra defeated! Final score: " + scene.getScore(),
                new Vector2(ww / 2f, wh / 2f + 10f), GameUiTheme.FONT_BODY_LARGE, GameUiTheme.TEXT_PRIMARY);
            r.drawTextCentered("Press ENTER for leaderboard",
                new Vector2(ww / 2f, wh / 2f - 50f), GameUiTheme.FONT_BODY_SMALL,
                GameUiTheme.TEXT_MUTED);
        }
    }
