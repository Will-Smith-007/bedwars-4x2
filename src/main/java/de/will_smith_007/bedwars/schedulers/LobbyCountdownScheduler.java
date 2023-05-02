package de.will_smith_007.bedwars.schedulers;

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

public class LobbyCountdownScheduler implements IScheduler, ICountdownOptions {

    private int taskID;
    @Setter
    @Getter
    private int countdown;
    private boolean isRunning = false;
    private final JavaPlugin JAVA_PLUGIN;
    private final ITeamHelper TEAM_HELPER;
    private final GameAssets GAME_ASSETS;
    private final ProtectionCountdownScheduler PROTECTION_COUNTDOWN_SCHEDULER;
    private final SpawnerScheduler SPAWNER_SCHEDULER;
    private final BedWarsConfig BEDWARS_CONFIG = BedWarsConfig.getInstance();
    private final IScoreboardManager SCOREBOARD_MANAGER;

    public LobbyCountdownScheduler(@NonNull JavaPlugin javaPlugin,
                                   @NonNull ITeamHelper teamHelper,
                                   @NonNull GameAssets gameAssets,
                                   @NonNull ProtectionCountdownScheduler protectionCountdownScheduler,
                                   @NonNull SpawnerScheduler spawnerScheduler,
                                   @NonNull IScoreboardManager scoreboardManager) {
        JAVA_PLUGIN = javaPlugin;
        TEAM_HELPER = teamHelper;
        GAME_ASSETS = gameAssets;
        PROTECTION_COUNTDOWN_SCHEDULER = protectionCountdownScheduler;
        SPAWNER_SCHEDULER = spawnerScheduler;
        SCOREBOARD_MANAGER = scoreboardManager;
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

        taskID = BUKKIT_SCHEDULER.scheduleSyncRepeatingTask(JAVA_PLUGIN, () -> {
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
                    GAME_ASSETS.setGameState(GameState.PROTECTION);

                    final List<String> gameWorlds = BEDWARS_CONFIG.getGameWorlds();
                    if (gameWorlds.isEmpty()) {
                        stop();
                        return;
                    }

                    Collections.shuffle(gameWorlds);
                    final String worldName = gameWorlds.get(0);
                    final World gameWorld = Bukkit.createWorld(new WorldCreator(worldName));

                    if (gameWorld == null) {
                        stop();
                        return;
                    }

                    setGamerulesAndFreezeVillagers(gameWorld);

                    for (Player player : players) {
                        final Optional<ITeam> optionalITeam = TEAM_HELPER.getTeam(player);
                        final boolean isTeamPresent = optionalITeam.isPresent();
                        final ITeam iTeam;

                        if (isTeamPresent) {
                            iTeam = optionalITeam.get();
                        } else {
                            iTeam = TEAM_HELPER.selectBedWarsTeam(player);
                        }

                        final Location teamSpawnLocation = iTeam.getTeamSpawnLocation(gameWorld);
                        player.teleport(teamSpawnLocation);
                        player.setGameMode(GameMode.SURVIVAL);
                    }

                    GAME_ASSETS.setGameConfiguration(new GameConfiguration(gameWorld));

                    for (Player player : players) {
                        SCOREBOARD_MANAGER.setTablist(player);
                        SCOREBOARD_MANAGER.updateScoreboard(player);
                    }

                    PROTECTION_COUNTDOWN_SCHEDULER.start();
                    SPAWNER_SCHEDULER.start();

                    stop();
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
