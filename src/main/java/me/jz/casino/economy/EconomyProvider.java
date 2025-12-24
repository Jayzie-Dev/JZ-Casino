package me.jz.casino.economy;

import org.bukkit.entity.Player;

/**
 * Abstract economy provider interface.
 * Allows for potential future economy system support beyond Vault.
 */
public interface EconomyProvider {

    /**
     * Check if player has sufficient balance.
     */
    boolean has(Player player, double amount);

    /**
     * Get player's current balance.
     */
    double getBalance(Player player);

    /**
     * Withdraw money from player's account.
     * Should be called synchronously before game starts.
     * 
     * @return true if withdrawal successful
     */
    boolean withdraw(Player player, double amount);

    /**
     * Deposit money to player's account.
     * Should be called synchronously after game ends.
     * 
     * @return true if deposit successful
     */
    boolean deposit(Player player, double amount);

    /**
     * Format amount with currency symbol.
     */
    String format(double amount);

    /**
     * Check if economy system is available.
     */
    boolean isAvailable();
}
