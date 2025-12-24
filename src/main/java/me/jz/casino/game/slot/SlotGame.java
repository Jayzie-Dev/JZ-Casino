package me.jz.casino.game.slot;

import me.jz.casino.game.CasinoGame;
import me.jz.casino.manager.CasinoManager;
import org.bukkit.entity.Player;

/**
 * Slot machine game implementation.
 * Manages game lifecycle and coordinates with GUI.
 */
public class SlotGame extends CasinoGame {

    private final CasinoManager casinoManager;
    private final SlotMachine slotMachine;
    private SlotResult result;

    public SlotGame(Player player, double betAmount, CasinoManager casinoManager, SlotMachine slotMachine) {
        super(player, betAmount);
        this.casinoManager = casinoManager;
        this.slotMachine = slotMachine;
    }

    @Override
    public void start() {
        // Generate result immediately
        // GUI will handle animation display
        this.result = slotMachine.spin(betAmount);
    }

    @Override
    public void end() {
        if (!isActive) return;
        
        setActive(false);
        
        // Pay out winnings if any
        if (result != null && result.isWin()) {
            casinoManager.endGame(player, result.getPayout());
        } else {
            casinoManager.endGame(player, 0);
        }
    }

    @Override
    public String getGameType() {
        return "Slot Machine";
    }

    public SlotResult getResult() {
        return result;
    }

    public SlotMachine getSlotMachine() {
        return slotMachine;
    }
}
