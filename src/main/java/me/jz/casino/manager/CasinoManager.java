package me.jz.casino.manager;

import me.jz.casino.CasinoPlugin;
import me.jz.casino.config.ConfigManager;
import me.jz.casino.economy.EconomyProvider;
import me.jz.casino.game.CasinoGame;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Central manager for all casino operations.
 * Handles validation, economy checks, and game lifecycle.
 */
public class CasinoManager {

    private final CasinoPlugin plugin;
    private final ConfigManager config;
    private final EconomyProvider economy;
    private final CooldownManager cooldownManager;
    private final PlayerDataManager playerDataManager;

    public CasinoManager(CasinoPlugin plugin, ConfigManager config, EconomyProvider economy) {
        this.plugin = plugin;
        this.config = config;
        this.economy = economy;
        this.cooldownManager = new CooldownManager(config.getCooldown());
        this.playerDataManager = new PlayerDataManager();
    }

    /**
     * Validate if player can start a game with given bet amount.
     * Checks: cooldown, active game, bet limits, balance.
     * 
     * @return null if valid, error message if invalid
     */
    public String validateGameStart(Player player, double betAmount) {
        // Check cooldown
        if (cooldownManager.isOnCooldown(player.getUniqueId())) {
            int remaining = cooldownManager.getRemainingCooldown(player.getUniqueId());
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("seconds", String.valueOf(remaining));
            return config.getMessage("cooldown-active", placeholders);
        }

        // Check if already in game
        if (playerDataManager.hasActiveGame(player.getUniqueId())) {
            return config.getMessage("game-in-progress", new HashMap<>());
        }

        // Validate bet amount
        double minBet = config.getMinBet();
        double maxBet = config.getMaxBet();

        if (betAmount < minBet) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("min", economy.format(minBet));
            return config.getMessage("bet-too-low", placeholders);
        }

        if (betAmount > maxBet) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("max", economy.format(maxBet));
            return config.getMessage("bet-too-high", placeholders);
        }

        // Check balance
        if (!economy.has(player, betAmount)) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("amount", economy.format(betAmount));
            return config.getMessage("insufficient-funds", placeholders);
        }

        return null; // Valid
    }

    /**
     * Start a game for player.
     * Withdraws bet amount and registers active game.
     * 
     * @return true if game started successfully
     */
    public boolean startGame(Player player, CasinoGame game, double betAmount) {
        // Withdraw bet amount
        if (!economy.withdraw(player, betAmount)) {
            player.sendMessage(config.getMessage("economy-error", new HashMap<>()));
            return false;
        }

        // Set cooldown
        cooldownManager.setCooldown(player.getUniqueId());

        // Register active game
        playerDataManager.setActiveGame(player.getUniqueId(), game);

        return true;
    }

    /**
     * End a game for player.
     * Deposits winnings if any and removes active game.
     */
    public void endGame(Player player, double winnings) {
        // Deposit winnings if any
        if (winnings > 0) {
            if (!economy.deposit(player, winnings)) {
                player.sendMessage(config.getMessage("economy-error", new HashMap<>()));
            }
        }

        // Remove active game
        playerDataManager.removeActiveGame(player.getUniqueId());
    }

    /**
     * Handle player disconnect cleanup.
     * Refunds bet if game was in progress.
     */
    public void handleDisconnect(Player player) {
        CasinoGame activeGame = playerDataManager.getActiveGame(player.getUniqueId());
        if (activeGame != null) {
            // Refund bet amount
            economy.deposit(player, activeGame.getBetAmount());
            playerDataManager.removeActiveGame(player.getUniqueId());
        }
    }

    // ===== GETTERS =====

    public CasinoPlugin getPlugin() {
        return plugin;
    }

    public ConfigManager getConfig() {
        return config;
    }

    public EconomyProvider getEconomy() {
        return economy;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
}
