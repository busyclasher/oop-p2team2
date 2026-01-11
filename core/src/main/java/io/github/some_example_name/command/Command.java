package io.github.some_example_name.command;

/**
 * Interface for the Command design pattern.
 * Commands encapsulate actions that can be executed and undone.
 * This enables undo/redo functionality in the game.
 */
public interface Command {
    
    /**
     * Execute the command.
     * This performs the action encapsulated by this command.
     */
    void execute();
    
    /**
     * Undo the command.
     * This reverses the action performed by execute().
     */
    void undo();
    
    /**
     * Get a human-readable description of this command.
     * 
     * @return Description of what this command does
     */
    String getDescription();
}
