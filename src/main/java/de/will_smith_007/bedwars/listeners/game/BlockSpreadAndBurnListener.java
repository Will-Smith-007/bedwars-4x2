package de.will_smith_007.bedwars.listeners.game;

import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockSpreadEvent;

/**
 * This {@link Listener} handles the {@link BlockSpreadEvent} and the {@link BlockBurnEvent}
 * to deny fire spreading and block burn.
 */
public class BlockSpreadAndBurnListener implements Listener {

    @EventHandler
    public void onFireSpread(@NonNull BlockSpreadEvent blockSpreadEvent) {
        blockSpreadEvent.setCancelled(true);
    }

    @EventHandler
    public void onBlockBurn(@NonNull BlockBurnEvent blockBurnEvent) {
        blockBurnEvent.setCancelled(true);
    }
}
