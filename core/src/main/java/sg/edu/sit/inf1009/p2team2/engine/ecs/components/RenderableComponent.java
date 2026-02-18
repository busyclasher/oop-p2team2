package sg.edu.sit.inf1009.p2team2.engine.ecs.components;

import com.badlogic.gdx.graphics.Color;
import sg.edu.sit.inf1009.p2team2.engine.ecs.ComponentAdapter;

public class RenderableComponent implements ComponentAdapter {
    private String spriteId;
    private int zIndex;
    private Color color;
    private boolean visible;

    public RenderableComponent() {
        this.spriteId = "";
        this.zIndex = 0;
        this.color = Color.WHITE.cpy();
        this.visible = true;
    }

    public String getSpriteId() {
        return spriteId;
    }

    public void setSpriteId(String spriteId) {
        this.spriteId = spriteId;
    }

    public int getZIndex() {
        return zIndex;
    }

    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isVisible() {
        return visible;
    }

    public void hide() {
        this.visible = false;
    }

    public void show() {
        this.visible = true;
    }
}
