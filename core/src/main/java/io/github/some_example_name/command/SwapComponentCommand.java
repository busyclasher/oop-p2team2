package io.github.some_example_name.command;

import io.github.some_example_name.model.SecureAccount;
import io.github.some_example_name.model.SecurityComponent;

/**
 * Command to swap/replace a component in an account.
 * Part of the Command pattern for undo/redo functionality.
 */
public class SwapComponentCommand implements Command {
    
    private final SecureAccount account;
    private final SecurityComponent newComponent;
    private SecurityComponent oldComponent; // Store for undo
    
    /**
     * Create a command to swap a component.
     * 
     * @param account The account to modify
     * @param newComponent The new component to add (replaces existing of same type)
     */
    public SwapComponentCommand(SecureAccount account, SecurityComponent newComponent) {
        if (account == null || newComponent == null) {
            throw new IllegalArgumentException("Account and component cannot be null");
        }
        this.account = account;
        this.newComponent = newComponent;
    }
    
    @Override
    public void execute() {
        // Store the old component before swapping
        oldComponent = account.getComponent(newComponent.getType());
        account.addComponent(newComponent);
    }
    
    @Override
    public void undo() {
        // Restore the old component
        if (oldComponent != null) {
            account.addComponent(oldComponent);
        } else {
            // If there was no old component, remove the new one
            account.removeComponent(newComponent.getType());
        }
    }
    
    @Override
    public String getDescription() {
        String name = newComponent.getName();
        String value = newComponent.getValue();
        return "Change " + name + " to " + value;
    }
    
    @Override
    public String toString() {
        return getDescription();
    }
}
