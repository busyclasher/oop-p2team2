package io.github.some_example_name;

import com.badlogic.gdx.Game;
import io.github.some_example_name.screens.GameScreen;

/**
 * Main game class using libGDX Game pattern for screen management.
 * Launches into the GameScreen for the cybersecurity education game.
 */
public class Main extends Game {
    
    @Override
    public void create() {
        // Launch directly into the game screen
        setScreen(new GameScreen(this));
    }
}
