package de.will_smith_007.bedwars.listeners.game;

import com.google.inject.Inject;
import de.will_smith_007.bedwars.game_assets.GameAssets;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

/**
 * This {@link Listener} handles the {@link PlayerInteractEvent} to deny
 * interaction with interactable blocks.
 */
public class PlayerInteractWithBlocksListener implements Listener {

    private final GameAssets gameAssets;

    @Inject
    public PlayerInteractWithBlocksListener(@NonNull GameAssets gameAssets) {
        this.gameAssets = gameAssets;
    }

    @EventHandler
    public void onPlayerInteract(@NonNull PlayerInteractEvent playerInteractEvent) {
        if (playerInteractEvent.getHand() != EquipmentSlot.HAND) return;
        if (playerInteractEvent.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        final Block clickedBlock = playerInteractEvent.getClickedBlock();
        if (clickedBlock == null) return;

        final Material material = clickedBlock.getType();
        if (!material.isInteractable()) return;

        if (!gameAssets.getBuiltBlocks().contains(clickedBlock))
            playerInteractEvent.setUseInteractedBlock(Event.Result.DENY);
    }
}
