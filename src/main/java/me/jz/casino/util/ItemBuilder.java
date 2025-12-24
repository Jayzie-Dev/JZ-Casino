package me.jz.casino.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

/**
 * Fluent builder for creating ItemStacks with metadata.
 * Used extensively in GUI creation.
 */
public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(Material material, int amount) {
        this.item = new ItemStack(material, amount);
        this.meta = item.getItemMeta();
    }

    /**
     * Set display name with color code support.
     */
    public ItemBuilder name(String name) {
        if (meta != null) {
            meta.setDisplayName(name);
        }
        return this;
    }

    /**
     * Set lore lines with color code support.
     */
    public ItemBuilder lore(String... lore) {
        if (meta != null) {
            meta.setLore(Arrays.asList(lore));
        }
        return this;
    }

    /**
     * Set lore from list.
     */
    public ItemBuilder lore(List<String> lore) {
        if (meta != null) {
            meta.setLore(lore);
        }
        return this;
    }

    /**
     * Set item amount.
     */
    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    /**
     * Make item glow (using enchantment effect).
     * Uses a hidden enchantment with flag to create glow effect.
     */
    public ItemBuilder glow() {
        if (meta != null) {
            // Add hidden enchantment for glow effect
            meta.addEnchant(org.bukkit.enchantments.Enchantment.LURE, 1, true);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    /**
     * Build and return the ItemStack.
     */
    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }
}
