package de.will_smith_007.bedwars.teams.parser;

import de.will_smith_007.bedwars.teams.enums.Team;
import lombok.NonNull;

import java.util.Optional;

public class TeamParser {

    public Optional<Team> parseTeam(@NonNull String teamName) {
        for (Team team : Team.values()) {
            final String shortenTeamName = team.getTeamName().replace("Team ", "");
            if (!shortenTeamName.equalsIgnoreCase(teamName)) return Optional.empty();
            return Optional.of(team);
        }
        return Optional.empty();
    }
}
