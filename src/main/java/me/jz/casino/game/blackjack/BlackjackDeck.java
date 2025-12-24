package me.jz.casino.game.blackjack;

import me.jz.casino.util.RandomUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a deck of cards for Blackjack.
 * Handles shuffling and dealing.
 */
public class BlackjackDeck {

    private final List<BlackjackCard> cards;

    public BlackjackDeck() {
        this.cards = new ArrayList<>();
        reset();
    }

    /**
     * Reset deck with all 52 cards and shuffle.
     */
    public void reset() {
        cards.clear();

        // Add all cards
        for (BlackjackCard.Suit suit : BlackjackCard.Suit.values()) {
            for (BlackjackCard.Rank rank : BlackjackCard.Rank.values()) {
                cards.add(new BlackjackCard(suit, rank));
            }
        }

        shuffle();
    }

    /**
     * Shuffle the deck using Fisher-Yates algorithm.
     */
    public void shuffle() {
        for (int i = cards.size() - 1; i > 0; i--) {
            int j = RandomUtil.nextInt(i + 1);
            // Swap
            BlackjackCard temp = cards.get(i);
            cards.set(i, cards.get(j));
            cards.set(j, temp);
        }
    }

    /**
     * Deal one card from the deck.
     */
    public BlackjackCard deal() {
        if (cards.isEmpty()) {
            reset(); // Auto-reshuffle if deck is empty
        }
        return cards.remove(cards.size() - 1);
    }

    /**
     * Get remaining cards in deck.
     */
    public int getRemainingCards() {
        return cards.size();
    }
}
