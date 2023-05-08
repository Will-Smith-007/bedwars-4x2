package de.will_smith_007.bedwars.teams.parser;

import com.google.inject.Singleton;
import de.will_smith_007.bedwars.teams.enums.BedWarsTeam;
import lombok.NonNull;

import java.util.Optional;

@Singleton
public class TeamParser {

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
