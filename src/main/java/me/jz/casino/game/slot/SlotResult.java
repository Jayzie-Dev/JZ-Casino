package me.jz.casino.game.slot;

/**
 * Represents the result of a slot machine spin.
 * Contains the three symbols and calculated payout.
 */
public class SlotResult {

    private final SlotSymbol[] symbols;
    private final double payout;
    private final boolean isWin;

    public SlotResult(SlotSymbol[] symbols, double payout, boolean isWin) {
        this.symbols = symbols;
        this.payout = payout;
        this.isWin = isWin;
    }

    public SlotSymbol[] getSymbols() {
        return symbols;
    }

    public SlotSymbol getSymbol(int index) {
        return symbols[index];
    }

    public double getPayout() {
        return payout;
    }

    public boolean isWin() {
        return isWin;
    }

    /**
     * Check if all three symbols match.
     */
    public boolean isJackpot() {
        return symbols[0].getName().equals(symbols[1].getName()) 
            && symbols[1].getName().equals(symbols[2].getName());
    }
}
