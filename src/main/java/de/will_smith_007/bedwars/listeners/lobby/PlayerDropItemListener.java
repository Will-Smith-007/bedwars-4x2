package de.will_smith_007.bedwars.listeners.lobby;

import de.will_smith_007.bedwars.lobby.enums.LobbyItem;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDropItemListener implements Listener {

    @EventHandler
    public void onPlayerDropItem(@NonNull PlayerDropItemEvent playerDropItemEvent) {
        final ItemStack itemStack = playerDropItemEvent.getItemDrop().getItemStack();
        if (itemStack.equals(LobbyItem.TEAM_SELECTOR.getItemStack()))
            playerDropItemEvent.setCancelled(true);
    }
}
