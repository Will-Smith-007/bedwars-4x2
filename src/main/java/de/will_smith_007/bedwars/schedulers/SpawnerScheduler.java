package de.will_smith_007.bedwars.schedulers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.will_smith_007.bedwars.schedulers.interfaces.IScheduler;
import de.will_smith_007.bedwars.setup.BedWarsSetup;
import de.will_smith_007.bedwars.spawner.Spawner;
import de.will_smith_007.bedwars.spawner.provider.interfaces.ISpawnerProvider;
import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

@Singleton
public class SpawnerScheduler implements IScheduler {

    private int taskID, ironCountdown, goldCountdown;
    private boolean isRunning = false;
    private final JavaPlugin javaPlugin;
    private final ISpawnerProvider spawnerProvider;

    @Inject
    public SpawnerScheduler(@NonNull JavaPlugin javaPlugin,
                            @NonNull ISpawnerProvider spawnerProvider) {
        this.javaPlugin = javaPlugin;
        this.spawnerProvider = spawnerProvider;
    }

    @Override
    public void start() {
        if (isRunning) return;

        isRunning = true;
        ironCountdown = 10;
        goldCountdown = 30;

        final Set<Spawner> bronzeSpawners = spawnerProvider.getSpawners(BedWarsSetup.SpawnerType.BRONZE);
        final Set<Spawner> ironSpawners = spawnerProvider.getSpawners(BedWarsSetup.SpawnerType.IRON);
        final Set<Spawner> goldSpawners = spawnerProvider.getSpawners(BedWarsSetup.SpawnerType.GOLD);

        taskID = BUKKIT_SCHEDULER.scheduleSyncRepeatingTask(javaPlugin, () -> {
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
