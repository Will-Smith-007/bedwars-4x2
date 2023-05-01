package de.will_smith_007.bedwars.file_config;

import de.will_smith_007.bedwars.teams.enums.BedWarsTeam;
import de.will_smith_007.bedwars.setup.BedWarsSetup;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class BedWarsConfig {

    private static BedWarsConfig instance;

    private final File BED_WARS_CONFIG;
    private final YamlConfiguration YAML_CONFIGURATION;

    public BedWarsConfig(@NonNull JavaPlugin javaPlugin) {
        instance = this;
        final Logger logger = javaPlugin.getLogger();

        final File bedWarsConfigDirectory = new File(javaPlugin.getDataFolder().getPath());
        final String configName = "config.yml";
        BED_WARS_CONFIG = new File(bedWarsConfigDirectory + "/" + configName);

        if (bedWarsConfigDirectory.mkdirs()) {
            logger.info("BedWars configuration directory was created.");
        }

        if (!BED_WARS_CONFIG.exists()) {
            try {
                if (BED_WARS_CONFIG.createNewFile()) {
                    logger.info("BedWars configuration file was created.");
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        YAML_CONFIGURATION = YamlConfiguration.loadConfiguration(BED_WARS_CONFIG);
    }

    public @NonNull List<String> getGameWorlds() {
        final ConfigurationSection configurationSection = YAML_CONFIGURATION.getConfigurationSection("Maps");
        return (configurationSection == null ? new LinkedList<>() : configurationSection.getKeys(false).stream().toList());
    }

    public String getLobbyWorld() {
        return YAML_CONFIGURATION.getString("LobbyWorld");
    }

    public Location getBedLocation(@NonNull BedWarsTeam bedWarsTeam, @NonNull World world) {
        final String worldName = world.getName();
        final String sectionName = "Maps." + worldName + "." + bedWarsTeam.getTeamName() + ".Bed";

        return new Location(world,
                YAML_CONFIGURATION.getInt(sectionName + ".X"),
                YAML_CONFIGURATION.getInt(sectionName + ".Y"),
                YAML_CONFIGURATION.getInt(sectionName + ".Z"));
    }

    public Location getTeamSpawnLocation(@NonNull BedWarsTeam bedWarsTeam, @NonNull World world) {
        final String worldName = world.getName();
        final String sectionName = "Maps." + worldName + "." + bedWarsTeam.getTeamName() + ".Spawn";

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

    public void setLobbyWorld(@NonNull World world) {
        final String worldName = world.getName();

        YAML_CONFIGURATION.set("LobbyWorld", worldName);

        saveConfig();
    }

    public void setBedLocations(@NonNull Map<BedWarsTeam, Location> bedLocations) {
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

    public void setTeamSpawnLocations(@NonNull Map<BedWarsTeam, Location> teamSpawnLocations) {
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
            YAML_CONFIGURATION.save(BED_WARS_CONFIG);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static @NonNull BedWarsConfig getInstance() {
        return instance;
    }
}
