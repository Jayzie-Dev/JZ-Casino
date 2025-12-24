package me.jz.casino.game.blackjack;

import org.bukkit.Material;

/**
 * Represents a playing card in Blackjack.
 */
public class BlackjackCard {

    public enum Suit {
        HEARTS("♥", "§c"),
        DIAMONDS("♦", "§c"),
        CLUBS("♣", "§8"),
        SPADES("♠", "§8");

        private final String symbol;
        private final String color;

        Suit(String symbol, String color) {
            this.symbol = symbol;
            this.color = color;
        }

        public String getSymbol() {
            return symbol;
        }

        public String getColor() {
            return color;
        }
    }

    public enum Rank {
        ACE("A", 11),
        TWO("2", 2),
        THREE("3", 3),
        FOUR("4", 4),
        FIVE("5", 5),
        SIX("6", 6),
        SEVEN("7", 7),
        EIGHT("8", 8),
        NINE("9", 9),
        TEN("10", 10),
        JACK("J", 10),
        QUEEN("Q", 10),
        KING("K", 10);

        private final String display;
        private final int value;

        Rank(String display, int value) {
            this.display = display;
            this.value = value;
        }

        public String getDisplay() {
            return display;
        }

        public int getValue() {
            return value;
        }
    }

    private final Suit suit;
    private final Rank rank;

    public BlackjackCard(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public int getValue() {
        return rank.getValue();
    }

    public boolean isAce() {
        return rank == Rank.ACE;
    }

    /**
     * Get display name with color and symbol.
     */
    public String getDisplayName() {
        return suit.getColor() + rank.getDisplay() + suit.getSymbol();
    }

    /**
     * Get material for GUI representation.
     */
    public Material getMaterial() {
        return switch (suit) {
            case HEARTS, DIAMONDS -> Material.RED_STAINED_GLASS_PANE;
            case CLUBS, SPADES -> Material.BLACK_STAINED_GLASS_PANE;
        };
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
