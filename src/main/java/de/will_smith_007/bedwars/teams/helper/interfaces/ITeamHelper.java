package de.will_smith_007.bedwars.teams.helper.interfaces;

import de.will_smith_007.bedwars.teams.enums.BedWarsTeam;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Optional;

public interface ITeamHelper {

    BedWarsTeam[] BED_WARS_TEAMS = BedWarsTeam.values();

    /**
     * Handles the team elimination if a player dies or disconnects during the game.
     *
     * @param team    The team which should be handled.
     * @param players Current online players which can receive the team elimination message.
     */
    void handleTeamElimination(@NonNull ITeam team, @NonNull Collection<? extends Player> players);

    /**
     * Automatically selects a team for the specified {@link Player} if he hasn't selected a team.
     *
     * @param player The player which should be added to a team.
     * @return A {@link ITeam} which can be selected for the player.
     */
    ITeam selectBedWarsTeam(@NonNull Player player);

    /**
     * Checks if a specified {@link BedWarsTeam} can be joined.
     *
     * @param bedWarsTeam The team which should be checked.
     * @return True if someone can join the team.
     */
    boolean canTeamJoined(@NonNull BedWarsTeam bedWarsTeam);

    /**
     * Gets the current team of a specified {@link Player}.
     *
     * @param player The player from which you want to get the team.
     * @return An {@link Optional} which contains the current {@link ITeam} of the player.
     * Can be empty if there couldn't be found a team for this player.
     */
    Optional<ITeam> getTeam(@NonNull Player player);
}
