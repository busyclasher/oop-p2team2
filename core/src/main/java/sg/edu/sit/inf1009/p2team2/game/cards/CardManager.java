package sg.edu.sit.inf1009.p2team2.game.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Manages the player's card hand and active card effects during gameplay.
 *
 * Lifecycle:
 * 1. Cards are added to the hand via {@link #addCard(Card)}.
 * 2. The player activates a card via {@link #activateCard(int)}.
 * 3. Active effects are ticked down each frame via {@link #update(float)}.
 *
 * Integration point: GamePlayScene should call {@link #update(float)} each
 * frame and check {@link #isEffectActive(Card.CardType)} when applying
 * game logic (scoring, damage, entity speed, etc.).
 */
public class CardManager {

    private static final int MAX_HAND_SIZE = 3;

    private final List<Card>         hand;
    private final List<ActiveEffect> activeEffects;
    private final Random             random;

    public CardManager() {
        this.hand          = new ArrayList<>();
        this.activeEffects = new ArrayList<>();
        this.random        = new Random();
    }

    /** Adds a card to the player's hand if there's room. Returns true if added. */
    public boolean addCard(Card card) {
        if (card == null || hand.size() >= MAX_HAND_SIZE) return false;
        hand.add(card);
        return true;
    }

    /** Activates the card at the given hand index and removes it from hand. */
    public boolean activateCard(int index) {
        if (index < 0 || index >= hand.size()) return false;
        Card card = hand.remove(index);
        card.markUsed();
        activeEffects.add(new ActiveEffect(card.getType(), card.getDuration()));
        return true;
    }

    /** Ticks down active effect timers and removes expired ones. */
    public void update(float dt) {
        Iterator<ActiveEffect> it = activeEffects.iterator();
        while (it.hasNext()) {
            ActiveEffect effect = it.next();
            effect.remaining -= dt;
            if (effect.remaining <= 0) it.remove();
        }
    }

    /** Returns true if the given card effect is currently active. */
    public boolean isEffectActive(Card.CardType type) {
        for (ActiveEffect e : activeEffects) {
            if (e.type == type) return true;
        }
        return false;
    }

    /** Generates a random card (for drops/rewards). */
    public Card generateRandomCard() {
        Card.CardType[] types = Card.CardType.values();
        Card.CardType chosen = types[random.nextInt(types.length)];
        float duration = (chosen == Card.CardType.EXTRA_LIFE) ? 0f : 5f + random.nextFloat() * 5f;
        return new Card(chosen, duration);
    }

    public List<Card> getHand() { return Collections.unmodifiableList(hand); }
    public int getHandSize()    { return hand.size(); }
    public int getMaxHandSize() { return MAX_HAND_SIZE; }

    public void clear() {
        hand.clear();
        activeEffects.clear();
    }

    private static class ActiveEffect {
        final Card.CardType type;
        float remaining;

        ActiveEffect(Card.CardType type, float duration) {
            this.type      = type;
            this.remaining = duration;
        }
    }
}
