package de.will_smith_007.bedwars.listeners.game;

import lombok.NonNull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

/**
 * This {@link Listener} handles the {@link EntitySpawnEvent} to deny almost all entity spawns
 * except villagers, dropped items, arrows, ender pearls and fishing hooks.
 */
public class EntitySpawnListener implements Listener {

    @EventHandler
    public void onEntitySpawn(@NonNull EntitySpawnEvent entitySpawnEvent) {
        final Entity spawnedEntity = entitySpawnEvent.getEntity();
        final EntityType entityType = spawnedEntity.getType();

        if (entityType != EntityType.VILLAGER
                && entityType != EntityType.DROPPED_ITEM
                && entityType != EntityType.ARROW
                && entityType != EntityType.ENDER_PEARL
                && entityType != EntityType.FISHING_HOOK) entitySpawnEvent.setCancelled(true);
    }
}
