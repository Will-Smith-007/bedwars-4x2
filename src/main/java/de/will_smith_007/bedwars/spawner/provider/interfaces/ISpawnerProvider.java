package de.will_smith_007.bedwars.spawner.provider.interfaces;

import de.will_smith_007.bedwars.setup.BedWarsSetup;
import de.will_smith_007.bedwars.spawner.Spawner;
import lombok.NonNull;

import java.util.Set;

public interface ISpawnerProvider {

    /**
     * Gets all configured spawners with the specified {@link de.will_smith_007.bedwars.setup.BedWarsSetup.SpawnerType}
     * in the specific world.
     *
     * @param spawnerType Type of spawner from which you want to get the spawners.
     * @return A {@link Set} with all found {@link Spawner}s.
     */
    Set<Spawner> getSpawners(@NonNull BedWarsSetup.SpawnerType spawnerType);
}
