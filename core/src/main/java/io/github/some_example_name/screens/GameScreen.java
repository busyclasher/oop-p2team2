package io.github.some_example_name.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.some_example_name.GameSession;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.*;
import io.github.some_example_name.observer.*;
import io.github.some_example_name.screens.animation.AnimationManager;
import io.github.some_example_name.screens.input.GameInputHandler;
import io.github.some_example_name.screens.renderers.GameRenderer;
import io.github.some_example_name.screens.renderers.UIRenderer;
import io.github.some_example_name.theme.GameTheme;

/**
 * Refactored main gameplay screen - now acts as a coordinator.
 * Delegates responsibilities to specialized classes:
 * - GameRenderer: Shape and visual rendering
 * - UIRenderer: Text and UI elements
 * - GameInputHandler: Input processing
 * - AnimationManager: Animation timers
 * 
 * Demonstrates the Single Responsibility Principle and Delegation pattern.
 * Reduced from 870 lines to ~180 lines by proper separation of concerns.
 */
public class GameScreen implements Screen, GameEventListener {
    
    @SuppressWarnings("unused")
    private final Main game;
    private final GameSession session;
    
    // Graphics infrastructure
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private BitmapFont titleFont;
    private BitmapFont smallFont;
    
    // Delegated subsystems
    private GameRenderer gameRenderer;
    private UIRenderer uiRenderer;
    private GameInputHandler inputHandler;
    private AnimationManager animationManager;
    
    // Screen dimensions
    private static final float WORLD_WIDTH = 1280;
    private static final float WORLD_HEIGHT = 720;
    
    public GameScreen(Main game) {
        this.game = game;
        this.session = new GameSession();
    }
    
    @Override
    public void show() {
        // Initialize camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        
        // Initialize graphics resources
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        
        // Initialize fonts with theme colors
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        font.setColor(GameTheme.TEXT_COLOR);
        
        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.5f);
        titleFont.setColor(GameTheme.NEON_CYAN);
        
        smallFont = new BitmapFont();
        smallFont.getData().setScale(1.2f);
        smallFont.setColor(GameTheme.DIM_COLOR);
        
        // Initialize delegated subsystems
        uiRenderer = new UIRenderer(batch, font, titleFont, smallFont);
        gameRenderer = new GameRenderer(shapeRenderer, batch, uiRenderer, font, titleFont);
        inputHandler = new GameInputHandler();
        animationManager = new AnimationManager();
        
        // Register as Observer for game events
        session.addListener(this);
        
        // Start the game
        session.startGame();
        
        // Add demo components for testing
        addDemoComponents();
    }
    
    /**
     * Add initial demo components to showcase the UI.
     */
    private void addDemoComponents() {
        session.addComponent(new PasswordComponent(PasswordStrength.STRONG));
        session.addComponent(new TwoFAComponent(TwoFAType.AUTHENTICATOR));
        session.addComponent(new UpdateComponent(UpdateSetting.AUTO));
    }
    
    @Override
    public void render(float delta) {
        // Update animation timers
        animationManager.update(delta, session.getTotalScore());
        
        // Update game logic
        session.update(delta);
        
        // Handle input (returns slot index if component changed)
        int changedSlot = inputHandler.handleInput(session);
        if (changedSlot >= 0) {
            animationManager.triggerSlotGlow(changedSlot);
        }
        
        // Update camera
        camera.update();
        
        // Delegate rendering
        gameRenderer.render(session, animationManager);
    }
    
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
    
    @Override
    public void pause() {
        session.setState(GameSession.GameState.PAUSED);
    }
    
    @Override
    public void resume() {
        session.setState(GameSession.GameState.PLAYING);
    }
    
    @Override
    public void hide() {
        // No action needed
    }
    
    @Override
    public void dispose() {
        session.removeListener(this);
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
        titleFont.dispose();
        smallFont.dispose();
    }
    
    // ==================== Observer Pattern Implementation ====================
    
    @Override
    public void onScoreChanged(int oldScore, int newScore, int delta) {
        // Trigger score pop animation
        animationManager.triggerScorePop();
        if (delta > 0) {
            animationManager.setEventMessage("+" + delta + " points!", 2f);
        } else {
            animationManager.setEventMessage(delta + " points", 2f);
        }
    }
    
    @Override
    public void onRiskChanged(int oldRisk, int newRisk) {
        // Flash risk bar when risk increases
        if (newRisk > oldRisk) {
            animationManager.triggerRiskFlash();
        }
    }
    
    @Override
    public void onComboChanged(int oldCombo, int newCombo) {
        // Flash combo display
        if (newCombo > oldCombo) {
            animationManager.triggerComboFlash();
            animationManager.setEventMessage(newCombo + "x COMBO!", 1.5f);
        }
    }
    
    @Override
    public void onThreatTriggered(String threatName, int damage) {
        // Set event message for threat
        animationManager.setEventMessage(threatName + " (-" + damage + ")", 3f);
    }
    
    @Override
    public void onComponentChanged(String componentType, String componentValue) {
        // Set event message for component change
        animationManager.setEventMessage(componentType + ": " + componentValue, 1f);
    }
}
