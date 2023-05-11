package de.will_smith_007.bedwars.listeners.game;

import com.google.inject.Inject;
import de.will_smith_007.bedwars.enums.GameState;
import de.will_smith_007.bedwars.enums.Message;
import de.will_smith_007.bedwars.events.BedBreakEvent;
import de.will_smith_007.bedwars.file_config.BedWarsConfig;
import de.will_smith_007.bedwars.game_assets.GameAssets;
import de.will_smith_007.bedwars.shop.enums.ShopItem;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BlockBuildingListener implements Listener {

    private final GameAssets gameAssets;
    private final Set<Block> builtBlocks;
    private final PluginManager pluginManager = Bukkit.getPluginManager();
    private final ITeamHelper teamHelper;
    private final BedWarsTeam[] bedWarsTeams = BedWarsTeam.values();
    private final BedWarsConfig bedWarsConfig;

    @Inject
    public BlockBuildingListener(@NonNull GameAssets gameAssets,
                                 @NonNull ITeamHelper teamHelper,
                                 @NonNull BedWarsConfig bedWarsConfig) {
        this.gameAssets = gameAssets;
        this.builtBlocks = gameAssets.getBuiltBlocks();
        this.teamHelper = teamHelper;
        this.bedWarsConfig = bedWarsConfig;
    }

    @EventHandler
    public void onBlockPlace(@NonNull BlockPlaceEvent blockPlaceEvent) {
        final GameState gameState = gameAssets.getGameState();
        final Player player = blockPlaceEvent.getPlayer();

        /*
         If the game state is ingame or protection then the block is going to be added to the built blocks set.
         Otherwise, the event is going to be cancelled if the player isn't a server operator.
         */
        switch (gameState) {
            case INGAME, PROTECTION -> {
                final Block block = blockPlaceEvent.getBlockPlaced();
                builtBlocks.add(block);

                // Team Chest placing handling
                final ItemStack possibleTeamChestItemStack = player.getInventory().getItemInMainHand();
                if (!possibleTeamChestItemStack.equals(ShopItem.TEAM_CHEST.getItemStack())) return;

                teamHelper.getTeam(player).ifPresent(iTeam -> {
                    final Map<Location, ITeam> teamChestLocations = gameAssets.getTeamChestLocations();
                    teamChestLocations.put(block.getLocation(), iTeam);
                });
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

                // Checks if the block is a team bed, then throw a new BedBreakEvent
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

                /*
                 If it's not a bed and the block isn't in the block built set, then cancel.
                 Otherwise, remove the block from this set and allow this event.
                 */
                if (!builtBlocks.contains(block)) {
                    blockBreakEvent.setCancelled(true);
                    return;
                }

                // If the broken block was a team chest, remove it from team chest locations
                final Map<Location, ITeam> teamChestLocations = gameAssets.getTeamChestLocations();
                teamChestLocations.remove(block.getLocation());

                blockBreakEvent.setCancelled(false);

                builtBlocks.remove(block);
            }
            default -> {
                if (!player.isOp()) blockBreakEvent.setCancelled(true);
            }
        }
    }
}
