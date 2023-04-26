package de.will_smith_007.bedwars.schedulers.interfaces;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

public interface IScheduler {

    BukkitScheduler BUKKIT_SCHEDULER = Bukkit.getScheduler();

    void start();

    void stop();

    boolean isRunning();
}
