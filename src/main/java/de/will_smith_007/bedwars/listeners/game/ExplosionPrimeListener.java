package de.will_smith_007.bedwars.listeners.game;

import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;

/**
 * This {@link Listener} handles the {@link ExplosionPrimeEvent} to deny all possible explosions.
 */
public class ExplosionPrimeListener implements Listener {

    @EventHandler
    public void onExplosionPrime(@NonNull ExplosionPrimeEvent explosionPrimeEvent) {
        explosionPrimeEvent.setCancelled(true);
    }
}
