package de.will_smith_007.bedwars.setup;

import de.will_smith_007.bedwars.teams.enums.Team;
import de.will_smith_007.bedwars.file_config.BedWarsConfig;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

@Getter
public class BedWarsSetup {

    @Setter
    private World gameWorld;
    @Setter
    private SetupAction setupAction;

    private final Map<Team, Location> BED_LOCATIONS = new HashMap<>();
    private final Map<Team, Location> TEAM_SPAWN_LOCATIONS = new HashMap<>();
    private final Map<Location, SpawnerType> SPAWNER_LOCATIONS = new HashMap<>();

    @Setter
    private Location spectatorLocation = null;

    public void setBedLocation(@NonNull Team team, @NonNull Location bedLocation) {
        BED_LOCATIONS.put(team, bedLocation);
    }

    public void setTeamSpawnLocation(@NonNull Team team, @NonNull Location teamSpawnLocation) {
        TEAM_SPAWN_LOCATIONS.put(team, teamSpawnLocation);
    }

    public void addSpawner(@NonNull SpawnerType spawnerType, @NonNull Location spawnerLocation) {
        final Location resultLocation = spawnerLocation.add(0.00d, 1.00d, 0.00d);
        SPAWNER_LOCATIONS.put(resultLocation, spawnerType);
    }

    public void saveSetup() {
        final BedWarsConfig bedWarsConfig = BedWarsConfig.getInstance();

        bedWarsConfig.setBedLocations(BED_LOCATIONS);
        bedWarsConfig.setTeamSpawnLocations(TEAM_SPAWN_LOCATIONS);
        bedWarsConfig.setSpawnerLocations(SPAWNER_LOCATIONS);

        if (spectatorLocation != null) {
            bedWarsConfig.setSpectatorLocation(spectatorLocation);
        }
    }

    public enum SetupAction {
        WORLD_SETUP,
        BED_SETUP,
        TEAM_SPAWN_SETUP,
        BRONZE_SPAWNER_SETUP,
        IRON_SPAWNER_SETUP,
        GOLD_SPAWNER_SETUP,
        SPECTATOR_SETUP
    }

    @Getter
    @RequiredArgsConstructor
    public enum SpawnerType {
        BRONZE("Bronze"),
        IRON("Iron"),
        GOLD("Gold");

        private final String name;
    }
}
