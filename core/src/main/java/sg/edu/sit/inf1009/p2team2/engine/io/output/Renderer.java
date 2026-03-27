package sg.edu.sit.inf1009.p2team2.engine.io.output;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Renderer abstraction over libGDX drawing APIs.
 * Provides sprite, text, and primitive drawing helpers used by scenes.
 */
public class Renderer {
    private static final float WORLD_WIDTH = 1280f;
    private static final float WORLD_HEIGHT = 720f;
    
    private Color clearColor;
    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private Map<String, BitmapFont> fonts;
    private GlyphLayout glyphLayout;
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
        this.fonts = new HashMap<>();
        this.glyphLayout = new GlyphLayout();
        this.spriteCache = new HashMap<>();
        this.viewportWidth = 0;
        this.viewportHeight = 0;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        this.tempScreenPosition = new Vector3();

        initialiseFonts();
        syncViewport();
    }

    private void initialiseFonts() {
        BitmapFont fallback = new BitmapFont();
        configureFont(fallback);
        fonts.put("default", fallback);
        fonts.put("body", fallback);
        fonts.put("body-small", fallback);
        fonts.put("body-tiny", fallback);
        fonts.put("body-large", fallback);
        fonts.put("title", fallback);
        fonts.put("title-small", fallback);

        loadGeneratedFont("title", "fonts/orbitron.ttf", 42,
            new Color(0.02f, 0.06f, 0.10f, 0.95f), 2f, 2, 2);
        loadGeneratedFont("title-small", "fonts/orbitron.ttf", 30,
            new Color(0.02f, 0.06f, 0.10f, 0.95f), 1.5f, 2, 2);
        loadGeneratedFont("body-large", "fonts/rajdhani-medium.ttf", 22,
            new Color(0.03f, 0.05f, 0.10f, 0.80f), 1.2f, 1, 1);
        loadGeneratedFont("body", "fonts/rajdhani-medium.ttf", 20,
            new Color(0.03f, 0.05f, 0.10f, 0.80f), 1.0f, 1, 1);
        loadGeneratedFont("body-small", "fonts/rajdhani-medium.ttf", 17,
            new Color(0.03f, 0.05f, 0.10f, 0.75f), 0.8f, 1, 1);
        loadGeneratedFont("body-tiny", "fonts/rajdhani-medium.ttf", 15,
            new Color(0.03f, 0.05f, 0.10f, 0.70f), 0.8f, 1, 1);
    }

    private void loadGeneratedFont(String key, String filePath, int size,
                                   Color borderColor, float borderWidth,
                                   int shadowX, int shadowY) {
        if (Gdx.files == null || !Gdx.files.internal(filePath).exists()) {
            return;
        }

        FreeTypeFontGenerator generator = null;
        try {
            generator = new FreeTypeFontGenerator(Gdx.files.internal(filePath));
            FreeTypeFontGenerator.FreeTypeFontParameter params =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
            params.size = size;
            params.minFilter = Texture.TextureFilter.Linear;
            params.magFilter = Texture.TextureFilter.Linear;
            params.borderColor = borderColor;
            params.borderWidth = borderWidth;
            params.shadowColor = new Color(0f, 0f, 0f, 0.35f);
            params.shadowOffsetX = shadowX;
            params.shadowOffsetY = shadowY;
            BitmapFont generated = generator.generateFont(params);
            configureFont(generated);
            fonts.put(key, generated);
        } catch (Exception e) {
            Gdx.app.error("Renderer", "Failed to generate font: " + key, e);
        } finally {
            if (generator != null) {
                generator.dispose();
            }
        }
    }

    private void configureFont(BitmapFont font) {
        font.getRegion().getTexture().setFilter(
            Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.getData().markupEnabled = false;
    }

    private BitmapFont resolveFont(String fontKey) {
        if (fontKey == null) {
            return fonts.get("default");
        }
        return fonts.getOrDefault(fontKey, fonts.get("default"));
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

    public void setClearColor(EngineColor color) {
        setClearColor(color.toGdxColor());
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
     * Draw a sprite at an exact pixel size (no rotation, no tint).
     * The position is the sprite's centre.
     *
     * @param spriteId    Path to sprite image
     * @param position    Centre position in world space
     * @param targetWidth  Desired width in world units
     * @param targetHeight Desired height in world units
     */
    public void drawSprite(String spriteId, Vector2 position, float targetWidth, float targetHeight) {
        Texture texture = getOrLoadTexture(spriteId);
        if (texture == null) return;
        float sx = targetWidth  / texture.getWidth();
        float sy = targetHeight / texture.getHeight();
        drawSprite(spriteId, position, 0f, new Vector2(sx, sy), Color.WHITE);
    }

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

    public void drawSprite(String spriteId, Vector2 position, float rotation, Vector2 scale, EngineColor color) {
        drawSprite(spriteId, position, rotation, scale, color.toGdxColor());
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
                texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
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
     * @param font Font name key
     * @param color Text color
     */
    public void drawText(String text, Vector2 position, String font, Color color) {
        BitmapFont chosenFont = resolveFont(font);
        Color oldColor = chosenFont.getColor();
        chosenFont.setColor(color);
        chosenFont.draw(spriteBatch, text, position.x, position.y);
        chosenFont.setColor(oldColor);
    }

    public void drawText(String text, Vector2 position, String font, EngineColor color) {
        drawText(text, position, font, color.toGdxColor());
    }

    /**
     * Draw centered text using the requested font.
     */
    public void drawTextCentered(String text, Vector2 center, String font, Color color) {
        BitmapFont chosenFont = resolveFont(font);
        glyphLayout.setText(chosenFont, text);
        drawText(text,
            new Vector2(center.x - glyphLayout.width / 2f, center.y + glyphLayout.height / 2f),
            font, color);
    }

    public void drawTextCentered(String text, Vector2 center, String font, EngineColor color) {
        drawTextCentered(text, center, font, color.toGdxColor());
    }

    /**
     * Draw text centered within a rectangle.
     */
    public void drawTextCentered(String text, Rectangle area, String font, Color color) {
        BitmapFont chosenFont = resolveFont(font);
        glyphLayout.setText(chosenFont, text);
        drawText(text,
            new Vector2(area.x + (area.width - glyphLayout.width) / 2f,
                area.y + (area.height + glyphLayout.height) / 2f),
            font, color);
    }

    public void drawTextCentered(String text, Rectangle area, String font, EngineColor color) {
        drawTextCentered(text, area, font, color.toGdxColor());
    }

    /**
     * Measure text width for layout calculations.
     */
    public float measureTextWidth(String text, String font) {
        glyphLayout.setText(resolveFont(font), text);
        return glyphLayout.width;
    }

    /**
     * Get the line height of the selected font for vertical layout.
     */
    public float getLineHeight(String font) {
        return resolveFont(font).getLineHeight();
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
        
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(filled ? ShapeRenderer.ShapeType.Filled : ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(color);
        shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        
        // Resume sprite batch if it was active
        if (batchWasActive) {
            spriteBatch.begin();
        }
    }

    public void drawRect(Rectangle rect, EngineColor color, boolean filled) {
        drawRect(rect, color.toGdxColor(), filled);
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

    public void drawCircle(Vector2 center, float radius, EngineColor color, boolean filled) {
        drawCircle(center, radius, color.toGdxColor(), filled);
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

    public void drawLine(Vector2 start, Vector2 end, EngineColor color, float thickness) {
        drawLine(start, end, color.toGdxColor(), thickness);
    }
    
    /**
     * Clean up resources
     */
    public void dispose() {
        spriteBatch.dispose();
        shapeRenderer.dispose();
        Set<BitmapFont> uniqueFonts = new HashSet<>(fonts.values());
        for (BitmapFont font : uniqueFonts) {
            font.dispose();
        }
        fonts.clear();
        
        // Dispose all cached textures
        for (Texture texture : spriteCache.values()) {
            texture.dispose();
        }
        spriteCache.clear();
    }
}
