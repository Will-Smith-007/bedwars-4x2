package de.will_smith_007.bedwars.teams.enums;

import de.will_smith_007.bedwars.teams.*;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BedWarsTeam {

    BLUE("BedWarsTeam Blue", TeamBlue.getInstance()),
    RED("BedWarsTeam Red", TeamRed.getInstance()),
    YELLOW("BedWarsTeam Yellow", TeamYellow.getInstance()),
    GREEN("BedWarsTeam Green", TeamGreen.getInstance());

    private final String teamName;
    private final ITeam team;
}
