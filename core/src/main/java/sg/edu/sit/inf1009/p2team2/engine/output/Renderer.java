package sg.edu.sit.inf1009.p2team2.engine.output;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Rendering façade used by scenes/systems.
 *
 * Concrete drawing (SpriteBatch, ShapeRenderer, fonts, etc.) is intentionally
 * left as TODO for the implementation phase.
 */
public class Renderer {
    private Color clearColor = Color.BLACK;
    private SpriteBatch batch;

    public Renderer() {
        this.batch = new SpriteBatch();
    }

    public void clear() {
        // TODO(HongYih): clear screen using clearColor.
    }

    public void setClearColor(Color color) {
        this.clearColor = color;
    }

    public Color getClearColor() {
        return clearColor;
    }

    public void begin() {
        // TODO(HongYih): begin frame rendering (batch begin, etc.).
        batch.begin(); // ivan added to test
    }

    public void end() {
        // TODO(HongYih): end frame rendering (batch end, etc.).
        batch.end(); // ivan added to test
    }

    public void drawSprite(String spriteId, Vector2 position, float rotation, Vector2 scale) {
        // TODO(HongYih): draw sprite with default color/tint.
    }

    public void drawSprite(String spriteId, Vector2 position, float rotation, Vector2 scale, Color color) {
        // TODO(HongYih): draw sprite with tint color.
    }

    public void drawText(String text, Vector2 position, String font, Color color) {
        // TODO(HongYih): render text with chosen font.
        
    }

    public void drawRect(Rectangle rect, Color color, boolean filled) {
        // TODO(HongYih): draw rectangle outline/filled.
    }

    public void drawCircle(Vector2 center, float radius, Color color, boolean filled) {
        // TODO(HongYih): draw circle outline/filled.
    }

    public void drawLine(Vector2 start, Vector2 end, Color color, float thickness) {
        // TODO(HongYih): draw line.
    }

    public void dispose() {
        // TODO(HongYih): dispose renderer resources (batches, fonts, textures).
    }

    public void drawTexture(Texture texture, float x, float y, float width, float height) {
     // Using LibGDX SpriteBatch logic usually found in your Renderer
     batch.draw(texture, x, y, width, height);
}
}

