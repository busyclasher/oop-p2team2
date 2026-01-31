package io.github.some_example_name.output;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Renderer {
    private Color clearColor;
    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    
    public Renderer() {
        clearColor = new Color(0.1f, 0.1f, 0.1f, 1f);
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
    }
    
    public void clear() {
        Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
    
    public void setClearColor(Color color) {
        this.clearColor.set(color);
    }
    
    public void begin() {
        spriteBatch.begin();
    }
    
    public void end() {
        spriteBatch.end();
    }
    
    // Draw sprite at screen coordinates
    public void drawSprite(String spriteId, Vector2 position, float rotation, Vector2 scale) {
        // TODO: Get texture from AssetManager
        // Texture texture = assetManager.getTexture(spriteId);
        // spriteBatch.draw(texture, position.x, position.y, ...);
    }
    
    public void drawSprite(String spriteId, Vector2 position, float rotation, Vector2 scale, Color color) {
        // TODO: Same with color tint
    }
    
    public void drawText(String text, Vector2 position, String font, Color color) {
        // TODO: Implement with BitmapFont
    }
    
    public void drawRect(Rectangle rect, Color color, boolean filled) {
        spriteBatch.end();
        
        if (filled) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        } else {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        }
        
        shapeRenderer.setColor(color);
        shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        shapeRenderer.end();
        
        spriteBatch.begin();
    }
    
    public void drawCircle(Vector2 center, float radius, Color color, boolean filled) {
        spriteBatch.end();
        
        if (filled) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        } else {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        }
        
        shapeRenderer.setColor(color);
        shapeRenderer.circle(center.x, center.y, radius);
        shapeRenderer.end();
        
        spriteBatch.begin();
    }
    
    public void drawLine(Vector2 start, Vector2 end, Color color, float thickness) {
        spriteBatch.end();
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.rectLine(start, end, thickness);
        shapeRenderer.end();
        
        spriteBatch.begin();
    }
    
    public void dispose() {
        spriteBatch.dispose();
        shapeRenderer.dispose();
    }
}
