package io.github.some_example_name.command;

import java.util.Stack;

/**
 * Manages command execution history for undo/redo functionality.
 * Implements the Memento/Command pattern for state management.
 */
public class CommandHistory {
    
    private final Stack<Command> undoStack;
    private final Stack<Command> redoStack;
    private final int maxHistorySize;
    
    /**
     * Create a command history with default max size (50 commands).
     */
    public CommandHistory() {
        this(50);
    }
    
    /**
     * Create a command history with specified max size.
     * 
     * @param maxHistorySize Maximum number of commands to remember
     */
    public CommandHistory(int maxHistorySize) {
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
        this.maxHistorySize = maxHistorySize;
    }
    
    /**
     * Execute a command and add it to history.
     * This clears the redo stack since we're on a new path.
     * 
     * @param command Command to execute
     */
    public void executeCommand(Command command) {
        if (command == null) {
            return;
        }
        
        command.execute();
        undoStack.push(command);
        redoStack.clear(); // Clear redo history on new action
        
        // Limit history size
        if (undoStack.size() > maxHistorySize) {
            undoStack.remove(0); // Remove oldest command
        }
    }
    
    /**
     * Undo the last command.
     * Moves the command from undo stack to redo stack.
     * 
     * @return true if undo was performed, false if nothing to undo
     */
    public boolean undo() {
        if (undoStack.isEmpty()) {
            return false;
        }
        
        Command command = undoStack.pop();
        command.undo();
        redoStack.push(command);
        return true;
    }
    
    /**
     * Redo the last undone command.
     * Moves the command from redo stack back to undo stack.
     * 
     * @return true if redo was performed, false if nothing to redo
     */
    public boolean redo() {
        if (redoStack.isEmpty()) {
            return false;
        }
        
        Command command = redoStack.pop();
        command.execute();
        undoStack.push(command);
        return true;
    }
    
    /**
     * Check if undo is available.
     * 
     * @return true if there are commands to undo
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }
    
    /**
     * Check if redo is available.
     * 
     * @return true if there are commands to redo
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
    
    /**
     * Get description of the next command that would be undone.
     * 
     * @return Description, or empty string if nothing to undo
     */
    public String getUndoDescription() {
        if (undoStack.isEmpty()) {
            return "";
        }
        return undoStack.peek().getDescription();
    }
    
    /**
     * Get description of the next command that would be redone.
     * 
     * @return Description, or empty string if nothing to redo
     */
    public String getRedoDescription() {
        if (redoStack.isEmpty()) {
            return "";
        }
        return redoStack.peek().getDescription();
    }
    
    /**
     * Clear all history.
     */
    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
    
    /**
     * Get the number of commands that can be undone.
     * 
     * @return Undo stack size
     */
    public int getUndoCount() {
        return undoStack.size();
    }
    
    /**
     * Get the number of commands that can be redone.
     * 
     * @return Redo stack size
     */
    public int getRedoCount() {
        return redoStack.size();
    }
}
