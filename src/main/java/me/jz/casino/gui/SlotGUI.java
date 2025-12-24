package me.jz.casino.gui;

import me.jz.casino.config.ConfigManager;
import me.jz.casino.game.slot.SlotGame;
import me.jz.casino.game.slot.SlotResult;
import me.jz.casino.game.slot.SlotSymbol;
import me.jz.casino.manager.CasinoManager;
import me.jz.casino.util.ItemBuilder;
import me.jz.casino.util.RandomUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Slot machine GUI with animated spinning.
 * Locks inventory during animation to prevent exploits.
 */
public class SlotGUI {

    private static final int[] REEL_SLOTS = {11, 13, 15}; // Positions for 3 reels
    private static final String TITLE = "§6§lSlot Machine";

    private final CasinoManager casinoManager;
    private final ConfigManager config;
    private final SlotGame game;
    private final Inventory inventory;
    private boolean isSpinning;

    public SlotGUI(CasinoManager casinoManager, SlotGame game) {
        this.casinoManager = casinoManager;
        this.config = casinoManager.getConfig();
        this.game = game;
        this.inventory = Bukkit.createInventory(null, 27, TITLE);
        this.isSpinning = false;

        setupGUI();
    }

    /**
     * Setup initial GUI layout.
     */
    private void setupGUI() {
        // Fill borders with glass panes
        ItemStack border = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                .name(" ")
                .build();

        for (int i = 0; i < 27; i++) {
            if (i < 9 || i > 17 || i % 9 == 0 || i % 9 == 8) {
                inventory.setItem(i, border);
            }
        }

        // Set initial reel symbols (question marks)
        ItemStack placeholder = new ItemBuilder(Material.PAPER)
                .name("§e❓")
                .lore("§7Spinning...")
                .build();

        for (int slot : REEL_SLOTS) {
            inventory.setItem(slot, placeholder);
        }

        // Info display
        ItemStack info = new ItemBuilder(Material.GOLD_INGOT)
                .name("§6§lBet Amount")
                .lore(
                    "§7Amount: §e" + casinoManager.getEconomy().format(game.getBetAmount()),
                    "",
                    "§7Match 3 symbols to win!",
                    "§7Higher rarity = bigger payout"
                )
                .build();
        inventory.setItem(22, info);
    }

    /**
     * Open GUI for player and start animation.
     */
    public void open() {
        game.getPlayer().openInventory(inventory);
        startSpinAnimation();
    }

    /**
     * Start the slot machine spin animation.
     * Uses scheduler to create visual spinning effect.
     */
    private void startSpinAnimation() {
        isSpinning = true;
        game.start(); // Generate result

        int duration = config.getSlotAnimationDuration();
        int frameDelay = config.getSlotFrameDelay();
        List<SlotSymbol> allSymbols = game.getSlotMachine().getSymbols();

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!game.getPlayer().getOpenInventory().getTopInventory().equals(inventory)) {
                    // Player closed inventory, cancel animation
                    cancel();
                    game.end();
                    return;
                }

                if (ticks >= duration) {
                    // Animation complete, show final result
                    showResult();
                    cancel();
                    return;
                }

                // Update reels with random symbols during animation
                if (ticks % frameDelay == 0) {
                    for (int i = 0; i < 3; i++) {
                        // Slow down each reel progressively
                        int reelStopTime = duration - (i * 15);
                        if (ticks < reelStopTime) {
                            SlotSymbol randomSymbol = RandomUtil.randomElement(allSymbols);
                            inventory.setItem(REEL_SLOTS[i], createSymbolItem(randomSymbol, true));
                        }
                    }
                }

                ticks++;
            }
        }.runTaskTimer(casinoManager.getPlugin(), 0L, 1L);
    }

    /**
     * Show final result after animation completes.
     */
    private void showResult() {
        isSpinning = false;
        SlotResult result = game.getResult();

        // Display final symbols
        for (int i = 0; i < 3; i++) {
            SlotSymbol symbol = result.getSymbol(i);
            inventory.setItem(REEL_SLOTS[i], createSymbolItem(symbol, false));
        }

        // Update info display with result
        Player player = game.getPlayer();
        if (result.isWin()) {
            ItemStack winInfo = new ItemBuilder(Material.EMERALD)
                    .name("§a§l✓ YOU WIN!")
                    .lore(
                        "§7Bet: §e" + casinoManager.getEconomy().format(game.getBetAmount()),
                        "§7Payout: §a" + casinoManager.getEconomy().format(result.getPayout()),
                        "",
                        "§a§lCongratulations!"
                    )
                    .glow()
                    .build();
            inventory.setItem(22, winInfo);

            player.sendMessage(config.getPrefix() + "§a§lYOU WIN! §7Payout: §a" 
                + casinoManager.getEconomy().format(result.getPayout()));
        } else {
            ItemStack loseInfo = new ItemBuilder(Material.REDSTONE)
                    .name("§c§l✗ No Match")
                    .lore(
                        "§7Bet: §e" + casinoManager.getEconomy().format(game.getBetAmount()),
                        "§7Lost: §c" + casinoManager.getEconomy().format(game.getBetAmount()),
                        "",
                        "§7Better luck next time!"
                    )
                    .build();
            inventory.setItem(22, loseInfo);

            player.sendMessage(config.getPrefix() + "§cNo match. Better luck next time!");
        }

        // End game after short delay
        Bukkit.getScheduler().runTaskLater(casinoManager.getPlugin(), () -> {
            game.end();
            player.closeInventory();
        }, 60L); // 3 seconds
    }

    /**
     * Create item representation of a symbol.
     */
    private ItemStack createSymbolItem(SlotSymbol symbol, boolean spinning) {
        Material material = getSymbolMaterial(symbol);
        
        ItemBuilder builder = new ItemBuilder(material)
                .name(symbol.getDisplayName());

        if (!spinning) {
            builder.lore(
                "§7Multiplier: §e" + symbol.getMultiplier() + "x",
                "§7Rarity: " + getRarityColor(symbol.getWeight()) + getRarityName(symbol.getWeight())
            );
        } else {
            builder.lore("§7Spinning...");
        }

        return builder.build();
    }

    /**
     * Map symbol to material for visual representation.
     */
    private Material getSymbolMaterial(SlotSymbol symbol) {
        return switch (symbol.getName().toUpperCase()) {
            case "CHERRY" -> Material.RED_DYE;
            case "LEMON" -> Material.YELLOW_DYE;
            case "ORANGE" -> Material.ORANGE_DYE;
            case "WATERMELON" -> Material.LIME_DYE;
            case "GRAPE" -> Material.PURPLE_DYE;
            case "BELL" -> Material.BELL;
            case "SEVEN" -> Material.NETHER_STAR;
            case "DIAMOND" -> Material.DIAMOND;
            case "JACKPOT" -> Material.GOLD_BLOCK;
            default -> Material.PAPER;
        };
    }

    /**
     * Get rarity name based on weight (lower weight = higher rarity).
     */
    private String getRarityName(int weight) {
        if (weight >= 30) return "Common";
        if (weight >= 15) return "Uncommon";
        if (weight >= 8) return "Rare";
        if (weight >= 4) return "Epic";
        return "Legendary";
    }

    /**
     * Get rarity color based on weight.
     */
    private String getRarityColor(int weight) {
        if (weight >= 30) return "§f";
        if (weight >= 15) return "§a";
        if (weight >= 8) return "§9";
        if (weight >= 4) return "§5";
        return "§6";
    }

    public boolean isSpinning() {
        return isSpinning;
    }

    public Inventory getInventory() {
        return inventory;
    }
}
