package de.will_smith_007.bedwars.schedulers;

import de.will_smith_007.bedwars.schedulers.interfaces.IScheduler;
import de.will_smith_007.bedwars.setup.BedWarsSetup;
import de.will_smith_007.bedwars.spawner.Spawner;
import de.will_smith_007.bedwars.spawner.provider.interfaces.ISpawnerProvider;
import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class SpawnerScheduler implements IScheduler {

    private int taskID, ironCountdown, goldCountdown;
    private boolean isRunning = false;
    private final JavaPlugin JAVA_PLUGIN;
    private final ISpawnerProvider SPAWNER_PROVIDER;

    public SpawnerScheduler(@NonNull JavaPlugin javaPlugin,
                            @NonNull ISpawnerProvider spawnerProvider) {
        JAVA_PLUGIN = javaPlugin;
        SPAWNER_PROVIDER = spawnerProvider;
    }

    @Override
    public void start() {
        if (isRunning) return;

        isRunning = true;
        ironCountdown = 10;
        goldCountdown = 30;

        final Set<Spawner> bronzeSpawners = SPAWNER_PROVIDER.getSpawners(BedWarsSetup.SpawnerType.BRONZE);
        final Set<Spawner> ironSpawners = SPAWNER_PROVIDER.getSpawners(BedWarsSetup.SpawnerType.IRON);
        final Set<Spawner> goldSpawners = SPAWNER_PROVIDER.getSpawners(BedWarsSetup.SpawnerType.GOLD);

        taskID = BUKKIT_SCHEDULER.scheduleSyncRepeatingTask(JAVA_PLUGIN, () -> {
            ironCountdown--;
            goldCountdown--;

            for (Spawner bronzeSpawner : bronzeSpawners) {
                bronzeSpawner.spawnItem();
            }

            if (ironCountdown == 0) {
                for (Spawner ironSpawner : ironSpawners) {
                    ironSpawner.spawnItem();
                }
                ironCountdown = 10;
            }

            if (goldCountdown == 0) {
                for (Spawner goldSpawner : goldSpawners) {
                    goldSpawner.spawnItem();
                }
                goldCountdown = 30;
            }
        }, 0L, 20L);
    }

    @Override
    public void stop() {
        if (!isRunning) return;
        isRunning = false;
        BUKKIT_SCHEDULER.cancelTask(taskID);
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }
}
