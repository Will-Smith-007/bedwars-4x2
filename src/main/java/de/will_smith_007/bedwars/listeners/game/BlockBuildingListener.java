package de.will_smith_007.bedwars.listeners.game;

import de.will_smith_007.bedwars.enums.GameState;
import de.will_smith_007.bedwars.enums.Message;
import de.will_smith_007.bedwars.events.BedBreakEvent;
import de.will_smith_007.bedwars.file_config.BedWarsConfig;
import de.will_smith_007.bedwars.game_assets.GameAssets;
import de.will_smith_007.bedwars.teams.enums.BedWarsTeam;
import de.will_smith_007.bedwars.teams.helper.interfaces.ITeamHelper;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.PluginManager;

import java.util.Optional;
import java.util.Set;

public class BlockBuildingListener implements Listener {

    private final GameAssets GAME_ASSETS;
    private final Set<Block> BUILDING_BLOCKS;
    private final PluginManager PLUGIN_MANAGER = Bukkit.getPluginManager();
    private final ITeamHelper TEAM_HELPER;
    private final BedWarsTeam[] BED_WARS_TEAMS = BedWarsTeam.values();
    private final BedWarsConfig BED_WARS_CONFIG = BedWarsConfig.getInstance();

    public BlockBuildingListener(@NonNull GameAssets gameAssets,
                                 @NonNull ITeamHelper teamHelper) {
        GAME_ASSETS = gameAssets;
        BUILDING_BLOCKS = gameAssets.getBUILDING_BLOCKS();
        TEAM_HELPER = teamHelper;
    }

    @EventHandler
    public void onBlockPlace(@NonNull BlockPlaceEvent blockPlaceEvent) {
        final GameState gameState = GAME_ASSETS.getGameState();
        final Player player = blockPlaceEvent.getPlayer();

        switch (gameState) {
            case INGAME, PROTECTION -> {
                final Block block = blockPlaceEvent.getBlockPlaced();
                BUILDING_BLOCKS.add(block);
            }
            default -> {
                if (player.isOp()) return;
                blockPlaceEvent.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(@NonNull BlockBreakEvent blockBreakEvent) {
        final GameState gameState = GAME_ASSETS.getGameState();
        final Player player = blockBreakEvent.getPlayer();

        switch (gameState) {
            case INGAME, PROTECTION -> {
                final Block block = blockBreakEvent.getBlock();
                final Material material = block.getType();

                if (material.toString().endsWith("BED")) {
                    blockBreakEvent.setCancelled(false);

                    final Location blockLocation = block.getLocation();
                    final World playerWorld = player.getWorld();

                    final Optional<ITeam> optionalITeam = TEAM_HELPER.getTeam(player);
                    optionalITeam.ifPresent(team -> {
                        for (BedWarsTeam bedwarsTeam : BED_WARS_TEAMS) {
                            final Location configuredBedLocation = BED_WARS_CONFIG.getBedLocation(bedwarsTeam, playerWorld);

                            if (configuredBedLocation.distance(blockLocation) > 1) continue;

                            final ITeam bedTeam = bedwarsTeam.getTeam();
                            if (team == bedTeam) {
                                player.sendPlainMessage(Message.PREFIX + "§cYou can't break your own bed.");
                                blockBreakEvent.setCancelled(true);
                                return;
                            }

                            blockBreakEvent.setDropItems(false);
                            PLUGIN_MANAGER.callEvent(new BedBreakEvent(player, blockLocation, bedTeam));
                        }
                    });
                    return;
                }

                if (!BUILDING_BLOCKS.contains(block)) {
                    blockBreakEvent.setCancelled(true);
                    return;
                }

                blockBreakEvent.setCancelled(false);

                BUILDING_BLOCKS.remove(block);
            }
            default -> {
                if (!player.isOp()) blockBreakEvent.setCancelled(true);
            }
        }
    }
}
