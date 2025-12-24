package me.jz.casino.gui;

import me.jz.casino.config.ConfigManager;
import me.jz.casino.economy.EconomyProvider;
import me.jz.casino.game.blackjack.BlackjackGame;
import me.jz.casino.game.dice.DiceGame;
import me.jz.casino.game.slot.SlotGame;
import me.jz.casino.game.slot.SlotMachine;
import me.jz.casino.manager.CasinoManager;
import me.jz.casino.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Main casino menu GUI.
 * Allows players to select games and enter bet amounts.
 */
public class CasinoMenuGUI {

    private static final String TITLE = "§6§l⭐ All-in Casino ⭐";
    
    // Slot identifiers for click handling
    public static final int SLOT_MACHINE_SLOT = 11;
    public static final int DICE_GAME_SLOT = 13;
    public static final int BLACKJACK_SLOT = 15;
    public static final int INFO_SLOT = 22;

    private final CasinoManager casinoManager;
    private final ConfigManager config;
    private final EconomyProvider economy;
    private final Player player;
    private final Inventory inventory;

    public CasinoMenuGUI(CasinoManager casinoManager, Player player) {
        this.casinoManager = casinoManager;
        this.config = casinoManager.getConfig();
        this.economy = casinoManager.getEconomy();
        this.player = player;
        this.inventory = Bukkit.createInventory(null, 27, TITLE);

        setupGUI();
    }

    /**
     * Setup main menu GUI layout.
     */
    private void setupGUI() {
        // Fill borders
        ItemStack border = new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE)
                .name(" ")
                .build();

        for (int i = 0; i < 27; i++) {
            if (i < 9 || i > 17 || i % 9 == 0 || i % 9 == 8) {
                inventory.setItem(i, border);
            }
        }

        // Slot Machine
        if (config.isSlotEnabled()) {
            ItemStack slotItem = new ItemBuilder(Material.GOLD_BLOCK)
                    .name("§6§l🎰 Slot Machine")
                    .lore(
                        "§7Match 3 symbols to win big!",
                        "",
                        "§7Min Bet: §e" + economy.format(config.getMinBet()),
                        "§7Max Bet: §e" + economy.format(config.getMaxBet()),
                        "",
                        "§e§lClick to play!"
                    )
                    .glow()
                    .build();
            inventory.setItem(SLOT_MACHINE_SLOT, slotItem);
        }

        // Dice Game
        if (config.isDiceEnabled()) {
            ItemStack diceItem = new ItemBuilder(Material.PAPER)
                    .name("§a§l🎲 Dice Game")
                    .lore(
                        "§7Roll higher than the dealer!",
                        "",
                        "§7Win Multiplier: §e" + config.getDiceWinMultiplier() + "x",
                        "§7Min Bet: §e" + economy.format(config.getMinBet()),
                        "§7Max Bet: §e" + economy.format(config.getMaxBet()),
                        "",
                        "§e§lClick to play!"
                    )
                    .build();
            inventory.setItem(DICE_GAME_SLOT, diceItem);
        }

        // Blackjack (V2 - disabled by default)
        if (config.isBlackjackEnabled()) {
            ItemStack blackjackItem = new ItemBuilder(Material.DIAMOND)
                    .name("§9§l♠ Blackjack")
                    .lore(
                        "§7Beat the dealer to 21!",
                        "",
                        "§7Win: §a" + config.getBlackjackWinMultiplier() + "x",
                        "§7Blackjack: §6" + config.getBlackjackMultiplier() + "x",
                        "§7Min Bet: §e" + economy.format(config.getMinBet()),
                        "§7Max Bet: §e" + economy.format(config.getMaxBet()),
                        "",
                        "§e§lClick to play!"
                    )
                    .glow()
                    .build();
            inventory.setItem(BLACKJACK_SLOT, blackjackItem);
        }

        // Player info
        ItemStack info = new ItemBuilder(Material.EMERALD)
                .name("§a§lYour Balance")
                .lore(
                    "§7Balance: §a" + economy.format(economy.getBalance(player)),
                    "",
                    "§7Cooldown: §e" + config.getCooldown() + "s",
                    "§7Min Bet: §e" + economy.format(config.getMinBet()),
                    "§7Max Bet: §e" + economy.format(config.getMaxBet())
                )
                .build();
        inventory.setItem(INFO_SLOT, info);
    }

    /**
     * Open menu for player.
     */
    public void open() {
        player.openInventory(inventory);
    }

    /**
     * Handle game selection with bet amount.
     * Called from click listener after bet input.
     */
    public void startSlotGame(double betAmount, SlotMachine slotMachine) {
        // Validate game start
        String error = casinoManager.validateGameStart(player, betAmount);
        if (error != null) {
            player.sendMessage(error);
            return;
        }

        // Create and start game
        SlotGame game = new SlotGame(player, betAmount, casinoManager, slotMachine);
        
        if (!casinoManager.startGame(player, game, betAmount)) {
            return; // Economy error
        }

        // Open slot GUI
        SlotGUI slotGUI = new SlotGUI(casinoManager, game);
        slotGUI.open();
    }

    /**
     * Start dice game with bet amount.
     */
    public void startDiceGame(double betAmount) {
        // Validate game start
        String error = casinoManager.validateGameStart(player, betAmount);
        if (error != null) {
            player.sendMessage(error);
            return;
        }

        // Create and start game
        DiceGame game = new DiceGame(player, betAmount, casinoManager);
        
        if (!casinoManager.startGame(player, game, betAmount)) {
            return; // Economy error
        }

        // Open dice GUI
        DiceGUI diceGUI = new DiceGUI(casinoManager, game);
        diceGUI.open();
    }

    /**
     * Start blackjack game with bet amount.
     */
    public void startBlackjackGame(double betAmount) {
        // Validate game start
        String error = casinoManager.validateGameStart(player, betAmount);
        if (error != null) {
            player.sendMessage(error);
            return;
        }

        // Create and start game
        BlackjackGame game = new BlackjackGame(player, betAmount, casinoManager);
        
        if (!casinoManager.startGame(player, game, betAmount)) {
            return; // Economy error
        }

        // Open blackjack GUI
        BlackjackGUI blackjackGUI = new BlackjackGUI(casinoManager, game);
        blackjackGUI.open();
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Player getPlayer() {
        return player;
    }
}
