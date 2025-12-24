package me.jz.casino.manager;

import me.jz.casino.game.CasinoGame;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages active game sessions for players.
 * Prevents multiple simultaneous games and tracks game state.
 */
public class PlayerDataManager {

    private final Map<UUID, CasinoGame> activeGames = new HashMap<>();

    /**
     * Check if player has an active game.
     */
    public boolean hasActiveGame(UUID playerId) {
        return activeGames.containsKey(playerId);
    }

    /**
     * Get player's active game.
     * Returns null if no active game.
     */
    public CasinoGame getActiveGame(UUID playerId) {
        return activeGames.get(playerId);
    }

    /**
     * Set player's active game.
     * Called when game starts.
     */
    public void setActiveGame(UUID playerId, CasinoGame game) {
        activeGames.put(playerId, game);
    }

    /**
     * Remove player's active game.
     * Called when game ends or player disconnects.
     */
    public void removeActiveGame(UUID playerId) {
        activeGames.remove(playerId);
    }

    /**
     * Clear all active games.
     * Used on plugin disable to cleanup.
     */
    public void clearAll() {
        activeGames.clear();
    }

    /**
     * Get count of active games.
     * Used for statistics.
     */
    public int getActiveGameCount() {
        return activeGames.size();
    }
}
