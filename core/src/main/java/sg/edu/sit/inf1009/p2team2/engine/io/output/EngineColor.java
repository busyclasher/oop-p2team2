package sg.edu.sit.inf1009.p2team2.engine.io.output;

import com.badlogic.gdx.graphics.Color;

/**
 * Engine-owned colour value used to keep framework-specific colour types
 * out of the game layer.
 */
public class EngineColor {
    public static final EngineColor WHITE = new EngineColor(1f, 1f, 1f, 1f);
    public static final EngineColor BLACK = new EngineColor(0f, 0f, 0f, 1f);
    public static final EngineColor RED = new EngineColor(1f, 0f, 0f, 1f);
    public static final EngineColor GREEN = new EngineColor(0f, 1f, 0f, 1f);
    public static final EngineColor BLUE = new EngineColor(0f, 0f, 1f, 1f);
    public static final EngineColor YELLOW = new EngineColor(1f, 1f, 0f, 1f);
    public static final EngineColor GOLD = new EngineColor(1f, 0.84f, 0f, 1f);
    public static final EngineColor CYAN = new EngineColor(0f, 1f, 1f, 1f);
    public static final EngineColor GRAY = new EngineColor(0.5f, 0.5f, 0.5f, 1f);
    public static final EngineColor LIGHT_GRAY = new EngineColor(0.75f, 0.75f, 0.75f, 1f);
    public static final EngineColor DARK_GRAY = new EngineColor(0.25f, 0.25f, 0.25f, 1f);
    public static final EngineColor ORANGE = new EngineColor(1f, 0.65f, 0f, 1f);
    public static final EngineColor PURPLE = new EngineColor(0.5f, 0f, 0.5f, 1f);
    public static final EngineColor MAGENTA = new EngineColor(1f, 0f, 1f, 1f);
    public static final EngineColor CLEAR = new EngineColor(0f, 0f, 0f, 0f);

    public float r;
    public float g;
    public float b;
    public float a;

    public EngineColor() {
        this(0f, 0f, 0f, 1f);
    }

    public EngineColor(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public EngineColor(EngineColor other) {
        this(other.r, other.g, other.b, other.a);
    }

    public Color toGdxColor() {
        return new Color(r, g, b, a);
    }

    public static EngineColor fromGdx(Color color) {
        return new EngineColor(color.r, color.g, color.b, color.a);
    }

    public EngineColor cpy() {
        return new EngineColor(this);
    }
}
