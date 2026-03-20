package sg.edu.sit.inf1009.p2team2.game.components;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HealthComponentTest {

    @Test
    void constructorSetsCurrentLivesToMax() {
        HealthComponent health = new HealthComponent(3);

        assertEquals(3, health.getMaxLives());
        assertEquals(3, health.getCurrentLives());
        assertFalse(health.isDead());
    }

    @Test
    void takeDamageNeverDropsBelowZero() {
        HealthComponent health = new HealthComponent(2);

        health.takeDamage();
        health.takeDamage();
        health.takeDamage();

        assertEquals(0, health.getCurrentLives());
        assertTrue(health.isDead());
    }

    @Test
    void gainLifeIsCappedAtCurrentMaxLives() {
        HealthComponent health = new HealthComponent(3);

        health.takeDamage();
        assertEquals(2, health.getCurrentLives());

        health.gainLife();
        health.gainLife();

        assertEquals(3, health.getCurrentLives());
        assertEquals(3, health.getMaxLives());
    }

    @Test
    void increaseMaxLivesRaisesCapAndRestoresOneLife() {
        HealthComponent health = new HealthComponent(3);

        health.takeDamage();
        assertEquals(2, health.getCurrentLives());

        health.increaseMaxLives();

        assertEquals(4, health.getMaxLives());
        assertEquals(3, health.getCurrentLives());
        assertFalse(health.isDead());
    }

    @Test
    void increaseMaxLivesFromFullCreatesVisibleExtraLife() {
        HealthComponent health = new HealthComponent(3);

        health.increaseMaxLives();

        assertEquals(4, health.getMaxLives());
        assertEquals(4, health.getCurrentLives());
    }
}
