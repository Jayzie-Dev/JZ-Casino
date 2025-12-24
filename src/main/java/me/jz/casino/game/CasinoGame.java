package me.jz.casino.game;

import org.bukkit.entity.Player;

/**
 * Abstract base class for all casino games.
 * Defines common game lifecycle and properties.
 */
public abstract class CasinoGame {

    protected final Player player;
    protected final double betAmount;
    protected boolean isActive;

    public CasinoGame(Player player, double betAmount) {
        this.player = player;
        this.betAmount = betAmount;
        this.isActive = true;
    }

    /**
     * Start the game.
     * Called after bet is withdrawn.
     */
    public abstract void start();

    /**
     * End the game and cleanup.
     * Called when game completes or player disconnects.
     */
    public abstract void end();

    /**
     * Get game type name for display.
     */
    public abstract String getGameType();

    // ===== GETTERS =====

    public Player getPlayer() {
        return player;
    }

    public double getBetAmount() {
        return betAmount;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }
}
