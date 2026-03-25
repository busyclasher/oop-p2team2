package sg.edu.sit.inf1009.p2team2.game.ui;

import com.badlogic.gdx.graphics.Color;

/**
 * Shared UI theme tokens for the CyberScouts game layer.
 * Keeps font keys and text colours consistent across scenes.
 */
public final class GameUiTheme {
    public static final String FONT_TITLE = "title";
    public static final String FONT_TITLE_SMALL = "title-small";
    public static final String FONT_BODY_LARGE = "body-large";
    public static final String FONT_BODY = "body";
    public static final String FONT_BODY_SMALL = "body-small";
    public static final String FONT_BODY_TINY = "body-tiny";

    public static final Color TITLE_PRIMARY = new Color(0.43f, 0.98f, 0.96f, 1f);
    public static final Color TITLE_SECONDARY = new Color(0.74f, 1.0f, 0.50f, 1f);
    public static final Color TEXT_PRIMARY = new Color(0.96f, 0.98f, 1.0f, 1f);
    public static final Color TEXT_MUTED = new Color(0.69f, 0.74f, 0.84f, 1f);
    public static final Color TEXT_SUBTLE = new Color(0.55f, 0.60f, 0.70f, 1f);
    public static final Color TEXT_HIGHLIGHT = new Color(1.0f, 0.89f, 0.40f, 1f);
    public static final Color TEXT_SUCCESS = new Color(0.45f, 1.0f, 0.65f, 1f);
    public static final Color TEXT_WARNING = new Color(1.0f, 0.68f, 0.25f, 1f);
    public static final Color TEXT_DANGER = new Color(1.0f, 0.42f, 0.48f, 1f);
    public static final Color TEXT_INFO = new Color(0.52f, 0.84f, 1.0f, 1f);

    private GameUiTheme() {
    }
}
