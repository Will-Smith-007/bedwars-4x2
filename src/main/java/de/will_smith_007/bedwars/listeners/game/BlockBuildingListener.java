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

    private final GameAssets gameAssets;
    private final Set<Block> builtBlocks;
    private final PluginManager pluginManager = Bukkit.getPluginManager();
    private final ITeamHelper teamHelper;
    private final BedWarsTeam[] bedWarsTeams = BedWarsTeam.values();
    private final BedWarsConfig bedWarsConfig = BedWarsConfig.getInstance();

    public BlockBuildingListener(@NonNull GameAssets gameAssets,
                                 @NonNull ITeamHelper teamHelper) {
        this.gameAssets = gameAssets;
        this.builtBlocks = gameAssets.getBuiltBlocks();
        this.teamHelper = teamHelper;
    }

    @EventHandler
    public void onBlockPlace(@NonNull BlockPlaceEvent blockPlaceEvent) {
        final GameState gameState = gameAssets.getGameState();
        final Player player = blockPlaceEvent.getPlayer();

        switch (gameState) {
            case INGAME, PROTECTION -> {
                final Block block = blockPlaceEvent.getBlockPlaced();
                builtBlocks.add(block);
            }
            default -> {
                if (player.isOp()) return;
                blockPlaceEvent.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(@NonNull BlockBreakEvent blockBreakEvent) {
        final GameState gameState = gameAssets.getGameState();
        final Player player = blockBreakEvent.getPlayer();

        switch (gameState) {
            case INGAME, PROTECTION -> {
                final Block block = blockBreakEvent.getBlock();
                final Material material = block.getType();

                if (material.toString().endsWith("BED")) {
                    blockBreakEvent.setCancelled(false);

                    final Location blockLocation = block.getLocation();
                    final World playerWorld = player.getWorld();

                    final Optional<ITeam> optionalITeam = teamHelper.getTeam(player);
                    optionalITeam.ifPresent(team -> {
                        for (BedWarsTeam bedwarsTeam : bedWarsTeams) {
                            final Location configuredBedLocation = bedWarsConfig.getBedLocation(bedwarsTeam, playerWorld);

                            if (configuredBedLocation.distance(blockLocation) > 1) continue;

                            final ITeam bedTeam = bedwarsTeam.getTeam();
                            if (team == bedTeam) {
                                player.sendPlainMessage(Message.PREFIX + "§cYou can't break your own bed.");
                                blockBreakEvent.setCancelled(true);
                                return;
                            }

                            blockBreakEvent.setDropItems(false);
                            pluginManager.callEvent(new BedBreakEvent(player, blockLocation, bedTeam));
                        }
                    });
                    return;
                }

                if (!builtBlocks.contains(block)) {
                    blockBreakEvent.setCancelled(true);
                    return;
                }

                blockBreakEvent.setCancelled(false);

                builtBlocks.remove(block);
            }
            default -> {
                if (!player.isOp()) blockBreakEvent.setCancelled(true);
            }
        }
    }
}
