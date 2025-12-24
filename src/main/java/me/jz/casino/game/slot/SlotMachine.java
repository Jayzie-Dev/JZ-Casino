package me.jz.casino.game.slot;

import me.jz.casino.config.ConfigManager;
import me.jz.casino.util.RandomUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Core slot machine logic with weighted RNG.
 * Handles symbol selection, payout calculation, and house edge.
 */
public class SlotMachine {

    private final List<SlotSymbol> symbols;
    private final Map<SlotSymbol, Integer> symbolWeights;
    private final double houseEdge;

    public SlotMachine(ConfigManager config) {
        this.symbols = new ArrayList<>();
        this.symbolWeights = new HashMap<>();
        this.houseEdge = config.getSlotHouseEdge() / 100.0; // Convert percentage to decimal

        // Load symbols from config
        Map<String, double[]> symbolData = config.getSlotSymbols();
        for (Map.Entry<String, double[]> entry : symbolData.entrySet()) {
            String name = entry.getKey();
            int weight = (int) entry.getValue()[0];
            double multiplier = entry.getValue()[1];
            String displayName = config.getSymbolDisplayName(name);

            SlotSymbol symbol = new SlotSymbol(name, weight, multiplier, displayName);
            symbols.add(symbol);
            symbolWeights.put(symbol, weight);
        }
    }

    /**
     * Spin the slot machine and generate result.
     * Uses weighted random selection for each reel.
     * 
     * @param betAmount The amount wagered
     * @return SlotResult containing symbols and payout
     */
    public SlotResult spin(double betAmount) {
        // Select 3 symbols using weighted random
        SlotSymbol[] reels = new SlotSymbol[3];
        for (int i = 0; i < 3; i++) {
            reels[i] = selectRandomSymbol();
        }

        // Calculate payout
        double payout = calculatePayout(reels, betAmount);
        boolean isWin = payout > 0;

        return new SlotResult(reels, payout, isWin);
    }

    /**
     * Select random symbol based on configured weights.
     * Uses ThreadLocalRandom for server-safe RNG.
     */
    private SlotSymbol selectRandomSymbol() {
        return RandomUtil.weightedRandom(symbolWeights);
    }

    /**
     * Calculate payout based on matching symbols.
     * All three symbols must match to win.
     * House edge is applied to reduce payout slightly.
     */
    private double calculatePayout(SlotSymbol[] reels, double betAmount) {
        // Check if all three symbols match
        if (!reels[0].getName().equals(reels[1].getName()) 
            || !reels[1].getName().equals(reels[2].getName())) {
            return 0.0; // No match, no payout
        }

        // Calculate base payout from multiplier
        double multiplier = reels[0].getMultiplier();
        double basePayout = betAmount * multiplier;

        // Apply house edge (reduces payout)
        double adjustedPayout = basePayout * (1.0 - houseEdge);

        return Math.max(0, adjustedPayout);
    }

    /**
     * Get all available symbols.
     */
    public List<SlotSymbol> getSymbols() {
        return new ArrayList<>(symbols);
    }

    /**
     * Calculate theoretical RTP (Return to Player) percentage.
     * Used for statistics and balancing.
     */
    public double calculateRTP() {
        int totalWeight = symbolWeights.values().stream().mapToInt(Integer::intValue).sum();
        double expectedReturn = 0.0;

        for (SlotSymbol symbol : symbols) {
            double probability = (double) symbol.getWeight() / totalWeight;
            // Probability of getting 3 matching symbols
            double matchProbability = Math.pow(probability, 3);
            double contribution = matchProbability * symbol.getMultiplier();
            expectedReturn += contribution;
        }

        // Apply house edge
        return expectedReturn * (1.0 - houseEdge);
    }
}
