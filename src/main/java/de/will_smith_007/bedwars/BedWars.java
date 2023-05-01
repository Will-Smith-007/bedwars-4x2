package de.will_smith_007.bedwars;

import de.will_smith_007.bedwars.commands.BedWarsCommand;
import de.will_smith_007.bedwars.commands.StartCommand;
import de.will_smith_007.bedwars.file_config.BedWarsConfig;
import de.will_smith_007.bedwars.game_assets.GameAssets;
import de.will_smith_007.bedwars.listeners.PlayerConnectionListener;
import de.will_smith_007.bedwars.listeners.game.*;
import de.will_smith_007.bedwars.listeners.setup.BedWarsSpawnerSetupListener;
import de.will_smith_007.bedwars.listeners.setup.BedWarsWorldSetupListener;
import de.will_smith_007.bedwars.lobby_countdown.LobbyCountdownHelper;
import de.will_smith_007.bedwars.schedulers.EndingCountdownScheduler;
import de.will_smith_007.bedwars.schedulers.LobbyCountdownScheduler;
import de.will_smith_007.bedwars.schedulers.ProtectionCountdownScheduler;
import de.will_smith_007.bedwars.schedulers.SpawnerScheduler;
import de.will_smith_007.bedwars.scoreboard.ScoreboardManager;
import de.will_smith_007.bedwars.spawner.provider.SpawnerProvider;
import de.will_smith_007.bedwars.teams.helper.TeamHelper;
import de.will_smith_007.bedwars.teams.parser.TeamParser;
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

        final SpawnerProvider spawnerProvider = new SpawnerProvider(gameAssets);

        final ProtectionCountdownScheduler protectionCountdownScheduler = new ProtectionCountdownScheduler(this);
        final SpawnerScheduler spawnerScheduler = new SpawnerScheduler(this, spawnerProvider);
        final EndingCountdownScheduler endingCountdownScheduler = new EndingCountdownScheduler(this);

        final TeamHelper teamHelper = new TeamHelper(gameAssets, endingCountdownScheduler);
        final ScoreboardManager scoreboardManager = new ScoreboardManager(gameAssets, teamHelper);

        final LobbyCountdownScheduler lobbyCountdownScheduler = new LobbyCountdownScheduler(
                this,
                teamHelper,
                gameAssets,
                protectionCountdownScheduler,
                spawnerScheduler,
                scoreboardManager
        );

        final LobbyCountdownHelper lobbyCountdownHelper = new LobbyCountdownHelper(lobbyCountdownScheduler);

        registerCommand("bedwars", new BedWarsCommand(teamParser));
        registerCommand("start", new StartCommand(lobbyCountdownHelper));

        registerListeners(
                new PlayerConnectionListener(gameAssets, lobbyCountdownHelper, teamHelper, scoreboardManager),
                new BedWarsSpawnerSetupListener(),
                new BedWarsWorldSetupListener(),
                new BlockBuildingListener(gameAssets),
                new TeamDamageListener(gameAssets, teamHelper),
                new PlayerDeathListener(gameAssets, teamHelper, scoreboardManager),
                new EntitySpawnListener(),
                new BedBreakListener(scoreboardManager),
                new BlockSpreadListener(),
                new ExplosionPrimeListener()
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
