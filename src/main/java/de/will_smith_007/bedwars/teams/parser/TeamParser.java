package de.will_smith_007.bedwars.teams.parser;

import de.will_smith_007.bedwars.teams.enums.BedWarsTeam;
import lombok.NonNull;

import java.util.Optional;

public class TeamParser {

    public Optional<BedWarsTeam> parseTeam(@NonNull String teamName) {
        for (BedWarsTeam bedWarsTeam : BedWarsTeam.values()) {
            final String shortenTeamName = bedWarsTeam.getTeamName().replace("BedWarsTeam ", "");
            if (!shortenTeamName.equalsIgnoreCase(teamName)) return Optional.empty();
            return Optional.of(bedWarsTeam);
        }
        return Optional.empty();
    }
}
