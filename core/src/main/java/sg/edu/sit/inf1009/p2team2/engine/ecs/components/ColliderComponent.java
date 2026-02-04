package sg.edu.sit.inf1009.p2team2.engine.ecs.components;

import com.badlogic.gdx.math.Rectangle;
import sg.edu.sit.inf1009.p2team2.engine.ecs.Component;

public class ColliderComponent implements Component {
    private Rectangle bounds;
    private boolean isTrigger;
    private int layer;
    private int mask;

    public ColliderComponent() {
        this.bounds = new Rectangle();
        this.isTrigger = false;
        this.layer = 0;
        this.mask = 0;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    public boolean isTrigger() {
        return isTrigger;
    }

    public void setTrigger(boolean trigger) {
        isTrigger = trigger;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public int getMask() {
        return mask;
    }

    public void setMask(int mask) {
        this.mask = mask;
    }

    public void updatePosition(float x, float y) {
        if (bounds == null) {
            bounds = new Rectangle();
        }
        bounds.setPosition(x, y);
    }
}

