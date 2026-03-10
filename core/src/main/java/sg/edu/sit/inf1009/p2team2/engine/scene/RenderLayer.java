package sg.edu.sit.inf1009.p2team2.engine.scene;

/**
 * RENDERLAYER - Interface
 * Represents a single renderable layer within a scene.
 * Layers are composable (background, entities, HUD) and ordered by z-order.
 */
public interface RenderLayer {

    /** Render this layer. */
    void render();

    /** Return the z-order used for sorting (lower = drawn first). */
    int getZOrder();

    /** Whether this layer should be drawn. */
    boolean isVisible();
}
