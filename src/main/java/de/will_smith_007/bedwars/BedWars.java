package de.will_smith_007.bedwars;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.will_smith_007.bedwars.commands.BedWarsCommand;
import de.will_smith_007.bedwars.commands.StartCommand;
import de.will_smith_007.bedwars.dependency_injection.InjectionModule;
import de.will_smith_007.bedwars.listeners.PlayerConnectionListener;
import de.will_smith_007.bedwars.listeners.game.*;
import de.will_smith_007.bedwars.listeners.lobby.InteractWithLobbyItemListener;
import de.will_smith_007.bedwars.listeners.lobby.LobbyInventoryClickListener;
import de.will_smith_007.bedwars.listeners.lobby.PlayerDropItemListener;
import de.will_smith_007.bedwars.listeners.setup.BedWarsSpawnerSetupListener;
import de.will_smith_007.bedwars.listeners.setup.BedWarsWorldSetupListener;
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
        // Creates the Injector Object to handle dependency injections
        final Injector injector = Guice.createInjector(new InjectionModule(this));

        // Command registration
        registerCommand("bedwars", injector.getInstance(BedWarsCommand.class));
        registerCommand("start", injector.getInstance(StartCommand.class));

        // Listener registration
        registerListeners(
                injector.getInstance(PlayerConnectionListener.class),
                new BedWarsSpawnerSetupListener(),
                injector.getInstance(BedWarsWorldSetupListener.class),
                injector.getInstance(BlockBuildingListener.class),
                injector.getInstance(TeamAndPlayerDamageListener.class),
                new EntitySpawnListener(),
                injector.getInstance(BedBreakListener.class),
                new BlockSpreadAndBurnListener(),
                new ExplosionPrimeListener(),
                injector.getInstance(FoodLevelChangeListener.class),
                injector.getInstance(EntityDamageAndDeathListener.class),
                injector.getInstance(ShopListener.class),
                new PlayerDropItemListener(),
                injector.getInstance(InteractWithLobbyItemListener.class),
                injector.getInstance(GameChatListener.class),
                injector.getInstance(LobbyInventoryClickListener.class),
                injector.getInstance(PlayerInteractWithBlocksListener.class)
        );

        getLogger().info("BedWars was started.");
    }

    @Override
    public void onDisable() {
        getLogger().info("BedWars was stopped.");
    }

    /**
     * Registers all listeners in the specified {@link Listener} array.
     *
     * @param listeners Classes which implements the {@link Listener} interface.
     */
    private void registerListeners(Listener @NonNull ... listeners) {
        final PluginManager pluginManager = Bukkit.getPluginManager();
        for (Listener listener : listeners) {
            pluginManager.registerEvents(listener, this);
        }
    }

    /**
     * Registers a specified {@link CommandExecutor} with the name of command as a {@link String}.
     *
     * @param command         Name of command which can be executed ingame or per console.
     * @param commandExecutor Class of the command which implements the {@link CommandExecutor} interface.
     */
    private void registerCommand(@NonNull String command, @NonNull CommandExecutor commandExecutor) {
        final PluginCommand pluginCommand = getCommand(command);
        if (pluginCommand == null) return;
        pluginCommand.setExecutor(commandExecutor);
    }
}
