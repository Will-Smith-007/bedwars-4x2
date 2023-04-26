package de.will_smith_007.bedwars.listeners;

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

public class BedWarsSpawnerSetupListener implements Listener {

    final Map<Player, BedWarsSetup> PLAYERS_IN_SETUP = BedWarsCommand.getPLAYERS_IN_SETUP();

    @EventHandler
    public void onPlayerInteractEvent(@NonNull PlayerInteractEvent playerInteractEvent) {
        final Player player = playerInteractEvent.getPlayer();

        if (!PLAYERS_IN_SETUP.containsKey(player)) return;
        if (!playerInteractEvent.getAction().isRightClick()) return;
        if (playerInteractEvent.getHand() == EquipmentSlot.OFF_HAND) return;

        final BedWarsSetup bedWarsSetup = PLAYERS_IN_SETUP.get(player);
        final BedWarsSetup.SetupAction setupAction = bedWarsSetup.getSetupAction();
        BedWarsSetup.SpawnerType spawnerType = null;

        switch (setupAction) {
            case BRONZE_SPAWNER_SETUP -> spawnerType = BedWarsSetup.SpawnerType.BRONZE;
            case IRON_SPAWNER_SETUP -> spawnerType = BedWarsSetup.SpawnerType.IRON;
            case GOLD_SPAWNER_SETUP -> spawnerType = BedWarsSetup.SpawnerType.GOLD;
        }

        if (spawnerType == null) return;

        final Block clickedBlock = playerInteractEvent.getClickedBlock();
        if (clickedBlock == null) return;
        final Location blockLocation = clickedBlock.getLocation();

        bedWarsSetup.addSpawner(spawnerType, blockLocation);
        PLAYERS_IN_SETUP.put(player, bedWarsSetup);

        player.sendPlainMessage(Message.PREFIX + "§aYou've added a§e spawner§a.");
    }
}
