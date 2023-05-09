package de.will_smith_007.bedwars.listeners.setup;

import de.will_smith_007.bedwars.commands.BedWarsCommand;
import de.will_smith_007.bedwars.enums.Message;
import de.will_smith_007.bedwars.setup.BedWarsSetup;
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
 * This {@link Listener} handles the {@link PlayerInteractEvent} on
 * item spawner setup.
 */
public class BedWarsSpawnerSetupListener implements Listener {

    private final Map<Player, BedWarsSetup> playersInSetup = BedWarsCommand.getPLAYERS_IN_SETUP();

    @EventHandler
    public void onPlayerInteractEvent(@NonNull PlayerInteractEvent playerInteractEvent) {
        final Player player = playerInteractEvent.getPlayer();

        if (!playersInSetup.containsKey(player)) return;
        if (!playerInteractEvent.getAction().isRightClick()) return;
        if (playerInteractEvent.getHand() == EquipmentSlot.OFF_HAND) return;

        final BedWarsSetup bedWarsSetup = playersInSetup.get(player);
        final BedWarsSetup.SetupAction setupAction = bedWarsSetup.getSetupAction();
        BedWarsSetup.SpawnerType spawnerType = null;

        // Spawner type initialization based on the current setup action step
        switch (setupAction) {
            case BRONZE_SPAWNER_SETUP -> spawnerType = BedWarsSetup.SpawnerType.BRONZE;
            case IRON_SPAWNER_SETUP -> spawnerType = BedWarsSetup.SpawnerType.IRON;
            case GOLD_SPAWNER_SETUP -> spawnerType = BedWarsSetup.SpawnerType.GOLD;
        }

        if (spawnerType == null) return;

        final Block clickedBlock = playerInteractEvent.getClickedBlock();
        if (clickedBlock == null) return;
        final Location blockLocation = clickedBlock.getLocation();

        // Adds the clicked block location to the map of configured spawners
        bedWarsSetup.addSpawner(spawnerType, blockLocation);
        playersInSetup.put(player, bedWarsSetup);

        player.sendPlainMessage(Message.PREFIX + "§aYou've added a§e spawner§a.");
    }
}
