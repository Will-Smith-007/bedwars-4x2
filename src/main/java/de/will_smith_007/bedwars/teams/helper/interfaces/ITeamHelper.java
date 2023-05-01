package de.will_smith_007.bedwars.teams.helper.interfaces;

import de.will_smith_007.bedwars.teams.enums.BedWarsTeam;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Optional;

public interface ITeamHelper {

    BedWarsTeam[] BED_WARS_TEAMS = BedWarsTeam.values();

    void removeBedWarsTeam(@NonNull Player player);

    void handleTeamElimination(@NonNull ITeam team, @NonNull Collection<? extends Player> players);

    ITeam selectBedWarsTeam(@NonNull Player player);

    boolean setBedWarsTeam(@NonNull Player player, @NonNull BedWarsTeam bedWarsTeam);

    boolean canTeamJoined(@NonNull BedWarsTeam bedWarsTeam);

    Optional<ITeam> getTeam(@NonNull Player player);
}
