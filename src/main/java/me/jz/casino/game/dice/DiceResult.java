package me.jz.casino.game.dice;

/**
 * Represents the result of a dice game.
 */
public class DiceResult {

    private final int playerRoll;
    private final int dealerRoll;
    private final boolean isWin;
    private final double payout;

    public DiceResult(int playerRoll, int dealerRoll, boolean isWin, double payout) {
        this.playerRoll = playerRoll;
        this.dealerRoll = dealerRoll;
        this.isWin = isWin;
        this.payout = payout;
    }

    public int getPlayerRoll() {
        return playerRoll;
    }

    public int getDealerRoll() {
        return dealerRoll;
    }

    public boolean isWin() {
        return isWin;
    }

    public double getPayout() {
        return payout;
    }

    public boolean isTie() {
        return playerRoll == dealerRoll;
    }
}
