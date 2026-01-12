package io.github.some_example_name.screens.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import io.github.some_example_name.GameSession;
import io.github.some_example_name.model.SecureAccount;
import io.github.some_example_name.model.SecurityComponentType;
import io.github.some_example_name.screens.animation.AnimationManager;
import io.github.some_example_name.systems.RiskMeter;
import io.github.some_example_name.systems.ThreatEvent;
import io.github.some_example_name.theme.GameTheme;

/**
 * Main renderer that coordinates all rendering operations.
 * Handles shape rendering (backgrounds, panels, bars, effects).
 * Delegates text/UI rendering to UIRenderer.
 * Demonstrates the Single Responsibility Principle and Delegation pattern.
 */
public class GameRenderer {
    
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final UIRenderer uiRenderer;
    private final BitmapFont font;
    private final BitmapFont titleFont;
    
    // Screen dimensions
    private static final float WORLD_WIDTH = 1280;
    private static final float WORLD_HEIGHT = 720;
    private static final float PADDING = 20;
    private static final float HEADER_HEIGHT = 80;
    private static final float COMPONENT_PANEL_WIDTH = 300;
    private static final float RISK_BAR_HEIGHT = 30;
    
    public GameRenderer(ShapeRenderer shapeRenderer, SpriteBatch batch, UIRenderer uiRenderer, 
                       BitmapFont font, BitmapFont titleFont) {
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.uiRenderer = uiRenderer;
        this.font = font;
        this.titleFont = titleFont;
    }
    
    /**
     * Render all visual elements.
     * 
     * @param session Game session
     * @param animManager Animation manager
     */
    public void render(GameSession session, AnimationManager animManager) {
        // Clear screen
        Gdx.gl.glClearColor(GameTheme.BG_COLOR.r, GameTheme.BG_COLOR.g, 
                           GameTheme.BG_COLOR.b, GameTheme.BG_COLOR.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Enable blending
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        // Draw shapes
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        drawBackground(animManager);
        drawPanels(animManager);
        drawRiskMeter(session, animManager);
        drawComponentSlots(session, animManager);
        drawSecurityScore(session);
        drawThreatEvent(session, animManager);
        
        // Draw UI (delegated)
        batch.setProjectionMatrix(batch.getProjectionMatrix());
        uiRenderer.render(session, animManager);
    }
    
    /**
     * Draw animated background with grid effect.
     */
    private void drawBackground(AnimationManager animManager) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Gradient background
        shapeRenderer.rect(0, 0, WORLD_WIDTH, WORLD_HEIGHT, 
            GameTheme.BG_COLOR, GameTheme.BG_COLOR, GameTheme.BG_GRADIENT_TOP, GameTheme.BG_GRADIENT_TOP);
        
        // Subtle animated grid lines
        shapeRenderer.setColor(GameTheme.GRID_COLOR);
        float gridSpacing = 50;
        float gridOffset = (animManager.getAnimTime() * 10) % gridSpacing;
        
        for (float x = gridOffset; x < WORLD_WIDTH; x += gridSpacing) {
            shapeRenderer.rectLine(x, 0, x, WORLD_HEIGHT, 1);
        }
        for (float y = gridOffset; y < WORLD_HEIGHT; y += gridSpacing) {
            shapeRenderer.rectLine(0, y, WORLD_WIDTH, y, 1);
        }
        
        shapeRenderer.end();
    }
    
    /**
     * Draw UI panels with borders and glows.
     */
    private void drawPanels(AnimationManager animManager) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Header panel with gradient
        shapeRenderer.rect(0, WORLD_HEIGHT - HEADER_HEIGHT, WORLD_WIDTH, HEADER_HEIGHT,
            new Color(0.1f, 0.12f, 0.2f, 0.95f), new Color(0.1f, 0.12f, 0.2f, 0.95f),
            new Color(0.15f, 0.18f, 0.28f, 0.95f), new Color(0.15f, 0.18f, 0.28f, 0.95f));
        
        // Left panel (components)
        drawRoundedPanel(PADDING, PADDING, COMPONENT_PANEL_WIDTH, 
                        WORLD_HEIGHT - HEADER_HEIGHT - PADDING * 2, GameTheme.NEON_CYAN);
        
        // Right panel (facts & info)
        float rightPanelX = WORLD_WIDTH - COMPONENT_PANEL_WIDTH - PADDING;
        drawRoundedPanel(rightPanelX, PADDING, COMPONENT_PANEL_WIDTH, 
                        WORLD_HEIGHT - HEADER_HEIGHT - PADDING * 2, GameTheme.NEON_PURPLE);
        
        shapeRenderer.end();
        
        // Draw panel borders
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        Gdx.gl.glLineWidth(2);
        
        // Header border with pulsing glow
        float glowIntensity = 0.5f + 0.2f * MathUtils.sin(animManager.getAnimTime() * 2);
        shapeRenderer.setColor(GameTheme.NEON_CYAN.r, GameTheme.NEON_CYAN.g, 
                              GameTheme.NEON_CYAN.b, glowIntensity);
        shapeRenderer.line(0, WORLD_HEIGHT - HEADER_HEIGHT, WORLD_WIDTH, WORLD_HEIGHT - HEADER_HEIGHT);
        
        // Left panel border
        shapeRenderer.setColor(GameTheme.NEON_CYAN.r, GameTheme.NEON_CYAN.g, GameTheme.NEON_CYAN.b, 0.6f);
        shapeRenderer.rect(PADDING, PADDING, COMPONENT_PANEL_WIDTH, 
                          WORLD_HEIGHT - HEADER_HEIGHT - PADDING * 2);
        
        // Right panel border
        shapeRenderer.setColor(GameTheme.NEON_PURPLE.r, GameTheme.NEON_PURPLE.g, GameTheme.NEON_PURPLE.b, 0.6f);
        shapeRenderer.rect(rightPanelX, PADDING, COMPONENT_PANEL_WIDTH, 
                          WORLD_HEIGHT - HEADER_HEIGHT - PADDING * 2);
        
        shapeRenderer.end();
        Gdx.gl.glLineWidth(1);
    }
    
    /**
     * Draw a rounded panel with accent color.
     */
    private void drawRoundedPanel(float x, float y, float width, float height, Color accentColor) {
        shapeRenderer.setColor(GameTheme.PANEL_COLOR);
        shapeRenderer.rect(x, y, width, height);
        
        // Top accent strip
        shapeRenderer.setColor(accentColor.r, accentColor.g, accentColor.b, 0.3f);
        shapeRenderer.rect(x, y + height - 4, width, 4);
    }
    
    /**
     * Draw the risk meter bar.
     */
    private void drawRiskMeter(GameSession session, AnimationManager animManager) {
        RiskMeter riskMeter = session.getRiskMeter();
        float riskPercent = riskMeter.getRiskPercentage();
        Color riskColor = riskMeter.getRiskColor();
        
        float barX = PADDING + COMPONENT_PANEL_WIDTH + 40;
        float barY = WORLD_HEIGHT - HEADER_HEIGHT - 220;
        float barWidth = WORLD_WIDTH - COMPONENT_PANEL_WIDTH * 2 - PADDING * 2 - 80;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Background
        shapeRenderer.rect(barX, barY, barWidth, RISK_BAR_HEIGHT,
            new Color(0.15f, 0.15f, 0.2f, 1f), new Color(0.15f, 0.15f, 0.2f, 1f),
            new Color(0.2f, 0.2f, 0.25f, 1f), new Color(0.2f, 0.2f, 0.25f, 1f));
        
        // Risk fill with pulsing effect when high
        float pulse = 1f;
        if (riskPercent > 0.7f) {
            pulse = 0.8f + 0.2f * MathUtils.sin(animManager.getAnimTime() * 8);
        }
        shapeRenderer.setColor(riskColor.r * pulse, riskColor.g * pulse, riskColor.b * pulse, 1f);
        shapeRenderer.rect(barX, barY, barWidth * riskPercent, RISK_BAR_HEIGHT);
        
        // Tick marks
        shapeRenderer.setColor(0.3f, 0.3f, 0.4f, 0.5f);
        for (int i = 1; i < 4; i++) {
            float tickX = barX + (barWidth * i / 4);
            shapeRenderer.rect(tickX - 1, barY, 2, RISK_BAR_HEIGHT);
        }
        
        shapeRenderer.end();
        
        // Border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(riskColor.r, riskColor.g, riskColor.b, 0.5f);
        shapeRenderer.rect(barX, barY, barWidth, RISK_BAR_HEIGHT);
        shapeRenderer.end();
        
        // Risk text
        batch.begin();
        font.setColor(riskColor);
        font.draw(batch, "RISK: " + riskMeter.getRiskLevel().getDisplayName() + 
                 " (" + riskMeter.getCurrentRisk() + "%)", barX, barY + RISK_BAR_HEIGHT + 25);
        batch.end();
    }
    
    /**
     * Draw component slots with glow effects.
     */
    private void drawComponentSlots(GameSession session, AnimationManager animManager) {
        float startX = PADDING + 15;
        float startY = WORLD_HEIGHT - HEADER_HEIGHT - 100;
        float slotHeight = 80;
        float slotWidth = COMPONENT_PANEL_WIDTH - 30;
        
        SecurityComponentType[] types = SecurityComponentType.values();
        SecureAccount account = session.getCurrentAccount();
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        for (int i = 0; i < types.length; i++) {
            float y = startY - i * (slotHeight + 10);
            boolean hasComponent = account.hasComponent(types[i]);
            
            // Slot background
            Color slotColor1, slotColor2;
            if (hasComponent) {
                slotColor1 = GameTheme.SLOT_FILLED_1;
                slotColor2 = GameTheme.SLOT_FILLED_2;
            } else {
                slotColor1 = GameTheme.SLOT_EMPTY_1;
                slotColor2 = GameTheme.SLOT_EMPTY_2;
            }
            shapeRenderer.rect(startX, y, slotWidth, slotHeight, slotColor1, slotColor1, slotColor2, slotColor2);
            
            // Glow effect when recently changed
            float glowTimer = animManager.getSlotGlowTimer(i);
            if (glowTimer > 0) {
                float glowAlpha = glowTimer / 0.5f * 0.5f;
                shapeRenderer.setColor(GameTheme.NEON_CYAN.r, GameTheme.NEON_CYAN.g, 
                                      GameTheme.NEON_CYAN.b, glowAlpha);
                shapeRenderer.rect(startX, y, slotWidth, slotHeight);
            }
        }
        
        shapeRenderer.end();
        
        // Slot borders
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (int i = 0; i < types.length; i++) {
            float y = startY - i * (slotHeight + 10);
            boolean hasComponent = account.hasComponent(types[i]);
            
            Color borderColor = hasComponent ? GameTheme.SLOT_BORDER_FILLED : GameTheme.SLOT_BORDER_EMPTY;
            if (animManager.getSlotGlowTimer(i) > 0) {
                borderColor = GameTheme.NEON_CYAN;
            }
            shapeRenderer.setColor(borderColor.r, borderColor.g, borderColor.b, 0.5f);
            shapeRenderer.rect(startX, y, slotWidth, slotHeight);
        }
        shapeRenderer.end();
    }
    
    /**
     * Draw the circular security score indicator.
     */
    private void drawSecurityScore(GameSession session) {
        float centerX = WORLD_WIDTH / 2;
        float centerY = WORLD_HEIGHT - HEADER_HEIGHT - 120;
        float radius = 60;
        
        int totalScore = session.getCurrentAccount().getTotalSecurityScore();
        int maxScore = 500;
        float percent = Math.min(1f, totalScore / (float) maxScore);
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Background circle
        shapeRenderer.setColor(0.15f, 0.15f, 0.2f, 1f);
        drawCircle(centerX, centerY, radius, 32);
        
        // Progress arc
        Color scoreColor = GameTheme.getScoreColor(percent);
        shapeRenderer.setColor(scoreColor);
        drawArc(centerX, centerY, radius - 5, 90, 360 * percent, 32);
        
        // Inner circle
        shapeRenderer.setColor(GameTheme.PANEL_COLOR);
        drawCircle(centerX, centerY, radius - 15, 32);
        
        shapeRenderer.end();
        
        // Score text
        batch.begin();
        font.setColor(scoreColor);
        String scoreText = String.valueOf(totalScore);
        font.draw(batch, scoreText, centerX - font.getSpaceXadvance() * scoreText.length() / 2, centerY + 10);
        batch.end();
    }
    
    /**
     * Draw threat event alert popup.
     */
    private void drawThreatEvent(GameSession session, AnimationManager animManager) {
        ThreatEvent threat = session.getActiveThreat();
        if (threat == null) return;
        
        float alertWidth = 500;
        float alertHeight = 150;
        float alertX = (WORLD_WIDTH - alertWidth) / 2;
        float alertY = (WORLD_HEIGHT - alertHeight) / 2;
        
        float pulse = 0.8f + 0.2f * MathUtils.sin(animManager.getThreatPulse());
        
        // Outer glow
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.8f * pulse, 0.1f, 0.1f, 0.3f);
        shapeRenderer.rect(alertX - 10, alertY - 10, alertWidth + 20, alertHeight + 20);
        
        // Background
        shapeRenderer.setColor(0.15f, 0.05f, 0.05f, 0.98f);
        shapeRenderer.rect(alertX, alertY, alertWidth, alertHeight);
        shapeRenderer.end();
        
        // Border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        Gdx.gl.glLineWidth(3);
        shapeRenderer.setColor(1f * pulse, 0.2f, 0.2f, 1f);
        shapeRenderer.rect(alertX, alertY, alertWidth, alertHeight);
        shapeRenderer.end();
        Gdx.gl.glLineWidth(1);
        
        // Warning triangle
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1f * pulse, 0.3f, 0.1f, 1f);
        float iconX = alertX + 30;
        float iconY = alertY + alertHeight - 50;
        shapeRenderer.triangle(iconX, iconY - 20, iconX + 20, iconY - 20, iconX + 10, iconY + 10);
        shapeRenderer.end();
        
        // Threat text
        batch.begin();
        titleFont.setColor(new Color(1f * pulse, 0.3f, 0.2f, 1f));
        titleFont.draw(batch, threat.getName(), alertX + 60, alertY + alertHeight - 20);
        
        font.setColor(GameTheme.TEXT_COLOR);
        String desc = threat.getDescription();
        if (desc.length() > 60) {
            desc = desc.substring(0, 57) + "...";
        }
        font.draw(batch, desc, alertX + 20, alertY + alertHeight - 70);
        batch.end();
        
        // Game over overlay if needed
        if (session.getState() == GameSession.GameState.GAME_OVER || 
            session.getState() == GameSession.GameState.LEVEL_COMPLETE) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 0.7f);
            shapeRenderer.rect(0, 0, WORLD_WIDTH, WORLD_HEIGHT);
            shapeRenderer.end();
        }
    }
    
    /**
     * Draw a filled circle using triangles.
     */
    private void drawCircle(float cx, float cy, float radius, int segments) {
        float angle = 0;
        float angleStep = 360f / segments;
        for (int i = 0; i < segments; i++) {
            float x1 = cx + radius * MathUtils.cosDeg(angle);
            float y1 = cy + radius * MathUtils.sinDeg(angle);
            float x2 = cx + radius * MathUtils.cosDeg(angle + angleStep);
            float y2 = cy + radius * MathUtils.sinDeg(angle + angleStep);
            shapeRenderer.triangle(cx, cy, x1, y1, x2, y2);
            angle += angleStep;
        }
    }
    
    /**
     * Draw a filled arc using triangles.
     */
    private void drawArc(float cx, float cy, float radius, float startAngle, float sweep, int segments) {
        if (sweep <= 0) return;
        float angle = startAngle;
        float angleStep = sweep / segments;
        for (int i = 0; i < segments; i++) {
            float x1 = cx + radius * MathUtils.cosDeg(angle);
            float y1 = cy + radius * MathUtils.sinDeg(angle);
            float x2 = cx + radius * MathUtils.cosDeg(angle + angleStep);
            float y2 = cy + radius * MathUtils.sinDeg(angle + angleStep);
            shapeRenderer.triangle(cx, cy, x1, y1, x2, y2);
            angle += angleStep;
        }
    }
}
