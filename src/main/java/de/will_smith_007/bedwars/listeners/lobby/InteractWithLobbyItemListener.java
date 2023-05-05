package de.will_smith_007.bedwars.listeners.lobby;

import de.will_smith_007.bedwars.inventories.interfaces.IBedWarsInventory;
import de.will_smith_007.bedwars.lobby.enums.LobbyItem;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class InteractWithLobbyItemListener implements Listener {

    private final IBedWarsInventory BED_WARS_INVENTORY;

    public InteractWithLobbyItemListener(@NonNull IBedWarsInventory bedWarsInventory) {
        BED_WARS_INVENTORY = bedWarsInventory;
    }

    @EventHandler
    public void onPlayerInteract(@NonNull PlayerInteractEvent playerInteractEvent) {
        if (playerInteractEvent.getHand() != EquipmentSlot.HAND) return;
        if (!playerInteractEvent.getAction().isRightClick()) return;
        if (!playerInteractEvent.hasItem()) return;

        final ItemStack itemStack = playerInteractEvent.getItem();

        if (itemStack == null) return;
        if (!itemStack.equals(LobbyItem.TEAM_SELECTOR.getItemStack())) return;

        final Player player = playerInteractEvent.getPlayer();
        playerInteractEvent.setCancelled(true);

        BED_WARS_INVENTORY.openInventory(player);
    }
}
