// TRANSFORM COMPONENT - Where entity is located

package io.github.some_example_name.components;


import com.badlogic.gdx.math.Vector2;

public class TransformComponent implements Component {
    public Vector2 position;
    public float rotation;
    public Vector2 scale;
    
    public TransformComponent() {
        this.position = new Vector2(0, 0);
        this.rotation = 0f;
        this.scale = new Vector2(1, 1);
    }
    
    public TransformComponent(float x, float y) {
        this.position = new Vector2(x, y);
        this.rotation = 0f;
        this.scale = new Vector2(1, 1);
    }
    
    public void translate(Vector2 delta) {
        position.add(delta);
    }
    
    public void translate(float dx, float dy) {
        position.add(dx, dy);
    }
    
    public void setPosition(float x, float y) {
        position.set(x, y);
    }
}

