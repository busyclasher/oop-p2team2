package sg.edu.sit.inf1009.p2team2.engine.scenes;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import sg.edu.sit.inf1009.p2team2.engine.core.EngineContext;
import sg.edu.sit.inf1009.p2team2.engine.input.Keyboard;
import sg.edu.sit.inf1009.p2team2.engine.input.Mouse;
import sg.edu.sit.inf1009.p2team2.engine.output.Renderer;

import java.util.ArrayList;
import java.util.List;

/**
 * MENU SCENE - ULTIMATE FIX
 * 
 * Solution: Disable mouse hover for 10 frames after keyboard use
 * This prevents mouse from overriding keyboard navigation
 */
public class MenuScene extends Scene {
    
    private List<MenuItem> menuItems;
    private int selectedIndex;
    private static final String BACKGROUND_SPRITE = "background_menu.png";
    
    // Disable mouse hover temporarily after keyboard use
    private int keyboardCooldown = 0;
    private static final int COOLDOWN_FRAMES = 10;
    
    public MenuScene(EngineContext context) {
        super(context);
        this.menuItems = new ArrayList<>();
        this.selectedIndex = 0;
    }
    
    @Override
    public void onEnter() {
        System.out.println("[MenuScene] Entered menu scene");
        selectedIndex = 0;
        keyboardCooldown = 0;
    }
    
    @Override
    public void onExit() {
        System.out.println("[MenuScene] Exiting menu scene");
    }
    
    @Override
    public void load() {
        System.out.println("[MenuScene] Loading menu resources...");
        
        menuItems.clear();
        
        float centerX = 400;
        float startY = 400;
        float spacing = 70;
        
        menuItems.add(new MenuItem("Start Game", new Vector2(centerX, startY)));
        menuItems.add(new MenuItem("Settings", new Vector2(centerX, startY - spacing)));
        menuItems.add(new MenuItem("Leaderboard", new Vector2(centerX, startY - spacing * 2)));
        menuItems.add(new MenuItem("Exit", new Vector2(centerX, startY - spacing * 3)));
        
        System.out.println("[MenuScene] Loaded " + menuItems.size() + " menu items");
    }
    
    @Override
    public void unload() {
        System.out.println("[MenuScene] Unloading menu resources");
        menuItems.clear();
    }
    
    @Override
    public void update(float dt) {
        
        // Decrease cooldown
        if (keyboardCooldown > 0) {
            keyboardCooldown--;
        }
    }
    
    @Override
    public void handleInput() {
        Keyboard keyboard = context.getInputManager().getKeyboard();
        Mouse mouse = context.getInputManager().getMouse();
        
        // KEYBOARD NAVIGATION (always works)
        if (keyboard.isKeyPressed(Input.Keys.UP)) {
            selectedIndex = (selectedIndex - 1 + menuItems.size()) % menuItems.size();
            keyboardCooldown = COOLDOWN_FRAMES;  // Disable mouse for 10 frames
            System.out.println("[MenuScene] UP → " + menuItems.get(selectedIndex).text);
        }
        else if (keyboard.isKeyPressed(Input.Keys.W)) {
            selectedIndex = (selectedIndex - 1 + menuItems.size()) % menuItems.size();
            keyboardCooldown = COOLDOWN_FRAMES;
            System.out.println("[MenuScene] W → " + menuItems.get(selectedIndex).text);
        }
        else if (keyboard.isKeyPressed(Input.Keys.DOWN)) {
            selectedIndex = (selectedIndex + 1) % menuItems.size();
            keyboardCooldown = COOLDOWN_FRAMES;
            System.out.println("[MenuScene] DOWN → " + menuItems.get(selectedIndex).text);
        }
        else if (keyboard.isKeyPressed(Input.Keys.S)) {
            selectedIndex = (selectedIndex + 1) % menuItems.size();
            keyboardCooldown = COOLDOWN_FRAMES;
            System.out.println("[MenuScene] S → " + menuItems.get(selectedIndex).text);
        }
        
        // SELECTION
        if (keyboard.isKeyPressed(Input.Keys.ENTER) || keyboard.isKeyPressed(Input.Keys.SPACE)) {
            activateMenuItem(selectedIndex);
        }
        
        // MOUSE HOVER - Only if keyboard cooldown expired
        if (keyboardCooldown == 0) {
            Vector2 mousePos = mouse.getPosition();
            for (int i = 0; i < menuItems.size(); i++) {
                MenuItem item = menuItems.get(i);
                if (item.contains(mousePos)) {
                    if (selectedIndex != i) {
                        selectedIndex = i;
                        System.out.println("[MenuScene] Mouse hover → " + menuItems.get(selectedIndex).text);
                    }
                    break;
                }
            }
        }
        
        // MOUSE CLICK - Always works immediately
        if (mouse.isButtonPressed(0)) {
            Vector2 mousePos = mouse.getPosition();
            for (int i = 0; i < menuItems.size(); i++) {
                if (menuItems.get(i).contains(mousePos)) {
                    selectedIndex = i;
                    activateMenuItem(i);
                    keyboardCooldown = 0;  // Reset cooldown on click
                    break;
                }
            }
        }
    }
    
    @Override
    public void render() {
        Renderer renderer = context.getOutputManager().getRenderer();
        
        renderer.clear();
        renderer.begin();
        
        // Draw background
        Vector2 screenCenter = new Vector2(400, 300);
        renderer.drawSprite(BACKGROUND_SPRITE, screenCenter, 0f, new Vector2(1, 1));
        
        // Draw title
        renderer.drawText(
            "MAIN MENU",
            new Vector2(320, 550),
            "default",
            Color.WHITE
        );
        
        // Draw menu items
        for (int i = 0; i < menuItems.size(); i++) {
            boolean isSelected = (i == selectedIndex);
            menuItems.get(i).render(renderer, isSelected);
        }
        
        // Draw controls hint
        renderer.drawText(
            "Arrow Keys / WASD / Mouse to navigate | Enter / Click to select",
            new Vector2(120, 50),
            "default",
            new Color(0.7f, 0.7f, 0.7f, 1f)
        );
        
        renderer.end();
    }
    
    private void activateMenuItem(int index) {
        if (index < 0 || index >= menuItems.size()) {
            return;
        }
        
        String action = menuItems.get(index).text;
        System.out.println("[MenuScene] Activated: " + action);
        
        switch (action) {
            case "Start Game":
                System.out.println("[MenuScene] Starting game...");
                // TODO: context.getSceneManager().push(new MainScene(context));
                break;
                
            case "Settings":
                System.out.println("[MenuScene] Opening settings...");
                // TODO: context.getSceneManager().push(new SettingsScene(context));
                break;
                
            case "Leaderboard":
                System.out.println("[MenuScene] Opening leaderboard...");
                // TODO: context.getSceneManager().push(new LeaderboardScene(context));
                break;
                
            case "Exit":
                System.out.println("[MenuScene] Exiting...");
                context.stop();
                break;
        }
    }
    
    private static class MenuItem {
        String text;
        Vector2 position;
        float width;
        float height;
        
        MenuItem(String text, Vector2 position) {
            this.text = text;
            this.position = position;
            this.width = 200;
            this.height = 50;
        }
        
        boolean contains(Vector2 point) {
            float left = position.x - width / 2;
            float right = position.x + width / 2;
            float bottom = position.y - height / 2;
            float top = position.y + height / 2;
            
            return point.x >= left && point.x <= right && 
                   point.y >= bottom && point.y <= top;
        }
        
        void render(Renderer renderer, boolean isSelected) {
            Color textColor = isSelected ? Color.YELLOW : Color.WHITE;
            Color bgColor = isSelected ? 
                new Color(0.4f, 0.4f, 0.4f, 0.8f) : 
                new Color(0.2f, 0.2f, 0.2f, 0.6f);
            
            float left = position.x - width / 2;
            float bottom = position.y - height / 2;
            
            com.badlogic.gdx.math.Rectangle bounds = new com.badlogic.gdx.math.Rectangle(
                left, bottom, width, height
            );
            
            renderer.drawRect(bounds, bgColor, true);
            renderer.drawRect(bounds, Color.WHITE, false);
            
            float textX = position.x - 60;
            float textY = position.y + 5;
            
            renderer.drawText(text, new Vector2(textX, textY), "default", textColor);
        }
    }
}