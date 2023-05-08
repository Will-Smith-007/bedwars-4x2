package de.will_smith_007.bedwars.setup;

import de.will_smith_007.bedwars.file_config.BedWarsConfig;
import de.will_smith_007.bedwars.teams.enums.BedWarsTeam;
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

    private final Map<BedWarsTeam, Location> bedLocations = new HashMap<>();
    private final Map<BedWarsTeam, Location> teamSpawnLocations = new HashMap<>();
    private final Map<Location, SpawnerType> spawnerLocations = new HashMap<>();

    @Setter
    private Location spectatorLocation = null;

    private final BedWarsConfig bedWarsConfig;

    public BedWarsSetup(@NonNull BedWarsConfig bedWarsConfig) {
        this.bedWarsConfig = bedWarsConfig;
    }

    public void setBedLocation(@NonNull BedWarsTeam bedWarsTeam, @NonNull Location bedLocation) {
        bedLocations.put(bedWarsTeam, bedLocation);
    }

    public void setTeamSpawnLocation(@NonNull BedWarsTeam bedWarsTeam, @NonNull Location teamSpawnLocation) {
        teamSpawnLocations.put(bedWarsTeam, teamSpawnLocation);
    }

    public void addSpawner(@NonNull SpawnerType spawnerType, @NonNull Location spawnerLocation) {
        final Location resultLocation = spawnerLocation.add(0.00d, 1.00d, 0.00d);
        spawnerLocations.put(resultLocation, spawnerType);
    }

    public void saveSetup() {
        bedWarsConfig.setBedLocations(bedLocations);
        bedWarsConfig.setTeamSpawnLocations(teamSpawnLocations);
        bedWarsConfig.setSpawnerLocations(spawnerLocations);

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
