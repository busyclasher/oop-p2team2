package io.github.some_example_name.theme;

import com.badlogic.gdx.graphics.Color;
import io.github.some_example_name.model.SecurityComponentType;

import java.util.HashMap;
import java.util.Map;

/**
 * Centralized color management for the game's cyberpunk/neon theme.
 * Provides all color constants and utility methods for component-based coloring.
 * Demonstrates the Utility Class pattern for shared resources.
 */
public final class GameTheme {
    
    // Private constructor to prevent instantiation
    private GameTheme() {
        throw new AssertionError("Utility class should not be instantiated");
    }
    
    // ==================== Background Colors ====================
    public static final Color BG_COLOR = new Color(0.06f, 0.08f, 0.12f, 1f);
    public static final Color BG_GRADIENT_TOP = new Color(0.08f, 0.1f, 0.18f, 1f);
    public static final Color PANEL_COLOR = new Color(0.1f, 0.12f, 0.2f, 0.9f);
    public static final Color PANEL_BORDER = new Color(0.2f, 0.4f, 0.6f, 0.5f);
    public static final Color GRID_COLOR = new Color(0.1f, 0.15f, 0.25f, 0.3f);
    
    // ==================== Neon Accent Colors ====================
    public static final Color NEON_CYAN = new Color(0.0f, 0.9f, 1f, 1f);
    public static final Color NEON_PINK = new Color(1f, 0.2f, 0.6f, 1f);
    public static final Color NEON_GREEN = new Color(0.2f, 1f, 0.4f, 1f);
    public static final Color NEON_ORANGE = new Color(1f, 0.6f, 0.1f, 1f);
    public static final Color NEON_PURPLE = new Color(0.7f, 0.3f, 1f, 1f);
    
    // ==================== UI Colors ====================
    public static final Color GOLD_COLOR = new Color(1f, 0.85f, 0.3f, 1f);
    public static final Color TEXT_COLOR = new Color(0.95f, 0.95f, 1f, 1f);
    public static final Color DIM_COLOR = new Color(0.5f, 0.55f, 0.7f, 1f);
    
    // ==================== Slot Colors ====================
    public static final Color SLOT_FILLED_1 = new Color(0.1f, 0.25f, 0.2f, 1f);
    public static final Color SLOT_FILLED_2 = new Color(0.15f, 0.35f, 0.25f, 1f);
    public static final Color SLOT_EMPTY_1 = new Color(0.2f, 0.12f, 0.12f, 1f);
    public static final Color SLOT_EMPTY_2 = new Color(0.25f, 0.15f, 0.15f, 1f);
    public static final Color SLOT_BORDER_FILLED = NEON_GREEN;
    public static final Color SLOT_BORDER_EMPTY = new Color(0.5f, 0.2f, 0.2f, 0.5f);
    
    // ==================== Component Type Colors ====================
    private static final Map<SecurityComponentType, Color> COMPONENT_COLORS = new HashMap<>();
    
    static {
        COMPONENT_COLORS.put(SecurityComponentType.PASSWORD, NEON_CYAN);
        COMPONENT_COLORS.put(SecurityComponentType.TWO_FA, NEON_PINK);
        COMPONENT_COLORS.put(SecurityComponentType.UPDATES, NEON_ORANGE);
        COMPONENT_COLORS.put(SecurityComponentType.RECOVERY, NEON_GREEN);
        COMPONENT_COLORS.put(SecurityComponentType.PRIVACY, NEON_PURPLE);
    }
    
    /**
     * Get the neon color associated with a component type.
     * 
     * @param type Component type
     * @return Associated color
     */
    public static Color getComponentColor(SecurityComponentType type) {
        return COMPONENT_COLORS.getOrDefault(type, TEXT_COLOR);
    }
    
    /**
     * Get an array of all component colors in order.
     * Useful for iterating through component slots.
     * 
     * @return Array of colors [CYAN, PINK, ORANGE, GREEN, PURPLE]
     */
    public static Color[] getComponentColorArray() {
        return new Color[] {
            NEON_CYAN,
            NEON_PINK,
            NEON_ORANGE,
            NEON_GREEN,
            NEON_PURPLE
        };
    }
    
    /**
     * Calculate color based on security score percentage.
     * Returns gradient from red (low) to green (high).
     * 
     * @param percent Score as percentage (0.0 to 1.0)
     * @return Color corresponding to score
     */
    public static Color getScoreColor(float percent) {
        if (percent < 0.3f) return new Color(1f, 0.3f, 0.3f, 1f);
        if (percent < 0.6f) return NEON_ORANGE;
        if (percent < 0.8f) return new Color(0.8f, 1f, 0.3f, 1f);
        return NEON_GREEN;
    }
    
    /**
     * Create a pulsing color effect by modulating brightness.
     * 
     * @param baseColor Base color to pulse
     * @param pulseAmount Pulse intensity (0.0 to 1.0)
     * @return New color with pulsing effect
     */
    public static Color createPulseColor(Color baseColor, float pulseAmount) {
        return new Color(
            baseColor.r * pulseAmount,
            baseColor.g * pulseAmount,
            baseColor.b * pulseAmount,
            baseColor.a
        );
    }
    
    /**
     * Create a faded version of a color with custom alpha.
     * 
     * @param baseColor Base color
     * @param alpha Alpha value (0.0 to 1.0)
     * @return New color with modified alpha
     */
    public static Color createFadedColor(Color baseColor, float alpha) {
        return new Color(baseColor.r, baseColor.g, baseColor.b, alpha);
    }
}
