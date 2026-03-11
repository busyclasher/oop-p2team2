package sg.edu.sit.inf1009.p2team2.engine.scene;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;

/**
 * SCENERENDERER - Abstract
 * Handles rendering for a specific scene using composable RenderLayer objects.
 *
 * # context: EngineContext
 * # renderLayers: List<RenderLayer>
 */
public abstract class SceneRenderer {

    protected final EngineContext context;
    protected final List<RenderLayer> renderLayers;
    private boolean visible;

    public SceneRenderer(EngineContext context) {
        this.context = context;
        this.renderLayers = new ArrayList<>();
        this.visible = true;
    }

    /** Render all visible layers in z-order. */
    public void render() {
        if (!visible) {
            return;
        }

        renderLayers.stream()
            .sorted(Comparator.comparingInt(RenderLayer::getZOrder))
            .filter(RenderLayer::isVisible)
            .forEach(RenderLayer::render);
    }

    /** Add a layer to this renderer. */
    public void addLayer(RenderLayer layer) {
        renderLayers.add(layer);
    }

    /** Remove a layer from this renderer. */
    public void removeLayer(RenderLayer layer) {
        renderLayers.remove(layer);
    }

    /** Set visibility on all layers at once. */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /** Whether this renderer should draw at all. */
    public boolean isVisible() {
        return visible;
    }
}
