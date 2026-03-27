package sg.edu.sit.inf1009.p2team2.game.scenes;

import com.badlogic.gdx.math.Vector2;
import sg.edu.sit.inf1009.p2team2.engine.io.input.Keyboard;
import sg.edu.sit.inf1009.p2team2.engine.io.input.Keys;
import sg.edu.sit.inf1009.p2team2.engine.io.input.Mouse;
import sg.edu.sit.inf1009.p2team2.engine.io.output.Renderer;
import sg.edu.sit.inf1009.p2team2.engine.scene.InputHandler;
import sg.edu.sit.inf1009.p2team2.game.audio.GameAudio;
import sg.edu.sit.inf1009.p2team2.game.scenes.GamePlayScene.GameState;

final class GamePlayInputHandler extends InputHandler {
        private final GamePlayScene scene;

        GamePlayInputHandler(GamePlayScene scene) {
            super(scene.getContext());
            this.scene = scene;
        }

        @Override
        public void handleInput() {
            Keyboard kb = scene.getContext().getInputManager().getKeyboard();

            switch (scene.getGameState()) {
                case PLAYING:
                case FRENZY:
                    if (kb.isKeyPressed(Keys.ESCAPE)) {
                        scene.openPauseMenu();
                    }
                    break;

                case QUIZ:
                    handleQuizInput(kb);
                    break;

                case QUIZ_FEEDBACK:
                    if (kb.isKeyPressed(Keys.ENTER) || kb.isKeyPressed(Keys.SPACE)) {
                        GameAudio.playUiClick(scene.getContext());
                        scene.clearQuizFeedbackTimer();
                        if (scene.getPostFeedbackState() == GameState.GAME_OVER) {
                            scene.goToGameOver();
                        } else {
                            scene.setGameState(scene.getPostFeedbackState());
                        }
                    }
                    break;

                case BUFF_SELECT:
                    handleBuffInput(kb);
                    break;

                case GAME_OVER:
                    if (kb.isKeyPressed(Keys.ENTER) || kb.isKeyPressed(Keys.SPACE)) {
                        GameAudio.playUiClick(scene.getContext());
                        scene.goToGameOver();
                    }
                    break;

                case WIN:
                    if (kb.isKeyPressed(Keys.ENTER) || kb.isKeyPressed(Keys.SPACE)) {
                        GameAudio.playUiClick(scene.getContext());
                        scene.goToLeaderboard();
                    }
                    break;

                default:
                    break;
            }
        }

        private void handleQuizInput(Keyboard kb) {
            // Keyboard
            if (kb.isKeyPressed(Keys.NUM_1)) { scene.submitQuizAnswer(0); return; }
            if (kb.isKeyPressed(Keys.NUM_2)) { scene.submitQuizAnswer(1); return; }
            if (kb.isKeyPressed(Keys.NUM_3)) { scene.submitQuizAnswer(2); return; }
            if (kb.isKeyPressed(Keys.NUM_4)) { scene.submitQuizAnswer(3); return; }

            // Mouse
            Mouse  mouse = scene.getContext().getInputManager().getMouse();
            Renderer r   = scene.getContext().getOutputManager().getRenderer();
            float ww = r.getWorldWidth(), wh = r.getWorldHeight();
            float cw = 680f, ch = 360f;
            float cx = (ww - cw) / 2f, cy = (wh - ch) / 2f;
            com.badlogic.gdx.math.Vector2 mp = mouse.getPosition();

            scene.setHoveredQuizOption(-1);
            for (int i = 0; i < 4; i++) {
                float oy = cy + ch - 160f - i * 46f;
                com.badlogic.gdx.math.Rectangle box =
                    new com.badlogic.gdx.math.Rectangle(cx + 20f, oy, cw - 40f, 38f);
                if (box.contains(mp.x, mp.y)) {
                    scene.setHoveredQuizOption(i);
                    if (mouse.isButtonPressed(0)) scene.submitQuizAnswer(i);
                    break;
                }
            }
        }

        private void handleBuffInput(Keyboard kb) {
            // Keyboard left/right to navigate cards
            if (kb.isKeyPressed(Keys.LEFT) || kb.isKeyPressed(Keys.A)) {
                scene.setBuffHoveredIdx((scene.getBuffHoveredIdx() - 1 + 3) % 3);
            } else if (kb.isKeyPressed(Keys.RIGHT) || kb.isKeyPressed(Keys.D)) {
                scene.setBuffHoveredIdx((scene.getBuffHoveredIdx() + 1) % 3);
            }
            // 1/2/3 hotkeys
            if (kb.isKeyPressed(Keys.NUM_1)) { scene.applyBuff(scene.getBuffChoice(0)); return; }
            if (kb.isKeyPressed(Keys.NUM_2)) { scene.applyBuff(scene.getBuffChoice(1)); return; }
            if (kb.isKeyPressed(Keys.NUM_3)) { scene.applyBuff(scene.getBuffChoice(2)); return; }
            // Enter/Space confirms hovered card
            if (kb.isKeyPressed(Keys.ENTER) || kb.isKeyPressed(Keys.SPACE)) {
                scene.applyBuff(scene.getBuffChoice(scene.getBuffHoveredIdx()));
                return;
            }
            // Mouse hover + click
            Mouse    mouse = scene.getContext().getInputManager().getMouse();
            Renderer r     = scene.getContext().getOutputManager().getRenderer();
            Vector2  mp    = mouse.getPosition();
            float ww = r.getWorldWidth(), wh = r.getWorldHeight();
            for (int i = 0; i < 3; i++) {
                if (scene.buffCardRect(i, ww, wh).contains(mp.x, mp.y)) {
                    scene.setBuffHoveredIdx(i);
                    if (mouse.isButtonPressed(0)) {
                        scene.applyBuff(scene.getBuffChoice(i));
                        return;
                    }
                }
            }
        }
    }
