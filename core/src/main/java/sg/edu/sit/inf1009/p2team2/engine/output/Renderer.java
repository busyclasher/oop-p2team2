package sg.edu.sit.inf1009.p2team2.engine.output;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.Map;

/**
 * RENDERER - Abstract Engine
 * Provides drawing primitives for the game.
 * 
 * Abstracts libGDX rendering APIs to provide a simple, generic interface.
 * 
 * ENHANCEMENTS:
 * - Added drawTexture() method for direct Texture drawing (backward compatibility)
 * - Added drawBackground() convenience method
 * - Added isDrawing() check method
 */
public class Renderer {
    private static final float WORLD_WIDTH = 1280f;
    private static final float WORLD_HEIGHT = 720f;
    
    private Color clearColor;
    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont defaultFont;
    private int viewportWidth;
    private int viewportHeight;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Vector3 tempScreenPosition;
    
    // Sprite cache
    private Map<String, Texture> spriteCache;
    
    /**
     * Constructor
     */
    public Renderer() {
        this.clearColor = new Color(0, 0, 0, 1); // Black by default
        this.spriteBatch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.defaultFont = new BitmapFont(); // libGDX default font
        this.spriteCache = new HashMap<>();
        this.viewportWidth = 0;
        this.viewportHeight = 0;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        this.tempScreenPosition = new Vector3();

        syncViewport();
    }

    /**
     * Resize renderer projection to match the window/framebuffer size.
     *
     * @param width viewport width
     * @param height viewport height
     */
    public void resizeViewport(int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }

        viewportWidth = width;
        viewportHeight = height;
        viewport.update(width, height, true);
        spriteBatch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
    }

    /**
     * Keep renderer projection in sync with the current window size.
     */
    private void syncViewport() {
        if (Gdx.graphics == null) {
            return;
        }

        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        if (width != viewportWidth || height != viewportHeight) {
            resizeViewport(width, height);
        }
    }
    
    /**
     * Clear the screen with the clear color
     */
    public void clear() {
        Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
    
    /**
     * Set the clear color
     * 
     * @param color New clear color
     */
    public void setClearColor(Color color) {
        this.clearColor = color;
    }
    
    /**
     * Begin rendering
     * Call this before any draw calls
     */
    public void begin() {
        syncViewport();
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
    }

    /**
     * Convert screen-space coordinates to world-space coordinates.
     *
     * @param screenX screen x
     * @param screenY screen y
     * @return world-space position
     */
    public Vector2 screenToWorld(float screenX, float screenY) {
        tempScreenPosition.set(screenX, screenY, 0f);
        viewport.unproject(tempScreenPosition);
        return new Vector2(tempScreenPosition.x, tempScreenPosition.y);
    }

    public float getWorldWidth() {
        return viewport.getWorldWidth();
    }

    public float getWorldHeight() {
        return viewport.getWorldHeight();
    }
    
    /**
     * End rendering
     * Call this after all draw calls are done
     */
    public void end() {
        spriteBatch.end();
    }
    
    /**
     * Check if currently drawing
     * @return true if batch is active
     */
    public boolean isDrawing() {
        return spriteBatch.isDrawing();
    }
    
    // ===== SPRITE DRAWING =====
    
    /**
     * Draw a sprite (basic version)
     * 
     * @param spriteId Path to sprite image (e.g., "player.png")
     * @param position Position to draw at
     * @param rotation Rotation in degrees
     * @param scale Scale (1.0 = normal size)
     */
    public void drawSprite(String spriteId, Vector2 position, float rotation, Vector2 scale) {
        drawSprite(spriteId, position, rotation, scale, Color.WHITE);
    }
    
    /**
     * Draw a sprite with color tint
     * 
     * @param spriteId Path to sprite image
     * @param position Position to draw at
     * @param rotation Rotation in degrees
     * @param scale Scale vector
     * @param color Color tint
     */
    public void drawSprite(String spriteId, Vector2 position, float rotation, Vector2 scale, Color color) {
        Texture texture = getOrLoadTexture(spriteId);
        
        if (texture == null) {
            Gdx.app.error("Renderer", "Failed to load texture: " + spriteId);
            return;
        }
        
        // Set color tint
        Color oldColor = spriteBatch.getColor();
        spriteBatch.setColor(color);
        
        // Calculate dimensions
        float width = texture.getWidth() * scale.x;
        float height = texture.getHeight() * scale.y;
        float originX = width / 2;
        float originY = height / 2;
        
        // Draw sprite
        spriteBatch.draw(
            texture,
            position.x - originX, position.y - originY, // position
            originX, originY,                           // origin (for rotation)
            width, height,                              // size
            1, 1,                                       // additional scale (not used)
            rotation,                                   // rotation
            0, 0,                                       // source position in texture
            texture.getWidth(), texture.getHeight(),   // source size
            false, false                                // flip x, flip y
        );
        
        // Restore color
        spriteBatch.setColor(oldColor);
    }
    
    /**
     * Draw a texture directly (for backward compatibility)
     * ADDED: This method allows MenuScene to pass Texture objects
     * 
     * @param texture Texture to draw
     * @param x X position (bottom-left corner)
     * @param y Y position (bottom-left corner)
     * @param width Width to draw
     * @param height Height to draw
     */
    public void drawTexture(Texture texture, float x, float y, float width, float height) {
        if (texture == null) {
            Gdx.app.error("Renderer", "Attempted to draw null texture");
            return;
        }
        spriteBatch.draw(texture, x, y, width, height);
    }
    
    /**
     * Draw a fullscreen background
     * ADDED: Convenience method for drawing backgrounds
     * 
     * @param spriteId Path to background image
     */
    public void drawBackground(String spriteId) {
        float screenWidth = viewport.getWorldWidth();
        float screenHeight = viewport.getWorldHeight();
        
        Vector2 center = new Vector2(screenWidth / 2f, screenHeight / 2f);
        
        Texture texture = getOrLoadTexture(spriteId);
        if (texture == null) {
            return;
        }
        
        // Calculate scale to fill screen
        float scaleX = screenWidth / texture.getWidth();
        float scaleY = screenHeight / texture.getHeight();
        
        drawSprite(spriteId, center, 0f, new Vector2(scaleX, scaleY));
    }
    
    /**
     * Get or load a texture from cache
     */
    private Texture getOrLoadTexture(String spriteId) {
        if (!spriteCache.containsKey(spriteId)) {
            try {
                Texture texture = new Texture(Gdx.files.internal(spriteId));
                spriteCache.put(spriteId, texture);
            } catch (Exception e) {
                Gdx.app.error("Renderer", "Could not load texture: " + spriteId, e);
                return null;
            }
        }
        return spriteCache.get(spriteId);
    }
    
    // ===== TEXT DRAWING =====
    
    /**
     * Draw text
     * 
     * @param text Text to draw
     * @param position Position to draw at
     * @param font Font name (currently unused, uses default font)
     * @param color Text color
     */
    public void drawText(String text, Vector2 position, String font, Color color) {
        Color oldColor = defaultFont.getColor();
        defaultFont.setColor(color);
        defaultFont.draw(spriteBatch, text, position.x, position.y);
        defaultFont.setColor(oldColor);
    }
    
    // ===== SHAPE DRAWING =====
    
    /**
     * Draw a rectangle
     * 
     * @param rect Rectangle to draw
     * @param color Color
     * @param filled true for filled, false for outline
     */
    public void drawRect(Rectangle rect, Color color, boolean filled) {
        syncViewport();
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Must end sprite batch before using shape renderer
        boolean batchWasActive = spriteBatch.isDrawing();
        if (batchWasActive) {
            spriteBatch.end();
        }
        
        shapeRenderer.begin(filled ? ShapeRenderer.ShapeType.Filled : ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(color);
        shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        shapeRenderer.end();
        
        // Resume sprite batch if it was active
        if (batchWasActive) {
            spriteBatch.begin();
        }
    }
    
    /**
     * Draw a circle
     * 
     * @param center Center position
     * @param radius Radius
     * @param color Color
     * @param filled true for filled, false for outline
     */
    public void drawCircle(Vector2 center, float radius, Color color, boolean filled) {
        syncViewport();
        shapeRenderer.setProjectionMatrix(camera.combined);

        boolean batchWasActive = spriteBatch.isDrawing();
        if (batchWasActive) {
            spriteBatch.end();
        }
        
        shapeRenderer.begin(filled ? ShapeRenderer.ShapeType.Filled : ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(color);
        shapeRenderer.circle(center.x, center.y, radius);
        shapeRenderer.end();
        
        if (batchWasActive) {
            spriteBatch.begin();
        }
    }
    
    /**
     * Draw a line
     * 
     * @param start Start position
     * @param end End position
     * @param color Color
     * @param thickness Line thickness (currently unused in libGDX basic line)
     */
    public void drawLine(Vector2 start, Vector2 end, Color color, float thickness) {
        syncViewport();
        shapeRenderer.setProjectionMatrix(camera.combined);

        boolean batchWasActive = spriteBatch.isDrawing();
        if (batchWasActive) {
            spriteBatch.end();
        }
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.rectLine(start.x, start.y, end.x, end.y, thickness);
        shapeRenderer.end();
        
        if (batchWasActive) {
            spriteBatch.begin();
        }
    }
    
    /**
     * Clean up resources
     */
    public void dispose() {
        spriteBatch.dispose();
        shapeRenderer.dispose();
        defaultFont.dispose();
        
        // Dispose all cached textures
        for (Texture texture : spriteCache.values()) {
            texture.dispose();
        }
        spriteCache.clear();
    }
}