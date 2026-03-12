package sg.edu.sit.inf1009.p2team2.game.components;

import sg.edu.sit.inf1009.p2team2.engine.entity.ComponentAdapter;

/**
 * ECS component that tracks the player's remaining lives.
 */
public class HealthComponent implements ComponentAdapter {

    private final int maxLives;
    private int currentLives;

    public HealthComponent(int maxLives) {
        this.maxLives     = maxLives;
        this.currentLives = maxLives;
    }

    public int  getMaxLives()     { return maxLives; }
    public int  getCurrentLives() { return currentLives; }
    public boolean isDead()       { return currentLives <= 0; }

    public void takeDamage() {
        if (currentLives > 0) {
            currentLives--;
        }
    }

    public void gainLife() {
        if (currentLives < maxLives) {
            currentLives++;
        }
    }
}
