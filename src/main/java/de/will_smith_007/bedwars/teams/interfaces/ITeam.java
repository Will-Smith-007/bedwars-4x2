package de.will_smith_007.bedwars.teams.interfaces;

import de.will_smith_007.bedwars.file_config.BedWarsConfig;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Set;

public interface ITeam {

    /**
     * Adds a specified {@link Player} to this team.
     *
     * @param player The player which should be added to this team.
     */
    void addPlayer(@NonNull Player player);

    /**
     * Removes a specified {@link Player} from this team.
     *
     * @param player The player which should be removed from this team.
     */
    void removePlayer(@NonNull Player player);

    /**
     * Sets whether a team bed exists or not.
     *
     * @param bedExists Whether the bed should be existing or not.
     */
    void setBedExists(boolean bedExists);

    void openTeamChest(@NonNull Player player);

    /**
     * Gets the {@link Scoreboard} team of this {@link ITeam}.
     *
     * @param scoreboard The scoreboard from which the team should be returned.
     * @return The {@link Team} from this {@link ITeam}.
     */
    @NonNull Team getTeam(@NonNull Scoreboard scoreboard);

    /**
     * Gets the configured spawn {@link Location} of this {@link ITeam}.
     *
     * @param world         The world in which the team {@link Location} should be returned.
     * @param bedWarsConfig The BedWars config class.
     * @return The {@link Location} of this {@link ITeam} spawn.
     */
    Location getTeamSpawnLocation(@NonNull World world, @NonNull BedWarsConfig bedWarsConfig);

    /**
     * Checks if the bed from this team is present or not.
     *
     * @return True if the bed is currently present.
     * False if it was broken.
     */
    boolean bedExists();

    /**
     * Gets the name of this {@link ITeam} including the color code.
     *
     * @return The name of this team.
     */
    @NonNull String getTeamName();

    /**
     * Gets the players which are currently in this team.
     *
     * @return A {@link Set} which contains the {@link Player}s of this {@link ITeam}.
     */
    @NonNull Set<Player> getPlayers();
}
