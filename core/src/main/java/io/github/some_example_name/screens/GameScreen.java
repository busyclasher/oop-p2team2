package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.some_example_name.GameSession;
import io.github.some_example_name.GameSession.GameState;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.*;
import io.github.some_example_name.systems.*;
import io.github.some_example_name.validation.*;
import io.github.some_example_name.observer.*;

import java.util.List;

/**
 * Main gameplay screen that renders the game UI with animations and visual polish.
 * Displays scores, risk meter, security components, and educational facts.
 */
public class GameScreen implements Screen, GameEventListener {
    
    private final Main game;
    private final GameSession session;
    
    // Graphics
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private BitmapFont titleFont;
    private BitmapFont smallFont;
    private GlyphLayout layout;
    
    // Animation timers
    private float animTime = 0f;
    private float scorePopTimer = 0f;
    private int lastScore = 0;
    private float factFadeAlpha = 1f;
    private float threatPulse = 0f;
    private float[] slotGlowTimers = new float[5];
    
    // Observer effect timers
    private float comboFlashTimer = 0f;
    private float riskFlashTimer = 0f;
    private String lastEventMessage = "";
    private float eventMessageTimer = 0f;
    
    // Screen dimensions
    private static final float WORLD_WIDTH = 1280;
    private static final float WORLD_HEIGHT = 720;
    
    // UI Layout constants
    private static final float PADDING = 20;
    private static final float HEADER_HEIGHT = 80;
    private static final float COMPONENT_PANEL_WIDTH = 300;
    private static final float RISK_BAR_HEIGHT = 30;
    
    // Color Palette - Cyberpunk/Neon Theme
    private static final Color BG_COLOR = new Color(0.06f, 0.08f, 0.12f, 1f);
    private static final Color BG_GRADIENT_TOP = new Color(0.08f, 0.1f, 0.18f, 1f);
    private static final Color PANEL_COLOR = new Color(0.1f, 0.12f, 0.2f, 0.9f);
    private static final Color PANEL_BORDER = new Color(0.2f, 0.4f, 0.6f, 0.5f);
    
    // Neon accent colors
    private static final Color NEON_CYAN = new Color(0.0f, 0.9f, 1f, 1f);
    private static final Color NEON_PINK = new Color(1f, 0.2f, 0.6f, 1f);
    private static final Color NEON_GREEN = new Color(0.2f, 1f, 0.4f, 1f);
    private static final Color NEON_ORANGE = new Color(1f, 0.6f, 0.1f, 1f);
    private static final Color NEON_PURPLE = new Color(0.7f, 0.3f, 1f, 1f);
    
    private static final Color GOLD_COLOR = new Color(1f, 0.85f, 0.3f, 1f);
    private static final Color TEXT_COLOR = new Color(0.95f, 0.95f, 1f, 1f);
    private static final Color DIM_COLOR = new Color(0.5f, 0.55f, 0.7f, 1f);
    
    public GameScreen(Main game) {
        this.game = game;
        this.session = new GameSession();
    }
    
    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        font.setColor(TEXT_COLOR);
        
        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.5f);
        titleFont.setColor(NEON_CYAN);
        
        smallFont = new BitmapFont();
        smallFont.getData().setScale(1.2f);
        smallFont.setColor(DIM_COLOR);
        
        layout = new GlyphLayout();
        
        // Register as Observer for game events
        session.addListener(this);
        
        // Start the game
        session.startGame();
        
        // Add demo components for testing
        addDemoComponents();
    }
    
    private void addDemoComponents() {
        // Add some components to showcase the UI
        session.addComponent(new PasswordComponent(PasswordStrength.STRONG));
        session.addComponent(new TwoFAComponent(TwoFAType.AUTHENTICATOR));
        session.addComponent(new UpdateComponent(UpdateSetting.AUTO));
    }
    
    @Override
    public void render(float delta) {
        // Update animations
        animTime += delta;
        threatPulse += delta * 8f;
        
        // Fact fade animation
        factFadeAlpha = 0.7f + 0.3f * MathUtils.sin(animTime * 0.5f);
        
        // Score pop animation
        if (scorePopTimer > 0) {
            scorePopTimer -= delta;
        }
        
        // Update slot glow timers
        for (int i = 0; i < slotGlowTimers.length; i++) {
            if (slotGlowTimers[i] > 0) {
                slotGlowTimers[i] -= delta;
            }
        }
        
        // Detect score changes for pop effect
        if (session.getTotalScore() != lastScore) {
            scorePopTimer = 0.5f;
            lastScore = session.getTotalScore();
        }
        
        // Update game logic
        session.update(delta);
        
        // Handle input
        handleInput();
        
        // Clear screen with gradient effect
        Gdx.gl.glClearColor(BG_COLOR.r, BG_COLOR.g, BG_COLOR.b, BG_COLOR.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Enable blending for transparency effects
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        camera.update();
        
        // Draw shapes (panels, bars)
        shapeRenderer.setProjectionMatrix(camera.combined);
        drawBackground();
        drawPanels();
        drawRiskMeter();
        drawComponentSlots();
        drawSecurityScore();
        
        // Draw text
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawHeader();
        drawScore();
        drawFact();
        drawComponents();
        drawThreatEvent();
        drawInstructions();
        drawGameState();
        batch.end();
    }
    
    private void handleInput() {
        // Number keys to add/cycle components
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            cycleComponent(SecurityComponentType.PASSWORD);
            slotGlowTimers[0] = 0.5f;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            cycleComponent(SecurityComponentType.TWO_FA);
            slotGlowTimers[1] = 0.5f;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            cycleComponent(SecurityComponentType.UPDATES);
            slotGlowTimers[2] = 0.5f;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            cycleComponent(SecurityComponentType.RECOVERY);
            slotGlowTimers[3] = 0.5f;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
            cycleComponent(SecurityComponentType.PRIVACY);
            slotGlowTimers[4] = 0.5f;
        }
        
        // Undo/Redo
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z) && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            session.undo();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Y) && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            session.redo();
        }
        
        // Submit build
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            session.submitBuild();
        }
        
        // Restart
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            session.startGame();
            addDemoComponents();
        }
        
        // Cycle difficulty (only in menu or when game just started)
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            session.cycleDifficulty();
        }
    }
    
    private void cycleComponent(SecurityComponentType type) {
        SecurityComponent newComponent = null;
        
        switch (type) {
            case PASSWORD:
                PasswordStrength[] pwStrengths = PasswordStrength.values();
                PasswordComponent pwComp = (PasswordComponent) session.getCurrentAccount().getComponent(type);
                int pwIndex = (pwComp == null) ? 0 : (pwComp.getStrength().ordinal() + 1) % pwStrengths.length;
                newComponent = new PasswordComponent(pwStrengths[pwIndex]);
                break;
                
            case TWO_FA:
                TwoFAType[] tfaTypes = TwoFAType.values();
                TwoFAComponent tfaComp = (TwoFAComponent) session.getCurrentAccount().getComponent(type);
                int tfaIndex = (tfaComp == null) ? 0 : (tfaComp.getTwoFAType().ordinal() + 1) % tfaTypes.length;
                newComponent = new TwoFAComponent(tfaTypes[tfaIndex]);
                break;
                
            case UPDATES:
                UpdateSetting[] updateSettings = UpdateSetting.values();
                UpdateComponent updComp = (UpdateComponent) session.getCurrentAccount().getComponent(type);
                int updIndex = (updComp == null) ? 0 : (updComp.getSetting().ordinal() + 1) % updateSettings.length;
                newComponent = new UpdateComponent(updateSettings[updIndex]);
                break;
                
            case RECOVERY:
                RecoveryType[] recTypes = RecoveryType.values();
                RecoveryComponent recComp = (RecoveryComponent) session.getCurrentAccount().getComponent(type);
                int recIndex = (recComp == null) ? 0 : (recComp.getRecoveryType().ordinal() + 1) % recTypes.length;
                newComponent = new RecoveryComponent(recTypes[recIndex]);
                break;
                
            case PRIVACY:
                PrivacyLevel[] privLevels = PrivacyLevel.values();
                PrivacyComponent privComp = (PrivacyComponent) session.getCurrentAccount().getComponent(type);
                int privIndex = (privComp == null) ? 0 : (privComp.getLevel().ordinal() + 1) % privLevels.length;
                newComponent = new PrivacyComponent(privLevels[privIndex]);
                break;
        }
        
        if (newComponent != null) {
            session.addComponent(newComponent);
        }
    }
    
    private void drawBackground() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Gradient background
        shapeRenderer.rect(0, 0, WORLD_WIDTH, WORLD_HEIGHT, 
            BG_COLOR, BG_COLOR, BG_GRADIENT_TOP, BG_GRADIENT_TOP);
        
        // Subtle animated grid lines for cyber effect
        shapeRenderer.setColor(0.1f, 0.15f, 0.25f, 0.3f);
        float gridSpacing = 50;
        float gridOffset = (animTime * 10) % gridSpacing;
        
        for (float x = gridOffset; x < WORLD_WIDTH; x += gridSpacing) {
            shapeRenderer.rectLine(x, 0, x, WORLD_HEIGHT, 1);
        }
        for (float y = gridOffset; y < WORLD_HEIGHT; y += gridSpacing) {
            shapeRenderer.rectLine(0, y, WORLD_WIDTH, y, 1);
        }
        
        shapeRenderer.end();
    }
    
    private void drawPanels() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Header panel with gradient
        shapeRenderer.rect(0, WORLD_HEIGHT - HEADER_HEIGHT, WORLD_WIDTH, HEADER_HEIGHT,
            new Color(0.1f, 0.12f, 0.2f, 0.95f), new Color(0.1f, 0.12f, 0.2f, 0.95f),
            new Color(0.15f, 0.18f, 0.28f, 0.95f), new Color(0.15f, 0.18f, 0.28f, 0.95f));
        
        // Left panel (components) with rounded effect
        drawRoundedPanel(PADDING, PADDING, COMPONENT_PANEL_WIDTH, WORLD_HEIGHT - HEADER_HEIGHT - PADDING * 2, NEON_CYAN);
        
        // Right panel (facts & info)
        float rightPanelX = WORLD_WIDTH - COMPONENT_PANEL_WIDTH - PADDING;
        drawRoundedPanel(rightPanelX, PADDING, COMPONENT_PANEL_WIDTH, WORLD_HEIGHT - HEADER_HEIGHT - PADDING * 2, NEON_PURPLE);
        
        shapeRenderer.end();
        
        // Draw panel borders with glow
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        Gdx.gl.glLineWidth(2);
        
        // Header bottom border with neon glow
        float glowIntensity = 0.5f + 0.2f * MathUtils.sin(animTime * 2);
        shapeRenderer.setColor(NEON_CYAN.r, NEON_CYAN.g, NEON_CYAN.b, glowIntensity);
        shapeRenderer.line(0, WORLD_HEIGHT - HEADER_HEIGHT, WORLD_WIDTH, WORLD_HEIGHT - HEADER_HEIGHT);
        
        // Left panel border
        shapeRenderer.setColor(NEON_CYAN.r, NEON_CYAN.g, NEON_CYAN.b, 0.6f);
        shapeRenderer.rect(PADDING, PADDING, COMPONENT_PANEL_WIDTH, WORLD_HEIGHT - HEADER_HEIGHT - PADDING * 2);
        
        // Right panel border
        shapeRenderer.setColor(NEON_PURPLE.r, NEON_PURPLE.g, NEON_PURPLE.b, 0.6f);
        shapeRenderer.rect(rightPanelX, PADDING, COMPONENT_PANEL_WIDTH, WORLD_HEIGHT - HEADER_HEIGHT - PADDING * 2);
        
        shapeRenderer.end();
        Gdx.gl.glLineWidth(1);
    }
    
    private void drawRoundedPanel(float x, float y, float width, float height, Color accentColor) {
        // Main panel body
        shapeRenderer.setColor(PANEL_COLOR);
        shapeRenderer.rect(x, y, width, height);
        
        // Top accent strip
        shapeRenderer.setColor(accentColor.r, accentColor.g, accentColor.b, 0.3f);
        shapeRenderer.rect(x, y + height - 4, width, 4);
    }
    
    private void drawHeader() {
        // Animated title with color cycling
        float hue = (animTime * 0.1f) % 1f;
        Color titleColor = new Color();
        titleColor.fromHsv(180 + hue * 30, 0.8f, 1f);
        titleColor.a = 1f;
        
        titleFont.setColor(NEON_CYAN);
        titleFont.draw(batch, "SECURE ACCOUNT BUILDER", PADDING, WORLD_HEIGHT - 25);
        
        // Difficulty badge
        DifficultyLevel diff = session.getDifficulty();
        float[] diffColor = diff.getColor();
        font.setColor(diffColor[0], diffColor[1], diffColor[2], 1f);
        font.draw(batch, diff.getDisplayName().toUpperCase(), 420, WORLD_HEIGHT - 30);
        
        // Level badge
        font.setColor(NEON_PURPLE);
        String levelText = "LVL " + session.getCurrentLevel();
        font.draw(batch, levelText, 540, WORLD_HEIGHT - 30);
        
        // Time with subtle pulse
        float timePulse = 0.8f + 0.2f * MathUtils.sin(animTime * 3);
        font.setColor(TEXT_COLOR.r * timePulse, TEXT_COLOR.g * timePulse, TEXT_COLOR.b * timePulse, 1f);
        font.draw(batch, session.getFormattedTime(), 640, WORLD_HEIGHT - 30);
    }
    
    private void drawScore() {
        float x = WORLD_WIDTH - 350;
        float y = WORLD_HEIGHT - 25;
        
        // Score with pop animation
        float scale = 1f;
        if (scorePopTimer > 0) {
            scale = 1f + 0.3f * (scorePopTimer / 0.5f);
        }
        
        font.getData().setScale(1.5f * scale);
        font.setColor(GOLD_COLOR);
        font.draw(batch, "SCORE: " + session.getTotalScore(), x, y);
        font.getData().setScale(1.5f);
        
        // Combo with fire effect
        int combo = session.getComboMultiplier();
        if (combo > 1) {
            float comboPulse = 0.7f + 0.3f * MathUtils.sin(animTime * 10);
            Color comboColor = new Color(1f, 0.3f + comboPulse * 0.3f, 0.1f, 1f);
            font.setColor(comboColor);
            font.draw(batch, combo + "x COMBO!", x + 170, y + MathUtils.sin(animTime * 5) * 3);
        }
    }
    
    private void drawSecurityScore() {
        float centerX = WORLD_WIDTH / 2;
        float centerY = WORLD_HEIGHT - HEADER_HEIGHT - 120;
        float radius = 60;
        
        int totalScore = session.getCurrentAccount().getTotalSecurityScore();
        int maxScore = 500; // Approximate max
        float percent = Math.min(1f, totalScore / (float) maxScore);
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Background circle
        shapeRenderer.setColor(0.15f, 0.15f, 0.2f, 1f);
        drawCircle(centerX, centerY, radius, 32);
        
        // Progress arc
        Color scoreColor = getScoreColor(percent);
        shapeRenderer.setColor(scoreColor);
        drawArc(centerX, centerY, radius - 5, 90, 360 * percent, 32);
        
        // Inner circle
        shapeRenderer.setColor(PANEL_COLOR);
        drawCircle(centerX, centerY, radius - 15, 32);
        
        shapeRenderer.end();
        
        // Score text
        batch.begin();
        font.setColor(scoreColor);
        layout.setText(font, String.valueOf(totalScore));
        font.draw(batch, String.valueOf(totalScore), centerX - layout.width / 2, centerY + 10);
        
        smallFont.setColor(DIM_COLOR);
        layout.setText(smallFont, "SECURITY");
        smallFont.draw(batch, "SECURITY", centerX - layout.width / 2, centerY - 15);
        batch.end();
    }
    
    private Color getScoreColor(float percent) {
        if (percent < 0.3f) return new Color(1f, 0.3f, 0.3f, 1f);
        if (percent < 0.6f) return NEON_ORANGE;
        if (percent < 0.8f) return new Color(0.8f, 1f, 0.3f, 1f);
        return NEON_GREEN;
    }
    
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
    
    private void drawRiskMeter() {
        RiskMeter riskMeter = session.getRiskMeter();
        float riskPercent = riskMeter.getRiskPercentage();
        Color riskColor = riskMeter.getRiskColor();
        
        float barX = PADDING + COMPONENT_PANEL_WIDTH + 40;
        float barY = WORLD_HEIGHT - HEADER_HEIGHT - 220;
        float barWidth = WORLD_WIDTH - COMPONENT_PANEL_WIDTH * 2 - PADDING * 2 - 80;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Background with subtle gradient
        shapeRenderer.rect(barX, barY, barWidth, RISK_BAR_HEIGHT,
            new Color(0.15f, 0.15f, 0.2f, 1f), new Color(0.15f, 0.15f, 0.2f, 1f),
            new Color(0.2f, 0.2f, 0.25f, 1f), new Color(0.2f, 0.2f, 0.25f, 1f));
        
        // Risk fill with pulsing glow effect when high
        float pulse = 1f;
        if (riskPercent > 0.7f) {
            pulse = 0.8f + 0.2f * MathUtils.sin(animTime * 8);
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
        font.draw(batch, "RISK: " + riskMeter.getRiskLevel().getDisplayName() + " (" + riskMeter.getCurrentRisk() + "%)", 
                  barX, barY + RISK_BAR_HEIGHT + 25);
        batch.end();
    }
    
    private void drawComponentSlots() {
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
            
            // Slot background with gradient
            Color slotColor1, slotColor2;
            if (hasComponent) {
                slotColor1 = new Color(0.1f, 0.25f, 0.2f, 1f);
                slotColor2 = new Color(0.15f, 0.35f, 0.25f, 1f);
            } else {
                slotColor1 = new Color(0.2f, 0.12f, 0.12f, 1f);
                slotColor2 = new Color(0.25f, 0.15f, 0.15f, 1f);
            }
            shapeRenderer.rect(startX, y, slotWidth, slotHeight, slotColor1, slotColor1, slotColor2, slotColor2);
            
            // Glow effect when recently changed
            if (slotGlowTimers[i] > 0) {
                float glowAlpha = slotGlowTimers[i] / 0.5f * 0.5f;
                shapeRenderer.setColor(NEON_CYAN.r, NEON_CYAN.g, NEON_CYAN.b, glowAlpha);
                shapeRenderer.rect(startX, y, slotWidth, slotHeight);
            }
        }
        
        shapeRenderer.end();
        
        // Slot borders
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (int i = 0; i < types.length; i++) {
            float y = startY - i * (slotHeight + 10);
            boolean hasComponent = account.hasComponent(types[i]);
            
            Color borderColor = hasComponent ? NEON_GREEN : new Color(0.5f, 0.2f, 0.2f, 0.5f);
            if (slotGlowTimers[i] > 0) {
                borderColor = NEON_CYAN;
            }
            shapeRenderer.setColor(borderColor.r, borderColor.g, borderColor.b, 0.5f);
            shapeRenderer.rect(startX, y, slotWidth, slotHeight);
        }
        shapeRenderer.end();
    }
    
    private void drawComponents() {
        float startX = PADDING + 20;
        float startY = WORLD_HEIGHT - HEADER_HEIGHT - 60;
        float slotHeight = 80;
        
        SecurityComponentType[] types = SecurityComponentType.values();
        SecureAccount account = session.getCurrentAccount();
        
        // Component type colors
        Color[] typeColors = { NEON_CYAN, NEON_PINK, NEON_ORANGE, NEON_GREEN, NEON_PURPLE };
        
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
                font.setColor(TEXT_COLOR);
                font.draw(batch, comp.getValue(), startX + 35, y - 25);
                
                // Score with icon
                smallFont.setColor(NEON_GREEN);
                smallFont.draw(batch, "+" + comp.getSecurityScore() + " pts", startX + 35, y - 50);
            } else {
                font.setColor(new Color(0.5f, 0.3f, 0.3f, 0.8f));
                font.draw(batch, "Not set", startX + 35, y - 25);
            }
        }
    }
    
    private void drawFact() {
        CybersecurityFact fact = session.getCurrentFact();
        if (fact == null) return;
        
        float x = WORLD_WIDTH - COMPONENT_PANEL_WIDTH - PADDING + 15;
        float y = WORLD_HEIGHT - HEADER_HEIGHT - 60;
        
        // Category header with glow effect
        titleFont.setColor(NEON_PURPLE.r, NEON_PURPLE.g, NEON_PURPLE.b, factFadeAlpha);
        titleFont.getData().setScale(1.5f);
        titleFont.draw(batch, "DID YOU KNOW?", x, y);
        titleFont.getData().setScale(2.5f);
        
        // Fact text with fade
        font.setColor(TEXT_COLOR.r, TEXT_COLOR.g, TEXT_COLOR.b, factFadeAlpha);
        String factText = fact.getFact();
        drawWrappedText(font, factText, x, y - 40, COMPONENT_PANEL_WIDTH - 30);
        
        // Category badge
        Color categoryColor = getCategoryColor(fact.getCategory());
        smallFont.setColor(categoryColor.r, categoryColor.g, categoryColor.b, 0.8f);
        smallFont.draw(batch, fact.getCategory().getDisplayName(), x, y - 150);
    }
    
    private Color getCategoryColor(SecurityComponentType type) {
        switch (type) {
            case PASSWORD: return NEON_CYAN;
            case TWO_FA: return NEON_PINK;
            case UPDATES: return NEON_ORANGE;
            case RECOVERY: return NEON_GREEN;
            case PRIVACY: return NEON_PURPLE;
            default: return TEXT_COLOR;
        }
    }
    
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
    
    private void drawThreatEvent() {
        ThreatEvent threat = session.getActiveThreat();
        if (threat == null) return;
        
        // Draw threat alert in center with pulsing effect
        float alertWidth = 500;
        float alertHeight = 150;
        float alertX = (WORLD_WIDTH - alertWidth) / 2;
        float alertY = (WORLD_HEIGHT - alertHeight) / 2;
        
        float pulse = 0.8f + 0.2f * MathUtils.sin(threatPulse);
        
        // Outer glow
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.8f * pulse, 0.1f, 0.1f, 0.3f);
        shapeRenderer.rect(alertX - 10, alertY - 10, alertWidth + 20, alertHeight + 20);
        
        // Background
        shapeRenderer.setColor(0.15f, 0.05f, 0.05f, 0.98f);
        shapeRenderer.rect(alertX, alertY, alertWidth, alertHeight);
        shapeRenderer.end();
        
        // Border with pulse
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        Gdx.gl.glLineWidth(3);
        shapeRenderer.setColor(1f * pulse, 0.2f, 0.2f, 1f);
        shapeRenderer.rect(alertX, alertY, alertWidth, alertHeight);
        shapeRenderer.end();
        Gdx.gl.glLineWidth(1);
        
        // Warning icon (triangle)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1f * pulse, 0.3f, 0.1f, 1f);
        float iconX = alertX + 30;
        float iconY = alertY + alertHeight - 50;
        shapeRenderer.triangle(iconX, iconY - 20, iconX + 20, iconY - 20, iconX + 10, iconY + 10);
        shapeRenderer.end();
        
        // Text
        batch.begin();
        titleFont.setColor(new Color(1f * pulse, 0.3f, 0.2f, 1f));
        titleFont.draw(batch, threat.getName(), alertX + 60, alertY + alertHeight - 20);
        
        font.setColor(TEXT_COLOR);
        drawWrappedText(font, threat.getDescription(), alertX + 20, alertY + alertHeight - 70, alertWidth - 40);
        batch.end();
    }
    
    private void drawInstructions() {
        float x = WORLD_WIDTH - COMPONENT_PANEL_WIDTH - PADDING + 15;
        float y = 200;
        
        smallFont.setColor(NEON_CYAN.r * 0.6f, NEON_CYAN.g * 0.6f, NEON_CYAN.b * 0.6f, 1f);
        smallFont.draw(batch, "CONTROLS:", x, y);
        
        smallFont.setColor(DIM_COLOR);
        smallFont.draw(batch, "[1-5] Cycle components", x, y - 25);
        smallFont.draw(batch, "[D] Change difficulty", x, y - 50);
        smallFont.draw(batch, "[Ctrl+Z] Undo", x, y - 75);
        smallFont.draw(batch, "[Ctrl+Y] Redo", x, y - 100);
        smallFont.draw(batch, "[Enter] Submit build", x, y - 125);
        smallFont.draw(batch, "[R] Restart", x, y - 150);
    }
    
    private void drawGameState() {
        GameState state = session.getState();
        
        if (state == GameState.GAME_OVER) {
            // Draw overlay
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 0.7f);
            shapeRenderer.rect(0, 0, WORLD_WIDTH, WORLD_HEIGHT);
            shapeRenderer.end();
            
            // Game over text
            batch.begin();
            titleFont.setColor(NEON_PINK);
            titleFont.getData().setScale(3f);
            layout.setText(titleFont, "GAME OVER");
            titleFont.draw(batch, "GAME OVER", (WORLD_WIDTH - layout.width) / 2, WORLD_HEIGHT / 2 + 50);
            titleFont.getData().setScale(2.5f);
            
            font.setColor(GOLD_COLOR);
            String scoreText = "Final Score: " + session.getTotalScore();
            layout.setText(font, scoreText);
            font.draw(batch, scoreText, (WORLD_WIDTH - layout.width) / 2, WORLD_HEIGHT / 2 - 20);
            
            smallFont.setColor(DIM_COLOR);
            String restartText = "Press [R] to restart";
            layout.setText(smallFont, restartText);
            smallFont.draw(batch, restartText, (WORLD_WIDTH - layout.width) / 2, WORLD_HEIGHT / 2 - 60);
            batch.end();
        }
        
        if (state == GameState.LEVEL_COMPLETE) {
            // Draw overlay
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 0.5f);
            shapeRenderer.rect(0, 0, WORLD_WIDTH, WORLD_HEIGHT);
            shapeRenderer.end();
            
            // Level complete text
            batch.begin();
            titleFont.setColor(NEON_GREEN);
            titleFont.getData().setScale(3f);
            layout.setText(titleFont, "LEVEL COMPLETE!");
            titleFont.draw(batch, "LEVEL COMPLETE!", (WORLD_WIDTH - layout.width) / 2, WORLD_HEIGHT / 2 + 50);
            titleFont.getData().setScale(2.5f);
            
            smallFont.setColor(DIM_COLOR);
            String continueText = "Press [Enter] to continue";
            layout.setText(smallFont, continueText);
            smallFont.draw(batch, continueText, (WORLD_WIDTH - layout.width) / 2, WORLD_HEIGHT / 2 - 20);
            batch.end();
        }
    }
    
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
    
    @Override
    public void pause() {
        session.setState(GameState.PAUSED);
    }
    
    @Override
    public void resume() {
        session.setState(GameState.PLAYING);
    }
    
    @Override
    public void hide() {}
    
    @Override
    public void dispose() {
        session.removeListener(this);
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
        titleFont.dispose();
        smallFont.dispose();
    }
    
    // ==================== Observer Pattern Methods ====================
    
    @Override
    public void onScoreChanged(int oldScore, int newScore, int delta) {
        // Trigger score pop animation
        scorePopTimer = 0.5f;
        if (delta > 0) {
            lastEventMessage = "+" + delta + " points!";
        } else {
            lastEventMessage = delta + " points";
        }
        eventMessageTimer = 2f;
    }
    
    @Override
    public void onRiskChanged(int oldRisk, int newRisk) {
        // Flash risk bar when risk increases
        if (newRisk > oldRisk) {
            riskFlashTimer = 0.5f;
        }
    }
    
    @Override
    public void onComboChanged(int oldCombo, int newCombo) {
        // Flash combo display
        if (newCombo > oldCombo) {
            comboFlashTimer = 0.5f;
            lastEventMessage = newCombo + "x COMBO!";
            eventMessageTimer = 1.5f;
        }
    }
    
    @Override
    public void onThreatTriggered(String threatName, int damage) {
        // Threat events are already handled by activeThreat display
        lastEventMessage = threatName + " (-" + damage + ")";
        eventMessageTimer = 3f;
    }
    
    @Override
    public void onComponentChanged(String componentType, String componentValue) {
        // Component changes trigger slot glow which is already handled
        lastEventMessage = componentType + ": " + componentValue;
        eventMessageTimer = 1f;
    }
}
