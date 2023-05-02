package de.will_smith_007.bedwars.listeners.setup;

import de.will_smith_007.bedwars.commands.BedWarsCommand;
import de.will_smith_007.bedwars.enums.Message;
import de.will_smith_007.bedwars.setup.BedWarsSetup;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.NonNull;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Map;

public class BedWarsWorldSetupListener implements Listener {

    private final Map<Player, BedWarsSetup> PLAYERS_IN_SETUP = BedWarsCommand.getPLAYERS_IN_SETUP();
    private final JavaPlugin JAVA_PLUGIN;
    private final BukkitScheduler BUKKIT_SCHEDULER = Bukkit.getScheduler();

    public BedWarsWorldSetupListener(@NonNull JavaPlugin javaPlugin) {
        JAVA_PLUGIN = javaPlugin;
    }

    @EventHandler
    public void onAsyncChat(@NonNull AsyncChatEvent asyncChatEvent) {
        final Player player = asyncChatEvent.getPlayer();
        if (!PLAYERS_IN_SETUP.containsKey(player)) return;

        final BedWarsSetup bedWarsSetup = PLAYERS_IN_SETUP.get(player);
        if (bedWarsSetup.getSetupAction() != BedWarsSetup.SetupAction.WORLD_SETUP) return;

        asyncChatEvent.setCancelled(true);

        final String message = ((TextComponent) asyncChatEvent.message()).content();
        BUKKIT_SCHEDULER.runTask(JAVA_PLUGIN, () -> {
            // Can't load worlds async
            final World world = Bukkit.createWorld(new WorldCreator(message));

            if (world == null) {
                player.sendPlainMessage(Message.PREFIX + "§cWorld §e" + message + "§c could not be found.");
                return;
            }

            player.teleport(world.getSpawnLocation());
            player.setGameMode(GameMode.CREATIVE);
            bedWarsSetup.setGameWorld(world);
            bedWarsSetup.setSetupAction(BedWarsSetup.SetupAction.BED_SETUP);

            PLAYERS_IN_SETUP.put(player, bedWarsSetup);

            player.sendPlainMessage(Message.PREFIX + "§aYou've successfully added the map §e" + message + "§a!");
            player.sendPlainMessage(Message.PREFIX + "§aNow set the team beds by using the command §e/bw setBed " +
                    "[Team]§a and look at the bed block and use the command §e/bw setup next§a if you're done.");
        });
    }
}
