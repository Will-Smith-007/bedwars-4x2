package de.will_smith_007.bedwars.schedulers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.will_smith_007.bedwars.enums.GameState;
import de.will_smith_007.bedwars.enums.Message;
import de.will_smith_007.bedwars.file_config.BedWarsConfig;
import de.will_smith_007.bedwars.game_assets.GameAssets;
import de.will_smith_007.bedwars.game_config.GameConfiguration;
import de.will_smith_007.bedwars.schedulers.interfaces.ICountdownOptions;
import de.will_smith_007.bedwars.schedulers.interfaces.IScheduler;
import de.will_smith_007.bedwars.scoreboard.interfaces.IScoreboardManager;
import de.will_smith_007.bedwars.teams.helper.interfaces.ITeamHelper;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Singleton
public class LobbyCountdownScheduler implements IScheduler, ICountdownOptions {

    private int taskID;
    @Setter
    @Getter
    private int countdown;
    private boolean isRunning = false;
    private final JavaPlugin javaPlugin;
    private final ITeamHelper teamHelper;
    private final GameAssets gameAssets;
    private final ProtectionCountdownScheduler protectionCountdownScheduler;
    private final SpawnerScheduler spawnerScheduler;
    private final BedWarsConfig bedWarsConfig;
    private final IScoreboardManager scoreboardManager;

    @Inject
    public LobbyCountdownScheduler(@NonNull JavaPlugin javaPlugin,
                                   @NonNull ITeamHelper teamHelper,
                                   @NonNull GameAssets gameAssets,
                                   @NonNull ProtectionCountdownScheduler protectionCountdownScheduler,
                                   @NonNull SpawnerScheduler spawnerScheduler,
                                   @NonNull IScoreboardManager scoreboardManager,
                                   @NonNull BedWarsConfig bedWarsConfig) {
        this.javaPlugin = javaPlugin;
        this.teamHelper = teamHelper;
        this.gameAssets = gameAssets;
        this.protectionCountdownScheduler = protectionCountdownScheduler;
        this.spawnerScheduler = spawnerScheduler;
        this.scoreboardManager = scoreboardManager;
        this.bedWarsConfig = bedWarsConfig;
    }

    @Override
    public @NonNull String getCountdownMessage(int currentCountdown) {
        return "The game is starting in §c" + countdown + (countdown == 1 ? " second§7." : " seconds§7.");
    }

    @Override
    public void playCountdownSound(@NonNull Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
    }

    @Override
    public void start() {
        if (isRunning) return;

        isRunning = true;
        countdown = 60;

        taskID = BUKKIT_SCHEDULER.scheduleSyncRepeatingTask(javaPlugin, () -> {
            final Collection<? extends Player> players = Bukkit.getOnlinePlayers();

            players.forEach(player -> player.setLevel(countdown));

            switch (countdown) {
                case 60, 30, 10, 5, 3, 2, 1 -> {
                    for (Player player : players) {
                        player.sendPlainMessage(Message.PREFIX + getCountdownMessage(countdown));
                        playCountdownSound(player);
                    }
                }
                case 0 -> {
                    gameAssets.setGameState(GameState.PROTECTION);

                    // If there isn't any configured game world, then return and stop this scheduler
                    final List<String> gameWorlds = bedWarsConfig.getGameWorlds();
                    if (gameWorlds.isEmpty()) {
                        stop();
                        return;
                    }

                    // Randomizes the list of game worlds and gets the first element of this list
                    Collections.shuffle(gameWorlds);
                    final String worldName = gameWorlds.get(0);
                    final World gameWorld = Bukkit.createWorld(new WorldCreator(worldName));

                    // If this world doesn't exist anymore, then return and stop this scheduler
                    if (gameWorld == null) {
                        stop();
                        return;
                    }

                    // Sets the game rules of the selected map and gives all villagers the potion effect slowness
                    setGamerulesAndFreezeVillagers(gameWorld);

                    for (Player player : players) {
                        final Optional<ITeam> optionalITeam = teamHelper.getTeam(player);
                        final boolean isTeamPresent = optionalITeam.isPresent();
                        final ITeam iTeam;

                        /*
                         If the player have selected a team, then use it.
                         Otherwise, select a team for the player.
                         */
                        if (isTeamPresent) {
                            iTeam = optionalITeam.get();
                        } else {
                            iTeam = teamHelper.selectBedWarsTeam(player);
                        }

                        // Teleports the player to the game world
                        final Location teamSpawnLocation = iTeam.getTeamSpawnLocation(gameWorld, bedWarsConfig);
                        player.teleport(teamSpawnLocation);
                        player.setGameMode(GameMode.SURVIVAL);

                        player.getInventory().clear();
                    }

                    gameAssets.setGameConfiguration(new GameConfiguration(gameWorld));

                    for (Player player : players) {
                        scoreboardManager.setScoreboardAndTablist(player);
                    }

                    protectionCountdownScheduler.start();
                    spawnerScheduler.start();

                    stop();
                }
                default -> {
                    return;
                }
            }
            countdown--;
        }, 0L, 20L);
    }

    @Override
    public void stop() {
        if (!isRunning) return;
        isRunning = false;
        BUKKIT_SCHEDULER.cancelTask(taskID);
        Bukkit.getOnlinePlayers().forEach(player -> player.setLevel(0));
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    private void setGamerulesAndFreezeVillagers(@NonNull World gameWorld) {
        gameWorld.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        gameWorld.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        gameWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);

        final Collection<Villager> villagerCollection = gameWorld.getEntitiesByClass(Villager.class);
        for (Villager villager : villagerCollection) {
            villager.addPotionEffect(
                    new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE,
                            10, false, false)
            );
        }
    }
}
