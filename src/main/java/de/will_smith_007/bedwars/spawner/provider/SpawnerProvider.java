package de.will_smith_007.bedwars.spawner.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.will_smith_007.bedwars.file_config.BedWarsConfig;
import de.will_smith_007.bedwars.game_assets.GameAssets;
import de.will_smith_007.bedwars.game_config.GameConfiguration;
import de.will_smith_007.bedwars.setup.BedWarsSetup;
import de.will_smith_007.bedwars.spawner.Spawner;
import de.will_smith_007.bedwars.spawner.provider.interfaces.ISpawnerProvider;
import lombok.NonNull;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;

@Singleton
public class SpawnerProvider implements ISpawnerProvider {

    private final GameAssets gameAssets;
    private final BedWarsConfig bedWarsConfig;

    @Inject
    public SpawnerProvider(@NonNull GameAssets gameAssets,
                           @NonNull BedWarsConfig bedWarsConfig) {
        this.gameAssets = gameAssets;
        this.bedWarsConfig = bedWarsConfig;
    }

    @Override
    public Set<Spawner> getSpawners(@NonNull BedWarsSetup.SpawnerType spawnerType) {
        final Set<Spawner> spawners = new HashSet<>();
        // The current data of a running game
        final GameConfiguration gameConfiguration = gameAssets.getGameConfiguration();

        if (gameConfiguration == null) return spawners;

        final Set<Location> locations = bedWarsConfig.getSpawners(
                spawnerType,
                gameConfiguration.gameWorld()
        );

        for (Location spawnerLocation : locations) {
            spawners.add(new Spawner(spawnerLocation, spawnerType));
        }

        return spawners;
    }
}
