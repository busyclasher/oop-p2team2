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

    private GameAudio() {
    }

    public static void loadGameSounds(Audio audio) {
        if (audio == null) {
            return;
        }
        audio.loadSound(UI_CLICK_SOUND_PATH, UI_CLICK_SOUND_ID);
        audio.loadSound(JUMP_SOUND_PATH, JUMP_SOUND_ID);
    }

    public static void playUiClick(EngineContext context) {
        play(context, UI_CLICK_SOUND_ID, 0.9f);
    }

    public static void playJump(EngineContext context) {
        play(context, JUMP_SOUND_ID, 0.85f);
    }

    private static void play(EngineContext context, String soundId, float volume) {
        if (context == null || context.getOutputManager() == null || context.getOutputManager().getAudio() == null) {
            return;
        }
        context.getOutputManager().getAudio().playSound(soundId, volume);
    }
}
