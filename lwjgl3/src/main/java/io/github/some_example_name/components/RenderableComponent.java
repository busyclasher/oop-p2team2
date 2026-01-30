// RENDERABLE COMPONENT - What to draw

package io.github.some_example_name.components;

import com.badlogic.gdx.graphics.Color;

public class RenderableComponent implements Component {
    public String spriteId;   // ID to look up texture
    public int zIndex;        // Drawing order (higher = on top)
    public Color color;       // Tint color
    public boolean visible;   // Should render?
    
    public RenderableComponent() {
        this.spriteId = "";
        this.zIndex = 0;
        this.color = Color.WHITE.cpy();
        this.visible = true;
    }
    
    public RenderableComponent(String spriteId) {
        this.spriteId = spriteId;
        this.zIndex = 0;
        this.color = Color.WHITE.cpy();
        this.visible = true;
    }
    
    public RenderableComponent(String spriteId, int zIndex) {
        this.spriteId = spriteId;
        this.zIndex = zIndex;
        this.color = Color.WHITE.cpy();
        this.visible = true;
    }
    
    public void hide() {
        visible = false;
    }
    
    public void show() {
        visible = true;
    }
    
    public void setColor(float r, float g, float b, float a) {
        color.set(r, g, b, a);
    }
}