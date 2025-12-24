package me.jz.casino.config;

import me.jz.casino.CasinoPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * Centralized configuration manager.
 * Provides type-safe access to all plugin configuration values.
 */
public class ConfigManager {

    private final CasinoPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(CasinoPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    /**
     * Load or reload configuration from disk.
     */
    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    // ===== ECONOMY SETTINGS =====

    public String getCurrencySymbol() {
        return config.getString("economy.currency-symbol", "$");
    }

    // ===== GLOBAL CASINO SETTINGS =====

    public int getCooldown() {
        return config.getInt("casino.cooldown", 5);
    }

    public double getMinBet() {
        return config.getDouble("casino.min-bet", 10.0);
    }

    public double getMaxBet() {
        return config.getDouble("casino.max-bet", 10000.0);
    }

    // ===== SLOT MACHINE SETTINGS =====

    public boolean isSlotEnabled() {
        return config.getBoolean("slot.enabled", true);
    }

    public int getSlotAnimationDuration() {
        return config.getInt("slot.animation.duration", 60);
    }

    public int getSlotFrameDelay() {
        return config.getInt("slot.animation.frame-delay", 3);
    }

    public double getSlotHouseEdge() {
        return config.getDouble("slot.house-edge", 5.0);
    }

    /**
     * Load slot symbols with weights and multipliers from config.
     * Returns map of symbol name to array [weight, multiplier].
     */
    public Map<String, double[]> getSlotSymbols() {
        Map<String, double[]> symbols = new HashMap<>();
        ConfigurationSection symbolsSection = config.getConfigurationSection("slot.symbols");
        
        if (symbolsSection != null) {
            for (String symbolName : symbolsSection.getKeys(false)) {
                int weight = symbolsSection.getInt(symbolName + ".weight", 10);
                double multiplier = symbolsSection.getDouble(symbolName + ".multiplier", 1.0);
                symbols.put(symbolName, new double[]{weight, multiplier});
            }
        }
        
        return symbols;
    }

    /**
     * Get display name for a symbol.
     */
    public String getSymbolDisplayName(String symbolName) {
        return config.getString("slot.symbols." + symbolName + ".display-name", symbolName);
    }

    // ===== DICE GAME SETTINGS =====

    public boolean isDiceEnabled() {
        return config.getBoolean("dice.enabled", true);
    }

    public double getDiceWinMultiplier() {
        return config.getDouble("dice.win-multiplier", 2.0);
    }

    public double getDiceHouseEdge() {
        return config.getDouble("dice.house-edge", 2.0);
    }

    public int getDiceRevealDelay() {
        return config.getInt("dice.reveal-delay", 40);
    }

    // ===== BLACKJACK SETTINGS (V2) =====

    public boolean isBlackjackEnabled() {
        return config.getBoolean("blackjack.enabled", false);
    }

    public double getBlackjackWinMultiplier() {
        return config.getDouble("blackjack.win-multiplier", 1.0);
    }

    public double getBlackjackMultiplier() {
        return config.getDouble("blackjack.blackjack-multiplier", 1.5);
    }

    public int getBlackjackDealerStand() {
        return config.getInt("blackjack.dealer-stand", 17);
    }

    public double getBlackjackHouseEdge() {
        return config.getDouble("blackjack.house-edge", 1.0);
    }

    // ===== MESSAGES =====

    public String getPrefix() {
        return config.getString("messages.prefix", "§6§l[Casino] §r");
    }

    public String getMessage(String key) {
        return getPrefix() + config.getString("messages." + key, "Message not found: " + key);
    }

    /**
     * Get message with placeholder replacement.
     */
    public String getMessage(String key, Map<String, String> placeholders) {
        String message = getMessage(key);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return message;
    }
}
