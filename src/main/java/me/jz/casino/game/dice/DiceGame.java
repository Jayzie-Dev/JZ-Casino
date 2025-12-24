package me.jz.casino.game.dice;

import me.jz.casino.config.ConfigManager;
import me.jz.casino.game.CasinoGame;
import me.jz.casino.manager.CasinoManager;
import me.jz.casino.util.RandomUtil;
import org.bukkit.entity.Player;

/**
 * Dice game implementation.
 * Simple high-roll game with configurable odds and house edge.
 */
public class DiceGame extends CasinoGame {

    private final CasinoManager casinoManager;
    private final ConfigManager config;
    private DiceResult result;

    public DiceGame(Player player, double betAmount, CasinoManager casinoManager) {
        super(player, betAmount);
        this.casinoManager = casinoManager;
        this.config = casinoManager.getConfig();
    }

    @Override
    public void start() {
        // Roll dice for player and dealer
        int playerRoll = rollDice();
        int dealerRoll = rollDice();

        // Apply house edge by slightly favoring dealer on ties
        // This maintains the configured house edge percentage
        boolean isWin = calculateWin(playerRoll, dealerRoll);
        
        double payout = 0.0;
        if (isWin) {
            double winMultiplier = config.getDiceWinMultiplier();
            double houseEdge = config.getDiceHouseEdge() / 100.0;
            
            // Calculate payout with house edge applied
            payout = betAmount * winMultiplier * (1.0 - houseEdge);
        }

        this.result = new DiceResult(playerRoll, dealerRoll, isWin, payout);
    }

    @Override
    public void end() {
        if (!isActive) return;
        
        setActive(false);
        
        // Pay out winnings if any
        if (result != null && result.isWin()) {
            casinoManager.endGame(player, result.getPayout());
        } else {
            casinoManager.endGame(player, 0);
        }
    }

    @Override
    public String getGameType() {
        return "Dice Game";
    }

    /**
     * Roll a six-sided die.
     * Returns value between 1 and 6 inclusive.
     */
    private int rollDice() {
        return RandomUtil.nextInt(1, 7);
    }

    /**
     * Determine if player wins.
     * Player wins if their roll is higher than dealer.
     * Ties go to dealer (house edge).
     */
    private boolean calculateWin(int playerRoll, int dealerRoll) {
        return playerRoll > dealerRoll;
    }

    public DiceResult getResult() {
        return result;
    }
}
