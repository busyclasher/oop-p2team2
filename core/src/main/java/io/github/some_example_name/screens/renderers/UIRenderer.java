package io.github.some_example_name.screens.renderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import io.github.some_example_name.GameSession;
import io.github.some_example_name.GameSession.GameState;
import io.github.some_example_name.model.SecureAccount;
import io.github.some_example_name.model.SecurityComponent;
import io.github.some_example_name.model.SecurityComponentType;
import io.github.some_example_name.screens.animation.AnimationManager;
import io.github.some_example_name.systems.CybersecurityFact;
import io.github.some_example_name.systems.DifficultyLevel;
import io.github.some_example_name.theme.GameTheme;

/**
 * Renders all UI elements including text, components, facts, and threats.
 * Separates UI rendering from game logic and shape rendering.
 * Demonstrates the Single Responsibility Principle.
 */
public class UIRenderer {
    
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final BitmapFont titleFont;
    private final BitmapFont smallFont;
    private final GlyphLayout layout;
    
    // Screen dimensions
    private static final float WORLD_WIDTH = 1280;
    private static final float WORLD_HEIGHT = 720;
    private static final float PADDING = 20;
    private static final float HEADER_HEIGHT = 80;
    private static final float COMPONENT_PANEL_WIDTH = 300;
    
    public UIRenderer(SpriteBatch batch, BitmapFont font, BitmapFont titleFont, BitmapFont smallFont) {
        this.batch = batch;
        this.font = font;
        this.titleFont = titleFont;
        this.smallFont = smallFont;
        this.layout = new GlyphLayout();
    }
    
    /**
     * Render all UI elements.
     * 
     * @param session Game session
     * @param animManager Animation manager
     */
    public void render(GameSession session, AnimationManager animManager) {
        batch.begin();
        
        drawHeader(session, animManager);
        drawScore(session, animManager);
        drawComponents(session, animManager);
        drawFact(session, animManager);
        drawInstructions();
        drawGameState(session);
        
        batch.end();
    }
    
    /**
     * Draw the header (title, difficulty, level, time).
     */
    private void drawHeader(GameSession session, AnimationManager animManager) {
        // Title with neon cyan color
        titleFont.setColor(GameTheme.NEON_CYAN);
        titleFont.draw(batch, "SECURE ACCOUNT BUILDER", PADDING, WORLD_HEIGHT - 25);
        
        // Difficulty badge
        DifficultyLevel diff = session.getDifficulty();
        float[] diffColor = diff.getColor();
        font.setColor(diffColor[0], diffColor[1], diffColor[2], 1f);
        font.draw(batch, diff.getDisplayName().toUpperCase(), 420, WORLD_HEIGHT - 30);
        
        // Level badge
        font.setColor(GameTheme.NEON_PURPLE);
        String levelText = "LVL " + session.getCurrentLevel();
        font.draw(batch, levelText, 540, WORLD_HEIGHT - 30);
        
        // Time with subtle pulse
        float timePulse = 0.8f + 0.2f * MathUtils.sin(animManager.getAnimTime() * 3);
        font.setColor(GameTheme.TEXT_COLOR.r * timePulse, GameTheme.TEXT_COLOR.g * timePulse, 
                     GameTheme.TEXT_COLOR.b * timePulse, 1f);
        font.draw(batch, session.getFormattedTime(), 640, WORLD_HEIGHT - 30);
    }
    
    /**
     * Draw the score and combo display.
     */
    private void drawScore(GameSession session, AnimationManager animManager) {
        float x = WORLD_WIDTH - 350;
        float y = WORLD_HEIGHT - 25;
        
        // Score with pop animation
        float scale = animManager.getScorePopScale();
        font.getData().setScale(1.5f * scale);
        font.setColor(GameTheme.GOLD_COLOR);
        font.draw(batch, "SCORE: " + session.getTotalScore(), x, y);
        font.getData().setScale(1.5f);
        
        // Combo with fire effect
        int combo = session.getComboMultiplier();
        if (combo > 1) {
            float comboPulse = 0.7f + 0.3f * MathUtils.sin(animManager.getAnimTime() * 10);
            Color comboColor = new Color(1f, 0.3f + comboPulse * 0.3f, 0.1f, 1f);
            font.setColor(comboColor);
            font.draw(batch, combo + "x COMBO!", x + 170, y + MathUtils.sin(animManager.getAnimTime() * 5) * 3);
        }
    }
    
    /**
     * Draw component slots and their details.
     */
    private void drawComponents(GameSession session, AnimationManager animManager) {
        float startX = PADDING + 20;
        float startY = WORLD_HEIGHT - HEADER_HEIGHT - 60;
        float slotHeight = 80;
        
        SecurityComponentType[] types = SecurityComponentType.values();
        SecureAccount account = session.getCurrentAccount();
        Color[] typeColors = GameTheme.getComponentColorArray();
        
        for (int i = 0; i < types.length; i++) {
            float y = startY - i * (slotHeight + 10);
            SecurityComponent comp = account.getComponent(types[i]);
            
            // Slot number with glow
            smallFont.setColor(typeColors[i].r, typeColors[i].g, typeColors[i].b, 0.8f);
            smallFont.draw(batch, "[" + (i + 1) + "]", startX, y);
            
            // Component name
            font.setColor(typeColors[i]);
            font.draw(batch, types[i].getDisplayName(), startX + 35, y);
            
            // Component value
            if (comp != null) {
                font.setColor(GameTheme.TEXT_COLOR);
                font.draw(batch, comp.getValue(), startX + 35, y - 25);
                
                // Score with icon
                smallFont.setColor(GameTheme.NEON_GREEN);
                smallFont.draw(batch, "+" + comp.getSecurityScore() + " pts", startX + 35, y - 50);
            } else {
                font.setColor(new Color(0.5f, 0.3f, 0.3f, 0.8f));
                font.draw(batch, "Not set", startX + 35, y - 25);
            }
        }
    }
    
    /**
     * Draw the cybersecurity fact panel.
     */
    private void drawFact(GameSession session, AnimationManager animManager) {
        CybersecurityFact fact = session.getCurrentFact();
        if (fact == null) return;
        
        float x = WORLD_WIDTH - COMPONENT_PANEL_WIDTH - PADDING + 15;
        float y = WORLD_HEIGHT - HEADER_HEIGHT - 60;
        float fadeAlpha = animManager.getFactFadeAlpha();
        
        // Category header with glow effect
        titleFont.setColor(GameTheme.NEON_PURPLE.r, GameTheme.NEON_PURPLE.g, GameTheme.NEON_PURPLE.b, fadeAlpha);
        titleFont.getData().setScale(1.5f);
        titleFont.draw(batch, "DID YOU KNOW?", x, y);
        titleFont.getData().setScale(2.5f);
        
        // Fact text with fade
        font.setColor(GameTheme.TEXT_COLOR.r, GameTheme.TEXT_COLOR.g, GameTheme.TEXT_COLOR.b, fadeAlpha);
        String factText = fact.getFact();
        drawWrappedText(font, factText, x, y - 40, COMPONENT_PANEL_WIDTH - 30);
        
        // Category badge
        Color categoryColor = GameTheme.getComponentColor(fact.getCategory());
        smallFont.setColor(categoryColor.r, categoryColor.g, categoryColor.b, 0.8f);
        smallFont.draw(batch, fact.getCategory().getDisplayName(), x, y - 150);
    }
    
    /**
     * Draw keyboard controls instructions.
     */
    private void drawInstructions() {
        float x = WORLD_WIDTH - COMPONENT_PANEL_WIDTH - PADDING + 15;
        float y = 200;
        
        smallFont.setColor(GameTheme.NEON_CYAN.r * 0.6f, GameTheme.NEON_CYAN.g * 0.6f, 
                          GameTheme.NEON_CYAN.b * 0.6f, 1f);
        smallFont.draw(batch, "CONTROLS:", x, y);
        
        smallFont.setColor(GameTheme.DIM_COLOR);
        smallFont.draw(batch, "[1-5] Cycle components", x, y - 25);
        smallFont.draw(batch, "[D] Change difficulty", x, y - 50);
        smallFont.draw(batch, "[Ctrl+Z] Undo", x, y - 75);
        smallFont.draw(batch, "[Ctrl+Y] Redo", x, y - 100);
        smallFont.draw(batch, "[Enter] Submit build", x, y - 125);
        smallFont.draw(batch, "[R] Restart", x, y - 150);
    }
    
    /**
     * Draw game state overlays (game over, level complete).
     */
    private void drawGameState(GameSession session) {
        GameState state = session.getState();
        
        if (state == GameState.GAME_OVER) {
            // Game over text
            titleFont.setColor(GameTheme.NEON_PINK);
            titleFont.getData().setScale(3f);
            layout.setText(titleFont, "GAME OVER");
            titleFont.draw(batch, "GAME OVER", (WORLD_WIDTH - layout.width) / 2, WORLD_HEIGHT / 2 + 50);
            titleFont.getData().setScale(2.5f);
            
            font.setColor(GameTheme.GOLD_COLOR);
            String scoreText = "Final Score: " + session.getTotalScore();
            layout.setText(font, scoreText);
            font.draw(batch, scoreText, (WORLD_WIDTH - layout.width) / 2, WORLD_HEIGHT / 2 - 20);
            
            smallFont.setColor(GameTheme.DIM_COLOR);
            String restartText = "Press [R] to restart";
            layout.setText(smallFont, restartText);
            smallFont.draw(batch, restartText, (WORLD_WIDTH - layout.width) / 2, WORLD_HEIGHT / 2 - 60);
        }
        
        if (state == GameState.LEVEL_COMPLETE) {
            // Level complete text
            titleFont.setColor(GameTheme.NEON_GREEN);
            titleFont.getData().setScale(3f);
            layout.setText(titleFont, "LEVEL COMPLETE!");
            titleFont.draw(batch, "LEVEL COMPLETE!", (WORLD_WIDTH - layout.width) / 2, WORLD_HEIGHT / 2 + 50);
            titleFont.getData().setScale(2.5f);
            
            smallFont.setColor(GameTheme.DIM_COLOR);
            String continueText = "Press [Enter] to continue";
            layout.setText(smallFont, continueText);
            smallFont.draw(batch, continueText, (WORLD_WIDTH - layout.width) / 2, WORLD_HEIGHT / 2 - 20);
        }
    }
    
    /**
     * Draw wrapped text within a maximum width.
     */
    private void drawWrappedText(BitmapFont font, String text, float x, float y, float maxWidth) {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        float lineY = y;
        
        for (String word : words) {
            String testLine = line.toString() + word + " ";
            layout.setText(font, testLine);
            
            if (layout.width > maxWidth && line.length() > 0) {
                font.draw(batch, line.toString(), x, lineY);
                line = new StringBuilder(word + " ");
                lineY -= 25;
            } else {
                line.append(word).append(" ");
            }
        }
        
        if (line.length() > 0) {
            font.draw(batch, line.toString(), x, lineY);
        }
    }
}
