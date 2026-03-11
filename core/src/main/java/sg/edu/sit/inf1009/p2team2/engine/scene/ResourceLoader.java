package sg.edu.sit.inf1009.p2team2.engine.scene;

import java.util.HashMap;
import java.util.Map;

/**
 * RESOURCELOADER - Abstract
 * Manages asset lifecycle for a scene.  Scenes no longer deal with resource
 * management directly — they just ask the loader for assets via getAsset().
 *
 * - loaded: boolean
 * - assets: Map<String, Object>
 */
public abstract class ResourceLoader {

    private boolean loaded;
    private final Map<String, Object> assets;

    public ResourceLoader() {
        this.loaded = false;
        this.assets = new HashMap<>();
    }

    /** Load all resources needed by this scene. */
    public abstract void load();

    /** Unload / dispose all resources. */
    public abstract void unload();

    /** Whether resources have been loaded. */
    public boolean isLoaded() {
        return loaded;
    }

    /** Retrieve a previously-loaded asset by name. */
    public Object getAsset(String name) {
        return assets.get(name);
    }

    // ── helpers for subclasses ──────────────────────────────────────

    /** Mark loading as complete. Call at the end of load(). */
    protected void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    /** Store an asset so getAsset() can find it later. */
    protected void putAsset(String name, Object asset) {
        assets.put(name, asset);
    }

    /** Remove an asset. */
    protected void removeAsset(String name) {
        assets.remove(name);
    }

    /** Clear all stored assets. */
    protected void clearAssets() {
        assets.clear();
    }
}
