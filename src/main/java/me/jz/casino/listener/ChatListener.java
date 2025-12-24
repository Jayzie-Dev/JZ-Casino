package me.jz.casino.listener;

import me.jz.casino.game.slot.SlotMachine;
import me.jz.casino.gui.CasinoMenuGUI;
import me.jz.casino.manager.CasinoManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Handles chat-based bet amount input.
 * Simple and straightforward approach.
 */
public class ChatListener implements Listener {

    private final CasinoManager casinoManager;
    private final SlotMachine slotMachine;

    public ChatListener(CasinoManager casinoManager, SlotMachine slotMachine) {
        this.casinoManager = casinoManager;
        this.slotMachine = slotMachine;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        // Check if player has pending game
        if (!player.hasMetadata("casino_pending_game")) {
            return;
        }

        // Cancel chat event to prevent message broadcast
        event.setCancelled(true);

        String message = event.getMessage().trim();
        String gameType = player.getMetadata("casino_pending_game").get(0).asString();

        // Remove metadata
        player.removeMetadata("casino_pending_game", casinoManager.getPlugin());

        // Handle cancel
        if (message.equalsIgnoreCase("cancel")) {
            player.sendMessage(casinoManager.getConfig().getPrefix() + "§cCancelled.");
            return;
        }

        // Parse bet amount
        double betAmount;
        try {
            betAmount = Double.parseDouble(message);
        } catch (NumberFormatException e) {
            player.sendMessage(casinoManager.getConfig().getPrefix() 
                + "§cInvalid amount. Please enter a number.");
            return;
        }

        // Start game on main thread
        casinoManager.getPlugin().getServer().getScheduler().runTask(
            casinoManager.getPlugin(), 
            () -> startGame(player, gameType, betAmount)
        );
    }

    /**
     * Start game with validated bet amount.
     * Must run on main thread.
     */
    private void startGame(Player player, String gameType, double betAmount) {
        CasinoMenuGUI menu = new CasinoMenuGUI(casinoManager, player);

        switch (gameType.toUpperCase()) {
            case "SLOT" -> menu.startSlotGame(betAmount, slotMachine);
            case "DICE" -> menu.startDiceGame(betAmount);
            case "BLACKJACK" -> menu.startBlackjackGame(betAmount);
            default -> player.sendMessage(casinoManager.getConfig().getPrefix() 
                + "§cUnknown game type.");
        }
    }
}
