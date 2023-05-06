package de.will_smith_007.bedwars.file_config;

import de.will_smith_007.bedwars.setup.BedWarsSetup;
import de.will_smith_007.bedwars.teams.enums.BedWarsTeam;
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

    /**
     * Gets all configured game worlds.
     *
     * @return A {@link List} which holds all configured world names.
     */
    public @NonNull List<String> getGameWorlds() {
        final ConfigurationSection configurationSection = YAML_CONFIGURATION.getConfigurationSection("Maps");
        return (configurationSection == null ? new LinkedList<>() : configurationSection.getKeys(false).stream().toList());
    }

    /**
     * Gets the name of the configured lobby world.
     *
     * @return Name of configured waiting lobby world.
     */
    public String getLobbyWorld() {
        return YAML_CONFIGURATION.getString("LobbyWorld");
    }

    /**
     * Gets the location of the configured team bed.
     *
     * @param bedWarsTeam Team from which you need the bed location.
     * @param world       World in which you need the bed location.
     * @return The {@link Location} from the {@link BedWarsTeam} in a specified {@link World}.
     */
    public Location getBedLocation(@NonNull BedWarsTeam bedWarsTeam, @NonNull World world) {
        final String worldName = world.getName();
        final String sectionName = "Maps." + worldName + "." + bedWarsTeam.getTeamName() + ".Bed";

        return new Location(world,
                YAML_CONFIGURATION.getInt(sectionName + ".X"),
                YAML_CONFIGURATION.getInt(sectionName + ".Y"),
                YAML_CONFIGURATION.getInt(sectionName + ".Z"));
    }

    /**
     * Gets the location of the configured team spawn.
     *
     * @param bedWarsTeam Team from which you need the spawn location.
     * @param world       World in which you need the spawn location.
     * @return The {@link Location} of the team spawn from the {@link BedWarsTeam} in a specified {@link World}
     */
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

    /**
     * Gets all configured spawners with the specified {@link de.will_smith_007.bedwars.setup.BedWarsSetup.SpawnerType}.
     *
     * @param spawnerType Type of spawner which you want to get.
     * @param world       World in which you need the spawner locations.
     * @return A {@link Set} which holds the {@link Location} of all spawners from the specified
     * {@link de.will_smith_007.bedwars.setup.BedWarsSetup.SpawnerType} in the specified {@link World}.
     */
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

    /**
     * Gets the configured spectator location where the players should be teleported to when they are eliminated.
     *
     * @param world World in which you need the spectator location.
     * @return A {@link Location} from the spectator spawn in a specified {@link World}.
     */
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

    /**
     * Sets the waiting lobby world in which the players should wait for game start or when the game ends.
     *
     * @param world World which should be the lobby world.
     */
    public void setLobbyWorld(@NonNull World world) {
        final String worldName = world.getName();

        YAML_CONFIGURATION.set("LobbyWorld", worldName);

        saveConfig();
    }

    /**
     * Sets the bed locations of all teams in the specified {@link Map}.
     *
     * @param bedLocations Map of all configured bed locations.
     */
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

    /**
     * Sets the spawn locations of all teams in the specified {@link Map}.
     *
     * @param teamSpawnLocations Map of all configured team spawn locations.
     */
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

    /**
     * Sets the spawner locations of all spawner types in the specified {@link Map}.
     *
     * @param spawnerLocations Map of all configured spawner locations.
     */
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

    /**
     * Sets the spectator location to which the eliminated players should be teleported to.
     *
     * @param location Location of the spectator spawn.
     */
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

    /**
     * Saves the BedWars configuration file.
     */
    private void saveConfig() {
        try {
            YAML_CONFIGURATION.save(BED_WARS_CONFIG);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    /**
     * Gets the instance of the {@link BedWarsConfig}.
     * It should be only one instance of this class.
     *
     * @return The instance of {@link BedWarsConfig}.
     */
    public static @NonNull BedWarsConfig getInstance() {
        return instance;
    }
}
