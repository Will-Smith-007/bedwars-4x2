package de.will_smith_007.bedwars.teams.enums;

import de.will_smith_007.bedwars.teams.*;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BedWarsTeam {

    BLUE("Team Blue", TeamBlue.getInstance()),
    RED("Team Red", TeamRed.getInstance()),
    YELLOW("Team Yellow", TeamYellow.getInstance()),
    GREEN("Team Green", TeamGreen.getInstance());

    private final String teamName;
    private final ITeam team;
}
