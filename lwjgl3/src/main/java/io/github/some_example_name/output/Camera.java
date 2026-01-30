package io.github.some_example_name.output;


import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Camera {
    private Vector2 position;
    private float zoom;
    private float rotation;
    private float viewportWidth;
    private float viewportHeight;
    private OrthographicCamera libgdxCamera;
    
    public Camera(float viewportWidth, float viewportHeight) {
        this.position = new Vector2(0, 0);
        this.zoom = 1.0f;
        this.rotation = 0f;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        
        libgdxCamera = new OrthographicCamera(viewportWidth, viewportHeight);
        libgdxCamera.position.set(0, 0, 0);
        libgdxCamera.update();
    }
    
    public void setPosition(Vector2 position) {
        this.position.set(position);
        libgdxCamera.position.set(position.x, position.y, 0);
        libgdxCamera.update();
    }
    
    public void setPosition(float x, float y) {
        this.position.set(x, y);
        libgdxCamera.position.set(x, y, 0);
        libgdxCamera.update();
    }
    
    public Vector2 getPosition() {
        return position.cpy();
    }
    
    public void setZoom(float zoom) {
        this.zoom = zoom;
        libgdxCamera.zoom = zoom;
        libgdxCamera.update();
    }
    
    public float getZoom() {
        return zoom;
    }
    
    public void translate(Vector2 delta) {
        position.add(delta);
        libgdxCamera.translate(delta.x, delta.y);
        libgdxCamera.update();
    }
    
    public void translate(float dx, float dy) {
        position.add(dx, dy);
        libgdxCamera.translate(dx, dy);
        libgdxCamera.update();
    }
    
    public Vector2 worldToScreen(Vector2 worldPos) {
        Vector3 screenPos = libgdxCamera.project(new Vector3(worldPos.x, worldPos.y, 0));
        return new Vector2(screenPos.x, screenPos.y);
    }
    
    public Vector2 screenToWorld(Vector2 screenPos) {
        Vector3 worldPos = libgdxCamera.unproject(new Vector3(screenPos.x, screenPos.y, 0));
        return new Vector2(worldPos.x, worldPos.y);
    }
    
    public void update() {
        libgdxCamera.update();
    }
    
    public OrthographicCamera getLibGDXCamera() {
        return libgdxCamera;
    }
}
