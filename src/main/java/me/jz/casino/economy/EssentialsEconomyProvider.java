package me.jz.casino.economy;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import me.jz.casino.util.NumberUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.math.BigDecimal;

/**
 * EssentialsX economy integration.
 * Direct integration without Vault dependency.
 * All transactions are synchronous to prevent race conditions.
 */
public class EssentialsEconomyProvider implements EconomyProvider {

    private final Essentials essentials;
    private final String currencySymbol;

    public EssentialsEconomyProvider(JavaPlugin plugin, String currencySymbol) {
        this.currencySymbol = currencySymbol;
        
        // Get EssentialsX plugin instance
        Essentials ess = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
        
        if (ess == null) {
            throw new IllegalStateException("EssentialsX plugin not found!");
        }
        
        this.essentials = ess;
    }

    @Override
    public boolean has(Player player, double amount) {
        if (!isAvailable()) return false;
        
        try {
            User user = essentials.getUser(player.getUniqueId());
            if (user == null) return false;
            
            BigDecimal balance = user.getMoney();
            BigDecimal required = BigDecimal.valueOf(amount);
            
            return balance.compareTo(required) >= 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public double getBalance(Player player) {
        if (!isAvailable()) return 0.0;
        
        try {
            User user = essentials.getUser(player.getUniqueId());
            if (user == null) return 0.0;
            
            return user.getMoney().doubleValue();
        } catch (Exception e) {
            return 0.0;
        }
    }

    @Override
    public boolean withdraw(Player player, double amount) {
        if (!isAvailable()) return false;
        
        try {
            // Round to prevent floating point issues
            amount = NumberUtil.roundMoney(amount);
            
            User user = essentials.getUser(player.getUniqueId());
            if (user == null) return false;
            
            // Verify sufficient funds
            if (!has(player, amount)) {
                return false;
            }
            
            // Perform withdrawal
            BigDecimal withdrawAmount = BigDecimal.valueOf(amount);
            user.takeMoney(withdrawAmount);
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deposit(Player player, double amount) {
        if (!isAvailable()) return false;
        
        try {
            // Round to prevent floating point issues
            amount = NumberUtil.roundMoney(amount);
            
            User user = essentials.getUser(player.getUniqueId());
            if (user == null) return false;
            
            // Perform deposit
            BigDecimal depositAmount = BigDecimal.valueOf(amount);
            user.giveMoney(depositAmount);
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String format(double amount) {
        return currencySymbol + NumberUtil.formatMoney(amount);
    }

    @Override
    public boolean isAvailable() {
        return essentials != null;
    }
}
