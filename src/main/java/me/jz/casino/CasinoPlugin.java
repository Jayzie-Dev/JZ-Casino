package me.jz.casino;

import me.jz.casino.config.ConfigManager;
import me.jz.casino.economy.EconomyProvider;
import me.jz.casino.economy.EssentialsEconomyProvider;
import me.jz.casino.game.slot.SlotMachine;
import me.jz.casino.gui.CasinoMenuGUI;
import me.jz.casino.listener.ChatListener;
import me.jz.casino.listener.InventoryClickListener;
import me.jz.casino.listener.PlayerQuitListener;
import me.jz.casino.manager.CasinoManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * All-in Casino Plugin - Main Class
 * 
 * Production-grade casino plugin with Vault economy integration.
 * Features: Slot Machine, Dice Game, and Blackjack (V2).
 * 
 * @author JZ
 * @version 1.0.0
 */
public class CasinoPlugin extends JavaPlugin {

    private ConfigManager configManager;
    private EconomyProvider economyProvider;
    private CasinoManager casinoManager;
    private SlotMachine slotMachine;

    @Override
    public void onEnable() {
        getLogger().info("╔════════════════════════════════════╗");
        getLogger().info("║   All-in Casino Plugin v1.0.0     ║");
        getLogger().info("║   Loading...                       ║");
        getLogger().info("╚════════════════════════════════════╝");

        // Load configuration
        try {
            configManager = new ConfigManager(this);
            getLogger().info("✓ Configuration loaded");
        } catch (Exception e) {
            getLogger().severe("✗ Failed to load configuration: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Setup economy
        try {
            economyProvider = new EssentialsEconomyProvider(this, configManager.getCurrencySymbol());
            if (!economyProvider.isAvailable()) {
                throw new IllegalStateException("EssentialsX economy not available");
            }
            getLogger().info("✓ EssentialsX economy connected");
        } catch (Exception e) {
            getLogger().severe("✗ Failed to setup economy: " + e.getMessage());
            getLogger().severe("✗ Make sure EssentialsX is installed!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize managers
        casinoManager = new CasinoManager(this, configManager, economyProvider);
        getLogger().info("✓ Casino manager initialized");

        // Initialize games
        slotMachine = new SlotMachine(configManager);
        getLogger().info("✓ Slot machine initialized");
        getLogger().info("  - Symbols loaded: " + slotMachine.getSymbols().size());
        getLogger().info("  - Theoretical RTP: " + String.format("%.2f%%", slotMachine.calculateRTP() * 100));

        // Register listeners
        getServer().getPluginManager().registerEvents(
            new InventoryClickListener(casinoManager, slotMachine), this);
        getServer().getPluginManager().registerEvents(
            new PlayerQuitListener(casinoManager), this);
        getServer().getPluginManager().registerEvents(
            new ChatListener(casinoManager, slotMachine), this);
        getLogger().info("✓ Event listeners registered");

        // Register commands (handled in onCommand)
        getLogger().info("✓ Commands registered");

        getLogger().info("╔════════════════════════════════════╗");
        getLogger().info("║   All-in Casino Plugin Enabled!   ║");
        getLogger().info("║   Use /casino to play             ║");
        getLogger().info("╚════════════════════════════════════╝");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling All-in Casino Plugin...");

        // Cleanup active games
        if (casinoManager != null) {
            casinoManager.getPlayerDataManager().clearAll();
            casinoManager.getCooldownManager().clearAll();
            getLogger().info("✓ Active games cleaned up");
        }

        getLogger().info("All-in Casino Plugin disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cThis command can only be used by players.");
            return true;
        }

        switch (command.getName().toLowerCase()) {
            case "casino" -> {
                if (!player.hasPermission("casino.use")) {
                    player.sendMessage(configManager.getMessage("no-permission", null));
                    return true;
                }

                // Open main casino menu
                CasinoMenuGUI menu = new CasinoMenuGUI(casinoManager, player);
                menu.open();
                return true;
            }

            case "casinoadmin" -> {
                if (!player.hasPermission("casino.admin")) {
                    player.sendMessage(configManager.getMessage("no-permission", null));
                    return true;
                }

                // V3 feature - admin dashboard
                player.sendMessage(configManager.getPrefix() 
                    + "§eAdmin dashboard coming in V3!");
                return true;
            }
        }

        return false;
    }

    // ===== GETTERS =====

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public EconomyProvider getEconomyProvider() {
        return economyProvider;
    }

    public CasinoManager getCasinoManager() {
        return casinoManager;
    }

    public SlotMachine getSlotMachine() {
        return slotMachine;
    }
}
