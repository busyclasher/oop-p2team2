package io.github.some_example_name.command;

import io.github.some_example_name.model.SecureAccount;
import io.github.some_example_name.model.SecurityComponent;
import io.github.some_example_name.model.SecurityComponentType;

/**
 * Command to remove a component from an account.
 * Part of the Command pattern for undo/redo functionality.
 */
public class RemoveComponentCommand implements Command {
    
    private final SecureAccount account;
    private final SecurityComponentType type;
    private SecurityComponent removedComponent; // Store for undo
    
    /**
     * Create a command to remove a component.
     * 
     * @param account The account to modify
     * @param type The type of component to remove
     */
    public RemoveComponentCommand(SecureAccount account, SecurityComponentType type) {
        if (account == null || type == null) {
            throw new IllegalArgumentException("Account and type cannot be null");
        }
        this.account = account;
        this.type = type;
    }
    
    @Override
    public void execute() {
        // Store the component before removing for undo
        removedComponent = account.getComponent(type);
        account.removeComponent(type);
    }
    
    @Override
    public void undo() {
        // Re-add the component that was removed
        if (removedComponent != null) {
            account.addComponent(removedComponent);
        }
    }
    
    @Override
    public String getDescription() {
        String name = type.getDisplayName();
        return "Remove " + name;
    }
    
    @Override
    public String toString() {
        return getDescription();
    }
}
