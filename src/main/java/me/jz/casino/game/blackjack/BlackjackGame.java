package me.jz.casino.game.blackjack;

import me.jz.casino.config.ConfigManager;
import me.jz.casino.game.CasinoGame;
import me.jz.casino.manager.CasinoManager;
import org.bukkit.entity.Player;

/**
 * Blackjack game implementation with lite rules.
 * - Dealer stands on 17
 * - No split
 * - No insurance
 * - Blackjack pays 1.5x
 */
public class BlackjackGame extends CasinoGame {

    private final CasinoManager casinoManager;
    private final ConfigManager config;
    private final BlackjackDeck deck;
    private final BlackjackHand playerHand;
    private final BlackjackHand dealerHand;
    private BlackjackResult result;
    private boolean playerTurnComplete;

    public BlackjackGame(Player player, double betAmount, CasinoManager casinoManager) {
        super(player, betAmount);
        this.casinoManager = casinoManager;
        this.config = casinoManager.getConfig();
        this.deck = new BlackjackDeck();
        this.playerHand = new BlackjackHand();
        this.dealerHand = new BlackjackHand();
        this.playerTurnComplete = false;
    }

    @Override
    public void start() {
        // Deal initial cards
        playerHand.addCard(deck.deal());
        dealerHand.addCard(deck.deal());
        playerHand.addCard(deck.deal());
        dealerHand.addCard(deck.deal());

        // Check for immediate blackjack
        if (playerHand.isBlackjack()) {
            playerTurnComplete = true;
            finishGame();
        }
    }

    /**
     * Player hits (takes another card).
     * @return The card that was dealt, or null if hit was not allowed
     */
    public BlackjackCard hit() {
        if (playerTurnComplete || !playerHand.canHit()) {
            return null;
        }

        BlackjackCard newCard = deck.deal();
        playerHand.addCard(newCard);

        // Check if bust or 21
        if (playerHand.isBust() || playerHand.getValue() == 21) {
            stand(); // Auto-stand
        }
        
        return newCard;
    }

    /**
     * Player stands (ends their turn).
     */
    public void stand() {
        if (playerTurnComplete) {
            return;
        }

        playerTurnComplete = true;
        playDealerTurn();
        finishGame();
    }

    /**
     * Play dealer's turn according to rules.
     * Dealer must hit until reaching dealer stand value.
     */
    private void playDealerTurn() {
        int dealerStand = config.getBlackjackDealerStand();

        // Dealer hits until reaching stand value
        while (dealerHand.getValue() < dealerStand && !dealerHand.isBust()) {
            dealerHand.addCard(deck.deal());
        }
    }

    /**
     * Finish game and calculate result.
     */
    private void finishGame() {
        int playerValue = playerHand.getValue();
        int dealerValue = dealerHand.getValue();

        // Determine outcome
        BlackjackResult.Outcome outcome;
        double payout = 0.0;

        if (playerHand.isBust()) {
            // Player bust = dealer wins
            outcome = BlackjackResult.Outcome.DEALER_WIN;
        } else if (playerHand.isBlackjack() && !dealerHand.isBlackjack()) {
            // Player blackjack = special payout
            outcome = BlackjackResult.Outcome.PLAYER_BLACKJACK;
            double blackjackMultiplier = config.getBlackjackMultiplier();
            double houseEdge = config.getBlackjackHouseEdge() / 100.0;
            payout = betAmount * (1 + blackjackMultiplier) * (1.0 - houseEdge);
        } else if (dealerHand.isBust()) {
            // Dealer bust = player wins
            outcome = BlackjackResult.Outcome.PLAYER_WIN;
            double winMultiplier = config.getBlackjackWinMultiplier();
            double houseEdge = config.getBlackjackHouseEdge() / 100.0;
            payout = betAmount * (1 + winMultiplier) * (1.0 - houseEdge);
        } else if (playerValue > dealerValue) {
            // Player higher = player wins
            outcome = BlackjackResult.Outcome.PLAYER_WIN;
            double winMultiplier = config.getBlackjackWinMultiplier();
            double houseEdge = config.getBlackjackHouseEdge() / 100.0;
            payout = betAmount * (1 + winMultiplier) * (1.0 - houseEdge);
        } else if (dealerValue > playerValue) {
            // Dealer higher = dealer wins
            outcome = BlackjackResult.Outcome.DEALER_WIN;
        } else {
            // Same value = push (tie)
            outcome = BlackjackResult.Outcome.PUSH;
            payout = betAmount; // Return bet
        }

        this.result = new BlackjackResult(outcome, playerValue, dealerValue, payout);
    }

    @Override
    public void end() {
        if (!isActive) return;

        setActive(false);

        // Pay out winnings if any
        if (result != null && result.getPayout() > 0) {
            casinoManager.endGame(player, result.getPayout());
        } else {
            casinoManager.endGame(player, 0);
        }
    }

    @Override
    public String getGameType() {
        return "Blackjack";
    }

    // ===== GETTERS =====

    public BlackjackHand getPlayerHand() {
        return playerHand;
    }

    public BlackjackHand getDealerHand() {
        return dealerHand;
    }

    public BlackjackResult getResult() {
        return result;
    }

    public boolean isPlayerTurnComplete() {
        return playerTurnComplete;
    }

    public boolean canHit() {
        return !playerTurnComplete && playerHand.canHit();
    }

    public boolean canStand() {
        return !playerTurnComplete;
    }
}
