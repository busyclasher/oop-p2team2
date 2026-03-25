package sg.edu.sit.inf1009.p2team2.game.audio;

import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.io.output.Audio;

/**
 * Shared game-layer sound identifiers and playback helpers.
 */
public final class GameAudio {
    public static final String UI_CLICK_SOUND_ID = "ui-click";
    public static final String UI_CLICK_SOUND_PATH = "audio/ui-click.wav";
    public static final String JUMP_SOUND_ID = "jump";
    public static final String JUMP_SOUND_PATH = "audio/jump.wav";
    public static final String GAME_OVER_SOUND_ID = "game-over";
    public static final String GAME_OVER_SOUND_PATH = "audio/game-over.wav";
    public static final String GAME_COMPLETE_SOUND_ID = "game-complete";
    public static final String GAME_COMPLETE_SOUND_PATH = "audio/game-complete.wav";

    private GameAudio() {
    }

    public static void loadGameSounds(Audio audio) {
        if (audio == null) {
            return;
        }
        audio.loadSound(UI_CLICK_SOUND_PATH, UI_CLICK_SOUND_ID);
        audio.loadSound(JUMP_SOUND_PATH, JUMP_SOUND_ID);
        audio.loadSound(GAME_OVER_SOUND_PATH, GAME_OVER_SOUND_ID);
        audio.loadSound(GAME_COMPLETE_SOUND_PATH, GAME_COMPLETE_SOUND_ID);
    }

    public static void playUiClick(EngineContext context) {
        play(context, UI_CLICK_SOUND_ID, 0.9f);
    }

    public static void playJump(EngineContext context) {
        // Keep jump prominent enough to cut through the background music mix.
        play(context, JUMP_SOUND_ID, 1.0f);
    }

    public static void playGameOver(EngineContext context) {
        play(context, GAME_OVER_SOUND_ID, 1.0f);
    }

    public static void playGameComplete(EngineContext context) {
        play(context, GAME_COMPLETE_SOUND_ID, 1.0f);
    }

    private static void play(EngineContext context, String soundId, float volume) {
        if (context == null || context.getOutputManager() == null || context.getOutputManager().getAudio() == null) {
            return;
        }
        context.getOutputManager().getAudio().playSound(soundId, volume);
    }
}
