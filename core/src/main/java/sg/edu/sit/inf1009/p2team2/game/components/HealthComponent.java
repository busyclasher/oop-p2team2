package sg.edu.sit.inf1009.p2team2.game.components;

import sg.edu.sit.inf1009.p2team2.engine.entity.ComponentAdapter;

/**
 * ECS component that tracks the player's remaining lives.
 */
public class HealthComponent implements ComponentAdapter {

    private int maxLives;
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

    /** Permanently raises the max life cap by 1 and restores 1 current life. */
    public void increaseMaxLives() {
        maxLives++;
        currentLives++;
    }
}
