package io.github.some_example_name.command;

import io.github.some_example_name.model.SecureAccount;
import io.github.some_example_name.model.SecurityComponent;

/**
 * Command to add a component to an account.
 * Part of the Command pattern for undo/redo functionality.
 */
public class AddComponentCommand implements Command {
    
    private final SecureAccount account;
    private final SecurityComponent component;
    
    /**
     * Create a command to add a component.
     * 
     * @param account The account to modify
     * @param component The component to add
     */
    public AddComponentCommand(SecureAccount account, SecurityComponent component) {
        if (account == null || component == null) {
            throw new IllegalArgumentException("Account and component cannot be null");
        }
        this.account = account;
        this.component = component;
    }
    
    @Override
    public void execute() {
        account.addComponent(component);
    }
    
    @Override
    public void undo() {
        account.removeComponent(component.getType());
    }
    
    @Override
    public String getDescription() {
        return "Add " + component.getName() + ": " + component.getValue();
    }
    
    @Override
    public String toString() {
        return getDescription();
    }
}
