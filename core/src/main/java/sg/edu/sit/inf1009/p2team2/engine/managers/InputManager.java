package sg.edu.sit.inf1009.p2team2.engine.managers;

import com.badlogic.gdx.math.Vector2;
import sg.edu.sit.inf1009.p2team2.engine.input.InputMap;
import sg.edu.sit.inf1009.p2team2.engine.input.Keyboard;
import sg.edu.sit.inf1009.p2team2.engine.input.Mouse;
import sg.edu.sit.inf1009.p2team2.engine.world.World;

/**
 * Centralized input manager (keyboard + mouse + action mapping).
 */
public class InputManager {
    private final Keyboard keyboard;
    private final Mouse mouse;
    private final InputMap mapper;

    private World world;
    private final EntityManager entityManager;

    public InputManager(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.keyboard = new Keyboard();
        this.mouse = new Mouse();
        this.mapper = new InputMap();
    }

    public void pollEvents() {
        // TODO(HongYih): update keyboard/mouse states from the platform input backend.
        keyboard.update();
        mouse.update();
    }

    public void processInput(float dt) {
        // TODO(HongYih): convert raw input into actions and apply to entities/world.
    }

    public boolean isActionActive(String actionName) {
        // TODO(HongYih): resolve actionName -> keyCode and check held state.
        return false;
    }

    public boolean isActionPressed(String actionName) {
        // TODO(HongYih): resolve actionName -> keyCode and check pressed state.
        return false;
    }

    public boolean isActionReleased(String actionName) {
        // TODO(HongYih): resolve actionName -> keyCode and check released state.
        return false;
    }

    public Vector2 getMousePosition() {
        return mouse.getPosition();
    }

    public boolean isMouseButtonDown() {
        // TODO(HongYih): pick a default button or overload with a button parameter.
        return false;
    }

    public void bindAction(String actionName, int keyCode) {
        mapper.bindAction(actionName, keyCode);
    }

    public void dispose() {
        // TODO(HongYih): release any platform-specific input resources.
    }
}

