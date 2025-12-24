package me.jz.casino.gui;

import me.jz.casino.config.ConfigManager;
import me.jz.casino.game.dice.DiceGame;
import me.jz.casino.game.dice.DiceResult;
import me.jz.casino.manager.CasinoManager;
import me.jz.casino.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Dice game GUI with delayed reveal animation.
 */
public class DiceGUI {

    private static final String TITLE = "§6§lDice Game";
    private static final int PLAYER_SLOT = 11;
    private static final int DEALER_SLOT = 15;

    private final CasinoManager casinoManager;
    private final ConfigManager config;
    private final DiceGame game;
    private final Inventory inventory;
    private boolean isRolling;

    public DiceGUI(CasinoManager casinoManager, DiceGame game) {
        this.casinoManager = casinoManager;
        this.config = casinoManager.getConfig();
        this.game = game;
        this.inventory = Bukkit.createInventory(null, 27, TITLE);
        this.isRolling = false;

        setupGUI();
    }

    /**
     * Setup initial GUI layout.
     */
    private void setupGUI() {
        // Fill borders
        ItemStack border = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                .name(" ")
                .build();

        for (int i = 0; i < 27; i++) {
            if (i < 9 || i > 17 || i % 9 == 0 || i % 9 == 8) {
                inventory.setItem(i, border);
            }
        }

        // Player dice placeholder
        ItemStack playerDice = new ItemBuilder(Material.PAPER)
                .name("§a§lYour Roll")
                .lore("§7Rolling...")
                .build();
        inventory.setItem(PLAYER_SLOT, playerDice);

        // Dealer dice placeholder
        ItemStack dealerDice = new ItemBuilder(Material.PAPER)
                .name("§c§lDealer Roll")
                .lore("§7Rolling...")
                .build();
        inventory.setItem(DEALER_SLOT, dealerDice);

        // Info display
        ItemStack info = new ItemBuilder(Material.GOLD_INGOT)
                .name("§6§lDice Game")
                .lore(
                    "§7Bet: §e" + casinoManager.getEconomy().format(game.getBetAmount()),
                    "",
                    "§7Roll higher than dealer to win!",
                    "§7Win Multiplier: §e" + config.getDiceWinMultiplier() + "x"
                )
                .build();
        inventory.setItem(22, info);
    }

    /**
     * Open GUI and start roll animation.
     */
    public void open() {
        game.getPlayer().openInventory(inventory);
        startRollAnimation();
    }

    /**
     * Start dice roll animation with delayed reveal.
     */
    private void startRollAnimation() {
        isRolling = true;
        game.start(); // Generate result

        int revealDelay = config.getDiceRevealDelay();

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!game.getPlayer().getOpenInventory().getTopInventory().equals(inventory)) {
                    // Player closed inventory
                    cancel();
                    game.end();
                    return;
                }

                if (ticks >= revealDelay) {
                    // Reveal result
                    showResult();
                    cancel();
                    return;
                }

                // Animate rolling dice
                if (ticks % 5 == 0) {
                    ItemStack rolling = new ItemBuilder(Material.PAPER)
                            .name("§e§l🎲 Rolling...")
                            .lore("§7Please wait...")
                            .build();
                    inventory.setItem(PLAYER_SLOT, rolling);
                    inventory.setItem(DEALER_SLOT, rolling);
                }

                ticks++;
            }
        }.runTaskTimer(casinoManager.getPlugin(), 0L, 1L);
    }

    /**
     * Show final dice result.
     */
    private void showResult() {
        isRolling = false;
        DiceResult result = game.getResult();
        Player player = game.getPlayer();

        // Show player roll
        ItemStack playerDice = new ItemBuilder(getDiceMaterial(result.getPlayerRoll()))
                .name("§a§lYour Roll: " + result.getPlayerRoll())
                .lore("§7You rolled a " + result.getPlayerRoll())
                .build();
        inventory.setItem(PLAYER_SLOT, playerDice);

        // Show dealer roll
        ItemStack dealerDice = new ItemBuilder(getDiceMaterial(result.getDealerRoll()))
                .name("§c§lDealer Roll: " + result.getDealerRoll())
                .lore("§7Dealer rolled a " + result.getDealerRoll())
                .build();
        inventory.setItem(DEALER_SLOT, dealerDice);

        // Update result display
        if (result.isWin()) {
            ItemStack winInfo = new ItemBuilder(Material.EMERALD)
                    .name("§a§l✓ YOU WIN!")
                    .lore(
                        "§7Your Roll: §a" + result.getPlayerRoll(),
                        "§7Dealer Roll: §c" + result.getDealerRoll(),
                        "",
                        "§7Bet: §e" + casinoManager.getEconomy().format(game.getBetAmount()),
                        "§7Payout: §a" + casinoManager.getEconomy().format(result.getPayout()),
                        "",
                        "§a§lCongratulations!"
                    )
                    .glow()
                    .build();
            inventory.setItem(22, winInfo);

            player.sendMessage(config.getPrefix() + "§a§lYOU WIN! §7(" + result.getPlayerRoll() 
                + " vs " + result.getDealerRoll() + ") Payout: §a" 
                + casinoManager.getEconomy().format(result.getPayout()));
        } else if (result.isTie()) {
            ItemStack tieInfo = new ItemBuilder(Material.GOLD_INGOT)
                    .name("§e§l⚖ TIE!")
                    .lore(
                        "§7Your Roll: §e" + result.getPlayerRoll(),
                        "§7Dealer Roll: §e" + result.getDealerRoll(),
                        "",
                        "§7Bet: §e" + casinoManager.getEconomy().format(game.getBetAmount()),
                        "§7Lost: §c" + casinoManager.getEconomy().format(game.getBetAmount()),
                        "",
                        "§7Ties go to the house!"
                    )
                    .build();
            inventory.setItem(22, tieInfo);

            player.sendMessage(config.getPrefix() + "§eTie! (" + result.getPlayerRoll() 
                + " vs " + result.getDealerRoll() + ") House wins on ties.");
        } else {
            ItemStack loseInfo = new ItemBuilder(Material.REDSTONE)
                    .name("§c§l✗ YOU LOSE")
                    .lore(
                        "§7Your Roll: §c" + result.getPlayerRoll(),
                        "§7Dealer Roll: §a" + result.getDealerRoll(),
                        "",
                        "§7Bet: §e" + casinoManager.getEconomy().format(game.getBetAmount()),
                        "§7Lost: §c" + casinoManager.getEconomy().format(game.getBetAmount()),
                        "",
                        "§7Better luck next time!"
                    )
                    .build();
            inventory.setItem(22, loseInfo);

            player.sendMessage(config.getPrefix() + "§cYou lose! (" + result.getPlayerRoll() 
                + " vs " + result.getDealerRoll() + ")");
        }

        // End game after delay
        Bukkit.getScheduler().runTaskLater(casinoManager.getPlugin(), () -> {
            game.end();
            player.closeInventory();
        }, 60L); // 3 seconds
    }

    /**
     * Get material representing dice value.
     */
    private Material getDiceMaterial(int value) {
        return switch (value) {
            case 1 -> Material.WHITE_CONCRETE;
            case 2 -> Material.LIGHT_GRAY_CONCRETE;
            case 3 -> Material.GRAY_CONCRETE;
            case 4 -> Material.CYAN_CONCRETE;
            case 5 -> Material.BLUE_CONCRETE;
            case 6 -> Material.PURPLE_CONCRETE;
            default -> Material.PAPER;
        };
    }

    public boolean isRolling() {
        return isRolling;
    }

    public Inventory getInventory() {
        return inventory;
    }
}
