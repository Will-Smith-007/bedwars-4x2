package de.will_smith_007.bedwars.listeners.lobby;

import com.google.inject.Inject;
import com.google.inject.name.Named;
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

    private final IBedWarsInventory bedWarsInventory;

    @Inject
    public InteractWithLobbyItemListener(@NonNull @Named("TeamSelectorInventory") IBedWarsInventory bedWarsInventory) {
        this.bedWarsInventory = bedWarsInventory;
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

        bedWarsInventory.openInventory(player);
    }
}
