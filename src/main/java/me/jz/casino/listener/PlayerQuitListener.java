package me.jz.casino.listener;

import me.jz.casino.manager.CasinoManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles player disconnect cleanup.
 * Refunds active bets to prevent money loss on disconnect.
 */
public class PlayerQuitListener implements Listener {

    private final CasinoManager casinoManager;

    public PlayerQuitListener(CasinoManager casinoManager) {
        this.casinoManager = casinoManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Handle any active games and refund bet
        casinoManager.handleDisconnect(player);
        
        // Clear cooldown on disconnect (optional - can be removed if you want cooldowns to persist)
        casinoManager.getCooldownManager().removeCooldown(player.getUniqueId());
    }
}
