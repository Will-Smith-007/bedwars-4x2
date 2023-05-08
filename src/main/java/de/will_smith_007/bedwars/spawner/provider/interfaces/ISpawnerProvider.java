package de.will_smith_007.bedwars.spawner.provider.interfaces;

import de.will_smith_007.bedwars.setup.BedWarsSetup;
import de.will_smith_007.bedwars.spawner.Spawner;
import lombok.NonNull;

import java.util.Set;

public interface ISpawnerProvider {

    Set<Spawner> getSpawners(@NonNull BedWarsSetup.SpawnerType spawnerType);
}
