package me.jz.casino.listener;

import me.jz.casino.game.slot.SlotMachine;
import me.jz.casino.gui.BlackjackGUI;
import me.jz.casino.gui.CasinoMenuGUI;
import me.jz.casino.manager.CasinoManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

/**
 * Handles all inventory click events for casino GUIs.
 * Prevents item movement and handles game selection.
 */
public class InventoryClickListener implements Listener {

    private final CasinoManager casinoManager;
    private final SlotMachine slotMachine;

    public InventoryClickListener(CasinoManager casinoManager, SlotMachine slotMachine) {
        this.casinoManager = casinoManager;
        this.slotMachine = slotMachine;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        InventoryView view = event.getView();
        Component titleComponent = view.title();
        String title = getPlainTitle(titleComponent);

        // Check if clicking in casino GUI
        if (!isCasinoGUI(title)) {
            return;
        }

        // Cancel all clicks in casino GUIs to prevent item movement
        event.setCancelled(true);

        // Only handle clicks in top inventory
        if (event.getClickedInventory() == null || 
            !event.getClickedInventory().equals(view.getTopInventory())) {
            return;
        }

        int slot = event.getSlot();

        // Handle main menu clicks
        if (title.contains("All-in Casino")) {
            handleMainMenuClick(player, slot);
        } else if (title.contains("Blackjack")) {
            handleBlackjackClick(player, slot, view.getTopInventory());
        }
        
        // Slot and Dice GUIs are view-only during animation
        // No click handling needed as they auto-close after result
    }

    /**
     * Handle clicks in main casino menu.
     */
    private void handleMainMenuClick(Player player, int slot) {
        if (slot == CasinoMenuGUI.SLOT_MACHINE_SLOT) {
            player.closeInventory();
            player.sendMessage(casinoManager.getConfig().getPrefix() 
                + "§eEnter your bet amount in chat (or type 'cancel'):");
            promptBetAmount(player, "SLOT");
            
        } else if (slot == CasinoMenuGUI.DICE_GAME_SLOT) {
            player.closeInventory();
            player.sendMessage(casinoManager.getConfig().getPrefix() 
                + "§eEnter your bet amount in chat (or type 'cancel'):");
            promptBetAmount(player, "DICE");
            
        } else if (slot == CasinoMenuGUI.BLACKJACK_SLOT) {
            player.closeInventory();
            player.sendMessage(casinoManager.getConfig().getPrefix() 
                + "§eEnter your bet amount in chat (or type 'cancel'):");
            promptBetAmount(player, "BLACKJACK");
        }
    }

    /**
     * Prompt player for bet amount via chat.
     */
    private void promptBetAmount(Player player, String gameType) {
        player.setMetadata("casino_pending_game", 
            new org.bukkit.metadata.FixedMetadataValue(casinoManager.getPlugin(), gameType));
    }

    /**
     * Handle clicks in blackjack GUI.
     */
    private void handleBlackjackClick(Player player, int slot, Inventory inventory) {
        // Find the BlackjackGUI instance
        if (casinoManager.getPlayerDataManager().hasActiveGame(player.getUniqueId())) {
            var game = casinoManager.getPlayerDataManager().getActiveGame(player.getUniqueId());
            
            if (game instanceof me.jz.casino.game.blackjack.BlackjackGame blackjackGame) {
                // Find GUI - we need to track this differently
                // For now, handle button clicks directly
                
                if (slot == 21) { // HIT button
                    blackjackGame.hit();
                    // Update display through a new GUI instance
                    BlackjackGUI gui = new BlackjackGUI(casinoManager, blackjackGame);
                    gui.updateDisplay();
                    
                    if (blackjackGame.isPlayerTurnComplete()) {
                        casinoManager.getPlugin().getServer().getScheduler().runTaskLater(
                            casinoManager.getPlugin(), 
                            gui::showResult, 
                            20L
                        );
                    }
                } else if (slot == 23) { // STAND button
                    blackjackGame.stand();
                    BlackjackGUI gui = new BlackjackGUI(casinoManager, blackjackGame);
                    casinoManager.getPlugin().getServer().getScheduler().runTaskLater(
                        casinoManager.getPlugin(), 
                        gui::showResult, 
                        20L
                    );
                }
            }
        }
    }

    /**
     * Check if inventory is a casino GUI.
     */
    private boolean isCasinoGUI(String title) {
        return title.contains("Casino") || 
               title.contains("Slot Machine") || 
               title.contains("Dice Game") ||
               title.contains("Blackjack");
    }

    /**
     * Extract plain text from Adventure Component title.
     */
    private String getPlainTitle(Component component) {
        if (component instanceof TextComponent) {
            return PlainTextComponentSerializer.plainText().serialize(component);
        }
        return "";
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        Component titleComponent = event.getView().title();
        String title = getPlainTitle(titleComponent);

        // If player closes slot or dice GUI during game, end game properly
        if (title.contains("Slot Machine") || title.contains("Dice Game")) {
            if (casinoManager.getPlayerDataManager().hasActiveGame(player.getUniqueId())) {
                // Game will handle cleanup through its end() method
                // This is called automatically when GUI closes during animation
            }
        }
    }
}
