package me.jz.casino.util;

import java.text.DecimalFormat;

/**
 * Utility for formatting numbers and currency in casino displays.
 */
public class NumberUtil {

    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");
    private static final DecimalFormat COMPACT_FORMAT = new DecimalFormat("#,##0.#");

    /**
     * Format money amount with 2 decimal places.
     * Example: 1234.5 -> "1,234.50"
     */
    public static String formatMoney(double amount) {
        return MONEY_FORMAT.format(amount);
    }

    /**
     * Format number with compact notation.
     * Example: 1234.5 -> "1,234.5"
     */
    public static String formatCompact(double number) {
        return COMPACT_FORMAT.format(number);
    }

    /**
     * Format percentage with 1 decimal place.
     * Example: 0.05 -> "5.0%"
     */
    public static String formatPercent(double decimal) {
        return String.format("%.1f%%", decimal * 100);
    }

    /**
     * Clamp value between min and max.
     */
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Round to 2 decimal places for money calculations.
     * Prevents floating point precision issues.
     */
    public static double roundMoney(double amount) {
        return Math.round(amount * 100.0) / 100.0;
    }
}
