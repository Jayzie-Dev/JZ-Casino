package me.jz.casino.game.blackjack;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a hand of cards in Blackjack.
 * Handles card management and value calculation with Ace logic.
 */
public class BlackjackHand {

    private final List<BlackjackCard> cards;

    public BlackjackHand() {
        this.cards = new ArrayList<>();
    }

    /**
     * Add card to hand.
     */
    public void addCard(BlackjackCard card) {
        cards.add(card);
    }

    /**
     * Get all cards in hand.
     */
    public List<BlackjackCard> getCards() {
        return new ArrayList<>(cards);
    }

    /**
     * Get number of cards in hand.
     */
    public int getCardCount() {
        return cards.size();
    }

    /**
     * Calculate best hand value.
     * Aces count as 11 or 1 to avoid busting.
     */
    public int getValue() {
        int value = 0;
        int aceCount = 0;

        // Calculate base value
        for (BlackjackCard card : cards) {
            value += card.getValue();
            if (card.isAce()) {
                aceCount++;
            }
        }

        // Adjust Aces from 11 to 1 if busting
        while (value > 21 && aceCount > 0) {
            value -= 10; // Convert one Ace from 11 to 1
            aceCount--;
        }

        return value;
    }

    /**
     * Check if hand is bust (over 21).
     */
    public boolean isBust() {
        return getValue() > 21;
    }

    /**
     * Check if hand is blackjack (21 with 2 cards).
     */
    public boolean isBlackjack() {
        return cards.size() == 2 && getValue() == 21;
    }

    /**
     * Check if hand can hit (not bust).
     */
    public boolean canHit() {
        return !isBust() && getValue() < 21;
    }

    /**
     * Get display string of all cards.
     */
    public String getDisplayString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cards.size(); i++) {
            if (i > 0) sb.append(" ");
            sb.append(cards.get(i).getDisplayName());
        }
        return sb.toString();
    }

    /**
     * Clear all cards from hand.
     */
    public void clear() {
        cards.clear();
    }
}
