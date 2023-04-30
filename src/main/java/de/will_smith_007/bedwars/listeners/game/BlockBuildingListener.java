package de.will_smith_007.bedwars.listeners.game;

import de.will_smith_007.bedwars.enums.GameState;
import de.will_smith_007.bedwars.events.BedBreakEvent;
import de.will_smith_007.bedwars.game_assets.GameAssets;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.PluginManager;

import java.util.Set;

public class BlockBuildingListener implements Listener {

    private final GameAssets GAME_ASSETS;
    private final Set<Block> BUILDING_BLOCKS;
    private final PluginManager PLUGIN_MANAGER = Bukkit.getPluginManager();

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
            default -> blockPlaceEvent.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(@NonNull BlockBreakEvent blockBreakEvent) {
        final GameState gameState = GAME_ASSETS.getGameState();

        switch (gameState) {
            case INGAME, PROTECTION, ENDING -> {
                final Block block = blockBreakEvent.getBlock();
                final Material material = block.getType();

                if (material.toString().endsWith("BED")) {
                    blockBreakEvent.setCancelled(false);
                    final Location blockLocation = block.getLocation();
                    final Player player = blockBreakEvent.getPlayer();
                    PLUGIN_MANAGER.callEvent(new BedBreakEvent(player, blockLocation));
                    return;
                }

                if (!BUILDING_BLOCKS.contains(block)) {
                    blockBreakEvent.setCancelled(true);
                    return;
                }

                blockBreakEvent.setCancelled(false);

                BUILDING_BLOCKS.remove(block);
            }
        }
    }
}
