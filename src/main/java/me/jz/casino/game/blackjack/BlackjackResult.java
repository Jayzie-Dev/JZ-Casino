package me.jz.casino.game.blackjack;

/**
 * Represents the result of a Blackjack game.
 */
public class BlackjackResult {

    public enum Outcome {
        PLAYER_BLACKJACK,  // Player got blackjack (21 with 2 cards)
        PLAYER_WIN,        // Player won (higher value or dealer bust)
        DEALER_WIN,        // Dealer won (higher value or player bust)
        PUSH               // Tie (same value)
    }

    private final Outcome outcome;
    private final int playerValue;
    private final int dealerValue;
    private final double payout;

    public BlackjackResult(Outcome outcome, int playerValue, int dealerValue, double payout) {
        this.outcome = outcome;
        this.playerValue = playerValue;
        this.dealerValue = dealerValue;
        this.payout = payout;
    }

    public Outcome getOutcome() {
        return outcome;
    }

    public int getPlayerValue() {
        return playerValue;
    }

    public int getDealerValue() {
        return dealerValue;
    }

    public double getPayout() {
        return payout;
    }

    public boolean isWin() {
        return outcome == Outcome.PLAYER_BLACKJACK || outcome == Outcome.PLAYER_WIN;
    }

    public boolean isPush() {
        return outcome == Outcome.PUSH;
    }
}
