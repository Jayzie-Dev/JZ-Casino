package me.jz.casino.util;

import java.util.concurrent.ThreadLocalRandom;
import java.util.List;
import java.util.Map;

/**
 * Thread-safe random utility for casino games.
 * Uses ThreadLocalRandom for server-safe, non-predictable RNG.
 */
public class RandomUtil {

    /**
     * Get random integer between min (inclusive) and max (exclusive).
     * Thread-safe implementation using ThreadLocalRandom.
     */
    public static int nextInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    /**
     * Get random integer between 0 (inclusive) and bound (exclusive).
     */
    public static int nextInt(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }

    /**
     * Get random double between 0.0 (inclusive) and 1.0 (exclusive).
     */
    public static double nextDouble() {
        return ThreadLocalRandom.current().nextDouble();
    }

    /**
     * Get random boolean with 50/50 probability.
     */
    public static boolean nextBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    /**
     * Select weighted random item from map.
     * Used for slot machine symbol selection with configurable weights.
     * 
     * @param weights Map of items to their weights
     * @return Selected item based on weighted probability
     */
    public static <T> T weightedRandom(Map<T, Integer> weights) {
        int totalWeight = weights.values().stream().mapToInt(Integer::intValue).sum();
        int random = nextInt(totalWeight);
        
        int currentWeight = 0;
        for (Map.Entry<T, Integer> entry : weights.entrySet()) {
            currentWeight += entry.getValue();
            if (random < currentWeight) {
                return entry.getKey();
            }
        }
        
        // Fallback to first entry (should never happen with valid weights)
        return weights.keySet().iterator().next();
    }

    /**
     * Get random element from list.
     */
    public static <T> T randomElement(List<T> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Cannot select from empty list");
        }
        return list.get(nextInt(list.size()));
    }
}
