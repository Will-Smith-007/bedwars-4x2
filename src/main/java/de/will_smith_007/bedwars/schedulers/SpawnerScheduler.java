package de.will_smith_007.bedwars.schedulers;

import de.will_smith_007.bedwars.schedulers.interfaces.IScheduler;
import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;

public class SpawnerScheduler implements IScheduler {

    private int taskID, ironCountdown, goldCountdown;
    private boolean isRunning = false;
    private final JavaPlugin JAVA_PLUGIN;

    public SpawnerScheduler(@NonNull JavaPlugin javaPlugin) {
        JAVA_PLUGIN = javaPlugin;
    }

    @Override
    public void start() {
        if (isRunning) return;

        isRunning = true;
        ironCountdown = 10;
        goldCountdown = 30;

        taskID = BUKKIT_SCHEDULER.scheduleSyncRepeatingTask(JAVA_PLUGIN, () -> {
            ironCountdown--;
            goldCountdown--;

            if (ironCountdown == 0) {
                //TODO: Spawn iron on all iron spawners
                ironCountdown = 10;
            }

            if (goldCountdown == 0) {
                //TODO: Spawn iron on all iron spawners
                goldCountdown = 30;
            }
        }, 0L, 20L);
    }

    @Override
    public void stop() {
        if (!isRunning) return;
        BUKKIT_SCHEDULER.cancelTask(taskID);
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }
}
