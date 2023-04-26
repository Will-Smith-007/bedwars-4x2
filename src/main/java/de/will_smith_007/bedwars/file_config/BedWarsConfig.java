package de.will_smith_007.bedwars.file_config;

import de.will_smith_007.bedwars.enums.Team;
import de.will_smith_007.bedwars.setup.BedWarsSetup;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class BedWarsConfig {

    private static BedWarsConfig instance;

    private final Logger LOGGER;
    private final File BEDWARS_CONFIG;
    private final YamlConfiguration YAML_CONFIGURATION;

    public BedWarsConfig(@NonNull JavaPlugin javaPlugin) {
        instance = this;
        LOGGER = javaPlugin.getLogger();

        final File bedWarsConfigDirectory = new File(javaPlugin.getDataFolder().getPath());
        final String configName = "config.yml";
        BEDWARS_CONFIG = new File(bedWarsConfigDirectory + "/" + configName);

        if (bedWarsConfigDirectory.mkdirs()) {
            LOGGER.info("BedWars configuration directory was created.");
        }

        if (!BEDWARS_CONFIG.exists()) {
            try {
                if (BEDWARS_CONFIG.createNewFile()) {
                    LOGGER.info("BedWars configuration file was created.");
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        YAML_CONFIGURATION = YamlConfiguration.loadConfiguration(BEDWARS_CONFIG);
    }

    public @NonNull Set<String> getGameWorlds() {
        final ConfigurationSection configurationSection = YAML_CONFIGURATION.getConfigurationSection("Maps");
        return (configurationSection == null ? new HashSet<>() : configurationSection.getKeys(false));
    }

    public String getLobbyWorld() {
        return YAML_CONFIGURATION.getString("LobbyWorld");
    }

    public Location getBedLocation(@NonNull Team team, @NonNull World world) {
        final String worldName = world.getName();
        final String sectionName = "Maps." + worldName + "." + team.getTeamName() + ".Bed";

        return new Location(world,
                YAML_CONFIGURATION.getInt(sectionName + ".X"),
                YAML_CONFIGURATION.getInt(sectionName + ".Y"),
                YAML_CONFIGURATION.getInt(sectionName + ".Z"));
    }

    public Location getTeamSpawnLocation(@NonNull Team team, @NonNull World world) {
        final String worldName = world.getName();
        final String sectionName = "Maps." + worldName + "." + team.getTeamName() + ".Spawn";

        return new Location(world,
                YAML_CONFIGURATION.getInt(sectionName + ".X"),
                YAML_CONFIGURATION.getInt(sectionName + ".Y"),
                YAML_CONFIGURATION.getInt(sectionName + ".Z"),
                (float) YAML_CONFIGURATION.getDouble(sectionName + ".Yaw"),
                (float) YAML_CONFIGURATION.getDouble(sectionName + ".Pitch"));
    }

    public Set<Location> getSpawners(@NonNull BedWarsSetup.SpawnerType spawnerType,
                                     @NonNull World world) {
        final String worldName = world.getName();
        final Set<Location> spawnerLocations = new HashSet<>();
        final String spawnerTypeName = spawnerType.getName();
        final String sectionName = "Maps." + worldName + "." + spawnerTypeName + " spawners";
        final ConfigurationSection configurationSection = YAML_CONFIGURATION.getConfigurationSection(sectionName);

        if (configurationSection == null) return spawnerLocations;

        for (String key : configurationSection.getKeys(false)) {
            final Location spawnerLocation = new Location(world,
                    YAML_CONFIGURATION.getInt(sectionName + "." + key + ".X"),
                    YAML_CONFIGURATION.getInt(sectionName + "." + key + ".Y"),
                    YAML_CONFIGURATION.getInt(sectionName + "." + key + ".Z"));
            spawnerLocations.add(spawnerLocation);
        }

        return spawnerLocations;
    }

    public Location getSpectatorLocation(@NonNull World world) {
        final String worldName = world.getName();
        final String sectionName = "Maps." + worldName + ".Spectator";

        return new Location(world,
                YAML_CONFIGURATION.getInt(sectionName + ".X"),
                YAML_CONFIGURATION.getInt(sectionName + ".Y"),
                YAML_CONFIGURATION.getInt(sectionName + ".Z"),
                (float) YAML_CONFIGURATION.getDouble(sectionName + ".Yaw"),
                (float) YAML_CONFIGURATION.getDouble(sectionName + ".Pitch"));
    }

    public void setBedLocations(@NonNull Map<Team, Location> bedLocations) {
        if (bedLocations.isEmpty()) return;

        bedLocations.forEach((team, location) -> {
            final String worldName = location.getWorld().getName();
            final String sectionName = "Maps." + worldName + "." + team.getTeamName() + ".Bed";

            YAML_CONFIGURATION.set(sectionName + ".X", location.getBlockX());
            YAML_CONFIGURATION.set(sectionName + ".Y", location.getBlockY());
            YAML_CONFIGURATION.set(sectionName + ".Z", location.getBlockZ());
        });

        saveConfig();
    }

    public void setTeamSpawnLocations(@NonNull Map<Team, Location> teamSpawnLocations) {
        if (teamSpawnLocations.isEmpty()) return;

        teamSpawnLocations.forEach((team, location) -> {
            final String worldName = location.getWorld().getName();
            final String sectionName = "Maps." + worldName + "." + team.getTeamName() + ".Spawn";

            YAML_CONFIGURATION.set(sectionName + ".X", location.getBlockX());
            YAML_CONFIGURATION.set(sectionName + ".Y", location.getBlockY());
            YAML_CONFIGURATION.set(sectionName + ".Z", location.getBlockZ());
            YAML_CONFIGURATION.set(sectionName + ".Yaw", location.getYaw());
            YAML_CONFIGURATION.set(sectionName + ".Pitch", location.getPitch());
        });

        saveConfig();
    }

    public void setSpawnerLocations(@NonNull Map<Location, BedWarsSetup.SpawnerType> spawnerLocations) {
        if (spawnerLocations.isEmpty()) return;

        int currentSpawner = 0;
        final Set<Map.Entry<Location, BedWarsSetup.SpawnerType>> locationSpawnerTypeEntry = spawnerLocations.entrySet();

        for (Map.Entry<Location, BedWarsSetup.SpawnerType> spawnerTypeEntry : locationSpawnerTypeEntry) {
            final Location location = spawnerTypeEntry.getKey();
            final BedWarsSetup.SpawnerType spawnerType = spawnerTypeEntry.getValue();
            final String worldName = location.getWorld().getName();
            final String spawnerTypeName = spawnerType.getName();
            final String sectionName = "Maps." + worldName + "." + spawnerTypeName + " spawners";

            YAML_CONFIGURATION.set(sectionName + "." + currentSpawner + ".X", location.getBlockX());
            YAML_CONFIGURATION.set(sectionName + "." + currentSpawner + ".Y", location.getBlockY());
            YAML_CONFIGURATION.set(sectionName + "." + currentSpawner + ".Z", location.getBlockZ());

            currentSpawner++;
        }

        saveConfig();
    }

    public void setSpectatorLocation(@NonNull Location location) {
        final String worldName = location.getWorld().getName();
        final String sectionName = "Maps." + worldName + ".Spectator";

        YAML_CONFIGURATION.set(sectionName + ".X", location.getBlockX());
        YAML_CONFIGURATION.set(sectionName + ".Y", location.getBlockY());
        YAML_CONFIGURATION.set(sectionName + ".Z", location.getBlockZ());
        YAML_CONFIGURATION.set(sectionName + ".Yaw", location.getYaw());
        YAML_CONFIGURATION.set(sectionName + ".Pitch", location.getPitch());

        saveConfig();
    }

    private void saveConfig() {
        try {
            YAML_CONFIGURATION.save(BEDWARS_CONFIG);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static @NonNull BedWarsConfig getInstance() {
        return instance;
    }
}
