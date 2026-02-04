package sg.edu.sit.inf1009.p2team2.engine.scenes;

import java.util.ArrayList;
import java.util.List;

import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.ui.Button;

public class MenuScene extends Scene {
    private final List<Button> buttons = new ArrayList<>();
    private int selectedIndex = 0;

    public MenuScene(EngineContext context) {
        super(context);
    }

    @Override
    public void onEnter() {
        // TODO(Ivan): scene enter hook (e.g., reset selection).
    }

    @Override
    public void onExit() {
        // TODO(Ivan): scene exit hook.
    }

    @Override
    public void load() {
        // TODO(Ivan): build menu buttons and load resources.
    }

    @Override
    public void unload() {
        // TODO(Ivan): unload menu resources.
    }

    @Override
    public void update() {
        // TODO(Ivan): update selection and menu logic.
    }

    @Override
    public void render() {
        // TODO(Ivan): draw menu using Renderer.
    }

    private void handleButtonClick() {
        // TODO(Ivan): execute action for the currently selected button.
    }
}

