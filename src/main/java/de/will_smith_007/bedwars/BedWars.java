package de.will_smith_007.bedwars;

import de.will_smith_007.bedwars.commands.BedWarsCommand;
import de.will_smith_007.bedwars.file_config.BedWarsConfig;
import de.will_smith_007.bedwars.game_assets.GameAssets;
import de.will_smith_007.bedwars.listeners.setup.BedWarsSpawnerSetupListener;
import de.will_smith_007.bedwars.listeners.setup.BedWarsWorldSetupListener;
import de.will_smith_007.bedwars.schedulers.EndingCountdownScheduler;
import de.will_smith_007.bedwars.schedulers.LobbyCountdownScheduler;
import de.will_smith_007.bedwars.schedulers.ProtectionCountdownScheduler;
import de.will_smith_007.bedwars.schedulers.SpawnerScheduler;
import de.will_smith_007.bedwars.spawner.provider.SpawnerProvider;
import de.will_smith_007.bedwars.teams.parser.TeamParser;
import de.will_smith_007.bedwars.teams.helper.TeamHelper;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BedWars extends JavaPlugin {

    @Override
    public void onEnable() {
        final BedWarsConfig bedWarsConfig = new BedWarsConfig(this);
        final GameAssets gameAssets = new GameAssets();
        final TeamParser teamParser = new TeamParser();
        final TeamHelper teamHelper = new TeamHelper();

        final SpawnerProvider spawnerProvider = new SpawnerProvider(gameAssets);

        final LobbyCountdownScheduler lobbyCountdownScheduler = new LobbyCountdownScheduler(this);
        final ProtectionCountdownScheduler protectionCountdownScheduler = new ProtectionCountdownScheduler(this);
        final SpawnerScheduler spawnerScheduler = new SpawnerScheduler(this, spawnerProvider);
        final EndingCountdownScheduler endingCountdownScheduler = new EndingCountdownScheduler(this);

        registerCommand("bedwars", new BedWarsCommand(teamParser));

        registerListeners(
                new BedWarsSpawnerSetupListener(),
                new BedWarsWorldSetupListener()
        );

        getLogger().info("BedWars was started.");
    }

    @Override
    public void onDisable() {
        getLogger().info("BedWars was stopped.");
    }

    private void registerListeners(Listener @NonNull ... listeners) {
        final PluginManager pluginManager = Bukkit.getPluginManager();
        for (Listener listener : listeners) {
            pluginManager.registerEvents(listener, this);
        }
    }

    private void registerCommand(@NonNull String command, @NonNull CommandExecutor commandExecutor) {
        final PluginCommand pluginCommand = getCommand(command);
        if (pluginCommand == null) return;
        pluginCommand.setExecutor(commandExecutor);
    }
}
