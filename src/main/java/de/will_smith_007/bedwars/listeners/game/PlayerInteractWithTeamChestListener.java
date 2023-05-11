package de.will_smith_007.bedwars.listeners.game;

import com.google.inject.Inject;
import de.will_smith_007.bedwars.enums.GameState;
import de.will_smith_007.bedwars.game_assets.GameAssets;
import de.will_smith_007.bedwars.teams.helper.interfaces.ITeamHelper;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Map;

/**
 * This {@link Listener} handles the {@link PlayerInteractEvent} to check
 * if the clicked block is a team chest and if the chest belongs to the player team.
 */
public class PlayerInteractWithTeamChestListener implements Listener {

    private final GameAssets gameAssets;
    private final ITeamHelper teamHelper;

    @Inject
    public PlayerInteractWithTeamChestListener(@NonNull GameAssets gameAssets,
                                               @NonNull ITeamHelper teamHelper) {
        this.gameAssets = gameAssets;
        this.teamHelper = teamHelper;
    }

    @EventHandler
    public void onPlayerInteractEvent(@NonNull PlayerInteractEvent playerInteractEvent) {
        if (playerInteractEvent.getHand() != EquipmentSlot.HAND) return;
        if (!playerInteractEvent.getAction().isRightClick()) return;

        final GameState gameState = gameAssets.getGameState();
        if (gameState == GameState.LOBBY) return;

        final Map<Location, ITeam> teamChestLocations = gameAssets.getTeamChestLocations();
        final Block clickedBlock = playerInteractEvent.getClickedBlock();

        if (clickedBlock == null) return;

        final Location clickedBlockLocation = clickedBlock.getLocation();
        if (!teamChestLocations.containsKey(clickedBlockLocation)) return;

        playerInteractEvent.setCancelled(true);

        final Player player = playerInteractEvent.getPlayer();

        teamHelper.getTeam(player).ifPresent(iTeam -> {
            final ITeam clickedLocationTeam = teamChestLocations.get(clickedBlockLocation);
            if (clickedLocationTeam != iTeam) return;

            iTeam.openTeamChest(player);
        });
    }
}
