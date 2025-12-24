package me.jz.casino.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages per-player cooldowns for casino games.
 * Prevents spam and exploitation through rate limiting.
 */
public class CooldownManager {

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final int cooldownSeconds;

    public CooldownManager(int cooldownSeconds) {
        this.cooldownSeconds = cooldownSeconds;
    }

    /**
     * Check if player is on cooldown.
     */
    public boolean isOnCooldown(UUID playerId) {
        if (!cooldowns.containsKey(playerId)) {
            return false;
        }
        
        long lastPlayed = cooldowns.get(playerId);
        long currentTime = System.currentTimeMillis();
        long cooldownMillis = cooldownSeconds * 1000L;
        
        return (currentTime - lastPlayed) < cooldownMillis;
    }

    /**
     * Get remaining cooldown time in seconds.
     * Returns 0 if no cooldown active.
     */
    public int getRemainingCooldown(UUID playerId) {
        if (!cooldowns.containsKey(playerId)) {
            return 0;
        }
        
        long lastPlayed = cooldowns.get(playerId);
        long currentTime = System.currentTimeMillis();
        long cooldownMillis = cooldownSeconds * 1000L;
        long elapsed = currentTime - lastPlayed;
        
        if (elapsed >= cooldownMillis) {
            return 0;
        }
        
        return (int) ((cooldownMillis - elapsed) / 1000L) + 1;
    }

    /**
     * Set cooldown for player.
     * Called when player starts a game.
     */
    public void setCooldown(UUID playerId) {
        cooldowns.put(playerId, System.currentTimeMillis());
    }

    /**
     * Remove cooldown for player.
     * Used for admin bypass or cleanup.
     */
    public void removeCooldown(UUID playerId) {
        cooldowns.remove(playerId);
    }

    /**
     * Clear all cooldowns.
     * Used on plugin reload or disable.
     */
    public void clearAll() {
        cooldowns.clear();
    }
}
