package de.will_smith_007.bedwars.spawner.provider;

import de.will_smith_007.bedwars.game_assets.GameAssets;
import de.will_smith_007.bedwars.game_config.GameConfiguration;
import de.will_smith_007.bedwars.setup.BedWarsSetup;
import de.will_smith_007.bedwars.spawner.Spawner;
import de.will_smith_007.bedwars.spawner.provider.interfaces.ISpawnerProvider;
import lombok.NonNull;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;

public class SpawnerProvider implements ISpawnerProvider {

    private final GameAssets GAME_ASSETS;

    public SpawnerProvider(@NonNull GameAssets gameAssets) {
        GAME_ASSETS = gameAssets;
    }

    @Override
    public Set<Spawner> getSpawners(@NonNull BedWarsSetup.SpawnerType spawnerType) {
        final Set<Spawner> spawners = new HashSet<>();
        final GameConfiguration gameConfiguration = GAME_ASSETS.getGameConfiguration();

        if (gameConfiguration == null) return spawners;

        final Set<Location> locations = BED_WARS_CONFIG.getSpawners(
                spawnerType,
                gameConfiguration.gameWorld()
        );

        for (Location spawnerLocation : locations) {
            spawners.add(new Spawner(spawnerLocation, spawnerType));
        }

        return spawners;
    }
}
