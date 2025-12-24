package me.jz.casino.game.slot;

/**
 * Represents a slot machine symbol with weight and multiplier.
 * Weight determines probability, multiplier determines payout.
 */
public class SlotSymbol {

    private final String name;
    private final int weight;
    private final double multiplier;
    private final String displayName;

    public SlotSymbol(String name, int weight, double multiplier, String displayName) {
        this.name = name;
        this.weight = weight;
        this.multiplier = multiplier;
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
