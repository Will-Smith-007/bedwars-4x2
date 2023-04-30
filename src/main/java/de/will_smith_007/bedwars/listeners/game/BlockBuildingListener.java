package de.will_smith_007.bedwars.listeners.game;

import de.will_smith_007.bedwars.enums.GameState;
import de.will_smith_007.bedwars.game_assets.GameAssets;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Set;

public class BlockBuildingListener implements Listener {

    private final GameAssets GAME_ASSETS;
    private final Set<Block> BUILDING_BLOCKS;

    public BlockBuildingListener(@NonNull GameAssets gameAssets) {
        GAME_ASSETS = gameAssets;
        BUILDING_BLOCKS = gameAssets.getBUILDING_BLOCKS();
    }

    @EventHandler
    public void onBlockPlace(@NonNull BlockPlaceEvent blockPlaceEvent) {
        final GameState gameState = GAME_ASSETS.getGameState();

        switch (gameState) {
            case INGAME, PROTECTION, ENDING -> {
                final Block block = blockPlaceEvent.getBlockPlaced();
                BUILDING_BLOCKS.add(block);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(@NonNull BlockBreakEvent blockBreakEvent) {
        final GameState gameState = GAME_ASSETS.getGameState();

        switch (gameState) {
            case INGAME, PROTECTION, ENDING -> {
                final Block block = blockBreakEvent.getBlock();
                final Material material = block.getType();

                if (!BUILDING_BLOCKS.contains(block) && !material.toString().endsWith("BED")) {
                    blockBreakEvent.setCancelled(true);
                    return;
                }

                blockBreakEvent.setCancelled(false);

                //TODO: If block is bed, throw BedBreakEvent and check locations.

                BUILDING_BLOCKS.remove(block);
            }
        }
    }
}
