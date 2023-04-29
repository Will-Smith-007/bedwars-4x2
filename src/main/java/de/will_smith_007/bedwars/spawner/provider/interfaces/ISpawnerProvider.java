package de.will_smith_007.bedwars.spawner.provider.interfaces;

import de.will_smith_007.bedwars.file_config.BedWarsConfig;
import de.will_smith_007.bedwars.setup.BedWarsSetup;
import de.will_smith_007.bedwars.spawner.Spawner;
import lombok.NonNull;

import java.util.Set;

public interface ISpawnerProvider {

    BedWarsConfig BED_WARS_CONFIG = BedWarsConfig.getInstance();

    Set<Spawner> getSpawners(@NonNull BedWarsSetup.SpawnerType spawnerType);
}
