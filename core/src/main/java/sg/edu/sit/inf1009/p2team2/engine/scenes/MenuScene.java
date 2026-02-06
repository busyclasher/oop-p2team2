package sg.edu.sit.inf1009.p2team2.engine.scenes;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.managers.SceneManager;
import sg.edu.sit.inf1009.p2team2.engine.ui.Button;

public class MenuScene extends Scene {
    private Texture backgroundTexture; // Class-level variable for the image
    private final List<Button> buttons = new ArrayList<>();
    private int selectedIndex = 0;

    public MenuScene(EngineContext context, SceneManager sceneManager) {
        super(context);
    }

    @Override
    public void onEnter() {
        // TODO(Ivan): scene enter hook (e.g., reset selection).
        // Reset selection index whenever the menu is revisited
        selectedIndex = 0;
        System.out.println("[MenuScene] Selection reset.");
    }

    @Override
    public void onExit() {
        // TODO(Ivan): scene exit hook.
        System.out.println("[MenuScene] Exiting menu scene.");
    }

    @Override
    public void load() {
        // TODO(Ivan): build menu buttons and load resources.
        // Build menu buttons and define their positions
        // Load the image from your assets folder
        // Ensure the file exists at: assets/background_menu.png
        backgroundTexture = new Texture("background_menu.png");
        
        // Load buttons as before
        buttons.add(new Button("Start Game", new Vector2(400, 200)));
        System.out.println("[MenuScene] Loading menu resources...");
        buttons.clear();
       // buttons.add(new Button("Start Game", new Vector2(400, 200)));
       // buttons.add(new Button("Settings", new Vector2(400, 300)));
       // buttons.add(new Button("Leaderboard", new Vector2(400, 400)));
       // buttons.add(new Button("Exit", new Vector2(400, 500)));
    }

    @Override
    public void unload() {
        // TODO(Ivan): unload menu resources.
        System.out.println("[MenuScene] Unloaded menu resources.");
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        buttons.clear();
    }
    

    @Override
    public void update() {
        // TODO(Ivan): update selection and menu logic.
        // Update selection logic based on input
        var input = context.getInputManager();
        
        if (input.isActionPressed("menu_up")) {
            selectedIndex = (selectedIndex - 1 + buttons.size()) % buttons.size();
        }
        
        if (input.isActionPressed("menu_down")) {
            selectedIndex = (selectedIndex + 1) % buttons.size();
        }

        if (input.isActionPressed("menu_select")) {
            handleButtonClick();
        }
    }

    @Override
    public void render() {
        // TODO(Ivan): draw menu using Renderer.
        // Use the Renderer to draw the menu and title
        var renderer = context.getOutputManager().getRenderer();
    
        // 1. Open the batch for drawing
        renderer.begin(); 

        // 2. Draw the background texture
        if (backgroundTexture != null) {
            renderer.drawTexture(backgroundTexture, 0, 0, 800, 600); 
        }

         // 3. Draw buttons and other UI elements
         for (Button b : buttons) {
             b.render(renderer, false);
        }

        // 4. Close the batch and send everything to the GPU
        renderer.end(); 
}

    private void handleButtonClick() {
        // TODO(Ivan): execute action for the currently selected button.
        String action = buttons.get(selectedIndex).getLabel();
        var sceneManager = context.getSceneManager();

        switch (action) {
            case "Start Game":
                System.out.println("[MenuScene] Starting Game...");
                // sceneManager.set(new GameplayScene(context)); 
                break;
            case "Settings":
                // Push SettingsScene onto stack
                sceneManager.push(new SettingsScene(context));
                break;
            case "Leaderboard":
                // Push LeadershipBoardScene onto stack
                sceneManager.push(new LeadershipBoardScene(context));
                break;
            case "Exit":
                System.exit(0);
                break;
        }
    }
}

