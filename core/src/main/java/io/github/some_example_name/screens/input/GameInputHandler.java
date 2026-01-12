package io.github.some_example_name.screens.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.github.some_example_name.GameSession;
import io.github.some_example_name.model.SecurityComponent;
import io.github.some_example_name.model.SecurityComponentType;
import io.github.some_example_name.model.factory.SecurityComponentFactory;

/**
 * Handles all keyboard and mouse input for the game screen.
 * Separates input processing from rendering logic.
 * Demonstrates the Single Responsibility Principle and Delegation pattern.
 */
public class GameInputHandler {
    
    private final SecurityComponentFactory componentFactory;
    
    public GameInputHandler() {
        this.componentFactory = new SecurityComponentFactory();
    }
    
    /**
     * Process all input and update game session accordingly.
     * Returns the slot index if a component was changed (for glow effect), or -1 otherwise.
     * 
     * @param session Game session to modify
     * @return Slot index (0-4) if component changed, -1 otherwise
     */
    public int handleInput(GameSession session) {
        int changedSlot = -1;
        
        // Number keys (1-5) to add/cycle components
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            cycleComponent(session, SecurityComponentType.PASSWORD);
            changedSlot = 0;
        }
        else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            cycleComponent(session, SecurityComponentType.TWO_FA);
            changedSlot = 1;
        }
        else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            cycleComponent(session, SecurityComponentType.UPDATES);
            changedSlot = 2;
        }
        else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            cycleComponent(session, SecurityComponentType.RECOVERY);
            changedSlot = 3;
        }
        else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
            cycleComponent(session, SecurityComponentType.PRIVACY);
            changedSlot = 4;
        }
        
        // Undo/Redo (Ctrl+Z / Ctrl+Y)
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z) && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            session.undo();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Y) && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            session.redo();
        }
        
        // Submit build (Enter)
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            session.submitBuild();
        }
        
        // Restart game (R)
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            session.startGame();
        }
        
        // Cycle difficulty (D)
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            session.cycleDifficulty();
        }
        
        return changedSlot;
    }
    
    /**
     * Cycle to the next variant of a component type.
     * Uses the factory to create the next component.
     * 
     * @param session Game session
     * @param type Component type to cycle
     */
    private void cycleComponent(GameSession session, SecurityComponentType type) {
        SecurityComponent newComponent = componentFactory.createNextComponent(
            type, 
            session.getCurrentAccount()
        );
        
        if (newComponent != null) {
            session.addComponent(newComponent);
        }
    }
    
    /**
     * Get the component factory used by this input handler.
     * 
     * @return Component factory
     */
    public SecurityComponentFactory getComponentFactory() {
        return componentFactory;
    }
}
