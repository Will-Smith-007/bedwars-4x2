package de.will_smith_007.bedwars.listeners.game;

import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockSpreadEvent;

public class BlockSpreadListener implements Listener {

    @EventHandler
    public void onFireSpread(@NonNull BlockSpreadEvent blockSpreadEvent) {
        blockSpreadEvent.setCancelled(true);
    }
}
