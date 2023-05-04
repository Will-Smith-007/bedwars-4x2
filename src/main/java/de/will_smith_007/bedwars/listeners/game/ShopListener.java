package de.will_smith_007.bedwars.listeners.game;

import de.will_smith_007.bedwars.inventories.interfaces.IBedWarsInventory;
import lombok.NonNull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class ShopListener implements Listener {

    private final IBedWarsInventory BED_WARS_INVENTORY;

    public ShopListener(@NonNull IBedWarsInventory bedWarsInventory) {
        BED_WARS_INVENTORY = bedWarsInventory;
    }

    @EventHandler
    public void onPlayerInteractAtEntity(@NonNull PlayerInteractAtEntityEvent playerInteractAtEntityEvent) {
        if (playerInteractAtEntityEvent.getHand() != EquipmentSlot.HAND) return;
        final Entity rightClickedEntity = playerInteractAtEntityEvent.getRightClicked();
        if (rightClickedEntity.getType() != EntityType.VILLAGER) return;

        final Player player = playerInteractAtEntityEvent.getPlayer();

        BED_WARS_INVENTORY.openInventory(player);
        playerInteractAtEntityEvent.setCancelled(true);
    }
}
