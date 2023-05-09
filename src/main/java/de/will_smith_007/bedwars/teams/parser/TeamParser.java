package de.will_smith_007.bedwars.teams.parser;

import com.google.inject.Singleton;
import de.will_smith_007.bedwars.teams.enums.BedWarsTeam;
import lombok.NonNull;

import java.util.Optional;

@Singleton
public class TeamParser {

    /**
     * Parses a team depending on the given team name.
     *
     * @param teamName The name of team from which you want to get the {@link BedWarsTeam}.
     * @return An {@link Optional} which contains the found {@link BedWarsTeam} or empty
     * if there couldn't be found a team for the given team name.
     * @apiNote If the given {@link String} starts with a "§" color code, the {@link String}
     * starts at position 2.
     */
    public Optional<BedWarsTeam> parseTeam(@NonNull String teamName) {
        if (teamName.startsWith("§")) teamName = teamName.substring(2);

        for (BedWarsTeam bedWarsTeam : BedWarsTeam.values()) {
            final String bedWarsTeamName = bedWarsTeam.getTeamName();

            if (bedWarsTeamName.equalsIgnoreCase(teamName)) return Optional.of(bedWarsTeam);

            final String shortenTeamName = bedWarsTeamName.replace("Team ", "");

            if (shortenTeamName.equalsIgnoreCase(teamName)) return Optional.of(bedWarsTeam);
        }
        return Optional.empty();
    }
}
