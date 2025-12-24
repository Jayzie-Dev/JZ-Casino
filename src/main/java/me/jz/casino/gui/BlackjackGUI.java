package me.jz.casino.gui;

import me.jz.casino.config.ConfigManager;
import me.jz.casino.game.blackjack.BlackjackCard;
import me.jz.casino.game.blackjack.BlackjackGame;
import me.jz.casino.game.blackjack.BlackjackHand;
import me.jz.casino.game.blackjack.BlackjackResult;
import me.jz.casino.manager.CasinoManager;
import me.jz.casino.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Blackjack GUI with interactive hit/stand buttons.
 */
public class BlackjackGUI {

    private static final String TITLE = "§8§lBlackjack";
    private static final int HIT_BUTTON_SLOT = 21;
    private static final int STAND_BUTTON_SLOT = 23;

    private final CasinoManager casinoManager;
    private final ConfigManager config;
    private final BlackjackGame game;
    private final Inventory inventory;

    public BlackjackGUI(CasinoManager casinoManager, BlackjackGame game) {
        this.casinoManager = casinoManager;
        this.config = casinoManager.getConfig();
        this.game = game;
        this.inventory = Bukkit.createInventory(null, 54, TITLE);

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

        for (int i = 0; i < 54; i++) {
            if (i < 9 || i >= 45 || i % 9 == 0 || i % 9 == 8) {
                inventory.setItem(i, border);
            }
        }

        updateDisplay();
    }

    /**
     * Update display with current game state.
     */
    public void updateDisplay() {
        // Clear card areas
        for (int i = 10; i <= 16; i++) {
            inventory.setItem(i, null);
        }
        for (int i = 28; i <= 34; i++) {
            inventory.setItem(i, null);
        }

        // Display dealer hand
        BlackjackHand dealerHand = game.getDealerHand();
        List<BlackjackCard> dealerCards = dealerHand.getCards();
        
        for (int i = 0; i < dealerCards.size(); i++) {
            BlackjackCard card = dealerCards.get(i);
            
            // Hide dealer's second card if player turn not complete
            if (i == 1 && !game.isPlayerTurnComplete()) {
                ItemStack hiddenCard = new ItemBuilder(Material.PAPER)
                        .name("§7???")
                        .lore("§7Hidden Card")
                        .build();
                inventory.setItem(10 + i, hiddenCard);
            } else {
                inventory.setItem(10 + i, createCardItem(card));
            }
        }

        // Dealer info
        String dealerValue = game.isPlayerTurnComplete() 
            ? String.valueOf(dealerHand.getValue())
            : "?";
        
        ItemStack dealerInfo = new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .name("§c§lDealer Hand")
                .lore(
                    "§7Value: §c" + dealerValue,
                    "§7Cards: §f" + dealerHand.getCardCount()
                )
                .build();
        inventory.setItem(19, dealerInfo);

        // Display player hand
        BlackjackHand playerHand = game.getPlayerHand();
        List<BlackjackCard> playerCards = playerHand.getCards();
        
        for (int i = 0; i < playerCards.size(); i++) {
            inventory.setItem(28 + i, createCardItem(playerCards.get(i)));
        }

        // Player info
        ItemStack playerInfo = new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE)
                .name("§a§lYour Hand")
                .lore(
                    "§7Value: §a" + playerHand.getValue(),
                    "§7Cards: §f" + playerHand.getCardCount(),
                    "",
                    playerHand.isBust() ? "§c§lBUST!" : 
                    playerHand.isBlackjack() ? "§6§lBLACKJACK!" : "§7"
                )
                .build();
        inventory.setItem(37, playerInfo);

        // Action buttons
        if (game.canHit()) {
            ItemStack hitButton = new ItemBuilder(Material.LIME_CONCRETE)
                    .name("§a§lHIT")
                    .lore(
                        "§7Take another card",
                        "",
                        "§e§lClick to Hit!"
                    )
                    .glow()
                    .build();
            inventory.setItem(HIT_BUTTON_SLOT, hitButton);
        } else {
            ItemStack hitDisabled = new ItemBuilder(Material.GRAY_CONCRETE)
                    .name("§7§lHIT")
                    .lore("§7Cannot hit")
                    .build();
            inventory.setItem(HIT_BUTTON_SLOT, hitDisabled);
        }

        if (game.canStand()) {
            ItemStack standButton = new ItemBuilder(Material.RED_CONCRETE)
                    .name("§c§lSTAND")
                    .lore(
                        "§7End your turn",
                        "",
                        "§e§lClick to Stand!"
                    )
                    .build();
            inventory.setItem(STAND_BUTTON_SLOT, standButton);
        } else {
            ItemStack standDisabled = new ItemBuilder(Material.GRAY_CONCRETE)
                    .name("§7§lSTAND")
                    .lore("§7Turn complete")
                    .build();
            inventory.setItem(STAND_BUTTON_SLOT, standDisabled);
        }

        // Bet info
        ItemStack betInfo = new ItemBuilder(Material.GOLD_INGOT)
                .name("§6§lBet Amount")
                .lore(
                    "§7Bet: §e" + casinoManager.getEconomy().format(game.getBetAmount()),
                    "",
                    "§7Win: §a" + casinoManager.getEconomy().format(game.getBetAmount() * 2),
                    "§7Blackjack: §6" + casinoManager.getEconomy().format(game.getBetAmount() * 2.5)
                )
                .build();
        inventory.setItem(49, betInfo);
    }

    /**
     * Show final result.
     */
    public void showResult() {
        updateDisplay(); // Show all cards

        BlackjackResult result = game.getResult();
        Player player = game.getPlayer();

        // Result display
        ItemStack resultItem;
        
        switch (result.getOutcome()) {
            case PLAYER_BLACKJACK -> {
                resultItem = new ItemBuilder(Material.NETHER_STAR)
                        .name("§6§l⭐ BLACKJACK! ⭐")
                        .lore(
                            "§7Your Hand: §a" + result.getPlayerValue(),
                            "§7Dealer Hand: §c" + result.getDealerValue(),
                            "",
                            "§7Bet: §e" + casinoManager.getEconomy().format(game.getBetAmount()),
                            "§7Payout: §6" + casinoManager.getEconomy().format(result.getPayout()),
                            "",
                            "§6§lAMAZING!"
                        )
                        .glow()
                        .build();
                
                player.sendMessage(config.getPrefix() + "§6§l⭐ BLACKJACK! §7Payout: §6" 
                    + casinoManager.getEconomy().format(result.getPayout()));
            }
            case PLAYER_WIN -> {
                resultItem = new ItemBuilder(Material.EMERALD)
                        .name("§a§l✓ YOU WIN!")
                        .lore(
                            "§7Your Hand: §a" + result.getPlayerValue(),
                            "§7Dealer Hand: §c" + result.getDealerValue(),
                            "",
                            "§7Bet: §e" + casinoManager.getEconomy().format(game.getBetAmount()),
                            "§7Payout: §a" + casinoManager.getEconomy().format(result.getPayout()),
                            "",
                            "§a§lCongratulations!"
                        )
                        .glow()
                        .build();
                
                player.sendMessage(config.getPrefix() + "§a§lYOU WIN! §7(" + result.getPlayerValue() 
                    + " vs " + result.getDealerValue() + ") Payout: §a" 
                    + casinoManager.getEconomy().format(result.getPayout()));
            }
            case PUSH -> {
                resultItem = new ItemBuilder(Material.GOLD_INGOT)
                        .name("§e§l⚖ PUSH (TIE)")
                        .lore(
                            "§7Your Hand: §e" + result.getPlayerValue(),
                            "§7Dealer Hand: §e" + result.getDealerValue(),
                            "",
                            "§7Bet Returned: §e" + casinoManager.getEconomy().format(game.getBetAmount()),
                            "",
                            "§7It's a tie!"
                        )
                        .build();
                
                player.sendMessage(config.getPrefix() + "§ePush! (" + result.getPlayerValue() 
                    + " vs " + result.getDealerValue() + ") Bet returned.");
            }
            default -> {
                resultItem = new ItemBuilder(Material.REDSTONE)
                        .name("§c§l✗ DEALER WINS")
                        .lore(
                            "§7Your Hand: §c" + result.getPlayerValue(),
                            "§7Dealer Hand: §a" + result.getDealerValue(),
                            "",
                            "§7Bet: §e" + casinoManager.getEconomy().format(game.getBetAmount()),
                            "§7Lost: §c" + casinoManager.getEconomy().format(game.getBetAmount()),
                            "",
                            "§7Better luck next time!"
                        )
                        .build();
                
                player.sendMessage(config.getPrefix() + "§cDealer wins! (" + result.getPlayerValue() 
                    + " vs " + result.getDealerValue() + ")");
            }
        }

        inventory.setItem(49, resultItem);

        // End game after delay
        Bukkit.getScheduler().runTaskLater(casinoManager.getPlugin(), () -> {
            game.end();
            player.closeInventory();
        }, 80L); // 4 seconds
    }

    /**
     * Create item representation of a card.
     */
    private ItemStack createCardItem(BlackjackCard card) {
        List<String> lore = new ArrayList<>();
        lore.add("§7Value: §f" + card.getValue());
        lore.add("§7Suit: §f" + card.getSuit().name());

        return new ItemBuilder(card.getMaterial())
                .name(card.getDisplayName())
                .lore(lore)
                .build();
    }

    /**
     * Handle hit button click.
     */
    public void handleHit() {
        if (!game.canHit()) return;

        game.hit();
        updateDisplay();

        // Check if game ended
        if (game.isPlayerTurnComplete()) {
            Bukkit.getScheduler().runTaskLater(casinoManager.getPlugin(), 
                this::showResult, 20L);
        }
    }

    /**
     * Handle stand button click.
     */
    public void handleStand() {
        if (!game.canStand()) return;

        game.stand();
        
        // Show result after brief delay
        Bukkit.getScheduler().runTaskLater(casinoManager.getPlugin(), 
            this::showResult, 20L);
    }

    /**
     * Open GUI for player.
     */
    public void open() {
        game.getPlayer().openInventory(inventory);
        game.start();
        updateDisplay();

        // Check for immediate blackjack
        if (game.isPlayerTurnComplete()) {
            Bukkit.getScheduler().runTaskLater(casinoManager.getPlugin(), 
                this::showResult, 40L);
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    public BlackjackGame getGame() {
        return game;
    }
}
