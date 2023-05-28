package de.will_smith_007.bedwars.schedulers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.will_smith_007.bedwars.enums.Message;
import de.will_smith_007.bedwars.schedulers.interfaces.ICountdownOptions;
import de.will_smith_007.bedwars.schedulers.interfaces.IScheduler;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

@Singleton
public class ProtectionCountdownScheduler implements IScheduler, ICountdownOptions {

    private int taskID, countdown;
    private boolean isRunning = false;
    private final JavaPlugin javaPlugin;

    @Inject
    public ProtectionCountdownScheduler(@NonNull JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    @Override
    public @NonNull String getCountdownMessage(int currentCountdown) {
        return "Protection ends in §c" + countdown + (countdown == 1 ? " second§7." : " seconds§7.");
    }

    @Override
    public void playCountdownSound(@NonNull Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
    }

    @Override
    public void start() {
        if (isRunning) return;

        isRunning = true;
        countdown = 10;

        taskID = BUKKIT_SCHEDULER.scheduleSyncRepeatingTask(javaPlugin, () -> {
            final Collection<? extends Player> players = Bukkit.getOnlinePlayers();

            switch (countdown) {
                case 10, 5, 3, 2, 1 -> {
                    for (Player player : players) {
                        player.sendPlainMessage(Message.PREFIX + getCountdownMessage(countdown));
                        playCountdownSound(player);
                    }
                }
                case 0 -> {
                    for (Player player : players) {
                        player.sendPlainMessage(Message.PREFIX + "§cProtection time is over!");
                        playCountdownSound(player);
                    }
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
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }
}
