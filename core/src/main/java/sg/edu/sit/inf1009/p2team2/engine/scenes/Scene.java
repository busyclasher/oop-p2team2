package sg.edu.sit.inf1009.p2team2.engine.scenes;

import sg.edu.sit.inf1009.p2team2.engine.collision.CollisionManager;
import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.managers.EntityManager;
//import sg.edu.sit.inf1009.p2team2.engine.managers.CollisionManager;
import sg.edu.sit.inf1009.p2team2.engine.managers.MovementManager;

/**
 * Base class for all scenes (screens/states) in the engine.
 */
public abstract class Scene {
    protected final EngineContext context;
    protected EntityManager entityManager;
    protected MovementManager movementManager;

    protected Scene(EngineContext context) {
        this.context = context;
    }

    public void onEnter() {
        // Optional hook.
        // Default implementation - can be overridden by child classes
        // Optional hook called by the SceneManager before this scene is paused or removed.
    }

    public void onExit() {
        // Optional hook.
        // Default implementation - can be overridden by child classes
    }

    public abstract void load(); 
        // TODO(Ivan): load assets/resources required by this scene.
        // Load assets/resources required by this scene.
        // Called once when the scene is first pushed to the stack.

    public abstract void unload(); 
        // TODO(Ivan): unload assets/resources required by this scene.
        // Unload assets/resources required by this scene to free memory.
        // Called when the scene is permanently removed from the stack.

    public abstract void update(float dt); 
        // TODO(Ivan): update scene state.
        // Update the scene state (logic, animations, physics).
        // Typically called once per frame by the SceneManager.

    public abstract void render();
        // TODO(Ivan): render scene using OutputManager/Renderer.
        // Render the scene using the Renderer accessible via EngineContext.
        
    public void handleInput() {
        // TODO(Ivan): route user input to scene controls.
    }

    public EngineContext getContext() {
        return context;
    }
    /**
     * Get the entity manager for this scene
     * Creates it if it doesn't exist
     * 
     * @return Entity manager
     */
    public EntityManager getEntityManager() {
        if (entityManager == null) {
            entityManager = new EntityManager();
        }
        return entityManager;
    }
    /**
     * Get the collision manager for this scene
     * Creates it if it doesn't exist
     * 
     * @return Collision manager
     */
    //public CollisionManager getCollisionManager() {
    //    if (collisionManager == null) {
    //        collisionManager = new CollisionManager(getEntityManager());
    //    }
    //    return collisionManager;
    //}
    /**
     * Get the movement manager for this scene
     * Creates it if it doesn't exist
     * 
     * @return Movement manager
     */
    public MovementManager getMovementManager() {
        if (movementManager == null) {
            movementManager = new MovementManager(getEntityManager());
        }
        return movementManager;
    }
}

